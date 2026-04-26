package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.cqframework.cql.shared.RoundingMode
import org.cqframework.cql.shared.ZERO
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument
import org.opencds.cqf.cql.engine.execution.State
import org.opencds.cqf.cql.engine.runtime.BaseTemporal
import org.opencds.cqf.cql.engine.runtime.Decimal
import org.opencds.cqf.cql.engine.runtime.Integer
import org.opencds.cqf.cql.engine.runtime.Interval
import org.opencds.cqf.cql.engine.runtime.IntervalHelper
import org.opencds.cqf.cql.engine.runtime.List
import org.opencds.cqf.cql.engine.runtime.Long
import org.opencds.cqf.cql.engine.runtime.Quantity
import org.opencds.cqf.cql.engine.runtime.SortHelper
import org.opencds.cqf.cql.engine.runtime.Value
import org.opencds.cqf.cql.engine.runtime.toCqlDecimal
import org.opencds.cqf.cql.engine.runtime.toCqlInteger
import org.opencds.cqf.cql.engine.runtime.toCqlList
import org.opencds.cqf.cql.engine.runtime.toCqlLong

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
object ExpandEvaluator {
    private fun addPer(addTo: Value, per: Quantity, state: State?): Value? {
        // Point types must stay the same, so for Integer and Long intervals, the per quantity is
        // rounded up.
        val rhs =
            when (addTo) {
                is Integer -> per.value!!.setScale(0, RoundingMode.CEILING).toInt().toCqlInteger()
                is Long -> per.value!!.setScale(0, RoundingMode.CEILING).toLong().toCqlLong()
                is Decimal -> per.value?.toCqlDecimal()
                is Quantity,
                is BaseTemporal -> per
                else ->
                    throw InvalidOperatorArgument(
                        "Expand(List<Interval<T>>, Quantity), Expand(Interval<T>, Quantity)",
                        "Expand(${addTo.typeAsString}, ${per.typeAsString})",
                    )
            }
        return AddEvaluator.add(addTo, rhs, state)
    }

    /**
     * Performs interval expansion for the given interval and per quantity.
     *
     * @param interval the interval to expand
     * @param per the size of the intervals to return
     * @param state the engine state
     * @return the list of smaller intervals of size per
     */
    private fun expandIntervalIntoIntervals(
        interval: Interval,
        per: Quantity,
        state: State?,
    ): kotlin.collections.List<Interval>? {
        var start = interval.start
        var nextStart = addPer(start!!, per, state)

        // per may be too small
        if (true != LessEvaluator.less(start, nextStart, state)?.value) {
            return null
        }

        val returnedIntervals = mutableListOf<Interval>()
        val endSuccessor = SuccessorEvaluator.successor(interval.end, per)
        while (true) {
            val lessOrEqual = LessOrEqualEvaluator.lessOrEqual(nextStart, endSuccessor, state)
            if (lessOrEqual == null) {
                return null
            }
            if (lessOrEqual.value) {
                returnedIntervals.add(
                    Interval(
                        start,
                        true,
                        PredecessorEvaluator.predecessor(nextStart, per),
                        true,
                        state,
                    )
                )
                start = nextStart
                nextStart = addPer(start!!, per, state)
            } else {
                break
            }
        }

        return returnedIntervals
    }

    /**
     * Implements the interval overload of the expand operator. The calculation is performed the
     * same way as with the list overload, but the starting point of each resulting interval is
     * returned, rather than the interval.
     *
     * @param interval the interval to expand
     * @param per the distance between the points to return
     * @param state the engine state
     * @return the list of points from the interval
     */
    private fun expandIntervalIntoPoints(
        interval: Interval?,
        per: Quantity?,
        state: State?,
    ): kotlin.collections.List<Value?>? {
        val returnedIntervals = expandIntervalsIntoIntervals(mutableListOf(interval), per, state)

        if (returnedIntervals == null) {
            return null
        }

        return returnedIntervals.map { obj -> obj.start }
    }

    /**
     * Prepares the intervals for expansion.
     *
     * @param intervals the list of intervals to prepare
     * @param per the per quantity for expansion
     * @param state the engine state
     * @return the prepared list of intervals
     */
    private fun prepareIntervals(
        intervals: kotlin.collections.List<Interval>,
        per: Quantity,
        state: State?,
    ): kotlin.collections.List<Interval> {
        // Ignore intervals with null boundaries and truncate the boundaries.
        var intervals = intervals
        intervals =
            intervals
                .filter { interval -> interval.low != null && interval.high != null }
                .map { interval -> IntervalHelper.truncateIntervalBoundaries(interval, per, state) }
                .filterNotNull()

        // Collapse overlapping intervals to avoid returning duplicate intervals
        intervals =
            CollapseEvaluator.collapse(
                intervals,
                Quantity().withValue(ZERO).withUnit(per.unit),
                state,
            )

        // Sort the intervals so that the expansion results are returned in order
        intervals = intervals.sortedWith { left, right -> SortHelper.compare(left, right, state) }

        return intervals
    }

    /**
     * Implements the list overload of the expand operator.
     *
     * @param list the list of intervals to expand
     * @param per the size of the intervals to return
     * @param state the engine state
     * @return the list of smaller intervals of size per
     */
    private fun expandIntervalsIntoIntervals(
        list: Iterable<Interval?>,
        per: Quantity?,
        state: State?,
    ): kotlin.collections.List<Interval>? {
        var intervals = list.filterNotNull()

        if (intervals.isEmpty()) {
            return intervals
        }

        // Infer the per quantity from the intervals if it is not provided
        val perOrDefault =
            per ?: IntervalHelper.quantityFromCoarsestPrecisionOfBoundaries(intervals)

        // Make sure the per quantity is compatible with the boundaries of the intervals
        val convertedPerOrDefault =
            if (IntervalHelper.isQuantityCompatibleWithBoundaries(perOrDefault, intervals)) {
                perOrDefault
            } else {
                val boundary = IntervalHelper.findNonNullBoundary(intervals)
                if (boundary is Quantity) {
                    val ucumService = state?.environment?.libraryManager?.ucumService!!
                    try {
                        Quantity()
                            .withValue(
                                ucumService.convert(
                                    perOrDefault.value!!,
                                    perOrDefault.unit!!,
                                    boundary.unit!!,
                                )
                            )
                            .withUnit(boundary.unit)
                    } catch (_: Exception) {
                        return null
                    }
                } else {
                    return null
                }
            }

        intervals = prepareIntervals(intervals, convertedPerOrDefault, state)

        return intervals.flatMap { interval ->
            val returnedIntervals =
                expandIntervalIntoIntervals(interval, convertedPerOrDefault, state)
            returnedIntervals ?: listOf()
        }
    }

    @JvmStatic
    fun expand(listOrInterval: Value?, per: Value?, state: State?): List? {
        if (listOrInterval == null) {
            return null
        }

        if (per is Quantity?) {
            if (listOrInterval is Interval) {
                return expandIntervalIntoPoints(listOrInterval, per, state)?.toCqlList()
            } else if (listOrInterval is List && listOrInterval.all { it is Interval? }) {
                val list = listOrInterval.filterIsInstance<Interval?>()
                return expandIntervalsIntoIntervals(list, per, state)?.toCqlList()
            }
        }

        throw InvalidOperatorArgument(
            "Expand(List<Interval<T>>, Quantity), Expand(Interval<T>, Quantity)",
            "Expand(${listOrInterval.typeAsString}, ${per?.typeAsString})",
        )
    }
}
