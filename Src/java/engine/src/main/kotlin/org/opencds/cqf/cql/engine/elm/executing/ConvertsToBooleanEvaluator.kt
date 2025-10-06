package org.opencds.cqf.cql.engine.elm.executing

import java.math.BigDecimal
import java.util.Locale
import org.apache.commons.lang3.ArrayUtils
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument

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
    fun convertsToBoolean(argument: Any?): Boolean? {
        if (argument == null) {
            return null
        }

        if (argument is Boolean) {
            return true
        }

        if (argument is Int) {
            val value = argument
            return (value == 0 || value == 1)
        }

        if (argument is BigDecimal) {
            val value = argument
            return (value.compareTo(BigDecimal("1.0")) == 0 ||
                value.compareTo(BigDecimal("0.0")) == 0)
        }

        if (argument is String) {
            return ArrayUtils.contains(validTrueValues, argument.lowercase(Locale.getDefault())) ||
                ArrayUtils.contains(validFalseValues, argument.lowercase(Locale.getDefault()))
        }

        throw InvalidOperatorArgument(
            "ConvertsToBoolean(String)",
            "ConvertsToBoolean(${argument.javaClass.name})",
        )
    }
}
