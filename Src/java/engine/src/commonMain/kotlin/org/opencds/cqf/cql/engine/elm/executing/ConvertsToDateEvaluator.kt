package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument
import org.opencds.cqf.cql.engine.runtime.Boolean
import org.opencds.cqf.cql.engine.runtime.CqlType
import org.opencds.cqf.cql.engine.runtime.Date
import org.opencds.cqf.cql.engine.runtime.String

/*

    ConvertsToDate(argument String) Boolean

    The ConvertsToDate operator returns true if its argument is or can be converted to a Date value. See the ToDate operator
        for a description of the supported conversions.

    If the input string is not formatted correctly, or does not represent a valid date value, the result is false.

    As with date literals, date values may be specified to any precision.

    If the argument is null, the result is null.

*/
object ConvertsToDateEvaluator {
    @JvmStatic
    fun convertsToDate(argument: CqlType?): Boolean? {
        if (argument == null) {
            return null
        }

        if (argument is Date) {
            return Boolean.TRUE
        }

        if (argument is String) {
            try {
                Date(argument.value)
            } catch (e: Exception) {
                return Boolean.FALSE
            }
            return Boolean.TRUE
        }

        throw InvalidOperatorArgument(
            "ConvertsToDate(String)",
            "ConvertsToDate(${argument.typeAsString})",
        )
    }
}
