package org.cqframework.cql.elm.requirements.fhir.utilities

import org.cqframework.cql.elm.requirements.fhir.utilities.constants.CqfConstants
import org.cqframework.cql.elm.requirements.fhir.utilities.constants.CqfmConstants

class SpecificationSupport
@JvmOverloads
constructor(val specificationLevel: SpecificationLevel = SpecificationLevel.US_QM_STU4) {
    val directReferenceCodeExtensionUrl: String
        get() {
            return when (specificationLevel) {
                SpecificationLevel.US_QM_STU4 -> CqfmConstants.DIRECT_REF_CODE_EXT_URL
                SpecificationLevel.US_QM_STU5,
                SpecificationLevel.QM_STU_1,
                SpecificationLevel.CRMI,
                SpecificationLevel.CPG -> CqfConstants.DIRECT_REF_CODE_EXT_URL
            }
        }

    val logicDefinitionExtensionUrl: String
        get() {
            return when (specificationLevel) {
                SpecificationLevel.US_QM_STU4 -> CqfmConstants.LOGIC_DEFINITION_EXT_URL
                SpecificationLevel.US_QM_STU5,
                SpecificationLevel.QM_STU_1,
                SpecificationLevel.CRMI,
                SpecificationLevel.CPG -> CqfConstants.LOGIC_DEFINITION_EXT_URL
            }
        }

    val relatedRequirementExtensionUrl: String
        get() {
            return when (specificationLevel) {
                SpecificationLevel.US_QM_STU4 -> CqfmConstants.RELATED_REQUIREMENT_EXT_URL
                SpecificationLevel.US_QM_STU5,
                SpecificationLevel.QM_STU_1,
                SpecificationLevel.CRMI,
                SpecificationLevel.CPG -> CqfConstants.RELATED_REQUIREMENT_EXT_URL
            }
        }

    val pertinenceExtensionUrl: String
        get() {
            return when (specificationLevel) {
                SpecificationLevel.US_QM_STU4 -> CqfmConstants.PERTINENCE_EXT_URL
                SpecificationLevel.US_QM_STU5,
                SpecificationLevel.QM_STU_1,
                SpecificationLevel.CRMI,
                SpecificationLevel.CPG -> CqfConstants.PERTINENCE_EXT_URL
            }
        }
}
