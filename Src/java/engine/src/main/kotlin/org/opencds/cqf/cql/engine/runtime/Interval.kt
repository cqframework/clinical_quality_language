package org.opencds.cqf.cql.engine.runtime

import java.math.BigDecimal
import java.util.Date
import kotlin.hashCode
import kotlin.toString
import org.opencds.cqf.cql.engine.elm.executing.AndEvaluator.and
import org.opencds.cqf.cql.engine.elm.executing.EqualEvaluator.equal
import org.opencds.cqf.cql.engine.elm.executing.EquivalentEvaluator.equivalent
import org.opencds.cqf.cql.engine.elm.executing.GreaterEvaluator.greater
import org.opencds.cqf.cql.engine.elm.executing.IntersectEvaluator.intersect
import org.opencds.cqf.cql.engine.elm.executing.MaxValueEvaluator.maxValue
import org.opencds.cqf.cql.engine.elm.executing.MinValueEvaluator.minValue
import org.opencds.cqf.cql.engine.elm.executing.PredecessorEvaluator.predecessor
import org.opencds.cqf.cql.engine.elm.executing.SubtractEvaluator.subtract
import org.opencds.cqf.cql.engine.elm.executing.SuccessorEvaluator.successor
import org.opencds.cqf.cql.engine.exception.InvalidInterval
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument
import org.opencds.cqf.cql.engine.execution.State

class Interval
@JvmOverloads
constructor(
    var low: Any?,
    val lowClosed: Boolean,
    var high: Any?,
    val highClosed: Boolean,
    val state: State? = null,
) : CqlType, Comparable<Interval> {
    var pointType: Class<*>? = null

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

        if (
            !(CqlType::class.java.isAssignableFrom(pointType) ||
                pointType!!.getName().startsWith("java")) && this.state == null
        ) {
            throw InvalidInterval(
                "Boundary values that are not CQL Types require Context to evaluate."
            )
        }

        if (this.high != null && this.high!!.javaClass != pointType) {
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
            val isStartGreater = greater(this.start, this.end, this.state)
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
            if (!lowClosed) {
                return successor(low)
            } else {
                return if (low == null) minValue(pointType!!.getTypeName()) else low
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
            if (!highClosed) {
                return predecessor(high)
            } else {
                return if (high == null) maxValue(pointType!!.getTypeName()) else high
            }
        }

    override fun compareTo(other: Interval): Int {
        val cqlList = CqlList()
        if (cqlList.compareTo(this.start, other.start) == 0) {
            return cqlList.compareTo(this.end, other.end)
        }
        return cqlList.compareTo(this.start, other.start)
    }

    override fun equivalent(other: Any?): Boolean? {
        return equivalent(this.start, (other as Interval).start, this.state) == true &&
            equivalent(this.end, other.end, this.state) == true
    }

    override fun equal(other: Any?): Boolean? {
        if (other is Interval) {
            if (this.isUncertain) {
                if (intersect(this, other, this.state) != null) {
                    return null
                }
            }

            val otherInterval = other
            return and(
                equal(this.start, otherInterval.start, this.state),
                equal(this.end, otherInterval.end, this.state),
            )
        }

        if (other is Int) {
            return equal(Interval(other, true, other, true, this.state))
        }

        throw InvalidOperatorArgument(
            "Cannot perform equal operation on types: '${this.javaClass.getName()}' and '${other?.javaClass?.getName()}'"
        )
    }

    override fun equals(other: Any?): Boolean {
        return if (other is Interval) equivalent(other) == true else false
    }

    override fun hashCode(): Int {
        return ((31 * (if (lowClosed) 1 else 0)) +
            (47 * (if (highClosed) 1 else 0)) +
            (13 * (if (low != null) low.hashCode() else 0)) +
            (89 * (if (high != null) high.hashCode() else 0)))
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
                "Cannot perform width operator with argument of type '${start.javaClass.getName()}'."
            )
        }
    }
}
