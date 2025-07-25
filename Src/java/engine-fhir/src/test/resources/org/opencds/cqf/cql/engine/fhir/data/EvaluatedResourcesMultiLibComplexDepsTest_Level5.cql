library EvaluatedResourcesMultiLibComplexDepsTest_Level5

using FHIR version '4.0.1'

include FHIRHelpers version '4.0.1'

context Patient

define "Encounters":
    [Encounter] E
        where E.status.value in { 'arrived', 'planned', 'triaged', 'finished' }
