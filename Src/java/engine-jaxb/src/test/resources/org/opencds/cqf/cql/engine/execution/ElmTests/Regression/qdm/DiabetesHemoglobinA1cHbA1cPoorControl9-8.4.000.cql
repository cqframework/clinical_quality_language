library DiabetesHemoglobinA1cHbA1cPoorControl9 version '8.4.000'

using QDM version '5.4'

include Adult_Outpatient_Encounters version '1.2.000' called AdultOutpatientEncounters
include MATGlobalCommonFunctions version '4.0.000' called Global
include Hospice version '2.0.000' called Hospice
include AdvancedIllnessandFrailtyExclusionECQM version '4.0.000' called FrailtyLTI

codesystem "LOINC": 'urn:oid:2.16.840.1.113883.6.1'

valueset "Diabetes": 'urn:oid:2.16.840.1.113883.3.464.1003.103.12.1001'
valueset "Ethnicity": 'urn:oid:2.16.840.1.114222.4.11.837'
valueset "HbA1c Laboratory Test": 'urn:oid:2.16.840.1.113883.3.464.1003.198.12.1013'
valueset "ONC Administrative Sex": 'urn:oid:2.16.840.1.113762.1.4.1'
valueset "Payer": 'urn:oid:2.16.840.1.114222.4.11.3591'
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

define "Denominator":
	"Initial Population"

define "Numerator":
	"Has Most Recent HbA1c Without Result"
		or "Has Most Recent Elevated HbA1c"
		or "Has No Record Of HbA1c"

define "Has Most Recent Elevated HbA1c":
	"Has Most Recent HbA1c".result > 9 '%'

define "Has No Record Of HbA1c":
	not exists ( ["Laboratory Test, Performed": "HbA1c Laboratory Test"] NoHbA1c
			where NoHbA1c.relevantPeriod during "Measurement Period"
	)

define "Has Most Recent HbA1c":
	Last(["Laboratory Test, Performed": "HbA1c Laboratory Test"] RecentHbA1c
			where RecentHbA1c.relevantPeriod during "Measurement Period"
			sort by start of relevantPeriod
	)

define "Initial Population":
	exists ( ["Patient Characteristic Birthdate": "Birth date"] BirthDate
			where Global."CalendarAgeInYearsAt"(BirthDate.birthDatetime, start of "Measurement Period")in Interval[18, 75 )
	)
		and exists ( AdultOutpatientEncounters."Qualifying Encounters" )
		and exists ( ["Diagnosis": "Diabetes"] Diabetes
				where Diabetes.prevalencePeriod overlaps "Measurement Period"
		)

define "Has Most Recent HbA1c Without Result":
	"Has Most Recent HbA1c" is not null
		and "Has Most Recent HbA1c".result is null

define "Denominator Exclusions":
	exists ( ["Patient Characteristic Birthdate": "Birth date"] BirthDate
			where ( Global."CalendarAgeInYearsAt"(BirthDate.birthDatetime, start of "Measurement Period")>= 65 )
				and FrailtyLTI."Has Spent More Than 90 Days in Long Term Care"
	)
		or Hospice."Has Hospice"
		or FrailtyLTI."Advanced Illness and Frailty Exclusion Including Under Age 80"