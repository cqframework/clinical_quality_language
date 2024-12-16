package org.cqframework.cql.cql2elm.model.invocation

import org.hl7.elm.r1.Expression
import org.hl7.elm.r1.Split

class SplitInvocation(expression: Split) : OperatorExpressionInvocation<Split>(expression) {
    override var operands: List<Expression>
        get() = listOf(expression.stringToSplit, expression.separator)
        set(operands) {
            require(operands.size == 2) { "Split operator requires two operands." }
            expression.stringToSplit = operands[0]
            expression.separator = operands[1]
        }
}
