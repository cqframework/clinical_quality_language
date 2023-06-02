library FollowUpCareforChildrenPrescribedADHDMedicationADD version '10.2.000'

using QDM version '5.5'

include MATGlobalCommonFunctions version '5.0.000' called Global
include Hospice version '2.2.000' called Hospice

codesystem "LOINC": 'urn:oid:2.16.840.1.113883.6.1'
codesystem "CPT": 'urn:oid:2.16.840.1.113883.6.12'

valueset "ADHD Medications": 'urn:oid:2.16.840.1.113883.3.464.1003.196.12.1171'
valueset "Ambulatory": 'urn:oid:2.16.840.1.113883.3.464.1003.122.12.1003'
valueset "Behavioral Health Follow-up Visit": 'urn:oid:2.16.840.1.113883.3.464.1003.101.12.1054'
valueset "Encounter Inpatient": 'urn:oid:2.16.840.1.113883.3.666.5.307'
valueset "Ethnicity": 'urn:oid:2.16.840.1.114222.4.11.837'
valueset "Home Healthcare Services": 'urn:oid:2.16.840.1.113883.3.464.1003.101.12.1016'
valueset "Hospital Observation Care - Initial": 'urn:oid:2.16.840.1.113883.3.464.1003.101.12.1002'
valueset "Mental Health Diagnoses": 'urn:oid:2.16.840.1.113883.3.464.1003.105.12.1004'
valueset "Narcolepsy": 'urn:oid:2.16.840.1.113883.3.464.1003.114.12.1011'
valueset "Office Visit": 'urn:oid:2.16.840.1.113883.3.464.1003.101.12.1001'
valueset "ONC Administrative Sex": 'urn:oid:2.16.840.1.113762.1.4.1'
valueset "Outpatient Consultation": 'urn:oid:2.16.840.1.113883.3.464.1003.101.12.1008'
valueset "Payer": 'urn:oid:2.16.840.1.114222.4.11.3591'
valueset "Preventive Care Services - Group Counseling": 'urn:oid:2.16.840.1.113883.3.464.1003.101.12.1027'
valueset "Preventive Care Services, Initial Office Visit, 0 to 17": 'urn:oid:2.16.840.1.113883.3.464.1003.101.12.1022'
valueset "Preventive Care Services-Individual Counseling": 'urn:oid:2.16.840.1.113883.3.464.1003.101.12.1026'
valueset "Preventive Care, Established Office Visit, 0 to 17": 'urn:oid:2.16.840.1.113883.3.464.1003.101.12.1024'
valueset "Psych Visit - Diagnostic Evaluation": 'urn:oid:2.16.840.1.113883.3.526.3.1492'
valueset "Psych Visit - Psychotherapy": 'urn:oid:2.16.840.1.113883.3.526.3.1496'
valueset "Psychotherapy and Pharmacologic Management": 'urn:oid:2.16.840.1.113883.3.464.1003.101.12.1055'
valueset "Race": 'urn:oid:2.16.840.1.114222.4.11.836'
valueset "Substance Abuse": 'urn:oid:2.16.840.1.113883.3.464.1003.106.12.1004'
valueset "Telehealth Services": 'urn:oid:2.16.840.1.113883.3.464.1003.101.12.1031'
valueset "Telephone Management": 'urn:oid:2.16.840.1.113883.3.464.1003.101.12.1053'

code "Birth date": '21112-8' from "LOINC" display 'Birth date'
code "Observation care discharge day management (This code is to be utilized to report all services provided to a patient on discharge from outpatient hospital observation status if the discharge is on other than the initial date of observation status. To report services to a patient designated as observation status or inpatient status and discharged on the same date, use the codes for Observation or Inpatient Care Services [including Admission and Discharge Services, 99234-99236 as appropriate.])": '99217' from "CPT" display 'Observation care discharge day management (This code is to be utilized to report all services provided to a patient on discharge from outpatient hospital "observation status" if the discharge is on other than the initial date of "observation status." To report services to a patient designated as "observation status" or "inpatient status" and discharged on the same date, use the codes for Observation or Inpatient Care Services [including Admission and Discharge Services, 99234-99236 as appropriate.])'

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

