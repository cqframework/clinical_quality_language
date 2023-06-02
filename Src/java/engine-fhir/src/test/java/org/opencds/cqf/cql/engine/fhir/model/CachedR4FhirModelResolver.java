package org.opencds.cqf.cql.engine.fhir.model;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.context.FhirVersionEnum;

public class CachedR4FhirModelResolver extends R4FhirModelResolver {
    public CachedR4FhirModelResolver() {
        super(FhirContext.forCached(FhirVersionEnum.R4));
    }
}
