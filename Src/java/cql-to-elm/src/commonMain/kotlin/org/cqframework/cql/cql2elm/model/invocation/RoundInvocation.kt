package org.cqframework.cql.cql2elm.model.invocation

import org.hl7.elm.r1.Expression
import org.hl7.elm.r1.Round

class RoundInvocation(expression: Round) : OperatorExpressionInvocation<Round>(expression) {
    override var operands: List<Expression>
        get() = listOfNotNull(expression.operand, expression.precision)
        set(operands) {
            require(operands.size in 1..2) { "Round operator requires one or two operands." }
            expression.operand = operands[0]
            if (operands.size > 1) {
                expression.precision = operands[1]
            }
        }
}
