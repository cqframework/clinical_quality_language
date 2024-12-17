package org.cqframework.cql.cql2elm.model.invocation

import org.cqframework.cql.cql2elm.model.OperatorResolution
import org.hl7.elm.r1.Expression
import org.hl7.elm.r1.FunctionRef
import org.hl7.elm.r1.TypeSpecifier

class FunctionRefInvocation(expression: FunctionRef) :
    AbstractExpressionInvocation<FunctionRef>(expression) {
    override var operands: List<Expression>
        get() = expression.operand as List<Expression>
        set(operands) {
            expression.operand = operands as MutableList<Expression?>
        }

    override var signature: List<TypeSpecifier>
        get() = expression.signature as List<TypeSpecifier>
        set(signature) {
            expression.signature = signature as MutableList<TypeSpecifier?>
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
