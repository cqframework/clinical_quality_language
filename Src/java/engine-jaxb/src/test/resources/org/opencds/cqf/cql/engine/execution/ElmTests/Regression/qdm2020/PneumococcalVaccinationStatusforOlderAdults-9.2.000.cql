library PneumococcalVaccinationStatusforOlderAdults version '9.2.000'

using QDM version '5.5'

include MATGlobalCommonFunctions version '5.0.000' called Global
include Hospice version '2.2.000' called Hospice

codesystem "LOINC": 'urn:oid:2.16.840.1.113883.6.1'

valueset "Annual Wellness Visit": 'urn:oid:2.16.840.1.113883.3.526.3.1240'
valueset "Care Services in Long-Term Residential Facility": 'urn:oid:2.16.840.1.113883.3.464.1003.101.12.1014'
valueset "Discharge Services - Nursing Facility": 'urn:oid:2.16.840.1.113883.3.464.1003.101.11.1065'
valueset "Ethnicity": 'urn:oid:2.16.840.1.114222.4.11.837'
valueset "Home Healthcare Services": 'urn:oid:2.16.840.1.113883.3.464.1003.101.12.1016'
valueset "Nursing Facility Visit": 'urn:oid:2.16.840.1.113883.3.464.1003.101.12.1012'
valueset "Office Visit": 'urn:oid:2.16.840.1.113883.3.464.1003.101.12.1001'
valueset "ONC Administrative Sex": 'urn:oid:2.16.840.1.113762.1.4.1'
valueset "Payer": 'urn:oid:2.16.840.1.114222.4.11.3591'
valueset "Pneumococcal Vaccine": 'urn:oid:2.16.840.1.113883.3.464.1003.110.12.1027'
valueset "Pneumococcal Vaccine Administered": 'urn:oid:2.16.840.1.113883.3.464.1003.110.12.1034'
valueset "Preventive Care Services - Established Office Visit, 18 and Up": 'urn:oid:2.16.840.1.113883.3.464.1003.101.12.1025'
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

define "Qualifying Encounters":
  ( ["Encounter, Performed": "Office Visit"]
    union ["Encounter, Performed": "Annual Wellness Visit"]
    union ["Encounter, Performed": "Preventive Care Services - Established Office Visit, 18 and Up"]
    union ["Encounter, Performed": "Preventive Care Services-Initial Office Visit, 18 and Up"]
    union ["Encounter, Performed": "Home Healthcare Services"]
    union ["Encounter, Performed": "Care Services in Long-Term Residential Facility"]
    union ["Encounter, Performed": "Nursing Facility Visit"]
    union ["Encounter, Performed": "Discharge Services - Nursing Facility"] ) ValidEncounter
    where ValidEncounter.relevantPeriod during "Measurement Period"

define "Denominator Exclusions":
  Hospice."Has Hospice"

define "Initial Population":
  exists ( ["Patient Characteristic Birthdate": "Birth date"] BirthDate
      where Global."CalendarAgeInYearsAt" ( BirthDate.birthDatetime, start of "Measurement Period" ) >= 65
  )
    and exists "Qualifying Encounters"

define "Numerator":
  exists ( ["Immunization, Administered": "Pneumococcal Vaccine"] PneumococcalVaccine
      where PneumococcalVaccine.relevantDatetime on or before 
      end of "Measurement Period"
  )
    or exists ( ["Procedure, Performed": "Pneumococcal Vaccine Administered"] PneumococcalVaccineGiven
        where PneumococcalVaccineGiven.relevantDatetime on or before 
        end of "Measurement Period"
    )