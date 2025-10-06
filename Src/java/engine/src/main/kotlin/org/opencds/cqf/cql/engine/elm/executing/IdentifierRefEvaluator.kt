package org.opencds.cqf.cql.engine.elm.executing

import org.opencds.cqf.cql.engine.execution.State

object IdentifierRefEvaluator {
    @JvmStatic
    fun internalEvaluate(name: String?, state: State?): Any? {
        if (name == null) {
            return null
        }

        return state!!.resolveIdentifierRef(name)
    }
}
