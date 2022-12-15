library AntidepressantMedicationManagement version '9.2.000'

using QDM version '5.5'

include MATGlobalCommonFunctions version '5.0.000' called Global
include Hospice version '2.2.000' called Hospice

codesystem "LOINC": 'urn:oid:2.16.840.1.113883.6.1'

valueset "Annual Wellness Visit": 'urn:oid:2.16.840.1.113883.3.526.3.1240'
valueset "Antidepressant Medication": 'urn:oid:2.16.840.1.113883.3.464.1003.196.12.1213'
valueset "Ethnicity": 'urn:oid:2.16.840.1.114222.4.11.837'
valueset "Home Healthcare Services": 'urn:oid:2.16.840.1.113883.3.464.1003.101.12.1016'
valueset "Major Depression": 'urn:oid:2.16.840.1.113883.3.464.1003.105.12.1007'
valueset "Nursing Facility Visit": 'urn:oid:2.16.840.1.113883.3.464.1003.101.12.1012'
valueset "Office Visit": 'urn:oid:2.16.840.1.113883.3.464.1003.101.12.1001'
valueset "ONC Administrative Sex": 'urn:oid:2.16.840.1.113762.1.4.1'
valueset "Payer": 'urn:oid:2.16.840.1.114222.4.11.3591'
valueset "Preventive Care Services - Established Office Visit, 18 and Up": 'urn:oid:2.16.840.1.113883.3.464.1003.101.12.1025'
valueset "Preventive Care Services-Initial Office Visit, 18 and Up": 'urn:oid:2.16.840.1.113883.3.464.1003.101.12.1023'
valueset "Psych Visit - Diagnostic Evaluation": 'urn:oid:2.16.840.1.113883.3.526.3.1492'
valueset "Psych Visit - Psychotherapy": 'urn:oid:2.16.840.1.113883.3.526.3.1496'
valueset "Race": 'urn:oid:2.16.840.1.114222.4.11.836'
valueset "Telephone Evaluation": 'urn:oid:2.16.840.1.113883.3.464.1003.101.12.1082'
valueset "Telephone Management": 'urn:oid:2.16.840.1.113883.3.464.1003.101.12.1053'

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

define "Antidepressant Dispensed 245 Days Before or 120 Days After Start of Measurement Period":
  First(["Medication, Dispensed": "Antidepressant Medication"] Antidepressant
      where Antidepressant.relevantDatetime 245 days or less before start of "Measurement Period"
        or Antidepressant.relevantDatetime 120 days or less on or after start of "Measurement Period"
      sort by relevantDatetime
  )

define "Denominator":
  "Initial Population"

define "Qualifying Encounters":
  ( ["Encounter, Performed": "Office Visit"]
    union ["Encounter, Performed": "Preventive Care Services - Established Office Visit, 18 and Up"]
    union ["Encounter, Performed": "Preventive Care Services-Initial Office Visit, 18 and Up"]
    union ["Encounter, Performed": "Home Healthcare Services"]
    union ["Encounter, Performed": "Telephone Management"]
    union ["Encounter, Performed": "Telephone Evaluation"]
    union ["Encounter, Performed": "Annual Wellness Visit"]
    union ["Encounter, Performed": "Nursing Facility Visit"]
    union ["Encounter, Performed": "Psych Visit - Diagnostic Evaluation"]
    union ["Encounter, Performed": "Psych Visit - Psychotherapy"] ) ValidEncounter
    let AntidepressantDispensed: "Antidepressant Dispensed 245 Days Before or 120 Days After Start of Measurement Period"
    where start of ValidEncounter.relevantPeriod within 60 days of AntidepressantDispensed.relevantDatetime

define "Initial Population":
  exists ( ["Patient Characteristic Birthdate": "Birth date"] BirthDate
      where Global."CalendarAgeInYearsAt" ( BirthDate.birthDatetime, start of "Measurement Period" ) >= 18
  )
    and "Has Initial Major Depression Diagnosis"
    and exists "Qualifying Encounters"

define "Numerator 2":
  "Cumulative Medication Duration Greater Than or Equal to 180 Days"

define "Numerator 1":
  "Cumulative Medication Duration Greater Than or Equal to 84 Days"

define "Antidepressant Medication Period In 231 Days After Initial Dispense":
  ["Medication, Active": "Antidepressant Medication"] ActiveAntidepressant
    let InitialAntidepressantDispensed: "Antidepressant Dispensed 245 Days Before or 120 Days After Start of Measurement Period"
    where ActiveAntidepressant.relevantPeriod starts 231 days or less on or after day of InitialAntidepressantDispensed.relevantDatetime
    return ActiveAntidepressant.relevantPeriod
      intersect Interval[InitialAntidepressantDispensed.relevantDatetime, InitialAntidepressantDispensed.relevantDatetime + 231 days]

define "Antidepressant Medication Period In 114 Days After Initial Dispense":
  ["Medication, Active": "Antidepressant Medication"] ActiveAntidepressant
    let AntidepressantDispensed: "Antidepressant Dispensed 245 Days Before or 120 Days After Start of Measurement Period"
    where ActiveAntidepressant.relevantPeriod starts 114 days or less on or after day of AntidepressantDispensed.relevantDatetime
    return ActiveAntidepressant.relevantPeriod
      intersect Interval[AntidepressantDispensed.relevantDatetime, AntidepressantDispensed.relevantDatetime + 114 days]

define "Cumulative Medication Duration Greater Than or Equal to 180 Days":
  "Cumulative Medication Duration"("Antidepressant Medication Period In 231 Days After Initial Dispense")>= 180

define "Cumulative Medication Duration Greater Than or Equal to 84 Days":
  "Cumulative Medication Duration"("Antidepressant Medication Period In 114 Days After Initial Dispense")>= 84

define "Has Initial Major Depression Diagnosis":
  exists ( ["Diagnosis": "Major Depression"] MajorDepression
      let AntidepressantDispensed: "Antidepressant Dispensed 245 Days Before or 120 Days After Start of Measurement Period"
      where ( start of MajorDepression.prevalencePeriod within 60 days of AntidepressantDispensed.relevantDatetime
          and AntidepressantDispensed.relevantDatetime is not null
      )
  )

define "Denominator Exclusions":
  Hospice."Has Hospice"
    or exists ( ["Medication, Active": "Antidepressant Medication"] OnAntiDepressant
        let AntidepressantDispensed: "Antidepressant Dispensed 245 Days Before or 120 Days After Start of Measurement Period"
        where OnAntiDepressant.relevantPeriod overlaps Interval[AntidepressantDispensed.relevantDatetime - 105 days, AntidepressantDispensed.relevantDatetime )
          and AntidepressantDispensed.relevantDatetime is not null
    )

define function "Cumulative Medication Duration"(Medication List<Interval<DateTime>>):
  Sum((collapse(Medication))CumulativeAntidepressant
      return all duration in days of CumulativeAntidepressant
  )