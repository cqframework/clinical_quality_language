package org.hl7.cql.model

@Suppress("TooManyFunctions")
data class TupleType
@JvmOverloads
constructor(val elements: MutableList<TupleTypeElement> = mutableListOf()) : BaseDataType() {

    fun addElement(element: TupleTypeElement) {
        elements.add(element)
    }

    fun addElements(elements: Collection<TupleTypeElement>) {
        this.elements.addAll(elements)
    }

    val sortedElements: List<TupleTypeElement>
        get() = elements.sortedWith { l, r -> l.name.compareTo(r.name) }

    override fun isSubTypeOf(other: DataType): Boolean {
        return when {
            other is TupleType ->
                sortedElements.size == other.sortedElements.size &&
                    sortedElements.zip(other.sortedElements).all { it.first.isSubTypeOf(it.second) }
            else -> super.isSubTypeOf(other)
        }
    }

    override fun isSuperTypeOf(other: DataType): Boolean {
        return when {
            other is TupleType ->
                sortedElements.size == other.sortedElements.size &&
                    sortedElements.zip(other.sortedElements).all {
                        it.first.isSuperTypeOf(it.second)
                    }
            else -> super.isSuperTypeOf(other)
        }
    }

    override fun toString(): String = elements.joinToString(",", "tuple{", "}")

    override fun toLabel(): String = elements.joinToString(", ", "tuple of ")

    override fun isCompatibleWith(other: DataType): Boolean {
        return if (other is ClassType) {
            this == other.tupleType
        } else super.isCompatibleWith(other)
    }

    override val isGeneric: Boolean
        get() = elements.any { it.type.isGeneric }

    override fun isInstantiable(callType: DataType, context: InstantiationContext): Boolean {
        // Call isInstantiable recursively to make sure that type parameters (if present) are bound
        return when (callType) {
            DataType.ANY -> elements.all { it.type.isInstantiable(callType, context) }
            is TupleType -> {
                sortedElements.size == callType.sortedElements.size &&
                    sortedElements.zip(callType.sortedElements).all {
                        it.first.name == it.second.name &&
                            it.first.type.isInstantiable(it.second.type, context)
                    }
            }
            else -> false
        }
    }

    override fun instantiate(context: InstantiationContext): DataType {
        return if (isGeneric) {
            TupleType(
                elements
                    .map { TupleTypeElement(it.name, it.type.instantiate(context), false) }
                    .toMutableList()
            )
        } else this
    }

    override fun hashCode(): Int {
        var result = 13
        for (e in elements) {
            result += (37 * e.hashCode())
        }

        return result
    }

    override fun equals(other: Any?): Boolean {
        return if (other is TupleType) {
            sortedElements.size == other.sortedElements.size &&
                sortedElements.zip(other.sortedElements).all { it.first == it.second }
        } else false
    }
}
