package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.hl7.elm.r1.As
import org.hl7.elm.r1.Length
import org.hl7.elm.r1.NamedTypeSpecifier
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument
import org.opencds.cqf.cql.engine.execution.State
import org.opencds.cqf.cql.engine.runtime.CqlType
import org.opencds.cqf.cql.engine.runtime.Integer
import org.opencds.cqf.cql.engine.runtime.List
import org.opencds.cqf.cql.engine.runtime.String
import org.opencds.cqf.cql.engine.runtime.toCqlInteger

/*
*** LIST NOTES ***
Length(argument List<T>) Integer

The Length operator returns the number of elements in a list.
If the argument is null, the result is 0.

*** STRING NOTES ***
Length(argument String) Integer

The Length operator returns the number of characters in a string.
If the argument is null, the result is null.
*/
object LengthEvaluator {
    fun length(operand: CqlType?): Integer? {
        if (operand is String) {
            return stringLength(operand)
        }

        if (operand is List) {
            return listLength(operand)
        }

        throw InvalidOperatorArgument(
            "Length(List<T>) or Length(String)",
            "Length(${operand?.typeAsString} )",
        )
    }

    fun stringLength(operand: String?): Integer? {
        if (operand == null) {
            return null
        }

        return operand.length.toCqlInteger()
    }

    fun listLength(operand: List?): Integer {
        if (operand == null) {
            return (0).toCqlInteger()
        }

        return operand.count().toCqlInteger()
    }

    @JvmStatic
    fun internalEvaluate(operand: CqlType?, length: Length?, state: State?): Integer? {
        // null operand case

        if (length!!.operand is As) {
            if ((length.operand as As).asTypeSpecifier is NamedTypeSpecifier) {
                return stringLength(operand as String?)
            } else {
                return listLength(operand as List?)
            }
        }

        return length(operand)
    }
}
