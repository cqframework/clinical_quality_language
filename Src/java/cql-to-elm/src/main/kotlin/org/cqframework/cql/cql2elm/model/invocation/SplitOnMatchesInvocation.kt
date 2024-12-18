package org.cqframework.cql.cql2elm.model.invocation

import org.hl7.elm.r1.Expression
import org.hl7.elm.r1.SplitOnMatches

class SplitOnMatchesInvocation(expression: SplitOnMatches) :
    OperatorExpressionInvocation<SplitOnMatches>(expression) {
    override var operands: List<Expression>
        get() = listOf(expression.stringToSplit!!, expression.separatorPattern!!)
        set(operands) {
            require(operands.size == 2) { "SplitOnMatches operator requires two operands." }
            expression.stringToSplit = operands[0]
            expression.separatorPattern = operands[1]
        }
}
