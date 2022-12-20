library AntithromboticTherapyByEndofHospitalDay2 version '8.1.000'

using QDM version '5.4'

include MATGlobalCommonFunctions version '4.0.000' called Global
include TJC_Overall version '3.6.000' called TJC

valueset "Antithrombotic Therapy": 'urn:oid:2.16.840.1.113883.3.117.1.7.1.201'
valueset "Ethnicity": 'urn:oid:2.16.840.1.114222.4.11.837'
valueset "INR": 'urn:oid:2.16.840.1.113883.3.117.1.7.1.213'
valueset "Intravenous or Intra arterial Thrombolytic (tPA) Therapy Prior to Arrival": 'urn:oid:2.16.840.1.113762.1.4.1110.21'
valueset "Intravenous or Intra-arterial Thrombolytic (t-PA) Therapy": 'urn:oid:2.16.840.1.113762.1.4.1045.21'
valueset "Medical Reason": 'urn:oid:2.16.840.1.113883.3.117.1.7.1.473'
valueset "ONC Administrative Sex": 'urn:oid:2.16.840.1.113762.1.4.1'
valueset "Patient Refusal": 'urn:oid:2.16.840.1.113883.3.117.1.7.1.93'
valueset "Payer": 'urn:oid:2.16.840.1.114222.4.11.3591'
valueset "Race": 'urn:oid:2.16.840.1.114222.4.11.836'
valueset "Thrombolytic (t-PA) Therapy": 'urn:oid:2.16.840.1.113883.3.117.1.7.1.226'
valueset "Ticagrelor Therapy": 'urn:oid:2.16.840.1.113762.1.4.1110.39'

context Patient

define "SDE Ethnicity":
	["Patient Characteristic Ethnicity": "Ethnicity"]

define "SDE Payer":
	["Patient Characteristic Payer": "Payer"]

define "SDE Race":
	["Patient Characteristic Race": "Race"]

define "SDE Sex":
	["Patient Characteristic Sex": "ONC Administrative Sex"]

define "Numerator":
	"Encounter with Antithrombotic Therapy"

define "Denominator":
	TJC."Ischemic Stroke Encounter"

define "Initial Population":
	TJC."Encounter with Principal Diagnosis and Age"

define "Thrombolytic Medication":
	["Medication, Administered": "Thrombolytic (t-PA) Therapy"]

define "Thrombolytic Therapy Medication or Procedures":
	"Thrombolytic Medication"
		union ["Procedure, Performed": "Intravenous or Intra-arterial Thrombolytic (t-PA) Therapy"]

define "No Antithrombotic Administered":
	["Medication, Not Administered": "Antithrombotic Therapy"] NoAntithromboticGiven
		where NoAntithromboticGiven.negationRationale in "Medical Reason"
			or NoAntithromboticGiven.negationRationale in "Patient Refusal"

define "No Antithrombotic Ordered for Medical Reason or Patient Refusal":
	["Medication, Not Ordered": "Antithrombotic Therapy"] NoAntithromboticOrder
		where NoAntithromboticOrder.negationRationale in "Medical Reason"
			or NoAntithromboticOrder.negationRationale in "Patient Refusal"

define "No Antithrombotic Ordered":
	"No Antithrombotic Ordered for Medical Reason or Patient Refusal"
		union "No Antithrombotic Administered"

define "Encounter with Comfort Measures":
	TJC."Ischemic Stroke Encounter" IschemicStrokeEncounter
		with TJC."Intervention Comfort Measures" ComfortMeasures
			such that Coalesce(start of ComfortMeasures.relevantPeriod, ComfortMeasures.authorDatetime)during TJC."CalendarDayOfOrDayAfter"(start of Global."HospitalizationWithObservation"(IschemicStrokeEncounter))

