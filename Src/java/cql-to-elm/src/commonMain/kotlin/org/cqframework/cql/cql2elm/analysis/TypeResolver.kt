@file:Suppress("TooManyFunctions")

package org.cqframework.cql.cql2elm.analysis

import org.hl7.cql.ast.AsExpression
import org.hl7.cql.ast.BetweenExpression
import org.hl7.cql.ast.BooleanLiteral
import org.hl7.cql.ast.BooleanTestExpression
import org.hl7.cql.ast.BooleanTestKind
import org.hl7.cql.ast.CaseExpression
import org.hl7.cql.ast.CastExpression
import org.hl7.cql.ast.ConversionExpression
import org.hl7.cql.ast.DateTimeComponentExpression
import org.hl7.cql.ast.DateTimeLiteral
import org.hl7.cql.ast.DecimalLiteral
import org.hl7.cql.ast.DifferenceBetweenExpression
import org.hl7.cql.ast.DifferenceOfExpression
import org.hl7.cql.ast.DurationBetweenExpression
import org.hl7.cql.ast.DurationOfExpression
import org.hl7.cql.ast.ElementExtractorExpression
import org.hl7.cql.ast.ExistsExpression
import org.hl7.cql.ast.Expression
import org.hl7.cql.ast.ExpressionFunctionBody
import org.hl7.cql.ast.FunctionCallExpression
import org.hl7.cql.ast.FunctionDefinition
import org.hl7.cql.ast.IdentifierExpression
import org.hl7.cql.ast.IfExpression
import org.hl7.cql.ast.IndexExpression
import org.hl7.cql.ast.IntLiteral
import org.hl7.cql.ast.IntervalRelationExpression
import org.hl7.cql.ast.IsExpression
import org.hl7.cql.ast.Library
import org.hl7.cql.ast.ListLiteral
import org.hl7.cql.ast.ListTransformExpression
import org.hl7.cql.ast.ListTransformKind
import org.hl7.cql.ast.Literal
import org.hl7.cql.ast.LiteralExpression
import org.hl7.cql.ast.LongLiteral
import org.hl7.cql.ast.MembershipExpression
import org.hl7.cql.ast.NamedTypeSpecifier
import org.hl7.cql.ast.NullLiteral
import org.hl7.cql.ast.OperatorBinaryExpression
import org.hl7.cql.ast.OperatorUnaryExpression
import org.hl7.cql.ast.QuantityLiteral
import org.hl7.cql.ast.QueryExpression
import org.hl7.cql.ast.StringLiteral
import org.hl7.cql.ast.TimeBoundaryExpression
import org.hl7.cql.ast.TimeLiteral
import org.hl7.cql.ast.TypeExtentExpression
import org.hl7.cql.ast.WidthExpression
import org.hl7.cql.model.DataType
import org.hl7.cql.model.ListType

/**
 * Walks the AST and infers types for all expressions, populating the [TypeTable]. Uses the
 * [OperatorRegistry] to resolve operator types and store operator resolution results.
 *
 * **Not thread-safe.** Each instance maintains mutable state and must not be shared across threads
 * or reused after [resolve] returns. A fresh instance is created per [CompilerFrontend.analyze]
 * call.
 *
 * Type inference for specific expression categories is split into extension function files:
 * - [TypeOperatorInference.kt] — is/as/cast/convert
 * - [TemporalTypeInference.kt] — date/time, interval, collection operators
 * - [QueryTypeInference.kt] — query expressions with scoping
 */
class TypeResolver(internal val operatorRegistry: OperatorRegistry) {

    /** Tracks expression definitions currently being resolved to detect circular references. */
    private val inProgressExpressions = mutableSetOf<String>()

    /** Tracks function definitions currently being resolved to detect circular references. */
    private val inProgressFunctions = mutableSetOf<FunctionDefinition>()

    /** Per-scope operand types for function body resolution. */
    private var operandScope: Map<String, DataType> = emptyMap()

    /** Query scope stack: maps alias/let names to their Resolution within a query. */
    private val queryScopes = mutableListOf<Map<String, Resolution>>()

    /** Type cache for resolved function definitions. */
    private val functionResultTypes = HashMap<FunctionDefinition, DataType>()

    /** Shorthand for resolving a System type by name. Used by extension functions. */
    internal fun type(name: String): DataType? = operatorRegistry.type(name)

    /** Push a query scope onto the stack. Used by [QueryTypeInference]. */
    internal fun pushQueryScope(scope: Map<String, Resolution>) {
        queryScopes.add(scope)
    }

    /** Pop the innermost query scope. Used by [QueryTypeInference]. */
    internal fun popQueryScope() {
        queryScopes.removeAt(queryScopes.lastIndex)
    }

