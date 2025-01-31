package org.cqframework.cql.cql2elm.model

import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import org.hl7.cql.model.ChoiceType
import org.hl7.cql.model.DataType
import org.hl7.cql.model.IntervalType
import org.hl7.cql.model.ListType

@Suppress("TooManyFunctions")
class ConversionMap {
    @Suppress("MagicNumber")
    enum class TypePrecedenceScore(val score: Int) {
        Simple(1),
        Tuple(2),
        Class(3),
        Interval(4),
        List(5),
        Choice(6),
        Other(7)
    }

    @Suppress("MagicNumber")
    enum class ConversionScore(val score: Int) {
        ExactMatch(0),
        SubType(1),
        Compatible(2),
        Cast(3),
        SimpleConversion(4),
        ComplexConversion(5),
        IntervalPromotion(6),
        ListDemotion(7),
        IntervalDemotion(8),
        ListPromotion(9)
    }

    private val map: MutableMap<DataType, MutableList<Conversion>> = HashMap()
    val genericConversions: MutableList<Conversion> = ArrayList()
    var isListDemotionEnabled: Boolean = true
    var isListPromotionEnabled: Boolean = true
    var isIntervalDemotionEnabled: Boolean = false
    var isIntervalPromotionEnabled: Boolean = false

    private fun hasConversion(conversion: Conversion, conversions: List<Conversion>): Boolean {
        return conversions.any { it.toType == conversion.toType }
    }

    fun getConversionOperator(fromType: DataType, toType: DataType): Operator? {
        return this.getConversions(fromType).firstOrNull { it.toType == toType }?.operator
    }

    fun add(conversion: Conversion) {
        // NOTE: The conversion map supports generic conversions, however, they turned out to be
        // quite expensive
        // computationally
        // so we introduced list promotion and demotion instead (we should add interval promotion
        // and demotion too,
        // would be quite useful)
        // Generic conversions could still be potentially useful, so I left the code, but it's never
        // used because the
        // generic conversions
        // are not added in the SystemLibraryHelper.
        if (conversion.isGeneric) {
            val conversions = genericConversions
            check(!hasConversion(conversion, conversions)) {
                "Conversion from ${conversion.fromType} to ${conversion.toType} is already defined."
            }

            conversions.add(conversion)
        } else {
            val conversions = getConversions(conversion.fromType)
            check(!hasConversion(conversion, conversions)) {
                "Conversion from ${conversion.fromType} to ${conversion.toType} is already defined."
            }

            conversions.add(conversion)
        }
    }

    fun getConversions(fromType: DataType): MutableList<Conversion> {
        return map.computeIfAbsent(fromType) { ArrayList() }
    }

    /*
    Returns conversions for the given type, or any supertype, recursively
     */
    private fun getAllConversions(fromType: DataType?): List<Conversion> {
        val conversions: MutableList<Conversion> = ArrayList()
        var currentType = fromType
        while (currentType != null && currentType != DataType.ANY) {
            conversions.addAll(getConversions(currentType))
            currentType = currentType.baseType
        }
        return conversions
    }

    private fun findCompatibleConversion(fromType: DataType, toType: DataType): Conversion? {
        if (fromType.isCompatibleWith(toType)) {
            return Conversion(fromType, toType)
        }

        return null
    }

    private fun findChoiceConversion(
        fromType: ChoiceType,
        toType: DataType,
        allowPromotionAndDemotion: Boolean,
        operatorMap: OperatorMap
    ): Conversion? {
        var result: Conversion? = null
        for (choice in fromType.types) {
            val choiceConversion =
                findConversion(choice, toType, true, allowPromotionAndDemotion, operatorMap)
            if (choiceConversion != null) {
                if (result == null) {
                    result = Conversion(fromType, toType, choiceConversion)
                } else {
                    result.addAlternativeConversion(choiceConversion)
                }
            }
        }

        return result
    }

    private fun findTargetChoiceConversion(
        fromType: DataType,
        toType: ChoiceType,
        allowPromotionAndDemotion: Boolean,
        operatorMap: OperatorMap
    ): Conversion? {
        for (choice in toType.types) {
            findConversion(fromType, choice, true, allowPromotionAndDemotion, operatorMap)?.let {
                return Conversion(fromType, toType, it)
            }
        }

        return null
    }

