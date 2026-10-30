package org.opencds.cqf.cql.engine.runtime

import org.cqframework.cql.shared.JsOnlyExport
import org.cqframework.cql.shared.QName

/**
 * Represents an instance of a non-system-defined class (instance of a named structured type defined
 * outside the System model), e.g. a FHIR.Patient.
 */
@JsOnlyExport
class ClassInstance(override val type: QName, override val elements: Map<kotlin.String, Value?>) :
    StructuredValue, NamedTypeValue {
    private val hash: Int by lazy { listOf(type, elements).hashCode() }

    override fun hashCode(): Int = hash

    override fun equals(other: Any?): kotlin.Boolean =
        this === other ||
            (other is ClassInstance && type == other.type && elements == other.elements)

    override fun toString(): kotlin.String = toStructuredValueString()
}
