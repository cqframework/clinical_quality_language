package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.cqframework.cql.cql2elm.ucum.UcumService
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument
import org.opencds.cqf.cql.engine.runtime.Boolean
import org.opencds.cqf.cql.engine.runtime.Quantity
import org.opencds.cqf.cql.engine.runtime.String
import org.opencds.cqf.cql.engine.runtime.Value
import org.opencds.cqf.cql.engine.runtime.toCqlBoolean

/*
    CanConvertQuantity(argument Quantity, unit String) Boolean

    The CanConvertQuantity operator returns true if the Quantity is or can be converted to an equivalent Quantity with
        the given Unit. Otherwise, the result is false.

    Note that implementations are not required to support quantity conversion, and so may return false, even if the
        conversion is valid. Implementations that do support unit conversion shall do so according to the conversion
        specified by UCUM.

    If either argument is null, the result is null.

    define CanConvertQuantity: CanConvertQuantity(5 'mg', 'g') // true
*/
object CanConvertQuantityEvaluator {
    @JvmStatic
    fun canConvertQuantity(argument: Value?, unit: Value?, ucumService: UcumService?): Boolean? {
        if (argument == null || unit == null) {
            return null
        }

        if (argument is Quantity && unit is String) {
            // The quantity can be converted if ConvertQuantity can convert it. Without a UCUM
            // service,
            // ConvertQuantity returns null, so the result is false.
            return (ConvertQuantityEvaluator.convertQuantity(argument, unit, ucumService) != null)
                .toCqlBoolean()
        }

        throw InvalidOperatorArgument(
            "CanConvertQuantity(Quantity, String)",
            "CanConvertQuantity(${argument.typeAsString}, ${unit.typeAsString})",
        )
    }
}
