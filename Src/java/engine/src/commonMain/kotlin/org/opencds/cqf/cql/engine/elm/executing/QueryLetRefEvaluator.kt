package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.hl7.elm.r1.QueryLetRef
import org.opencds.cqf.cql.engine.execution.State
import org.opencds.cqf.cql.engine.runtime.Value

object QueryLetRefEvaluator {
    @JvmStatic
    fun internalEvaluate(elm: QueryLetRef?, state: State?): Value? {
        return state!!.resolveVariable(elm!!.name)!!.value
    }
}
