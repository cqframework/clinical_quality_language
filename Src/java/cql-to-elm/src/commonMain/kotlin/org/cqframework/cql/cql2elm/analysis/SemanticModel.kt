package org.cqframework.cql.cql2elm.analysis

import org.cqframework.cql.cql2elm.CqlCompilerOptions

/**
 * Aggregates the results of semantic analysis into a single object. Wraps the [SymbolTable],
 * [TypeTable], and [OperatorRegistry] so that downstream phases (code generation, validation) can
 * receive a single dependency instead of three separate parameters.
 *
 * An optional [CqlCompilerOptions] is threaded through for phases that need to inspect compiler
 * settings (e.g., compatibility level, signature level).
 */
class SemanticModel(
    val symbolTable: SymbolTable,
    val typeTable: TypeTable,
    val operatorRegistry: OperatorRegistry,
    val options: CqlCompilerOptions? = null,
)
