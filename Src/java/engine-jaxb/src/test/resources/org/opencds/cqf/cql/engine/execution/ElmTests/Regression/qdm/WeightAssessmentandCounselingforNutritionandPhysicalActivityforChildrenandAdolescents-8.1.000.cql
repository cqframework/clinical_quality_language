library WeightAssessmentandCounselingforNutritionandPhysicalActivityforChildrenandAdolescents version '8.1.000'

using QDM version '5.4'

include MATGlobalCommonFunctions version '4.0.000' called Global
include Hospice version '2.0.000' called Hospice

codesystem "LOINC": 'urn:oid:2.16.840.1.113883.6.1'

valueset "BMI percentile": 'urn:oid:2.16.840.1.113883.3.464.1003.121.12.1012'
valueset "Counseling for Nutrition": 'urn:oid:2.16.840.1.113883.3.464.1003.195.12.1003'
valueset "Counseling for Physical Activity": 'urn:oid:2.16.840.1.113883.3.464.1003.118.12.1035'
valueset "Ethnicity": 'urn:oid:2.16.840.1.114222.4.11.837'
valueset "Height": 'urn:oid:2.16.840.1.113883.3.464.1003.121.12.1014'
valueset "Home Healthcare Services": 'urn:oid:2.16.840.1.113883.3.464.1003.101.12.1016'
valueset "Office Visit": 'urn:oid:2.16.840.1.113883.3.464.1003.101.12.1001'
valueset "ONC Administrative Sex": 'urn:oid:2.16.840.1.113762.1.4.1'
valueset "Payer": 'urn:oid:2.16.840.1.114222.4.11.3591'
valueset "Pregnancy": 'urn:oid:2.16.840.1.113883.3.526.3.378'
valueset "Preventive Care - Established Office Visit, 0 to 17": 'urn:oid:2.16.840.1.113883.3.464.1003.101.12.1024'
valueset "Preventive Care Services - Group Counseling": 'urn:oid:2.16.840.1.113883.3.464.1003.101.12.1027'
valueset "Preventive Care Services-Individual Counseling": 'urn:oid:2.16.840.1.113883.3.464.1003.101.12.1026'
valueset "Preventive Care- Initial Office Visit, 0 to 17": 'urn:oid:2.16.840.1.113883.3.464.1003.101.12.1022'
valueset "Race": 'urn:oid:2.16.840.1.114222.4.11.836'
valueset "Weight": 'urn:oid:2.16.840.1.113883.3.464.1003.121.12.1015'

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

define "Denominator":
	"Initial Population"

define "Numerator 1":
	exists ( "BMI Percentile in Measurement Period" )
		and exists ( "Height in Measurement Period" )
		and exists ( "Weight in Measurement Period" )

define "Weight in Measurement Period":
	["Physical Exam, Performed": "Weight"] Weight
		where Weight.relevantPeriod during "Measurement Period"
			and Weight.result is not null

define "Qualifying Encounter":
	( ["Encounter, Performed": "Office Visit"]
		union ["Encounter, Performed": "Preventive Care Services-Individual Counseling"]
		union ["Encounter, Performed": "Preventive Care - Established Office Visit, 0 to 17"]
		union ["Encounter, Performed": "Preventive Care- Initial Office Visit, 0 to 17"]
		union ["Encounter, Performed": "Preventive Care Services - Group Counseling"]
		union ["Encounter, Performed": "Home Healthcare Services"] ) ValidEncounter
		where ValidEncounter.relevantPeriod during "Measurement Period"

define "Pregnancy Diagnosis Which Overlaps Measurement Period":
	["Diagnosis": "Pregnancy"] Pregnancy
		where Pregnancy.prevalencePeriod overlaps "Measurement Period"

define "Height in Measurement Period":
	["Physical Exam, Performed": "Height"] Height
		where Height.relevantPeriod during "Measurement Period"
			and Height.result is not null

define "Denominator Exclusions":
	Hospice."Has Hospice"
		or exists ( "Pregnancy Diagnosis Which Overlaps Measurement Period" )

define "Numerator 2":
	exists ["Intervention, Performed": "Counseling for Nutrition"] NutritionCounseling
		where NutritionCounseling.relevantPeriod during "Measurement Period"

define "Numerator 3":
	exists ["Intervention, Performed": "Counseling for Physical Activity"] ActivityCounseling
		where ActivityCounseling.relevantPeriod during "Measurement Period"

define "BMI Percentile in Measurement Period":
	["Physical Exam, Performed": "BMI percentile"] BMIPercentile
		where BMIPercentile.relevantPeriod during "Measurement Period"
			and BMIPercentile.result is not null

define "Initial Population":
	exists ["Patient Characteristic Birthdate": "Birth date"] BirthDate
		where Global."CalendarAgeInYearsAt"(BirthDate.birthDatetime, start of "Measurement Period")in Interval[3, 17 )
			and exists ( "Qualifying Encounter" )

define "Stratification 2":
	exists ["Patient Characteristic Birthdate": "Birth date"] BirthDate
		where Global."CalendarAgeInYearsAt"(BirthDate.birthDatetime, start of "Measurement Period")>= 11
			and Global."CalendarAgeInYearsAt"(BirthDate.birthDatetime, start of "Measurement Period")< 17

define "Stratification 1":
	exists ["Patient Characteristic Birthdate": "Birth date"] BirthDate
		where Global."CalendarAgeInYearsAt"(BirthDate.birthDatetime, start of "Measurement Period")>= 3
			and Global."CalendarAgeInYearsAt"(BirthDate.birthDatetime, start of "Measurement Period")< 11