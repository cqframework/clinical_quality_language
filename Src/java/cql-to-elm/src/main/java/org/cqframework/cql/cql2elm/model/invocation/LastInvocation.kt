package org.cqframework.cql.cql2elm.model.invocation

import org.hl7.elm.r1.Expression
import org.hl7.elm.r1.Last

class LastInvocation(expression: Last) : OperatorExpressionInvocation<Last>(expression) {
    override var operands: List<Expression>
        get() = listOf(expression.source!!)
        set(operands) {
            require(operands.size == 1) { "Unary operator expected." }
            expression.source = operands[0]
        }
}
