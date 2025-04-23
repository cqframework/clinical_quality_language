package org.cqframework.cql.elm.requirements.fhir.utilities;

import org.cqframework.cql.elm.requirements.fhir.utilities.constants.CqfConstants;
import org.cqframework.cql.elm.requirements.fhir.utilities.constants.CqfmConstants;

public class SpecificationSupport {

    public SpecificationSupport() {
        this(SpecificationLevel.US_QM_STU4);
    }

    public SpecificationSupport(SpecificationLevel specificationLevel) {
        this.specificationLevel = specificationLevel;
    }

    private SpecificationLevel specificationLevel;

    public SpecificationLevel getSpecificationLevel() {
        return this.specificationLevel;
    }

    public String getDirectReferenceCodeExtensionUrl() {
        switch (specificationLevel) {
            case US_QM_STU4:
                return CqfmConstants.DIRECT_REF_CODE_EXT_URL;
            case US_QM_STU5:
            case QM_STU_1:
            case CRMI:
            case CPG:
            default:
                return CqfConstants.DIRECT_REF_CODE_EXT_URL;
        }
    }

    public String getLogicDefinitionExtensionUrl() {
        switch (specificationLevel) {
            case US_QM_STU4:
                return CqfmConstants.LOGIC_DEFINITION_EXT_URL;
            case US_QM_STU5:
            case QM_STU_1:
            case CRMI:
            case CPG:
            default:
                return CqfConstants.LOGIC_DEFINITION_EXT_URL;
        }
    }

    public String getRelatedRequirementExtensionUrl() {
        switch (specificationLevel) {
            case US_QM_STU4:
                return CqfmConstants.RELATED_REQUIREMENT_EXT_URL;
            case US_QM_STU5:
            case QM_STU_1:
            case CRMI:
            case CPG:
            default:
                return CqfConstants.RELATED_REQUIREMENT_EXT_URL;
        }
    }

    public String getPertinenceExtensionUrl() {
        switch (specificationLevel) {
            case US_QM_STU4:
                return CqfmConstants.PERTINENCE_EXT_URL;
            case US_QM_STU5:
            case QM_STU_1:
            case CRMI:
            case CPG:
            default:
                return CqfConstants.PERTINENCE_EXT_URL;
        }
    }
}
