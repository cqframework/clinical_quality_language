package org.cqframework.fhir.api.r4

import org.hl7.fhir.r4.model.CodeableConcept
import org.hl7.fhir.r4.model.Coding
import org.hl7.fhir.r4.model.Parameters
import org.hl7.fhir.r4.model.ValueSet
import org.hl7.fhir.r4.model.codesystems.ConceptSubsumptionOutcome

interface FhirTerminologyService : FhirService {
    // https://hl7.org/fhir/valueset-operation-expand.html
    // TODO: Consider activeOnly, as well as includeDraft and expansion parameters (see Measure
    // Terminology Service in
    // the QM IG)
    fun expand(url: String?): ValueSet?

    fun expand(url: String?, systemVersion: Iterable<String?>?): ValueSet?

    // https://hl7.org/fhir/codesystem-operation-lookup.html
    // TODO: Define LookupResult class
    fun lookup(code: String?, systemUrl: String?): Parameters?

    fun lookup(coding: Coding?): Parameters?

    // https://hl7.org/fhir/valueset-operation-validate-code.html
    // TODO: Define ValidateResult class
    fun validateCodeInValueSet(
        url: String?,
        code: String?,
        systemUrl: String?,
        display: String?,
    ): Parameters?

    fun validateCodingInValueSet(url: String?, code: Coding?): Parameters?

    fun validateCodeableConceptInValueSet(url: String?, concept: CodeableConcept?): Parameters?

    // https://hl7.org/fhir/codesystem-operation-validate-code.html
    fun validateCodeInCodeSystem(
        url: String?,
        code: String?,
        systemUrl: String?,
        display: String?,
    ): Parameters?

    fun validateCodingInCodeSystem(url: String?, code: Coding?): Parameters?

    fun validateCodeableConceptInCodeSystem(url: String?, concept: CodeableConcept?): Parameters?

    // https://hl7.org/fhir/codesystem-operation-subsumes.html
    fun subsumes(codeA: String?, codeB: String?, systemUrl: String?): ConceptSubsumptionOutcome?

    fun subsumes(
        codeA: Coding?,
        codeB: Coding?,
    ): ConceptSubsumptionOutcome? // https://hl7.org/fhir/conceptmap-operation-translate.html
    // TODO: Translation support
}
