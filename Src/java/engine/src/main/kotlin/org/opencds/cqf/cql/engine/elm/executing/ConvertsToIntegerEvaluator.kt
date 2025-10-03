package org.opencds.cqf.cql.engine.elm.executing

import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument

/*

    ConvertsToInteger(argument String) Boolean

    The ConvertsToInteger operator returns true if its argument is or can be converted to an Integer value. See the ToInteger
        operator for a description of the supported conversions.

    If the input string is not formatted correctly, or cannot be interpreted as a valid Integer value, the result is false.

*/
object ConvertsToIntegerEvaluator {
    @JvmStatic
    fun convertsToInteger(argument: Any?): Boolean? {
        if (argument == null) {
            return null
        }

        if (argument is Boolean) {
            return true
        }

        if (argument is Int) {
            return true
        }

        if (argument is Long) {
            return true
        }

        if (argument is String) {
            try {
                argument.toInt()
            } catch (nfe: NumberFormatException) {
                return false
            }
            return true
        }

        throw InvalidOperatorArgument(
            "ConvertsToInteger(String)",
            String.format("ConvertsToInteger(%s)", argument.javaClass.name),
        )
    }
}
