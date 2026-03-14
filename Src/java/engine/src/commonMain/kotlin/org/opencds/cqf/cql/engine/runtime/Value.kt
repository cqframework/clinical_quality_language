package org.opencds.cqf.cql.engine.runtime

import kotlin.jvm.JvmStatic
import org.cqframework.cql.shared.BigDecimal
import org.cqframework.cql.shared.RoundingMode
import org.opencds.cqf.cql.engine.elm.executing.MaxValueEvaluator.maxValue
import org.opencds.cqf.cql.engine.elm.executing.MinValueEvaluator.minValue

object Value {
    const val MAX_INT: Int = Int.MAX_VALUE
    const val MAX_LONG: Long = Long.MAX_VALUE

    /** Set to (10<sup>28</sup> - 1) / 10<sup>8</sup>. */
    val MAX_DECIMAL: BigDecimal = BigDecimal("99999999999999999999.99999999")

    const val MIN_INT: Int = Int.MIN_VALUE
    const val MIN_LONG: Long = Long.MIN_VALUE

    /** Set to (-10<sup>28</sup> + 1) / 10<sup>8</sup>. */
    val MIN_DECIMAL: BigDecimal = BigDecimal("-99999999999999999999.99999999")

    fun verifyPrecision(value: BigDecimal, targetScale: Int?): BigDecimal {
        // NOTE: The CQL specification does not mandate a maximum precision, it specifies a minimum
        // precision,
        // implementations are free to provide more precise values. However, for simplicity and to
        // provide
        // a consistent reference implementation, this engine applies the minimum precision as the
        // maximum precision.
        // NOTE: precision is often used loosely to mean "number of decimal places", which is not
        // what
        // BigDecimal.precision() means
        // BigDecimal.scale() (when positive) is the number of digits to the right of the decimal
        // at most 8 decimal places
        var value = value
        if (value.scale() > 8) {
            value = value.setScale(8, RoundingMode.FLOOR)
        }

        if (value.scale() < 0) {
            value = value.setScale(0, RoundingMode.FLOOR)
        }

        if (targetScale != null && value.scale() > targetScale) {
            value = value.stripTrailingZeros()
        }

        return value
    }

    fun validateDecimal(ret: BigDecimal, targetScale: Int?): BigDecimal? {
        if (ret.compareTo(maxValue("Decimal") as BigDecimal) > 0) {
            return null
        } else if (ret.compareTo(minValue("Decimal") as BigDecimal) < 0) {
            return null
        } else {
            return verifyPrecision(ret, targetScale)
        }
    }

    fun validateInteger(ret: Int): Int? {
        if (ret > MAX_INT || ret < MIN_INT) {
            return null
        }
        return ret
    }

    fun validateInteger(ret: Double): Int? {
        if (ret > MAX_INT || ret < MIN_INT) {
            return null
        }
        return ret.toInt()
    }

    fun validateLong(ret: Long): Long? {
        if (ret > MAX_LONG || ret < MIN_LONG) {
            return null
        }
        return ret
    }

    fun validateLong(ret: Double): Long? {
        if (ret > MAX_LONG || ret < MIN_LONG) {
            return null
        }
        return ret.toLong()
    }

    /**
     * Returns the coarsest scale of the given decimal values. If no values are provided, returns 0.
     *
     * @param values the stream of decimal values
     * @return the coarsest scale
     */
    fun getCoarsestScale(values: List<BigDecimal?>): Int {
        return values.filterNotNull().minOfOrNull { obj -> obj.scale() } ?: 0
    }

    /**
     * Rounds the decimal value to the specified scale.
     *
     * @param value the value to round
     * @param scale the scale to round to
     * @param useCeiling whether to return the ceiling or floor value
     * @return the rounded value
     */
    @JvmStatic
    fun roundToScale(value: BigDecimal, scale: Int, useCeiling: Boolean): BigDecimal {
        if (scale < value.scale()) {
            return value.setScale(
                scale,
                if (useCeiling) RoundingMode.CEILING else RoundingMode.FLOOR,
            )
        }
        return value
    }
}
