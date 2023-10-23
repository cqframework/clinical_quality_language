package org.opencds.cqf.cql.engine.elm.executing;

import org.cqframework.cql.elm.visiting.ElmLibraryVisitor;
import org.hl7.elm.r1.*;
import org.opencds.cqf.cql.engine.data.DataProvider;
import org.opencds.cqf.cql.engine.execution.State;
import org.opencds.cqf.cql.engine.runtime.Code;
import org.opencds.cqf.cql.engine.runtime.Concept;
import org.opencds.cqf.cql.engine.runtime.Interval;
import org.opencds.cqf.cql.engine.runtime.ValueSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class RetrieveEvaluator {
    private static final Logger logger = LoggerFactory.getLogger(RetrieveEvaluator .class);
    // LUKETODO:  change is going to be in here
    // LUKETODO:  It'll need some notion of "recontextualizing" the retrieve. This could be a stack of current contexts, or perhaps a separate branch statement.

    @SuppressWarnings("unchecked")
    public static Object internalEvaluate(Retrieve elm, State state, ElmLibraryVisitor<Object, State> visitor) {
        final Expression context = elm.getContext();

        boolean changedContext = false;

        if (context != null) {
            // LUKETODO:  this is an IDType....... JP will need to figure this out
            // LUKETODO:  add a pertinent comment that this whole thing is a hack to "get the type to identify itself"
            final Object contextValue = visitor.visitExpression(context, state);
            final String name = contextValue.getClass().getPackage().getName();
            final DataProvider dataProvider = state.getEnvironment().resolveDataProvider(name);
            final String contextTypeName = contextValue.getClass().getSimpleName();
            final String contextIdPath = instanceOfCast(dataProvider.getContextPath(contextTypeName, contextTypeName), String.class);
            final Object contextId = dataProvider.resolvePath(contextValue, contextIdPath);

            logger.info("contextTypeName: [{}], contextIdPath: [{}],  contextId: [{}]", contextTypeName, contextIdPath, contextId);

            state.setContextValue(contextTypeName, contextId);
            state.enterContext(contextTypeName);

            changedContext = true;
        }

        QName dataType = state.getEnvironment().fixupQName(elm.getDataType());
        DataProvider dataProvider = state.getEnvironment().resolveDataProvider(dataType);
        Iterable<Code> codes = null;
        String valueSet = null;
        if (elm.getCodes() != null) {
            if (elm.getCodes() instanceof ValueSetRef) {
                ValueSet vs = ValueSetRefEvaluator.toValueSet(state, (ValueSetRef)elm.getCodes());
                valueSet = vs.getId();
            }
            else {
                Object codesResult = visitor.visitExpression(elm.getCodes(), state);
                if (codesResult instanceof ValueSet) {
                    valueSet = ((ValueSet)codesResult).getId();
                } else if (codesResult instanceof String) {
                    List<Code> codesList = new ArrayList<>();
                    codesList.add(new Code().withCode((String) codesResult));
                    codes = codesList;
                } else if (codesResult instanceof Code) {
                    List<Code> codesList = new ArrayList<>();
                    codesList.add((Code) codesResult);
                    codes = codesList;
                } else if (codesResult instanceof Concept) {
                    List<Code> codesList = new ArrayList<>();
                    for (Code conceptCode : ((Concept) codesResult).getCodes()) {
                        codesList.add(conceptCode);
                    }
                    codes = codesList;
                } else {
                    codes = (Iterable<Code>) codesResult;
                }
            }
        }
        Interval dateRange = null;
        if (elm.getDateRange() != null) {
            dateRange = (Interval) visitor.visitExpression(elm.getDateRange(), state);
        }

        Iterable<Object> result = dataProvider.retrieve(state.getCurrentContext(),
                (String) dataProvider.getContextPath(state.getCurrentContext(), dataType.getLocalPart()),
                state.getCurrentContextValue(), dataType.getLocalPart(), elm.getTemplateId(),
                elm.getCodeProperty(), codes, valueSet, elm.getDateProperty(), elm.getDateLowProperty(), elm.getDateHighProperty(),
                dateRange);

        // LUKETODO:  it's only at this point that I have the practitioner
        // LUKETODO:  we don't have access to the types here (Practitioner/Patient/etc)
        // LUKETODO:  how to model this in a general way?
        // LUKETODO:  can I get the expression name "Primary Care Doctor" ? from somewhere in here, or just the resource type?

        // TODO: We probably shouldn't eagerly load this, but we need to track
        // this throughout the engine and only add it to the list when it's actually used
        var evaluatedResource = state.getEvaluatedResources();
        if (result instanceof List) {
            evaluatedResource.addAll((List<?>)result);
        } else {
            for (var o : result) {
                evaluatedResource.add(o);
            }
        }

        if (changedContext) {
            state.exitContext();
        }

        return result;
    }

    private static <T,S> S instanceOfCast(T superType, Class<S> clazz) {
        if (clazz.isInstance(superType)) {
            return clazz.cast(superType);
        }

        throw new IllegalArgumentException(String.format("Subtype of %s is not as expected", superType));
    }
}
