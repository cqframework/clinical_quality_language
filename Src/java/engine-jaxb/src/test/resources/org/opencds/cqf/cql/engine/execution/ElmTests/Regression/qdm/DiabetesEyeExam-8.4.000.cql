library DiabetesEyeExam version '8.4.000'

using QDM version '5.4'

include MATGlobalCommonFunctions version '4.0.000' called Global
include Hospice version '2.0.000' called Hospice
include AdvancedIllnessandFrailtyExclusionECQM version '4.0.000' called FrailtyLTI

codesystem "LOINC": 'urn:oid:2.16.840.1.113883.6.1'

valueset "Annual Wellness Visit": 'urn:oid:2.16.840.1.113883.3.526.3.1240'
valueset "Diabetes": 'urn:oid:2.16.840.1.113883.3.464.1003.103.12.1001'
valueset "Diabetic Retinopathy": 'urn:oid:2.16.840.1.113883.3.526.3.327'
valueset "Ethnicity": 'urn:oid:2.16.840.1.114222.4.11.837'
valueset "Home Healthcare Services": 'urn:oid:2.16.840.1.113883.3.464.1003.101.12.1016'
valueset "Office Visit": 'urn:oid:2.16.840.1.113883.3.464.1003.101.12.1001'
valueset "ONC Administrative Sex": 'urn:oid:2.16.840.1.113762.1.4.1'
valueset "Ophthalmological Services": 'urn:oid:2.16.840.1.113883.3.526.3.1285'
valueset "Payer": 'urn:oid:2.16.840.1.114222.4.11.3591'
valueset "Preventive Care Services - Established Office Visit, 18 and Up": 'urn:oid:2.16.840.1.113883.3.464.1003.101.12.1025'
valueset "Preventive Care Services-Initial Office Visit, 18 and Up": 'urn:oid:2.16.840.1.113883.3.464.1003.101.12.1023'
valueset "Race": 'urn:oid:2.16.840.1.114222.4.11.836'
valueset "Retinal or Dilated Eye Exam": 'urn:oid:2.16.840.1.113883.3.464.1003.115.12.1088'

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

define "Retinal Exam in Measurement Period":
	["Physical Exam, Performed": "Retinal or Dilated Eye Exam"] RetinalExam
		where RetinalExam.relevantPeriod during "Measurement Period"

define "Denominator":
	"Initial Population"

define "Qualifying Encounters":
	( ["Encounter, Performed": "Office Visit"]
		union ["Encounter, Performed": "Annual Wellness Visit"]
		union ["Encounter, Performed": "Preventive Care Services - Established Office Visit, 18 and Up"]
		union ["Encounter, Performed": "Preventive Care Services-Initial Office Visit, 18 and Up"]
		union ["Encounter, Performed": "Home Healthcare Services"]
		union ["Encounter, Performed": "Ophthalmological Services"] ) Encounter
		where Encounter.relevantPeriod during "Measurement Period"

define "Retinal Exam in Measurement Period or Year Prior":
	["Physical Exam, Performed": "Retinal or Dilated Eye Exam"] RetinalExam
		where 
		end of RetinalExam.relevantPeriod between start of "Measurement Period" - 1 year and 
		end of "Measurement Period"

define "Denominator Exclusions":
	exists ( ["Patient Characteristic Birthdate"] BirthDate
			where Global."CalendarAgeInYearsAt"(BirthDate.birthDatetime, start of "Measurement Period")>= 65
				and FrailtyLTI."Has Spent More Than 90 Days in Long Term Care"
	)
		or FrailtyLTI."Advanced Illness and Frailty Exclusion Including Under Age 80"
		or Hospice."Has Hospice"

define "Diabetic Retinopathy Overlapping Measurement Period":
	exists ( ["Diagnosis": "Diabetic Retinopathy"] Retinopathy
			where Retinopathy.prevalencePeriod overlaps "Measurement Period"
	)

define "Numerator":
	( "Diabetic Retinopathy Overlapping Measurement Period"
			and exists ( "Retinal Exam in Measurement Period" )
	)
		or ( not ( "Diabetic Retinopathy Overlapping Measurement Period" )
				and exists ( "Retinal Exam in Measurement Period or Year Prior" )
		)

define "Initial Population":
	exists ( ["Patient Characteristic Birthdate": "Birth date"] BirthDate
			where Global."CalendarAgeInYearsAt"(BirthDate.birthDatetime, start of "Measurement Period")in Interval[18, 75 )
	)
		and exists ( "Qualifying Encounters" )
		and exists ( ["Diagnosis": "Diabetes"] Diabetes
				where Diabetes.prevalencePeriod overlaps "Measurement Period"
		)