library BCSComponent version '0.0.001'

using FHIR version '4.0.1'

include FHIRHelpers version '4.0.001' called FHIRHelpers
include SupplementalDataElementsFHIR4 version '2.0.000' called SDE
include MATGlobalCommonFunctionsFHIR4 version '5.0.000' called Global
include AdultOutpatientEncountersFHIR4 version '2.0.000' called AdultOutpatientEncounters
include HospiceFHIR4 version '2.0.000' called Hospice
include AdvancedIllnessandFrailtyExclusionECQMFHIR4 version '5.12.000' called Frailty

codesystem "LOINC": 'http://loinc.org'
codesystem "SNOMEDCT": 'http://snomed.info/sct'

valueset "Bilateral Mastectomy": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113883.3.464.1003.198.12.1005'
valueset "Ethnicity": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.114222.4.11.837'
valueset "Female": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113883.3.560.100.2'
valueset "History of bilateral mastectomy": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113883.3.464.1003.198.12.1068'
valueset "Left": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113883.3.464.1003.122.12.1036'
valueset "Mammography": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113883.3.464.1003.108.12.1018'
valueset "ONC Administrative Sex": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113762.1.4.1'
valueset "Payer": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.114222.4.11.3591'
valueset "Race": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.114222.4.11.836'
valueset "Right": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113883.3.464.1003.122.12.1035'
valueset "Status Post Left Mastectomy": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113883.3.464.1003.198.12.1069'
valueset "Status Post Right Mastectomy": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113883.3.464.1003.198.12.1070'
valueset "Unilateral Mastectomy Left": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113883.3.464.1003.198.12.1133'
valueset "Unilateral Mastectomy Right": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113883.3.464.1003.198.12.1134'
valueset "Unilateral Mastectomy, Unspecified Laterality": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113883.3.464.1003.198.12.1071'

code "Birth date": '21112-8' from "LOINC" display 'Birth date'
code "Left (qualifier value)": '7771000' from "SNOMEDCT" display 'Left (qualifier value)'
code "Right (qualifier value)": '24028007' from "SNOMEDCT" display 'Right (qualifier value)'

parameter "Measurement Period" Interval<DateTime>

context Patient

define "SDE Ethnicity":
  SDE."SDE Ethnicity"

define "SDE Payer":
  SDE."SDE Payer"

define "SDE Race":
  SDE."SDE Race"

define "SDE Sex":
  SDE."SDE Sex"

define "Initial Population":
  Patient.gender = 'female'
      and Global."CalendarAgeInYearsAt" ( FHIRHelpers.ToDate ( Patient.birthDate ), start of "Measurement Period" ) in Interval[51, 74 )
      and exists AdultOutpatientEncounters."Qualifying Encounters"

define "Denominator":
  "Initial Population"

define "Right Mastectomy Diagnosis":
  ( ( [Condition: "Status Post Right Mastectomy"] )
      union ( [Condition: "Unilateral Mastectomy, Unspecified Laterality"] UnilateralMastectomyDiagnosis
          where UnilateralMastectomyDiagnosis.bodySite in "Right"
      ) ) RightMastectomy
      where Global."Normalize Interval" ( RightMastectomy.onset ) starts on or before
      end of "Measurement Period"

define "Right Mastectomy Procedure":
  [Procedure: "Unilateral Mastectomy Right"] UnilateralMastectomyRightPerformed
      where Global."Normalize Interval" ( UnilateralMastectomyRightPerformed.performed ) ends on or before
      end of "Measurement Period"
        and UnilateralMastectomyRightPerformed.status = 'completed'

define "Left Mastectomy Procedure":
  [Procedure: "Unilateral Mastectomy Left"] UnilateralMastectomyLeftPerformed
      where Global."Normalize Interval" ( UnilateralMastectomyLeftPerformed.performed ) ends on or before
      end of "Measurement Period"
        and UnilateralMastectomyLeftPerformed.status = 'completed'

define "Bilateral Mastectomy Diagnosis":
  [Condition: "History of bilateral mastectomy"] BilateralMastectomyHistory
      where Global."Normalize Interval" ( BilateralMastectomyHistory.onset ) starts on or before
      end of "Measurement Period"

define "Bilateral Mastectomy Procedure":
  [Procedure: "Bilateral Mastectomy"] BilateralMastectomyPerformed
      where Global."Normalize Interval" ( BilateralMastectomyPerformed.performed ) ends on or before
      end of "Measurement Period"
        and BilateralMastectomyPerformed.status = 'completed'

define "Numerator":
  exists ( [DiagnosticReport: "Mammography"] Mammogram
        where ( Global."Normalize Interval" ( Mammogram.effective ) ends 27 months or less on or before
          end of "Measurement Period"
        )
          and Mammogram.status in { 'final', 'amended', 'corrected', 'appended' }
    )

define "Denominator Exclusion":
  Hospice."Has Hospice"
      or ( ( exists "Right Mastectomy Diagnosis"
            or exists "Right Mastectomy Procedure"
        )
          and ( exists "Left Mastectomy"
              or exists "Left Mastectomy Procedure"
          )
      )
      or exists "Bilateral Mastectomy Diagnosis"
      or exists "Bilateral Mastectomy Procedure"
      or Frailty."Advanced Illness and Frailty Exclusion Not Including Over Age 80"
      or ( Global."CalendarAgeInYearsAt" ( FHIRHelpers.ToDate ( Patient.birthDate ), start of "Measurement Period" ) >= 65
          and Frailty."Long Term Care Periods Longer Than 90 Consecutive Days"
      )

define "Left Mastectomy":
  ( ( [Condition: "Status Post Left Mastectomy"] )
      union ( [Condition: "Unilateral Mastectomy, Unspecified Laterality"] UnilateralMastectomyDiagnosis
          where UnilateralMastectomyDiagnosis.bodySite in "Left"
      ) ) LeftMastectomy
      where Global."Normalize Interval" ( LeftMastectomy.onset ) starts on or before
      end of "Measurement Period"
