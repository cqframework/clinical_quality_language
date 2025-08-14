library TestContext

using FHIR version '4.0.1'

include FHIRHelpers version '4.0.1'

context Patient

define Encounters: [Encounter]

context Practitioner

define Patients: [Patient]

context Encounter

define Observations: [Observation]

context RelatedPerson

define AdverseEvents: [AdverseEvent]

