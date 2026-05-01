package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.opencds.cqf.cql.engine.execution.State
import org.opencds.cqf.cql.engine.runtime.Value

object IdentifierRefEvaluator {
    @JvmStatic
    fun internalEvaluate(name: kotlin.String?, state: State?): Value? {
        if (name == null) {
            return null
        }

        return state!!.resolveIdentifierRef(name)
    }
}
