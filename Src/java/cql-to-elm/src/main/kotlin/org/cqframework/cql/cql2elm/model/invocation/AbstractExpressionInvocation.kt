package org.cqframework.cql.cql2elm.model.invocation

import org.cqframework.cql.cql2elm.model.Invocation
import org.cqframework.cql.cql2elm.model.OperatorResolution
import org.cqframework.cql.cql2elm.tracking.Trackable.resultType
import org.hl7.cql.model.DataType
import org.hl7.elm.r1.Expression

/**
 * The AbstractExpressionInvocation can be used to more simply make invocations for classes that
 * only extend Expression.
 */
abstract class AbstractExpressionInvocation<E : Expression>(override val expression: E) :
    Invocation {

    override var resultType: DataType?
        get() = expression.resultType
        set(resultType) {
            expression.resultType = resultType
        }

    override var resolution: OperatorResolution? = null
}
