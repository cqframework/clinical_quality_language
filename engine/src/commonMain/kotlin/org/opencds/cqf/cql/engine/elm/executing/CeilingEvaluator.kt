package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.cqframework.cql.shared.BigDecimal
import org.cqframework.cql.shared.ONE
import org.cqframework.cql.shared.RoundingMode
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument
import org.opencds.cqf.cql.engine.runtime.Decimal
import org.opencds.cqf.cql.engine.runtime.DecimalHelper
import org.opencds.cqf.cql.engine.runtime.Integer
import org.opencds.cqf.cql.engine.runtime.Quantity
import org.opencds.cqf.cql.engine.runtime.Value

/*
Ceiling(argument Decimal) Integer

The Ceiling operator returns the first integer greater than or equal to the argument.
When invoked with an Integer argument, the argument will be implicitly converted to Decimal.
If the argument is null, the result is null.
*/
object CeilingEvaluator {
    @JvmStatic
    fun ceiling(operand: Value?): Integer? {
        if (operand == null) {
            return null
        }

        if (operand is Decimal) {
            return ceiling(operand.value)
        } else if (operand is Quantity) {
            return ceiling(operand.value!!)
        }

        throw InvalidOperatorArgument("Ceiling(Decimal)", "Ceiling(${operand.typeAsString})")
    }

    private fun ceiling(value: BigDecimal): Integer? {
        // setScale always rounds toward zero on JS and WASM, so round toward zero and then step up
        // if a positive fractional part was dropped.
        val truncated = value.setScale(0, RoundingMode.DOWN)
        val result = if (value > truncated) truncated.add(ONE) else truncated
        return DecimalHelper.toIntegerOrNull(result)
    }
}
