library TestCase9b

using FHIR version '4.0.1'

include FHIRHelpers version '4.0.1'

valueset "Aspirin": 'http://example.org/fhir/ValueSet/aspirin'

context Patient

/*
Element that is a choice, one of which is a reference, included in a with that also has a conjunctive requirement
[MedicationRequest.medication](http://hl7.org/fhir/medicationrequest-definitions.html#MedicationRequest.medication_x_)
dataRequirement: { type: MedicationRequest }
dataRequirement: { type: Medication, codeFilter: { path: code, valueset: Aspirin }, relatedRequirement: { targetId: G10001, targetProperty: medication } }
*/
define MedicationRequestForAspirin:
  [MedicationRequest] R
    with [Medication] M
      such that M.id = Last(Split(R.medication.reference, '/'))
        and M.code in "Aspirin"

