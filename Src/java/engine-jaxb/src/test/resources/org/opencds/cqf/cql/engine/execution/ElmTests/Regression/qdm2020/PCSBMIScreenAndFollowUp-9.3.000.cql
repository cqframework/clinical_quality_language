library PCSBMIScreenAndFollowUp version '9.3.000'

using QDM version '5.5'

include MATGlobalCommonFunctions version '5.0.000' called Global

codesystem "LOINC": 'urn:oid:2.16.840.1.113883.6.1'
codesystem "ICD10CM": 'urn:oid:2.16.840.1.113883.6.90'
codesystem "SNOMEDCT": 'urn:oid:2.16.840.1.113883.6.96'

valueset "Encounter to Evaluate BMI": 'urn:oid:2.16.840.1.113883.3.600.1.1751'
valueset "Ethnicity": 'urn:oid:2.16.840.1.114222.4.11.837'
valueset "Follow Up for Above Normal BMI": 'urn:oid:2.16.840.1.113883.3.600.1.1525'
valueset "Follow Up for Below Normal BMI": 'urn:oid:2.16.840.1.113883.3.600.1.1528'
valueset "Hospice Care Ambulatory": 'urn:oid:2.16.840.1.113883.3.526.3.1584'
valueset "Medical Reason": 'urn:oid:2.16.840.1.113883.3.526.3.1007'
valueset "Medications for Above Normal BMI": 'urn:oid:2.16.840.1.113883.3.526.3.1561'
valueset "Medications for Below Normal BMI": 'urn:oid:2.16.840.1.113883.3.526.3.1562'
valueset "ONC Administrative Sex": 'urn:oid:2.16.840.1.113762.1.4.1'
valueset "Overweight or Obese": 'urn:oid:2.16.840.1.113762.1.4.1047.502'
valueset "Palliative or Hospice Care": 'urn:oid:2.16.840.1.113883.3.600.1.1579'
valueset "Patient Declined": 'urn:oid:2.16.840.1.113883.3.526.3.1582'
valueset "Payer": 'urn:oid:2.16.840.1.114222.4.11.3591'
valueset "Pregnancy or Other Related Diagnoses": 'urn:oid:2.16.840.1.113883.3.600.1.1623'
valueset "Race": 'urn:oid:2.16.840.1.114222.4.11.836'
valueset "Referrals Where Weight Assessment May Occur": 'urn:oid:2.16.840.1.113883.3.600.1.1527'

code "Birth date": '21112-8' from "LOINC" display 'Birth date'
code "Body mass index (BMI) [Ratio]": '39156-5' from "LOINC" display 'Body mass index (BMI) [Ratio]'
code "Encounter for palliative care": 'Z51.5' from "ICD10CM" display 'Encounter for palliative care'
code "Underweight (finding)": '248342006' from "SNOMEDCT" display 'Underweight (finding)'

parameter "Measurement Period" Interval<DateTime>

context Patient

define "Denominator":
  "Initial Population"

define "Denominator Exceptions":
  exists "Medical Reason for Not Documenting a Follow Up Plan for Low or High BMI"
    or exists "Medical Reason or Patient Reason for Not Performing BMI Exam"

define "Denominator Exclusions":
  "Patient Has Order for or Receiving Hospice Care or Palliative Care"
    or exists "Pregnancy at Any Time During the Measurement Period"

define "High BMI and Follow Up Provided":
  "Most Recent Documented BMI" MostRecentBMI
    with ["Intervention, Performed": "Follow Up for Above Normal BMI"] AboveNormalFollowUp
      such that MostRecentBMI.relevantDatetime 12 months or less on or before day of start of AboveNormalFollowUp.relevantPeriod
        and AboveNormalFollowUp.reason in "Overweight or Obese"
    where MostRecentBMI.result >= 25 'kg/m2'

define "High BMI and Order for Follow Up":
  "Most Recent Documented BMI" MostRecentBMI
    with ( ( ( ["Intervention, Order": "Follow Up for Above Normal BMI"]
        union ["Intervention, Order": "Referrals Where Weight Assessment May Occur"] ) ReferralHighBMI
        where ReferralHighBMI.reason in "Overweight or Obese"
    )
      union ["Medication, Order": "Medications for Above Normal BMI"] ) HighBMIIntervention
      such that MostRecentBMI.relevantDatetime 12 months or less on or before day of HighBMIIntervention.authorDatetime
    where MostRecentBMI.result >= 25 'kg/m2'

define "Initial Population":
  exists ( ["Patient Characteristic Birthdate": "Birth date"] Birthdate
      with "Qualifying Encounter During Measurement Period" QualifyingEncounter
        such that Global."CalendarAgeInYearsAt" ( Birthdate.birthDatetime, start of QualifyingEncounter.relevantPeriod ) >= 18
  )

