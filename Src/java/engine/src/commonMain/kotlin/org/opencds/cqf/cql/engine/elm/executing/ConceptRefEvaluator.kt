package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.hl7.elm.r1.ConceptRef
import org.opencds.cqf.cql.engine.execution.Libraries
import org.opencds.cqf.cql.engine.execution.State
import org.opencds.cqf.cql.engine.runtime.Code
import org.opencds.cqf.cql.engine.runtime.Concept

object ConceptRefEvaluator {
    @JvmStatic
    fun toConcept(cr: ConceptRef, state: State?): Concept {
        val enteredLibrary = state!!.enterLibrary(cr.libraryName)
        try {
            val cd = Libraries.resolveConceptRef(cr.name, state.getCurrentLibrary()!!)

            val codeList = ArrayList<Code?>()
            for (r in cd.code) {
                val codeDef = Libraries.resolveCodeRef(r.name, state.getCurrentLibrary()!!)
                val cs = CodeSystemRefEvaluator.toCodeSystem(codeDef.codeSystem!!, state)
                val c = CodeRefEvaluator.toCode(codeDef, cs!!)
                codeList.add(c)
            }

            return Concept().withDisplay(cd.display).withCodes(codeList)
        } finally {
            state.exitLibrary(enteredLibrary)
        }
    }
}
