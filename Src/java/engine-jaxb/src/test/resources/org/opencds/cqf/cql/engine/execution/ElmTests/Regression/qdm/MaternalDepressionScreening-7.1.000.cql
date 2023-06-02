library MaternalDepressionScreening version '7.1.000'

using QDM version '5.4'

include MATGlobalCommonFunctions version '4.0.000' called Global

codesystem "LOINC": 'urn:oid:2.16.840.1.113883.6.1'
codesystem "SNOMEDCT": 'urn:oid:2.16.840.1.113883.6.96'

valueset "Ethnicity": 'urn:oid:2.16.840.1.114222.4.11.837'
valueset "Office Visit": 'urn:oid:2.16.840.1.113883.3.464.1003.101.12.1001'
valueset "ONC Administrative Sex": 'urn:oid:2.16.840.1.113762.1.4.1'
valueset "Payer": 'urn:oid:2.16.840.1.114222.4.11.3591'
valueset "Preventive Care - Established Office Visit, 0 to 17": 'urn:oid:2.16.840.1.113883.3.464.1003.101.11.1120'
valueset "Preventive Care - Initial Office Visit, 0 to 17": 'urn:oid:2.16.840.1.113883.3.464.1003.101.11.1110'
valueset "Race": 'urn:oid:2.16.840.1.114222.4.11.836'

code "Birth date": '21112-8' from "LOINC" display 'Birth date'
code "Maternal postpartum depression care (regime/therapy)": '428231000124106' from "SNOMEDCT" display 'Maternal postpartum depression care (regime/therapy)'
code "Maternal postpartum depression screening (procedure)": '428221000124108' from "SNOMEDCT" display 'Maternal postpartum depression screening (procedure)'

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

define "Qualifying Encounters":
	( ["Encounter, Performed": "Office Visit"]
			union ["Encounter, Performed": "Preventive Care - Established Office Visit, 0 to 17"]
			union ["Encounter, Performed": "Preventive Care - Initial Office Visit, 0 to 17"]
	)

define "Initial Population":
	exists "Qualifying Encounters Before 6 Months of Age"
		and exists "Turns 6 Months of Age During Measurement Period"

define "Numerator":
	exists ( ( ["Intervention, Performed": "Maternal postpartum depression care (regime/therapy)"]
			union ["Assessment, Performed": "Maternal postpartum depression screening (procedure)"] ) DepressionScreening
			with ["Patient Characteristic Birthdate": "Birth date"] BirthDate
				such that Global."CalendarAgeInMonthsAt"(BirthDate.birthDatetime, DepressionScreening.authorDatetime)<= 6
	)

define "Qualifying Encounters Before 6 Months of Age":
	"Qualifying Encounters" ValidEncounters
		with ["Patient Characteristic Birthdate": "Birth date"] BirthDate
			such that Global."CalendarAgeInMonthsAt"(BirthDate.birthDatetime, start of ValidEncounters.relevantPeriod)<= 6

define "Turns 6 Months of Age During Measurement Period":
	["Patient Characteristic Birthdate": "Birth date"] BirthDate
		where Global."CalendarAgeInMonthsAt"(BirthDate.birthDatetime, start of "Measurement Period")< 6
			and Global."CalendarAgeInMonthsAt"(BirthDate.birthDatetime, 
				end of "Measurement Period"
			)>= 6