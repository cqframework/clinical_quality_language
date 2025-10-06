package org.opencds.cqf.cql.engine.elm.executing

import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument

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
    fun lastPositionOf(string: Any?, pattern: Any?): Any? {
        if (pattern == null || string == null) {
            return null
        }

        if (pattern is String) {
            return (string as String).lastIndexOf(pattern)
        }

        throw InvalidOperatorArgument(
            "LastPositionOf(String, String)",
            String.format("LastPositionOf(%s, %s)", pattern.javaClass.name, string.javaClass.name),
        )
    }
}
