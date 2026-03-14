package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.hl7.elm.r1.As
import org.hl7.elm.r1.Length
import org.hl7.elm.r1.NamedTypeSpecifier
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument
import org.opencds.cqf.cql.engine.execution.State
import org.opencds.cqf.cql.engine.util.javaClassName

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
    fun length(operand: Any?): Any? {
        if (operand is String) {
            return stringLength(operand)
        }

        if (operand is Iterable<*>) {
            return listLength(operand)
        }

        throw InvalidOperatorArgument(
            "Length(List<T>) or Length(String)",
            "Length(${operand?.javaClassName} )",
        )
    }

    fun stringLength(operand: String?): Int? {
        if (operand == null) {
            return null
        }

        return operand.length
    }

    fun listLength(operand: Iterable<*>?): Int? {
        if (operand == null) {
            return 0
        }

        return operand.count()
    }

    @JvmStatic
    fun internalEvaluate(operand: Any?, length: Length?, state: State?): Any? {
        // null operand case

        if (length!!.operand is As) {
            if ((length.operand as As).asTypeSpecifier is NamedTypeSpecifier) {
                return stringLength(operand as String?)
            } else {
                return listLength(operand as Iterable<*>?)
            }
        }

        return length(operand)
    }
}
