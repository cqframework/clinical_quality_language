/*
The process of translating a CQL retrieve into a FHIR query involves potentially
different approaches depending on the data access capabilities of the environment
in which the CQL will be evaluated. Because the optimal ELM approach depends on the
environment, the CQL translator focuses on property-based evaluation (though it does
support a few of the search-based approaches, and those are tested here).

The actual rewrite is expected to take place as a "planning" step by the execution
engine, either once as a "plan" step that rewrites the ELM for a particular environment,
or as part of evaluation using run-time resolution.

The tests here began as a way to validate all of the approaches, but is now focused only
on making sure that there are capabilities provided, not on implementing translation for
a particular target environment.

In particular, the translator supports:
* Author-time selection of a particular search path through the use of the undocumented "?" syntax
This is not something we propose exposing to authors, but is supported as a way to perform "syntactic"
optimization if necessary. Current behavior is that this only works outside a retrieve, when used within
a retrieve it is still considered a code path, but the "?" prefix is retained.
* Support for the .resolve() function that, given a FHIR reference resolves to the referenced resource
* Support for the .reference() function that, given a FHIR resource, returns the FHIR reference to it
* Support for equality of FHIR references

The planning step would then be able to optimize for a particular environment based on:
* Whether it's a file-based environment and the engine is expected to perform most of the query work
* Whether it's a relational environment where references would be expected to be supported as joins by the data-access layer
* Whether it's an API environment where references would be expected to be supported via "include" parameters
 */

library TestInclude

using FHIR version '4.0.1'

include FHIRHelpers version '4.0.1'

codesystem "SNOMED": 'http://snomed.info/sct'
codesystem "RXNORM": 'http://www.nlm.nih.gov/research/umls/rxnorm'

code "aspirin 325 MG / oxycodone hydrochloride 4.84 MG Oral Tablet": '848768' from "RXNORM" display 'aspirin 325 MG / oxycodone hydrochloride 4.84 MG Oral Tablet'
code "Acute hepatic failure": '197270009' from "SNOMED" display 'Acute hepatic failure'
code "HIV Negative": '165815009' from "SNOMED" display 'HIV Negative'

context Patient

// Search Parameters

define TestPractitionerSearch1:
  [Practitioner: "?name" = 'ABC']

define TestPractitionerSearch1A:
  [Practitioner] P
    where P."?name" = 'ABC'

/*
// [base]/Practitioner?name='ABC'

define TestObservationEncounterSearch1:
  [Observation: encounter = FHIRHelpers.reference('XYZ')]

// Expected FHIR URL for the retrieve
// [base]/Observation?subject=123&encounter=XYZ

define TestMedicationSearch1:
  [MedicationRequest: medication = FHIRHelpers.reference('XYZ')]

// Expected FHIR URL for the retrieve
// [base]/MedicationRequest?subject=123&medication=XYZ

// TODO: Optimize to a search
define TestMedicationSearch2:
  [MedicationRequest] MR
    with [Medication] M such that MR.medication = M.reference()

// Chaining

define TestMedicationRequestChaining1:
  [MedicationRequest: medication.code ~ "aspirin 325 MG / oxycodone hydrochloride 4.84 MG Oral Tablet"]

// Expected FHIR URL for the retrieve
// [base]/MedicationRequest?subject=123&medication.code=http://www.nlm.nih.gov/research/umls/rxnorm|848768

define TestEncounterChaining1:
  [Encounter: diagnosis.code ~ "Acute hepatic failure"]

// NOTE: This is the only reasonable translation here, but it is not right or useful or correct
// Really it ought to be split into two retrieves, one for diagnoses with a condition and one for diagnoses with a procedure
// [base]/Encounter?subject=123&diagnosis:Condition.code=http://snomed.info/sct|197270009&diagnosis:Procedure.code=http://snomed.info/sct|197270009

// Reverse Chaining

// Expected FHIR URL for the retrieve
// [base]/Encounter?_has:Observation:context:code=http://snomed.info/sct|165815009

// Includes
*/

// If a reference is resolved in the scope of a query, that resolve can be rewritten as an include in the retrieve for that source
// MedicationRequest
define TestMedicationRequest1:
  [MedicationRequest] MR
    where MR.medication.reference.resolve().as(Medication).code ~ "aspirin 325 MG / oxycodone hydrochloride 4.84 MG Oral Tablet"

