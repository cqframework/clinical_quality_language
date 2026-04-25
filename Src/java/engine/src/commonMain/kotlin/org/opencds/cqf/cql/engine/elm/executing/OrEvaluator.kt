package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument
import org.opencds.cqf.cql.engine.runtime.Boolean
import org.opencds.cqf.cql.engine.runtime.CqlType
import org.opencds.cqf.cql.engine.runtime.toCqlBoolean

/*
or (left Boolean, right Boolean) Boolean

The or operator returns true if either of its arguments are true.
If both arguments are false, the result is false. Otherwise, the result is null.
*/
object OrEvaluator {
    @JvmStatic
    fun or(left: CqlType?, right: CqlType?): Boolean? {
        if (left == null && right == null) {
            return null
        }

        if (left == null && right is Boolean) {
            return if (right.value) Boolean.TRUE else null
        }

        if (right == null && left is Boolean) {
            return if (left.value) Boolean.TRUE else null
        }

        if (left is Boolean && right is Boolean) {
            return (left.value || right.value).toCqlBoolean()
        }

        throw InvalidOperatorArgument(
            "Or(Boolean, Boolean)",
            "Or(${if (left == null) "Null" else left.typeAsString}, ${if (right == null) "Null" else right.typeAsString})",
        )
    }
}
