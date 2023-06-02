package org.opencds.cqf.cql.engine.elm.execution;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import org.opencds.cqf.cql.engine.execution.Context;
import org.opencds.cqf.cql.engine.runtime.BaseTemporal;
import org.opencds.cqf.cql.engine.runtime.CqlList;
import org.opencds.cqf.cql.engine.runtime.Interval;
import org.opencds.cqf.cql.engine.runtime.Quantity;

/*
collapse(argument List<Interval<T>>) List<Interval<T>>
collapse(argument List<Interval<T>>, per Quantity) List<Interval<T>>

The collapse operator returns the unique set of intervals that completely covers the ranges present in the given list of intervals.
    In other words, adjacent intervals within a sorted list are merged if they either overlap or meet.

Note that because the semantics for overlaps and meets are themselves defined in terms of the interval successor and predecessor operators,
    sets of date/time-based intervals that are only defined to a particular precision will calculate meets and overlaps at that precision.
    For example, a list of DateTime-based intervals where the boundaries are all specified to the hour will collapse at the hour precision,
        unless the collapse precision is overridden with the per argument.

The per argument determines the precision at which the collapse will be performed, and must be a quantity value that is compatible with the
    point type of the input intervals. For numeric intervals, this means a default unit ('1'). For date/time intervals, this means a temporal duration.

If the list of intervals is empty, the result is empty. If the list of intervals contains a single interval, the result is a list with that interval.
    If the list of intervals contains nulls, they will be excluded from the resulting list.

If the list argument is null, the result is null.

If the per argument is null, the default unit interval for the point type of the intervals involved will be used
    (i.e. the interval that has a width equal to the result of the successor function for the point type).
*/

public class CollapseEvaluator extends org.cqframework.cql.elm.execution.Collapse
{
    private static Interval getIntervalWithPerApplied(Interval interval, Quantity per, Context context)
    {
        if (per.getValue().equals(new BigDecimal("0")))
        {
            return interval;
        }

        if (interval.getPointType().getTypeName().contains("Integer"))
        {
            return new Interval(
                    interval.getStart(),
                    true,
                    AddEvaluator.add(interval.getEnd(), per.getValue().intValue()),
                    true
            );
        }
        else if (interval.getPointType().getTypeName().contains("BigDecimal"))
        {
            return new Interval(
                    interval.getStart(),
                    true,
                    AddEvaluator.add(interval.getEnd(), per.getValue()),
                    true
            );
        }
        // Quantity, Date, DateTime, and Time
        else
        {
            return new Interval(
                    interval.getStart(),
                    true,
                    AddEvaluator.add(interval.getEnd(), per),
                    true
            );
        }
    }

    public static List<Interval> collapse(Iterable<Interval> list, Quantity per, Context context)
    {
        if (list == null)
        {
            return null;
        }

        List<Interval> intervals = CqlList.toList(list, false);

        if (intervals.size() == 1 || intervals.isEmpty())
        {
            return intervals;
        }

        boolean isTemporal =
                intervals.get(0).getStart() instanceof BaseTemporal
                        || intervals.get(0).getEnd() instanceof BaseTemporal;

        intervals.sort(new CqlList().valueSort);

        if (per == null)
        {
            per = new Quantity().withValue(new BigDecimal(0)).withDefaultUnit();
        }

        String precision = per.getUnit().equals("1") ? null : per.getUnit();

        for (int i = 0; i < intervals.size() - 1; ++i)
        {
            Interval applyPer = getIntervalWithPerApplied(intervals.get(i), per, context);

            if (isTemporal) {
                if (per.getValue().compareTo(BigDecimal.ONE) == 0 || per.getValue().compareTo(BigDecimal.ZERO) == 0) {
                    // Temporal DataTypes already receive the precision adjustments at the OverlapsEvaluator and MeetsEvaluator.
                    // But they can only do full units (ms, seconds, days): They cannot do "4 days" of precision.
                    // The getIntervalWithPerApplied takes that into account.
                    applyPer = intervals.get(i);
                } else {
                    precision = "millisecond";
                }
            }

            Boolean doMerge = AnyTrueEvaluator.anyTrue(
                    Arrays.asList(
                            OverlapsEvaluator.overlaps(applyPer, intervals.get(i+1), precision, context),
                            MeetsEvaluator.meets(applyPer, intervals.get(i+1), precision, context)
                    )
            );

            if (doMerge == null)
            {
                continue;
            }

            if (doMerge)
            {
                Boolean isNextEndGreater =
                        isTemporal
                                ? AfterEvaluator.after((intervals.get(i+1)).getEnd(), applyPer.getEnd(), precision, context)
                                : GreaterEvaluator.greater((intervals.get(i+1)).getEnd(), applyPer.getEnd(), context);

                intervals.set(
                        i,
                        new Interval(
                                applyPer.getStart(), true,
                                isNextEndGreater != null && isNextEndGreater ? (intervals.get(i+1)).getEnd() : applyPer.getEnd(), true
                        )
                );
                intervals.remove(i+1);
                i -= 1;
            }
        }

        return intervals;
    }

    @Override
    protected Object internalEvaluate(Context context)
    {
        @SuppressWarnings("unchecked")
        Iterable<Interval> list = (Iterable<Interval>) getOperand().get(0).evaluate(context);
        Quantity per = (Quantity) getOperand().get(1).evaluate(context);

        return collapse(list, per, context);
    }
}