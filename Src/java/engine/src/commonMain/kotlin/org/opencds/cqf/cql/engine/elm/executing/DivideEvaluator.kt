package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.cqframework.cql.shared.BigDecimal
import org.cqframework.cql.shared.RoundingMode
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument
import org.opencds.cqf.cql.engine.execution.State
import org.opencds.cqf.cql.engine.runtime.Interval
import org.opencds.cqf.cql.engine.runtime.Quantity
import org.opencds.cqf.cql.engine.runtime.Value
import org.opencds.cqf.cql.engine.util.javaClassName

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
    private fun divideHelper(left: BigDecimal, right: BigDecimal?, state: State?): BigDecimal? {
        if (EqualEvaluator.equal(right, BigDecimal("0.0"), state) == true) {
            return null
        }

        return try {
            Value.verifyPrecision(left.divide(right!!), null)
        } catch (e: ArithmeticException) {
            left.divide(right!!, 8, RoundingMode.FLOOR)
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
            val leftValue = left.value!!
            val leftUnit = left.unit!!
            val rightValue = right.value!!
            val rightUnit = right.unit!!
            if (EqualEvaluator.equal(rightValue, BigDecimal("0.0"), state) != false) {
                return null
            }
            val resultValue: BigDecimal?
            val resultUnit: String
            if (rightUnit == "1") {
                resultValue = divideHelper(leftValue, rightValue, state)
                resultUnit = leftUnit
            } else {
                val ucumService = state?.environment?.libraryManager?.ucumService!!
                try {
                    val result =
                        ucumService.divideBy(Pair(leftValue, leftUnit), Pair(rightValue, rightUnit))
                    val unverifiedResultValue = result.first
                    resultValue = Value.verifyPrecision(unverifiedResultValue, null)
                    val rawResultUnit = result.second
                    resultUnit = rawResultUnit.ifEmpty { "1" }
                } catch (e: Exception) {
                    @Suppress("TooGenericExceptionThrown") throw RuntimeException(e)
                }
            }
            return Quantity().withValue(resultValue).withUnit(resultUnit)
        } else if (left is Quantity && right is BigDecimal) {
            val value = divideHelper(left.value!!, right, state)
            return if (value != null) Quantity().withValue(value).withUnit(left.unit) else null
        } else if (left is Interval && right is Interval) {
            return Interval(
                divide(left.start, right.start, state),
                true,
                divide(left.end, right.end, state),
                true,
                state,
            )
        }

        throw InvalidOperatorArgument(
            "Divide(Decimal, Decimal), Divide(Quantity, Decimal), Divide(Quantity, Quantity)",
            "Divide(${left.javaClassName}, ${right.javaClassName})",
        )
    }
}
