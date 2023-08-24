package org.opencds.cqf.cql.engine.fhir.model;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.context.FhirVersionEnum;

public class CachedR5FhirModelResolver extends R5FhirModelResolver {
    public CachedR5FhirModelResolver() {
        super(FhirContext.forCached(FhirVersionEnum.R5));
    }
}
