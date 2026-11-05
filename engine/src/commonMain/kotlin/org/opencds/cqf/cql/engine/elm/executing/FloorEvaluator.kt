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
Floor(argument Decimal) Integer

The Floor operator returns the first integer less than or equal to the argument.
When invoked with an Integer argument, the argument will be implicitly converted to Decimal.
If the argument is null, the result is null.
*/
object FloorEvaluator {
    @JvmStatic
    fun floor(operand: Value?): Integer? {
        if (operand == null) {
            return null
        }

        if (operand is Decimal) {
            return floor(operand.value)
        } else if (operand is Quantity) {
            return floor(operand.value!!)
        }

        throw InvalidOperatorArgument("Floor(Decimal)", "Floor(${operand.typeAsString})")
    }

    private fun floor(value: BigDecimal): Integer? {
        // setScale always rounds toward zero on JS and WASM, so round toward zero and then step
        // down if a negative fractional part was dropped.
        val truncated = value.setScale(0, RoundingMode.DOWN)
        val result = if (value < truncated) truncated.subtract(ONE) else truncated
        return DecimalHelper.toIntegerOrNull(result)
    }
}
