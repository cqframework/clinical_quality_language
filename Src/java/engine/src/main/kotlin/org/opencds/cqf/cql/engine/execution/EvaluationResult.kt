package org.opencds.cqf.cql.engine.execution

import org.opencds.cqf.cql.engine.debug.DebugResult

class EvaluationResult {
    val results = mutableMapOf<EvaluationExpressionRef, ExpressionResult>()

    val expressionResults: Map<String, ExpressionResult>
        get() {
            return results
                .filterKeys { key -> key !is EvaluationFunctionRef }
                .mapKeys { entry -> entry.key.name }
        }

    fun forExpression(expressionName: String): ExpressionResult? {
        return this.expressionResults[expressionName]
    }

    var debugResult: DebugResult? = null
}
