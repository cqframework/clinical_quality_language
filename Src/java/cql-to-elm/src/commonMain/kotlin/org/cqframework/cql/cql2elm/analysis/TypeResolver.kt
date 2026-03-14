@file:Suppress("TooManyFunctions")

package org.cqframework.cql.cql2elm.analysis

import org.hl7.cql.ast.BinaryOperator
import org.hl7.cql.ast.BooleanLiteral
import org.hl7.cql.ast.BooleanTestExpression
import org.hl7.cql.ast.BooleanTestKind
import org.hl7.cql.ast.DateTimeLiteral
import org.hl7.cql.ast.DecimalLiteral
import org.hl7.cql.ast.Expression
import org.hl7.cql.ast.ExpressionFunctionBody
import org.hl7.cql.ast.FunctionCallExpression
import org.hl7.cql.ast.FunctionDefinition
import org.hl7.cql.ast.IdentifierExpression
import org.hl7.cql.ast.IfExpression
import org.hl7.cql.ast.IndexExpression
import org.hl7.cql.ast.IntLiteral
import org.hl7.cql.ast.Library
import org.hl7.cql.ast.Literal
import org.hl7.cql.ast.LiteralExpression
import org.hl7.cql.ast.LongLiteral
import org.hl7.cql.ast.NamedTypeSpecifier
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
 * [OperatorRegistry] to resolve operator types and store operator resolution results.
 */
class TypeResolver(private val operatorRegistry: OperatorRegistry) {

    /** Tracks expressions currently being resolved to detect circular references. */
    private val inProgress = mutableSetOf<String>()

    /** Per-scope operand types for function body resolution. */
    private var operandScope: Map<String, DataType> = emptyMap()

    /** Type cache for resolved function definitions. */
    private val functionResultTypes = HashMap<FunctionDefinition, DataType>()

    fun resolve(library: Library, symbolTable: SymbolTable): TypeTable {
        val typeTable = TypeTable()

        // Resolve types for parameter defaults
        for ((_, paramDef) in symbolTable.parameterDefinitions) {
            paramDef.default?.let { inferType(it, typeTable, symbolTable) }
        }

        // Resolve types for expression definition bodies
        for ((_, exprDef) in symbolTable.expressionDefinitions) {
            resolveExpressionDef(exprDef.name.value, typeTable, symbolTable)
        }

        // Resolve types for function definition bodies
        for ((_, funcDefs) in symbolTable.functionDefinitions) {
            for (funcDef in funcDefs) {
                resolveFunctionDef(funcDef, typeTable, symbolTable)
            }
        }

        return typeTable
    }

    @Suppress("ReturnCount")
    private fun resolveExpressionDef(
        name: String,
        typeTable: TypeTable,
        symbolTable: SymbolTable,
    ): DataType? {
        if (name in inProgress) return null // Circular reference
        val exprDef = symbolTable.expressionDefinitions[name] ?: return null
        // Already resolved?
        typeTable[exprDef.expression]?.let {
            return it
        }
        inProgress.add(name)
        try {
            return inferType(exprDef.expression, typeTable, symbolTable)
        } finally {
            inProgress.remove(name)
        }
    }

    @Suppress("ReturnCount")
    private fun resolveFunctionDef(
        funcDef: FunctionDefinition,
        typeTable: TypeTable,
        symbolTable: SymbolTable,
    ): DataType? {
        functionResultTypes[funcDef]?.let {
            return it
        }
        val body = funcDef.body
        if (body !is ExpressionFunctionBody) return null

        // Build operand scope
        val scope = mutableMapOf<String, DataType>()
        for (operand in funcDef.operands) {
            val type = resolveTypeSpecifier(operand.type) ?: continue
            scope[operand.name.value] = type
        }

        val previousScope = operandScope
        operandScope = scope
        try {
            val resultType = inferType(body.expression, typeTable, symbolTable)
            if (resultType != null) {
                functionResultTypes[funcDef] = resultType
            }
            return resultType
        } finally {
            operandScope = previousScope
        }
    }

    private fun resolveTypeSpecifier(typeSpec: org.hl7.cql.ast.TypeSpecifier): DataType? {
        return when (typeSpec) {
            is NamedTypeSpecifier -> operatorRegistry.type(typeSpec.name.simpleName)
            else -> null
        }
    }

    /**
     * Infer the type of an expression and store it in the [TypeTable]. Returns the inferred type,
     * or null if the type cannot be determined.
     */
    @Suppress("CyclomaticComplexMethod")
    internal fun inferType(
        expression: Expression,
        typeTable: TypeTable,
        symbolTable: SymbolTable,
    ): DataType? {
        typeTable[expression]?.let {
            return it
        }

        val type =
            when (expression) {
                is LiteralExpression -> inferLiteralType(expression.literal)
                is OperatorBinaryExpression -> inferBinaryType(expression, typeTable, symbolTable)
                is OperatorUnaryExpression -> inferUnaryType(expression, typeTable, symbolTable)
                is BooleanTestExpression -> inferBooleanTestType(expression, typeTable, symbolTable)
                is IfExpression -> inferIfType(expression, typeTable, symbolTable)
                is FunctionCallExpression ->
                    inferFunctionCallType(expression, typeTable, symbolTable)
                is IndexExpression -> inferIndexType(expression, typeTable, symbolTable)
                is IdentifierExpression -> inferIdentifierType(expression, typeTable, symbolTable)
                else -> null
            }

        if (type != null) {
            typeTable[expression] = type
        }
        return type
    }

