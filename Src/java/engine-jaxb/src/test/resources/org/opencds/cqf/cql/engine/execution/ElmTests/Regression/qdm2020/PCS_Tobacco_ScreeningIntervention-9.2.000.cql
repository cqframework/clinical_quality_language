library PCS_Tobacco_ScreeningIntervention version '9.2.000'

using QDM version '5.5'

include MATGlobalCommonFunctions version '5.0.000' called Global

codesystem "LOINC": 'urn:oid:2.16.840.1.113883.6.1'
codesystem "CPT": 'urn:oid:2.16.840.1.113883.6.12'

valueset "Annual Wellness Visit": 'urn:oid:2.16.840.1.113883.3.526.3.1240'
valueset "Ethnicity": 'urn:oid:2.16.840.1.114222.4.11.837'
valueset "Home Healthcare Services": 'urn:oid:2.16.840.1.113883.3.464.1003.101.12.1016'
valueset "Limited Life Expectancy": 'urn:oid:2.16.840.1.113883.3.526.3.1259'
valueset "Medical Reason": 'urn:oid:2.16.840.1.113883.3.526.3.1007'
valueset "Occupational Therapy Evaluation": 'urn:oid:2.16.840.1.113883.3.526.3.1011'
valueset "Office Visit": 'urn:oid:2.16.840.1.113883.3.464.1003.101.12.1001'
valueset "ONC Administrative Sex": 'urn:oid:2.16.840.1.113762.1.4.1'
valueset "Ophthalmological Services": 'urn:oid:2.16.840.1.113883.3.526.3.1285'
valueset "Payer": 'urn:oid:2.16.840.1.114222.4.11.3591'
valueset "Physical Therapy Evaluation": 'urn:oid:2.16.840.1.113883.3.526.3.1022'
valueset "Preventive Care Services - Established Office Visit, 18 and Up": 'urn:oid:2.16.840.1.113883.3.464.1003.101.12.1025'
valueset "Preventive Care Services - Group Counseling": 'urn:oid:2.16.840.1.113883.3.464.1003.101.12.1027'
valueset "Preventive Care Services - Other": 'urn:oid:2.16.840.1.113883.3.464.1003.101.12.1030'
valueset "Preventive Care Services-Individual Counseling": 'urn:oid:2.16.840.1.113883.3.464.1003.101.12.1026'
valueset "Preventive Care Services-Initial Office Visit, 18 and Up": 'urn:oid:2.16.840.1.113883.3.464.1003.101.12.1023'
valueset "Psych Visit - Diagnostic Evaluation": 'urn:oid:2.16.840.1.113883.3.526.3.1492'
valueset "Psych Visit - Psychotherapy": 'urn:oid:2.16.840.1.113883.3.526.3.1496'
valueset "Psychoanalysis": 'urn:oid:2.16.840.1.113883.3.526.3.1141'
valueset "Race": 'urn:oid:2.16.840.1.114222.4.11.836'
valueset "Speech and Hearing Evaluation": 'urn:oid:2.16.840.1.113883.3.526.3.1530'
valueset "Tobacco Non-User": 'urn:oid:2.16.840.1.113883.3.526.3.1189'
valueset "Tobacco Use Cessation Counseling": 'urn:oid:2.16.840.1.113883.3.526.3.509'
valueset "Tobacco Use Cessation Pharmacotherapy": 'urn:oid:2.16.840.1.113883.3.526.3.1190'
valueset "Tobacco Use Screening": 'urn:oid:2.16.840.1.113883.3.526.3.1278'
valueset "Tobacco User": 'urn:oid:2.16.840.1.113883.3.526.3.1170'

code "Birth date": '21112-8' from "LOINC" display 'Birth date'
code "Health behavior assessment, or re-assessment (ie, health-focused clinical interview, behavioral observations, clinical decision making)": '96156' from "CPT" display 'Health behavior assessment, or re-assessment (ie, health-focused clinical interview, behavioral observations, clinical decision making)'
code "Health behavior intervention, individual, face-to-face; initial 30 minutes": '96158' from "CPT" display 'Health behavior intervention, individual, face-to-face; initial 30 minutes'

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

define "Preventive Visit During Measurement Period":
  ( ["Encounter, Performed": "Annual Wellness Visit"]
    union ["Encounter, Performed": "Preventive Care Services - Established Office Visit, 18 and Up"]
    union ["Encounter, Performed": "Preventive Care Services - Group Counseling"]
    union ["Encounter, Performed": "Preventive Care Services - Other"]
    union ["Encounter, Performed": "Preventive Care Services-Individual Counseling"]
    union ["Encounter, Performed": "Preventive Care Services-Initial Office Visit, 18 and Up"] ) PreventiveEncounter
    where PreventiveEncounter.relevantPeriod during "Measurement Period"

define "Denominator 1":
  "Initial Population"

define "Denominator 3":
  "Initial Population"

define "Initial Population":
  exists ( ["Patient Characteristic Birthdate": "Birth date"] BirthDate
      where Global."CalendarAgeInYearsAt" ( BirthDate.birthDatetime, start of "Measurement Period" ) >= 18
  )
    and ( Count("Office Based Visit During Measurement Period")>= 2
        or exists "Preventive Visit During Measurement Period"
    )

define "Medical Reason for Not Screening for Tobacco Use":
  ["Assessment, Not Performed": "Tobacco Use Screening"] NoTobaccoUseScreening
    where NoTobaccoUseScreening.authorDatetime during "Measurement Period"
      and NoTobaccoUseScreening.negationRationale in "Medical Reason"