    private fun findListConversion(
        fromType: ListType,
        toType: ListType,
        operatorMap: OperatorMap
    ): Conversion? {
        return findConversion(
                fromType.elementType,
                toType.elementType,
                isImplicit = true,
                allowPromotionAndDemotion = false,
                operatorMap = operatorMap
            )
            ?.let { Conversion(fromType, toType, it) }
    }

    private fun findIntervalConversion(
        fromType: IntervalType,
        toType: IntervalType,
        operatorMap: OperatorMap
    ): Conversion? {
        return findConversion(
                fromType.pointType,
                toType.pointType,
                isImplicit = true,
                allowPromotionAndDemotion = false,
                operatorMap = operatorMap
            )
            ?.let { Conversion(fromType, toType, it) }
    }

    private fun findListDemotion(
        fromType: ListType,
        toType: DataType,
        operatorMap: OperatorMap
    ): Conversion? {
        val elementType = fromType.elementType
        return if (elementType.isSubTypeOf(toType)) {
            Conversion(fromType, toType, null)
        } else {
            findConversion(
                    elementType,
                    toType,
                    isImplicit = true,
                    allowPromotionAndDemotion = false,
                    operatorMap = operatorMap
                )
                ?.let { Conversion(fromType, toType, it) }
        }
    }

    private fun findListPromotion(
        fromType: DataType,
        toType: ListType,
        operatorMap: OperatorMap
    ): Conversion? {
        return if (fromType.isSubTypeOf(toType.elementType)) {
            Conversion(fromType, toType, null)
        } else {
            findConversion(
                    fromType,
                    toType.elementType,
                    isImplicit = true,
                    allowPromotionAndDemotion = false,
                    operatorMap = operatorMap
                )
                ?.let { Conversion(fromType, toType, it) }
        }
    }

    private fun findIntervalDemotion(
        fromType: IntervalType,
        toType: DataType,
        operatorMap: OperatorMap
    ): Conversion? {
        val pointType = fromType.pointType
        return if (pointType.isSubTypeOf(toType)) {
            Conversion(fromType, toType, null)
        } else {
            findConversion(
                    pointType,
                    toType,
                    isImplicit = true,
                    allowPromotionAndDemotion = false,
                    operatorMap = operatorMap
                )
                ?.let { Conversion(fromType, toType, it) }
        }
    }

    private fun findIntervalPromotion(
        fromType: DataType,
        toType: IntervalType,
        operatorMap: OperatorMap
    ): Conversion? {
        return if (fromType.isSubTypeOf(toType.pointType)) {
            Conversion(fromType, toType, null)
        } else {
            findConversion(
                    fromType,
                    toType.pointType,
                    isImplicit = true,
                    allowPromotionAndDemotion = false,
                    operatorMap = operatorMap
                )
                ?.let { Conversion(fromType, toType, it) }
        }
    }

    @Suppress("UnusedParameter")
    private fun ensureGenericConversionInstantiated(
        fromType: DataType,
        toType: DataType,
        isImplicit: Boolean,
        operatorMap: OperatorMap
    ): Boolean {
        var operatorsInstantiated = false
        for (c in genericConversions) {
            if (c.operator != null) {
                // instantiate the generic...
                val instantiationResult =
                    (c.operator as GenericOperator).instantiate(
                        Signature(fromType),
                        operatorMap,
                        this,
                        false
                    )
                val operator = instantiationResult.operator
                if (operator != null && !operatorMap.containsOperator(operator)) {
                    operatorMap.addOperator(operator)
                    val conversion = Conversion(operator, true)
                    this.add(conversion)
                    operatorsInstantiated = true
                }
            }
        }

        return operatorsInstantiated
    }

    @Suppress("NestedBlockDepth", "ComplexCondition")
    private fun internalFindConversion(
        fromType: DataType,
        toType: DataType,
        isImplicit: Boolean
    ): Conversion? {
        var result: Conversion? = null
        var score = Int.MAX_VALUE
        for (conversion in getAllConversions(fromType)) {
            if (
                (!isImplicit || conversion.isImplicit) &&
                    (conversion.toType.isSuperTypeOf(toType) || conversion.toType.isGeneric)
            ) {
                // Lower score is better. If the conversion matches the target type exactly,
                // the score is 0.
                // If the conversion is generic, the score is 1 (because that will be
                // instantiated to an exact
                // match)
                // If the conversion is a super type, it should only be used if an exact match
                // cannot be found.
                // If the score is equal to an existing, it indicates a duplicate conversion
                val newScore =
                    ((if (conversion.fromType == fromType) 0
                    else (if (conversion.fromType.isGeneric) 1 else 2)) +
                        (if (conversion.toType == toType) 0
                        else (if (conversion.toType.isGeneric) 1 else 2)))
                if (newScore < score) {
                    result = conversion
                    score = newScore
                } else
                    require(newScore != score) {
                        // ERROR
                        "Ambiguous implicit conversion from $fromType to ${result!!.toType} or ${conversion.toType}."
                    }
            }
        }

        return result
    }

