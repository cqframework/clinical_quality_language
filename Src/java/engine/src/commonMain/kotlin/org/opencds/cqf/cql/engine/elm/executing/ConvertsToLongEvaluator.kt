package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument
import org.opencds.cqf.cql.engine.runtime.Boolean
import org.opencds.cqf.cql.engine.runtime.Integer
import org.opencds.cqf.cql.engine.runtime.String
import org.opencds.cqf.cql.engine.runtime.Value

/*

    ConvertsToLong(argument String) Boolean

    The ConvertsToLong operator returns true if its argument is or can be converted to an Long value. See the ToLong
        operator for a description of the supported conversions.

    If the input string is not formatted correctly, or cannot be interpreted as a valid Integer value, the result is false.

*/
object ConvertsToLongEvaluator {
    @JvmStatic
    fun convertsToLong(argument: Value?): Boolean? {
        if (argument == null) {
            return null
        }

        if (argument is Boolean) {
            return Boolean.TRUE
        }

        if (argument is Integer) {
            return Boolean.TRUE
        }

        if (argument is String) {
            try {
                argument.value.toLong()
            } catch (nfe: NumberFormatException) {
                return Boolean.FALSE
            }
            return Boolean.TRUE
        }

        throw InvalidOperatorArgument(
            "ConvertsToLong(String)",
            "ConvertsToLong(${argument.typeAsString})",
        )
    }
}
