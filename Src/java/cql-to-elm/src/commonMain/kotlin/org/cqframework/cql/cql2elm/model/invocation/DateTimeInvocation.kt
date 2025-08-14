package org.cqframework.cql.cql2elm.model.invocation

import kotlin.jvm.JvmStatic
import org.hl7.elm.r1.DateTime
import org.hl7.elm.r1.Expression

class DateTimeInvocation(expression: DateTime) :
    OperatorExpressionInvocation<DateTime>(expression) {
    override var operands: List<Expression>
        get() =
            listOfNotNull(
                expression.year,
                expression.month,
                expression.day,
                expression.hour,
                expression.minute,
                expression.second,
                expression.millisecond,
                expression.timezoneOffset
            )
        set(operands) {
            setDateTimeFieldsFromOperands(expression, operands)
        }

    @Suppress("MagicNumber")
    companion object {
        @JvmStatic
        fun setDateTimeFieldsFromOperands(dt: DateTime, operands: List<Expression>) {
            require(operands.size in 1..8) { "DateTime operator requires one to eight operands." }

            dt.year = operands[0]
            if (operands.size > 1) {
                dt.month = operands[1]
            }
            if (operands.size > 2) {
                dt.day = operands[2]
            }
            if (operands.size > 3) {
                dt.hour = operands[3]
            }
            if (operands.size > 4) {
                dt.minute = operands[4]
            }
            if (operands.size > 5) {
                dt.second = operands[5]
            }
            if (operands.size > 6) {
                dt.millisecond = operands[6]
            }
            if (operands.size > 7) {
                dt.timezoneOffset = operands[7]
            }
        }
    }
}
