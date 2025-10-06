package org.opencds.cqf.cql.engine.elm.executing

import org.cqframework.cql.elm.visiting.ElmLibraryVisitor
import org.hl7.elm.r1.ExpressionDef
import org.hl7.elm.r1.OperandRef
import org.opencds.cqf.cql.engine.execution.State

object OperandRefEvaluator {
    fun internalEvaluate(
        operandRef: OperandRef?,
        state: State?,
        visitor: ElmLibraryVisitor<Any?, State?>,
    ): Any? {
        val variable = state!!.resolveVariable(operandRef!!.name, true)!!.value
        // We're executing the logic here, so this is valid check in execution context
        if (variable is ExpressionDef) {
            return visitor.visitExpressionDef(variable, state)
        } else {
            return variable
        }
    }
}