define "Medical Reason for Not Ordering Tobacco Cessation Pharmacotherapy":
  ["Medication, Not Ordered": "Tobacco Use Cessation Pharmacotherapy"] NoCessationPharmacotherapyOrdered
    where NoCessationPharmacotherapyOrdered.authorDatetime during "Measurement Period"
      and NoCessationPharmacotherapyOrdered.negationRationale in "Medical Reason"

define "Medical Reason for Not Counseling Tobacco User":
  ["Intervention, Not Performed": "Tobacco Use Cessation Counseling"] NoTobaccoCessationCounseling
    where NoTobaccoCessationCounseling.authorDatetime during "Measurement Period"
      and NoTobaccoCessationCounseling.negationRationale in "Medical Reason"

define "Tobacco Cessation Pharmacotherapy Ordered":
  ["Medication, Order": "Tobacco Use Cessation Pharmacotherapy"] CessationPharmacotherapyOrdered
    where CessationPharmacotherapyOrdered.authorDatetime during "Measurement Period"

define "Active Pharmacotherapy for Tobacco Cessation":
  ["Medication, Active": "Tobacco Use Cessation Pharmacotherapy"] TakingCessationPharmacotherapy
    where TakingCessationPharmacotherapy.relevantPeriod overlaps after "Measurement Period"

define "Tobacco Cessation Counseling Given":
  ["Intervention, Performed": "Tobacco Use Cessation Counseling"] TobaccoCessationCounseling
    where TobaccoCessationCounseling.relevantDatetime during "Measurement Period"

define "Numerator 2":
  exists "Tobacco Cessation Counseling Given"
    or exists "Tobacco Cessation Pharmacotherapy Ordered"
    or exists "Active Pharmacotherapy for Tobacco Cessation"

define "Has Limited Life Expectancy":
  exists ( ["Diagnosis": "Limited Life Expectancy"] LimitedLifeExpectancy
      where LimitedLifeExpectancy.prevalencePeriod overlaps after "Measurement Period"
  )

define "Denominator Exceptions 1":
  exists "Medical Reason for Not Screening for Tobacco Use"
    or "Has Limited Life Expectancy"

define "Denominator Exceptions 2":
  exists "Medical Reason for Not Counseling Tobacco User"
    or exists "Medical Reason for Not Ordering Tobacco Cessation Pharmacotherapy"
    or "Has Limited Life Expectancy"

define "Denominator Exceptions 3":
  exists "Medical Reason for Not Screening for Tobacco Use"
    or exists "Medical Reason for Not Counseling Tobacco User"
    or exists "Medical Reason for Not Ordering Tobacco Cessation Pharmacotherapy"
    or "Has Limited Life Expectancy"

define "Office Based Visit During Measurement Period":
  ( ["Encounter, Performed": "Health behavior intervention, individual, face-to-face; initial 30 minutes"]
    union ["Encounter, Performed": "Health behavior assessment, or re-assessment (ie, health-focused clinical interview, behavioral observations, clinical decision making)"]
    union ["Encounter, Performed": "Home Healthcare Services"]
    union ["Encounter, Performed": "Occupational Therapy Evaluation"]
    union ["Encounter, Performed": "Office Visit"]
    union ["Encounter, Performed": "Ophthalmological Services"]
    union ["Encounter, Performed": "Physical Therapy Evaluation"]
    union ["Encounter, Performed": "Psych Visit - Diagnostic Evaluation"]
    union ["Encounter, Performed": "Psych Visit - Psychotherapy"]
    union ["Encounter, Performed": "Psychoanalysis"]
    union ["Encounter, Performed": "Speech and Hearing Evaluation"] ) OfficeBasedEncounter
    where OfficeBasedEncounter.relevantPeriod during "Measurement Period"

define "Most Recent Tobacco Use Screening Indicates Tobacco Non User":
  ( Last(["Assessment, Performed": "Tobacco Use Screening"] TobaccoUseScreening
      where TobaccoUseScreening.relevantDatetime during "Measurement Period"
      sort by relevantDatetime
  )) MostRecentTobaccoUseScreening
    where MostRecentTobaccoUseScreening.result in "Tobacco Non-User"

define "Most Recent Tobacco Use Screening Indicates Tobacco User":
  ( Last(["Assessment, Performed": "Tobacco Use Screening"] TobaccoUseScreening
      where TobaccoUseScreening.relevantDatetime during "Measurement Period"
      sort by relevantDatetime
  )) MostRecentTobaccoUseScreening
    where MostRecentTobaccoUseScreening.result in "Tobacco User"

define "Denominator 2":
  "Initial Population"
    and "Most Recent Tobacco Use Screening Indicates Tobacco User" is not null

define "Numerator 1":
  "Most Recent Tobacco Use Screening Indicates Tobacco Non User" is not null
    or "Most Recent Tobacco Use Screening Indicates Tobacco User" is not null

define "Numerator 3":
  "Most Recent Tobacco Use Screening Indicates Tobacco Non User" is not null
    or ( "Most Recent Tobacco Use Screening Indicates Tobacco User" is not null
        and ( exists "Tobacco Cessation Counseling Given"
            or exists "Tobacco Cessation Pharmacotherapy Ordered"
            or exists "Active Pharmacotherapy for Tobacco Cessation"
        )
    )