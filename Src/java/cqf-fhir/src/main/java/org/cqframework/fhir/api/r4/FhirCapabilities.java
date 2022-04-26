package org.cqframework.fhir.api.r4;

import org.hl7.fhir.r4.model.CapabilityStatement;

/**
 * This interface provides capability discovery services for the Fhir platform API.
 * It provides an R4 version of the interface for the implementation of the Fhir
 * [capabilities](https://hl7.org/fhir/R4/http.html#capabilities) interaction.
 */
public interface FhirCapabilities extends org.cqframework.fhir.api.FhirCapabilities {
    /**
     * Returns a resource describing the capabilities of this FHIR service.
     * @return
     */
    CapabilityStatement capabilities();
}