    @Suppress("CyclomaticComplexMethod")
    fun findConversion(
        fromType: DataType,
        toType: DataType,
        isImplicit: Boolean,
        allowPromotionAndDemotion: Boolean,
        operatorMap: OperatorMap
    ): Conversion? {
        var result =
            findCompatibleConversion(fromType, toType)
                ?: internalFindConversion(fromType, toType, isImplicit)

        if (
            result == null &&
                ensureGenericConversionInstantiated(fromType, toType, isImplicit, operatorMap)
        ) {
            result = internalFindConversion(fromType, toType, isImplicit)
        }

        if (result == null) {
            // NOTE: FHIRPath Implicit conversion from list to singleton
            // If the fromType is a list and the target type is a singleton (potentially with a
            // compatible conversion),
            // Convert by invoking a singleton
            result =
                when {
                    fromType is ListType &&
                        toType !is ListType &&
                        (allowPromotionAndDemotion || isListDemotionEnabled) ->
                        findListDemotion(fromType, toType, operatorMap)
                    fromType !is ListType &&
                        toType is ListType &&
                        (allowPromotionAndDemotion || isListPromotionEnabled) ->
                        findListPromotion(fromType, toType, operatorMap)
                    fromType is IntervalType &&
                        toType !is IntervalType &&
                        (allowPromotionAndDemotion || isIntervalDemotionEnabled) ->
                        findIntervalDemotion(fromType, toType, operatorMap)
                    fromType !is IntervalType &&
                        toType is IntervalType &&
                        (allowPromotionAndDemotion || isIntervalPromotionEnabled) ->
                        findIntervalPromotion(fromType, toType, operatorMap)

                    // If the fromType is a choice, attempt to find a conversion from one of the
                    // choice
                    // types
                    fromType is ChoiceType ->
                        findChoiceConversion(
                            fromType,
                            toType,
                            allowPromotionAndDemotion,
                            operatorMap
                        )

                    // If the target type is a choice,
                    // attempt to find a conversion to
                    // one of the choice types
                    fromType !is ChoiceType && toType is ChoiceType ->
                        findTargetChoiceConversion(
                            fromType,
                            toType,
                            allowPromotionAndDemotion,
                            operatorMap
                        )

                    // If both types are lists,
                    // attempt to find a conversion between the element
                    // types
                    fromType is ListType && toType is ListType ->
                        findListConversion(fromType, toType, operatorMap)

                    // If both types are intervals, attempt to find a conversion between the point
                    // types
                    fromType is IntervalType && toType is IntervalType ->
                        findIntervalConversion(fromType, toType, operatorMap)
                    else -> null
                }
        }

        return result
    }

    companion object {
        @JvmStatic
        fun getTypePrecedenceScore(operand: DataType): Int {
            return when (operand.javaClass.simpleName) {
                "SimpleType" -> TypePrecedenceScore.Simple.score
                "TupleType" -> TypePrecedenceScore.Tuple.score
                "ClassType" -> TypePrecedenceScore.Class.score
                "IntervalType" -> TypePrecedenceScore.Interval.score
                "ListType" -> TypePrecedenceScore.List.score
                "ChoiceType" -> TypePrecedenceScore.Choice.score
                else -> TypePrecedenceScore.Other.score
            }
        }

        fun getConversionScore(
            callOperand: DataType,
            operand: DataType,
            conversion: Conversion?
        ): Int {
            return when {
                operand == callOperand -> ConversionScore.ExactMatch.score
                operand.isSuperTypeOf(callOperand) -> ConversionScore.SubType.score
                callOperand.isCompatibleWith(operand) -> ConversionScore.Compatible.score
                conversion != null -> conversion.score
                else ->
                    throw IllegalArgumentException(
                        "Could not determine conversion score for conversion"
                    )
            }
        }
    }
}
