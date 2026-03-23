package org.cqframework.cql.cql2elm.codegen

import org.cqframework.cql.cql2elm.analysis.OperatorRegistry
import org.cqframework.cql.cql2elm.analysis.SemanticModel
import org.cqframework.cql.cql2elm.analysis.Slot
import org.cqframework.cql.cql2elm.analysis.Synthetic
import org.cqframework.cql.cql2elm.model.Conversion
import org.cqframework.cql.cql2elm.model.OperatorResolution
import org.cqframework.cql.cql2elm.tracking.Trackable.resultType
import org.cqframework.cql.shared.BigDecimal
import org.cqframework.cql.shared.QName
import org.hl7.cql.ast.AsExpression
import org.hl7.cql.ast.BetweenExpression
import org.hl7.cql.ast.BooleanTestExpression
import org.hl7.cql.ast.CaseChildren
import org.hl7.cql.ast.CaseExpression
import org.hl7.cql.ast.CastExpression
import org.hl7.cql.ast.ConversionExpression
import org.hl7.cql.ast.DateTimeComponentExpression
import org.hl7.cql.ast.DifferenceBetweenExpression
import org.hl7.cql.ast.DifferenceOfExpression
import org.hl7.cql.ast.DurationBetweenExpression
import org.hl7.cql.ast.DurationOfExpression
import org.hl7.cql.ast.ElementExtractorExpression
import org.hl7.cql.ast.ExistsExpression
import org.hl7.cql.ast.ExpandCollapseExpression
import org.hl7.cql.ast.Expression
import org.hl7.cql.ast.ExpressionFold
import org.hl7.cql.ast.ExternalConstantExpression
import org.hl7.cql.ast.FunctionCallExpression
import org.hl7.cql.ast.IdentifierExpression
import org.hl7.cql.ast.IfExpression
import org.hl7.cql.ast.ImplicitCastExpression
import org.hl7.cql.ast.IndexExpression
import org.hl7.cql.ast.IntervalExpression
import org.hl7.cql.ast.IntervalRelationExpression
import org.hl7.cql.ast.IsExpression
import org.hl7.cql.ast.ListTransformExpression
import org.hl7.cql.ast.LiteralChildren
import org.hl7.cql.ast.LiteralExpression
import org.hl7.cql.ast.MembershipExpression
import org.hl7.cql.ast.OperatorBinaryExpression
import org.hl7.cql.ast.OperatorUnaryExpression
import org.hl7.cql.ast.PropertyAccessExpression
import org.hl7.cql.ast.QueryChildren
import org.hl7.cql.ast.QueryExpression
import org.hl7.cql.ast.RetrieveExpression
import org.hl7.cql.ast.TimeBoundaryExpression
import org.hl7.cql.ast.TypeExtentExpression
import org.hl7.cql.ast.UnsupportedExpression
import org.hl7.cql.ast.WidthExpression
import org.hl7.cql.model.DataType
import org.hl7.elm.r1.Element
import org.hl7.elm.r1.Expression as ElmExpression
import org.hl7.elm.r1.Literal as ElmLiteral

