@file:Suppress("UnusedParameter")

package org.cqframework.cql.cql2elm.analysis

import org.cqframework.cql.cql2elm.model.OperatorResolution
import org.hl7.cql.ast.ContextDefinition
import org.hl7.cql.ast.Expression
import org.hl7.cql.ast.ExpressionDefinition
import org.hl7.cql.ast.FunctionDefinition
import org.hl7.cql.ast.IdentifierExpression
import org.hl7.cql.ast.Library
import org.hl7.cql.ast.ParameterDefinition
import org.hl7.cql.model.DataType

/**
 * Entry point for compiler front-end analysis. Future implementations will perform declaration
 * collection, type inference, and semantic validation prior to code generation.
 */
class CompilerFrontend(
    private val symbolCollector: SymbolCollector = SymbolCollector(),
    private val operatorRegistry: OperatorRegistry = OperatorRegistry.createSystemRegistry(),
    private val semanticValidator: SemanticValidator = SemanticValidator(),
) {
    data class Result(
        val library: Library,
        val symbolTable: SymbolTable,
        val typeTable: TypeTable = TypeTable(),
        val operatorRegistry: OperatorRegistry,
        val diagnostics: kotlin.collections.List<Diagnostic> = emptyList(),
    )

    fun analyze(library: Library): Result {
        val symbols = symbolCollector.collect(library)
        val typeResolver = TypeResolver(operatorRegistry)
        val typeTable = typeResolver.resolve(library, symbols)
        semanticValidator.validate(library, symbols)
        return Result(library, symbols, typeTable, operatorRegistry)
    }
}

/**
 * Maps AST [Expression] nodes to their inferred [DataType]. Uses a plain [HashMap] keyed by AST
 * node identity. Since AST nodes are data classes that include [org.hl7.cql.ast.Locator] in their
 * fields, structurally identical expressions at different source positions have different locators
 * and thus different equals/hashCode values, avoiding collisions.
 */
class TypeTable {
    private val types = HashMap<Expression, DataType>()
    private val operatorResolutions = HashMap<Expression, OperatorResolution>()
    private val identifierResolutions = HashMap<IdentifierExpression, Resolution>()

    operator fun get(expression: Expression): DataType? = types[expression]

    operator fun set(expression: Expression, type: DataType) {
        types[expression] = type
    }

    fun contains(expression: Expression): Boolean = expression in types

    fun getOperatorResolution(expression: Expression): OperatorResolution? =
        operatorResolutions[expression]

    fun setOperatorResolution(expression: Expression, resolution: OperatorResolution) {
        operatorResolutions[expression] = resolution
    }

    fun getIdentifierResolution(expression: IdentifierExpression): Resolution? =
        identifierResolutions[expression]

    fun setIdentifierResolution(expression: IdentifierExpression, resolution: Resolution) {
        identifierResolutions[expression] = resolution
    }
}

/** Describes a resolved reference to a declaration. */
sealed interface Resolution {
    data class ExpressionRef(val definition: ExpressionDefinition) : Resolution

    data class ParameterRef(val definition: ParameterDefinition) : Resolution

    data class ContextRef(val definition: ContextDefinition) : Resolution

    data class OperandRef(val name: String, val type: DataType) : Resolution

    data class AliasRef(val name: String, val type: DataType) : Resolution

    data class QueryLetRef(val name: String, val type: DataType) : Resolution
}

/**
 * Symbol table that retains definitions, contexts, and inferred types discovered during analysis.
 */
data class SymbolTable(
    val expressionDefinitions: Map<String, ExpressionDefinition> = emptyMap(),
    val parameterDefinitions: Map<String, ParameterDefinition> = emptyMap(),
    val contextDefinitions: List<ContextDefinition> = emptyList(),
    val functionDefinitions: Map<String, List<FunctionDefinition>> = emptyMap(),
) {
    fun resolveExpression(name: String): Resolution.ExpressionRef? =
        expressionDefinitions[name]?.let { Resolution.ExpressionRef(it) }

    fun resolveParameter(name: String): Resolution.ParameterRef? =
        parameterDefinitions[name]?.let { Resolution.ParameterRef(it) }

    fun resolveFunctions(name: String): List<FunctionDefinition> =
        functionDefinitions[name] ?: emptyList()
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
        val functionDefs = mutableMapOf<String, MutableList<FunctionDefinition>>()

        // Collect parameter definitions from library definitions
        for (definition in library.definitions) {
            when (definition) {
                is ParameterDefinition -> {
                    parameterDefs[definition.name.value] = definition
                }
                else -> {} // Other definition types (using, include, etc.) are not collected yet
            }
        }

        // Collect context, expression, and function definitions from statements
        for (statement in library.statements) {
            when (statement) {
                is ContextDefinition -> {
                    contextDefs.add(statement)
                }
                is ExpressionDefinition -> {
                    expressionDefs[statement.name.value] = statement
                }
                is FunctionDefinition -> {
                    functionDefs.getOrPut(statement.name.value) { mutableListOf() }.add(statement)
                }
                else -> {}
            }
        }

        return SymbolTable(
            expressionDefinitions = expressionDefs,
            parameterDefinitions = parameterDefs,
            contextDefinitions = contextDefs,
            functionDefinitions = functionDefs,
        )
    }
}

class SemanticValidator {
    fun validate(library: Library, symbolTable: SymbolTable) {
        // Semantic validation will be implemented as the front-end expands.
    }
}
