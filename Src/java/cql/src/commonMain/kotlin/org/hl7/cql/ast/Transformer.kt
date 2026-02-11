package org.hl7.cql.ast

/**
 * A reusable visitor that can traverse and optionally rewrite any node in the CQL AST. Subclasses
 * can override the *visit* functions to normalize locators, enrich nodes with inferred metadata, or
 * return entirely different subtrees. By default, the transformer rebuilds nodes with the visits
 * applied to their children and returns the original node when no modification is required.
 */
@Suppress("TooManyFunctions")
open class Transformer {

    open fun visitLibrary(library: Library): Library =
        library.copy(
            definitions = library.definitions.map { visitDefinition(it) },
            statements = library.statements.map { visitStatement(it) },
        )

    open fun visitDefinition(definition: Definition): Definition =
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

    open fun visitUsingDefinition(definition: UsingDefinition): Definition = definition

    open fun visitIncludeDefinition(definition: IncludeDefinition): Definition = definition

    open fun visitCodeSystemDefinition(definition: CodeSystemDefinition): Definition = definition

    open fun visitValueSetDefinition(definition: ValueSetDefinition): Definition =
        definition.copy(codesystems = definition.codesystems.map { visitTerminologyReference(it) })

    open fun visitCodeDefinition(definition: CodeDefinition): Definition =
        definition.copy(system = visitTerminologyReference(definition.system))

    open fun visitConceptDefinition(definition: ConceptDefinition): Definition =
        definition.copy(codes = definition.codes.map { visitTerminologyReference(it) })

    open fun visitParameterDefinition(definition: ParameterDefinition): Definition =
        definition.copy(
            type = definition.type?.let { visitTypeSpecifier(it) },
            default = definition.default?.let { visitExpression(it) },
        )

    open fun visitUnsupportedDefinition(definition: UnsupportedDefinition): Definition = definition

    open fun visitStatement(statement: Statement): Statement =
        when (statement) {
            is ContextDefinition -> visitContextDefinition(statement)
            is ExpressionDefinition -> visitExpressionDefinition(statement)
            is FunctionDefinition -> visitFunctionDefinition(statement)
            is UnsupportedStatement -> visitUnsupportedStatement(statement)
        }

    open fun visitContextDefinition(statement: ContextDefinition): Statement = statement

    open fun visitExpressionDefinition(statement: ExpressionDefinition): Statement =
        statement.copy(expression = visitExpression(statement.expression))

    open fun visitFunctionDefinition(statement: FunctionDefinition): Statement =
        statement.copy(
            operands = statement.operands.map { visitOperandDefinition(it) },
            returnType = statement.returnType?.let { visitTypeSpecifier(it) },
            body = visitFunctionBody(statement.body),
        )

    open fun visitUnsupportedStatement(statement: UnsupportedStatement): Statement = statement

    open fun visitOperandDefinition(definition: OperandDefinition): OperandDefinition =
        definition.copy(type = visitTypeSpecifier(definition.type))

    open fun visitFunctionBody(body: FunctionBody): FunctionBody =
        when (body) {
            is ExpressionFunctionBody -> visitExpressionFunctionBody(body)
            is ExternalFunctionBody -> visitExternalFunctionBody(body)
        }

    open fun visitExpressionFunctionBody(body: ExpressionFunctionBody): FunctionBody =
        body.copy(expression = visitExpression(body.expression))

    open fun visitExternalFunctionBody(body: ExternalFunctionBody): FunctionBody = body

