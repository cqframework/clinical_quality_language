package org.opencds.cqf.cql.engine.runtime

import kotlin.jvm.JvmStatic
import org.cqframework.cql.shared.BigDecimal
import org.cqframework.cql.shared.RoundingMode
import org.opencds.cqf.cql.engine.runtime.Constants.MAX_DECIMAL
import org.opencds.cqf.cql.engine.runtime.Constants.MIN_DECIMAL

object DecimalHelper {
    fun verifyPrecision(value: BigDecimal, targetScale: Int?): BigDecimal {
        // NOTE: The CQL specification does not mandate a maximum precision, it specifies a minimum
        // precision, implementations are free to provide more precise values. However, for
        // simplicity and to provide a consistent reference implementation, this engine applies the
        // minimum precision as the maximum precision.
        // NOTE: Precision is often used loosely to mean "number of decimal places", which is not
        // what BigDecimal.precision() means. BigDecimal.scale() (when positive) is the number of
        // digits to the right of the decimal at most 8 decimal places.
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
        if (ret.compareTo(MAX_DECIMAL) > 0) {
            return null
        } else if (ret.compareTo(MIN_DECIMAL) < 0) {
            return null
        } else {
            return verifyPrecision(ret, targetScale)
        }
    }

    /**
     * Returns the coarsest scale of the given decimal values. If no values are provided, returns 0.
     *
     * @param values the stream of decimal values
     * @return the coarsest scale
     */
    fun getCoarsestScale(values: kotlin.collections.List<BigDecimal?>): Int {
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
    fun roundToScale(value: BigDecimal, scale: Int, useCeiling: kotlin.Boolean): BigDecimal {
        if (scale < value.scale()) {
            return value.setScale(
                scale,
                if (useCeiling) RoundingMode.CEILING else RoundingMode.FLOOR,
            )
        }
        return value
    }
}
