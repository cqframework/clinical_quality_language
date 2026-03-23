package org.cqframework.cql.cql2elm.analysis

import org.cqframework.cql.cql2elm.CqlCompilerOptions
import org.cqframework.cql.cql2elm.model.OperatorResolution
import org.hl7.cql.ast.Expression
import org.hl7.cql.ast.FunctionCallExpression
import org.hl7.cql.ast.FunctionDefinition
import org.hl7.cql.ast.IdentifierExpression
import org.hl7.cql.model.DataType

/**
 * The unified result of semantic analysis. Provides a single API for codegen and post-processing to
 * query types, resolutions, and symbol information without knowing about the internal
 * [SymbolTable]/[TypeTable] split.
 *
 * During analysis, [SymbolTable] and [TypeTable] are built by separate passes. Once analysis is
 * complete, they are wrapped in a [SemanticModel] and become the single artifact passed downstream.
 */
class SemanticModel(
    internal val symbolTable: SymbolTable,
    internal val typeTable: TypeTable,
    val operatorRegistry: OperatorRegistry,
    val options: CqlCompilerOptions? = null,
    /** Expressions flagged with semantic errors. Codegen emits Null for these. */
    internal val errors: MutableSet<Expression> = mutableSetOf(),
    /** Side table of synthetic conversions keyed by parent expression + slot. */
    val syntheticTable: SyntheticTable = SyntheticTable(),
    /** Model context for type/QName resolution. Passed to codegen. */
    val modelContext: ModelContext = ModelContext.systemOnly(),
) {
    /** Metrics collected during analysis. Populated by [SemanticAnalyzer]. */
    var metrics: AnalysisMetrics = AnalysisMetrics()
        internal set

    /** Check if an expression has a semantic error (codegen should emit Null). */
    fun hasError(expression: Expression): Boolean = expression in errors

    /** Flag an expression as having a semantic error. */
    fun addError(expression: Expression) {
        errors.add(expression)
    }

    // --- Type queries (delegated to TypeTable) ---

    /** Look up the inferred type of an expression. */
    operator fun get(expression: Expression): DataType? = typeTable[expression]

    /** Look up the operator resolution for an expression. */
    fun getOperatorResolution(expression: Expression): OperatorResolution? =
        typeTable.getOperatorResolution(expression)

    /** Look up how an identifier expression was resolved. */
    fun getIdentifierResolution(expression: IdentifierExpression): Resolution? =
        typeTable.getIdentifierResolution(expression)

    /** Look up if a function call resolved to a user-defined function. */
    fun getFunctionCallResolution(expression: FunctionCallExpression): FunctionDefinition? =
        typeTable.getFunctionCallResolution(expression)

    // --- Symbol queries (delegated to SymbolTable) ---

    fun resolveExpression(name: String) = symbolTable.resolveExpression(name)

    fun resolveParameter(name: String) = symbolTable.resolveParameter(name)

    fun resolveFunctions(name: String): List<FunctionDefinition> =
        symbolTable.resolveFunctions(name)

    fun resolveCodeSystem(name: String) = symbolTable.resolveCodeSystem(name)

    fun resolveValueSet(name: String) = symbolTable.resolveValueSet(name)

    fun resolveCode(name: String) = symbolTable.resolveCode(name)

    fun resolveConcept(name: String) = symbolTable.resolveConcept(name)

    fun resolveInclude(name: String) = symbolTable.resolveInclude(name)
}