define "Encounter with Thrombolytic Medication":
	TJC."Ischemic Stroke Encounter" IschemicStrokeEncounter
		with "Thrombolytic Medication" Thrombolytic
			such that Thrombolytic.relevantPeriod starts during Global."HospitalizationWithObservation"(IschemicStrokeEncounter)

define "Encounter with Thrombolytic Therapy Medication or Procedures":
	TJC."Ischemic Stroke Encounter" IschemicStrokeEncounter
		with "Thrombolytic Therapy Medication or Procedures" ThrombolyticTherapy
			such that ThrombolyticTherapy.relevantPeriod starts 24 hours or less before start of Global."HospitalizationWithObservation"(IschemicStrokeEncounter)

define "Encounter Less Than Two Days":
	TJC."Ischemic Stroke Encounter" IschemicStrokeEncounter
		where TJC."HospitalizationWithObservationLengthofStay"(IschemicStrokeEncounter)< 2

define "Encounter With Thrombolytic Therapy Prior to Arrival":
	TJC."Ischemic Stroke Encounter" IschemicStrokeEncounter
		where exists IschemicStrokeEncounter.diagnoses Diagnosis
			where Diagnosis in "Intravenous or Intra arterial Thrombolytic (tPA) Therapy Prior to Arrival"

define "Denominator Exclusions":
	"Encounter Less Than Two Days"
		union "Encounter with Thrombolytic Therapy Medication or Procedures"
		union "Encounter with Thrombolytic Medication"
		union "Encounter with Comfort Measures"
		union "Encounter With An INR Greater Than 3.5"
		union "Encounter With Thrombolytic Therapy Prior to Arrival"
		union "Encounter With Thrombolytic Therapy Documented As Already Given"

define "Encounter With An INR Greater Than 3.5":
	TJC."Ischemic Stroke Encounter" IschemicStrokeEncounter
		with ["Laboratory Test, Performed": "INR"] INR
			such that INR.resultDatetime during TJC."CalendarDayOfOrDayAfter"(start of Global."HospitalizationWithObservation"(IschemicStrokeEncounter))
				and INR.result > 3.5

define "Encounter with Antithrombotic Therapy":
	TJC."Ischemic Stroke Encounter" IschemicStrokeEncounter
		with ["Medication, Administered": "Antithrombotic Therapy"] Antithrombotic
			such that Antithrombotic.relevantPeriod starts during TJC."CalendarDayOfOrDayAfter"(start of Global."HospitalizationWithObservation"(IschemicStrokeEncounter))

define "No Antithrombotic Ordered Day Of or Day After Hospital Arrival":
	TJC."Ischemic Stroke Encounter" IschemicStrokeEncounter
		with "No Antithrombotic Ordered" NoAntithrombotic
			such that NoAntithrombotic.authorDatetime during TJC."CalendarDayOfOrDayAfter"(start of Global."HospitalizationWithObservation"(IschemicStrokeEncounter))

define "Encounter With Ticagrelor Therapy Given Day Of or Day After Hospital Arrival":
	TJC."Ischemic Stroke Encounter" IschemicStrokeEncounter
		with ["Medication, Administered": "Ticagrelor Therapy"] Ticagrelor
			such that Ticagrelor.relevantPeriod starts during TJC."CalendarDayOfOrDayAfter"(start of Global."HospitalizationWithObservation"(IschemicStrokeEncounter))

define "Denominator Exceptions":
	"No Antithrombotic Ordered Day Of or Day After Hospital Arrival"
		union "Encounter With Ticagrelor Therapy Given Day Of or Day After Hospital Arrival"

define "Encounter With Thrombolytic Therapy Documented As Already Given":
	TJC."Ischemic Stroke Encounter" IschemicStrokeEncounter
		with [Diagnosis: "Intravenous or Intra arterial Thrombolytic (tPA) Therapy Prior to Arrival"] PriorTPA
			such that PriorTPA.authorDatetime during Global."HospitalizationWithObservation"(IschemicStrokeEncounter)