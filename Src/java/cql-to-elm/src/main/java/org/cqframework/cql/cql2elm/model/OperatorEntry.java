package org.cqframework.cql.cql2elm.model;

import java.util.*;
import org.hl7.cql.model.ChoiceType;
import org.hl7.cql.model.DataType;

public class OperatorEntry {
    public OperatorEntry(String name) {
        if (name == null || name.equals("")) {
            throw new IllegalArgumentException("name is null or empty");
        }

        this.name = name;
    }

    private String name;

    public String getName() {
        return this.name;
    }

    private SignatureNodes signatures = new SignatureNodes();
    private Map<Signature, GenericOperator> genericOperators = new HashMap<>();

    private static class SignatureNode {
        public SignatureNode(Operator operator) {
            if (operator == null) {
                throw new IllegalArgumentException("operator is null.");
            }

            this.operator = operator;
        }

        private Operator operator;

        public Operator getOperator() {
            return operator;
        }

        public Signature getSignature() {
            return operator.getSignature();
        }

        /*
        The invocation signature is the call signature with arguments of type Any set to the operand types
         */
        private Signature getInvocationSignature(Signature callSignature, Signature operatorSignature) {
            if (callSignature.getSize() == operatorSignature.getSize()) {
                DataType[] invocationTypes = new DataType[callSignature.getSize()];
                Iterator<DataType> callTypes = callSignature.getOperandTypes().iterator();
                Iterator<DataType> operatorTypes =
                        operatorSignature.getOperandTypes().iterator();
                boolean isResolved = false;
                for (int i = 0; i < invocationTypes.length; i++) {
                    DataType callType = callTypes.next();
                    DataType operatorType = operatorTypes.next();
                    if (callType.equals(DataType.ANY) && !operatorType.equals(DataType.ANY)) {
                        isResolved = true;
                        invocationTypes[i] = operatorType;
                    } else {
                        invocationTypes[i] = callType;
                    }
                }
                if (isResolved) {
                    return new Signature(invocationTypes);
                }
            }
            return callSignature;
        }

        private OperatorResolution getOperatorResolution(
                Operator operator,
                Signature callSignature,
                Signature invocationSignature,
                ConversionMap conversionMap,
                OperatorMap operatorMap,
                boolean allowPromotionAndDemotion,
                boolean requireConversions) {
            Conversion[] conversions = getConversions(
                    callSignature, operator.getSignature(), conversionMap, operatorMap, allowPromotionAndDemotion);
            OperatorResolution result = new OperatorResolution(operator, conversions);
            if (requireConversions && conversions == null) {
                return null;
            }
            return result;
        }

        public List<OperatorResolution> resolve(
                CallContext callContext, ConversionMap conversionMap, OperatorMap operatorMap) {
            List<OperatorResolution> results = null;
            Signature invocationSignature = getInvocationSignature(callContext.getSignature(), operator.getSignature());

            // Attempt exact match against this signature
            if (operator.getSignature().equals(invocationSignature)) {
                OperatorResolution result = getOperatorResolution(
                        operator,
                        callContext.getSignature(),
                        invocationSignature,
                        conversionMap,
                        operatorMap,
                        callContext.isAllowPromotionAndDemotion(),
                        false);
                if (result != null) {
                    results = new ArrayList<>();
                    results.add(result);
                    return results;
                }
            }

            // Attempt to resolve against sub signatures
            results = subSignatures.resolve(callContext, conversionMap, operatorMap);

            // If no subsignatures match, attempt subType match against this signature
            if (results == null && operator.getSignature().isSuperTypeOf(invocationSignature)) {
                OperatorResolution result = getOperatorResolution(
                        operator,
                        callContext.getSignature(),
                        invocationSignature,
                        conversionMap,
                        operatorMap,
                        callContext.isAllowPromotionAndDemotion(),
                        false);
                if (result != null) {
                    results = new ArrayList<>();
                    results.add(result);
                    return results;
                }
            }

            if (results == null && conversionMap != null) {
                // Attempt to find a conversion path from the call signature to the target signature
                OperatorResolution result = getOperatorResolution(
                        operator,
                        callContext.getSignature(),
                        invocationSignature,
                        conversionMap,
                        operatorMap,
                        callContext.isAllowPromotionAndDemotion(),
                        true);
                if (result != null) {
                    if (results == null) {
                        results = new ArrayList<>();
                    }
                    results.add(result);
                }
            }

            return results;
        }

