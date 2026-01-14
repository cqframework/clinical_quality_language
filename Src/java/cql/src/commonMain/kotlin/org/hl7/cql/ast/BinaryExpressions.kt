package org.hl7.cql.ast

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

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
@SerialName("binary")
data class OperatorBinaryExpression(
    val operator: BinaryOperator,
    override val left: Expression,
    override val right: Expression,
    override val locator: Locator = Locator.UNKNOWN,
) : BinaryExpression