/**
 * The code-generation fold: an [ExpressionFold]<[ElmExpression]> that converts each CQL AST
 * expression into an equivalent ELM node. This is the final stage of the CQL-to-ELM pipeline:
 * ```
 * CQL AST + SemanticModel  ──► EmissionContext (ExpressionFold<ElmExpression>)  ──► ELM
 * ```
 *
 * ## How the fold works
 *
 * [fold] is overridden to route every expression through [emitExpression], which:
 * 1. Checks whether the [SemanticValidator][org.cqframework.cql.cql2elm.analysis.SemanticValidator]
 *    flagged this expression with an error — if so, emits `Null` immediately.
 * 2. Dispatches to the catamorphism (`super.fold`) which pre-folds children via `fold(child)` and
 *    then calls the matching `on*` handler with fully-decorated ELM children.
 * 3. Decorates the result with `resultType` from the [SemanticModel].
 *
 * Because children are pre-folded through [emitExpression] before `on*` is called, every child the
 * handler receives is already a decorated [ElmExpression].
 *
 * ## Synthetic application pattern
 *
 * Each `on*` handler wraps child ELM nodes with [applySynthetics] before passing them to the
 * emission function. [applySynthetics] looks up the [SyntheticTable] for implicit conversions
 * recorded by [TypeUnifier][org.cqframework.cql.cql2elm.analysis.TypeUnifier] at a given `(parent,
 * Slot)` and wraps the ELM expression in the appropriate conversion nodes (operator conversions,
 * implicit casts, list/interval conversions). This keeps the AST immutable — type coercions are
 * applied only at code-generation time.
 *
 * ## Extension file convention
 *
 * The `on*` handlers in this class are thin routers: they delegate to extension functions defined
 * in per-domain files inside the `codegen` package. Each domain gets its own file:
 *
 * | File                            | Scope                                    |
 * |---------------------------------|------------------------------------------|
 * | [LiteralEmission.kt]            | Literal type handlers                    |
 * | [OperatorEmission.kt]           | Binary / unary operator emission         |
 * | [TemporalEmission.kt]           | Date/time literal parsing                |
 * | [TemporalOperatorEmission.kt]   | Temporal operator emission               |
 * | [IntervalOperatorEmission.kt]   | Interval operator emission               |
 * | [ListOperatorEmission.kt]       | List / set operator emission             |
 * | [CollectionOperatorEmission.kt] | Collection operator emission             |
 * | [TypeOperatorEmission.kt]       | is / as / cast / convert emission        |
 * | [ReferenceEmission.kt]          | Identifier and reference emission        |
 * | [PropertyAccessEmission.kt]     | Property access emission                 |
 * | [FunctionEmission.kt]           | Function call emission                   |
 * | [SystemFunctionEmission.kt]     | System function emission                 |
 * | [QueryEmission.kt]              | Query expression emission                |
 * | [RetrieveEmission.kt]           | Retrieve expression emission             |
 * | [CaseEmission.kt]               | Case expression emission                 |
 * | [DefinitionEmission.kt]         | Definition emission (usings, parameters) |
 * | [StatementEmission.kt]          | Statement emission (expressions, funcs)  |
 * | [TerminologyEmission.kt]        | Terminology definition emission          |
 * | [EmissionHelpers.kt]            | Shared utilities                         |
 *
 * ## How to add emission for a new expression type
 * 1. Add the `on*` handler in this class (the compiler will force this once the new [Expression]
 *    subtype is added to the AST and [ExpressionFold]).
 * 2. Implement ELM construction — either inline in the handler or in an existing/new emission
 *    extension file (see below).
 * 3. Call [applySynthetics] for every child slot that may carry an implicit conversion.
 *
 * ## How to add a new emission extension file
 * 1. Create a new `.kt` file in the `codegen` package.
 * 2. Define extension functions on [EmissionContext] (e.g., `internal fun
 *    EmissionContext.emitFoo(...)`).
 * 3. Call the extension function from the `on*` handler in this class.
 *
 * ## What NOT to put here
 * - Type inference or operator resolution logic — those belong in the analysis phases.
 * - AST mutation — the AST is immutable by this stage; use [SyntheticTable] for conversions.
 * - Library-level orchestration — that belongs in [ElmEmitter].
 */
@Suppress("TooManyFunctions")
class EmissionContext(val semanticModel: SemanticModel) : ExpressionFold<ElmExpression> {
    val operatorRegistry: OperatorRegistry
        get() = semanticModel.operatorRegistry

    internal val modelContext: org.cqframework.cql.cql2elm.analysis.ModelContext
        get() = semanticModel.modelContext

    val typesNamespace
        get() = modelContext.typesNamespace

    /**
     * Resolve an AST type name to the correct ELM [QName], checking system types first, then loaded
     * models. Falls back to [typesNamespace] if no model match is found.
     */
    internal fun resolveTypeQName(name: String): QName {
        // System types (Integer, String, Code, etc.) live in the ELM types namespace
        val systemType = operatorRegistry.systemModel.resolveTypeName(name)
        if (systemType != null) {
            return dataTypeToQName(systemType)
        }
        return modelContext.typeNameToQName(name)
    }

