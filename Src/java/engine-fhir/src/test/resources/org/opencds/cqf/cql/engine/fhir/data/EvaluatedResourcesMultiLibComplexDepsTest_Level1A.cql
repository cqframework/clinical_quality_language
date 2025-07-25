library EvaluatedResourcesMultiLibComplexDepsTest_Level1A

using FHIR version '4.0.1'

include FHIRHelpers version '4.0.1'

// Level 5 > { 'arrived', 'finished', 'in-progress', 'planned', 'triaged' }
// Level 3A > { 'arrived', 'in-progress', 'planned', 'triaged' }
// Level 2 > { 'arrived', 'in-progress', 'planned' }
include EvaluatedResourcesMultiLibComplexDepsTest_Level2 called Level2

parameter "Measurement Period" Interval<DateTime> default Interval[@2021-01-01T00:00:00.000, @2022-01-01T00:00:00.000)

define "Encounters A":
    Level2."Encounters" E
        where E.status.value in { 'arrived', 'cancelled', 'in-progress', 'planned' }

define "Encounters B":
    Level2."Encounters" E
        where E.status.value in { 'cancelled', 'finished', 'in-progress', 'planned' }
