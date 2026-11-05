/*
NOTE: Preliminary QICore 6.0.0 uplift to support connectathon 38 testing
*/
library PCSBMIScreenAndFollowUpFHIR version '0.2.000'

using QICore version '6.0.0'

include FHIRHelpers version '4.0.1' called FHIRHelpers
include QICoreCommon version '3.0.000' called QICoreCommon
include SupplementalDataElements version '4.0.000' called SDE
include Hospice version '7.0.000' called Hospice
include PalliativeCare version '2.0.000' called PalliativeCare

codesystem "ActCode": 'http://terminology.hl7.org/CodeSystem/v3-ActCode'
codesystem "ICD10CM": 'http://hl7.org/fhir/sid/icd-10-cm'
codesystem "LOINC": 'http://loinc.org'
codesystem "ObservationCategoryCodes": 'http://terminology.hl7.org/CodeSystem/observation-category'

valueset "Encounter to Evaluate BMI": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113883.3.600.1.1751'
valueset "Follow Up for Above Normal BMI": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113883.3.600.1.1525'
valueset "Follow Up for Below Normal BMI": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113883.3.600.1.1528'
valueset "Medical Reason": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113883.3.526.3.1007'
valueset "Medications for Above Normal BMI": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113883.3.526.3.1561'
valueset "Medications for Below Normal BMI": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113883.3.526.3.1562'
valueset "Overweight or Obese": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113762.1.4.1047.502'
valueset "Patient Declined": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113883.3.526.3.1582'
valueset "Pregnancy or Other Related Diagnoses": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113883.3.600.1.1623'
valueset "Referrals Where Weight Assessment May Occur": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113883.3.600.1.1527'
valueset "Underweight": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113883.3.526.3.1563'

code "Body mass index (BMI) [Ratio]": '39156-5' from "LOINC" display 'Body mass index (BMI) [Ratio]'
code "Encounter for palliative care": 'Z51.5' from "ICD10CM" display 'Encounter for palliative care'
code "Functional Assessment of Chronic Illness Therapy - Palliative Care Questionnaire (FACIT-Pal)": '71007-9' from "LOINC" display 'Functional Assessment of Chronic Illness Therapy - Palliative Care Questionnaire (FACIT-Pal)'
code "virtual": 'VR' from "ActCode" display 'virtual'

parameter "Measurement Period" Interval<DateTime> default Interval[@2025-01-01T00:00:00Z, @2026-01-01T00:00:00Z)

context Patient

define "Initial Population":
  exists "Qualifying Encounter during Day of Measurement Period" QualifyingEncounter
    where "AgeInYearsAt"(date from start of QualifyingEncounter.period) >= 18

define "Denominator":
  "Initial Population"

define "Denominator Exclusions":
  Hospice."Has Hospice Services"
    or PalliativeCare."Has Palliative Care in the Measurement Period"
    or exists "Is Pregnant during Day of Measurement Period"

define "Numerator":
  exists "High BMI and Follow up Provided"
    or exists "Low BMI and Follow up Provided"
    or "Has Normal BMI"

define "Denominator Exceptions":
  exists "Medical Reason for Not Documenting a Follow up Plan for Low or High BMI"
    or exists "Medical Reason or Patient Reason for Not Performing BMI Exam"

define "BMI during Measurement Period":
  [USCoreBMIProfile] BMI
    where BMI.value > 0 'kg/m2'
      and BMI.status in { 'final', 'amended', 'corrected' }
      and BMI.effective.toInterval ( ) during day of "Measurement Period"

define "Documented Low BMI during Measurement Period":
  "BMI during Measurement Period" BMI
    where BMI.effective.toInterval ( ) during day of "Measurement Period"
      and BMI.value < 18.5 'kg/m2'

define "Documented High BMI during Measurement Period":
  "BMI during Measurement Period" BMI
    where BMI.effective.toInterval ( ) during day of "Measurement Period"
      and BMI.value >= 25 'kg/m2'

define "Has Normal BMI":
  exists ( "BMI during Measurement Period" BMI
      where BMI.value >= 18.5 'kg/m2'
        and BMI.value < 25 'kg/m2'
  )
    and not ( exists "Documented High BMI during Measurement Period"
        or exists "Documented Low BMI during Measurement Period"
    )

define "High BMI and Follow up Provided":
  "Documented High BMI during Measurement Period" HighBMI
    with ( "High BMI Interventions Ordered"
      union "High BMI Interventions Performed" ) HighBMIInterventions
      such that Coalesce(HighBMIInterventions.performed.toInterval(), HighBMIInterventions.authoredOn.toInterval()) starts during day of "Measurement Period"

define "High BMI Interventions Ordered":
  ( ( [ServiceRequest: "Follow Up for Above Normal BMI"]
      union [ServiceRequest: "Referrals Where Weight Assessment May Occur"]
      union [MedicationRequest: "Medications for Above Normal BMI"] ) HighInterventionsOrdered
      where HighInterventionsOrdered.reasonCode in "Overweight or Obese"
        or ( exists [ConditionProblemsHealthConcerns: "Overweight or Obese"] OverweightObese
            where ( OverweightObese.isProblemListItem ( )
                or OverweightObese.isHealthConcern ( )
            )
              and OverweightObese.isActive ( )
              and OverweightObese.prevalenceInterval ( ) starts before or on day of HighInterventionsOrdered.authoredOn
        )
  )

