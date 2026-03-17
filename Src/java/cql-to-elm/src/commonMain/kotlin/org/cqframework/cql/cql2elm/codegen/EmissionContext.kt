package org.cqframework.cql.cql2elm.codegen

import org.cqframework.cql.cql2elm.ModelManager
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
import org.hl7.cql.ast.IndexExpression
import org.hl7.cql.ast.IntervalRelationExpression
import org.hl7.cql.ast.IsExpression
import org.hl7.cql.ast.ListTransformExpression
import org.hl7.cql.ast.LiteralExpression
import org.hl7.cql.ast.MembershipExpression
import org.hl7.cql.ast.OperatorBinaryExpression
import org.hl7.cql.ast.OperatorUnaryExpression
import org.hl7.cql.ast.PropertyAccessExpression
import org.hl7.cql.ast.QueryExpression
import org.hl7.cql.ast.RetrieveExpression
import org.hl7.cql.ast.TimeBoundaryExpression
import org.hl7.cql.ast.TypeExtentExpression
import org.hl7.cql.ast.UnsupportedExpression
import org.hl7.cql.ast.WidthExpression
import org.hl7.cql.model.DataType
import org.hl7.elm.r1.As
import org.hl7.elm.r1.Element
import org.hl7.elm.r1.Expression as ElmExpression
import org.hl7.elm.r1.Literal as ElmLiteral

/**
 * Shared state and helpers used by all emission extension functions. Implements [ExpressionFold] to
 * provide compile-time exhaustive dispatch over the AST — adding a new Expression subtype without a
 * handler is a compile error.
 */
