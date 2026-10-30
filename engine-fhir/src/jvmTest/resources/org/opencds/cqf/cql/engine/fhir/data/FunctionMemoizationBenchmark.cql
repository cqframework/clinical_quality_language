library FunctionMemoizationBenchmark

using FHIR version '4.0.1'

include FHIRHelpers version '4.0.1'

define fluent function "prevalence"(condition FHIR.Condition):
  Interval[
    FHIRHelpers.ToDateTime(condition.onset as FHIR.dateTime),
    FHIRHelpers.ToDateTime(condition.abatement as FHIR.dateTime)
  ]

define fluent function "hospitalization"(encounter FHIR.Encounter):
  FHIRHelpers.ToInterval(encounter.period)

define "Conditions": [Condition]

// No condition overlaps any encounter, so every pair is compared
define "Encounters Without Condition":
  [Encounter] E
    without "Conditions" C
      such that C.prevalence() overlaps E.hospitalization()
