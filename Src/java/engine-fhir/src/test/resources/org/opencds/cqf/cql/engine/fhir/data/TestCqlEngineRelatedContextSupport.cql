// N.B.  This makes a ton of difference so be sure to define it correctly
//library EvaluatedResourcesTest version '1.0'
library TestCqlEngineRelatedContextSupport version '1.0'

using FHIR version '4.0.1'

context Patient

define "Primary Care Doctor": singleton from (["Practitioner" : id in Patient.generalPractitioner.reference.value])

define "All Patient for GP":
    ["Primary Care Doctor" -> "Patient"]
