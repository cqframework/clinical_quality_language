library DepressionRemissionatTwelveMonths version '9.4.000'

using QDM version '5.5'

include MATGlobalCommonFunctions version '5.0.000' called Global

codesystem "LOINC": 'urn:oid:2.16.840.1.113883.6.1'
codesystem "SNOMEDCT": 'urn:oid:2.16.840.1.113883.6.96'

valueset "Bipolar Disorder": 'urn:oid:2.16.840.1.113883.3.67.1.101.1.128'
valueset "Care Services in Long-Term Residential Facility": 'urn:oid:2.16.840.1.113883.3.464.1003.101.12.1014'
valueset "Contact or Office Visit": 'urn:oid:2.16.840.1.113762.1.4.1080.5'
valueset "Dysthymia": 'urn:oid:2.16.840.1.113883.3.67.1.101.1.254'
valueset "Ethnicity": 'urn:oid:2.16.840.1.114222.4.11.837'
valueset "Major Depression Including Remission": 'urn:oid:2.16.840.113883.3.67.1.101.3.2444'
valueset "ONC Administrative Sex": 'urn:oid:2.16.840.1.113762.1.4.1'
valueset "Palliative Care Encounter": 'urn:oid:2.16.840.1.113883.3.600.1.1575'
valueset "Palliative or Hospice Care": 'urn:oid:2.16.840.1.113883.3.600.1.1579'
valueset "Payer": 'urn:oid:2.16.840.1.114222.4.11.3591'
valueset "Personality Disorder Emotionally Labile": 'urn:oid:2.16.840.1.113883.3.67.1.101.1.246'
valueset "Pervasive Developmental Disorder": 'urn:oid:2.16.840.1.113883.3.464.1003.105.12.1152'
valueset "PHQ 9 and PHQ 9M Tools": 'urn:oid:2.16.840.1.113883.3.67.1.101.1.263'
valueset "Race": 'urn:oid:2.16.840.1.114222.4.11.836'
valueset "Schizophrenia or Psychotic Disorder": 'urn:oid:2.16.840.1.113883.3.464.1003.105.12.1104'

code "Birth date": '21112-8' from "LOINC" display 'Birth date'
code "Dead (finding)": '419099009' from "SNOMEDCT" display 'Dead (finding)'

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

define "Initial Population":
  exists ( ["Patient Characteristic Birthdate": "Birth date"] BirthDate
      with "Index Depression Assessment" IndexAssessment
        such that Global."CalendarAgeInYearsAt" ( BirthDate.birthDatetime, IndexAssessment.relevantDatetime ) >= 12
          and "Index Depression Assessment" is not null
  )

define "Denominator Exclusions":
  "Has An Order For Palliative or Hospice Care"
    or "Has Palliative Care Encounter"
    or "Has Long Term Care Encounter"
    or "Patient Expired"
    or "Has Disorder Diagnoses"

define "Stratification 1":
  exists ( ["Patient Characteristic Birthdate": "Birth date"] BirthDate
      with "Index Depression Assessment" IndexAssessment
        such that Global."CalendarAgeInYearsAt" ( BirthDate.birthDatetime, IndexAssessment.relevantDatetime ) >= 12
          and Global."CalendarAgeInYearsAt" ( BirthDate.birthDatetime, IndexAssessment.relevantDatetime ) < 18
  )

define "Stratification 2":
  exists ( ["Patient Characteristic Birthdate": "Birth date"] BirthDate
      with "Index Depression Assessment" IndexAssessment
        such that Global."CalendarAgeInYearsAt" ( BirthDate.birthDatetime, IndexAssessment.relevantDatetime ) >= 18
  )

define "Denominator Identification Period":
  Interval[start of "Measurement Period" - 14 months, start of "Measurement Period" - 2 months )

define "Depression Assessments Greater than 9":
  ["Assessment, Performed": "PHQ 9 and PHQ 9M Tools"] DepressionAssessment
    where DepressionAssessment.result > 9

define "Depression Diagnoses":
  ["Diagnosis": "Major Depression Including Remission"]
    union ["Diagnosis": "Dysthymia"]

define "Depression Encounter":
  ["Encounter, Performed": "Contact or Office Visit"] ValidEncounter
    with "Depression Diagnoses" Depression
      such that ValidEncounter.relevantPeriod overlaps Depression.prevalencePeriod
        and ValidEncounter.relevantPeriod ends during "Denominator Identification Period"

define "Has An Order For Palliative or Hospice Care":
  exists ( ["Intervention, Order": "Palliative or Hospice Care"] PalliativeCareOrder
      where PalliativeCareOrder.authorDatetime occurs before 
      end of "Measure Assessment Period"
  )

define "Has Disorder Diagnoses":
  exists ( ( ["Diagnosis": "Bipolar Disorder"]
      union ["Diagnosis": "Personality Disorder Emotionally Labile"]
      union ["Diagnosis": "Schizophrenia or Psychotic Disorder"]
      union ["Diagnosis": "Pervasive Developmental Disorder"] ) DisorderDiagnoses
      where DisorderDiagnoses.prevalencePeriod starts before 
      end of "Measure Assessment Period"
  )

define "Has Long Term Care Encounter":
  exists ( ["Encounter, Performed": "Care Services in Long-Term Residential Facility"] EncounterLongTermCare
      where EncounterLongTermCare.relevantPeriod starts before 
      end of "Measure Assessment Period"
  )

define "Index Depression Assessment":
  First("Depression Assessments Greater than 9" DepressionAssessment
      with "Depression Encounter" DepressionEncounter
        such that DepressionAssessment.relevantDatetime is not null
          and DepressionAssessment.relevantDatetime in Interval[Global."ToDate"((start of DepressionEncounter.relevantPeriod)- 7 days), 
          end of DepressionEncounter.relevantPeriod]
      sort by relevantDatetime
  )

define "Measure Assessment Period":
  "Index Depression Assessment" FirstIndexAssessment
    return Interval[Global."ToDate" ( FirstIndexAssessment.relevantDatetime ) + 12 months - 60 days, Global."ToDate" ( FirstIndexAssessment.relevantDatetime ) + 12 months + 60 days]

define "Patient Expired":
  exists ( ["Patient Characteristic Expired": "Dead (finding)"] Deceased
      where Deceased.expiredDatetime occurs before 
      end of "Measure Assessment Period"
  )

define "Numerator":
  Last(["Assessment, Performed": "PHQ 9 and PHQ 9M Tools"] DepressionAssessment
      where Global."ToDate"(DepressionAssessment.relevantDatetime)during "Measure Assessment Period"
      sort by relevantDatetime
  ).result < 5

define "Has Palliative Care Encounter":
  exists ( ["Encounter, Performed": "Palliative Care Encounter"] PalliativeCareEncounter
      where PalliativeCareEncounter.relevantPeriod starts before 
      end of "Measure Assessment Period"
  )