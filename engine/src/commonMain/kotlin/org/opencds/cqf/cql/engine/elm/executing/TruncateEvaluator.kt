package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.cqframework.cql.shared.RoundingMode
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument
import org.opencds.cqf.cql.engine.runtime.Decimal
import org.opencds.cqf.cql.engine.runtime.Integer
import org.opencds.cqf.cql.engine.runtime.Value
import org.opencds.cqf.cql.engine.runtime.toCqlInteger

/*
Truncate(argument Decimal) Integer

The Truncate operator returns the integer component of its argument.
When invoked with an Integer argument, the argument will be implicitly converted to Decimal.
If the argument is null, the result is null.
*/
object TruncateEvaluator {
    @JvmStatic
    fun truncate(operand: Value?): Integer? {
        if (operand == null) {
            return null
        }

        if (operand is Decimal) {
            val `val` = operand.value.toDouble()
            if (`val` < 0) {
                return operand.value.setScale(0, RoundingMode.CEILING).toInt().toCqlInteger()
            } else {
                return operand.value.setScale(0, RoundingMode.FLOOR).toInt().toCqlInteger()
            }
        }

        throw InvalidOperatorArgument("Truncate(Decimal)", "Truncate(${operand.typeAsString})")
    }
}
