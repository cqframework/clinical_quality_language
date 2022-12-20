library DocumentationofCurrentMedicationsintheMedicalRecord version '9.1.000'

using QDM version '5.4'

include MATGlobalCommonFunctions version '4.0.000' called Global

codesystem "LOINC": 'urn:oid:2.16.840.1.113883.6.1'
codesystem "SNOMEDCT": 'urn:oid:2.16.840.1.113883.6.96'

valueset "Ethnicity": 'urn:oid:2.16.840.1.114222.4.11.837'
valueset "Medical or Other reason not done": 'urn:oid:2.16.840.1.113883.3.600.1.1502'
valueset "Medications Encounter Code Set": 'urn:oid:2.16.840.1.113883.3.600.1.1834'
valueset "ONC Administrative Sex": 'urn:oid:2.16.840.1.113762.1.4.1'
valueset "Payer": 'urn:oid:2.16.840.1.114222.4.11.3591'
valueset "Race": 'urn:oid:2.16.840.1.114222.4.11.836'

code "Birth date": '21112-8' from "LOINC" display 'Birth date'
code "Documentation of current medications (procedure)": '428191000124101' from "SNOMEDCT" display 'Documentation of current medications (procedure)'

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

define "Medications Not Documented for Medical Reason":
	["Procedure, Not Performed": "Documentation of current medications (procedure)"] NotPerformed
		where NotPerformed.negationRationale in "Medical or Other reason not done"

define "Denominator Exceptions":
	"Qualifying Encounters During Measurement Period" EncounterDuringMeasurementPeriod
		with "Medications Not Documented for Medical Reason" MedicationsNotDocumented
			such that MedicationsNotDocumented.authorDatetime during EncounterDuringMeasurementPeriod.relevantPeriod

define "Medications Documented During Qualifying Encounter":
	"Qualifying Encounters During Measurement Period" QualifyingEncounterDuringMeasurementPeriod
		with ["Procedure, Performed": "Documentation of current medications (procedure)"] MedicationsDocumented
			such that MedicationsDocumented.relevantPeriod during QualifyingEncounterDuringMeasurementPeriod.relevantPeriod

define "Numerator":
	"Medications Documented During Qualifying Encounter"

define "Initial Population":
	"Qualifying Encounters During Measurement Period" QualifyingEncounter
		where "Patient Age 18 or Older at Start of Measurement Period"

define "Qualifying Encounters During Measurement Period":
	["Encounter, Performed": "Medications Encounter Code Set"] QualifyingEncounter
		where QualifyingEncounter.relevantPeriod during "Measurement Period"

define "Patient Age 18 or Older at Start of Measurement Period":
	exists ["Patient Characteristic Birthdate": "Birth date"] BirthDate
		where Global."CalendarAgeInYearsAt"(BirthDate.birthDatetime, start of "Measurement Period")>= 18