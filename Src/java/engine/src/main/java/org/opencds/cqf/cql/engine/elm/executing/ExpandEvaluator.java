package org.opencds.cqf.cql.engine.elm.executing;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument;
import org.opencds.cqf.cql.engine.execution.State;
import org.opencds.cqf.cql.engine.runtime.*;

/*

expand(argument List<Interval<T>>, per Quantity) List<Interval<T>>
expand(argument Interval<T>, per Quantity) List<T>

The expand operator returns the set of intervals of width per for all the intervals in the input.

The per argument must be a quantity value that is compatible with the point type of the input intervals.
    For numeric intervals, this means a default unit ('1').
    For date/time intervals, this means a temporal duration.

Note that if the values in the intervals are more precise than the per quantity, the more precise values will be
    truncated to the precision specified by the per quantity.

If the list of intervals is empty, the result is empty. If the list of intervals contains nulls, they will be excluded
    from the resulting list.

If the list argument is null, the result is null.

If the per argument is null, the default unit interval for the point type of the intervals involved will be used
    (i.e. the interval that has a width equal to the result of the successor function for the point type).

The interval overload of the expand operator will return a list of the start values of the expanded intervals.
*/

public class ExpandEvaluator {
    private static Object addPer(Object addTo, Quantity per) {
        // Point types must stay the same, so for Integer and Long intervals, the per quantity is rounded up.
        if (addTo instanceof Integer) {
            return AddEvaluator.add(
                    addTo, per.getValue().setScale(0, RoundingMode.CEILING).intValue());
        } else if (addTo instanceof Long) {
            return AddEvaluator.add(
                    addTo, per.getValue().setScale(0, RoundingMode.CEILING).longValue());
        } else if (addTo instanceof BigDecimal) {
            return AddEvaluator.add(addTo, per.getValue());
        } else if (addTo instanceof Quantity) {
            return AddEvaluator.add(addTo, per);
        } else if (addTo instanceof BaseTemporal) {
            return AddEvaluator.add(addTo, per);
        }

        throw new InvalidOperatorArgument(
                "Expand(List<Interval<T>>, Quantity), Expand(Interval<T>, Quantity)",
                String.format(
                        "Expand(%s, %s)",
                        addTo.getClass().getName(), per.getClass().getName()));
    }

    /**
     * Performs interval expansion for the given interval and per quantity.
     *
     * @param interval the interval to expand
     * @param per the size of the intervals to return
     * @param state the engine state
     * @return the list of smaller intervals of size per
     */
    private static List<Interval> expandIntervalIntoIntervals(Interval interval, Quantity per, State state) {
        var start = interval.getStart();
        var nextStart = addPer(start, per);

        // per may be too small
        if (!Boolean.TRUE.equals(LessEvaluator.less(start, nextStart, state))) {
            return null;
        }

        var returnedIntervals = new ArrayList<Interval>();
        var endSuccessor = SuccessorEvaluator.successor(interval.getEnd(), per);
        while (true) {
            var lessOrEqual = LessOrEqualEvaluator.lessOrEqual(nextStart, endSuccessor, state);
            if (lessOrEqual == null) {
                return null;
            }
            if (lessOrEqual) {
                returnedIntervals.add(
                        new Interval(start, true, PredecessorEvaluator.predecessor(nextStart, per), true));
                start = nextStart;
                nextStart = addPer(start, per);
            } else {
                break;
            }
        }

        return returnedIntervals;
    }

    /**
     * Implements the interval overload of the expand operator. The calculation is performed the same
     * way as with the list overload, but the starting point of each resulting interval is returned,
     * rather than the interval.
     *
     * @param interval the interval to expand
     * @param per the distance between the points to return
     * @param state the engine state
     * @return the list of points from the interval
     */
    private static List<Object> expandIntervalIntoPoints(Interval interval, Quantity per, State state) {
        var returnedIntervals = expandIntervalsIntoIntervals(Collections.singletonList(interval), per, state);

        if (returnedIntervals == null) {
            return null;
        }

        return returnedIntervals.stream().map(Interval::getStart).collect(Collectors.toList());
    }

    /**
     * Prepares the intervals for expansion.
     *
     * @param intervals the list of intervals to prepare
     * @param per the per quantity for expansion
     * @param state the engine state
     * @return the prepared list of intervals
     */
    private static List<Interval> prepareIntervals(List<Interval> intervals, Quantity per, State state) {
        // Ignore intervals with null boundaries and truncate the boundaries.
        intervals = intervals.stream()
                .filter(interval -> interval.getLow() != null && interval.getHigh() != null)
                .map(interval -> IntervalHelper.truncateIntervalBoundaries(interval, per, state))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        // Collapse overlapping intervals to avoid returning duplicate intervals
        intervals = CollapseEvaluator.collapse(
                intervals, new Quantity().withValue(BigDecimal.ZERO).withUnit(per.getUnit()), state);

        if (intervals == null) {
            return null;
        }

        // Sort the intervals so that the expansion results are returned in order
        intervals.sort(new CqlList().valueSort);

        return intervals;
    }

    /**
     * Implements the list overload of the expand operator.
     *
     * @param list the list of intervals to expand
     * @param per the size of the intervals to return
     * @param state the engine state
     * @return the list of smaller intervals of size per
     */
    private static List<Interval> expandIntervalsIntoIntervals(Iterable<Interval> list, Quantity per, State state) {
        var intervals = CqlList.toList(list, false);

        if (intervals.isEmpty()) {
            return intervals;
        }

        // Infer the per quantity from the intervals if it is not provided
        var perOrDefault = per == null ? IntervalHelper.quantityFromCoarsestPrecisionOfBoundaries(intervals) : per;

        // Make sure the per quantity is compatible with the boundaries of the intervals
        if (!IntervalHelper.isQuantityCompatibleWithBoundaries(perOrDefault, intervals)) {
            return null;
        }

        intervals = prepareIntervals(intervals, perOrDefault, state);
        if (intervals == null) {
            return null;
        }

        return intervals.stream()
                .filter(Objects::nonNull)
                .flatMap(interval -> {
                    var returnedIntervals = expandIntervalIntoIntervals(interval, perOrDefault, state);
                    return returnedIntervals == null ? Stream.empty() : returnedIntervals.stream();
                })
                .collect(Collectors.toList());
    }

    public static Object expand(Object listOrInterval, Quantity per, State state) {
        if (listOrInterval == null) {
            return null;
        }

        if (listOrInterval instanceof Interval) {
            return expandIntervalIntoPoints((Interval) listOrInterval, per, state);
        } else if (listOrInterval instanceof Iterable) {
            @SuppressWarnings("unchecked")
            var list = (Iterable<Interval>) listOrInterval;
            return expandIntervalsIntoIntervals(list, per, state);
        }

        throw new InvalidOperatorArgument(
                "Expand(List<Interval<T>>, Quantity), Expand(Interval<T>, Quantity)",
                String.format(
                        "Expand(%s, %s)",
                        listOrInterval.getClass().getName(), per.getClass().getName()));
    }
}
