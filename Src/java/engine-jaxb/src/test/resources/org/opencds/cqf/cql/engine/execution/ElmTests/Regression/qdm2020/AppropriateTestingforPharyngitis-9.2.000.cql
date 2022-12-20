library AppropriateTestingforPharyngitis version '9.2.000'

using QDM version '5.5'

include MATGlobalCommonFunctions version '5.0.000' called Global
include Hospice version '2.2.000' called Hospice

codesystem "LOINC": 'urn:oid:2.16.840.1.113883.6.1'
codesystem "CPT": 'urn:oid:2.16.840.1.113883.6.12'

valueset "Acute Pharyngitis": 'urn:oid:2.16.840.1.113883.3.464.1003.102.12.1011'
valueset "Acute Tonsillitis": 'urn:oid:2.16.840.1.113883.3.464.1003.102.12.1012'
valueset "Antibiotic Medications for Pharyngitis": 'urn:oid:2.16.840.1.113883.3.464.1003.196.12.1001'
valueset "Comorbid Conditions for Respiratory Conditions": 'urn:oid:2.16.840.1.113883.3.464.1003.102.12.1025'
valueset "Competing Conditions for Respiratory Conditions": 'urn:oid:2.16.840.1.113883.3.464.1003.102.12.1017'
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
valueset "Telephone Visits": 'urn:oid:2.16.840.1.113883.3.464.1003.101.12.1080'

code "Birth date": '21112-8' from "LOINC" display 'Birth date'
code "Observation care discharge day management (This code is to be utilized to report all services provided to a patient on discharge from outpatient hospital observation status if the discharge is on other than the initial date of observation status. To report services to a patient designated as observation status or inpatient status and discharged on the same date, use the codes for Observation or Inpatient Care Services [including Admission and Discharge Services, 99234-99236 as appropriate.])": '99217' from "CPT" display 'Observation care discharge day management (This code is to be utilized to report all services provided to a patient on discharge from outpatient hospital "observation status" if the discharge is on other than the initial date of "observation status." To report services to a patient designated as "observation status" or "inpatient status" and discharged on the same date, use the codes for Observation or Inpatient Care Services [including Admission and Discharge Services, 99234-99236 as appropriate.])'

parameter "Measurement Period" Interval<DateTime>

context Patient

define "Denominator":
  "Initial Population"

define "Group A Streptococcus Lab Test With Result":
  ["Laboratory Test, Performed": "Group A Streptococcus Test"] GroupAStreptococcusTest
    where GroupAStreptococcusTest.result is not null

define "In Hospice":
  "Initial Population" EligibleEncounters
    where Hospice."Has Hospice"

define "Pharyngitis or Tonsillitis":
  ( ["Diagnosis": "Acute Pharyngitis"]
      union ["Diagnosis": "Acute Tonsillitis"]
  )

define "SDE Ethnicity":
  ["Patient Characteristic Ethnicity": "Ethnicity"]

define "SDE Payer":
  ["Patient Characteristic Payer": "Payer"]

define "SDE Race":
  ["Patient Characteristic Race": "Race"]

define "SDE Sex":
  ["Patient Characteristic Sex": "ONC Administrative Sex"]

define "Stratification 2":
  "Encounter With Pharyngitis or Tonsillitis" EncounterWithPharyngitis
    with ["Patient Characteristic Birthdate": "Birth date"] BirthDate
      such that Global."CalendarAgeInYearsAt" ( BirthDate.birthDatetime, start of "Measurement Period" ) in Interval[18, 64]
    return EncounterWithPharyngitis.VisitWithAntibiotic

define "Stratification 3":
  "Encounter With Pharyngitis or Tonsillitis" EncounterWithPharyngitis
    with ["Patient Characteristic Birthdate": "Birth date"] BirthDate
      such that Global."CalendarAgeInYearsAt" ( BirthDate.birthDatetime, start of "Measurement Period" ) >= 65
    return EncounterWithPharyngitis.VisitWithAntibiotic

define "Numerator":
  from
    "Group A Streptococcus Lab Test With Result" GroupAStreptococcusTest,
    "Encounter With Pharyngitis or Tonsillitis" EncounterWithPharyngitis
    where ( GroupAStreptococcusTest.relevantDatetime 3 days or less on or before day of 
      end of EncounterWithPharyngitis.VisitWithAntibiotic.relevantPeriod
        or GroupAStreptococcusTest.relevantDatetime 3 days or less on or after day of 
        end of EncounterWithPharyngitis.VisitWithAntibiotic.relevantPeriod
    )
    return EncounterWithPharyngitis.VisitWithAntibiotic

define "Encounter With Antibiotic Ordered Within Three Days":
  "Qualifying Encounter" EDOrAmbulatoryVisit
    with ["Medication, Order": "Antibiotic Medications for Pharyngitis"] AntibioticOrdered
      such that ( EDOrAmbulatoryVisit.relevantPeriod starts 3 days or less on or before AntibioticOrdered.authorDatetime )

