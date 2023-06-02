library TJC_Overall version '4.4.000'

using QDM version '5.5'

include MATGlobalCommonFunctions version '5.0.000' called Global

codesystem "LOINC": 'urn:oid:2.16.840.1.113883.6.1' 

valueset "Comfort Measures": 'urn:oid:1.3.6.1.4.1.33895.1.3.0.45' 
valueset "Discharge To Acute Care Facility": 'urn:oid:2.16.840.1.113883.3.117.1.7.1.87' 
valueset "Discharged to Health Care Facility for Hospice Care": 'urn:oid:2.16.840.1.113883.3.117.1.7.1.207' 
valueset "Discharged to Home for Hospice Care": 'urn:oid:2.16.840.1.113883.3.117.1.7.1.209' 
valueset "Emergency Department Visit": 'urn:oid:2.16.840.1.113883.3.117.1.7.1.292' 
valueset "Encounter Inpatient": 'urn:oid:2.16.840.1.113883.3.666.5.307' 
valueset "Hemorrhagic Stroke": 'urn:oid:2.16.840.1.113883.3.117.1.7.1.212' 
valueset "Ischemic Stroke": 'urn:oid:2.16.840.1.113883.3.117.1.7.1.247' 
valueset "Left Against Medical Advice": 'urn:oid:2.16.840.1.113883.3.117.1.7.1.308' 
valueset "Non-Elective Inpatient Encounter": 'urn:oid:2.16.840.1.113883.3.117.1.7.1.424' 
valueset "Observation Services": 'urn:oid:2.16.840.1.113762.1.4.1111.143' 
valueset "Patient Expired": 'urn:oid:2.16.840.1.113883.3.117.1.7.1.309' 

code "Birth date": '21112-8' from "LOINC" display 'Birth date'

parameter "Measurement Period" Interval<DateTime>

context Patient

define "Intervention Comfort Measures":
  ["Intervention, Order": "Comfort Measures"]
    union ["Intervention, Performed": "Comfort Measures"]

define "Non Elective Inpatient Encounter":
  ["Encounter, Performed": "Non-Elective Inpatient Encounter"] NonElectiveEncounter
    where Global."LengthInDays" ( NonElectiveEncounter.relevantPeriod ) <= 120
      and NonElectiveEncounter.relevantPeriod ends during "Measurement Period"

define "Ischemic Stroke Encounters with Discharge Disposition":
  ( ( "Ischemic Stroke Encounter" IschemicStrokeEncounter
        where IschemicStrokeEncounter.dischargeDisposition in "Discharge To Acute Care Facility"
          or IschemicStrokeEncounter.dischargeDisposition in "Left Against Medical Advice"
          or IschemicStrokeEncounter.dischargeDisposition in "Patient Expired"
          or IschemicStrokeEncounter.dischargeDisposition in "Discharged to Home for Hospice Care"
          or IschemicStrokeEncounter.dischargeDisposition in "Discharged to Health Care Facility for Hospice Care"
    )
  )

define "Encounter with Principal Diagnosis and Age":
  "All Stroke Encounter" AllStrokeEncounter
    with ["Patient Characteristic Birthdate": "Birth date"] BirthDate
      such that Global."CalendarAgeInYearsAt" ( BirthDate.birthDatetime, start of AllStrokeEncounter.relevantPeriod ) >= 18

define "Ischemic Stroke Encounter":
  "Encounter with Principal Diagnosis and Age" EncounterWithAge
    where exists ( EncounterWithAge.diagnoses Diagnosis
        where Diagnosis.code in "Ischemic Stroke"
          and Diagnosis.rank = 1
    )

define "All Stroke Encounter":
  "Non Elective Inpatient Encounter" NonElectiveEncounter
    where exists ( NonElectiveEncounter.diagnoses Diagnosis
        where Diagnosis.rank = 1
          and ( Diagnosis.code in "Hemorrhagic Stroke"
              or Diagnosis.code in "Ischemic Stroke"
          )
    )

define "Encounter with Comfort Measures during Hospitalization":
  "Ischemic Stroke Encounter" IschemicStrokeEncounter
    with "Intervention Comfort Measures" ComfortMeasure
      such that Coalesce(start of ComfortMeasure.relevantPeriod, ComfortMeasure.authorDatetime)during Global."HospitalizationWithObservation" ( IschemicStrokeEncounter )

define function "HospitalizationWithObservationLengthofStay"(Encounter "Encounter, Performed" ):
  Global."LengthInDays" ( Global."HospitalizationWithObservation" ( Encounter ) )

define function "CalendarDayOfOrDayAfter"(StartValue DateTime ):
  Interval[Global."ToDate" ( StartValue ), Global."ToDate" ( StartValue + 2 days ) )

