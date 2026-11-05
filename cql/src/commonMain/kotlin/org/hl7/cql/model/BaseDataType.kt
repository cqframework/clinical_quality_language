package org.hl7.cql.model

abstract class BaseDataType protected constructor(private val base: DataType? = null) : DataType {
    override val baseType
        get() = base ?: DataType.ANY

    override fun toLabel(): String = toString()

    override fun isSubTypeOf(other: DataType): Boolean {
        var currentType: DataType = this
        while (currentType != DataType.ANY) {
            if (currentType == other) {
                return true
            }
            currentType = currentType.baseType
        }

        return currentType == other
    }

    override fun isSuperTypeOf(other: DataType): Boolean {
        var currentType = other
        while (currentType != DataType.ANY) {
            if (this == currentType) {
                return true
            }
            currentType = currentType.baseType
        }

        return this == currentType
    }

    /**
     * @param other
     * @return The first supertype of this type that is also a supertype of other
     */
    override fun getCommonSuperTypeOf(other: DataType): DataType {
        var currentType: DataType = this
        while (currentType != DataType.ANY) {
            if (currentType.isSuperTypeOf(other)) {
                return currentType
            }
            currentType = currentType.baseType
        }

        return DataType.ANY
    }

    // Note that this is not how implicit/explicit conversions are defined, the notion of
    // type compatibility is used to support implicit casting, such as casting a "null"
    // literal to any other type, or casting a class to an equivalent tuple.
    override fun isCompatibleWith(other: DataType): Boolean {
        return when (other) {
            // Any data type is compatible with itself
            this -> true
            // A type is compatible with a choice type if it is a subtype of one of the choice types
            is ChoiceType -> other.types.any { this.isSubTypeOf(it) }
            else -> false
        }
    }
}
