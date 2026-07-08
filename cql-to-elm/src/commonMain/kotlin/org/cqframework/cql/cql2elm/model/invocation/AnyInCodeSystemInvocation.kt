package org.cqframework.cql.cql2elm.model.invocation

import org.hl7.elm.r1.AnyInCodeSystem
import org.hl7.elm.r1.Expression

/** Created by Bryn on 9/12/2018. */
class AnyInCodeSystemInvocation(expression: AnyInCodeSystem) :
    OperatorExpressionInvocation<AnyInCodeSystem>(expression) {
    override var operands: List<Expression>
        get() = listOfNotNull(expression.codes, expression.codesystemExpression)
        set(operands) {
            require(operands.size in 1..2) {
                "AnyInCodeSystem operator requires one or two operands."
            }
            expression.codes = operands[0]
            if (operands.size > 1) {
                expression.codesystemExpression = operands[1]
            }
        }
}
