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
import org.hl7.cql.ast.ExpandCollapseExpression
import org.hl7.cql.ast.Expression
import org.hl7.cql.ast.ExpressionFold
import org.hl7.cql.ast.ExpressionFunctionBody
import org.hl7.cql.ast.ExternalConstantExpression
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
import org.hl7.cql.ast.PropertyAccessExpression
import org.hl7.cql.ast.QuantityLiteral
import org.hl7.cql.ast.QueryExpression
import org.hl7.cql.ast.RetrieveExpression
import org.hl7.cql.ast.StringLiteral
import org.hl7.cql.ast.TimeBoundaryExpression
import org.hl7.cql.ast.TimeLiteral
import org.hl7.cql.ast.TypeExtentExpression
import org.hl7.cql.ast.UnsupportedExpression
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
class TypeResolver(internal val operatorRegistry: OperatorRegistry) : ExpressionFold<DataType?> {

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

    /** The type table for the current resolution pass. Set by [resolve] before use. */
    internal lateinit var typeTable: TypeTable

    /** The symbol table for the current resolution pass. Set by [resolve] before use. */
    internal lateinit var symbolTable: SymbolTable

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
        this.typeTable = typeTable
        this.symbolTable = symbolTable

        for ((_, paramDef) in symbolTable.parameterDefinitions) {
            paramDef.default?.let { inferType(it) }
        }

        for ((_, exprDef) in symbolTable.expressionDefinitions) {
            resolveExpressionDef(exprDef.name.value)
        }

        for ((_, funcDefs) in symbolTable.functionDefinitions) {
            for (funcDef in funcDefs) {
                resolveFunctionDef(funcDef)
            }
        }

