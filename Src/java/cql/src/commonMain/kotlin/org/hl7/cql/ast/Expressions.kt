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
sealed interface UnaryExpression : Expression {
    val operand: Expression
}

@Serializable
sealed interface BinaryExpression : Expression {
    val left: Expression
    val right: Expression
}

@Serializable
@SerialName("identifier")
data class IdentifierExpression(
    val name: QualifiedIdentifier,
    override val locator: Locator = Locator.UNKNOWN,
) : Expression

@Serializable
@SerialName("externalConstant")
data class ExternalConstantExpression(
    val name: String,
    override val locator: Locator = Locator.UNKNOWN,
) : Expression

@Serializable
@SerialName("exists")
data class ExistsExpression(
    override val operand: Expression,
    override val locator: Locator = Locator.UNKNOWN,
) : UnaryExpression

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
    override val left: Expression,
    override val right: Expression,
    override val locator: Locator = Locator.UNKNOWN,
) : BinaryExpression

@Serializable
enum class ListTransformKind {
    @SerialName("distinct") DISTINCT,
    @SerialName("flatten") FLATTEN,
}

@Serializable
@SerialName("listTransform")
data class ListTransformExpression(
    val listTransformKind: ListTransformKind,
    override val operand: Expression,
    override val locator: Locator = Locator.UNKNOWN,
) : UnaryExpression

@Serializable
enum class ExpandCollapseKind {
    @SerialName("expand") EXPAND,
    @SerialName("collapse") COLLAPSE,
}

@Serializable
@SerialName("expandCollapse")
data class ExpandCollapseExpression(
    val expandCollapseKind: ExpandCollapseKind,
    override val operand: Expression,
    val perPrecision: String? = null,
    val perExpression: Expression? = null,
    override val locator: Locator = Locator.UNKNOWN,
) : UnaryExpression

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
@SerialName("index")
data class IndexExpression(
    val target: Expression,
    val index: Expression,
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

/**
 * A constructed interval with expression-valued bounds and closed flags. Unlike [IntervalLiteral]
 * (which represents the CQL `Interval[x, y]` syntax with static closed/open brackets), this node is
 * produced by the lowering phase for runtime interval construction (e.g., Interval<Any> expansion
 * where closed flags come from Property access on the source interval).
 */
@Serializable
@SerialName("intervalExpression")
data class IntervalExpression(
    val low: Expression,
    val high: Expression,
    val lowClosedExpression: Expression,
    val highClosedExpression: Expression,
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
data class TupleElementValue(
    val name: Identifier,
    val expression: Expression,
    override val locator: Locator = Locator.UNKNOWN,
) : AstNode

@Serializable
@SerialName("is")
data class IsExpression(
    override val operand: Expression,
    val type: TypeSpecifier,
    val negated: Boolean = false,
    override val locator: Locator = Locator.UNKNOWN,
) : UnaryExpression

@Serializable
@SerialName("as")
data class AsExpression(
    override val operand: Expression,
    val type: TypeSpecifier,
    /**
     * When true, this is an implicitly-inserted cast (e.g., null wrapping in list/interval
     * contexts). Implicit casts serialize to `asType` (no `strict` field) in ELM, matching the
     * legacy translator's behavior for internally generated casts. User-written `x as T`
     * expressions are explicit (`implicit = false`) and serialize to `asTypeSpecifier` + `strict =
     * false`.
     */
    val implicit: Boolean = false,
    override val locator: Locator = Locator.UNKNOWN,
) : UnaryExpression

@Serializable
@SerialName("cast")
data class CastExpression(
    override val operand: Expression,
    val type: TypeSpecifier,
    override val locator: Locator = Locator.UNKNOWN,
) : UnaryExpression

/**
 * Represents a boolean test expression: `x is null`, `x is true`, `x is false`, and their negated
 * variants `x is not null`, `x is not true`, `x is not false`.
 *
 * This is distinct from equality comparison (`x = null`) which uses [OperatorBinaryExpression].
 */
@Serializable
enum class BooleanTestKind {
    @SerialName("isNull") IS_NULL,
    @SerialName("isTrue") IS_TRUE,
    @SerialName("isFalse") IS_FALSE,
}

@Serializable
@SerialName("booleanTest")
data class BooleanTestExpression(
    val kind: BooleanTestKind,
    val negated: Boolean,
    override val operand: Expression,
    override val locator: Locator = Locator.UNKNOWN,
) : UnaryExpression

@Serializable
@SerialName("unknown")
data class UnsupportedExpression(
    val description: String,
    override val locator: Locator = Locator.UNKNOWN,
) : Expression
