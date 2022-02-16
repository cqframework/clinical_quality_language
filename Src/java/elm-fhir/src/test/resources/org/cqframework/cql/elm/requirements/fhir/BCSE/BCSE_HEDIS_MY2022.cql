library BCSE_HEDIS_MY2022 version '1.0.0'

using FHIR version '4.0.1'

include FHIRHelpers version '4.0.1' called FHIRHelpers
include NCQA_HealthPlanEnrollment version '1.0.0' called Enrollment
include NCQA_Status version '1.0.0' called Status
include NCQA_FHIRBase version '1.0.0' called FHIRBase
include NCQA_Hospice version '1.0.0' called Hospice
include NCQA_AdvancedIllnessandFrailty version '1.0.0' called AdvancedIllnessFrailty
include NCQA_PalliativeCare version '1.0.0' called PalliativeCare

valueset "Absence of Left Breast": 'https://www.ncqa.org/fhir/valueset/2.16.840.1.113883.3.464.1004.1329'
valueset "Absence of Right Breast": 'https://www.ncqa.org/fhir/valueset/2.16.840.1.113883.3.464.1004.1330'
valueset "Bilateral Mastectomy": 'https://www.ncqa.org/fhir/valueset/2.16.840.1.113883.3.464.1004.1042'
valueset "Bilateral Modifier": 'https://www.ncqa.org/fhir/valueset/2.16.840.1.113883.3.464.1004.1043'
valueset "Clinical Bilateral Modifier": 'https://www.ncqa.org/fhir/valueset/2.16.840.1.113883.3.464.1004.1951'
valueset "Clinical Left Modifier": 'https://www.ncqa.org/fhir/valueset/2.16.840.1.113883.3.464.1004.1949'
valueset "Clinical Right Modifier": 'https://www.ncqa.org/fhir/valueset/2.16.840.1.113883.3.464.1004.1950'
valueset "Clinical Unilateral Mastectomy": 'https://www.ncqa.org/fhir/valueset/2.16.840.1.113883.3.464.1004.1948'
valueset "History of Bilateral Mastectomy": 'https://www.ncqa.org/fhir/valueset/2.16.840.1.113883.3.464.1004.1331'
valueset "Left Modifier": 'https://www.ncqa.org/fhir/valueset/2.16.840.1.113883.3.464.1004.1148'
valueset "Mammography": 'https://www.ncqa.org/fhir/valueset/2.16.840.1.113883.3.464.1004.1168'
valueset "Right Modifier": 'https://www.ncqa.org/fhir/valueset/2.16.840.1.113883.3.464.1004.1230'
valueset "Unilateral Mastectomy": 'https://www.ncqa.org/fhir/valueset/2.16.840.1.113883.3.464.1004.1256'
valueset "Unilateral Mastectomy Left": 'https://www.ncqa.org/fhir/valueset/2.16.840.1.113883.3.464.1004.1334'
valueset "Unilateral Mastectomy Right": 'https://www.ncqa.org/fhir/valueset/2.16.840.1.113883.3.464.1004.1335'

parameter "Measurement Period" Interval<DateTime>

context Patient

define "Initial Population":
  AgeInYearsAt(date from 
    end of "Measurement Period"
  )in Interval[52, 74]
    and Patient.gender.value = 'female'
    and "Enrolled During Participation Period"

define "Enrolled During Participation Period":
  Enrollment."Health Plan Enrollment Criteria" ( "Member Coverage", date from 
  end of "Measurement Period", Interval[date from "October 1 Two Years Prior to the Measurement Period", date from 
  end of "Measurement Period" - 2 years], 0 )
    and Enrollment."Health Plan Enrollment Criteria" ( "Member Coverage", date from 
    end of "Measurement Period", Interval[date from start of "Measurement Period" - 1 year, date from 
    end of "Measurement Period" - 1 year], 45 )
    and Enrollment."Health Plan Enrollment Criteria" ( "Member Coverage", date from 
    end of "Measurement Period", Interval[date from start of "Measurement Period", date from 
    end of "Measurement Period"], 45 )

define "Member Coverage":
  [Coverage] C
    where FHIRBase."Normalize Interval" ( C.period ) overlaps "Participation Period"

define "Participation Period":
  Interval["October 1 Two Years Prior to the Measurement Period", 
  end of "Measurement Period"]

define "October 1 Two Years Prior to the Measurement Period":
  DateTime((year from start of "Measurement Period" - 2), 10, 1, 0, 0, 0, 0, 0)

