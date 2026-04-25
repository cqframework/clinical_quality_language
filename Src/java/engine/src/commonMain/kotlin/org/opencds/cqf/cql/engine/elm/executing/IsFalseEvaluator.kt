package org.opencds.cqf.cql.engine.elm.executing

import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument
import org.opencds.cqf.cql.engine.runtime.Boolean
import org.opencds.cqf.cql.engine.runtime.CqlType
import org.opencds.cqf.cql.engine.runtime.toCqlBoolean

/*
is false(argument Boolean) Boolean

The is false operator determines whether or not its argument evaluates to false.
If the argument evaluates to false, the result is true; otherwise, the result is false.
*/

object IsFalseEvaluator {
    fun isFalse(operand: CqlType?): Boolean {
        if (operand is Boolean?) {
            return (operand?.value == false).toCqlBoolean()
        }

        throw InvalidOperatorArgument("IsFalse(Boolean)", "IsFalse(${operand.typeAsString})")
    }
}
