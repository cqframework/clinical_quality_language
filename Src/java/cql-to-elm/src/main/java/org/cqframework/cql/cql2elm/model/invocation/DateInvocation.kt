package org.cqframework.cql.cql2elm.model.invocation

import java.util.*
import org.hl7.elm.r1.Date
import org.hl7.elm.r1.Expression

class DateInvocation(expression: Date) : OperatorExpressionInvocation<Date>(expression) {
    override var operands: List<Expression>
        get() = listOfNotNull(expression.year, expression.month, expression.day)
        set(operands) {
            setDateFieldsFromOperands(expression, operands)
        }

    companion object {
        @JvmStatic
        fun setDateFieldsFromOperands(dt: Date, operands: List<Expression>) {
            require(operands.size in 1..3) { "Date operator requires one to three operands." }
            dt.year = operands[0]
            if (operands.size > 1) {
                dt.month = operands[1]
            }
            if (operands.size > 2) {
                dt.day = operands[2]
            }
        }
    }
}
