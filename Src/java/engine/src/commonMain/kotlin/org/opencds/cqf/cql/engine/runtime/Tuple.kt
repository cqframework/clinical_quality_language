package org.opencds.cqf.cql.engine.runtime

/** Represents a CQL Tuple value. */
class Tuple : StructuredValue() {
    override val elements: MutableMap<String, Any?> = mutableMapOf()

    fun withElements(elements: MutableMap<String, Any?>): Tuple {
        this.elements.clear()
        this.elements.putAll(elements)
        return this
    }

    override fun toString(): String {
        return toPrettyString("Tuple")
    }
}