define "Encounter With Antibiotic Active In 30 Days Prior":
  "Encounter With Pharyngitis or Tonsillitis" EncounterWithPharyngitis
    with ["Medication, Active": "Antibiotic Medications for Pharyngitis"] ActiveAntibiotic
      such that ActiveAntibiotic.relevantPeriod overlaps Interval[Global."ToDate" ( ( start of EncounterWithPharyngitis.AcutePharyngitisTonsillitis.prevalencePeriod ) - 30 days ), Global."ToDate" ( start of EncounterWithPharyngitis.AcutePharyngitisTonsillitis.prevalencePeriod ) )
    return EncounterWithPharyngitis.VisitWithAntibiotic

define "Stratification 1":
  "Encounter With Pharyngitis or Tonsillitis" EncounterWithPharyngitis
    with ["Patient Characteristic Birthdate": "Birth date"] BirthDate
      such that Global."CalendarAgeInYearsAt" ( BirthDate.birthDatetime, start of "Measurement Period" ) in Interval[3, 17]
    return EncounterWithPharyngitis.VisitWithAntibiotic

define "Encounter With Comorbid Condition Prior":
  ( "Encounter With Pharyngitis or Tonsillitis" EncounterWithPharyngitis
      with ["Diagnosis": "Comorbid Conditions for Respiratory Conditions"] ComorbidCondition
        such that start of ComorbidCondition.prevalencePeriod in Interval[start of EncounterWithPharyngitis.VisitWithAntibiotic.relevantPeriod - 1 year, start of EncounterWithPharyngitis.VisitWithAntibiotic.relevantPeriod]
      return EncounterWithPharyngitis.VisitWithAntibiotic
  )

define "Initial Population":
  "Encounter With Pharyngitis or Tonsillitis" EncounterWithPharyngitis
    with ["Patient Characteristic Birthdate": "Birth date"] BirthDate
      such that Global."CalendarAgeInYearsAt" ( BirthDate.birthDatetime, start of "Measurement Period" ) >= 3
    return EncounterWithPharyngitis.VisitWithAntibiotic

define "Encounter With Pharyngitis or Tonsillitis":
  from
    "Encounter With Antibiotic Ordered Within Three Days" VisitWithAntibiotic,
    "Pharyngitis or Tonsillitis" AcutePharyngitisTonsillitis
    where AcutePharyngitisTonsillitis.prevalencePeriod starts during VisitWithAntibiotic.relevantPeriod

define "Encounter With Competing Diagnosis for Upper Respiratory Infection":
  "Encounter With Pharyngitis or Tonsillitis" EncounterWithPharyngitis
    with ["Diagnosis": "Competing Conditions for Respiratory Conditions"] CompetingCondition
      such that EncounterWithPharyngitis.AcutePharyngitisTonsillitis !~ CompetingCondition
        and CompetingCondition.prevalencePeriod starts 3 days or less after start of EncounterWithPharyngitis.AcutePharyngitisTonsillitis.prevalencePeriod
    return EncounterWithPharyngitis.VisitWithAntibiotic

define "Denominator Exclusions":
  "In Hospice"
    union "Encounter With Antibiotic Active In 30 Days Prior"
    union "Encounter With Competing Diagnosis for Upper Respiratory Infection"
    union "Encounter With Comorbid Condition Prior"

define "Qualifying Encounter":
  ( ["Encounter, Performed": "Observation care discharge day management (This code is to be utilized to report all services provided to a patient on discharge from outpatient hospital observation status if the discharge is on other than the initial date of observation status. To report services to a patient designated as observation status or inpatient status and discharged on the same date, use the codes for Observation or Inpatient Care Services [including Admission and Discharge Services, 99234-99236 as appropriate.])"]
    union ["Encounter, Performed": "Emergency Department Visit"]
    union ["Encounter, Performed": "Home Healthcare Services"]
    union ["Encounter, Performed": "Hospital Observation Care - Initial"]
    union ["Encounter, Performed": "Medical Disability Exam"]
    union ["Encounter, Performed": "Office Visit"]
    union ["Encounter, Performed": "Telephone Visits"]
    union ["Encounter, Performed": "Outpatient Consultation"]
    union ["Encounter, Performed": "Preventive Care Services - Established Office Visit, 18 and Up"]
    union ["Encounter, Performed": "Preventive Care Services - Group Counseling"]
    union ["Encounter, Performed": "Preventive Care Services - Other"]
    union ["Encounter, Performed": "Preventive Care Services, Initial Office Visit, 0 to 17"]
    union ["Encounter, Performed": "Preventive Care Services-Individual Counseling"]
    union ["Encounter, Performed": "Preventive Care Services-Initial Office Visit, 18 and Up"]
    union ["Encounter, Performed": "Preventive Care, Established Office Visit, 0 to 17"] ) ValidEncounter
    where ValidEncounter.relevantPeriod during "Measurement Period"