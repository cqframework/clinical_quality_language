package org.opencds.cqf.cql.engine.elm.executing

import org.cqframework.cql.elm.visiting.ElmLibraryVisitor
import org.hl7.elm.r1.List
import org.opencds.cqf.cql.engine.execution.State

object ListEvaluator {
    fun internalEvaluate(
        list: List?,
        state: State?,
        visitor: ElmLibraryVisitor<Any?, State?>,
    ): Any {
        val result = mutableListOf<Any?>()
        for (element in list!!.element) {
            val obj = visitor.visitExpression(element, state)
            result.add(obj)
        }
        return result
    }
}
