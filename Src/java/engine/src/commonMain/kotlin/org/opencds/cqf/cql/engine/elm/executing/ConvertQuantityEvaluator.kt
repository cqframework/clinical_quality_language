package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.cqframework.cql.cql2elm.ucum.UcumService
import org.cqframework.cql.shared.BigDecimal
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument
import org.opencds.cqf.cql.engine.runtime.Quantity
import org.opencds.cqf.cql.engine.util.javaClassName

/*
    convert <quantity> to <unit>
    ConvertQuantity(argument Quantity, unit String)

    The ConvertQuantity operator converts a Quantity to an equivalent Quantity with the given unit. If the unit of the
        input quantity can be converted to the target unit, the result is an equivalent Quantity with the target unit.
        Otherwise, the result is null.

    Note that implementations are not required to support quantity conversion. Implementations that do support unit
        conversion shall do so according to the conversion specified by UCUM. Implementations that do not support unit
        conversion shall throw an error if an unsupported unit conversion is requested with this operation.

    If either argument is null, the result is null.

    define ConvertQuantity: ConvertQuantity(5 'mg', 'g')
    define ConvertSyntax: convert 5 'mg' to 'g'

*/
object ConvertQuantityEvaluator {
    @JvmStatic
    fun convertQuantity(argument: Any?, unit: Any?, ucumService: UcumService?): Any? {
        if (argument == null || unit == null) {
            return null
        }

        if (argument is Quantity) {
            if (ucumService == null) {
                return null
            }
            try {
                val result: BigDecimal =
                    ucumService.convert(argument.value!!, argument.unit!!, unit as String)
                return Quantity().withValue(result).withUnit(unit)
            } catch (e: Exception) {
                return null
            }
        }

        throw InvalidOperatorArgument(
            "ConvertQuantity(Quantity, String)",
            "ConvertQuantity(${argument.javaClassName}, ${unit.javaClassName})",
        )
    }
}
