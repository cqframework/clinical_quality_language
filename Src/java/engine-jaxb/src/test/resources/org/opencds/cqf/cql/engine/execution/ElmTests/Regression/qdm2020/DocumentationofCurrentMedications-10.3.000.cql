library DocumentationofCurrentMedications version '10.3.000'

using QDM version '5.5'

include MATGlobalCommonFunctions version '5.0.000' called Global

codesystem "LOINC": 'urn:oid:2.16.840.1.113883.6.1'
codesystem "SNOMEDCT": 'urn:oid:2.16.840.1.113883.6.96'

valueset "Encounter to Document Medications": 'urn:oid:2.16.840.1.113883.3.600.1.1834'
valueset "Ethnicity": 'urn:oid:2.16.840.1.114222.4.11.837'
valueset "Medical Reason": 'urn:oid:2.16.840.1.113883.3.526.3.1007'
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

define "Numerator":
  "Qualifying Encounter During Measurement Period" QualifyingEncounter
    with ( ["Procedure, Performed": "Documentation of current medications (procedure)"]
      union ["Intervention, Performed": "Documentation of current medications (procedure)"] ) MedicationsDocumented
      such that MedicationsDocumented.relevantDatetime during QualifyingEncounter.relevantPeriod

define "Initial Population":
  "Qualifying Encounter During Measurement Period" QualifyingEncounter
    with ["Patient Characteristic Birthdate": "Birth date"] BirthDate
      such that Global."CalendarAgeInYearsAt" ( BirthDate.birthDatetime, start of "Measurement Period" ) >= 18

define "Denominator":
  "Initial Population"

define "Denominator Exceptions":
  "Qualifying Encounter During Measurement Period" QualifyingEncounter
    with ( ["Procedure, Not Performed": "Documentation of current medications (procedure)"]
      union ["Intervention, Not Performed": "Documentation of current medications (procedure)"] ) MedicationsNotDocumented
      such that MedicationsNotDocumented.authorDatetime during QualifyingEncounter.relevantPeriod
        and MedicationsNotDocumented.negationRationale in "Medical Reason"

define "Qualifying Encounter During Measurement Period":
  ["Encounter, Performed": "Encounter to Document Medications"] ValidEncounter
    where ValidEncounter.relevantPeriod during "Measurement Period"