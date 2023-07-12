library EvaluatedResourcesTest version '1.0'

using FHIR version '4.0.1'

context Patient

define "Union":
   "Encounter" union "Condition"

define "Encounter":
    [Encounter]

define "Condition":
    [Condition]