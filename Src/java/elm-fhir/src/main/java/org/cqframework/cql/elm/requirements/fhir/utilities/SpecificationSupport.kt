package org.cqframework.cql.elm.requirements.fhir.utilities

import org.cqframework.cql.elm.requirements.fhir.utilities.constants.CqfConstants
import org.cqframework.cql.elm.requirements.fhir.utilities.constants.CqfmConstants

class SpecificationSupport @JvmOverloads constructor(val specificationLevel: SpecificationLevel = SpecificationLevel.US_QM_STU4) {
    val directReferenceCodeExtensionUrl: String
        get() {
            when (specificationLevel) {
                SpecificationLevel.US_QM_STU4 -> return CqfmConstants.DIRECT_REF_CODE_EXT_URL
                SpecificationLevel.US_QM_STU5, SpecificationLevel.QM_STU_1, SpecificationLevel.CRMI, SpecificationLevel.CPG -> return CqfConstants.DIRECT_REF_CODE_EXT_URL
                else -> return CqfConstants.DIRECT_REF_CODE_EXT_URL
            }
        }

    val logicDefinitionExtensionUrl: String
        get() {
            when (specificationLevel) {
                SpecificationLevel.US_QM_STU4 -> return CqfmConstants.LOGIC_DEFINITION_EXT_URL
                SpecificationLevel.US_QM_STU5, SpecificationLevel.QM_STU_1, SpecificationLevel.CRMI, SpecificationLevel.CPG -> return CqfConstants.LOGIC_DEFINITION_EXT_URL
                else -> return CqfConstants.LOGIC_DEFINITION_EXT_URL
            }
        }

    val relatedRequirementExtensionUrl: String
        get() {
            when (specificationLevel) {
                SpecificationLevel.US_QM_STU4 -> return CqfmConstants.RELATED_REQUIREMENT_EXT_URL
                SpecificationLevel.US_QM_STU5, SpecificationLevel.QM_STU_1, SpecificationLevel.CRMI, SpecificationLevel.CPG -> return CqfConstants.RELATED_REQUIREMENT_EXT_URL
                else -> return CqfConstants.RELATED_REQUIREMENT_EXT_URL
            }
        }

    val pertinenceExtensionUrl: String
        get() {
            when (specificationLevel) {
                SpecificationLevel.US_QM_STU4 -> return CqfmConstants.PERTINENCE_EXT_URL
                SpecificationLevel.US_QM_STU5, SpecificationLevel.QM_STU_1, SpecificationLevel.CRMI, SpecificationLevel.CPG -> return CqfConstants.PERTINENCE_EXT_URL
                else -> return CqfConstants.PERTINENCE_EXT_URL
            }
        }
}
