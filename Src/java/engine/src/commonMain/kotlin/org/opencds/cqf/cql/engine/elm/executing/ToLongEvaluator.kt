package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument
import org.opencds.cqf.cql.engine.runtime.Boolean
import org.opencds.cqf.cql.engine.runtime.Constants.MAX_LONG
import org.opencds.cqf.cql.engine.runtime.Constants.MIN_LONG
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
            try {
                return operand.value.toLong().toCqlLong()
            } catch (nfe: NumberFormatException) {
                try {
                    val ret = operand.value.toDouble()
                    if (ret > MAX_LONG || ret < MIN_LONG) {
                        return null
                    }
                    return ret.toLong().toCqlLong()
                } catch (e: NumberFormatException) {
                    return null
                }
            }
        }

        throw InvalidOperatorArgument("ToLong(String)", "ToLong(${operand.typeAsString})")
    }
}
