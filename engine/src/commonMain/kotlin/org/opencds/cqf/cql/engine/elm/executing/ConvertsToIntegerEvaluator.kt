package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument
import org.opencds.cqf.cql.engine.runtime.Boolean
import org.opencds.cqf.cql.engine.runtime.Integer
import org.opencds.cqf.cql.engine.runtime.Long
import org.opencds.cqf.cql.engine.runtime.String
import org.opencds.cqf.cql.engine.runtime.Value
import org.opencds.cqf.cql.engine.runtime.toCqlBoolean

/*

    ConvertsToInteger(argument String) Boolean

    The ConvertsToInteger operator returns true if its argument is or can be converted to an Integer value. See the ToInteger
        operator for a description of the supported conversions.

    If the input string is not formatted correctly, or cannot be interpreted as a valid Integer value, the result is false.

*/
object ConvertsToIntegerEvaluator {
    @JvmStatic
    fun convertsToInteger(argument: Value?): Boolean? {
        if (argument == null) {
            return null
        }

        if (argument is Boolean) {
            return Boolean.TRUE
        }

        if (argument is Integer) {
            return Boolean.TRUE
        }

        if (argument is Long) {
            return (argument.value in Int.MIN_VALUE..Int.MAX_VALUE).toCqlBoolean()
        }

        if (argument is String) {
            return (ToIntegerEvaluator.parse(argument.value) != null).toCqlBoolean()
        }

        throw InvalidOperatorArgument(
            "ConvertsToInteger(String)",
            "ConvertsToInteger(${argument.typeAsString})",
        )
    }
}
