package org.cqframework.cql.cql2elm.model.invocation

import org.hl7.elm.r1.Expression
import org.hl7.elm.r1.IndexOf

class IndexOfInvocation(expression: IndexOf) : OperatorExpressionInvocation<IndexOf>(expression) {
    override var operands: List<Expression>
        get() = listOfNotNull(expression.source, expression.element)
        set(operands) {
            require(operands.size == 2) { "IndexOf operator requires two operands." }
            expression.source = operands[0]
            expression.element = operands[1]
        }
}
