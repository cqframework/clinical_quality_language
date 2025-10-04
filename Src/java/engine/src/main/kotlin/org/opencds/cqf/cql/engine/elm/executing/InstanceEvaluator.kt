package org.opencds.cqf.cql.engine.elm.executing

import org.cqframework.cql.elm.visiting.ElmLibraryVisitor
import org.hl7.elm.r1.Instance
import org.opencds.cqf.cql.engine.execution.State

object InstanceEvaluator {
    fun internalEvaluate(
        instance: Instance?,
        state: State?,
        visitor: ElmLibraryVisitor<Any?, State?>,
    ): Any? {
        val `object` = state!!.environment.createInstance(instance!!.classType!!)
        for (element in instance.element) {
            val value = visitor.visitExpression(element.value!!, state)
            state.environment.setValue(`object`, element.name!!, value)
        }

        return `object`
    }
}
