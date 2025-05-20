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
    private static List<Interval> expandClosedIntRange(int start, int end, int per) {
        List<Interval> expansions = new ArrayList<>();

        if (start == end) {
            expansions.add(new Interval(start, true, end, true));
            return expansions;
        }

        for (int i = start; i <= end; i += per) {
            int nextEnd = Math.min(i + per - 1, end);
            expansions.add(new Interval(i, true, nextEnd, false));
        }

        return expansions;
    }

    private static List<Interval> expandClosedDecimalRange(BigDecimal start, BigDecimal end, BigDecimal per) {
        List<Interval> expansions = new ArrayList<>();

        var scale = Math.max(0, per.scale());
        start = start.setScale(scale, RoundingMode.DOWN);
        // If the per is more precise than the end, we'll use the
        // successor of the end to ensure we don't miss
        // the last interval.
        boolean isPerMorePrecise = per.scale() > end.scale();
        if (isPerMorePrecise) {
            end = successor(end);
        }

        end = end.setScale(scale, RoundingMode.DOWN);
        if (start.compareTo(end) == 0) {
            expansions.add(new Interval(start, true, end, true));
            return expansions;
        }

        for (BigDecimal i = start; isPerMorePrecise ? i.compareTo(end) < 0 : i.compareTo(end) <= 0; i = i.add(per)) {
            expansions.add(new Interval(i, true, i, true));
        }

        return expansions;
    }

    private static List<Interval> expandClosedTemporalRange(BaseTemporal start, BaseTemporal end, Quantity per) {

        var precision = per.getUnit() == null ? "1" : per.getUnit();
        var i = DurationBetweenEvaluator.duration(start, end, Precision.fromString(precision));

        var iterations = TruncatedDivideEvaluator.div(
                i, per.getValue().setScale(0, RoundingMode.DOWN).intValue(), null);

        if (iterations == null) {
            return emptyList();
        }

        var finalIterations = iterations instanceof Integer
                ? (Integer) iterations + 1
                : ((BigDecimal) iterations).setScale(0, RoundingMode.DOWN).intValue() + 1;

        List<Interval> expansions = new ArrayList<>();

        if (finalIterations == 1) {
            if (start.getPrecision() == Precision.fromString(precision)
                    && end.getPrecision() == Precision.fromString(precision)) {
                expansions.add(new Interval(start, true, end, true));
            }
        }

        for (int j = 0; j < finalIterations; j++) {
            end = (BaseTemporal) addPer(start, per);
            expansions.add(new Interval(start, true, end, false));
            start = end;
        }

        return expansions;
    }

    private static Quantity perOrDefault(Quantity per, Interval interval) {
        if (per == null) {
            if (interval.getStart() instanceof BaseTemporal || interval.getEnd() instanceof BaseTemporal) {
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

    private static List<Interval> expand(Iterable<Interval> list, Quantity per, State state) {
        List<Interval> intervals = CqlList.toList(list, false);

        // Remove null or null-endpointed intervals
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

        // At this point, we have a list of intervals that are all the same type, no
        // nulls, no overlaps, sorted by start, and no null endpoints.
        return intervals.stream()
                .map(i -> expandInterval(i, localPer, state))
                .flatMap(List::stream)
                .distinct()
                .collect(toList());
    }

    private static List<Object> expand(Interval interval, Quantity per, State state) {
        // The calculation is performed the same way, but the starting point of each
        // resulting interval is returned,
        // rather than the interval
        var resultingIntervals = expand(Collections.singletonList(interval), per, state);
        return resultingIntervals.stream().map(Interval::getStart).collect(toList());
    }

    private static List<Interval> expandDecimalInterval(Interval interval, Quantity per) {
        Objects.requireNonNull(interval, "start cannot be null");
        Objects.requireNonNull(interval.getStart(), "start cannont be null");
        Objects.requireNonNull(interval.getEnd(), "end cannot be null");
        Objects.requireNonNull(per, "per can not be null");
        if (!(interval.getStart() instanceof BigDecimal) || !(interval.getEnd() instanceof BigDecimal)) {
            throw new IllegalArgumentException("non-integer interval");
        }

        var start =
                interval.getLowClosed() ? (BigDecimal) interval.getLow() : predeccesor((BigDecimal) interval.getLow());
        var end =
                interval.getHighClosed() ? (BigDecimal) interval.getHigh() : successor((BigDecimal) interval.getHigh());

        return expandClosedDecimalRange(start, end, per.getValue());
    }

    private static BigDecimal successor(BigDecimal decimal) {
        var value = Math.max(0, decimal.scale());
        return decimal.add(new BigDecimal("1").divide(new BigDecimal(10).pow(value)));
    }

    private static BigDecimal predeccesor(BigDecimal decimal) {
        var value = Math.max(0, decimal.scale());
        return decimal.add(new BigDecimal("1").divide(new BigDecimal(10).pow(value)));
    }

    private static List<Interval> expandIntegerInterval(Interval interval, Quantity per) {
        Objects.requireNonNull(interval, "start cannot be null");
        Objects.requireNonNull(interval.getStart(), "start cannont be null");
        Objects.requireNonNull(interval.getEnd(), "end cannot be null");
        Objects.requireNonNull(per, "per can not be null");
        if (!(interval.getStart() instanceof Integer) || !(interval.getEnd() instanceof Integer)) {
            throw new IllegalArgumentException("non-integer interval");
        }

        // If the per is not a whole-number value, convert the ints to decimal and
        // expand that range.
        if (per.getValue().scale() > 0) {
            return expandClosedDecimalRange(
                    new BigDecimal(((Integer) interval.getStart()).longValue()),
                    new BigDecimal(((Integer) interval.getEnd()).longValue()),
                    per.getValue());
        } else {
            return expandClosedIntRange(
                    ((Integer) interval.getStart()).intValue(),
                    ((Integer) interval.getEnd()).intValue(),
                    per.getValue().intValue());
        }
    }

    private static List<Interval> expandInterval(Interval interval, Quantity per, State state) {
        Objects.requireNonNull(interval, "start cannot be null");
        Objects.requireNonNull(interval.getStart(), "start cannont be null");
        Objects.requireNonNull(interval.getEnd(), "end cannot be null");
        Objects.requireNonNull(per, "per can not be null");

        if (interval.getStart() instanceof Integer && interval.getEnd() instanceof Integer) {
            return expandIntegerInterval(interval, per);
        } else if (interval.getStart() instanceof BigDecimal && interval.getEnd() instanceof BigDecimal) {
            return expandDecimalInterval(interval, per);
        } else if (interval.getStart() instanceof BaseTemporal && interval.getEnd() instanceof BaseTemporal) {
            return expandClosedTemporalRange((BaseTemporal) interval.getStart(), (BaseTemporal) interval.getEnd(), per);
        }

        throw new InvalidOperatorArgument(
                "Expand(List<Interval<T>>, Quantity), Expand(Interval<T>, Quantity)",
                String.format(
                        "Expand(%s, %s)",
                        interval.getClass().getName(), per.getClass().getName()));
    }

    // Intervals with null endpoints are not expanded, null intervals are not
    // expanded
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
}
