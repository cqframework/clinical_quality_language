package org.hl7.cql.ast

/**
 * Identity-transform [ExpressionFold]: an AST-to-AST rewrite where most nodes pass through
 * unchanged. Each `on*` method returns the original node when no children changed (checked via
 * `===` reference equality, so unchanged subtrees produce zero allocations).
 *
 * To rewrite specific node types, subclass and override the corresponding `on*` methods. Return the
 * rewritten node, or call `super.on*()` to get the default identity behavior.
 *
 * This fold only traverses expression trees. To rewrite all expressions in a [Library]
 * (definitions, statements), use the companion [rewriteLibrary] top-level function:
 * ```
 * val myFold = object : RewritingFold() {
 *     override fun onIdentifier(expr: IdentifierExpression): Expression {
 *         // rewrite identifiers, pass everything else through
 *     }
 * }
 * val rewritten = rewriteLibrary(myFold, library)
 * ```
 *
 * Concrete subclass in the pipeline:
 * - `AgeInDesugarer` — structural desugar of `AgeIn` phrases before analysis
 *
 * @see ExpressionFold for the full fold interface and extension guidance
 * @see rewriteLibrary for library-level traversal
 */
@Suppress("TooManyFunctions")
abstract class RewritingFold : ExpressionFold<Expression> {

    // --- Leaves: no expression children, return as-is ---

    override fun onLiteral(
        expr: LiteralExpression,
        children: LiteralChildren<Expression>,
    ): Expression = rewriteLiteral(expr, children)

    override fun onIdentifier(expr: IdentifierExpression): Expression = expr

    override fun onExternalConstant(expr: ExternalConstantExpression): Expression = expr

    override fun onTypeExtent(expr: TypeExtentExpression): Expression = expr

    override fun onRetrieve(expr: RetrieveExpression): Expression = expr

    override fun onUnsupported(expr: UnsupportedExpression): Expression = expr

    // --- Unary ---

    override fun onUnaryOperator(expr: OperatorUnaryExpression, operand: Expression): Expression =
        if (operand === expr.operand) expr else expr.copy(operand = operand)

    override fun onBooleanTest(expr: BooleanTestExpression, operand: Expression): Expression =
        if (operand === expr.operand) expr else expr.copy(operand = operand)

    override fun onExists(expr: ExistsExpression, operand: Expression): Expression =
        if (operand === expr.operand) expr else expr.copy(operand = operand)

    override fun onIs(expr: IsExpression, operand: Expression): Expression =
        if (operand === expr.operand) expr else expr.copy(operand = operand)

    override fun onAs(expr: AsExpression, operand: Expression): Expression =
        if (operand === expr.operand) expr else expr.copy(operand = operand)

    override fun onImplicitCast(expr: ImplicitCastExpression, operand: Expression): Expression =
        if (operand === expr.operand) expr else expr.copy(operand = operand)

    override fun onCast(expr: CastExpression, operand: Expression): Expression =
        if (operand === expr.operand) expr else expr.copy(operand = operand)

    override fun onConversion(expr: ConversionExpression, operand: Expression): Expression =
        if (operand === expr.operand) expr else expr.copy(operand = operand)

    override fun onListTransform(expr: ListTransformExpression, operand: Expression): Expression =
        if (operand === expr.operand) expr else expr.copy(operand = operand)

    override fun onDateTimeComponent(
        expr: DateTimeComponentExpression,
        operand: Expression,
    ): Expression = if (operand === expr.operand) expr else expr.copy(operand = operand)

    override fun onDurationOf(expr: DurationOfExpression, operand: Expression): Expression =
        if (operand === expr.operand) expr else expr.copy(operand = operand)

    override fun onDifferenceOf(expr: DifferenceOfExpression, operand: Expression): Expression =
        if (operand === expr.operand) expr else expr.copy(operand = operand)

    override fun onTimeBoundary(expr: TimeBoundaryExpression, operand: Expression): Expression =
        if (operand === expr.operand) expr else expr.copy(operand = operand)

    override fun onWidth(expr: WidthExpression, operand: Expression): Expression =
        if (operand === expr.operand) expr else expr.copy(operand = operand)

    override fun onElementExtractor(
        expr: ElementExtractorExpression,
        operand: Expression,
    ): Expression = if (operand === expr.operand) expr else expr.copy(operand = operand)

    override fun onPropertyAccess(expr: PropertyAccessExpression, target: Expression): Expression =
        if (target === expr.target) expr else expr.copy(target = target)

