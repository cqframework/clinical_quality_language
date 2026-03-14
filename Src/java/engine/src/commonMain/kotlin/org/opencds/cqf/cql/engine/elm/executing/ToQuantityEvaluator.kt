package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.cqframework.cql.shared.BigDecimal
import org.opencds.cqf.cql.engine.execution.State
import org.opencds.cqf.cql.engine.runtime.Quantity
import org.opencds.cqf.cql.engine.runtime.Ratio
import org.opencds.cqf.cql.engine.runtime.Value
import org.opencds.cqf.cql.engine.util.javaClassName

/*
ToQuantity(argument Decimal) Quantity
ToQuantity(argument Integer) Quantity
ToQuantity(argument Ratio) Quantity
ToQuantity(argument String) Quantity

Description:
The ToQuantity operator converts the value of its argument to a Quantity value.

For the String overload, the operator accepts strings using the following format:
(+|-)?#0(.0#)?('<unit>')?

Meaning an optional polarity indicator, followed by any number of digits (including none) followed by at least one digit,
  optionally followed by a decimal point, at least one digit, and any number of additional digits, all optionally
  followed by a unit designator as a string literal specifying a valid, case-sensitive UCUM unit of measure. Spaces are
  allowed between the quantity value and the unit designator.

Note that the decimal value of the quantity returned by this operator must be a valid value in the range representable
  for Decimal values in CQL.

If the input string is not formatted correctly, or cannot be interpreted as a valid Quantity value, the result is null.

For the Integer and Decimal overloads, the operator returns a quantity with the value of the argument and a unit of '1'
  (the default unit).

For the Ratio overload, the operator is equivalent to dividing the numerator of the ratio by the denominator.

If the argument is null, the result is null.

The following examples illustrate the behavior of the ToQuantity operator:

define DecimalOverload: ToQuantity(0.1) // 0.1 '1'
define IntegerOverload: ToQuantity(13) // 13 '1'
define StringOverload: ToQuantity('-0.1 \'mg\'') // -0.1 'mg'
define IsNull: ToQuantity('444 \'cm')

*/
object ToQuantityEvaluator {
    @JvmStatic
    fun toQuantity(str: String): Quantity? {
        // Tabs are treated like spaces for Units
        var str = str
        str = str.replace("[\t]".toRegex(), " ").trim { it <= ' ' }
        val index = str.indexOf(' ')

        var number = str
        var quantity: Quantity? = Quantity()

        if (index > 0) {
            number = str.substring(0, index)
            quantity!!.unit = str.substring(index + 1).replace("[\' ]".toRegex(), "")
        }
        quantity = ToQuantityEvaluator.setValue(quantity!!, number)

        return quantity
    }

    private fun setValue(quantity: Quantity, str: String): Quantity? {
        try {
            val number = BigDecimal(str)
            if (Value.validateDecimal(number, null) == null) {
                return null
            }
            quantity.value = number
        } catch (nfe: NumberFormatException) {
            return null
        }
        return quantity
    }

    @JvmStatic
    fun toQuantity(operand: Any?, state: State?): Quantity? {
        if (operand == null) {
            return null
        }

        if (operand is Quantity) {
            return operand
        }

        if (operand is String) {
            val str = operand
            return toQuantity(str)
        } else if (operand is Int) {
            val ret = BigDecimal(operand)
            if (Value.validateDecimal(ret, null) == null) {
                return null
            }
            return Quantity().withValue(ret).withDefaultUnit()
        } else if (operand is BigDecimal) {
            if (Value.validateDecimal(operand, null) == null) {
                return null
            }
            return Quantity().withValue(operand).withDefaultUnit()
        } else if (operand is Ratio) {
            return DivideEvaluator.divide(operand.numerator, operand.denominator, state)
                as Quantity?
        }

        throw IllegalArgumentException(
            "Cannot cast a value of type ${operand.javaClassName} as Quantity - use String, Integer, Decimal, or Ratio values."
        )
    }
}
