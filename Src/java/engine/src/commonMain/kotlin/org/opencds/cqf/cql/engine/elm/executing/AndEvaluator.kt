package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument
import org.opencds.cqf.cql.engine.util.javaClassName

/*
and (left Boolean, right Boolean) Boolean

The and operator returns true if both its arguments are true.
If either argument is false, the result is false. Otherwise, the result is null.

The following examples illustrate the behavior of the and operator:
define IsTrue = true and true
define IsFalse = true and false
define IsAlsoFalse = false and null
define IsNull = true and null
*/
object AndEvaluator {
    @JvmStatic
    fun and(left: Any?, right: Any?): Boolean? {
        if (left == null && right == null) {
            return null
        }

        if (left == null && right is Boolean) {
            return if (right) null else false
        }

        if (right == null && left is Boolean) {
            return if (left) null else false
        }

        if (left is Boolean && right is Boolean) {
            return left && right
        }

        throw InvalidOperatorArgument(
            "And(Boolean, Boolean)",
            "And(${if (left == null) "Null" else left.javaClassName}, ${if (right == null) "Null" else right.javaClassName})",
        )
    }
}
