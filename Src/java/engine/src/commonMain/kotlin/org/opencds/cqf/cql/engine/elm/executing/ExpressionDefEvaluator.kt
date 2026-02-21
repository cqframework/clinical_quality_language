package org.opencds.cqf.cql.engine.elm.executing

import org.cqframework.cql.elm.visiting.ElmLibraryVisitor
import org.hl7.elm.r1.ExpressionDef
import org.opencds.cqf.cql.engine.execution.ExpressionResult
import org.opencds.cqf.cql.engine.execution.State

object ExpressionDefEvaluator {
    fun internalEvaluate(
        expressionDef: ExpressionDef?,
        state: State?,
        visitor: ElmLibraryVisitor<Any?, State?>,
    ): Any? {
        var isEnteredContext = false
        if (expressionDef!!.context != null) {
            isEnteredContext = state!!.enterContext(expressionDef.context)
        }
        try {
            state!!.pushEvaluatedResourceStack()
            val libraryId = state.getCurrentLibrary()!!.identifier
            val isExpressionCachingEnabled = state.cache.isExpressionCachingEnabled
            val isExpressionCached = state.cache.isExpressionCached(libraryId, expressionDef.name)

            if (isExpressionCachingEnabled && isExpressionCached) {
                val er = state.cache.getCachedExpression(libraryId, expressionDef.name)
                state.evaluatedResources!!.addAll(er!!.evaluatedResources!!)

                // TODO(jmoringe): make public interface
                val frame = state.topActivationFrame
                check(frame.element === expressionDef)
                frame.isCached = true

                return er.value
            }

            val value = visitor.visitExpression(expressionDef.expression!!, state)

            if (state.cache.isExpressionCachingEnabled) {
                val er = ExpressionResult(value, state.evaluatedResources)
                state.cache.cacheExpression(libraryId, expressionDef.name, er)
            }

            return value
        } finally {
            state!!.popEvaluatedResourceStack()
            // state.enterContext.getContext() == null will result in isEnteredContext = false,
            // which means pop() won't
            // be called
            state.exitContext(isEnteredContext)
        }
    }
}
