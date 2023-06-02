library PreventiveCareandScreeningScreeningforHighBloodPressureandFollowUpDocumented version '8.2.000'

using QDM version '5.4'

include MATGlobalCommonFunctions version '4.0.000' called Global

codesystem "LOINC": 'urn:oid:2.16.840.1.113883.6.1'

valueset "Anti-Hypertensive Pharmacologic Therapy": 'urn:oid:2.16.840.1.113883.3.600.1476'
valueset "BP Screening Encounter Codes": 'urn:oid:2.16.840.1.113883.3.600.1920'
valueset "Diagnosis of hypertension": 'urn:oid:2.16.840.1.113883.3.600.263'
valueset "Dietary Recommendations": 'urn:oid:2.16.840.1.113883.3.600.1515'
valueset "ECG 12 lead or study order": 'urn:oid:2.16.840.1.113883.3.600.2448'
valueset "Ethnicity": 'urn:oid:2.16.840.1.114222.4.11.837'
valueset "Finding of Elevated Blood Pressure or Hypertension": 'urn:oid:2.16.840.1.113762.1.4.1047.514'
valueset "Followup within 4 weeks": 'urn:oid:2.16.840.1.113883.3.600.1537'
valueset "Followup within one year": 'urn:oid:2.16.840.1.113883.3.600.1474'
valueset "Laboratory Tests for Hypertension": 'urn:oid:2.16.840.1.113883.3.600.1482'
valueset "Lifestyle Recommendation": 'urn:oid:2.16.840.1.113883.3.600.1508'
valueset "Medical or Other reason not done": 'urn:oid:2.16.840.1.113883.3.600.1.1502'
valueset "Moderation of ETOH Consumption Recommendation": 'urn:oid:2.16.840.1.113883.3.600.823'
valueset "ONC Administrative Sex": 'urn:oid:2.16.840.1.113762.1.4.1'
valueset "Patient Reason refused": 'urn:oid:2.16.840.1.113883.3.600.791'
valueset "Payer": 'urn:oid:2.16.840.1.114222.4.11.3591'
valueset "Physical Activity Recommendation": 'urn:oid:2.16.840.1.113883.3.600.1518'
valueset "Race": 'urn:oid:2.16.840.1.114222.4.11.836'
valueset "Referral to Alternative Provider / Primary Care Provider": 'urn:oid:2.16.840.1.113883.3.600.1475'
valueset "Weight Reduction Recommended": 'urn:oid:2.16.840.1.113883.3.600.1510'

code "Birth date": '21112-8' from "LOINC" display 'Birth date'
code "Diastolic blood pressure": '8462-4' from "LOINC" display 'Diastolic blood pressure'
code "Systolic blood pressure": '8480-6' from "LOINC" display 'Systolic blood pressure'

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

define "Qualifying Encounters During Measurement Period":
	["Encounter, Performed": "BP Screening Encounter Codes"] QualifyingEncounter
		where QualifyingEncounter.relevantPeriod during "Measurement Period"

define "Qualifying Encounters with Blood Pressure Screening Results":
	"Qualifying Encounters During Measurement Period" QualifyingEncounter
		with ["Physical Exam, Performed": "Diastolic blood pressure"] DiastolicBP
			such that DiastolicBP.relevantPeriod starts during QualifyingEncounter.relevantPeriod
				and DiastolicBP.result is not null
		with ["Physical Exam, Performed": "Systolic blood pressure"] SystolicBP
			such that SystolicBP.relevantPeriod starts during QualifyingEncounter.relevantPeriod
				and SystolicBP.result is not null
		sort by start of relevantPeriod

define "Most Recent Blood Pressure Screening Encounter":
	Last("Qualifying Encounters with Blood Pressure Screening Results")

define "Most Recent Diastolic Reading During Most Recent Blood Pressure Screening Encounter":
	Last(["Physical Exam, Performed": "Diastolic blood pressure"] DiastolicBP
			with "Most Recent Blood Pressure Screening Encounter" LastBPEncounter
				such that DiastolicBP.relevantPeriod during LastBPEncounter.relevantPeriod
					and DiastolicBP.result is not null
			sort by start of relevantPeriod
	)

