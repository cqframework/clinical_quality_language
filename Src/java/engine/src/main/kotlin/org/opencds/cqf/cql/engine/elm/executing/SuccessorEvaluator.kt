package org.opencds.cqf.cql.engine.elm.executing

import java.math.BigDecimal
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument
import org.opencds.cqf.cql.engine.exception.TypeOverflow
import org.opencds.cqf.cql.engine.runtime.*

/*
successor of<T>(argument T) T

The successor operator returns the successor of the argument. For example, the successor of 1 is 2.
  If the argument is already the maximum value for the type, a run-time error is thrown.
The successor operator is defined for the Integer, Long, Decimal, DateTime, and Time types.
For Integer, Long successor is equivalent to adding 1.
For Decimal, successor is equivalent to adding the minimum precision value for the Decimal type, or 10^-08.
For DateTime and Time values, successor is equivalent to adding a time-unit quantity for the lowest specified precision of the value.
  For example, if the DateTime is fully specified, successor is equivalent to adding 1 millisecond;
    if the DateTime is specified to the second, successor is equivalent to adding one second, etc.
If the argument is null, the result is null.
*/
object SuccessorEvaluator {
    /**
     * Checks if the given BigDecimal value exceeds the maximum allowed value for Decimal type.
     *
     * @param value the value to check
     * @return the value if it does not exceed the maximum allowed value
     * @throws TypeOverflow if the value exceeds the maximum allowed for Decimal type
     */
    private fun checkMaxDecimal(value: BigDecimal): BigDecimal {
        if (value.compareTo(Value.MAX_DECIMAL) > 0) {
            throw TypeOverflow(
                "The result of the successor operation exceeds the maximum value allowed for the Decimal type"
            )
        }
        return value
    }

    @JvmStatic
    fun successor(value: Any?): Any? {
        if (value == null) {
            return null
        }

        if (value is Int) {
            if (value >= Value.MAX_INT) {
                throw TypeOverflow(
                    "The result of the successor operation exceeds the maximum value allowed for the Integer type"
                )
            }
            return value + 1
        } else if (value is Long) {
            if (value >= Value.MAX_LONG) {
                throw TypeOverflow(
                    "The result of the successor operation exceeds the maximum value allowed for the Long type"
                )
            }
            return value + 1
        } else if (value is BigDecimal) {
            return checkMaxDecimal(value.add(BigDecimal("0.00000001")))
        } else if (value is Quantity) {
            if (value.value!!.compareTo(Value.MAX_DECIMAL) >= 0) {
                throw TypeOverflow(
                    "The result of the successor operation exceeds the maximum value allowed for the Decimal type"
                )
            }
            val quantity = value
            return Quantity()
                .withValue(successor(quantity.value) as BigDecimal)
                .withUnit(quantity.unit)
        } else if (value is Date) {
            val dt = value
            return Date(dt.date!!.plus(1, dt.precision!!.toChronoUnit()), dt.precision!!)
        } else if (value is DateTime) {
            val dt = value
            return DateTime(dt.dateTime!!.plus(1, dt.precision!!.toChronoUnit()), dt.precision!!)
        } else if (value is Time) {
            val t = value
            when (t.precision!!) {
                Precision.HOUR ->
                    if (t.time.getHour() == 23) {
                        throw TypeOverflow(
                            "The result of the successor operation exceeds the maximum value allowed for the Time type"
                        )
                    }
                Precision.MINUTE ->
                    if (t.time.getHour() == 23 && t.time.getMinute() == 23) {
                        throw TypeOverflow(
                            "The result of the successor operation exceeds the maximum value allowed for the Time type"
                        )
                    }
                Precision.SECOND ->
                    if (
                        t.time.getHour() == 23 &&
                            t.time.getMinute() == 23 &&
                            t.time.getSecond() == 59
                    ) {
                        throw TypeOverflow(
                            "The result of the successor operation exceeds the maximum value allowed for the Time type"
                        )
                    }
                Precision.MILLISECOND ->
                    if (
                        t.time.getHour() == 23 &&
                            t.time.getMinute() == 59 &&
                            t.time.getSecond() == 59 &&
                            t.time.get(Precision.MILLISECOND.toChronoField()) == 999
                    ) {
                        throw TypeOverflow(
                            "The result of the successor operation exceeds the maximum value allowed for the Time type"
                        )
                    }
                Precision.DAY,
                Precision.MONTH,
                Precision.WEEK,
                Precision.YEAR -> {}
            }
            return Time(t.time.plus(1, t.precision!!.toChronoUnit()), t.precision!!)
        }

        throw InvalidOperatorArgument(
            "The Successor operation is not implemented for type ${value.javaClass.name}"
        )
    }

    /**
     * Returns the successor of the given value, taking into account the precision of the given
     * quantity. This is a convenience method and not an overload of the successor operator.
     *
     * @param value the value to get the successor of
     * @param quantity the quantity specifying the precision
     * @return the successor of the value
     */
    @JvmStatic
    fun successor(value: Any?, quantity: Quantity?): Any? {
        if (value is BigDecimal) {
            if (quantity!!.value!!.scale() > 0) {
                return checkMaxDecimal(
                    value.add(BigDecimal.ONE.scaleByPowerOfTen(-quantity.value!!.scale()))
                )
            }
            return checkMaxDecimal(value.add(BigDecimal.ONE))
        } else if (value is Quantity) {
            return Quantity()
                .withValue(successor(value.value, quantity) as BigDecimal)
                .withUnit(value.unit)
        }

        return successor(value)
    }
}
