package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument
import org.opencds.cqf.cql.engine.runtime.Boolean
import org.opencds.cqf.cql.engine.runtime.String
import org.opencds.cqf.cql.engine.runtime.Value
import org.opencds.cqf.cql.engine.runtime.toCqlBoolean

object EndsWithEvaluator {
    @JvmStatic
    fun endsWith(argument: Value?, suffix: Value?): Boolean? {
        if (argument == null || suffix == null) {
            return null
        }

        if (argument is String && suffix is String) {
            return argument.endsWith(suffix).toCqlBoolean()
        }

        throw InvalidOperatorArgument(
            "EndsWith(String, String)",
            "EndsWith(${argument.typeAsString}, ${suffix.typeAsString})",
        )
    }
}
