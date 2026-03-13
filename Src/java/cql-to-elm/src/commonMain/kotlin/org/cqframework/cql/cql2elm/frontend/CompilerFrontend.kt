@file:Suppress("UnusedParameter")

package org.cqframework.cql.cql2elm.frontend

import org.hl7.cql.ast.ContextDefinition
import org.hl7.cql.ast.Expression
import org.hl7.cql.ast.ExpressionDefinition
import org.hl7.cql.ast.Library
import org.hl7.cql.ast.ParameterDefinition
import org.hl7.cql.model.DataType

/**
 * Entry point for compiler front-end analysis. Future implementations will perform declaration
 * collection, type inference, and semantic validation prior to code generation.
 */
class CompilerFrontend(
    private val symbolCollector: SymbolCollector = SymbolCollector(),
    private val typeResolver: TypeResolver = TypeResolver(),
    private val semanticValidator: SemanticValidator = SemanticValidator(),
) {
    data class Result(
        val library: Library,
        val symbolTable: SymbolTable,
        val typeTable: TypeTable = TypeTable(),
        val diagnostics: kotlin.collections.List<Diagnostic> = emptyList(),
    )

    fun analyze(library: Library): Result {
        val symbols = symbolCollector.collect(library)
        val typeTable = typeResolver.resolve(library, symbols)
        semanticValidator.validate(library, symbols)
        return Result(library, symbols, typeTable)
    }
}

/**
 * Maps AST [Expression] nodes to their inferred [DataType]. Uses a plain [HashMap] keyed by AST
 * node identity (each AST builder creates unique instances, so reference equality via default
 * `hashCode`/`equals` on data classes is sufficient).
 */
class TypeTable {
    private val types = HashMap<Expression, DataType>()

    operator fun get(expression: Expression): DataType? = types[expression]

    operator fun set(expression: Expression, type: DataType) {
        types[expression] = type
    }

    fun contains(expression: Expression): Boolean = expression in types
}

/** Describes a resolved reference to a declaration. */
sealed interface Resolution {
    data class ExpressionRef(val definition: ExpressionDefinition) : Resolution

    data class ParameterRef(val definition: ParameterDefinition) : Resolution

    data class ContextRef(val definition: ContextDefinition) : Resolution
}

/**
 * Symbol table that retains definitions, contexts, and inferred types discovered during analysis.
 */
data class SymbolTable(
    val expressionDefinitions: Map<String, ExpressionDefinition> = emptyMap(),
    val parameterDefinitions: Map<String, ParameterDefinition> = emptyMap(),
    val contextDefinitions: List<ContextDefinition> = emptyList(),
) {
    fun resolveExpression(name: String): Resolution.ExpressionRef? =
        expressionDefinitions[name]?.let { Resolution.ExpressionRef(it) }

    fun resolveParameter(name: String): Resolution.ParameterRef? =
        parameterDefinitions[name]?.let { Resolution.ParameterRef(it) }
}

/** Represents a diagnostic message produced during compilation. */
data class Diagnostic(val severity: Severity, val message: String) {
    enum class Severity {
        INFO,
        WARNING,
        ERROR,
    }
}

class SymbolCollector {
    fun collect(library: Library): SymbolTable {
        val expressionDefs = mutableMapOf<String, ExpressionDefinition>()
        val parameterDefs = mutableMapOf<String, ParameterDefinition>()
        val contextDefs = mutableListOf<ContextDefinition>()

        // Collect parameter definitions from library definitions
        for (definition in library.definitions) {
            when (definition) {
                is ParameterDefinition -> {
                    parameterDefs[definition.name.value] = definition
                }
                else -> {} // Other definition types (using, include, etc.) are not collected yet
            }
        }

        // Collect context and expression definitions from statements
        for (statement in library.statements) {
            when (statement) {
                is ContextDefinition -> {
                    contextDefs.add(statement)
                }
                is ExpressionDefinition -> {
                    expressionDefs[statement.name.value] = statement
                }
                else -> {} // Function definitions etc. are not collected yet
            }
        }

        return SymbolTable(
            expressionDefinitions = expressionDefs,
            parameterDefinitions = parameterDefs,
            contextDefinitions = contextDefs,
        )
    }
}

class TypeResolver {
    fun resolve(library: Library, symbolTable: SymbolTable): TypeTable {
        val typeTable = TypeTable()
        // Type inference for literals and expressions will be expanded in future milestones.
        // For now, the TypeTable is returned empty. The ElmEmitter handles literal type
        // mapping directly during emission (literals have self-evident types).
        return typeTable
    }
}

class SemanticValidator {
    fun validate(library: Library, symbolTable: SymbolTable) {
        // Semantic validation will be implemented as the front-end expands.
    }
}
