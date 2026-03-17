package org.hl7.cql.ast

/**
 * A catamorphism (fold) over the [Expression] AST. Children are pre-folded by [fold] before being
 * passed to the handler — implementors combine child results without manual recursion.
 *
 * Adding a new [Expression] subtype without adding a handler in [fold] is a compile error (sealed
 * class exhaustiveness). Adding a handler in [fold] without a corresponding algebra method is also
 * a compile error.
 *
 * For nodes with expression children, the handler receives the original AST node plus the
 * pre-folded child results. For leaf nodes (no expression children), the handler receives only the
 * AST node.
 *
 * Usage:
 * ```
 * val myAlgebra = object : ExpressionFold<MyResult> {
 *     override fun onIf(expr: IfExpression, condition: MyResult,
 *                       thenBranch: MyResult, elseBranch: MyResult) = ...
 *     override fun onIdentifier(expr: IdentifierExpression) = ...
 *     // ... one method per expression type
 * }
 * val result = myAlgebra.fold(someExpression)
 * ```
 */
@Suppress("TooManyFunctions")
interface ExpressionFold<R> {

    // --- Leaves (no expression children) ---
    fun onLiteral(expr: LiteralExpression, children: LiteralChildren<R>): R

    fun onIdentifier(expr: IdentifierExpression): R

    fun onExternalConstant(expr: ExternalConstantExpression): R

    fun onTypeExtent(expr: TypeExtentExpression): R

    fun onRetrieve(expr: RetrieveExpression): R

    fun onUnsupported(expr: UnsupportedExpression): R

    // --- Unary (one expression child) ---
    fun onUnaryOperator(expr: OperatorUnaryExpression, operand: R): R

    fun onBooleanTest(expr: BooleanTestExpression, operand: R): R

    fun onExists(expr: ExistsExpression, operand: R): R

    fun onIs(expr: IsExpression, operand: R): R

    fun onAs(expr: AsExpression, operand: R): R

    fun onCast(expr: CastExpression, operand: R): R

    fun onConversion(expr: ConversionExpression, operand: R): R

    fun onListTransform(expr: ListTransformExpression, operand: R): R

    fun onDateTimeComponent(expr: DateTimeComponentExpression, operand: R): R

    fun onDurationOf(expr: DurationOfExpression, operand: R): R

    fun onDifferenceOf(expr: DifferenceOfExpression, operand: R): R

    fun onTimeBoundary(expr: TimeBoundaryExpression, operand: R): R

    fun onWidth(expr: WidthExpression, operand: R): R

    fun onElementExtractor(expr: ElementExtractorExpression, operand: R): R

    fun onPropertyAccess(expr: PropertyAccessExpression, target: R): R

    // --- Binary (two expression children) ---
    fun onBinaryOperator(expr: OperatorBinaryExpression, left: R, right: R): R

    fun onMembership(expr: MembershipExpression, left: R, right: R): R

    fun onIndex(expr: IndexExpression, target: R, index: R): R

    fun onDurationBetween(expr: DurationBetweenExpression, lower: R, upper: R): R

    fun onDifferenceBetween(expr: DifferenceBetweenExpression, lower: R, upper: R): R

    fun onIntervalRelation(expr: IntervalRelationExpression, left: R, right: R): R

    // --- Ternary ---
    fun onIf(expr: IfExpression, condition: R, thenBranch: R, elseBranch: R): R

    fun onBetween(expr: BetweenExpression, input: R, lower: R, upper: R): R

    // --- Variable children ---
    fun onCase(expr: CaseExpression, comparand: R?, cases: List<CaseChildren<R>>, elseResult: R): R

    fun onFunctionCall(expr: FunctionCallExpression, target: R?, arguments: List<R>): R

    fun onExpandCollapse(expr: ExpandCollapseExpression, operand: R, per: R?): R

    fun onQuery(expr: QueryExpression, children: QueryChildren<R>): R

