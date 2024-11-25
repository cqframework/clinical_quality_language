package org.cqframework.cql.cql2elm.model

import org.hl7.cql.model.ChoiceType
import org.hl7.cql.model.DataType
import org.hl7.cql.model.InstantiationContext

class Signature(vararg types: DataType) {
    var operandTypes: List<DataType>
        private set

    init {
        operandTypes = listOf(*types)
    }

    val size: Int
        get() = operandTypes.size

    fun isSuperTypeOf(other: Signature): Boolean {
        return size == other.size &&
            operandTypes.zip(other.operandTypes).all { it.first.isSuperTypeOf(it.second) }
    }

    val containsChoices by lazy { hasChoices() }

    private fun hasChoices(): Boolean {
        return operandTypes.any { it is ChoiceType }
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
        return Signature(*operandTypes.map { it.instantiate(context) }.toTypedArray())
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
                // if a conversion is required, store it in the conversions array
                for (i in operandTypes.indices) {
                    val first = operandTypes[i]
                    val second = other.operandTypes[i]
                    if (first.isSubTypeOf(second)) {
                        continue
                    }

                    val conversion =
                        conversionMap?.findConversion(
                            first,
                            second,
                            true,
                            allowPromotionAndDemotion,
                            operatorMap
                        )
                    if (conversion != null) {
                        conversions[i] = conversion
                        continue
                    }

                    return false
                }

                return true
            }
    }

    override fun hashCode(): Int {
        var result = 53
        for (operandType in operandTypes) {
            result += (39 * operandType.hashCode())
        }

        return result
    }

    override fun equals(other: Any?): Boolean {
        return other is Signature &&
            size == other.size &&
            operandTypes.zip(other.operandTypes).all { it.first == it.second }
    }

    override fun toString(): String {
        val builder = StringBuilder()
        builder.append("(")
        for (i in operandTypes.indices) {
            if (i > 0) {
                builder.append(",")
            }

            builder.append(operandTypes[i].toString())
        }
        builder.append(")")
        return builder.toString()
    }
}
