package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.hl7.elm.r1.CodeSystemRef
import org.opencds.cqf.cql.engine.execution.State
import org.opencds.cqf.cql.engine.runtime.Boolean
import org.opencds.cqf.cql.engine.runtime.List
import org.opencds.cqf.cql.engine.runtime.Value

object AnyInCodeSystemEvaluator {
    @JvmStatic
    fun internalEvaluate(
        codes: Value?,
        codeSystemRef: CodeSystemRef?,
        codeSystem: Value?,
        state: State?,
    ): Boolean? {
        var cs: Value? = null
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
