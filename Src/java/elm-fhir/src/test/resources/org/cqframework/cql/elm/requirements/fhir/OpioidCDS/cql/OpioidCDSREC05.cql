library OpioidCDSREC05 version '2.0.1'

using FHIR version '4.0.1'

include OpioidCDSCommon version '2.0.1' called Common
include OpioidCDSRoutines version '2.0.1' called Routines

/*
**
** Recommendation #5
**  When opioids are started, providers should prescribe the lowest effective dosage.
**  Providers should use caution when prescribing opioids at any dosage, should
**  carefully reassess evidence of individual benefits and risks when considering
**  increasing dosage to ≥50 morphine milligram equivalents (MME)/day, and should
**  avoid increasing dosage to ≥90 MME/day or carefully justify a decision to titrate
**  dosage to >90 MME/day (recommendation category: A, evidence type: 3)
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
**    Patient Morphine Milligram Equivalent (MME) greater than or equal to 50
**  Then
**    Recommend to consider tapering dosage when MME is greater than or equal to 50 and less than 90 or taper dosage now when MME is greater than or equal to 90:
**      Will reduce dosage
**      Benefits outweigh risks, snooze 3 months
**      Active pain, snooze 1 month
**      N/A - see comment, snooze 3 months
**
*/

// META: Plan Definition: http://fhir.org/guides/cdc/opioid-cds-r4/PlanDefinition/opioid-cds-05

parameter ContextPrescriptions List<MedicationRequest>

context Patient

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
    and "Total MME" >= 50 'mg/d'

define "Exclusion Criteria":
  Common."End of Life Assessment"

define "Total MME":
  Common.TotalMME(
    (
      "Chronic Pain Opioid Analgesic with Ambulatory Misuse Potential Prescriptions"
        union Common."Active Ambulatory Opioid Rx"
    ) AmbulatoryOpioidPrescription
      where Routines."Is Chronic Pain Prescription?"( AmbulatoryOpioidPrescription )
  )

define "Taper Now": "Total MME".value >= 90

define "Consider Tapering": "Total MME".value < 90

define "Get Indicator":
  if "Is Recommendation Applicable?"
    then 'warning'
  else null

define "Get Summary":
  if "Is Recommendation Applicable?"
    then
      'High risk for opioid overdose - '
        + case when "Taper Now"
           then 'taper now'
           else 'consider tapering'
         end
  else null

define "Get Detail":
  if "Is Recommendation Applicable?"
    then'Total morphine milligram equivalent (MME) is ' + ToString("Total MME") + '. Taper to less than 50.'
  else null
