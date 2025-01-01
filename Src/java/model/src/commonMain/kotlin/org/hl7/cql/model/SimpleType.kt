package org.hl7.cql.model

data class SimpleType(
    override val name: String,
    val base: DataType? = null,
    override var target: String? = null
) : BaseDataType(base), NamedType {

    constructor(name: String) : this(name, null, null)

    init {
        require(name.isNotEmpty()) { "name can not be empty" }
    }

    override val namespace: String
        get() {
            val qualifierIndex = name.indexOf('.')
            return if (qualifierIndex > 0) {
                name.substring(0, qualifierIndex)
            } else ""
        }

    override val simpleName: String
        get() {
            val qualifierIndex = name.indexOf('.')
            return if (qualifierIndex > 0) {
                name.substring(qualifierIndex + 1)
            } else name
        }

    override fun toString(): String = name

    override fun isCompatibleWith(other: DataType): Boolean {
        // The system type "Any" can be implicitly cast to any other type.
        return this == DataType.ANY || super.isCompatibleWith(other)
    }

    override val isGeneric: Boolean = false

    override fun isInstantiable(callType: DataType, context: InstantiationContext): Boolean {
        return when {
            this.isSuperTypeOf(callType) -> true
            else -> {
                val instantiableElements = context.getSimpleConversionTargets(callType)
                check(instantiableElements.size <= 1) {
                    "Ambiguous generic instantiation involving $callType to $instantiableElements"
                }

                instantiableElements.isNotEmpty()
            }
        }
    }

    override fun instantiate(context: InstantiationContext): DataType = this
}
