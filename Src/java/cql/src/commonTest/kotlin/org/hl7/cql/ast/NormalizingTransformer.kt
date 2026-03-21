package org.hl7.cql.ast

/**
 * Test-only AST normalizer that replaces every [Locator] with [Locator.UNKNOWN] so structural
 * comparisons can ignore source metadata.
 *
 * Expression traversal is handled by [RewritingFold] (compile-time exhaustive dispatch via
 * [ExpressionFold]). Non-expression children that carry locators (literals, type specifiers, query
 * clauses, interval phrases, terminology references) are normalized in the relevant `on*` overrides
 * using private helpers.
 *
 * Library-level normalization manually traverses definitions and statements, calling [fold] for
 * their expression parts.
 */
object NormalizingTransformer : RewritingFold() {

    // -----------------------------------------------------------------------
    // Expression-level: fold() clears the locator on every Expression node.
    // -----------------------------------------------------------------------

    override fun fold(expr: Expression): Expression = super.fold(expr).clearLocator()

    // -----------------------------------------------------------------------
    // on* overrides for nodes with non-expression children that carry locators.
    // The expression children are already normalized by the catamorphism (they
    // go through fold() → clearLocator()). We only handle non-expression fields.
    // -----------------------------------------------------------------------

    override fun onLiteral(
        expr: LiteralExpression,
        children: LiteralChildren<Expression>,
    ): Expression {
        // Rebuild the literal with normalized children and cleared locators on the Literal itself
        // and any nested non-expression nodes (TupleElementValue, QuantityLiteral, etc.).
        val normalized = normalizeLiteral(expr.literal, children)
        return expr.copy(literal = normalized)
    }

    override fun onIs(expr: IsExpression, operand: Expression): Expression =
        expr.copy(operand = operand, type = normalizeTypeSpecifier(expr.type))

    override fun onAs(expr: AsExpression, operand: Expression): Expression =
        expr.copy(operand = operand, type = normalizeTypeSpecifier(expr.type))

    override fun onCast(expr: CastExpression, operand: Expression): Expression =
        expr.copy(operand = operand, type = normalizeTypeSpecifier(expr.type))

    override fun onConversion(expr: ConversionExpression, operand: Expression): Expression =
        expr.copy(
            operand = operand,
            destinationType = expr.destinationType?.let { normalizeTypeSpecifier(it) },
        )

    override fun onImplicitCast(expr: ImplicitCastExpression, operand: Expression): Expression =
        expr.copy(operand = operand, type = normalizeTypeSpecifier(expr.type))

    override fun onTypeExtent(expr: TypeExtentExpression): Expression =
        expr.copy(type = normalizeNamedTypeSpecifier(expr.type))

    override fun onRetrieve(expr: RetrieveExpression): Expression =
        expr.copy(
            typeSpecifier = normalizeNamedTypeSpecifier(expr.typeSpecifier),
            terminology = expr.terminology?.let { normalizeTerminologyRestriction(it) },
        )

    override fun onCase(
        expr: CaseExpression,
        comparand: Expression?,
        cases: List<CaseChildren<Expression>>,
        elseResult: Expression,
    ): Expression =
        expr.copy(
            comparand = comparand,
            cases =
                cases.mapIndexed { i, c ->
                    expr.cases[i].copy(
                        condition = c.condition,
                        result = c.result,
                        locator = Locator.UNKNOWN,
                    )
                },
            elseResult = elseResult,
        )

    override fun onIntervalRelation(
        expr: IntervalRelationExpression,
        left: Expression,
        right: Expression,
    ): Expression =
        expr.copy(left = left, phrase = normalizeIntervalOperatorPhrase(expr.phrase), right = right)

