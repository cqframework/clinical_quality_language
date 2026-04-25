package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument
import org.opencds.cqf.cql.engine.execution.State
import org.opencds.cqf.cql.engine.runtime.Boolean
import org.opencds.cqf.cql.engine.runtime.CqlType
import org.opencds.cqf.cql.engine.runtime.Decimal
import org.opencds.cqf.cql.engine.runtime.Integer
import org.opencds.cqf.cql.engine.runtime.Quantity
import org.opencds.cqf.cql.engine.runtime.Ratio
import org.opencds.cqf.cql.engine.runtime.String

/*

    ConvertsToQuantity(argument Decimal) Boolean
    ConvertsToQuantity(argument Integer) Boolean
    ConvertsToQuantity(argument Ratio) Boolean
    ConvertsToQuantity(argument String) Boolean

    The ConvertsToQuantity operator returns true if its argument is or can be converted to a Quantity value. See the ToQuantity
        operator for a description of the supported conversions.

    If the input string is not formatted correctly, or cannot be interpreted as a valid Quantity value, the result is false.

    If the argument is null, the result is null.

*/
object ConvertsToQuantityEvaluator {
    @JvmStatic
    fun convertsToQuantity(argument: CqlType?, state: State?): Boolean? {
        if (argument == null) {
            return null
        }

        if (argument is Quantity) {
            return Boolean.TRUE
        }

        if (argument is String || argument is Ratio || argument is Decimal || argument is Integer) {
            try {
                val response = ToQuantityEvaluator.toQuantity(argument, state)
                if (response == null) {
                    return Boolean.FALSE
                }
            } catch (e: Exception) {
                return Boolean.FALSE
            }
            return Boolean.TRUE
        }

        throw InvalidOperatorArgument(
            "ConvertsToQuantity(String) or ConvertsToQuantity(Ratio) or ConvertsToQuantity(Integer) or ConvertsToQuantity(Decimal)",
            "ConvertsToQuantity(${argument.typeAsString})",
        )
    }
}
