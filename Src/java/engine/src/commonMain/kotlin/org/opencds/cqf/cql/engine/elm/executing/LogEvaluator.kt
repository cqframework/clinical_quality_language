package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import kotlin.math.ln
import org.cqframework.cql.shared.BigDecimal
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument
import org.opencds.cqf.cql.engine.runtime.CqlType
import org.opencds.cqf.cql.engine.runtime.Decimal
import org.opencds.cqf.cql.engine.runtime.Value
import org.opencds.cqf.cql.engine.runtime.toCqlDecimal

/*
Log(argument Decimal, base Decimal) Decimal

The Log operator computes the logarithm of its first argument, using the second argument as the base.
When invoked with Integer arguments, the arguments will be implicitly converted to Decimal.
If either argument is null, the result is null.
*/
object LogEvaluator {
    @JvmStatic
    fun log(left: CqlType?, right: CqlType?): Decimal? {
        if (left == null || right == null) {
            return null
        }

        if (left is Decimal && right is Decimal) {
            val base = right.value.toDouble()
            val argument = left.value.toDouble()

            // Logarithm is not defined for base 1.
            if (base == 1.0) {
                return null
            }

            return Value.verifyPrecision(BigDecimal(ln(argument) / ln(base)), null).toCqlDecimal()
        }

        throw InvalidOperatorArgument(
            "Log(Decimal, Decimal)",
            "Log(${left.typeAsString}, ${right.typeAsString})",
        )
    }
}
