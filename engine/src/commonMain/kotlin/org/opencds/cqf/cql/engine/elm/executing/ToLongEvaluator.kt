package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument
import org.opencds.cqf.cql.engine.runtime.Boolean
import org.opencds.cqf.cql.engine.runtime.Integer
import org.opencds.cqf.cql.engine.runtime.Long
import org.opencds.cqf.cql.engine.runtime.String
import org.opencds.cqf.cql.engine.runtime.Value
import org.opencds.cqf.cql.engine.runtime.toCqlLong

/*
ToLong(argument String) Long

The ToLong operator converts the value of its argument to an Long value.
The operator accepts strings using the following format:
  (+|-)?#0
Meaning an optional polarity indicator, followed by any number of digits (including none), followed by at least one digit.
Note that the integer value returned by this operator must be a valid value in the range representable for Long values in CQL.
If the input string is not formatted correctly, or cannot be interpreted as a valid long value, the result is null.
If the argument is null, the result is null.
*/
object ToLongEvaluator {
    /** The format ToLong accepts: an optional polarity indicator followed by digits. */
    private val LONG_FORMAT = Regex("[+-]?[0-9]+")

    /**
     * Parses [value] as a Long, or returns null if it is not formatted correctly or out of range.
     */
    internal fun parse(value: kotlin.String): kotlin.Long? =
        if (LONG_FORMAT.matches(value)) value.toLongOrNull() else null

    @JvmStatic
    fun toLong(operand: Value?): Long? {
        if (operand == null) {
            return null
        }

        if (operand is Boolean) {
            return (if (operand.value) 1L else 0L).toCqlLong()
        }

        if (operand is Integer) {
            return operand.value.toLong().toCqlLong()
        }

        if (operand is String) {
            return parse(operand.value)?.toCqlLong()
        }

        throw InvalidOperatorArgument("ToLong(String)", "ToLong(${operand.typeAsString})")
    }
}