    override fun onQuery(expr: QueryExpression, children: QueryChildren<Expression>): Expression {
        // Rebuild the full query, normalizing all non-expression sub-nodes.
        val newSources =
            expr.sources.mapIndexed { i, src ->
                val newSource =
                    when (val qs = src.source) {
                        is ExpressionQuerySource -> {
                            val childExpr = children.sourceExpressions[i]
                            if (childExpr != null)
                                qs.copy(expression = childExpr, locator = Locator.UNKNOWN)
                            else qs.copy(locator = Locator.UNKNOWN)
                        }
                        is RetrieveExpression ->
                            qs.copy(
                                typeSpecifier = normalizeNamedTypeSpecifier(qs.typeSpecifier),
                                terminology =
                                    qs.terminology?.let { normalizeTerminologyRestriction(it) },
                                locator = Locator.UNKNOWN,
                            )
                    }
                src.copy(source = newSource, locator = Locator.UNKNOWN)
            }

        val newLets =
            expr.lets.mapIndexed { i, let ->
                let.copy(expression = children.letExpressions[i], locator = Locator.UNKNOWN)
            }

        val newInclusions =
            expr.inclusions.mapIndexed { i, inc ->
                when (inc) {
                    is WithClause -> {
                        val newSrc =
                            normalizeInclusionSource(
                                inc.source,
                                children.inclusionSourceExpressions[i],
                            )
                        inc.copy(
                            source = newSrc,
                            condition = children.inclusionConditions[i],
                            locator = Locator.UNKNOWN,
                        )
                    }
                    is WithoutClause -> {
                        val newSrc =
                            normalizeInclusionSource(
                                inc.source,
                                children.inclusionSourceExpressions[i],
                            )
                        inc.copy(
                            source = newSrc,
                            condition = children.inclusionConditions[i],
                            locator = Locator.UNKNOWN,
                        )
                    }
                }
            }

        return expr.copy(
            sources = newSources,
            lets = newLets,
            inclusions = newInclusions,
            where = children.where,
            aggregate =
                expr.aggregate?.let { agg ->
                    agg.copy(
                        starting = children.aggregateStarting ?: agg.starting,
                        expression = children.aggregateExpression ?: agg.expression,
                        locator = Locator.UNKNOWN,
                    )
                },
            result =
                expr.result?.let { ret ->
                    ret.copy(
                        expression = children.returnExpression ?: ret.expression,
                        locator = Locator.UNKNOWN,
                    )
                },
            sort =
                expr.sort?.let { sort ->
                    sort.copy(
                        items =
                            sort.items.mapIndexed { i, item ->
                                item.copy(
                                    expression =
                                        children.sortExpressions.getOrElse(i) { item.expression },
                                    locator = Locator.UNKNOWN,
                                )
                            },
                        locator = Locator.UNKNOWN,
                    )
                },
        )
    }

    private fun normalizeInclusionSource(
        source: AliasedQuerySource,
        childExpr: Expression?,
    ): AliasedQuerySource {
        val qs = source.source
        val newQs =
            when (qs) {
                is ExpressionQuerySource ->
                    if (childExpr != null)
                        qs.copy(expression = childExpr, locator = Locator.UNKNOWN)
                    else qs.copy(locator = Locator.UNKNOWN)
                is RetrieveExpression ->
                    qs.copy(
                        typeSpecifier = normalizeNamedTypeSpecifier(qs.typeSpecifier),
                        terminology = qs.terminology?.let { normalizeTerminologyRestriction(it) },
                        locator = Locator.UNKNOWN,
                    )
            }
        return source.copy(source = newQs, locator = Locator.UNKNOWN)
    }

    // -----------------------------------------------------------------------
    // Library-level normalization (definitions and statements that aren't
    // covered by ExpressionFold).
    // -----------------------------------------------------------------------

    fun normalizeLibrary(library: Library): Library =
        library.copy(
            definitions = library.definitions.map { normalizeDefinition(it) },
            statements = library.statements.map { normalizeStatement(it) },
            locator = Locator.UNKNOWN,
        )

