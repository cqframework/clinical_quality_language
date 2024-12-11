package org.hl7.cql.model

import java.util.*

data class SimpleType
@JvmOverloads
constructor(
    override val name: String,
    private val base: DataType? = null,
    override var target: String? = null
) : BaseDataType(base), NamedType {

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
                require(instantiableElements.size <= 1) {
                    String.format(
                        Locale.US,
                        "Ambiguous generic instantiation involving %s to %s.",
                        callType.toString(),
                        instantiableElements.toString()
                    )
                }

                instantiableElements.isNotEmpty()
            }
        }
    }

    override fun instantiate(context: InstantiationContext): DataType {
        return this
    }
}
