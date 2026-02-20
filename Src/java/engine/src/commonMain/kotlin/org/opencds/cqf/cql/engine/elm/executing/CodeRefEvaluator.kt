package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.hl7.elm.r1.CodeDef
import org.hl7.elm.r1.CodeRef
import org.opencds.cqf.cql.engine.execution.Libraries
import org.opencds.cqf.cql.engine.execution.State
import org.opencds.cqf.cql.engine.runtime.Code
import org.opencds.cqf.cql.engine.runtime.CodeSystem

object CodeRefEvaluator {
    fun toCode(cd: CodeDef, cs: CodeSystem): Code? {
        return Code()
            .withCode(cd.id)
            .withSystem(cs.id)
            .withDisplay(cd.display)
            .withVersion(cs.version)
    }

    @JvmStatic
    fun toCode(cr: CodeRef, state: State?): Code? {
        val enteredLibrary = state!!.enterLibrary(cr.libraryName)
        try {
            val cd = Libraries.resolveCodeRef(cr.name, state.getCurrentLibrary()!!)
            val cs = CodeSystemRefEvaluator.toCodeSystem(cd.codeSystem!!, state)
            return toCode(cd, cs!!)
        } finally {
            state.exitLibrary(enteredLibrary)
        }
    }
}
