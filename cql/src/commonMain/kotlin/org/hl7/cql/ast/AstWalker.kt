package org.hl7.cql.ast

/**
 * Read-only traversal of the CQL AST. Subclasses can override the visit functions to implement
 * analyses without cloning or mutating the original tree. Each visit method is responsible for
 * walking its children and invoking the appropriate hooks.
 */
@Suppress("TooManyFunctions", "LongMethod", "LargeClass")
open class AstWalker {

    open fun visitLibrary(library: Library) {
        library.definitions.forEach { visitDefinition(it) }
        library.statements.forEach { visitStatement(it) }
    }

    open fun visitDefinition(definition: Definition) {
        when (definition) {
            is UsingDefinition -> visitUsingDefinition(definition)
            is IncludeDefinition -> visitIncludeDefinition(definition)
            is CodeSystemDefinition -> visitCodeSystemDefinition(definition)
            is ValueSetDefinition -> visitValueSetDefinition(definition)
            is CodeDefinition -> visitCodeDefinition(definition)
            is ConceptDefinition -> visitConceptDefinition(definition)
            is ParameterDefinition -> visitParameterDefinition(definition)
            is UnsupportedDefinition -> visitUnsupportedDefinition(definition)
        }
    }

    open fun visitUsingDefinition(definition: UsingDefinition) {}

    open fun visitIncludeDefinition(definition: IncludeDefinition) {}

    open fun visitCodeSystemDefinition(definition: CodeSystemDefinition) {}

    open fun visitValueSetDefinition(definition: ValueSetDefinition) {}

    open fun visitCodeDefinition(definition: CodeDefinition) {}

    open fun visitConceptDefinition(definition: ConceptDefinition) {}

    open fun visitParameterDefinition(definition: ParameterDefinition) {
        definition.default?.let { visitExpression(it) }
    }

    open fun visitUnsupportedDefinition(definition: UnsupportedDefinition) {}

    open fun visitStatement(statement: Statement) {
        when (statement) {
            is ContextDefinition -> visitContextDefinition(statement)
            is ExpressionDefinition -> visitExpressionDefinition(statement)
            is FunctionDefinition -> visitFunctionDefinition(statement)
            is UnsupportedStatement -> visitUnsupportedStatement(statement)
        }
    }

    open fun visitContextDefinition(statement: ContextDefinition) {}

    open fun visitExpressionDefinition(statement: ExpressionDefinition) {
        visitExpression(statement.expression)
    }

    open fun visitFunctionDefinition(statement: FunctionDefinition) {
        statement.operands.forEach { visitOperandDefinition(it) }
        visitFunctionBody(statement.body)
    }

    open fun visitUnsupportedStatement(statement: UnsupportedStatement) {}

    open fun visitOperandDefinition(definition: OperandDefinition) {}

    open fun visitFunctionBody(body: FunctionBody) {
        when (body) {
            is ExpressionFunctionBody -> visitExpressionFunctionBody(body)
            is ExternalFunctionBody -> visitExternalFunctionBody(body)
        }
    }

    open fun visitExpressionFunctionBody(body: ExpressionFunctionBody) {
        visitExpression(body.expression)
    }

    open fun visitExternalFunctionBody(body: ExternalFunctionBody) {}

