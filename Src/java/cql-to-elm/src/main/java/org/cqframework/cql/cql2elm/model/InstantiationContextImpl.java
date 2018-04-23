package org.cqframework.cql.cql2elm.model;

import org.hl7.cql.model.*;

import java.util.ArrayList;
import java.util.Map;

public class InstantiationContextImpl implements InstantiationContext {
    public InstantiationContextImpl(Map<TypeParameter, DataType> typeMap, OperatorMap operatorMap, ConversionMap conversionMap) {
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
    }

    private Map<TypeParameter, DataType> typeMap;
    private OperatorMap operatorMap;
    private ConversionMap conversionMap;
    private int conversionScore;
    public int getConversionScore() {
        return conversionScore;
    }

    @Override
    public boolean isInstantiable(TypeParameter parameter, DataType callType) {
        // If the type is not yet bound, bind it to the call type.
        DataType boundType = typeMap.get(parameter);
        if (boundType == null) {
            if (parameter.canBind(callType)) {
                typeMap.put(parameter, callType);
                return true;
            }
            else {
                return false;
            }
        }
        else {
            // If the type is bound, and is a super type of the call type, return true;
            if (boundType.isSuperTypeOf(callType) || callType.isCompatibleWith(boundType)) {
                return true;
            }
            else if (callType.isSuperTypeOf(boundType) || boundType.isCompatibleWith(callType)) {
                // If the call type is a super type of the bound type, switch the bound type for this parameter to the call type
                if (parameter.canBind(callType)) {
                    typeMap.put(parameter, callType);
                    return true;
                }
                else {
                    return false;
                }
            }
            else {
                // If there is an implicit conversion path from the call type to the bound type, return true
                Conversion conversion = conversionMap.findConversion(callType, boundType, true, operatorMap);
                if (conversion != null) {
                    // if the conversion is a list promotion, switch the bound type to the call type
                    if (boundType instanceof ListType) {
                        ListType boundListType = (ListType)boundType;
                        if (boundListType.getElementType().isSuperTypeOf(callType) || callType.isCompatibleWith(boundListType.getElementType())) {
                            if (parameter.canBind(callType)) {
                                typeMap.put(parameter, callType);
                                conversionScore -= 4; // This removes the list promotion
                                return true;
                            }
                            else {
                                return false;
                            }
                        }
                    }
                    return true;
                }

                // If there is an implicit conversion path from the bound type to the call type
                conversion = conversionMap.findConversion(boundType, callType, true, operatorMap);
                if (conversion != null) {
                    // switch the bound type to the call type and return true
                    if (parameter.canBind(callType)) {
                        typeMap.put(parameter, callType);
                        conversionScore -= 2; // This removes the conversion from the instantiation
                        return true;
                    }
                    else {
                        return false;
                    }
                }

                // Find the first supertype that is a supertype of both types
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
            }
        }

        return false;
    }

    @Override
    public DataType instantiate(TypeParameter parameter) {
        DataType result = typeMap.get(parameter);
        if (result == null) {
            throw new IllegalArgumentException(String.format("Could not resolve type parameter %s.", parameter.getIdentifier()));
        }

        return result;
    }

    @Override
    public Iterable<IntervalType> getIntervalConversionTargets(DataType callType) {
        ArrayList<IntervalType> results = new ArrayList<IntervalType>();
        for (Conversion c : conversionMap.getConversions(callType)) {
            if (c.getToType() instanceof IntervalType) {
                results.add((IntervalType)c.getToType());
                conversionScore += 2;
            }
        }

        return results;
    }

    @Override
    public Iterable<ListType> getListConversionTargets(DataType callType) {
        ArrayList<ListType> results = new ArrayList<ListType>();
        for (Conversion c : conversionMap.getConversions(callType)) {
            if (c.getToType() instanceof ListType) {
                results.add((ListType)c.getToType());
                conversionScore += 2;
            }
        }

        if (results.isEmpty()) {
            for (Conversion c : conversionMap.getGenericConversions()) {
                if (c.getOperator() != null) {
                    if (c.getToType() instanceof ListType) {
                        // instantiate the generic...
                        InstantiationResult instantiationResult = ((GenericOperator)c.getOperator()).instantiate(new Signature(callType), operatorMap, conversionMap);
                        Operator operator = instantiationResult.getOperator();
                        // TODO: Consider impact of conversion score of the generic instantiation on this conversion score
                        if (operator != null) {
                            operatorMap.addOperator(operator);
                            Conversion conversion = new Conversion(operator, true);
                            conversionMap.add(conversion);
                            results.add((ListType)conversion.getToType());
                        }
                    }
                }
            }
        }

        // NOTE: FHIRPath support
        // Add list promotion if no other conversion is found
        if (results.isEmpty()) {
            if (!(callType instanceof ListType)) {
                results.add(new ListType(callType));
                conversionScore += 4; // List promotion has a score of 4
            }
        }

        return results;
    }

    @Override
    public Iterable<SimpleType> getSimpleConversionTargets(DataType callType) {
        ArrayList<SimpleType> results = new ArrayList<SimpleType>();
        for (Conversion c : conversionMap.getConversions(callType)) {
            if (c.getToType() instanceof SimpleType) {
                results.add((SimpleType)c.getToType());
                conversionScore += 2;
            }
        }

        if (results.isEmpty()) {
            for (Conversion c : conversionMap.getGenericConversions()) {
                if (c.getOperator() != null) {
                    if (c.getToType() instanceof SimpleType) {
                        InstantiationResult instantiationResult = ((GenericOperator)c.getOperator()).instantiate(new Signature(callType), operatorMap, conversionMap);
                        Operator operator = instantiationResult.getOperator();
                        // TODO: Consider impact of conversion score of the generic instantiation on this conversion score
                        if (operator != null) {
                            operatorMap.addOperator(operator);
                            Conversion conversion = new Conversion(operator, true);
                            conversionMap.add(conversion);
                            results.add((SimpleType)conversion.getToType());
                        }
                    }
                }
            }
        }

        // NOTE: FHIRPath Support
        // Add list demotion if no other conversion is found
        if (results.isEmpty()) {
            if (callType instanceof ListType) {
                ListType callListType = (ListType)callType;
                if (callListType.getElementType() instanceof SimpleType) {
                    results.add((SimpleType)callListType.getElementType());
                    conversionScore += 3; // List demotion has a score of 3
                }
            }
        }

        return results;
    }
}
