package org.opencds.cqf.cql.engine.runtime

import kotlin.jvm.JvmOverloads
import kotlin.toString
import org.cqframework.cql.shared.BigDecimal
import org.opencds.cqf.cql.engine.elm.executing.GreaterEvaluator.greater
import org.opencds.cqf.cql.engine.elm.executing.MaxValueEvaluator.maxValue
import org.opencds.cqf.cql.engine.elm.executing.MinValueEvaluator.minValue
import org.opencds.cqf.cql.engine.elm.executing.PredecessorEvaluator.predecessor
import org.opencds.cqf.cql.engine.elm.executing.SubtractEvaluator.subtract
import org.opencds.cqf.cql.engine.elm.executing.SuccessorEvaluator.successor
import org.opencds.cqf.cql.engine.exception.InvalidInterval
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument
import org.opencds.cqf.cql.engine.execution.State
import org.opencds.cqf.cql.engine.util.Date
import org.opencds.cqf.cql.engine.util.JavaClass
import org.opencds.cqf.cql.engine.util.javaClass
import org.opencds.cqf.cql.engine.util.javaClassName

class Interval
@JvmOverloads
constructor(
    var low: Any?,
    val lowClosed: Boolean,
    var high: Any?,
    val highClosed: Boolean,
    state: State? = null,
) : CqlType {
    var pointType: JavaClass<*>? = null

    var isUncertain: Boolean = false
        private set

    init {
        if (this.low != null) {
            pointType = this.low!!.javaClass
        } else if (this.high != null) {
            pointType = this.high!!.javaClass
        }

        if (pointType == null) {
            throw InvalidInterval("Low or high boundary of an interval must be present.")
        }

        if (this.low != null && this.high != null && (this.low!!::class != this.high!!::class)) {
            throw InvalidInterval(
                "Low and high boundary values of an interval must be of the same type."
            )
        }

        // Special case for measure processing - MeasurementPeriod is a java date
        if (low is Date && high is Date) {
            if ((low as Date).after(high as Date)) {
                throw InvalidInterval(
                    "Invalid Interval - the ending boundary (${high}) must be greater than or equal to the starting boundary (${low})."
                )
            }
        } else if (low != null && high != null) {
            val isStartGreater = greater(this.start, this.end, state)
            if (isStartGreater == null || isStartGreater) {
                throw InvalidInterval(
                    "Invalid Interval - the ending boundary (${high}) must be greater than or equal to the starting boundary (${low})."
                )
            }
        }
    }

    fun setUncertain(uncertain: Boolean): Interval {
        this.isUncertain = uncertain
        return this
    }

    val start: Any?
        /*
        Returns the starting point of the interval.

        If the low boundary of the interval is open, returns the Successor of the low value of the interval.
        Note that if the low value of the interval is null, the result is null.

        If the low boundary of the interval is closed and the low value of the interval is not null,
        returns the low value of the interval. Otherwise, the result is the minimum value of
        the point type of the interval.
         */
        get() {
            return if (!lowClosed) {
                successor(low)
            } else if (low != null) {
                low
            } else if (high is Quantity) {
                val highQuantity = high as Quantity
                Quantity().withValue(Value.MIN_DECIMAL).withUnit(highQuantity.unit)
            } else {
                minValue(pointType!!.getTypeName())
            }
        }

    val end: Any?
        /*
        Returns the ending point of an interval.

        If the high boundary of the interval is open, returns the Predecessor of the high value of the interval.
        Note that if the high value of the interval is null, the result is null.

        If the high boundary of the interval is closed and the high value of the interval is not null,
        returns the high value of the interval. Otherwise, the result is the maximum value of
        the point type of the interval.
         */
        get() {
            return if (!highClosed) {
                predecessor(high)
            } else if (high != null) {
                high
            } else if (low is Quantity) {
                val lowQuantity = low as Quantity
                Quantity().withValue(Value.MAX_DECIMAL).withUnit(lowQuantity.unit)
            } else {
                maxValue(pointType!!.getTypeName())
            }
        }

    fun compareTo(other: Interval, state: State?): Int {
        val cqlList = CqlList(state)
        if (cqlList.compareTo(this.start, other.start) == 0) {
            return cqlList.compareTo(this.end, other.end)
        }
        return cqlList.compareTo(this.start, other.start)
    }

    override fun toString(): String {
        return "Interval${if (this.lowClosed) "[" else "("}${if (this.low == null) "null" else this.low.toString()}, ${if (this.high == null) "null" else this.high.toString()}${if (this.highClosed) "]" else ")"}"
    }

    companion object {
        fun getSize(start: Any?, end: Any?, state: State?): Any? {
            if (start == null || end == null) {
                return null
            }

            if (start is Int || start is BigDecimal || start is Quantity) {
                return subtract(end, start, state)
            }

            throw InvalidOperatorArgument(
                "Cannot perform width operator with argument of type '${start.javaClassName}'."
            )
        }
    }
}
