package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.opencds.cqf.cql.engine.execution.State
import org.opencds.cqf.cql.engine.runtime.CqlType

object RepeatEvaluator {
    fun repeat(source: CqlType?, element: CqlType?, scope: kotlin.String?): CqlType {
        // TODO
        throw NotImplementedError("Repeat operation not yet implemented")
    }

    @JvmStatic
    fun internalEvaluate(
        source: CqlType?,
        element: CqlType?,
        scope: kotlin.String?,
        state: State?,
    ): CqlType {
        return repeat(source, element, scope)
    }
}
