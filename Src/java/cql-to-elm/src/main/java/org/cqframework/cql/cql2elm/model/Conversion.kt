package org.cqframework.cql.cql2elm.model

import org.hl7.cql.model.ChoiceType
import org.hl7.cql.model.ClassType
import org.hl7.cql.model.DataType
import org.hl7.cql.model.IntervalType
import org.hl7.cql.model.ListType
import org.hl7.cql.model.SimpleType

class Conversion {
    enum class ConversionType {
      Simple,
        Cast,
        ListConversion,
        ListPromotion,
        ListDemotion,
        IntervalConversion,
        IntervalPromotion,
        IntervalDemotion
    }

    private constructor(fromType: DataType, toType: DataType, isImplicit: Boolean = false, choiceConversion: Conversion? = null) {
        this.fromType = fromType
        this.toType = toType
        this.isImplicit = isImplicit
        this.conversion = choiceConversion
    }

    constructor(operator: Operator, isImplicit: Boolean) {
        require(operator.signature.operandTypes.size == 1) { "Conversion operator must be unary." }
        this.fromType = operator.signature.operandTypes[0]
        this.toType = requireNotNull(operator.resultType) { "Conversion operator must have a result type." }
        this.isImplicit = isImplicit
    }

    constructor(fromType: DataType, toType: DataType) : this(fromType, toType, false)

    constructor(fromType: ChoiceType, toType: DataType, choiceConversion: Conversion) : this(fromType, toType, true, choiceConversion) {
        this.type = ConversionType.Cast
    }

    constructor(fromType: DataType, toType: ChoiceType, choiceConversion: Conversion) : this(fromType, toType, false, choiceConversion) {
        this.type = ConversionType.Cast
    }

    constructor(fromType: ListType, toType: ListType, elementConversion: Conversion): this(fromType, toType, true, elementConversion) {
        this.type = ConversionType.ListConversion
    }

    constructor(fromType: ListType, toType: DataType, elementConversion: Conversion) : this(fromType, toType, true, elementConversion) {
        this.type = ConversionType.ListDemotion
    }

    constructor(fromType: DataType, toType: ListType, elementConversion: Conversion) : this(fromType, toType, true, elementConversion) {
        this.type = ConversionType.ListPromotion
    }

    constructor(fromType: IntervalType, toType: DataType, elementConversion: Conversion) : this(fromType, toType, true, elementConversion) {
        this.type = ConversionType.IntervalDemotion
    }

    constructor(fromType: DataType, toType: IntervalType, elementConversion: Conversion) : this(fromType, toType, true, elementConversion) {
        this.type = ConversionType.IntervalPromotion
    }

    constructor(fromType: IntervalType, toType: IntervalType, pointConversion: Conversion) : this(fromType, toType, true, pointConversion) {
        this.type = ConversionType.IntervalConversion
    }

    var isImplicit: Boolean = false
        private set

    var operator: Operator? = null
        private set

    var conversion: Conversion? = null
        private set

    var type = ConversionType.Simple
        private set

    val alternativeConversions: MutableList<Conversion> by lazy { mutableListOf() }

    fun hasAlternativeConversions(): Boolean {
        return alternativeConversions.isNotEmpty()
    }

    fun addAlternativeConversion(alternativeConversion: Conversion) {
        require(fromType is ChoiceType) { "Alternative conversions can only be used with choice types" }

        // TODO: Should also guard against adding an alternative that is not one of the component types of the fromType
        // This should never happen though with current usage
        alternativeConversions.add(alternativeConversion)
    }

    val score: Int
        get() {
            val nestedScore = conversion?.score ?: 0
            return when (type) {
                ConversionType.Cast -> ConversionMap.ConversionScore.Cast.score() + nestedScore
                ConversionType.IntervalDemotion -> ConversionMap.ConversionScore.IntervalDemotion.score() + nestedScore
                ConversionType.ListDemotion -> ConversionMap.ConversionScore.ListDemotion.score() + nestedScore
                ConversionType.IntervalPromotion -> ConversionMap.ConversionScore.IntervalPromotion.score() + nestedScore
                ConversionType.ListPromotion -> ConversionMap.ConversionScore.ListPromotion.score() + nestedScore
                ConversionType.ListConversion -> if ((toType as ListType?)!!.elementType is SimpleType) {
                    ConversionMap.ConversionScore.SimpleConversion.score() + nestedScore
                } else {
                    ConversionMap.ConversionScore.ComplexConversion.score() + nestedScore
                }
                ConversionType.IntervalConversion -> if ((toType as IntervalType?)!!.pointType is SimpleType) {
                    ConversionMap.ConversionScore.SimpleConversion.score() + nestedScore
                } else {
                    ConversionMap.ConversionScore.ComplexConversion.score() + nestedScore
                }
                else -> if (toType is ClassType) {
                    ConversionMap.ConversionScore.ComplexConversion.score() + nestedScore
                } else {
                    ConversionMap.ConversionScore.SimpleConversion.score() + nestedScore
                }
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

    var fromType: DataType
        private set

    var toType: DataType
        private set
}
