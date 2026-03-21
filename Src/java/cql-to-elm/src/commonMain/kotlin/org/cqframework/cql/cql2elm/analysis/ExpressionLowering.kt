@file:Suppress("TooManyFunctions")

package org.cqframework.cql.cql2elm.analysis

import org.hl7.cql.ast.AsExpression
import org.hl7.cql.ast.BetweenExpression
import org.hl7.cql.ast.BinaryOperator
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
import org.hl7.cql.ast.ExpressionDefinition
import org.hl7.cql.ast.ExpressionFold
import org.hl7.cql.ast.ExpressionFunctionBody
import org.hl7.cql.ast.ExternalConstantExpression
import org.hl7.cql.ast.FunctionCallExpression
import org.hl7.cql.ast.FunctionDefinition
import org.hl7.cql.ast.Identifier
import org.hl7.cql.ast.IdentifierExpression
import org.hl7.cql.ast.IfExpression
import org.hl7.cql.ast.ImplicitCastExpression
import org.hl7.cql.ast.IndexExpression
import org.hl7.cql.ast.IntervalExpression
import org.hl7.cql.ast.IntervalRelationExpression
import org.hl7.cql.ast.IsExpression
import org.hl7.cql.ast.Library
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
import org.hl7.cql.ast.Statement
import org.hl7.cql.ast.StringLiteral
import org.hl7.cql.ast.TimeBoundaryExpression
import org.hl7.cql.ast.TypeExtentExpression
import org.hl7.cql.ast.UnsupportedExpression
import org.hl7.cql.ast.WidthExpression
import org.hl7.cql.model.IntervalType

/**
 * AST→AST structural lowering phase. Rewrites complex CQL phrases into trees of simpler AST nodes,
 * using existing node types. Reads the [SemanticModel] for type information (is this an interval?)
 * but does not modify types — that's the analysis phase's job.
 *
 * After lowering, each AST node maps 1:1 to an ELM node, making emission purely mechanical.
 *
 * The lowered AST shares child expression nodes by reference with the original AST. Only the
 * rewritten parent nodes are new objects. The TypeResolver re-types the new nodes in a
 * post-lowering pass.
 */
