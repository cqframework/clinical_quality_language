package org.hl7.cql.ast

import kotlin.jvm.JvmInline

/**
 * Unique identifier assigned to an [AstNode]. The identifier is stable for the lifetime of the
 * associated AST instance and can be used as a key into side tables for analyses like type
 * inference.
 */
@JvmInline
value class AstNodeId(val value: Long)

/**
 * Records a mapping between AST nodes and their unique identifiers. Lookups rely on reference
 * equality so structurally equivalent nodes receive distinct identifiers.
 */
class AstIdTable internal constructor() {

    private data class Entry(val node: AstNode, val id: AstNodeId)

    private val entries = mutableListOf<Entry>()
    private var nextId: Long = 1

    /**
     * Returns the identifier for [node], assigning one if this is the first time the node has been
     * encountered.
     */
    fun idFor(node: AstNode): AstNodeId {
        entries.firstOrNull { it.node === node }?.let { return it.id }
        val id = AstNodeId(nextId++)
        entries += Entry(node, id)
        return id
    }

    /** Number of nodes with assigned identifiers. */
    val size: Int
        get() = entries.size

    /** Returns all identifiers and their associated nodes. Primarily intended for debugging/tests. */
    fun entries(): List<Pair<AstNodeId, AstNode>> = entries.map { it.id to it.node }
}

internal fun assignAstNodeIds(library: Library): AstIdTable =
    AstIdTable().also { AstIdAssigner(it).visitLibrary(library) }

internal fun assignAstNodeIds(expression: Expression): AstIdTable =
    AstIdTable().also { AstIdAssigner(it).visitExpression(expression) }

/**
 * Walks the AST and ensures every node reachable from the root is registered in the provided
 * [AstIdTable]. The traversal mirrors [AstWalker], adding support for type specifiers that are not
 * otherwise visited.
 */
internal class AstIdAssigner(private val ids: AstIdTable) : AstWalker() {

    private fun register(node: AstNode) {
        ids.idFor(node)
    }

    override fun visitLibrary(library: Library) {
        register(library)
        super.visitLibrary(library)
    }

    override fun visitDefinition(definition: Definition) {
        register(definition)
        super.visitDefinition(definition)
    }

    override fun visitUsingDefinition(definition: UsingDefinition) {
        register(definition)
        super.visitUsingDefinition(definition)
    }

    override fun visitIncludeDefinition(definition: IncludeDefinition) {
        register(definition)
        super.visitIncludeDefinition(definition)
    }

    override fun visitCodeSystemDefinition(definition: CodeSystemDefinition) {
        register(definition)
        super.visitCodeSystemDefinition(definition)
    }

    override fun visitValueSetDefinition(definition: ValueSetDefinition) {
        register(definition)
        super.visitValueSetDefinition(definition)
    }

    override fun visitCodeDefinition(definition: CodeDefinition) {
        register(definition)
        super.visitCodeDefinition(definition)
    }

    override fun visitConceptDefinition(definition: ConceptDefinition) {
        register(definition)
        super.visitConceptDefinition(definition)
    }

    override fun visitParameterDefinition(definition: ParameterDefinition) {
        register(definition)
        definition.type?.let { registerTypeSpecifier(it) }
        super.visitParameterDefinition(definition)
    }

    override fun visitUnsupportedDefinition(definition: UnsupportedDefinition) {
        register(definition)
        super.visitUnsupportedDefinition(definition)
    }

    override fun visitStatement(statement: Statement) {
        register(statement)
        super.visitStatement(statement)
    }

    override fun visitContextDefinition(statement: ContextDefinition) {
        register(statement)
        super.visitContextDefinition(statement)
    }

    override fun visitExpressionDefinition(statement: ExpressionDefinition) {
        register(statement)
        super.visitExpressionDefinition(statement)
    }

    override fun visitFunctionDefinition(statement: FunctionDefinition) {
        register(statement)
        statement.returnType?.let { registerTypeSpecifier(it) }
        statement.operands.forEach { registerTypeSpecifier(it.type) }
        super.visitFunctionDefinition(statement)
    }

    override fun visitUnsupportedStatement(statement: UnsupportedStatement) {
        register(statement)
        super.visitUnsupportedStatement(statement)
    }

    override fun visitOperandDefinition(definition: OperandDefinition) {
        registerTypeSpecifier(definition.type)
        super.visitOperandDefinition(definition)
    }

    override fun visitFunctionBody(body: FunctionBody) {
        register(body)
        super.visitFunctionBody(body)
    }

    override fun visitExpressionFunctionBody(body: ExpressionFunctionBody) {
        register(body)
        super.visitExpressionFunctionBody(body)
    }

    override fun visitExternalFunctionBody(body: ExternalFunctionBody) {
        register(body)
        super.visitExternalFunctionBody(body)
    }

    override fun visitExpression(expression: Expression) {
        register(expression)
        super.visitExpression(expression)
    }

    override fun visitIdentifierExpression(expression: IdentifierExpression) {
        register(expression)
        super.visitIdentifierExpression(expression)
    }

