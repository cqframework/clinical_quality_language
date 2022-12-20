library TJC_Overall version '3.6.000'

using QDM version '5.4'

include MATGlobalCommonFunctions version '4.0.000' called Global

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
valueset "Ticagrelor Therapy": 'urn:oid:2.16.840.1.113762.1.4.1110.39' 

code "Birth date": '21112-8' from "LOINC" display 'Birth date'

parameter "Measurement Period" Interval<DateTime>

context Patient

define "Intervention Comfort Measures":
	["Intervention, Order": "Comfort Measures"]
		union ["Intervention, Performed": "Comfort Measures"]

define "All Stroke Encounter":
	"Non Elective Inpatient Encounter" NonElectiveEncounter
		where NonElectiveEncounter.principalDiagnosis in "Hemorrhagic Stroke"
			or NonElectiveEncounter.principalDiagnosis in "Ischemic Stroke"

define "Ischemic Stroke Encounter":
	"Encounter with Principal Diagnosis and Age" EncounterWithAge
		where EncounterWithAge.principalDiagnosis in "Ischemic Stroke"

define "Non Elective Inpatient Encounter":
	["Encounter, Performed": "Non-Elective Inpatient Encounter"] NonElectiveEncounter
		where Global."LengthInDays"(NonElectiveEncounter.relevantPeriod)<= 120
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

define "Comfort Measures during Hospitalization":
	"Ischemic Stroke Encounter" IschemicStrokeEncounter
		with "Intervention Comfort Measures" ComfortMeasure
			such that Coalesce(start of ComfortMeasure.relevantPeriod, ComfortMeasure.authorDatetime)during Global."HospitalizationWithObservation"(IschemicStrokeEncounter)

define "Encounter with Principal Diagnosis and Age":
	"All Stroke Encounter" AllStrokeEncounter
		with ["Patient Characteristic Birthdate": "Birth date"] BirthDate
			such that Global."CalendarAgeInYearsAt"(BirthDate.birthDatetime, start of AllStrokeEncounter.relevantPeriod)>= 18

define function "HospitalizationWithObservationLengthofStay"(Encounter "Encounter, Performed" ):
	Global."LengthInDays"(Global."HospitalizationWithObservation"(Encounter))

define function "CalendarDayOfOrDayAfter"(StartValue DateTime ):
	Interval[Global."ToDate"(StartValue), Global."ToDate"(StartValue + 2 days))

