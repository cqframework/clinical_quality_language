package org.opencds.cqf.cql.engine.elm.executing

import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument

/*
 * StartsWith(argument String, prefix String) Boolean
 *
 * The StartsWith operator returns true if the given string starts with the given prefix.
 *
 * If the prefix is the empty string, the result is true.
 *
 * If either argument is null, the result is null.
 */
object StartsWithEvaluator {
    @JvmStatic
    fun startsWith(argument: Any?, prefix: Any?): Any? {
        if (argument == null || prefix == null) {
            return null
        }

        if (argument is String && prefix is String) {
            return argument.startsWith(prefix)
        }

        throw InvalidOperatorArgument(
            "StartsWith(String, String)",
            String.format("StartsWith(%s, %s)", argument.javaClass.name, prefix.javaClass.name),
        )
    }
}