define "Most Recent Systolic Reading During Most Recent Blood Pressure Screening Encounter":
	Last(["Physical Exam, Performed": "Systolic blood pressure"] SystolicBP
			with "Most Recent Blood Pressure Screening Encounter" LastBPEncounter
				such that SystolicBP.relevantPeriod during LastBPEncounter.relevantPeriod
					and SystolicBP.result is not null
			sort by start of relevantPeriod
	)

define "No Blood Pressure Screen":
	["Physical Exam, Not Performed": "Systolic blood pressure"]
		union ["Physical Exam, Not Performed": "Diastolic blood pressure"]

define "No Blood Pressure Screen Due to Medical Reason or Patient Refusal":
	"No Blood Pressure Screen" NoBPScreen
		with "Qualifying Encounters During Measurement Period" QualifyingEncounter
			such that ( NoBPScreen.negationRationale in "Medical or Other reason not done"
					or NoBPScreen.negationRationale in "Patient Reason refused"
			)
				and NoBPScreen.authorDatetime same day as start of QualifyingEncounter.relevantPeriod

define "Patients Age 18 Years and Older at Start of Measurement Period":
	exists ["Patient Characteristic Birthdate": "Birth date"] BirthDate
		where Global."CalendarAgeInYearsAt"(BirthDate.birthDatetime, start of "Measurement Period")>= 18

define "Prehypertensive Blood Pressure at Most Recent Screening Encounter":
	from
		"Most Recent Systolic Reading During Most Recent Blood Pressure Screening Encounter" LastSystolicBP,
		"Most Recent Diastolic Reading During Most Recent Blood Pressure Screening Encounter" LastDiastolicBP
		where ( LastSystolicBP.result >= 120 'mm[Hg]'
				or LastDiastolicBP.result >= 80 'mm[Hg]'
		)
			and LastSystolicBP.result < 140 'mm[Hg]'
			and LastDiastolicBP.result < 90 'mm[Hg]'

define "Antihypertensive Medication Not Ordered Due to Patient Refusal":
	( ["Medication, Not Ordered": "Anti-Hypertensive Pharmacologic Therapy"] NoAntihypertensives
			with "Qualifying Encounters During Measurement Period" QualifyingEncounter
				such that NoAntihypertensives.negationRationale in "Patient Reason refused"
					and NoAntihypertensives.authorDatetime same day as start of QualifyingEncounter.relevantPeriod
	)

define "Antihypertensive Medications Ordered":
	["Medication, Order": "Anti-Hypertensive Pharmacologic Therapy"] AntihypertensiveMeds
		with "Most Recent Blood Pressure Screening Encounter" LastBPEncounter
			such that AntihypertensiveMeds.authorDatetime same day as start of LastBPEncounter.relevantPeriod

define "Blood Pressure Normal at Most Recent Screening Encounter":
	from
		"Most Recent Systolic Reading During Most Recent Blood Pressure Screening Encounter" LastSystolicBP,
		"Most Recent Diastolic Reading During Most Recent Blood Pressure Screening Encounter" LastDiastolicBP
		where ( LastSystolicBP.result < 120 'mm[Hg]'
				and LastDiastolicBP.result < 80 'mm[Hg]'
		)

define "Denominator Exclusions":
	exists ( ["Diagnosis": "Diagnosis of hypertension"] Hypertension
			with "Qualifying Encounters During Measurement Period" QualifyingEncounter
				such that Hypertension.prevalencePeriod starts before start of QualifyingEncounter.relevantPeriod
	)

define "Diagnostic Study Not Ordered Due to Patient Refusal":
	( ["Diagnostic Study, Not Ordered": "ECG 12 lead or study order"] NoDiagnosticStudy
			with "Qualifying Encounters During Measurement Period" QualifyingEncounter
				such that NoDiagnosticStudy.negationRationale in "Patient Reason refused"
					and NoDiagnosticStudy.authorDatetime same day as start of QualifyingEncounter.relevantPeriod
	)

