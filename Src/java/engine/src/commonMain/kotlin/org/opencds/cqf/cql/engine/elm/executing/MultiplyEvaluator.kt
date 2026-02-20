package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.cqframework.cql.shared.BigDecimal
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument
import org.opencds.cqf.cql.engine.execution.State
import org.opencds.cqf.cql.engine.runtime.Interval
import org.opencds.cqf.cql.engine.runtime.Quantity
import org.opencds.cqf.cql.engine.runtime.Value
import org.opencds.cqf.cql.engine.util.javaClassName

/*
*(left Integer, right Integer) Integer
*(left Long, right Long) Long
*(left Decimal, right Decimal) Decimal
*(left Decimal, right Quantity) Quantity
*(left Quantity, right Decimal) Quantity
*(left Quantity, right Quantity) Quantity

The multiply (*) operator performs numeric multiplication of its arguments.
When invoked with mixed Integer and Decimal arguments, the Integer argument will be implicitly converted to Decimal.
For multiplication operations involving quantities, the resulting quantity will have the appropriate unit. For example:
12 'cm' * 3 'cm'
3 'cm' * 12 'cm2'
In this example, the first result will have a unit of 'cm2', and the second result will have a unit of 'cm3'.
If either argument is null, the result is null.
*/
@Suppress("CyclomaticComplexMethod", "ReturnCount")
object MultiplyEvaluator {
    @JvmStatic
    fun multiply(left: Any?, right: Any?, state: State?): Any? {
        if (left == null || right == null) {
            return null
        }

        // *(Integer, Integer)
        if (left is Int) {
            return left * right as Int
        } else if (left is Long) {
            return left * right as Long
        } else if (left is BigDecimal && right is BigDecimal) {
            return Value.verifyPrecision(left.multiply(right), null)
        } else if (left is Quantity && right is Quantity) {
            val leftValue = left.value!!
            val leftUnit = left.unit!!
            val rightValue = right.value!!
            val rightUnit = right.unit!!
            val unverifiedResultValue: BigDecimal
            val resultUnit: String

            // Two fast-path cases: if either unit is "1", skip unit conversion
            if (leftUnit == "1") {
                unverifiedResultValue = leftValue.multiply(rightValue)
                resultUnit = rightUnit
            } else if (rightUnit == "1") {
                unverifiedResultValue = leftValue.multiply(rightValue)
                resultUnit = leftUnit
            } else {
                try {
                    val ucumService = state?.environment?.libraryManager?.ucumService!!
                    val result =
                        ucumService.multiply(Pair(leftValue, leftUnit), Pair(rightValue, rightUnit))
                    unverifiedResultValue = result.first
                    val rawResultUnit = result.second
                    resultUnit = rawResultUnit.ifEmpty { "1" }
                } catch (e: Exception) {
                    @Suppress("TooGenericExceptionThrown") throw RuntimeException(e)
                }
            }
            val resultValue = Value.verifyPrecision(unverifiedResultValue, null)
            return Quantity().withValue(resultValue).withUnit(resultUnit)
        } else if (left is BigDecimal && right is Quantity) {
            val value = Value.verifyPrecision(left.multiply(right.value!!), null)
            return right.withValue(value)
        } else if (left is Quantity && right is BigDecimal) {
            val value = Value.verifyPrecision((left.value)!!.multiply(right), null)
            return left.withValue(value)
        } else if (left is Interval && right is Interval) {
            return Interval(
                multiply(left.start, right.start, state),
                true,
                multiply(left.end, right.end, state),
                true,
                state,
            )
        }

        throw InvalidOperatorArgument(
            "Multiply(Integer, Integer), Multiply(Long, Long), Multiply(Decimal, Decimal), Multiply(Decimal, Quantity), Multiply(Quantity, Decimal) or Multiply(Quantity, Quantity)",
            "Multiply(${left.javaClassName}, ${right.javaClassName})",
        )
    }
}
