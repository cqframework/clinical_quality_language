package org.hl7.cql.model

import java.util.SortedSet

class TupleType private constructor(val elements: SortedSet<TupleTypeElement>) : BaseDataType() {

    constructor(elements: Iterable<TupleTypeElement>) : this(elements.sortedByName())

    override fun isSubTypeOf(other: DataType): Boolean {
        return if (other is TupleType) {
            elements.zipAll(other.elements) { a, b -> a.isSubTypeOf(b) }
        } else super.isSubTypeOf(other)
    }

    override fun isSuperTypeOf(other: DataType): Boolean {
        return if (other is TupleType) {
            elements.zipAll(other.elements) { a, b -> a.isSuperTypeOf(b) }
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
                elements.zipAll(callType.elements) { a, b ->
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
                    .sortedByName()
            )
        } else this
    }

    override fun equals(other: Any?): Boolean {
        return if (other is TupleType) {
            elements.zipAll(other.elements) { a, b -> a == b }
        } else false
    }

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }

    companion object {
        private fun Iterable<TupleTypeElement>.sortedByName(): SortedSet<TupleTypeElement> {
            return toSortedSet(compareBy { it.name })
        }

        private fun Collection<TupleTypeElement>.zipAll(
            other: Collection<TupleTypeElement>,
            predicate: (a: TupleTypeElement, b: TupleTypeElement) -> Boolean
        ): Boolean {
            return size == other.size && zip(other).all { predicate(it.first, it.second) }
        }
    }
}
