/*
@issue: [#558](https://github.com/cqframework/clinical_quality_language/issues/558)
 */
library Issue558

using FHIR version '4.0.1'

parameter "Measurement Period" Interval<DateTime>

context Patient

define "InitPop":
  a