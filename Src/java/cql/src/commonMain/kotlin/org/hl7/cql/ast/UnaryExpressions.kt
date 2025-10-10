package org.hl7.cql.ast

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("unary")
data class UnaryExpression(
    val operator: UnaryOperator,
    val operand: Expression,
    override val locator: Locator = Locator.UNKNOWN,
) : Expression

@Serializable
enum class UnaryOperator {
    @SerialName("negate") NEGATE,
    @SerialName("positive") POSITIVE,
    @SerialName("not") NOT,
    @SerialName("successor") SUCCESSOR,
    @SerialName("predecessor") PREDECESSOR,
}

