package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.cqframework.cql.shared.BigDecimal
import org.cqframework.cql.shared.ONE
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument
import org.opencds.cqf.cql.engine.exception.TypeOverflow
import org.opencds.cqf.cql.engine.runtime.Constants
import org.opencds.cqf.cql.engine.runtime.Date
import org.opencds.cqf.cql.engine.runtime.DateTime
import org.opencds.cqf.cql.engine.runtime.Decimal
import org.opencds.cqf.cql.engine.runtime.Integer
import org.opencds.cqf.cql.engine.runtime.Long
import org.opencds.cqf.cql.engine.runtime.Precision
import org.opencds.cqf.cql.engine.runtime.Quantity
import org.opencds.cqf.cql.engine.runtime.Time
import org.opencds.cqf.cql.engine.runtime.Value
import org.opencds.cqf.cql.engine.runtime.toCqlDecimal
import org.opencds.cqf.cql.engine.runtime.toCqlInteger
import org.opencds.cqf.cql.engine.runtime.toCqlLong

/*
successor of<T>(argument T) T

The successor operator returns the successor of the argument. For example, the successor of 1 is 2.
  If the argument is already the maximum value for the type, or the result cannot otherwise be
  represented (i.e. would result in overflow), the result is null.
The successor operator is defined for the Integer, Long, Decimal, DateTime, and Time types.
For Integer, Long successor is equivalent to adding 1.
For Decimal, successor is equivalent to adding the minimum precision value for the Decimal type, or 10^-08.
For DateTime and Time values, successor is equivalent to adding a time-unit quantity for the
  lowest specified precision of the value.
  For example, if the DateTime is fully specified, successor is equivalent to adding 1 millisecond;
    if the DateTime is specified to the second, successor is equivalent to adding one second, etc.
If the argument is null, the result is null.
*/
object SuccessorEvaluator {
    // The maximum representable year for Date/DateTime values (see the bounds enforced by the
    // Date and DateTime types).
    private const val MAX_YEAR = 9999

    /**
     * Checks if the given BigDecimal value exceeds the maximum allowed value for Decimal type.
     *
     * @param value the value to check
     * @return the value if it does not exceed the maximum allowed value
     * @throws TypeOverflow if the value exceeds the maximum allowed for Decimal type
     */
    private fun checkMaxDecimal(value: BigDecimal): BigDecimal {
        if (value.compareTo(Constants.MAX_DECIMAL) > 0) {
            throw TypeOverflow(
                "The result of the successor operation exceeds the maximum value allowed for the Decimal type"
            )
        }
        return value
    }

    @JvmStatic
    fun successor(value: Value?): Value? {
        if (value == null) {
            return null
        }

        if (value is Integer) {
            if (value.value >= Constants.MAX_INT) {
                throw TypeOverflow(
                    "The result of the successor operation exceeds the maximum value allowed for the Integer type"
                )
            }
            return (value.value + 1).toCqlInteger()
        } else if (value is Long) {
            if (value.value >= Constants.MAX_LONG) {
                throw TypeOverflow(
                    "The result of the successor operation exceeds the maximum value allowed for the Long type"
                )
            }
            return (value.value + 1).toCqlLong()
        } else if (value is Decimal) {
            return checkMaxDecimal(value.value.add(BigDecimal("0.00000001"))).toCqlDecimal()
        } else if (value is Quantity) {
            if (value.value!!.compareTo(Constants.MAX_DECIMAL) >= 0) {
                throw TypeOverflow(
                    "The result of the successor operation exceeds the maximum value allowed for the Decimal type"
                )
            }
            val quantity = value
            return Quantity()
                .withValue((successor(quantity.value?.toCqlDecimal()) as Decimal).value)
                .withUnit(quantity.unit)
        } else if (value is Date) {
            val next = value.date!!.plus(1, value.precision!!.toChronoUnit())
            // If incrementing overflows the representable year range, the result is null.
            return if (next.getYear() > MAX_YEAR) null else Date(next, value.precision!!)
        } else if (value is DateTime) {
            val next = value.dateTime!!.plus(1, value.precision!!.toChronoUnit())
            // If incrementing overflows the representable year range, the result is null.
            return if (next.getYear() > MAX_YEAR) null else DateTime(next, value.precision!!)
        } else if (value is Time) {
            val t = value
            // If the value is already the maximum for its precision, incrementing overflows the
            // representable range and the result is null.
            val overflow =
                when (t.precision!!) {
                    Precision.HOUR -> t.time.getHour() == 23
                    Precision.MINUTE -> t.time.getHour() == 23 && t.time.getMinute() == 59
                    Precision.SECOND ->
                        t.time.getHour() == 23 &&
                            t.time.getMinute() == 59 &&
                            t.time.getSecond() == 59
                    Precision.MILLISECOND ->
                        t.time.getHour() == 23 &&
                            t.time.getMinute() == 59 &&
                            t.time.getSecond() == 59 &&
                            t.time.get(Precision.MILLISECOND.toChronoField()) == 999
                    Precision.DAY,
                    Precision.MONTH,
                    Precision.WEEK,
                    Precision.YEAR -> false
                }
            return if (overflow) null
            else Time(t.time.plus(1, t.precision!!.toChronoUnit()), t.precision!!)
        }

        throw InvalidOperatorArgument(
            "The Successor operation is not implemented for type ${value.typeAsString}"
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
    fun successor(value: Value?, quantity: Quantity?): Value? {
        if (value is Decimal) {
            if (quantity!!.value!!.scale() > 0) {
                return checkMaxDecimal(
                        value.value.add(ONE.scaleByPowerOfTen(-quantity.value!!.scale()))
                    )
                    .toCqlDecimal()
            }
            return checkMaxDecimal(value.value.add(ONE)).toCqlDecimal()
        } else if (value is Quantity) {
            return Quantity()
                .withValue((successor(value.value?.toCqlDecimal(), quantity) as Decimal).value)
                .withUnit(value.unit)
        }

        return successor(value)
    }
}
