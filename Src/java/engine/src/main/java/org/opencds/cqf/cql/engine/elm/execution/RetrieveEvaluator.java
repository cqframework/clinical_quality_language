package org.opencds.cqf.cql.engine.elm.execution;

import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import org.cqframework.cql.elm.execution.ValueSetRef;
import org.opencds.cqf.cql.engine.data.DataProvider;
import org.opencds.cqf.cql.engine.execution.Context;
import org.opencds.cqf.cql.engine.runtime.Code;
import org.opencds.cqf.cql.engine.runtime.Concept;
import org.opencds.cqf.cql.engine.runtime.Interval;
import org.opencds.cqf.cql.engine.runtime.ValueSet;

public class RetrieveEvaluator extends org.cqframework.cql.elm.execution.Retrieve {

    @SuppressWarnings("unchecked")
    protected Object internalEvaluate(Context context) {
        QName dataType = context.fixupQName(this.dataType);
        DataProvider dataProvider = context.resolveDataProvider(dataType);
        Iterable<Code> codes = null;
        String valueSet = null;
        if (this.getCodes() != null) {
            if (this.getCodes() instanceof ValueSetRef) {
                ValueSet vs = ValueSetRefEvaluator.toValueSet(context, (ValueSetRef)this.getCodes());
                valueSet = vs.getId();
            }
            else {
                Object codesResult = this.getCodes().evaluate(context);
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
        if (this.getDateRange() != null) {
            dateRange = (Interval) this.getDateRange().evaluate(context);
        }

        Object result = dataProvider.retrieve(context.getCurrentContext(),
                (String) dataProvider.getContextPath(context.getCurrentContext(), dataType.getLocalPart()),
                context.getCurrentContextValue(), dataType.getLocalPart(), getTemplateId(),
                getCodeProperty(), codes, valueSet, getDateProperty(), getDateLowProperty(), getDateHighProperty(),
                dateRange);

        // append list results to evaluatedResources list
        if (result instanceof List) {
            context.getEvaluatedResources().addAll((List<?>)result);
        } else {
            context.getEvaluatedResources().add(result);
        }

        return result;
    }
}
