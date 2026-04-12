package org.cqframework.cql.cql2elm.model

import org.cqframework.cql.cql2elm.model.ConversionMap.ConversionScore
import org.hl7.cql.model.ChoiceType
import org.hl7.cql.model.ClassType
import org.hl7.cql.model.DataType
import org.hl7.cql.model.IntervalType
import org.hl7.cql.model.ListType
import org.hl7.cql.model.SimpleType

sealed class Conversion {
    abstract val fromType: DataType
    abstract val toType: DataType
    abstract val score: Int
    open val isImplicit: Boolean
        get() = true

    @Suppress("MemberNameEqualsClassName")
    open val conversion: Conversion?
        get() = null

    open val operator: Operator?
        get() = null

    val isGeneric: Boolean
        get() = operator is GenericOperator

    class OperatorConversion(override val operator: Operator, override val isImplicit: Boolean) :
        Conversion() {
        override val fromType: DataType = singletonOperand(operator)
        override val toType: DataType = ensureResultType(operator)

        override val score: Int
            get() =
                if (toType is ClassType) ConversionScore.ComplexConversion.score
                else ConversionScore.SimpleConversion.score
    }

    class Cast(override val fromType: DataType, override val toType: DataType) : Conversion() {
        override val score: Int
            get() = ConversionScore.Cast.score
    }

    class ChoiceNarrowingCast(
        override val fromType: ChoiceType,
        override val toType: DataType,
        override val conversion: Conversion,
    ) : Conversion() {
        private val alternativeConversions: MutableList<Conversion> = mutableListOf()

        fun getAlternativeConversions(): List<Conversion> = alternativeConversions

        fun hasAlternativeConversions(): Boolean = alternativeConversions.isNotEmpty()

        fun addAlternativeConversion(alternativeConversion: Conversion) {
            alternativeConversions.add(alternativeConversion)
        }

        override val score: Int
            get() = ConversionScore.Cast.score + conversion.score
    }

    class ChoiceWideningCast(
        override val fromType: DataType,
        override val toType: ChoiceType,
        override val conversion: Conversion,
    ) : Conversion() {
        override val score: Int
            get() = ConversionScore.Cast.score + conversion.score
    }

    class ListConversion(
        override val fromType: ListType,
        override val toType: ListType,
        override val conversion: Conversion,
    ) : Conversion() {
        override val score: Int
            get() {
                val baseScore =
                    if (toType.elementType is SimpleType) ConversionScore.SimpleConversion.score
                    else ConversionScore.ComplexConversion.score
                return baseScore + conversion.score
            }
    }

    class ListPromotion(
        override val fromType: DataType,
        override val toType: ListType,
        override val conversion: Conversion?,
    ) : Conversion() {
        override val score: Int
            get() = ConversionScore.ListPromotion.score + (conversion?.score ?: 0)
    }

    class ListDemotion(
        override val fromType: ListType,
        override val toType: DataType,
        override val conversion: Conversion?,
    ) : Conversion() {
        override val score: Int
            get() = ConversionScore.ListDemotion.score + (conversion?.score ?: 0)
    }

    class IntervalConversion(
        override val fromType: IntervalType,
        override val toType: IntervalType,
        override val conversion: Conversion,
    ) : Conversion() {
        override val score: Int
            get() {
                val baseScore =
                    if (toType.pointType is SimpleType) ConversionScore.SimpleConversion.score
                    else ConversionScore.ComplexConversion.score
                return baseScore + conversion.score
            }
    }

    class IntervalPromotion(
        override val fromType: DataType,
        override val toType: IntervalType,
        override val conversion: Conversion?,
    ) : Conversion() {
        override val score: Int
            get() = ConversionScore.IntervalPromotion.score + (conversion?.score ?: 0)
    }

    class IntervalDemotion(
        override val fromType: IntervalType,
        override val toType: DataType,
        override val conversion: Conversion?,
    ) : Conversion() {
        override val score: Int
            get() = ConversionScore.IntervalDemotion.score + (conversion?.score ?: 0)
    }

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
