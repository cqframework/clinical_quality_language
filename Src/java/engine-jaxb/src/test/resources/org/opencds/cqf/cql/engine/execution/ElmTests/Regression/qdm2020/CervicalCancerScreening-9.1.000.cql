library CervicalCancerScreening version '9.1.000'

using QDM version '5.5'

include MATGlobalCommonFunctions version '5.0.000' called Global
include Hospice version '2.2.000' called Hospice

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

define "Denominator":
  "Initial Population"

define "Qualifying Encounters":
  ( ["Encounter, Performed": "Office Visit"]
    union ["Encounter, Performed": "Preventive Care Services - Established Office Visit, 18 and Up"]
    union ["Encounter, Performed": "Preventive Care Services-Initial Office Visit, 18 and Up"]
    union ["Encounter, Performed": "Home Healthcare Services"] ) ValidEncounter
    where ValidEncounter.relevantPeriod during "Measurement Period"

define "SDE Ethnicity":
  ["Patient Characteristic Ethnicity": "Ethnicity"]

define "SDE Payer":
  ["Patient Characteristic Payer": "Payer"]

define "SDE Race":
  ["Patient Characteristic Race": "Race"]

define "SDE Sex":
  ["Patient Characteristic Sex": "ONC Administrative Sex"]

define "Absence of Cervix":
  ( ["Procedure, Performed": "Hysterectomy with No Residual Cervix"] NoCervixProcedure
      where NoCervixProcedure.relevantPeriod ends on or before 
      end of "Measurement Period"
  )
    union ( ["Diagnosis": "Congenital absence of cervix (disorder)"] NoCervixDiagnosis
        where NoCervixDiagnosis.prevalencePeriod starts on or before 
        end of "Measurement Period"
    )

define "Cervical Cytology Within 3 Years":
  ["Laboratory Test, Performed": "Pap Test"] CervicalCytology
    where CervicalCytology.relevantDatetime 3 years or less on or before 
    end of "Measurement Period"
      and CervicalCytology.result is not null

define "HPV Test Within 5 Years for Women Age 30 and Older":
  ["Laboratory Test, Performed": "HPV Test"] HPVTest
    with ["Patient Characteristic Birthdate": "Birth date"] BirthDate
      such that Global."CalendarAgeInYearsAt" ( BirthDate.birthDatetime, HPVTest.relevantDatetime ) >= 30
    where HPVTest.relevantDatetime 5 years or less on or before 
    end of "Measurement Period"
      and HPVTest.result is not null

define "Initial Population":
  exists ( ["Patient Characteristic Birthdate": "Birth date"] BirthDate
      where Global."CalendarAgeInYearsAt" ( BirthDate.birthDatetime, start of "Measurement Period" ) in Interval[23, 64 )
  )
    and exists ( ["Patient Characteristic Sex": "Female"] )
    and exists "Qualifying Encounters"

define "Numerator":
  exists "Cervical Cytology Within 3 Years"
    or exists "HPV Test Within 5 Years for Women Age 30 and Older"

define "Denominator Exclusions":
  Hospice."Has Hospice"
    or exists "Absence of Cervix"