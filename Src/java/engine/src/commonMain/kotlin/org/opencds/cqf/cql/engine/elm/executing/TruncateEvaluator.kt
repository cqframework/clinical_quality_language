package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.cqframework.cql.shared.BigDecimal
import org.cqframework.cql.shared.RoundingMode
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument
import org.opencds.cqf.cql.engine.util.javaClassName

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

        throw InvalidOperatorArgument("Truncate(Decimal)", "Truncate(${operand.javaClassName})")
    }
}
