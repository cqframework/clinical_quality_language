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

        public Operator resolve(CallContext callContext, ConversionMap conversionMap) {
            if (operator.getSignature().equals(callContext.getSignature())) {
                return operator;
            }

            Operator result = subSignatures.resolve(callContext, conversionMap);
            if (result == null && operator.getSignature().isSuperTypeOf(callContext.getSignature())) {
                result = operator;
            }

            if (result == null) {
                // Attempt to find a conversion path from the call signature to the target signature
                Operator[] conversions = new Operator[operator.getSignature().getSize()];
                boolean isConvertible = callContext.getSignature().isConvertibleTo(operator.getSignature(), conversionMap, conversions);
                if (isConvertible) {
                    callContext.setConversions(conversions);
                    result = operator;
                }
            }

            return result;
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

        public Operator resolve(CallContext callContext, ConversionMap conversionMap) {
            Operator result = null;

            for (SignatureNode n : signatures.values()) {
                Operator nodeResult = n.resolve(callContext, conversionMap);
                if (nodeResult != null) {
                    if (result != null) {
                        throw new IllegalArgumentException(String.format("Invocation of operator %s with signature %s is ambiguous between %s and %s.",
                                result.getName(), callContext.getSignature(), result.getSignature(), nodeResult.getSignature()));
                    }

                    result = nodeResult;
                }
            }

            return result;
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

    private void addGenericOperator(GenericOperator operator) {
        if (genericOperators.containsKey(operator.getSignature())) {
            throw new IllegalArgumentException(String.format("Operator %s already has a generic registration for signature: %s.", name, operator.getSignature().toString()));
        }

        genericOperators.put(operator.getSignature(), operator);
    }

    public Operator resolve(CallContext callContext, ConversionMap conversionMap) {
        if (callContext == null) {
            throw new IllegalArgumentException("callContext is null");
        }

        Operator result = signatures.resolve(callContext, conversionMap);

        // If there is no resolution, attempt to instantiate a generic signature
        if (result == null) {
            result = instantiate(callContext.getSignature());
            if (result != null) {
                // If the generic signature was instantiated, store it as an actual signature.
                signatures.add(new SignatureNode(result));
            }
        }

        return result;
    }

    private Operator instantiate(Signature signature) {
        List<Operator> instantiations = new ArrayList<Operator>();
        for (GenericOperator genericOperator : genericOperators.values()) {
            Operator instantiation = genericOperator.instantiate(signature);
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
