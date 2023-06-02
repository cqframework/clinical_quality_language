library StatinTherapyforthePreventionandTreatmentofCardiovascularDisease version '4.3.000'

using QDM version '5.5'

include MATGlobalCommonFunctions version '5.0.000' called Global

codesystem "LOINC": 'urn:oid:2.16.840.1.113883.6.1'
codesystem "ICD10CM": 'urn:oid:2.16.840.1.113883.6.90'

valueset "Annual Wellness Visit": 'urn:oid:2.16.840.1.113883.3.526.3.1240'
valueset "Atherosclerosis and Peripheral Arterial Disease": 'urn:oid:2.16.840.1.113762.1.4.1047.21'
valueset "Breastfeeding": 'urn:oid:2.16.840.1.113762.1.4.1047.73'
valueset "CABG Surgeries": 'urn:oid:2.16.840.1.113883.3.666.5.694'
valueset "CABG, PCI Procedure": 'urn:oid:2.16.840.1.113762.1.4.1138.566'
valueset "Carotid Intervention": 'urn:oid:2.16.840.1.113883.3.117.1.7.1.204'
valueset "Cerebrovascular Disease, Stroke, TIA": 'urn:oid:2.16.840.1.113762.1.4.1047.44'
valueset "Diabetes": 'urn:oid:2.16.840.1.113883.3.464.1003.103.12.1001'
valueset "End Stage Renal Disease": 'urn:oid:2.16.840.1.113883.3.526.3.353'
valueset "Ethnicity": 'urn:oid:2.16.840.1.114222.4.11.837'
valueset "Hepatitis A": 'urn:oid:2.16.840.1.113883.3.464.1003.110.12.1024'
valueset "Hepatitis B": 'urn:oid:2.16.840.1.113883.3.67.1.101.1.269'
valueset "High Intensity Statin Therapy": 'urn:oid:2.16.840.1.113883.3.526.3.1572'
valueset "Hospice Care Ambulatory": 'urn:oid:2.16.840.1.113883.3.526.3.1584'
valueset "Hypercholesterolemia": 'urn:oid:2.16.840.1.113762.1.4.1047.100'
valueset "Ischemic Heart Disease or Other Related Diagnoses": 'urn:oid:2.16.840.1.113762.1.4.1047.46'
valueset "LDL Cholesterol": 'urn:oid:2.16.840.1.113883.3.526.3.1573'
valueset "Liver Disease": 'urn:oid:2.16.840.1.113762.1.4.1047.42'
valueset "Low Intensity Statin Therapy": 'urn:oid:2.16.840.1.113883.3.526.3.1574'
valueset "Moderate Intensity Statin Therapy": 'urn:oid:2.16.840.1.113883.3.526.3.1575'
valueset "Myocardial Infarction": 'urn:oid:2.16.840.1.113883.3.526.3.403'
valueset "Office Visit": 'urn:oid:2.16.840.1.113883.3.464.1003.101.12.1001'
valueset "ONC Administrative Sex": 'urn:oid:2.16.840.1.113762.1.4.1'
valueset "Outpatient Consultation": 'urn:oid:2.16.840.1.113883.3.464.1003.101.12.1008'
valueset "Outpatient Encounters for Preventive Care": 'urn:oid:2.16.840.1.113883.3.526.3.1576'
valueset "Palliative or Hospice Care": 'urn:oid:2.16.840.1.113883.3.600.1.1579'
valueset "Payer": 'urn:oid:2.16.840.1.114222.4.11.3591'
valueset "PCI": 'urn:oid:2.16.840.1.113762.1.4.1045.67'
valueset "Pregnancy or Other Related Diagnoses": 'urn:oid:2.16.840.1.113883.3.600.1.1623'
valueset "Preventive Care Services - Established Office Visit, 18 and Up": 'urn:oid:2.16.840.1.113883.3.464.1003.101.12.1025'
valueset "Preventive Care Services - Other": 'urn:oid:2.16.840.1.113883.3.464.1003.101.12.1030'
valueset "Preventive Care Services-Individual Counseling": 'urn:oid:2.16.840.1.113883.3.464.1003.101.12.1026'
valueset "Preventive Care Services-Initial Office Visit, 18 and Up": 'urn:oid:2.16.840.1.113883.3.464.1003.101.12.1023'
valueset "Race": 'urn:oid:2.16.840.1.114222.4.11.836'
valueset "Rhabdomyolysis": 'urn:oid:2.16.840.1.113762.1.4.1047.102'
valueset "Stable and Unstable Angina": 'urn:oid:2.16.840.1.113762.1.4.1047.47'
valueset "Statin Allergen": 'urn:oid:2.16.840.1.113762.1.4.1110.42'

