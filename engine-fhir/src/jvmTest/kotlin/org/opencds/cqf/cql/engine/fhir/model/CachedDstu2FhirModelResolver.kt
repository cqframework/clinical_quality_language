package org.opencds.cqf.cql.engine.fhir.model

import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.context.FhirVersionEnum

class CachedDstu2FhirModelResolver :
    Dstu2FhirModelResolver(FhirContext.forCached(FhirVersionEnum.DSTU2))
