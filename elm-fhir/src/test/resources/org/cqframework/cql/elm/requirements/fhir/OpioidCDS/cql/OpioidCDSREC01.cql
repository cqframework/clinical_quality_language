library OpioidCDSREC01 version '2.0.1'

using FHIR version '4.0.1'

include FHIRHelpers version '4.0.1' called FHIRHelpers
include OpioidCDSCommon version '2.0.1' called Common
include OpioidCDSRoutines version '2.0.1' called Routines

/*
**
**  Recommendation #1
**    Nonpharmacologic therapy and nonopioid pharmacologic therapy are preferred
**    for chronic pain. Clinicians should consider opioid therapy only if expected
**    benefits for both pain and function are anticipated to outweigh risks to the
**    patient. If opioids are used, they should be combined with nonpharmacologic
**    therapy and nonopioid pharmacologic therapy, as appropriate
**    (recommendation category: A, evidence type: 3).
**
**  When
**    Prescription is for treating chronic pain
**    Provider is prescribing an opioid with ambulatory misuse potential in the outpatient setting
**    Opioid review is useful for this patient:
**      Patient is 18 or over
**      Patient does not have findings indicating limited life expectancy
**      Patient does not have orders for therapies indicating end of life care
**      Patient is not undergoing active cancer treatment:
**        Patient has had at least 2 encounters within the past year with any diagnosis of cancer
**    Patient is opioid naive:
**      No indication of opioid use in the past 90 days from a prescription, self report, or a dispensing event
**  Then
**    Recommend the use of nonpharmacologic therapy and nonopioid pharmacologic therapy as an alternative. Potential actions include:
**      Document reason for opioid use
**      Document that alternative therapy was attempted, snooze 3 months
**      N/A - see comment, snooze 3 months
**      Select from order set for alternative therapies
**      Remove triggering medication
**
*/

// META: Plan Definition: http://fhir.org/guides/cdc/opioid-cds-r4/PlanDefinition/opioid-cds-01

parameter ContextPrescriptions List<FHIR.MedicationRequest>

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
    and Routines."Is Opioid Naive?"

define "Exclusion Criteria":
  false

define "Get Indicator":
  if "Is Recommendation Applicable?"
    then 'warning'
  else null

define "Get Summary":
  if "Is Recommendation Applicable?"
    // NOTE: Wordsmithing here
    then 'Recommend use of nonpharmacologic therapy and nonopioid pharmacologic therapy as alternative'
  else null

define "Get Detail":
  if "Is Recommendation Applicable?"
    then 'Medication requests(s): ' + Combine(Common.GetMedicationNames("Chronic Pain Opioid Analgesic with Ambulatory Misuse Potential Prescriptions"), ', ')
  else null
