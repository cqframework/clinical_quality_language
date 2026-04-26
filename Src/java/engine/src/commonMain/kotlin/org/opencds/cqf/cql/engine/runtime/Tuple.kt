package org.opencds.cqf.cql.engine.runtime

/** Represents a CQL Tuple value. */
class Tuple : StructuredValue() {
    override val typeAsString = "Tuple"

    override val elements: MutableMap<kotlin.String, Value?> = mutableMapOf()

    fun withElements(elements: MutableMap<kotlin.String, Value?>): Tuple {
        this.elements.clear()
        this.elements.putAll(elements)
        return this
    }

    override fun toString(): kotlin.String {
        return toPrettyString("Tuple")
    }
}
