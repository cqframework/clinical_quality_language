library OpioidCDSREC07 version '2.0.1'

using FHIR version '4.0.1'

include FHIRHelpers version '4.0.1' called FHIRHelpers
include OpioidCDSCommon version '2.0.1' called Common
include OpioidCDSRoutines version '2.0.1' called Routines

/*
**
**  Recommendation #7
**    Clinicians should evaluate benefits and harms with patients within 1 to 4
**    weeks of starting opioid therapy for chronic pain or of dose escalation.
**    Clinicians should evaluate benefits and harms of continued therapy with
**    patients every 3 months or more frequently. If benefits do not outweigh
**    harms of continued opioid therapy, clinicians should optimize other
**    therapies and work with patients to taper opioids to lower dosages or to
**    taper and discontinue opioids (recommendation category: A, evidence type: 4)
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
**    Patient has not undergone an opioid misuse assessment procedure in the last 90 days
**  Then
**    Recommend performing opioid misuse assessment
**      Will schedule opioid misuse assessment
**      Benefits outweigh risks, snooze 3 months
**      N/A - see comment, snooze 3 months
**
*/

// META: Plan Definition: http://fhir.org/guides/cdc/opioid-cds-r4/PlanDefinition/opioid-cds-07

parameter ContextPrescriptions List<MedicationRequest>

context Patient

define "Risk Assessment Interval":
  Interval[Today() - 91 days, Today() - 1 day]

define "Previous 10 Days Interval":
  Interval[Today() - 10 days, Today()]

define "Encounter Interval":
  Interval[Today() - 12 months - 1 days, Today() - 1 day]

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
    and not exists ( "Opioid Risk Assessment in Past 90 Days" )
    and (
      "Prescribed Opioids for 7 of Past 10 Days with an Encounter"
        or Common."Prescribed Opioids for 21 or more of 30 Days for each of the past 3 Months"
    )

define "Exclusion Criteria":
  Common."End of Life Assessment"

define "Opioid Risk Assessment in Past 90 Days":
  [Procedure: Common."Opioid misuse assessment procedure"] riskAssessment
    where start of (riskAssessment.performed as FHIR.Period) in day of "Risk Assessment Interval"

define "Prescribed Opioids for 7 of Past 10 Days with an Encounter":
  Common."Days on Opioids during Period"( "Previous 10 Days Interval" ) >= 7
    and exists (
      [Encounter: Common."Office Visit"] AmbulatoryEncounter
        where AmbulatoryEncounter.period during "Encounter Interval"
    )

define "Get Indicator":
  if "Is Recommendation Applicable?"
    then 'warning'
  else null

define "Get Summary":
  if "Is Recommendation Applicable?"
    then 'Patients on opioid therapy should be evaluated for benefits and harms within 1 to 4 weeks of starting opioid therapy and every 3 months or more subsequently.'
  else null

define "Get Detail":
  if "Is Recommendation Applicable?"
    then
      if Common."Prescribed Opioids for 21 or more of 30 Days for each of the past 3 Months"
        then 'No evaluation for benefits and harms associated with opioid therapy has been performed for the patient in the past 3 months'
      else 'No evaluation for benefits and harms has been performed for the patient starting opioid therapy'
  else null
