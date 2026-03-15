@file:Suppress("TooManyFunctions")

package org.cqframework.cql.cql2elm.analysis

import org.hl7.cql.ast.AliasedQuerySource
import org.hl7.cql.ast.AsExpression
import org.hl7.cql.ast.BetweenExpression
import org.hl7.cql.ast.BooleanLiteral
import org.hl7.cql.ast.BooleanTestExpression
import org.hl7.cql.ast.BooleanTestKind
import org.hl7.cql.ast.CastExpression
import org.hl7.cql.ast.ConversionExpression
import org.hl7.cql.ast.DateTimeComponent
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
import org.hl7.cql.ast.ExpressionQuerySource
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
import org.hl7.cql.model.IntervalType
import org.hl7.cql.model.ListType

/**
 * Walks the AST and infers types for all expressions, populating the [TypeTable]. Uses the
 * [OperatorRegistry] to resolve operator types and store operator resolution results.
 *
 * **Not thread-safe.** Each instance maintains mutable state (`inProgress`, `operandScope`,
 * `functionResultTypes`) and must not be shared across threads or reused after [resolve] returns. A
 * fresh instance is created per [CompilerFrontend.analyze] call.
 */
class TypeResolver(private val operatorRegistry: OperatorRegistry) {

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
        if (name in inProgressExpressions) return null // Circular reference — illegal in CQL
        val exprDef = symbolTable.expressionDefinitions[name] ?: return null
        // Already resolved?
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
        if (funcDef in inProgressFunctions) return null // Circular reference — illegal in CQL
        val body = funcDef.body
        if (body !is ExpressionFunctionBody) return null

        inProgressFunctions.add(funcDef)

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
                is IfExpression -> inferIfType(expression, typeTable, symbolTable)
                is FunctionCallExpression ->
                    inferFunctionCallType(expression, typeTable, symbolTable)
                is IndexExpression -> inferIndexType(expression, typeTable, symbolTable)
                is IdentifierExpression -> inferIdentifierType(expression, typeTable, symbolTable)
                is IsExpression -> inferIsType(expression, typeTable, symbolTable)
                is AsExpression -> inferAsType(expression, typeTable, symbolTable)
                is CastExpression -> inferCastType(expression, typeTable, symbolTable)
                is ConversionExpression -> inferConversionType(expression, typeTable, symbolTable)
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
        // Infer target type for fluent calls (not yet used for resolution, but ensures
        // the target expression is typed for future milestones)
        expression.target?.let { inferType(it, typeTable, symbolTable) }

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

        // Try user-defined functions with subtype matching
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

    private fun inferIsType(
        expression: IsExpression,
        typeTable: TypeTable,
        symbolTable: SymbolTable,
    ): DataType? {
        inferType(expression.operand, typeTable, symbolTable)
        return operatorRegistry.type("Boolean")
    }

    private fun inferAsType(
        expression: AsExpression,
        typeTable: TypeTable,
        symbolTable: SymbolTable,
    ): DataType? {
        inferType(expression.operand, typeTable, symbolTable)
        return resolveTypeSpecifier(expression.type)
    }

    private fun inferCastType(
        expression: CastExpression,
        typeTable: TypeTable,
        symbolTable: SymbolTable,
    ): DataType? {
        inferType(expression.operand, typeTable, symbolTable)
        return resolveTypeSpecifier(expression.type)
    }

    @Suppress("ReturnCount")
    private fun inferConversionType(
        expression: ConversionExpression,
        typeTable: TypeTable,
        symbolTable: SymbolTable,
    ): DataType? {
        inferType(expression.operand, typeTable, symbolTable)
        val destType = expression.destinationType ?: return null
        return resolveTypeSpecifier(destType)
    }

    @Suppress("CyclomaticComplexMethod")
    private fun inferDateTimeComponentType(
        expression: DateTimeComponentExpression,
        typeTable: TypeTable,
        symbolTable: SymbolTable,
    ): DataType? {
        inferType(expression.operand, typeTable, symbolTable)
        return when (expression.component) {
            DateTimeComponent.DATE -> operatorRegistry.type("Date")
            DateTimeComponent.TIME -> operatorRegistry.type("Time")
            DateTimeComponent.TIMEZONE_OFFSET -> operatorRegistry.type("Decimal")
            else -> operatorRegistry.type("Integer")
        }
    }

