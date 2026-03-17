@file:Suppress("UnusedParameter")

package org.cqframework.cql.cql2elm.analysis

import org.cqframework.cql.cql2elm.CqlCompilerOptions
import org.cqframework.cql.cql2elm.ModelManager
import org.cqframework.cql.cql2elm.model.OperatorResolution
import org.cqframework.cql.cql2elm.utils.IdentityHashMap
import org.hl7.cql.ast.CodeDefinition
import org.hl7.cql.ast.CodeSystemDefinition
import org.hl7.cql.ast.ConceptDefinition
import org.hl7.cql.ast.ContextDefinition
import org.hl7.cql.ast.Expression
import org.hl7.cql.ast.ExpressionDefinition
import org.hl7.cql.ast.FunctionDefinition
import org.hl7.cql.ast.IdentifierExpression
import org.hl7.cql.ast.IncludeDefinition
import org.hl7.cql.ast.Library
import org.hl7.cql.ast.ParameterDefinition
import org.hl7.cql.ast.ValueSetDefinition
import org.hl7.cql.model.DataType

/**
 * Entry point for compiler front-end analysis. Future implementations will perform declaration
 * collection, type inference, and semantic validation prior to code generation.
 */
class CompilerFrontend(
    private val symbolCollector: SymbolCollector = SymbolCollector(),
    private val operatorRegistry: OperatorRegistry = OperatorRegistry.createSystemRegistry(),
    private val semanticValidator: SemanticValidator = SemanticValidator(),
    val modelManager: ModelManager? = null,
    val options: CqlCompilerOptions? = null,
) {
    data class Result(
        val library: Library,
        val semanticModel: SemanticModel,
        val diagnostics: kotlin.collections.List<Diagnostic> = emptyList(),
    ) {
        /** Convenience accessor for backward compatibility. */
        val symbolTable: SymbolTable
            get() = semanticModel.symbolTable

        /** Convenience accessor for backward compatibility. */
        val typeTable: TypeTable
            get() = semanticModel.typeTable

        /** Convenience accessor for backward compatibility. */
        val operatorRegistry: OperatorRegistry
            get() = semanticModel.operatorRegistry
    }

    fun analyze(library: Library): Result {
        val symbols = symbolCollector.collect(library)
        val typeResolver = TypeResolver(operatorRegistry)
        val typeTable = typeResolver.resolve(library, symbols)
        val semanticModel = SemanticModel(symbols, typeTable, operatorRegistry, options)
        semanticValidator.validate(library, symbols, semanticModel)
        return Result(library, semanticModel)
    }
}

/**
 * Maps AST [Expression] nodes to their inferred [DataType]. Uses an [IdentityHashMap] keyed by
 * reference identity so that structurally identical nodes at different positions never collide —
 * even when they share the same [org.hl7.cql.ast.Locator].
 */
class TypeTable {
    private val types = IdentityHashMap<Expression, DataType>()
    private val operatorResolutions = IdentityHashMap<Expression, OperatorResolution>()
    private val identifierResolutions = IdentityHashMap<IdentifierExpression, Resolution>()

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

    data class CodeSystemRef(val definition: CodeSystemDefinition) : Resolution

    data class ValueSetRef(val definition: ValueSetDefinition) : Resolution

    data class CodeRef(val definition: CodeDefinition) : Resolution

    data class ConceptRef(val definition: ConceptDefinition) : Resolution
}

/**
 * Symbol table that retains definitions, contexts, and inferred types discovered during analysis.
 */