    @Suppress("CyclomaticComplexMethod")
    private fun normalizeDefinition(definition: Definition): Definition =
        when (definition) {
            is UsingDefinition -> definition.copy(locator = Locator.UNKNOWN)
            is IncludeDefinition -> definition.copy(locator = Locator.UNKNOWN)
            is CodeSystemDefinition -> definition.copy(locator = Locator.UNKNOWN)
            is ValueSetDefinition ->
                definition.copy(
                    codesystems = definition.codesystems.map { normalizeTerminologyReference(it) },
                    locator = Locator.UNKNOWN,
                )
            is CodeDefinition ->
                definition.copy(
                    system = normalizeTerminologyReference(definition.system),
                    locator = Locator.UNKNOWN,
                )
            is ConceptDefinition ->
                definition.copy(
                    codes = definition.codes.map { normalizeTerminologyReference(it) },
                    locator = Locator.UNKNOWN,
                )
            is ParameterDefinition ->
                definition.copy(
                    type = definition.type?.let { normalizeTypeSpecifier(it) },
                    default = definition.default?.let { fold(it) },
                    locator = Locator.UNKNOWN,
                )
            is UnsupportedDefinition -> definition.copy(locator = Locator.UNKNOWN)
        }

    private fun normalizeStatement(statement: Statement): Statement =
        when (statement) {
            is ContextDefinition -> statement.copy(locator = Locator.UNKNOWN)
            is ExpressionDefinition ->
                statement.copy(expression = fold(statement.expression), locator = Locator.UNKNOWN)
            is FunctionDefinition ->
                statement.copy(
                    operands =
                        statement.operands.map {
                            it.copy(
                                type = normalizeTypeSpecifier(it.type),
                                locator = Locator.UNKNOWN,
                            )
                        },
                    returnType = statement.returnType?.let { normalizeTypeSpecifier(it) },
                    body =
                        when (val body = statement.body) {
                            is ExpressionFunctionBody ->
                                body.copy(
                                    expression = fold(body.expression),
                                    locator = Locator.UNKNOWN,
                                )
                            is ExternalFunctionBody -> body.copy(locator = Locator.UNKNOWN)
                        },
                    locator = Locator.UNKNOWN,
                )
            is UnsupportedStatement -> statement.copy(locator = Locator.UNKNOWN)
        }

    // -----------------------------------------------------------------------
    // Non-expression helpers: type specifiers, literals, terminology, phrases
    // -----------------------------------------------------------------------

    @Suppress("CyclomaticComplexMethod")
    private fun normalizeLiteral(literal: Literal, children: LiteralChildren<Expression>): Literal =
        when (literal) {
            is StringLiteral -> literal.copy(locator = Locator.UNKNOWN)
            is LongLiteral -> literal.copy(locator = Locator.UNKNOWN)
            is IntLiteral -> literal.copy(locator = Locator.UNKNOWN)
            is DecimalLiteral -> literal.copy(locator = Locator.UNKNOWN)
            is BooleanLiteral -> literal.copy(locator = Locator.UNKNOWN)
            is NullLiteral -> literal.copy(locator = Locator.UNKNOWN)
            is QuantityLiteral -> literal.copy(locator = Locator.UNKNOWN)
            is DateTimeLiteral -> literal.copy(locator = Locator.UNKNOWN)
            is TimeLiteral -> literal.copy(locator = Locator.UNKNOWN)
            is TupleLiteral ->
                literal.copy(
                    elements =
                        literal.elements.mapIndexed { i, el ->
                            el.copy(
                                expression = children.tupleElements[i],
                                locator = Locator.UNKNOWN,
                            )
                        },
                    locator = Locator.UNKNOWN,
                )
            is InstanceLiteral ->
                literal.copy(
                    type = literal.type?.let { normalizeNamedTypeSpecifier(it) },
                    elements =
                        literal.elements.mapIndexed { i, el ->
                            el.copy(
                                expression = children.tupleElements[i],
                                locator = Locator.UNKNOWN,
                            )
                        },
                    locator = Locator.UNKNOWN,
                )
            is IntervalLiteral ->
                literal.copy(
                    lower = children.intervalLow ?: literal.lower,
                    upper = children.intervalHigh ?: literal.upper,
                    locator = Locator.UNKNOWN,
                )
            is ListLiteral ->
                literal.copy(
                    elements = children.elements,
                    elementType = literal.elementType?.let { normalizeTypeSpecifier(it) },
                    locator = Locator.UNKNOWN,
                )
            is RatioLiteral ->
                literal.copy(
                    numerator = literal.numerator.copy(locator = Locator.UNKNOWN),
                    denominator = literal.denominator.copy(locator = Locator.UNKNOWN),
                    locator = Locator.UNKNOWN,
                )
            is CodeLiteral ->
                literal.copy(
                    system = normalizeTerminologyReference(literal.system),
                    locator = Locator.UNKNOWN,
                )
            is ConceptLiteral ->
                literal.copy(
                    codes =
                        literal.codes.map {
                            it.copy(
                                system = normalizeTerminologyReference(it.system),
                                locator = Locator.UNKNOWN,
                            )
                        },
                    locator = Locator.UNKNOWN,
                )
        }

