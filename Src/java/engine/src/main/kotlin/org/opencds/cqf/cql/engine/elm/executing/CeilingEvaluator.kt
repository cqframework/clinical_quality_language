package org.opencds.cqf.cql.engine.elm.executing

import java.math.BigDecimal
import kotlin.math.ceil
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument
import org.opencds.cqf.cql.engine.runtime.Quantity

/*
Ceiling(argument Decimal) Integer

The Ceiling operator returns the first integer greater than or equal to the argument.
When invoked with an Integer argument, the argument will be implicitly converted to Decimal.
If the argument is null, the result is null.
*/
object CeilingEvaluator {
    @JvmStatic
    fun ceiling(operand: Any?): Any? {
        if (operand == null) {
            return null
        }

        if (operand is BigDecimal) {
            return BigDecimal.valueOf(ceil(operand.toDouble())).toInt()
        } else if (operand is Quantity) {
            return BigDecimal.valueOf(ceil(operand.value!!.toDouble())).toInt()
        }

        throw InvalidOperatorArgument("Ceiling(Decimal)", "Ceiling(${operand.javaClass.name})")
    }
}
