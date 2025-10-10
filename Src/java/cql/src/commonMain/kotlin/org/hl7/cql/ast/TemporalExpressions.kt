package org.hl7.cql.ast

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonClassDiscriminator

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
    override val operand: Expression,
    override val locator: Locator = Locator.UNKNOWN,
) : UnaryExpression

@Serializable
@SerialName("differenceOf")
data class DifferenceOfExpression(
    val precision: String,
    override val operand: Expression,
    override val locator: Locator = Locator.UNKNOWN,
) : UnaryExpression

@Serializable
@SerialName("width")
data class WidthExpression(
    override val operand: Expression,
    override val locator: Locator = Locator.UNKNOWN,
) : UnaryExpression

@Serializable
enum class ElementExtractorKind {
    @SerialName("singleton") SINGLETON,
    @SerialName("point") POINT,
}

@Serializable
@SerialName("elementExtractor")
data class ElementExtractorExpression(
    val kind: ElementExtractorKind,
    override val operand: Expression,
    override val locator: Locator = Locator.UNKNOWN,
) : UnaryExpression

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
    override val operand: Expression,
    val destinationType: TypeSpecifier? = null,
    val destinationUnit: String? = null,
    override val locator: Locator = Locator.UNKNOWN,
) : UnaryExpression

@Serializable
enum class TimeBoundaryKind {
    @SerialName("start") START,
    @SerialName("end") END,
}

@Serializable
@SerialName("timeBoundary")
data class TimeBoundaryExpression(
    val kind: TimeBoundaryKind,
    override val operand: Expression,
    override val locator: Locator = Locator.UNKNOWN,
) : UnaryExpression

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonClassDiscriminator("kind")
sealed interface IntervalOperatorPhrase : AstNode

@Serializable
enum class IntervalBoundarySelector {
    @SerialName("start") START,
    @SerialName("end") END,
    @SerialName("occurs") OCCURS,
}

@Serializable
enum class OffsetRelativeQualifier {
    @SerialName("orMore") OR_MORE,
    @SerialName("orLess") OR_LESS,
}

@Serializable
enum class ExclusiveRelativeQualifier {
    @SerialName("lessThan") LESS_THAN,
    @SerialName("moreThan") MORE_THAN,
}

@Serializable
enum class ConcurrentQualifier {
    @SerialName("as") AS,
    @SerialName("orBefore") OR_BEFORE,
    @SerialName("orAfter") OR_AFTER,
}

@Serializable
enum class TemporalRelationshipDirection {
    @SerialName("before") BEFORE,
    @SerialName("after") AFTER,
}

@Serializable
data class TemporalRelationshipPhrase(
    val direction: TemporalRelationshipDirection,
    val inclusive: Boolean,
    val leadingQualifier: ExclusiveRelativeQualifier? = null,
    override val locator: Locator = Locator.UNKNOWN,
) : AstNode

@Serializable
data class QuantityOffset(
    val quantity: QuantityLiteral,
    val offsetQualifier: OffsetRelativeQualifier? = null,
    val exclusiveQualifier: ExclusiveRelativeQualifier? = null,
    override val locator: Locator = Locator.UNKNOWN,
) : AstNode

@Serializable
@SerialName("intervalRelation")
data class IntervalRelationExpression(
    override val left: Expression,
    val phrase: IntervalOperatorPhrase,
    override val right: Expression,
    override val locator: Locator = Locator.UNKNOWN,
) : BinaryExpression

@Serializable
@SerialName("concurrent")
data class ConcurrentIntervalPhrase(
    val leftBoundary: IntervalBoundarySelector? = null,
    val precision: String? = null,
    val qualifier: ConcurrentQualifier = ConcurrentQualifier.AS,
    val rightBoundary: IntervalBoundarySelector? = null,
    override val locator: Locator = Locator.UNKNOWN,
) : IntervalOperatorPhrase

@Serializable
enum class InclusionVariant {
    @SerialName("during") DURING,
    @SerialName("includedIn") INCLUDED_IN,
}

@Serializable
@SerialName("includes")
data class IncludesIntervalPhrase(
    val proper: Boolean,
    val precision: String? = null,
    val rightBoundary: IntervalBoundarySelector? = null,
    override val locator: Locator = Locator.UNKNOWN,
) : IntervalOperatorPhrase

@Serializable
@SerialName("includedIn")
data class IncludedInIntervalPhrase(
    val leftBoundary: IntervalBoundarySelector? = null,
    val proper: Boolean,
    val variant: InclusionVariant,
    val precision: String? = null,
    override val locator: Locator = Locator.UNKNOWN,
) : IntervalOperatorPhrase

@Serializable
@SerialName("beforeOrAfter")
data class BeforeOrAfterIntervalPhrase(
    val leftBoundary: IntervalBoundarySelector? = null,
    val offset: QuantityOffset? = null,
    val relationship: TemporalRelationshipPhrase,
    val precision: String? = null,
    val rightBoundary: IntervalBoundarySelector? = null,
    override val locator: Locator = Locator.UNKNOWN,
) : IntervalOperatorPhrase

@Serializable
@SerialName("within")
data class WithinIntervalPhrase(
    val leftBoundary: IntervalBoundarySelector? = null,
    val proper: Boolean,
    val quantity: QuantityLiteral,
    val rightBoundary: IntervalBoundarySelector? = null,
    override val locator: Locator = Locator.UNKNOWN,
) : IntervalOperatorPhrase

@Serializable
@SerialName("meets")
data class MeetsIntervalPhrase(
    val direction: TemporalRelationshipDirection? = null,
    val precision: String? = null,
    override val locator: Locator = Locator.UNKNOWN,
) : IntervalOperatorPhrase

@Serializable
@SerialName("overlaps")
data class OverlapsIntervalPhrase(
    val direction: TemporalRelationshipDirection? = null,
    val precision: String? = null,
    override val locator: Locator = Locator.UNKNOWN,
) : IntervalOperatorPhrase

@Serializable
@SerialName("starts")
data class StartsIntervalPhrase(
    val precision: String? = null,
    override val locator: Locator = Locator.UNKNOWN,
) : IntervalOperatorPhrase

@Serializable
@SerialName("ends")
data class EndsIntervalPhrase(
    val precision: String? = null,
    override val locator: Locator = Locator.UNKNOWN,
) : IntervalOperatorPhrase

@Serializable
@SerialName("unsupportedInterval")
data class UnsupportedIntervalPhrase(
    val text: String,
    override val locator: Locator = Locator.UNKNOWN,
) : IntervalOperatorPhrase
