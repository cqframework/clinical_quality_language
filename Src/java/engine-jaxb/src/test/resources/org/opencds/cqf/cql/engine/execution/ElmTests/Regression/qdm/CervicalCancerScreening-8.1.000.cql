library CervicalCancerScreening version '8.1.000'

using QDM version '5.4'

include MATGlobalCommonFunctions version '4.0.000' called Global
include Hospice version '2.0.000' called Hospice

codesystem "LOINC": 'urn:oid:2.16.840.1.113883.6.1'
codesystem "SNOMEDCT": 'urn:oid:2.16.840.1.113883.6.96'

valueset "Ethnicity": 'urn:oid:2.16.840.1.114222.4.11.837'
valueset "Female": 'urn:oid:2.16.840.1.113883.3.560.100.2'
valueset "Home Healthcare Services": 'urn:oid:2.16.840.1.113883.3.464.1003.101.12.1016'
valueset "HPV Test": 'urn:oid:2.16.840.1.113883.3.464.1003.110.12.1059'
valueset "Hysterectomy with No Residual Cervix": 'urn:oid:2.16.840.1.113883.3.464.1003.198.12.1014'
valueset "Office Visit": 'urn:oid:2.16.840.1.113883.3.464.1003.101.12.1001'
valueset "ONC Administrative Sex": 'urn:oid:2.16.840.1.113762.1.4.1'
valueset "Pap Test": 'urn:oid:2.16.840.1.113883.3.464.1003.108.12.1017'
valueset "Payer": 'urn:oid:2.16.840.1.114222.4.11.3591'
valueset "Preventive Care Services - Established Office Visit, 18 and Up": 'urn:oid:2.16.840.1.113883.3.464.1003.101.12.1025'
valueset "Preventive Care Services-Initial Office Visit, 18 and Up": 'urn:oid:2.16.840.1.113883.3.464.1003.101.12.1023'
valueset "Race": 'urn:oid:2.16.840.1.114222.4.11.836'

code "Birth date": '21112-8' from "LOINC" display 'Birth date'
code "Congenital absence of cervix (disorder)": '37687000' from "SNOMEDCT" display 'Congenital absence of cervix (disorder)'

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
		union ["Encounter, Performed": "Preventive Care Services - Established Office Visit, 18 and Up"]
		union ["Encounter, Performed": "Preventive Care Services-Initial Office Visit, 18 and Up"]
		union ["Encounter, Performed": "Home Healthcare Services"] ) ValidEncounter
		where ValidEncounter.relevantPeriod during "Measurement Period"

define "Congenital Absence of Cervix":
	["Diagnosis": "Congenital absence of cervix (disorder)"] NoCervixBirth
		where NoCervixBirth.prevalencePeriod starts before 
		end "Measurement Period"

define "Pap Test with Results":
	["Laboratory Test, Performed": "Pap Test"] PapTestWithResult
		where PapTestWithResult.result is not null

define "Pap Test Within 3 Years":
	"Pap Test with Results" PapTest
		where PapTest.relevantPeriod starts 3 years or less before 
		end of "Measurement Period"

define "Surgical Absence of Cervix":
	["Procedure, Performed": "Hysterectomy with No Residual Cervix"] NoCervixHysterectomy
		where NoCervixHysterectomy.relevantPeriod ends before 
		end of "Measurement Period"

define "Denominator Exclusions":
	Hospice."Has Hospice"
		or exists "Surgical Absence of Cervix"
		or exists "Congenital Absence of Cervix"

define "Numerator":
	exists "Pap Test Within 3 Years"
		or exists "Pap Test With HPV Co Test Within 5 Years"

define "Pap Test With HPV Co Test Within 5 Years":
	"Pap Test Within 5 Years" PapTestOver30YearsOld
		with ["Laboratory Test, Performed": "HPV Test"] HPVTest
			such that HPVTest.result is not null
				and HPVTest.relevantPeriod starts within 1 day of start of PapTestOver30YearsOld.relevantPeriod

define "Pap Test Within 5 Years":
	( "Pap Test with Results" PapTestOver30YearsOld
			with ["Patient Characteristic Birthdate": "Birth date"] BirthDate
				such that Global."CalendarAgeInYearsAt"(BirthDate.birthDatetime, start of PapTestOver30YearsOld.relevantPeriod)>= 30
					and PapTestOver30YearsOld.relevantPeriod starts 5 years or less before 
					end of "Measurement Period"
	)

define "Initial Population":
	exists ( ["Patient Characteristic Birthdate": "Birth date"] BirthDate
			where Global."CalendarAgeInYearsAt"(BirthDate.birthDatetime, start of "Measurement Period")>= 23
				and Global."CalendarAgeInYearsAt"(BirthDate.birthDatetime, start of "Measurement Period")<= 64
	)
		and exists ( ["Patient Characteristic Sex": "Female"] )
		and exists "Qualifying Encounters"