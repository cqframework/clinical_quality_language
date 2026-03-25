package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.hl7.elm.r1.Is
import org.hl7.elm.r1.NamedTypeSpecifier
import org.opencds.cqf.cql.engine.execution.State

/*
is<T>(argument Any) Boolean

The is operator allows the type of a result to be tested.
If the run-time type of the argument is of the type being tested, the result of the operator is true;
  otherwise, the result is false.
*/
object IsEvaluator {

    @JvmStatic
    fun internalEvaluate(`is`: Is?, operand: Any?, state: State?): Any? {
        val type = `is`?.isTypeSpecifier ?: NamedTypeSpecifier().withName(`is`?.isType)

        return state!!.environment.`is`(operand, type)
    }
}
