package org.hl7.cql.ast

/**
 * Test-only transformer that traverses the AST and clears every locator so structural comparisons
 * can ignore source metadata. It leverages the production [Transformer] to visit all children and
 * then rewrites each node with [Locator.UNKNOWN].
 */
object NormalizingTransformer : Transformer() {

    override fun visitLibrary(library: Library): Library =
        super.visitLibrary(library).copy(locator = Locator.UNKNOWN)

    override fun visitUsingDefinition(definition: UsingDefinition): Definition =
        (super.visitUsingDefinition(definition) as UsingDefinition).copy(locator = Locator.UNKNOWN)

    override fun visitIncludeDefinition(definition: IncludeDefinition): Definition =
        (super.visitIncludeDefinition(definition) as IncludeDefinition).copy(
            locator = Locator.UNKNOWN
        )

    override fun visitCodeSystemDefinition(definition: CodeSystemDefinition): Definition =
        (super.visitCodeSystemDefinition(definition) as CodeSystemDefinition).copy(
            locator = Locator.UNKNOWN
        )

    override fun visitValueSetDefinition(definition: ValueSetDefinition): Definition =
        (super.visitValueSetDefinition(definition) as ValueSetDefinition).copy(
            locator = Locator.UNKNOWN
        )

    override fun visitCodeDefinition(definition: CodeDefinition): Definition =
        (super.visitCodeDefinition(definition) as CodeDefinition).copy(locator = Locator.UNKNOWN)

    override fun visitConceptDefinition(definition: ConceptDefinition): Definition =
        (super.visitConceptDefinition(definition) as ConceptDefinition).copy(
            locator = Locator.UNKNOWN
        )

    override fun visitParameterDefinition(definition: ParameterDefinition): Definition =
        (super.visitParameterDefinition(definition) as ParameterDefinition).copy(
            locator = Locator.UNKNOWN
        )

    override fun visitUnsupportedDefinition(definition: UnsupportedDefinition): Definition =
        (super.visitUnsupportedDefinition(definition) as UnsupportedDefinition).copy(
            locator = Locator.UNKNOWN
        )

    override fun visitExpressionDefinition(statement: ExpressionDefinition): Statement =
        (super.visitExpressionDefinition(statement) as ExpressionDefinition).copy(
            locator = Locator.UNKNOWN
        )

    override fun visitFunctionDefinition(statement: FunctionDefinition): Statement =
        (super.visitFunctionDefinition(statement) as FunctionDefinition).copy(
            locator = Locator.UNKNOWN
        )

    override fun visitContextDefinition(statement: ContextDefinition): Statement =
        (super.visitContextDefinition(statement) as ContextDefinition).copy(
            locator = Locator.UNKNOWN
        )

    override fun visitUnsupportedStatement(statement: UnsupportedStatement): Statement =
        (super.visitUnsupportedStatement(statement) as UnsupportedStatement).copy(
            locator = Locator.UNKNOWN
        )

    override fun visitOperandDefinition(definition: OperandDefinition): OperandDefinition =
        super.visitOperandDefinition(definition).copy(locator = Locator.UNKNOWN)

    override fun visitExpressionFunctionBody(body: ExpressionFunctionBody): FunctionBody =
        (super.visitExpressionFunctionBody(body) as ExpressionFunctionBody).copy(
            locator = Locator.UNKNOWN
        )

    override fun visitExternalFunctionBody(body: ExternalFunctionBody): FunctionBody =
        (super.visitExternalFunctionBody(body) as ExternalFunctionBody).copy(
            locator = Locator.UNKNOWN
        )

    override fun visitExpression(expression: Expression): Expression =
        super.visitExpression(expression).withUnknownLocator()

    override fun visitLiteral(literal: Literal): Literal =
        super.visitLiteral(literal).withUnknownLocator()

    override fun visitQuantityLiteral(literal: QuantityLiteral): Literal =
        (super.visitQuantityLiteral(literal) as QuantityLiteral).copy(locator = Locator.UNKNOWN)

    override fun visitTypeSpecifier(typeSpecifier: TypeSpecifier): TypeSpecifier =
        super.visitTypeSpecifier(typeSpecifier).withUnknownLocator()

    override fun visitNamedTypeSpecifier(typeSpecifier: NamedTypeSpecifier): NamedTypeSpecifier =
        super.visitNamedTypeSpecifier(typeSpecifier).copy(locator = Locator.UNKNOWN)

    override fun visitListTypeSpecifier(typeSpecifier: ListTypeSpecifier): TypeSpecifier =
        (super.visitListTypeSpecifier(typeSpecifier) as ListTypeSpecifier).copy(
            locator = Locator.UNKNOWN
        )

