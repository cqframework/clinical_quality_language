package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument
import org.opencds.cqf.cql.engine.runtime.CqlType
import org.opencds.cqf.cql.engine.runtime.String
import org.opencds.cqf.cql.engine.runtime.toCqlString

/*
Lower(argument String) String

The Lower operator returns the lower case of its argument.
If the argument is null, the result is null.
*/
object LowerEvaluator {
    @JvmStatic
    fun lower(operand: CqlType?): String? {
        if (operand == null) {
            return null
        }

        if (operand is String) {
            return operand.value.lowercase().toCqlString()
        }

        throw InvalidOperatorArgument("Lower(String)", "Lower(${operand.typeAsString})")
    }
}
