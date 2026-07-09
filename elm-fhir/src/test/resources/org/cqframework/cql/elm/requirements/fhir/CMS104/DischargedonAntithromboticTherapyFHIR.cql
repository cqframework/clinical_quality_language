library DischargedonAntithromboticTherapyFHIR version '2.0.004'

using FHIR version '4.0.1'

include FHIRHelpers version '4.0.001' called FHIRHelpers
include SupplementalDataElementsFHIR4 version '2.0.000' called SDE
include TJCOverallFHIR version '1.4.000' called TJC
include MATGlobalCommonFunctionsFHIR4 version '6.1.000' called Global

valueset "Antithrombotic Therapy": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113883.3.117.1.7.1.201' 
valueset "Comfort Measures": 'http://cts.nlm.nih.gov/fhir/ValueSet/1.3.6.1.4.1.33895.1.3.0.45' 
valueset "Discharge To Acute Care Facility": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113883.3.117.1.7.1.87' 
valueset "Discharged to Health Care Facility for Hospice Care": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113883.3.117.1.7.1.207' 
valueset "Discharged to Home for Hospice Care": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113883.3.117.1.7.1.209' 
valueset "Emergency Department Visit": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113883.3.117.1.7.1.292' 
valueset "Hemorrhagic Stroke": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113883.3.117.1.7.1.212' 
valueset "Ischemic Stroke": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113883.3.117.1.7.1.247' 
valueset "Left Against Medical Advice": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113883.3.117.1.7.1.308' 
valueset "Medical Reason": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113883.3.117.1.7.1.473' 
valueset "Non-Elective Inpatient Encounter": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113883.3.117.1.7.1.424' 
valueset "Patient Expired": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113883.3.117.1.7.1.309' 
valueset "Patient Refusal": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113883.3.117.1.7.1.93' 
valueset "Pharmacological Contraindications For Antithrombotic Therapy": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113762.1.4.1110.52' 

parameter "Measurement Period" Interval<DateTime>

context Patient

define "SDE Ethnicity":
  SDE."SDE Ethnicity"

define "SDE Payer":
  SDE."SDE Payer"

define "SDE Race":
  SDE."SDE Race"

define "SDE Sex":
  SDE."SDE Sex"

define "Denominator":
  TJC."Ischemic Stroke Encounter"

define "Initial Population":
  TJC."Encounter with Principal Diagnosis and Age"

define "Denominator Exclusions":
  TJC."Ischemic Stroke Encounters with Discharge Disposition"
      		union TJC."Comfort Measures during Hospitalization"

define "Antithrombotic Therapy at Discharge":
  ["MedicationRequest": medication in "Antithrombotic Therapy"] Antithrombotic
      	  where exists (Antithrombotic.category C where FHIRHelpers.ToConcept(C) ~ Global."Community" or FHIRHelpers.ToConcept(C) ~ Global."Discharge")
            and Antithrombotic.status in { 'active', 'completed' }
      	    and Antithrombotic.intent.value = 'order'

define "Denominator Exceptions":
  "Encounter With No Antithrombotic At Discharge"
      		union "Encounter With Pharmacological Contraindications for Antithrombotic Therapy at Discharge"

define "Antithrombotic Not Given at Discharge":
  ["MedicationRequest": medication in "Antithrombotic Therapy"] NoAntithromboticDischarge
          where NoAntithromboticDischarge.doNotPerform is true
      			and (NoAntithromboticDischarge.reasonCode in "Medical Reason"
      				or NoAntithromboticDischarge.reasonCode in "Patient Refusal")
            and exists (NoAntithromboticDischarge.category C where FHIRHelpers.ToConcept(C) ~ Global."Community" or FHIRHelpers.ToConcept(C) ~ Global."Discharge")
            and NoAntithromboticDischarge.status = 'completed'
            and NoAntithromboticDischarge.intent = 'order'

define "Pharmacological Contraindications for Antithrombotic Therapy at Discharge":
  ["MedicationRequest": medication in "Pharmacological Contraindications For Antithrombotic Therapy"] Pharmacological
          where exists (Pharmacological.category C where FHIRHelpers.ToConcept(C) ~ Global."Community" or FHIRHelpers.ToConcept(C) ~ Global."Discharge")
            and Pharmacological.status in { 'active', 'completed' }
            and Pharmacological.intent = 'order'

define "Encounter With No Antithrombotic At Discharge":
  TJC."Ischemic Stroke Encounter" IschemicStrokeEncounter
      	with "Antithrombotic Not Given at Discharge" NoDischargeAntithrombotic
      	such that NoDischargeAntithrombotic.authoredOn during IschemicStrokeEncounter.period

define "Encounter With Pharmacological Contraindications for Antithrombotic Therapy at Discharge":
  TJC."Ischemic Stroke Encounter" IschemicStrokeEncounter
          with "Pharmacological Contraindications for Antithrombotic Therapy at Discharge" DischargePharmacological
            such that DischargePharmacological.authoredOn during IschemicStrokeEncounter.period

define "Numerator":
  TJC."Ischemic Stroke Encounter" IschemicStrokeEncounter
      		with "Antithrombotic Therapy at Discharge" DischargeAntithrombotic
      			such that DischargeAntithrombotic.authoredOn during IschemicStrokeEncounter.period

