package org.cqframework.cql.cql2elm.model;

import org.cqframework.cql.elm.tracking.DataType;
import org.cqframework.cql.elm.tracking.TypeParameter;

import java.util.*;

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

    public OperatorResolution resolveOperator(CallContext callContext, ConversionMap conversionMap) {
        OperatorEntry entry = getEntry(callContext.getOperatorName());
        List<OperatorResolution> results = entry.resolve(callContext, conversionMap);

        // If there is a resolution, but it involves generic conversion operators, instantiate the generic conversions
        if (results != null) {
            for (OperatorResolution resolution : results) {
                if (resolution.hasConversions()) {
                    Iterator<DataType> operands = resolution.getOperator().getSignature().getOperandTypes().iterator();
                    Iterator<DataType> callOperands = callContext.getSignature().getOperandTypes().iterator();
                    Iterator<Conversion> conversions = resolution.getConversions().iterator();
                    List<Conversion> instantiatedConversions = new ArrayList<>();
                    boolean anyInstantiated = false;
                    while (operands.hasNext()) {
                        DataType operand = operands.next();
                        DataType callOperand = callOperands.next();
                        Conversion conversion = conversions.next();
                        if (conversion != null && conversion.getOperator() instanceof GenericOperator) {
                            GenericOperator genericOperator = (GenericOperator)conversion.getOperator();
                            Map<TypeParameter, DataType> typeParameters = new HashMap<>();

                            DataType sourceType = genericOperator.getSignature().getOperandTypes().iterator().next(); // Assumes unary, enforced by the conversion map
                            if (sourceType instanceof TypeParameter) {
                                typeParameters.put((TypeParameter)sourceType, callOperand);
                            }

                            DataType targetType = genericOperator.getResultType();
                            if (targetType instanceof TypeParameter) {
                                typeParameters.put((TypeParameter)targetType, operand);
                            }

                            Operator instantiatedOperator = genericOperator.instantiate(new Signature(callOperand), typeParameters, conversionMap);
                            Conversion instantiatedConversion = new Conversion(instantiatedOperator, conversion.isImplicit());
                            instantiatedConversions.add(instantiatedConversion);

                            // Add the instantiated operator to the operator map
                            this.addOperator(instantiatedOperator);

                            // Add the instantiated conversion to the conversion map
                            conversionMap.add(instantiatedConversion);

                            anyInstantiated = true;
                        }
                        else {
                            instantiatedConversions.add(conversion);
                        }
                    }

                    if (anyInstantiated) {
                        resolution.setConversions(instantiatedConversions);
                    }
                }
            }
        }

        // Score each resolution and return the lowest score
        // Duplicate scores indicate ambiguous match
        OperatorResolution result = null;
        if (results != null) {
            int lowestScore = Integer.MAX_VALUE;
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
                    result = resolution;
                }
                else if (score == lowestScore) {
                    throw new IllegalArgumentException(String.format("Call to operator %s with signature %s is ambiguous between %s and %s.",
                            callContext.getOperatorName(), callContext.getSignature(),
                            result.getOperator().getSignature(), resolution.getOperator().getSignature()));
                }
            }
        }

        return result;
    }
}
