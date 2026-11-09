package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument
import org.opencds.cqf.cql.engine.runtime.Boolean
import org.opencds.cqf.cql.engine.runtime.Integer
import org.opencds.cqf.cql.engine.runtime.Long
import org.opencds.cqf.cql.engine.runtime.String
import org.opencds.cqf.cql.engine.runtime.Value
import org.opencds.cqf.cql.engine.runtime.toCqlInteger

/*
ToInteger(argument String) Integer

The ToInteger operator converts the value of its argument to an Integer value.
The operator accepts strings using the following format:
  (+|-)?#0
Meaning an optional polarity indicator, followed by any number of digits (including none), followed by at least one digit.
Note that the integer value returned by this operator must be a valid value in the range representable for Integer values in CQL.
If the input string is not formatted correctly, or cannot be interpreted as a valid Integer value, the result is null.
If the argument is null, the result is null.

ToInteger(argument Long) Integer

If the argument can be represented as an Integer, the result is that Integer value; otherwise, the result is null.
*/
object ToIntegerEvaluator {
    /** The format ToInteger accepts: an optional polarity indicator followed by digits. */
    private val INTEGER_FORMAT = Regex("[+-]?[0-9]+")

    /**
     * Parses [value] as an Integer, or returns null if it is not formatted correctly or out of
     * range.
     */
    internal fun parse(value: kotlin.String): Int? =
        if (INTEGER_FORMAT.matches(value)) value.toIntOrNull() else null

    @JvmStatic
    fun toInteger(operand: Value?): Integer? {
        if (operand == null) {
            return null
        }

        if (operand is Boolean) {
            return (if (operand.value) 1 else 0).toCqlInteger()
        }

        if (operand is Integer) {
            return operand
        }

        if (operand is Long) {
            val value = operand.value
            return if (value in Int.MIN_VALUE..Int.MAX_VALUE) value.toInt().toCqlInteger() else null
        }

        if (operand is String) {
            return parse(operand.value)?.toCqlInteger()
        }

        throw InvalidOperatorArgument(
            "ToInteger(String), ToInteger(Long) or ToInteger(Boolean)",
            "ToInteger(${operand.typeAsString})",
        )
    }
}
