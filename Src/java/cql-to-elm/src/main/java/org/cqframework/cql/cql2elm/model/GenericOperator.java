package org.cqframework.cql.cql2elm.model;

import org.hl7.cql.model.DataType;
import org.hl7.cql.model.InstantiationContext;
import org.hl7.cql.model.TypeParameter;

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

    public Operator instantiate(Signature callSignature, ConversionMap conversionMap) {
        return instantiate(callSignature, null, conversionMap);
    }

    public Operator instantiate(Signature callSignature, Map<TypeParameter, DataType> parameters, ConversionMap conversionMap) {
        Map<TypeParameter, DataType> typeMap = new HashMap<>();

        for (TypeParameter p : typeParameters) {
            typeMap.put(p, null);
        }

        if (parameters != null) {
            for (Map.Entry<TypeParameter,DataType> entry : parameters.entrySet()) {
                typeMap.put(entry.getKey(), entry.getValue());
            }
        }

        InstantiationContext context = new InstantiationContextImpl(typeMap, conversionMap);

        Boolean instantiable = getSignature().isInstantiable(callSignature, context);
        if (instantiable) {
            Operator result = new Operator(getName(), getSignature().instantiate(context), getResultType().instantiate(context));
            result.setAccessLevel(getAccessLevel());
            result.setLibraryName(getLibraryName());
            return result;
        }

        return null;
    }
}