    fun resolve(library: Library, symbolTable: SymbolTable): TypeTable {
        val typeTable = TypeTable()

        for ((_, paramDef) in symbolTable.parameterDefinitions) {
            paramDef.default?.let { inferType(it, typeTable, symbolTable) }
        }

        for ((_, exprDef) in symbolTable.expressionDefinitions) {
            resolveExpressionDef(exprDef.name.value, typeTable, symbolTable)
        }

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
        if (name in inProgressExpressions) return null
        val exprDef = symbolTable.expressionDefinitions[name] ?: return null
        typeTable[exprDef.expression]?.let {
            return it
        }
        inProgressExpressions.add(name)
        try {
            return inferType(exprDef.expression, typeTable, symbolTable)
        } finally {
            inProgressExpressions.remove(name)
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
        if (funcDef in inProgressFunctions) return null
        val body = funcDef.body
        if (body !is ExpressionFunctionBody) return null

        inProgressFunctions.add(funcDef)
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
            inProgressFunctions.remove(funcDef)
        }
    }

    internal fun resolveTypeSpecifier(typeSpec: org.hl7.cql.ast.TypeSpecifier): DataType? {
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
                is LiteralExpression -> inferLiteralType(expression.literal, typeTable, symbolTable)
                is OperatorBinaryExpression -> inferBinaryType(expression, typeTable, symbolTable)
                is OperatorUnaryExpression -> inferUnaryType(expression, typeTable, symbolTable)
                is BooleanTestExpression -> inferBooleanTestType(expression, typeTable, symbolTable)
                is CaseExpression -> inferCaseType(expression, typeTable, symbolTable)
                is IfExpression -> inferIfType(expression, typeTable, symbolTable)
                is FunctionCallExpression ->
                    inferFunctionCallType(expression, typeTable, symbolTable)
                is IndexExpression -> inferIndexType(expression, typeTable, symbolTable)
                is IdentifierExpression -> inferIdentifierType(expression, typeTable, symbolTable)
                // Type operators (TypeOperatorInference.kt)
                is IsExpression -> inferIsType(expression, typeTable, symbolTable)
                is AsExpression -> inferAsType(expression, typeTable, symbolTable)
                is CastExpression -> inferCastType(expression, typeTable, symbolTable)
                is ConversionExpression -> inferConversionType(expression, typeTable, symbolTable)
                // Temporal/interval/collection (TemporalTypeInference.kt)
                is DateTimeComponentExpression ->
                    inferDateTimeComponentType(expression, typeTable, symbolTable)
                is DurationBetweenExpression ->
                    inferDurationBetweenType(expression, typeTable, symbolTable)
                is DifferenceBetweenExpression ->
                    inferDifferenceBetweenType(expression, typeTable, symbolTable)
                is DurationOfExpression -> inferDurationOfType(expression, typeTable, symbolTable)
                is DifferenceOfExpression ->
                    inferDifferenceOfType(expression, typeTable, symbolTable)
                is TimeBoundaryExpression ->
                    inferTimeBoundaryType(expression, typeTable, symbolTable)
                is WidthExpression -> inferWidthType(expression, typeTable, symbolTable)
                is ElementExtractorExpression ->
                    inferElementExtractorType(expression, typeTable, symbolTable)
                is TypeExtentExpression -> inferTypeExtentType(expression)
                is ExistsExpression -> inferExistsType(expression, typeTable, symbolTable)
                is BetweenExpression -> inferBetweenType(expression, typeTable, symbolTable)
                is MembershipExpression -> inferMembershipType(expression, typeTable, symbolTable)
                is IntervalRelationExpression ->
                    inferIntervalRelationType(expression, typeTable, symbolTable)
                // List operators
                is ListTransformExpression ->
                    inferListTransformType(expression, typeTable, symbolTable)
                // Queries (QueryTypeInference.kt)
                is QueryExpression -> inferQueryType(expression, typeTable, symbolTable)
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

        // Check query scope first (innermost scope wins)
        for (i in queryScopes.indices.reversed()) {
            queryScopes[i][name]?.let { resolution ->
                typeTable.setIdentifierResolution(expression, resolution)
                return when (resolution) {
                    is Resolution.AliasRef -> resolution.type
                    is Resolution.QueryLetRef -> resolution.type
                    else -> null
                }
            }
        }

        // Check operand scope (for function body resolution)
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
            paramDef.type?.let { typeSpec ->
                resolveTypeSpecifier(typeSpec)?.let {
                    return it
                }
            }
            paramDef.default?.let {
                return inferType(it, typeTable, symbolTable)
            }
            return null
        }

