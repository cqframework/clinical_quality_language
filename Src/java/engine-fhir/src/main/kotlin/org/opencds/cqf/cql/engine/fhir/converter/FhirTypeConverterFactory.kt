package org.opencds.cqf.cql.engine.fhir.converter

import ca.uhn.fhir.context.FhirVersionEnum

class FhirTypeConverterFactory {
    /**
     * Creates a FHIR version specific FhirTypeConverter
     *
     * @param fhirVersionEnum the version of FHIR to create a converter for
     * @return a FhirTypeConverter
     * @throws IllegalArgumentException if the FHIR version specified is not supported
     */
    fun create(fhirVersionEnum: FhirVersionEnum): FhirTypeConverter {
        return when (fhirVersionEnum) {
            FhirVersionEnum.DSTU2_HL7ORG -> Dstu2FhirTypeConverter()
            FhirVersionEnum.DSTU3 -> Dstu3FhirTypeConverter()
            FhirVersionEnum.R4 -> R4FhirTypeConverter()
            FhirVersionEnum.R5 -> R5FhirTypeConverter()
            else ->
                throw IllegalArgumentException(
                    "Unsupported FHIR version for type conversion: $fhirVersionEnum"
                )
        }
    }
}
