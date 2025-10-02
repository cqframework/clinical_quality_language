package org.opencds.cqf.cql.engine.fhir.retrieve

import ca.uhn.fhir.context.FhirVersionEnum
import org.opencds.cqf.cql.engine.fhir.exception.FhirVersionMisMatchException

interface FhirVersionIntegrityChecker {
    @Throws(FhirVersionMisMatchException::class)
    fun validateFhirVersionIntegrity(fhirVersion: FhirVersionEnum)

    val fhirVersion: FhirVersionEnum
}
