package org.cqframework.cql.cql2elm.model.invocation

import org.hl7.elm.r1.Expression
import org.hl7.elm.r1.InValueSet

class InValueSetInvocation(expression: InValueSet) :
    OperatorExpressionInvocation<InValueSet>(expression) {
    override var operands: List<Expression>
        get() = listOfNotNull(expression.code, expression.valuesetExpression)
        set(operands) {
            require(operands.size in 1..2) { "InValueSet operator requires one or two operands." }
            expression.code = operands[0]
            if (operands.size > 1) {
                expression.valuesetExpression = operands[1]
            }
        }
}
