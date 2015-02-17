package org.cqframework.cql.cql2elm.model;

import org.cqframework.cql.elm.tracking.DataType;
import org.cqframework.cql.elm.tracking.TypeParameter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GenericOperator extends Operator {
    public GenericOperator(String name, Signature signature, DataType resultType, TypeParameter... typeParameters) {
        super(name, signature, resultType);

        for (TypeParameter typeParameter : typeParameters) {
            this.typeParameters.add(typeParameter);
        }
    }

    private List<TypeParameter> typeParameters = new ArrayList<>();
    public Iterable<TypeParameter> getTypeParameters() {
        return this.typeParameters;
    }

    public Operator instantiate(Signature callSignature) {
        Map<TypeParameter, DataType> typeMap = new HashMap<>();

        for (TypeParameter p : typeParameters) {
            typeMap.put(p, null);
        }

        Boolean instantiable = getSignature().isInstantiable(callSignature, typeMap);
        if (instantiable) {
            return new Operator(getName(), getSignature().instantiate(typeMap), getResultType().instantiate(typeMap));
        }

        return null;
    }
}
