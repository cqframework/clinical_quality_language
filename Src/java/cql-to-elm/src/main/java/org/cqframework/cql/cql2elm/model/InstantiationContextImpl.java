package org.cqframework.cql.cql2elm.model;

import java.util.ArrayList;
import java.util.Map;
import org.hl7.cql.model.*;

public class InstantiationContextImpl extends ResolutionContextImpl implements InstantiationContext, ResolutionContext {
    public InstantiationContextImpl(
            Map<TypeParameter, DataType> typeMap,
            Map<WildcardType, DataType> wildcardMap,
            OperatorMap operatorMap,
            ConversionMap conversionMap,
            boolean allowPromotionAndDemotion) {
        super(wildcardMap);

        if (typeMap == null) {
            throw new IllegalArgumentException("typeMap is null");
        }

        if (operatorMap == null) {
            throw new IllegalArgumentException("operatorMap is null");
        }

        if (conversionMap == null) {
            throw new IllegalArgumentException("conversionMap is null");
        }

        this.typeMap = typeMap;
        this.operatorMap = operatorMap;
        this.conversionMap = conversionMap;
        this.allowPromotionAndDemotion = allowPromotionAndDemotion;
    }

    private Map<TypeParameter, DataType> typeMap;
    private OperatorMap operatorMap;
    private ConversionMap conversionMap;
    private boolean allowPromotionAndDemotion;
    private int conversionScore;

    public int getConversionScore() {
        return conversionScore;
    }

    @Override
    public boolean isInstantiable(TypeParameter parameter, DataType callType) {
        DataType boundType = typeMap.get(parameter);

        // If the call type is a wildcard, bind it to the type parameter, then use the bound type
        // If there is no bound type, use Any as the call type
        if (callType instanceof WildcardType) {
            matchWildcard(((WildcardType) callType), parameter);
            if (boundType == null) {
                callType = DataType.ANY;
            } else {
                callType = boundType;
            }
        }

        // If the type is not yet bound, bind it to the call type.
        if (boundType == null) {
            if (parameter.canBind(callType)) {
                typeMap.put(parameter, callType);
                return true;
            } else {
                return false;
            }
        } else {
            // If the type is bound, and is a super type of the call type, return true;
            if (boundType.isSuperTypeOf(callType) || callType.isCompatibleWith(boundType)) {
                return true;
            } else if (callType.isSuperTypeOf(boundType) || boundType.isCompatibleWith(callType)) {
                // If the call type is a super type of the bound type, switch the bound type for this parameter to the
                // call type
                if (parameter.canBind(callType)) {
                    typeMap.put(parameter, callType);
                    return true;
                } else {
                    return false;
                }
            } else {
                // If there is an implicit conversion path from the call type to the bound type, return true
                Conversion conversion =
                        conversionMap.findConversion(callType, boundType, true, allowPromotionAndDemotion, operatorMap);
                if (conversion != null) {
                    // if the conversion is a list promotion, switch the bound type to the call type
                    if (boundType instanceof ListType) {
                        ListType boundListType = (ListType) boundType;
                        if (boundListType.getElementType().isSuperTypeOf(callType)
                                || callType.isCompatibleWith(boundListType.getElementType())) {
                            if (parameter.canBind(callType)) {
                                typeMap.put(parameter, callType);
                                conversionScore -=
                                        ConversionMap.ConversionScore.ListPromotion
                                                .score(); // This removes the list promotion
                                return true;
                            } else {
                                return false;
                            }
                        }
                    }
                    return true;
                }

                // If there is an implicit conversion path from the bound type to the call type
                conversion =
                        conversionMap.findConversion(boundType, callType, true, allowPromotionAndDemotion, operatorMap);
                if (conversion != null) {
                    // switch the bound type to the call type and return true
                    if (parameter.canBind(callType)) {
                        typeMap.put(parameter, callType);
                        conversionScore -= ((conversion.getToType() instanceof SimpleType)
                                ? ConversionMap.ConversionScore.SimpleConversion.score()
                                : ConversionMap.ConversionScore.ComplexConversion
                                        .score()); // This removes the conversion from the instantiation
                        return true;
                    } else {
                        return false;
                    }
                }

                // Find the first supertype that is a supertype of both types
                /*
                // This code doesn't play well with generic signatures... it ends up treating everything like an Any, resulting in all sorts of surprising resolutions
                DataType boundCommonSuperType = boundType.getCommonSuperTypeOf(callType);
                DataType callCommonSuperType = callType.getCommonSuperTypeOf(boundType);
                if (boundCommonSuperType != null && callCommonSuperType != null) {
                    if (boundCommonSuperType.isSuperTypeOf(callCommonSuperType)) {
                        if (parameter.canBind(boundCommonSuperType)) {
                            typeMap.put(parameter, boundCommonSuperType);
                            return true;
                        }
                        else {
                            return false;
                        }
                    }
                    else {
                        if (parameter.canBind(callCommonSuperType)) {
                            typeMap.put(parameter, callCommonSuperType);
                            return true;
                        }
                        else {
                            return false;
                        }
                    }
                }
                */
            }
        }

        return false;
    }

    @Override
    public DataType instantiate(TypeParameter parameter) {
        DataType result = typeMap.get(parameter);
        if (result == null) {
            throw new IllegalArgumentException(
                    String.format("Could not resolve type parameter %s.", parameter.getIdentifier()));
        }

        return result;
    }