    fun normalizeTypeSpecifier(typeSpecifier: TypeSpecifier): TypeSpecifier =
        when (typeSpecifier) {
            is NamedTypeSpecifier -> normalizeNamedTypeSpecifier(typeSpecifier)
            is ListTypeSpecifier ->
                typeSpecifier.copy(
                    elementType = normalizeTypeSpecifier(typeSpecifier.elementType),
                    locator = Locator.UNKNOWN,
                )
            is IntervalTypeSpecifier ->
                typeSpecifier.copy(
                    pointType = normalizeTypeSpecifier(typeSpecifier.pointType),
                    locator = Locator.UNKNOWN,
                )
            is TupleTypeSpecifier ->
                typeSpecifier.copy(
                    elements =
                        typeSpecifier.elements.map {
                            it.copy(
                                type = normalizeTypeSpecifier(it.type),
                                locator = Locator.UNKNOWN,
                            )
                        },
                    locator = Locator.UNKNOWN,
                )
            is ChoiceTypeSpecifier ->
                typeSpecifier.copy(
                    choices = typeSpecifier.choices.map { normalizeTypeSpecifier(it) },
                    locator = Locator.UNKNOWN,
                )
        }

    private fun normalizeNamedTypeSpecifier(ts: NamedTypeSpecifier): NamedTypeSpecifier =
        ts.copy(locator = Locator.UNKNOWN)

    @Suppress("CyclomaticComplexMethod")
    private fun normalizeIntervalOperatorPhrase(
        phrase: IntervalOperatorPhrase
    ): IntervalOperatorPhrase =
        when (phrase) {
            is ConcurrentIntervalPhrase -> phrase.copy(locator = Locator.UNKNOWN)
            is IncludesIntervalPhrase -> phrase.copy(locator = Locator.UNKNOWN)
            is IncludedInIntervalPhrase -> phrase.copy(locator = Locator.UNKNOWN)
            is BeforeOrAfterIntervalPhrase ->
                phrase.copy(
                    offset =
                        phrase.offset?.let {
                            it.copy(
                                quantity = it.quantity.copy(locator = Locator.UNKNOWN),
                                locator = Locator.UNKNOWN,
                            )
                        },
                    relationship = phrase.relationship.copy(locator = Locator.UNKNOWN),
                    locator = Locator.UNKNOWN,
                )
            is WithinIntervalPhrase ->
                phrase.copy(
                    quantity = phrase.quantity.copy(locator = Locator.UNKNOWN),
                    locator = Locator.UNKNOWN,
                )
            is MeetsIntervalPhrase -> phrase.copy(locator = Locator.UNKNOWN)
            is OverlapsIntervalPhrase -> phrase.copy(locator = Locator.UNKNOWN)
            is StartsIntervalPhrase -> phrase.copy(locator = Locator.UNKNOWN)
            is EndsIntervalPhrase -> phrase.copy(locator = Locator.UNKNOWN)
            is UnsupportedIntervalPhrase -> phrase.copy(locator = Locator.UNKNOWN)
        }

