package org.cqframework.cql.cql2elm.model.invocation

import org.hl7.elm.r1.Expression
import org.hl7.elm.r1.UnaryExpression

class UnaryExpressionInvocation<U : UnaryExpression>(expression: U) :
    OperatorExpressionInvocation<U>(expression) {
    override var operands: List<Expression>
        get() = listOf(expression.operand)
        set(operands) {
            require(operands.size == 1) { "Unary operator expected." }
            expression.operand = operands[0]
        }
}
