package org.opencds.cqf.cql.engine.elm.executing

import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument

/*
xor (left Boolean, right Boolean) Boolean

The xor (exclusive or) operator returns true if one argument is true and the other is false.
If both arguments are true or both arguments are false, the result is false. Otherwise, the result is null.
*/
object XorEvaluator {
    @JvmStatic
    fun xor(left: Any?, right: Any?): Any? {
        if (left == null || right == null) {
            return null
        }

        if (left is Boolean && right is Boolean) {
            return (left xor right)
        }

        throw InvalidOperatorArgument(
            "Xor(Boolean, Boolean)",
            String.format("Xor(%s, %s)", left.javaClass.name, right.javaClass.name),
        )
    }
}