    /**
     * Convert a resolved [DataType] to a [QName] with the correct namespace. ModelContext handles
     * both system types (ELM types namespace) and model types (model-specific namespace).
     */
    internal fun dataTypeToQName(type: DataType): QName = modelContext.dataTypeToQName(type)

    /**
     * Check whether the given AST expression is an [IdentifierExpression] that resolved to an
     * included library alias (via [Resolution.IncludeRef]). Returns the alias string if so, `null`
     * otherwise. Used by function call and property access emission to detect cross-library
     * references (e.g., `Common.TestMessage(...)` where `Common` is a library alias).
     */
    internal fun resolveLibraryAlias(expression: Expression?): String? {
        val identifier = expression as? IdentifierExpression ?: return null
        val resolution = semanticModel.getIdentifierResolution(identifier)
        return (resolution as? org.cqframework.cql.cql2elm.analysis.Resolution.IncludeRef)?.alias
    }

    /**
     * Convert a resolved [DataType] to an ELM TypeSpecifier with the correct namespace. For
     * NamedType, uses [dataTypeToQName] which handles model types correctly. For other types
     * (ListType, IntervalType, etc.), delegates to the TypeBuilder.
     */
    internal fun dataTypeToTypeSpecifier(type: DataType): org.hl7.elm.r1.TypeSpecifier =
        modelContext.dataTypeToTypeSpecifier(type, operatorRegistry.typeBuilder)

    /**
     * Set resultType on an ELM element via the Trackable extension property. This sets the internal
     * resultType for downstream consumers but does NOT set resultTypeName or resultTypeSpecifier on
     * the serialized output, matching the legacy translator's default behavior.
     */
    fun decorate(element: Element, type: DataType) {
        element.resultType = type
    }

    fun createIntLiteral(value: Int): ElmLiteral {
        return ElmLiteral()
            .withValueType(QName(typesNamespace, "Integer"))
            .withValue(value.toString())
    }

    fun createDecimalLiteral(value: BigDecimal): ElmLiteral {
        return ElmLiteral()
            .withValueType(QName(typesNamespace, "Decimal"))
            .withValue(value.toString())
    }

    /** Look up the operator resolution for an AST expression. */
    fun lookupResolution(expression: Expression): OperatorResolution? =
        semanticModel.getOperatorResolution(expression)

    /** Wrap an expression in a conversion operator (e.g., ToDecimal, ToLong). */
    fun wrapConversion(expression: ElmExpression, conversionName: String): ElmExpression {
        return createConversionElm(conversionName, expression)
    }

    /**
     * Apply any synthetics recorded in the [SyntheticTable] for the given parent/slot. Wraps the
     * ELM expression in the appropriate ELM conversion nodes.
     */
    @Suppress("CyclomaticComplexMethod")
    fun applySynthetics(parent: Expression, slot: Slot, elm: ElmExpression): ElmExpression {
        val synthetics = semanticModel.syntheticTable.get(parent, slot)
        var result = elm
        for (s in synthetics) {
            result =
                when (s) {
                    is Synthetic.OperatorConversion -> createConversionElm(s.operatorName, result)
                    is Synthetic.ImplicitCast -> emitImplicitCast(result, s.targetType)
                    is Synthetic.ListConversion ->
                        emitListConversionQuery(result, s.innerOperatorName)
                    is Synthetic.ListDemotion -> emitListDemotionQuery(result, s.targetElementType)
                    is Synthetic.IntervalConversion ->
                        emitIntervalConversion(result, s.innerOperatorName)
                    is Synthetic.LibraryConversion ->
                        emitLibraryFunctionRef(s.libraryName, s.functionName, result)
                }
        }
        return result
    }

