package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.hl7.elm.r1.ValueSetRef
import org.opencds.cqf.cql.engine.execution.Libraries
import org.opencds.cqf.cql.engine.execution.State
import org.opencds.cqf.cql.engine.runtime.Value
import org.opencds.cqf.cql.engine.runtime.ValueSet
import org.opencds.cqf.cql.engine.runtime.toCqlList
import org.opencds.cqf.cql.engine.terminology.ValueSetInfo

object ValueSetRefEvaluator {
    @JvmStatic
    fun toValueSet(state: State?, vsr: ValueSetRef?): ValueSet {
        val enteredLibrary = state!!.enterLibrary(vsr!!.libraryName)
        try {
            val vsd = Libraries.resolveValueSetRef(vsr.name, state.getCurrentLibrary()!!)
            return ValueSet(
                id = vsd.id,
                version = vsd.version,
                codeSystems =
                    vsd.codeSystem.map { csr -> CodeSystemRefEvaluator.toCodeSystem(csr, state) },
            )
        } finally {
            state.exitLibrary(enteredLibrary)
        }
    }

    @JvmStatic
    fun internalEvaluate(state: State?, vsr: ValueSetRef?): Value {
        val vs = toValueSet(state, vsr)

        if (vsr!!.isPreserve() != null && vsr.isPreserve()!!) {
            return vs
        } else {
            val tp = state!!.environment.terminologyProvider
            return tp!!.expand(ValueSetInfo.fromValueSet(vs)).toCqlList()
        }
    }
}
