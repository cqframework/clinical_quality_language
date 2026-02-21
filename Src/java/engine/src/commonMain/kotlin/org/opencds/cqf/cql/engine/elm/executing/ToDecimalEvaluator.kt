package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.cqframework.cql.shared.BigDecimal
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument
import org.opencds.cqf.cql.engine.runtime.Value
import org.opencds.cqf.cql.engine.util.javaClassName

/*

ToDecimal(argument String) Decimal

The ToDecimal operator converts the value of its argument to a Decimal value.
The operator accepts strings using the following format:
  (+|-)?#0(.0#)?
Meaning an optional polarity indicator, followed by any number of digits (including none), followed by at least one digit,
  followed optionally by a decimal point, at least one digit, and any number of additional digits (including none).
Note that the decimal value returned by this operator must be limited in precision and scale to the maximum precision and
  scale representable for Decimal values within CQL.
If the input string is not formatted correctly, or cannot be interpreted as a valid Decimal value, the result is null.
If the argument is null, the result is null.

*/
object ToDecimalEvaluator {
    @JvmStatic
    fun toDecimal(operand: Any?): Any? {
        if (operand == null) {
            return null
        }

        if (operand is Boolean) {
            return if (operand) BigDecimal("1.0") else BigDecimal("0.0")
        }

        if (operand is BigDecimal) {
            return operand
        }

        if (operand is Int) {
            return BigDecimal(operand)
        }

        if (operand is Long) {
            return BigDecimal(operand)
        }

        if (operand is String) {
            try {
                if (operand.contains(".")) {
                    val decimalSplit: Array<String?> =
                        operand.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    if (
                        (decimalSplit[0]!!.contains("-") || decimalSplit[0]!!.contains("+")) &&
                            decimalSplit[0]!!.length == 1
                    ) {
                        return null
                    } else if (decimalSplit[0]!!.length == 0) {
                        return null
                    }
                }
                return Value.validateDecimal(BigDecimal(operand), null)
            } catch (nfe: NumberFormatException) {
                return null
            }
        }

        throw InvalidOperatorArgument("ToDecimal(String)", "ToDecimal(${operand.javaClassName})")
    }
}
