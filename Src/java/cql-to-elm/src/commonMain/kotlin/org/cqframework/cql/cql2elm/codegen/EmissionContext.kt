package org.cqframework.cql.cql2elm.codegen

import org.cqframework.cql.cql2elm.analysis.ConversionSlot
import org.cqframework.cql.cql2elm.analysis.ImplicitConversion
import org.cqframework.cql.cql2elm.analysis.OperatorRegistry
import org.cqframework.cql.cql2elm.analysis.SemanticModel
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
import org.hl7.elm.r1.Instance
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
 * ## Implicit conversion application pattern
 *
 * Each `on*` handler wraps child ELM nodes with [applyConversions] before passing them to the
 * emission function. [applyConversions] looks up the [ConversionTable] for implicit conversions
 * recorded by [ConversionPlanner][org.cqframework.cql.cql2elm.analysis.ConversionPlanner] at a
 * given `(parent, ConversionSlot)` and wraps the ELM expression in the appropriate conversion nodes
 * (operator conversions, implicit casts, list/interval conversions). This keeps the AST immutable —
 * type coercions are applied only at code-generation time.
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
 * 3. Call [applyConversions] for every child slot that may carry an implicit conversion.
 *
 * ## How to add a new emission extension file
 * 1. Create a new `.kt` file in the `codegen` package.
 * 2. Define extension functions on [EmissionContext] (e.g., `internal fun
 *    EmissionContext.emitFoo(...)`).
 * 3. Call the extension function from the `on*` handler in this class.
 *
 * ## What NOT to put here
 * - Type inference or operator resolution logic — those belong in the analysis phases.
 * - AST mutation — the AST is immutable by this stage; use [ConversionTable] for conversions.
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
        // Model types take precedence over system types for unqualified names.
        // This ensures FHIR.CodeSystem is resolved as {http://hl7.org/fhir}CodeSystem
        // when a FHIR model is loaded, not as {urn:hl7-org:elm-types:r1}CodeSystem.
        val modelQName = modelContext.typeNameToQName(name)
        if (modelQName.namespaceURI != modelContext.typesNamespace) {
            // Found in a non-system model — use the model namespace
            return modelQName
        }
        // System type or not found in any model
        val systemType = operatorRegistry.systemModel.resolveTypeName(name)
        if (systemType != null) {
            return dataTypeToQName(systemType)
        }
        return modelQName
    }

    /**
     * Resolve a type QName from a [QualifiedIdentifier]. When the identifier has a model qualifier
     * (e.g., FHIR.Bundle.Entry), resolve in the specified model's namespace to avoid system-type
     * shadowing (e.g., FHIR.CodeSystem != System.CodeSystem).
     */
    internal fun resolveTypeQName(qualifiedName: org.hl7.cql.ast.QualifiedIdentifier): QName {
        if (qualifiedName.parts.size > 1) {
            val modelQualifier = qualifiedName.parts.first()
            val typeName = qualifiedName.parts.drop(1).joinToString(".")
            // Explicit System qualifier → always resolve in system namespace
            if (modelQualifier == "System") {
                val systemType = operatorRegistry.systemModel.resolveTypeName(typeName)
                if (systemType != null) return dataTypeToQName(systemType)
                return QName(modelContext.typesNamespace, typeName)
            }
            val model = modelContext.resolveModelByName(modelQualifier)
            if (model != null) {
                val modelUrl = model.modelInfo.targetUrl ?: model.modelInfo.url!!
                return QName(modelUrl, typeName)
            }
        }
        return resolveTypeQName(qualifiedName.simpleName)
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
     * Apply any implicit conversions recorded in the [ConversionTable] for the given parent/slot.
     * Wraps the ELM expression in the appropriate ELM conversion nodes.
     */
    @Suppress("CyclomaticComplexMethod")
    fun applyConversions(
        parent: Expression,
        slot: ConversionSlot,
        elm: ElmExpression,
    ): ElmExpression {
        val conversions = semanticModel.conversionTable.get(parent, slot)
        var result = elm
        for (s in conversions) {
            result = applySingleConversion(result, s)
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

    /**
     * Apply a single [ImplicitConversion] to an expression. Factored out of [applyConversions] for
     * reuse.
     */
    private fun applySingleConversion(
        elm: ElmExpression,
        conversion: ImplicitConversion,
    ): ElmExpression =
        when (conversion) {
            is ImplicitConversion.OperatorConversion ->
                createConversionElm(conversion.operatorName, elm)
            is ImplicitConversion.ImplicitCast -> emitImplicitCast(elm, conversion.targetType)
            is ImplicitConversion.ListConversion ->
                emitListConversionQuery(
                    elm,
                    conversion.innerOperatorName,
                    conversion.innerLibraryName,
                )
            is ImplicitConversion.ListDemotion ->
                emitListDemotionQuery(elm, conversion.targetElementType)
            is ImplicitConversion.IntervalConversion ->
                emitIntervalConversion(
                    elm,
                    conversion.innerOperatorName,
                    conversion.innerLibraryName,
                )
            is ImplicitConversion.LibraryConversion ->
                emitLibraryFunctionRef(conversion.libraryName, conversion.functionName, elm)
            is ImplicitConversion.ChoiceNarrowing -> emitChoiceNarrowing(elm, conversion)
        }

    /**
     * Emit a Case expression for multi-branch choice narrowing. Each branch tests the runtime type
     * of the operand with Is, casts with As, then applies the inner conversion chain. The else
     * branch is a typed Null.
     */
    private fun emitChoiceNarrowing(
        expression: ElmExpression,
        narrowing: ImplicitConversion.ChoiceNarrowing,
    ): ElmExpression {
        val caseExpr = org.hl7.elm.r1.Case()
        for (branch in narrowing.branches) {
            val isExpr = emitIsTest(expression, branch.fromType)
            var thenExpr: ElmExpression = emitImplicitCast(expression, branch.fromType)
            for (inner in branch.innerConversions) {
                thenExpr = applySingleConversion(thenExpr, inner)
            }
            caseExpr.caseItem.add(
                org.hl7.elm.r1.CaseItem().apply {
                    `when` = isExpr
                    then = thenExpr
                }
            )
        }
        caseExpr.`else` = org.hl7.elm.r1.Null()
        return caseExpr
    }

    /** Emit an Is(expression, type) test. */
    private fun emitIsTest(expression: ElmExpression, type: DataType): ElmExpression =
        org.hl7.elm.r1.Is().apply {
            operand = expression
            if (type is org.hl7.cql.model.SimpleType || type is org.hl7.cql.model.ClassType) {
                isType = dataTypeToQName(type)
            } else {
                isTypeSpecifier = dataTypeToTypeSpecifier(type)
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
        innerLibraryName: String? = null,
    ): ElmExpression {
        val aliasRef = org.hl7.elm.r1.AliasRef().apply { name = "X" }
        val convertedElement =
            if (innerLibraryName != null) {
                emitLibraryFunctionRef(innerLibraryName, innerOperatorName, aliasRef)
            } else {
                createConversionElm(innerOperatorName, aliasRef)
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
        innerLibraryName: String? = null,
    ): ElmExpression {
        fun convertBound(bound: ElmExpression): ElmExpression =
            if (innerLibraryName != null)
                emitLibraryFunctionRef(innerLibraryName, innerOperatorName, bound)
            else createConversionElm(innerOperatorName, bound)
        // Literal intervals: wrap each bound in the conversion operator
        if (expression is org.hl7.elm.r1.Interval) {
            return org.hl7.elm.r1.Interval().apply {
                low = expression.low?.let { convertBound(it) }
                high = expression.high?.let { convertBound(it) }
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
                convertBound(
                    org.hl7.elm.r1.Property().apply {
                        path = "low"
                        source = expression
                    }
                )
            high =
                convertBound(
                    org.hl7.elm.r1.Property().apply {
                        path = "high"
                        source = expression
                    }
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
        // For list and interval literals, use pre-folded children and apply conversions
        // so that element/bound type conversions are applied correctly.
        return when (literal) {
            is org.hl7.cql.ast.ListLiteral -> {
                val list = org.hl7.elm.r1.List()
                if (children.elements.isNotEmpty()) {
                    list.element =
                        children.elements
                            .mapIndexed { i, elem ->
                                applyConversions(expr, ConversionSlot.ListElement(i), elem)
                            }
                            .toMutableList()
                }
                list
            }
            is org.hl7.cql.ast.IntervalLiteral -> {
                org.hl7.elm.r1.Interval().apply {
                    low =
                        children.intervalLow?.let {
                            applyConversions(expr, ConversionSlot.IntervalLow, it)
                        }
                    high =
                        children.intervalHigh?.let {
                            applyConversions(expr, ConversionSlot.IntervalHigh, it)
                        }
                    lowClosed = literal.lowerClosed
                    highClosed = literal.upperClosed
                }
            }
            is org.hl7.cql.ast.InstanceLiteral -> {
                val instance = Instance()
                literal.type?.let { typeSpec ->
                    instance.classType = resolveTypeQName(typeSpec.name)
                }
                if (children.tupleElements.isNotEmpty()) {
                    instance.element =
                        children.tupleElements
                            .mapIndexed { i, elem ->
                                org.hl7.elm.r1.InstanceElement().apply {
                                    name = literal.elements[i].name.value
                                    value =
                                        applyConversions(expr, ConversionSlot.ListElement(i), elem)
                                }
                            }
                            .toMutableList()
                }
                instance
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
            applyConversions(expr, ConversionSlot.Left, left),
            applyConversions(expr, ConversionSlot.Right, right),
        )
    }

    override fun onUnaryOperator(expr: OperatorUnaryExpression, operand: ElmExpression) =
        emitUnaryOperator(expr, applyConversions(expr, ConversionSlot.Operand, operand))

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
            applyConversions(expr, ConversionSlot.ThenBranch, thenBranch),
            applyConversions(expr, ConversionSlot.ElseBranch, elseBranch),
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
                    condition =
                        applyConversions(expr, ConversionSlot.CaseCondition(i), c.condition),
                    result = applyConversions(expr, ConversionSlot.CaseBranch(i), c.result),
                )
            },
            applyConversions(expr, ConversionSlot.ElseBranch, elseResult),
        )

    override fun onIs(expr: IsExpression, operand: ElmExpression) = emitIsExpression(expr, operand)

    override fun onAs(expr: AsExpression, operand: ElmExpression): ElmExpression {
        val result = emitAsExpression(expr, operand)
        return applyConversions(expr, ConversionSlot.Operand, result)
    }

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
            arguments.mapIndexed { index, arg ->
                applyConversions(expr, ConversionSlot.Argument(index), arg)
            },
        )

    override fun onPropertyAccess(
        expr: PropertyAccessExpression,
        target: ElmExpression,
    ): ElmExpression {
        val property = emitPropertyAccess(expr, target)
        return applyConversions(expr, ConversionSlot.PropertyResult, property)
    }

    override fun onIndex(expr: IndexExpression, target: ElmExpression, index: ElmExpression) =
        emitIndexExpression(expr, target, index)

    override fun onExists(expr: ExistsExpression, operand: ElmExpression) =
        emitExists(expr, applyConversions(expr, ConversionSlot.Operand, operand))

    override fun onMembership(
        expr: MembershipExpression,
        left: ElmExpression,
        right: ElmExpression,
    ) =
        emitMembership(
            expr,
            applyConversions(expr, ConversionSlot.Left, left),
            applyConversions(expr, ConversionSlot.Right, right),
        )

    override fun onListTransform(expr: ListTransformExpression, operand: ElmExpression) =
        emitListTransform(expr, applyConversions(expr, ConversionSlot.Operand, operand))

    override fun onExpandCollapse(
        expr: ExpandCollapseExpression,
        operand: ElmExpression,
        per: ElmExpression?,
    ): ElmExpression =
        emitExpandCollapse(expr, applyConversions(expr, ConversionSlot.Operand, operand), per)

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
        emitWidth(expr, applyConversions(expr, ConversionSlot.Operand, operand))

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
            applyConversions(expr, ConversionSlot.Left, left),
            applyConversions(expr, ConversionSlot.Right, right),
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
