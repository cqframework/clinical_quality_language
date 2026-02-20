package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.cqframework.cql.shared.BigDecimal
import org.cqframework.cql.shared.ONE
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument
import org.opencds.cqf.cql.engine.exception.TypeOverflow
import org.opencds.cqf.cql.engine.exception.TypeUnderflow
import org.opencds.cqf.cql.engine.runtime.*
import org.opencds.cqf.cql.engine.util.javaClassName

/*
predecessor of<T>(argument T) T

The predecessor operator returns the predecessor of the argument.
  For example, the predecessor of 2 is 1. If the argument is already the minimum value for the type, a run-time error is thrown.
The predecessor operator is defined for the Integer, Long, Decimal, DateTime, and Time types.
For Integer, Long predecessor is equivalent to subtracting 1.
For Decimal, predecessor is equivalent to subtracting the minimum precision value for the Decimal type, or 10^-08.
For DateTime and Time values, predecessor is equivalent to subtracting a time-unit quantity for the lowest specified precision of the value.
  For example, if the DateTime is fully specified, predecessor is equivalent to subtracting 1 millisecond;
    if the DateTime is specified to the second, predecessor is equivalent to subtracting one second, etc.
If the argument is null, the result is null.
*/
object PredecessorEvaluator {
    /**
     * Checks if the given BigDecimal value is less than the minimum allowed value for Decimal type.
     *
     * @param value the value to check
     * @return the value if it is not less than the minimum allowed value
     * @throws TypeOverflow if the value is less than the minimum allowed for Decimal type
     */
    private fun checkMinDecimal(value: BigDecimal): BigDecimal {
        if (value.compareTo(Value.MIN_DECIMAL) < 0) {
            throw TypeUnderflow(
                "The result of the predecessor operation precedes the minimum value allowed for the Decimal type"
            )
        }
        return value
    }

    @JvmStatic
    fun predecessor(value: Any?): Any? {
        if (value == null) {
            return null
        }

        if (value is Int) {
            if (value <= Value.MIN_INT) {
                throw TypeUnderflow(
                    "The result of the predecessor operation precedes the minimum value allowed for the Integer type"
                )
            }
            return value - 1
        } else if (value is Long) {
            if (value <= Value.MIN_LONG) {
                throw TypeUnderflow(
                    "The result of the predecessor operation precedes the minimum value allowed for the Long type"
                )
            }
            return value - 1
        } else if (value is BigDecimal) {
            return checkMinDecimal(value.subtract(BigDecimal("0.00000001")))
        } else if (value is Quantity) {
            if (value.value!!.compareTo(Value.MIN_DECIMAL) <= 0) {
                throw TypeUnderflow(
                    "The result of the predecessor operation precedes the minimum value allowed for the Decimal type"
                )
            }
            val quantity = value
            return Quantity()
                .withValue(predecessor(quantity.value) as BigDecimal)
                .withUnit(quantity.unit)
        } else if (value is Date) {
            val dt = value
            return Date(dt.date!!.minus(1, dt.precision!!.toChronoUnit()), dt.precision!!)
        } else if (value is DateTime) {
            val dt = value
            return DateTime(dt.dateTime!!.minus(1, dt.precision!!.toChronoUnit()), dt.precision!!)
        } else if (value is Time) {
            val t = value
            when (t.precision!!) {
                Precision.HOUR ->
                    if (t.time.getHour() == 0) {
                        throw TypeUnderflow(
                            "The result of the successor operation precedes the minimum value allowed for the Time type"
                        )
                    }
                Precision.MINUTE ->
                    if (t.time.getHour() == 0 && t.time.getMinute() == 0) {
                        throw TypeUnderflow(
                            "The result of the successor operation precedes the minimum value allowed for the Time type"
                        )
                    }
                Precision.SECOND ->
                    if (
                        t.time.getHour() == 0 && t.time.getMinute() == 0 && t.time.getSecond() == 0
                    ) {
                        throw TypeUnderflow(
                            "The result of the successor operation precedes the minimum value allowed for the Time type"
                        )
                    }
                Precision.MILLISECOND ->
                    if (
                        t.time.getHour() == 0 &&
                            t.time.getMinute() == 0 &&
                            t.time.getSecond() == 0 &&
                            t.time.get(Precision.MILLISECOND.toChronoField()) == 0
                    ) {
                        throw TypeUnderflow(
                            "The result of the successor operation precedes the minimum value allowed for the Time type"
                        )
                    }
                Precision.DAY,
                Precision.MONTH,
                Precision.WEEK,
                Precision.YEAR -> {}
            }
            return Time(t.time.minus(1, t.precision!!.toChronoUnit()), t.precision!!)
        }

        throw InvalidOperatorArgument(
            "The Predecessor operation is not implemented for type ${value.javaClassName}"
        )
    }

    /**
     * Returns the predecessor of the given value, taking into account the precision of the given
     * quantity. This is a convenience method and not an overload of the predecessor operator.
     *
     * @param value the value to get the predecessor of
     * @param quantity the quantity specifying the precision
     * @return the predecessor of the value
     */
    @JvmStatic
    fun predecessor(value: Any?, quantity: Quantity): Any? {
        if (value is BigDecimal) {
            if (quantity.value!!.scale() > 0) {
                return checkMinDecimal(
                    value.subtract(ONE.scaleByPowerOfTen(-quantity.value!!.scale()))
                )
            }
            return checkMinDecimal(value.subtract(ONE))
        } else if (value is Quantity) {
            return Quantity()
                .withValue(predecessor(value.value, quantity) as BigDecimal)
                .withUnit(value.unit)
        }

        return predecessor(value)
    }
}