define "Denominator":
  "Initial Population"

define "Exclusions":
  Hospice."Hospice Intervention or Encounter"
    or "Mastectomy Exclusion"
    or AdvancedIllnessFrailty."Advanced Illness and Frailty Exclusion Not Including Over Age 80"
    or PalliativeCare."Palliative Care Overlapping Period" ( "Measurement Period" )

define "Mastectomy Exclusion":
  ( ( exists "Right Mastectomy Diagnosis"
        or exists "Right Mastectomy Procedure"
    )
      and ( exists "Left Mastectomy Diagnosis"
          or exists "Left Mastectomy Procedure"
      )
  )
    or exists "Bilateral Mastectomy Diagnosis"
    or exists "Bilateral Mastectomy Procedure"

define "Right Mastectomy Diagnosis":
  ( Status."Active Condition" ( [Condition: "Absence of Right Breast"] ) ) RightMastectomyDiagnosis
    where FHIRBase."Prevalence Period" ( RightMastectomyDiagnosis ) starts on or before 
    end of "Measurement Period"

define "Right Mastectomy Procedure":
  ( ( Status."Completed Procedure" ( [Procedure: "Unilateral Mastectomy Right"] ) )
    union ( ( Status."Completed Procedure" ( [Procedure: "Unilateral Mastectomy"] ) ) UnilateralMastectomyProcedure
        where UnilateralMastectomyProcedure.bodySite in "Right Modifier"
    )
    union ( ( Status."Completed Procedure" ( [Procedure: "Clinical Unilateral Mastectomy"] ) ) ClinicalUnilateralMastectomyProcedure
        where ClinicalUnilateralMastectomyProcedure.bodySite in "Clinical Right Modifier"
    ) ) RightMastectomyProcedure
    where FHIRBase."Normalize Interval" ( RightMastectomyProcedure.performed ) ends on or before 
    end of "Measurement Period"

define "Left Mastectomy Diagnosis":
  ( Status."Active Condition" ( [Condition: "Absence of Left Breast"] ) ) LeftMastectomyDiagnosis
    where FHIRBase."Prevalence Period" ( LeftMastectomyDiagnosis ) starts on or before 
    end of "Measurement Period"

define "Left Mastectomy Procedure":
  ( ( Status."Completed Procedure" ( [Procedure: "Unilateral Mastectomy Left"] ) )
    union ( ( Status."Completed Procedure" ( [Procedure: "Unilateral Mastectomy"] ) ) UnilateralMastectomyProcedure
        where UnilateralMastectomyProcedure.bodySite in "Left Modifier"
    )
    union ( ( Status."Completed Procedure" ( [Procedure: "Clinical Unilateral Mastectomy"] ) ) ClinicalUnilateralMastectomyProcedure
        where ClinicalUnilateralMastectomyProcedure.bodySite in "Clinical Left Modifier"
    ) ) LeftMastectomyProcedure
    where FHIRBase."Normalize Interval" ( LeftMastectomyProcedure.performed ) ends on or before 
    end of "Measurement Period"

define "Bilateral Mastectomy Diagnosis":
  ( Status."Active Condition" ( [Condition: "History of Bilateral Mastectomy"] ) ) BilateralMastectomyHistory
    where FHIRBase."Prevalence Period" ( BilateralMastectomyHistory ) starts on or before 
    end of "Measurement Period"

define "Bilateral Mastectomy Procedure":
  ( ( Status."Completed Procedure" ( [Procedure: "Bilateral Mastectomy"] ) )
    union ( ( Status."Completed Procedure" ( [Procedure: "Unilateral Mastectomy"] ) ) UnilateralMastectomyProcedure
        where UnilateralMastectomyProcedure.bodySite in "Bilateral Modifier"
    )
    union ( ( Status."Completed Procedure" ( [Procedure: "Clinical Unilateral Mastectomy"] ) ) ClinicalUnilateralMastectomyProcedure
        where ClinicalUnilateralMastectomyProcedure.bodySite in "Clinical Bilateral Modifier"
    ) ) BilateralMastectomyPerformed
    where FHIRBase."Normalize Interval" ( BilateralMastectomyPerformed.performed ) ends on or before 
    end of "Measurement Period"

define "Numerator":
  exists ( [Observation: "Mammography"] Mammogram
      where FHIRBase."Normalize Interval" ( Mammogram.effective ) ends during "Participation Period"
  )