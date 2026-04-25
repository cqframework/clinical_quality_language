package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.opencds.cqf.cql.engine.execution.State
import org.opencds.cqf.cql.engine.runtime.CqlType

object IdentifierRefEvaluator {
    @JvmStatic
    fun internalEvaluate(name: kotlin.String?, state: State?): CqlType? {
        if (name == null) {
            return null
        }

        return state!!.resolveIdentifierRef(name)
    }
}
