package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.opencds.cqf.cql.engine.execution.State

object RepeatEvaluator {
    fun repeat(source: Any?, element: Any?, scope: String?): Any {
        // TODO
        throw NotImplementedError("Repeat operation not yet implemented")
    }

    @JvmStatic
    fun internalEvaluate(source: Any?, element: Any?, scope: String?, state: State?): Any {
        return repeat(source, element, scope)
    }
}
