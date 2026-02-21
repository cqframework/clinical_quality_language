package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument
import org.opencds.cqf.cql.engine.runtime.Value
import org.opencds.cqf.cql.engine.util.javaClassName

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
    fun toLong(operand: Any?): Any? {
        if (operand == null) {
            return null
        }

        if (operand is Boolean) {
            return if (operand) 1 else 0
        }

        if (operand is Int) {
            return operand.toLong()
        }

        if (operand is String) {
            try {
                return operand.toLong()
            } catch (nfe: NumberFormatException) {
                try {
                    val ret = operand.toDouble()
                    if (Value.validateLong(ret) == null) {
                        return null
                    }
                    return ret.toLong()
                } catch (e: NumberFormatException) {
                    return null
                }
            }
        }

        throw InvalidOperatorArgument("ToLong(String)", "ToLong(${operand.javaClassName})")
    }
}
