package org.hl7.fhir.dstu2.model;

import ca.uhn.fhir.model.api.annotation.DatatypeDef;
import ca.uhn.fhir.model.primitive.UriDt;

@DatatypeDef(name = "uuid")
public class AnnotatedUuidType extends UriDt {
    private static final long serialVersionUID = 3L;
}
