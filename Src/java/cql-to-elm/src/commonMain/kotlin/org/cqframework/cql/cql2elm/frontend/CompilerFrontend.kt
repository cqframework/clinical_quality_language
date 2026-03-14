@file:Suppress("UnusedParameter")

package org.cqframework.cql.cql2elm.frontend

import org.cqframework.cql.cql2elm.model.OperatorResolution
import org.hl7.cql.ast.BinaryOperator
import org.hl7.cql.ast.BooleanLiteral
import org.hl7.cql.ast.ContextDefinition
import org.hl7.cql.ast.DateTimeLiteral
import org.hl7.cql.ast.DecimalLiteral
import org.hl7.cql.ast.Expression
import org.hl7.cql.ast.ExpressionDefinition
import org.hl7.cql.ast.IntLiteral
import org.hl7.cql.ast.Library
import org.hl7.cql.ast.Literal
import org.hl7.cql.ast.LiteralExpression
import org.hl7.cql.ast.LongLiteral
import org.hl7.cql.ast.NullLiteral
import org.hl7.cql.ast.OperatorBinaryExpression
import org.hl7.cql.ast.OperatorUnaryExpression
import org.hl7.cql.ast.ParameterDefinition
import org.hl7.cql.ast.QuantityLiteral
import org.hl7.cql.ast.StringLiteral
import org.hl7.cql.ast.TimeLiteral
import org.hl7.cql.ast.UnaryOperator
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

/**
 * Walks the AST and infers types for all expressions, populating the [TypeTable]. Uses the
 * [OperatorRegistry] to resolve operator types and store [OperatorResolution] results.
 */
class TypeResolver(private val operatorRegistry: OperatorRegistry) {

    fun resolve(library: Library, symbolTable: SymbolTable): TypeTable {
        val typeTable = TypeTable()

        // Resolve types for parameter defaults
        for ((_, paramDef) in symbolTable.parameterDefinitions) {
            paramDef.default?.let { inferType(it, typeTable) }
        }

        // Resolve types for expression definition bodies
        for ((_, exprDef) in symbolTable.expressionDefinitions) {
            inferType(exprDef.expression, typeTable)
        }

        return typeTable
    }

    /**
     * Infer the type of an expression and store it in the [TypeTable]. Returns the inferred type,
     * or null if the type cannot be determined (e.g., null literals).
     */
    @Suppress("CyclomaticComplexMethod")
    private fun inferType(expression: Expression, typeTable: TypeTable): DataType? {
        // Check if already resolved
        typeTable[expression]?.let {
            return it
        }

        val type =
            when (expression) {
                is LiteralExpression -> inferLiteralType(expression.literal, typeTable)
                is OperatorBinaryExpression -> inferBinaryType(expression, typeTable)
                is OperatorUnaryExpression -> inferUnaryType(expression, typeTable)
                else -> null
            }

        if (type != null) {
            typeTable[expression] = type
        }
        return type
    }

    private fun inferLiteralType(literal: Literal, typeTable: TypeTable): DataType? {
        return when (literal) {
            is IntLiteral -> operatorRegistry.type("Integer")
            is LongLiteral -> operatorRegistry.type("Long")
            is DecimalLiteral -> operatorRegistry.type("Decimal")
            is StringLiteral -> operatorRegistry.type("String")
            is BooleanLiteral -> operatorRegistry.type("Boolean")
            is QuantityLiteral -> operatorRegistry.type("Quantity")
            is DateTimeLiteral -> {
                // Date-only literals (no time component) have type Date
                if (isDateOnly(literal.text)) operatorRegistry.type("Date")
                else operatorRegistry.type("DateTime")
            }
            is TimeLiteral -> operatorRegistry.type("Time")
            is NullLiteral -> null
            else -> null
        }
    }

    /**
     * Determine whether a date/time literal string represents a date-only value (no time
     * components).
     */
    private fun isDateOnly(text: String): Boolean {
        // A date-only literal has the form @YYYY, @YYYY-MM, or @YYYY-MM-DD without any T component
        return !text.contains('T')
    }

    @Suppress("ReturnCount")
    private fun inferBinaryType(
        expression: OperatorBinaryExpression,
        typeTable: TypeTable,
    ): DataType? {
        val leftType = inferType(expression.left, typeTable) ?: return null
        val rightType = inferType(expression.right, typeTable) ?: return null
        val opName = binaryOperatorToSystemName(expression.operator) ?: return null
        val resolution =
            operatorRegistry.resolve(opName, listOf(leftType, rightType)) ?: return null
        typeTable.setOperatorResolution(expression, resolution)
        return resolution.operator.resultType
    }

    @Suppress("ReturnCount")
    private fun inferUnaryType(
        expression: OperatorUnaryExpression,
        typeTable: TypeTable,
    ): DataType? {
        val operandType = inferType(expression.operand, typeTable) ?: return null
        val opName = unaryOperatorToSystemName(expression.operator) ?: return null
        if (opName == "Positive") {
            // Positive is identity - propagate operand type
            return operandType
        }
        val resolution = operatorRegistry.resolve(opName, listOf(operandType)) ?: return null
        typeTable.setOperatorResolution(expression, resolution)
        return resolution.operator.resultType
    }

    companion object {
        @Suppress("CyclomaticComplexMethod")
        fun binaryOperatorToSystemName(op: BinaryOperator): String? {
            return when (op) {
                BinaryOperator.ADD -> "Add"
                BinaryOperator.SUBTRACT -> "Subtract"
                BinaryOperator.MULTIPLY -> "Multiply"
                BinaryOperator.DIVIDE -> "Divide"
                BinaryOperator.MODULO -> "Modulo"
                BinaryOperator.POWER -> "Power"
                BinaryOperator.CONCAT -> "Concatenate"
                BinaryOperator.EQUALS -> "Equal"
                BinaryOperator.NOT_EQUALS -> "Equal" // NotEqual is Not(Equal(...))
                BinaryOperator.EQUIVALENT -> "Equivalent"
                BinaryOperator.NOT_EQUIVALENT -> "Equivalent" // Not(Equivalent(...))
                BinaryOperator.LT -> "Less"
                BinaryOperator.LTE -> "LessOrEqual"
                BinaryOperator.GT -> "Greater"
                BinaryOperator.GTE -> "GreaterOrEqual"
                BinaryOperator.AND -> "And"
                BinaryOperator.OR -> "Or"
                BinaryOperator.XOR -> "Xor"
                BinaryOperator.IMPLIES -> "Implies"
                else -> null
            }
        }

        fun unaryOperatorToSystemName(op: UnaryOperator): String? {
            return when (op) {
                UnaryOperator.NEGATE -> "Negate"
                UnaryOperator.NOT -> "Not"
                UnaryOperator.SUCCESSOR -> "Successor"
                UnaryOperator.PREDECESSOR -> "Predecessor"
                UnaryOperator.POSITIVE -> "Positive"
            }
        }
    }
}

class SemanticValidator {
    fun validate(library: Library, symbolTable: SymbolTable) {
        // Semantic validation will be implemented as the front-end expands.
    }
}