        private Conversion[] getConversions(
                Signature callSignature,
                Signature operatorSignature,
                ConversionMap conversionMap,
                OperatorMap operatorMap,
                boolean allowPromotionAndDemotion) {
            if (callSignature == null
                    || operatorSignature == null
                    || callSignature.getSize() != operatorSignature.getSize()) {
                return null;
            }

            Conversion[] conversions = new Conversion[callSignature.getSize()];
            boolean isConvertible = callSignature.isConvertibleTo(
                    operatorSignature, conversionMap, operatorMap, allowPromotionAndDemotion, conversions);

            if (isConvertible) {
                return conversions;
            }

            return null;
        }

        private SignatureNodes subSignatures = new SignatureNodes();

        public boolean hasSubSignatures() {
            return subSignatures.hasSignatures();
        }

        @Override
        public int hashCode() {
            return operator.getSignature().hashCode();
        }

        @Override
        public boolean equals(Object o) {
            if (o instanceof SignatureNode) {
                SignatureNode that = (SignatureNode) o;
                return this.operator.getName().equals(that.operator.getName())
                        && this.getSignature().equals(that.getSignature());
            }

            return false;
        }

        public String toString() {
            return operator.toString();
        }
    }

    private static class SignatureNodes {
        private Map<Signature, SignatureNode> signatures = new HashMap<>();

        public boolean hasSignatures() {
            return signatures.size() > 0;
        }

        public boolean contains(Operator operator) {
            boolean result = signatures.containsKey(operator.getSignature());
            if (!result) {
                for (SignatureNode n : signatures.values()) {
                    result = n.subSignatures.contains(operator);
                    if (result) {
                        break;
                    }
                }
            }

            return result;
        }

        public void add(SignatureNode node) {
            if (node == null) {
                throw new IllegalArgumentException("node is null.");
            }

            if (signatures.containsKey(node.getSignature())) {
                throw new IllegalArgumentException(String.format(
                        "Operator %s already has a registration for signature: %s.",
                        node.operator.getName(), node.getSignature().toString()));
            }

            boolean added = false;
            for (SignatureNode n : signatures.values()) {
                if (n.getSignature().isSuperTypeOf(node.getSignature())) {
                    n.subSignatures.add(node);
                    added = true;
                    break;
                }
            }

            if (!added) {
                for (SignatureNode n : signatures.values().toArray(new SignatureNode[signatures.size()])) {
                    if (node.getSignature().isSuperTypeOf(n.getSignature())) {
                        signatures.remove(n.getSignature());
                        node.subSignatures.add(n);
                    }
                }

                signatures.put(node.getSignature(), node);
            }
        }

        public List<OperatorResolution> resolve(
                CallContext callContext, ConversionMap conversionMap, OperatorMap operatorMap) {
            ArrayList<OperatorResolution> results = null;

            int signatureCount = 0;
            for (SignatureNode n : signatures.values()) {

                if (n.getSignature().getSize() == callContext.getSignature().getSize()) {
                    signatureCount++;

                    // Any subSignature will count as an overload
                    if (n.hasSubSignatures()) {
                        signatureCount++;
                    }
                }

                List<OperatorResolution> nodeResults = n.resolve(callContext, conversionMap, operatorMap);
                if (nodeResults != null) {
                    if (results == null) {
                        results = new ArrayList<>();
                    }
                    results.addAll(nodeResults);
                }
            }

            if (results != null && signatureCount > 1) {
                for (OperatorResolution result : results) {
                    result.setOperatorHasOverloads();
                }
            }

            return results;
        }
    }

