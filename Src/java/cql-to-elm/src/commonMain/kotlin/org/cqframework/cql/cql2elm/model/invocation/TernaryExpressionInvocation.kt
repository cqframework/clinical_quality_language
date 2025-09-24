package org.cqframework.cql.cql2elm.model.invocation

import org.hl7.elm.r1.Expression
import org.hl7.elm.r1.TernaryExpression

class TernaryExpressionInvocation<T : TernaryExpression>(expression: T) :
    OperatorExpressionInvocation<T>(expression) {
    override var operands: List<Expression>
        get() = expression.operand
        set(operands) {
            expression.operand = operands.toMutableList()
        }
}
