library DiabeticRetinopathyCommunicationwiththePhysicianManagingOngoingDiabetesCare version '8.1.000'

using QDM version '5.4'

include MATGlobalCommonFunctions version '4.0.000' called Global

codesystem "LOINC": 'urn:oid:2.16.840.1.113883.6.1'
codesystem "SNOMEDCT": 'urn:oid:2.16.840.1.113883.6.96'

valueset "Care Services in Long-Term Residential Facility": 'urn:oid:2.16.840.1.113883.3.464.1003.101.12.1014'
valueset "Diabetic Retinopathy": 'urn:oid:2.16.840.1.113883.3.526.3.327'
valueset "Ethnicity": 'urn:oid:2.16.840.1.114222.4.11.837'
valueset "Level of Severity of Retinopathy Findings": 'urn:oid:2.16.840.1.113883.3.526.3.1283'
valueset "Macular Edema Findings Present": 'urn:oid:2.16.840.1.113883.3.526.3.1320'
valueset "Macular Exam": 'urn:oid:2.16.840.1.113883.3.526.3.1251'
valueset "Medical Reason": 'urn:oid:2.16.840.1.113883.3.526.3.1007'
valueset "Nursing Facility Visit": 'urn:oid:2.16.840.1.113883.3.464.1003.101.12.1012'
valueset "Office Visit": 'urn:oid:2.16.840.1.113883.3.464.1003.101.12.1001'
valueset "ONC Administrative Sex": 'urn:oid:2.16.840.1.113762.1.4.1'
valueset "Ophthalmological Services": 'urn:oid:2.16.840.1.113883.3.526.3.1285'
valueset "Outpatient Consultation": 'urn:oid:2.16.840.1.113883.3.464.1003.101.12.1008'
valueset "Patient Reason": 'urn:oid:2.16.840.1.113883.3.526.3.1008'
valueset "Payer": 'urn:oid:2.16.840.1.114222.4.11.3591'
valueset "Race": 'urn:oid:2.16.840.1.114222.4.11.836'

code "Birth date": '21112-8' from "LOINC" display 'Birth date'
code "Healthcare professional (occupation)": '223366009' from "SNOMEDCT" display 'Healthcare professional (occupation)'
code "Macular edema absent (situation)": '428341000124108' from "SNOMEDCT" display 'Macular edema absent (situation)'
code "Medical practitioner (occupation)": '158965000' from "SNOMEDCT" display 'Medical practitioner (occupation)'
code "Ophthalmologist (occupation)": '422234006' from "SNOMEDCT" display 'Ophthalmologist (occupation)'
code "Optometrist (occupation)": '28229004' from "SNOMEDCT" display 'Optometrist (occupation)'
code "Physician (occupation)": '309343006' from "SNOMEDCT" display 'Physician (occupation)'

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

define "Numerator":
	"Results of Dilated Macular or Fundus Exam Communicated"

define "Denominator":
	"Initial Population"
		and exists "Macular Exam Performed"

define "Face to Face Encounter During Measurement Period":
	( ["Encounter, Performed": "Office Visit"]
		union ["Encounter, Performed": "Ophthalmological Services"]
		union ["Encounter, Performed": "Outpatient Consultation"]
		union ["Encounter, Performed": "Care Services in Long-Term Residential Facility"]
		union ["Encounter, Performed": "Nursing Facility Visit"] ) FaceToFaceEncounter
		where FaceToFaceEncounter.relevantPeriod during "Measurement Period"

define "Diabetic Retinopathy Encounter":
	"Face to Face Encounter During Measurement Period" ValidFaceToFaceEncounter
		with ["Diagnosis": "Diabetic Retinopathy"] DiabeticRetinopathy
			such that DiabeticRetinopathy.prevalencePeriod overlaps ValidFaceToFaceEncounter.relevantPeriod

define "Macular Exam Performed":
	["Diagnostic Study, Performed": "Macular Exam"] MacularExam
		with "Diabetic Retinopathy Encounter" EncounterDiabeticRetinopathy
			such that MacularExam.relevantPeriod during EncounterDiabeticRetinopathy.relevantPeriod
		where MacularExam.result is not null

define "Medical or Patient Reason for Not Communicating Absence of Macular Edema":
	["Communication, Not Performed": "Macular edema absent (situation)"] MacularEdemaAbsentNotCommunicated
		with "Diabetic Retinopathy Encounter" EncounterDiabeticRetinopathy
			such that MacularEdemaAbsentNotCommunicated.authorDatetime during EncounterDiabeticRetinopathy.relevantPeriod
		where ( MacularEdemaAbsentNotCommunicated.negationRationale in "Medical Reason"
				or MacularEdemaAbsentNotCommunicated.negationRationale in "Patient Reason"
		)

