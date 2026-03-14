package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.hl7.elm.r1.ValueSetRef
import org.opencds.cqf.cql.engine.execution.State

object AnyInValueSetEvaluator {
    @JvmStatic
    fun internalEvaluate(
        codes: Any?,
        valueSetRef: ValueSetRef?,
        valueset: Any?,
        state: State?,
    ): Any? {
        if (codes == null) {
            return false
        }

        var vs: Any? = null
        if (valueSetRef != null) {
            vs = ValueSetRefEvaluator.toValueSet(state, valueSetRef)
        } else if (valueset != null) {
            vs = valueset
        }

        if (vs == null) {
            return null
        }

        if (codes is Iterable<*>) {
            var result: Any?
            for (code in codes) {
                result = InValueSetEvaluator.inValueSet(code, vs, state)
                if (result is Boolean && result) {
                    return true
                }
            }
        }

        return false
    }
}
