package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.cqframework.cql.shared.BigDecimal
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument
import org.opencds.cqf.cql.engine.execution.State
import org.opencds.cqf.cql.engine.runtime.Interval
import org.opencds.cqf.cql.engine.runtime.Quantity
import org.opencds.cqf.cql.engine.util.javaClassName

/*
div(left Integer, right Integer) Integer
div(left Decimal, right Decimal) Decimal
div(left Long, right Long) Long
div(left Quantity, right Quantity) Quantity

The div operator performs truncated division of its arguments.
When invoked with mixed Integer and Decimal arguments, the Integer argument will be implicitly converted to Decimal.
If either argument is null, the result is null.
*/
object TruncatedDivideEvaluator {
    @JvmStatic
    fun div(left: Any?, right: Any?, state: State?): Any? {
        if (left == null || right == null) {
            return null
        }

        if (left is Int) {
            if (right as Int == 0) {
                return null
            }

            return left / right
        } else if (left is Long) {
            if (right as Long == 0L) {
                return null
            }

            return left / right
        } else if (left is BigDecimal) {
            if (EqualEvaluator.equal(right, BigDecimal("0.0"), state) == true) {
                return null
            }

            return left.divideAndRemainder(right as BigDecimal)[0]
        } else if (left is Quantity) {
            if (EqualEvaluator.equal((right as Quantity).value, BigDecimal("0.0"), state) == true) {
                return null
            }
            return Quantity()
                .withUnit(left.unit)
                .withValue(left.value!!.divideAndRemainder(right.value!!)[0])
        } else if (left is Interval && right is Interval) {
            val leftInterval = left
            val rightInterval = right

            return Interval(
                div(leftInterval.start, rightInterval.start, state),
                true,
                div(leftInterval.end, rightInterval.end, state),
                true,
            )
        }

        throw InvalidOperatorArgument(
            "TruncatedDivide(Integer, Integer), TruncatedDivide(Decimal, Decimal),  TruncatedDivide(Quantity, Quantity)",
            "TruncatedDivide(${left.javaClassName}, ${right.javaClassName})",
        )
    }
}
