package org.cqframework.cql.cql2elm.model.invocation

import org.hl7.elm.r1.Expression
import org.hl7.elm.r1.Slice

/** Created by Bryn on 5/17/2017. */
class TakeInvocation(expression: Slice) : OperatorExpressionInvocation<Slice>(expression) {
    override var operands: List<Expression>
        get() = listOf(expression.source, expression.endIndex)
        set(operands) {
            require(operands.size == 2) { "Take operator requires two operands." }
            expression.source = operands[0]
            expression.endIndex = operands[1]
        }
}