library EvaluatedResourcesMultiLibComplexDepsTest_Level2

using FHIR version '4.0.1'

include FHIRHelpers version '4.0.1'

// Level 5 > { 'arrived', 'finished', 'in-progress', 'planned', 'triaged' }
// Level 3A > { 'arrived', 'in-progress', 'planned', 'triaged' }
include EvaluatedResourcesMultiLibComplexDepsTest_Level3A called Level3A

// Level 2 > { 'arrived', 'in-progress', 'planned' }
define "Encounters":
    Level3A."Encounters" E
        where not(E.status.value in { 'triaged' })