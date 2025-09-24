library TestRetrieveWithConcept version '0.1.0'

/*
https://github.com/cqframework/clinical_quality_language/issues/501
Retrieve with a Direct-Reference Code against a type that has a CodeableConcept
for the terminology path results in a Concept being passed to the retrieve,
rather than a List<Code>
 */

using FHIR version '1.0.2'

include FHIRHelpers version '1.0.2' called FHIRHelpers

codesystem "LOINC": 'http://loinc.org'

code "Tobacco Smoking Status": '72166-2' from "LOINC"

context Patient

define "Test Tobacco Smoking Status": [Observation: "Tobacco Smoking Status"]