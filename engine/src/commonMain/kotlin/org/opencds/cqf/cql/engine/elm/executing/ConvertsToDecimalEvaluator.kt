package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument
import org.opencds.cqf.cql.engine.runtime.Boolean
import org.opencds.cqf.cql.engine.runtime.Decimal
import org.opencds.cqf.cql.engine.runtime.Integer
import org.opencds.cqf.cql.engine.runtime.String
import org.opencds.cqf.cql.engine.runtime.Value
import org.opencds.cqf.cql.engine.runtime.toCqlBoolean

/*

    ConvertsToDecimal(argument String) Boolean

    The ToDecimal operator returns true if its argument is or can be converted to a Decimal value. See the ToDecimal operator
        for a description of the supported conversions.

    If the input string is not formatted correctly, or cannot be interpreted as a valid Decimal value, the result is false.

    If the argument is null, the result is null.

*/
object ConvertsToDecimalEvaluator {
    @JvmStatic
    fun convertsToDecimal(argument: Value?): Boolean? {
        if (argument == null) {
            return null
        }

        if (argument is Boolean) {
            return Boolean.TRUE
        }

        if (argument is Integer) {
            return Boolean.TRUE
        }

        if (argument is Decimal) {
            return Boolean.TRUE
        }

        if (argument is String) {
            return (ToDecimalEvaluator.parse(argument.value) != null).toCqlBoolean()
        }

        throw InvalidOperatorArgument(
            "ConvertsToDecimal(String)",
            "ConvertsToDecimal(${argument.typeAsString})",
        )
    }
}
