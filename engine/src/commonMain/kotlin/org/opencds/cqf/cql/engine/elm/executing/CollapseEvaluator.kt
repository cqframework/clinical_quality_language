package org.opencds.cqf.cql.engine.elm.executing

import org.cqframework.cql.shared.BigDecimal
import org.cqframework.cql.shared.ONE
import org.cqframework.cql.shared.ZERO
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument
import org.opencds.cqf.cql.engine.execution.State
import org.opencds.cqf.cql.engine.runtime.BaseTemporal
import org.opencds.cqf.cql.engine.runtime.Interval
import org.opencds.cqf.cql.engine.runtime.List
import org.opencds.cqf.cql.engine.runtime.Quantity
import org.opencds.cqf.cql.engine.runtime.SortHelper
import org.opencds.cqf.cql.engine.runtime.Value
import org.opencds.cqf.cql.engine.runtime.decimalTypeName
import org.opencds.cqf.cql.engine.runtime.integerTypeName
import org.opencds.cqf.cql.engine.runtime.toCqlDecimal
import org.opencds.cqf.cql.engine.runtime.toCqlInteger
import org.opencds.cqf.cql.engine.runtime.toCqlList

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
        } else if (interval.pointType == integerTypeName) {
            return Interval(
                interval.start,
                true,
                AddEvaluator.add(interval.end, per.value!!.toInt().toCqlInteger(), state),
                true,
                state,
            )
        } else if (interval.pointType == decimalTypeName) {
            return Interval(
                interval.start,
                true,
                AddEvaluator.add(interval.end, per.value?.toCqlDecimal(), state),
                true,
                state,
            )
        } else {
            return Interval(
                interval.start,
                true,
                AddEvaluator.add(interval.end, per, state),
                true,
                state,
            )
        }
    }

    fun collapse(list: Value?, per: Value?, state: State?): List? {
        if (list == null) {
            return null
        }

        if (!(list is List && list.all { it is Interval? } && per is Quantity?)) {
            throw InvalidOperatorArgument(
                "Collapse(List<Interval<T>>, Quantity)",
                "Collapse(${list.typeAsString}, ${per?.typeAsString})",
            )
        }

        val intervals = list.filterIsInstance<Interval>().toMutableList()

        return collapse(intervals, per, state).toCqlList()
    }

    fun collapse(
        intervals: kotlin.collections.List<Interval>,
        per: Quantity?,
        state: State?,
    ): kotlin.collections.List<Interval> {
        val intervals = intervals.toMutableList()

        if (intervals.size == 1 || intervals.isEmpty()) {
            return intervals
        }
        val first = intervals[0]
        val isTemporal = first.start is BaseTemporal || first.end is BaseTemporal

        intervals.sortWith { left, right -> SortHelper.compare(left, right, state) }
        val effectivePer = per ?: Quantity().withValue(BigDecimal(0)).withDefaultUnit()
        var precision = if (effectivePer.unit == "1") null else effectivePer.unit

        var i = 0
        while (i < intervals.size - 1) {
            var applyPer = getIntervalWithPerApplied(intervals[i], effectivePer, state)

            if (isTemporal) {
                if (
                    effectivePer.value!!.compareTo(ONE) == 0 ||
                        effectivePer.value!!.compareTo(ZERO) == 0
                ) {
                    // Temporal DataTypes already receive the precision adjustments at the
                    // OverlapsEvaluator and
                    // MeetsEvaluator.
                    // But they can only do full units (ms, seconds, days): They cannot do "4 days"
                    // of precision.
                    // The getIntervalWithPerApplied takes that into account.
                    applyPer = intervals[i]
                } else {
                    precision = "millisecond"
                }
            }

            val doMerge =
                AnyTrueEvaluator.anyTrue(
                        listOf(
                                OverlapsEvaluator.overlaps(
                                    applyPer,
                                    intervals[i + 1],
                                    precision,
                                    state,
                                ),
                                MeetsEvaluator.meets(applyPer, intervals[i + 1], precision, state),
                            )
                            .toCqlList()
                    )
                    .value

            if (doMerge) {
                val isNextEndGreater =
                    if (isTemporal)
                        AfterEvaluator.after(intervals[i + 1].end, applyPer.end, precision, state)
                            ?.value
                    else GreaterEvaluator.greater(intervals[i + 1].end, applyPer.end, state)?.value

                intervals[i] =
                    Interval(
                        applyPer.start,
                        true,
                        if (isNextEndGreater != null && isNextEndGreater) (intervals[i + 1]).end
                        else applyPer.end,
                        true,
                        state,
                    )
                intervals.removeAt(i + 1)
                i -= 1
            }
            ++i
        }

        return intervals
    }
}
