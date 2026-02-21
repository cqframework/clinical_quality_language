package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument
import org.opencds.cqf.cql.engine.runtime.Code
import org.opencds.cqf.cql.engine.runtime.Concept
import org.opencds.cqf.cql.engine.util.javaClassName

/*
ToConcept(argument Code) Concept

The ToConcept operator converts a value of type Code to a Concept value with the given Code as its primary and only Code.
If the Code has a display value, the resulting Concept will have the same display value.
If the argument is null, the result is null.
*/
object ToConceptEvaluator {
    @JvmStatic
    fun toConcept(operand: Any?): Any? {
        if (operand == null) {
            return null
        }

        val result = Concept()

        if (operand is Iterable<*>) {
            for (code in operand) {
                result.withCode(code as Code?)
            }
            return result
        } else if (operand is Code) {
            result.withCode(operand)
            return result
        }

        throw InvalidOperatorArgument("ToConcept(Code)", "ToConcept(${operand.javaClassName})")
    }
}
