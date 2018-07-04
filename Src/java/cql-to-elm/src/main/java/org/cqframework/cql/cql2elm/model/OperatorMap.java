package org.cqframework.cql.cql2elm.model;

import org.hl7.cql.model.*;

import java.util.*;
import java.util.jar.Pack200;

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
        CallContext call = new CallContext(libraryName, operatorName, false, signature);
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
                    if (operand.equals(callOperand)) {
                        score += ConversionMap.ConversionScore.ExactMatch.score();
                    }
                    else if (operand.isSuperTypeOf(callOperand)) {
                        score += ConversionMap.ConversionScore.SubType.score();
                    }
                    else if (callOperand.isCompatibleWith(operand)) {
                        score += ConversionMap.ConversionScore.Compatible.score();
                    }
                    else if (conversion != null) {
                        if (conversion.isCast()) {
                            score += ConversionMap.ConversionScore.Cast.score();
                        }
                        else if (conversion.isIntervalDemotion()) {
                            score += ConversionMap.ConversionScore.IntervalDemotion.score();
                        }
                        else if (conversion.isListDemotion()) {
                            score += ConversionMap.ConversionScore.ListDemotion.score();
                        }
                        else if (conversion.isIntervalPromotion()) {
                            score += ConversionMap.ConversionScore.IntervalPromotion.score();
                        }
                        else if (conversion.isListPromotion()) {
                            score += ConversionMap.ConversionScore.ListPromotion.score();
                        }
                        else if (conversion.isListConversion()) {
                            if (((ListType)conversion.getToType()).getElementType() instanceof SimpleType) {
                                score += ConversionMap.ConversionScore.SimpleConversion.score();
                            }
                            else {
                                score += ConversionMap.ConversionScore.ComplexConversion.score();
                            }
                        }
                        else if (conversion.isIntervalConversion()) {
                            if (((IntervalType)conversion.getToType()).getPointType() instanceof SimpleType) {
                                score += ConversionMap.ConversionScore.SimpleConversion.score();
                            }
                            else {
                                score += ConversionMap.ConversionScore.ComplexConversion.score();
                            }
                        }
                        else if (conversion.getToType() instanceof ClassType) {
                            score += ConversionMap.ConversionScore.ComplexConversion.score();
                        }
                        else {
                            score += ConversionMap.ConversionScore.SimpleConversion.score();
                        }
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
                // ERROR:
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
