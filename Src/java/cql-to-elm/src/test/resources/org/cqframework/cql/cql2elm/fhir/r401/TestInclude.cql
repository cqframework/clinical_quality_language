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
// Resolve as search parameter first, then element name
// TODO: Allow search parameters in arbitrary where clause?...

define TestEncounterSearch1:
  [Practitioner: name = 'ABC']

// [base]/Practitioner?name='ABC'

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

// If a reference is resolved in the scope of a query, that resolve can be rewritten as an include in the retrieve for that source
// MedicationRequest
define TestMedicationRequest1:
  [MedicationRequest] MR
    where MR.medication ~ "aspirin 325 MG / oxycodone hydrochloride 4.84 MG Oral Tablet"
      or (MR.medication.reference.resolve() as Medication).code ~ "aspirin 325 MG / oxycodone hydrochloride 4.84 MG Oral Tablet"

// Expected FHIR URL for the retrieve
// [base]/MedicationRequest?patient=123&_include=MedicationRequest:medication

define TestMedicationRequest1A:
  [MedicationRequest] MR
    with [Medication] M such that MR.medication.references(M) and M.code ~ "aspirin 325 MG / oxycodone hydrochloride 4.84 MG Oral Tablet"

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

// TODO: Improve syntax with a "references" keyword?
/*
define TestEncounterReverseInclude1:
  [Encounter] E
    let P: [Provenance: target references E]
*/

