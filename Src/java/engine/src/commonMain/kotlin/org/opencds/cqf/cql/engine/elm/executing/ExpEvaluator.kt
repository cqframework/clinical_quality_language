package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.cqframework.cql.shared.BigDecimal
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument
import org.opencds.cqf.cql.engine.exception.UndefinedResult
import org.opencds.cqf.cql.engine.util.javaClassName

/*
Exp(argument Decimal) Decimal

The Exp operator raises e to the power of its argument.
When invoked with an Integer argument, the argument will be implicitly converted to Decimal.
If the argument is null, the result is null.
*/
object ExpEvaluator {
    @JvmStatic
    fun exp(operand: Any?): Any? {
        if (operand == null) {
            return null
        }

        if (operand is BigDecimal) {
            try {
                return BigDecimal(kotlin.math.exp(operand.toDouble()))
            } catch (nfe: NumberFormatException) {
                if (operand.compareTo(BigDecimal(0)) > 0) {
                    throw UndefinedResult("Results in positive infinity")
                } else if (operand.compareTo(BigDecimal(0)) < 0) {
                    throw UndefinedResult("Results in negative infinity")
                } else {
                    throw UndefinedResult(nfe.message)
                }
            }
        }

        throw InvalidOperatorArgument("Exp(Decimal)", "Exp(${operand.javaClassName})")
    }
}
