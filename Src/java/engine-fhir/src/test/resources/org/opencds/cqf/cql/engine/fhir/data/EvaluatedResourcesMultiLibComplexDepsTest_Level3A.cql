library EvaluatedResourcesMultiLibComplexDepsTest_Level3A

using FHIR version '4.0.1'

include FHIRHelpers version '4.0.1'

// Level 5 > { 'arrived', 'finished', 'in-progress', 'planned', 'triaged' }
include EvaluatedResourcesMultiLibComplexDepsTest_Level5 called Level5

// Level 3A > { 'arrived', 'in-progress', 'planned', 'triaged' }
define "Encounters":
    Level5."Encounters" E
        where not(E.status.value in { 'finished' })
