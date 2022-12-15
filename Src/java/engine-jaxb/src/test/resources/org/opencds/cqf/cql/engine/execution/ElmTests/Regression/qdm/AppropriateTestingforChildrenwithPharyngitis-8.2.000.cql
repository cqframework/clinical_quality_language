library AppropriateTestingforChildrenwithPharyngitis version '8.2.000'

using QDM version '5.4'

include MATGlobalCommonFunctions version '4.0.000' called Global
include Hospice version '2.0.000' called Hospice

codesystem "LOINC": 'urn:oid:2.16.840.1.113883.6.1'

valueset "Acute Pharyngitis": 'urn:oid:2.16.840.1.113883.3.464.1003.102.12.1011'
valueset "Acute Tonsillitis": 'urn:oid:2.16.840.1.113883.3.464.1003.102.12.1012'
valueset "Antibiotic Medications for Pharyngitis": 'urn:oid:2.16.840.1.113883.3.464.1003.196.12.1001'
valueset "Competing Conditions for Respiratory Conditions": 'urn:oid:2.16.840.1.113883.3.464.1003.102.12.1017'
valueset "Discharge Services- Observation Care": 'urn:oid:2.16.840.1.113883.3.464.1003.101.12.1039'
valueset "Emergency Department Visit": 'urn:oid:2.16.840.1.113883.3.464.1003.101.12.1010'
valueset "Ethnicity": 'urn:oid:2.16.840.1.114222.4.11.837'
valueset "Group A Streptococcus Test": 'urn:oid:2.16.840.1.113883.3.464.1003.198.12.1012'
valueset "Home Healthcare Services": 'urn:oid:2.16.840.1.113883.3.464.1003.101.12.1016'
valueset "Hospital Observation Care - Initial": 'urn:oid:2.16.840.1.113883.3.464.1003.101.12.1002'
valueset "Medical Disability Exam": 'urn:oid:2.16.840.1.113883.3.464.1003.101.11.1233'
valueset "Office Visit": 'urn:oid:2.16.840.1.113883.3.464.1003.101.12.1001'
valueset "ONC Administrative Sex": 'urn:oid:2.16.840.1.113762.1.4.1'
valueset "Outpatient Consultation": 'urn:oid:2.16.840.1.113883.3.464.1003.101.12.1008'
valueset "Payer": 'urn:oid:2.16.840.1.114222.4.11.3591'
valueset "Preventive Care Services - Established Office Visit, 18 and Up": 'urn:oid:2.16.840.1.113883.3.464.1003.101.12.1025'
valueset "Preventive Care Services - Group Counseling": 'urn:oid:2.16.840.1.113883.3.464.1003.101.12.1027'
valueset "Preventive Care Services - Other": 'urn:oid:2.16.840.1.113883.3.464.1003.101.12.1030'
valueset "Preventive Care Services, Initial Office Visit, 0 to 17": 'urn:oid:2.16.840.1.113883.3.464.1003.101.12.1022'
valueset "Preventive Care Services-Individual Counseling": 'urn:oid:2.16.840.1.113883.3.464.1003.101.12.1026'
valueset "Preventive Care Services-Initial Office Visit, 18 and Up": 'urn:oid:2.16.840.1.113883.3.464.1003.101.12.1023'
valueset "Preventive Care, Established Office Visit, 0 to 17": 'urn:oid:2.16.840.1.113883.3.464.1003.101.12.1024'
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

define "In Hospice":
	"Initial Population" EligibleEncounters
		where Hospice."Has Hospice"

define "Encounter With Antibiotic Ordered Within Three Days":
	"Qualifying Encounter" EDOrAmbulatoryVisit
		with ["Medication, Order": "Antibiotic Medications for Pharyngitis"] AntibioticOrdered
			such that ( EDOrAmbulatoryVisit.relevantPeriod starts 3 days or less on or before AntibioticOrdered.authorDatetime )
		where EDOrAmbulatoryVisit.relevantPeriod during "Measurement Period"

define "Encounter With Pharyngitis or Tonsillitis":
	from
		"Encounter With Antibiotic Ordered Within Three Days" VisitWithAntibiotic,
		"Pharyngitis or Tonsillitis" AcutePharyngitisTonsillitis
		where AcutePharyngitisTonsillitis.prevalencePeriod starts during VisitWithAntibiotic.relevantPeriod

