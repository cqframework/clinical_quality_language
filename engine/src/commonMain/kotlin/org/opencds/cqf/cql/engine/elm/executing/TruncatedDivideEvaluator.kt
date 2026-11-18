package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.cqframework.cql.shared.BigDecimal
import org.cqframework.cql.shared.RoundingMode
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument
import org.opencds.cqf.cql.engine.execution.State
import org.opencds.cqf.cql.engine.runtime.Decimal
import org.opencds.cqf.cql.engine.runtime.DecimalHelper
import org.opencds.cqf.cql.engine.runtime.Integer
import org.opencds.cqf.cql.engine.runtime.Interval
import org.opencds.cqf.cql.engine.runtime.Long
import org.opencds.cqf.cql.engine.runtime.Quantity
import org.opencds.cqf.cql.engine.runtime.Value
import org.opencds.cqf.cql.engine.runtime.computeInMoreGranularUnit
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
    fun div(left: Value?, right: Value?, state: State?): Value? {
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
            return divideQuantities(left, right, state)
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

    /**
     * "For division operations involving quantities, the resulting quantity will have an
     * appropriate unit." Div counts how many times the right quantity fits into the left, so it is
     * unitless after converting both to a common unit, the same way as for division (FHIR-59659):
     * `250 'cm' div 1 'm'` is `2 '1'`. Dividing by a unitless quantity keeps the left unit, as in
     * Divide. Quantities with no common unit are divided as in Divide, and the value is truncated.
     * "If the result of the operation cannot be represented … the result is null."
     */
    private fun divideQuantities(left: Quantity, right: Quantity, state: State?): Quantity? {
        val quotient =
            if (Quantity.isDefaultUnit(right.unit)) {
                Quantity()
                    .withUnit(left.unit)
                    .withValue(left.value!!.divideAndRemainder(right.value!!)[0])
            } else {
                computeInMoreGranularUnit(
                    left,
                    right,
                    { _, leftValue, rightValue ->
                        Quantity()
                            .withUnit("1")
                            .withValue(leftValue.divideAndRemainder(rightValue)[0])
                    },
                    state,
                ) ?: divideWithoutCommonUnit(left, right, state)
            }
        // The quotient is a whole number, so give it no decimal places (as in "4.14 div 2.06 //
        // 2"),
        // whatever scale unit conversion left on it. DOWN truncates toward zero.
        return quotient
            ?.takeIf { DecimalHelper.validateDecimal(it.value!!, null) != null }
            ?.let {
                Quantity().withUnit(it.unit).withValue(it.value!!.setScale(0, RoundingMode.DOWN))
            }
    }

    /**
     * Divides as in Divide; [divideQuantities] truncates the value. Divide throws when UCUM can't
     * combine the units, and "attempting to operate on quantities with invalid or special units
     * will result in null".
     */
    private fun divideWithoutCommonUnit(left: Quantity, right: Quantity, state: State?): Quantity? {
        return try {
            DivideEvaluator.divide(left, right, state) as Quantity?
        } catch (ignored: Exception) {
            null
        }
    }
}
