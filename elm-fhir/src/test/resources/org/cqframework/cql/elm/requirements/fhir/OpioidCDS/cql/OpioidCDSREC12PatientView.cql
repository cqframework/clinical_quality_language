library OpioidCDSREC12PatientView version '2.0.1'

using FHIR version '4.0.1'

include FHIRHelpers version '4.0.1' called FHIRHelpers
include OpioidCDSCommon version '2.0.1' called Common
include OpioidCDSCommonConfig version '2.0.1' called Config

/*
**
**  Recommendation #12
**    Clinicians should offer or arrange evidence-based treatment (usually
**    medication-assisted treatment with buprenorphine or methadone in combination
**    with behavioral therapies) for patients with opioid use disorder
**    (recommendation category: A, evidence type: 2)
**
**  When
**    Patient is 18 years and older and not receiving evidence-based treatment for
**      opioid use disorder, which may include medication treatment with buprenorphine
**      or methadone, or opioid-specific behavioral counseling
**    Patient has a diagnosis of opioid use disorder in the past 90 days
**  Then
**    Recommend opioid agonist or partial agonist treatment with methadone maintenance
**    therapy, buprenorphine therapy, and/or behavioral therapy. Potential actions include:
**      Order methadone or buprenorphine
**      Refer to qualified treatment provider (i.e. substance disorder specialist)
**      N/A - see comment; snooze 3 months
**
*/

// META: PlanDefinition: http://fhir.org/guides/cdc/opioid-cds-r4/PlanDefinition/plandefinition-OpioidCDSREC12PatientView

context Patient

define "Opioid Use Disorder Lookback Period":
  Interval[Today() - 90 days, Today()]

define "Is Recommendation Applicable?":
  "Inclusion Criteria"
    and not "Exclusion Criteria"

define "Inclusion Criteria":
  "Patient 18 or Older?"
    and "Not Receiving Evidence-Based Treatment for Opioid Use Disorder"
    and "Presence of Diagnosis of Opioid Use Disorder"
    and "Positive Result from Opioid Use Disorder Evaluation Tool"

define "Exclusion Criteria":
  false

define "Patient 18 or Older?":
  Config."Age Less than 18 Years Is Enabled"
    and AgeInYears() >= 18

define "Not Receiving Evidence-Based Treatment for Opioid Use Disorder":
  Config."Evidence Based Treatment Criteria For Opioid Use Disorder"
    and not (
      exists (
        [MedicationRequest: Common."Buprenorphine and methadone medications"] MR
          where MR.status in { 'active', 'completed' }
            and date from MR.authoredOn during day of "Opioid Use Disorder Lookback Period"
      )
      or exists (
        [Procedure: Common."Substance misuse behavioral counseling"] P
          where P.status ~ 'completed'
            and (
              case
                when P.performed is FHIR.dateTime then
                  Interval[FHIRHelpers.ToDateTime(P.performed as FHIR.dateTime), FHIRHelpers.ToDateTime(P.performed as FHIR.dateTime)]
                when P.performed is FHIR.Period then
                  FHIRHelpers.ToInterval(P.performed as FHIR.Period)
                else
                  null as Interval<DateTime>
                end
            ) during day of "Opioid Use Disorder Lookback Period"
      )
    )

define "Presence of Diagnosis of Opioid Use Disorder":
  exists (
    Common."US Core-Categorized Conditions" C
      where C.code in Common."Opioid misuse disorders"
        and exists (
          C.clinicalStatus.coding Coding
            where FHIRHelpers.ToCode(Coding) ~ Common."Active Condition"
        )
        and date from C.recordedDate during day of "Opioid Use Disorder Lookback Period"
    )

define "Positive Result from Opioid Use Disorder Evaluation Tool":
  true

define "Get Indicator":
  if "Is Recommendation Applicable?"
    then 'warning'
  else null

define "Get Summary":
  if "Is Recommendation Applicable?"
    then 'Recommend opioid agonist or partial agonist treatment with methadone maintenance therapy or buprenorphine and/or behavioral therapy'
  else null

define "Get Detail":
  if "Is Recommendation Applicable?"
    then null
  else null
