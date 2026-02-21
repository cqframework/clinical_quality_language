package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.cqframework.cql.shared.BigDecimal
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument
import org.opencds.cqf.cql.engine.runtime.*
import org.opencds.cqf.cql.engine.util.javaClassName

/*

    ConvertsToString(argument Boolean) Boolean
    ConvertsToString(argument Integer) Boolean
    ConvertsToString(argument Long) Boolean
    ConvertsToString(argument Decimal) Boolean
    ConvertsToString(argument Quantity) Boolean
    ConvertsToString(argument Ratio) Boolean
    ConvertsToString(argument Date) Boolean
    ConvertsToString(argument DateTime) Boolean
    ConvertsToString(argument Time) Boolean
    Description:

    The ConvertsToString operator returns true if its argument is or can be converted to a String value. See the ToString operator
        for a description of the supported conversions.

    If the argument is null, the result is null.

*/
object ConvertsToStringEvaluator {
    @JvmStatic
    fun convertsToString(argument: Any?): Boolean? {
        if (argument == null) {
            return null
        }

        if (
            argument is Boolean ||
                argument is Int ||
                argument is Long ||
                argument is BigDecimal ||
                argument is Quantity ||
                argument is Ratio ||
                argument is Date ||
                argument is DateTime ||
                argument is String ||
                argument is Time
        ) {
            return true
        }

        throw InvalidOperatorArgument(
            "ConvertsToString(Boolean) or ConvertsToString(Long) or ConvertsToString(Integer) or ConvertsToString(Decimal) or ConvertsToString(Quantity) or ConvertsToString(Ratio) or ConvertsToString(Date) or ConvertsToString(DateTime) or ConvertsToString(Time)",
            "ConvertsToString(${argument.javaClassName})",
        )
    }
}
