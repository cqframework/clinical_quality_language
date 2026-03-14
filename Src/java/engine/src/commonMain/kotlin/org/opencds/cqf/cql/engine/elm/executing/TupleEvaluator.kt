package org.opencds.cqf.cql.engine.elm.executing

import org.opencds.cqf.cql.engine.runtime.Tuple

object TupleEvaluator {
    fun internalEvaluate(ret: MutableMap<String, Any?>?): Tuple {
        return Tuple().withElements(ret!!)
    }
}
