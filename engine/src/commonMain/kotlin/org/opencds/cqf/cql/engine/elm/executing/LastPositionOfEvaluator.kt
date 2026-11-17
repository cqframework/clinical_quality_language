package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument
import org.opencds.cqf.cql.engine.runtime.Integer
import org.opencds.cqf.cql.engine.runtime.String
import org.opencds.cqf.cql.engine.runtime.Value
import org.opencds.cqf.cql.engine.runtime.toCqlInteger

/*
 * LastPositionOf(pattern String, argument String) Integer
 *
 * The LastPositionOf operator returns the 0-based index of the last appearance of the given pattern in the given string.
 *
 * If the pattern is not found, the result is -1.
 *
 * If either argument is null, the result is null.
 */
object LastPositionOfEvaluator {
    @JvmStatic
    fun lastPositionOf(string: Value?, pattern: Value?): Integer? {
        if (pattern == null || string == null) {
            return null
        }

        if (pattern is String) {
            val argument = (string as String).value
            // Search from the end of the string, so an empty pattern is found at its length
            return argument.lastIndexOf(pattern.value, argument.length).toCqlInteger()
        }

        throw InvalidOperatorArgument(
            "LastPositionOf(String, String)",
            "LastPositionOf(${pattern.typeAsString}, ${string.typeAsString})",
        )
    }
}
