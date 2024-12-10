package org.hl7.cql.model

import java.util.*
import org.hl7.cql.model.DataType.Companion.ANY

data class ListType(val elementType: DataType) : BaseDataType() {
    override fun isSubTypeOf(other: DataType): Boolean {
        return if (other is ListType) {
            elementType.isSubTypeOf(other.elementType)
        } else {
            super.isSubTypeOf(other)
        }
    }

    override fun isSuperTypeOf(other: DataType): Boolean {
        return if (other is ListType) {
            elementType.isSuperTypeOf(other.elementType)
        } else {
            super.isSuperTypeOf(other)
        }
    }

    override fun toString(): String {
        return String.format(Locale.US, "list<%s>", elementType.toString())
    }

    override fun toLabel(): String {
        return String.format(Locale.US, "List of %s", elementType.toLabel())
    }

    override val isGeneric: Boolean
        get() = elementType.isGeneric

    override fun isInstantiable(callType: DataType, context: InstantiationContext): Boolean {
        return when (callType) {
            ANY -> elementType.isInstantiable(callType, context)
            is ListType -> elementType.isInstantiable(callType.elementType, context)
            else -> {
                val instantiableElements =
                    context.getListConversionTargets(callType).filter {
                        elementType.isInstantiable(it.elementType, context)
                    }
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
        return ListType(elementType.instantiate(context))
    }
}
