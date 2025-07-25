library EvaluatedResourcesMultiLibComplexDepsTest_Level1B

using FHIR version '4.0.1'

include FHIRHelpers version '4.0.1'

include EvaluatedResourcesMultiLibComplexDepsTest_Level2 called Level2

parameter "Measurement Period" Interval<DateTime> default Interval[@2021-07-01T00:00:00.000, @2022-07-01T00:00:00.000)

define "Encounters A":
    Level2."Encounters" E
        where E.status = 'arrived'

define "Encounters B":
    Level2."Encounters" E
        where E.status = 'triaged'