    // --- Binary ---

    override fun onBinaryOperator(
        expr: OperatorBinaryExpression,
        left: Expression,
        right: Expression,
    ): Expression =
        if (left === expr.left && right === expr.right) expr
        else expr.copy(left = left, right = right)

    override fun onMembership(
        expr: MembershipExpression,
        left: Expression,
        right: Expression,
    ): Expression =
        if (left === expr.left && right === expr.right) expr
        else expr.copy(left = left, right = right)

    override fun onIndex(expr: IndexExpression, target: Expression, index: Expression): Expression =
        if (target === expr.target && index === expr.index) expr
        else expr.copy(target = target, index = index)

    override fun onDurationBetween(
        expr: DurationBetweenExpression,
        lower: Expression,
        upper: Expression,
    ): Expression =
        if (lower === expr.lower && upper === expr.upper) expr
        else expr.copy(lower = lower, upper = upper)

    override fun onDifferenceBetween(
        expr: DifferenceBetweenExpression,
        lower: Expression,
        upper: Expression,
    ): Expression =
        if (lower === expr.lower && upper === expr.upper) expr
        else expr.copy(lower = lower, upper = upper)

    override fun onIntervalRelation(
        expr: IntervalRelationExpression,
        left: Expression,
        right: Expression,
    ): Expression =
        if (left === expr.left && right === expr.right) expr
        else expr.copy(left = left, right = right)

    // --- Ternary ---

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

    override fun onBetween(
        expr: BetweenExpression,
        input: Expression,
        lower: Expression,
        upper: Expression,
    ): Expression =
        if (input === expr.input && lower === expr.lower && upper === expr.upper) expr
        else expr.copy(input = input, lower = lower, upper = upper)

    // --- Four children ---

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

    // --- Variable children ---

    override fun onCase(
        expr: CaseExpression,
        comparand: Expression?,
        cases: List<CaseChildren<Expression>>,
        elseResult: Expression,
    ): Expression = rewriteCase(expr, comparand, cases, elseResult)

    override fun onFunctionCall(
        expr: FunctionCallExpression,
        target: Expression?,
        arguments: List<Expression>,
    ): Expression {
        val targetSame = target === expr.target
        val argsSame =
            arguments.size == expr.arguments.size &&
                arguments.indices.all { arguments[it] === expr.arguments[it] }
        if (targetSame && argsSame) return expr
        return expr.copy(target = target, arguments = arguments)
    }

    override fun onExpandCollapse(
        expr: ExpandCollapseExpression,
        operand: Expression,
        per: Expression?,
    ): Expression =
        if (operand === expr.operand && per === expr.perExpression) expr
        else expr.copy(operand = operand, perExpression = per)

    override fun onQuery(expr: QueryExpression, children: QueryChildren<Expression>): Expression =
        rewriteQuery(expr, children)
}

// ---------------------------------------------------------------------------
// Identity-check reconstruction helpers. Internal so Lowering (which implements
// ExpressionFold<Expression> directly, not via RewritingFold) can delegate to them
// for nodes it does not actively rewrite.
// ---------------------------------------------------------------------------

fun rewriteCase(
    expr: CaseExpression,
    comparand: Expression?,
    cases: List<CaseChildren<Expression>>,
    elseResult: Expression,
): Expression {
    val comparandSame = comparand === expr.comparand
    val elseResultSame = elseResult === expr.elseResult
    val casesSame =
        cases.size == expr.cases.size &&
            cases.indices.all { i ->
                cases[i].condition === expr.cases[i].condition &&
                    cases[i].result === expr.cases[i].result
            }
    if (comparandSame && casesSame && elseResultSame) return expr
    return expr.copy(
        comparand = comparand,
        cases =
            cases.mapIndexed { i, c ->
                expr.cases[i].copy(condition = c.condition, result = c.result)
            },
        elseResult = elseResult,
    )
}

