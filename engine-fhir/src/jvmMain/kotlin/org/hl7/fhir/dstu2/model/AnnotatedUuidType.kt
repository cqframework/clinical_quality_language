package org.hl7.fhir.dstu2.model

import ca.uhn.fhir.model.api.annotation.DatatypeDef
import ca.uhn.fhir.model.primitive.UriDt

@DatatypeDef(name = "uuid") class AnnotatedUuidType : UriDt()
