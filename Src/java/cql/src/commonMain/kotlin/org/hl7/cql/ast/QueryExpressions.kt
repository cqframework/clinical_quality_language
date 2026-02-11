package org.hl7.cql.ast

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonClassDiscriminator

@Serializable
@SerialName("query")
data class QueryExpression(
    val sources: List<AliasedQuerySource>,
    val lets: List<LetClauseItem>,
    val inclusions: List<QueryInclusionClause>,
    val where: Expression? = null,
    val aggregate: AggregateClause? = null,
    val result: ReturnClause? = null,
    val sort: SortClause? = null,
    override val locator: Locator = Locator.UNKNOWN,
) : Expression

@Serializable
data class AliasedQuerySource(
    val source: QuerySource,
    val alias: Identifier,
    override val locator: Locator = Locator.UNKNOWN,
) : AstNode

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonClassDiscriminator("kind")
sealed interface QuerySource : AstNode

@Serializable
@SerialName("retrieve")
data class RetrieveExpression(
    val typeSpecifier: NamedTypeSpecifier,
    val terminology: TerminologyRestriction? = null,
    val context: QualifiedIdentifier? = null,
    val codePath: QualifiedIdentifier? = null,
    val comparator: TerminologyComparator? = null,
    override val locator: Locator = Locator.UNKNOWN,
) : QuerySource, Expression

@Serializable
data class TerminologyRestriction(
    val terminology: Expression,
    override val locator: Locator = Locator.UNKNOWN,
) : AstNode

@Serializable
enum class TerminologyComparator {
    @SerialName("in") IN,
    @SerialName("equals") EQUALS,
    @SerialName("equivalent") EQUIVALENT,
}

@Serializable
@SerialName("expressionSource")
data class ExpressionQuerySource(
    val expression: Expression,
    override val locator: Locator = Locator.UNKNOWN,
) : QuerySource

@Serializable
data class LetClauseItem(
    val identifier: Identifier,
    val expression: Expression,
    override val locator: Locator = Locator.UNKNOWN,
) : AstNode

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonClassDiscriminator("kind")
sealed interface QueryInclusionClause : AstNode

@Serializable
@SerialName("with")
data class WithClause(
    val source: AliasedQuerySource,
    val condition: Expression,
    override val locator: Locator = Locator.UNKNOWN,
) : QueryInclusionClause

@Serializable
@SerialName("without")
data class WithoutClause(
    val source: AliasedQuerySource,
    val condition: Expression,
    override val locator: Locator = Locator.UNKNOWN,
) : QueryInclusionClause

@Serializable
data class AggregateClause(
    val distinct: Boolean = false,
    val identifier: Identifier,
    val starting: Expression? = null,
    val expression: Expression,
    override val locator: Locator = Locator.UNKNOWN,
) : AstNode

@Serializable
data class ReturnClause(
    val all: Boolean = false,
    val distinct: Boolean = false,
    val expression: Expression,
    override val locator: Locator = Locator.UNKNOWN,
) : AstNode

@Serializable
data class SortClause(
    val items: List<SortByItem>,
    override val locator: Locator = Locator.UNKNOWN,
) : AstNode

@Serializable
data class SortByItem(
    val expression: Expression,
    val direction: SortDirection = SortDirection.ASCENDING,
    override val locator: Locator = Locator.UNKNOWN,
) : AstNode

@Serializable
enum class SortDirection {
    @SerialName("asc") ASCENDING,
    @SerialName("desc") DESCENDING,
}
