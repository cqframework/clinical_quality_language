library EXM165_FHIR4 version '8.5.000'

/*
Controlling High Blood Pressure (CBP)
*/

using QICore version '4.1.0'

include FHIRHelpers version '4.0.1'

include MATGlobalCommonFunctions_QICore4 version '5.0.000' called Common
include AdultOutpatientEncounters_QICore4 version '2.0.000' called AdultOutpatientEncounters
include SupplementalDataElements_QICore4 version '2.0.0' called SDE
include AdvancedIllnessandFrailtyExclusion_QICore4 version '5.0.000' called FrailtyLTI
include Hospice_QICore4 version '2.0.000' called Hospice
//include NCQACommon_FHIR version '3.10.000' called Common
//include Participation_FHIR version '1.0.0'

codesystem "LOINC": 'urn:oid:2.16.840.1.113883.6.1'

valueset "Chronic Kidney Disease, Stage 5": 'urn:oid:2.16.840.1.113883.3.526.3.1002'
valueset "Dialysis Services": 'urn:oid:2.16.840.1.113883.3.464.1003.109.12.1013'
valueset "ED": 'urn:oid:2.16.840.1.113883.3.464.1003.101.12.1085'
valueset "Encounter Inpatient": 'urn:oid:2.16.840.1.113883.3.666.5.307'
valueset "End Stage Renal Disease": 'urn:oid:2.16.840.1.113883.3.526.3.353'
valueset "ESRD Monthly Outpatient Services": 'urn:oid:2.16.840.1.113883.3.464.1003.109.12.1014'
valueset "Essential Hypertension": 'urn:oid:2.16.840.1.113883.3.464.1003.104.12.1011'
valueset "Ethnicity": 'urn:oid:2.16.840.1.114222.4.11.837'
valueset "Kidney Transplant": 'urn:oid:2.16.840.1.113883.3.464.1003.109.12.1012'
valueset "Kidney Transplant Recipient": 'urn:oid:2.16.840.1.113883.3.464.1003.109.12.1029'
valueset "ONC Administrative Sex": 'urn:oid:2.16.840.1.113762.1.4.1'
valueset "Payer": 'urn:oid:2.16.840.1.114222.4.11.3591'
valueset "Pregnancy": 'urn:oid:2.16.840.1.113883.3.526.3.378'
valueset "Race": 'urn:oid:2.16.840.1.114222.4.11.836'
valueset "Vascular Access for Dialysis": 'urn:oid:2.16.840.1.113883.3.464.1003.109.12.1011'

code "Birth date": '21112-8' from "LOINC" display 'Birth date'
code "Blood pressure": '85354-9' from "LOINC" display 'blood pressure'
code "Diastolic blood pressure": '8462-4' from "LOINC" display 'Diastolic blood pressure'
code "Systolic blood pressure": '8480-6' from "LOINC" display 'Systolic blood pressure'

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

define "Has Diastolic Blood Pressure Less Than 90":
	"Lowest Diastolic Reading on Most Recent Blood Pressure Day".value < 90

define "Has Systolic Blood Pressure Less Than 140":
	"Lowest Systolic Reading on Most Recent Blood Pressure Day".value < 140

define "Lowest Diastolic Reading on Most Recent Blood Pressure Day":
	First(
    "Qualifying Diastolic Blood Pressure Reading" DBPReading
		  where DBPReading.effective same day as "Most Recent Blood Pressure Day"
      return singleton from (
        DBPReading.component C
          where C.code ~ "Diastolic blood pressure"
          return C.value as Quantity
      )
			sort ascending
	)

define "Lowest Systolic Reading on Most Recent Blood Pressure Day":
	First(
    "Qualifying Systolic Blood Pressure Reading" SBPReading
		  where SBPReading.effective same day as "Most Recent Blood Pressure Day"
      return singleton from (
        SBPReading.component C
          where C.code ~ "Systolic blood pressure"
          return C.value as Quantity
      )
      sort ascending
	)