define "Pharyngitis or Tonsillitis":
	( ["Diagnosis": "Acute Pharyngitis"]
			union ["Diagnosis": "Acute Tonsillitis"]
	)

define "Denominator":
	"Initial Population"

define "Qualifying Encounter":
	( ["Encounter, Performed": "Discharge Services- Observation Care"]
		union ["Encounter, Performed": "Emergency Department Visit"]
		union ["Encounter, Performed": "Home Healthcare Services"]
		union ["Encounter, Performed": "Hospital Observation Care - Initial"]
		union ["Encounter, Performed": "Medical Disability Exam"]
		union ["Encounter, Performed": "Office Visit"]
		union ["Encounter, Performed": "Outpatient Consultation"]
		union ["Encounter, Performed": "Preventive Care Services - Established Office Visit, 18 and Up"]
		union ["Encounter, Performed": "Preventive Care Services - Group Counseling"]
		union ["Encounter, Performed": "Preventive Care Services - Other"]
		union ["Encounter, Performed": "Preventive Care Services, Initial Office Visit, 0 to 17"]
		union ["Encounter, Performed": "Preventive Care Services-Individual Counseling"]
		union ["Encounter, Performed": "Preventive Care Services-Initial Office Visit, 18 and Up"]
		union ["Encounter, Performed": "Preventive Care, Established Office Visit, 0 to 17"] ) ValidEncounter
		where ValidEncounter.relevantPeriod during "Measurement Period"

define "Group A Streptococcus Lab Test With Result":
	["Laboratory Test, Performed": "Group A Streptococcus Test"] GroupAStreptococcusTest
		where GroupAStreptococcusTest.result is not null

define "Encounter With Antibiotic Active In 30 Days Prior":
	"Encounter With Pharyngitis or Tonsillitis" EncounterWithPharyngitis
		with ["Medication, Active": "Antibiotic Medications for Pharyngitis"] ActiveAntibiotic
			such that ActiveAntibiotic.relevantPeriod overlaps Interval[Global."ToDate"((start of EncounterWithPharyngitis.AcutePharyngitisTonsillitis.prevalencePeriod)- 30 days), Global."ToDate"(start of EncounterWithPharyngitis.AcutePharyngitisTonsillitis.prevalencePeriod))
		return EncounterWithPharyngitis.VisitWithAntibiotic

define "Denominator Exclusions":
	"In Hospice"
		union "Encounter With Antibiotic Active In 30 Days Prior"
		union "Competing Diagnosis for Upper Respiratory Infection"

define "Initial Population":
	"Encounter With Pharyngitis or Tonsillitis" EncounterWithPharyngitis
		with ["Patient Characteristic Birthdate": "Birth date"] BirthDate
			such that Global."CalendarAgeInYearsAt"(BirthDate.birthDatetime, start of "Measurement Period")in Interval[3, 18 )
		return EncounterWithPharyngitis.VisitWithAntibiotic

define "Numerator":
	from
		"Group A Streptococcus Lab Test With Result" GroupAStreptococcusTest,
		"Encounter With Pharyngitis or Tonsillitis" EncounterWithPharyngitis
		where ( start of GroupAStreptococcusTest.relevantPeriod 3 days or less on or before day of 
			end of EncounterWithPharyngitis.VisitWithAntibiotic.relevantPeriod
				or start of GroupAStreptococcusTest.relevantPeriod 3 days or less on or after day of 
				end of EncounterWithPharyngitis.VisitWithAntibiotic.relevantPeriod
		)
		return EncounterWithPharyngitis.VisitWithAntibiotic

define "Competing Diagnosis for Upper Respiratory Infection":
	"Encounter With Pharyngitis or Tonsillitis" EncounterWithPharyngitis
		with ["Diagnosis": "Competing Conditions for Respiratory Conditions"] CompetingCondition
			such that EncounterWithPharyngitis.AcutePharyngitisTonsillitis !~ CompetingCondition
				and CompetingCondition.prevalencePeriod starts 3 days or less after start of EncounterWithPharyngitis.AcutePharyngitisTonsillitis.prevalencePeriod
		return EncounterWithPharyngitis.VisitWithAntibiotic