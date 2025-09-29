package org.cqframework.fhir.api.stu3;

import org.hl7.fhir.dstu3.model.CapabilityStatement;

/**
 * This interface provides capability discovery services for the Fhir platform API.
 * It provides an STU3 version of the interface for the implementation of the Fhir
 * [capabilities](https://hl7.org/fhir/STU3/http.html#capabilities) interaction.
 */
public interface FhirCapabilities extends org.cqframework.fhir.api.FhirCapabilities {
    /**
     * Returns a resource describing the capabilities of this FHIR service.
     * @return
     */
    CapabilityStatement capabilities();
}
