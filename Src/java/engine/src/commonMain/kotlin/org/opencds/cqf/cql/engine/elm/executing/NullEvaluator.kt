package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.opencds.cqf.cql.engine.execution.State
import org.opencds.cqf.cql.engine.runtime.Value

object NullEvaluator {
    @JvmStatic
    fun internalEvaluate(state: State?): Value? {
        return null
    }
}
