package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.cqframework.cql.shared.BigDecimal
import org.cqframework.cql.shared.RoundingMode
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument
import org.opencds.cqf.cql.engine.execution.State
import org.opencds.cqf.cql.engine.runtime.Decimal
import org.opencds.cqf.cql.engine.runtime.DecimalHelper
import org.opencds.cqf.cql.engine.runtime.Interval
import org.opencds.cqf.cql.engine.runtime.Quantity
import org.opencds.cqf.cql.engine.runtime.Value
import org.opencds.cqf.cql.engine.runtime.toCqlDecimal

/*
/(left Decimal, right Decimal) Decimal
/(left Quantity, right Decimal) Quantity
/(left Quantity, right Quantity) Quantity

The divide (/) operator performs numeric division of its arguments.
Note that this operator is Decimal division; for Integer division, use the truncated divide (div) operator.
When invoked with Integer arguments, the arguments will be implicitly converted to Decimal.
For division operations involving quantities, the resulting quantity will have the appropriate unit. For example:
12 'cm2' / 3 'cm'
In this example, the result will have a unit of 'cm'.
If either argument is null, the result is null.
*/
@Suppress("CyclomaticComplexMethod")
object DivideEvaluator {
    /** 1 with enough digits that UCUM's significant-figure arithmetic keeps the exact factor. */
    private val PRECISE_ONE = BigDecimal("1.00000000000000000000")

    private fun divideHelper(left: Decimal, right: Decimal?, state: State?): Decimal? {
        if (EqualEvaluator.equal(right, BigDecimal("0.0").toCqlDecimal(), state)?.value == true) {
            return null
        }

        return try {
            DecimalHelper.verifyPrecision(left.value.divide(right!!.value), null).toCqlDecimal()
        } catch (e: ArithmeticException) {
            left.value.divide(right!!.value, 8, RoundingMode.FLOOR).toCqlDecimal()
        }
    }

    @JvmStatic
    fun divide(left: Value?, right: Value?, state: State?): Value? {
        if (left == null || right == null) {
            return null
        }

        if (left is Decimal && right is Decimal) {
            return divideHelper(left, right, state)
        } else if (left is Quantity && right is Quantity) {
            val leftValue = left.value!!
            val leftUnit = left.unit!!
            val rightValue = right.value!!
            val rightUnit = right.unit!!
            if (
                EqualEvaluator.equal(
                        rightValue.toCqlDecimal(),
                        BigDecimal("0.0").toCqlDecimal(),
                        state,
                    )
                    ?.value != false
            ) {
                return null
            }
            val resultValue: Decimal?
            val resultUnit: String
            if (rightUnit == "1") {
                resultValue =
                    divideHelper(leftValue.toCqlDecimal(), rightValue.toCqlDecimal(), state)
                resultUnit = leftUnit
            } else {
                val ucumService = state?.environment?.libraryManager?.ucumService!!
                try {
                    // UCUM gives the result unit and the conversion factor between the units, but
                    // rounds its result to the operands' significant figures (-5.5 / 2.0 gives
                    // -2.8). So take only the factor from UCUM, at high precision, and divide the
                    // values as Decimals: -5.5 'mg' / 2.0 'mg' is -2.75 '1'.
                    val units =
                        ucumService.divideBy(
                            Pair(PRECISE_ONE, leftUnit),
                            Pair(PRECISE_ONE, rightUnit),
                        )
                    resultValue =
                        divideHelper(
                            leftValue.multiply(units.first).toCqlDecimal(),
                            rightValue.toCqlDecimal(),
                            state,
                        )
                    resultUnit = units.second.ifEmpty { "1" }
                } catch (e: Exception) {
                    @Suppress("TooGenericExceptionThrown") throw RuntimeException(e)
                }
            }
            return Quantity().withValue(resultValue?.value).withUnit(resultUnit)
        } else if (left is Quantity && right is Decimal) {
            val value = divideHelper(left.value!!.toCqlDecimal(), right, state)
            return if (value != null) Quantity().withValue(value.value).withUnit(left.unit)
            else null
        } else if (left is Interval && right is Interval) {
            return Interval(
                divide(left.start, right.start, state),
                true,
                divide(left.end, right.end, state),
                true,
                state = state,
            )
        }

        throw InvalidOperatorArgument(
            "Divide(Decimal, Decimal), Divide(Quantity, Decimal), Divide(Quantity, Quantity)",
            "Divide(${left.typeAsString}, ${right.typeAsString})",
        )
    }
}
