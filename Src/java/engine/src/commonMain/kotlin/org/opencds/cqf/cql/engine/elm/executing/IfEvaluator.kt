package org.opencds.cqf.cql.engine.elm.executing

import org.cqframework.cql.elm.visiting.ElmLibraryVisitor
import org.hl7.elm.r1.If
import org.opencds.cqf.cql.engine.execution.State

object IfEvaluator {
    fun internalEvaluate(elm: If?, state: State?, visitor: ElmLibraryVisitor<Any?, State?>): Any? {
        var condition = visitor.visitExpression(elm!!.condition!!, state)

        if (condition == null) {
            condition = false
        }

        return if (condition as Boolean) visitor.visitExpression(elm.then!!, state)
        else visitor.visitExpression(elm.`else`!!, state)
    }
}
