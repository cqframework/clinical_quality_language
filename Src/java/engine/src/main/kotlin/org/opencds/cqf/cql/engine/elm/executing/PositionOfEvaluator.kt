package org.opencds.cqf.cql.engine.elm.executing

import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument

/*
PositionOf(pattern String, argument String) Integer

The PositionOf operator returns the 0-based index of the given pattern in the given string.
If the pattern is not found, the result is -1.
If either argument is null, the result is null.
*/
object PositionOfEvaluator {
    @JvmStatic
    fun positionOf(pattern: Any?, string: Any?): Any? {
        if (pattern == null || string == null) {
            return null
        }

        if (pattern is String) {
            return (string as String).indexOf(pattern)
        }

        throw InvalidOperatorArgument(
            "PositionOf(String, String)",
            String.format("PositionOf(%s, %s)", pattern.javaClass.name, string.javaClass.name),
        )
    }
}
