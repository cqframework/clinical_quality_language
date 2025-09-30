package org.cqframework.cql.elm.requirements.fhir.utilities.constants

object CrmiConstants {
    const val EFFECTIVE_DATA_REQUIREMENTS_EXT_URL: String =
        "http://hl7.org/fhir/uv/crmi/StructureDefinition/crmi-effectiveDataRequirements"
    const val EFFECTIVE_DATA_REQUIREMENTS_IDENTIFIER: String = "effective-data-requirements"

    const val SOFTWARE_SYSTEM_EXT_URL: String =
        "http://hl7.org/fhir/uv/crmi/StructureDefinition/crmi-softwaresystem"
    const val SOFTWARE_SYSTEM_DEVICE_PROFILE_URL: String =
        "http://hl7.org/fhir/uv/crmi/StructureDefinition/crmi-softwaresystemdevice"
    const val SOFTWARE_SYSTEM_DEVICE_TYPE_SYSTEM_URL: String =
        "http://terminology.hl7.org/CodeSystem/software-system-type"
}
