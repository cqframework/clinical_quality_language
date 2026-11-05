library TestForwardDeclarationImplicitConversion

// This is to test that forward declartions for overloaded functions works as expected.
using FHIR version '4.0.0'

include FHIRHelpers version '4.0.0' called FHIRHelpers

parameter "Measurement Period" Interval<DateTime>

context Patient

define "Inpatient Encounter" :
  ["Encounter"] Encounter
    where LengthInDays(Encounter.period) <= 120

define function "LengthInDays"(Value Interval<DateTime>): null