define "Medical or Patient Reason for Not Communicating Level of Severity of Retinopathy":
	["Communication, Not Performed": "Level of Severity of Retinopathy Findings"] LevelOfSeverityNotCommunicated
		with "Diabetic Retinopathy Encounter" EncounterDiabeticRetinopathy
			such that LevelOfSeverityNotCommunicated.authorDatetime during EncounterDiabeticRetinopathy.relevantPeriod
		where ( LevelOfSeverityNotCommunicated.negationRationale in "Medical Reason"
				or LevelOfSeverityNotCommunicated.negationRationale in "Patient Reason"
		)

define "Medical or Patient Reason for Not Communicating Presence of Macular Edema":
	["Communication, Not Performed": "Macular Edema Findings Present"] MacularEdemaPresentNotCommunicated
		with "Diabetic Retinopathy Encounter" EncounterDiabeticRetinopathy
			such that MacularEdemaPresentNotCommunicated.authorDatetime during EncounterDiabeticRetinopathy.relevantPeriod
		where ( MacularEdemaPresentNotCommunicated.negationRationale in "Medical Reason"
				or MacularEdemaPresentNotCommunicated.negationRationale in "Patient Reason"
		)

define "Results of Dilated Macular or Fundus Exam Communicated":
	exists "Level of Severity of Retinopathy Findings Communicated"
		and ( exists "Macular Edema Absence Communicated"
				or exists "Macular Edema Presence Communicated"
		)

define "Denominator Exceptions":
	exists "Medical or Patient Reason for Not Communicating Level of Severity of Retinopathy"
		or exists "Medical or Patient Reason for Not Communicating Absence of Macular Edema"
		or exists "Medical or Patient Reason for Not Communicating Presence of Macular Edema"

define "Level of Severity of Retinopathy Findings Communicated":
	["Communication, Performed": "Level of Severity of Retinopathy Findings"] LevelOfSeverityCommunicated
		with "Diabetic Retinopathy Encounter" EncounterDiabeticRetinopathy
			such that LevelOfSeverityCommunicated.authorDatetime after start of EncounterDiabeticRetinopathy.relevantPeriod
				and ( LevelOfSeverityCommunicated.sender ~ "Healthcare professional (occupation)"
						or LevelOfSeverityCommunicated.sender ~ "Ophthalmologist (occupation)"
						or LevelOfSeverityCommunicated.sender ~ "Optometrist (occupation)"
				)
				and ( LevelOfSeverityCommunicated.recipient ~ "Physician (occupation)"
						or LevelOfSeverityCommunicated.recipient ~ "Healthcare professional (occupation)"
						or LevelOfSeverityCommunicated.recipient ~ "Medical practitioner (occupation)"
				)

define "Macular Edema Absence Communicated":
	["Communication, Performed": "Macular edema absent (situation)"] MacularEdemaAbsentCommunicated
		with "Diabetic Retinopathy Encounter" EncounterDiabeticRetinopathy
			such that MacularEdemaAbsentCommunicated.authorDatetime after start of EncounterDiabeticRetinopathy.relevantPeriod
				and ( MacularEdemaAbsentCommunicated.sender ~ "Healthcare professional (occupation)"
						or MacularEdemaAbsentCommunicated.sender ~ "Ophthalmologist (occupation)"
						or MacularEdemaAbsentCommunicated.sender ~ "Optometrist (occupation)"
				)
				and ( MacularEdemaAbsentCommunicated.recipient ~ "Physician (occupation)"
						or MacularEdemaAbsentCommunicated.recipient ~ "Healthcare professional (occupation)"
						or MacularEdemaAbsentCommunicated.recipient ~ "Medical practitioner (occupation)"
				)

define "Macular Edema Presence Communicated":
	["Communication, Performed": "Macular Edema Findings Present"] MacularEdemaPresentCommunicated
		with "Diabetic Retinopathy Encounter" EncounterDiabeticRetinopathy
			such that MacularEdemaPresentCommunicated.authorDatetime after start of EncounterDiabeticRetinopathy.relevantPeriod
				and ( MacularEdemaPresentCommunicated.sender ~ "Healthcare professional (occupation)"
						or MacularEdemaPresentCommunicated.sender ~ "Ophthalmologist (occupation)"
						or MacularEdemaPresentCommunicated.sender ~ "Optometrist (occupation)"
				)
				and ( MacularEdemaPresentCommunicated.recipient ~ "Physician (occupation)"
						or MacularEdemaPresentCommunicated.recipient ~ "Healthcare professional (occupation)"
						or MacularEdemaPresentCommunicated.recipient ~ "Medical practitioner (occupation)"
				)

define "Initial Population":
	exists ( ["Patient Characteristic Birthdate": "Birth date"] BirthDate
			where Global."CalendarAgeInYearsAt"(BirthDate.birthDatetime, start of "Measurement Period")>= 18
	)
		and exists "Diabetic Retinopathy Encounter"