package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.opencds.cqf.cql.engine.execution.State
import org.opencds.cqf.cql.engine.runtime.BaseTemporal
import org.opencds.cqf.cql.engine.runtime.Interval
import org.opencds.cqf.cql.engine.runtime.Precision

/*

*** NOTES FOR INTERVAL ***
after(left Interval<T>, right Interval<T>) Boolean
after(left T, right Interval<T>) Boolean
after(left Interval<T>, right T) Boolean

The after operator for intervals returns true if the first interval starts after the second one ends.
  In other words, if the starting point of the first interval is greater than the ending point of the second interval.
For the point-interval overload, the operator returns true if the given point is greater than the end of the interval.
For the interval-point overload, the operator returns true if the given interval starts after the given point.
This operator uses the semantics described in the Start and End operators to determine interval boundaries.
If either argument is null, the result is null.


*** NOTES FOR DATETIME ***
after _precision_ of(left Date, right Date) Boolean
after _precision_ of(left DateTime, right DateTime) Boolean
after _precision_ of(left Time, right Time) Boolean

The after-precision-of operator compares two date/time values to the specified precision to determine whether the first
    argument is the after the second argument. The comparison is performed by considering each precision in order,
    beginning with years (or hours for time values). If the values are the same, comparison proceeds to the next
    precision; if the first value is greater than the second, the result is true; if the first value is less than the
    second, the result is false; if either input has no value for the precision, the comparison stops and the result is
    null; if the specified precision has been reached, the comparison stops and the result is false.

If no precision is specified, the comparison is performed beginning with years (or hours for time values) and proceeding
    to the finest precision specified in either input.

For Date values, precision must be one of: year, month, or day.
For DateTime values, precision must be one of: year, month, day, hour, minute, second, or millisecond.
For Time values, precision must be one of: hour, minute, second, or millisecond.

Note specifically that due to variability in the way week numbers are determined, comparisons involving weeks are not supported.

When this operator is called with both Date and DateTime inputs, the Date values will be implicitly converted to
    DateTime values as defined by the ToDateTime operator.

As with all date/time calculations, comparisons are performed respecting the timezone offset.

If either or both arguments are null, the result is null.

*/
object AfterEvaluator {
    @JvmStatic
    fun after(left: Any?, right: Any?, precision: String?, state: State?): Boolean? {
        var precision = precision
        if (left == null || right == null) {
            return null
        }

        // (Interval, Interval)
        if (left is Interval && right is Interval) {
            return after(left.start, right.end, precision, state)
        } else if (left is Interval) {
            return after(left.start, right, precision, state)
        } else if (right is Interval) {
            return after(left, right.end, precision, state)
        } else if (left is BaseTemporal && right is BaseTemporal) {
            if (precision == null) {
                precision = BaseTemporal.getHighestPrecision(left, right)
            }

            val result = left.compareToPrecision(right, Precision.fromString(precision))
            return if (result == null) null else result > 0
        }

        return GreaterEvaluator.greater(left, right, state)
    }
}
