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
    override val locator: Locator = Locator.UNKNOWN,
) : UnaryExpression

@Serializable
@SerialName("cast")
data class CastExpression(
    override val operand: Expression,
    val type: TypeSpecifier,
    override val locator: Locator = Locator.UNKNOWN,
) : UnaryExpression

@Serializable
@SerialName("unknown")
data class UnsupportedExpression(
    val description: String,
    override val locator: Locator = Locator.UNKNOWN,
) : Expression
