library HIVScreening version '3.3.000'

using QDM version '5.5'

include MATGlobalCommonFunctions version '5.0.000' called Global

codesystem "LOINC": 'urn:oid:2.16.840.1.113883.6.1'

valueset "Ethnicity": 'urn:oid:2.16.840.1.114222.4.11.837'
valueset "Human Immunodeficiency Virus (HIV) Laboratory Test Codes (Ab and Ag)": 'urn:oid:2.16.840.1.113762.1.4.1056.50'
valueset "Indicators of Human Immunodeficiency Virus (HIV)": 'urn:oid:2.16.840.1.113762.1.4.1056.54'
valueset "Office Visit": 'urn:oid:2.16.840.1.113883.3.464.1003.101.12.1001'
valueset "ONC Administrative Sex": 'urn:oid:2.16.840.1.113762.1.4.1'
valueset "Payer": 'urn:oid:2.16.840.1.114222.4.11.3591'
valueset "Preventive Care Services - Established Office Visit, 18 and Up": 'urn:oid:2.16.840.1.113883.3.464.1003.101.12.1025'
valueset "Preventive Care Services, Initial Office Visit, 0 to 17": 'urn:oid:2.16.840.1.113883.3.464.1003.101.12.1022'
valueset "Preventive Care Services-Initial Office Visit, 18 and Up": 'urn:oid:2.16.840.1.113883.3.464.1003.101.12.1023'
valueset "Preventive Care, Established Office Visit, 0 to 17": 'urn:oid:2.16.840.1.113883.3.464.1003.101.12.1024'
valueset "Race": 'urn:oid:2.16.840.1.114222.4.11.836'

code "Birth date": '21112-8' from "LOINC" display 'Birth date'
code "HIV 1 and 2 tests - Meaningful Use set": '75622-1' from "LOINC" display 'HIV 1 and 2 tests - Meaningful Use set'

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
  exists ( "HIV Test Performed" )

define "Denominator Exclusions":
  exists ( ["Diagnosis": "Indicators of Human Immunodeficiency Virus (HIV)"] HIVIndicators
      where HIVIndicators.prevalencePeriod starts before start of "Measurement Period"
  )

define "HIV Test Performed":
  ( ["Laboratory Test, Performed": "Human Immunodeficiency Virus (HIV) Laboratory Test Codes (Ab and Ag)"]
    union ["Laboratory Test, Performed": "HIV 1 and 2 tests - Meaningful Use set"] ) HIVTest
    where exists ( ["Patient Characteristic Birthdate": "Birth date"] BirthDate
        where Global."CalendarAgeInYearsAt" ( BirthDate.birthDatetime, HIVTest.relevantDatetime ) in Interval[15, 66 )
    )
      and HIVTest.relevantDatetime before 
      end of "Measurement Period"

define "Initial Population":
  exists ( ["Patient Characteristic Birthdate": "Birth date"] BirthDate
      where Global."CalendarAgeInYearsAt" ( BirthDate.birthDatetime, start of "Measurement Period" ) in Interval[15, 66 )
  )
    and exists "Qualifying Encounters"

define "Qualifying Encounters":
  ( ["Encounter, Performed": "Preventive Care Services, Initial Office Visit, 0 to 17"]
    union ["Encounter, Performed": "Preventive Care Services-Initial Office Visit, 18 and Up"]
    union ["Encounter, Performed": "Preventive Care, Established Office Visit, 0 to 17"]
    union ["Encounter, Performed": "Preventive Care Services - Established Office Visit, 18 and Up"]
    union ["Encounter, Performed": "Office Visit"] ) Encounter
    where Encounter.relevantPeriod during "Measurement Period"

define "Denominator":
  "Initial Population"