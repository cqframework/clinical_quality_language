package org.opencds.cqf.cql.engine.runtime

import org.cqframework.cql.shared.JsOnlyExport

/** Represents a CQL Tuple value. */
@JsOnlyExport
class Tuple : StructuredValue() {
    override val typeAsString = "Tuple"

    override val elements: MutableMap<kotlin.String, Value?> = mutableMapOf()

    fun withElements(elements: MutableMap<kotlin.String, Value?>): Tuple {
        this.elements.clear()
        this.elements.putAll(elements)
        return this
    }
}
