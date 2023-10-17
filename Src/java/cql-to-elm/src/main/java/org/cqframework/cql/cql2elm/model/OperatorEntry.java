package org.cqframework.cql.cql2elm.model;

import org.hl7.cql.model.ChoiceType;
import org.hl7.cql.model.DataType;

import java.util.*;

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

        public List<OperatorResolution> resolve(CallContext callContext, ConversionMap conversionMap, OperatorMap operatorMap) {
            List<OperatorResolution> results = null;
            if (operator.getSignature().equals(callContext.getSignature())) {
                results = new ArrayList<>();
                results.add(new OperatorResolution(operator));
                return results;
            }

            results = subSignatures.resolve(callContext, conversionMap, operatorMap);
            if (results == null && operator.getSignature().isSuperTypeOf(callContext.getSignature())) {
                results = new ArrayList<>();
                results.add(new OperatorResolution(operator));
            }

            if (results == null && conversionMap != null) {
                // Attempt to find a conversion path from the call signature to the target signature
                Conversion[] conversions = new Conversion[operator.getSignature().getSize()];
                boolean isConvertible = callContext.getSignature().isConvertibleTo(operator.getSignature(), conversionMap, operatorMap, callContext.getAllowPromotionAndDemotion(), conversions);
                if (isConvertible) {
                    OperatorResolution resolution = new OperatorResolution(operator);
                    resolution.setConversions(conversions);
                    results = new ArrayList<>();
                    results.add(resolution);
                }
            }

            return results;
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
                SignatureNode that = (SignatureNode)o;
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
                throw new IllegalArgumentException(String.format("Operator %s already has a registration for signature: %s.", node.operator.getName(), node.getSignature().toString()));
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

        public List<OperatorResolution> resolve(CallContext callContext, ConversionMap conversionMap, OperatorMap operatorMap) {
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
            return containsGenericOperator((GenericOperator)operator);
        }
        else {
            return signatures.contains(operator);
        }
    }

    public void addOperator(Operator operator) {
        if (operator instanceof GenericOperator) {
            addGenericOperator((GenericOperator)operator);
        }
        else {
            signatures.add(new SignatureNode(operator));
        }
    }

    private boolean containsGenericOperator(GenericOperator operator) {
        return genericOperators.containsKey(operator.getSignature());
    }

    private void addGenericOperator(GenericOperator operator) {
        if (genericOperators.containsKey(operator.getSignature())) {
            throw new IllegalArgumentException(String.format("Operator %s already has a generic registration for signature: %s.", name, operator.getSignature().toString()));
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
                    for (DataType type : ((ChoiceType)operand).getTypes()) {
                        list.add(type);
                    }
                }
                else {
                    list.add(operand);
                }
                operandList.add(list);
            }

            DataType[] result = new DataType[callSignature.getSize()];
            collectSignatures(operandList, result, 0, signatures);
        }
        else {
            signatures.add(callSignature);
        }
        return signatures;
    }

    private void collectSignatures(ArrayList<ArrayList<DataType>> operandList, DataType[] result, int k, List<Signature> signatures) {
        if (k == operandList.size()) {
            signatures.add(new Signature(result));
        }
        else {
            for (int j = 0; j < operandList.get(k).size(); j++) {
                result[k] = operandList.get(k).get(j);
                collectSignatures(operandList, result, k + 1, signatures);
            }
        }
    }

    public List<OperatorResolution> resolve(CallContext callContext, OperatorMap operatorMap, ConversionMap conversionMap) {
        if (callContext == null) {
            throw new IllegalArgumentException("callContext is null");
        }

        List<OperatorResolution> results = signatures.resolve(callContext, conversionMap, operatorMap);

        // If there is no resolution, or all resolutions require conversion, attempt to instantiate a generic signature
        if (results == null || allResultsUseConversion(results)) {
            // If the callContext signature contains choices, attempt instantiation with all possible combinations of the call signature (ouch, this could really hurt...)
            boolean signaturesInstantiated = false;
            List<Signature> callSignatures = expandChoices(callContext.getSignature());
            for (Signature callSignature : callSignatures) {
                Operator result = instantiate(callSignature, operatorMap, conversionMap, callContext.getAllowPromotionAndDemotion());
                if (result != null && !signatures.contains(result)) {
                    // If the generic signature was instantiated, store it as an actual signature.
                    signatures.add(new SignatureNode(result));
                    signaturesInstantiated = true;
                }
            }

            // re-attempt the resolution with the instantiated signature registered
            if (signaturesInstantiated) {
                results = signatures.resolve(callContext, conversionMap, operatorMap);
            }
        }

        return results;
    }

    private Operator instantiate(Signature signature, OperatorMap operatorMap, ConversionMap conversionMap, boolean allowPromotionAndDemotion) {
        List<Operator> instantiations = new ArrayList<Operator>();
        int lowestConversionScore = Integer.MAX_VALUE;
        Operator instantiation = null;
        for (GenericOperator genericOperator : genericOperators.values()) {
            InstantiationResult instantiationResult = genericOperator.instantiate(signature, operatorMap, conversionMap, allowPromotionAndDemotion);
            if (instantiationResult.getOperator() != null) {
                if (instantiationResult.getConversionScore() <= lowestConversionScore) {
                    if (instantiation == null || instantiationResult.getConversionScore() < lowestConversionScore) {
                        instantiation = instantiationResult.getOperator();
                        lowestConversionScore = instantiationResult.getConversionScore();
                    }
                    else {
                        throw new IllegalArgumentException(String.format("Ambiguous generic instantiation of operator %s between signature %s and %s.",
                                this.name, instantiation.getSignature().toString(), instantiationResult.getOperator().getSignature().toString()));
                    }
                }
            }
        }

        return instantiation;
    }
}
