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

    class OperatorConversion(val operator: Operator, override val isImplicit: Boolean) :
        Conversion() {
        override val fromType: DataType = singletonOperand(operator)
        override val toType: DataType = ensureResultType(operator)
        val isGeneric: Boolean
            get() = operator is GenericOperator

        override val score: Int
            get() =
                if (toType is ClassType) ConversionScore.ComplexConversion.score
                else ConversionScore.SimpleConversion.score

        companion object {
            fun singletonOperand(operator: Operator): DataType {
                require(operator.signature.operandTypes.size == 1) {
                    "Conversion operator must be unary."
                }
                return operator.signature.operandTypes[0]
            }

            fun ensureResultType(operator: Operator): DataType {
                requireNotNull(operator.resultType) {
                    "Conversion operator must have a result type."
                }
                return operator.resultType!!
            }
        }
    }

    class Cast(override val fromType: DataType, override val toType: DataType) : Conversion() {
        override val score: Int
            get() = ConversionScore.Cast.score
    }

    class ChoiceNarrowingCast(
        override val fromType: ChoiceType,
        override val toType: DataType,
        val innerConversion: Conversion,
        val alternativeConversions: List<Conversion> = emptyList(),
    ) : Conversion() {
        override val score: Int
            get() = ConversionScore.Cast.score + innerConversion.score
    }

    class ChoiceWideningCast(
        override val fromType: DataType,
        override val toType: ChoiceType,
        val innerConversion: Conversion,
    ) : Conversion() {
        override val score: Int
            get() = ConversionScore.Cast.score + innerConversion.score
    }

    class ListConversion(
        override val fromType: ListType,
        override val toType: ListType,
        val elementConversion: Conversion,
    ) : Conversion() {
        override val score: Int
            get() {
                val baseScore =
                    if (toType.elementType is SimpleType) ConversionScore.SimpleConversion.score
                    else ConversionScore.ComplexConversion.score
                return baseScore + elementConversion.score
            }
    }

    class ListPromotion(
        override val fromType: DataType,
        override val toType: ListType,
        val elementConversion: Conversion?,
    ) : Conversion() {
        override val score: Int
            get() = ConversionScore.ListPromotion.score + (elementConversion?.score ?: 0)
    }

    class ListDemotion(
        override val fromType: ListType,
        override val toType: DataType,
        val elementConversion: Conversion?,
    ) : Conversion() {
        override val score: Int
            get() = ConversionScore.ListDemotion.score + (elementConversion?.score ?: 0)
    }

    class IntervalConversion(
        override val fromType: IntervalType,
        override val toType: IntervalType,
        val pointConversion: Conversion,
    ) : Conversion() {
        override val score: Int
            get() {
                val baseScore =
                    if (toType.pointType is SimpleType) ConversionScore.SimpleConversion.score
                    else ConversionScore.ComplexConversion.score
                return baseScore + pointConversion.score
            }
    }

    class IntervalPromotion(
        override val fromType: DataType,
        override val toType: IntervalType,
        val pointConversion: Conversion?,
    ) : Conversion() {
        override val score: Int
            get() = ConversionScore.IntervalPromotion.score + (pointConversion?.score ?: 0)
    }

    class IntervalDemotion(
        override val fromType: IntervalType,
        override val toType: DataType,
        val pointConversion: Conversion?,
    ) : Conversion() {
        override val score: Int
            get() = ConversionScore.IntervalDemotion.score + (pointConversion?.score ?: 0)
    }
}
