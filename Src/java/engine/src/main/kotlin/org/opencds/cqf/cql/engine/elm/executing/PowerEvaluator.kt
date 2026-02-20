package org.opencds.cqf.cql.engine.elm.executing

import java.math.BigDecimal
import kotlin.math.abs
import kotlin.math.pow
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument
import org.opencds.cqf.cql.engine.runtime.Value

/*
^(argument Integer, exponent Integer) Integer
^(argument Decimal, exponent Decimal) Decimal

The power (^) operator raises the first argument to the power given by the second argument.
When invoked with mixed Integer and Decimal arguments, the Integer argument will be implicitly converted to Decimal.
If either argument is null, the result is null.
*/
object PowerEvaluator {
    @JvmStatic
    fun power(left: Any?, right: Any?): Any? {
        if (left == null || right == null) {
            return null
        }

        if (left is Int) {
            if ((right as Int) < 0) {
                return BigDecimal(1).divide(BigDecimal(left).pow(abs(right)))
            }
            return BigDecimal(left).pow(right).toInt()
        }

        if (left is Long) {
            if ((right as Long) < 0) {
                return BigDecimal(1).divide(BigDecimal(left).pow(abs(right.toInt())))
            }

            return BigDecimal(left).pow((right.toInt() as Int?)!!).toLong()
        }

        if (left is BigDecimal) {
            return Value.verifyPrecision(
                BigDecimal((left.toDouble()).pow((right as BigDecimal).toDouble())),
                null,
            )
        }

        throw InvalidOperatorArgument(
            "Power(Integer, Integer), Power(Long, Long) or Power(Decimal, Decimal)",
            "Power(${left.javaClass.name}, ${right.javaClass.name})",
        )
    }
}
