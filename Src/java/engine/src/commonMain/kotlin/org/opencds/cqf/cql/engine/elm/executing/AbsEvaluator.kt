package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument
import org.opencds.cqf.cql.engine.runtime.CqlType
import org.opencds.cqf.cql.engine.runtime.Decimal
import org.opencds.cqf.cql.engine.runtime.Integer
import org.opencds.cqf.cql.engine.runtime.Long
import org.opencds.cqf.cql.engine.runtime.Quantity
import org.opencds.cqf.cql.engine.runtime.toCqlDecimal
import org.opencds.cqf.cql.engine.runtime.toCqlInteger
import org.opencds.cqf.cql.engine.runtime.toCqlLong

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
    fun abs(operand: CqlType?): CqlType? {
        if (operand == null) {
            return null
        }

        return when (operand) {
            is Integer -> kotlin.math.abs(operand.value).toCqlInteger()
            is Long -> kotlin.math.abs(operand.value).toCqlLong()
            is Decimal -> operand.value.abs().toCqlDecimal()
            is Quantity -> Quantity().withValue((operand.value)!!.abs()).withUnit(operand.unit)
            else ->
                throw InvalidOperatorArgument(
                    "Abs(Integer), Abs(Long), Abs(Decimal) or Abs(Quantity)",
                    "Abs(${operand.typeAsString})",
                )
        }
    }
}
