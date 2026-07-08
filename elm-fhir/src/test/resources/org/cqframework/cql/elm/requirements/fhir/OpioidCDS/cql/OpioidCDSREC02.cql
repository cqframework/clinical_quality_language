library OpioidCDSREC02 version '2.0.1'

using FHIR version '4.0.1'

include FHIRHelpers version '4.0.1' called FHIRHelpers
include OpioidCDSCommon version '2.0.1' called Common
include OpioidCDSCommonConfig version '2.0.1' called Config
include OpioidCDSRoutines version '2.0.1' called Routines

/*
**
**  Recommendation #2
**    Before starting opioid therapy for chronic pain, clinicians should establish
**    treatment goals with all patients, including realistic goals for pain and
**    function, and should consider how opioid therapy will be discontinued if
**    benefits do not outweigh risks. Clinicians should continue opioid therapy
**    only if there is clinically meaningful improvement in pain and function
**    that outweighs risks to patient safety
**    (recommendation category: A, evidence type: 4).
**
**  When
**    Provider is prescribing an opioid with ambulatory misuse potential in the outpatient setting
**    Prescription is for treating chronic pain.
**    Opioid review is useful for this patient:
**      Patient is 18 or over
**      Patient does not have findings indicating limited life expectancy
**      Patient does not have orders for therapies indicating end of life care
**      Patient is not undergoing active cancer treatment:
**        Patient has had at least 2 encounters within the past year with any diagnosis of cancer
**    Patient is opioid naive:
**      No indication of opioid use in the past 90 days from a prescription, self report, or a dispensing event
**    Patient does not have an opioid treatment plan that was created, edited, or reviewed by the prescribing physician in the past 90 days
**  Then
**    Recommend creation, review or update of an opioid treatment plan. Potential actions include:
**      Create treatment plan with goals for opioid use
**      Update patient’s opioid treatment plan
**      Review patient’s opioid treatment plan
**      N/A - see comment, snooze 3 months
**      Taper / Discontinue opioid
**
*/

// META: PlanDefinition: http://fhir.org/guides/cdc/opioid-cds-r4/PlanDefinition/opioid-cds-02

parameter ContextPrescriptions List<MedicationRequest>

context Patient

define "Treatment Plan Lookback Period":
  Interval[Today() - 90 days, Today()]

define "Chronic Pain Opioid Analgesic with Ambulatory Misuse Potential Prescriptions":
  ( Common."Is Opioid Analgesic with Ambulatory Misuse Potential?"( ContextPrescriptions ) ) AmbulatoryOpioidPrescription
    where Routines."Is Chronic Pain Prescription?"( AmbulatoryOpioidPrescription )

define "Patient Is Being Prescribed Opioid Analgesic with Ambulatory Misuse Potential":
  exists( "Chronic Pain Opioid Analgesic with Ambulatory Misuse Potential Prescriptions" )

define "Is Recommendation Applicable?":
  "Inclusion Criteria"

define "Inclusion Criteria":
  "Patient Is Being Prescribed Opioid Analgesic with Ambulatory Misuse Potential"
    and Routines."Is Opioid Naive?"
    and Routines."Is Opioid Review Useful?"
    and "No Opioid Treatment Plan Created, was Edited or Reviewed in Past 90 Days?"

define "No Opioid Treatment Plan Created, was Edited or Reviewed in Past 90 Days?":
  Config."Opioid Treatment Plan Verification Enabled"
    and not (
      exists ( "Opioid Treatment Plan Creation or Update in Past 90 Days" )
        or "Had Opioid Treatment Plan Reviewed in Past 90 Days"
    )

define "Opioid Treatment Plan Creation or Update in Past 90 Days":
  [CarePlan: category in Common."Pain treatment plan"] PainTreatmentPlan
    where PainTreatmentPlan.status in { 'active', 'completed' }
      and (
        FHIRHelpers.ToDateTime( PainTreatmentPlan.created ) during day of "Treatment Plan Lookback Period"
          or PainTreatmentPlan.meta.lastUpdated during day of "Treatment Plan Lookback Period"
      )
      and exists(
        "Chronic Pain Opioid Analgesic with Ambulatory Misuse Potential Prescriptions".recorder Recorder
          where Recorder ~ PainTreatmentPlan.author
      )

define "Had Opioid Treatment Plan Reviewed in Past 90 Days":
  exists (
    [Procedure: Common."Pain management procedure"] PainManagementProcedure
      where PainManagementProcedure.performed during day of "Treatment Plan Lookback Period"
        and exists (
          PainManagementProcedure.performer Performer
            with "Chronic Pain Opioid Analgesic with Ambulatory Misuse Potential Prescriptions" ChronicPainScript
            such that Performer.actor ~ ChronicPainScript.recorder
        )
  )

define "Get Indicator":
  if "Is Recommendation Applicable?"
    then 'warning'
  else null

define "Get Summary":
  if "Is Recommendation Applicable?"
    then 'Recommend creation, review or update of opioid treatment plan'
  else null

define "Get Detail":
  if "Is Recommendation Applicable?"
    then 'Medication requests(s): ' + Combine(Common.GetMedicationNames("Chronic Pain Opioid Analgesic with Ambulatory Misuse Potential Prescriptions"), ', ')
  else null
