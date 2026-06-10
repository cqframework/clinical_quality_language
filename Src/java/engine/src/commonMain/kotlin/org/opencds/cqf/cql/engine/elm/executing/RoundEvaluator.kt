package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.cqframework.cql.shared.BigDecimal
import org.cqframework.cql.shared.RoundingMode
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument
import org.opencds.cqf.cql.engine.runtime.Decimal
import org.opencds.cqf.cql.engine.runtime.Integer
import org.opencds.cqf.cql.engine.runtime.Value
import org.opencds.cqf.cql.engine.runtime.toCqlDecimal

/*
Round(argument Decimal) Decimal
Round(argument Decimal, precision Integer) Decimal

The Round operator returns the nearest whole number to its argument. The semantics of round are defined as a traditional
  round, meaning that a decimal value of 0.5 or higher will round to 1.
When invoked with an Integer argument, the argument will be implicitly converted to Decimal.
If the argument is null, the result is null.
Precision determines the decimal place at which the rounding will occur.
If precision is not specified or null, 0 is assumed.
*/
object RoundEvaluator {
    @JvmStatic
    fun round(operand: Value?, precision: Value?): Decimal? {
        var rm = RoundingMode.HALF_UP

        if (operand == null) {
            return null
        }

        if (operand is Decimal && precision is Integer?) {
            if (operand.value.compareTo(BigDecimal(0)) < 0) {
                rm = RoundingMode.HALF_DOWN
            }

            if (precision == null || (precision.value == 0)) {
                return operand.value.setScale(0, rm).toCqlDecimal()
            } else {
                return operand.value.setScale(precision.value, rm).toCqlDecimal()
            }
        }

        throw InvalidOperatorArgument(
            "Round(Decimal) or Round(Decimal, Integer)",
            "Round(${operand.typeAsString}${if (precision == null) "" else ", " + precision.typeAsString})",
        )
    }
}