@Suppress("TooManyFunctions")
class EmissionContext(val semanticModel: SemanticModel, val modelManager: ModelManager? = null) :
    ExpressionFold<ElmExpression> {
    val operatorRegistry: OperatorRegistry
        get() = semanticModel.operatorRegistry

    val typesNamespace = "urn:hl7-org:elm-types:r1"

    /**
     * Model names loaded via using definitions. Populated during [emitUsings] and used by
     * [buildRetrieveForType] to resolve types against loaded models.
     */
    internal val loadedModelNames = mutableListOf<String>()

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

    /** Wrap an expression in a Coalesce with an empty string fallback. */
    fun wrapCoalesce(expression: ElmExpression): ElmExpression {
        val emptyString = ElmLiteral().withValueType(QName(typesNamespace, "String")).withValue("")
        return org.hl7.elm.r1.Coalesce().apply { operand = mutableListOf(expression, emptyString) }
    }

    /**
     * Apply conversions from an [OperatorResolution]. Calls [handler] for each conversion with the
     * operand index and the conversion operator name.
     */
    inline fun applyConversions(resolution: OperatorResolution, handler: (Int, String) -> Unit) {
        if (resolution.hasConversions()) {
            resolution.conversions.forEachIndexed { index, conversion ->
                if (conversion != null) {
                    val convName = operatorRegistry.conversionOperatorName(conversion)
                    if (convName != null) {
                        handler(index, convName)
                    }
                }
            }
        }
    }

    /**
     * Apply conversions from an [OperatorResolution], handling both operator-based conversions
     * (e.g., ToDecimal) and cast conversions (wrapping in As). The [operands] list is mutated in
     * place with wrapped expressions.
     */
    fun applyAllConversions(resolution: OperatorResolution, operands: MutableList<ElmExpression>) {
        if (!resolution.hasConversions()) return
        resolution.conversions.forEachIndexed { index, conversion ->
            if (conversion != null && index < operands.size) {
                operands[index] = applyConversion(operands[index], conversion)
            }
        }
    }

    /**
     * Apply a single [Conversion] to an ELM expression, returning the wrapped expression. Handles
     * both operator-based conversions (ToDecimal, etc.) and cast conversions (As wrapping).
     */
    fun applyConversion(expression: ElmExpression, conversion: Conversion): ElmExpression {
        val convName = operatorRegistry.conversionOperatorName(conversion)
        if (convName != null) {
            return wrapConversion(expression, convName)
        }
        // Cast conversion: wrap in As
        if (conversion.isCast) {
            return wrapAsConversion(expression, conversion)
        }
        // List conversion: wrap in implicit Query with element-level conversion
        // Only apply for operator-based conversions (ToDecimal, ToLong, etc.)
        // not for cast conversions (As wrapping)
        if (
            conversion.isListConversion &&
                conversion.conversion != null &&
                conversion.conversion!!.operator != null
        ) {
            return wrapListConversion(expression, conversion.conversion!!)
        }
        // Interval conversion: expand into Interval with Property access on
        // low/high/lowClosed/highClosed
        if (conversion.isIntervalConversion && conversion.conversion != null) {
            return wrapIntervalConversion(expression, conversion)
        }
        // List/interval promotions and demotions not yet handled
        return expression
    }

    /**
     * Wrap a list expression in an implicit Query that applies an element-level conversion.
     * Produces: Query(source=[alias "X" from list], return=Return(conversion(AliasRef("X"))))
     */
    fun wrapListConversion(
        listExpression: ElmExpression,
        elementConversion: Conversion,
    ): ElmExpression {
        val aliasRef = org.hl7.elm.r1.AliasRef().apply { name = "X" }
        val convertedElement = applyConversion(aliasRef, elementConversion)
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
     * Wrap an expression in an ELM [As] node based on a cast [Conversion]. Uses `asType` for simple
     * named types and `asTypeSpecifier` for complex types (list, interval, choice).
     */
    fun wrapAsConversion(expression: ElmExpression, conversion: Conversion): ElmExpression {
        val targetType = conversion.toType
        return As().apply {
            operand = expression
            // Simple named types use asType (QName), complex types use asTypeSpecifier
            if (
                targetType is org.hl7.cql.model.SimpleType ||
                    targetType is org.hl7.cql.model.ClassType
            ) {
                asType = operatorRegistry.typeBuilder.dataTypeToQName(targetType)
            } else {
                asTypeSpecifier = operatorRegistry.typeBuilder.dataTypeToTypeSpecifier(targetType)
            }
        }
    }

    /**
     * Expand an interval expression by extracting Property paths on low/high/lowClosed/highClosed
     * and applying the inner conversion to the low and high values. This matches the legacy
     * translator's `convertIntervalExpression` behavior for interval type demotion (e.g.,
     * `Interval<Any>` → `Interval<Integer>`).
     */
    fun wrapIntervalConversion(expression: ElmExpression, conversion: Conversion): ElmExpression {
        val innerConversion = conversion.conversion!!
        return org.hl7.elm.r1.Interval().apply {
            low =
                applyConversion(
                    org.hl7.elm.r1.Property().apply {
                        path = "low"
                        source = expression
                    },
                    innerConversion,
                )
            lowClosedExpression =
                org.hl7.elm.r1.Property().apply {
                    path = "lowClosed"
                    source = expression
                }
            high =
                applyConversion(
                    org.hl7.elm.r1.Property().apply {
                        path = "high"
                        source = expression
                    },
                    innerConversion,
                )
            highClosedExpression =
                org.hl7.elm.r1.Property().apply {
                    path = "highClosed"
                    source = expression
                }
        }
    }

    /**
     * Apply an implicit conversion from [fromType] to [toType] if one exists. Checks type names for
     * known implicit conversion operators (e.g., Integer→Decimal via ToDecimal, Code→Concept via
     * ToConcept). Used for element-level type promotion in lists, intervals, if/case branches.
     */
    fun applyImplicitConversion(
        expression: ElmExpression,
        fromType: DataType,
        toType: DataType,
    ): ElmExpression {
        if (fromType == toType) return expression
        // Known implicit conversion operators based on type names
        val convName = implicitConversionName(fromType.toString(), toType.toString())
        if (convName != null) {
            return wrapConversion(expression, convName)
        }
        return expression
    }

    /** Map known implicit conversion type pairs to their operator names. */
    private fun implicitConversionName(fromTypeName: String, toTypeName: String): String? =
        when {
            fromTypeName == "System.Integer" && toTypeName == "System.Long" -> "ToLong"
            fromTypeName == "System.Integer" && toTypeName == "System.Decimal" -> "ToDecimal"
            fromTypeName == "System.Long" && toTypeName == "System.Decimal" -> "ToDecimal"
            fromTypeName == "System.Code" && toTypeName == "System.Concept" -> "ToConcept"
            else -> null
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

        val elmExpr = fold(expression)

        // Set result type from the SemanticModel
        val type = semanticModel[expression]
        if (type != null) {
            decorate(elmExpr, type)
        }

        return elmExpr
    }

    // --- ExpressionFold implementation ---

    override fun onLiteral(expr: LiteralExpression): ElmExpression {
        // For list literals, pass the inferred list type for null element wrapping
        if (expr.literal is org.hl7.cql.ast.ListLiteral) {
            val listType = semanticModel[expr]
            val elementType =
                if (listType is org.hl7.cql.model.ListType) listType.elementType else null
            return emitList(expr.literal as org.hl7.cql.ast.ListLiteral, elementType)
        }
        // For interval literals, pass the inferred point type for null bound wrapping
        if (expr.literal is org.hl7.cql.ast.IntervalLiteral) {
            val intervalType = semanticModel[expr]
            val pointType =
                if (intervalType is org.hl7.cql.model.IntervalType) intervalType.pointType else null
            return emitInterval(expr.literal as org.hl7.cql.ast.IntervalLiteral, pointType)
        }
        return emitLiteral(expr.literal)
    }

    override fun onIdentifier(expr: IdentifierExpression) = emitIdentifierExpression(expr)

    override fun onExternalConstant(expr: ExternalConstantExpression): ElmExpression =
        throw ElmEmitter.UnsupportedNodeException(
            "ExternalConstantExpression (%${expr.name}) is not yet supported."
        )

    override fun onBinaryOperator(expr: OperatorBinaryExpression) = emitBinaryOperator(expr)

    override fun onUnaryOperator(expr: OperatorUnaryExpression) = emitUnaryOperator(expr)

    override fun onBooleanTest(expr: BooleanTestExpression) = emitBooleanTest(expr)

    override fun onIf(expr: IfExpression) = emitIfExpression(expr)

    override fun onCase(expr: CaseExpression) = emitCaseExpression(expr)

    override fun onIs(expr: IsExpression) = emitIsExpression(expr)

    override fun onAs(expr: AsExpression) = emitAsExpression(expr)

    override fun onCast(expr: CastExpression) = emitCastExpression(expr)

    override fun onConversion(expr: ConversionExpression) = emitConversionExpression(expr)

    override fun onFunctionCall(expr: FunctionCallExpression) = emitFunctionCall(expr)

    override fun onPropertyAccess(expr: PropertyAccessExpression) = emitPropertyAccess(expr)

    override fun onIndex(expr: IndexExpression) = emitIndexExpression(expr)

    override fun onExists(expr: ExistsExpression) = emitExists(expr)

    override fun onMembership(expr: MembershipExpression) = emitMembership(expr)

    override fun onListTransform(expr: ListTransformExpression) = emitListTransform(expr)

    override fun onExpandCollapse(expr: ExpandCollapseExpression): ElmExpression =
        emitExpandCollapse(expr)

    override fun onDateTimeComponent(expr: DateTimeComponentExpression) =
        emitDateTimeComponent(expr)

    override fun onDurationBetween(expr: DurationBetweenExpression) = emitDurationBetween(expr)

    override fun onDifferenceBetween(expr: DifferenceBetweenExpression) =
        emitDifferenceBetween(expr)

    override fun onDurationOf(expr: DurationOfExpression) = emitDurationOf(expr)

    override fun onDifferenceOf(expr: DifferenceOfExpression) = emitDifferenceOf(expr)

    override fun onTimeBoundary(expr: TimeBoundaryExpression) = emitTimeBoundary(expr)

    override fun onWidth(expr: WidthExpression) = emitWidth(expr)

    override fun onElementExtractor(expr: ElementExtractorExpression) = emitElementExtractor(expr)

    override fun onTypeExtent(expr: TypeExtentExpression) = emitTypeExtent(expr)

    override fun onBetween(expr: BetweenExpression) = emitBetween(expr)

    override fun onIntervalRelation(expr: IntervalRelationExpression) = emitIntervalRelation(expr)

    override fun onQuery(expr: QueryExpression) = emitQuery(expr)

    override fun onRetrieve(expr: RetrieveExpression) = emitRetrieve(expr)

    override fun onUnsupported(expr: UnsupportedExpression): ElmExpression =
        throw ElmEmitter.UnsupportedNodeException(
            "UnsupportedExpression '${expr.description}' is not supported."
        )
}