    private fun inferDurationBetweenType(
        expression: DurationBetweenExpression,
        typeTable: TypeTable,
        symbolTable: SymbolTable,
    ): DataType? {
        inferType(expression.lower, typeTable, symbolTable)
        inferType(expression.upper, typeTable, symbolTable)
        return operatorRegistry.type("Integer")
    }

    private fun inferDifferenceBetweenType(
        expression: DifferenceBetweenExpression,
        typeTable: TypeTable,
        symbolTable: SymbolTable,
    ): DataType? {
        inferType(expression.lower, typeTable, symbolTable)
        inferType(expression.upper, typeTable, symbolTable)
        return operatorRegistry.type("Integer")
    }

    @Suppress("ReturnCount")
    private fun inferTimeBoundaryType(
        expression: TimeBoundaryExpression,
        typeTable: TypeTable,
        symbolTable: SymbolTable,
    ): DataType? {
        val operandType = inferType(expression.operand, typeTable, symbolTable) ?: return null
        if (operandType is IntervalType) return operandType.pointType
        return operandType
    }

    @Suppress("ReturnCount")
    private fun inferWidthType(
        expression: WidthExpression,
        typeTable: TypeTable,
        symbolTable: SymbolTable,
    ): DataType? {
        val operandType = inferType(expression.operand, typeTable, symbolTable) ?: return null
        if (operandType is IntervalType) return operandType.pointType
        return operandType
    }

    @Suppress("ReturnCount")
    private fun inferElementExtractorType(
        expression: ElementExtractorExpression,
        typeTable: TypeTable,
        symbolTable: SymbolTable,
    ): DataType? {
        val operandType = inferType(expression.operand, typeTable, symbolTable) ?: return null
        return when (expression.elementExtractorKind) {
            org.hl7.cql.ast.ElementExtractorKind.SINGLETON -> {
                if (operandType is ListType) operandType.elementType else operandType
            }
            org.hl7.cql.ast.ElementExtractorKind.POINT -> {
                if (operandType is IntervalType) operandType.pointType else operandType
            }
        }
    }

    private fun inferTypeExtentType(expression: TypeExtentExpression): DataType? {
        return resolveTypeSpecifier(expression.type)
    }

    private fun inferExistsType(
        expression: ExistsExpression,
        typeTable: TypeTable,
        symbolTable: SymbolTable,
    ): DataType? {
        inferType(expression.operand, typeTable, symbolTable)
        return operatorRegistry.type("Boolean")
    }

    private fun inferBetweenType(
        expression: BetweenExpression,
        typeTable: TypeTable,
        symbolTable: SymbolTable,
    ): DataType? {
        inferType(expression.input, typeTable, symbolTable)
        inferType(expression.lower, typeTable, symbolTable)
        inferType(expression.upper, typeTable, symbolTable)
        return operatorRegistry.type("Boolean")
    }

    private fun inferMembershipType(
        expression: MembershipExpression,
        typeTable: TypeTable,
        symbolTable: SymbolTable,
    ): DataType? {
        inferType(expression.left, typeTable, symbolTable)
        inferType(expression.right, typeTable, symbolTable)
        return operatorRegistry.type("Boolean")
    }

    private fun inferDurationOfType(
        expression: DurationOfExpression,
        typeTable: TypeTable,
        symbolTable: SymbolTable,
    ): DataType? {
        inferType(expression.operand, typeTable, symbolTable)
        return operatorRegistry.type("Integer")
    }

    private fun inferDifferenceOfType(
        expression: DifferenceOfExpression,
        typeTable: TypeTable,
        symbolTable: SymbolTable,
    ): DataType? {
        inferType(expression.operand, typeTable, symbolTable)
        return operatorRegistry.type("Integer")
    }

    private fun inferIntervalRelationType(
        expression: IntervalRelationExpression,
        typeTable: TypeTable,
        symbolTable: SymbolTable,
    ): DataType? {
        inferType(expression.left, typeTable, symbolTable)
        inferType(expression.right, typeTable, symbolTable)
        return operatorRegistry.type("Boolean")
    }

