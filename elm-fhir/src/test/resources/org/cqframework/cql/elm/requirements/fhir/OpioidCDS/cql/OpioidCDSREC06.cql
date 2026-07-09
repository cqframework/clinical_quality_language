library OpioidCDSREC06 version '2.0.1'

using FHIR version '4.0.1'

include FHIRHelpers version '4.0.1' called FHIRHelpers
include OpioidCDSCommon version '2.0.1' called Common
include OpioidCDSCommonConfig version '2.0.1' called Config
include OpioidCDSRoutines version '2.0.1' called Routines

/*
**
**  Recommendation #6
**    Long-term opioid use often begins with treatment of acute pain. When opioids
**    are used for acute pain, clinicians should prescribe the lowest effective
**    dose of immediate-release opioids and should prescribe no greater quantity
**    than needed for the expected duration of pain severe enough to require
**    opioids. Three days or less will often be sufficient; more than seven days
**    will rarely be needed (recommendation category: A, evidence type: 4).
**
**  When
**    Provider is prescribing an opioid analgesic with ambulatory misuse potential in the outpatient setting
**    Opioid review is useful for this patient:
**      Patient is 18 or over
**      Patient does not have findings indicating limited life expectancy
**      Patient does not have orders for therapies indicating end of life care
**      Patient is not undergoing active cancer treatment:
**        Patient has had at least 2 encounters within the past year with any diagnosis of cancer
**    Prescription is for treating acute pain.
**    Prescription is for more than 7 days or for an extended release opioid
**  Then
**    Recommend limiting therapy to no more than 3 days with an absolute maximum of 7 days and limit to immediate release opioids. Potential actions include:
**      Change order to ≤ 7 days
**      Document reason for not limiting therapy; snooze 3 months
**      N/A - see comment; snooze 3 months
**
*/

// META: Plan Definition: http://fhir.org/guides/cdc/opioid-cds-r4/PlanDefinition/opioid-cds-06

parameter ContextPrescriptions List<MedicationRequest>

context Patient

define "Acute Pain Opioid Analgesic with Ambulatory Misuse Potential Prescriptions":
  ( Common."Is Opioid Analgesic with Ambulatory Misuse Potential?"( ContextPrescriptions ) ) AmbulatoryOpioidPrescription
    where Routines."Is Acute Pain Prescription?"( AmbulatoryOpioidPrescription )

define "Patient Is Being Prescribed Opioid Analgesic with Ambulatory Misuse Potential for Acute Pain":
  exists( "Acute Pain Opioid Analgesic with Ambulatory Misuse Potential Prescriptions" )

define "Is Recommendation Applicable?":
  "Inclusion Criteria"
    and not "Exclusion Criteria"

define "Inclusion Criteria":
  "Patient Is Being Prescribed Opioid Analgesic with Ambulatory Misuse Potential for Acute Pain"
    and Routines."Is Opioid Review Useful?"
    and "Order is for Long-Acting Opioid"

define "Exclusion Criteria":
  false

define "Order is for Long-Acting Opioid":
  exists (
    "Acute Pain Opioid Analgesic with Ambulatory Misuse Potential Prescriptions" AmbulatoryOpioidPrescription
      where AmbulatoryOpioidPrescription.medication in Common."Extended release opioid with ambulatory misuse potential"
        or Common.GetDurationInDays( AmbulatoryOpioidPrescription.dispenseRequest.expectedSupplyDuration ) > 7
  )

define "Get Indicator":
  if "Is Recommendation Applicable?"
    then 'warning'
  else null

define "Get Summary":
  if "Is Recommendation Applicable?"
    then 'Recommend limit therapy to <= 3 days to maximum of <= 7 days and to limit to immediate release opioids'
  else null

  define "Get Detail":
    if "Is Recommendation Applicable?"
      then 'Medication requests(s): ' + Combine(Common.GetMedicationNames("Acute Pain Opioid Analgesic with Ambulatory Misuse Potential Prescriptions"), ', ')
    else null
