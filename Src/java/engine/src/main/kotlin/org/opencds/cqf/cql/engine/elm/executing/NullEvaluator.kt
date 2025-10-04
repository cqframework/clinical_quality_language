package org.opencds.cqf.cql.engine.elm.executing

import org.opencds.cqf.cql.engine.execution.State

object NullEvaluator {
    @JvmStatic
    fun internalEvaluate(state: State?): Any? {
        return null
    }
}
