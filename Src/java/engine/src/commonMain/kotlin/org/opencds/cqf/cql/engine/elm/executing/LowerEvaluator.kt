package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument
import org.opencds.cqf.cql.engine.util.javaClassName

/*
Lower(argument String) String

The Lower operator returns the lower case of its argument.
If the argument is null, the result is null.
*/
object LowerEvaluator {
    @JvmStatic
    fun lower(operand: Any?): Any? {
        if (operand == null) {
            return null
        }

        if (operand is String) {
            return operand.lowercase()
        }

        throw InvalidOperatorArgument("Lower(String)", "Lower(${operand.javaClassName})")
    }
}
