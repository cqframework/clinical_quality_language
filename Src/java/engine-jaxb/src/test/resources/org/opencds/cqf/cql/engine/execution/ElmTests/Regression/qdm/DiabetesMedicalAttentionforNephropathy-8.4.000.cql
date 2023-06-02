library DiabetesMedicalAttentionforNephropathy version '8.4.000'

using QDM version '5.4'

include Adult_Outpatient_Encounters version '1.2.000' called AdultOutpatientEncounters
include MATGlobalCommonFunctions version '4.0.000' called Global
include Hospice version '2.0.000' called Hospice
include AdvancedIllnessandFrailtyExclusionECQM version '4.0.000' called FrailtyLTI

codesystem "LOINC": 'urn:oid:2.16.840.1.113883.6.1'

valueset "ACE Inhibitor or ARB": 'urn:oid:2.16.840.1.113883.3.526.3.1139'
valueset "Diabetes": 'urn:oid:2.16.840.1.113883.3.464.1003.103.12.1001'
valueset "Diabetic Nephropathy": 'urn:oid:2.16.840.1.113883.3.464.1003.109.12.1004'
valueset "Dialysis Education": 'urn:oid:2.16.840.1.113883.3.464.1003.109.12.1016'
valueset "Dialysis Services": 'urn:oid:2.16.840.1.113883.3.464.1003.109.12.1013'
valueset "ESRD Monthly Outpatient Services": 'urn:oid:2.16.840.1.113883.3.464.1003.109.12.1014'
valueset "Ethnicity": 'urn:oid:2.16.840.1.114222.4.11.837'
valueset "Glomerulonephritis and Nephrotic Syndrome": 'urn:oid:2.16.840.1.113883.3.464.1003.109.12.1018'
valueset "Hypertensive Chronic Kidney Disease": 'urn:oid:2.16.840.1.113883.3.464.1003.109.12.1017'
valueset "Kidney Failure": 'urn:oid:2.16.840.1.113883.3.464.1003.109.12.1028'
valueset "Kidney Transplant": 'urn:oid:2.16.840.1.113883.3.464.1003.109.12.1012'
valueset "ONC Administrative Sex": 'urn:oid:2.16.840.1.113762.1.4.1'
valueset "Other Services Related to Dialysis": 'urn:oid:2.16.840.1.113883.3.464.1003.109.12.1015'
valueset "Payer": 'urn:oid:2.16.840.1.114222.4.11.3591'
valueset "Proteinuria": 'urn:oid:2.16.840.1.113883.3.526.3.1003'
valueset "Race": 'urn:oid:2.16.840.1.114222.4.11.836'
valueset "Urine Protein Tests": 'urn:oid:2.16.840.1.113883.3.464.1003.109.12.1024'
valueset "Vascular Access for Dialysis": 'urn:oid:2.16.840.1.113883.3.464.1003.109.12.1011'

code "Birth date": '21112-8' from "LOINC" display 'Birth date'

parameter "Measurement Period" Interval<DateTime>

context Patient

define "SDE Ethnicity":
	["Patient Characteristic Ethnicity": "Ethnicity"]

define "SDE Payer":
	["Patient Characteristic Payer": "Payer"]

define "SDE Race":
	["Patient Characteristic Race": "Race"]

define "SDE Sex":
	["Patient Characteristic Sex": "ONC Administrative Sex"]

define "Renal Procedures":
	( ["Procedure, Performed": "Kidney Transplant"]
			union ["Procedure, Performed": "Vascular Access for Dialysis"]
			union ["Procedure, Performed": "Dialysis Services"]
	)

define "Renal Interventions":
	["Intervention, Performed": "Other Services Related to Dialysis"]
		union ["Intervention, Performed": "Dialysis Education"]

define "Nephropathy Diagnoses":
	( ["Diagnosis": "Hypertensive Chronic Kidney Disease"]
		union ["Diagnosis": "Kidney Failure"]
		union ["Diagnosis": "Glomerulonephritis and Nephrotic Syndrome"]
		union ["Diagnosis": "Diabetic Nephropathy"]
		union ["Diagnosis": "Proteinuria"] ) NephropathyDiagnoses
		where NephropathyDiagnoses.prevalencePeriod overlaps "Measurement Period"

define "Denominator":
	"Initial Population"

define "Protein Urea Lab Test":
	["Laboratory Test, Performed": "Urine Protein Tests"] ProteinUreaResult
		where ProteinUreaResult.result is not null

define "End Stage Renal Disease Encounter":
	["Encounter, Performed": "ESRD Monthly Outpatient Services"]

define "Numerator":
	exists ( "Active ACE or ARB Medications" )
		or exists ( "Nephropathy Diagnoses" )
		or exists ( "Nephropathy Screenings" )

define "Active ACE or ARB Medications":
	["Medication, Active": "ACE Inhibitor or ARB"] ACEorARBMedication
		where ACEorARBMedication.relevantPeriod overlaps "Measurement Period"

define "Nephropathy Screenings":
	( "Renal Procedures"
		union "Renal Interventions"
		union "End Stage Renal Disease Encounter"
		union "Protein Urea Lab Test" ) ScreeningNephropathy
		where ScreeningNephropathy.relevantPeriod during "Measurement Period"

define "Initial Population":
	exists ( ["Patient Characteristic Birthdate": "Birth date"] BirthDate
			where Global."CalendarAgeInYearsAt"(BirthDate.birthDatetime, start of "Measurement Period")in Interval[18, 75 )
	)
		and exists ( AdultOutpatientEncounters."Qualifying Encounters" )
		and exists ( ["Diagnosis": "Diabetes"] Diabetes
				where Diabetes.prevalencePeriod overlaps "Measurement Period"
		)

define "Denominator Exclusions":
	exists ( ["Patient Characteristic Birthdate": "Birth date"] BirthDate
			where ( Global."CalendarAgeInYearsAt"(BirthDate.birthDatetime, start of "Measurement Period")>= 65 )
				and FrailtyLTI."Has Spent More Than 90 Days in Long Term Care"
	)
		or FrailtyLTI."Advanced Illness and Frailty Exclusion Including Under Age 80"
		or Hospice."Has Hospice"