    override fun visitIntervalTypeSpecifier(typeSpecifier: IntervalTypeSpecifier): TypeSpecifier =
        (super.visitIntervalTypeSpecifier(typeSpecifier) as IntervalTypeSpecifier).copy(
            locator = Locator.UNKNOWN
        )

    override fun visitTupleTypeSpecifier(typeSpecifier: TupleTypeSpecifier): TypeSpecifier =
        (super.visitTupleTypeSpecifier(typeSpecifier) as TupleTypeSpecifier).copy(
            locator = Locator.UNKNOWN
        )

    override fun visitChoiceTypeSpecifier(typeSpecifier: ChoiceTypeSpecifier): TypeSpecifier =
        (super.visitChoiceTypeSpecifier(typeSpecifier) as ChoiceTypeSpecifier).copy(
            locator = Locator.UNKNOWN
        )

    override fun visitTupleElement(element: TupleElement): TupleElement =
        super.visitTupleElement(element).copy(locator = Locator.UNKNOWN)

    override fun visitTupleElementValue(value: TupleElementValue): TupleElementValue =
        super.visitTupleElementValue(value).copy(locator = Locator.UNKNOWN)

    override fun visitTerminologyReference(reference: TerminologyReference): TerminologyReference =
        super.visitTerminologyReference(reference).copy(locator = Locator.UNKNOWN)

    override fun visitCaseItem(item: CaseItem): CaseItem =
        super.visitCaseItem(item).copy(locator = Locator.UNKNOWN)

    override fun visitLetClauseItem(item: LetClauseItem): LetClauseItem =
        super.visitLetClauseItem(item).copy(locator = Locator.UNKNOWN)

    override fun visitAggregateClause(clause: AggregateClause): AggregateClause =
        super.visitAggregateClause(clause).copy(locator = Locator.UNKNOWN)

    override fun visitReturnClause(clause: ReturnClause): ReturnClause =
        super.visitReturnClause(clause).copy(locator = Locator.UNKNOWN)

    override fun visitSortClause(clause: SortClause): SortClause =
        super.visitSortClause(clause).copy(locator = Locator.UNKNOWN)

    override fun visitSortByItem(item: SortByItem): SortByItem =
        super.visitSortByItem(item).copy(locator = Locator.UNKNOWN)

    override fun visitWithClause(clause: WithClause): QueryInclusionClause =
        (super.visitWithClause(clause) as WithClause).copy(locator = Locator.UNKNOWN)

    override fun visitWithoutClause(clause: WithoutClause): QueryInclusionClause =
        (super.visitWithoutClause(clause) as WithoutClause).copy(locator = Locator.UNKNOWN)

    override fun visitRetrieveExpression(expression: RetrieveExpression): Expression =
        (super.visitRetrieveExpression(expression) as RetrieveExpression).copy(
            locator = Locator.UNKNOWN
        )

    override fun visitAliasedQuerySource(source: AliasedQuerySource): AliasedQuerySource =
        super.visitAliasedQuerySource(source).copy(locator = Locator.UNKNOWN)

    override fun visitExpressionQuerySource(source: ExpressionQuerySource): QuerySource =
        (super.visitExpressionQuerySource(source) as ExpressionQuerySource).copy(
            locator = Locator.UNKNOWN
        )

    override fun visitTerminologyRestriction(
        restriction: TerminologyRestriction
    ): TerminologyRestriction =
        super.visitTerminologyRestriction(restriction).copy(locator = Locator.UNKNOWN)

    override fun visitConcurrentIntervalPhrase(
        phrase: ConcurrentIntervalPhrase
    ): IntervalOperatorPhrase =
        (super.visitConcurrentIntervalPhrase(phrase) as ConcurrentIntervalPhrase).copy(
            locator = Locator.UNKNOWN
        )

    override fun visitIncludesIntervalPhrase(
        phrase: IncludesIntervalPhrase
    ): IntervalOperatorPhrase =
        (super.visitIncludesIntervalPhrase(phrase) as IncludesIntervalPhrase).copy(
            locator = Locator.UNKNOWN
        )

    override fun visitIncludedInIntervalPhrase(
        phrase: IncludedInIntervalPhrase
    ): IntervalOperatorPhrase =
        (super.visitIncludedInIntervalPhrase(phrase) as IncludedInIntervalPhrase).copy(
            locator = Locator.UNKNOWN
        )

    override fun visitBeforeOrAfterIntervalPhrase(
        phrase: BeforeOrAfterIntervalPhrase
    ): IntervalOperatorPhrase =
        (super.visitBeforeOrAfterIntervalPhrase(phrase) as BeforeOrAfterIntervalPhrase).copy(
            locator = Locator.UNKNOWN
        )

