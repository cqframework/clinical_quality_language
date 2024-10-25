package org.cqframework.cql.cql2elm.model;

import java.util.*;
import org.hl7.cql.model.DataType;
import org.hl7.cql.model.TypeParameter;
import org.hl7.cql.model.WildcardType;

public class GenericOperator extends Operator {
    public GenericOperator(String name, Signature signature, DataType resultType, TypeParameter... typeParameters) {
        super(name, signature, resultType);

        // TODO: This constructor really ought to be replacing the TypeParameter references in its signature with copies
        // of the referenced type parameter given here,
        // but the constructor order and signature hiding of the base make that quite difficult here...
        for (TypeParameter typeParameter : typeParameters) {
            this.typeParameters.add(typeParameter);
        }
    }

    private List<TypeParameter> typeParameters = new ArrayList<>();

    public Iterable<TypeParameter> getTypeParameters() {
        return this.typeParameters;
    }

    public InstantiationResult instantiate(
            Signature callSignature,
            OperatorMap operatorMap,
            ConversionMap conversionMap,
            boolean allowPromotionAndDemotion,
            DataType targetType) {
        return instantiate(callSignature, null, operatorMap, conversionMap, allowPromotionAndDemotion, targetType);
    }

    public InstantiationResult instantiate(
            Signature callSignature,
            Map<TypeParameter, DataType> parameters,
            OperatorMap operatorMap,
            ConversionMap conversionMap,
            boolean allowPromotionAndDemotion,
            DataType targetType) {
        Map<TypeParameter, DataType> typeMap = new HashMap<>();

        for (TypeParameter p : typeParameters) {
            typeMap.put(p, null);
        }

        if (parameters != null) {
            for (Map.Entry<TypeParameter, DataType> entry : parameters.entrySet()) {
                typeMap.put(entry.getKey(), entry.getValue());
            }
        }

        Map<WildcardType, DataType> wildcardMap = new HashMap<>();

        InstantiationContextImpl context = new InstantiationContextImpl(
                typeMap, wildcardMap, operatorMap, conversionMap, allowPromotionAndDemotion, getResultType() instanceof TypeParameter ? (TypeParameter)getResultType() : null, targetType);

        Boolean instantiable = getSignature().isInstantiable(callSignature, context);
        if (instantiable) {
            Operator result = new Operator(
                    getName(),
                    getSignature().instantiate(context),
                    getResultType().instantiate(context));
            result.setAccessLevel(getAccessLevel());
            result.setLibraryName(getLibraryName());
            Signature invocationSignature = getInvocationSignature(callSignature, context);
            return new InstantiationResult(this, result, invocationSignature, context.getConversionScore());
        }

        return new InstantiationResult(this, null, null, context.getConversionScore());
    }

    private Signature getInvocationSignature(Signature callSignature, InstantiationContextImpl context) {
        Iterator<DataType> callSignatureTypes = callSignature.getOperandTypes().iterator();
        DataType[] invocationSignature = new DataType[callSignature.getSize()];
        for (int i = 0; i < callSignature.getSize(); i++) {
            invocationSignature[i] =
                    callSignatureTypes.next().resolveWildcards(context).instantiate(context);
        }
        return new Signature(invocationSignature);
    }
}
