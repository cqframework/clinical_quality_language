library TestCase9a

using FHIR version '4.0.1'

include FHIRHelpers version '4.0.1'

context Patient

/*
Singleton element that is a reference
[MedicationRequest.encounter](http://hl7.org/fhir/medicationrequest-definitions.html#MedicationRequest.encounter)
dataRequirement: { id: G10001, type: MedicationRequest }
dataRequirement: { type: Encounter, relatedRequirement { targetId : G10001, targetPath: encounter } }
*/
define MedicationRequestWithEncounter:
  [MedicationRequest] M
    with [Encounter] E
      such that E.id = Last(Split(M.encounter.reference, '/'))