        return typeTable
    }

    @Suppress("ReturnCount")
    private fun resolveExpressionDef(name: String): DataType? {
        if (name in inProgressExpressions) return null
        val exprDef = symbolTable.expressionDefinitions[name] ?: return null
        typeTable[exprDef.expression]?.let {
            return it
        }
        inProgressExpressions.add(name)
        try {
            return inferType(exprDef.expression)
        } finally {
            inProgressExpressions.remove(name)
        }
    }

    @Suppress("ReturnCount")
    private fun resolveFunctionDef(funcDef: FunctionDefinition): DataType? {
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
            val resultType = inferType(body.expression)
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
    fun inferType(expression: Expression): DataType? {
        typeTable[expression]?.let {
            return it
        }

        val type = fold(expression)

        if (type != null) {
            typeTable[expression] = type
        }
        return type
    }

    // --- ExpressionFold<DataType?> implementation ---

    override fun onLiteral(expr: LiteralExpression): DataType? = inferLiteralType(expr.literal)

    override fun onIdentifier(expr: IdentifierExpression): DataType? = inferIdentifierType(expr)

    override fun onExternalConstant(expr: ExternalConstantExpression): DataType? = null

    override fun onBinaryOperator(expr: OperatorBinaryExpression): DataType? = inferBinaryType(expr)

    override fun onUnaryOperator(expr: OperatorUnaryExpression): DataType? = inferUnaryType(expr)

    override fun onBooleanTest(expr: BooleanTestExpression): DataType? = inferBooleanTestType(expr)

    override fun onIf(expr: IfExpression): DataType? = inferIfType(expr)

    override fun onCase(expr: CaseExpression): DataType? = inferCaseType(expr)

    override fun onIs(expr: IsExpression): DataType? = inferIsType(expr)

    override fun onAs(expr: AsExpression): DataType? = inferAsType(expr)

    override fun onCast(expr: CastExpression): DataType? = inferCastType(expr)

    override fun onConversion(expr: ConversionExpression): DataType? = inferConversionType(expr)

    override fun onFunctionCall(expr: FunctionCallExpression): DataType? =
        inferFunctionCallType(expr)

    override fun onPropertyAccess(expr: PropertyAccessExpression): DataType? = null

    override fun onIndex(expr: IndexExpression): DataType? = inferIndexType(expr)

    override fun onExists(expr: ExistsExpression): DataType? = inferExistsType(expr)

    override fun onMembership(expr: MembershipExpression): DataType? = inferMembershipType(expr)

    override fun onListTransform(expr: ListTransformExpression): DataType? =
        inferListTransformType(expr)

    override fun onExpandCollapse(expr: ExpandCollapseExpression): DataType? = null

    override fun onDateTimeComponent(expr: DateTimeComponentExpression): DataType? =
        inferDateTimeComponentType(expr)

    override fun onDurationBetween(expr: DurationBetweenExpression): DataType? =
        inferDurationBetweenType(expr)

    override fun onDifferenceBetween(expr: DifferenceBetweenExpression): DataType? =
        inferDifferenceBetweenType(expr)

    override fun onDurationOf(expr: DurationOfExpression): DataType? = inferDurationOfType(expr)

    override fun onDifferenceOf(expr: DifferenceOfExpression): DataType? =
        inferDifferenceOfType(expr)

    override fun onTimeBoundary(expr: TimeBoundaryExpression): DataType? =
        inferTimeBoundaryType(expr)

    override fun onWidth(expr: WidthExpression): DataType? = inferWidthType(expr)

    override fun onElementExtractor(expr: ElementExtractorExpression): DataType? =
        inferElementExtractorType(expr)

    override fun onTypeExtent(expr: TypeExtentExpression): DataType? = inferTypeExtentType(expr)

    override fun onBetween(expr: BetweenExpression): DataType? = inferBetweenType(expr)

    override fun onIntervalRelation(expr: IntervalRelationExpression): DataType? =
        inferIntervalRelationType(expr)

    override fun onQuery(expr: QueryExpression): DataType? = inferQueryType(expr)

    override fun onRetrieve(expr: RetrieveExpression): DataType? = null

    override fun onUnsupported(expr: UnsupportedExpression): DataType? = null

    // --- Private inference methods ---

    @Suppress("ReturnCount")
    private fun inferIdentifierType(expression: IdentifierExpression): DataType? {
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
            return resolveExpressionDef(name)
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
                return inferType(it)
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
            is ListLiteral -> inferListLiteralType(literal)
            else -> null
        }
    }

    private fun inferListLiteralType(literal: ListLiteral): DataType? {
        val elementTypes = literal.elements.mapNotNull { inferType(it) }
        if (elementTypes.isEmpty()) return ListType(operatorRegistry.type("Any") ?: return null)
        val commonType = elementTypes.reduce { acc, type -> acc.getCommonSuperTypeOf(type) }
        return ListType(commonType)
    }

    @Suppress("ReturnCount")
    private fun inferBinaryType(expression: OperatorBinaryExpression): DataType? {
        val leftType = inferType(expression.left) ?: return null
        val rightType = inferType(expression.right) ?: return null
        val opName = OperatorNames.binaryOperatorToSystemName(expression.operator) ?: return null
        val resolution =
            operatorRegistry.resolve(opName, listOf(leftType, rightType)) ?: return null
        typeTable.setOperatorResolution(expression, resolution)
        return resolution.operator.resultType
    }

    @Suppress("ReturnCount")
    private fun inferBooleanTestType(expression: BooleanTestExpression): DataType? {
        val operandType = inferType(expression.operand) ?: return null
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
    private fun inferUnaryType(expression: OperatorUnaryExpression): DataType? {
        val operandType = inferType(expression.operand) ?: return null
        val opName = OperatorNames.unaryOperatorToSystemName(expression.operator) ?: return null
        if (opName == "Positive") return operandType
        val resolution = operatorRegistry.resolve(opName, listOf(operandType)) ?: return null
        typeTable.setOperatorResolution(expression, resolution)
        return resolution.operator.resultType
    }

    @Suppress("ReturnCount")
    private fun inferIfType(expression: IfExpression): DataType? {
        inferType(expression.condition)
        val thenType = inferType(expression.thenBranch)
        val elseType = inferType(expression.elseBranch)
        return when {
            thenType == null -> elseType
            elseType == null -> thenType
            else -> thenType.getCommonSuperTypeOf(elseType)
        }
    }

    private fun inferCaseType(expression: CaseExpression): DataType? {
        expression.comparand?.let { inferType(it) }
        val branchTypes =
            expression.cases.mapNotNull { caseItem ->
                inferType(caseItem.condition)
                inferType(caseItem.result)
            }
        val elseType = inferType(expression.elseResult)
        val allTypes = branchTypes + listOfNotNull(elseType)
        if (allTypes.isEmpty()) return null
        return allTypes.reduce { acc, type -> acc.getCommonSuperTypeOf(type) }
    }

    @Suppress("ReturnCount", "CyclomaticComplexMethod")
    private fun inferFunctionCallType(expression: FunctionCallExpression): DataType? {
        expression.target?.let { inferType(it) }

        val functionName = expression.function.value
        val argTypes = expression.arguments.map { inferType(it) }

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
                return resolveFunctionDef(funcDef)
            }
        }
        return null
    }

    @Suppress("ReturnCount")
    private fun inferIndexType(expression: IndexExpression): DataType? {
        val targetType = inferType(expression.target) ?: return null
        val indexType = inferType(expression.index) ?: return null
        val resolution =
            operatorRegistry.resolve("Indexer", listOf(targetType, indexType)) ?: return null
        typeTable.setOperatorResolution(expression, resolution)
        return resolution.operator.resultType
    }

    @Suppress("ReturnCount")
    private fun inferListTransformType(expression: ListTransformExpression): DataType? {
        val operandType = inferType(expression.operand) ?: return null
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
