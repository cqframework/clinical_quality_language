package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.hl7.elm.r1.CodeSystemRef
import org.opencds.cqf.cql.engine.execution.State
import org.opencds.cqf.cql.engine.runtime.Boolean
import org.opencds.cqf.cql.engine.runtime.CqlType
import org.opencds.cqf.cql.engine.runtime.List

object AnyInCodeSystemEvaluator {
    @JvmStatic
    fun internalEvaluate(
        codes: CqlType?,
        codeSystemRef: CodeSystemRef?,
        codeSystem: CqlType?,
        state: State?,
    ): Boolean? {
        var cs: CqlType? = null
        if (codeSystemRef != null) {
            cs = CodeSystemRefEvaluator.toCodeSystem(codeSystemRef, state)
        } else if (codeSystem != null) {
            cs = codeSystem
        }

        if (codes == null || cs == null) return null

        if (codes is List) {
            for (code in codes) {
                if (InCodeSystemEvaluator.inCodeSystem(code, cs, state)?.value == true) {
                    return Boolean.TRUE
                }
            }
        }

        return Boolean.FALSE
    }
}