    @Suppress("CyclomaticComplexMethod")
    open fun visitExpression(expression: Expression) {
        when (expression) {
            is IdentifierExpression -> visitIdentifierExpression(expression)
            is ExistsExpression -> visitExistsExpression(expression)
            is MembershipExpression -> visitMembershipExpression(expression)
            is BetweenExpression -> visitBetweenExpression(expression)
            is DurationBetweenExpression -> visitDurationBetweenExpression(expression)
            is DifferenceBetweenExpression -> visitDifferenceBetweenExpression(expression)
            is DurationOfExpression -> visitDurationOfExpression(expression)
            is DifferenceOfExpression -> visitDifferenceOfExpression(expression)
            is WidthExpression -> visitWidthExpression(expression)
            is ElementExtractorExpression -> visitElementExtractorExpression(expression)
            is TypeExtentExpression -> visitTypeExtentExpression(expression)
            is ConversionExpression -> visitConversionExpression(expression)
            is TimeBoundaryExpression -> visitTimeBoundaryExpression(expression)
            is FunctionCallExpression -> visitFunctionCallExpression(expression)
            is PropertyAccessExpression -> visitPropertyAccessExpression(expression)
            is IndexExpression -> visitIndexExpression(expression)
            is IfExpression -> visitIfExpression(expression)
            is CaseExpression -> visitCaseExpression(expression)
            is QueryExpression -> visitQueryExpression(expression)
            is RetrieveExpression -> visitRetrieveExpression(expression)
            is ListTransformExpression -> visitListTransformExpression(expression)
            is ExpandCollapseExpression -> visitExpandCollapseExpression(expression)
            is IntervalRelationExpression -> visitIntervalRelationExpression(expression)
            is IsExpression -> visitIsExpression(expression)
            is AsExpression -> visitAsExpression(expression)
            is CastExpression -> visitCastExpression(expression)
            is LiteralExpression -> visitLiteralExpression(expression)
            is OperatorBinaryExpression -> visitOperatorBinaryExpression(expression)
            is OperatorUnaryExpression -> visitOperatorUnaryExpression(expression)
            is ExternalConstantExpression -> visitExternalConstantExpression(expression)
            is DateTimeComponentExpression -> visitDateTimeComponentExpression(expression)
            is UnsupportedExpression -> visitUnsupportedExpression(expression)
        }
    }

    open fun visitIdentifierExpression(expression: IdentifierExpression) {}

    open fun visitExistsExpression(expression: ExistsExpression) {
        visitExpression(expression.operand)
    }

    open fun visitMembershipExpression(expression: MembershipExpression) {
        visitExpression(expression.left)
        visitExpression(expression.right)
    }

    open fun visitBetweenExpression(expression: BetweenExpression) {
        visitExpression(expression.input)
        visitExpression(expression.lower)
        visitExpression(expression.upper)
    }

    open fun visitDurationBetweenExpression(expression: DurationBetweenExpression) {
        visitExpression(expression.lower)
        visitExpression(expression.upper)
    }

    open fun visitDifferenceBetweenExpression(expression: DifferenceBetweenExpression) {
        visitExpression(expression.lower)
        visitExpression(expression.upper)
    }

    open fun visitDurationOfExpression(expression: DurationOfExpression) {
        visitExpression(expression.operand)
    }

    open fun visitDifferenceOfExpression(expression: DifferenceOfExpression) {
        visitExpression(expression.operand)
    }

    open fun visitWidthExpression(expression: WidthExpression) {
        visitExpression(expression.operand)
    }

    open fun visitElementExtractorExpression(expression: ElementExtractorExpression) {
        visitExpression(expression.operand)
    }

    open fun visitTypeExtentExpression(expression: TypeExtentExpression) {}

    open fun visitConversionExpression(expression: ConversionExpression) {
        visitExpression(expression.operand)
    }

    open fun visitTimeBoundaryExpression(expression: TimeBoundaryExpression) {
        visitExpression(expression.operand)
    }

    open fun visitDateTimeComponentExpression(expression: DateTimeComponentExpression) {
        visitExpression(expression.operand)
    }

    open fun visitExternalConstantExpression(expression: ExternalConstantExpression) {}

    open fun visitListTransformExpression(expression: ListTransformExpression) {
        visitExpression(expression.operand)
    }

    open fun visitExpandCollapseExpression(expression: ExpandCollapseExpression) {
        visitExpression(expression.operand)
        expression.perExpression?.let { visitExpression(it) }
    }

    open fun visitFunctionCallExpression(expression: FunctionCallExpression) {
        expression.target?.let { visitExpression(it) }
        expression.arguments.forEach { visitExpression(it) }
    }

    open fun visitPropertyAccessExpression(expression: PropertyAccessExpression) {
        visitExpression(expression.target)
    }

    open fun visitIndexExpression(expression: IndexExpression) {
        visitExpression(expression.target)
        visitExpression(expression.index)
    }

