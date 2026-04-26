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

            return argument.matches(pattern.value.toRegex()).toCqlBoolean()
        }

        throw InvalidOperatorArgument(
            "Matches(String, String)",
            "Matches(${argument.typeAsString}, ${pattern.typeAsString})",
        )
    }
}
