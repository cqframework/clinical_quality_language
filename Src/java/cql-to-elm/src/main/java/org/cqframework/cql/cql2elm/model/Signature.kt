package org.cqframework.cql.cql2elm.model

import org.hl7.cql.model.ChoiceType
import org.hl7.cql.model.DataType
import org.hl7.cql.model.InstantiationContext

data class Signature(val operandTypes: List<DataType>) {
    constructor(vararg types: DataType) : this(listOf(*types))

    val containsChoices by lazy { operandTypes.any { it is ChoiceType } }
    val size: Int
        get() = operandTypes.size

    fun isSuperTypeOf(other: Signature): Boolean {
        return size == other.size &&
            operandTypes.zip(other.operandTypes).all { it.first.isSuperTypeOf(it.second) }
    }

    fun isSubTypeOf(other: Signature): Boolean {
        return size == other.size &&
            operandTypes.zip(other.operandTypes).all { it.first.isSubTypeOf(it.second) }
    }

    fun isInstantiable(callSignature: Signature, context: InstantiationContext): Boolean {
        return size == callSignature.size &&
            operandTypes.zip(callSignature.operandTypes).all {
                it.first.isInstantiable(it.second, context)
            }
    }

    fun instantiate(context: InstantiationContext): Signature {
        return Signature(operandTypes.map { it.instantiate(context) })
    }

    fun isConvertibleTo(
        other: Signature,
        conversionMap: ConversionMap?,
        operatorMap: OperatorMap,
        allowPromotionAndDemotion: Boolean,
        conversions: Array<Conversion?>
    ): Boolean {
        return size == other.size &&
            run {
                // Each operand must be a subtype or convertible
                // Store the conversions for each operand
                // If a conversion is needed and not found, return false (not convertible)
                for (i in operandTypes.indices) {
                    val first = operandTypes[i]
                    val second = other.operandTypes[i]
                    if (first.isSubTypeOf(second)) {
                        continue
                    }

                    conversions[i] =
                        conversionMap?.findConversion(
                            first,
                            second,
                            true,
                            allowPromotionAndDemotion,
                            operatorMap
                        )

                    if (conversions[i] == null) {
                        return false
                    }
                }

                return true
            }
    }

    override fun toString(): String {
        return "(" + operandTypes.joinToString { it.toString() } + ")"
    }
}
