package org.hl7.cql.ast

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("unary")
data class OperatorUnaryExpression(
    val operator: UnaryOperator,
    override val operand: Expression,
    override val locator: Locator = Locator.UNKNOWN,
) : UnaryExpression

@Serializable
enum class UnaryOperator {
    @SerialName("negate") NEGATE,
    @SerialName("positive") POSITIVE,
    @SerialName("not") NOT,
    @SerialName("successor") SUCCESSOR,
    @SerialName("predecessor") PREDECESSOR,
}
