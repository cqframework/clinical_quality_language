package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument
import org.opencds.cqf.cql.engine.util.javaClassName

/*
+(left String, right String) String

The concatenate (+) operator performs string concatenation of its arguments.
If either argument is null, the result is null.
*/
object ConcatenateEvaluator {
    @JvmStatic
    fun concatenate(left: Any?, right: Any?): Any? {
        if (left == null || right == null) {
            return null
        }

        if (left is String && right is String) {
            return left + right
        }

        throw InvalidOperatorArgument(
            "Concatenate(String, String)",
            "Concatenate(${left.javaClassName}, ${right.javaClassName})",
        )
    }
}
