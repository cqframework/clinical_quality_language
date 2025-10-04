package org.opencds.cqf.cql.engine.elm.executing

import org.opencds.cqf.cql.engine.execution.State

object AliasRefEvaluator {
    @JvmStatic
    fun internalEvaluate(name: String?, state: State?): Any? {
        return state!!.resolveAlias(name)
    }
}
