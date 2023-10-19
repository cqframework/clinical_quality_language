package org.opencds.cqf.cql.engine.elm.executing;

import org.hl7.elm.r1.ValueSetRef;
import org.cqframework.cql.elm.visiting.ElmLibraryVisitor;
import org.hl7.elm.r1.Retrieve;
import org.opencds.cqf.cql.engine.data.DataProvider;
import org.opencds.cqf.cql.engine.execution.State;
import org.opencds.cqf.cql.engine.runtime.Code;
import org.opencds.cqf.cql.engine.runtime.Concept;
import org.opencds.cqf.cql.engine.runtime.Interval;
import org.opencds.cqf.cql.engine.runtime.ValueSet;

import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.List;

public class RetrieveEvaluator {
    // LUKETODO:  change is going to be in here
    // LUKETODO:  It'll need some notion of "recontextualizing" the retrieve. This could be a stack of current contexts, or perhaps a separate branch statement.
    /*
        context Patient

        define "Mother": singleton from ([RelatedPerson: "Mother Relationship"])

        define "Estimated Due Date":
          Last(
            ["Mother" -> "Observation": "Estimated Due Date Exam"] Exam
              sort by effective
          )

        define "Gestational Age in Days at Birth":
          (280 - (duration in days between "Estimated Due Date" and "Birth Date")) div 7
     */

    @SuppressWarnings("unchecked")
    public static Object internalEvaluate(Retrieve elm, State state, ElmLibraryVisitor<Object, State> visitor) {
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
