package org.opencds.cqf.cql.engine.execution

import org.opencds.cqf.cql.engine.debug.DebugResult

class EvaluationResult {
    /** Includes both expression results and function evaluation results. */
    internal val results = mutableMapOf<EvaluationExpressionRef, ExpressionResult>()

    /** Selects only the expression results, excluding function evaluation results. */
    val expressionResults: Map<String, ExpressionResult>
        get() {
            return results
                .filterKeys { key -> key !is EvaluationFunctionRef }
                .mapKeys { entry -> entry.key.name }
        }

    /** Returns the ExpressionResult for the given expression name. */
    operator fun get(name: String): ExpressionResult? {
        return expressionResults[name]
    }

    /** Returns the ExpressionResult for the given expression name. */
    operator fun get(ref: EvaluationExpressionRef): ExpressionResult? {
        return results[ref]
    }

    var debugResult: DebugResult? = null
}
