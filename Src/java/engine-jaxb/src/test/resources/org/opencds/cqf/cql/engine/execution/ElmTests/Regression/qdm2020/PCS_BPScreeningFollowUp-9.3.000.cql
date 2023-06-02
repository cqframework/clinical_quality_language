library PCS_BPScreeningFollowUp version '9.3.000'

using QDM version '5.5'

include MATGlobalCommonFunctions version '5.0.000' called Global

codesystem "LOINC": 'urn:oid:2.16.840.1.113883.6.1'

valueset "Diagnosis of Hypertension": 'urn:oid:2.16.840.1.113883.3.600.263'
valueset "Dietary Recommendations": 'urn:oid:2.16.840.1.113883.3.600.1515'
valueset "Encounter to Screen for Blood Pressure": 'urn:oid:2.16.840.1.113883.3.600.1920'
valueset "Ethnicity": 'urn:oid:2.16.840.1.114222.4.11.837'
valueset "Finding of Elevated Blood Pressure or Hypertension": 'urn:oid:2.16.840.1.113762.1.4.1047.514'
valueset "Follow Up Within 4 Weeks": 'urn:oid:2.16.840.1.113883.3.526.3.1578'
valueset "Follow Up Within One Year": 'urn:oid:2.16.840.1.113883.3.526.3.1579'
valueset "Laboratory Tests for Hypertension": 'urn:oid:2.16.840.1.113883.3.600.1482'
valueset "Lifestyle Recommendation": 'urn:oid:2.16.840.1.113883.3.526.3.1581'
valueset "Medical Reason": 'urn:oid:2.16.840.1.113883.3.526.3.1007'
valueset "ONC Administrative Sex": 'urn:oid:2.16.840.1.113762.1.4.1'
valueset "Patient Declined": 'urn:oid:2.16.840.1.113883.3.526.3.1582'
valueset "Payer": 'urn:oid:2.16.840.1.114222.4.11.3591'
valueset "Pharmacologic Therapy for Hypertension": 'urn:oid:2.16.840.1.113883.3.526.1577'
valueset "Race": 'urn:oid:2.16.840.1.114222.4.11.836'
valueset "Recommendation to Increase Physical Activity": 'urn:oid:2.16.840.1.113883.3.600.1518'
valueset "Referral or Counseling for Alcohol Consumption": 'urn:oid:2.16.840.1.113883.3.526.3.1583'
valueset "Referral to Primary Care or Alternate Provider": 'urn:oid:2.16.840.1.113883.3.526.3.1580'
valueset "Weight Reduction Recommended": 'urn:oid:2.16.840.1.113883.3.600.1510'

code "Birth date": '21112-8' from "LOINC" display 'Birth date'
code "Diastolic blood pressure": '8462-4' from "LOINC" display 'Diastolic blood pressure'
code "EKG 12 channel panel": '34534-8' from "LOINC" display 'EKG 12 channel panel'
code "EKG study": '11524-6' from "LOINC" display 'EKG study'
code "Systolic blood pressure": '8480-6' from "LOINC" display 'Systolic blood pressure'

parameter "Measurement Period" Interval<DateTime>

context Patient

define "Encounter with Second Hypertensive Reading":
  "Encounter with High Blood Pressure Reading"
    intersect "Encounter with High Blood Pressure Reading Within Year Prior"

define "Numerator":
  "Encounter with Normal Blood Pressure Reading"
    union ( "Encounter with Prehypertensive Blood Pressure Reading"
        intersect ( ( "Encounter with Order for Intervention for Hypertension"
              intersect "Encounter with Order for Follow Up Within One Year"
          )
            union "Encounter with Referral to Alternate Provider for High Blood Pressure Reading"
        )
    )
    union ( "Encounter with First Hypertensive Reading"
        intersect ( ( "Encounter with Order for Intervention for Hypertension"
              intersect "Encounter with Order for Follow Up Within 4 Weeks"
          )
            union "Encounter with Referral to Alternate Provider for High Blood Pressure Reading"
        )
    )
    union ( "Encounter with Second Hypertensive Reading"
        intersect ( ( "Encounter with Order for Intervention for Hypertension"
              intersect ( "Encounter with Order for Laboratory Test for Hypertension"
                  union "Encounter with Order for Electrocardiogram for Hypertension"
                  union "Encounter with Order for Hypertension Medication"
              )
          )
            union "Encounter with Referral to Alternate Provider for High Blood Pressure Reading"
        )
    )

define "SDE Ethnicity":
  ["Patient Characteristic Ethnicity": "Ethnicity"]

define "SDE Payer":
  ["Patient Characteristic Payer": "Payer"]

define "SDE Race":
  ["Patient Characteristic Race": "Race"]

define "SDE Sex":
  ["Patient Characteristic Sex": "ONC Administrative Sex"]

define "Encounter with First Hypertensive Reading":
  "Encounter with High Blood Pressure Reading"
    except "Encounter with High Blood Pressure Reading Within Year Prior"

