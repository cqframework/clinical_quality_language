package org.hl7.cql.model

interface DataType {
    val baseType: DataType

    fun toLabel(): String

    fun isSubTypeOf(other: DataType): Boolean

    fun isSuperTypeOf(other: DataType): Boolean

    fun getCommonSuperTypeOf(other: DataType): DataType

    // Note that this is not how implicit/explicit conversions are defined, the notion of
    // type compatibility is used to support implicit casting, such as casting a "null"
    // literal to any other type, or casting a class to an equivalent tuple.
    fun isCompatibleWith(other: DataType): Boolean

    val isGeneric: Boolean

    fun isInstantiable(callType: DataType, context: InstantiationContext): Boolean

    fun instantiate(context: InstantiationContext): DataType

    companion object {
      val ANY: SimpleType = SimpleType("System.Any")
    }
}
