package org.hl7.cql.model

class TupleType private constructor(val elements: Set<TupleTypeElement>) : BaseDataType() {

    constructor(elements: Iterable<TupleTypeElement>) : this(elements.toSet())

    val sortedElements = elements.sortedWith(compareBy { it.name })

    override fun isSubTypeOf(other: DataType): Boolean {
        return if (other is TupleType) { sortedElements.zipAll(other.sortedElements) { a, b -> a.isSubTypeOf(b) }
        } else super.isSubTypeOf(other)
    }

    override fun isSuperTypeOf(other: DataType): Boolean {
        return if (other is TupleType) {
            sortedElements.zipAll(other.sortedElements) { a, b -> a.isSuperTypeOf(b) }
        } else super.isSuperTypeOf(other)
    }

    override fun toString(): String = elements.joinToString(",", "tuple{", "}")

    override fun toLabel(): String = elements.joinToString(", ", "tuple of ")

    override fun isCompatibleWith(other: DataType): Boolean {
        return if (other is ClassType) {
            this == other.tupleType
        } else super.isCompatibleWith(other)
    }

    override val isGeneric: Boolean = elements.any { it.type.isGeneric }

    override fun isInstantiable(callType: DataType, context: InstantiationContext): Boolean {
        // Call isInstantiable recursively to make sure that type parameters (if present) are bound
        return when (callType) {
            DataType.ANY -> elements.all { it.type.isInstantiable(callType, context) }
            is TupleType -> {
                sortedElements.zipAll(callType.sortedElements) { a, b ->
                    a.type.isInstantiable(b.type, context)
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
            )
        } else this
    }

    override fun equals(other: Any?): Boolean {
        return if (other is TupleType) {
            elements.zipAll(other.elements) { a, b -> a == b }
        } else false
    }

    override fun hashCode(): Int {
        var result = 13
        for (e in elements) {
            result = 37 * result + e.hashCode()
        }
        return result
    }

    companion object {
        private fun Collection<TupleTypeElement>.zipAll(
            other: Collection<TupleTypeElement>,
            predicate: (a: TupleTypeElement, b: TupleTypeElement) -> Boolean
        ): Boolean = size == other.size && zip(other).all { predicate(it.first, it.second) }
    }
}
