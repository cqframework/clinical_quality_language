package org.opencds.cqf.cql.engine.fhir.model

import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.context.FhirVersionEnum

class CachedR5FhirModelResolver : R5FhirModelResolver(FhirContext.forCached(FhirVersionEnum.R5))
