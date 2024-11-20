package org.cqframework.cql.cql2elm.model.invocation

import java.util.*
import org.hl7.elm.r1.Combine
import org.hl7.elm.r1.Expression

class CombineInvocation(expression: Combine) : OperatorExpressionInvocation<Combine>(expression) {
    override var operands: List<Expression>
        get() = listOfNotNull(expression.source, expression.separator)
        set(operands) {
            require(operands.size in 1..2) { "Combine operator requires one or two operands." }

            expression.source = operands[0]
            if (operands.size > 1) {
                expression.separator = operands[1]
            }
        }
}
