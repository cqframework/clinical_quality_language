library DischargedonStatinMedication version '8.1.000'

using QDM version '5.4'

include MATGlobalCommonFunctions version '4.0.000' called Global
include TJC_Overall version '3.6.000' called TJC

valueset "Ethnicity": 'urn:oid:2.16.840.1.114222.4.11.837'
valueset "LDL-c": 'urn:oid:2.16.840.1.113883.3.117.1.7.1.215'
valueset "Medical Reason": 'urn:oid:2.16.840.1.113883.3.117.1.7.1.473'
valueset "ONC Administrative Sex": 'urn:oid:2.16.840.1.113762.1.4.1'
valueset "Patient Refusal": 'urn:oid:2.16.840.1.113883.3.117.1.7.1.93'
valueset "Payer": 'urn:oid:2.16.840.1.114222.4.11.3591'
valueset "Race": 'urn:oid:2.16.840.1.114222.4.11.836'
valueset "Statin Allergen": 'urn:oid:2.16.840.1.113762.1.4.1110.42'
valueset "Statin Grouper": 'urn:oid:2.16.840.1.113762.1.4.1110.19'

context Patient

define "SDE Ethnicity":
	["Patient Characteristic Ethnicity": "Ethnicity"]

define "SDE Payer":
	["Patient Characteristic Payer": "Payer"]

define "SDE Race":
	["Patient Characteristic Race": "Race"]

define "SDE Sex":
	["Patient Characteristic Sex": "ONC Administrative Sex"]

define "Statin Allergy":
	["Allergy/Intolerance": "Statin Allergen"]

define "Statin at Discharge":
	["Medication, Discharge": "Statin Grouper"]

define "Statin Not Given at Discharge":
	["Medication, Not Discharged": "Statin Grouper"] NoStatinDischarge
		where NoStatinDischarge.negationRationale in "Medical Reason"
			or NoStatinDischarge.negationRationale in "Patient Refusal"

define "Denominator":
	TJC."Ischemic Stroke Encounter"

define "Initial Population":
	TJC."Encounter with Principal Diagnosis and Age"

define "Numerator":
	TJC."Ischemic Stroke Encounter" IschemicStrokeEncounter
		with "Statin at Discharge" DischargeStatin
			such that DischargeStatin.authorDatetime during IschemicStrokeEncounter.relevantPeriod

define "Encounter with Max LDL less than 70 mg per dL":
	TJC."Ischemic Stroke Encounter" IschemicStrokeEncounter
		where Max(["Laboratory Test, Performed": "LDL-c"] Ldl
				where Ldl.resultDatetime during Interval[Global."ToDate"(start of IschemicStrokeEncounter.relevantPeriod - 30 days), 
				end of IschemicStrokeEncounter.relevantPeriod]
				return Ldl.result as Quantity
		)< 70 'mg/dL'

define "Denominator Exceptions":
	( TJC."Ischemic Stroke Encounter" IschemicStrokeEncounter
			with "Statin Not Given at Discharge" NoDischargeStatin
				such that NoDischargeStatin.authorDatetime during IschemicStrokeEncounter.relevantPeriod
	)
		union ( TJC."Ischemic Stroke Encounter" IschemicStrokeEncounter
				with "Statin Allergy" StatinAllergy
					such that StatinAllergy.prevalencePeriod starts on or before 
					end of IschemicStrokeEncounter.relevantPeriod
		)
		union "Encounter with Max LDL less than 70 mg per dL"

define "Denominator Exclusions":
	TJC."Ischemic Stroke Encounters with Discharge Disposition"
		union TJC."Comfort Measures during Hospitalization"