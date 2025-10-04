package org.opencds.cqf.cql.engine.elm.executing

import org.cqframework.cql.elm.visiting.ElmLibraryVisitor
import org.hl7.elm.r1.Property
import org.opencds.cqf.cql.engine.execution.State
import org.opencds.cqf.cql.engine.runtime.Tuple

object PropertyEvaluator {
    fun internalEvaluate(
        elm: Property?,
        state: State?,
        visitor: ElmLibraryVisitor<Any?, State?>,
    ): Any? {
        var target: Any? = null

        if (elm!!.source != null) {
            target = visitor.visitExpression(elm.source!!, state)
            // Tuple element access
            if (target is Tuple) {
                // NOTE: translator will throw error if Tuple does not contain the specified element
                // -- no need for
                // x.containsKey() check
                return target.elements.get(elm.path)
            }
        } else if (elm.scope != null) {
            target = state!!.resolveVariable(elm.scope, true)!!.value
        }

        if (target == null) {
            return null
        }

        if (target is Iterable<*>) {}

        return state!!.environment.resolvePath(target, elm.path!!)
    }
}
