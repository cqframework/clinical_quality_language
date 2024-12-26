package org.cqframework.cql.cql2elm.model.invocation

import org.hl7.elm.r1.BinaryExpression
import org.hl7.elm.r1.Expression

class BinaryExpressionInvocation<B : BinaryExpression>(expression: B) :
    OperatorExpressionInvocation<B>(expression) {
    override var operands: List<Expression>
        get() = expression.operand
        set(operands) {
            require(operands.size == 2) { "BinaryExpression requires two operands." }
            expression.operand = operands.toMutableList()
        }
}
