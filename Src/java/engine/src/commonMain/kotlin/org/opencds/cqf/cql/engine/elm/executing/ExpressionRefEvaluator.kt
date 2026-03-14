package org.opencds.cqf.cql.engine.elm.executing

import org.cqframework.cql.elm.visiting.ElmLibraryVisitor
import org.hl7.elm.r1.ExpressionRef
import org.opencds.cqf.cql.engine.execution.Libraries
import org.opencds.cqf.cql.engine.execution.State

object ExpressionRefEvaluator {
    fun internalEvaluate(
        expressionRef: ExpressionRef?,
        state: State?,
        visitor: ElmLibraryVisitor<Any?, State?>,
    ): Any? {
        val enteredLibrary = state!!.enterLibrary(expressionRef!!.libraryName)
        try {
            val def =
                Libraries.resolveExpressionRef(expressionRef.name, state.getCurrentLibrary()!!)
            state.pushActivationFrame(def, def.context!!)
            try {
                val result = visitor.visitExpressionDef(def, state)
                state.storeIntermediateResultForTracing(result)
                return result
            } finally {
                state.popActivationFrame()
            }
        } finally {
            state.exitLibrary(enteredLibrary)
        }
    }
}
