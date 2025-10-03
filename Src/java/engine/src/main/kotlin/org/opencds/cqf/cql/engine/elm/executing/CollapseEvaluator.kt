package org.opencds.cqf.cql.engine.elm.executing

import java.math.BigDecimal
import org.opencds.cqf.cql.engine.execution.State
import org.opencds.cqf.cql.engine.runtime.BaseTemporal
import org.opencds.cqf.cql.engine.runtime.CqlList
import org.opencds.cqf.cql.engine.runtime.Interval
import org.opencds.cqf.cql.engine.runtime.Quantity

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
object CollapseEvaluator {
    private fun getIntervalWithPerApplied(
        interval: Interval,
        per: Quantity,
        state: State?,
    ): Interval {
        if (per.value == BigDecimal("0")) {
            return interval
        }

        if (interval.pointType!!.getTypeName().contains("Integer")) {
            return Interval(
                interval.start,
                true,
                AddEvaluator.add(interval.end, per.value!!.toInt()),
                true,
            )
        } else if (interval.pointType!!.getTypeName().contains("BigDecimal")) {
            return Interval(interval.start, true, AddEvaluator.add(interval.end, per.value), true)
        } else {
            return Interval(interval.start, true, AddEvaluator.add(interval.end, per), true)
        }
    }

    fun collapse(list: Iterable<Interval?>?, per: Quantity?, state: State?): List<Interval?>? {
        var per = per
        if (list == null) {
            return null
        }

        var intervals = CqlList.toList(list, false)

        if (intervals.size == 1 || intervals.isEmpty()) {
            return intervals
        }

        val isTemporal =
            intervals.get(0)!!.start is BaseTemporal || intervals.get(0)!!.end is BaseTemporal

        intervals.sortWith(CqlList().valueSort)

        if (per == null) {
            per = Quantity().withValue(BigDecimal(0)).withDefaultUnit()
        }

        var precision = if (per.unit == "1") null else per.unit

        var i = 0
        while (i < intervals.size - 1) {
            var applyPer = getIntervalWithPerApplied(intervals.get(i)!!, per, state)

            if (isTemporal) {
                if (
                    per.value!!.compareTo(BigDecimal.ONE) == 0 ||
                        per.value!!.compareTo(BigDecimal.ZERO) == 0
                ) {
                    // Temporal DataTypes already receive the precision adjustments at the
                    // OverlapsEvaluator and
                    // MeetsEvaluator.
                    // But they can only do full units (ms, seconds, days): They cannot do "4 days"
                    // of precision.
                    // The getIntervalWithPerApplied takes that into account.
                    applyPer = intervals.get(i)!!
                } else {
                    precision = "millisecond"
                }
            }

            val doMerge =
                AnyTrueEvaluator.anyTrue(
                    listOf<Boolean?>(
                        OverlapsEvaluator.overlaps(
                            applyPer,
                            intervals.get(i + 1),
                            precision,
                            state,
                        ),
                        MeetsEvaluator.meets(applyPer, intervals.get(i + 1), precision, state),
                    )
                )

            if (doMerge == null) {
                ++i
                continue
            }

            if (doMerge) {
                val isNextEndGreater =
                    if (isTemporal)
                        AfterEvaluator.after(
                            (intervals.get(i + 1))!!.end,
                            applyPer.end,
                            precision,
                            state,
                        )
                    else GreaterEvaluator.greater((intervals.get(i + 1))!!.end, applyPer.end, state)

                intervals.set(
                    i,
                    Interval(
                        applyPer.start,
                        true,
                        if (isNextEndGreater != null && isNextEndGreater)
                            (intervals.get(i + 1))!!.end
                        else applyPer.end,
                        true,
                    ),
                )
                intervals.removeAt(i + 1)
                i -= 1
            }
            ++i
        }

        return intervals
    }
}
