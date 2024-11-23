package org.cqframework.cql.cql2elm.model

import org.hl7.cql.model.DataType

class CallContext(
    val libraryName: String?,
    val operatorName: String,
    private val allowPromotionAndDemotion: Boolean,
    private val allowFluent: Boolean,
    val mustResolve: Boolean,
    vararg signature: DataType
) {
    val signature: Signature

    fun isAllowPromotionAndDemotion(): Boolean {
        return allowPromotionAndDemotion
    }

    fun isAllowFluent(): Boolean {
        return allowFluent
    }

    init {
        require(operatorName.isNotEmpty()) { "operatorName is empty" }
        this.signature = Signature(*signature)
    }
}
