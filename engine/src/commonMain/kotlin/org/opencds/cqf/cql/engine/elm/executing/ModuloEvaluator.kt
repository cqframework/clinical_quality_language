package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.cqframework.cql.shared.BigDecimal
import org.cqframework.cql.shared.RoundingMode
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument
import org.opencds.cqf.cql.engine.execution.State
import org.opencds.cqf.cql.engine.runtime.Decimal
import org.opencds.cqf.cql.engine.runtime.Integer
import org.opencds.cqf.cql.engine.runtime.Long
import org.opencds.cqf.cql.engine.runtime.Quantity
import org.opencds.cqf.cql.engine.runtime.Value
import org.opencds.cqf.cql.engine.runtime.computeInMoreGranularUnit
import org.opencds.cqf.cql.engine.runtime.toCqlDecimal
import org.opencds.cqf.cql.engine.runtime.toCqlInteger
import org.opencds.cqf.cql.engine.runtime.toCqlLong

/*
mod(left Integer, right Integer) Integer
mod(left Long, right Long) Long
mod(left Decimal, right Decimal) Decimal
mod(left Quantity, right Quantity) Quantity

The mod operator computes the remainder of the division of its arguments.
When invoked with mixed Integer and Decimal arguments, the Integer argument will be implicitly converted to Decimal.
If either argument is null, the result is null.
*/
object ModuloEvaluator {
    @JvmStatic
    fun modulo(left: Value?, right: Value?, state: State?): Value? {
        if (left == null || right == null) {
            return null
        }

        if (left is Integer && right is Integer) {
            if (right.value == 0) {
                return null
            }
            return (left.value % right.value).toCqlInteger()
        }

        if (left is Long && right is Long) {
            if (right.value == 0L) {
                return null
            }
            return (left.value % right.value).toCqlLong()
        }

        if (left is Decimal && right is Decimal) {
            if (right.value == BigDecimal("0.0")) {
                return null
            }
            return left.value.remainder(right.value).setScale(8, RoundingMode.FLOOR).toCqlDecimal()
        }

        if (left is Quantity) {
            if ((right as Quantity).value!!.compareTo(BigDecimal("0.0")) == 0) {
                return null
            }

            return moduloQuantities(left, right, state)
        }

        throw InvalidOperatorArgument(
            "Modulo(Integer, Integer), Modulo(Long, Long) or Modulo(Decimal, Decimal), , Modulo(Quantity, Quantity)",
            "Modulo(${left.typeAsString}, ${right.typeAsString})",
        )
    }

    /**
     * Modulo "should have the same units as its operands after converting to common units in the
     * same way as for division" (FHIR-59659), since the result is what is left over of the original
     * quantity. Division converts to the most granular unit, so `1 'm' mod 30 'cm'` is `10 'cm'`. A
     * unitless right operand keeps the left unit, as in Divide: `10 'g' mod 3` is `1 'g'`.
     * Quantities that can't be converted to a common unit give null.
     */
    private fun moduloQuantities(left: Quantity, right: Quantity, state: State?): Quantity? {
        if (Quantity.isDefaultUnit(right.unit)) {
            return Quantity().withUnit(left.unit).withValue(remainder(left.value!!, right.value!!))
        }

        return computeInMoreGranularUnit(
            left,
            right,
            { unit, leftValue, rightValue ->
                Quantity().withUnit(unit).withValue(remainder(leftValue, rightValue))
            },
            state,
        )
    }

    private fun remainder(left: BigDecimal, right: BigDecimal): BigDecimal {
        return left.remainder(right).setScale(8, RoundingMode.FLOOR)
    }
}
