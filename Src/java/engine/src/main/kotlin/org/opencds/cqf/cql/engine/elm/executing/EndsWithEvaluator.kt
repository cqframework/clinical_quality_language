package org.opencds.cqf.cql.engine.elm.executing

object EndsWithEvaluator {
    @JvmStatic
    fun endsWith(argument: String?, suffix: String?): Any? {
        if (argument == null || suffix == null) {
            return null
        }
        return argument.endsWith(suffix)
    }
}
