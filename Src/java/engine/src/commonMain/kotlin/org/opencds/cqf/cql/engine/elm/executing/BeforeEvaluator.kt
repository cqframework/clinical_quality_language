package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.opencds.cqf.cql.engine.execution.State
import org.opencds.cqf.cql.engine.runtime.BaseTemporal
import org.opencds.cqf.cql.engine.runtime.Interval
import org.opencds.cqf.cql.engine.runtime.Precision

/*

*** NOTES FOR INTERVAL ***
before(left Interval<T>, right Interval<T>) Boolean
before(left T, right Interval<T>) Boolean
before(left interval<T>, right T) Boolean

The before operator for intervals returns true if the first interval ends before the second one starts.
  In other words, if the ending point of the first interval is less than the starting point of the second interval.
For the point-interval overload, the operator returns true if the given point is less than the start of the interval.
For the interval-point overload, the operator returns true if the given interval ends before the given point.
This operator uses the semantics described in the Start and End operators to determine interval boundaries.
If either argument is null, the result is null.


*** NOTES FOR DATETIME ***
before _precision_ of(left Date, right Date) Boolean
before _precision_ of(left DateTime, right DateTime) Boolean
before _precision_ of(left Time, right Time) Boolean

The before-precision-of operator compares two date/time values to the specified precision to determine whether the first
    argument is the before the second argument. The comparison is performed by considering each precision in order,
    beginning with years (or hours for time values). If the values are the same, comparison proceeds to the next
    precision; if the first value is less than the second, the result is true; if the first value is greater than the
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
object BeforeEvaluator {
    @JvmStatic
    fun before(left: Any?, right: Any?, precision: String?, state: State?): Boolean? {
        var precision = precision
        if (left == null || right == null) {
            return null
        }

        if (left is Interval && right is Interval) {
            return before(left.end, right.start, precision, state)
        } else if (left is Interval) {
            return before(left.end, right, precision, state)
        } else if (right is Interval) {
            return before(left, right.start, precision, state)
        } else if (left is BaseTemporal && right is BaseTemporal) {
            if (precision == null) {
                precision = BaseTemporal.getHighestPrecision(left, right)
            }

            val result = left.compareToPrecision(right, Precision.fromString(precision))
            return if (result == null) null else result < 0
        }

        return LessEvaluator.less(left, right, state)
    }
}
