package org.cqframework.cql.cql2elm.model.invocation

import java.util.*
import org.hl7.elm.r1.Expression
import org.hl7.elm.r1.Message

class MessageInvocation(expression: Message) : OperatorExpressionInvocation<Message>(expression) {
    override var operands: List<Expression>
        get() =
            listOf(
                expression.source,
                expression.condition,
                expression.code,
                expression.severity,
                expression.message
            )
        set(operands) {
            require(operands.size == 5) { "Message operator requires five operands." }
            expression.source = operands[0]
            expression.condition = operands[1]
            expression.code = operands[2]
            expression.severity = operands[3]
            expression.message = operands[4]
        }
}
