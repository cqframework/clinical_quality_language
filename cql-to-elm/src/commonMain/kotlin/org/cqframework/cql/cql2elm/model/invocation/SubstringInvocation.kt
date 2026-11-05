package org.cqframework.cql.cql2elm.model.invocation

import org.hl7.elm.r1.Expression
import org.hl7.elm.r1.Substring

class SubstringInvocation(expression: Substring) :
    OperatorExpressionInvocation<Substring>(expression) {
    override var operands: List<Expression>
        get() = listOfNotNull(expression.stringToSub, expression.startIndex, expression.length)
        @Suppress("MagicNumber")
        set(operands) {
            require(operands.size in 2..3) { "Substring operator requires two or three operands." }
            expression.stringToSub = operands[0]
            expression.startIndex = operands[1]
            if (operands.size > 2) {
                expression.length = operands[2]
            }
        }
}
