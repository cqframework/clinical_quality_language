package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument
import org.opencds.cqf.cql.engine.util.javaClassName

/*

    ConvertsToLong(argument String) Boolean

    The ConvertsToLong operator returns true if its argument is or can be converted to an Long value. See the ToLong
        operator for a description of the supported conversions.

    If the input string is not formatted correctly, or cannot be interpreted as a valid Integer value, the result is false.

*/
object ConvertsToLongEvaluator {
    @JvmStatic
    fun convertsToLong(argument: Any?): Boolean? {
        if (argument == null) {
            return null
        }

        if (argument is Boolean) {
            return true
        }

        if (argument is Int) {
            return true
        }

        if (argument is String) {
            try {
                argument.toLong()
            } catch (nfe: NumberFormatException) {
                return false
            }
            return true
        }

        throw InvalidOperatorArgument(
            "ConvertsToLong(String)",
            "ConvertsToLong(${argument.javaClassName})",
        )
    }
}
