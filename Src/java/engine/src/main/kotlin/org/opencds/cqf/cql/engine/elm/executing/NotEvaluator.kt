package org.opencds.cqf.cql.engine.elm.executing

import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument

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

        throw InvalidOperatorArgument("Not(Boolean)", "Not(${operand.javaClass.name})")
    }
}
