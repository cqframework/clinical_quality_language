package org.cqframework.cql.cql2elm.analysis

/**
 * Metrics collected during semantic analysis. Stored in [SemanticModel] for diagnostics, testing,
 * and convergence monitoring.
 */
data class AnalysisMetrics(
    // COLLECT
    val definitionCount: Int = 0,
    val statementCount: Int = 0,

    // INFER
    val expressionCount: Int = 0,
    val typedCount: Int = 0,
    val unresolvedCount: Int = 0,
    val operatorResolutionCount: Int = 0,
    val identifierResolutionCount: Int = 0,

    // CONVERT (future — not yet implemented)
    val conversionsInserted: Int = 0,
    val conversionsByKind: Map<String, Int> = emptyMap(),

    // Loop (future — not yet implemented)
    val inferConvertIterations: Int = 1,
    val newConversionsPerIteration: List<Int> = emptyList(),

    // VALIDATE
    val errorCount: Int = 0,
    val warningCount: Int = 0,
) {
    override fun toString(): String = buildString {
        append("AnalysisMetrics(")
        append("defs=$definitionCount, stmts=$statementCount, ")
        append("exprs=$expressionCount, typed=$typedCount, unresolved=$unresolvedCount, ")
        append("opResolutions=$operatorResolutionCount, idResolutions=$identifierResolutionCount, ")
        append("errors=$errorCount, warnings=$warningCount")
        if (conversionsInserted > 0) {
            append(", conversions=$conversionsInserted $conversionsByKind")
        }
        if (inferConvertIterations > 1) {
            append(", iterations=$inferConvertIterations, perIter=$newConversionsPerIteration")
        }
        append(")")
    }
}
