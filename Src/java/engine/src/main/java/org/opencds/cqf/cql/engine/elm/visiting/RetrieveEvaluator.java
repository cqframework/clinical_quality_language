package org.opencds.cqf.cql.engine.elm.visiting;

import org.hl7.elm.r1.ValueSetRef;
import org.hl7.elm.r1.Retrieve;
import org.opencds.cqf.cql.engine.data.DataProvider;
import org.opencds.cqf.cql.engine.execution.CqlEngine;
import org.opencds.cqf.cql.engine.execution.State;
import org.opencds.cqf.cql.engine.runtime.Code;
import org.opencds.cqf.cql.engine.runtime.Concept;
import org.opencds.cqf.cql.engine.runtime.Interval;
import org.opencds.cqf.cql.engine.runtime.ValueSet;

import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.List;

public class RetrieveEvaluator {

    @SuppressWarnings("unchecked")
    public static Object internalEvaluate(Retrieve elm, State state, CqlEngine visitor) {
        QName dataType = state.fixupQName(elm.getDataType());
        DataProvider dataProvider = state.resolveDataProvider(dataType);
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

        return result;
    }
}
