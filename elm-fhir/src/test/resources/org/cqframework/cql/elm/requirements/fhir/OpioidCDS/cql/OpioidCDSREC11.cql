library OpioidCDSREC11 version '2.0.1'

using FHIR version '4.0.1'

include FHIRHelpers version '4.0.1' called FHIRHelpers
include OpioidCDSCommon version '2.0.1' called Common
include OpioidCDSRoutines version '2.0.1' called Routines

/*
**
**  Recommendation #11
**    Clinicians should avoid prescribing opioid pain medication and benzodiazepines
**    concurrently whenever possible (recommendation category: A, evidence type: 3)
**
**  When
**    Provider is prescribing an opioid analgesic with ambulatory misuse potential in the outpatient setting
**    Provider is prescribing a benzodiazepine medication
**    Opioid review is useful for this patient:
**      Patient is 18 or over
**      Patient does not have findings indicating limited life expectancy
**      Patient does not have orders for therapies indicating end of life care
**      Patient is not undergoing active cancer treatment:
**        Patient has had at least 2 encounters within the past year with any diagnosis of cancer
**    Patient prescribed opioid analgesic with ambulatory misuse potential and benzodiazepine medication concurrently
**  Then
**    Recommend to avoid prescribing opioid pain medication and benzodiazepine concurrently
**      Will revise
**      Benefits outweigh risks, snooze 3 months
**      N/A - see comment, snooze 3 months
**
*/

// META: Plan Definition: http://fhir.org/guides/cdc/opioid-cds-r4/PlanDefinition/opioid-cds-11

parameter ContextPrescriptions List<MedicationRequest>

context Patient

define "Opioid Analgesic with Ambulatory Misuse Potential Prescriptions":
  Common."Is Opioid Analgesic with Ambulatory Misuse Potential?"( ContextPrescriptions )

define "Benzodiazepine Prescriptions":
  Common."Is Benzodiazepine?"( ContextPrescriptions )

define "Patient Is Being Prescribed Opioid Analgesic with Ambulatory Misuse Potential":
  exists( "Opioid Analgesic with Ambulatory Misuse Potential Prescriptions" )

define "Patient Is Being Prescribed Benzodiazepine":
  exists( "Benzodiazepine Prescriptions" )

define "Is Recommendation Applicable?":
  "Inclusion Criteria"
    and not "Exclusion Criteria"

define "Inclusion Criteria":
  (
    (
      "Patient Is Being Prescribed Opioid Analgesic with Ambulatory Misuse Potential"
        and exists Common."Active Ambulatory Benzodiazepine Rx"
    )
      or (
        "Patient Is Being Prescribed Benzodiazepine"
          and exists Common."Active Ambulatory Opioid Rx"
      )
  )
    and Routines."Is Opioid Review Useful?"

define "Exclusion Criteria":
  Common."End of Life Assessment"

define "Get Indicator":
  if "Is Recommendation Applicable?"
    then 'warning'
  else null

define "Get Summary":
  if "Is Recommendation Applicable?"
    then 'Avoid prescribing opioid pain medication and benzodiazepine concurrently whenever possible.'
  else null

define "Get Detail":
  if "Is Recommendation Applicable?"
    then
      if "Patient Is Being Prescribed Benzodiazepine"
        then 'The benzodiazepine prescription request is concurrent with an active opioid prescription'
      else 'The opioid prescription request is concurrent with an active benzodiazepine prescription'
  else null
