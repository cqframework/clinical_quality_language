package org.opencds.cqf.cql.engine.fhir.model

import org.cqframework.cql.shared.QName
import org.opencds.cqf.cql.engine.elm.executing.EquivalentEvaluator
import org.opencds.cqf.cql.engine.fhir.fhirModelNamespaceUri
import org.opencds.cqf.cql.engine.model.ModelResolver
import org.opencds.cqf.cql.engine.runtime.Boolean
import org.opencds.cqf.cql.engine.runtime.ClassInstance
import org.opencds.cqf.cql.engine.runtime.Value

abstract class BaseFhirModelResolver : ModelResolver {
    override fun objectEquivalent(
        left: ClassInstance,
        right: ClassInstance,
        equivalent: (l: Value?, r: Value?) -> Boolean,
    ): Boolean {
        // "id" elements are excluded from equivalence checking for instances of FHIR.Resource and
        // FHIR.Element (see https://hl7.org/fhir/fhirpath.html#changes)
        if (
            `is`(left.type.getLocalPart(), QName(fhirModelNamespaceUri, "Resource")) == true ||
                `is`(left.type.getLocalPart(), QName(fhirModelNamespaceUri, "Element")) == true
        ) {
            return EquivalentEvaluator.structuredValueElementsEquivalent(
                left.elements.filterKeys { it != "id" },
                right.elements.filterKeys { it != "id" },
                equivalent,
            )
        }

        return EquivalentEvaluator.structuredValueElementsEquivalent(
            left.elements,
            right.elements,
            equivalent,
        )
    }
}
