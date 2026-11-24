library LargeChartMemoryBenchmark

using FHIR version '4.0.1'

include FHIRHelpers version '4.0.1'

codesystem "LOINC": 'http://loinc.org'
codesystem "SNOMED": 'http://snomed.info/sct'

code "Systolic blood pressure": '8480-6' from "LOINC"
code "Glucose": '2339-0' from "LOINC"
code "HbA1c": '4548-4' from "LOINC"
code "Diabetes": '44054006' from "SNOMED"
code "Hypertension": '38341003' from "SNOMED"

parameter "Measurement Period" Interval<DateTime>
  default Interval[@2022-01-01T00:00:00.000Z, @2023-01-01T00:00:00.000Z)

context Patient

define "Observation Count":
  Count([Observation])

define "Systolic In Period":
  [Observation: "Systolic blood pressure"] O
    where (O.effective as dateTime) during "Measurement Period"

define "Average Systolic In Period":
  Avg("Systolic In Period" O return (O.value as Quantity).value)

define "Most Recent HbA1c":
  Last(
    [Observation: "HbA1c"] O
      sort by FHIRHelpers.ToDateTime(effective as dateTime)
  )

define "High Glucose Readings":
  [Observation: "Glucose"] O
    where (O.value as Quantity) > 150 'mg/dL'

define "Encounters In Period":
  [Encounter] E
    where E.period during "Measurement Period"

define "Active Diabetes":
  [Condition: "Diabetes"] C
    where exists (C.clinicalStatus.coding Status where Status.code = 'active')

define "Diabetic Encounters In Period":
  "Encounters In Period" E
    with "Active Diabetes" C
      such that (C.onset as dateTime) before end of E.period

define "Active Medications":
  [MedicationRequest] M
    where M.status = 'active'

define "Has Hypertension":
  exists [Condition: "Hypertension"]

define "Initial Population":
  AgeInYearsAt(start of "Measurement Period") >= 18
    and exists "Diabetic Encounters In Period"

define "Numerator":
  ("Most Recent HbA1c".value as Quantity) < 9 '%'