    override fun visitExistsExpression(expression: ExistsExpression) {
        register(expression)
        super.visitExistsExpression(expression)
    }

    override fun visitMembershipExpression(expression: MembershipExpression) {
        register(expression)
        super.visitMembershipExpression(expression)
    }

    override fun visitBetweenExpression(expression: BetweenExpression) {
        register(expression)
        super.visitBetweenExpression(expression)
    }

    override fun visitDurationBetweenExpression(expression: DurationBetweenExpression) {
        register(expression)
        super.visitDurationBetweenExpression(expression)
    }

    override fun visitDifferenceBetweenExpression(expression: DifferenceBetweenExpression) {
        register(expression)
        super.visitDifferenceBetweenExpression(expression)
    }

    override fun visitDurationOfExpression(expression: DurationOfExpression) {
        register(expression)
        super.visitDurationOfExpression(expression)
    }

    override fun visitDifferenceOfExpression(expression: DifferenceOfExpression) {
        register(expression)
        super.visitDifferenceOfExpression(expression)
    }

    override fun visitWidthExpression(expression: WidthExpression) {
        register(expression)
        super.visitWidthExpression(expression)
    }

    override fun visitElementExtractorExpression(expression: ElementExtractorExpression) {
        register(expression)
        super.visitElementExtractorExpression(expression)
    }

    override fun visitTypeExtentExpression(expression: TypeExtentExpression) {
        register(expression)
        registerTypeSpecifier(expression.type)
        super.visitTypeExtentExpression(expression)
    }

    override fun visitConversionExpression(expression: ConversionExpression) {
        register(expression)
        expression.destinationType?.let { registerTypeSpecifier(it) }
        super.visitConversionExpression(expression)
    }

    override fun visitTimeBoundaryExpression(expression: TimeBoundaryExpression) {
        register(expression)
        super.visitTimeBoundaryExpression(expression)
    }

    override fun visitDateTimeComponentExpression(expression: DateTimeComponentExpression) {
        register(expression)
        super.visitDateTimeComponentExpression(expression)
    }

    override fun visitFunctionCallExpression(expression: FunctionCallExpression) {
        register(expression)
        super.visitFunctionCallExpression(expression)
    }

    override fun visitPropertyAccessExpression(expression: PropertyAccessExpression) {
        register(expression)
        super.visitPropertyAccessExpression(expression)
    }

    override fun visitIndexExpression(expression: IndexExpression) {
        register(expression)
        super.visitIndexExpression(expression)
    }

    override fun visitIfExpression(expression: IfExpression) {
        register(expression)
        super.visitIfExpression(expression)
    }

    override fun visitCaseExpression(expression: CaseExpression) {
        register(expression)
        super.visitCaseExpression(expression)
    }

    override fun visitCaseItem(item: CaseItem) {
        register(item)
        super.visitCaseItem(item)
    }

    override fun visitTupleElementValue(value: TupleElementValue) {
        register(value)
        super.visitTupleElementValue(value)
    }

    override fun visitQueryExpression(expression: QueryExpression) {
        register(expression)
        super.visitQueryExpression(expression)
    }

    override fun visitAliasedQuerySource(source: AliasedQuerySource) {
        register(source)
        super.visitAliasedQuerySource(source)
    }

    override fun visitQuerySource(source: QuerySource) {
        register(source)
        super.visitQuerySource(source)
    }

    override fun visitRetrieveExpression(expression: RetrieveExpression) {
        register(expression)
        registerTypeSpecifier(expression.typeSpecifier)
        super.visitRetrieveExpression(expression)
        expression.terminology?.let { register(it) }
    }

    override fun visitTerminologyRestriction(restriction: TerminologyRestriction) {
        register(restriction)
        super.visitTerminologyRestriction(restriction)
    }

    override fun visitExpressionQuerySource(source: ExpressionQuerySource) {
        register(source)
        super.visitExpressionQuerySource(source)
    }

    override fun visitLetClauseItem(item: LetClauseItem) {
        register(item)
        super.visitLetClauseItem(item)
    }

    override fun visitQueryInclusionClause(clause: QueryInclusionClause) {
        register(clause)
        super.visitQueryInclusionClause(clause)
    }

    override fun visitWithClause(clause: WithClause) {
        register(clause)
        super.visitWithClause(clause)
    }

    override fun visitWithoutClause(clause: WithoutClause) {
        register(clause)
        super.visitWithoutClause(clause)
    }

    override fun visitAggregateClause(clause: AggregateClause) {
        register(clause)
        super.visitAggregateClause(clause)
    }

    override fun visitReturnClause(clause: ReturnClause) {
        register(clause)
        super.visitReturnClause(clause)
    }

    override fun visitSortClause(clause: SortClause) {
        register(clause)
        super.visitSortClause(clause)
    }

    override fun visitSortByItem(item: SortByItem) {
        register(item)
        super.visitSortByItem(item)
    }

    override fun visitIsExpression(expression: IsExpression) {
        register(expression)
        registerTypeSpecifier(expression.type)
        super.visitIsExpression(expression)
    }

