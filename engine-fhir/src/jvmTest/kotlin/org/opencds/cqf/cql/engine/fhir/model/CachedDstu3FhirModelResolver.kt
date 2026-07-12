package org.opencds.cqf.cql.engine.fhir.model

import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.context.FhirVersionEnum

class CachedDstu3FhirModelResolver :
    Dstu3FhirModelResolver(FhirContext.forCached(FhirVersionEnum.DSTU3))
