package org.opencds.cqf.cql.engine.elm.executing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.xml.namespace.QName;
import org.cqframework.cql.elm.visiting.ElmLibraryVisitor;
import org.hl7.elm.r1.*;
import org.opencds.cqf.cql.engine.data.DataProvider;
import org.opencds.cqf.cql.engine.execution.State;
import org.opencds.cqf.cql.engine.runtime.Code;
import org.opencds.cqf.cql.engine.runtime.Concept;
import org.opencds.cqf.cql.engine.runtime.Interval;
import org.opencds.cqf.cql.engine.runtime.ValueSet;

public class RetrieveEvaluator {

    @SuppressWarnings("unchecked")
    public static Object internalEvaluate(Retrieve elm, State state, ElmLibraryVisitor<Object, State> visitor) {
        final Expression context = elm.getContext();

        boolean isEnteredContext = false;
        Iterable<Object> result = Collections.emptyList();

        if (context != null) {
            /*
               This whole block is a bit a hack in the sense that the need to switch to the context (e.g. Practitioner) identifies itself in a non-domain specific way
            */
            final Object contextValue = visitor.visitExpression(context, state);
            final String name = contextValue.getClass().getPackage().getName();
            final DataProvider dataProvider = state.getEnvironment().resolveDataProvider(name);
            final String contextTypeName = contextValue.getClass().getSimpleName();
            final String contextId = dataProvider.resolveId(contextValue);

            state.setContextValue(contextTypeName, contextId);
            isEnteredContext = state.enterContext(contextTypeName);
        }

        try {
            // Push an activation frame so that the execution of the
            // retrieve can be tracked. Mainly in terms of start and
            // end time for integration into a profile.
            state.pushActivationFrame(elm);

            QName dataType = state.getEnvironment().fixupQName(elm.getDataType());
            DataProvider dataProvider = state.getEnvironment().resolveDataProvider(dataType);
            Iterable<Code> codes = null;
            String valueSet = null;
            if (elm.getCodes() != null) {
                if (elm.getCodes() instanceof ValueSetRef) {
                    ValueSet vs = ValueSetRefEvaluator.toValueSet(state, (ValueSetRef) elm.getCodes());
                    valueSet = vs.getId();
                } else {
                    Object codesResult = visitor.visitExpression(elm.getCodes(), state);
                    if (codesResult instanceof ValueSet) {
                        valueSet = ((ValueSet) codesResult).getId();
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

            result = dataProvider.retrieve(
                    state.getCurrentContext(),
                    (String) dataProvider.getContextPath(state.getCurrentContext(), dataType.getLocalPart()),
                    state.getCurrentContextValue(),
                    dataType.getLocalPart(),
                    elm.getTemplateId(),
                    elm.getCodeProperty(),
                    codes,
                    valueSet,
                    elm.getDateProperty(),
                    elm.getDateLowProperty(),
                    elm.getDateHighProperty(),
                    dateRange);

            // TODO: We probably shouldn't eagerly load this, but we need to track
            // this throughout the engine and only add it to the list when it's actually used
            var evaluatedResource = state.getEvaluatedResources();
            if (result instanceof List) {
                evaluatedResource.addAll((List<?>) result);
            } else {
                for (var o : result) {
                    evaluatedResource.add(o);
                }
            }
        } finally {
            // Need to effectively reverse the context change we did at the beginning of this method
            state.exitContext(isEnteredContext);

            // The activation frame was pushed for tracking execution
            // time and should not have any local variables in it.
            state.popActivationFrame();
        }

        return result;
    }
}
