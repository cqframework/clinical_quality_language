package org.opencds.cqf.cql.engine.elm.executing;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
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

    // TODO: fill this out
    private static List<Object> getExpandedValues(Object start, Object end, Quantity per, State state) {
        List<Object> expansion = new ArrayList<>();
        Object current = start;
        while (LessOrEqualEvaluator.lessOrEqual(current, end, state)) {
            expansion.add(current);
            current = addPer(current, per);
        }
        return expansion;
    }

    public static List<Interval> getExpandedInterval(Interval interval, Quantity per, State state) {
        if (interval.getLow() == null || interval.getHigh() == null) {
            return null;
        }

        List<Interval> expansion = new ArrayList<>();
        Object start = interval.getStart();
        Object end = addPer(start, per);

        if ((start instanceof Integer || start instanceof BigDecimal)
                && !per.getUnit().equals("1")) {
            return null;
        }

        if (EqualEvaluator.equal(start, interval.getEnd(), state)) {
            expansion.add(new Interval(start, true, start, true));
            return expansion;
        }

        while (LessOrEqualEvaluator.lessOrEqual(PredecessorEvaluator.predecessor(end), interval.getEnd(), state)) {
            expansion.add(new Interval(start, true, end, false));
            start = end;
            end = addPer(start, per);
        }

        return expansion;
    }

    public static List<Interval> getExpandedInterval(Interval interval, Quantity per, String precision) {
        if (interval.getLow() == null || interval.getHigh() == null) {
            return null;
        }

        Object i;
        try {
            i = DurationBetweenEvaluator.duration(
                    interval.getStart(), interval.getEnd(), Precision.fromString(precision));
        } catch (Exception e) {
            return null;
        }
        if (i instanceof Integer) {
            List<Interval> expansion = new ArrayList<>();
            Interval unit = null;
            Object start = interval.getStart();
            Object end = AddEvaluator.add(start, per);
            for (int j = 0; j < (Integer) i; ++j) {
                unit = new Interval(start, true, end, false);
                expansion.add(unit);
                start = end;
                end = AddEvaluator.add(start, per);
            }

            if (unit != null) {
                i = DurationBetweenEvaluator.duration(
                        unit.getEnd(), interval.getEnd(), Precision.fromString(precision));
                if (i instanceof Integer && (Integer) i == 1) {
                    expansion.add(new Interval(start, true, end, false));
                }
            } else {
                // special case - although the width of Interval[@2018-01-01, @2018-01-01] is 0, expansion result is not
                // empty
                if (((BaseTemporal) start).getPrecision() == Precision.fromString(precision)
                        && ((BaseTemporal) end).getPrecision() == Precision.fromString(precision)) {
                    expansion.add(new Interval(start, true, end, false));
                }
            }

            return expansion;
        }

        // uncertainty
        return null;
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
        if (interval == null) {
            return null;
        }

        per = perOrDefault(per, interval);
        String precision = per.getUnit().equals("1") ? null : per.getUnit();
        List<Object> values = getExpandedValues(interval.getStart(), interval.getEnd(), per, state);
        if (values == null || values.isEmpty()) {
            return null;
        }

        return values;
    }

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
        String precision = per.getUnit().equals("1") ? null : per.getUnit();

        // prevent duplicates
        Set<Interval> set = new TreeSet<>();
        for (Interval interval : intervals) {
            if (interval == null) {
                continue;
            }

            List<Interval> temp = isTemporal(interval)
                    ? getExpandedInterval(interval, per, precision)
                    : getExpandedInterval(interval, per, state);
            if (temp == null) {
                return null;
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
