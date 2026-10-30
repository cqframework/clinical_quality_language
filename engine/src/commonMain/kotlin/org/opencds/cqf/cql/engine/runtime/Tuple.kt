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

    private val hash: Int by lazy { elements.hashCode() }

    override fun hashCode(): Int = hash

    override fun equals(other: Any?): kotlin.Boolean =
        this === other || (other is Tuple && elements == other.elements)

    override fun toString(): kotlin.String = toStructuredValueString()
}
