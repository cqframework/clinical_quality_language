package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.cqframework.cql.shared.BigDecimal
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument
import org.opencds.cqf.cql.engine.execution.State
import org.opencds.cqf.cql.engine.runtime.CqlType
import org.opencds.cqf.cql.engine.runtime.Decimal
import org.opencds.cqf.cql.engine.runtime.Integer
import org.opencds.cqf.cql.engine.runtime.Interval
import org.opencds.cqf.cql.engine.runtime.Long
import org.opencds.cqf.cql.engine.runtime.Quantity
import org.opencds.cqf.cql.engine.runtime.toCqlDecimal
import org.opencds.cqf.cql.engine.runtime.toCqlInteger
import org.opencds.cqf.cql.engine.runtime.toCqlLong

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
    fun div(left: CqlType?, right: CqlType?, state: State?): CqlType? {
        if (left == null || right == null) {
            return null
        }

        if (left is Integer && right is Integer) {
            if (right.value == 0) {
                return null
            }

            return (left.value / right.value).toCqlInteger()
        } else if (left is Long && right is Long) {
            if (right.value == 0L) {
                return null
            }

            return (left.value / right.value).toCqlLong()
        } else if (left is Decimal && right is Decimal) {
            if (
                EqualEvaluator.equal(right, BigDecimal("0.0").toCqlDecimal(), state)?.value == true
            ) {
                return null
            }

            return left.value.divideAndRemainder(right.value)[0].toCqlDecimal()
        } else if (left is Quantity && right is Quantity) {
            if (
                EqualEvaluator.equal(
                        right.value?.toCqlDecimal(),
                        BigDecimal("0.0").toCqlDecimal(),
                        state,
                    )
                    ?.value == true
            ) {
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
            "TruncatedDivide(${left.typeAsString}, ${right.typeAsString})",
        )
    }
}
