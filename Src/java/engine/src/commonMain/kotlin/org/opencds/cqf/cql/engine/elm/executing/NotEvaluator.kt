package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument
import org.opencds.cqf.cql.engine.util.javaClassName

/*
not (argument Boolean) Boolean

The not operator returns true if the argument is false and false if the argument is true. Otherwise, the result is null.
*/
object NotEvaluator {
    @JvmStatic
    fun not(operand: Any?): Boolean? {
        if (operand == null) {
            return null
        }

        if (operand is Boolean) {
            return !operand
        }

        throw InvalidOperatorArgument("Not(Boolean)", "Not(${operand.javaClassName})")
    }
}
