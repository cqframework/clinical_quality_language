package org.cqframework.cql.cql2elm.model.invocation

import org.cqframework.cql.cql2elm.model.OperatorResolution
import org.hl7.elm.r1.Expression
import org.hl7.elm.r1.FunctionRef
import org.hl7.elm.r1.TypeSpecifier

class FunctionRefInvocation(expression: FunctionRef) :
    AbstractExpressionInvocation<FunctionRef>(expression) {
    override var operands: List<Expression>
        get() = expression.operand
        set(operands) {
            expression.operand = operands.toMutableList()
        }

    override var signature: List<TypeSpecifier>
        get() = expression.signature
        set(signature) {
            expression.signature = signature.toMutableList()
        }

    override var resolution: OperatorResolution?
        get() = super.resolution
        set(resolution) {
            super.resolution = resolution
            if (resolution?.libraryName != expression.libraryName) {
                expression.libraryName = resolution?.libraryName
            }
        }
}