    /** Emit a FunctionRef to a library conversion function (e.g., FHIRHelpers.ToDateTime). */
    private fun emitLibraryFunctionRef(
        libraryName: String,
        functionName: String,
        operand: ElmExpression,
    ): ElmExpression =
        org.hl7.elm.r1.FunctionRef().apply {
            this.libraryName = libraryName
            this.name = functionName
            this.operand = mutableListOf(operand)
        }

    /** Emit an implicit As(targetType) wrapping. */
    private fun emitImplicitCast(expression: ElmExpression, targetType: DataType): ElmExpression {
        return org.hl7.elm.r1.As().apply {
            operand = expression
            if (
                targetType is org.hl7.cql.model.SimpleType ||
                    targetType is org.hl7.cql.model.ClassType
            ) {
                asType = dataTypeToQName(targetType)
            } else {
                asTypeSpecifier = dataTypeToTypeSpecifier(targetType)
            }
        }
    }

    /** Emit Coalesce(expression, '') wrapping for CONCAT null-coalescing. */
    /** Emit Coalesce(expression, '') for CONCAT null-coalescing. */
    internal fun emitCoalesceWrap(expression: ElmExpression): ElmExpression {
        val emptyStringLiteral =
            ElmLiteral().withValueType(QName(typesNamespace, "String")).withValue("")
        return org.hl7.elm.r1.Coalesce().apply {
            operand = mutableListOf(expression, emptyStringLiteral)
        }
    }

    /** Emit a Query wrapping that applies an operator conversion to each list element. */
    private fun emitListConversionQuery(
        listExpression: ElmExpression,
        innerOperatorName: String,
    ): ElmExpression {
        val aliasRef = org.hl7.elm.r1.AliasRef().apply { name = "X" }
        val convertedElement = createConversionElm(innerOperatorName, aliasRef)
        return org.hl7.elm.r1.Query().apply {
            source =
                mutableListOf(
                    org.hl7.elm.r1.AliasedQuerySource().apply {
                        alias = "X"
                        expression = listExpression
                    }
                )
            `let` = mutableListOf()
            relationship = mutableListOf()
            `return` =
                org.hl7.elm.r1.ReturnClause().apply {
                    distinct = false
                    expression = convertedElement
                }
        }
    }

    /** Emit a Query wrapping that applies an As cast to each list element (list demotion). */
    private fun emitListDemotionQuery(
        listExpression: ElmExpression,
        targetElementType: DataType,
    ): ElmExpression {
        val aliasRef = org.hl7.elm.r1.AliasRef().apply { name = "X" }
        val castElement =
            org.hl7.elm.r1.As().apply {
                operand = aliasRef
                if (
                    targetElementType is org.hl7.cql.model.SimpleType ||
                        targetElementType is org.hl7.cql.model.ClassType
                ) {
                    asType = dataTypeToQName(targetElementType)
                } else {
                    asTypeSpecifier = dataTypeToTypeSpecifier(targetElementType)
                }
            }
        return org.hl7.elm.r1.Query().apply {
            source =
                mutableListOf(
                    org.hl7.elm.r1.AliasedQuerySource().apply {
                        alias = "X"
                        expression = listExpression
                    }
                )
            `let` = mutableListOf()
            relationship = mutableListOf()
            `return` =
                org.hl7.elm.r1.ReturnClause().apply {
                    distinct = false
                    expression = castElement
                }
        }
    }

    /** Emit interval conversion by wrapping bounds with inner operator conversion. */
    private fun emitIntervalConversion(
        expression: ElmExpression,
        innerOperatorName: String,
    ): ElmExpression {
        // Literal intervals: wrap each bound in the conversion operator
        if (expression is org.hl7.elm.r1.Interval) {
            return org.hl7.elm.r1.Interval().apply {
                low = expression.low?.let { createConversionElm(innerOperatorName, it) }
                high = expression.high?.let { createConversionElm(innerOperatorName, it) }
                lowClosed = expression.lowClosed
                highClosed = expression.highClosed
                lowClosedExpression = expression.lowClosedExpression
                highClosedExpression = expression.highClosedExpression
            }
        }
        // Non-literal intervals (e.g., Property references): decompose into bound access +
        // conversion + reconstruction. This produces the same output as the legacy translator
        // for interval type promotion (e.g., Interval<Date> → Interval<DateTime>).
        return org.hl7.elm.r1.Interval().apply {
            low =
                createConversionElm(
                    innerOperatorName,
                    org.hl7.elm.r1.Property().apply {
                        path = "low"
                        source = expression
                    },
                )
            high =
                createConversionElm(
                    innerOperatorName,
                    org.hl7.elm.r1.Property().apply {
                        path = "high"
                        source = expression
                    },
                )
            lowClosedExpression =
                org.hl7.elm.r1.Property().apply {
                    path = "lowClosed"
                    source = expression
                }
            highClosedExpression =
                org.hl7.elm.r1.Property().apply {
                    path = "highClosed"
                    source = expression
                }
        }
    }

