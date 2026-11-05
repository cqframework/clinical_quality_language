library OpioidCDSREC08 version '2.0.1'

using FHIR version '4.0.1'

include FHIRHelpers version '4.0.1' called FHIRHelpers
include OpioidCDSCommon version '2.0.1' called Common
include OpioidCDSCommonConfig version '2.0.1' called Config
include OpioidCDSRoutines version '2.0.1' called Routines

/*
**
**  Recommendation #8
**    Before starting and periodically during continuation of opioid therapy,
**    clinicians should evaluate risk factors for opioid-related harms. Clinicians
**    should incorporate into the management plan strategies to mitigate risk,
**    including considering offering naloxone when factors that increase risk for
**    opioid overdose, such as history of overdose, history of substance use disorder,
**    higher opioid dosages (≥50 MME/day), or concurrent benzodiazepine use, are
**    present (recommendation category: A, evidence type: 4)
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
**    Patient is not currently prescribed naloxone medications
**    Factors that increase risk for opioid overdose are present:
**      Concurrent benzodiazepine use
**      High opioid dosages (MME/day >= 50)
**      History of substance abuse
**  Then
**    Recommend offering naloxone medications
**      Will offer naloxone medications
**      Benefits outweigh risks, snooze 3 months
**      N/A - see comment, snooze 3 months
**
*/

// META: Plan Definition: http://fhir.org/guides/cdc/opioid-cds-r4/PlanDefinition/opioid-cds-08

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
    and (
      "Total MME" >= 50 'mg/d'
        or "On Benzodiazepine"
        or "Has Substance Abuse History"
    )

define "Exclusion Criteria":
  (
    Config."Evidence of Naloxone Enabled"
      and "On Naloxone"
  )
    or Common."End of Life Assessment"

define "Total MME":
  Common.TotalMME(
    "Chronic Pain Opioid Analgesic with Ambulatory Misuse Potential Prescriptions"
      union Common."Active Ambulatory Opioid Rx"
  )

define "On Naloxone":
  exists( Common."Active Ambulatory Naloxone Rx" )

define "On Benzodiazepine":
  exists( Common."Active Ambulatory Benzodiazepine Rx" )

define "Has Substance Abuse History":
  exists (
    [Procedure: Common."Substance misuse behavioral counseling"]
    union
    Common."US Core-Categorized Conditions" C
      where C.code in Common."Conditions documenting substance misuse"
  )

define "Get Indicator":
  if "Is Recommendation Applicable?"
    then 'warning'
  else null

define "Get Summary":
  if "Is Recommendation Applicable?"
    then 'Incorporate into the management plan strategies to mitigate risk; including considering offering naloxone when factors that increase risk for opioid overdose are present'
  else null

define "Detail Descriptions":
	{
    { hasCondition: "Total MME" >= 50 'mg/d', text: 'Average MME (' + ToString("Total MME") + ') >= 50 mg/d' },
    { hasCondition: "On Benzodiazepine", text: 'concurrent use of benzodiazepine'},
    { hasCondition: "Has Substance Abuse History", text: 'history of alcohol or drug abuse' }
  }

define "Detail List":
	Combine("Detail Descriptions" dd where dd.hasCondition return dd.text, ', ')

define "Get Detail":
  if "Is Recommendation Applicable?"
    then
      'Consider offering naloxone given following risk factor(s) for opioid overdose: '
        + if "Detail List" is not null and "Detail List" != ''
         then "Detail List" + '.'
        else 'N/A.'
  else null