data class SymbolTable(
    val expressionDefinitions: Map<String, ExpressionDefinition> = emptyMap(),
    val parameterDefinitions: Map<String, ParameterDefinition> = emptyMap(),
    val contextDefinitions: List<ContextDefinition> = emptyList(),
    val functionDefinitions: Map<String, List<FunctionDefinition>> = emptyMap(),
    val includeDefinitions: Map<String, IncludeDefinition> = emptyMap(),
    val codeSystemDefinitions: Map<String, CodeSystemDefinition> = emptyMap(),
    val valueSetDefinitions: Map<String, ValueSetDefinition> = emptyMap(),
    val codeDefinitions: Map<String, CodeDefinition> = emptyMap(),
    val conceptDefinitions: Map<String, ConceptDefinition> = emptyMap(),
) {
    fun resolveExpression(name: String): Resolution.ExpressionRef? =
        expressionDefinitions[name]?.let { Resolution.ExpressionRef(it) }

    fun resolveParameter(name: String): Resolution.ParameterRef? =
        parameterDefinitions[name]?.let { Resolution.ParameterRef(it) }

    fun resolveFunctions(name: String): List<FunctionDefinition> =
        functionDefinitions[name] ?: emptyList()

    fun resolveCodeSystem(name: String): Resolution.CodeSystemRef? =
        codeSystemDefinitions[name]?.let { Resolution.CodeSystemRef(it) }

    fun resolveValueSet(name: String): Resolution.ValueSetRef? =
        valueSetDefinitions[name]?.let { Resolution.ValueSetRef(it) }

    fun resolveCode(name: String): Resolution.CodeRef? =
        codeDefinitions[name]?.let { Resolution.CodeRef(it) }

    fun resolveConcept(name: String): Resolution.ConceptRef? =
        conceptDefinitions[name]?.let { Resolution.ConceptRef(it) }
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
        val includeDefs = mutableMapOf<String, IncludeDefinition>()
        val codeSystemDefs = mutableMapOf<String, CodeSystemDefinition>()
        val valueSetDefs = mutableMapOf<String, ValueSetDefinition>()
        val codeDefs = mutableMapOf<String, CodeDefinition>()
        val conceptDefs = mutableMapOf<String, ConceptDefinition>()

        // Collect definitions from library definitions
        for (definition in library.definitions) {
            when (definition) {
                is ParameterDefinition -> parameterDefs[definition.name.value] = definition
                is IncludeDefinition -> {
                    val alias = definition.alias?.value ?: definition.libraryIdentifier.simpleName
                    includeDefs[alias] = definition
                }
                is CodeSystemDefinition -> codeSystemDefs[definition.name.value] = definition
                is ValueSetDefinition -> valueSetDefs[definition.name.value] = definition
                is CodeDefinition -> codeDefs[definition.name.value] = definition
                is ConceptDefinition -> conceptDefs[definition.name.value] = definition
                else -> {}
            }
        }

        // Collect context, expression, and function definitions from statements
        for (statement in library.statements) {
            when (statement) {
                is ContextDefinition -> contextDefs.add(statement)
                is ExpressionDefinition -> expressionDefs[statement.name.value] = statement
                is FunctionDefinition ->
                    functionDefs.getOrPut(statement.name.value) { mutableListOf() }.add(statement)
                else -> {}
            }
        }

        return SymbolTable(
            expressionDefinitions = expressionDefs,
            parameterDefinitions = parameterDefs,
            contextDefinitions = contextDefs,
            functionDefinitions = functionDefs,
            includeDefinitions = includeDefs,
            codeSystemDefinitions = codeSystemDefs,
            valueSetDefinitions = valueSetDefs,
            codeDefinitions = codeDefs,
            conceptDefinitions = conceptDefs,
        )
    }
}

/**
 * Walks the AST after type resolution and flags expressions with semantic errors. Codegen reads
 * these flags and emits `Null` for flagged expressions, matching the legacy translator's error
 * recovery behavior.
 */
class SemanticValidator {
    fun validate(library: Library, symbolTable: SymbolTable, semanticModel: SemanticModel) {
        // Check expression definitions
        for ((name, exprDef) in symbolTable.expressionDefinitions) {
            validateExpression(exprDef.expression, symbolTable, semanticModel)
        }
        // Check function definitions
        for ((_, funcDefs) in symbolTable.functionDefinitions) {
            for (funcDef in funcDefs) {
                val body = funcDef.body
                if (body is org.hl7.cql.ast.ExpressionFunctionBody) {
                    validateExpression(body.expression, symbolTable, semanticModel)
                    // If any sub-expression in the function body has an error,
                    // flag the entire body (legacy replaces the whole body with Null)
                    if (hasNestedError(body.expression, semanticModel)) {
                        semanticModel.addError(body.expression)
                    }
                }
            }
        }
        // Check parameter defaults
        for ((_, paramDef) in symbolTable.parameterDefinitions) {
            paramDef.default?.let { validateExpression(it, symbolTable, semanticModel) }
        }
    }