    @Suppress("CyclomaticComplexMethod")
    open fun visitExpression(expression: Expression): Expression =
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
            is DateTimeComponentExpression -> visitDateTimeComponentExpression(expression)
            is ExternalConstantExpression -> visitExternalConstantExpression(expression)
            is UnsupportedExpression -> visitUnsupportedExpression(expression)
            is LiteralExpression -> visitLiteralExpression(expression)
            is OperatorBinaryExpression -> visitOperatorBinaryExpression(expression)
            is OperatorUnaryExpression -> visitOperatorUnaryExpression(expression)
        }

    open fun visitIdentifierExpression(expression: IdentifierExpression): Expression = expression

    open fun visitExistsExpression(expression: ExistsExpression): Expression =
        expression.copy(operand = visitExpression(expression.operand))

    open fun visitMembershipExpression(expression: MembershipExpression): Expression =
        expression.copy(
            left = visitExpression(expression.left),
            right = visitExpression(expression.right),
        )

    open fun visitBetweenExpression(expression: BetweenExpression): Expression =
        expression.copy(
            input = visitExpression(expression.input),
            lower = visitExpression(expression.lower),
            upper = visitExpression(expression.upper),
        )

    open fun visitDurationBetweenExpression(expression: DurationBetweenExpression): Expression =
        expression.copy(
            lower = visitExpression(expression.lower),
            upper = visitExpression(expression.upper),
        )

    open fun visitDifferenceBetweenExpression(expression: DifferenceBetweenExpression): Expression =
        expression.copy(
            lower = visitExpression(expression.lower),
            upper = visitExpression(expression.upper),
        )

    open fun visitDurationOfExpression(expression: DurationOfExpression): Expression =
        expression.copy(operand = visitExpression(expression.operand))

    open fun visitDifferenceOfExpression(expression: DifferenceOfExpression): Expression =
        expression.copy(operand = visitExpression(expression.operand))

    open fun visitWidthExpression(expression: WidthExpression): Expression =
        expression.copy(operand = visitExpression(expression.operand))

    open fun visitElementExtractorExpression(expression: ElementExtractorExpression): Expression =
        expression.copy(operand = visitExpression(expression.operand))

    open fun visitTypeExtentExpression(expression: TypeExtentExpression): Expression =
        expression.copy(type = visitNamedTypeSpecifier(expression.type))

    open fun visitConversionExpression(expression: ConversionExpression): Expression =
        expression.copy(
            operand = visitExpression(expression.operand),
            destinationType = expression.destinationType?.let { visitTypeSpecifier(it) },
        )

    open fun visitTimeBoundaryExpression(expression: TimeBoundaryExpression): Expression =
        expression.copy(operand = visitExpression(expression.operand))

    open fun visitFunctionCallExpression(expression: FunctionCallExpression): Expression =
        expression.copy(
            target = expression.target?.let { visitExpression(it) },
            arguments = expression.arguments.map { visitExpression(it) },
        )

    open fun visitPropertyAccessExpression(expression: PropertyAccessExpression): Expression =
        expression.copy(target = visitExpression(expression.target))

    open fun visitIndexExpression(expression: IndexExpression): Expression =
        expression.copy(
            target = visitExpression(expression.target),
            index = visitExpression(expression.index),
        )

    open fun visitIfExpression(expression: IfExpression): Expression =
        expression.copy(
            condition = visitExpression(expression.condition),
            thenBranch = visitExpression(expression.thenBranch),
            elseBranch = visitExpression(expression.elseBranch),
        )

    open fun visitCaseExpression(expression: CaseExpression): Expression =
        expression.copy(
            comparand = expression.comparand?.let { visitExpression(it) },
            cases = expression.cases.map { visitCaseItem(it) },
            elseResult = visitExpression(expression.elseResult),
        )

    open fun visitCaseItem(item: CaseItem): CaseItem =
        item.copy(
            condition = visitExpression(item.condition),
            result = visitExpression(item.result),
        )

    open fun visitQueryExpression(expression: QueryExpression): Expression =
        expression.copy(
            sources = expression.sources.map { visitAliasedQuerySource(it) },
            lets = expression.lets.map { visitLetClauseItem(it) },
            inclusions = expression.inclusions.map { visitQueryInclusionClause(it) },
            where = expression.where?.let { visitExpression(it) },
            aggregate = expression.aggregate?.let { visitAggregateClause(it) },
            result = expression.result?.let { visitReturnClause(it) },
            sort = expression.sort?.let { visitSortClause(it) },
        )

    open fun visitRetrieveExpression(expression: RetrieveExpression): Expression =
        expression.copy(
            typeSpecifier = visitNamedTypeSpecifier(expression.typeSpecifier),
            terminology = expression.terminology?.let { visitTerminologyRestriction(it) },
            comparator = expression.comparator,
            context = expression.context,
            codePath = expression.codePath,
        )

    open fun visitListTransformExpression(expression: ListTransformExpression): Expression =
        expression.copy(operand = visitExpression(expression.operand))

    open fun visitExpandCollapseExpression(expression: ExpandCollapseExpression): Expression =
        expression.copy(
            operand = visitExpression(expression.operand),
            perExpression = expression.perExpression?.let { visitExpression(it) },
        )

    open fun visitIntervalRelationExpression(expression: IntervalRelationExpression): Expression =
        expression.copy(
            left = visitExpression(expression.left),
            phrase = visitIntervalOperatorPhrase(expression.phrase),
            right = visitExpression(expression.right),
        )

    open fun visitIsExpression(expression: IsExpression): Expression =
        expression.copy(
            operand = visitExpression(expression.operand),
            type = visitTypeSpecifier(expression.type),
        )

    open fun visitAsExpression(expression: AsExpression): Expression =
        expression.copy(
            operand = visitExpression(expression.operand),
            type = visitTypeSpecifier(expression.type),
        )

    open fun visitCastExpression(expression: CastExpression): Expression =
        expression.copy(
            operand = visitExpression(expression.operand),
            type = visitTypeSpecifier(expression.type),
        )

    open fun visitDateTimeComponentExpression(expression: DateTimeComponentExpression): Expression =
        expression.copy(operand = visitExpression(expression.operand))

    open fun visitExternalConstantExpression(expression: ExternalConstantExpression): Expression =
        expression

    open fun visitUnsupportedExpression(expression: UnsupportedExpression): Expression = expression

    open fun visitLiteralExpression(expression: LiteralExpression): Expression =
        expression.copy(literal = visitLiteral(expression.literal))

    open fun visitOperatorBinaryExpression(expression: OperatorBinaryExpression): Expression =
        expression.copy(
            left = visitExpression(expression.left),
            right = visitExpression(expression.right),
        )

    open fun visitOperatorUnaryExpression(expression: OperatorUnaryExpression): Expression =
        expression.copy(operand = visitExpression(expression.operand))

    @Suppress("CyclomaticComplexMethod")
    open fun visitLiteral(literal: Literal): Literal =
        when (literal) {
            is StringLiteral -> visitStringLiteral(literal)
            is LongLiteral -> visitLongLiteral(literal)
            is IntLiteral -> visitIntLiteral(literal)
            is DecimalLiteral -> visitDecimalLiteral(literal)
            is BooleanLiteral -> visitBooleanLiteral(literal)
            is NullLiteral -> visitNullLiteral(literal)
            is QuantityLiteral -> visitQuantityLiteral(literal)
            is DateTimeLiteral -> visitDateTimeLiteral(literal)
            is TimeLiteral -> visitTimeLiteral(literal)
            is TupleLiteral -> visitTupleLiteral(literal)
            is InstanceLiteral -> visitInstanceLiteral(literal)
            is IntervalLiteral -> visitIntervalLiteral(literal)
            is ListLiteral -> visitListLiteral(literal)
            is RatioLiteral -> visitRatioLiteral(literal)
            is CodeLiteral -> visitCodeLiteral(literal)
            is ConceptLiteral -> visitConceptLiteral(literal)
        }

    open fun visitStringLiteral(literal: StringLiteral): Literal = literal

    open fun visitLongLiteral(literal: LongLiteral): Literal = literal

    open fun visitIntLiteral(literal: IntLiteral): Literal = literal

    open fun visitDecimalLiteral(literal: DecimalLiteral): Literal = literal

    open fun visitBooleanLiteral(literal: BooleanLiteral): Literal = literal

    open fun visitNullLiteral(literal: NullLiteral): Literal = literal

    open fun visitQuantityLiteral(literal: QuantityLiteral): Literal = literal

    open fun visitDateTimeLiteral(literal: DateTimeLiteral): Literal = literal

    open fun visitTimeLiteral(literal: TimeLiteral): Literal = literal

    open fun visitTupleLiteral(literal: TupleLiteral): Literal =
        literal.copy(elements = literal.elements.map { visitTupleElementValue(it) })

    open fun visitInstanceLiteral(literal: InstanceLiteral): Literal =
        literal.copy(
            type = literal.type?.let { visitNamedTypeSpecifier(it) },
            elements = literal.elements.map { visitTupleElementValue(it) },
        )

    open fun visitIntervalLiteral(literal: IntervalLiteral): Literal =
        literal.copy(lower = visitExpression(literal.lower), upper = visitExpression(literal.upper))

    open fun visitListLiteral(literal: ListLiteral): Literal =
        literal.copy(
            elements = literal.elements.map { visitExpression(it) },
            elementType = literal.elementType?.let { visitTypeSpecifier(it) },
        )

    open fun visitRatioLiteral(literal: RatioLiteral): Literal =
        literal.copy(
            numerator = visitQuantityLiteral(literal.numerator) as QuantityLiteral,
            denominator = visitQuantityLiteral(literal.denominator) as QuantityLiteral,
        )

    open fun visitCodeLiteral(literal: CodeLiteral): Literal =
        literal.copy(system = visitTerminologyReference(literal.system))

    open fun visitConceptLiteral(literal: ConceptLiteral): Literal =
        literal.copy(codes = literal.codes.map { visitCodeLiteral(it) as CodeLiteral })

    open fun visitTypeSpecifier(typeSpecifier: TypeSpecifier): TypeSpecifier =
        when (typeSpecifier) {
            is NamedTypeSpecifier -> visitNamedTypeSpecifier(typeSpecifier)
            is ListTypeSpecifier -> visitListTypeSpecifier(typeSpecifier)
            is IntervalTypeSpecifier -> visitIntervalTypeSpecifier(typeSpecifier)
            is TupleTypeSpecifier -> visitTupleTypeSpecifier(typeSpecifier)
            is ChoiceTypeSpecifier -> visitChoiceTypeSpecifier(typeSpecifier)
        }

    open fun visitNamedTypeSpecifier(typeSpecifier: NamedTypeSpecifier): NamedTypeSpecifier =
        typeSpecifier

    open fun visitListTypeSpecifier(typeSpecifier: ListTypeSpecifier): TypeSpecifier =
        typeSpecifier.copy(elementType = visitTypeSpecifier(typeSpecifier.elementType))

    open fun visitIntervalTypeSpecifier(typeSpecifier: IntervalTypeSpecifier): TypeSpecifier =
        typeSpecifier.copy(pointType = visitTypeSpecifier(typeSpecifier.pointType))

    open fun visitTupleTypeSpecifier(typeSpecifier: TupleTypeSpecifier): TypeSpecifier =
        typeSpecifier.copy(elements = typeSpecifier.elements.map { visitTupleElement(it) })

    open fun visitChoiceTypeSpecifier(typeSpecifier: ChoiceTypeSpecifier): TypeSpecifier =
        typeSpecifier.copy(choices = typeSpecifier.choices.map { visitTypeSpecifier(it) })

    open fun visitTupleElement(element: TupleElement): TupleElement =
        element.copy(type = visitTypeSpecifier(element.type))

    open fun visitTupleElementValue(value: TupleElementValue): TupleElementValue =
        value.copy(expression = visitExpression(value.expression))

    open fun visitTerminologyReference(reference: TerminologyReference): TerminologyReference =
        reference

    open fun visitTerminologyRestriction(
        restriction: TerminologyRestriction
    ): TerminologyRestriction =
        restriction.copy(terminology = visitExpression(restriction.terminology))

    open fun visitLetClauseItem(item: LetClauseItem): LetClauseItem =
        item.copy(expression = visitExpression(item.expression))

    open fun visitAggregateClause(clause: AggregateClause): AggregateClause =
        clause.copy(
            starting = clause.starting?.let { visitExpression(it) },
            expression = visitExpression(clause.expression),
        )

    open fun visitReturnClause(clause: ReturnClause): ReturnClause =
        clause.copy(expression = visitExpression(clause.expression))

    open fun visitSortClause(clause: SortClause): SortClause =
        clause.copy(items = clause.items.map { visitSortByItem(it) })

    open fun visitSortByItem(item: SortByItem): SortByItem =
        item.copy(expression = visitExpression(item.expression))

    open fun visitQueryInclusionClause(clause: QueryInclusionClause): QueryInclusionClause =
        when (clause) {
            is WithClause -> visitWithClause(clause)
            is WithoutClause -> visitWithoutClause(clause)
        }

    open fun visitWithClause(clause: WithClause): QueryInclusionClause =
        clause.copy(
            source = visitAliasedQuerySource(clause.source),
            condition = visitExpression(clause.condition),
        )

    open fun visitWithoutClause(clause: WithoutClause): QueryInclusionClause =
        clause.copy(
            source = visitAliasedQuerySource(clause.source),
            condition = visitExpression(clause.condition),
        )

    open fun visitAliasedQuerySource(source: AliasedQuerySource): AliasedQuerySource =
        source.copy(source = visitQuerySource(source.source))

    open fun visitQuerySource(source: QuerySource): QuerySource =
        when (source) {
            is RetrieveExpression -> visitRetrieveExpression(source) as QuerySource
            is ExpressionQuerySource -> visitExpressionQuerySource(source)
        }

    open fun visitExpressionQuerySource(source: ExpressionQuerySource): QuerySource =
        source.copy(expression = visitExpression(source.expression))

    open fun visitIntervalOperatorPhrase(phrase: IntervalOperatorPhrase): IntervalOperatorPhrase =
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

    open fun visitConcurrentIntervalPhrase(
        phrase: ConcurrentIntervalPhrase
    ): IntervalOperatorPhrase = phrase

    open fun visitIncludesIntervalPhrase(phrase: IncludesIntervalPhrase): IntervalOperatorPhrase =
        phrase

    open fun visitIncludedInIntervalPhrase(
        phrase: IncludedInIntervalPhrase
    ): IntervalOperatorPhrase = phrase

    open fun visitBeforeOrAfterIntervalPhrase(
        phrase: BeforeOrAfterIntervalPhrase
    ): IntervalOperatorPhrase =
        phrase.copy(
            offset = phrase.offset?.let { visitQuantityOffset(it) },
            relationship = visitTemporalRelationshipPhrase(phrase.relationship),
        )

    open fun visitWithinIntervalPhrase(phrase: WithinIntervalPhrase): IntervalOperatorPhrase =
        phrase.copy(quantity = visitQuantityLiteral(phrase.quantity) as QuantityLiteral)

    open fun visitMeetsIntervalPhrase(phrase: MeetsIntervalPhrase): IntervalOperatorPhrase = phrase

    open fun visitOverlapsIntervalPhrase(phrase: OverlapsIntervalPhrase): IntervalOperatorPhrase =
        phrase

    open fun visitStartsIntervalPhrase(phrase: StartsIntervalPhrase): IntervalOperatorPhrase =
        phrase

    open fun visitEndsIntervalPhrase(phrase: EndsIntervalPhrase): IntervalOperatorPhrase = phrase

    open fun visitUnsupportedIntervalPhrase(
        phrase: UnsupportedIntervalPhrase
    ): IntervalOperatorPhrase = phrase

    open fun visitQuantityOffset(offset: QuantityOffset): QuantityOffset =
        offset.copy(quantity = visitQuantityLiteral(offset.quantity) as QuantityLiteral)

    open fun visitTemporalRelationshipPhrase(
        phrase: TemporalRelationshipPhrase
    ): TemporalRelationshipPhrase = phrase
}

/** Transform the library using the supplied [Transformer]. */
fun Library.transform(transformer: Transformer): Library = transformer.visitLibrary(this)

/** Transform the expression using the supplied [Transformer]. */
fun Expression.transform(transformer: Transformer): Expression = transformer.visitExpression(this)
