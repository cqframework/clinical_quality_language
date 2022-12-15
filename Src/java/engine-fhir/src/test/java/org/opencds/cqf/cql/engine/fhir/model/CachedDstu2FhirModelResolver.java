package org.opencds.cqf.cql.engine.fhir.model;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.context.FhirVersionEnum;

public class CachedDstu2FhirModelResolver extends Dstu2FhirModelResolver {
    public CachedDstu2FhirModelResolver() {
        super(FhirContext.forCached(FhirVersionEnum.DSTU2));
    }
}
