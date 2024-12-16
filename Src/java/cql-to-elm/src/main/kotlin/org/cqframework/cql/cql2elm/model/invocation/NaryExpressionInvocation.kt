package org.cqframework.cql.cql2elm.model.invocation

import org.hl7.elm.r1.Expression
import org.hl7.elm.r1.NaryExpression

class NaryExpressionInvocation(expression: NaryExpression) :
    OperatorExpressionInvocation<NaryExpression>(expression) {
    override var operands: List<Expression>
        get() = expression.operand
        set(operands) {
            expression.operand = operands
        }
}
