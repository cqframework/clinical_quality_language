package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.hl7.elm.r1.CodeSystemRef
import org.opencds.cqf.cql.engine.execution.Libraries
import org.opencds.cqf.cql.engine.execution.State
import org.opencds.cqf.cql.engine.runtime.CodeSystem

// References a code system by its previously defined name
object CodeSystemRefEvaluator {
    @JvmStatic
    fun toCodeSystem(csr: CodeSystemRef?, state: State?): CodeSystem? {
        val enteredLibrary = state!!.enterLibrary(csr!!.libraryName)
        try {
            val csd = Libraries.resolveCodeSystemRef(csr.name, state.getCurrentLibrary()!!)
            return CodeSystem().withId(csd.id).withVersion(csd.version).withName(csd.name)
        } finally {
            state.exitLibrary(enteredLibrary)
        }
    }
}
