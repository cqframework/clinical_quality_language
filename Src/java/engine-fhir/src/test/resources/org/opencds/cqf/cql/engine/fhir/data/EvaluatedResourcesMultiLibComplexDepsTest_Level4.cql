library EvaluatedResourcesMultiLibComplexDepsTest_Level4

using FHIR version '4.0.1'

include FHIRHelpers version '4.0.1'

include EvaluatedResourcesMultiLibComplexDepsTest_Level5 called Level5

define "Encounter Subjects":
    Level5."Encounters" E
        where E.subject.reference != 'Patient/pat3'
        return E.subject.reference