    @Suppress("ReturnCount")
    private fun inferQueryType(
        expression: QueryExpression,
        typeTable: TypeTable,
        symbolTable: SymbolTable,
    ): DataType? {
        // Build scope from sources
        val scope = mutableMapOf<String, Resolution>()
        for (source in expression.sources) {
            val elementType = inferSourceElementType(source, typeTable, symbolTable) ?: continue
            scope[source.alias.value] = Resolution.AliasRef(source.alias.value, elementType)
        }

        // Push scope for lets, where, inclusions, return, aggregate
        queryScopes.add(scope)
        try {
            // Resolve let clause types and add to scope
            for (letItem in expression.lets) {
                val letType = inferType(letItem.expression, typeTable, symbolTable)
                if (letType != null) {
                    scope[letItem.identifier.value] =
                        Resolution.QueryLetRef(letItem.identifier.value, letType)
                }
            }

            // Resolve inclusion clauses
            for (inclusion in expression.inclusions) {
                inferInclusionType(inclusion, typeTable, symbolTable)
            }

            // Resolve where
            expression.where?.let { inferType(it, typeTable, symbolTable) }

            // Resolve aggregate
            expression.aggregate?.let { agg ->
                val aggType = inferAggregateType(agg, expression, scope, typeTable, symbolTable)
                resolveSortItems(expression, typeTable, symbolTable)
                return aggType
            }

            // Resolve return
            val resultType =
                expression.result?.let { ret ->
                    val retType = inferType(ret.expression, typeTable, symbolTable) ?: return null
                    ListType(retType)
                }
                    ?: run {
                        // No return clause: result is List<sourceType>
                        if (expression.sources.size > 1) {
                            throw UnsupportedOperationException(
                                "Multi-source queries without return clause are not yet supported."
                            )
                        }
                        val sourceType =
                            expression.sources.firstOrNull()?.let {
                                inferSourceElementType(it, typeTable, symbolTable)
                            } ?: return null
                        ListType(sourceType)
                    }

            // Resolve sort (runs regardless of whether return clause is present)
            resolveSortItems(expression, typeTable, symbolTable)

            return resultType
        } finally {
            queryScopes.removeAt(queryScopes.lastIndex)
        }
    }

    private fun inferSourceElementType(
        source: AliasedQuerySource,
        typeTable: TypeTable,
        symbolTable: SymbolTable,
    ): DataType? {
        val querySource = source.source
        val sourceType =
            when (querySource) {
                is ExpressionQuerySource ->
                    inferType(querySource.expression, typeTable, symbolTable)
                else -> null
            } ?: return null
        // Unwrap ListType to get element type
        return if (sourceType is ListType) sourceType.elementType else sourceType
    }

    private fun inferInclusionType(
        inclusion: org.hl7.cql.ast.QueryInclusionClause,
        typeTable: TypeTable,
        symbolTable: SymbolTable,
    ) {
        val (source, condition) =
            when (inclusion) {
                is org.hl7.cql.ast.WithClause -> inclusion.source to inclusion.condition
                is org.hl7.cql.ast.WithoutClause -> inclusion.source to inclusion.condition
            }
        val elementType = inferSourceElementType(source, typeTable, symbolTable) ?: return
        val innerScope =
            mapOf(source.alias.value to Resolution.AliasRef(source.alias.value, elementType))
        queryScopes.add(innerScope)
        try {
            inferType(condition, typeTable, symbolTable)
        } finally {
            queryScopes.removeAt(queryScopes.lastIndex)
        }
    }

    private fun resolveSortItems(
        expression: QueryExpression,
        typeTable: TypeTable,
        symbolTable: SymbolTable,
    ) {
        expression.sort?.let { sort ->
            for (item in sort.items) {
                inferType(item.expression, typeTable, symbolTable)
            }
        }
    }

    @Suppress("ReturnCount")
    private fun inferAggregateType(
        agg: org.hl7.cql.ast.AggregateClause,
        query: QueryExpression,
        scope: MutableMap<String, Resolution>,
        typeTable: TypeTable,
        symbolTable: SymbolTable,
    ): DataType? {
        // Resolve starting expression type
        val startingType =
            agg.starting?.let { inferType(it, typeTable, symbolTable) }
                ?: operatorRegistry.type("Any")

        // Add accumulator to scope — legacy uses AliasRef for the accumulator identifier
        if (startingType != null) {
            scope[agg.identifier.value] = Resolution.AliasRef(agg.identifier.value, startingType)
        }

        val aggType = inferType(agg.expression, typeTable, symbolTable)
        return aggType
    }
}
