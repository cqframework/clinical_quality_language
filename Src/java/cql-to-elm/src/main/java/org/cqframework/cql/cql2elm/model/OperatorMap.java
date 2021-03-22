package org.cqframework.cql.cql2elm.model;

import org.hl7.cql.model.*;

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

    public boolean supportsOperator(String libraryName, String operatorName, DataType... signature) {
        CallContext call = new CallContext(libraryName, operatorName, false, false, false, signature);
        try {
            OperatorResolution resolution = resolveOperator(call, null);
            if (resolution == null) {
                return false;
            }
        }
        catch (Exception e) {
            return false;
        }

        return true;
    }

    // Returns true if the given type supports the operations necessary to be the point type of an interval
    // (i.e. comparison, successor, and predecessor)
    public boolean isPointType(DataType type) {
        return supportsOperator("System", "LessOrEqual", type, type) && supportsOperator("System", "Successor", type);
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
                Iterator<DataType> operands = resolution.getOperator().getSignature().getOperandTypes().iterator();
                Iterator<DataType> callOperands = callContext.getSignature().getOperandTypes().iterator();
                Iterator<Conversion> conversions = resolution.hasConversions() ? resolution.getConversions().iterator() : null;
                int score = ConversionMap.ConversionScore.ExactMatch.score();
                while (operands.hasNext()) {
                    DataType operand = operands.next();
                    DataType callOperand = callOperands.next();
                    Conversion conversion = conversions != null ? conversions.next() : null;
                    score += ConversionMap.getConversionScore(callOperand, operand, conversion);
                }

                resolution.setScore(score);

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
                if (callContext.getMustResolve()) {
                    // ERROR:
                    StringBuilder message = new StringBuilder("Call to operator ").append(callContext.getOperatorName())
                            .append(callContext.getSignature()).append(" is ambiguous with: ");
                    for (OperatorResolution resolution : lowestScoringResults) {
                        message.append("\n  - ").append(resolution.getOperator().getName()).append(resolution.getOperator().getSignature());
                    }
                    throw new IllegalArgumentException(message.toString());
                }
                else {
                    return null;
                }
            }
            else {
                result = lowestScoringResults.get(0);
            }
        }

        return result;
    }
}
