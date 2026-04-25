package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.cqframework.cql.shared.BigDecimal
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument
import org.opencds.cqf.cql.engine.runtime.Boolean
import org.opencds.cqf.cql.engine.runtime.CqlType
import org.opencds.cqf.cql.engine.runtime.Decimal
import org.opencds.cqf.cql.engine.runtime.Integer
import org.opencds.cqf.cql.engine.runtime.String

/*
ToBoolean(argument String) Boolean

The ToBoolean operator converts the value of its argument to a Boolean value.
The operator accepts the following string representations:
true: true t yes y 1
false: false f no n 0
Note that the operator will ignore case when interpreting the string as a Boolean value.
If the input cannot be interpreted as a valid Boolean value, the result is null.
If the argument is null, the result is null.
*/
object ToBooleanEvaluator {
    @JvmStatic
    fun toBoolean(operand: CqlType?): Boolean? {
        if (operand == null) {
            return null
        }

        if (operand is Boolean) {
            return operand
        }

        if (operand is Integer) {
            if (operand.value == 1) {
                return Boolean.TRUE
            }
            if (operand.value == 0) {
                return Boolean.FALSE
            }

            return null
        }

        if (operand is Decimal) {
            if (operand.value.compareTo(BigDecimal("0.0")) == 0) {
                return Boolean.FALSE
            }

            if (operand.value.compareTo(BigDecimal("1.0")) == 0) {
                return Boolean.TRUE
            }

            return null
        }

        if (operand is String) {
            val compare = operand.value.lowercase()
            if (
                compare == "true" ||
                    compare == "t" ||
                    compare == "yes" ||
                    compare == "y" ||
                    compare == "1"
            ) {
                return Boolean.TRUE
            } else if (
                compare == "false" ||
                    compare == "f" ||
                    compare == "no" ||
                    compare == "n" ||
                    compare == "0"
            ) {
                return Boolean.FALSE
            }

            return null
        }

        throw InvalidOperatorArgument("ToBoolean(String)", "ToBoolean(${operand.typeAsString})")
    }
}
