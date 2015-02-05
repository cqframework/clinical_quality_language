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

    private Map<Signature, Operator> operators = new HashMap<>();
    private Map<Signature, GenericOperator> genericOperators = new HashMap<>();

    public void addOperator(Operator operator) {
        if (operator instanceof GenericOperator) {
            addGenericOperator((GenericOperator)operator);
        }
        else {
            if (operators.containsKey(operator.getSignature())) {
                throw new IllegalArgumentException(String.format("Operator %s already has a registration for signature: %s.", name, operator.getSignature().toString()));
            }

            operators.put(operator.getSignature(), operator);
        }
    }

    private void addGenericOperator(GenericOperator operator) {
        if (genericOperators.containsKey(operator.getSignature())) {
            throw new IllegalArgumentException(String.format("Operator %s already has a generic registration for signature: %s.", name, operator.getSignature().toString()));
        }

        genericOperators.put(operator.getSignature(), operator);
    }

    public Operator resolve(Signature signature) {
        if (signature == null) {
            throw new IllegalArgumentException("signature is null");
        }

        Operator result = null;
        for (Operator o : operators.values()) {
            if (o.getSignature().isSuperTypeOf(signature)) {
                if (result != null) {
                    throw new IllegalArgumentException(String.format("Invocation of operator %s with signature %s is ambiguous between %s and %s.",
                            this.name, signature, result.getSignature(), o.getSignature()));
                }
                result = o;
            }
        }

        // If there is no resolution, attempt to instantiate a generic signature
        if (result == null) {
            result = instantiate(signature);
            if (result != null) {
                // If the generic signature was instantiated, store it as an actual signature.
                operators.put(result.getSignature(), result);
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
