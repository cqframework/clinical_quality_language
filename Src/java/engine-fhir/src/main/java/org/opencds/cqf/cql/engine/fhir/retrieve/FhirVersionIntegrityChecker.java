package org.opencds.cqf.cql.engine.fhir.retrieve;

import ca.uhn.fhir.context.FhirVersionEnum;
import org.opencds.cqf.cql.engine.fhir.exception.FhirVersionMisMatchException;

public interface FhirVersionIntegrityChecker {
    void validateFhirVersionIntegrity(FhirVersionEnum fhirVersion) throws FhirVersionMisMatchException;

    FhirVersionEnum getFhirVersion();
}
