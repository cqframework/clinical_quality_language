package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument
import org.opencds.cqf.cql.engine.util.javaClassName

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
            "StartsWith(${argument.javaClassName}, ${prefix.javaClassName})",
        )
    }
}
