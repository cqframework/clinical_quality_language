package org.opencds.cqf.cql.engine.elm.executing

import java.util.Locale
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument

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
            return operand.lowercase(Locale.getDefault())
        }

        throw InvalidOperatorArgument(
            "Lower(String)",
            String.format("Lower(%s)", operand.javaClass.name),
        )
    }
}
