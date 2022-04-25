package org.cqframework.fhir.api;

import org.hl7.fhir.instance.model.api.IBaseBundle;

public interface FhirTransactions {
    IBaseBundle transaction(IBaseBundle transaction);
}
