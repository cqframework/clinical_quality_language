package org.hl7.cql.ast

import org.hl7.cql.ast.analysis.AnalysisPipeline
import org.hl7.cql.ast.analysis.AstAnalysis
import org.hl7.cql.ast.analysis.IdentifierShadowingAnalysis

/**
 * Coordinates parsing, AST analysis, and future compilation stages. Parsing is handled by
 * [Builder], while analyses such as identifier shadowing checks and type inference are executed as
 * follow-up steps.
 */
class CqlFrontend(
    private val analysisFactories: () -> List<AstAnalysis> =
        { listOf(IdentifierShadowingAnalysis()) },
    private val typeInferencer: TypeInferencer = StubTypeInferencer(),
) {

    /** Builds a [Library] AST from [text] without performing additional analyses. */
    fun buildLibrary(text: String, sourceId: String? = null): LibraryResult =
        Builder(sourceId).parseLibrary(text)

    /** Builds an [Expression] AST from [text] without running analyses. */
    fun buildExpression(text: String, sourceId: String? = null): ExpressionResult =
        Builder(sourceId).parseExpression(text)

    /**
     * Runs the configured analyses for [library], producing an [AnalysisMetadata] side table and any
     * associated [Problem]s.
     */
    fun analyzeLibrary(library: Library): LibraryAnalysisResult {
        val ids = assignAstNodeIds(library)
        val analysisProblems = newPipeline().analyze(library)
        val typeResult = typeInferencer.infer(library, ids)
        val metadata = AnalysisMetadata(ids, typeResult.typeTable)
        val problems = analysisProblems + typeResult.problems
        return LibraryAnalysisResult(metadata, problems)
    }

    /**
     * Runs the configured analyses for an arbitrary [expression], returning the same metadata and
     * problem reporting as [analyzeLibrary].
     */
    fun analyzeExpression(expression: Expression): ExpressionAnalysisResult {
        val ids = assignAstNodeIds(expression)
        val analysisProblems = newPipeline().analyze(expression)
        val typeResult = typeInferencer.infer(expression, ids)
        val metadata = AnalysisMetadata(ids, typeResult.typeTable)
        val problems = analysisProblems + typeResult.problems
        return ExpressionAnalysisResult(metadata, problems)
    }

    private fun newPipeline(): AnalysisPipeline = AnalysisPipeline(analysisFactories())
}

/**
 * Shared metadata produced while analyzing a CQL artifact. The [ids] table is intended for any pass
 * that needs to associate information with AST nodes, while [typeTable] stores (or will store)
 * inferred types.
 */
data class AnalysisMetadata(
    val ids: AstIdTable,
    val typeTable: TypeTable,
)

/** Result of running the analysis pipeline for a library. */
data class LibraryAnalysisResult(
    val metadata: AnalysisMetadata,
    val problems: List<Problem>,
)

/** Result of running the analysis pipeline for a standalone expression. */
data class ExpressionAnalysisResult(
    val metadata: AnalysisMetadata,
    val problems: List<Problem>,
)
