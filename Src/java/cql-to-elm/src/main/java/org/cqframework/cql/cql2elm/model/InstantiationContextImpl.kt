package org.cqframework.cql.cql2elm.model

import org.hl7.cql.model.DataType
import org.hl7.cql.model.InstantiationContext
import org.hl7.cql.model.IntervalType
import org.hl7.cql.model.ListType
import org.hl7.cql.model.SimpleType
import org.hl7.cql.model.TypeParameter

class InstantiationContextImpl(
    private val typeMap: MutableMap<TypeParameter, DataType?>,
    private val operatorMap: OperatorMap,
    private val conversionMap: ConversionMap,
    private val allowPromotionAndDemotion: Boolean
) : InstantiationContext {

    var conversionScore: Int = 0
        private set

    override fun isInstantiable(parameter: TypeParameter, callType: DataType): Boolean {
        // If the type is not yet bound, bind it to the call type.
        val boundType = typeMap[parameter]
        if (boundType == null) {
            if (parameter.canBind(callType)) {
                typeMap[parameter] = callType
                return true
            } else {
                return false
            }
        } else {
            // If the type is bound, and is a super type of the call type, return true;
            if (boundType.isSuperTypeOf(callType) || callType.isCompatibleWith(boundType)) {
                return true
            } else if (callType.isSuperTypeOf(boundType) || boundType.isCompatibleWith(callType)) {
                // If the call type is a super type of the bound type, switch the bound type for
                // this parameter to the
                // call type
                if (parameter.canBind(callType)) {
                    typeMap[parameter] = callType
                    return true
                } else {
                    return false
                }
            } else {
                // If there is an implicit conversion path from the call type to the bound type,
                // return true
                var conversion =
                    conversionMap.findConversion(
                        callType,
                        boundType,
                        true,
                        allowPromotionAndDemotion,
                        operatorMap
                    )
                if (conversion != null) {
                    // if the conversion is a list promotion, switch the bound type to the call type
                    if (boundType is ListType) {
                        if (
                            boundType.elementType.isSuperTypeOf(callType) ||
                                callType.isCompatibleWith(boundType.elementType)
                        ) {
                            if (parameter.canBind(callType)) {
                                typeMap[parameter] = callType
                                conversionScore -=
                                    ConversionMap.ConversionScore.ListPromotion
                                        .score() // This removes the list promotion
                                return true
                            } else {
                                return false
                            }
                        }
                    }
                    return true
                }

                // If there is an implicit conversion path from the bound type to the call type
                conversion =
                    conversionMap.findConversion(
                        boundType,
                        callType,
                        true,
                        allowPromotionAndDemotion,
                        operatorMap
                    )
                if (conversion != null) {
                    // switch the bound type to the call type and return true
                    if (parameter.canBind(callType)) {
                        typeMap[parameter] = callType
                        conversionScore -=
                            (if ((conversion.toType is SimpleType))
                                ConversionMap.ConversionScore.SimpleConversion.score()
                            else
                                ConversionMap.ConversionScore.ComplexConversion
                                    .score()) // This removes the conversion from the instantiation
                        return true
                    } else {
                        return false
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

        return false
    }

    override fun instantiate(parameter: TypeParameter): DataType {
        val result =
            typeMap[parameter]
                ?: throw IllegalArgumentException(
                    String.format("Could not resolve type parameter %s.", parameter.identifier)
                )

        return result
    }

    override fun getIntervalConversionTargets(callType: DataType): Iterable<IntervalType> {
        val results = ArrayList<IntervalType>()
        for (c in conversionMap.getConversions(callType)) {
            if (c.toType is IntervalType) {
                results.add(c.toType as IntervalType)
                conversionScore += ConversionMap.ConversionScore.ComplexConversion.score()
            }
        }

        if (results.isEmpty()) {
            for (c in conversionMap.genericConversions) {
                if (c.operator != null) {
                    if (c.toType is IntervalType) {
                        // instantiate the generic...
                        val instantiationResult =
                            (c.operator as GenericOperator).instantiate(
                                Signature(callType),
                                operatorMap,
                                conversionMap,
                                false
                            )
                        val operator = instantiationResult.operator
                        // TODO: Consider impact of conversion score of the generic instantiation on
                        // this conversion
                        // score
                        if (operator != null) {
                            operatorMap.addOperator(operator)
                            val conversion = Conversion(operator, true)
                            conversionMap.add(conversion)
                            results.add(conversion.toType as IntervalType)
                        }
                    }
                }
            }
        }

        // Add interval promotion if no other conversion is found
        if (results.isEmpty()) {
            if (
                callType !is IntervalType &&
                    operatorMap.isPointType(callType) &&
                    (allowPromotionAndDemotion || conversionMap.isIntervalPromotionEnabled)
            ) {
                results.add(IntervalType(callType))
                conversionScore += ConversionMap.ConversionScore.IntervalPromotion.score()
            }
        }

        return results
    }

    override fun getListConversionTargets(callType: DataType): Iterable<ListType> {
        val results = ArrayList<ListType>()
        for (c in conversionMap.getConversions(callType)) {
            if (c.toType is ListType) {
                results.add(c.toType as ListType)
                conversionScore += ConversionMap.ConversionScore.ComplexConversion.score()
            }
        }

        if (results.isEmpty()) {
            for (c in conversionMap.genericConversions) {
                if (c.operator != null) {
                    if (c.toType is ListType) {
                        // instantiate the generic...
                        val instantiationResult =
                            (c.operator as GenericOperator).instantiate(
                                Signature(callType),
                                operatorMap,
                                conversionMap,
                                false
                            )
                        val operator = instantiationResult.operator
                        // TODO: Consider impact of conversion score of the generic instantiation on
                        // this conversion
                        // score
                        if (operator != null) {
                            operatorMap.addOperator(operator)
                            val conversion = Conversion(operator, true)
                            conversionMap.add(conversion)
                            results.add(conversion.toType as ListType)
                        }
                    }
                }
            }
        }

        // NOTE: FHIRPath support
        // Add list promotion if no other conversion is found
        if (results.isEmpty()) {
            if (
                callType !is ListType &&
                    (allowPromotionAndDemotion || conversionMap.isListPromotionEnabled)
            ) {
                results.add(ListType(callType))
                conversionScore += ConversionMap.ConversionScore.ListPromotion.score()
            }
        }

        return results
    }

    override fun getSimpleConversionTargets(callType: DataType): Iterable<SimpleType> {
        val results = ArrayList<SimpleType>()
        for (c in conversionMap.getConversions(callType)) {
            if (c.toType is SimpleType) {
                results.add(c.toType as SimpleType)
                conversionScore += ConversionMap.ConversionScore.SimpleConversion.score()
            }
        }

        if (results.isEmpty()) {
            for (c in conversionMap.genericConversions) {
                if (c.operator != null) {
                    if (c.toType is SimpleType) {
                        val instantiationResult =
                            (c.operator as GenericOperator).instantiate(
                                Signature(callType),
                                operatorMap,
                                conversionMap,
                                false
                            )
                        val operator = instantiationResult.operator
                        // TODO: Consider impact of conversion score of the generic instantiation on
                        // this conversion
                        // score
                        if (operator != null) {
                            operatorMap.addOperator(operator)
                            val conversion = Conversion(operator, true)
                            conversionMap.add(conversion)
                            results.add(conversion.toType as SimpleType)
                        }
                    }
                }
            }
        }

        // Add interval demotion if no other conversion is found
        if (results.isEmpty()) {
            if (callType is IntervalType) {
                if (
                    callType.pointType is SimpleType &&
                        (allowPromotionAndDemotion || conversionMap.isIntervalDemotionEnabled)
                ) {
                    results.add(callType.pointType as SimpleType)
                    conversionScore += ConversionMap.ConversionScore.IntervalDemotion.score()
                }
            }
        }

        // NOTE: FHIRPath Support
        // Add list demotion if no other conversion is found
        if (results.isEmpty()) {
            if (callType is ListType) {
                if (
                    callType.elementType is SimpleType &&
                        (allowPromotionAndDemotion || conversionMap.isListDemotionEnabled)
                ) {
                    results.add(callType.elementType as SimpleType)
                    conversionScore += ConversionMap.ConversionScore.ListDemotion.score()
                }
            }
        }

        return results
    }
}
