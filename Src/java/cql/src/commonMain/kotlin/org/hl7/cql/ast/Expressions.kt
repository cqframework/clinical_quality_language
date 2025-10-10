package org.hl7.cql.ast

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonClassDiscriminator

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonClassDiscriminator("kind")
sealed interface Expression : AstNode

@Serializable
@SerialName("identifier")
data class IdentifierExpression(
    val name: QualifiedIdentifier,
    override val locator: Locator = Locator.UNKNOWN,
) : Expression

@Serializable
@SerialName("literal")
data class LiteralExpression(
    val literal: Literal,
    override val locator: Locator = Locator.UNKNOWN,
) : Expression

@Serializable
@SerialName("exists")
data class ExistsExpression(
    val operand: Expression,
    override val locator: Locator = Locator.UNKNOWN,
) : Expression

@Serializable
enum class MembershipOperator {
    @SerialName("in") IN,
    @SerialName("contains") CONTAINS,
}

@Serializable
@SerialName("membership")
data class MembershipExpression(
    val operator: MembershipOperator,
    val precision: String? = null,
    val left: Expression,
    val right: Expression,
    override val locator: Locator = Locator.UNKNOWN,
) : Expression

@Serializable
@SerialName("between")
data class BetweenExpression(
    val input: Expression,
    val lower: Expression,
    val upper: Expression,
    val properly: Boolean = false,
    override val locator: Locator = Locator.UNKNOWN,
) : Expression

@Serializable
@SerialName("durationBetween")
data class DurationBetweenExpression(
    val precision: String,
    val lower: Expression,
    val upper: Expression,
    override val locator: Locator = Locator.UNKNOWN,
) : Expression

@Serializable
@SerialName("differenceBetween")
data class DifferenceBetweenExpression(
    val precision: String,
    val lower: Expression,
    val upper: Expression,
    override val locator: Locator = Locator.UNKNOWN,
) : Expression

@Serializable
@SerialName("durationOf")
data class DurationOfExpression(
    val precision: String,
    val operand: Expression,
    override val locator: Locator = Locator.UNKNOWN,
) : Expression

@Serializable
@SerialName("differenceOf")
data class DifferenceOfExpression(
    val precision: String,
    val operand: Expression,
    override val locator: Locator = Locator.UNKNOWN,
) : Expression

@Serializable
@SerialName("width")
data class WidthExpression(
    val operand: Expression,
    override val locator: Locator = Locator.UNKNOWN,
) : Expression

@Serializable
enum class ElementExtractorKind {
    @SerialName("singleton") SINGLETON,
    @SerialName("point") POINT,
}

@Serializable
@SerialName("elementExtractor")
data class ElementExtractorExpression(
    val kind: ElementExtractorKind,
    val operand: Expression,
    override val locator: Locator = Locator.UNKNOWN,
) : Expression

@Serializable
enum class TypeExtentKind {
    @SerialName("minimum") MINIMUM,
    @SerialName("maximum") MAXIMUM,
}

@Serializable
@SerialName("typeExtent")
data class TypeExtentExpression(
    val kind: TypeExtentKind,
    val type: NamedTypeSpecifier,
    override val locator: Locator = Locator.UNKNOWN,
) : Expression

@Serializable
@SerialName("conversion")
data class ConversionExpression(
    val operand: Expression,
    val destinationType: TypeSpecifier? = null,
    val destinationUnit: String? = null,
    override val locator: Locator = Locator.UNKNOWN,
) : Expression

@Serializable
enum class TimeBoundaryKind {
    @SerialName("start") START,
    @SerialName("end") END,
}

@Serializable
@SerialName("timeBoundary")
data class TimeBoundaryExpression(
    val kind: TimeBoundaryKind,
    val operand: Expression,
    override val locator: Locator = Locator.UNKNOWN,
) : Expression

@Serializable
@SerialName("functionCall")
data class FunctionCallExpression(
    val target: Expression?,
    val function: Identifier,
    val arguments: List<Expression>,
    override val locator: Locator = Locator.UNKNOWN,
) : Expression

@Serializable
@SerialName("propertyAccess")
data class PropertyAccessExpression(
    val target: Expression,
    val property: Identifier,
    override val locator: Locator = Locator.UNKNOWN,
) : Expression

@Serializable
@SerialName("binary")
data class BinaryExpression(
    val operator: BinaryOperator,
    val left: Expression,
    val right: Expression,
    override val locator: Locator = Locator.UNKNOWN,
) : Expression

@Serializable
@SerialName("unary")
data class UnaryExpression(
    val operator: UnaryOperator,
    val operand: Expression,
    override val locator: Locator = Locator.UNKNOWN,
) : Expression

@Serializable
@SerialName("if")
data class IfExpression(
    val condition: Expression,
   val thenBranch: Expression,
   val elseBranch: Expression,
   override val locator: Locator = Locator.UNKNOWN,
) : Expression

@Serializable
data class CaseItem(
    val condition: Expression,
    val result: Expression,
    override val locator: Locator = Locator.UNKNOWN,
) : AstNode

@Serializable
@SerialName("case")
data class CaseExpression(
    val comparand: Expression?,
    val cases: List<CaseItem>,
    val elseResult: Expression,
    override val locator: Locator = Locator.UNKNOWN,
) : Expression

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
enum class ListTransformKind {
    @SerialName("distinct") DISTINCT,
    @SerialName("flatten") FLATTEN,
}

@Serializable
@SerialName("listTransform")
data class ListTransformExpression(
    val kind: ListTransformKind,
    val operand: Expression,
    override val locator: Locator = Locator.UNKNOWN,
) : Expression

