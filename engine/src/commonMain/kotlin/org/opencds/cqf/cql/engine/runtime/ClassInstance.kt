package org.opencds.cqf.cql.engine.runtime

import org.cqframework.cql.shared.JsOnlyExport
import org.cqframework.cql.shared.QName

/**
 * Represents an instance of a non-system-defined class (instance of a named structured type defined
 * outside the System model), e.g. a FHIR.Patient.
 */
@JsOnlyExport
class ClassInstance(
    override val type: QName,
    override val elements: MutableMap<kotlin.String, Value?>,
) : StructuredValue(), NamedTypeValue {
    // An absent element and a null-valued element are the same thing for a class instance, so
    // null-valued entries are ignored when comparing and hashing.
    override fun equals(other: Any?): kotlin.Boolean {
        if (this === other) return true
        if (other !is ClassInstance) return false
        if (type != other.type) return false
        for ((key, value) in elements) {
            if (value != null && value != other.elements[key]) return false
        }
        for ((key, value) in other.elements) {
            if (value != null && elements[key] == null) return false
        }
        return true
    }

    override fun hashCode(): Int {
        var result = 0
        for ((key, value) in elements) {
            if (value != null) {
                result += key.hashCode() xor value.hashCode()
            }
        }
        return 31 * type.hashCode() + result
    }
}
