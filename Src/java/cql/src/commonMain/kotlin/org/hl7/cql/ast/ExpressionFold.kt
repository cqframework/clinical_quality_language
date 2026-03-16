package org.hl7.cql.ast

/**
 * A fold (catamorphism) over the [Expression] AST. Implementors define how each expression type
 * maps to a result of type [R]. The [fold] function handles dispatch — adding a new [Expression]
 * subtype without adding a case here is a compile error (sealed class exhaustiveness).
 *
 * Child expressions are **not** automatically folded — the implementor receives the raw AST nodes
 * and decides whether/how to recurse (by calling [fold] on sub-expressions). This gives full
 * control over traversal order, short-circuiting, and accumulator threading.
 *
 * Usage:
 * ```
 * val myAlgebra = object : ExpressionFold<MyResult> {
 *     override fun onLiteral(expr: LiteralExpression) = ...
 *     override fun onBinaryOperator(expr: OperatorBinaryExpression) = ...
 *     // ... one method per expression type
 * }
 * val result = myAlgebra.fold(someExpression)
 * ```
 */
@Suppress("TooManyFunctions")
interface ExpressionFold<R> {

    // --- Literals and identifiers ---
    fun onLiteral(expr: LiteralExpression): R

    fun onIdentifier(expr: IdentifierExpression): R

    fun onExternalConstant(expr: ExternalConstantExpression): R

    // --- Operators ---
    fun onBinaryOperator(expr: OperatorBinaryExpression): R

    fun onUnaryOperator(expr: OperatorUnaryExpression): R

    fun onBooleanTest(expr: BooleanTestExpression): R

    // --- Control flow ---
    fun onIf(expr: IfExpression): R

    fun onCase(expr: CaseExpression): R

    // --- Type operators ---
    fun onIs(expr: IsExpression): R

    fun onAs(expr: AsExpression): R

    fun onCast(expr: CastExpression): R

    fun onConversion(expr: ConversionExpression): R

    // --- Functions and access ---
    fun onFunctionCall(expr: FunctionCallExpression): R

    fun onPropertyAccess(expr: PropertyAccessExpression): R

    fun onIndex(expr: IndexExpression): R

    // --- Collections ---
    fun onExists(expr: ExistsExpression): R

    fun onMembership(expr: MembershipExpression): R

    fun onListTransform(expr: ListTransformExpression): R

    fun onExpandCollapse(expr: ExpandCollapseExpression): R

    // --- Temporal ---
    fun onDateTimeComponent(expr: DateTimeComponentExpression): R

    fun onDurationBetween(expr: DurationBetweenExpression): R

    fun onDifferenceBetween(expr: DifferenceBetweenExpression): R

    fun onDurationOf(expr: DurationOfExpression): R

    fun onDifferenceOf(expr: DifferenceOfExpression): R

    fun onTimeBoundary(expr: TimeBoundaryExpression): R

    fun onWidth(expr: WidthExpression): R

    fun onElementExtractor(expr: ElementExtractorExpression): R

    fun onTypeExtent(expr: TypeExtentExpression): R

    fun onBetween(expr: BetweenExpression): R

    fun onIntervalRelation(expr: IntervalRelationExpression): R

    // --- Queries ---
    fun onQuery(expr: QueryExpression): R

    fun onRetrieve(expr: RetrieveExpression): R

    // --- Unsupported (placeholder for unimplemented grammar rules) ---
    fun onUnsupported(expr: UnsupportedExpression): R

    /**
     * Fold an [Expression] by dispatching to the appropriate handler. This is the single `when`
     * that enforces exhaustiveness — if a new [Expression] subtype is added without a corresponding
     * handler method, compilation fails here.
     */
    @Suppress("CyclomaticComplexMethod")
    fun fold(expr: Expression): R =
        when (expr) {
            is LiteralExpression -> onLiteral(expr)
            is IdentifierExpression -> onIdentifier(expr)
            is ExternalConstantExpression -> onExternalConstant(expr)
            is OperatorBinaryExpression -> onBinaryOperator(expr)
            is OperatorUnaryExpression -> onUnaryOperator(expr)
            is BooleanTestExpression -> onBooleanTest(expr)
            is IfExpression -> onIf(expr)
            is CaseExpression -> onCase(expr)
            is IsExpression -> onIs(expr)
            is AsExpression -> onAs(expr)
            is CastExpression -> onCast(expr)
            is ConversionExpression -> onConversion(expr)
            is FunctionCallExpression -> onFunctionCall(expr)
            is PropertyAccessExpression -> onPropertyAccess(expr)
            is IndexExpression -> onIndex(expr)
            is ExistsExpression -> onExists(expr)
            is MembershipExpression -> onMembership(expr)
            is ListTransformExpression -> onListTransform(expr)
            is ExpandCollapseExpression -> onExpandCollapse(expr)
            is DateTimeComponentExpression -> onDateTimeComponent(expr)
            is DurationBetweenExpression -> onDurationBetween(expr)
            is DifferenceBetweenExpression -> onDifferenceBetween(expr)
            is DurationOfExpression -> onDurationOf(expr)
            is DifferenceOfExpression -> onDifferenceOf(expr)
            is TimeBoundaryExpression -> onTimeBoundary(expr)
            is WidthExpression -> onWidth(expr)
            is ElementExtractorExpression -> onElementExtractor(expr)
            is TypeExtentExpression -> onTypeExtent(expr)
            is BetweenExpression -> onBetween(expr)
            is IntervalRelationExpression -> onIntervalRelation(expr)
            is QueryExpression -> onQuery(expr)
            is RetrieveExpression -> onRetrieve(expr)
            is UnsupportedExpression -> onUnsupported(expr)
        }
}
