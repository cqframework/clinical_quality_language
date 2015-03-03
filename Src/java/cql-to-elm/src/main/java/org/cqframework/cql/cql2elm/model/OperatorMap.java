package org.cqframework.cql.cql2elm.model;

import java.util.HashMap;
import java.util.Map;

public class OperatorMap {
    private Map<String, OperatorEntry> operators = new HashMap<>();

    public void addOperator(Operator operator) {
        OperatorEntry entry = getEntry(operator.getName());
        entry.addOperator(operator);
    }

    private OperatorEntry getEntry(String operatorName) {
        if (operatorName == null || operatorName.equals("")) {
            throw new IllegalArgumentException("operatorName is null or empty.");
        }

        OperatorEntry entry = operators.get(operatorName);
        if (entry == null) {
            entry = new OperatorEntry(operatorName);
            operators.put(operatorName, entry);
        }

        return entry;
    }

    public Operator resolveOperator(CallContext callContext, ConversionMap conversionMap) {
        OperatorEntry entry = getEntry(callContext.getOperatorName());
        return entry.resolve(callContext, conversionMap);
    }
}
