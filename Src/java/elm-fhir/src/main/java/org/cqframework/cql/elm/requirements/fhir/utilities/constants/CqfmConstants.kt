package org.cqframework.cql.elm.requirements.fhir.utilities.constants

// constants defined in the Quality Measures IG: http://hl7.org/fhir/us/cqfmeasures
object CqfmConstants {
    // Extensions
    const val PARAMETERS_EXT_URL: String =
        "http://hl7.org/fhir/us/cqfmeasures/StructureDefinition/cqfm-parameter"
    const val DATA_REQUIREMENT_EXT_URL: String =
        "http://hl7.org/fhir/us/cqfmeasures/StructureDefinition/cqfm-dataRequirement"
    const val RELATED_REQUIREMENT_EXT_URL: String =
        "http://hl7.org/fhir/us/cqfmeasures/StructureDefinition/cqfm-relatedRequirement"
    const val DIRECT_REF_CODE_EXT_URL: String =
        "http://hl7.org/fhir/us/cqfmeasures/StructureDefinition/cqfm-directReferenceCode"
    const val LOGIC_DEFINITION_EXT_URL: String =
        "http://hl7.org/fhir/us/cqfmeasures/StructureDefinition/cqfm-logicDefinition"
    const val PERTINENCE_EXT_URL: String =
        "http://hl7.org/fhir/us/cqfmeasures/StructureDefinition/cqfm-pertinence"
    const val EFFECTIVE_DATA_REQS_EXT_URL: String =
        "http://hl7.org/fhir/us/cqfmeasures/StructureDefinition/cqfm-effectiveDataRequirements"

    // Profiles
    const val COMPUTABLE_MEASURE_PROFILE_URL: String =
        "http://hl7.org/fhir/us/cqfmeasures/StructureDefinition/computable-measure-cqfm"
}
