@file:Suppress("TooManyFunctions")

package org.cqframework.cql.cql2elm.analysis

import org.cqframework.cql.cql2elm.ModelManager
import org.cqframework.cql.cql2elm.model.Conversion
import org.cqframework.cql.cql2elm.model.OperatorResolution
import org.hl7.cql.ast.AsExpression
import org.hl7.cql.ast.BetweenExpression
import org.hl7.cql.ast.BooleanLiteral
import org.hl7.cql.ast.BooleanTestExpression
import org.hl7.cql.ast.BooleanTestKind
import org.hl7.cql.ast.CaseChildren
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
import org.hl7.cql.ast.ImplicitCastExpression
import org.hl7.cql.ast.IndexExpression
import org.hl7.cql.ast.IntLiteral
import org.hl7.cql.ast.IntervalExpression
import org.hl7.cql.ast.IntervalRelationExpression
import org.hl7.cql.ast.IsExpression
import org.hl7.cql.ast.Library
import org.hl7.cql.ast.ListLiteral
import org.hl7.cql.ast.ListTransformExpression
import org.hl7.cql.ast.ListTransformKind
import org.hl7.cql.ast.Literal
import org.hl7.cql.ast.LiteralChildren
import org.hl7.cql.ast.LiteralExpression
import org.hl7.cql.ast.LongLiteral
import org.hl7.cql.ast.MembershipExpression
import org.hl7.cql.ast.NamedTypeSpecifier
import org.hl7.cql.ast.NullLiteral
import org.hl7.cql.ast.OperatorBinaryExpression
import org.hl7.cql.ast.OperatorUnaryExpression
import org.hl7.cql.ast.PropertyAccessExpression
import org.hl7.cql.ast.QuantityLiteral
import org.hl7.cql.ast.QueryChildren
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
import org.hl7.cql.model.TupleType
import org.hl7.cql.model.TupleTypeElement

/**
 * Walks the AST and infers types for all expressions, populating the [TypeTable]. Uses the
 * [OperatorRegistry] to resolve operator types and store operator resolution results.
 *
 * **Not thread-safe.** Each instance maintains mutable state and must not be shared across threads
 * or reused after [resolve] returns. A fresh instance is created per [SemanticAnalyzer.analyze]
 * call.
 *
 * Type inference for specific expression categories is split into extension function files:
 * - [TypeOperatorInference.kt] — is/as/cast/convert
 * - [TemporalTypeInference.kt] — date/time, interval, collection operators
 * - [QueryTypeInference.kt] — query expressions with scoping
 */