fun rewriteLiteral(expr: LiteralExpression, children: LiteralChildren<Expression>): Expression {
    val literal = expr.literal
    val newLiteral: Literal =
        when (literal) {
            is ListLiteral -> {
                val same =
                    children.elements.size == literal.elements.size &&
                        children.elements.indices.all {
                            children.elements[it] === literal.elements[it]
                        }
                if (same) return expr
                literal.copy(elements = children.elements)
            }
            is IntervalLiteral -> {
                val low = children.intervalLow
                val high = children.intervalHigh
                if (low === literal.lower && high === literal.upper) return expr
                literal.copy(lower = low ?: literal.lower, upper = high ?: literal.upper)
            }
            is TupleLiteral -> {
                val same =
                    children.tupleElements.size == literal.elements.size &&
                        children.tupleElements.indices.all {
                            children.tupleElements[it] === literal.elements[it].expression
                        }
                if (same) return expr
                literal.copy(
                    elements =
                        literal.elements.mapIndexed { i, el ->
                            el.copy(expression = children.tupleElements[i])
                        }
                )
            }
            is InstanceLiteral -> {
                val same =
                    children.tupleElements.size == literal.elements.size &&
                        children.tupleElements.indices.all {
                            children.tupleElements[it] === literal.elements[it].expression
                        }
                if (same) return expr
                literal.copy(
                    elements =
                        literal.elements.mapIndexed { i, el ->
                            el.copy(expression = children.tupleElements[i])
                        }
                )
            }
            // ConceptLiteral codes are CodeLiteral, not expressions — no expression children
            is ConceptLiteral -> return expr
            // Simple literals (StringLiteral, IntLiteral, etc.) have no expression children
            else -> return expr
        }
    return expr.copy(literal = newLiteral)
}

@Suppress("CyclomaticComplexMethod")
fun rewriteQuery(expr: QueryExpression, c: QueryChildren<Expression>): Expression {
    // Check each field for reference equality against the original.
    val sourcesSame =
        c.sourceExpressions.size == expr.sources.size &&
            c.sourceExpressions.indices.all { i ->
                val qs = expr.sources[i].source
                if (qs is ExpressionQuerySource) c.sourceExpressions[i] === qs.expression
                else c.sourceExpressions[i] == null
            }

    val letsSame =
        c.letExpressions.size == expr.lets.size &&
            c.letExpressions.indices.all { c.letExpressions[it] === expr.lets[it].expression }

    val inclusionCondsSame =
        c.inclusionConditions.size == expr.inclusions.size &&
            c.inclusionConditions.indices.all { i ->
                when (val inc = expr.inclusions[i]) {
                    is WithClause -> c.inclusionConditions[i] === inc.condition
                    is WithoutClause -> c.inclusionConditions[i] === inc.condition
                }
            }

    val inclusionSourcesSame =
        c.inclusionSourceExpressions.size == expr.inclusions.size &&
            c.inclusionSourceExpressions.indices.all { i ->
                val qs =
                    when (val inc = expr.inclusions[i]) {
                        is WithClause -> inc.source.source
                        is WithoutClause -> inc.source.source
                    }
                if (qs is ExpressionQuerySource) c.inclusionSourceExpressions[i] === qs.expression
                else c.inclusionSourceExpressions[i] == null
            }

    val whereSame = c.where === expr.where
    val returnSame =
        if (expr.result != null) c.returnExpression === expr.result.expression
        else c.returnExpression == null

    val aggStartSame =
        if (expr.aggregate?.starting != null) c.aggregateStarting === expr.aggregate.starting
        else c.aggregateStarting == null
    val aggExprSame =
        if (expr.aggregate != null) c.aggregateExpression === expr.aggregate.expression
        else c.aggregateExpression == null

    val sortItems = expr.sort?.items ?: emptyList()
    val sortSame =
        c.sortExpressions.size == sortItems.size &&
            c.sortExpressions.indices.all { c.sortExpressions[it] === sortItems[it].expression }

    if (
        sourcesSame &&
            letsSame &&
            inclusionCondsSame &&
            inclusionSourcesSame &&
            whereSame &&
            returnSame &&
            aggStartSame &&
            aggExprSame &&
            sortSame
    )
        return expr

    val newSources =
        expr.sources.mapIndexed { i, src ->
            val qs = src.source
            if (qs is ExpressionQuerySource && c.sourceExpressions[i] !== qs.expression) {
                src.copy(source = qs.copy(expression = c.sourceExpressions[i] as Expression))
            } else {
                src
            }
        }

    val newLets =
        expr.lets.mapIndexed { i, let ->
            if (c.letExpressions[i] !== let.expression) let.copy(expression = c.letExpressions[i])
            else let
        }

    val newInclusions =
        expr.inclusions.mapIndexed { i, inc ->
            val newCond = c.inclusionConditions[i]
            val newSrcExpr = c.inclusionSourceExpressions[i]
            when (inc) {
                is WithClause -> {
                    val condChanged = newCond !== inc.condition
                    val qs = inc.source.source
                    val srcChanged = qs is ExpressionQuerySource && newSrcExpr !== qs.expression
                    if (!condChanged && !srcChanged) inc
                    else {
                        val newSource =
                            if (srcChanged)
                                inc.source.copy(
                                    source = qs.copy(expression = newSrcExpr as Expression)
                                )
                            else inc.source
                        inc.copy(source = newSource, condition = newCond)
                    }
                }
                is WithoutClause -> {
                    val condChanged = newCond !== inc.condition
                    val qs = inc.source.source
                    val srcChanged = qs is ExpressionQuerySource && newSrcExpr !== qs.expression
                    if (!condChanged && !srcChanged) inc
                    else {
                        val newSource =
                            if (srcChanged)
                                inc.source.copy(
                                    source = qs.copy(expression = newSrcExpr as Expression)
                                )
                            else inc.source
                        inc.copy(source = newSource, condition = newCond)
                    }
                }
            }
        }

    val newWhere = c.where
    val newResult =
        if (expr.result != null && c.returnExpression !== expr.result.expression)
            expr.result.copy(expression = c.returnExpression as Expression)
        else expr.result

    val newAggregate =
        if (expr.aggregate != null) {
            val startChanged =
                expr.aggregate.starting != null && c.aggregateStarting !== expr.aggregate.starting
            val exprChanged = c.aggregateExpression !== expr.aggregate.expression
            if (startChanged || exprChanged) {
                expr.aggregate.copy(
                    starting = c.aggregateStarting ?: expr.aggregate.starting,
                    expression = c.aggregateExpression ?: expr.aggregate.expression,
                )
            } else expr.aggregate
        } else null

    val newSort =
        if (expr.sort != null && !sortSame) {
            expr.sort.copy(
                items =
                    sortItems.mapIndexed { i, item ->
                        if (c.sortExpressions[i] !== item.expression)
                            item.copy(expression = c.sortExpressions[i])
                        else item
                    }
            )
        } else expr.sort

    return expr.copy(
        sources = newSources,
        lets = newLets,
        inclusions = newInclusions,
        where = newWhere,
        aggregate = newAggregate,
        result = newResult,
        sort = newSort,
    )
}

