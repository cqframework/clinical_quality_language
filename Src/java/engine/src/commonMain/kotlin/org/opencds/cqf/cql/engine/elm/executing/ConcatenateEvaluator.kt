package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument
import org.opencds.cqf.cql.engine.runtime.CqlType
import org.opencds.cqf.cql.engine.runtime.String
import org.opencds.cqf.cql.engine.runtime.toCqlString

/*
+(left String, right String) String

The concatenate (+) operator performs string concatenation of its arguments.
If either argument is null, the result is null.
*/
object ConcatenateEvaluator {
    @JvmStatic
    fun concatenate(left: CqlType?, right: CqlType?): CqlType? {
        if (left == null || right == null) {
            return null
        }

        if (left is String && right is String) {
            return (left.value + right.value).toCqlString()
        }

        throw InvalidOperatorArgument(
            "Concatenate(String, String)",
            "Concatenate(${left.typeAsString}, ${right.typeAsString})",
        )
    }
}
