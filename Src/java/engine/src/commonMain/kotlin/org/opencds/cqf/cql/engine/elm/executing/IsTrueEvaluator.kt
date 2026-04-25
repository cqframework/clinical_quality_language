package org.opencds.cqf.cql.engine.elm.executing

import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument
import org.opencds.cqf.cql.engine.runtime.Boolean
import org.opencds.cqf.cql.engine.runtime.CqlType
import org.opencds.cqf.cql.engine.runtime.toCqlBoolean

/*
is true(argument Boolean) Boolean

The is true operator determines whether or not its argument evaluates to true.
If the argument evaluates to true, the result is true; otherwise, the result is false.
*/

object IsTrueEvaluator {
    fun isTrue(operand: CqlType?): Boolean {
        if (operand is Boolean?) {
            return (operand?.value == true).toCqlBoolean()
        }

        throw InvalidOperatorArgument("IsTrue(Boolean)", "IsTrue(${operand.typeAsString})")
    }
}
