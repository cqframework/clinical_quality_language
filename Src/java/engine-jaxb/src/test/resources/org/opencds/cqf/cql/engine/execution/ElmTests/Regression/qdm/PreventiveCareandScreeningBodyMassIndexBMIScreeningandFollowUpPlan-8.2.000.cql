library PreventiveCareandScreeningBodyMassIndexBMIScreeningandFollowUpPlan version '8.2.000'

using QDM version '5.4'

include MATGlobalCommonFunctions version '4.0.000' called Global

codesystem "LOINC": 'urn:oid:2.16.840.1.113883.6.1'

valueset "Above Normal Follow-up": 'urn:oid:2.16.840.1.113883.3.600.1.1525'
valueset "Above Normal Medications": 'urn:oid:2.16.840.1.113883.3.600.1.1498'
valueset "Below Normal Follow up": 'urn:oid:2.16.840.1.113883.3.600.1.1528'
valueset "Below Normal Medications": 'urn:oid:2.16.840.1.113883.3.600.1.1499'
valueset "BMI Encounter Code Set": 'urn:oid:2.16.840.1.113883.3.600.1.1751'
valueset "Ethnicity": 'urn:oid:2.16.840.1.114222.4.11.837'
valueset "Medical or Other reason not done": 'urn:oid:2.16.840.1.113883.3.600.1.1502'
valueset "ONC Administrative Sex": 'urn:oid:2.16.840.1.113762.1.4.1'
valueset "Overweight or Obese": 'urn:oid:2.16.840.1.113762.1.4.1047.502'
valueset "Palliative care encounter": 'urn:oid:2.16.840.1.113883.3.600.1.1575'
valueset "Palliative or Hospice Care": 'urn:oid:2.16.840.1.113883.3.600.1.1579'
valueset "Patient Reason refused": 'urn:oid:2.16.840.1.113883.3.600.791'
valueset "Payer": 'urn:oid:2.16.840.1.114222.4.11.3591'
valueset "Pregnancy Dx": 'urn:oid:2.16.840.1.113883.3.600.1.1623'
valueset "Race": 'urn:oid:2.16.840.1.114222.4.11.836'
valueset "Referrals where weight assessment may occur": 'urn:oid:2.16.840.1.113883.3.600.1.1527'
valueset "Underweight": 'urn:oid:2.16.840.1.113883.3.600.2388'

code "Birth date": '21112-8' from "LOINC" display 'Birth date'
code "Body mass index (BMI) [Ratio]": '39156-5' from "LOINC" display 'Body mass index (BMI) [Ratio]'

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

define "Pregnancy During Measurement Period":
	["Diagnosis": "Pregnancy Dx"] Pregnancy
		where Pregnancy.prevalencePeriod overlaps "Measurement Period"

define "Numerator":
	"Has Normal BMI" is not null
		or "Has High BMI with Follow Up"
		or "Has Low BMI with Follow Up"

define "Denominator Exceptions":
	exists ( "No BMI Due to Medical Reason" )
		or exists ( "No Medication Order For High BMI Due to Medical Reason" )
		or exists ( "No Follow Up For High BMI Due to Medical Reason" )
		or exists ( "No Medication Order For Low BMI Due to Medical Reason" )
		or exists ( "No Follow Up For Low BMI Due to Medical Reason" )
		or exists ( "No BMI Referral Due to Medical Reason" )

define "Has Normal BMI":
	"Most Recent Documented BMI" BMIValue
		where BMIValue.result included in Interval[18.5 'kg/m2', 25 'kg/m2' )

define "Palliative Care Encounter Performed":
	["Encounter, Performed": "Palliative care encounter"] PalliativeCareEncounter
		with "Qualifying Encounters During the Measurement Period" QualifyingEncounter
			such that PalliativeCareEncounter.relevantPeriod starts on or before 
			end of QualifyingEncounter.relevantPeriod

define "No BMI Due to Patient Refusal":
	["Physical Exam, Not Performed": "Body mass index (BMI) [Ratio]"] NoBMI
		with "Qualifying Encounters During the Measurement Period" QualifyingEncounter
			such that NoBMI.negationRationale in "Patient Reason refused"
				and NoBMI.authorDatetime during QualifyingEncounter.relevantPeriod

define "Has High BMI with Follow Up":
	Last(["Physical Exam, Performed": "Body mass index (BMI) [Ratio]"] LastBMI
			with "High BMI Follow Up Encounters" HighBMIEncounter
				such that LastBMI.relevantPeriod starts 12 months or less on or before 
				end of HighBMIEncounter.relevantPeriod
			sort by authorDatetime
	).result >= 25 'kg/m2'

define "Has Low BMI with Follow Up":
	Last(["Physical Exam, Performed": "Body mass index (BMI) [Ratio]"] LastBMI
			with "Low BMI Follow Up Encounters" LowBMIEncounter
				such that LastBMI.relevantPeriod starts 12 months or less on or before 
				end of LowBMIEncounter.relevantPeriod
			sort by authorDatetime
	).result < 18.5 'kg/m2'

define "No BMI Due to Medical Reason":
	["Physical Exam, Not Performed": "Body mass index (BMI) [Ratio]"] NoBMI
		with "Qualifying Encounters During the Measurement Period" QualifyingEncounter
			such that NoBMI.negationRationale in "Medical or Other reason not done"
				and NoBMI.authorDatetime 12 months or less on or before day of QualifyingEncounter.relevantPeriod

