package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.cqframework.cql.shared.BigDecimal
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument
import org.opencds.cqf.cql.engine.exception.UndefinedResult
import org.opencds.cqf.cql.engine.runtime.CqlType
import org.opencds.cqf.cql.engine.runtime.Decimal
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
    fun ln(operand: CqlType?): Decimal? {
        if (operand == null) {
            return null
        }

        if (operand is Decimal) {
            val retVal: BigDecimal?
            try {
                retVal = BigDecimal(kotlin.math.ln(operand.value.toDouble()))
            } catch (nfe: NumberFormatException) {
                if (operand.value.compareTo(BigDecimal(0)) < 0) {
                    return null
                } else if (operand.value.compareTo(BigDecimal(0)) == 0) {
                    throw UndefinedResult("Results in negative infinity")
                } else {
                    throw UndefinedResult(nfe.message)
                }
            }
            return Value.verifyPrecision(retVal, null).toCqlDecimal()
        }

        throw InvalidOperatorArgument("Ln(Decimal)", "Ln(${operand.typeAsString})")
    }
}
