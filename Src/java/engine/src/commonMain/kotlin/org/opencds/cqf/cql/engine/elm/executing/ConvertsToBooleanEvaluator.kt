package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.cqframework.cql.shared.BigDecimal
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument
import org.opencds.cqf.cql.engine.runtime.Boolean
import org.opencds.cqf.cql.engine.runtime.Decimal
import org.opencds.cqf.cql.engine.runtime.Integer
import org.opencds.cqf.cql.engine.runtime.String
import org.opencds.cqf.cql.engine.runtime.Value
import org.opencds.cqf.cql.engine.runtime.toCqlBoolean

/*

    ConvertsToBoolean(argument String) Boolean

    The ConvertsToBoolean operator returns true if its argument is or can be converted to a Boolean value. See the ToBoolean
        operator for a description of the supported conversions.

    If the input cannot be interpreted as a valid Boolean value, the result is false.

    If the argument is null, the result is null.

*/
object ConvertsToBooleanEvaluator {
    private val validTrueValues = arrayOf("true", "t", "yes", "y", "1")
    private val validFalseValues = arrayOf("false", "f", "no", "n", "0")

    @JvmStatic
    fun convertsToBoolean(argument: Value?): Boolean? {
        if (argument == null) {
            return null
        }

        if (argument is Boolean) {
            return Boolean.TRUE
        }

        if (argument is Integer) {
            val value = argument
            return (value.value == 0 || value.value == 1).toCqlBoolean()
        }

        if (argument is Decimal) {
            val value = argument
            return (value.value.compareTo(BigDecimal("1.0")) == 0 ||
                    value.value.compareTo(BigDecimal("0.0")) == 0)
                .toCqlBoolean()
        }

        if (argument is String) {
            return (validTrueValues.contains(argument.value.lowercase()) ||
                    validFalseValues.contains(argument.value.lowercase()))
                .toCqlBoolean()
        }

        throw InvalidOperatorArgument(
            "ConvertsToBoolean(String)",
            "ConvertsToBoolean(${argument.typeAsString})",
        )
    }
}
