library EvaluatedResourcesMultiLibLinearDepsTestWarningHiding

using FHIR version '4.0.1'
include "FHIRHelpers" version '4.0.1'

define "Definition":
    ({1, 2, 3}) "Definition" return "Definition" //Warn, hides line 45
