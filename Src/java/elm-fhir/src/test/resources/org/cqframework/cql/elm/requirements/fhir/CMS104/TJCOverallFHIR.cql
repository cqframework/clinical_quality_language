library TJCOverallFHIR version '1.4.000'

/*@update: @@BTR 2020-03-31 ->
Incremented version to 5.0.000
Updated FHIR version to 4.0.1
@@
@update: @@BTR 2021-07-02 ->
Incremented GCL version to 6.0.000
Applied 2021 AU changes
@@*/

using FHIR version '4.0.1'

include MATGlobalCommonFunctionsFHIR4 version '6.1.000' called Global
include FHIRHelpers version '4.0.001' called FHIRHelpers

codesystem "LOINC": 'http://loinc.org' 

valueset "Comfort Measures": 'http://cts.nlm.nih.gov/fhir/ValueSet/1.3.6.1.4.1.33895.1.3.0.45' 
valueset "Discharge To Acute Care Facility": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113883.3.117.1.7.1.87' 
valueset "Discharged to Health Care Facility for Hospice Care": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113883.3.117.1.7.1.207' 
valueset "Discharged to Home for Hospice Care": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113883.3.117.1.7.1.209' 
valueset "Emergency Department Visit": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113883.3.117.1.7.1.292' 
valueset "Encounter Inpatient": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113883.3.666.5.307' 
valueset "Hemorrhagic Stroke": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113883.3.117.1.7.1.212' 
valueset "Ischemic Stroke": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113883.3.117.1.7.1.247' 
valueset "Left Against Medical Advice": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113883.3.117.1.7.1.308' 
valueset "Non-Elective Inpatient Encounter": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113883.3.117.1.7.1.424' 
valueset "Observation Services": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113762.1.4.1111.143' 
valueset "Patient Expired": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113883.3.117.1.7.1.309' 
valueset "Ticagrelor Therapy": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113762.1.4.1110.39' 

code "Birth date": '21112-8' from "LOINC" display 'Birth date'

parameter "Measurement Period" Interval<DateTime>
  default Interval[@2019-01-01T00:00:00.0, @2020-01-01T00:00:00.0)

context Patient

define "Intervention Comfort Measures":
  (["ServiceRequest": "Comfort Measures"] P
          		where P.intent = 'order')
          		union
          		(["Procedure": "Comfort Measures"] InterventionPerformed
          		where InterventionPerformed.status = 'completed')

define "All Stroke Encounter":
  "Non Elective Inpatient Encounter" NonElectiveEncounter
              where Global.PrincipalDiagnosis(NonElectiveEncounter).code in "Hemorrhagic Stroke"
                or Global.PrincipalDiagnosis(NonElectiveEncounter).code in "Ischemic Stroke"

define "Ischemic Stroke Encounter":
  "Encounter with Principal Diagnosis and Age" EncounterWithAge
          		where Global.PrincipalDiagnosis(EncounterWithAge).code in "Ischemic Stroke"

define "Ischemic Stroke Encounters with Discharge Disposition":
  "Ischemic Stroke Encounter" IschemicStrokeEncounter
          		where IschemicStrokeEncounter.hospitalization.dischargeDisposition in "Discharge To Acute Care Facility"
          			or IschemicStrokeEncounter.hospitalization.dischargeDisposition in "Left Against Medical Advice"
          			or IschemicStrokeEncounter.hospitalization.dischargeDisposition in "Patient Expired"
          			or IschemicStrokeEncounter.hospitalization.dischargeDisposition in "Discharged to Home for Hospice Care"
          			or IschemicStrokeEncounter.hospitalization.dischargeDisposition in "Discharged to Health Care Facility for Hospice Care"
          
          // Translator issue with 1.4.9-SNAPSHOT: https://github.com/cqframework/clinical_quality_language/issues/511

define "Non Elective Inpatient Encounter":
  ["Encounter": "Non-Elective Inpatient Encounter"] NonElectiveEncounter
          		where Global."LengthInDays"(NonElectiveEncounter.period) <= 120
          			and NonElectiveEncounter.period ends during day of "Measurement Period"

define "Encounter with Principal Diagnosis and Age":
  "All Stroke Encounter" AllStrokeEncounter
          		with ["Patient"] BirthDate
          			such that AgeInYearsAt(start of AllStrokeEncounter.period) >= 18

define "Comfort Measures during Hospitalization":
  "Ischemic Stroke Encounter" IschemicStrokeEncounter
      with "Intervention Comfort Measures" ComfortMeasure
      such that FHIRHelpers.ToDateTime(Coalesce(ComfortMeasure.performed as dateTime,ComfortMeasure.authoredOn)) during Global."HospitalizationWithObservation"(IschemicStrokeEncounter)

define function "CalendarDayOfOrDayAfter"(StartValue DateTime ):
  Interval[date from ( StartValue ), date from (StartValue) + 1 day ]

