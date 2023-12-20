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
        }

        throw new InvalidOperatorArgument(
                "Expand(List<Interval<T>>, Quantity)",
                String.format(
                        "Expand(%s, %s)",
                        addTo.getClass().getName(), per.getClass().getName()));
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

    public static List<Interval> expand(Iterable<Interval> list, Quantity per, State state) {
        if (list == null) {
            return null;
        }

        List<Interval> intervals = CqlList.toList(list, false);

        if (intervals.isEmpty()) {
            return intervals;
        }

        // collapses overlapping intervals
        intervals = CollapseEvaluator.collapse(
                intervals,
                new Quantity().withValue(BigDecimal.ZERO).withUnit(per == null ? "1" : per.getUnit()),
                state);

        boolean isTemporal = intervals.get(0).getStart() instanceof BaseTemporal
                || intervals.get(0).getEnd() instanceof BaseTemporal;

        intervals.sort(new CqlList().valueSort);

        if (per == null) {
            if (isTemporal) {
                per = new Quantity()
                        .withValue(new BigDecimal("1.0"))
                        .withUnit(BaseTemporal.getLowestPrecision(
                                (BaseTemporal) intervals.get(0).getStart(),
                                (BaseTemporal) intervals.get(0).getEnd()));
            } else {
                per = new Quantity().withValue(new BigDecimal("1.0")).withDefaultUnit();
            }
        }

        String precision = per.getUnit().equals("1") ? null : per.getUnit();

        // prevent duplicates
        Set<Interval> set = new TreeSet<>();
        for (Interval interval : intervals) {
            if (interval == null) {
                continue;
            }

            List<Interval> temp = isTemporal
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
}