    open fun visitIfExpression(expression: IfExpression) {
        visitExpression(expression.condition)
        visitExpression(expression.thenBranch)
        visitExpression(expression.elseBranch)
    }

    open fun visitCaseExpression(expression: CaseExpression) {
        expression.comparand?.let { visitExpression(it) }
        expression.cases.forEach { visitCaseItem(it) }
        visitExpression(expression.elseResult)
    }

    open fun visitCaseItem(item: CaseItem) {
        visitExpression(item.condition)
        visitExpression(item.result)
    }

    open fun visitTupleElementValue(value: TupleElementValue) {
        visitExpression(value.expression)
    }

    open fun visitLiteralExpression(expression: LiteralExpression) {
        visitLiteral(expression.literal)
    }

    open fun visitLiteral(literal: Literal) {
        when (literal) {
            is QuantityLiteral -> visitQuantityLiteral(literal)
            is TupleLiteral -> visitTupleLiteral(literal)
            is InstanceLiteral -> visitInstanceLiteral(literal)
            is IntervalLiteral -> visitIntervalLiteral(literal)
            is ListLiteral -> visitListLiteral(literal)
            is RatioLiteral -> visitRatioLiteral(literal)
            is CodeLiteral -> visitCodeLiteral(literal)
            is ConceptLiteral -> visitConceptLiteral(literal)
            else -> {}
        }
    }

    open fun visitQuantityLiteral(literal: QuantityLiteral) {}

    open fun visitTupleLiteral(literal: TupleLiteral) {
        literal.elements.forEach { visitTupleElementValue(it) }
    }

    open fun visitInstanceLiteral(literal: InstanceLiteral) {
        literal.elements.forEach { visitTupleElementValue(it) }
    }

    open fun visitIntervalLiteral(literal: IntervalLiteral) {
        visitExpression(literal.lower)
        visitExpression(literal.upper)
    }

    open fun visitListLiteral(literal: ListLiteral) {
        literal.elements.forEach { visitExpression(it) }
    }

    open fun visitRatioLiteral(literal: RatioLiteral) {
        visitQuantityLiteral(literal.numerator)
        visitQuantityLiteral(literal.denominator)
    }

    open fun visitCodeLiteral(literal: CodeLiteral) {}

    open fun visitConceptLiteral(literal: ConceptLiteral) {
        literal.codes.forEach { visitCodeLiteral(it) }
    }

    open fun visitOperatorBinaryExpression(expression: OperatorBinaryExpression) {
        visitExpression(expression.left)
        visitExpression(expression.right)
    }

    open fun visitOperatorUnaryExpression(expression: OperatorUnaryExpression) {
        visitExpression(expression.operand)
    }

    open fun visitIsExpression(expression: IsExpression) {
        visitExpression(expression.operand)
    }

    open fun visitAsExpression(expression: AsExpression) {
        visitExpression(expression.operand)
    }

    open fun visitCastExpression(expression: CastExpression) {
        visitExpression(expression.operand)
    }

    open fun visitQueryExpression(expression: QueryExpression) {
        expression.sources.forEach { visitAliasedQuerySource(it) }
        expression.lets.forEach { visitLetClauseItem(it) }
        expression.inclusions.forEach { visitQueryInclusionClause(it) }
        expression.where?.let { visitExpression(it) }
        expression.aggregate?.let { visitAggregateClause(it) }
        expression.result?.let { visitReturnClause(it) }
        expression.sort?.let { visitSortClause(it) }
    }

    open fun visitAliasedQuerySource(source: AliasedQuerySource) {
        visitQuerySource(source.source)
    }

    open fun visitQuerySource(source: QuerySource) {
        when (source) {
            is RetrieveExpression -> visitRetrieveExpression(source)
            is ExpressionQuerySource -> visitExpressionQuerySource(source)
        }
    }

    open fun visitRetrieveExpression(expression: RetrieveExpression) {
        expression.terminology?.let { visitTerminologyRestriction(it) }
    }

    open fun visitTerminologyRestriction(restriction: TerminologyRestriction) {
        visitExpression(restriction.terminology)
    }