    @Suppress("ReturnCount")
    private fun inferIdentifierType(
        expression: IdentifierExpression,
        typeTable: TypeTable,
        symbolTable: SymbolTable,
    ): DataType? {
        val name = expression.name.simpleName

        // Check operand scope first (for function body resolution)
        operandScope[name]?.let { type ->
            typeTable.setIdentifierResolution(expression, Resolution.OperandRef(name, type))
            return type
        }

        // Check expression definitions (with forward reference support)
        symbolTable.resolveExpression(name)?.let { resolution ->
            typeTable.setIdentifierResolution(expression, resolution)
            return resolveExpressionDef(name, typeTable, symbolTable)
        }

        // Check parameter definitions
        symbolTable.resolveParameter(name)?.let { resolution ->
            typeTable.setIdentifierResolution(expression, resolution)
            val paramDef = resolution.definition
            // Try declared type first
            paramDef.type?.let { typeSpec ->
                resolveTypeSpecifier(typeSpec)?.let {
                    return it
                }
            }
            // Fall back to default expression type
            paramDef.default?.let {
                return inferType(it, typeTable, symbolTable)
            }
            return null
        }

        return null
    }

    @Suppress("CyclomaticComplexMethod")
    private fun inferLiteralType(literal: Literal): DataType? {
        return when (literal) {
            is IntLiteral -> operatorRegistry.type("Integer")
            is LongLiteral -> operatorRegistry.type("Long")
            is DecimalLiteral -> operatorRegistry.type("Decimal")
            is StringLiteral -> operatorRegistry.type("String")
            is BooleanLiteral -> operatorRegistry.type("Boolean")
            is QuantityLiteral -> operatorRegistry.type("Quantity")
            is DateTimeLiteral -> {
                if (!literal.text.contains('T')) operatorRegistry.type("Date")
                else operatorRegistry.type("DateTime")
            }
            is TimeLiteral -> operatorRegistry.type("Time")
            is NullLiteral -> null
            else -> null
        }
    }

    @Suppress("ReturnCount")
    private fun inferBinaryType(
        expression: OperatorBinaryExpression,
        typeTable: TypeTable,
        symbolTable: SymbolTable,
    ): DataType? {
        val leftType = inferType(expression.left, typeTable, symbolTable) ?: return null
        val rightType = inferType(expression.right, typeTable, symbolTable) ?: return null
        val opName = binaryOperatorToSystemName(expression.operator) ?: return null
        val resolution =
            operatorRegistry.resolve(opName, listOf(leftType, rightType)) ?: return null
        typeTable.setOperatorResolution(expression, resolution)
        return resolution.operator.resultType
    }

    @Suppress("ReturnCount")
    private fun inferBooleanTestType(
        expression: BooleanTestExpression,
        typeTable: TypeTable,
        symbolTable: SymbolTable,
    ): DataType? {
        val operandType = inferType(expression.operand, typeTable, symbolTable) ?: return null
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
        symbolTable: SymbolTable,
    ): DataType? {
        val operandType = inferType(expression.operand, typeTable, symbolTable) ?: return null
        val opName = unaryOperatorToSystemName(expression.operator) ?: return null
        if (opName == "Positive") return operandType
        val resolution = operatorRegistry.resolve(opName, listOf(operandType)) ?: return null
        typeTable.setOperatorResolution(expression, resolution)
        return resolution.operator.resultType
    }

    /**
     * Infer the type for an if-then-else expression. The result type is the common supertype of the
     * then and else branches.
     */
    @Suppress("ReturnCount")
    private fun inferIfType(
        expression: IfExpression,
        typeTable: TypeTable,
        symbolTable: SymbolTable,
    ): DataType? {
        inferType(expression.condition, typeTable, symbolTable)
        val thenType = inferType(expression.thenBranch, typeTable, symbolTable)
        val elseType = inferType(expression.elseBranch, typeTable, symbolTable)
        return when {
            thenType == null -> elseType
            elseType == null -> thenType
            else -> thenType.getCommonSuperTypeOf(elseType)
        }
    }

    @Suppress("ReturnCount", "CyclomaticComplexMethod")
    private fun inferFunctionCallType(
        expression: FunctionCallExpression,
        typeTable: TypeTable,
        symbolTable: SymbolTable,
    ): DataType? {
        val functionName = expression.function.value
        val argTypes = expression.arguments.map { inferType(it, typeTable, symbolTable) }

        // Coalesce special handling
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

        val nonNullArgTypes = argTypes.filterNotNull()
        if (nonNullArgTypes.size != argTypes.size) return null

        // Try system operator first
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

        // Try user-defined functions
        val candidates = symbolTable.resolveFunctions(functionName)
        for (funcDef in candidates) {
            if (funcDef.operands.size != nonNullArgTypes.size) continue
            val operandTypes = funcDef.operands.map { resolveTypeSpecifier(it.type) }
            if (operandTypes.any { it == null }) continue
            // Simple type matching (exact match)
            val matches =
                operandTypes.zip(nonNullArgTypes).all { (expected, actual) -> expected == actual }
            if (matches) {
                return resolveFunctionDef(funcDef, typeTable, symbolTable)
            }
        }
        return null
    }

    @Suppress("ReturnCount")
    private fun inferIndexType(
        expression: IndexExpression,
        typeTable: TypeTable,
        symbolTable: SymbolTable,
    ): DataType? {
        val targetType = inferType(expression.target, typeTable, symbolTable) ?: return null
        val indexType = inferType(expression.index, typeTable, symbolTable) ?: return null
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
