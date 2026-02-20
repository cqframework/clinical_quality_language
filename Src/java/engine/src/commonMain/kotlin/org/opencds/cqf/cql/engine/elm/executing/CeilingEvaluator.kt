package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import kotlin.math.ceil
import org.cqframework.cql.shared.BigDecimal
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument
import org.opencds.cqf.cql.engine.runtime.Quantity
import org.opencds.cqf.cql.engine.util.javaClassName

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
            return BigDecimal(ceil(operand.toDouble())).toInt()
        } else if (operand is Quantity) {
            return BigDecimal(ceil(operand.value!!.toDouble())).toInt()
        }

        throw InvalidOperatorArgument("Ceiling(Decimal)", "Ceiling(${operand.javaClassName})")
    }
}
