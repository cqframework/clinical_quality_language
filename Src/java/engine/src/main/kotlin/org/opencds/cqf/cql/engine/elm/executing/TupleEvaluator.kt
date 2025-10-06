package org.opencds.cqf.cql.engine.elm.executing

import org.opencds.cqf.cql.engine.execution.State
import org.opencds.cqf.cql.engine.runtime.Tuple

object TupleEvaluator {
    fun internalEvaluate(ret: MutableMap<String, Any?>?, state: State?): Tuple {
        return Tuple(state).withElements(ret!!)
    }
}
