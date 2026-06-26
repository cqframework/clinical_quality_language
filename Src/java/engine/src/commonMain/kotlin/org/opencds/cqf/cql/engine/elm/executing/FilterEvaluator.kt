package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.hl7.elm.r1.Filter
import org.opencds.cqf.cql.engine.execution.State
import org.opencds.cqf.cql.engine.execution.Variable
import org.opencds.cqf.cql.engine.runtime.Boolean
import org.opencds.cqf.cql.engine.runtime.List
import org.opencds.cqf.cql.engine.runtime.Value
import org.opencds.cqf.cql.engine.runtime.toCqlList

object FilterEvaluator {
    @JvmStatic
    fun filter(elm: Filter?, source: Value?, condition: Value?, state: State?): List? {

        if (source == null) {
            return null
        }

        val ret = mutableListOf<Value?>()

        if (source is List) {
            for (obj in source) {
                try {
                    // Hmmm... This is hard without the alias.
                    // TODO: verify this works for all cases -> will scope always be present?
                    if (elm!!.scope != null) {
                        state!!.push(Variable(elm.scope!!).withValue(obj))
                    }

                    if (condition is Boolean && condition.value) {
                        ret.add(obj)
                    }
                } finally {
                    state!!.pop()
                }
            }
        }

        return ret.toCqlList()
    }
}
