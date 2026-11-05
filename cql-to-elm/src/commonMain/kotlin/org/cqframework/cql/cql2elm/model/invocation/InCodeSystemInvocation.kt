package org.cqframework.cql.cql2elm.model.invocation

import org.hl7.elm.r1.Expression
import org.hl7.elm.r1.InCodeSystem

class InCodeSystemInvocation(expression: InCodeSystem) :
    OperatorExpressionInvocation<InCodeSystem>(expression) {
    override var operands: List<Expression>
        get() = listOfNotNull(expression.code, expression.codesystemExpression)
        set(operands) {
            require(operands.size in 1..2) { "InCodeSystem operator requires one or two operands." }

            expression.code = operands[0]
            if (operands.size > 1) {
                expression.codesystemExpression = operands[1]
            }
        }
}