    private fun normalizeTerminologyReference(ref: TerminologyReference): TerminologyReference =
        ref.copy(locator = Locator.UNKNOWN)

    private fun normalizeTerminologyRestriction(
        restriction: TerminologyRestriction
    ): TerminologyRestriction =
        restriction.copy(terminology = fold(restriction.terminology), locator = Locator.UNKNOWN)
}

// ---------------------------------------------------------------------------
// clearLocator: sealed-exhaustive locator clearing for Expression
// ---------------------------------------------------------------------------

@Suppress("CyclomaticComplexMethod")
private fun Expression.clearLocator(): Expression =
    when (this) {
        is IdentifierExpression -> copy(locator = Locator.UNKNOWN)
        is ExternalConstantExpression -> copy(locator = Locator.UNKNOWN)
        is ExistsExpression -> copy(locator = Locator.UNKNOWN)
        is MembershipExpression -> copy(locator = Locator.UNKNOWN)
        is BetweenExpression -> copy(locator = Locator.UNKNOWN)
        is DurationBetweenExpression -> copy(locator = Locator.UNKNOWN)
        is DifferenceBetweenExpression -> copy(locator = Locator.UNKNOWN)
        is DurationOfExpression -> copy(locator = Locator.UNKNOWN)
        is DifferenceOfExpression -> copy(locator = Locator.UNKNOWN)
        is WidthExpression -> copy(locator = Locator.UNKNOWN)
        is ElementExtractorExpression -> copy(locator = Locator.UNKNOWN)
        is TypeExtentExpression -> copy(locator = Locator.UNKNOWN)
        is DateTimeComponentExpression -> copy(locator = Locator.UNKNOWN)
        is ConversionExpression -> copy(locator = Locator.UNKNOWN)
        is TimeBoundaryExpression -> copy(locator = Locator.UNKNOWN)
        is FunctionCallExpression -> copy(locator = Locator.UNKNOWN)
        is PropertyAccessExpression -> copy(locator = Locator.UNKNOWN)
        is IndexExpression -> copy(locator = Locator.UNKNOWN)
        is IfExpression -> copy(locator = Locator.UNKNOWN)
        is CaseExpression -> copy(locator = Locator.UNKNOWN)
        is QueryExpression -> copy(locator = Locator.UNKNOWN)
        is RetrieveExpression -> copy(locator = Locator.UNKNOWN)
        is ListTransformExpression -> copy(locator = Locator.UNKNOWN)
        is ExpandCollapseExpression -> copy(locator = Locator.UNKNOWN)
        is IntervalRelationExpression -> copy(locator = Locator.UNKNOWN)
        is IsExpression -> copy(locator = Locator.UNKNOWN)
        is AsExpression -> copy(locator = Locator.UNKNOWN)
        is CastExpression -> copy(locator = Locator.UNKNOWN)
        is ImplicitCastExpression -> copy(locator = Locator.UNKNOWN)
        is IntervalExpression -> copy(locator = Locator.UNKNOWN)
        is LiteralExpression -> copy(locator = Locator.UNKNOWN)
        is OperatorBinaryExpression -> copy(locator = Locator.UNKNOWN)
        is OperatorUnaryExpression -> copy(locator = Locator.UNKNOWN)
        is BooleanTestExpression -> copy(locator = Locator.UNKNOWN)
        is UnsupportedExpression -> copy(locator = Locator.UNKNOWN)
    }
