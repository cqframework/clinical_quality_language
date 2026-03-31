package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.hl7.elm.r1.As
import org.hl7.elm.r1.NamedTypeSpecifier
import org.hl7.elm.r1.TypeSpecifier
import org.opencds.cqf.cql.engine.exception.InvalidCast
import org.opencds.cqf.cql.engine.execution.State

/*
as<T>(argument Any) T
cast as<T>(argument Any) T

The as operator allows the result of an expression to be cast as a given target type.
  This allows expressions to be written that are statically typed against the expected run-time type of the argument.
If the argument is not of the specified type at run-time the result is null.
The cast prefix indicates that if the argument is not of the specified type at run-time then an exception is thrown.
Example:
The following examples illustrate the use of the as operator.
define AllProcedures: [Procedure]
define ImagingProcedures:
  AllProcedures P
    where P is ImagingProcedure
    return P as ImagingProcedure
define RuntimeError:
  ImagingProcedures P
    return cast P as Observation
*/
object AsEvaluator {
    @JvmStatic
    fun internalEvaluate(operand: Any?, `as`: As, isStrict: Boolean, state: State?): Any? {
        val type = `as`.asTypeSpecifier ?: NamedTypeSpecifier().withName(`as`.asType)
        return `as`(operand, type, isStrict, state)
    }

    fun `as`(operand: Any?, type: TypeSpecifier, isStrict: Boolean, state: State?): Any? {
        if (IsEvaluator.`is`(operand, type, state) == true) {
            return operand
        }

        if (isStrict) {
            throw InvalidCast("Cannot cast $operand to $type.")
        }

        return null
    }
}
