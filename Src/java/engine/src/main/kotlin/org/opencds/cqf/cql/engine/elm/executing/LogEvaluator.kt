package org.opencds.cqf.cql.engine.elm.executing

import java.math.BigDecimal
import kotlin.math.ln
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument
import org.opencds.cqf.cql.engine.runtime.Value

/*
Log(argument Decimal, base Decimal) Decimal

The Log operator computes the logarithm of its first argument, using the second argument as the base.
When invoked with Integer arguments, the arguments will be implicitly converted to Decimal.
If either argument is null, the result is null.
*/
object LogEvaluator {
    @JvmStatic
    fun log(left: Any?, right: Any?): Any? {
        if (left == null || right == null) {
            return null
        }

        if (left is BigDecimal) {
            val base = (right as BigDecimal).toDouble()
            val argument = left.toDouble()

            // Logarithm is not defined for base 1.
            if (base == 1.0) {
                return null
            }

            return Value.verifyPrecision(BigDecimal.valueOf(ln(argument) / ln(base)), null)
        }

        throw InvalidOperatorArgument(
            "Log(Decimal, Decimal)",
            String.format("Log(%s, %s)", left.javaClass.name, right.javaClass.name),
        )
    }
}