    public boolean containsOperator(Operator operator) {
        if (operator instanceof GenericOperator) {
            return containsGenericOperator((GenericOperator) operator);
        } else {
            return signatures.contains(operator);
        }
    }

    public void addOperator(Operator operator) {
        if (operator instanceof GenericOperator) {
            addGenericOperator((GenericOperator) operator);
        } else {
            signatures.add(new SignatureNode(operator));
        }
    }

    private boolean containsGenericOperator(GenericOperator operator) {
        return genericOperators.containsKey(operator.getSignature());
    }

    private void addGenericOperator(GenericOperator operator) {
        if (genericOperators.containsKey(operator.getSignature())) {
            throw new IllegalArgumentException(String.format(
                    "Operator %s already has a generic registration for signature: %s.",
                    name, operator.getSignature().toString()));
        }

        genericOperators.put(operator.getSignature(), operator);
    }

    private boolean allResultsUseConversion(List<OperatorResolution> results) {
        for (OperatorResolution resolution : results) {
            if (!resolution.hasConversions()) {
                return false;
            }
        }

        return true;
    }

    public List<Signature> expandChoices(Signature callSignature) {
        ArrayList<Signature> signatures = new ArrayList<Signature>();
        if (callSignature.containsChoices()) {

            ArrayList<ArrayList<DataType>> operandList = new ArrayList<ArrayList<DataType>>();
            for (DataType operand : callSignature.getOperandTypes()) {
                ArrayList<DataType> list = new ArrayList<DataType>();
                if (operand instanceof ChoiceType) {
                    for (DataType type : ((ChoiceType) operand).getTypes()) {
                        list.add(type);
                    }
                } else {
                    list.add(operand);
                }
                operandList.add(list);
            }

            DataType[] result = new DataType[callSignature.getSize()];
            collectSignatures(operandList, result, 0, signatures);
        } else {
            signatures.add(callSignature);
        }
        return signatures;
    }

    private void collectSignatures(
            ArrayList<ArrayList<DataType>> operandList, DataType[] result, int k, List<Signature> signatures) {
        if (k == operandList.size()) {
            signatures.add(new Signature(result));
        } else {
            for (int j = 0; j < operandList.get(k).size(); j++) {
                result[k] = operandList.get(k).get(j);
                collectSignatures(operandList, result, k + 1, signatures);
            }
        }
    }

    public List<OperatorResolution> resolve(
            CallContext callContext, OperatorMap operatorMap, ConversionMap conversionMap) {
        if (callContext == null) {
            throw new IllegalArgumentException("callContext is null");
        }

        // Attempt to instantiate any generic signatures
        // If the callContext signature contains choices, attempt instantiation with all possible combinations of
        // the call signature (ouch, this could really hurt...)
        boolean signaturesInstantiated = false;
        List<Signature> callSignatures = expandChoices(callContext.getSignature());
        for (Signature callSignature : callSignatures) {
            List<Operator> instantiations =
                    instantiate(callSignature, operatorMap, conversionMap, callContext.isAllowPromotionAndDemotion());
            for (Operator instantiation : instantiations) {
                // If the generic signature was instantiated, store it as an actual signature.
                if (!signatures.contains(instantiation)) {
                    signatures.add(new SignatureNode(instantiation));
                }
            }
        }

        return signatures.resolve(callContext, conversionMap, operatorMap);
    }

    private List<Operator> instantiate(
            Signature signature,
            OperatorMap operatorMap,
            ConversionMap conversionMap,
            boolean allowPromotionAndDemotion) {
        List<Operator> instantiations = new ArrayList<Operator>();

        for (GenericOperator genericOperator : genericOperators.values()) {
            InstantiationResult instantiationResult =
                    genericOperator.instantiate(signature, operatorMap, conversionMap, allowPromotionAndDemotion);
            if (instantiationResult.getOperator() != null) {
                instantiations.add(instantiationResult.getOperator());
            }
        }

        return instantiations;
    }
}