        // Check terminology definitions
        symbolTable.resolveCodeSystem(name)?.let { resolution ->
            typeTable.setIdentifierResolution(expression, resolution)
            return type("CodeSystem")
        }
        symbolTable.resolveValueSet(name)?.let { resolution ->
            typeTable.setIdentifierResolution(expression, resolution)
            return type("ValueSet")
        }
        symbolTable.resolveCode(name)?.let { resolution ->
            typeTable.setIdentifierResolution(expression, resolution)
            return type("Code")
        }
        symbolTable.resolveConcept(name)?.let { resolution ->
            typeTable.setIdentifierResolution(expression, resolution)
            return type("Concept")
        }

        return null
    }

    @Suppress("CyclomaticComplexMethod")
    private fun inferLiteralType(
        literal: Literal,
        typeTable: TypeTable,
        symbolTable: SymbolTable,
    ): DataType? {
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
            is ListLiteral -> inferListLiteralType(literal, typeTable, symbolTable)
            else -> null
        }
    }

    private fun inferListLiteralType(
        literal: ListLiteral,
        typeTable: TypeTable,
        symbolTable: SymbolTable,
    ): DataType? {
        val elementTypes = literal.elements.mapNotNull { inferType(it, typeTable, symbolTable) }
        if (elementTypes.isEmpty()) return ListType(operatorRegistry.type("Any") ?: return null)
        val commonType = elementTypes.reduce { acc, type -> acc.getCommonSuperTypeOf(type) }
        return ListType(commonType)
    }

    @Suppress("ReturnCount")
    private fun inferBinaryType(
        expression: OperatorBinaryExpression,
        typeTable: TypeTable,
        symbolTable: SymbolTable,
    ): DataType? {
        val leftType = inferType(expression.left, typeTable, symbolTable) ?: return null
        val rightType = inferType(expression.right, typeTable, symbolTable) ?: return null
        val opName = OperatorNames.binaryOperatorToSystemName(expression.operator) ?: return null
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
        val opName = OperatorNames.unaryOperatorToSystemName(expression.operator) ?: return null
        if (opName == "Positive") return operandType
        val resolution = operatorRegistry.resolve(opName, listOf(operandType)) ?: return null
        typeTable.setOperatorResolution(expression, resolution)
        return resolution.operator.resultType
    }

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

    private fun inferCaseType(
        expression: CaseExpression,
        typeTable: TypeTable,
        symbolTable: SymbolTable,
    ): DataType? {
        expression.comparand?.let { inferType(it, typeTable, symbolTable) }
        val branchTypes =
            expression.cases.mapNotNull { caseItem ->
                inferType(caseItem.condition, typeTable, symbolTable)
                inferType(caseItem.result, typeTable, symbolTable)
            }
        val elseType = inferType(expression.elseResult, typeTable, symbolTable)
        val allTypes = branchTypes + listOfNotNull(elseType)
        if (allTypes.isEmpty()) return null
        return allTypes.reduce { acc, type -> acc.getCommonSuperTypeOf(type) }
    }

    @Suppress("ReturnCount", "CyclomaticComplexMethod")
    private fun inferFunctionCallType(
        expression: FunctionCallExpression,
        typeTable: TypeTable,
        symbolTable: SymbolTable,
    ): DataType? {
        expression.target?.let { inferType(it, typeTable, symbolTable) }

        val functionName = expression.function.value
        val argTypes = expression.arguments.map { inferType(it, typeTable, symbolTable) }

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

        val candidates = symbolTable.resolveFunctions(functionName)
        for (funcDef in candidates) {
            if (funcDef.operands.size != nonNullArgTypes.size) continue
            val operandTypes = funcDef.operands.map { resolveTypeSpecifier(it.type) }
            if (operandTypes.any { it == null }) continue
            val matches =
                operandTypes.zip(nonNullArgTypes).all { (expected, actual) ->
                    expected == actual || actual.isSubTypeOf(expected!!)
                }
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

    @Suppress("ReturnCount")
    private fun inferListTransformType(
        expression: ListTransformExpression,
        typeTable: TypeTable,
        symbolTable: SymbolTable,
    ): DataType? {
        val operandType = inferType(expression.operand, typeTable, symbolTable) ?: return null
        val opName =
            when (expression.listTransformKind) {
                ListTransformKind.DISTINCT -> "Distinct"
                ListTransformKind.FLATTEN -> "Flatten"
            }
        val resolution = operatorRegistry.resolve(opName, listOf(operandType)) ?: return null
        typeTable.setOperatorResolution(expression, resolution)
        return resolution.operator.resultType
    }
}
