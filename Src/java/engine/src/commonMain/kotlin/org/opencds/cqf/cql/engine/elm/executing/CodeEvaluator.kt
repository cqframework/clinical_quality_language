package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.hl7.elm.r1.CodeSystemRef
import org.opencds.cqf.cql.engine.execution.Libraries
import org.opencds.cqf.cql.engine.execution.State
import org.opencds.cqf.cql.engine.runtime.Code

/*
structured type Code
{
  code String,
  display String,
  system String,
  version String
}

The Code type represents single terminology codes within CQL.
*/
object CodeEvaluator {
    @JvmStatic
    fun internalEvaluate(
        codeSystemRef: CodeSystemRef?,
        c: String?,
        display: String?,
        state: State?,
    ): Any? {
        val code = Code().withCode(c).withDisplay(display)
        if (codeSystemRef != null) {
            val enteredLibrary = state!!.enterLibrary(codeSystemRef.libraryName)
            try {
                val codeSystemDef =
                    Libraries.resolveCodeSystemRef(codeSystemRef.name, state.getCurrentLibrary()!!)
                code.system = codeSystemDef.id
                code.version = codeSystemDef.version
            } finally {
                state.exitLibrary(enteredLibrary)
            }
        }

        return code
    }
}
