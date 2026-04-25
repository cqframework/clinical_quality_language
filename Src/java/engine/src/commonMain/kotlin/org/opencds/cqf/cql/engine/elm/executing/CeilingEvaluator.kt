package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import kotlin.math.ceil
import org.cqframework.cql.shared.BigDecimal
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument
import org.opencds.cqf.cql.engine.runtime.CqlType
import org.opencds.cqf.cql.engine.runtime.Decimal
import org.opencds.cqf.cql.engine.runtime.Integer
import org.opencds.cqf.cql.engine.runtime.Quantity
import org.opencds.cqf.cql.engine.runtime.toCqlInteger

/*
Ceiling(argument Decimal) Integer

The Ceiling operator returns the first integer greater than or equal to the argument.
When invoked with an Integer argument, the argument will be implicitly converted to Decimal.
If the argument is null, the result is null.
*/
object CeilingEvaluator {
    @JvmStatic
    fun ceiling(operand: CqlType?): Integer? {
        if (operand == null) {
            return null
        }

        if (operand is Decimal) {
            return BigDecimal(ceil(operand.value.toDouble())).toInt().toCqlInteger()
        } else if (operand is Quantity) {
            return BigDecimal(ceil(operand.value!!.toDouble())).toInt().toCqlInteger()
        }

        throw InvalidOperatorArgument("Ceiling(Decimal)", "Ceiling(${operand.typeAsString})")
    }
}
