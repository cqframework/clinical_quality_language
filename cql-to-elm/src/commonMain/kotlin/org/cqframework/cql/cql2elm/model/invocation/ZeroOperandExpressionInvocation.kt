package org.cqframework.cql.cql2elm.model.invocation

import org.hl7.elm.r1.Expression
import org.hl7.elm.r1.OperatorExpression

class ZeroOperandExpressionInvocation(expression: OperatorExpression) :
    OperatorExpressionInvocation<OperatorExpression>(expression) {
    override var operands: List<Expression>
        get() = emptyList()
        set(operands) {
            require(operands.isEmpty()) { "Zero operand operator expected." }
        }
}
