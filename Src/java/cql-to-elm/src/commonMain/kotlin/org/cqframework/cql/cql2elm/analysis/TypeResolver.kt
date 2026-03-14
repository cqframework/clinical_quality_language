package org.cqframework.cql.cql2elm.analysis

import org.hl7.cql.ast.BinaryOperator
import org.hl7.cql.ast.BooleanLiteral
import org.hl7.cql.ast.BooleanTestExpression
import org.hl7.cql.ast.BooleanTestKind
import org.hl7.cql.ast.DateTimeLiteral
import org.hl7.cql.ast.DecimalLiteral
import org.hl7.cql.ast.Expression
import org.hl7.cql.ast.FunctionCallExpression
import org.hl7.cql.ast.IfExpression
import org.hl7.cql.ast.IndexExpression
import org.hl7.cql.ast.IntLiteral
import org.hl7.cql.ast.Library
import org.hl7.cql.ast.Literal
import org.hl7.cql.ast.LiteralExpression
import org.hl7.cql.ast.LongLiteral
import org.hl7.cql.ast.NullLiteral
import org.hl7.cql.ast.OperatorBinaryExpression
import org.hl7.cql.ast.OperatorUnaryExpression
import org.hl7.cql.ast.QuantityLiteral
import org.hl7.cql.ast.StringLiteral
import org.hl7.cql.ast.TimeLiteral
import org.hl7.cql.ast.UnaryOperator
import org.hl7.cql.model.DataType
import org.hl7.cql.model.ListType

/**
 * Walks the AST and infers types for all expressions, populating the [TypeTable]. Uses the
 * [OperatorRegistry] to resolve operator types and store
 * [org.cqframework.cql.cql2elm.model.OperatorResolution] results.
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
                is BooleanTestExpression -> inferBooleanTestType(expression, typeTable)
                is IfExpression -> inferIfType(expression, typeTable)
                is FunctionCallExpression -> inferFunctionCallType(expression, typeTable)
                is IndexExpression -> inferIndexType(expression, typeTable)
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

    /**
     * Infer the type for boolean test expressions: `is null`, `is true`, `is false`. These resolve
     * as unary system operators (IsNull, IsTrue, IsFalse).
     */
    @Suppress("ReturnCount")
    private fun inferBooleanTestType(
        expression: BooleanTestExpression,
        typeTable: TypeTable,
    ): DataType? {
        val operandType = inferType(expression.operand, typeTable) ?: return null
        val opName =
            when (expression.kind) {
                BooleanTestKind.IS_NULL -> "IsNull"
                BooleanTestKind.IS_TRUE -> "IsTrue"
                BooleanTestKind.IS_FALSE -> "IsFalse"
            }
        val resolution = operatorRegistry.resolve(opName, listOf(operandType)) ?: return null
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

    /**
     * Infer the type for an if-then-else expression. The result type is the common supertype of the
     * then and else branches.
     */
    @Suppress("ReturnCount")
    private fun inferIfType(expression: IfExpression, typeTable: TypeTable): DataType? {
        inferType(expression.condition, typeTable)
        val thenType = inferType(expression.thenBranch, typeTable)
        val elseType = inferType(expression.elseBranch, typeTable)
        // Result type is the common supertype of both branches
        return when {
            thenType == null -> elseType
            elseType == null -> thenType
            else -> thenType.getCommonSuperTypeOf(elseType)
        }
    }

    /**
     * Infer the type for a function call expression by resolving the function name as a system
     * operator.
     */
    @Suppress("ReturnCount")
    private fun inferFunctionCallType(
        expression: FunctionCallExpression,
        typeTable: TypeTable,
    ): DataType? {
        val functionName = expression.function.value
        val argTypes = expression.arguments.map { inferType(it, typeTable) }

        // For Coalesce, special handling: Coalesce with a single list argument
        // should resolve as Coalesce<T>(List<T>) -> T
        if (functionName == "Coalesce" && argTypes.size == 1) {
            val singleArgType = argTypes[0]
            if (singleArgType is ListType) {
                val resolution = operatorRegistry.resolve(functionName, listOf(singleArgType))
                if (resolution != null) {
                    typeTable.setOperatorResolution(expression, resolution)
                    return resolution.operator.resultType
                }
            }
        }

        // Try to resolve as a system operator
        val nonNullArgTypes = argTypes.filterNotNull()
        if (nonNullArgTypes.size != argTypes.size) return null

        val resolution =
            operatorRegistry.resolve(
                functionName,
                nonNullArgTypes,
                allowPromotionAndDemotion = true,
            )
        if (resolution != null) {
            typeTable.setOperatorResolution(expression, resolution)
            return resolution.operator.resultType
        }
        return null
    }

    /**
     * Infer the type for an index expression (e.g., 'John'[1]). Resolves as the Indexer operator.
     */
    @Suppress("ReturnCount")
    private fun inferIndexType(expression: IndexExpression, typeTable: TypeTable): DataType? {
        val targetType = inferType(expression.target, typeTable) ?: return null
        val indexType = inferType(expression.index, typeTable) ?: return null
        val resolution =
            operatorRegistry.resolve("Indexer", listOf(targetType, indexType)) ?: return null
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
