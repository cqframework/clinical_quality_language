package org.opencds.cqf.cql.engine.elm.executing

import java.math.BigDecimal
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument
import org.opencds.cqf.cql.engine.runtime.Interval
import org.opencds.cqf.cql.engine.runtime.Quantity
import org.opencds.cqf.cql.engine.runtime.Value

/*
*(left Integer, right Integer) Integer
*(left Long, right Long) Long
*(left Decimal, right Decimal) Decimal
*(left Decimal, right Quantity) Quantity
*(left Quantity, right Decimal) Quantity
*(left Quantity, right Quantity) Quantity

The multiply (*) operator performs numeric multiplication of its arguments.
When invoked with mixed Integer and Decimal arguments, the Integer argument will be implicitly converted to Decimal.
TODO: For multiplication operations involving quantities, the resulting quantity will have the appropriate unit. For example:
12 'cm' * 3 'cm'
3 'cm' * 12 'cm2'
In this example, the first result will have a unit of 'cm2', and the second result will have a unit of 'cm3'.
If either argument is null, the result is null.
*/
object MultiplyEvaluator {
    @JvmStatic
    fun multiply(left: Any?, right: Any?): Any? {
        if (left == null || right == null) {
            return null
        }

        // *(Integer, Integer)
        if (left is Int) {
            return left * right as Int
        }

        if (left is Long) {
            return left * right as Long
        } else if (left is BigDecimal && right is BigDecimal) {
            return Value.verifyPrecision(left.multiply(right), null)
        } else if (left is Quantity && right is Quantity) {
            // TODO: unit multiplication i.e. cm*cm = cm^2
            val unit = if (left.unit == "1") right.unit else left.unit
            val value = Value.verifyPrecision((left.value)!!.multiply(right.value), null)
            return Quantity().withValue(value).withUnit(unit)
        } else if (left is BigDecimal && right is Quantity) {
            val value = Value.verifyPrecision(left.multiply(right.value), null)
            return right.withValue(value)
        } else if (left is Quantity && right is BigDecimal) {
            val value = Value.verifyPrecision((left.value)!!.multiply(right), null)
            return left.withValue(value)
        } else if (left is Interval && right is Interval) {
            val leftInterval = left
            val rightInterval = right
            return Interval(
                multiply(leftInterval.start, rightInterval.start),
                true,
                multiply(leftInterval.end, rightInterval.end),
                true,
            )
        }

        throw InvalidOperatorArgument(
            "Multiply(Integer, Integer), Multiply(Long, Long), Multiply(Decimal, Decimal), Multiply(Decimal, Quantity), Multiply(Quantity, Decimal) or Multiply(Quantity, Quantity)",
            "Multiply(${left.javaClass.name}, ${right.javaClass.name})",
        )
    }
}
