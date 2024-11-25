package org.cqframework.cql.cql2elm.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.hl7.cql.model.DataType;
import org.hl7.cql.model.TypeParameter;

public class GenericOperator extends Operator {
    public GenericOperator(String name, Signature signature, DataType resultType, TypeParameter... typeParameters) {
        super(name, signature, resultType);

        // TODO: This constructor really ought to be replacing the TypeParameter references in its signature with copies
        // of the referenced type parameter given here,
        // but the constructor order and signature hiding of the base make that quite difficult here...
        Collections.addAll(this.typeParameters, typeParameters);
    }

    private final List<TypeParameter> typeParameters = new ArrayList<>();

    public Iterable<TypeParameter> getTypeParameters() {
        return this.typeParameters;
    }

    public InstantiationResult instantiate(
            Signature callSignature,
            OperatorMap operatorMap,
            ConversionMap conversionMap,
            boolean allowPromotionAndDemotion) {
        return instantiate(callSignature, null, operatorMap, conversionMap, allowPromotionAndDemotion);
    }

    public InstantiationResult instantiate(
            Signature callSignature,
            Map<TypeParameter, DataType> parameters,
            OperatorMap operatorMap,
            ConversionMap conversionMap,
            boolean allowPromotionAndDemotion) {
        Map<TypeParameter, DataType> typeMap = new HashMap<>();

        for (TypeParameter p : typeParameters) {
            typeMap.put(p, null);
        }

        if (parameters != null) {
            typeMap.putAll(parameters);
        }

        InstantiationContextImpl context =
                new InstantiationContextImpl(typeMap, operatorMap, conversionMap, allowPromotionAndDemotion);

        boolean instantiable = signature.isInstantiable(callSignature, context);
        if (instantiable) {
            Operator result = new Operator(name, signature.instantiate(context), resultType.instantiate(context));
            result.accessLevel = accessLevel;
            result.setLibraryName(getLibraryName());
            return new InstantiationResult(this, result, context.getConversionScore());
        }

        return new InstantiationResult(this, null, context.getConversionScore());
    }
}