    override fun visitWithinIntervalPhrase(phrase: WithinIntervalPhrase): IntervalOperatorPhrase =
        (super.visitWithinIntervalPhrase(phrase) as WithinIntervalPhrase).copy(
            locator = Locator.UNKNOWN
        )

    override fun visitMeetsIntervalPhrase(phrase: MeetsIntervalPhrase): IntervalOperatorPhrase =
        (super.visitMeetsIntervalPhrase(phrase) as MeetsIntervalPhrase).copy(
            locator = Locator.UNKNOWN
        )

    override fun visitOverlapsIntervalPhrase(
        phrase: OverlapsIntervalPhrase
    ): IntervalOperatorPhrase =
        (super.visitOverlapsIntervalPhrase(phrase) as OverlapsIntervalPhrase).copy(
            locator = Locator.UNKNOWN
        )

    override fun visitStartsIntervalPhrase(phrase: StartsIntervalPhrase): IntervalOperatorPhrase =
        (super.visitStartsIntervalPhrase(phrase) as StartsIntervalPhrase).copy(
            locator = Locator.UNKNOWN
        )

    override fun visitEndsIntervalPhrase(phrase: EndsIntervalPhrase): IntervalOperatorPhrase =
        (super.visitEndsIntervalPhrase(phrase) as EndsIntervalPhrase).copy(
            locator = Locator.UNKNOWN
        )

    override fun visitUnsupportedIntervalPhrase(
        phrase: UnsupportedIntervalPhrase
    ): IntervalOperatorPhrase =
        (super.visitUnsupportedIntervalPhrase(phrase) as UnsupportedIntervalPhrase).copy(
            locator = Locator.UNKNOWN
        )

    override fun visitQuantityOffset(offset: QuantityOffset): QuantityOffset =
        super.visitQuantityOffset(offset).copy(locator = Locator.UNKNOWN)

    override fun visitTemporalRelationshipPhrase(
        phrase: TemporalRelationshipPhrase
    ): TemporalRelationshipPhrase =
        super.visitTemporalRelationshipPhrase(phrase).copy(locator = Locator.UNKNOWN)
}

private fun Expression.withUnknownLocator(): Expression =
    when (this) {
        is IdentifierExpression -> copy(locator = Locator.UNKNOWN)
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
        is ExternalConstantExpression -> copy(locator = Locator.UNKNOWN)
        is UnsupportedExpression -> copy(locator = Locator.UNKNOWN)
        is LiteralExpression -> copy(locator = Locator.UNKNOWN)
        is OperatorBinaryExpression -> copy(locator = Locator.UNKNOWN)
        is OperatorUnaryExpression -> copy(locator = Locator.UNKNOWN)
    }

private fun Literal.withUnknownLocator(): Literal =
    when (this) {
        is StringLiteral -> copy(locator = Locator.UNKNOWN)
        is LongLiteral -> copy(locator = Locator.UNKNOWN)
        is IntLiteral -> copy(locator = Locator.UNKNOWN)
        is DecimalLiteral -> copy(locator = Locator.UNKNOWN)
        is BooleanLiteral -> copy(locator = Locator.UNKNOWN)
        is NullLiteral -> copy(locator = Locator.UNKNOWN)
        is QuantityLiteral -> copy(locator = Locator.UNKNOWN)
        is DateTimeLiteral -> copy(locator = Locator.UNKNOWN)
        is TimeLiteral -> copy(locator = Locator.UNKNOWN)
        is TupleLiteral -> copy(locator = Locator.UNKNOWN)
        is InstanceLiteral -> copy(locator = Locator.UNKNOWN)
        is IntervalLiteral -> copy(locator = Locator.UNKNOWN)
        is ListLiteral -> copy(locator = Locator.UNKNOWN)
        is RatioLiteral -> copy(locator = Locator.UNKNOWN)
        is CodeLiteral -> copy(locator = Locator.UNKNOWN)
        is ConceptLiteral -> copy(locator = Locator.UNKNOWN)
    }

private fun TypeSpecifier.withUnknownLocator(): TypeSpecifier =
    when (this) {
        is NamedTypeSpecifier -> copy(locator = Locator.UNKNOWN)
        is ListTypeSpecifier -> copy(locator = Locator.UNKNOWN)
        is IntervalTypeSpecifier -> copy(locator = Locator.UNKNOWN)
        is TupleTypeSpecifier -> copy(locator = Locator.UNKNOWN)
        is ChoiceTypeSpecifier -> copy(locator = Locator.UNKNOWN)
    }
