package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument
import org.opencds.cqf.cql.engine.runtime.CqlType
import org.opencds.cqf.cql.engine.runtime.String
import org.opencds.cqf.cql.engine.runtime.toCqlString

/*
Upper(argument String) String

The Upper operator returns the upper case of its argument.
If the argument is null, the result is null.
*/
object UpperEvaluator {
    @JvmStatic
    fun upper(operand: CqlType?): String? {
        if (operand == null) {
            return null
        }

        if (operand is String) {
            return operand.value.uppercase().toCqlString()
        }

        throw InvalidOperatorArgument("Upper(String)", "Upper(${operand.typeAsString})")
    }
}
