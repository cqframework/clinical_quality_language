library EvaluatedResourcesMultiLibTest2 version '1.0'

using FHIR version '4.0.1'

context Patient

define "Union":
   "Procedure" union "Condition"

define "Procedure":
    [Procedure]

define "Condition":
    [Condition]
