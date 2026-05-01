package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument
import org.opencds.cqf.cql.engine.runtime.Boolean
import org.opencds.cqf.cql.engine.runtime.Value
import org.opencds.cqf.cql.engine.runtime.toCqlBoolean

/*
xor (left Boolean, right Boolean) Boolean

The xor (exclusive or) operator returns true if one argument is true and the other is false.
If both arguments are true or both arguments are false, the result is false. Otherwise, the result is null.
*/
object XorEvaluator {
    @JvmStatic
    fun xor(left: Value?, right: Value?): Boolean? {
        if (left == null || right == null) {
            return null
        }

        if (left is Boolean && right is Boolean) {
            return (left.value xor right.value).toCqlBoolean()
        }

        throw InvalidOperatorArgument(
            "Xor(Boolean, Boolean)",
            "Xor(${left.typeAsString}, ${right.typeAsString})",
        )
    }
}