code "Birth date": '21112-8' from "LOINC" display 'Birth date'
code "Encounter for palliative care": 'Z51.5' from "ICD10CM" display 'Encounter for palliative care'

parameter "Measurement Period" Interval<DateTime>

context Patient

define "Adverse Reaction to Statin":
  ["Adverse Event": "Statin Allergen"] StatinReaction
    where StatinReaction.relevantDatetime during "Measurement Period"

define "Allergy or Intolerance to Statin":
  ["Allergy/Intolerance": "Statin Allergen"] StatinAllergyIntolerance
    where StatinAllergyIntolerance.prevalencePeriod overlaps "Measurement Period"

define "ASCVD Diagnosis or Procedure Before End of Measurement Period":
  ( ( ["Diagnosis": "Myocardial Infarction"]
      union ["Diagnosis": "Cerebrovascular Disease, Stroke, TIA"]
      union ["Diagnosis": "Atherosclerosis and Peripheral Arterial Disease"]
      union ["Diagnosis": "Ischemic Heart Disease or Other Related Diagnoses"]
      union ["Diagnosis": "Stable and Unstable Angina"] ) ASCVDDiagnosis
      where ASCVDDiagnosis.prevalencePeriod starts before 
      end of "Measurement Period"
  )
    union ( ( ["Procedure, Performed": "PCI"]
        union ["Procedure, Performed": "CABG Surgeries"]
        union ["Procedure, Performed": "Carotid Intervention"]
        union ["Procedure, Performed": "CABG, PCI Procedure"] ) ASCVDProcedure
        where ASCVDProcedure.relevantPeriod starts before 
        end of "Measurement Period"
    )

define "Current or Prior LDL Result Greater Than or Equal To 190":
  ["Laboratory Test, Performed": "LDL Cholesterol"] LDL
    where LDL.result >= 190 'mg/dL'
      and LDL.relevantDatetime before 
      end of "Measurement Period"

define "Currently Taking Statin Therapy Through the End of the Measurement Period":
  ( ["Medication, Active": "Low Intensity Statin Therapy"]
    union ["Medication, Active": "Moderate Intensity Statin Therapy"]
    union ["Medication, Active": "High Intensity Statin Therapy"] ) ActiveStatin
    where ActiveStatin.relevantPeriod overlaps after "Measurement Period"

define "Denominator 1":
  "Initial Population"
    and exists "ASCVD Diagnosis or Procedure Before End of Measurement Period"

define "Denominator 2":
  "Initial Population"
    and ( exists "Current or Prior LDL Result Greater Than or Equal To 190"
        or "Has Hypercholesterolemia"
    )
    and not exists "ASCVD Diagnosis or Procedure Before End of Measurement Period"

define "Denominator 3":
  "Initial Population"
    and exists ( ["Patient Characteristic Birthdate": "Birth date"] BirthDate
        where Global."CalendarAgeInYearsAt" ( BirthDate.birthDatetime, start of "Measurement Period" ) >= 40
          and Global."CalendarAgeInYearsAt" ( BirthDate.birthDatetime, start of "Measurement Period" ) <= 75
    )
    and "Has Diabetes"
    and "Highest LDL Result Within 3 Years is Greater Than or Equal To 70 and Less Than 190" is not null
    and not exists "ASCVD Diagnosis or Procedure Before End of Measurement Period"
    and not exists "Current or Prior LDL Result Greater Than or Equal To 190"
    and not "Has Hypercholesterolemia"

define "Denominator Exceptions":
  "Patient Has Order for or Receiving Hospice Care or Palliative Care"
    or "Has ESRD"
    or "Has Hepatitis or Liver Disease"
    or exists "Adverse Reaction to Statin"
    or exists "Allergy or Intolerance to Statin"

define "Denominator Exceptions 3":
  "Patient Has Order for or Receiving Hospice Care or Palliative Care"
    or "Has ESRD"
    or "Has Hepatitis or Liver Disease"
    or exists "Adverse Reaction to Statin"
    or exists "Allergy or Intolerance to Statin"
    or "Most Recent LDL Result Within 3 Years is Less Than 70"

define "Has Diabetes":
  exists ( ["Diagnosis": "Diabetes"] Diabetes
      where Diabetes.prevalencePeriod overlaps "Measurement Period"
  )

define "Has ESRD":
  exists ( ["Diagnosis": "End Stage Renal Disease"] ESRD
      where ESRD.prevalencePeriod overlaps "Measurement Period"
  )

