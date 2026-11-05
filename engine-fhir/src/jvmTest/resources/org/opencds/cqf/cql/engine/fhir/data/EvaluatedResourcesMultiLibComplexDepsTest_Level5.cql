library EvaluatedResourcesMultiLibComplexDepsTest_Level5

using FHIR version '4.0.1'

include FHIRHelpers version '4.0.1'

context Patient

/*
all statuses:
----
planned
arrived
triaged
in-progress
onleave
finished
cancelled
entered-in-error
unknown
*/

// Level 5 > { 'arrived', 'finished', 'in-progress', 'planned', 'triaged' }
define "Encounters":
    [Encounter] E
        where E.status.value in { 'arrived', 'planned', 'triaged', 'finished', 'in-progress' }
