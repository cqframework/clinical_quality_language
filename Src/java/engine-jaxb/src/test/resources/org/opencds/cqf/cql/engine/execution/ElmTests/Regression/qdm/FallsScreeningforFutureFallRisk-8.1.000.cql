library FallsScreeningforFutureFallRisk version '8.1.000'

using QDM version '5.4'

include MATGlobalCommonFunctions version '4.0.000' called Global
include Hospice version '2.0.000' called Hospice

codesystem "LOINC": 'urn:oid:2.16.840.1.113883.6.1'

valueset "Ambulatory Status": 'urn:oid:2.16.840.1.113883.3.464.1003.118.11.1219'
valueset "Annual Wellness Visit": 'urn:oid:2.16.840.1.113883.3.526.3.1240'
valueset "Audiology Visit": 'urn:oid:2.16.840.1.113883.3.464.1003.101.12.1066'
valueset "Care Services in Long-Term Residential Facility": 'urn:oid:2.16.840.1.113883.3.464.1003.101.12.1014'
valueset "Discharge Services - Nursing Facility": 'urn:oid:2.16.840.1.113883.3.464.1003.101.12.1013'
valueset "Ethnicity": 'urn:oid:2.16.840.1.114222.4.11.837'
valueset "Falls Screening": 'urn:oid:2.16.840.1.113883.3.464.1003.118.12.1028'
valueset "Home Healthcare Services": 'urn:oid:2.16.840.1.113883.3.464.1003.101.12.1016'
valueset "Nursing Facility Visit": 'urn:oid:2.16.840.1.113883.3.464.1003.101.12.1012'
valueset "Office Visit": 'urn:oid:2.16.840.1.113883.3.464.1003.101.12.1001'
valueset "ONC Administrative Sex": 'urn:oid:2.16.840.1.113762.1.4.1'
valueset "Ophthalmological Services": 'urn:oid:2.16.840.1.113883.3.526.3.1285'
valueset "Patient not ambulatory": 'urn:oid:2.16.840.1.113883.3.464.1003.118.12.1009'
valueset "Payer": 'urn:oid:2.16.840.1.114222.4.11.3591'
valueset "Preventive Care Services - Established Office Visit, 18 and Up": 'urn:oid:2.16.840.1.113883.3.464.1003.101.12.1025'
valueset "Preventive Care Services-Individual Counseling": 'urn:oid:2.16.840.1.113883.3.464.1003.101.12.1026'
valueset "Preventive Care Services-Initial Office Visit, 18 and Up": 'urn:oid:2.16.840.1.113883.3.464.1003.101.12.1023'
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
	exists ["Assessment, Performed": "Falls Screening"] FallsScreen
		where FallsScreen.authorDatetime during "Measurement Period"

define "Qualifying Encounter":
	( ["Encounter, Performed": "Office Visit"]
		union ["Encounter, Performed": "Annual Wellness Visit"]
		union ["Encounter, Performed": "Preventive Care Services - Established Office Visit, 18 and Up"]
		union ["Encounter, Performed": "Preventive Care Services-Initial Office Visit, 18 and Up"]
		union ["Encounter, Performed": "Home Healthcare Services"]
		union ["Encounter, Performed": "Ophthalmological Services"]
		union ["Encounter, Performed": "Preventive Care Services-Individual Counseling"]
		union ["Encounter, Performed": "Discharge Services - Nursing Facility"]
		union ["Encounter, Performed": "Nursing Facility Visit"]
		union ["Encounter, Performed": "Care Services in Long-Term Residential Facility"]
		union ["Encounter, Performed": "Audiology Visit"] ) ValidEncounter
		where ValidEncounter.relevantPeriod during "Measurement Period"

define "Ambulatory Status Assessment During Measurement Period":
	["Assessment, Performed": "Ambulatory Status"] AmbulatoryStatusAssessment
		where AmbulatoryStatusAssessment.authorDatetime during "Measurement Period"
			and AmbulatoryStatusAssessment.result is not null

define "Assessed as Not Ambulatory During Measurement Period":
	exists ( "Ambulatory Status Assessment During Measurement Period" AmbulatoryStatusAssessed
			where AmbulatoryStatusAssessed.result in "Patient not ambulatory"
	)

define "Assessed as Not Ambulatory Prior to Measurement Period":
	Last(["Assessment, Performed": "Ambulatory Status"] PriorAmbulatoryStatus
			where PriorAmbulatoryStatus.authorDatetime before start "Measurement Period"
			sort by authorDatetime
	).result in "Patient not ambulatory"

define "Denominator Exclusions":
	"Assessed as Not Ambulatory During Measurement Period"
		or ( "Assessed as Not Ambulatory Prior to Measurement Period"
				and not exists ( "Ambulatory Status Assessment During Measurement Period" )
		)
		or Hospice."Has Hospice"

define "Initial Population":
	exists ( ["Patient Characteristic Birthdate": "Birth date"] BirthDate
			where Global."CalendarAgeInYearsAt"(BirthDate.birthDatetime, start of "Measurement Period")>= 65
	)
		and exists "Qualifying Encounter"