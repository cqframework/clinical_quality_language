package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument
import org.opencds.cqf.cql.engine.runtime.Boolean
import org.opencds.cqf.cql.engine.runtime.CqlType
import org.opencds.cqf.cql.engine.runtime.toCqlBoolean

/*
not (argument Boolean) Boolean

The not operator returns true if the argument is false and false if the argument is true. Otherwise, the result is null.
*/
object NotEvaluator {
    @JvmStatic
    fun not(operand: CqlType?): Boolean? {
        if (operand == null) {
            return null
        }

        if (operand is Boolean) {
            return (!operand.value).toCqlBoolean()
        }

        throw InvalidOperatorArgument("Not(Boolean)", "Not(${operand.typeAsString})")
    }
}
