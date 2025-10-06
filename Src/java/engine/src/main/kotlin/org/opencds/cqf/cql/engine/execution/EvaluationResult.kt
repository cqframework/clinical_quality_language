package org.opencds.cqf.cql.engine.execution

import org.opencds.cqf.cql.engine.debug.DebugResult

class EvaluationResult {
    val expressionResults = mutableMapOf<String?, ExpressionResult?>()

    fun forExpression(expressionName: String?): ExpressionResult? {
        return this.expressionResults[expressionName]
    }

    var debugResult: DebugResult? = null
}
