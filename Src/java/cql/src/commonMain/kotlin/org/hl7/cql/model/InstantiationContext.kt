package org.hl7.cql.model

interface InstantiationContext {
    fun isInstantiable(parameter: TypeParameter, callType: DataType): Boolean

    fun instantiate(parameter: TypeParameter): DataType

    fun getSimpleConversionTargets(callType: DataType): List<SimpleType>

    fun getIntervalConversionTargets(callType: DataType): List<IntervalType>

    fun getListConversionTargets(callType: DataType): List<ListType>
}