    override fun visitAsExpression(expression: AsExpression) {
        register(expression)
        registerTypeSpecifier(expression.type)
        super.visitAsExpression(expression)
    }

    override fun visitCastExpression(expression: CastExpression) {
        register(expression)
        registerTypeSpecifier(expression.type)
        super.visitCastExpression(expression)
    }

    override fun visitUnsupportedExpression(expression: UnsupportedExpression) {
        register(expression)
        super.visitUnsupportedExpression(expression)
    }

    override fun visitLiteralExpression(expression: LiteralExpression) {
        register(expression)
        super.visitLiteralExpression(expression)
    }

    override fun visitLiteral(literal: Literal) {
        register(literal)
        super.visitLiteral(literal)
    }

    override fun visitQuantityLiteral(literal: QuantityLiteral) {
        register(literal)
        super.visitQuantityLiteral(literal)
    }

    override fun visitTupleLiteral(literal: TupleLiteral) {
        register(literal)
        super.visitTupleLiteral(literal)
    }

    override fun visitInstanceLiteral(literal: InstanceLiteral) {
        register(literal)
        literal.type?.let { registerTypeSpecifier(it) }
        super.visitInstanceLiteral(literal)
    }

    override fun visitIntervalLiteral(literal: IntervalLiteral) {
        register(literal)
        super.visitIntervalLiteral(literal)
    }

    override fun visitListLiteral(literal: ListLiteral) {
        register(literal)
        literal.elementType?.let { registerTypeSpecifier(it) }
        super.visitListLiteral(literal)
    }

    override fun visitRatioLiteral(literal: RatioLiteral) {
        register(literal)
        super.visitRatioLiteral(literal)
    }

    override fun visitCodeLiteral(literal: CodeLiteral) {
        register(literal)
        super.visitCodeLiteral(literal)
    }

    override fun visitConceptLiteral(literal: ConceptLiteral) {
        register(literal)
        super.visitConceptLiteral(literal)
    }

    override fun visitIntervalRelationExpression(expression: IntervalRelationExpression) {
        register(expression)
        super.visitIntervalRelationExpression(expression)
    }

    override fun visitIntervalOperatorPhrase(phrase: IntervalOperatorPhrase) {
        register(phrase)
        super.visitIntervalOperatorPhrase(phrase)
    }

    override fun visitConcurrentIntervalPhrase(phrase: ConcurrentIntervalPhrase) {
        register(phrase)
        super.visitConcurrentIntervalPhrase(phrase)
    }

    override fun visitIncludesIntervalPhrase(phrase: IncludesIntervalPhrase) {
        register(phrase)
        super.visitIncludesIntervalPhrase(phrase)
    }

    override fun visitIncludedInIntervalPhrase(phrase: IncludedInIntervalPhrase) {
        register(phrase)
        super.visitIncludedInIntervalPhrase(phrase)
    }

    override fun visitBeforeOrAfterIntervalPhrase(phrase: BeforeOrAfterIntervalPhrase) {
        register(phrase)
        super.visitBeforeOrAfterIntervalPhrase(phrase)
    }

    override fun visitWithinIntervalPhrase(phrase: WithinIntervalPhrase) {
        register(phrase)
        super.visitWithinIntervalPhrase(phrase)
    }

    override fun visitMeetsIntervalPhrase(phrase: MeetsIntervalPhrase) {
        register(phrase)
        super.visitMeetsIntervalPhrase(phrase)
    }

    override fun visitOverlapsIntervalPhrase(phrase: OverlapsIntervalPhrase) {
        register(phrase)
        super.visitOverlapsIntervalPhrase(phrase)
    }

    override fun visitStartsIntervalPhrase(phrase: StartsIntervalPhrase) {
        register(phrase)
        super.visitStartsIntervalPhrase(phrase)
    }

    override fun visitEndsIntervalPhrase(phrase: EndsIntervalPhrase) {
        register(phrase)
        super.visitEndsIntervalPhrase(phrase)
    }

    override fun visitUnsupportedIntervalPhrase(phrase: UnsupportedIntervalPhrase) {
        register(phrase)
        super.visitUnsupportedIntervalPhrase(phrase)
    }

    override fun visitQuantityOffset(offset: QuantityOffset) {
        register(offset)
        super.visitQuantityOffset(offset)
    }

    override fun visitTemporalRelationshipPhrase(phrase: TemporalRelationshipPhrase) {
        register(phrase)
        super.visitTemporalRelationshipPhrase(phrase)
    }

    private fun registerTypeSpecifier(typeSpecifier: TypeSpecifier) {
        register(typeSpecifier)
        when (typeSpecifier) {
            is NamedTypeSpecifier -> {}
            is ListTypeSpecifier -> registerTypeSpecifier(typeSpecifier.elementType)
            is IntervalTypeSpecifier -> registerTypeSpecifier(typeSpecifier.pointType)
            is TupleTypeSpecifier ->
                typeSpecifier.elements.forEach { registerTypeSpecifier(it.type) }
            is ChoiceTypeSpecifier ->
                typeSpecifier.choices.forEach { registerTypeSpecifier(it) }
        }
    }
}
