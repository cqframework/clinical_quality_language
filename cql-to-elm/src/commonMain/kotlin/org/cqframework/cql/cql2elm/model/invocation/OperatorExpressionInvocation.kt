package org.cqframework.cql.cql2elm.model.invocation

import kotlin.jvm.JvmSuppressWildcards
import org.hl7.elm.r1.OperatorExpression
import org.hl7.elm.r1.TypeSpecifier

/** Created by Bryn on 4/12/2018. */
abstract class OperatorExpressionInvocation<O : OperatorExpression>(expression: O) :
    AbstractExpressionInvocation<O>(expression) {
    override var signature: List<@JvmSuppressWildcards TypeSpecifier>
        get() = expression.signature
        set(signature) {
            expression.signature = signature.toMutableList()
        }
}
