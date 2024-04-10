library LocalIdTest

using FHIR version '4.0.1'
include FHIRHelpers version '4.0.1'

 // https://github.com/cqframework/clinical_quality_language/issues/1338
define "Encounter with Condition":
  [Encounter] E
    with [Condition] C
      such that C.onset overlaps E.period
