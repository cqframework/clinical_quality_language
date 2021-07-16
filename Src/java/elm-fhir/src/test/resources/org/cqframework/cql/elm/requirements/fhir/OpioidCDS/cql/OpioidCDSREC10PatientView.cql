library OpioidCDSREC10PatientView version '2.0.1'

using FHIR version '4.0.1'

include FHIRHelpers version '4.0.1' called FHIRHelpers
include OpioidCDSCommon version '2.0.1' called Common
include OpioidCDSRoutines version '2.0.1' called Routines

code "Urine Drug Screening": '310627008' from Common.SNOMED display 'Urine drug screening (procedure)'

/*
**  Recommendation #10
**    When prescribing opioids for chronic pain, providers should use urine drug
**    testing before starting opioid therapy and consider urine drug testing at
**    least annually to assess for prescribed medications as well as other controlled
**    prescription drugs and illicit drugs (recommendation category: B, evidence type: 4)
**
**  When
**    Provider is prescribing an opioid analgesic with ambulatory misuse potential in the outpatient setting
**    Prescription is for treating chronic pain.
**    Opioid review is useful for this patient:
**      Patient is 18 or over
**      Patient does not have findings indicating limited life expectancy
**      Patient does not have orders for therapies indicating end of life care
**      Patient is not undergoing active cancer treatment:
**        Patient has had at least 2 encounters within the past year with any diagnosis of cancer
**    Urine drug screening has not been performed in last 12 months
**  Then
**    Recommend urine drug screening
**      Will perform urine screening
**      Not for chronic pain management, snooze 3 months
**      N/A - see comment, snooze 3 months
**
*/

// META: Plan Definition: http://fhir.org/guides/cdc/opioid-cds-r4/PlanDefinition/opioid-cds-10-patient-view

context Patient

define "Lookback Year":
  Interval[Today() - 12 months - 1 days, Today() - 1 day]

define "Patient Is Being Prescribed Opioid Analgesic with Ambulatory Misuse Potential":
  exists (
    Common."Active Ambulatory Opioid Rx" AmbulatoryOpioidPrescription
      where Routines."Is Chronic Pain Prescription?"( AmbulatoryOpioidPrescription )
  )

define "Is Recommendation Applicable?":
  "Inclusion Criteria"
    and not "Exclusion Criteria"

define "Inclusion Criteria":
  "Patient Is Being Prescribed Opioid Analgesic with Ambulatory Misuse Potential"
    and Routines."Is Opioid Review Useful?"
    and not "Patient had Urine Screening in Last 12 Months"

define "Exclusion Criteria":
  Common."End of Life Assessment"

define "Patient had Urine Screening in Last 12 Months":
  exists( "Urine Screenings during the Last 12 Months" )

define "Urine Screenings during the Last 12 Months":
  (
    [Observation: "code" in Common."Non-opioid drug urine screening"] IllicitDrugScreen
      where date from IllicitDrugScreen.effective in day of "Lookback Year"
        and not (IllicitDrugScreen.status.value in { 'unknown', 'entered-in-error', 'cancelled' })
  )
  union
  (
    [Observation: "code" in Common."Opioid drug urine screening"] OpioidDrugScreen
      where date from OpioidDrugScreen.effective in day of "Lookback Year"
        and not (OpioidDrugScreen.status.value in { 'unknown', 'entered-in-error', 'cancelled' })
  )

define "No Screening In Last 12 Months Indicator":
  if "Is Recommendation Applicable?"
    then 'warning'
  else null

define "No Screening In Last 12 Months Summary":
  if "Is Recommendation Applicable?"
    then 'Annual Urine Screening Check'
  else null

define "No Screening In Last 12 Months Detail":
  if "Is Recommendation Applicable?"
    then 'Patients on opioid therapy should have a urine drug test performed every 12 months.'
  else null

/*
PlanDefinition:
  Inclusion Criteria:
    No Screening in Last 12 months - ServiceRequest - Urine Screening
    Opioids found in urine screening
    Illicit drugs found in urine screening
*/

// Service Request - Urine Screening
// Detected Issue - Opioids found in urine screening
// Detected Issue - Illicit drugs found in urine screening

define "Urine Drug Screening Request":
    ServiceRequest {
      //identifier:,
      instantiatesCanonical: { FHIR.canonical { value: 'http://fhir.org/guides/cdc/activitydefinition/urine-screening-request' } },
      status: FHIR.ServiceRequestStatus { value: 'draft' },
      intent: FHIR.ServiceRequestIntent { value: 'proposal' },
      priority: FHIR.ServiceRequestPriority { value: 'routine' },
      code: FHIR.CodeableConcept { coding: { ToCoding("Urine Drug Screening") } },
      subject: FHIR.Reference { reference: FHIR.string { value: 'Patient/' + Patient.id } },
      occurrence: FHIR.Period { start: FHIR.dateTime { value: Today() }, end: FHIR.dateTime { value: Today() + 7 days } },
      authoredOn: FHIR.dateTime { value: Now() },
      reasonCode: { FHIR.CodeableConcept { text: FHIR.string { value: "No Screening In Last 12 Months Detail" } } }
      // doesn't really work, need a relatedArtifact here...
      //reasonCode: { FHIR.CodeableConcept { text: FHIR.string { value: 'https://www.cdc.gov/mmwr/volumes/65/rr/rr6501e1.htm#10_When_prescribing_opioids' } } }
    }

/*
define "Opioids Issue":
  DetectedIssue {
    //identifier:,
    status: FHIR.ObservationStatus { value: 'preliminary' },
    code: FHIR.CodeableConcept { text: FHIR.string { value: "Evidence of Opioids Summary" } },
    severity: FHIR.DetectedIssueSeverity { value: 'moderate' },
    patient: FHIR.Reference { reference: FHIR.string { value: 'Patient/' + Patient.id } },
    identified: FHIR.dateTime { value: Now() },
    // TODO: Device representation...
    // author:,
    // TODO: Reference the lab results that were used to infer this
    // implicated:,
    detail: FHIR.string { value: "Evidence of Opioids Detail" },
    reference: FHIR.uri { value: 'http://fhir.org/guides/cdc/opioid-cds/PlanDefinition/opioidcds-10-patient-view'}
  }

define "Illicit Drugs Issue":
  DetectedIssue {
    //identifier:,
    status: FHIR.ObservationStatus { value: 'preliminary' },
    code: FHIR.CodeableConcept { text: FHIR.string { value: "Evidence of Illicit Drugs Summary" } },
    severity: FHIR.DetectedIssueSeverity { value: 'moderate' },
    patient: FHIR.Reference { reference: FHIR.string { value: 'Patient/' + Patient.id } },
    identified: FHIR.dateTime { value: Now() },
    // TODO: Device representation...
    // author:,
    // TODO: Reference the lab results that were used to infer this
    // implicated:,
    detail: FHIR.string { value: "Evidence of Illicit Drugs Detail" },
    reference: FHIR.uri { value: 'http://fhir.org/guides/cdc/opioid-cds/PlanDefinition/opioidcds-10-patient-view'}
  }
*/

define function ToCoding(code System.Code):
  FHIR.Coding {
    code: FHIR.code { value: code.code },
    system: FHIR.uri { value: code.system },
    version: FHIR.string { value: code.version },
    display: FHIR.string { value: code.display }
  }
