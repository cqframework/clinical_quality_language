package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument
import org.opencds.cqf.cql.engine.runtime.Boolean
import org.opencds.cqf.cql.engine.runtime.String
import org.opencds.cqf.cql.engine.runtime.Value
import org.opencds.cqf.cql.engine.runtime.toCqlBoolean

object MatchesEvaluator {
    @JvmStatic
    fun matches(argument: Value?, pattern: Value?): Boolean? {
        if (argument == null || pattern == null) {
            return null
        }

        if (argument is String && pattern is String) {
            // Regular expressions "use partial matching", and "the start/end of line markers ^ and
            // $ can be used to match the entire string" (Matches, CQL 1.5 and later). Kotlin's
            // CharSequence.matches(Regex) only succeeds on a match of the entire input, so use
            // containsMatchIn instead.
            return pattern.value.toRegex().containsMatchIn(argument.value).toCqlBoolean()
        }

        throw InvalidOperatorArgument(
            "Matches(String, String)",
            "Matches(${argument.typeAsString}, ${pattern.typeAsString})",
        )
    }
}