define "Electrocardiogram Ordered for Hypertension":
	["Diagnostic Study, Order": "ECG 12 lead or study order"] ECG
		with "Most Recent Blood Pressure Screening Encounter" LastBPEncounter
			such that ECG.authorDatetime same day as start of LastBPEncounter.relevantPeriod

define "Follow Up Within 4 Weeks":
	["Intervention, Order": "Followup within 4 weeks"] Followup4Weeks
		with "Most Recent Blood Pressure Screening Encounter" LastBPEncounter
			such that Followup4Weeks.authorDatetime same day as start of LastBPEncounter.relevantPeriod

define "Hypertensive Followup Interventions Not Ordered":
	["Intervention, Not Ordered": "Referral to Alternative Provider / Primary Care Provider"]
		union ["Intervention, Not Ordered": "Lifestyle Recommendation"]
		union ["Intervention, Not Ordered": "Weight Reduction Recommended"]
		union ["Intervention, Not Ordered": "Dietary Recommendations"]
		union ["Intervention, Not Ordered": "Physical Activity Recommendation"]
		union ["Intervention, Not Ordered": "Moderation of ETOH Consumption Recommendation"]

define "Hypertensive Followup Interventions Not Ordered Due to Patient Refusal":
	"Hypertensive Followup Interventions Not Ordered" NoInterventions
		with "Qualifying Encounters During Measurement Period" QualifyingEncounter
			such that NoInterventions.negationRationale in "Patient Reason refused"
				and NoInterventions.authorDatetime same day as start of QualifyingEncounter.relevantPeriod

define "Hypertensive Interventions":
	["Intervention, Order": "Lifestyle Recommendation"]
		union ["Intervention, Order": "Weight Reduction Recommended"]
		union ["Intervention, Order": "Dietary Recommendations"]
		union ["Intervention, Order": "Physical Activity Recommendation"]
		union ["Intervention, Order": "Moderation of ETOH Consumption Recommendation"]

define "Initial Population":
	"Patients Age 18 Years and Older at Start of Measurement Period"
		and exists ( "Qualifying Encounters During Measurement Period" )

define "Last Diastolic One Year Prior to Encounter":
	Last(["Physical Exam, Performed": "Diastolic blood pressure"] DiastolicBP
			with "Most Recent Blood Pressure Screening Encounter" LastBPEncounter
				such that DiastolicBP.relevantPeriod starts 1 year or less before start LastBPEncounter.relevantPeriod
			sort by start of relevantPeriod
	)

define "Last Diastolic One Year Prior to Encounter High":
	"Last Diastolic One Year Prior to Encounter" LastDiastolic
		where LastDiastolic.result >= 90 'mm[Hg]'

define "Last Systolic One Year Prior to Encounter":
	Last(["Physical Exam, Performed": "Systolic blood pressure"] SystolicBP
			with "Most Recent Blood Pressure Screening Encounter" LastBPEncounter
				such that SystolicBP.relevantPeriod starts 1 year or less before start LastBPEncounter.relevantPeriod
			sort by start of relevantPeriod
	)

define "Last Systolic One Year Prior to Encounter High":
	"Last Systolic One Year Prior to Encounter" LastSystolic
		where LastSystolic.result >= 140 'mm[Hg]'

define "Denominator":
	"Initial Population"

define "High Blood Pressure at Most Recent Screening Encounter":
	from
		"Most Recent Systolic Reading During Most Recent Blood Pressure Screening Encounter" LastSystolicBP,
		"Most Recent Diastolic Reading During Most Recent Blood Pressure Screening Encounter" LastDiastolicBP
		where ( LastSystolicBP.result >= 140 'mm[Hg]'
				or LastDiastolicBP.result >= 90 'mm[Hg]'
		)

define "Last Blood Pressure Within One Year High":
	"Last Diastolic One Year Prior to Encounter High" is not null
		or "Last Systolic One Year Prior to Encounter High" is not null