    /**
     * Wrap a list expression in an implicit Query that applies a cast element-level conversion.
     * Used by [emitSetOperator] for list demotion (List<Any> → List<T>). Produces:
     * Query(source=[alias "X" from list], return=Return(As(AliasRef("X"), targetType)))
     */
    fun wrapListConversion(
        listExpression: ElmExpression,
        elementConversion: Conversion,
    ): ElmExpression {
        val aliasRef = org.hl7.elm.r1.AliasRef().apply { name = "X" }
        val convertedElement = wrapAsConversion(aliasRef, elementConversion)
        return org.hl7.elm.r1.Query().apply {
            source =
                mutableListOf(
                    org.hl7.elm.r1.AliasedQuerySource().apply {
                        alias = "X"
                        expression = listExpression
                    }
                )
            `let` = mutableListOf()
            relationship = mutableListOf()
            `return` =
                org.hl7.elm.r1.ReturnClause().apply {
                    distinct = false
                    expression = convertedElement
                }
        }
    }

    /**
     * Wrap an expression in an ELM [As] node based on a cast [Conversion]. Used by
     * [wrapListConversion] for list-demotion in set operators.
     */
    fun wrapAsConversion(expression: ElmExpression, conversion: Conversion): ElmExpression {
        val targetType = conversion.toType
        return org.hl7.elm.r1.As().apply {
            operand = expression
            if (
                targetType is org.hl7.cql.model.SimpleType ||
                    targetType is org.hl7.cql.model.ClassType
            ) {
                asType = dataTypeToQName(targetType)
            } else {
                asTypeSpecifier = dataTypeToTypeSpecifier(targetType)
            }
        }
    }

    /**
     * Recursively emit an AST [Expression] into an ELM expression. Dispatches via [fold] for
     * compile-time exhaustiveness, then decorates with result type from the [SemanticModel].
     *
     * If the expression was flagged with a semantic error by the [SemanticValidator], emits `Null`
     * instead. This is a mechanical transformation driven by analysis data, not error handling.
     */
    fun emitExpression(expression: Expression): ElmExpression {
        // Semantic error → emit Null (analysis decided this expression is invalid)
        if (semanticModel.hasError(expression)) {
            return org.hl7.elm.r1.Null()
        }

        // QueryExpression needs special handling: emitQuery manages source/scope iteration
        // internally and cannot use the catamorphism's default child pre-folding.
        val elmExpr =
            if (expression is QueryExpression) {
                emitQuery(expression)
            } else {
                super<ExpressionFold>.fold(expression)
            }

        // Set result type from the SemanticModel
        val type = semanticModel[expression]
        if (type != null) {
            decorate(elmExpr, type)
        }

        return elmExpr
    }

    /**
     * Override [fold] to route through [emitExpression] for decoration and error checking. When the
     * catamorphism's default `fold()` body calls `fold(child)`, it goes through
     * `emitExpression(child)` which adds resultType decoration and semantic error handling.
     */
    override fun fold(expr: Expression): ElmExpression = emitExpression(expr)

    // --- ExpressionFold implementation ---
    // Children are pre-folded by the catamorphism through fold() → emitExpression(), so they
    // are fully decorated ELM expressions. The on* handlers use them directly.