class TypeResolver(
    internal val operatorRegistry: OperatorRegistry,
    private val syntheticTable: SyntheticTable? = null,
    private val modelManager: ModelManager? = null,
) : ExpressionFold<DataType?> {

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
            is org.hl7.cql.ast.ListTypeSpecifier -> {
                val elementType = resolveTypeSpecifier(typeSpec.elementType) ?: return null
                ListType(elementType)
            }
            is org.hl7.cql.ast.IntervalTypeSpecifier -> {
                val pointType = resolveTypeSpecifier(typeSpec.pointType) ?: return null
                org.hl7.cql.model.IntervalType(pointType)
            }
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
        typeTable.expressionCount++

        // QueryExpression needs special handling: its children must be folded within scoping
        // context (aliases, lets) that the catamorphism cannot provide (it pre-folds all
        // children before calling onQuery). So we short-circuit to inferQueryType() which
        // manages scopes and calls inferType() on children with proper context.
        val type =
            if (expression is QueryExpression) {
                inferQueryType(expression)
            } else {
                super<ExpressionFold>.fold(expression)
            }

        if (type != null) {
            typeTable[expression] = type
        }
        return type
    }

    /**
     * Override [fold] to route through [inferType] for caching. When the catamorphism's default
     * `fold()` body calls `fold(child)`, it goes through `inferType(child)` which checks the cache
     * first, avoiding redundant computation.
     */
    override fun fold(expr: Expression): DataType? = inferType(expr)

    // --- ExpressionFold<DataType?> implementation ---
    // Children are pre-folded by the catamorphism. The pre-folded child results are the cached
    // types from inferType(). Extension functions that previously called inferType() on children
    // directly no longer need to — the child types are available via the typeTable or the
    // pre-folded parameters.

    override fun onLiteral(
        expr: LiteralExpression,
        children: LiteralChildren<DataType?>,
    ): DataType? = inferLiteralType(expr.literal)

    override fun onIdentifier(expr: IdentifierExpression): DataType? = inferIdentifierType(expr)

    override fun onExternalConstant(expr: ExternalConstantExpression): DataType? = null

    override fun onBinaryOperator(
        expr: OperatorBinaryExpression,
        left: DataType?,
        right: DataType?,
    ): DataType? = inferBinaryType(expr, left, right)

    override fun onUnaryOperator(expr: OperatorUnaryExpression, operand: DataType?): DataType? =
        inferUnaryType(expr, operand)

    override fun onBooleanTest(expr: BooleanTestExpression, operand: DataType?): DataType? =
        inferBooleanTestType(expr, operand)

    override fun onIf(
        expr: IfExpression,
        condition: DataType?,
        thenBranch: DataType?,
        elseBranch: DataType?,
    ): DataType? = inferIfType(expr, thenBranch, elseBranch)

    override fun onCase(
        expr: CaseExpression,
        comparand: DataType??,
        cases: List<CaseChildren<DataType?>>,
        elseResult: DataType?,
    ): DataType? = inferCaseType(expr, cases, elseResult)

    override fun onIs(expr: IsExpression, operand: DataType?): DataType? = inferIsType(expr)

    override fun onAs(expr: AsExpression, operand: DataType?): DataType? = inferAsType(expr)

    override fun onImplicitCast(expr: ImplicitCastExpression, operand: DataType?): DataType? =
        resolveTypeSpecifier(expr.type)

    override fun onCast(expr: CastExpression, operand: DataType?): DataType? = inferCastType(expr)

    override fun onConversion(expr: ConversionExpression, operand: DataType?): DataType? =
        inferConversionType(expr)

    override fun onFunctionCall(
        expr: FunctionCallExpression,
        target: DataType??,
        arguments: List<DataType?>,
    ): DataType? = inferFunctionCallType(expr, arguments)

    override fun onPropertyAccess(expr: PropertyAccessExpression, target: DataType?): DataType? {
        if (target == null) return null
        return resolvePropertyType(target, expr.property.value)
    }

    override fun onIndex(expr: IndexExpression, target: DataType?, index: DataType?): DataType? =
        inferIndexType(expr, target, index)

    override fun onExists(expr: ExistsExpression, operand: DataType?): DataType? =
        inferExistsType(expr)

    override fun onMembership(
        expr: MembershipExpression,
        left: DataType?,
        right: DataType?,
    ): DataType? = inferMembershipType(expr)

    override fun onListTransform(expr: ListTransformExpression, operand: DataType?): DataType? =
        inferListTransformType(expr, operand)

    override fun onExpandCollapse(
        expr: ExpandCollapseExpression,
        operand: DataType?,
        per: DataType??,
    ): DataType? = inferExpandCollapseType(expr, operand)

    override fun onDateTimeComponent(
        expr: DateTimeComponentExpression,
        operand: DataType?,
    ): DataType? = inferDateTimeComponentType(expr)

    override fun onDurationBetween(
        expr: DurationBetweenExpression,
        lower: DataType?,
        upper: DataType?,
    ): DataType? = inferDurationBetweenType(expr)

    override fun onDifferenceBetween(
        expr: DifferenceBetweenExpression,
        lower: DataType?,
        upper: DataType?,
    ): DataType? = inferDifferenceBetweenType(expr)

    override fun onDurationOf(expr: DurationOfExpression, operand: DataType?): DataType? =
        inferDurationOfType(expr)

    override fun onDifferenceOf(expr: DifferenceOfExpression, operand: DataType?): DataType? =
        inferDifferenceOfType(expr)

    override fun onTimeBoundary(expr: TimeBoundaryExpression, operand: DataType?): DataType? =
        inferTimeBoundaryType(expr, operand)

    override fun onWidth(expr: WidthExpression, operand: DataType?): DataType? =
        inferWidthType(expr, operand)

    override fun onElementExtractor(
        expr: ElementExtractorExpression,
        operand: DataType?,
    ): DataType? = inferElementExtractorType(expr, operand)

    override fun onTypeExtent(expr: TypeExtentExpression): DataType? = inferTypeExtentType(expr)

    override fun onBetween(
        expr: BetweenExpression,
        input: DataType?,
        lower: DataType?,
        upper: DataType?,
    ): DataType? = inferBetweenType(expr)

    override fun onIntervalExpression(
        expr: IntervalExpression,
        low: DataType?,
        high: DataType?,
        lowClosed: DataType?,
        highClosed: DataType?,
    ): DataType? = low?.let { org.hl7.cql.model.IntervalType(it) }

    override fun onIntervalRelation(
        expr: IntervalRelationExpression,
        left: DataType?,
        right: DataType?,
    ): DataType? = inferIntervalRelationType(expr)

    override fun onQuery(expr: QueryExpression, children: QueryChildren<DataType?>): DataType? =
        inferQueryType(expr)

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

        // Check context definitions (e.g., "Patient" from "context Patient").
        // Context definitions create an implicit ExpressionDef at emission time, so identifiers
        // referencing the context name should resolve as expression references.
        symbolTable.resolveContext(name)?.let { resolution ->
            typeTable.setIdentifierResolution(expression, resolution)
            return resolveContextType(name)
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
            is NullLiteral -> operatorRegistry.type("Any")
            is ListLiteral -> inferListLiteralType(literal)
            is org.hl7.cql.ast.IntervalLiteral -> inferIntervalLiteralType(literal)
            is org.hl7.cql.ast.TupleLiteral -> inferTupleLiteralType(literal)
            is org.hl7.cql.ast.InstanceLiteral -> inferInstanceLiteralType(literal)
            else -> null
        }
    }

    private fun inferIntervalLiteralType(literal: org.hl7.cql.ast.IntervalLiteral): DataType? {
        val anyType = operatorRegistry.type("Any")
        val lowType = inferType(literal.lower)
        val highType = inferType(literal.upper)
        // Exclude Any (from null) for point type computation
        val nonNullTypes = listOfNotNull(lowType, highType).filter { it != anyType }
        val pointType =
            if (nonNullTypes.isEmpty()) anyType else findCommonTypeWithConversions(nonNullTypes)
        return org.hl7.cql.model.IntervalType(pointType)
    }

    private fun inferListLiteralType(literal: ListLiteral): DataType? {
        val anyType = operatorRegistry.type("Any")
        // Infer all element types, then exclude Any (from null literals) for common type
        // computation
        val elementTypes = literal.elements.mapNotNull { inferType(it) }
        val nonNullTypes = elementTypes.filter { it != anyType }
        if (nonNullTypes.isEmpty()) return ListType(anyType)
        val commonType = findCommonTypeWithConversions(nonNullTypes)
        return ListType(commonType)
    }

    private fun inferTupleLiteralType(literal: org.hl7.cql.ast.TupleLiteral): DataType? {
        val elements =
            literal.elements.map { elem ->
                val elemType = inferType(elem.expression) ?: return null
                TupleTypeElement(elem.name.value, elemType)
            }
        return TupleType(elements)
    }

    /**
     * Find the common type among a list of types, considering implicit conversions. Falls back to
     * [DataType.getCommonSuperTypeOf] first, and if the result is `Any`, checks for implicit
     * conversions (e.g., Integer→Decimal promotes to Decimal).
     */
    private fun findCommonTypeWithConversions(types: List<DataType>): DataType {
        if (types.size == 1) return types[0]
        val basic = types.reduce { acc, type -> acc.getCommonSuperTypeOf(type) }
        if (basic != DataType.ANY && basic.toString() != "System.Any") return basic
        // Check if all types can be converted to any one of the distinct types
        val distinct = types.distinct()
        for (candidate in distinct) {
            if (distinct.all { it == candidate || canImplicitlyConvert(it, candidate) }) {
                return candidate
            }
        }
        // Type unification: produce ChoiceType when types can't be unified via conversion.
        // ChoiceType sorts alphabetically internally (deterministic ordering).
        if (distinct.size > 1) {
            return org.hl7.cql.model.ChoiceType(distinct)
        }
        return basic
    }

    /** Check if there's an implicit conversion from [from] to [to]. */
    private fun canImplicitlyConvert(from: DataType, to: DataType): Boolean {
        if (from.isSuperTypeOf(to) || to.isSuperTypeOf(from)) return true
        val conversion =
            operatorRegistry.conversionMap.findConversion(
                from,
                to,
                isImplicit = true,
                allowPromotionAndDemotion = false,
                operatorMap = operatorRegistry.systemOperators,
            )
        return conversion != null
    }

    private fun inferInstanceLiteralType(literal: org.hl7.cql.ast.InstanceLiteral): DataType? {
        // Resolve element expressions for side effects (e.g., identifier resolution)
        literal.elements.forEach { inferType(it.expression) }
        val typeName = literal.type?.name?.simpleName ?: return null
        return operatorRegistry.type(typeName)
    }

    @Suppress("ReturnCount")
    private fun inferBinaryType(
        expression: OperatorBinaryExpression,
        leftType: DataType?,
        rightType: DataType?,
    ): DataType? {
        if (leftType == null || rightType == null) return null
        val effectiveLeft =
            syntheticTable?.effectiveType(expression, Slot.Left, leftType, operatorRegistry)
                ?: leftType
        val effectiveRight =
            syntheticTable?.effectiveType(expression, Slot.Right, rightType, operatorRegistry)
                ?: rightType
        val opName = OperatorNames.binaryOperatorToSystemName(expression.operator) ?: return null
        val resolution =
            operatorRegistry.resolve(opName, listOf(effectiveLeft, effectiveRight)) ?: return null
        typeTable.setOperatorResolution(expression, resolution)
        return resolution.operator.resultType
    }

    @Suppress("ReturnCount")
    private fun inferBooleanTestType(
        expression: BooleanTestExpression,
        operandType: DataType?,
    ): DataType? {
        if (operandType == null) return null
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
        operandType: DataType?,
    ): DataType? {
        if (operandType == null) return null
        val effectiveOperand =
            syntheticTable?.effectiveType(expression, Slot.Operand, operandType, operatorRegistry)
                ?: operandType
        val opName = OperatorNames.unaryOperatorToSystemName(expression.operator) ?: return null
        if (opName == "Positive") return effectiveOperand
        val resolution = operatorRegistry.resolve(opName, listOf(effectiveOperand)) ?: return null
        typeTable.setOperatorResolution(expression, resolution)
        return resolution.operator.resultType
    }

    @Suppress("ReturnCount")
    private fun inferIfType(
        expression: IfExpression,
        thenType: DataType?,
        elseType: DataType?,
    ): DataType? {
        val anyType = operatorRegistry.type("Any")
        // condition type is pre-folded but unused here
        return when {
            thenType == null -> elseType
            elseType == null -> thenType
            // When one branch is Any (null), use the other branch's type
            thenType == anyType -> elseType
            elseType == anyType -> thenType
            else -> findCommonTypeWithConversions(listOf(thenType, elseType))
        }
    }

    private fun inferCaseType(
        expression: CaseExpression,
        cases: List<CaseChildren<DataType?>>,
        elseType: DataType?,
    ): DataType? {
        val anyType = operatorRegistry.type("Any")
        // comparand pre-folded but unused; case conditions pre-folded but unused
        val branchTypes = cases.mapNotNull { it.result }
        val allTypes = branchTypes + listOfNotNull(elseType)
        if (allTypes.isEmpty()) return null
        // Filter out Any (null) types for common type computation
        val nonNullTypes = allTypes.filter { it != anyType }
        if (nonNullTypes.isEmpty()) return anyType
        return findCommonTypeWithConversions(nonNullTypes)
    }

    /**
     * Infer the result type for multi-arg Coalesce by computing the common type of concrete
     * (non-Any) arguments, filtering out null/Any types. This matches the legacy translator which
     * computes the result from the concrete args rather than using the generic overload result.
     *
     * The resolution is computed with Any/null args replaced by the result type (to prevent Any
     * from polluting generic type parameter unification), then patched to include cast conversions
     * at the null-arg positions so the ConversionAnalyzer's generic `recordResolutionConversions`
     * picks them up without Coalesce-specific code.
     */
    @Suppress("ReturnCount")
    private fun inferMultiArgCoalesceType(
        expression: FunctionCallExpression,
        argTypes: List<DataType?>,
    ): DataType? {
        val anyType = operatorRegistry.type("Any")
        val concreteTypes = argTypes.filterNotNull().filter { it != anyType }
        val resultType =
            if (concreteTypes.isEmpty()) {
                anyType
            } else {
                findCommonTypeWithConversions(concreteTypes)
            }
        // Resolve with Any/null args replaced by the result type so the resolution matches
        // the correct overload without Any polluting generic type unification.
        val resolveTypes = argTypes.map { if (it == null || it == anyType) resultType else it }
        val resolution =
            operatorRegistry.resolve("Coalesce", resolveTypes, allowPromotionAndDemotion = true)
        if (resolution != null) {
            // Patch the resolution: inject cast conversions at null-arg positions when the
            // resolution has other conversions (i.e., type promotion occurred). This lets the
            // ConversionAnalyzer handle null wrapping generically via recordResolutionConversions.
            val patched =
                if (resolution.hasConversions() && resultType != anyType) {
                    val patchedConversions =
                        argTypes.mapIndexed { i, t ->
                            if ((t == null || t == anyType) && t != resultType) {
                                Conversion(anyType, resultType)
                            } else {
                                resolution.conversions.getOrNull(i)
                            }
                        }
                    OperatorResolution(resolution.operator, patchedConversions).also {
                        it.score = resolution.score
                        it.allowFluent = resolution.allowFluent
                        it.libraryIdentifier = resolution.libraryIdentifier
                        it.libraryName = resolution.libraryName
                        it.operatorHasOverloads = resolution.operatorHasOverloads
                    }
                } else {
                    resolution
                }
            typeTable.setOperatorResolution(expression, patched)
        }
        return resultType
    }

    @Suppress("ReturnCount", "CyclomaticComplexMethod")
    private fun inferFunctionCallType(
        expression: FunctionCallExpression,
        argTypes: List<DataType?>,
    ): DataType? {
        // target pre-folded but unused

        val functionName = expression.function.value

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

        // Multi-arg Coalesce: result type is the common type of concrete (non-Any) arguments,
        // matching the legacy translator which doesn't apply operator overload conversions.
        if (functionName == "Coalesce" && argTypes.size > 1) {
            return inferMultiArgCoalesceType(expression, argTypes)
        }

        val nonNullArgTypes = argTypes.filterNotNull()
        if (nonNullArgTypes.size != argTypes.size) return null

        // Apply effective types from synthetic table.
        // Skip ImplicitCast on Any-typed args — the cast is for emission (As wrapping),
        // not for resolution. Applying it changes Any→List<Any> which breaks generic
        // resolution (T unifies to List<Any> instead of Any).
        val anyType = operatorRegistry.type("Any")
        val effectiveArgTypes =
            if (syntheticTable != null) {
                nonNullArgTypes.mapIndexed { index, type ->
                    if (type == anyType) type // Don't change Any — resolution handles it
                    else
                        syntheticTable.effectiveType(
                            expression,
                            Slot.Argument(index),
                            type,
                            operatorRegistry,
                        ) ?: type
                }
            } else {
                nonNullArgTypes
            }

        var resolution =
            operatorRegistry.resolve(
                functionName,
                effectiveArgTypes,
                allowPromotionAndDemotion = true,
            )

        // When resolution fails and there are Any-typed args (from null), retry with
        // Any args replaced by List<Any> — handles cases like IndexOf(null, {}) where
        // null should be inferred as List<Any> to match the operator signature.
        // Patch the resolution with cast conversions for the substituted positions.
        if (resolution == null) {
            val anyType = operatorRegistry.type("Any")
            val hasAnyArgs = effectiveArgTypes.any { it == anyType }
            if (hasAnyArgs) {
                val listAny = ListType(anyType)
                val substituted = effectiveArgTypes.map { if (it == anyType) listAny else it }
                val retryResolution =
                    operatorRegistry.resolve(
                        functionName,
                        substituted,
                        allowPromotionAndDemotion = true,
                    )
                if (retryResolution != null) {
                    // Inject cast conversions at positions where Any was substituted
                    val patchedConversions =
                        effectiveArgTypes.mapIndexed { i, t ->
                            if (t == anyType) Conversion(anyType, listAny)
                            else retryResolution.conversions.getOrNull(i)
                        }
                    resolution =
                        OperatorResolution(retryResolution.operator, patchedConversions).also {
                            it.score = retryResolution.score
                            it.allowFluent = retryResolution.allowFluent
                            it.libraryIdentifier = retryResolution.libraryIdentifier
                            it.libraryName = retryResolution.libraryName
                            it.operatorHasOverloads = retryResolution.operatorHasOverloads
                        }
                }
            }
        }

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
    private fun inferIndexType(
        expression: IndexExpression,
        targetType: DataType?,
        indexType: DataType?,
    ): DataType? {
        if (targetType == null || indexType == null) return null
        val resolution =
            operatorRegistry.resolve("Indexer", listOf(targetType, indexType)) ?: return null
        typeTable.setOperatorResolution(expression, resolution)
        return resolution.operator.resultType
    }

    @Suppress("ReturnCount")
    private fun inferListTransformType(
        expression: ListTransformExpression,
        operandType: DataType?,
    ): DataType? {
        if (operandType == null) return null
        val opName =
            when (expression.listTransformKind) {
                ListTransformKind.DISTINCT -> "Distinct"
                ListTransformKind.FLATTEN -> "Flatten"
            }
        val resolution = operatorRegistry.resolve(opName, listOf(operandType)) ?: return null
        typeTable.setOperatorResolution(expression, resolution)
        return resolution.operator.resultType
    }

    /** Resolve the type of a context identifier (e.g., "Patient") from the loaded models. */
    private fun resolveContextType(contextName: String): DataType? {
        val mm = modelManager ?: return null
        for (usingDef in symbolTable.usingDefinitions) {
            if (usingDef.modelIdentifier.simpleName == "System") continue
            val model =
                try {
                    mm.resolveModel(usingDef.modelIdentifier.simpleName, usingDef.version?.value)
                } catch (_: Exception) {
                    continue
                }
            val ctx = model.resolveContextName(contextName, mustResolve = false)
            if (ctx != null) return ctx.type
        }
        return null
    }

    /** Resolve the type of a property on a source type (e.g., "birthDatetime" on QDM.Patient). */
    private fun resolvePropertyType(sourceType: DataType, propertyName: String): DataType? {
        if (sourceType is org.hl7.cql.model.ClassType) {
            // Search elements including inherited ones
            var current: DataType? = sourceType
            while (current is org.hl7.cql.model.ClassType) {
                val element = current.elements.firstOrNull { it.name == propertyName }
                if (element != null) return element.type
                current = current.baseType
            }
        }
        if (sourceType is org.hl7.cql.model.IntervalType) {
            return when (propertyName) {
                "low",
                "high" -> sourceType.pointType
                "lowClosed",
                "highClosed" -> operatorRegistry.type("Boolean")
                else -> null
            }
        }
        if (sourceType is org.hl7.cql.model.TupleType) {
            return sourceType.elements.firstOrNull { it.name == propertyName }?.type
        }
        return null
    }
}
