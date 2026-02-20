package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument
import org.opencds.cqf.cql.engine.util.javaClassName

/*
ToChars(argument String) List<String>

The ToChars operator takes a string and returns a list with one string for each character in the input, in the order in which they appear in the string.

If the argument is null, the result is null.
*/
object ToCharsEvaluator {
    @JvmStatic
    fun toChars(operand: Any?): List<String?>? {
        if (operand == null) {
            return null
        }

        if (operand is String) {
            val result = mutableListOf<String?>()
            for (c in operand.toCharArray()) {
                result.add(c.toString())
            }
            return result
        }

        throw InvalidOperatorArgument("ToChars(String)", "ToInteger(${operand.javaClassName})")
    }
}
