package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument
import org.opencds.cqf.cql.engine.runtime.Boolean
import org.opencds.cqf.cql.engine.runtime.Date
import org.opencds.cqf.cql.engine.runtime.DateTime
import org.opencds.cqf.cql.engine.runtime.Decimal
import org.opencds.cqf.cql.engine.runtime.Integer
import org.opencds.cqf.cql.engine.runtime.Long
import org.opencds.cqf.cql.engine.runtime.Quantity
import org.opencds.cqf.cql.engine.runtime.Ratio
import org.opencds.cqf.cql.engine.runtime.String
import org.opencds.cqf.cql.engine.runtime.Time
import org.opencds.cqf.cql.engine.runtime.Value

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
    fun convertsToString(argument: Value?): Boolean? {
        if (argument == null) {
            return null
        }

        if (
            argument is Boolean ||
                argument is Integer ||
                argument is Long ||
                argument is Decimal ||
                argument is Quantity ||
                argument is Ratio ||
                argument is Date ||
                argument is DateTime ||
                argument is String ||
                argument is Time
        ) {
            return Boolean.TRUE
        }

        throw InvalidOperatorArgument(
            "ConvertsToString(Boolean) or ConvertsToString(Long) or ConvertsToString(Integer) or ConvertsToString(Decimal) or ConvertsToString(Quantity) or ConvertsToString(Ratio) or ConvertsToString(Date) or ConvertsToString(DateTime) or ConvertsToString(Time)",
            "ConvertsToString(${argument.typeAsString})",
        )
    }
}
