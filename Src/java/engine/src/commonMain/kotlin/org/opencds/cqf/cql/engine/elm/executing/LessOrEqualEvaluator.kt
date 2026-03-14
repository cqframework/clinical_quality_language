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
<=(left Integer, right Integer) Boolean
<=(left Long, right Long) Boolean
<=(left Decimal, right Decimal) Boolean
<=(left Quantity, right Quantity) Boolean
<=(left Date, right Date) Boolean
<=(left DateTime, right DateTime) Boolean
<=(left Time, right Time) Boolean
<=(left String, right String) Boolean

The less or equal (<=) operator returns true if the first argument is less than or equal to the second argument.

String comparisons are strictly lexical based on the Unicode value of the individual characters in the string.

For comparisons involving quantities, the dimensions of each quantity must be the same, but not necessarily the unit.
    For example, units of 'cm' and 'm' are comparable, but units of 'cm2' and 'cm' are not. Attempting to operate on
    quantities with invalid units will result in a null. When a quantity has no units specified, it is treated as a
    quantity with the default unit ('1').

For date/time values, the comparison is performed by considering each precision in order, beginning with years
    (or hours for time values). If the values are the same, comparison proceeds to the next precision; if the
    first value is less than the second, the result is true; if the first value is greater than the second, the
    result is false; if one input has a value for the precision and the other does not, the comparison stops and
    the result is null; if neither input has a value for the precision or the last precision has been reached,
    the comparison stops and the result is true.
*/
object LessOrEqualEvaluator {
    @JvmStatic
    fun lessOrEqual(left: Any?, right: Any?, state: State?): Boolean? {
        if (left == null || right == null) {
            return null
        }

        if (left is Int && right is Int) {
            return left.compareTo(right) <= 0
        }

        if (left is Long && right is Long) {
            return left.compareTo(right) <= 0
        } else if (left is BigDecimal && right is BigDecimal) {
            return left.compareTo(right) <= 0
        } else if (left is Quantity && right is Quantity) {
            if (left.value == null || right.value == null) {
                return null
            }
            val nullableCompareTo = compareQuantities(left, right, state)
            return if (nullableCompareTo == null) null else nullableCompareTo <= 0
        } else if (left is BaseTemporal && right is BaseTemporal) {
            val i = left.compare(right, false)
            return if (i == null) null else i <= 0
        } else if (left is String && right is String) {
            return left.compareTo(right) <= 0
        } else if ((left is Interval && right is Int) || (left is Int && right is Interval)) {
            return LessEvaluator.less(left, right, state)
        }

        throw InvalidOperatorArgument(
            "LessOrEqual(Integer, Integer), LessOrEqual(Long, Long), LessOrEqual(Decimal, Decimal), LessOrEqual(Quantity, Quantity), LessOrEqual(Date, Date), LessOrEqual(DateTime, DateTime), LessOrEqual(Time, Time) or LessOrEqual(String, String)",
            "LessOrEqual(${left.javaClassName}, ${right.javaClassName})",
        )
    }
}
