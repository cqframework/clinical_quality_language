package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument
import org.opencds.cqf.cql.engine.execution.State
import org.opencds.cqf.cql.engine.runtime.BaseTemporal
import org.opencds.cqf.cql.engine.runtime.Interval
import org.opencds.cqf.cql.engine.runtime.Precision
import org.opencds.cqf.cql.engine.util.javaClassName

/*
*** SameOrAfter Temporal Overload ***
same _precision_ or after(left Date, right Date) Boolean
same _precision_ or after(left DateTime, right DateTime) Boolean
same _precision_ or after(left Time, right Time) Boolean

The same-precision-or after operator compares two date/time values to the specified precision to determine whether the
    first argument is the same or after the second argument. The comparison is performed by considering each precision
    in order, beginning with years (or hours for time values). If the values are the same, comparison proceeds to the
    next precision; if the first value is greater than the second, the result is true; if the first value is less than
    the second, the result is false; if either input has no value for the precision, the comparison stops and the result
    is null; if the specified precision has been reached, the comparison stops and the result is true.

If no precision is specified, the comparison is performed beginning with years (or hours for time values) and proceeding
    to the finest precision specified in either input.

For Date values, precision must be one of: year, month, or day.
For DateTime values, precision must be one of: year, month, day, hour, minute, second, or millisecond.
For Time values, precision must be one of: hour, minute, second, or millisecond.

Note specifically that due to variability in the way week numbers are determined, comparisons involving weeks are not supported.

When this operator is called with both Date and DateTime inputs, the Date values will be implicitly converted to DateTime as defined by the ToDateTime operator.

As with all date/time calculations, comparisons are performed respecting the timezone offset.

If either or both arguments are null, the result is null.

Note that in timing phrases, the keyword on may be used as a synonym for same for this operator

*** SameOrAfter Interval Overload ***
same _precision_ or after(left Interval<T>, right Interval<T>) Boolean
same _precision_ or after(left T, right Interval<T>) Boolean
same _precision_ or after(left Interval<T>, right T) Boolean

The same-precision-or after operator for intervals returns true if the first interval starts on or after the
    second one ends, using the semantics described in the Start and End operators to determine interval
    boundaries, and for date/time values, performing the comparisons at the specified precision, as described
    in the Same or After (Date/Time) operator for date/time values.

If no precision is specified, comparisons are performed beginning with years (or hours for time values) and
    proceeding to the finest precision specified in either input.

For Date-based intervals, precision must be one of: year, month, or day.

For DateTime-based intervals, precision must be one of: year, month, day, hour, minute, second, or millisecond.

For Time-based intervals, precision must be one of: hour, minute, second, or millisecond.

Note specifically that due to variability in the way week numbers are determined, comparisons involving weeks
    are not supported.

When this operator is called with a mixture of Date-based and DateTime-based intervals, the Date values will
    be implicitly converted to DateTime values as defined by the ToDateTime operator.

When this operator is called with a mixture of point values and intervals, the point values are implicitly
    converted to an interval starting and ending on the given point value.

For comparisons involving date/time or time intervals with imprecision, note that the result of the comparison
    may be null, depending on whether the values involved are specified to the level of precision used for the comparison.

As with all date/time calculations, comparisons are performed respecting the timezone offset.

If either or both arguments are null, the result is null.

Note that in timing phrases, the keyword on may be used as a synonym for same for this operator.

*** OnOrAfter DateTime overload ***
on or after _precision_ (left Date, right Date) Boolean
on or after _precision_ (left DateTime, right DateTime) Boolean
on or after _precision_ (left Time, right Time) Boolean

The on or after operator for date/time values is a synonym for the same or after operator and is supported to enable
    natural phrasing. See the description of the Same Or After (Date/Time) operator.

Note that this operator can be invoked using either the on or after or the after or on syntax.

In timing phrases, the keyword same is a synonym for on.

*** OnOrAfter Interval overload ***
on or after precision (left Interval<T>, right Interval<T>) Boolean
on or after precision (left T, right Interval<T>) Boolean
on or after precision (left Interval<T>, right T) Boolean

The on or after operator for intervals returns true if the first interval starts on or after the second one ends.
    In other words, if the starting point of the first interval is greater than or equal to the ending point of the second interval.
For the point-interval overload, the operator returns true if the given point is greater than or equal to the end of the interval.
For the interval-point overload, the operator returns true if the given interval starts on or after the given point.
This operator uses the semantics described in the Start and End operators to determine interval boundaries.
If precision is specified and the point type is a date/time type,
    comparisons used in the operation are performed at the specified precision.
If either argument is null, the result is null.
Note that this operator can be invoked using either the on or after or the after or on syntax.
*/
object SameOrAfterEvaluator {
    fun onOrAfter(left: Any?, right: Any?, precision: String?, state: State?): Boolean? {
        // Interval, Interval
        if (left is Interval && right is Interval) {
            if (left.start is BaseTemporal) {
                return sameOrAfter(left.start, right.end, precision, state)
            }
            return GreaterOrEqualEvaluator.greaterOrEqual(left.start, right.end, state)
        } else if (left is Interval) {
            if (right is BaseTemporal) {
                return sameOrAfter(left.start, right, precision, state)
            }
            return GreaterOrEqualEvaluator.greaterOrEqual(left.start, right, state)
        } else if (right is Interval) {
            if (left is BaseTemporal) {
                return sameOrAfter(left, right.end, precision, state)
            }
            return GreaterOrEqualEvaluator.greaterOrEqual(left, right.end, state)
        }

        throw InvalidOperatorArgument(
            "OnOrAfter(Date, Date), OnOrAfter(DateTime, DateTime), OnOrAfter(Time, Time), OnOrAfter(Interval<T>, Interval<T>), OnOrAfter(T, Interval<T>) or OnOrAfter(Interval<T>, T)",
            "OnOrAfter(${left!!.javaClassName}, ${right!!.javaClassName})",
        )
    }

    @JvmStatic
    fun sameOrAfter(left: Any?, right: Any?, precision: String?, state: State?): Boolean? {
        var precision = precision
        if (left == null || right == null) {
            return null
        }

        // Interval OnOrAfter overload
        if (left is Interval || right is Interval) {
            return onOrAfter(left, right, precision, state)
        }

        if (precision == null) {
            precision =
                BaseTemporal.getHighestPrecision(left as BaseTemporal, right as BaseTemporal)
        }

        if (left is BaseTemporal && right is BaseTemporal) {
            val result = left.compareToPrecision(right, Precision.fromString(precision))
            return if (result == null) null else result == 0 || result > 0
        }

        throw InvalidOperatorArgument(
            "SameOrAfter(Date, Date), SameOrAfter(DateTime, DateTime), SameOrAfter(Time, Time), SameOrAfter(Interval<T>, Interval<T>), SameOrAfter(T, Interval<T>) or SameOrAfter(Interval<T>, T)",
            "SameOrAfter(${left.javaClassName}, ${right.javaClassName})",
        )
    }
}
