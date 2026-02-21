package org.opencds.cqf.cql.engine.execution

import org.opencds.cqf.cql.engine.debug.DebugResult
import org.opencds.cqf.cql.engine.execution.trace.Trace

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

    /** Returns the ExpressionResult for the given expression reference. */
    operator fun get(ref: EvaluationExpressionRef): ExpressionResult? {
        return results[ref]
    }

    /** Sets the ExpressionResult for the given expression reference. */
    operator fun set(ref: EvaluationExpressionRef, result: ExpressionResult) {
        results[ref] = result
    }

    var debugResult: DebugResult? = null

    /** Trace information collected during evaluation. Only used when tracing is enabled. */
    var trace: Trace? = null
}
