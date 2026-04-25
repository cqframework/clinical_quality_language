package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument
import org.opencds.cqf.cql.engine.runtime.Boolean
import org.opencds.cqf.cql.engine.runtime.CqlType
import org.opencds.cqf.cql.engine.runtime.Integer
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
*/
object ToIntegerEvaluator {
    @JvmStatic
    fun toInteger(operand: CqlType?): Integer? {
        if (operand == null) {
            return null
        }

        if (operand is Boolean) {
            return (if (operand.value) 1 else 0).toCqlInteger()
        }

        if (operand is Integer) {
            return operand
        }

        if (operand is String) {
            try {
                return operand.value.toInt().toCqlInteger()
            } catch (nfe: NumberFormatException) {
                try {
                    val ret = operand.value.toDouble()
                    if (Value.validateInteger(ret) == null) {
                        return null
                    }
                    return ret.toInt().toCqlInteger()
                } catch (e: NumberFormatException) {
                    return null
                }
            }
        }

        throw InvalidOperatorArgument("ToInteger(String)", "ToInteger(${operand.typeAsString})")
    }
}