define "Denominator 1":
  "Initial Population 1"

define "Denominator 2":
  "Initial Population 2"

define "Qualifying Encounter":
  ( ["Encounter, Performed": "Office Visit"]
    union ["Encounter, Performed": "Home Healthcare Services"]
    union ["Encounter, Performed": "Preventive Care, Established Office Visit, 0 to 17"]
    union ["Encounter, Performed": "Preventive Care Services, Initial Office Visit, 0 to 17"] ) ValidEncounter
    where ValidEncounter.relevantPeriod during "Measurement Period"

define "Narcolepsy Exclusion":
  ["Diagnosis": "Narcolepsy"] Narcolepsy
    where Narcolepsy.prevalencePeriod starts before 
    end "Measurement Period"

define "Two or More Followup Encounters":
  Count("Encounters 300 Days after Initial ADHD Medication")>= 2

define "Denominator Exclusions 1":
  Hospice."Has Hospice"
    or exists ( "Narcolepsy Exclusion" )
    or exists ( "Inpatient Stay with Qualifying Diagnosis within 30 Days of Initial ADHD Dispense" )
    or exists ( "Previously on ADHD Medication Exclusion" )

define "Denominator Exclusions 2":
  Hospice."Has Hospice"
    or exists ( "Narcolepsy Exclusion" )
    or exists ( "Inpatient Stay with Qualifying Diagnosis within 300 Days of Initial ADHD Dispense" )
    or exists ( "Previously on ADHD Medication Exclusion" )

define "Has ADHD Cumulative Medication Duration Greater Than or Equal to 210 Days":
  "Cumulative Medication Duration"("ADHD Medications Taken within 300 Days after First ADHD Medication Dispensed")>= 210

define "Numerator 2":
  exists ( "Encounter 30 Days after Initial ADHD Medication" )
    and ( ( "Two or More Followup Encounters" )
        or ( exists ( "Encounters 300 Days after Initial ADHD Medication" )
            and exists ( "Telehealth Services or Telephone Management Encounter" )
        )
    )

define "Numerator 1":
  exists ( "Encounter 30 Days after Initial ADHD Medication" )

define "Initial Population 1":
  exists ["Patient Characteristic Birthdate": "Birth date"] BirthDate
    where Global."CalendarAgeInYearsAt" ( BirthDate.birthDatetime, start of "Measurement Period" ) in Interval[6, 12 )
      and exists ( "Qualifying Encounter" )
      and "First ADHD Medication Dispensed" is not null

define "Inpatient Stay with Qualifying Diagnosis within 300 Days of Initial ADHD Dispense":
  "Inpatient Stay with Qualifying Diagnosis" InpatientStay
    with "First ADHD Medication Dispensed" InitialADHDMedication
      such that InpatientStay.relevantPeriod starts 300 days or less on or after day of InitialADHDMedication.relevantDatetime

define "Inpatient Stay with Qualifying Diagnosis within 30 Days of Initial ADHD Dispense":
  "Inpatient Stay with Qualifying Diagnosis" InpatientStay
    with "First ADHD Medication Dispensed" InitialADHDMedication
      such that InpatientStay.relevantPeriod starts 30 days or less on or after InitialADHDMedication.relevantDatetime

define "Previously on ADHD Medication Exclusion":
  ["Medication, Active": "ADHD Medications"] ActiveADHDMedication
    with "First ADHD Medication Dispensed" InitialADHDMedication
      such that ActiveADHDMedication.relevantPeriod starts 120 days or less before InitialADHDMedication.relevantDatetime

define "ADHD Medications Taken within 300 Days after First ADHD Medication Dispensed":
  ["Medication, Active": "ADHD Medications"] ActiveADHDMedications
    with "First ADHD Medication Dispensed" InitialADHDMedication
      such that ActiveADHDMedications.relevantPeriod starts 300 days or less on or after day of InitialADHDMedication.relevantDatetime

