package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import kotlin.math.abs
import kotlin.math.pow
import org.cqframework.cql.shared.BigDecimal
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument
import org.opencds.cqf.cql.engine.runtime.Decimal
import org.opencds.cqf.cql.engine.runtime.DecimalHelper
import org.opencds.cqf.cql.engine.runtime.Integer
import org.opencds.cqf.cql.engine.runtime.Long
import org.opencds.cqf.cql.engine.runtime.Value
import org.opencds.cqf.cql.engine.runtime.toCqlDecimal
import org.opencds.cqf.cql.engine.runtime.toCqlInteger
import org.opencds.cqf.cql.engine.runtime.toCqlLong

/*
^(argument Integer, exponent Integer) Integer
^(argument Decimal, exponent Decimal) Decimal

The power (^) operator raises the first argument to the power given by the second argument.
When invoked with mixed Integer and Decimal arguments, the Integer argument will be implicitly converted to Decimal.
If either argument is null, the result is null.
*/
object PowerEvaluator {
    @JvmStatic
    fun power(left: Value?, right: Value?): Value? {
        if (left == null || right == null) {
            return null
        }

        if (left is Integer && right is Integer) {
            if (right.value < 0) {
                return reciprocalPower(left.value.toLong(), abs(right.value))
            }
            return BigDecimal(left.value).pow(right.value).toInt().toCqlInteger()
        }

        if (left is Long && right is Long) {
            if (right.value < 0) {
                return reciprocalPower(left.value, abs(right.value.toInt()))
            }

            return BigDecimal(left.value).pow(right.value.toInt()).toLong().toCqlLong()
        }

        if (left is Decimal && right is Decimal) {
            val result = left.value.toDouble().pow(right.value.toDouble())
            // If the result cannot be represented as a Decimal (0.0 ^ -1.0 is infinite and
            // -1.0 ^ 0.5 is undefined), the spec requires the result to be null, as for Exp and Ln.
            if (result.isInfinite() || result.isNaN()) {
                return null
            }
            return DecimalHelper.verifyPrecision(BigDecimal(result), null).toCqlDecimal()
        }

        throw InvalidOperatorArgument(
            "Power(Integer, Integer), Power(Long, Long) or Power(Decimal, Decimal)",
            "Power(${left.typeAsString}, ${right.typeAsString})",
        )
    }

    /**
     * Returns 1 / base^exponent, or null when base is 0, since that division by zero cannot be
     * represented.
     */
    private fun reciprocalPower(base: kotlin.Long, exponent: Int): Decimal? {
        if (base == 0L) {
            return null
        }
        return BigDecimal(1).divide(BigDecimal(base).pow(exponent)).toCqlDecimal()
    }
}
