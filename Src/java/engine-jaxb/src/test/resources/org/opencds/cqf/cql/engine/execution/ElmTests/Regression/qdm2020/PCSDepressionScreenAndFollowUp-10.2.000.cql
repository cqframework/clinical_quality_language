library PCSDepressionScreenAndFollowUp version '10.2.000'

using QDM version '5.5'

include MATGlobalCommonFunctions version '5.0.000' called Global

codesystem "LOINC": 'urn:oid:2.16.840.1.113883.6.1'

valueset "Adolescent Depression Medications": 'urn:oid:2.16.840.1.113883.3.526.3.1567'
valueset "Adult Depression Medications": 'urn:oid:2.16.840.1.113883.3.526.3.1566'
valueset "Bipolar Diagnosis": 'urn:oid:2.16.840.1.113883.3.600.450'
valueset "Depression Diagnosis": 'urn:oid:2.16.840.1.113883.3.600.145'
valueset "Encounter to Screen for Depression": 'urn:oid:2.16.840.1.113883.3.600.1916'
valueset "Ethnicity": 'urn:oid:2.16.840.1.114222.4.11.837'
valueset "Follow Up for Adolescent Depression": 'urn:oid:2.16.840.1.113883.3.526.3.1569'
valueset "Follow Up for Adult Depression": 'urn:oid:2.16.840.1.113883.3.526.3.1568'
valueset "Medical Reason": 'urn:oid:2.16.840.1.113883.3.526.3.1007'
valueset "Negative Depression Screening": 'urn:oid:2.16.840.1.113883.3.526.3.1564'
valueset "ONC Administrative Sex": 'urn:oid:2.16.840.1.113762.1.4.1'
valueset "Patient Declined": 'urn:oid:2.16.840.1.113883.3.526.3.1582'
valueset "Payer": 'urn:oid:2.16.840.1.114222.4.11.3591'
valueset "Physical Therapy Evaluation": 'urn:oid:2.16.840.1.113883.3.526.3.1022'
valueset "Positive Depression Screening": 'urn:oid:2.16.840.1.113883.3.526.3.1565'
valueset "Race": 'urn:oid:2.16.840.1.114222.4.11.836'
valueset "Referral for Adolescent Depression": 'urn:oid:2.16.840.1.113883.3.526.3.1570'
valueset "Referral for Adult Depression": 'urn:oid:2.16.840.1.113883.3.526.3.1571'

code "Adolescent depression screening assessment": '73831-0' from "LOINC" display 'Adolescent depression screening assessment'
code "Adult depression screening assessment": '73832-8' from "LOINC" display 'Adult depression screening assessment'
code "Birth date": '21112-8' from "LOINC" display 'Birth date'

parameter "Measurement Period" Interval<DateTime>

context Patient

define "Denominator":
  "Initial Population"

define "Denominator Exceptions":
  ( exists "Medical or Patient Reason for Not Screening Adolescent for Depression"
      and not "Has Adolescent Depression Screening"
  )
    or ( exists "Medical or Patient Reason for Not Screening Adult for Depression"
        and not "Has Adult Depression Screening"
    )

define "Follow Up For Positive Adolescent Depression Screening":
  ["Medication, Order": "Adolescent Depression Medications"]
    union ["Intervention, Order": "Referral for Adolescent Depression"]
    union ["Intervention, Performed": "Follow Up for Adolescent Depression"]

define "Follow Up for Positive Adult Depression Screening":
  ["Medication, Order": "Adult Depression Medications"]
    union ["Intervention, Order": "Referral for Adult Depression"]
    union ["Intervention, Performed": "Follow Up for Adult Depression"]

define "Has Most Recent Adolescent Screening Negative":
  ( "Most Recent Adolescent Depression Screening" AdolescentScreen
      where AdolescentScreen.result in "Negative Depression Screening"
  ) is not null

define "Has Most Recent Adult Screening Negative":
  ( "Most Recent Adult Depression Screening" AdultScreen
      where AdultScreen.result in "Negative Depression Screening"
  ) is not null

define "Initial Population":
  "Patient Age 12 Years or Older at Start of Measurement Period"
    and exists ( "Qualifying Encounter During Measurement Period" )

define "Medical or Patient Reason for Not Screening Adolescent for Depression":
  ["Assessment, Not Performed": "Adolescent depression screening assessment"] NoAdolescentScreen
    with "Qualifying Encounter During Measurement Period" QualifyingEncounter
      such that NoAdolescentScreen.authorDatetime during QualifyingEncounter.relevantPeriod
    where ( NoAdolescentScreen.negationRationale in "Patient Declined"
        or NoAdolescentScreen.negationRationale in "Medical Reason"
    )

define "Medical or Patient Reason for Not Screening Adult for Depression":
  ["Assessment, Not Performed": "Adult depression screening assessment"] NoAdultScreen
    with "Qualifying Encounter During Measurement Period" QualifyingEncounter
      such that NoAdultScreen.authorDatetime during QualifyingEncounter.relevantPeriod
    where ( NoAdultScreen.negationRationale in "Patient Declined"
        or NoAdultScreen.negationRationale in "Medical Reason"
    )

define "Most Recent Adolescent Depression Screening":
  Last(["Assessment, Performed": "Adolescent depression screening assessment"] AdolescentDepressionScreening
      with "Qualifying Encounter During Measurement Period" QualifyingEncounter
        such that AdolescentDepressionScreening.relevantDatetime 14 days or less on or before day of start of QualifyingEncounter.relevantPeriod
          and AdolescentDepressionScreening.result is not null
      sort by relevantDatetime
  )

