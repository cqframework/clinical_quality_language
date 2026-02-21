package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.hl7.elm.r1.As
import org.opencds.cqf.cql.engine.execution.State
import org.opencds.cqf.cql.engine.util.JavaClass

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
    private fun resolveType(`as`: As, state: State?): JavaClass<*>? {
        if (`as`.asTypeSpecifier != null) {
            return state!!.environment.resolveType(`as`.asTypeSpecifier)
        }

        return state!!.environment.resolveType(`as`.asType)
    }

    @JvmStatic
    fun internalEvaluate(operand: Any?, `as`: As, isStrict: Boolean, state: State?): Any? {
        val clazz = resolveType(`as`, state)
        return state!!.environment.`as`(operand, clazz!!, isStrict)
    }
}
