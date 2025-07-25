library EvaluatedResourcesMultiLibComplexDepsTest_Level2

using FHIR version '4.0.1'

include FHIRHelpers version '4.0.1'

include EvaluatedResourcesMultiLibComplexDepsTest_Level3A called Level3A

define "Encounters":
    Level3A."Encounters"