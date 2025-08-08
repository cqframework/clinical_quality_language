library EvaluatedResourcesMultiLibComplexDepsTest_Level3B

using FHIR version '4.0.1'

include FHIRHelpers version '4.0.1'

include EvaluatedResourcesMultiLibComplexDepsTest_Level4 called Level4

define "Encounter Subjects":
    Level4."Encounter Subjects" S
        where S != 'Patient/pat2'