    /**
     * Fold an [Expression] by pre-folding children and dispatching to the appropriate handler. This
     * is the single `when` that enforces exhaustiveness — if a new [Expression] subtype is added
     * without a corresponding handler, compilation fails.
     */
    @Suppress("CyclomaticComplexMethod")
    fun fold(expr: Expression): R =
        when (expr) {
            // Leaves
            is LiteralExpression -> onLiteral(expr, foldLiteral(expr.literal))
            is IdentifierExpression -> onIdentifier(expr)
            is ExternalConstantExpression -> onExternalConstant(expr)
            is TypeExtentExpression -> onTypeExtent(expr)
            is RetrieveExpression -> onRetrieve(expr)
            is UnsupportedExpression -> onUnsupported(expr)

            // Unary
            is OperatorUnaryExpression -> onUnaryOperator(expr, fold(expr.operand))
            is BooleanTestExpression -> onBooleanTest(expr, fold(expr.operand))
            is ExistsExpression -> onExists(expr, fold(expr.operand))
            is IsExpression -> onIs(expr, fold(expr.operand))
            is AsExpression -> onAs(expr, fold(expr.operand))
            is CastExpression -> onCast(expr, fold(expr.operand))
            is ConversionExpression -> onConversion(expr, fold(expr.operand))
            is ListTransformExpression -> onListTransform(expr, fold(expr.operand))
            is DateTimeComponentExpression -> onDateTimeComponent(expr, fold(expr.operand))
            is DurationOfExpression -> onDurationOf(expr, fold(expr.operand))
            is DifferenceOfExpression -> onDifferenceOf(expr, fold(expr.operand))
            is TimeBoundaryExpression -> onTimeBoundary(expr, fold(expr.operand))
            is WidthExpression -> onWidth(expr, fold(expr.operand))
            is ElementExtractorExpression -> onElementExtractor(expr, fold(expr.operand))
            is PropertyAccessExpression -> onPropertyAccess(expr, fold(expr.target))

            // Binary
            is OperatorBinaryExpression -> onBinaryOperator(expr, fold(expr.left), fold(expr.right))
            is MembershipExpression -> onMembership(expr, fold(expr.left), fold(expr.right))
            is IndexExpression -> onIndex(expr, fold(expr.target), fold(expr.index))
            is DurationBetweenExpression ->
                onDurationBetween(expr, fold(expr.lower), fold(expr.upper))
            is DifferenceBetweenExpression ->
                onDifferenceBetween(expr, fold(expr.lower), fold(expr.upper))
            is IntervalRelationExpression ->
                onIntervalRelation(expr, fold(expr.left), fold(expr.right))

            // Ternary
            is IfExpression ->
                onIf(expr, fold(expr.condition), fold(expr.thenBranch), fold(expr.elseBranch))
            is BetweenExpression ->
                onBetween(expr, fold(expr.input), fold(expr.lower), fold(expr.upper))

            // Variable children
            is CaseExpression ->
                onCase(
                    expr,
                    expr.comparand?.let { fold(it) },
                    expr.cases.map { CaseChildren(fold(it.condition), fold(it.result)) },
                    fold(expr.elseResult),
                )
            is FunctionCallExpression ->
                onFunctionCall(expr, expr.target?.let { fold(it) }, expr.arguments.map { fold(it) })
            is ExpandCollapseExpression ->
                onExpandCollapse(expr, fold(expr.operand), expr.perExpression?.let { fold(it) })
            is QueryExpression -> onQuery(expr, foldQueryChildren(expr))
        }

    private fun foldQueryChildren(expr: QueryExpression): QueryChildren<R> {
        val sources =
            expr.sources.map { source ->
                when (val qs = source.source) {
                    is ExpressionQuerySource -> fold(qs.expression)
                    else -> null
                }
            }
        return QueryChildren(
            sourceExpressions = sources,
            letExpressions = expr.lets.map { fold(it.expression) },
            inclusionConditions =
                expr.inclusions.map {
                    when (it) {
                        is WithClause -> fold(it.condition)
                        is WithoutClause -> fold(it.condition)
                    }
                },
            inclusionSourceExpressions =
                expr.inclusions.map {
                    when (it) {
                        is WithClause -> {
                            val qs = it.source.source
                            if (qs is ExpressionQuerySource) fold(qs.expression) else null
                        }
                        is WithoutClause -> {
                            val qs = it.source.source
                            if (qs is ExpressionQuerySource) fold(qs.expression) else null
                        }
                    }
                },
            where = expr.where?.let { fold(it) },
            returnExpression = expr.result?.let { fold(it.expression) },
            aggregateStarting = expr.aggregate?.starting?.let { fold(it) },
            aggregateExpression = expr.aggregate?.let { fold(it.expression) },
            sortExpressions = expr.sort?.items?.map { fold(it.expression) } ?: emptyList(),
        )
    }

    private fun foldLiteral(literal: Literal): LiteralChildren<R> =
        when (literal) {
            is ListLiteral -> LiteralChildren(elements = literal.elements.map { fold(it) })
            is IntervalLiteral ->
                LiteralChildren(
                    intervalLow = fold(literal.lower),
                    intervalHigh = fold(literal.upper),
                )
            is TupleLiteral ->
                LiteralChildren(tupleElements = literal.elements.map { fold(it.expression) })
            is InstanceLiteral ->
                LiteralChildren(tupleElements = literal.elements.map { fold(it.expression) })
            is ConceptLiteral -> LiteralChildren() // codes are CodeLiteral, not expressions
            else -> LiteralChildren() // simple literals have no expression children
        }
}

/** Pre-folded children of a [CaseItem]. */
data class CaseChildren<R>(val condition: R, val result: R)

/** Pre-folded expression children of a [Literal] within a [LiteralExpression]. */
data class LiteralChildren<R>(
    val elements: List<R> = emptyList(),
    val intervalLow: R? = null,
    val intervalHigh: R? = null,
    val tupleElements: List<R> = emptyList(),
)

/** Pre-folded expression children of a [QueryExpression]. */
data class QueryChildren<R>(
    val sourceExpressions: List<R?>,
    val letExpressions: List<R>,
    val inclusionConditions: List<R>,
    val inclusionSourceExpressions: List<R?>,
    val where: R?,
    val returnExpression: R?,
    val aggregateStarting: R?,
    val aggregateExpression: R?,
    val sortExpressions: List<R>,
)
