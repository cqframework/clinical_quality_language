package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.cqframework.cql.shared.BigDecimal
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument
import org.opencds.cqf.cql.engine.execution.State
import org.opencds.cqf.cql.engine.runtime.BaseTemporal
import org.opencds.cqf.cql.engine.runtime.Interval
import org.opencds.cqf.cql.engine.runtime.Quantity
import org.opencds.cqf.cql.engine.runtime.compareQuantities
import org.opencds.cqf.cql.engine.util.javaClassName

/*
>=(left Integer, right Integer) Boolean
>=(left Long, right Long) Boolean
>=(left Decimal, right Decimal) Boolean
>=(left Quantity, right Quantity) Boolean
>=(left DateTime, right DateTime) Boolean
>=(left Time, right Time) Boolean
>=(left String, right String) Boolean

The greater or equal (>=) operator returns true if the first argument is greater than or equal to the second argument.
For comparisons involving quantities, the dimensions of each quantity must be the same, but not necessarily the unit.
  For example, units of 'cm' and 'm' are comparable, but units of 'cm2' and  'cm' are not.
For comparisons involving date/time or time values with imprecision, note that the result of the comparison may be null,
  depending on whether the values involved are specified to the level of precision used for the comparison.
If either argument is null, the result is null.
*/
object GreaterOrEqualEvaluator {
    @JvmStatic
    fun greaterOrEqual(left: Any?, right: Any?, state: State?): Boolean? {
        if (left == null || right == null) {
            return null
        }

        if (left is Int && right is Int) {
            return left.compareTo(right) >= 0
        }

        if (left is Long && right is Long) {
            return left.compareTo(right) >= 0
        } else if (left is BigDecimal && right is BigDecimal) {
            return left.compareTo(right) >= 0
        } else if (left is Quantity && right is Quantity) {
            if (left.value == null || right.value == null) {
                return null
            }
            val nullableCompareTo = compareQuantities(left, right, state)
            return if (nullableCompareTo == null) null else nullableCompareTo >= 0
        } else if (left is BaseTemporal && right is BaseTemporal) {
            val i = left.compare(right, false)
            return if (i == null) null else i >= 0
        } else if (left is String && right is String) {
            return left.compareTo(right) >= 0
        } else if ((left is Interval && right is Int) || (left is Int && right is Interval)) {
            return GreaterEvaluator.greater(left, right, state)
        }

        throw InvalidOperatorArgument(
            "GreaterOrEqual(Integer, Integer), GreaterOrEqual(Long, Long), GreaterOrEqual(Decimal, Decimal), GreaterOrEqual(Quantity, Quantity), GreaterOrEqual(Date, Date), GreaterOrEqual(DateTime, DateTime), GreaterOrEqual(Time, Time) or GreaterOrEqual(String, String)",
            "Cannot perform greater than or equal operator on types ${left.javaClassName} and ${right.javaClassName}",
        )
    }
}