define "Blood Pressure Days":
	( ( "Qualifying Diastolic Blood Pressure Reading" DBPExam
				return date from DBPExam.effective
		)
			intersect ( "Qualifying Systolic Blood Pressure Reading" SBPExam
					return date from SBPExam.effective
			)
	)

define "Most Recent Blood Pressure Day":
	Max("Blood Pressure Days")

define "Qualifying Diastolic Blood Pressure Reading":
	["observation-bp"] BloodPressure
	  where BloodPressure.DiastolicBP.value is not null
      and BloodPressure.status in {'final', 'amended'}
      and BloodPressure.effective during "Measurement Period"
      // TODO: Turn this into a value set
      and not (GetEncounter(BloodPressure.encounter).class.code in { 'EMER', 'IMP', 'ACUTE', 'NONAC', 'PRENC', 'SS'})

// TODO: Move to Common Functions
define function GetEncounter(reference Reference):
  singleton from ( [Encounter: id in Common.GetId(reference.reference)] )

define "Qualifying Systolic Blood Pressure Reading":
	["observation-bp"] BloodPressure
	  where BloodPressure.status in {'final', 'amended'}
      and BloodPressure.effective during "Measurement Period"
      // TODO: Turn this into a value set
	    and not (GetEncounter(BloodPressure.encounter).class.code in { 'EMER', 'IMP', 'ACUTE', 'NONAC', 'PRENC', 'SS'})

//Note: Once we can specify that the FHIR BP Profile must be used, then status, code, and unit will not be required

define "Numerator":
	"Has Diastolic Blood Pressure Less Than 90"
		and "Has Systolic Blood Pressure Less Than 140"

define "Initial Population":
	Common.CalendarAgeInYearsAt(ToDate(Patient.birthDate), end of "Measurement Period") between 18 and 85
		and exists "Essential Hypertension Diagnosis"
		and exists AdultOutpatientEncounters."Qualifying Encounters"

define "Pregnancy Or Renal Diagnosis Exclusions":
	( ["Condition": "Pregnancy"]
		union ["Condition": "End Stage Renal Disease"]
		union ["Condition": "Kidney Transplant Recipient"]
		union ["Condition": "Chronic Kidney Disease, Stage 5"] ) PregnancyESRDDiagnosis
      where PregnancyESRDDiagnosis.verificationStatus = Common."confirmed"
        and PregnancyESRDDiagnosis.recordedDate before end of "Measurement Period"

define "Denominator":
	"Initial Population"

define "End Stage Renal Disease Encounter":
	["Encounter": "ESRD Monthly Outpatient Services"] ESRDEncounter
		where ESRDEncounter.status in { 'arrived', 'triaged', 'in-progress', 'onleave', 'finished' }
		  and ESRDEncounter.period starts before end of "Measurement Period"

define "End Stage Renal Disease Procedures":
	( ["Procedure": "Vascular Access for Dialysis"]
		union ["Procedure": "Kidney Transplant"]
		union ["Procedure": "Dialysis Services"] ) ESRDProcedure
		where ESRDProcedure.status = 'completed'
      and Common."Normalize Interval"(ESRDProcedure.performed) starts before end of "Measurement Period"

define "Essential Hypertension Diagnosis":
	["Condition": "Essential Hypertension"] Hypertension
    where Hypertension.verificationStatus = ToConcept(Common."confirmed")
     and (Hypertension.recordedDate 6 months or less on or after start of "Measurement Period"
      or Hypertension.recordedDate before end of "Measurement Period"
    )


// TODO: Translate exclusions
/*
define "Denominator Exclusions":
    Hospice."Has Hospice"
        or exists "Pregnancy Or Renal Diagnosis Exclusions"
        or ( exists "End Stage Renal Disease Procedures"
                or exists "End Stage Renal Disease Encounter"
        )
   or (Common."CalendarAgeInYearsAt"(ToDateTime(Patient.birthDate), start of "Measurement Period")>= 65 )
                and FrailtyLTI."Has Spent More Than 90 Days in Long Term Care"
      or FrailtyLTI."Advanced Illness and Frailty Exclusion Including Over Age 80"
*/