define "Medical Reason for Not Documenting a Follow Up Plan for Low or High BMI":
  ( ["Intervention, Not Ordered": "Referrals Where Weight Assessment May Occur"]
    union ["Intervention, Not Ordered": "Follow Up for Above Normal BMI"]
    union ["Intervention, Not Performed": "Follow Up for Above Normal BMI"]
    union ["Intervention, Not Ordered": "Follow Up for Below Normal BMI"]
    union ["Intervention, Not Performed": "Follow Up for Below Normal BMI"]
    union ["Medication, Not Ordered": "Medications for Above Normal BMI"]
    union ["Medication, Not Ordered": "Medications for Below Normal BMI"] ) NoBMIFollowUp
    with "Qualifying Encounter During Measurement Period" QualifyingEncounter
      such that NoBMIFollowUp.authorDatetime same day as start of QualifyingEncounter.relevantPeriod
    where NoBMIFollowUp.negationRationale in "Medical Reason"

define "Medical Reason or Patient Reason for Not Performing BMI Exam":
  ["Physical Exam, Not Performed": "Body mass index (BMI) [Ratio]"] NoBMI
    with "Qualifying Encounter During Measurement Period" QualifyingEncounter
      such that NoBMI.authorDatetime same day as start of QualifyingEncounter.relevantPeriod
    where ( NoBMI.negationRationale in "Medical Reason"
        or NoBMI.negationRationale in "Patient Declined"
    )

define "Most Recent Documented BMI":
  Last(["Physical Exam, Performed": "Body mass index (BMI) [Ratio]"] BMI
      with "Qualifying Encounter During Measurement Period" QualifyingEncounter
        such that BMI.relevantDatetime 12 months or less on or before day of 
        end of QualifyingEncounter.relevantPeriod
          and BMI.result >= 0 'kg/m2'
      sort by relevantDatetime
  )

define "Normal BMI":
  "Most Recent Documented BMI" BMIValue
    where BMIValue.result included in Interval[18.5 'kg/m2', 25 'kg/m2' )

define "Numerator":
  "Normal BMI" is not null
    or "High BMI and Order for Follow Up" is not null
    or "High BMI and Follow Up Provided" is not null
    or "Low BMI and Order for Follow Up" is not null
    or "Low BMI and Follow Up Provided" is not null

define "Patient Has Order for or Receiving Hospice Care or Palliative Care":
  exists ( ( ["Intervention, Order": "Hospice Care Ambulatory"]
      union ["Intervention, Order": "Palliative or Hospice Care"] ) PalliativeOrHospiceCareOrder
      with "Qualifying Encounter During Measurement Period" QualifyingEncounter
        such that PalliativeOrHospiceCareOrder.authorDatetime on or before 
        end of QualifyingEncounter.relevantPeriod
  )
    or exists ( ( ["Intervention, Performed": "Hospice Care Ambulatory"]
        union ["Intervention, Performed": "Palliative or Hospice Care"]
        union ["Encounter, Performed": "Encounter for palliative care"] ) PalliativeOrHospiceCarePerformed
        with "Qualifying Encounter During Measurement Period" QualifyingEncounter
          such that PalliativeOrHospiceCarePerformed.relevantPeriod starts on or before 
          end of QualifyingEncounter.relevantPeriod
    )

define "Pregnancy at Any Time During the Measurement Period":
  ["Diagnosis": "Pregnancy or Other Related Diagnoses"] Pregnancy
    where Pregnancy.prevalencePeriod overlaps "Measurement Period"

define "Qualifying Encounter During Measurement Period":
  ["Encounter, Performed": "Encounter to Evaluate BMI"] BMIEncounter
    where BMIEncounter.relevantPeriod during "Measurement Period"

define "SDE Ethnicity":
  ["Patient Characteristic Ethnicity": "Ethnicity"]

define "SDE Payer":
  ["Patient Characteristic Payer": "Payer"]

define "SDE Race":
  ["Patient Characteristic Race": "Race"]

define "SDE Sex":
  ["Patient Characteristic Sex": "ONC Administrative Sex"]

define "Low BMI and Order for Follow Up":
  "Most Recent Documented BMI" MostRecentBMI
    with ( ( ( ["Intervention, Order": "Follow Up for Below Normal BMI"]
        union ["Intervention, Order": "Referrals Where Weight Assessment May Occur"] ) ReferralLowBMI
        where ReferralLowBMI.reason ~ "Underweight (finding)"
    )
      union ["Medication, Order": "Medications for Below Normal BMI"] ) LowBMIIntervention
      such that MostRecentBMI.relevantDatetime 12 months or less on or before day of LowBMIIntervention.authorDatetime
    where MostRecentBMI.result < 18.5 'kg/m2'

define "Low BMI and Follow Up Provided":
  "Most Recent Documented BMI" MostRecentBMI
    with ["Intervention, Performed": "Follow Up for Below Normal BMI"] BelowNormalFollowUp
      such that MostRecentBMI.relevantDatetime 12 months or less on or before day of start of BelowNormalFollowUp.relevantPeriod
        and BelowNormalFollowUp.reason ~ "Underweight (finding)"
    where MostRecentBMI.result < 18.5 'kg/m2'