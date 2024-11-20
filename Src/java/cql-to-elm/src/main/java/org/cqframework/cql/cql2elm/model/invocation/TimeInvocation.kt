package org.cqframework.cql.cql2elm.model.invocation

import java.util.*
import org.hl7.elm.r1.Expression
import org.hl7.elm.r1.Time

class TimeInvocation(expression: Time) : OperatorExpressionInvocation<Time>(expression) {
    override var operands: List<Expression>
        get() =
            listOfNotNull(
                expression.hour,
                expression.minute,
                expression.second,
                expression.millisecond
            )
        set(operands) {
            setTimeFieldsFromOperands(expression, operands)
        }

    companion object {
        @JvmStatic
        fun setTimeFieldsFromOperands(t: Time, operands: List<Expression>) {
            require(operands.size in 1..4) { "Time operator requires at one to four operands." }
            t.hour = operands[0]
            if (operands.size > 1) {
                t.minute = operands[1]
            }
            if (operands.size > 2) {
                t.second = operands[2]
            }
            if (operands.size > 3) {
                t.millisecond = operands[3]
            }
        }
    }
}
