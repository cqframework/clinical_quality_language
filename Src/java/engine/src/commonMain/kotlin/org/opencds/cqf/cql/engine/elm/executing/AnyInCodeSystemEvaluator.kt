package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.hl7.elm.r1.CodeSystemRef
import org.opencds.cqf.cql.engine.execution.State

object AnyInCodeSystemEvaluator {
    @JvmStatic
    fun internalEvaluate(
        codes: Any?,
        codeSystemRef: CodeSystemRef?,
        codeSystem: Any?,
        state: State?,
    ): Any? {
        var cs: Any? = null
        if (codeSystemRef != null) {
            cs = CodeSystemRefEvaluator.toCodeSystem(codeSystemRef, state)
        } else if (codeSystem != null) {
            cs = codeSystem
        }

        if (codes == null || cs == null) return null

        if (codes is Iterable<*>) {
            var result: Any?
            for (code in codes) {
                result = InCodeSystemEvaluator.inCodeSystem(code, cs, state)
                if (result is Boolean && result) {
                    return true
                }
            }
        }

        return false
    }
}