class ExpressionLowering(
    private val semanticModel: SemanticModel,
    private val syntheticTable: SyntheticTable = semanticModel.syntheticTable,
) : ExpressionFold<Expression> {

    /** Rewrite an expression, transferring any synthetics from the original to the replacement. */
    private fun rewrite(original: Expression, replacement: Expression): Expression {
        if (replacement !== original) {
            syntheticTable.transfer(original, replacement)
        }
        return replacement
    }

    fun lowerLibrary(library: Library): Library {
        val newStatements = library.statements.map { lowerStatement(it) }
        val newDefinitions = library.definitions.map { lowerDefinition(it) }
        return if (
            newStatements.indices.all { newStatements[it] === library.statements[it] } &&
                newDefinitions.indices.all { newDefinitions[it] === library.definitions[it] }
        ) {
            library
        } else {
            library.copy(statements = newStatements, definitions = newDefinitions)
        }
    }

    private fun lowerStatement(statement: Statement): Statement =
        when (statement) {
            is ExpressionDefinition -> {
                val lowered = fold(statement.expression)
                if (lowered === statement.expression) statement
                else statement.copy(expression = lowered)
            }
            is FunctionDefinition -> {
                val body = statement.body
                if (body is ExpressionFunctionBody) {
                    val lowered = fold(body.expression)
                    if (lowered === body.expression) statement
                    else statement.copy(body = body.copy(expression = lowered))
                } else {
                    statement
                }
            }
            else -> statement
        }

    private fun lowerDefinition(
        definition: org.hl7.cql.ast.Definition
    ): org.hl7.cql.ast.Definition =
        when (definition) {
            is org.hl7.cql.ast.ParameterDefinition -> {
                val default = definition.default
                if (default != null) {
                    val lowered = fold(default)
                    if (lowered === default) definition else definition.copy(default = lowered)
                } else {
                    definition
                }
            }
            else -> definition
        }

    // --- Structural lowering handlers ---

    override fun onBinaryOperator(
        expr: OperatorBinaryExpression,
        left: Expression,
        right: Expression,
    ): Expression {
        val op = expr.operator

        // CONCAT (&): wrap operands in Coalesce(operand, '')
        if (op == BinaryOperator.CONCAT) {
            val l = wrapInCoalesce(left)
            val r = wrapInCoalesce(right)
            return if (l === left && r === right) {
                if (left === expr.left && right === expr.right) expr
                else rewrite(expr, expr.copy(left = left, right = right))
            } else {
                rewrite(expr, expr.copy(left = l, right = r))
            }
        }

        // Add on strings → rewrite operator to CONCAT (emission maps CONCAT → Concatenate)
        if (op == BinaryOperator.ADD) {
            val resolution = semanticModel.getOperatorResolution(expr)
            if (resolution?.operator?.resultType?.toString() == "System.String") {
                return rewrite(
                    expr,
                    expr.copy(operator = BinaryOperator.CONCAT, left = left, right = right),
                )
            }
        }

        // Identity for other operators
        return if (left === expr.left && right === expr.right) expr
        else expr.copy(left = left, right = right)
    }

    private fun wrapInCoalesce(expr: Expression): Expression {
        // Idempotent: don't double-wrap
        if (expr is FunctionCallExpression && expr.function.value == "Coalesce") return expr
        val emptyString =
            LiteralExpression(literal = StringLiteral(value = ""), locator = expr.locator)
        return FunctionCallExpression(
            target = null,
            function = Identifier("Coalesce"),
            arguments = listOf(expr, emptyString),
            locator = expr.locator,
        )
    }

    // --- Identity handlers (pass through pre-folded children) ---

    override fun onLiteral(expr: LiteralExpression, children: LiteralChildren<Expression>) = expr

    override fun onIdentifier(expr: IdentifierExpression) = expr

    override fun onExternalConstant(expr: ExternalConstantExpression) = expr

    override fun onUnaryOperator(expr: OperatorUnaryExpression, operand: Expression): Expression =
        if (operand === expr.operand) expr else expr.copy(operand = operand)

    override fun onBooleanTest(expr: BooleanTestExpression, operand: Expression): Expression =
        if (operand === expr.operand) expr else expr.copy(operand = operand)

    override fun onIf(
        expr: IfExpression,
        condition: Expression,
        thenBranch: Expression,
        elseBranch: Expression,
    ): Expression =
        if (
            condition === expr.condition &&
                thenBranch === expr.thenBranch &&
                elseBranch === expr.elseBranch
        )
            expr
        else expr.copy(condition = condition, thenBranch = thenBranch, elseBranch = elseBranch)

    override fun onCase(
        expr: CaseExpression,
        comparand: Expression?,
        cases: List<CaseChildren<Expression>>,
        elseResult: Expression,
    ): Expression = expr // cases have complex children; pass through for now

    override fun onIs(expr: IsExpression, operand: Expression) =
        if (operand === expr.operand) expr else expr.copy(operand = operand)

    override fun onAs(expr: AsExpression, operand: Expression) =
        if (operand === expr.operand) expr else expr.copy(operand = operand)

    override fun onImplicitCast(expr: ImplicitCastExpression, operand: Expression) =
        if (operand === expr.operand) expr else expr.copy(operand = operand)

    override fun onCast(expr: CastExpression, operand: Expression) =
        if (operand === expr.operand) expr else expr.copy(operand = operand)

    override fun onConversion(expr: ConversionExpression, operand: Expression) =
        if (operand === expr.operand) expr else expr.copy(operand = operand)

    override fun onFunctionCall(
        expr: FunctionCallExpression,
        target: Expression?,
        arguments: List<Expression>,
    ): Expression {
        // CalculateAgeIn*At with mixed DateTime/Date args: enforce compatibility by
        // converting the DateTime operand to Date, matching the legacy translator's
        // behavior (SystemFunctionResolver.enforceCompatible). This ensures the (Date,Date)
        // overload is used rather than promoting Date→DateTime.
        val lowered = lowerCalculateAgeAtCompatibility(expr, arguments)
        if (lowered != null) return lowered

        val argsChanged = arguments.indices.any { arguments[it] !== expr.arguments[it] }
        val targetChanged = target !== expr.target
        return if (!targetChanged && !argsChanged) expr
        else expr.copy(target = target, arguments = arguments)
    }

    /**
     * For CalculateAgeIn{Years,Months,Weeks,Days}At calls where one arg is DateTime and the other
     * is Date, wrap the DateTime arg in ConversionExpression(ToDate) so both operands are Date.
     * This matches the CQL spec behavior: age calculations at day precision or coarser should
     * operate on Date, not DateTime.
     *
     * Returns null if no lowering is needed.
     */
    private fun lowerCalculateAgeAtCompatibility(
        expr: FunctionCallExpression,
        arguments: List<Expression>,
    ): Expression? {
        if (arguments.size != 2) return null
        val name = expr.function.value
        // Only apply to day-or-coarser precisions (Years, Months, Weeks, Days).
        // Hours, Minutes, Seconds require DateTime precision — no conversion needed.
        val isAgeAtDayOrCoarser =
            name.startsWith("CalculateAgeIn") &&
                name.endsWith("At") &&
                !name.contains("Hours") &&
                !name.contains("Minutes") &&
                !name.contains("Seconds")
        if (!isAgeAtDayOrCoarser) return null

        val type0 = semanticModel[expr.arguments[0]]?.toString()
        val type1 = semanticModel[expr.arguments[1]]?.toString()
        if (type0 == null || type1 == null) return null

        // If one is DateTime and the other is Date, convert the DateTime one to Date.
        val newArgs = arguments.toMutableList()
        if (type0 == "System.DateTime" && type1 == "System.Date") {
            newArgs[0] = wrapInToDate(arguments[0], expr)
        } else if (type0 == "System.Date" && type1 == "System.DateTime") {
            newArgs[1] = wrapInToDate(arguments[1], expr)
        } else {
            return null
        }
        // Don't use rewrite() here — we don't want to transfer the old synthetics
        // (e.g., Date→DateTime on arg[1]) since the new args are both Date and the
        // (Date,Date) overload matches exactly with no conversions needed.
        return expr.copy(arguments = newArgs)
    }

    private fun wrapInToDate(operand: Expression, context: Expression): ConversionExpression =
        ConversionExpression(
            operand = operand,
            destinationType =
                org.hl7.cql.ast.NamedTypeSpecifier(
                    name = org.hl7.cql.ast.QualifiedIdentifier(listOf("Date"))
                ),
            locator = context.locator,
        )

    override fun onPropertyAccess(expr: PropertyAccessExpression, target: Expression) =
        if (target === expr.target) expr else expr.copy(target = target)

    override fun onIndex(expr: IndexExpression, target: Expression, index: Expression) =
        if (target === expr.target && index === expr.index) expr
        else expr.copy(target = target, index = index)

    override fun onExists(expr: ExistsExpression, operand: Expression) =
        if (operand === expr.operand) expr else expr.copy(operand = operand)

    override fun onMembership(
        expr: MembershipExpression,
        left: Expression,
        right: Expression,
    ): Expression {
        var l = left
        var r = right
        val leftType = semanticModel[expr.left]
        val rightType = semanticModel[expr.right]
        // Interval<Any> expansion for membership operators
        if (
            expr.operator == org.hl7.cql.ast.MembershipOperator.IN &&
                rightType is IntervalType &&
                rightType.pointType.toString() == "System.Any" &&
                leftType != null &&
                leftType.toString() != "System.Any"
        ) {
            r = expandIntervalToType(right, leftType)
        } else if (
            expr.operator == org.hl7.cql.ast.MembershipOperator.CONTAINS &&
                leftType is IntervalType &&
                leftType.pointType.toString() == "System.Any" &&
                rightType != null &&
                rightType.toString() != "System.Any"
        ) {
            l = expandIntervalToType(left, rightType)
        }
        return if (l === expr.left && r === expr.right) expr
        else rewrite(expr, expr.copy(left = l, right = r))
    }

    override fun onListTransform(expr: ListTransformExpression, operand: Expression): Expression {
        // Heterogeneous flatten: wrap operand in Query(return=As(X, List<T>)) when the
        // list literal has mixed List<T> and T elements.
        if (expr.listTransformKind == org.hl7.cql.ast.ListTransformKind.FLATTEN) {
            val wrapped = lowerHeterogeneousFlatten(expr, operand)
            if (wrapped != null) return rewrite(expr, expr.copy(operand = wrapped))
        }
        return if (operand === expr.operand) expr else expr.copy(operand = operand)
    }

    /** Returns a Query-wrapped operand if the flatten has a heterogeneous list, or null. */
    private fun lowerHeterogeneousFlatten(
        expr: ListTransformExpression,
        operand: Expression,
    ): Expression? {
        val srcExpr = expr.operand
        if (srcExpr !is LiteralExpression) return null
        val literal = srcExpr.literal
        if (literal !is org.hl7.cql.ast.ListLiteral) return null

        val elemTypes = literal.elements.mapNotNull { semanticModel[it] }
        if (elemTypes.isEmpty()) return null
        val hasListType = elemTypes.any { it is org.hl7.cql.model.ListType }
        val hasNonListType = elemTypes.any { it !is org.hl7.cql.model.ListType }
        if (!hasListType || !hasNonListType) return null

        val targetListType = elemTypes.filterIsInstance<org.hl7.cql.model.ListType>().first()
        val loc = expr.locator

        // Build AST: Query(source=[X from operand], return=As(X, targetListType))
        val aliasName = "X"
        val aliasRef =
            IdentifierExpression(
                name = org.hl7.cql.ast.QualifiedIdentifier(listOf(aliasName)),
                locator = loc,
            )
        val typeSpec =
            org.hl7.cql.ast.ListTypeSpecifier(
                elementType =
                    org.hl7.cql.ast.NamedTypeSpecifier(
                        name =
                            org.hl7.cql.ast.QualifiedIdentifier(
                                listOf(
                                    targetListType.elementType.toString().removePrefix("System.")
                                )
                            )
                    )
            )
        val castExpr = ImplicitCastExpression(operand = aliasRef, type = typeSpec, locator = loc)
        return org.hl7.cql.ast.QueryExpression(
            sources =
                listOf(
                    org.hl7.cql.ast.AliasedQuerySource(
                        source = org.hl7.cql.ast.ExpressionQuerySource(expression = operand),
                        alias = org.hl7.cql.ast.Identifier(aliasName),
                        locator = loc,
                    )
                ),
            lets = emptyList(),
            inclusions = emptyList(),
            result =
                org.hl7.cql.ast.ReturnClause(
                    all = true,
                    distinct = false,
                    expression = castExpr,
                    locator = loc,
                ),
            locator = loc,
        )
    }

    override fun onExpandCollapse(
        expr: ExpandCollapseExpression,
        operand: Expression,
        per: Expression?,
    ) =
        if (operand === expr.operand && per === expr.perExpression) expr
        else expr.copy(operand = operand, perExpression = per)

    override fun onDateTimeComponent(expr: DateTimeComponentExpression, operand: Expression) =
        if (operand === expr.operand) expr else expr.copy(operand = operand)

    override fun onDurationBetween(
        expr: DurationBetweenExpression,
        lower: Expression,
        upper: Expression,
    ) =
        if (lower === expr.lower && upper === expr.upper) expr
        else expr.copy(lower = lower, upper = upper)

    override fun onDifferenceBetween(
        expr: DifferenceBetweenExpression,
        lower: Expression,
        upper: Expression,
    ) =
        if (lower === expr.lower && upper === expr.upper) expr
        else expr.copy(lower = lower, upper = upper)

    override fun onDurationOf(expr: DurationOfExpression, operand: Expression) =
        if (operand === expr.operand) expr else expr.copy(operand = operand)

    override fun onDifferenceOf(expr: DifferenceOfExpression, operand: Expression) =
        if (operand === expr.operand) expr else expr.copy(operand = operand)

    override fun onTimeBoundary(expr: TimeBoundaryExpression, operand: Expression) =
        if (operand === expr.operand) expr else expr.copy(operand = operand)

    override fun onWidth(expr: WidthExpression, operand: Expression) =
        if (operand === expr.operand) expr else expr.copy(operand = operand)

    override fun onElementExtractor(expr: ElementExtractorExpression, operand: Expression) =
        if (operand === expr.operand) expr else expr.copy(operand = operand)

    override fun onTypeExtent(expr: TypeExtentExpression) = expr

    override fun onBetween(
        expr: BetweenExpression,
        input: Expression,
        lower: Expression,
        upper: Expression,
    ) =
        if (input === expr.input && lower === expr.lower && upper === expr.upper) expr
        else expr.copy(input = input, lower = lower, upper = upper)

    override fun onIntervalExpression(
        expr: IntervalExpression,
        low: Expression,
        high: Expression,
        lowClosed: Expression,
        highClosed: Expression,
    ): Expression =
        if (
            low === expr.low &&
                high === expr.high &&
                lowClosed === expr.lowClosedExpression &&
                highClosed === expr.highClosedExpression
        )
            expr
        else
            expr.copy(
                low = low,
                high = high,
                lowClosedExpression = lowClosed,
                highClosedExpression = highClosed,
            )

    override fun onIntervalRelation(
        expr: IntervalRelationExpression,
        left: Expression,
        right: Expression,
    ): Expression {
        // Interval type promotion: when both operands are non-literal intervals with
        // different point types, expand the one needing conversion into bound access +
        // conversion + reconstruction. This handles Interval<Date> → Interval<DateTime>
        // for operators like IncludedIn/Includes. The IntervalConversion synthetic tells
        // us which slot needs conversion and what operator to use.
        var loweredLeft = left
        var loweredRight = right
        val leftType = semanticModel[expr.left]
        val rightType = semanticModel[expr.right]

        // Interval<Any> expansion: when one operand is Interval<Any> (non-literal) and the
        // other has a concrete point type, expand using PropertyAccessExpression + AsExpression.
        if (
            leftType is IntervalType &&
                rightType is IntervalType &&
                rightType.pointType.toString() == "System.Any" &&
                leftType.pointType.toString() != "System.Any" &&
                !isIntervalLiteral(expr.right)
        ) {
            loweredRight = expandIntervalToType(right, leftType.pointType)
        }

        val phrase = expr.phrase
        // Apply boundary selectors per phrase type. Identity-preserving: only create new
        // objects when boundaries actually changed operands.
        // Use lowered operands (Interval<Any> may have been expanded above).
        val l: Expression
        val r: Expression
        when (phrase) {
            is org.hl7.cql.ast.BeforeOrAfterIntervalPhrase ->
                return lowerBeforeOrAfter(expr, phrase, loweredLeft, loweredRight)
            is org.hl7.cql.ast.ConcurrentIntervalPhrase -> {
                l = applyBoundary(loweredLeft, phrase.leftBoundary)
                r = applyBoundary(loweredRight, phrase.rightBoundary)
            }
            is org.hl7.cql.ast.IncludesIntervalPhrase -> {
                l = loweredLeft
                r = applyBoundary(loweredRight, phrase.rightBoundary)
                if (isPointOperand(expr.right, phrase.rightBoundary, expr.left)) {
                    return lowerToMembership(
                        expr,
                        org.hl7.cql.ast.MembershipOperator.CONTAINS,
                        l,
                        r,
                        phrase.precision,
                        phrase.proper,
                    )
                }
            }
            is org.hl7.cql.ast.IncludedInIntervalPhrase -> {
                l = applyBoundary(loweredLeft, phrase.leftBoundary)
                r = loweredRight
                if (isPointOperand(expr.left, phrase.leftBoundary, null)) {
                    return lowerToMembership(
                        expr,
                        org.hl7.cql.ast.MembershipOperator.IN,
                        l,
                        r,
                        phrase.precision,
                        phrase.proper,
                    )
                }
            }
            is org.hl7.cql.ast.WithinIntervalPhrase ->
                return lowerWithin(expr, phrase, loweredLeft, loweredRight)
            else -> {
                l = loweredLeft
                r = loweredRight
            }
        }
        return if (l === expr.left && r === expr.right) expr
        else rewrite(expr, expr.copy(left = l, right = r))
    }

    /** Apply a boundary selector, wrapping in TimeBoundaryExpression for START/END. */
    private fun applyBoundary(
        operand: Expression,
        boundary: org.hl7.cql.ast.IntervalBoundarySelector?,
    ): Expression =
        when (boundary) {
            org.hl7.cql.ast.IntervalBoundarySelector.START ->
                TimeBoundaryExpression(
                    timeBoundaryKind = org.hl7.cql.ast.TimeBoundaryKind.START,
                    operand = operand,
                    locator = operand.locator,
                )
            org.hl7.cql.ast.IntervalBoundarySelector.END ->
                TimeBoundaryExpression(
                    timeBoundaryKind = org.hl7.cql.ast.TimeBoundaryKind.END,
                    operand = operand,
                    locator = operand.locator,
                )
            else -> operand // OCCURS and null are no-ops
        }

    /**
     * Lower a before/after phrase. Applies boundary selectors, point-interval promotion, quantity
     * offsets, and direction-based interval extraction — all as AST rewrites.
     */
    @Suppress("CyclomaticComplexMethod", "LongMethod", "NestedBlockDepth")
    private fun lowerBeforeOrAfter(
        expr: IntervalRelationExpression,
        phrase: org.hl7.cql.ast.BeforeOrAfterIntervalPhrase,
        foldedLeft: Expression,
        foldedRight: Expression,
    ): Expression {
        val isBefore =
            phrase.relationship.direction == org.hl7.cql.ast.TemporalRelationshipDirection.BEFORE
        val leftType = semanticModel[expr.left]
        val rightType = semanticModel[expr.right]

        if (phrase.offset == null) {
            // No offset: apply boundaries, then point-interval promotion
            var left = applyBoundary(foldedLeft, phrase.leftBoundary)
            var right = applyBoundary(foldedRight, phrase.rightBoundary)
            val leftIsPoint =
                leftType != null &&
                    (leftType !is IntervalType ||
                        phrase.leftBoundary == org.hl7.cql.ast.IntervalBoundarySelector.START ||
                        phrase.leftBoundary == org.hl7.cql.ast.IntervalBoundarySelector.END)
            val rightIsPoint =
                rightType != null &&
                    (rightType !is IntervalType ||
                        phrase.rightBoundary == org.hl7.cql.ast.IntervalBoundarySelector.START ||
                        phrase.rightBoundary == org.hl7.cql.ast.IntervalBoundarySelector.END)
            val leftIsInterval = leftType is IntervalType && !leftIsPoint
            val rightIsInterval = rightType is IntervalType && !rightIsPoint
            if (leftIsPoint && rightIsInterval) {
                left = promotePointToInterval(left)
            } else if (rightIsPoint && leftIsInterval) {
                right = promotePointToInterval(right)
            }
            // Return the interval relation with lowered operands but same phrase
            return rewrite(expr, expr.copy(left = left, right = right))
        }

        // Offset case: apply boundaries, then direction-based extraction
        var left = applyBoundary(foldedLeft, phrase.leftBoundary)
        var right = applyBoundary(foldedRight, phrase.rightBoundary)
        val leftStillInterval =
            leftType is IntervalType &&
                phrase.leftBoundary != org.hl7.cql.ast.IntervalBoundarySelector.START &&
                phrase.leftBoundary != org.hl7.cql.ast.IntervalBoundarySelector.END
        val rightStillInterval =
            rightType is IntervalType &&
                phrase.rightBoundary != org.hl7.cql.ast.IntervalBoundarySelector.START &&
                phrase.rightBoundary != org.hl7.cql.ast.IntervalBoundarySelector.END
        if (leftStillInterval) {
            left =
                TimeBoundaryExpression(
                    timeBoundaryKind =
                        if (isBefore) org.hl7.cql.ast.TimeBoundaryKind.END
                        else org.hl7.cql.ast.TimeBoundaryKind.START,
                    operand = left,
                    locator = left.locator,
                )
        }
        if (rightStillInterval) {
            right =
                TimeBoundaryExpression(
                    timeBoundaryKind =
                        if (isBefore) org.hl7.cql.ast.TimeBoundaryKind.START
                        else org.hl7.cql.ast.TimeBoundaryKind.END,
                    operand = right,
                    locator = right.locator,
                )
        }
        return rewrite(expr, expr.copy(left = left, right = right))
    }

    /** Promote a point to a degenerate interval: If(IsNull(p), Null, Interval[p, p]). */
    private fun promotePointToInterval(point: Expression): Expression {
        val loc = point.locator
        return IfExpression(
            condition =
                org.hl7.cql.ast.BooleanTestExpression(
                    operand = point,
                    kind = org.hl7.cql.ast.BooleanTestKind.IS_NULL,
                    negated = false,
                    locator = loc,
                ),
            thenBranch = LiteralExpression(literal = org.hl7.cql.ast.NullLiteral(), locator = loc),
            elseBranch =
                LiteralExpression(
                    literal =
                        org.hl7.cql.ast.IntervalLiteral(
                            lower = point,
                            upper = point,
                            lowerClosed = true,
                            upperClosed = true,
                            locator = loc,
                        ),
                    locator = loc,
                ),
            locator = loc,
        )
    }

    /**
     * Lower a within phrase: `A within N days of B` → `In(A, Interval[Sub(start,qty),
     * Add(end,qty)])`. For points, start=end=the point. For intervals, start=Start(B), end=End(B).
     * For intervals with boundary selector, start=end=the boundary-applied point. Non-proper adds
     * null check: `And(In(...), Not(IsNull(point)))`.
     */
    @Suppress("CyclomaticComplexMethod", "LongMethod")
    private fun lowerWithin(
        expr: IntervalRelationExpression,
        phrase: org.hl7.cql.ast.WithinIntervalPhrase,
        foldedLeft: Expression,
        foldedRight: Expression,
    ): Expression {
        val loc = expr.locator
        val left = applyBoundary(foldedLeft, phrase.leftBoundary)
        val rightType = semanticModel[expr.right]
        val rightIsInterval = rightType is IntervalType
        val rightBoundary = phrase.rightBoundary

        // Determine rightStart and rightEnd as AST expressions
        val rightStart: Expression
        val rightEnd: Expression
        if (rightIsInterval) {
            if (
                rightBoundary == org.hl7.cql.ast.IntervalBoundarySelector.START ||
                    rightBoundary == org.hl7.cql.ast.IntervalBoundarySelector.END
            ) {
                val point = applyBoundary(foldedRight, rightBoundary)
                rightStart = point
                rightEnd = point
            } else {
                rightStart =
                    TimeBoundaryExpression(
                        timeBoundaryKind = org.hl7.cql.ast.TimeBoundaryKind.START,
                        operand = foldedRight,
                        locator = loc,
                    )
                rightEnd =
                    TimeBoundaryExpression(
                        timeBoundaryKind = org.hl7.cql.ast.TimeBoundaryKind.END,
                        operand = foldedRight,
                        locator = loc,
                    )
            }
        } else {
            val point = applyBoundary(foldedRight, rightBoundary)
            rightStart = point
            rightEnd = point
        }

        // Build: In(left, Interval[Subtract(rightStart, qty), Add(rightEnd, qty)])
        val qty = LiteralExpression(literal = phrase.quantity, locator = loc)
        val qty2 = LiteralExpression(literal = phrase.quantity, locator = loc)
        val closed = !phrase.proper
        val lower =
            OperatorBinaryExpression(
                operator = org.hl7.cql.ast.BinaryOperator.SUBTRACT,
                left = rightStart,
                right = qty,
                locator = loc,
            )
        val upper =
            OperatorBinaryExpression(
                operator = org.hl7.cql.ast.BinaryOperator.ADD,
                left = rightEnd,
                right = qty2,
                locator = loc,
            )
        val interval =
            LiteralExpression(
                literal =
                    org.hl7.cql.ast.IntervalLiteral(
                        lower = lower,
                        upper = upper,
                        lowerClosed = closed,
                        upperClosed = closed,
                        locator = loc,
                    ),
                locator = loc,
            )
        val inExpr =
            MembershipExpression(
                operator = org.hl7.cql.ast.MembershipOperator.IN,
                left = left,
                right = interval,
                locator = loc,
            )

        // Non-proper with point (or boundary-applied interval): add null check
        val rightEffectivelyPoint = !rightIsInterval || rightBoundary != null
        return if (!phrase.proper && rightEffectivelyPoint) {
            val nullTarget = applyBoundary(foldedRight, rightBoundary)
            OperatorBinaryExpression(
                operator = org.hl7.cql.ast.BinaryOperator.AND,
                left = inExpr,
                right =
                    org.hl7.cql.ast.OperatorUnaryExpression(
                        operator = org.hl7.cql.ast.UnaryOperator.NOT,
                        operand =
                            org.hl7.cql.ast.BooleanTestExpression(
                                operand = nullTarget,
                                kind = org.hl7.cql.ast.BooleanTestKind.IS_NULL,
                                negated = false,
                                locator = loc,
                            ),
                        locator = loc,
                    ),
                locator = loc,
            )
        } else {
            inExpr
        }
    }

    /**
     * Check if an expression is a point type (not list/interval) for includes/includedIn dispatch.
     */
    private fun isPointOperand(
        expression: Expression,
        boundary: org.hl7.cql.ast.IntervalBoundarySelector?,
        otherExpression: Expression?,
    ): Boolean {
        // Boundary START/END always extracts a point
        if (boundary != null && boundary != org.hl7.cql.ast.IntervalBoundarySelector.OCCURS) {
            return true
        }
        val type = semanticModel[expression]
        if (type != null) {
            if (type !is org.hl7.cql.model.ListType && type !is IntervalType) return true
        }
        // Empty list treated as element when the other side has concrete element type
        if (otherExpression != null && isEmptyListLiteral(expression)) {
            val otherType = semanticModel[otherExpression]
            if (otherType is org.hl7.cql.model.ListType) {
                val elemType = otherType.elementType
                if (elemType.toString() != "System.Any") return true
            }
        }
        // Interval<Any> left: treat right as element (Contains)
        if (otherExpression != null) {
            val otherType = semanticModel[otherExpression]
            if (otherType is IntervalType && otherType.pointType.toString() == "System.Any") {
                return true
            }
        }
        return false
    }

    private fun isEmptyListLiteral(expression: Expression): Boolean =
        expression is LiteralExpression &&
            expression.literal is org.hl7.cql.ast.ListLiteral &&
            (expression.literal as org.hl7.cql.ast.ListLiteral).elements.isEmpty()

    /** Rewrite includes/includedIn to MembershipExpression when one operand is a point. */
    private fun lowerToMembership(
        expr: IntervalRelationExpression,
        operator: org.hl7.cql.ast.MembershipOperator,
        left: Expression,
        right: Expression,
        precision: String?,
        proper: Boolean,
    ): Expression {
        // MembershipExpression doesn't have a proper flag. For proper Contains/In,
        // the emission handles it. Keep as IntervalRelationExpression for proper cases.
        if (proper) {
            return rewrite(expr, expr.copy(left = left, right = right))
        }
        return rewrite(
            expr,
            MembershipExpression(
                operator = operator,
                left = left,
                right = right,
                precision = precision,
                locator = expr.locator,
            ),
        )
    }

    /**
     * Expand a non-literal Interval<Any> expression into an explicit interval construction with
     * PropertyAccessExpression for bounds, wrapped in AsExpression for the target point type.
     */
    private fun expandIntervalToType(
        intervalExpr: Expression,
        targetPointType: org.hl7.cql.model.DataType,
    ): Expression {
        val loc = intervalExpr.locator
        val typeSpec =
            org.hl7.cql.ast.NamedTypeSpecifier(
                name =
                    org.hl7.cql.ast.QualifiedIdentifier(
                        listOf(targetPointType.toString().removePrefix("System."))
                    )
            )
        // Produce IntervalExpression with Property-extracted bounds cast to the target type,
        // and dynamic closed flags from Property access on the source interval.
        val lowProp =
            org.hl7.cql.ast.PropertyAccessExpression(
                target = intervalExpr,
                property = org.hl7.cql.ast.Identifier("low"),
                locator = loc,
            )
        val highProp =
            org.hl7.cql.ast.PropertyAccessExpression(
                target = intervalExpr,
                property = org.hl7.cql.ast.Identifier("high"),
                locator = loc,
            )
        val lowAs = ImplicitCastExpression(operand = lowProp, type = typeSpec, locator = loc)
        val highAs = ImplicitCastExpression(operand = highProp, type = typeSpec, locator = loc)
        val lowClosedProp =
            org.hl7.cql.ast.PropertyAccessExpression(
                target = intervalExpr,
                property = org.hl7.cql.ast.Identifier("lowClosed"),
                locator = loc,
            )
        val highClosedProp =
            org.hl7.cql.ast.PropertyAccessExpression(
                target = intervalExpr,
                property = org.hl7.cql.ast.Identifier("highClosed"),
                locator = loc,
            )
        return IntervalExpression(
            low = lowAs,
            high = highAs,
            lowClosedExpression = lowClosedProp,
            highClosedExpression = highClosedProp,
            locator = loc,
        )
    }

    private fun isIntervalLiteral(expr: Expression): Boolean =
        expr is LiteralExpression && expr.literal is org.hl7.cql.ast.IntervalLiteral

    override fun onQuery(expr: QueryExpression, children: QueryChildren<Expression>) = expr

    override fun onRetrieve(expr: RetrieveExpression) = expr

    override fun onUnsupported(expr: UnsupportedExpression) = expr
}
