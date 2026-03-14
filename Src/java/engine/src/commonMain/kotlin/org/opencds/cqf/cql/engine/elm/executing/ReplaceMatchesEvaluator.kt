package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic

object ReplaceMatchesEvaluator {
    @JvmStatic
    fun replaceMatches(argument: String?, pattern: String?, substitution: String?): Any? {
        if (argument == null || pattern == null || substitution == null) {
            return null
        }

        return argument.replace(pattern.toRegex(), substitution)
    }
}
