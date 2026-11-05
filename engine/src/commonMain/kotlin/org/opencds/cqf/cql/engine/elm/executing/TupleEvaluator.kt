package org.opencds.cqf.cql.engine.elm.executing

import org.opencds.cqf.cql.engine.runtime.Tuple
import org.opencds.cqf.cql.engine.runtime.Value

object TupleEvaluator {
    fun internalEvaluate(ret: MutableMap<kotlin.String, Value?>?): Tuple {
        return Tuple().withElements(ret!!)
    }
}
