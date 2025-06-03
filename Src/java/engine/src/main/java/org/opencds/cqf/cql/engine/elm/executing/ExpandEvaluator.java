package org.opencds.cqf.cql.engine.elm.executing;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
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
            return AddEvaluator.add((BaseTemporal) addTo, per);
        }

        throw new InvalidOperatorArgument(
                "Expand(List<Interval<T>>, Quantity), Expand(Interval<T>, Quantity)",
                String.format(
                        "Expand(%s, %s)",
                        addTo.getClass().getName(), per.getClass().getName()));
    }

    /**
     * Truncates the decimal value to the specified scale if the value has a greater scale.
     *
     * @param value the value to truncate
     * @param scale the scale to truncate to
     * @param roundToCeiling whether to round towards the ceiling or floor value
     * @return the truncated value
     */
    private static BigDecimal truncateDecimalIfNecessary(BigDecimal value, int scale, boolean roundToCeiling) {
        if (scale < value.scale()) {
            return value.setScale(scale, roundToCeiling ? RoundingMode.CEILING : RoundingMode.FLOOR);
        }
        return value;
    }

    /**
     * Truncates the temporal value to the specified precision if the value has a greater precision.
     *
     * @param value the value to truncate
     * @param precision the precision to truncate to
     * @param roundToCeiling whether to round towards the ceiling or floor value
     * @return the truncated value
     */
    private static BaseTemporal truncateTemporalIfNecessary(
            BaseTemporal value, Precision precision, boolean roundToCeiling) {
        if (value.getPrecision() == precision || value.isUncertain(precision)) {
            return value;
        }

        var roundedToFloor = value.copy().setPrecision(precision);
        if (roundToCeiling) {
            return (BaseTemporal)
                    roundedToFloor.getUncertaintyInterval(precision).getEnd();
        }

        return roundedToFloor;
    }

    /**
     * Handles the case of the interval boundaries being more precise than the per quantity. When the boundaries
     * are truncated, the truncated start is rounded to the ceiling value and the truncated end is rounded to
     * the floor value so that the truncated interval is fully contained within the original interval. If the
     * truncated start becomes greater than the truncated end (e.g. for interval = Interval[0.3, 0.5] and per = 1),
     * this method returns null.
     *
     * @param interval the interval with the boundaries to truncate
     * @param per the quantity specifying the precision to truncate to
     * @param state the engine state
     * @return the interval with the truncated boundaries
     */
    private static Interval truncateIntervalBoundariesIfNecessary(Interval interval, Quantity per, State state) {
        var start = interval.getStart();
        var end = interval.getEnd();

        if (start instanceof BigDecimal) {
            var perScale = per.getValue().scale();
            var truncatedStart = truncateDecimalIfNecessary((BigDecimal) start, perScale, true);
            var truncatedEnd = truncateDecimalIfNecessary((BigDecimal) end, perScale, false);

            if (truncatedStart.compareTo(truncatedEnd) <= 0) {
                return new Interval(truncatedStart, true, truncatedEnd, true);
            }

            return null;
        } else if (start instanceof BaseTemporal) {
            var precision = Precision.fromString(per.getUnit());
            var truncatedStart = truncateTemporalIfNecessary((BaseTemporal) start, precision, true);
            var truncatedEnd = truncateTemporalIfNecessary((BaseTemporal) end, precision, false);

            if (LessOrEqualEvaluator.lessOrEqual(truncatedStart, truncatedEnd, state)) {
                return new Interval(truncatedStart, true, truncatedEnd, true);
            }

            return null;
        }

        return interval;
    }

    /**
     * Performs interval expansion for the given interval and per quantity.
     *
     * @param interval the interval to expand
     * @param per the size of the intervals to return
     * @param state the engine state
     * @return the list of smaller intervals of size per
     */
    public static List<Interval> getExpandedInterval(Interval interval, Quantity per, State state) {
        if (interval.getLow() == null || interval.getHigh() == null) {
            return null;
        }

        Object intervalStart = interval.getStart();
        Object intervalEnd = interval.getEnd();

        // Make sure that the interval point type is compatible with the per quantity
        if ((intervalStart instanceof Integer || intervalStart instanceof Long || intervalStart instanceof BigDecimal)
                != per.getUnit().equals("1")) {
            return null;
        }

        // If the interval boundaries are more precise than the per quantity, the more precise values are truncated to
        // the precision specified by the per quantity.
        Interval truncatedInterval = truncateIntervalBoundariesIfNecessary(interval, per, state);
        if (truncatedInterval == null) {
            return null;
        }

        Object start = truncatedInterval.getStart();
        Object nextStart = addPer(start, per);

        // per may be too small to keep adding it to the start value
        if (!LessEvaluator.less(start, nextStart, state)) {
            return null;
        }

        List<Interval> expansion = new ArrayList<>();
        Object endSuccessor = SuccessorEvaluator.successor(truncatedInterval.getEnd(), per);
        while (LessOrEqualEvaluator.lessOrEqual(nextStart, endSuccessor, state)) {
            expansion.add(new Interval(start, true, PredecessorEvaluator.predecessor(nextStart, per), true));
            start = nextStart;
            nextStart = addPer(start, per);
        }

        return expansion;
    }

    private static boolean isTemporal(Interval interval) {
        return interval.getStart() instanceof BaseTemporal || interval.getEnd() instanceof BaseTemporal;
    }

    private static Quantity perOrDefault(Quantity per, Interval interval) {
        if (per == null) {
            if (isTemporal(interval)) {
                return new Quantity()
                        .withValue(new BigDecimal("1.0"))
                        .withUnit(BaseTemporal.getLowestPrecision(
                                (BaseTemporal) interval.getStart(), (BaseTemporal) interval.getEnd()));
            } else {
                return new Quantity().withValue(new BigDecimal("1.0")).withDefaultUnit();
            }
        }
        return per;
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
    private static List<Object> expand(Interval interval, Quantity per, State state) {
        var resultingIntervals = expand(Collections.singletonList(interval), per, state);

        return resultingIntervals.stream().map(Interval::getStart).collect(Collectors.toList());
    }

    /**
     * Implements the list overload of the expand operator.
     *
     * @param list the list of intervals to expand
     * @param per the size of the intervals to return
     * @param state the engine state
     * @return the list of smaller intervals of size per
     */
    private static List<Interval> expand(Iterable<Interval> list, Quantity per, State state) {
        List<Interval> intervals = CqlList.toList(list, false);

        if (intervals.isEmpty()) {
            return intervals;
        }

        // collapses overlapping intervals
        intervals = CollapseEvaluator.collapse(
                intervals,
                new Quantity().withValue(BigDecimal.ZERO).withUnit(per == null ? "1" : per.getUnit()),
                state);

        intervals.sort(new CqlList().valueSort);
        per = perOrDefault(per, intervals.get(0));

        // prevent duplicates
        Set<Interval> set = new TreeSet<>();
        for (Interval interval : intervals) {
            if (interval == null) {
                continue;
            }

            List<Interval> temp = getExpandedInterval(interval, per, state);
            if (temp == null) {
                continue;
            }

            if (!temp.isEmpty()) {
                set.addAll(temp);
            }
        }

        return set.isEmpty() ? new ArrayList<>() : new ArrayList<>(set);
    }

    public static Object expand(Object listOrInterval, Quantity per, State state) {
        if (listOrInterval == null) {
            return null;
        }

        if (listOrInterval instanceof Interval) {
            return expand((Interval) listOrInterval, per, state);
        } else if (listOrInterval instanceof Iterable) {
            @SuppressWarnings("unchecked")
            var list = (Iterable<Interval>) listOrInterval;
            return expand(list, per, state);
        }

        throw new InvalidOperatorArgument(
                "Expand(List<Interval<T>>, Quantity), Expand(Interval<T>, Quantity)",
                String.format(
                        "Expand(%s, %s)",
                        listOrInterval.getClass().getName(), per.getClass().getName()));
    }
}