    override fun onLiteral(
        expr: LiteralExpression,
        children: LiteralChildren<ElmExpression>,
    ): ElmExpression {
        val literal = expr.literal
        // For list and interval literals, use pre-folded children and apply synthetics
        // so that element/bound type conversions are applied correctly.
        return when (literal) {
            is org.hl7.cql.ast.ListLiteral -> {
                val list = org.hl7.elm.r1.List()
                if (children.elements.isNotEmpty()) {
                    list.element =
                        children.elements
                            .mapIndexed { i, elem ->
                                applySynthetics(expr, Slot.ListElement(i), elem)
                            }
                            .toMutableList()
                }
                list
            }
            is org.hl7.cql.ast.IntervalLiteral -> {
                org.hl7.elm.r1.Interval().apply {
                    low = children.intervalLow?.let { applySynthetics(expr, Slot.IntervalLow, it) }
                    high =
                        children.intervalHigh?.let { applySynthetics(expr, Slot.IntervalHigh, it) }
                    lowClosed = literal.lowerClosed
                    highClosed = literal.upperClosed
                }
            }
            else -> emitLiteral(literal)
        }
    }

    override fun onIdentifier(expr: IdentifierExpression) = emitIdentifierExpression(expr)

    override fun onExternalConstant(expr: ExternalConstantExpression): ElmExpression =
        throw ElmEmitter.UnsupportedNodeException(
            "ExternalConstantExpression (%${expr.name}) is not yet supported."
        )

    override fun onBinaryOperator(
        expr: OperatorBinaryExpression,
        left: ElmExpression,
        right: ElmExpression,
    ): ElmExpression {
        return emitBinaryOperator(
            expr,
            applySynthetics(expr, Slot.Left, left),
            applySynthetics(expr, Slot.Right, right),
        )
    }

    override fun onUnaryOperator(expr: OperatorUnaryExpression, operand: ElmExpression) =
        emitUnaryOperator(expr, applySynthetics(expr, Slot.Operand, operand))

    override fun onBooleanTest(expr: BooleanTestExpression, operand: ElmExpression) =
        emitBooleanTest(expr, operand)

    override fun onIf(
        expr: IfExpression,
        condition: ElmExpression,
        thenBranch: ElmExpression,
        elseBranch: ElmExpression,
    ) =
        emitIfExpression(
            expr,
            condition,
            applySynthetics(expr, Slot.ThenBranch, thenBranch),
            applySynthetics(expr, Slot.ElseBranch, elseBranch),
        )

    override fun onCase(
        expr: CaseExpression,
        comparand: ElmExpression?,
        cases: List<CaseChildren<ElmExpression>>,
        elseResult: ElmExpression,
    ) =
        emitCaseExpression(
            expr,
            comparand,
            cases.mapIndexed { i, c ->
                CaseChildren(
                    condition = applySynthetics(expr, Slot.CaseCondition(i), c.condition),
                    result = applySynthetics(expr, Slot.CaseBranch(i), c.result),
                )
            },
            applySynthetics(expr, Slot.ElseBranch, elseResult),
        )

    override fun onIs(expr: IsExpression, operand: ElmExpression) = emitIsExpression(expr, operand)

    override fun onAs(expr: AsExpression, operand: ElmExpression) = emitAsExpression(expr, operand)

    override fun onImplicitCast(expr: ImplicitCastExpression, operand: ElmExpression) =
        emitImplicitCastExpression(expr, operand)

    override fun onCast(expr: CastExpression, operand: ElmExpression) =
        emitCastExpression(expr, operand)

    override fun onConversion(expr: ConversionExpression, operand: ElmExpression) =
        emitConversionExpression(expr, operand)

    override fun onFunctionCall(
        expr: FunctionCallExpression,
        target: ElmExpression?,
        arguments: List<ElmExpression>,
    ) =
        emitFunctionCall(
            expr,
            target,
            arguments.mapIndexed { index, arg -> applySynthetics(expr, Slot.Argument(index), arg) },
        )

    override fun onPropertyAccess(expr: PropertyAccessExpression, target: ElmExpression) =
        emitPropertyAccess(expr, target)