@Serializable
enum class ExpandCollapseKind {
    @SerialName("expand") EXPAND,
    @SerialName("collapse") COLLAPSE,
}

@Serializable
@SerialName("expandCollapse")
data class ExpandCollapseExpression(
    val kind: ExpandCollapseKind,
    val operand: Expression,
    val perPrecision: String? = null,
    val perExpression: Expression? = null,
    override val locator: Locator = Locator.UNKNOWN,
) : Expression

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

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonClassDiscriminator("kind")
sealed interface Literal : AstNode

@Serializable
@SerialName("string")
data class StringLiteral(val value: String, override val locator: Locator = Locator.UNKNOWN) :
    Literal

@Serializable
@SerialName("number")
data class NumberLiteral(
    val value: String,
    val isDecimal: Boolean,
    override val locator: Locator = Locator.UNKNOWN,
) : Literal

@Serializable
@SerialName("boolean")
data class BooleanLiteral(val value: Boolean, override val locator: Locator = Locator.UNKNOWN) :
    Literal

@Serializable
@SerialName("null")
data class NullLiteral(override val locator: Locator = Locator.UNKNOWN) : Literal

@Serializable
@SerialName("quantity")
data class QuantityLiteral(
    val value: String,
    val unit: String?,
    override val locator: Locator = Locator.UNKNOWN,
) : Literal

@Serializable
@SerialName("dateTime")
data class DateTimeLiteral(val text: String, override val locator: Locator = Locator.UNKNOWN) :
    Literal

@Serializable
@SerialName("time")
data class TimeLiteral(val text: String, override val locator: Locator = Locator.UNKNOWN) : Literal

@Serializable
@SerialName("tuple")
data class TupleLiteral(
    val elements: List<TupleElementValue>,
    override val locator: Locator = Locator.UNKNOWN,
) : Literal

@Serializable
data class TupleElementValue(
    val name: Identifier,
    val expression: Expression,
    override val locator: Locator = Locator.UNKNOWN,
) : AstNode

@Serializable
@SerialName("instance")
data class InstanceLiteral(
    val type: NamedTypeSpecifier? = null,
    val elements: List<TupleElementValue>,
    override val locator: Locator = Locator.UNKNOWN,
) : Literal

@Serializable
@SerialName("interval")
data class IntervalLiteral(
    val lower: Expression,
    val upper: Expression,
    val lowerClosed: Boolean,
    val upperClosed: Boolean,
    override val locator: Locator = Locator.UNKNOWN,
) : Literal

@Serializable
@SerialName("list")
data class ListLiteral(
    val elements: List<Expression>,
    val elementType: TypeSpecifier? = null,
    override val locator: Locator = Locator.UNKNOWN,
) : Literal

@Serializable
@SerialName("ratio")
data class RatioLiteral(
    val numerator: QuantityLiteral,
    val denominator: QuantityLiteral,
    override val locator: Locator = Locator.UNKNOWN,
) : Literal

@Serializable
@SerialName("code")
data class CodeLiteral(
    val code: String,
    val system: TerminologyReference,
    val display: String? = null,
    override val locator: Locator = Locator.UNKNOWN,
) : Literal

@Serializable
@SerialName("concept")
data class ConceptLiteral(
    val codes: List<CodeLiteral>,
    val display: String? = null,
    override val locator: Locator = Locator.UNKNOWN,
) : Literal

@Serializable
@SerialName("is")
data class IsExpression(
    val operand: Expression,
    val type: TypeSpecifier,
    val negated: Boolean = false,
    override val locator: Locator = Locator.UNKNOWN,
) : Expression

@Serializable
@SerialName("as")
data class AsExpression(
    val operand: Expression,
    val type: TypeSpecifier,
    override val locator: Locator = Locator.UNKNOWN,
) : Expression

@Serializable
@SerialName("cast")
data class CastExpression(
    val operand: Expression,
    val type: TypeSpecifier,
    override val locator: Locator = Locator.UNKNOWN,
) : Expression

@Serializable
@SerialName("unknown")
data class UnsupportedExpression(
    val description: String,
    override val locator: Locator = Locator.UNKNOWN,
) : Expression

@Serializable
enum class BinaryOperator {
    @SerialName("add") ADD,
    @SerialName("subtract") SUBTRACT,
    @SerialName("multiply") MULTIPLY,
    @SerialName("divide") DIVIDE,
    @SerialName("modulo") MODULO,
    @SerialName("power") POWER,
    @SerialName("concat") CONCAT,
    @SerialName("equals") EQUALS,
    @SerialName("notEquals") NOT_EQUALS,
    @SerialName("equivalent") EQUIVALENT,
    @SerialName("notEquivalent") NOT_EQUIVALENT,
    @SerialName("lessThan") LT,
    @SerialName("lessOrEqual") LTE,
    @SerialName("greaterThan") GT,
    @SerialName("greaterOrEqual") GTE,
    @SerialName("and") AND,
    @SerialName("or") OR,
    @SerialName("xor") XOR,
    @SerialName("implies") IMPLIES,
    @SerialName("union") UNION,
    @SerialName("intersect") INTERSECT,
    @SerialName("except") EXCEPT,
}

@Serializable
enum class UnaryOperator {
    @SerialName("negate") NEGATE,
    @SerialName("positive") POSITIVE,
    @SerialName("not") NOT,
    @SerialName("successor") SUCCESSOR,
    @SerialName("predecessor") PREDECESSOR,
}
