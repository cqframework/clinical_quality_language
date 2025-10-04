package org.opencds.cqf.cql.engine.elm.executing

import java.math.BigDecimal
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument
import org.opencds.cqf.cql.engine.runtime.Quantity

/*
Abs(argument Integer) Integer
Abs(argument Long) Integer
Abs(argument Decimal) Decimal
Abs(argument Quantity) Quantity

The Abs operator returns the absolute value of its argument.
When taking the absolute value of a quantity, the unit is unchanged.
If the argument is null, the result is null.
*/
object AbsEvaluator {
    @JvmStatic
    fun abs(operand: Any?): Any? {
        if (operand == null) {
            return null
        }

        if (operand is Int) {
            return kotlin.math.abs(operand)
        } else if (operand is Long) {
            return kotlin.math.abs(operand)
        } else if (operand is BigDecimal) {
            return operand.abs()
        } else if (operand is Quantity) {
            return Quantity().withValue((operand.value)!!.abs()).withUnit(operand.unit)
        }

        throw InvalidOperatorArgument(
            "Abs(Integer), Abs(Long), Abs(Decimal) or Abs(Quantity)",
            String.format("Abs(%s)", operand.javaClass.name),
        )
    }
}
