package org.opencds.cqf.cql.engine.fhir.model;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.context.FhirVersionEnum;

public class CachedDstu3FhirModelResolver extends Dstu3FhirModelResolver {
    public CachedDstu3FhirModelResolver() {
        super(FhirContext.forCached(FhirVersionEnum.DSTU3));
    }
}