define "High BMI Interventions Performed":
  ( [Procedure: "Follow Up for Above Normal BMI"] HighInterventionsPerformed
      where HighInterventionsPerformed.reasonCode in "Overweight or Obese"
        or ( exists [ConditionProblemsHealthConcerns: "Overweight or Obese"] OverweightObese
            where OverweightObese.prevalenceInterval ( ) starts before or on day of HighInterventionsPerformed.performed
              and not ( OverweightObese.prevalenceInterval ( ) ends before day of HighInterventionsPerformed.performed )
        )
  )

define "Medical Reason or Patient Reason for Not Performing BMI Exam":
  [ObservationCancelled: code = "Body mass index (BMI) [Ratio]"] NoBMI
    with "Qualifying Encounter during Day of Measurement Period" QualifyingEncounter
      such that NoBMI.effective.toInterval ( ) ends same day as start of QualifyingEncounter.period
    where NoBMI.status = 'cancelled'
      and exists(NoBMI.extension E
        where E.url = 'http://hl7.org/fhir/us/qicore/StructureDefinition/qicore-notDoneReason'
          and (E.value as Code in "Patient Declined"
          or E.value as Code in "Medical Reason"
          )
      )

define "Low BMI Interventions Ordered":
  ( ( [ServiceRequest: "Follow Up for Below Normal BMI"]
      union [ServiceRequest: "Referrals Where Weight Assessment May Occur"]
      union [MedicationRequest: "Medications for Below Normal BMI"] ) LowInterventionsOrdered
      where LowInterventionsOrdered.reasonCode in "Underweight"
        or ( exists [ConditionProblemsHealthConcerns: "Underweight"] Under
            where ( Under.isHealthConcern ( ) )
              and Under.isActive ( )
              and Under.prevalenceInterval ( ) starts before or on day of LowInterventionsOrdered.authoredOn
              and LowInterventionsOrdered.authoredOn during day of "Measurement Period"
        )
  )

define "Low BMI Interventions Performed":
  ( [Procedure: "Follow Up for Below Normal BMI"] LowInterventionsPerformed
      where LowInterventionsPerformed.reasonCode in "Underweight"
        and LowInterventionsPerformed.status = 'completed'
        or ( exists [ConditionProblemsHealthConcerns: "Underweight"] Under
            where ( Under.isHealthConcern ( ) )
              and Under.isActive ( )
              and Under.prevalenceInterval ( ) starts before or on day of LowInterventionsPerformed.performed.toInterval ( )
              and LowInterventionsPerformed.performed.toInterval ( ) during day of "Measurement Period"
              and not ( Under.prevalenceInterval ( ) ends before day of LowInterventionsPerformed.performed.toInterval ( ) )
        )
  )

define "Low BMI and Follow up Provided":
  ( "Documented Low BMI during Measurement Period" LowBMI
      with ( "Low BMI Interventions Ordered"
        union "Low BMI Interventions Performed" ) LowBMIInterventions
        such that Coalesce(LowBMIInterventions.performed.toInterval(), LowBMIInterventions.authoredOn.toInterval()) starts during day of "Measurement Period"
  )

define "Medical Reason for Not Documenting a Follow up Plan for Low or High BMI":
  ( ( [ServiceNotRequested: "Referrals Where Weight Assessment May Occur"]
      union [ServiceNotRequested: "Follow Up for Above Normal BMI"]
      union [ServiceNotRequested: "Follow Up for Below Normal BMI"] ) NoBMIFollowUp
      with "Qualifying Encounter during Day of Measurement Period" QualifyingEncounter
        such that NoBMIFollowUp.authoredOn same day as start of QualifyingEncounter.period
      where NoBMIFollowUp.status ~ 'completed'
        and exists(NoBMIFollowUp.extension E
        where E.url = 'http://hl7.org/fhir/us/qicore/StructureDefinition/qicore-doNotPerformReason'
          and E.value as Code in "Medical Reason"
      )
  )
    union ( ( [MedicationNotRequested: "Medications for Above Normal BMI"]
        union [MedicationNotRequested: "Medications for Below Normal BMI"] ) NoBMIFollowUp
        with "Qualifying Encounter during Day of Measurement Period" QualifyingEncounter
          such that NoBMIFollowUp.authoredOn same day as start of QualifyingEncounter.period
        where NoBMIFollowUp.status ~ 'completed'
          and NoBMIFollowUp.reasonCode in "Medical Reason"
    )

define "Is Pregnant during Day of Measurement Period":
  ([ConditionProblemsHealthConcerns: "Pregnancy or Other Related Diagnoses"]
    union [ConditionEncounterDiagnosis: "Pregnancy or Other Related Diagnoses"]) PregnancyDiagnosis
    with "Qualifying Encounter during Day of Measurement Period" QualifyingEncounter
      such that PregnancyDiagnosis.clinicalStatus ~ QICoreCommon."active"
        and PregnancyDiagnosis.prevalenceInterval ( ) overlaps day of "Measurement Period"

define "Qualifying Encounter during Day of Measurement Period":
  [Encounter: type in "Encounter to Evaluate BMI"] BMIEncounter
    where BMIEncounter.period during day of "Measurement Period"
      and BMIEncounter.class !~ "virtual"
      and BMIEncounter.status = 'finished'

define "SDE Ethnicity":
  SDE."SDE Ethnicity"

define "SDE Payer":
  SDE."SDE Payer"

define "SDE Race":
  SDE."SDE Race"

define "SDE Sex":
  SDE."SDE Sex"