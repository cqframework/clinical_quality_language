library PrimaryOpenAngleGlaucomaPOAGOpticNerveEvaluation version '8.1.000'

using QDM version '5.4'

include MATGlobalCommonFunctions version '4.0.000' called Global

codesystem "LOINC": 'urn:oid:2.16.840.1.113883.6.1'

valueset "Care Services in Long-Term Residential Facility": 'urn:oid:2.16.840.1.113883.3.464.1003.101.12.1014'
valueset "Cup to Disc Ratio": 'urn:oid:2.16.840.1.113883.3.526.3.1333'
valueset "Ethnicity": 'urn:oid:2.16.840.1.114222.4.11.837'
valueset "Medical Reason": 'urn:oid:2.16.840.1.113883.3.526.3.1007'
valueset "Nursing Facility Visit": 'urn:oid:2.16.840.1.113883.3.464.1003.101.12.1012'
valueset "Office Visit": 'urn:oid:2.16.840.1.113883.3.464.1003.101.12.1001'
valueset "ONC Administrative Sex": 'urn:oid:2.16.840.1.113762.1.4.1'
valueset "Ophthalmological Services": 'urn:oid:2.16.840.1.113883.3.526.3.1285'
valueset "Optic Disc Exam for Structural Abnormalities": 'urn:oid:2.16.840.1.113883.3.526.3.1334'
valueset "Outpatient Consultation": 'urn:oid:2.16.840.1.113883.3.464.1003.101.12.1008'
valueset "Payer": 'urn:oid:2.16.840.1.114222.4.11.3591'
valueset "Primary Open-Angle Glaucoma": 'urn:oid:2.16.840.1.113883.3.526.3.326'
valueset "Race": 'urn:oid:2.16.840.1.114222.4.11.836'

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

define "Face to Face Encounter During Measurement Period":
	( ["Encounter, Performed": "Office Visit"]
		union ["Encounter, Performed": "Ophthalmological Services"]
		union ["Encounter, Performed": "Outpatient Consultation"]
		union ["Encounter, Performed": "Nursing Facility Visit"]
		union ["Encounter, Performed": "Care Services in Long-Term Residential Facility"] ) FaceToFaceEncounter
		where FaceToFaceEncounter.relevantPeriod during "Measurement Period"

define "Numerator":
	exists "Cup to Disc Ratio Performed with Result"
		and exists "Optic Disc Exam Performed with Result"

define "Denominator":
	"Initial Population"

define "Medical Reason for Not Performing Cup to Disc Ratio":
	["Diagnostic Study, Not Performed": "Cup to Disc Ratio"] CupToDiscExamNotPerformed
		with "Primary Open Angle Glaucoma Encounter" EncounterWithPOAG
			such that CupToDiscExamNotPerformed.authorDatetime during EncounterWithPOAG.relevantPeriod
		where CupToDiscExamNotPerformed.negationRationale in "Medical Reason"

define "Cup to Disc Ratio Performed with Result":
	["Diagnostic Study, Performed": "Cup to Disc Ratio"] CupToDiscExamPerformed
		with "Primary Open Angle Glaucoma Encounter" EncounterWithPOAG
			such that CupToDiscExamPerformed.relevantPeriod during EncounterWithPOAG.relevantPeriod
				and CupToDiscExamPerformed.result is not null

define "Primary Open Angle Glaucoma Encounter":
	"Face to Face Encounter During Measurement Period" ValidFaceToFaceEncounter
		with ["Diagnosis": "Primary Open-Angle Glaucoma"] PrimaryOpenAngleGlaucoma
			such that PrimaryOpenAngleGlaucoma.prevalencePeriod overlaps ValidFaceToFaceEncounter.relevantPeriod

define "Optic Disc Exam Performed with Result":
	["Diagnostic Study, Performed": "Optic Disc Exam for Structural Abnormalities"] OpticDiscExamPerformed
		with "Primary Open Angle Glaucoma Encounter" EncounterWithPOAG
			such that OpticDiscExamPerformed.relevantPeriod during EncounterWithPOAG.relevantPeriod
				and OpticDiscExamPerformed.result is not null

define "Medical Reason for Not Performing Optic Disc Exam":
	["Diagnostic Study, Not Performed": "Optic Disc Exam for Structural Abnormalities"] OpticDiscExamNotPerformed
		with "Primary Open Angle Glaucoma Encounter" EncounterWithPOAG
			such that OpticDiscExamNotPerformed.authorDatetime during EncounterWithPOAG.relevantPeriod
		where OpticDiscExamNotPerformed.negationRationale in "Medical Reason"

define "Denominator Exceptions":
	exists "Medical Reason for Not Performing Cup to Disc Ratio"
		or exists "Medical Reason for Not Performing Optic Disc Exam"

define "Initial Population":
	exists ( ["Patient Characteristic Birthdate": "Birth date"] BirthDate
			where Global."CalendarAgeInYearsAt"(BirthDate.birthDatetime, start of "Measurement Period")>= 18
	)
		and exists "Primary Open Angle Glaucoma Encounter"