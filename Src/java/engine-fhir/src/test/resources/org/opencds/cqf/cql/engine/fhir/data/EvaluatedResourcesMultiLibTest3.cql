library EvaluatedResourcesMultiLibTest3 version '1.0'

using FHIR version '4.0.1'

context Patient

define "Union":
   "Encounter" union "Procedure"

define "Encounter":
    [Encounter]

define "Procedure":
    [Procedure]