define "Labs Ordered for Hypertension":
	["Laboratory Test, Order": "Laboratory Tests for Hypertension"] HypertensionLabs
		with "Most Recent Blood Pressure Screening Encounter" LastBPEncounter
			such that HypertensionLabs.authorDatetime same day as start of LastBPEncounter.relevantPeriod

define "Lab Test for Hypertension Not Ordered Due to Patient Refusal":
	( ["Laboratory Test, Not Ordered": "Laboratory Tests for Hypertension"] NoLabsOrdered
			with "Qualifying Encounters During Measurement Period" QualifyingEncounter
				such that NoLabsOrdered.negationRationale in "Patient Reason refused"
					and NoLabsOrdered.authorDatetime same day as start of QualifyingEncounter.relevantPeriod
	)

define "Interventions for Second Hypertensive Reading":
	exists ( "Antihypertensive Medications Ordered" )
		or exists ( "Labs Ordered for Hypertension" )
		or exists ( "Electrocardiogram Ordered for Hypertension" )

define "Has Second Hypertensive Reading":
	"Last Blood Pressure Within One Year High"
		and "High Blood Pressure at Most Recent Screening Encounter" is not null

define "Denominator Exceptions":
	exists ( "No Blood Pressure Screen Due to Medical Reason or Patient Refusal" )
		or exists ( "Hypertensive Followup Interventions Not Ordered Due to Patient Refusal" )
		or exists ( "Antihypertensive Medication Not Ordered Due to Patient Refusal" )
		or exists ( "Lab Test for Hypertension Not Ordered Due to Patient Refusal" )
		or exists ( "Diagnostic Study Not Ordered Due to Patient Refusal" )

define "Numerator":
	( "Blood Pressure Normal at Most Recent Screening Encounter" is not null )
		or ( "Has Prehypertensive Reading With Interventions" )
		or ( "Has First Hypertensive Reading With Interventions" )
		or ( "Has Second Hypertensive Reading With Interventions" )

define "Referral to Alternate Provider":
	( ["Intervention, Order": "Referral to Alternative Provider / Primary Care Provider"] Referral
			with "Most Recent Blood Pressure Screening Encounter" LastBPEncounter
				such that Referral.authorDatetime same day as start of LastBPEncounter.relevantPeriod
					and Referral.reason in "Finding of Elevated Blood Pressure or Hypertension"
	)

define "Hypertensive Interventions at Most Recent Blood Pressure Encounter":
	exists ( "Hypertensive Interventions" HighBloodPressureInterventions
			with "Most Recent Blood Pressure Screening Encounter" MostRecentBloodPressureEncounter
				such that HighBloodPressureInterventions.authorDatetime same day as start of MostRecentBloodPressureEncounter.relevantPeriod
	)

define "Has Second Hypertensive Reading With Interventions":
	"Has Second Hypertensive Reading"
		and ( "Hypertensive Interventions at Most Recent Blood Pressure Encounter"
				and "Interventions for Second Hypertensive Reading"
		)
		or exists ( "Referral to Alternate Provider" )

define "Has First Hypertensive Reading With Interventions":
	"High Blood Pressure at Most Recent Screening Encounter" is not null
		and not "Last Blood Pressure Within One Year High"
		and ( "Hypertensive Interventions at Most Recent Blood Pressure Encounter"
				and exists ( "Follow Up Within 4 Weeks" )
		)
		or exists ( "Referral to Alternate Provider" )

define "Follow Up One Year":
	exists ( ["Intervention, Order": "Followup within one year"] FollowupOneYear
			with "Most Recent Blood Pressure Screening Encounter" LastBPEncounter
				such that FollowupOneYear.authorDatetime same day as start of LastBPEncounter.relevantPeriod
	)

define "Has Prehypertensive Reading With Interventions":
	"Prehypertensive Blood Pressure at Most Recent Screening Encounter" is not null
		and ( "Hypertensive Interventions at Most Recent Blood Pressure Encounter"
				and "Follow Up One Year"
		)
		or exists ( "Referral to Alternate Provider" )