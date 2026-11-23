package org.opencds.cqf.cql.engine.runtime

import kotlin.js.JsExport
import kotlin.jvm.JvmOverloads
import org.cqframework.cql.shared.JsOnlyExport
import org.cqframework.cql.shared.QName
import org.opencds.cqf.cql.engine.elm.executing.GreaterEvaluator.greater
import org.opencds.cqf.cql.engine.elm.executing.MaxValueEvaluator.maxValue
import org.opencds.cqf.cql.engine.elm.executing.MinValueEvaluator.minValue
import org.opencds.cqf.cql.engine.elm.executing.PredecessorEvaluator.predecessor
import org.opencds.cqf.cql.engine.elm.executing.SubtractEvaluator.subtract
import org.opencds.cqf.cql.engine.elm.executing.SuccessorEvaluator.successor
import org.opencds.cqf.cql.engine.exception.InvalidInterval
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument
import org.opencds.cqf.cql.engine.execution.State

@JsOnlyExport
class Interval
@JvmOverloads
@Suppress("NON_EXPORTABLE_TYPE")
constructor(
    val low: Value?,
    val lowClosed: kotlin.Boolean,
    val high: Value?,
    val highClosed: kotlin.Boolean,
    val isUncertain: kotlin.Boolean = false,
) : Value {
    override val typeAsString: kotlin.String
        get() = "Interval<${this.pointType}>"

    /** Inferred from the runtime values of the low and/or high boundaries. */
    val pointType: QName

    init {
        if (low == null && high == null) {
            throw InvalidInterval("Low or high boundary of an interval must be present.")
        }

        val lowNamedType = getNamedTypeForCqlValue(low)
        val highNamedType = getNamedTypeForCqlValue(high)

        if (lowNamedType == null) {
            throw InvalidInterval(
                "The low boundary value of the interval must be an instance of a CQL named type."
            )
        }
        if (highNamedType == null) {
            throw InvalidInterval(
                "The high boundary value of the interval must be an instance of a CQL named type."
            )
        }

        if (low != null && high != null) {
            // Make sure low and high are of the same type
            if (lowNamedType != highNamedType) {
                throw InvalidInterval(
                    "Low and high boundary values of an interval must be of the same type."
                )
            }
        }

        // Use the type of the non-null boundary
        pointType = if (low == null) highNamedType else lowNamedType
    }

    @JsExport.Ignore
    constructor(
        low: Value?,
        lowClosed: kotlin.Boolean,
        high: Value?,
        highClosed: kotlin.Boolean,
        isUncertain: kotlin.Boolean = false,
        state: State? = null,
    ) : this(low, lowClosed, high, highClosed, isUncertain) {
        if (low != null && high != null) {
            val isStartGreater = greater(this.start, this.end, state)
            if (isStartGreater == null || isStartGreater.value) {
                throw InvalidInterval(
                    "Invalid Interval - the ending boundary ($high) must be greater than or equal to the starting boundary ($low)."
                )
            }
        }
    }

    fun withLow(low: Value?): Interval {
        return Interval(low, lowClosed, high, highClosed, isUncertain)
    }

    fun withHigh(high: Value?): Interval {
        return Interval(low, lowClosed, high, highClosed, isUncertain)
    }

    val start: Value?
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
                Quantity().withValue(Constants.MIN_DECIMAL).withUnit(high.unit)
            } else {
                minValue(pointType)
            }
        }

    val end: Value?
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
                Quantity().withValue(Constants.MAX_DECIMAL).withUnit(low.unit)
            } else {
                maxValue(pointType)
            }
        }

    private val hash: Int by lazy {
        listOf(low, lowClosed, high, highClosed, isUncertain).hashCode()
    }

    override fun hashCode(): Int = hash

    override fun equals(other: Any?): kotlin.Boolean =
        this === other ||
            (other is Interval &&
                low == other.low &&
                lowClosed == other.lowClosed &&
                high == other.high &&
                highClosed == other.highClosed &&
                isUncertain == other.isUncertain)

    override fun toString(): kotlin.String {
        return "Interval${if (this.lowClosed) "[" else "("}${if (this.low == null) "null" else this.low.toString()}, ${if (this.high == null) "null" else this.high.toString()}${if (this.highClosed) "]" else ")"}"
    }

    companion object {
        @JsExport.Ignore
        fun getSize(start: Value?, end: Value?, state: State?): Value? {
            if (start == null || end == null) {
                return null
            }

            if (start is Integer || start is Decimal || start is Quantity) {
                return subtract(end, start, state)
            }

            throw InvalidOperatorArgument(
                "Cannot perform width operator with argument of type '${start.typeAsString}'."
            )
        }
    }
}