    @Override
    public Iterable<IntervalType> getIntervalConversionTargets(DataType callType) {
        ArrayList<IntervalType> results = new ArrayList<IntervalType>();
        for (Conversion c : conversionMap.getConversions(callType)) {
            if (c.getToType() instanceof IntervalType) {
                results.add((IntervalType) c.getToType());
                conversionScore += ConversionMap.ConversionScore.ComplexConversion.score();
            }
        }

        if (results.isEmpty()) {
            for (Conversion c : conversionMap.getGenericConversions()) {
                if (c.getOperator() != null) {
                    if (c.getToType() instanceof IntervalType) {
                        // instantiate the generic...
                        InstantiationResult instantiationResult = ((GenericOperator) c.getOperator())
                                .instantiate(new Signature(callType), operatorMap, conversionMap, false);
                        Operator operator = instantiationResult.getOperator();
                        // TODO: Consider impact of conversion score of the generic instantiation on this conversion
                        // score
                        if (operator != null) {
                            operatorMap.addOperator(operator);
                            Conversion conversion = new Conversion(operator, true);
                            conversionMap.add(conversion);
                            results.add((IntervalType) conversion.getToType());
                        }
                    }
                }
            }
        }

        // Add interval promotion if no other conversion is found
        if (results.isEmpty()) {
            if (!(callType instanceof IntervalType)
                    && operatorMap.isPointType(callType)
                    && (allowPromotionAndDemotion || conversionMap.isIntervalPromotionEnabled())) {
                results.add(new IntervalType(callType));
                conversionScore += ConversionMap.ConversionScore.IntervalPromotion.score();
            }
        }

        return results;
    }

    @Override
    public Iterable<ListType> getListConversionTargets(DataType callType) {
        ArrayList<ListType> results = new ArrayList<ListType>();
        for (Conversion c : conversionMap.getConversions(callType)) {
            if (c.getToType() instanceof ListType) {
                results.add((ListType) c.getToType());
                conversionScore += ConversionMap.ConversionScore.ComplexConversion.score();
            }
        }

        if (results.isEmpty()) {
            for (Conversion c : conversionMap.getGenericConversions()) {
                if (c.getOperator() != null) {
                    if (c.getToType() instanceof ListType) {
                        // instantiate the generic...
                        InstantiationResult instantiationResult = ((GenericOperator) c.getOperator())
                                .instantiate(new Signature(callType), operatorMap, conversionMap, false);
                        Operator operator = instantiationResult.getOperator();
                        // TODO: Consider impact of conversion score of the generic instantiation on this conversion
                        // score
                        if (operator != null) {
                            operatorMap.addOperator(operator);
                            Conversion conversion = new Conversion(operator, true);
                            conversionMap.add(conversion);
                            results.add((ListType) conversion.getToType());
                        }
                    }
                }
            }
        }

        // NOTE: FHIRPath support
        // Add list promotion if no other conversion is found
        if (results.isEmpty()) {
            if (!(callType instanceof ListType)) {
                if (allowPromotionAndDemotion || conversionMap.isListPromotionEnabled()) {
                    results.add(new ListType(callType));
                    conversionScore += ConversionMap.ConversionScore.ListPromotion.score();
                }
                // else if (callType.equals(DataType.ANY)) {
                //    results.add(new ListType(callType));
                //    conversionScore += ConversionMap.ConversionScore.Compatible.score();
                // }
            }
        }

        return results;
    }

    @Override
    public Iterable<SimpleType> getSimpleConversionTargets(DataType callType) {
        ArrayList<SimpleType> results = new ArrayList<SimpleType>();
        for (Conversion c : conversionMap.getConversions(callType)) {
            if (c.getToType() instanceof SimpleType) {
                results.add((SimpleType) c.getToType());
                conversionScore += ConversionMap.ConversionScore.SimpleConversion.score();
            }
        }

        if (results.isEmpty()) {
            for (Conversion c : conversionMap.getGenericConversions()) {
                if (c.getOperator() != null) {
                    if (c.getToType() instanceof SimpleType) {
                        InstantiationResult instantiationResult = ((GenericOperator) c.getOperator())
                                .instantiate(new Signature(callType), operatorMap, conversionMap, false);
                        Operator operator = instantiationResult.getOperator();
                        // TODO: Consider impact of conversion score of the generic instantiation on this conversion
                        // score
                        if (operator != null) {
                            operatorMap.addOperator(operator);
                            Conversion conversion = new Conversion(operator, true);
                            conversionMap.add(conversion);
                            results.add((SimpleType) conversion.getToType());
                        }
                    }
                }
            }
        }

        // Add interval demotion if no other conversion is found
        if (results.isEmpty()) {
            if (callType instanceof IntervalType) {
                IntervalType callIntervalType = (IntervalType) callType;
                if (callIntervalType.getPointType() instanceof SimpleType
                        && (allowPromotionAndDemotion || conversionMap.isIntervalDemotionEnabled())) {
                    results.add((SimpleType) callIntervalType.getPointType());
                    conversionScore += ConversionMap.ConversionScore.IntervalDemotion.score();
                }
            }
        }

        // NOTE: FHIRPath Support
        // Add list demotion if no other conversion is found
        if (results.isEmpty()) {
            if (callType instanceof ListType) {
                ListType callListType = (ListType) callType;
                if (callListType.getElementType() instanceof SimpleType
                        && (allowPromotionAndDemotion || conversionMap.isListDemotionEnabled())) {
                    results.add((SimpleType) callListType.getElementType());
                    conversionScore += ConversionMap.ConversionScore.ListDemotion.score();
                }
            }
        }

        return results;
    }
}
