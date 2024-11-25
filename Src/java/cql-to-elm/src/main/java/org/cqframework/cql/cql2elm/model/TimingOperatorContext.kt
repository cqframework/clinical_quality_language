package org.cqframework.cql.cql2elm.model

import org.hl7.elm.r1.Expression

class TimingOperatorContext() {
    @JvmField var left: Expression? = null
    @JvmField var right: Expression? = null

    constructor(left: Expression, right: Expression) : this() {
        this.left = left
        this.right = right
    }
}
