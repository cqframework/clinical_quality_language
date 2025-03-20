package org.hl7.cql.model

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
            DataType.ANY -> elementType.isInstantiable(callType, context)
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

    @Suppress("ForbiddenComment")
    // TODO: Remove hashCode and equals. Everything works without these methods but the compiled ELM
    // is different because [org.cqframework.cql.cql2elm.LibraryBuilder.normalizeListTypes] returns
    // the choice options in a different order.
    override fun hashCode(): Int {
        return 67 * elementType.hashCode()
    }

    override fun equals(o: Any?): Boolean {
        if (o is ListType) {
            val (elementType1) = o
            return elementType == elementType1
        }
        return false
    }
}
