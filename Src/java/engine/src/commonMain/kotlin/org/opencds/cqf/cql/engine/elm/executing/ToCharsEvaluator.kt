package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument
import org.opencds.cqf.cql.engine.runtime.CqlType
import org.opencds.cqf.cql.engine.runtime.List
import org.opencds.cqf.cql.engine.runtime.String
import org.opencds.cqf.cql.engine.runtime.toCqlList
import org.opencds.cqf.cql.engine.runtime.toCqlString

/*
ToChars(argument String) List<String>

The ToChars operator takes a string and returns a list with one string for each character in the input, in the order in which they appear in the string.

If the argument is null, the result is null.
*/
object ToCharsEvaluator {
    @JvmStatic
    fun toChars(operand: CqlType?): List? {
        if (operand == null) {
            return null
        }

        if (operand is String) {
            val result = mutableListOf<String>()
            for (c in operand.value.toCharArray()) {
                result.add(c.toString().toCqlString())
            }
            return result.toCqlList()
        }

        throw InvalidOperatorArgument("ToChars(String)", "ToInteger(${operand.typeAsString})")
    }
}
