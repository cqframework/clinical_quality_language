package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.cqframework.cql.shared.BigDecimal
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument
import org.opencds.cqf.cql.engine.runtime.Quantity
import org.opencds.cqf.cql.engine.util.javaClassName

/*
Floor(argument Decimal) Integer

The Floor operator returns the first integer less than or equal to the argument.
When invoked with an Integer argument, the argument will be implicitly converted to Decimal.
If the argument is null, the result is null.
*/
object FloorEvaluator {
    @JvmStatic
    fun floor(operand: Any?): Any? {
        if (operand == null) {
            return null
        }

        if (operand is BigDecimal) {
            return BigDecimal(kotlin.math.floor(operand.toDouble())).toInt()
        } else if (operand is Quantity) {
            return BigDecimal(kotlin.math.floor(operand.value!!.toDouble())).toInt()
        }

        throw InvalidOperatorArgument("Floor(Decimal)", "Floor(${operand.javaClassName})")
    }
}