// ---------------------------------------------------------------------------
// Library-level traversal
// ---------------------------------------------------------------------------

/**
 * Walks a [Library]'s definitions and statements, folding any expression children using [fold].
 * Returns the original library (by reference) if nothing changed.
 *
 * This is the companion to [ExpressionFold] for library-level traversal. [ExpressionFold.fold]
 * handles expressions; this function handles the outer Library → Definition/Statement structure
 * that contains those expressions.
 *
 * Definition/statement types that contain foldable expressions:
 * - [ExpressionDefinition] — folds the expression body
 * - [FunctionDefinition] with [ExpressionFunctionBody] — folds the body expression
 * - [ParameterDefinition] with non-null default — folds the default expression
 * - All other definitions/statements — returned as-is (no expression children)
 */
fun rewriteLibrary(fold: ExpressionFold<Expression>, library: Library): Library {
    val newStatements = library.statements.map { rewriteStatement(fold, it) }
    val newDefinitions = library.definitions.map { rewriteDefinition(fold, it) }

    val statementsSame = newStatements.indices.all { newStatements[it] === library.statements[it] }
    val definitionsSame =
        newDefinitions.indices.all { newDefinitions[it] === library.definitions[it] }

    return if (statementsSame && definitionsSame) library
    else library.copy(statements = newStatements, definitions = newDefinitions)
}

private fun rewriteStatement(fold: ExpressionFold<Expression>, statement: Statement): Statement =
    when (statement) {
        is ExpressionDefinition -> {
            val rewritten = fold.fold(statement.expression)
            if (rewritten === statement.expression) statement
            else statement.copy(expression = rewritten)
        }
        is FunctionDefinition -> {
            val body = statement.body
            if (body is ExpressionFunctionBody) {
                val rewritten = fold.fold(body.expression)
                if (rewritten === body.expression) statement
                else statement.copy(body = body.copy(expression = rewritten))
            } else {
                statement
            }
        }
        else -> statement
    }

private fun rewriteDefinition(
    fold: ExpressionFold<Expression>,
    definition: Definition,
): Definition =
    when (definition) {
        is ParameterDefinition -> {
            val default = definition.default
            if (default != null) {
                val rewritten = fold.fold(default)
                if (rewritten === default) definition else definition.copy(default = rewritten)
            } else {
                definition
            }
        }
        else -> definition
    }
