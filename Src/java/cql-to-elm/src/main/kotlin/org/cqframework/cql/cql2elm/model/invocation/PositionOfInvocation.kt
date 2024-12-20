package org.cqframework.cql.cql2elm.model.invocation

import org.hl7.elm.r1.Expression
import org.hl7.elm.r1.PositionOf

class PositionOfInvocation(expression: PositionOf) :
    OperatorExpressionInvocation<PositionOf>(expression) {
    override var operands: List<Expression>
        get() = listOf(expression.pattern, expression.string)
        set(operands) {
            require(operands.size == 2) { "PositionOf operator requires two operands." }
            expression.pattern = operands[0]
            expression.string = operands[1]
        }
}
