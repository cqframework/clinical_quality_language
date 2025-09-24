library TestIntervalImplicitConversion

using FHIR version '3.0.0'

include FHIRHelpers version '3.0.0' called FHIRHelpers

parameter "Measurement Period" Interval<DateTime>

context Patient

define "Inpatient Encounter" :
  ["Encounter"] Encounter
    where LengthInDays(Encounter.period) <= 120
      and Encounter.period ends during "Measurement Period"

define function "LengthInDays"(Value Interval<DateTime>):
	difference in days between start of Value and end of Value
