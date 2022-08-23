package org.cqframework.fhir.api;

import org.hl7.fhir.instance.model.api.IBaseConformance;

/**
 * This interface provides capability discovery services for the Fhir platform API.
 * It provides a version-independent interface for the implementation of the Fhir
 * [capabilities](https://hl7.org/fhir/http.html#capabilities) interaction.
 */
public interface FhirCapabilities {
    /**
     * Returns a resource describing the capabilities of this FHIR service.
     * @return
     */
    IBaseConformance capabilities();
}