    override fun onIndex(expr: IndexExpression, target: ElmExpression, index: ElmExpression) =
        emitIndexExpression(expr, target, index)

    override fun onExists(expr: ExistsExpression, operand: ElmExpression) =
        emitExists(expr, applySynthetics(expr, Slot.Operand, operand))

    override fun onMembership(
        expr: MembershipExpression,
        left: ElmExpression,
        right: ElmExpression,
    ) =
        emitMembership(
            expr,
            applySynthetics(expr, Slot.Left, left),
            applySynthetics(expr, Slot.Right, right),
        )

    override fun onListTransform(expr: ListTransformExpression, operand: ElmExpression) =
        emitListTransform(expr, applySynthetics(expr, Slot.Operand, operand))

    override fun onExpandCollapse(
        expr: ExpandCollapseExpression,
        operand: ElmExpression,
        per: ElmExpression?,
    ): ElmExpression = emitExpandCollapse(expr, applySynthetics(expr, Slot.Operand, operand), per)

    override fun onDateTimeComponent(expr: DateTimeComponentExpression, operand: ElmExpression) =
        emitDateTimeComponent(expr, operand)

    override fun onDurationBetween(
        expr: DurationBetweenExpression,
        lower: ElmExpression,
        upper: ElmExpression,
    ) = emitDurationBetween(expr, lower, upper)

    override fun onDifferenceBetween(
        expr: DifferenceBetweenExpression,
        lower: ElmExpression,
        upper: ElmExpression,
    ) = emitDifferenceBetween(expr, lower, upper)

    override fun onDurationOf(expr: DurationOfExpression, operand: ElmExpression) =
        emitDurationOf(expr, operand)

    override fun onDifferenceOf(expr: DifferenceOfExpression, operand: ElmExpression) =
        emitDifferenceOf(expr, operand)

    override fun onTimeBoundary(expr: TimeBoundaryExpression, operand: ElmExpression) =
        emitTimeBoundary(expr, operand)

    override fun onWidth(expr: WidthExpression, operand: ElmExpression) =
        emitWidth(expr, applySynthetics(expr, Slot.Operand, operand))

    override fun onElementExtractor(expr: ElementExtractorExpression, operand: ElmExpression) =
        emitElementExtractor(expr, operand)

    override fun onTypeExtent(expr: TypeExtentExpression) = emitTypeExtent(expr)

    override fun onBetween(
        expr: BetweenExpression,
        input: ElmExpression,
        lower: ElmExpression,
        upper: ElmExpression,
    ) = emitBetween(expr, input, lower, upper)

    override fun onIntervalExpression(
        expr: IntervalExpression,
        low: ElmExpression,
        high: ElmExpression,
        lowClosed: ElmExpression,
        highClosed: ElmExpression,
    ): ElmExpression {
        return org.hl7.elm.r1.Interval().apply {
            this.low = low
            this.high = high
            lowClosedExpression = lowClosed
            highClosedExpression = highClosed
        }
    }

    override fun onIntervalRelation(
        expr: IntervalRelationExpression,
        left: ElmExpression,
        right: ElmExpression,
    ) =
        emitIntervalRelation(
            expr,
            applySynthetics(expr, Slot.Left, left),
            applySynthetics(expr, Slot.Right, right),
        )

    override fun onQuery(
        expr: QueryExpression,
        children: QueryChildren<ElmExpression>,
    ): ElmExpression {
        // Query emission is handled by emitExpression() which short-circuits to emitQuery().
        // This handler is only here for compile-time exhaustiveness; it should not be reached
        // in normal flow since fold() → emitExpression() bypasses the catamorphism for queries.
        return emitQuery(expr)
    }

    override fun onRetrieve(expr: RetrieveExpression) = emitRetrieve(expr)

    override fun onUnsupported(expr: UnsupportedExpression): ElmExpression =
        throw ElmEmitter.UnsupportedNodeException(
            "UnsupportedExpression '${expr.description}' is not supported."
        )
}