    /** Check if an expression or any of its descendants has been flagged with an error. */
    private fun hasNestedError(
        expression: org.hl7.cql.ast.Expression,
        semanticModel: SemanticModel,
    ): Boolean {
        if (semanticModel.hasError(expression)) return true
        var found = false
        val walker =
            object : org.hl7.cql.ast.AstWalker() {
                override fun visitExpression(expression: org.hl7.cql.ast.Expression) {
                    if (semanticModel.hasError(expression)) {
                        found = true
                        return
                    }
                    if (!found) super.visitExpression(expression)
                }
            }
        walker.visitExpression(expression)
        return found
    }

    private fun validateExpression(
        expression: org.hl7.cql.ast.Expression,
        symbolTable: SymbolTable,
        semanticModel: SemanticModel,
    ) {
        val walker =
            object : org.hl7.cql.ast.AstWalker() {
                override fun visitIdentifierExpression(
                    expression: org.hl7.cql.ast.IdentifierExpression
                ) {
                    // If TypeResolver recorded a resolution, the identifier is valid
                    // (includes query aliases, let bindings, operands, global definitions)
                    if (semanticModel.getIdentifierResolution(expression) != null) return

                    // No resolution AND no inferred type → unresolved identifier
                    if (semanticModel[expression] == null) {
                        semanticModel.addError(expression)
                    }
                }

                override fun visitFunctionCallExpression(
                    expression: org.hl7.cql.ast.FunctionCallExpression
                ) {
                    // Visit children first
                    expression.target?.let { visitExpression(it) }
                    expression.arguments.forEach { visitExpression(it) }

                    // If type inference succeeded OR operator resolution found a match,
                    // the function call is valid
                    if (semanticModel[expression] != null) return
                    if (semanticModel.getOperatorResolution(expression) != null) return

                    // No type and no resolution — check if the function exists at all
                    val name = expression.function.value
                    // Check user-defined functions
                    val userFuncs = symbolTable.resolveFunctions(name)
                    if (userFuncs.isNotEmpty()) {
                        val callArity = expression.arguments.size
                        val anyArityMatch = userFuncs.any { it.operands.size == callArity }
                        if (!anyArityMatch) {
                            // Name exists but no signature matches → error
                            semanticModel.addError(expression)
                        } else if (semanticModel[expression] == null) {
                            // Signature matches but type is null → recursive call or
                            // other resolution failure → error
                            semanticModel.addError(expression)
                        }
                        return
                    }

                    // Try resolving as a system operator with the argument types we have.
                    // If the operator registry knows this name at all, it's valid but
                    // we just couldn't resolve the specific overload.
                    val argTypes = expression.arguments.mapNotNull { semanticModel[it] }
                    if (argTypes.size == expression.arguments.size) {
                        // All args have types — if resolution still failed, the call
                        // truly doesn't match any operator
                        semanticModel.addError(expression)
                    } else {
                        // Some args have null types (e.g., Ratio not yet implemented).
                        // Don't flag — it's a type inference gap, not a semantic error.
                    }
                }

                override fun visitAsExpression(expression: org.hl7.cql.ast.AsExpression) {
                    visitExpression(expression.operand)
                    // Validate: can the operand type be cast to the target type?
                    val operandType = semanticModel[expression.operand]
                    val targetType = semanticModel[expression]
                    if (operandType != null && targetType != null) {
                        // A list cannot be cast to a non-list type
                        if (
                            operandType is org.hl7.cql.model.ListType &&
                                targetType !is org.hl7.cql.model.ListType
                        ) {
                            semanticModel.addError(expression)
                        }
                    }
                }

                // Don't walk into sort-by items — they use a separate emission path
                // (ByColumn/ByDirection) and their identifiers are property paths, not
                // resolvable references
                override fun visitSortByItem(item: org.hl7.cql.ast.SortByItem) {
                    // Skip — sort items are not validated for identifier resolution
                }
            }
        walker.visitExpression(expression)
    }
}
