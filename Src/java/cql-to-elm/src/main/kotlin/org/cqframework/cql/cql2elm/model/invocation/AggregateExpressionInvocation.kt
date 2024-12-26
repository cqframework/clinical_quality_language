package org.cqframework.cql.cql2elm.model.invocation

import org.hl7.elm.r1.AggregateExpression
import org.hl7.elm.r1.Expression
import org.hl7.elm.r1.TypeSpecifier

class AggregateExpressionInvocation<A : AggregateExpression>(expression: A) :
    AbstractExpressionInvocation<A>(expression) {

    override var operands: List<Expression>
        get() = listOf(expression.source!!)
        set(operands) {
            require(operands.size == 1) { "Unary operator expected." }
            expression.source = operands[0]
        }

    override var signature: List<TypeSpecifier>
        get() = expression.signature
        set(signature) {
            expression.signature = signature.toMutableList()
        }
}