define "Initial Population":
  "Qualifying Encounter During Measurement Period" QualifyingEncounter
    with ["Patient Characteristic Birthdate": "Birth date"] BirthDate
      such that Global."CalendarAgeInYearsAt" ( BirthDate.birthDatetime, start of "Measurement Period" ) >= 18

define "Encounter with Order for Follow Up Within One Year":
  "Qualifying Encounter During Measurement Period" QualifyingEncounter
    with ["Intervention, Order": "Follow Up Within One Year"] FollowupOneYear
      such that FollowupOneYear.authorDatetime same day as start of QualifyingEncounter.relevantPeriod

define "Encounter with Order for Follow Up Within 4 Weeks":
  "Qualifying Encounter During Measurement Period" QualifyingEncounter
    with ["Intervention, Order": "Follow Up Within 4 Weeks"] Followup4Weeks
      such that Followup4Weeks.authorDatetime same day as start of QualifyingEncounter.relevantPeriod

define "Encounter with Order for Hypertension Medication":
  "Qualifying Encounter During Measurement Period" QualifyingEncounter
    with ["Medication, Order": "Pharmacologic Therapy for Hypertension"] AntihypertensiveMedication
      such that AntihypertensiveMedication.authorDatetime same day as start of QualifyingEncounter.relevantPeriod

define "Encounter with Order for Laboratory Test for Hypertension":
  "Qualifying Encounter During Measurement Period" QualifyingEncounter
    with ["Laboratory Test, Order": "Laboratory Tests for Hypertension"] HypertensionLabs
      such that HypertensionLabs.authorDatetime same day as start of QualifyingEncounter.relevantPeriod

define "Encounter with Referral to Alternate Provider for High Blood Pressure Reading":
  "Qualifying Encounter During Measurement Period" QualifyingEncounter
    with ["Intervention, Order": "Referral to Primary Care or Alternate Provider"] Referral
      such that Referral.authorDatetime same day as start of QualifyingEncounter.relevantPeriod
        and Referral.reason in "Finding of Elevated Blood Pressure or Hypertension"

define "Encounter with Order for Hypertension Follow Up Declined by Patient":
  "Qualifying Encounter During Measurement Period" QualifyingEncounter
    with ( ["Diagnostic Study, Not Ordered": "EKG 12 channel panel"]
      union ["Diagnostic Study, Not Ordered": "EKG study"]
      union ["Intervention, Not Ordered": "Follow Up Within One Year"]
      union ["Intervention, Not Ordered": "Follow Up Within 4 Weeks"]
      union ["Intervention, Not Ordered": "Referral to Primary Care or Alternate Provider"]
      union ["Intervention, Not Ordered": "Lifestyle Recommendation"]
      union ["Intervention, Not Ordered": "Weight Reduction Recommended"]
      union ["Intervention, Not Ordered": "Dietary Recommendations"]
      union ["Intervention, Not Ordered": "Recommendation to Increase Physical Activity"]
      union ["Intervention, Not Ordered": "Referral or Counseling for Alcohol Consumption"]
      union ["Laboratory Test, Not Ordered": "Laboratory Tests for Hypertension"]
      union ["Medication, Not Ordered": "Pharmacologic Therapy for Hypertension"] ) NoFollowUpOrdered
      such that NoFollowUpOrdered.negationRationale in "Patient Declined"
        and NoFollowUpOrdered.authorDatetime same day as start of QualifyingEncounter.relevantPeriod

define "Encounter with Medical Reason for Not Obtaining or Patient Declined Blood Pressure Measurement":
  "Qualifying Encounter During Measurement Period" QualifyingEncounter
    with ( ["Physical Exam, Not Performed": "Systolic blood pressure"]
      union ["Physical Exam, Not Performed": "Diastolic blood pressure"] ) NoBPScreen
      such that ( NoBPScreen.negationRationale in "Medical Reason"
          or NoBPScreen.negationRationale in "Patient Declined"
      )
        and NoBPScreen.authorDatetime same day as start of QualifyingEncounter.relevantPeriod

define "Denominator Exceptions":
  "Encounter with Medical Reason for Not Obtaining or Patient Declined Blood Pressure Measurement"
    union "Encounter with Order for Hypertension Follow Up Declined by Patient"

define "Denominator Exclusions":
  "Qualifying Encounter During Measurement Period" QualifyingEncounter
    with ["Diagnosis": "Diagnosis of Hypertension"] Hypertension
      such that Hypertension.prevalencePeriod overlaps before QualifyingEncounter.relevantPeriod

define "Denominator":
  "Initial Population"

define "Encounter with Order for Electrocardiogram for Hypertension":
  "Qualifying Encounter During Measurement Period" QualifyingEncounter
    with ( ["Diagnostic Study, Order": "EKG 12 channel panel"]
      union ["Diagnostic Study, Order": "EKG study"] ) ECG
      such that ECG.authorDatetime same day as start of QualifyingEncounter.relevantPeriod

