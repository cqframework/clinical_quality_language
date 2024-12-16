package org.cqframework.cql.cql2elm.model.invocation

import org.hl7.elm.r1.Expression
import org.hl7.elm.r1.Slice

/** Created by Bryn on 5/17/2017. */
class TailInvocation(expression: Slice) : OperatorExpressionInvocation<Slice>(expression) {
    override var operands: List<Expression>
        get() = listOf(expression.source)
        set(operands) {
            require(operands.size == 1) { "Unary operator expected." }
            expression.source = operands[0]
        }
}
