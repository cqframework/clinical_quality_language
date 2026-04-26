package org.opencds.cqf.cql.engine.runtime

import kotlin.IllegalStateException
import org.cqframework.cql.shared.ONE
import org.cqframework.cql.shared.RoundingMode
import org.opencds.cqf.cql.engine.elm.executing.LessOrEqualEvaluator.lessOrEqual
import org.opencds.cqf.cql.engine.exception.InvalidPrecision
import org.opencds.cqf.cql.engine.execution.State

class IntervalHelper private constructor() {
    init {
        throw IllegalStateException("Utility class")
    }

    companion object {
        /**
         * Returns the first non-null boundary from the list of intervals.
         *
         * @param intervals the list of intervals to search
         * @return the first non-null boundary found
         */
        fun findNonNullBoundary(intervals: kotlin.collections.List<Interval?>): Value? {
            return intervals
                .filterNotNull()
                .flatMap { interval -> listOf(interval.start, interval.end) }
                .firstOrNull { obj -> obj != null }
        }

        /**
         * Creates a Quantity based on the coarsest precision of the boundaries from the given
         * intervals.
         *
         * @param intervals the list of intervals to use
         * @return a Quantity with a value of 1 and the scale and unit determined by the precision
         *   of the interval boundaries
         */
        fun quantityFromCoarsestPrecisionOfBoundaries(
            intervals: kotlin.collections.List<Interval?>
        ): Quantity {
            val nonNullBoundary = findNonNullBoundary(intervals)

            when (nonNullBoundary) {
                is Decimal -> {
                    val scale =
                        DecimalHelper.getCoarsestScale(
                            intervals.filterNotNull().flatMap { interval ->
                                listOf(
                                    (interval.start as Decimal?)?.value,
                                    (interval.end as Decimal?)?.value,
                                )
                            }
                        )
                    return Quantity()
                        .withValue(ONE.setScale(scale, RoundingMode.UNNECESSARY))
                        .withDefaultUnit()
                }

                is Quantity -> {
                    val scale =
                        DecimalHelper.getCoarsestScale(
                            intervals
                                .filterNotNull()
                                .flatMap { interval ->
                                    listOf(interval.start as Quantity?, interval.end as Quantity?)
                                }
                                .filterNotNull()
                                .map { obj -> obj.value }
                        )
                    return Quantity()
                        .withValue(ONE.setScale(scale, RoundingMode.UNNECESSARY))
                        .withUnit(nonNullBoundary.unit)
                }

                is BaseTemporal -> {
                    val precision =
                        BaseTemporal.getLowestPrecision(
                            *(intervals
                                .filterNotNull()
                                .flatMap { interval ->
                                    listOf(
                                        interval.start as BaseTemporal?,
                                        interval.end as BaseTemporal?,
                                    )
                                }
                                .toTypedArray())
                        )
                    return Quantity().withValue(ONE).withUnit(precision)
                }

                else -> {
                    return Quantity().withValue(ONE).withDefaultUnit()
                }
            }
        }

        /**
         * Checks if the given quantity is compatible with the boundaries of the intervals.
         *
         * @param quantity the quantity to check
         * @param intervals the list of intervals to check against
         * @return true if the quantity is compatible with the boundaries, false otherwise
         */
        fun isQuantityCompatibleWithBoundaries(
            quantity: Quantity,
            intervals: kotlin.collections.List<Interval?>,
        ): kotlin.Boolean {
            val nonNullBoundary = findNonNullBoundary(intervals)

            when (nonNullBoundary) {
                is Integer,
                is Long,
                is Decimal -> {
                    return Quantity.isDefaultUnit(quantity.unit)
                }

                is Quantity -> {
                    return Quantity.unitsEqual(quantity.unit, nonNullBoundary.unit)
                }

                is BaseTemporal -> {
                    try {
                        Precision.fromString(quantity.unit!!)
                        return true
                    } catch (e: InvalidPrecision) {
                        return false // quantity unit is not a valid temporal unit
                    }
                }

                else -> {
                    return true
                }
            }
        }

        /**
         * Truncates the boundaries of the given interval to the precision specified by the given
         * quantity. When the boundaries are truncated, the truncated start is rounded towards
         * positive infinity and the truncated end is rounded towards negative infinity. If the
         * truncated start becomes greater than the truncated end (e.g. for interval = Interval[0.3,
         * 0.5] and quantity = 1 '1'), this method returns null.
         *
         * @param interval the interval with the boundaries to truncate
         * @param quantity the quantity specifying the precision to truncate to
         * @param state the engine state
         * @return the interval with the truncated boundaries
         */
        fun truncateIntervalBoundaries(
            interval: Interval,
            quantity: Quantity,
            state: State?,
        ): Interval? {
            val start = interval.start
            val end = interval.end

            when (start) {
                is Decimal if end is Decimal -> {
                    val quantityScale = quantity.value!!.scale()
                    val truncatedStart =
                        DecimalHelper.roundToScale(start.value, quantityScale, true)
                    val truncatedEnd = DecimalHelper.roundToScale(end.value, quantityScale, false)

                    if (truncatedStart <= truncatedEnd) {
                        return Interval(
                            truncatedStart.toCqlDecimal(),
                            true,
                            truncatedEnd.toCqlDecimal(),
                            true,
                            state,
                        )
                    }

                    return null
                }

                is Quantity if end is Quantity -> {
                    val quantityScale = quantity.value!!.scale()
                    val truncatedStart =
                        Quantity()
                            .withValue(
                                DecimalHelper.roundToScale(start.value!!, quantityScale, true)
                            )
                            .withUnit(start.unit)
                    val truncatedEnd =
                        Quantity()
                            .withValue(
                                DecimalHelper.roundToScale(end.value!!, quantityScale, false)
                            )
                            .withUnit(end.unit)

                    if (truncatedStart <= truncatedEnd) {
                        return Interval(truncatedStart, true, truncatedEnd, true, state)
                    }

                    return null
                }

                is BaseTemporal if end is BaseTemporal -> {
                    val precision = Precision.fromString(quantity.unit!!)
                    val truncatedStart = start.roundToPrecision(precision, true)
                    val truncatedEnd = end.roundToPrecision(precision, false)

                    if (true == lessOrEqual(truncatedStart, truncatedEnd, state)?.value) {
                        return Interval(truncatedStart, true, truncatedEnd, true)
                    }

                    return null
                }

                else -> return interval
            }
        }
    }
}
