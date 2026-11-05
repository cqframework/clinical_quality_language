library TestCase9e

using FHIR version '4.0.1'

include FHIRHelpers version '4.0.1'

valueset "Aspirin": 'http://example.org/fhir/ValueSet/aspirin'

context Patient

/*
Element that is a choice, one of which is a reference, included in a where
[MedicationRequest.medication](http://hl7.org/fhir/medicationrequest-definitions.html#MedicationRequest.medication[x])
dataRequirement: { id: G10001, type: MedicationRequest }
dataRequirement: { type: Medication, codeFilter: { path: code, valueset: Aspirin }, relatedRequirement { targetId : G10001, targetPath: medication } }
*/
define MedicationRequestWithAspirinInWhere:
  [MedicationRequest] R
    where exists (
      [Medication] M
        where M.id = Last(Split(R.medication.reference, '/'))
          and M.code in "Aspirin"
  )
