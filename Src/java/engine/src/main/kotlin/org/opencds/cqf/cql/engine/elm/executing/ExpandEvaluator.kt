package org.opencds.cqf.cql.engine.elm.executing

import java.math.BigDecimal
import java.math.RoundingMode
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument
import org.opencds.cqf.cql.engine.execution.State
import org.opencds.cqf.cql.engine.runtime.*

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
    private fun addPer(addTo: Any, per: Quantity): Any? {
        // Point types must stay the same, so for Integer and Long intervals, the per quantity is
        // rounded up.
        return when (addTo) {
            is Int -> AddEvaluator.add(addTo, per.value!!.setScale(0, RoundingMode.CEILING).toInt())
            is Long ->
                AddEvaluator.add(addTo, per.value!!.setScale(0, RoundingMode.CEILING).toLong())
            is BigDecimal -> AddEvaluator.add(addTo, per.value)
            is Quantity -> AddEvaluator.add(addTo, per)
            is BaseTemporal -> AddEvaluator.add(addTo, per)
            else ->
                throw InvalidOperatorArgument(
                    "Expand(List<Interval<T>>, Quantity), Expand(Interval<T>, Quantity)",
                    "Expand(${addTo.javaClass.name}, ${per.javaClass.name})",
                )
        }
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
    ): List<Interval?>? {
        var start = interval.start
        var nextStart = addPer(start!!, per)

        // per may be too small
        if (true != LessEvaluator.less(start, nextStart, state)) {
            return null
        }

        val returnedIntervals = ArrayList<Interval?>()
        val endSuccessor = SuccessorEvaluator.successor(interval.end, per)
        while (true) {
            val lessOrEqual = LessOrEqualEvaluator.lessOrEqual(nextStart, endSuccessor, state)
            if (lessOrEqual == null) {
                return null
            }
            if (lessOrEqual) {
                returnedIntervals.add(
                    Interval(start, true, PredecessorEvaluator.predecessor(nextStart, per), true)
                )
                start = nextStart
                nextStart = addPer(start!!, per)
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
    ): List<Any?>? {
        val returnedIntervals = expandIntervalsIntoIntervals(mutableListOf(interval), per, state)

        if (returnedIntervals == null) {
            return null
        }

        return returnedIntervals.map { obj -> obj!!.start }
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
        intervals: List<Interval?>?,
        per: Quantity,
        state: State?,
    ): List<Interval?>? {
        // Ignore intervals with null boundaries and truncate the boundaries.
        var intervals = intervals
        intervals =
            intervals!!
                .filter { interval -> interval!!.low != null && interval.high != null }
                .map { interval ->
                    IntervalHelper.truncateIntervalBoundaries(interval!!, per, state)
                }
                .filter { obj -> obj != null }

        // Collapse overlapping intervals to avoid returning duplicate intervals
        intervals =
            CollapseEvaluator.collapse(
                intervals,
                Quantity().withValue(BigDecimal.ZERO).withUnit(per.unit),
                state,
            )

        if (intervals == null) {
            return null
        }

        // Sort the intervals so that the expansion results are returned in order
        intervals = intervals.sortedWith(CqlList().valueSort)

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
    ): List<Interval?>? {
        var intervals: List<Interval?> = CqlList.toList(list, false)

        if (intervals.isEmpty()) {
            return intervals
        }

        // Infer the per quantity from the intervals if it is not provided
        val perOrDefault =
            if (per == null) IntervalHelper.quantityFromCoarsestPrecisionOfBoundaries(intervals)
            else per

        // Make sure the per quantity is compatible with the boundaries of the intervals
        if (!IntervalHelper.isQuantityCompatibleWithBoundaries(perOrDefault, intervals)) {
            return null
        }

        intervals = prepareIntervals(intervals, perOrDefault, state)!!

        return intervals.filterNotNull().flatMap { interval ->
            val returnedIntervals = expandIntervalIntoIntervals(interval, perOrDefault, state)
            returnedIntervals ?: listOf()
        }
    }

    @JvmStatic
    fun expand(listOrInterval: Any?, per: Quantity?, state: State?): Any? {
        if (listOrInterval == null) {
            return null
        }

        if (listOrInterval is Interval) {
            return expandIntervalIntoPoints(listOrInterval, per, state)
        } else if (listOrInterval is Iterable<*>) {
            val list = listOrInterval as Iterable<Interval?>
            return expandIntervalsIntoIntervals(list, per, state)
        }

        throw InvalidOperatorArgument(
            "Expand(List<Interval<T>>, Quantity), Expand(Interval<T>, Quantity)",
            "Expand(${listOrInterval.javaClass.name}, ${per?.javaClass?.getName()})",
        )
    }
}
