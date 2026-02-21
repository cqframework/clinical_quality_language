package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.opencds.cqf.cql.engine.execution.State

object NullEvaluator {
    @JvmStatic
    fun internalEvaluate(state: State?): Any? {
        return null
    }
}
