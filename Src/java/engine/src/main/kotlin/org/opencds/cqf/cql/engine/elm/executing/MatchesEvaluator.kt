package org.opencds.cqf.cql.engine.elm.executing

object MatchesEvaluator {
    @JvmStatic
    fun matches(argument: String?, pattern: String?): Any? {
        if (argument == null || pattern == null) {
            return null
        }

        return argument.matches(pattern.toRegex())
    }
}
