package org.cqframework.cql.cql2elm.model;

import org.hl7.cql.model.DataType;

import java.util.*;

public class OperatorMap {
    private Map<String, OperatorEntry> operators = new HashMap<>();

    public boolean containsOperator(Operator operator) {
        OperatorEntry entry = getEntry(operator.getName());
        return entry.containsOperator(operator);
    }

    public void addOperator(Operator operator) {
        OperatorEntry entry = getEntry(operator.getName());
        entry.addOperator(operator);
    }

    public boolean containsOperator(Operator operator) {
        OperatorEntry entry = getEntry(operator.getName());
        return entry.containsOperator(operator);
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

    public OperatorResolution resolveOperator(CallContext callContext, ConversionMap conversionMap) {
        OperatorEntry entry = getEntry(callContext.getOperatorName());
        List<OperatorResolution> results = entry.resolve(callContext, this, conversionMap);

        // Score each resolution and return the lowest score
        // Duplicate scores indicate ambiguous match
        OperatorResolution result = null;
        if (results != null) {
            int lowestScore = Integer.MAX_VALUE;
            List<OperatorResolution> lowestScoringResults = new ArrayList<>();
            for (OperatorResolution resolution : results) {
                // for each operand
                    // exact match = 0
                    // subtype = 1
                    // conversion = 2
                Iterator<DataType> operands = resolution.getOperator().getSignature().getOperandTypes().iterator();
                Iterator<DataType> callOperands = callContext.getSignature().getOperandTypes().iterator();
                Iterator<Conversion> conversions = resolution.hasConversions() ? resolution.getConversions().iterator() : null;
                int score = 0;
                while (operands.hasNext()) {
                    DataType operand = operands.next();
                    DataType callOperand = callOperands.next();
                    Conversion conversion = conversions != null ? conversions.next() : null;
                    if (operand.equals(callOperand)) {
                        score += 0;
                    }
                    else if (operand.isSuperTypeOf(callOperand)) {
                        score += 1;
                    }
                    else if (conversion != null) {
                        score += 2;
                    }
                }

                if (score < lowestScore) {
                    lowestScore = score;
                    lowestScoringResults.clear();
                    lowestScoringResults.add(resolution);
                }
                else if (score == lowestScore) {
                    lowestScoringResults.add(resolution);
                }
            }

            if (lowestScoringResults.size() > 1) {
                StringBuilder message = new StringBuilder("Call to operator ").append(callContext.getOperatorName())
                        .append(callContext.getSignature()).append(" is ambiguous with: ");
                for (OperatorResolution resolution : lowestScoringResults) {
                    message.append("\n  - ").append(resolution.getOperator().getName()).append(resolution.getOperator().getSignature());
                }
                throw new IllegalArgumentException(message.toString());
            }
            else {
                result = lowestScoringResults.get(0);
            }
        }

        return result;
    }
}
