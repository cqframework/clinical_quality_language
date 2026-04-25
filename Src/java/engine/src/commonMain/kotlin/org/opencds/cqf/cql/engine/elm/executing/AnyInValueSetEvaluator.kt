package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.hl7.elm.r1.ValueSetRef
import org.opencds.cqf.cql.engine.execution.State
import org.opencds.cqf.cql.engine.runtime.Boolean
import org.opencds.cqf.cql.engine.runtime.CqlType
import org.opencds.cqf.cql.engine.runtime.List

object AnyInValueSetEvaluator {
    @JvmStatic
    fun internalEvaluate(
        codes: CqlType?,
        valueSetRef: ValueSetRef?,
        valueset: CqlType?,
        state: State?,
    ): Boolean? {
        if (codes == null) {
            return Boolean.FALSE
        }

        var vs: CqlType? = null
        if (valueSetRef != null) {
            vs = ValueSetRefEvaluator.toValueSet(state, valueSetRef)
        } else if (valueset != null) {
            vs = valueset
        }

        if (vs == null) {
            return null
        }

        if (codes is List) {
            for (code in codes) {
                if (InValueSetEvaluator.inValueSet(code, vs, state)?.value == true) {
                    return Boolean.TRUE
                }
            }
        }

        return Boolean.FALSE
    }
}
