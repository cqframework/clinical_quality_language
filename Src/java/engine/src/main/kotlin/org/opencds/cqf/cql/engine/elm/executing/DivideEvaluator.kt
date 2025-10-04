package org.opencds.cqf.cql.engine.elm.executing

import java.math.BigDecimal
import java.math.RoundingMode
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument
import org.opencds.cqf.cql.engine.execution.State
import org.opencds.cqf.cql.engine.runtime.Interval
import org.opencds.cqf.cql.engine.runtime.Quantity
import org.opencds.cqf.cql.engine.runtime.Value

/*
/(left Decimal, right Decimal) Decimal
/(left Quantity, right Decimal) Quantity
/(left Quantity, right Quantity) Quantity

The divide (/) operator performs numeric division of its arguments.
Note that this operator is Decimal division; for Integer division, use the truncated divide (div) operator.
When invoked with Integer arguments, the arguments will be implicitly converted to Decimal.
TODO: For division operations involving quantities, the resulting quantity will have the appropriate unit. For example:
12 'cm2' / 3 'cm'
In this example, the result will have a unit of 'cm'.
If either argument is null, the result is null.
*/
object DivideEvaluator {
    private fun divideHelper(left: BigDecimal, right: BigDecimal?, state: State?): BigDecimal? {
        if (EqualEvaluator.equal(right, BigDecimal("0.0"), state) == true) {
            return null
        }

        try {
            return Value.verifyPrecision(left.divide(right), null)
        } catch (e: ArithmeticException) {
            return left.divide(right, 8, RoundingMode.FLOOR)
        }
    }

    @JvmStatic
    fun divide(left: Any?, right: Any?, state: State?): Any? {
        if (left == null || right == null) {
            return null
        }

        if (left is BigDecimal && right is BigDecimal) {
            return divideHelper(left, right, state)
        } else if (left is Quantity && right is Quantity) {
            val value = divideHelper(left.value!!, right.value, state)
            if (value == null) {
                return null
            }
            return Quantity().withValue(value).withUnit(left.unit)
        } else if (left is Quantity && right is BigDecimal) {
            val value = divideHelper(left.value!!, right, state)
            if (value == null) {
                return null
            }
            return Quantity().withValue(value).withUnit(left.unit)
        } else if (left is Interval && right is Interval) {
            val leftInterval = left
            val rightInterval = right

            return Interval(
                divide(leftInterval.start, rightInterval.start, state),
                true,
                divide(leftInterval.end, rightInterval.end, state),
                true,
            )
        }

        throw InvalidOperatorArgument(
            "Divide(Decimal, Decimal), Divide(Quantity, Decimal), Divide(Quantity, Quantity)",
            String.format("Divide(%s, %s)", left.javaClass.name, right.javaClass.name),
        )
    }
}
