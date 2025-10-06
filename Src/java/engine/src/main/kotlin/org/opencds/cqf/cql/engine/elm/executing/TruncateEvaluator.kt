package org.opencds.cqf.cql.engine.elm.executing

import java.math.BigDecimal
import java.math.RoundingMode
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument

/*
Truncate(argument Decimal) Integer

The Truncate operator returns the integer component of its argument.
When invoked with an Integer argument, the argument will be implicitly converted to Decimal.
If the argument is null, the result is null.
*/
object TruncateEvaluator {
    @JvmStatic
    fun truncate(operand: Any?): Any? {
        if (operand == null) {
            return null
        }

        if (operand is BigDecimal) {
            val `val` = operand.toDouble()
            if (`val` < 0) {
                return operand.setScale(0, RoundingMode.CEILING).toInt()
            } else {
                return operand.setScale(0, RoundingMode.FLOOR).toInt()
            }
        }

        throw InvalidOperatorArgument(
            "Truncate(Decimal)",
            String.format("Truncate(%s)", operand.javaClass.name),
        )
    }
}
