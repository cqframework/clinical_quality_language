library QueryJoinBenchmark

using FHIR version '4.0.1'

include FHIRHelpers version '4.0.1'

codesystem "ConditionClinicalStatusCodes": 'http://terminology.hl7.org/CodeSystem/condition-clinical'

code "active": 'active' from "ConditionClinicalStatusCodes"

define fluent function "prevalence"(condition FHIR.Condition):
  Interval[
    FHIRHelpers.ToDateTime(condition.onset as FHIR.dateTime),
    FHIRHelpers.ToDateTime(condition.abatement as FHIR.dateTime)
  ]

define fluent function "hospitalization"(encounter FHIR.Encounter):
  FHIRHelpers.ToInterval(encounter.period)

define "Conditions": [Condition]

// No condition overlaps any encounter, so every pair is compared
define "Without, expression source":
  [Encounter] E
    without "Conditions" C
      such that C.prevalence() overlaps E.hospitalization()

define "Without, retrieve source":
  [Encounter] E
    without [Condition] C
      such that C.prevalence() overlaps E.hospitalization()

// Every encounter is finished, but only the last condition is active
define "With, row and related conjuncts":
  [Encounter] E
    with "Conditions" C
      such that E.status = 'finished'
        and C.clinicalStatus ~ "active"
        and C.prevalence() before end of E.hospitalization()

define "Cross product":
  from [Encounter] E, "Conditions" C
    where C.prevalence() overlaps E.hospitalization()
    return all E
