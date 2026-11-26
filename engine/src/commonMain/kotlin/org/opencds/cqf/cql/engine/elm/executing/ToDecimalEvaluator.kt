package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.cqframework.cql.shared.BigDecimal
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument
import org.opencds.cqf.cql.engine.runtime.Boolean
import org.opencds.cqf.cql.engine.runtime.Decimal
import org.opencds.cqf.cql.engine.runtime.DecimalHelper
import org.opencds.cqf.cql.engine.runtime.Integer
import org.opencds.cqf.cql.engine.runtime.Long
import org.opencds.cqf.cql.engine.runtime.String
import org.opencds.cqf.cql.engine.runtime.Value
import org.opencds.cqf.cql.engine.runtime.toCqlDecimal

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
    /**
     * The format ToDecimal accepts: an optional polarity indicator, digits and an optional
     * fraction.
     */
    private val DECIMAL_FORMAT = Regex("[+-]?[0-9]+(\\.[0-9]+)?")

    /**
     * Parses [value] as a Decimal, or returns null if it is not formatted correctly or out of
     * range.
     */
    internal fun parse(value: kotlin.String): BigDecimal? =
        if (DECIMAL_FORMAT.matches(value)) DecimalHelper.validateDecimal(BigDecimal(value), null)
        else null

    @JvmStatic
    fun toDecimal(operand: Value?): Value? {
        if (operand == null) {
            return null
        }

        if (operand is Boolean) {
            return if (operand.value) BigDecimal("1.0").toCqlDecimal()
            else BigDecimal("0.0").toCqlDecimal()
        }

        if (operand is Decimal) {
            return operand
        }

        if (operand is Integer) {
            return BigDecimal(operand.value).toCqlDecimal()
        }

        if (operand is Long) {
            return BigDecimal(operand.value).toCqlDecimal()
        }

        if (operand is String) {
            return parse(operand.value)?.toCqlDecimal()
        }

        throw InvalidOperatorArgument("ToDecimal(String)", "ToDecimal(${operand.typeAsString})")
    }
}