define "No BMI Referral Due to Medical Reason":
	["Intervention, Not Ordered": "Referrals where weight assessment may occur"] NoReferral
		with "Qualifying Encounters During the Measurement Period" QualifyingEncounter
			such that NoReferral.negationRationale in "Medical or Other reason not done"
				and NoReferral.authorDatetime 12 months or less on or before day of QualifyingEncounter.relevantPeriod

define "No Medication Order For High BMI Due to Medical Reason":
	["Medication, Not Ordered": "Above Normal Medications"] HighBMINoMedications
		with "Qualifying Encounters During the Measurement Period" QualifyingEncounter
			such that HighBMINoMedications.negationRationale in "Medical or Other reason not done"
				and HighBMINoMedications.authorDatetime 12 months or less on or before day of QualifyingEncounter.relevantPeriod

define "No Medication Order For Low BMI Due to Medical Reason":
	["Medication, Not Ordered": "Below Normal Medications"] LowBMINoMedications
		with "Qualifying Encounters During the Measurement Period" QualifyingEncounter
			such that LowBMINoMedications.negationRationale in "Medical or Other reason not done"
				and LowBMINoMedications.authorDatetime 12 months or less on or before day of QualifyingEncounter.relevantPeriod

define "Most Recent Documented BMI":
	Last(["Physical Exam, Performed": "Body mass index (BMI) [Ratio]"] LastBMI
			with "Qualifying Encounters During the Measurement Period" QualifyingEncounter
				such that(LastBMI.relevantPeriod starts 12 months or less on or before start of QualifyingEncounter.relevantPeriod
						or LastBMI.relevantPeriod starts during QualifyingEncounter.relevantPeriod
				)
					and LastBMI.result is not null
			sort by authorDatetime
	)

define "Initial Population":
	exists ( "Qualifying Encounters During the Measurement Period" )

define "High BMI Follow Up Encounters":
	"Qualifying Encounters During the Measurement Period" QualifyingEncounter
		with ( ["Intervention, Order": "Above Normal Follow-up"]
			union ["Intervention, Performed": "Above Normal Follow-up"]
			union ( ["Intervention, Order": "Referrals where weight assessment may occur"] Referral
					where Referral.reason in "Overweight or Obese"
			)
			union ["Medication, Order": "Above Normal Medications"] ) HighBMIInterventions
			such that Coalesce(start of HighBMIInterventions.relevantPeriod, HighBMIInterventions.authorDatetime)12 months or less on or before 
			end of QualifyingEncounter.relevantPeriod

define "Low BMI Follow Up Encounters":
	"Qualifying Encounters During the Measurement Period" QualifyingEncounter
		with ( ["Intervention, Order": "Below Normal Follow up"]
			union ["Intervention, Performed": "Below Normal Follow up"]
			union ( ["Intervention, Order": "Referrals where weight assessment may occur"] Referral
					where Referral.reason in "Underweight"
			)
			union ["Medication, Order": "Below Normal Medications"] ) LowBMIInterventions
			such that Coalesce(start of LowBMIInterventions.relevantPeriod, LowBMIInterventions.authorDatetime)12 months or less on or before 
			end of QualifyingEncounter.relevantPeriod

define "No Follow Up For High BMI Due to Medical Reason":
	( ["Intervention, Not Ordered": "Above Normal Follow-up"]
		union ["Intervention, Not Performed": "Above Normal Follow-up"] ) HighBMINoFollowup
		with "Qualifying Encounters During the Measurement Period" QualifyingEncounter
			such that HighBMINoFollowup.negationRationale in "Medical or Other reason not done"
				and HighBMINoFollowup.authorDatetime 12 months or less on or before day of QualifyingEncounter.relevantPeriod

define "Denominator Exclusions":
	exists ( "Palliative or Hospice Care Order or Encounter" )
		or exists ( "No BMI Due to Patient Refusal" )
		or exists ( "Pregnancy During Measurement Period" )

define "Palliative or Hospice Care Order or Encounter":
	"Palliative or Hospice Care Ordered"
		union "Palliative Care Encounter Performed"

define "Qualifying Encounters During the Measurement Period":
	["Encounter, Performed": "BMI Encounter Code Set"] BMIEncounter
		with ["Patient Characteristic Birthdate": "Birth date"] BirthDate
			such that Global."CalendarAgeInYearsAt"(BirthDate.birthDatetime, start of BMIEncounter.relevantPeriod)>= 18
		where BMIEncounter.relevantPeriod during "Measurement Period"
		sort by start of relevantPeriod

define "No Follow Up For Low BMI Due to Medical Reason":
	( ["Intervention, Not Ordered": "Below Normal Follow up"]
		union ["Intervention, Not Performed": "Below Normal Follow up"] ) LowBMINoFollowUp
		with "Qualifying Encounters During the Measurement Period" QualifyingEncounter
			such that LowBMINoFollowUp.negationRationale in "Medical or Other reason not done"
				and LowBMINoFollowUp.authorDatetime 12 months or less on or before day of QualifyingEncounter.relevantPeriod

define "Palliative or Hospice Care Ordered":
	["Intervention, Order": "Palliative or Hospice Care"] PalliativeOrHospiceCareOrder
		with "Qualifying Encounters During the Measurement Period" QualifyingEncounter
			such that PalliativeOrHospiceCareOrder.authorDatetime on or before 
			end of QualifyingEncounter.relevantPeriod