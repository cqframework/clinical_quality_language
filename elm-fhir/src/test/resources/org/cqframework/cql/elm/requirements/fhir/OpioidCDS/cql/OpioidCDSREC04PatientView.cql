library OpioidCDSREC04PatientView version '2.0.1'

using FHIR version '4.0.1'

include FHIRHelpers version '4.0.1' called FHIRHelpers
include OpioidCDSCommon version '2.0.1' called Common
include OpioidCDSRoutines version '2.0.1' called Routines

/*
**
**  Recommendation #4
**    Prescribe immediate-release opioids instead of extended-release/long-acting (ER/LA) opioids
**
**  When
**    Provider is prescribing an extended release opioid with ambulatory misuse potential in the outpatient setting
**    Prescription is for treating chronic pain.
**    Opioid review is useful for this patient:
**      Patient is 18 or over
**      Patient does not have findings indicating limited life expectancy
**      Patient does not have orders for therapies indicating end of life care
**      Patient is not undergoing active cancer treatment:
**        Patient has had at least 2 encounters within the past year with any diagnosis of cancer
**    Patient is opioid naive:
**      No indication of opioid use in the past 90 days from a prescription, self report, or a dispensing event
**  Then
**    Recommend use of immediate release opioids:
**      Will prescribe immediate release opioid analgesic with ambulatory misuse potential
**      Benefits outweigh risks, snooze 3 months
**      N/A - see comment, snooze 3 months
**
*/

// META: Plan Definition: http://fhir.org/guides/cdc/opioid-cds-r4/PlanDefinition/opioid-cds-04

context Patient

define "Chronic Pain Extended Release Opioid Analgesic with Ambulatory Misuse Potential Prescriptions":
  Common."Active Ambulatory Opioid Rx" AmbulatoryOpioidPrescription
    where Routines."Is Chronic Pain Prescription?"(AmbulatoryOpioidPrescription)
      and AmbulatoryOpioidPrescription.medication in Common."Extended release opioid with ambulatory misuse potential"

define "Patient Is Being Prescribed Extended Release Opioid Analgesic with Ambulatory Misuse Potential":
  exists ("Chronic Pain Extended Release Opioid Analgesic with Ambulatory Misuse Potential Prescriptions")

define "Is Recommendation Applicable?":
  "Inclusion Criteria"
    and not "Exclusion Criteria"

define "Inclusion Criteria":
  "Patient Is Being Prescribed Extended Release Opioid Analgesic with Ambulatory Misuse Potential"
    and Routines."Is Opioid Review Useful?"
    and Routines."Is Opioid Naive?"

define "Exclusion Criteria":
  Common."End of Life Assessment"

define "Get Indicator":
  if "Is Recommendation Applicable?"
    then 'warning'
  else null

define "Get Summary":
  if "Is Recommendation Applicable?"
    then 'Recommend use of immediate-release opioids instead of extended release/long acting opioids when starting patient on opioids.'
  else null

define "Get Detail":
  if "Is Recommendation Applicable?"
    then 'The following medication requests(s) release rates should be re-evaluated: ' + Combine(Common.GetMedicationNames("Chronic Pain Extended Release Opioid Analgesic with Ambulatory Misuse Potential Prescriptions"), ', ')
  else null
