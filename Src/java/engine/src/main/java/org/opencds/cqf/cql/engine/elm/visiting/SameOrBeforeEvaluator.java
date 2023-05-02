package org.opencds.cqf.cql.engine.elm.visiting;

import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument;
import org.opencds.cqf.cql.engine.execution.State;
import org.opencds.cqf.cql.engine.runtime.BaseTemporal;
import org.opencds.cqf.cql.engine.runtime.Interval;
import org.opencds.cqf.cql.engine.runtime.Precision;

/*
*** SameOrBefore Temporal Overload ***
same _precision_ or before(left Date, right Date) Boolean
same _precision_ or before(left DateTime, right DateTime) Boolean
same _precision_ or before(left Time, right Time) Boolean

The same-precision-or before operator compares two date/time values to the specified precision to determine whether the
    first argument is the same or before the second argument. The comparison is performed by considering each precision
    in order, beginning with years (or hours for time values). If the values are the same, comparison proceeds to the
    next precision; if the first value is less than the second, the result is true; if the first value is greater than
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

Note that in timing phrases, the keyword on may be used as a synonym for same for this operator.

*** SameOrBefore Interval Overload ***
same _precision_ or before(left Interval<T>, right Interval<T>) Boolean
same _precision_ or before(left T, right Interval<T>) Boolean
same _precision_ or before(left Interval<T>, right T) Boolean

The same-precision-or before operator returns true if the first interval ends on or before the second one starts,
    using the semantics described in the Start and End operators to determine interval boundaries, and for date/time
    values, performing the comparisons at the specified precision, as described in the Same or Before (Date/Time)
    operator for date/time values.

If no precision is specified, comparisons are performed beginning with years (or hours for time values) and proceeding
    to the finest precision specified in either input.

For Date-based intervals, precision must be one of: year, month, or day.

For DateTime-based intervals, precision must be one of: year, month, day, hour, minute, second, or millisecond.

For Time-based intervals, precision must be one of: hour, minute, second, or millisecond.

Note specifically that due to variability in the way week numbers are determined, comparisons involving weeks are not supported.

When this operator is called with a mixture of Date- and DateTime-based intervals, the Date values will be implicitly
    converted to DateTime values as defined by the ToDateTime operator.

When this operator is called with a mixture of point values and intervals, the point values are implicitly converted
    to an interval starting and ending on the given point value.

For comparisons involving date/time or time values with imprecision, note that the result of the comparison may be null,
    depending on whether the values involved are specified to the level of precision used for the comparison.

As with all date/time calculations, comparisons are performed respecting the timezone offset.

If either or both arguments are null, the result is null.

Note that in timing phrases, the keyword on may be used as a synonym for same for this operator.

*** OnOrBefore DateTime overload ***
on or before _precision_ (left Date, right Date) Boolean
on or before _precision_ (left DateTime, right DateTime) Boolean
on or before _precision_ (left Time, right Time) Boolean

The on or before operator for date/time values is a synonym for the same or before operator and is supported to enable
    natural phrasing. See the description of the Same Or Before (Date/Time) operator.

Note that this operator can be invoked using either the on or before or the before or on syntax.

In timing phrases, the keyword same is a synonym for on.

*** OnOrBefore Interval overload ***
on or before precision (left Interval<T>, right Interval<T>) Boolean
on or before precision (left T, right Interval<T>) Boolean
on or before precision (left interval<T>, right T) Boolean

The on or before operator for intervals returns true if the first interval ends on or before the second one starts.
    In other words, if the ending point of the first interval is less than or equal to the starting point of the second interval.
For the point-interval overload, the operator returns true if the given point is less than or equal to the start of the interval.
For the interval-point overload, the operator returns true if the given interval ends on or before the given point.
This operator uses the semantics described in the Start and End operators to determine interval boundaries.
If precision is specified and the point type is a date/time type,
    comparisons used in the operation are performed at the specified precision.
If either argument is null, the result is null.
Note that this operator can be invoked using either the on or before or the before or on syntax.
*/

public class SameOrBeforeEvaluator {

    public static Boolean onOrBefore(Object left, Object right, String precision, State state) {
        // Interval, Interval
        if (left instanceof Interval && right instanceof Interval) {
            if (((Interval) left).getStart() instanceof BaseTemporal) {
                return sameOrBefore(((Interval) left).getEnd(), ((Interval) right).getStart(), precision, state);
            }
            return LessOrEqualEvaluator.lessOrEqual(((Interval) left).getEnd(), ((Interval) right).getStart(), state);
        }

        // Interval, Point
        else if (left instanceof Interval) {
            if (right instanceof BaseTemporal) {
                return sameOrBefore(((Interval) left).getEnd(), right, precision, state);
            }
            return LessOrEqualEvaluator.lessOrEqual(((Interval) left).getEnd(), right, state);
        }

        // Point, Interval
        else if (right instanceof Interval) {
            if (left instanceof BaseTemporal) {
                return sameOrBefore(left, ((Interval) right).getStart(), precision, state);
            }
            return LessOrEqualEvaluator.lessOrEqual(left, ((Interval) right).getStart(), state);
        }

        throw new InvalidOperatorArgument(
                "OnOrBefore(Date, Date), OnOrBefore(DateTime, DateTime), OnOrBefore(Time, Time), OnOrBefore(Interval<T>, Interval<T>), OnOrBefore(T, Interval<T>) or OnOrBefore(Interval<T>, T)",
                String.format("OnOrBefore(%s, %s)", left.getClass().getName(), right.getClass().getName())
        );
    }

    public static Boolean sameOrBefore(Object left, Object right, String precision, State state) {
        if (left == null || right == null) {
            return null;
        }

        // Interval OnOrBefore overload
        if (left instanceof Interval || right instanceof Interval) {
            return onOrBefore(left, right, precision, state);
        }

        if (precision == null) {
            precision = BaseTemporal.getHighestPrecision((BaseTemporal) left, (BaseTemporal) right);
        }

        if (left instanceof BaseTemporal && right instanceof BaseTemporal) {
            Integer result = ((BaseTemporal) left).compareToPrecision((BaseTemporal) right, Precision.fromString(precision));
            return result == null ? null : result == 0 || result < 0;
        }

        throw new InvalidOperatorArgument(
                "SameOrBefore(Date, Date), SameOrBefore(DateTime, DateTime), SameOrBefore(Time, Time), SameOrBefore(Interval<T>, Interval<T>), SameOrBefore(T, Interval<T>) or SameOrBefore(Interval<T>, T)",
                String.format("SameOrBefore(%s, %s)", left.getClass().getName(), right.getClass().getName())
        );
    }
}
