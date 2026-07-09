package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.opencds.cqf.cql.engine.execution.State
import org.opencds.cqf.cql.engine.runtime.Value

object RepeatEvaluator {
    fun repeat(source: Value?, element: Value?, scope: kotlin.String?): Value {
        // TODO
        throw NotImplementedError("Repeat operation not yet implemented")
    }

    @JvmStatic
    fun internalEvaluate(
        source: Value?,
        element: Value?,
        scope: kotlin.String?,
        state: State?,
    ): Value {
        return repeat(source, element, scope)
    }
}
