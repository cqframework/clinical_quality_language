package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument
import org.opencds.cqf.cql.engine.execution.State
import org.opencds.cqf.cql.engine.runtime.BaseTemporal
import org.opencds.cqf.cql.engine.runtime.Interval
import org.opencds.cqf.cql.engine.runtime.Precision
import org.opencds.cqf.cql.engine.util.javaClassName

/*

*Temporal Overload
same _precision_ as(left Date, right Date) Boolean
same _precision_ as(left DateTime, right DateTime) Boolean
same _precision_ as(left Time, right Time) Boolean

    The same-precision-as operator compares two date/time values to the specified precision for equality.
        The comparison is performed by considering each precision in order, beginning with years (or hours for time values).
        If the values are the same, comparison proceeds to the next precision;
        if the values are different, the comparison stops and the result is false;
        if either input has no value for the precision, the comparison stops and the result is null; if the specified precision has been reached,
            the comparison stops and the result is true.

    If no precision is specified, the comparison is performed beginning with years (or hours for time values)
        and proceeding to the finest precision specified in either input.

    For Date values, precision must be one of: year, month, or day.
    For DateTime values, precision must be one of: year, month, day, hour, minute, second, or millisecond.
    For Time values, precision must be one of: hour, minute, second, or millisecond.

    Note specifically that due to variability in the way week numbers are determined, comparisons involving weeks are not supported.

    When this operator is called with both Date and DateTime inputs, the Date values will be implicitly converted to DateTime as defined by the ToDateTime operator.

    As with all date/time calculations, comparisons are performed respecting the timezone offset.

    If either or both arguments are null, the result is null.

*Interval Overload
same _precision_ as(left Interval<T>, right Interval<T>) Boolean

    The same-precision-as operator for intervals returns true if the two intervals start and end at the same value,
        using the semantics described in the Start and End operators to determine interval boundaries, and for date/time value,
        performing the comparisons at the specified precision, as described in the Same As operator for date/time values.

    If no precision is specified, comparisons are performed beginning with years (or hours for time values) and proceeding
        to the finest precision specified in either input.

    For Date-based intervals, precision must be one of: year, month, or day.

    For DateTime-based intervals, precision must be one of: year, month, day, hour, minute, second, or millisecond.

    For Time-based intervals, precision must be one of: hour, minute, second, or millisecond.

    Note specifically that due to variability in the way week numbers are determined, comparisons involving weeks are not supported.

    When this operator is called with a mixture of Date- and DateTime-based intervals, the Date values will be implicitly
        converted to DateTime values as defined by the ToDateTime operator.

    For comparisons involving date/time or time values with imprecision, note that the result of the comparison may be null,
        depending on whether the values involved are specified to the level of precision used for the comparison.

    As with all date/time calculations, comparisons are performed respecting the timezone offset.

    If either or both arguments are null, the result is null.

*/
object SameAsEvaluator {
    @JvmStatic
    fun sameAs(left: Any?, right: Any?, precision: String?, state: State?): Boolean? {
        var precision = precision
        if (left == null || right == null) {
            return null
        }

        if (left is Interval && right is Interval) {
            val leftStart = left.start
            val leftEnd = left.end
            val rightStart = right.start
            val rightEnd = right.end

            if (
                leftStart is BaseTemporal &&
                    leftEnd is BaseTemporal &&
                    rightStart is BaseTemporal &&
                    rightEnd is BaseTemporal
            ) {
                var startPrecision: String? = null
                if (precision == null) {
                    startPrecision = BaseTemporal.getHighestPrecision(leftStart, rightStart)
                    precision = BaseTemporal.getHighestPrecision(leftEnd, rightEnd)
                }
                val startResult =
                    leftStart.compareToPrecision(
                        rightStart,
                        Precision.fromString(
                            if (startPrecision == null) precision else startPrecision
                        ),
                    )
                val endResult =
                    leftEnd.compareToPrecision(rightEnd, Precision.fromString(precision))
                if (startResult == null && endResult == null) {
                    return null
                } else if (startResult == null && endResult != 0) {
                    return false
                } else if (endResult == null && startResult != 0) {
                    return false
                }
                return if (startResult == null || endResult == null) null
                else startResult == 0 && endResult == 0
            } else {
                val startResult = EqualEvaluator.equal(leftStart, rightStart, state)
                val endResult = EqualEvaluator.equal(leftEnd, rightEnd, state)
                if (startResult == null && endResult == null) {
                    return null
                } else if (startResult == null && !endResult!!) {
                    return false
                } else if (endResult == null && !startResult!!) {
                    return false
                }
                return if (startResult == null || endResult == null) null
                else startResult && endResult
            }
        } else if (left is BaseTemporal && right is BaseTemporal) {
            if (precision == null) {
                precision = BaseTemporal.getHighestPrecision(left, right)
            }
            val result = left.compareToPrecision(right, Precision.fromString(precision))
            return if (result == null) null else result == 0
        }

        throw InvalidOperatorArgument(
            "SameAs(Date, Date), SameAs(DateTime, DateTime), SameAs(Time, Time) or SameAs(Interval<T>, Interval<T>)",
            "SameAs(${left.javaClassName}, ${right.javaClassName})",
        )
    }
}
