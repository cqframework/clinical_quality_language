package org.cqframework.cql.elm.requirements.fhir.utilities.constants;

// constants defined in the Quality Measures IG: http://hl7.org/fhir/us/cqfmeasures
public class CqfmConstants {

    private CqfmConstants() {}

    // Extensions
    public static final String PARAMETERS_EXT_URL =
            "http://hl7.org/fhir/us/cqfmeasures/StructureDefinition/cqfm-parameter";
    public static final String DATA_REQUIREMENT_EXT_URL =
            "http://hl7.org/fhir/us/cqfmeasures/StructureDefinition/cqfm-dataRequirement";
    public static final String RELATED_REQUIREMENT_EXT_URL =
            "http://hl7.org/fhir/us/cqfmeasures/StructureDefinition/cqfm-relatedRequirement";
    public static final String DIRECT_REF_CODE_EXT_URL =
            "http://hl7.org/fhir/us/cqfmeasures/StructureDefinition/cqfm-directReferenceCode";
    public static final String LOGIC_DEFINITION_EXT_URL =
            "http://hl7.org/fhir/us/cqfmeasures/StructureDefinition/cqfm-logicDefinition";
    public static final String PERTINENCE_EXT_URL =
            "http://hl7.org/fhir/us/cqfmeasures/StructureDefinition/cqfm-pertinence";
    public static final String EFFECTIVE_DATA_REQS_EXT_URL =
            "http://hl7.org/fhir/us/cqfmeasures/StructureDefinition/cqfm-effectiveDataRequirements";

    // Profiles
    public static final String COMPUTABLE_MEASURE_PROFILE_URL =
            "http://hl7.org/fhir/us/cqfmeasures/StructureDefinition/computable-measure-cqfm";
}
