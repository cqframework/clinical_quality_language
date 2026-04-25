package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument
import org.opencds.cqf.cql.engine.runtime.CqlType
import org.opencds.cqf.cql.engine.runtime.String
import org.opencds.cqf.cql.engine.runtime.toCqlString

object ReplaceMatchesEvaluator {
    @JvmStatic
    fun replaceMatches(argument: CqlType?, pattern: CqlType?, substitution: CqlType?): String? {
        if (argument == null || pattern == null || substitution == null) {
            return null
        }

        if (argument is String && pattern is String && substitution is String) {

            return argument.replace(pattern.value.toRegex(), substitution.value).toCqlString()
        }

        throw InvalidOperatorArgument(
            "ReplaceMatches(String, String, String)",
            "ReplaceMatches(${argument.typeAsString}, ${pattern.typeAsString}, ${substitution.typeAsString})",
        )
    }
}
