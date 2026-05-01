package org.opencds.cqf.cql.engine.runtime

import kotlin.js.ExperimentalJsExport
import org.cqframework.cql.shared.JsOnlyExport
import org.cqframework.cql.shared.QName

/**
 * Represents an instance of a non-system-defined class (instance of a named structured type defined
 * outside the System model), e.g. a FHIR.Patient.
 */
@OptIn(ExperimentalJsExport::class)
@JsOnlyExport
class ClassInstance(
    override val type: QName,
    override val elements: MutableMap<kotlin.String, Value?>,
) : StructuredValue(), NamedTypeValue {
    override fun toString(): kotlin.String {
        return toPrettyString(type.toString())
    }
}
