package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.cqframework.cql.shared.BigDecimal
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument
import org.opencds.cqf.cql.engine.runtime.Decimal
import org.opencds.cqf.cql.engine.runtime.DecimalHelper
import org.opencds.cqf.cql.engine.runtime.Value
import org.opencds.cqf.cql.engine.runtime.toCqlDecimal

/*
Ln(argument Decimal) Decimal

The Ln operator computes the natural logarithm of its argument.
When invoked with an Integer argument, the argument will be implicitly converted to Decimal.
If the argument is null, the result is null.
*/
object LnEvaluator {
    @JvmStatic
    fun ln(operand: Value?): Decimal? {
        if (operand == null) {
            return null
        }

        if (operand is Decimal) {
            val result = kotlin.math.ln(operand.value.toDouble())
            // If the result cannot be represented as a Decimal (Ln(0) is negative infinity and Ln
            // of a negative number is undefined), the spec requires the result to be null.
            if (result.isInfinite() || result.isNaN()) {
                return null
            }
            return DecimalHelper.verifyPrecision(BigDecimal(result), null).toCqlDecimal()
        }

        throw InvalidOperatorArgument("Ln(Decimal)", "Ln(${operand.typeAsString})")
    }
}
