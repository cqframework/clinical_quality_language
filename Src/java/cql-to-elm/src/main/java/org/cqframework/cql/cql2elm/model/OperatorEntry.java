package org.cqframework.cql.cql2elm.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        public List<OperatorResolution> resolve(CallContext callContext, ConversionMap conversionMap) {
            List<OperatorResolution> results = null;
            if (operator.getSignature().equals(callContext.getSignature())) {
                results = new ArrayList<>();
                results.add(new OperatorResolution(operator));
                return results;
            }

            results = subSignatures.resolve(callContext, conversionMap);
            if (results == null && operator.getSignature().isSuperTypeOf(callContext.getSignature())) {
                results = new ArrayList<>();
                results.add(new OperatorResolution(operator));
            }

            if (results == null && conversionMap != null) {
                // Attempt to find a conversion path from the call signature to the target signature
                Conversion[] conversions = new Conversion[operator.getSignature().getSize()];
                boolean isConvertible = callContext.getSignature().isConvertibleTo(operator.getSignature(), conversionMap, conversions);
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

        public boolean contains(Operator operator) {
            return signatures.containsKey(operator.getSignature());
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

        public List<OperatorResolution> resolve(CallContext callContext, ConversionMap conversionMap) {
            ArrayList<OperatorResolution> results = null;

            for (SignatureNode n : signatures.values()) {
                List<OperatorResolution> nodeResults = n.resolve(callContext, conversionMap);
                if (nodeResults != null) {
                    if (results == null) {
                        results = new ArrayList();
                    }
                    results.addAll(nodeResults);
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

    public List<OperatorResolution> resolve(CallContext callContext, ConversionMap conversionMap) {
        if (callContext == null) {
            throw new IllegalArgumentException("callContext is null");
        }

        List<OperatorResolution> results = signatures.resolve(callContext, conversionMap);

        // If there is no resolution, or all resolutions require conversion, attempt to instantiate a generic signature
        if (results == null || allResultsUseConversion(results)) {
            Operator result = instantiate(callContext.getSignature(), conversionMap);
            if (result != null && !signatures.contains(result)) {
                // If the generic signature was instantiated, store it as an actual signature.
                signatures.add(new SignatureNode(result));
            }

            // re-attempt the resolution with the instantiated signature registered
            results = signatures.resolve(callContext, conversionMap);
        }



        return results;
    }

    private Operator instantiate(Signature signature, ConversionMap conversionMap) {
        List<Operator> instantiations = new ArrayList<Operator>();
        for (GenericOperator genericOperator : genericOperators.values()) {
            Operator instantiation = genericOperator.instantiate(signature, conversionMap);
            if (instantiation != null) {
                instantiations.add(instantiation);
            }
        }

        switch (instantiations.size()) {
            case 0 : return null;
            case 1 : return instantiations.get(0);
            default : throw new IllegalArgumentException(String.format("Ambiguous generic instantiation of operator %s with signature %s.", this.name, signature.toString()));
        }
    }
}
