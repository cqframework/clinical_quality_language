package org.hl7.cql.model

import org.hl7.cql.model.DataType.Companion.ANY

data class ListType(val elementType: DataType) : BaseDataType() {
    override fun isSubTypeOf(other: DataType): Boolean {
        return if (other is ListType) {
            elementType.isSubTypeOf(other.elementType)
        } else super.isSubTypeOf(other)
    }

    override fun isSuperTypeOf(other: DataType): Boolean {
        return if (other is ListType) {
            elementType.isSuperTypeOf(other.elementType)
        } else super.isSuperTypeOf(other)
    }

    override fun toString(): String = "list<${elementType}>"

    override fun toLabel(): String = "List of ${elementType.toLabel()}"

    override val isGeneric: Boolean = elementType.isGeneric

    override fun isInstantiable(callType: DataType, context: InstantiationContext): Boolean {
        return when (callType) {
            ANY -> elementType.isInstantiable(callType, context)
            is ListType -> elementType.isInstantiable(callType.elementType, context)
            else -> {
                val instantiableElements =
                    context.getListConversionTargets(callType).filter {
                        elementType.isInstantiable(it.elementType, context)
                    }
                check(instantiableElements.size <= 1) {
                    "Ambiguous generic instantiation involving $callType to $instantiableElements"
                }

                instantiableElements.isNotEmpty()
            }
        }
    }

    override fun instantiate(context: InstantiationContext): DataType {
        return ListType(elementType.instantiate(context))
    }
}
