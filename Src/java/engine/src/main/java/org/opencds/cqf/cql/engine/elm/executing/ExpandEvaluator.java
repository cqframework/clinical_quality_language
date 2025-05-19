package org.opencds.cqf.cql.engine.elm.executing;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
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

*/

public class ExpandEvaluator {
    private static Object addPer(Object addTo, Quantity per) {
        if (addTo instanceof Integer) {
            return AddEvaluator.add(addTo, per.getValue().intValue());
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

    public static List<Interval> getExpandedInterval(Interval interval, Quantity per, State state) {
        if (!contributesToExpansion(interval)) {
            return emptyList();
        }

        Object start = interval.getStart();
        Object end = interval.getEnd();

        // Numeric types can only be scaled by numeric values
        // so we consider an expansion unit other than 1 to be invalid
        if (((start instanceof Integer) || (start instanceof BigDecimal))
                && !per.getUnit().equals("1")) {
            return emptyList();
        }

        var scale = per.getValue().scale();

        // BigDecimals must be truncated/extended to the precision of the per
        if (start instanceof BigDecimal) {
            start = ((BigDecimal) start).setScale(scale, RoundingMode.DOWN);
            end = ((BigDecimal) end).setScale(scale, RoundingMode.DOWN);
        }

        // If the start and end are integers and the per is a decimal, we need to convert
        // the start and end to BigDecimal so that we can set the scale
        if (start instanceof Integer && scale > 0) {
            start = new BigDecimal(((Integer) start).longValue()).setScale(scale, RoundingMode.DOWN);
            end = new BigDecimal(((Integer) end).longValue()).setScale(scale, RoundingMode.DOWN);
        }

        var i = SubtractEvaluator.subtract(end, start);

        var iterations = TruncatedDivideEvaluator.div(i, per.getValue(), null);

        if (iterations == null) {
            return emptyList();
        }

        var finalIterations = iterations instanceof Integer
                ? (Integer) iterations + 1
                : ((BigDecimal) iterations).setScale(0, RoundingMode.DOWN).intValue() + 1;

        List<Interval> expansions = new ArrayList<>();

        if (finalIterations == 1) {
            expansions.add(new Interval(start, true, end, true));
        }

        for (int j = 0; j < finalIterations; j++) {
            end = addPer(start, per);
            expansions.add(new Interval(start, true, end, false));
            start = end;
        }

        return expansions;
    }

    public static List<Interval> getExpandedInterval(Interval interval, Quantity per) {
        if (!contributesToExpansion(interval)) {
            return emptyList();
        }

        var precision = per.getUnit() == null ? "1" : per.getUnit();
        var i = DurationBetweenEvaluator.duration(
                interval.getStart(), interval.getEnd(), Precision.fromString(precision));

        var iterations = TruncatedDivideEvaluator.div(
                i, per.getValue().setScale(0, RoundingMode.DOWN).intValue(), null);

        if (iterations == null) {
            return emptyList();
        }

        var finalIterations = iterations instanceof Integer
                ? (Integer) iterations + 1
                : ((BigDecimal) iterations).setScale(0, RoundingMode.DOWN).intValue() + 1;

        var start = interval.getStart();
        var end = interval.getEnd();
        List<Interval> expansions = new ArrayList<>();

        if (finalIterations == 1) {
            if (((BaseTemporal) start).getPrecision() == Precision.fromString(precision)
                    && ((BaseTemporal) end).getPrecision() == Precision.fromString(precision)) {
                expansions.add(new Interval(start, true, end, true));
            }
        }

        for (int j = 0; j < finalIterations; j++) {
            end = addPer(start, per);
            expansions.add(new Interval(start, true, end, false));
            start = end;
        }

        return expansions;
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

    private static List<Object> expand(Interval interval, Quantity per, State state) {
        // The calculation is performed the same way, but the starting point of each resulting interval is returned,
        // rather than the interval
        var resultingIntervals = expand(Collections.singletonList(interval), per, state);
        return resultingIntervals.stream().map(Interval::getStart).collect(toList());
    }

    private static List<Interval> expand(Iterable<Interval> list, Quantity per, State state) {
        List<Interval> intervals = CqlList.toList(list, false);

        intervals = intervals.stream()
                .filter(ExpandEvaluator::contributesToExpansion)
                .collect(toList());

        if (intervals.isEmpty()) {
            return emptyList();
        }

        final var localPer = perOrDefault(per, intervals.get(0));

        // collapses overlapping intervals
        intervals = CollapseEvaluator.collapse(
                intervals, new Quantity().withValue(BigDecimal.ZERO).withUnit(localPer.getUnit()), state);

        intervals.sort(new CqlList().valueSort);

        return intervals.stream()
                .map(i -> isTemporal(i) ? getExpandedInterval(i, localPer) : getExpandedInterval(i, localPer, state))
                .flatMap(List::stream)
                .distinct()
                .collect(toList());
    }

    // Intervals with null endpoints are not expanded, null intervals are not expanded
    private static boolean contributesToExpansion(Interval interval) {
        if (interval == null || interval.getLow() == null || interval.getHigh() == null) {
            return false;
        }

        return true;
    }

    public static Object expand(Object listOrInterval, Quantity per, State state) {
        if (listOrInterval == null) {
            return emptyList();
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
