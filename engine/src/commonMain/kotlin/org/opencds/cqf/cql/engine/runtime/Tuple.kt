package org.opencds.cqf.cql.engine.runtime

import kotlin.jvm.JvmOverloads
import org.cqframework.cql.shared.JsOnlyExport

/** Represents a CQL Tuple value. */
@JsOnlyExport
class Tuple
@JvmOverloads
constructor(override val elements: Map<kotlin.String, Value?> = emptyMap()) : StructuredValue {
    override val typeAsString = "Tuple"

    fun withElements(elements: Map<kotlin.String, Value?>): Tuple {
        return Tuple(elements)
    }

    private var hash = 0

    override fun hashCode(): Int {
        var result = hash
        if (result == 0) {
            result = elements.hashCode()
            hash = result
        }
        return result
    }

    override fun equals(other: Any?): kotlin.Boolean =
        this === other || (other is Tuple && elements == other.elements)

    override fun toString(): kotlin.String = toStructuredValueString()
}
