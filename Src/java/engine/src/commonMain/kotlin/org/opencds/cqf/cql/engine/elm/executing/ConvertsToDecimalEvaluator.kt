package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.cqframework.cql.shared.BigDecimal
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument
import org.opencds.cqf.cql.engine.util.javaClassName

/*

    ConvertsToDecimal(argument String) Boolean

    The ToDecimal operator returns true if its argument is or can be converted to a Decimal value. See the ToDecimal operator
        for a description of the supported conversions.

    If the input string is not formatted correctly, or cannot be interpreted as a valid Decimal value, the result is false.

    If the argument is null, the result is null.

*/
object ConvertsToDecimalEvaluator {
    @JvmStatic
    fun convertsToDecimal(argument: Any?): Boolean? {
        if (argument == null) {
            return null
        }

        if (argument is Boolean) {
            return true
        }

        if (argument is Int) {
            return true
        }

        if (argument is BigDecimal) {
            return true
        }

        if (argument is String) {
            try {
                argument.toDouble()
            } catch (nfe: NumberFormatException) {
                return false
            }
            return true
        }

        throw InvalidOperatorArgument(
            "ConvertsToDecimal(String)",
            "ConvertsToDecimal(${argument.javaClassName})",
        )
    }
}
