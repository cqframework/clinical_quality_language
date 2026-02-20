package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.hl7.elm.r1.Filter
import org.opencds.cqf.cql.engine.execution.State
import org.opencds.cqf.cql.engine.execution.Variable

object FilterEvaluator {
    @JvmStatic
    fun filter(elm: Filter?, source: Any?, condition: Any?, state: State?): Any? {
        var ret: MutableList<Any?>? = ArrayList<Any?>()

        if (source == null) {
            ret = null
        }

        if (source is Iterable<*>) {
            for (obj in source as MutableList<*>) {
                try {
                    // Hmmm... This is hard without the alias.
                    // TODO: verify this works for all cases -> will scope always be present?
                    if (elm!!.scope != null) {
                        state!!.push(Variable(elm.scope!!).withValue(obj))
                    }

                    if (condition is Boolean && condition) {
                        ret!!.add(obj)
                    }
                } finally {
                    state!!.pop()
                }
            }
        }

        return ret
    }
}
