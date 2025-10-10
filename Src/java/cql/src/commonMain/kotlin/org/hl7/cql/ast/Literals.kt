package org.hl7.cql.ast

import kotlinx.serialization.Contextual
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonClassDiscriminator
import org.cqframework.cql.shared.BigDecimal

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonClassDiscriminator("kind")
sealed interface Literal : AstNode

@Serializable
@SerialName("string")
data class StringLiteral(val value: String, override val locator: Locator = Locator.UNKNOWN) :
    Literal

@Serializable
@SerialName("long")
data class LongLiteral(val value: Long, override val locator: Locator = Locator.UNKNOWN) : Literal

@Serializable
@SerialName("long")
data class IntLiteral(val value: Int, override val locator: Locator = Locator.UNKNOWN) : Literal

@Serializable
@SerialName("long")
data class DecimalLiteral(
    @Contextual val value: BigDecimal,
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
@SerialName("literal")
data class LiteralExpression(
    val literal: Literal,
    override val locator: Locator = Locator.UNKNOWN,
) : Expression
