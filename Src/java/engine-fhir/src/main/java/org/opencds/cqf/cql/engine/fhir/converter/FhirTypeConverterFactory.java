package org.opencds.cqf.cql.engine.fhir.converter;

import ca.uhn.fhir.context.FhirVersionEnum;

public class FhirTypeConverterFactory {

    /**
     * Creates a FHIR version specific FhirTypeConverter
     * @param fhirVersionEnum the version of FHIR to create a converter for
     * @return a FhirTypeConverter
     * @throws IllegalArgumentException if the FHIR version specified is not supported
     */
    public FhirTypeConverter create(FhirVersionEnum fhirVersionEnum) {
        switch (fhirVersionEnum) {
            case DSTU2_HL7ORG:
                return new Dstu2FhirTypeConverter();
            case DSTU3:
                return new Dstu3FhirTypeConverter();
            case R4:
                return new R4FhirTypeConverter();
            case R5:
                return new R5FhirTypeConverter();
            default:
                throw new IllegalArgumentException(
                        String.format("Unsupported FHIR version for type conversion: %s", fhirVersionEnum));
        }
    }
}