define "Has Hepatitis or Liver Disease":
  exists ( ( ["Diagnosis": "Hepatitis A"]
      union ["Diagnosis": "Hepatitis B"]
      union ["Diagnosis": "Liver Disease"] ) HepatitisLiverDisease
      where HepatitisLiverDisease.prevalencePeriod overlaps "Measurement Period"
  )

define "Has Hypercholesterolemia":
  exists ( ["Diagnosis": "Hypercholesterolemia"] Hypercholesterolemia
      where Hypercholesterolemia.prevalencePeriod starts before 
      end of "Measurement Period"
  )

define "Highest LDL Result Within 3 Years is Greater Than or Equal To 70 and Less Than 190":
  ( Max(["Laboratory Test, Performed": "LDL Cholesterol"] LDL
      where LDL.relevantDatetime 3 years or less before 
      end of "Measurement Period"
      return LDL.result as Quantity
  )) LDLResult
    where LDLResult in Interval[70 'mg/dL', 190 'mg/dL' )

define "Initial Population":
  exists ( ["Patient Characteristic Birthdate": "Birth date"] BirthDate
      where Global."CalendarAgeInYearsAt" ( BirthDate.birthDatetime, start of "Measurement Period" ) >= 21
  )
    and exists "Qualifying Encounter During Measurement Period"

define "Most Recent LDL Result Within 3 Years is Less Than 70":
  ( Last(["Laboratory Test, Performed": "LDL Cholesterol"] LDL
      where LDL.relevantDatetime 3 years or less before 
      end "Measurement Period"
      sort by relevantDatetime
  )) MostRecentLDL
    return MostRecentLDL.result < 70 'mg/dL'

define "Numerator":
  exists "Statin Therapy Ordered During Measurement Period"
    or exists "Currently Taking Statin Therapy Through the End of the Measurement Period"

define "Patient Has Order for or Receiving Hospice Care or Palliative Care":
  exists ( ( ["Intervention, Order": "Hospice Care Ambulatory"]
      union ["Intervention, Order": "Palliative or Hospice Care"] ) PalliativeOrHospiceCareOrder
      where PalliativeOrHospiceCareOrder.authorDatetime on or before 
      end of "Measurement Period"
  )
    or exists ( ( ["Intervention, Performed": "Hospice Care Ambulatory"]
        union ["Intervention, Performed": "Palliative or Hospice Care"]
        union ["Encounter, Performed": "Encounter for palliative care"] ) PalliativeOrHospiceCarePerformed
        where PalliativeOrHospiceCarePerformed.relevantPeriod starts on or before 
        end of "Measurement Period"
    )

define "Qualifying Encounter During Measurement Period":
  ( ["Encounter, Performed": "Annual Wellness Visit"]
    union ["Encounter, Performed": "Office Visit"]
    union ["Encounter, Performed": "Outpatient Consultation"]
    union ["Encounter, Performed": "Outpatient Encounters for Preventive Care"]
    union ["Encounter, Performed": "Preventive Care Services - Established Office Visit, 18 and Up"]
    union ["Encounter, Performed": "Preventive Care Services - Other"]
    union ["Encounter, Performed": "Preventive Care Services-Individual Counseling"]
    union ["Encounter, Performed": "Preventive Care Services-Initial Office Visit, 18 and Up"] ) ValidEncounter
    where ValidEncounter.relevantPeriod during "Measurement Period"

define "SDE Ethnicity":
  ["Patient Characteristic Ethnicity": "Ethnicity"]

define "SDE Payer":
  ["Patient Characteristic Payer": "Payer"]

define "SDE Race":
  ["Patient Characteristic Race": "Race"]

define "SDE Sex":
  ["Patient Characteristic Sex": "ONC Administrative Sex"]

define "Statin Therapy Ordered During Measurement Period":
  ( ["Medication, Order": "Low Intensity Statin Therapy"]
    union ["Medication, Order": "Moderate Intensity Statin Therapy"]
    union ["Medication, Order": "High Intensity Statin Therapy"] ) StatinOrdered
    where StatinOrdered.authorDatetime during "Measurement Period"

define "Denominator Exclusions":
  exists ( ( ["Diagnosis": "Rhabdomyolysis"]
      union ["Diagnosis": "Breastfeeding"]
      union ["Diagnosis": "Pregnancy or Other Related Diagnoses"] ) ExclusionDiagnosis
      where ExclusionDiagnosis.prevalencePeriod overlaps "Measurement Period"
  )