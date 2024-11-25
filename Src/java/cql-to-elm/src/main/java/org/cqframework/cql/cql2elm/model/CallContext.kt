package org.cqframework.cql.cql2elm.model

import org.hl7.cql.model.DataType

class CallContext(
    val libraryName: String?,
    val operatorName: String,
    val allowPromotionAndDemotion: Boolean,
    val allowFluent: Boolean,
    val mustResolve: Boolean,
    operandTypes: List<DataType>
) {
    constructor(
    libraryName: String?,
    operatorName: String,
    allowPromotionAndDemotion: Boolean,
    allowFluent: Boolean,
    mustResolve: Boolean,
    vararg operandTypes: DataType
    ) : this(libraryName, operatorName, allowPromotionAndDemotion, allowFluent, mustResolve, operandTypes.toList())

    val signature: Signature
    init {
        require(operatorName.isNotEmpty()) { "operatorName is empty" }
        this.signature = Signature(operandTypes)
    }
}
