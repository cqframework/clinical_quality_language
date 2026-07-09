package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.cqframework.cql.shared.BigDecimal
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument
import org.opencds.cqf.cql.engine.runtime.Decimal
import org.opencds.cqf.cql.engine.runtime.Value
import org.opencds.cqf.cql.engine.runtime.toCqlDecimal

/*
Exp(argument Decimal) Decimal

The Exp operator raises e to the power of its argument.
When invoked with an Integer argument, the argument will be implicitly converted to Decimal.
If the argument is null, the result is null.
*/
object ExpEvaluator {
    @JvmStatic
    fun exp(operand: Value?): Decimal? {
        if (operand == null) {
            return null
        }

        if (operand is Decimal) {
            val result = kotlin.math.exp(operand.value.toDouble())
            // If the result cannot be represented as a Decimal (i.e. it overflows to infinity),
            // the spec requires the result to be null.
            return if (result.isInfinite() || result.isNaN()) {
                null
            } else {
                BigDecimal(result).toCqlDecimal()
            }
        }

        throw InvalidOperatorArgument("Exp(Decimal)", "Exp(${operand.typeAsString})")
    }
}
