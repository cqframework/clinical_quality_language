package org.opencds.cqf.cql.engine.elm.executing

import org.cqframework.cql.elm.visiting.ElmLibraryVisitor
import org.hl7.elm.r1.If
import org.opencds.cqf.cql.engine.execution.State
import org.opencds.cqf.cql.engine.runtime.Boolean
import org.opencds.cqf.cql.engine.runtime.Value

object IfEvaluator {
    fun internalEvaluate(
        elm: If?,
        state: State?,
        visitor: ElmLibraryVisitor<Value?, State?>,
    ): Value? {
        var condition = visitor.visitExpression(elm!!.condition!!, state) as Boolean?

        if (condition == null) {
            condition = Boolean.FALSE
        }

        return if (condition.value) visitor.visitExpression(elm.then!!, state)
        else visitor.visitExpression(elm.`else`!!, state)
    }
}
