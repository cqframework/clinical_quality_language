library OpioidCDSREC09 version '2.0.1'

using FHIR version '4.0.1'

include FHIRHelpers version '4.0.1' called FHIRHelpers
include OpioidCDSCommon version '2.0.1' called Common
include OpioidCDSCommonConfig version '2.0.1' called Config
include OpioidCDSRoutines version '2.0.1' called Routines

/*
**
**  Recommendation #9
**    Clinicians should review the patient’s history of controlled substance
**    prescriptions using state prescription drug monitoring program (PDMP) data
**    to determine whether the patient is receiving opioid dosages or dangerous
**    combinations that put him or her at high risk for overdose. Clinicians
**    should review PDMP data when starting opioid therapy for chronic pain
**    and periodically during opioid therapy for chronic pain, ranging from
**    every prescription to every 3 months
**    (recommendation category: A, evidence type: 4).
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
**    PDMP data has not been reviewed in the past 90 days
**  Then
**    Recommend offering naloxone medications
**      Recommend launch of SMART-APP to search PDMP data for this patient
**      Document PDMP data reviewed; snooze 3 months
**      N/A - see comment, snooze 3 months
**
*/

// META: Plan Definition: http://fhir.org/guides/cdc/opioid-cds-r4/PlanDefinition/opioid-cds-09

parameter ContextPrescriptions List<MedicationRequest>

context Patient

define "Look Back 90 Days":
  Interval[Today() - 90 days, Today()]

define "Chronic Pain Opioid Analgesic with Ambulatory Misuse Potential Prescriptions":
  ( Common."Is Opioid Analgesic with Ambulatory Misuse Potential?"( ContextPrescriptions ) ) AmbulatoryOpioidPrescription
    where Routines."Is Chronic Pain Prescription?"( AmbulatoryOpioidPrescription )

define "Patient Is Being Prescribed Opioid Analgesic with Ambulatory Misuse Potential":
  exists( "Chronic Pain Opioid Analgesic with Ambulatory Misuse Potential Prescriptions" )

define "Is Recommendation Applicable?":
  "Inclusion Criteria"
    and not "Exclusion Criteria"

define "Inclusion Criteria":
  "Patient Is Being Prescribed Opioid Analgesic with Ambulatory Misuse Potential"
    and Routines."Is Opioid Review Useful?"
    and "PDMP Data Not Reviewed in Past 90 Days"

define "Exclusion Criteria":
  false

define "PDMP Data Not Reviewed in Past 90 Days":
  Config."PDMP Data Not Reviewed in Past 90 Days Criteria Enabled"
    and not (
      exists (
        [Procedure: Common."PDMP review procedure"] P
          where P.status ~ 'completed'
            and P.performed during "Look Back 90 Days"
      )
      or exists (
        [Observation: Common."PDMP data reviewed finding"] O
          where O.status in { 'final', 'amended' }
            and (
              if O.effective is FHIR.Period
                then (O.effective as FHIR.Period) during day of "Look Back 90 Days"
                else if O.effective is FHIR.dateTime
                  then date from O.effective in day of "Look Back 90 Days"
                  else false
          )
      )
    )

define "Get Indicator":
  if "Is Recommendation Applicable?"
    then 'warning'
  else null

define "Get Summary":
  if "Is Recommendation Applicable?"
    then 'Notify: PDMP data for this patient has not been reviewed in the past 90 days.'
  else null

define "Get Detail":
  if "Is Recommendation Applicable?"
    then 'Medication requests(s): ' + Combine(Common.GetMedicationNames("Chronic Pain Opioid Analgesic with Ambulatory Misuse Potential Prescriptions"), ', ')
  else null
