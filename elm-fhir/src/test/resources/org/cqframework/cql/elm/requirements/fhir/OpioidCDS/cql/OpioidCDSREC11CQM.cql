library OpioidCDSREC11CQM version '2.0.1'

using FHIR version '4.0.0'

include FHIRHelpers version '4.0.0'

include OpioidCDSREC11 version '2.0.1'
include OpioidCDSRoutines version '2.0.1' called Routines

context Patient

define "Initial Population":
  Routines."Is Opioid Review Useful?"

define "Denominator":
  true

define "Numerator":
  OpioidCDSREC11."Is Recommendation Applicable?"
