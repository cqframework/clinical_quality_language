package org.opencds.cqf.cql.engine.runtime

import org.cqframework.cql.shared.QName

/**
 * Represents an instance of a non-system-defined class (instance of a named structured type defined
 * outside the System model), e.g. a FHIR.Patient.
 */
class ClassInstance(
    override val type: QName,
    override val elements: MutableMap<kotlin.String, Value?>,
) : StructuredValue(), NamedTypeValue {
    override fun toString(): kotlin.String {
        return toPrettyString(type.toString())
    }
}