define "Encounter with Order for Intervention for Hypertension":
  "Qualifying Encounter During Measurement Period" QualifyingEncounter
    with ( ["Intervention, Order": "Lifestyle Recommendation"]
      union ["Intervention, Order": "Weight Reduction Recommended"]
      union ["Intervention, Order": "Dietary Recommendations"]
      union ["Intervention, Order": "Recommendation to Increase Physical Activity"]
      union ["Intervention, Order": "Referral or Counseling for Alcohol Consumption"] ) HighBloodPressureIntervention
      such that HighBloodPressureIntervention.authorDatetime same day as start of QualifyingEncounter.relevantPeriod

define "Encounter with High Blood Pressure Reading":
  "Qualifying Encounter During Measurement Period" QualifyingEncounter
    let EncounterLastSystolicBP: Last(["Physical Exam, Performed": "Systolic blood pressure"] SystolicBP
        where SystolicBP.relevantDatetime during QualifyingEncounter.relevantPeriod
        sort by relevantDatetime
    ),
    EncounterLastDiastolicBP: Last(["Physical Exam, Performed": "Diastolic blood pressure"] DiastolicBP
        where DiastolicBP.relevantDatetime during QualifyingEncounter.relevantPeriod
        sort by relevantDatetime
    )
    where EncounterLastSystolicBP.result > 0 'mm[Hg]'
      and EncounterLastDiastolicBP.result > 0 'mm[Hg]'
      and ( EncounterLastSystolicBP.result >= 140 'mm[Hg]'
          or EncounterLastDiastolicBP.result >= 90 'mm[Hg]'
      )

define "Encounter with High Blood Pressure Reading Within Year Prior":
  "Qualifying Encounter During Measurement Period" QualifyingEncounter
    let EncounterLastSystolicBP: Last(["Physical Exam, Performed": "Systolic blood pressure"] SystolicBP
        where SystolicBP.relevantDatetime 1 year or less before start of QualifyingEncounter.relevantPeriod
        sort by relevantDatetime
    ),
    EncounterLastDiastolicBP: Last(["Physical Exam, Performed": "Diastolic blood pressure"] DiastolicBP
        where DiastolicBP.relevantDatetime 1 year or less before start of QualifyingEncounter.relevantPeriod
        sort by relevantDatetime
    )
    where EncounterLastSystolicBP.result > 0 'mm[Hg]'
      and EncounterLastDiastolicBP.result > 0 'mm[Hg]'
      and ( EncounterLastSystolicBP.result >= 140 'mm[Hg]'
          or EncounterLastDiastolicBP.result >= 90 'mm[Hg]'
      )

define "Encounter with Normal Blood Pressure Reading":
  "Qualifying Encounter During Measurement Period" QualifyingEncounter
    let EncounterLastSystolicBP: Last(["Physical Exam, Performed": "Systolic blood pressure"] SystolicBP
        where SystolicBP.relevantDatetime during QualifyingEncounter.relevantPeriod
        sort by relevantDatetime
    ),
    EncounterLastDiastolicBP: Last(["Physical Exam, Performed": "Diastolic blood pressure"] DiastolicBP
        where DiastolicBP.relevantDatetime during QualifyingEncounter.relevantPeriod
        sort by relevantDatetime
    )
    where EncounterLastSystolicBP.result included in Interval[1 'mm[Hg]', 120 'mm[Hg]' )
      and EncounterLastDiastolicBP.result included in Interval[1 'mm[Hg]', 80 'mm[Hg]' )

define "Encounter with Prehypertensive Blood Pressure Reading":
  "Qualifying Encounter During Measurement Period" QualifyingEncounter
    let EncounterLastSystolicBP: Last(["Physical Exam, Performed": "Systolic blood pressure"] SystolicBP
        where SystolicBP.relevantDatetime during QualifyingEncounter.relevantPeriod
        sort by relevantDatetime
    ),
    EncounterLastDiastolicBP: Last(["Physical Exam, Performed": "Diastolic blood pressure"] DiastolicBP
        where DiastolicBP.relevantDatetime during QualifyingEncounter.relevantPeriod
        sort by relevantDatetime
    )
    where EncounterLastSystolicBP.result included in Interval[1 'mm[Hg]', 140 'mm[Hg]' )
      and EncounterLastDiastolicBP.result included in Interval[1 'mm[Hg]', 90 'mm[Hg]' )
      and ( EncounterLastSystolicBP.result >= 120 'mm[Hg]'
          or EncounterLastDiastolicBP.result >= 80 'mm[Hg]'
      )

define "Qualifying Encounter During Measurement Period":
  ["Encounter, Performed": "Encounter to Screen for Blood Pressure"] ValidEncounter
    where ValidEncounter.relevantPeriod during "Measurement Period"