define "Encounter 30 Days after Initial ADHD Medication":
  ( "Qualifying Numerator Encounter" ValidNumeratorEncounter
      with "First ADHD Medication Dispensed" InitialADHDMedication
        such that ValidNumeratorEncounter.relevantPeriod starts 30 days or less on or after InitialADHDMedication.relevantDatetime
  )

define "First ADHD Medication Dispensed":
  First(["Medication, Dispensed": "ADHD Medications"] FirstADHDMedication
      where FirstADHDMedication.relevantDatetime 60 days or less after start "Measurement Period"
        or FirstADHDMedication.relevantDatetime 90 days or less on or before start "Measurement Period"
      sort by relevantDatetime
  )

define "Telehealth Services or Telephone Management Encounter":
  ( ["Encounter, Performed": "Telehealth Services"]
    union ["Encounter, Performed": "Telephone Management"] ) TelehealthEncounter
    with "First ADHD Medication Dispensed" InitialADHDMedication
      such that TelehealthEncounter.relevantPeriod starts 31 days or more after day of InitialADHDMedication.relevantDatetime
        and TelehealthEncounter.relevantPeriod starts 300 days or less after day of InitialADHDMedication.relevantDatetime

define "Inpatient Stay with Qualifying Diagnosis":
  ["Encounter, Performed": "Encounter Inpatient"] InpatientStay
    where exists ( InpatientStay.diagnoses EncounterDiagnoses
        where EncounterDiagnoses.rank = 1
          and ( EncounterDiagnoses.code in "Mental Health Diagnoses"
              or EncounterDiagnoses.code in "Substance Abuse"
          )
    )

define "Qualifying Numerator Encounter":
  ( ["Encounter, Performed": "Office Visit"]
      union ["Encounter, Performed": "Hospital Observation Care - Initial"]
      union ["Encounter, Performed": "Preventive Care Services - Group Counseling"]
      union ["Encounter, Performed": "Behavioral Health Follow-up Visit"]
      union ["Encounter, Performed": "Preventive Care Services-Individual Counseling"]
      union ( ["Encounter, Performed": "Psychotherapy and Pharmacologic Management"] PsychPharmMgmt
          where exists ( PsychPharmMgmt.facilityLocations Location
              where Location.code in "Ambulatory"
          )
      )
      union ["Encounter, Performed": "Observation care discharge day management (This code is to be utilized to report all services provided to a patient on discharge from outpatient hospital observation status if the discharge is on other than the initial date of observation status. To report services to a patient designated as observation status or inpatient status and discharged on the same date, use the codes for Observation or Inpatient Care Services [including Admission and Discharge Services, 99234-99236 as appropriate.])"]
      union ["Encounter, Performed": "Outpatient Consultation"]
      union ["Encounter, Performed": "Home Healthcare Services"]
      union ["Encounter, Performed": "Preventive Care Services, Initial Office Visit, 0 to 17"]
      union ["Encounter, Performed": "Preventive Care, Established Office Visit, 0 to 17"]
      union ["Encounter, Performed": "Psych Visit - Diagnostic Evaluation"]
      union ["Encounter, Performed": "Psych Visit - Psychotherapy"]
  )

define "Initial Population 2":
  exists ["Patient Characteristic Birthdate": "Birth date"] BirthDate
    where Global."CalendarAgeInYearsAt" ( BirthDate.birthDatetime, start of "Measurement Period" ) in Interval[6, 12 )
      and exists ( "Qualifying Encounter" )
      and "First ADHD Medication Dispensed" is not null
      and "Has ADHD Cumulative Medication Duration Greater Than or Equal to 210 Days"

define "Encounters 300 Days after Initial ADHD Medication":
  "Qualifying Numerator Encounter" ValidNumeratorEncounter
    with "First ADHD Medication Dispensed" InitialADHDMedication
      such that start of ValidNumeratorEncounter.relevantPeriod in day of Interval[InitialADHDMedication.relevantDatetime + 31 days, InitialADHDMedication.relevantDatetime + 300 days]

define function "Cumulative Medication Duration"(Medication List<"Medication, Active">):
  Sum((collapse(Medication.relevantPeriod))MedicationPeriod
      return all duration in days of MedicationPeriod
  )