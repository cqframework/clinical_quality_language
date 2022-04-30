package org.cqframework.fhir.api.stu3;

import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Coding;
import org.hl7.fhir.dstu3.model.Parameters;
import org.hl7.fhir.dstu3.model.ValueSet;

public interface FhirTerminologyService extends FhirService {
    // https://hl7.org/fhir/valueset-operation-expand.html
    // TODO: Consider activeOnly, as well as includeDraft and expansion parameters (see Measure Terminology Service in the QM IG)
    ValueSet expand(String url);
    ValueSet expand(String url, Iterable<String> systemVersion);

    // https://hl7.org/fhir/codesystem-operation-lookup.html
    // TODO: Define LookupResult class
    Parameters lookup(String code, String systemUrl);
    Parameters lookup(Coding coding);

    // https://hl7.org/fhir/valueset-operation-validate-code.html
    // TODO: Define ValidateResult class
    Parameters validateCodeInValueSet(String url, String code, String systemUrl, String display);
    Parameters validateCodingInValueSet(String url, Coding code);
    Parameters validateCodeableConceptInValueSet(String url, CodeableConcept concept);

    // https://hl7.org/fhir/codesystem-operation-validate-code.html
    Parameters validateCodeInCodeSystem(String url, String code, String systemUrl, String display);
    Parameters validateCodingInCodeSystem(String url, Coding code);
    Parameters validateCodeableConceptInCodeSystem(String url, CodeableConcept concept);

    // https://hl7.org/fhir/codesystem-operation-subsumes.html
    String subsumes(String codeA, String codeB, String systemUrl);
    String subsumes(Coding codeA, Coding codeB);

    // https://hl7.org/fhir/conceptmap-operation-translate.html
    // TODO: Translation support
}
