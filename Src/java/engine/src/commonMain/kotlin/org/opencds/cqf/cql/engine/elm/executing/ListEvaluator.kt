package org.opencds.cqf.cql.engine.elm.executing

import org.cqframework.cql.elm.visiting.ElmLibraryVisitor
import org.opencds.cqf.cql.engine.execution.State
import org.opencds.cqf.cql.engine.runtime.List
import org.opencds.cqf.cql.engine.runtime.Value
import org.opencds.cqf.cql.engine.runtime.toCqlList

object ListEvaluator {
    fun internalEvaluate(
        list: org.hl7.elm.r1.List?,
        state: State?,
        visitor: ElmLibraryVisitor<Value?, State?>,
    ): List {
        val result = mutableListOf<Value?>()
        for (element in list!!.element) {
            val obj = visitor.visitExpression(element, state)
            result.add(obj)
        }
        return result.toCqlList()
    }
}
