package org.opencds.cqf.cql.engine.fhir.model

import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.context.FhirVersionEnum

class CachedR4FhirModelResolver : R4FhirModelResolver(FhirContext.forCached(FhirVersionEnum.R4))
