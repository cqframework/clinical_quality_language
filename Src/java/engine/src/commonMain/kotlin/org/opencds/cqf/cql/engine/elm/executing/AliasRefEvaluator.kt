package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.opencds.cqf.cql.engine.execution.State
import org.opencds.cqf.cql.engine.runtime.CqlType

object AliasRefEvaluator {
    @JvmStatic
    fun internalEvaluate(name: kotlin.String?, state: State?): CqlType? {
        return state!!.resolveAlias(name)
    }
}
