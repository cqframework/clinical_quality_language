package org.opencds.cqf.cql.engine.elm.executing;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument;
import org.opencds.cqf.cql.engine.exception.InvalidPrecision;
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
    private static List<Interval> expandClosedLongRange(long start, long end, long per) {
        List<Interval> expansions = new ArrayList<>();

        if (start == end) {
            expansions.add(new Interval(start, true, end, true));
            return expansions;
        }

        for (long i = start; i <= end; i += per) {
            var nextEnd = Math.min(i + per - 1, end);
            expansions.add(new Interval(i, true, nextEnd, true));
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

    private static List<Interval> expandClosedQuantityRange(Quantity start, Quantity end, Quantity per) {
        List<Interval> expansions = new ArrayList<>();

        // Unit conversion not supported
        if (!(start.getUnit().equals(end.getUnit()) && start.getUnit().equals(per.getUnit()))) {
            return emptyList();
        }

        if (start.getValue().compareTo(end.getValue()) == 0) {
            expansions.add(new Interval(start, true, end, true));
            return expansions;
        }

        for (BigDecimal i = start.getValue(); i.compareTo(end.getValue()) <= 0; i = i.add(per.getValue())) {
            expansions.add(new Interval(i, true, i, true));
        }

        return expansions;
    }

    private static List<Interval> expandClosedTemporalRange(BaseTemporal start, BaseTemporal end, Quantity per) {

        Precision precision = null;
        try {
            precision = Precision.fromString(per.getUnit());
        } catch (InvalidPrecision e) {
            // If the precision is not valid, we cannot expand the interval.
            return emptyList();
        }

        Object i = null;
        try {
            i = DurationBetweenEvaluator.duration(start, end, precision);
        } catch (Exception e) {
            // If the duration between the start and end is not valid
            // at a given precision we cannot expand the interval.
            return emptyList();
        }

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
            if (start.getPrecision() == precision && end.getPrecision() == precision) {
                expansions.add(new Interval(start, true, end, true));
            }
        }

        for (int j = 0; j < finalIterations; j++) {
            end = (BaseTemporal) AddEvaluator.add(start, per);
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

    private static List<Interval> expand(List<Interval> intervals, Quantity per, State state) {
        // Remove null or null-endpointed intervals
        intervals = intervals.stream()
                .filter(i -> i != null && i.getLow() != null && i.getHigh() != null)
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
        var start =
                interval.getLowClosed() ? (BigDecimal) interval.getLow() : predeccesor((BigDecimal) interval.getLow());
        var end =
                interval.getHighClosed() ? (BigDecimal) interval.getHigh() : successor((BigDecimal) interval.getHigh());

        return expandClosedDecimalRange(start, end, per.getValue());
    }

    private static BigDecimal successor(BigDecimal decimal) {
        var scale = Math.max(0, decimal.scale());
        return decimal.add(BigDecimal.ONE.divide(BigDecimal.TEN.pow(scale)));
    }

    private static BigDecimal predeccesor(BigDecimal decimal) {
        var scale = Math.max(0, decimal.scale());
        return decimal.subtract(BigDecimal.ONE.divide(BigDecimal.TEN.pow(scale)));
    }

    private static List<Interval> expandNumericInterval(Interval interval, Quantity per) {
        // If the per is not a whole-number value, convert the ints to decimal and
        // expand that range.
        var start = interval.getStart();
        var end = interval.getEnd();
        var numericStart = start instanceof Integer ? ((Integer) start).longValue() : ((Long) start);
        var numericEnd = end instanceof Integer ? ((Integer) end).longValue() : ((Long) end);
        if (per.getValue().scale() > 0) {
            return expandClosedDecimalRange(new BigDecimal(numericStart), new BigDecimal(numericEnd), per.getValue());
        } else {
            return expandClosedLongRange(
                    numericStart, numericEnd, per.getValue().longValue());
        }
    }

    private static List<Interval> expandInterval(Interval interval, Quantity per, State state) {
        if ((interval.getStart() instanceof Integer && interval.getEnd() instanceof Integer)
                || (interval.getStart() instanceof Long && interval.getEnd() instanceof Long)) {
            return expandNumericInterval(interval, per);
        } else if (interval.getStart() instanceof BigDecimal && interval.getEnd() instanceof BigDecimal) {
            return expandDecimalInterval(interval, per);
        } else if (interval.getStart() instanceof BaseTemporal && interval.getEnd() instanceof BaseTemporal) {
            return expandClosedTemporalRange((BaseTemporal) interval.getStart(), (BaseTemporal) interval.getEnd(), per);
        } else if (interval.getStart() instanceof Quantity && interval.getEnd() instanceof Quantity) {
            return expandClosedQuantityRange((Quantity) interval.getStart(), (Quantity) interval.getEnd(), per);
        }

        throw new InvalidOperatorArgument(
                "Expand(List<Interval<T>>, Quantity), Expand(Interval<T>, Quantity)",
                String.format(
                        "Expand(%s, %s)",
                        interval.getClass().getName(), per.getClass().getName()));
    }

    public static Object expand(Object listOrInterval, Quantity per, State state) {
        if (listOrInterval == null) {
            return emptyList();
        }

        if (listOrInterval instanceof Interval) {
            return expand((Interval) listOrInterval, per, state);
        } else if (listOrInterval instanceof List) {
            @SuppressWarnings("unchecked")
            var list = (List<Interval>) listOrInterval;
            return expand(list, per, state);
        }

        throw new InvalidOperatorArgument(
                "Expand(List<Interval<T>>, Quantity), Expand(Interval<T>, Quantity)",
                String.format(
                        "Expand(%s, %s)",
                        listOrInterval.getClass().getName(), per.getClass().getName()));
    }
}