// Expected FHIR URL for the retrieve
// [base]/MedicationRequest?patient=123&_include=MedicationRequest:medication

define TestMedicationRequest1A:
  [MedicationRequest] MR
    with [Medication] M such that MR.medication = M.reference() and M.code ~ "aspirin 325 MG / oxycodone hydrochloride 4.84 MG Oral Tablet"

//X.<reference>.references(Y) <=> X.<reference> = Y.reference()

define TestMedicationRequest1B:
  [MedicationRequest] MR
    with [MR.medication -> Medication] M such that M.code ~ "aspirin 325 MG / oxycodone hydrochloride 4.84 MG Oral Tablet"

define TestMedicationRequest1C:
  [MedicationRequest] MR
    let M: singleton from ([MR.medication -> Medication])
    where M.code ~ "aspirin 325 MG / oxycodone hydrochloride 4.84 MG Oral Tablet"

/*
// [base]/MedicationRequest?patient=123&_include=MedicationRequest:medication
// TODO: Optimize to a chaining request if supported:
// [base]/MedicationRequest?patient=123&medication.code=http://www.nlm.nih.gov/research/umls/rxnorm|848768

define TestMedicationRequest1A1:
  [MedicationRequest] MR
    with [Medication] M such that MR.medication = M.reference() and M.code ~ "aspirin 325 MG / oxycodone hydrochloride 4.84 MG Oral Tablet"

// [base]/MedicationRequest?patient=123&_include=MedicationRequest:medication
// TODO: Optimize to a chaining request if supported:
// [base]/MedicationRequest?patient=123&medication.code=http://www.nlm.nih.gov/research/umls/rxnorm|848768

define TestMedicationRequest2:
  [MedicationRequest] MR
    let Med: MR.medication.reference.resolve() as Medication
    where MR.medication ~ "aspirin 325 MG / oxycodone hydrochloride 4.84 MG Oral Tablet"
      or Med.code ~ "aspirin 325 MG / oxycodone hydrochloride 4.84 MG Oral Tablet"

// Expected FHIR URL for the retrieve
// [base]/MedicationRequest?patient=123&_include=MedicationRequest:medication

define TestMedicationRequest2A:
  [MedicationRequest] MR
    let Med: [Medication] M where MR.medication.references(M)
    where MR.medication ~ "aspirin 325 MG / oxycodone hydrochloride 4.84 MG Oral Tablet"
      or Med.code ~ "aspirin 325 MG / oxycodone hydrochloride 4.84 MG Oral Tablet"

// Expected FHIR URL for the retrieve
// [base]/MedicationRequest?patient=123&_include=MedicationRequest:medication

define TestMedicationRequest3:
  [MedicationRequest: code ~ "aspirin 325 MG / oxycodone hydrochloride 4.84 MG Oral Tablet"]

// Expected FHIR URL for the retrieve
// [base]/MedicationRequest?subject=123&code=http://www.nlm.nih.gov/research/umls/rxnorm|848768

// Encounter and Diagnosis (Condition)
define TestEncounter1:
  [Encounter] E
    where exists (E.diagnosis D
      where (D.condition.resolve() as Condition).code ~ "Acute hepatic failure"
    )

// Expected FHIR URL for the retrieve
// [base]/Encounter?subject=123&_include=Encounter:diagnosis:Condition

define TestEncounter1A:
  [Encounter] E
    with [Condition] C such that exists (E.diagnosis D where D.condition.references(C)) and C.code ~ "Acute hepatic failure"

// [base]/Encounter?subject=123&_include=Encounter:diagnosis:Condition

define TestEncounter2:
  [Encounter] E
    where exists (E.diagnosis D
      let C: D.condition.resolve() as Condition
      where C.code ~ "Acute hepatic failure"
    )

// Expected FHIR URL for the retrieve
// [base]/Encounter?subject=123&_include=Encounter:diagnosis:Condition

// Reverse Includes

define TestEncounterRevereseInclude1:
  [Encounter] E
    let P: [Provenance] N where N.target.references(E)

// [base]/Encounter?_revinclude=Provenance:target
*/

// TODO: Improve syntax with a "references" keyword?
/*
define TestEncounterReverseInclude1:
  [Encounter] E
    let P: [Provenance: target references E]
*/

