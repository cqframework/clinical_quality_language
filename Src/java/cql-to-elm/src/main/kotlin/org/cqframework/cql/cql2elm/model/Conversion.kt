package org.cqframework.cql.cql2elm.model

import org.cqframework.cql.cql2elm.model.ConversionMap.ConversionScore.Cast
import org.cqframework.cql.cql2elm.model.ConversionMap.ConversionScore.ComplexConversion
import org.cqframework.cql.cql2elm.model.ConversionMap.ConversionScore.IntervalDemotion
import org.cqframework.cql.cql2elm.model.ConversionMap.ConversionScore.IntervalPromotion
import org.cqframework.cql.cql2elm.model.ConversionMap.ConversionScore.ListDemotion
import org.cqframework.cql.cql2elm.model.ConversionMap.ConversionScore.ListPromotion
import org.cqframework.cql.cql2elm.model.ConversionMap.ConversionScore.SimpleConversion
import org.hl7.cql.model.ChoiceType
import org.hl7.cql.model.ClassType
import org.hl7.cql.model.DataType
import org.hl7.cql.model.IntervalType
import org.hl7.cql.model.ListType
import org.hl7.cql.model.SimpleType

class Conversion(
    val fromType: DataType,
    val toType: DataType,
    val isImplicit: Boolean = false,
    val conversion: Conversion? = null,
    val operator: Operator? = null
) {
    constructor(
        operator: Operator,
        isImplicit: Boolean
    ) : this(singletonOperand(operator), ensureResultType(operator), isImplicit, null, operator)

    constructor(fromType: DataType, toType: DataType) : this(fromType, toType, true) {
        this.isCast = true
    }

    constructor(
        fromType: ChoiceType,
        toType: DataType,
        choiceConversion: Conversion
    ) : this(fromType, toType, true, choiceConversion) {
        this.isCast = true
    }

    constructor(
        fromType: DataType,
        toType: ChoiceType,
        choiceConversion: Conversion
    ) : this(fromType, toType, true, choiceConversion) {
        this.isCast = true
    }

    constructor(
        fromType: ListType,
        toType: ListType,
        elementConversion: Conversion
    ) : this(fromType, toType, true, elementConversion) {
        this.isListConversion = true
    }

    constructor(
        fromType: ListType,
        toType: DataType,
        elementConversion: Conversion?
    ) : this(fromType, toType, true, elementConversion) {
        this.isListDemotion = true
    }

    constructor(
        fromType: DataType,
        toType: ListType,
        elementConversion: Conversion?
    ) : this(fromType, toType, true, elementConversion) {
        this.isListPromotion = true
    }

    constructor(
        fromType: IntervalType,
        toType: DataType,
        elementConversion: Conversion?
    ) : this(fromType, toType, true, elementConversion) {
        this.isIntervalDemotion = true
    }

    constructor(
        fromType: DataType,
        toType: IntervalType,
        elementConversion: Conversion?
    ) : this(fromType, toType, true, elementConversion) {
        this.isIntervalPromotion = true
    }

    constructor(
        fromType: IntervalType,
        toType: IntervalType,
        pointConversion: Conversion
    ) : this(fromType, toType, true, pointConversion) {
        this.isIntervalConversion = true
    }

    private val alternativeConversions: MutableList<Conversion> = mutableListOf()

    fun getAlternativeConversions(): List<Conversion> {
        return alternativeConversions
    }

    fun hasAlternativeConversions(): Boolean {
        return alternativeConversions.isNotEmpty()
    }

    fun addAlternativeConversion(alternativeConversion: Conversion) {
        require(fromType is ChoiceType) {
            "Alternative conversions can only be used with choice types"
        }

        // Should also guard against adding an alternative that is not one of the component
        // types of the fromType
        // This should never happen though with current usage
        alternativeConversions.add(alternativeConversion)
    }

    val score: Int
        get() {
            val nestedScore = conversion?.score ?: 0
            return when {
                isCast -> Cast.score + nestedScore
                isIntervalDemotion -> IntervalDemotion.score + nestedScore
                isListDemotion -> ListDemotion.score + nestedScore
                isIntervalPromotion -> IntervalPromotion.score + nestedScore
                isListPromotion -> ListPromotion.score + nestedScore
                isListConversion && toType is ListType && toType.elementType is SimpleType ->
                    SimpleConversion.score + nestedScore
                isListConversion -> ComplexConversion.score + nestedScore
                isIntervalConversion && toType is IntervalType && toType.pointType is SimpleType ->
                    SimpleConversion.score + nestedScore
                isIntervalConversion -> ComplexConversion.score + nestedScore
                toType is ClassType -> ComplexConversion.score + nestedScore
                else -> SimpleConversion.score + nestedScore
            }
        }

    val isGeneric: Boolean
        get() = operator is GenericOperator

    var isCast: Boolean = false
        private set

    var isListConversion: Boolean = false
        private set

    var isListPromotion: Boolean = false
        private set

    var isListDemotion: Boolean = false
        private set

    var isIntervalConversion: Boolean = false
        private set

    var isIntervalPromotion: Boolean = false
        private set

    var isIntervalDemotion: Boolean = false
        private set

    companion object {
        fun singletonOperand(operator: Operator): DataType {
            require(operator.signature.operandTypes.size == 1) {
                "Conversion operator must be unary."
            }
            return operator.signature.operandTypes[0]
        }

        fun ensureResultType(operator: Operator): DataType {
            requireNotNull(operator.resultType) { "Conversion operator must have a result type." }
            return operator.resultType!!
        }
    }
}
