package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument
import org.opencds.cqf.cql.engine.util.javaClassName

/*
or (left Boolean, right Boolean) Boolean

The or operator returns true if either of its arguments are true.
If both arguments are false, the result is false. Otherwise, the result is null.
*/
object OrEvaluator {
    @JvmStatic
    fun or(left: Any?, right: Any?): Boolean? {
        if (left == null && right == null) {
            return null
        }

        if (left == null && right is Boolean) {
            return if (right) true else null
        }

        if (right == null && left is Boolean) {
            return if (left) true else null
        }

        if (left is Boolean && right is Boolean) {
            return left || right
        }

        throw InvalidOperatorArgument(
            "Or(Boolean, Boolean)",
            "Or(${if (left == null) "Null" else left.javaClassName}, ${if (right == null) "Null" else right.javaClassName})",
        )
    }
}