    open fun visitExpressionQuerySource(source: ExpressionQuerySource) {
        visitExpression(source.expression)
    }

    open fun visitLetClauseItem(item: LetClauseItem) {
        visitExpression(item.expression)
    }

    open fun visitQueryInclusionClause(clause: QueryInclusionClause) {
        when (clause) {
            is WithClause -> visitWithClause(clause)
            is WithoutClause -> visitWithoutClause(clause)
        }
    }

    open fun visitWithClause(clause: WithClause) {
        visitAliasedQuerySource(clause.source)
        visitExpression(clause.condition)
    }

    open fun visitWithoutClause(clause: WithoutClause) {
        visitAliasedQuerySource(clause.source)
        visitExpression(clause.condition)
    }

    open fun visitAggregateClause(clause: AggregateClause) {
        clause.starting?.let { visitExpression(it) }
        visitExpression(clause.expression)
    }

    open fun visitReturnClause(clause: ReturnClause) {
        visitExpression(clause.expression)
    }

    open fun visitSortClause(clause: SortClause) {
        clause.items.forEach { visitSortByItem(it) }
    }

    open fun visitSortByItem(item: SortByItem) {
        visitExpression(item.expression)
    }

    open fun visitIntervalRelationExpression(expression: IntervalRelationExpression) {
        visitExpression(expression.left)
        visitIntervalOperatorPhrase(expression.phrase)
        visitExpression(expression.right)
    }

    open fun visitIntervalOperatorPhrase(phrase: IntervalOperatorPhrase) {
        when (phrase) {
            is ConcurrentIntervalPhrase -> visitConcurrentIntervalPhrase(phrase)
            is IncludesIntervalPhrase -> visitIncludesIntervalPhrase(phrase)
            is IncludedInIntervalPhrase -> visitIncludedInIntervalPhrase(phrase)
            is BeforeOrAfterIntervalPhrase -> visitBeforeOrAfterIntervalPhrase(phrase)
            is WithinIntervalPhrase -> visitWithinIntervalPhrase(phrase)
            is MeetsIntervalPhrase -> visitMeetsIntervalPhrase(phrase)
            is OverlapsIntervalPhrase -> visitOverlapsIntervalPhrase(phrase)
            is StartsIntervalPhrase -> visitStartsIntervalPhrase(phrase)
            is EndsIntervalPhrase -> visitEndsIntervalPhrase(phrase)
            is UnsupportedIntervalPhrase -> visitUnsupportedIntervalPhrase(phrase)
        }
    }

    open fun visitConcurrentIntervalPhrase(phrase: ConcurrentIntervalPhrase) {}

    open fun visitIncludesIntervalPhrase(phrase: IncludesIntervalPhrase) {}

    open fun visitIncludedInIntervalPhrase(phrase: IncludedInIntervalPhrase) {}

    open fun visitBeforeOrAfterIntervalPhrase(phrase: BeforeOrAfterIntervalPhrase) {
        phrase.offset?.let { visitQuantityOffset(it) }
        visitTemporalRelationshipPhrase(phrase.relationship)
    }

    open fun visitWithinIntervalPhrase(phrase: WithinIntervalPhrase) {
        visitQuantityLiteral(phrase.quantity)
    }

    open fun visitMeetsIntervalPhrase(phrase: MeetsIntervalPhrase) {}

    open fun visitOverlapsIntervalPhrase(phrase: OverlapsIntervalPhrase) {}

    open fun visitStartsIntervalPhrase(phrase: StartsIntervalPhrase) {}

    open fun visitEndsIntervalPhrase(phrase: EndsIntervalPhrase) {}

    open fun visitUnsupportedIntervalPhrase(phrase: UnsupportedIntervalPhrase) {}

    open fun visitQuantityOffset(offset: QuantityOffset) {
        visitQuantityLiteral(offset.quantity)
    }

    open fun visitTemporalRelationshipPhrase(phrase: TemporalRelationshipPhrase) {}

    open fun visitUnsupportedExpression(expression: UnsupportedExpression) {}
}