define "Most Recent Adolescent Screening Positive and Follow Up Provided":
  from
    "Most Recent Adolescent Depression Screening" LastAdolescentScreen,
    "Follow Up For Positive Adolescent Depression Screening" FollowUpPositiveAdolescentScreen,
    "Qualifying Encounter During Measurement Period" QualifyingEncounter
    where LastAdolescentScreen.relevantDatetime 14 days or less on or before day of start of QualifyingEncounter.relevantPeriod
      and LastAdolescentScreen.result in "Positive Depression Screening"
      and ( FollowUpPositiveAdolescentScreen.authorDatetime same day as 
        end of QualifyingEncounter.relevantPeriod
          or FollowUpPositiveAdolescentScreen.relevantDatetime same day as 
          end of QualifyingEncounter.relevantPeriod
      )

define "Most Recent Adult Depression Screening":
  Last(["Assessment, Performed": "Adult depression screening assessment"] AdultDepressionScreening
      with "Qualifying Encounter During Measurement Period" QualifyingEncounter
        such that AdultDepressionScreening.relevantDatetime 14 days or less on or before day of start of QualifyingEncounter.relevantPeriod
          and AdultDepressionScreening.result is not null
      sort by relevantDatetime
  )

define "Most Recent Adult Depression Screening Positive and Follow Up Provided":
  from
    "Most Recent Adult Depression Screening" LastAdultScreen,
    "Follow Up for Positive Adult Depression Screening" FollowUpPositiveAdultScreen,
    "Qualifying Encounter During Measurement Period" QualifyingEncounter
    where LastAdultScreen.relevantDatetime 14 days or less on or before day of start of QualifyingEncounter.relevantPeriod
      and LastAdultScreen.result in "Positive Depression Screening"
      and ( FollowUpPositiveAdultScreen.authorDatetime same day as 
        end of QualifyingEncounter.relevantPeriod
          or FollowUpPositiveAdultScreen.relevantDatetime same day as 
          end of QualifyingEncounter.relevantPeriod
      )

define "Numerator":
  ( "Patient Age 12 to 16 Years at Start of Measurement Period"
      and ( "Has Most Recent Adolescent Screening Negative"
          or exists "Most Recent Adolescent Screening Positive and Follow Up Provided"
      )
  )
    or ( "Patient Age 17 Years at Start of Measurement Period"
        and ( "Has Most Recent Adolescent Screening Negative"
            or exists "Most Recent Adolescent Screening Positive and Follow Up Provided"
            or "Has Most Recent Adult Screening Negative"
            or exists "Most Recent Adult Depression Screening Positive and Follow Up Provided"
        )
    )
    or ( "Patient Age 18 Years or Older at Start of Measurement Period"
        and ( "Has Most Recent Adult Screening Negative"
            or exists "Most Recent Adult Depression Screening Positive and Follow Up Provided"
        )
    )

define "Patient Age 12 to 16 Years at Start of Measurement Period":
  exists ["Patient Characteristic Birthdate": "Birth date"] BirthDate
    where Global."CalendarAgeInYearsAt" ( BirthDate.birthDatetime, start of "Measurement Period" ) < 17

define "Patient Age 12 Years or Older at Start of Measurement Period":
  exists ["Patient Characteristic Birthdate": "Birth date"] BirthDate
    where Global."CalendarAgeInYearsAt" ( BirthDate.birthDatetime, start of "Measurement Period" ) >= 12

define "Patient Age 17 Years at Start of Measurement Period":
  exists ["Patient Characteristic Birthdate": "Birth date"] BirthDate
    where Global."CalendarAgeInYearsAt" ( BirthDate.birthDatetime, start of "Measurement Period" ) = 17

define "Patient Age 18 Years or Older at Start of Measurement Period":
  exists ["Patient Characteristic Birthdate": "Birth date"] BirthDate
    where Global."CalendarAgeInYearsAt" ( BirthDate.birthDatetime, start of "Measurement Period" ) >= 18

define "Qualifying Encounter During Measurement Period":
  ( ["Encounter, Performed": "Encounter to Screen for Depression"]
    union ["Encounter, Performed": "Physical Therapy Evaluation"] ) ValidEncounter
    where ValidEncounter.relevantPeriod during "Measurement Period"

define "SDE Ethnicity":
  ["Patient Characteristic Ethnicity": "Ethnicity"]

define "SDE Payer":
  ["Patient Characteristic Payer": "Payer"]

define "SDE Race":
  ["Patient Characteristic Race": "Race"]

define "SDE Sex":
  ["Patient Characteristic Sex": "ONC Administrative Sex"]

define "History of Bipolar or Depression Diagnosis Before Qualifying Encounter":
  ( ["Diagnosis": "Bipolar Diagnosis"]
    union ["Diagnosis": "Depression Diagnosis"] ) DiagnosisBipolarorDepression
    with "Qualifying Encounter During Measurement Period" QualifyingEncounter
      such that DiagnosisBipolarorDepression.prevalencePeriod starts before QualifyingEncounter.relevantPeriod

define "Denominator Exclusions":
  exists "History of Bipolar or Depression Diagnosis Before Qualifying Encounter"

define "Has Adult Depression Screening":
  exists ( ["Assessment, Performed": "Adult depression screening assessment"] AdultScreening
      with "Qualifying Encounter During Measurement Period" QualifyingEncounter
        such that AdultScreening.relevantDatetime 14 days or less on or before day of start of QualifyingEncounter.relevantPeriod
          and AdultScreening.result is not null
  )

define "Has Adolescent Depression Screening":
  exists ( ["Assessment, Performed": "Adolescent depression screening assessment"] AdolescentScreening
      with "Qualifying Encounter During Measurement Period" QualifyingEncounter
        such that AdolescentScreening.relevantDatetime 14 days or less on or before day of start of QualifyingEncounter.relevantPeriod
          and AdolescentScreening.result is not null
  )