package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.opencds.cqf.cql.engine.runtime.Ratio
import org.opencds.cqf.cql.engine.util.javaClassName

/*

    ToRatio(argument String) Ratio

    The ToRatio operator converts the value of its argument to a Ratio value. The operator accepts strings using the following format:
    <quantity>:<quantity>

    where <quantity> is the format used to by the ToQuantity operator.

    If the input string is not formatted correctly, or cannot be interpreted as a valid Ratio value, the result is null.

    If the argument is null, the result is null.

    The following examples illustrate the behavior of the ToRatio operator:

    define IsValid: ToRatio('1.0 \'mg\':2.0 \'mg\'')
    define IsNull: ToRatio('1.0 \'mg\';2.0 \'mg\'')

*/
object ToRatioEvaluator {
    @JvmStatic
    fun toRatio(operand: Any?): Any? {
        if (operand == null) {
            return null
        }

        if (operand is Ratio) {
            return operand
        }

        if (operand is String) {
            val quantityStrings: Array<String?> =
                operand.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            if (quantityStrings.size == 2) {
                return Ratio()
                    .withNumerator(ToQuantityEvaluator.toQuantity(quantityStrings[0]!!)!!)
                    .withDenominator(ToQuantityEvaluator.toQuantity(quantityStrings[1]!!)!!)
            }
            return null
        }

        throw IllegalArgumentException(
            "Cannot cast a value of type ${operand.javaClassName} as Ratio - use String values."
        )
    }
}
