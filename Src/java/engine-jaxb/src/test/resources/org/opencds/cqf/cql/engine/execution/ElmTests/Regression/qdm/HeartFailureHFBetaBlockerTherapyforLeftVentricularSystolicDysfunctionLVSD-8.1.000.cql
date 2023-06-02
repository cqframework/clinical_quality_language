library HeartFailureHFBetaBlockerTherapyforLeftVentricularSystolicDysfunctionLVSD version '8.1.000'

using QDM version '5.4'

include MATGlobalCommonFunctions version '4.0.000' called Global

codesystem "LOINC": 'urn:oid:2.16.840.1.113883.6.1'

valueset "Allergy to Beta Blocker Therapy": 'urn:oid:2.16.840.1.113883.3.526.3.1177'
valueset "Arrhythmia": 'urn:oid:2.16.840.1.113883.3.526.3.366'
valueset "Asthma": 'urn:oid:2.16.840.1.113883.3.526.3.362'
valueset "Atrioventricular Block": 'urn:oid:2.16.840.1.113883.3.526.3.367'
valueset "Beta Blocker Therapy for LVSD": 'urn:oid:2.16.840.1.113883.3.526.3.1184'
valueset "Beta Blocker Therapy Ingredient": 'urn:oid:2.16.840.1.113883.3.526.3.1493'
valueset "Bradycardia": 'urn:oid:2.16.840.1.113883.3.526.3.412'
valueset "Cardiac Pacer": 'urn:oid:2.16.840.1.113883.3.526.3.1193'
valueset "Cardiac Pacer in Situ": 'urn:oid:2.16.840.1.113883.3.526.3.368'
valueset "Care Services in Long-Term Residential Facility": 'urn:oid:2.16.840.1.113883.3.464.1003.101.12.1014'
valueset "Discharge Services - Hospital Inpatient": 'urn:oid:2.16.840.1.113883.3.464.1003.101.12.1007'
valueset "Ejection Fraction": 'urn:oid:2.16.840.1.113883.3.526.3.1134'
valueset "Ethnicity": 'urn:oid:2.16.840.1.114222.4.11.837'
valueset "Heart Failure": 'urn:oid:2.16.840.1.113883.3.526.3.376'
valueset "Heart Rate": 'urn:oid:2.16.840.1.113883.3.526.3.1176'
valueset "Home Healthcare Services": 'urn:oid:2.16.840.1.113883.3.464.1003.101.12.1016'
valueset "Hypotension": 'urn:oid:2.16.840.1.113883.3.526.3.370'
valueset "Intolerance to Beta Blocker Therapy": 'urn:oid:2.16.840.1.113883.3.526.3.1178'
valueset "Left Ventricular Systolic Dysfunction": 'urn:oid:2.16.840.1.113883.3.526.3.1091'
valueset "Medical Reason": 'urn:oid:2.16.840.1.113883.3.526.3.1007'
valueset "Moderate or Severe": 'urn:oid:2.16.840.1.113883.3.526.3.1092'
valueset "Moderate or Severe LVSD": 'urn:oid:2.16.840.1.113883.3.526.3.1090'
valueset "Nursing Facility Visit": 'urn:oid:2.16.840.1.113883.3.464.1003.101.12.1012'
valueset "Office Visit": 'urn:oid:2.16.840.1.113883.3.464.1003.101.12.1001'
valueset "ONC Administrative Sex": 'urn:oid:2.16.840.1.113762.1.4.1'
valueset "Outpatient Consultation": 'urn:oid:2.16.840.1.113883.3.464.1003.101.12.1008'
valueset "Patient Provider Interaction": 'urn:oid:2.16.840.1.113883.3.526.3.1012'
valueset "Patient Reason": 'urn:oid:2.16.840.1.113883.3.526.3.1008'
valueset "Payer": 'urn:oid:2.16.840.1.114222.4.11.3591'
valueset "Race": 'urn:oid:2.16.840.1.114222.4.11.836'
valueset "System Reason": 'urn:oid:2.16.840.1.113883.3.526.3.1009'

code "Birth date": '21112-8' from "LOINC" display 'Birth date'

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

define "Moderate or Severe LVSD Findings":
	( ["Diagnostic Study, Performed": "Ejection Fraction"] EjectionFraction
			where EjectionFraction.result < 40 '%'
	)
		union ["Diagnosis": "Moderate or Severe LVSD"]
		union ( ["Diagnosis": "Left Ventricular Systolic Dysfunction"] LVSD
				where LVSD.severity in "Moderate or Severe"
		)

define "Inpatient Discharge During Measurement Period":
	["Encounter, Performed": "Discharge Services - Hospital Inpatient"] InpatientEncounter
		where InpatientEncounter.relevantPeriod during "Measurement Period"

define "Face to Face Encounter During Measurement Period":
	( ["Encounter, Performed": "Care Services in Long-Term Residential Facility"]
		union ["Encounter, Performed": "Home Healthcare Services"]
		union ["Encounter, Performed": "Nursing Facility Visit"]
		union ["Encounter, Performed": "Office Visit"]
		union ["Encounter, Performed": "Outpatient Consultation"] ) FaceToFaceEncounter
		where FaceToFaceEncounter.relevantPeriod during "Measurement Period"

define "Heart Failure Inpatient Encounter":
	"Inpatient Discharge During Measurement Period" InpatientEncounter
		with ["Diagnosis": "Heart Failure"] HeartFailure
			such that HeartFailure.prevalencePeriod overlaps InpatientEncounter.relevantPeriod

define "Heart Failure Inpatient Encounter with Moderate or Severe LVSD":
	"Heart Failure Inpatient Encounter" HFInpatientEncounter
		with "Moderate or Severe LVSD Findings" InpatientLVSDFindings
			such that InpatientLVSDFindings.prevalencePeriod starts before 
			end of HFInpatientEncounter.relevantPeriod
				or InpatientLVSDFindings.relevantPeriod starts before 
				end of HFInpatientEncounter.relevantPeriod

define "Heart Failure Outpatient Encounter with Moderate or Severe LVSD":
	"Heart Failure Outpatient Encounter" HFOutpatientEncounter
		with "Moderate or Severe LVSD Findings" OutpatientLVSDFindings
			such that OutpatientLVSDFindings.prevalencePeriod starts before 
			end of HFOutpatientEncounter.relevantPeriod
				or OutpatientLVSDFindings.relevantPeriod starts before 
				end of HFOutpatientEncounter.relevantPeriod

define "Allergy or Intolerance to Beta Blocker Therapy Ingredient Outpatient":
	["Allergy/Intolerance": "Beta Blocker Therapy Ingredient"] BetaBlockerAllergyIntolerance
		with "Heart Failure Outpatient Encounter with Moderate or Severe LVSD" ModerateOrSevereLVSDHFOutpatientEncounter
			such that BetaBlockerAllergyIntolerance.prevalencePeriod overlaps after ModerateOrSevereLVSDHFOutpatientEncounter.relevantPeriod

define "Allergy or Intolerance to Beta Blocker Therapy Ingredient Inpatient":
	["Allergy/Intolerance": "Beta Blocker Therapy Ingredient"] BetaBlockerAllergyIntolerance
		with "Heart Failure Inpatient Encounter with Moderate or Severe LVSD" ModerateOrSevereLVSDHFInpatientEncounter
			such that BetaBlockerAllergyIntolerance.prevalencePeriod overlaps after ModerateOrSevereLVSDHFInpatientEncounter.relevantPeriod

define "Arrhythmia Diagnosis Outpatient":
	["Diagnosis": "Arrhythmia"] Arrhythmia
		with "Heart Failure Outpatient Encounter with Moderate or Severe LVSD" ModerateOrSevereLVSDHFOutpatientEncounter
			such that Arrhythmia.prevalencePeriod overlaps ModerateOrSevereLVSDHFOutpatientEncounter.relevantPeriod

define "Arrhythmia Diagnosis Inpatient":
	["Diagnosis": "Arrhythmia"] Arrhythmia
		with "Heart Failure Inpatient Encounter with Moderate or Severe LVSD" ModerateOrSevereLVSDHFInpatientEncounter
			such that Arrhythmia.prevalencePeriod overlaps ModerateOrSevereLVSDHFInpatientEncounter.relevantPeriod

define "Asthma Diagnosis Outpatient":
	["Diagnosis": "Asthma"] Asthma
		with "Heart Failure Outpatient Encounter with Moderate or Severe LVSD" ModerateOrSevereLVSDHFOutpatientEncounter
			such that Asthma.prevalencePeriod overlaps ModerateOrSevereLVSDHFOutpatientEncounter.relevantPeriod

define "Asthma Diagnosis Inpatient":
	["Diagnosis": "Asthma"] Asthma
		with "Heart Failure Inpatient Encounter with Moderate or Severe LVSD" ModerateOrSevereLVSDHFInpatientEncounter
			such that Asthma.prevalencePeriod overlaps ModerateOrSevereLVSDHFInpatientEncounter.relevantPeriod

define "Atrioventricular Block Diagnosis Outpatient":
	["Diagnosis": "Atrioventricular Block"] AtrioventricularBlock
		with "Heart Failure Outpatient Encounter with Moderate or Severe LVSD" ModerateOrSevereLVSDHFOutpatientEncounter
			such that AtrioventricularBlock.prevalencePeriod overlaps ModerateOrSevereLVSDHFOutpatientEncounter.relevantPeriod

define "Atrioventricular Block Diagnosis Inpatient":
	["Diagnosis": "Atrioventricular Block"] AtrioventricularBlock
		with "Heart Failure Inpatient Encounter with Moderate or Severe LVSD" ModerateOrSevereLVSDHFInpatientEncounter
			such that AtrioventricularBlock.prevalencePeriod overlaps ModerateOrSevereLVSDHFInpatientEncounter.relevantPeriod

define "Beta Blocker Therapy for LVSD Ordered Outpatient":
	["Medication, Order": "Beta Blocker Therapy for LVSD"] BetaBlockerOrdered
		with "Heart Failure Outpatient Encounter with Moderate or Severe LVSD" ModerateOrSevereLVSDHFOutpatientEncounter
			such that BetaBlockerOrdered.authorDatetime during ModerateOrSevereLVSDHFOutpatientEncounter.relevantPeriod

define "Bradycardia Diagnosis Outpatient":
	["Diagnosis": "Bradycardia"] Bradycardia
		with "Heart Failure Outpatient Encounter with Moderate or Severe LVSD" ModerateOrSevereLVSDHFOutpatientEncounter
			such that Bradycardia.prevalencePeriod overlaps ModerateOrSevereLVSDHFOutpatientEncounter.relevantPeriod

define "Bradycardia Diagnosis Inpatient":
	["Diagnosis": "Bradycardia"] Bradycardia
		with "Heart Failure Inpatient Encounter with Moderate or Severe LVSD" ModerateOrSevereLVSDHFInpatientEncounter
			such that Bradycardia.prevalencePeriod overlaps ModerateOrSevereLVSDHFInpatientEncounter.relevantPeriod

define "Cardiac Pacer Device Implanted Outpatient":
	["Device, Applied": "Cardiac Pacer"] ImplantedCardiacPacer
		with "Heart Failure Outpatient Encounter with Moderate or Severe LVSD" ModerateOrSevereLVSDHFOutpatientEncounter
			such that ImplantedCardiacPacer.relevantPeriod overlaps after ModerateOrSevereLVSDHFOutpatientEncounter.relevantPeriod

define "Cardiac Pacer Device Implanted Inpatient":
	["Device, Applied": "Cardiac Pacer"] ImplantedCardiacPacer
		with "Heart Failure Inpatient Encounter with Moderate or Severe LVSD" ModerateOrSevereLVSDHFInpatientEncounter
			such that ImplantedCardiacPacer.relevantPeriod overlaps after ModerateOrSevereLVSDHFInpatientEncounter.relevantPeriod

define "Currently Taking Beta Blocker Therapy for LVSD Outpatient":
	["Medication, Active": "Beta Blocker Therapy for LVSD"] ActiveBetaBlocker
		with "Heart Failure Outpatient Encounter with Moderate or Severe LVSD" ModerateOrSevereLVSDHFOutpatientEncounter
			such that ActiveBetaBlocker.relevantPeriod overlaps after ModerateOrSevereLVSDHFOutpatientEncounter.relevantPeriod

define "Currently Taking Beta Blocker Therapy for LVSD Inpatient":
	["Medication, Active": "Beta Blocker Therapy for LVSD"] ActiveBetaBlocker
		with "Heart Failure Inpatient Encounter with Moderate or Severe LVSD" ModerateOrSevereLVSDHFInpatientEncounter
			such that ActiveBetaBlocker.relevantPeriod overlaps after ModerateOrSevereLVSDHFInpatientEncounter.relevantPeriod

define "Denominator 1":
	"Initial Population 1"
		and exists "Heart Failure Outpatient Encounter with Moderate or Severe LVSD"

define "Denominator 2":
	"Initial Population 2"
		and exists "Heart Failure Inpatient Encounter with Moderate or Severe LVSD"

define "Diagnosis of Allergy to Beta Blocker Therapy Outpatient":
	["Diagnosis": "Allergy to Beta Blocker Therapy"] BetaBlockerAllergyDiagnosis
		with "Heart Failure Outpatient Encounter with Moderate or Severe LVSD" ModerateOrSevereLVSDHFOutpatientEncounter
			such that BetaBlockerAllergyDiagnosis.prevalencePeriod overlaps after ModerateOrSevereLVSDHFOutpatientEncounter.relevantPeriod

define "Diagnosis of Allergy to Beta Blocker Therapy Inpatient":
	["Diagnosis": "Allergy to Beta Blocker Therapy"] BetaBlockerAllergyDiagnosis
		with "Heart Failure Inpatient Encounter with Moderate or Severe LVSD" ModerateOrSevereLVSDHFInpatientEncounter
			such that BetaBlockerAllergyDiagnosis.prevalencePeriod overlaps after ModerateOrSevereLVSDHFInpatientEncounter.relevantPeriod

define "Diagnosis of Intolerance to Beta Blocker Therapy Outpatient":
	["Diagnosis": "Intolerance to Beta Blocker Therapy"] BetaBlockerIntoleranceDiagnosis
		with "Heart Failure Outpatient Encounter with Moderate or Severe LVSD" ModerateOrSevereLVSDHFOutpatientEncounter
			such that BetaBlockerIntoleranceDiagnosis.prevalencePeriod overlaps after ModerateOrSevereLVSDHFOutpatientEncounter.relevantPeriod

define "Diagnosis of Intolerance to Beta Blocker Therapy Inpatient":
	["Diagnosis": "Intolerance to Beta Blocker Therapy"] BetaBlockerIntoleranceDiagnosis
		with "Heart Failure Inpatient Encounter with Moderate or Severe LVSD" ModerateOrSevereLVSDHFInpatientEncounter
			such that BetaBlockerIntoleranceDiagnosis.prevalencePeriod overlaps after ModerateOrSevereLVSDHFInpatientEncounter.relevantPeriod

define "Hypotension Diagnosis Outpatient":
	["Diagnosis": "Hypotension"] Hypotension
		with "Heart Failure Outpatient Encounter with Moderate or Severe LVSD" ModerateOrSevereLVSDHFOutpatientEncounter
			such that Hypotension.prevalencePeriod overlaps ModerateOrSevereLVSDHFOutpatientEncounter.relevantPeriod

define "Hypotension Diagnosis Inpatient":
	["Diagnosis": "Hypotension"] Hypotension
		with "Heart Failure Inpatient Encounter with Moderate or Severe LVSD" ModerateOrSevereLVSDHFInpatientEncounter
			such that Hypotension.prevalencePeriod overlaps ModerateOrSevereLVSDHFInpatientEncounter.relevantPeriod

define "Numerator 1":
	exists "Beta Blocker Therapy for LVSD Ordered Outpatient"
		or exists "Currently Taking Beta Blocker Therapy for LVSD Outpatient"

define "Qualifying Encounter":
	( ["Encounter, Performed": "Care Services in Long-Term Residential Facility"]
		union ["Encounter, Performed": "Home Healthcare Services"]
		union ["Encounter, Performed": "Nursing Facility Visit"]
		union ["Encounter, Performed": "Office Visit"]
		union ["Encounter, Performed": "Outpatient Consultation"]
		union ["Encounter, Performed": "Patient Provider Interaction"] ) ValidEncounter
		where ValidEncounter.relevantPeriod during "Measurement Period"

define "Atrioventricular Block without Cardiac Pacer Inpatient":
	exists "Atrioventricular Block Diagnosis Inpatient"
		and not exists "Diagnosis of Cardiac Pacer in Situ Inpatient"
		and not exists "Cardiac Pacer Device Implanted Inpatient"

define "Atrioventricular Block without Cardiac Pacer Outpatient":
	exists "Atrioventricular Block Diagnosis Outpatient"
		and not exists "Diagnosis of Cardiac Pacer in Situ Outpatient"
		and not exists "Cardiac Pacer Device Implanted Outpatient"

define "Beta Blocker Therapy for LVSD at Inpatient Discharge":
	["Medication, Discharge": "Beta Blocker Therapy for LVSD"] BetaBlockerAtDischarge
		with "Heart Failure Inpatient Encounter with Moderate or Severe LVSD" ModerateOrSevereLVSDHFInpatientEncounter
			such that BetaBlockerAtDischarge.authorDatetime during ModerateOrSevereLVSDHFInpatientEncounter.relevantPeriod

define "Diagnosis of Cardiac Pacer in Situ Inpatient":
	["Diagnosis": "Cardiac Pacer in Situ"] CardiacPacerDiagnosis
		with "Heart Failure Inpatient Encounter with Moderate or Severe LVSD" ModerateOrSevereLVSDHFInpatientEncounter
			such that CardiacPacerDiagnosis.prevalencePeriod overlaps after ModerateOrSevereLVSDHFInpatientEncounter.relevantPeriod

define "Diagnosis of Cardiac Pacer in Situ Outpatient":
	["Diagnosis": "Cardiac Pacer in Situ"] CardiacPacerDiagnosis
		with "Heart Failure Outpatient Encounter with Moderate or Severe LVSD" ModerateOrSevereLVSDHFOutpatientEncounter
			such that CardiacPacerDiagnosis.prevalencePeriod overlaps after ModerateOrSevereLVSDHFOutpatientEncounter.relevantPeriod

define "Heart Failure Outpatient Encounter":
	"Face to Face Encounter During Measurement Period" ValidFaceToFaceEncounter
		with ["Diagnosis": "Heart Failure"] HeartFailure
			such that HeartFailure.prevalencePeriod overlaps ValidFaceToFaceEncounter.relevantPeriod

define "Denominator Exceptions 1":
	exists "Consecutive Heart Rates Less than 50 Outpatient"
		or exists "Medical Patient or System Reason for Not Ordering Beta Blocker for LVSD Outpatient"
		or exists "Arrhythmia Diagnosis Outpatient"
		or exists "Hypotension Diagnosis Outpatient"
		or exists "Asthma Diagnosis Outpatient"
		or exists "Diagnosis of Allergy to Beta Blocker Therapy Outpatient"
		or exists "Diagnosis of Intolerance to Beta Blocker Therapy Outpatient"
		or exists "Bradycardia Diagnosis Outpatient"
		or exists "Allergy or Intolerance to Beta Blocker Therapy Ingredient Outpatient"
		or "Atrioventricular Block without Cardiac Pacer Outpatient"

define "Denominator Exceptions 2":
	exists "Consecutive Heart Rates Less than 50 Inpatient"
		or exists "Medical Patient or System Reason for No Beta Blocker for LVSD at Inpatient Discharge"
		or exists "Arrhythmia Diagnosis Inpatient"
		or exists "Hypotension Diagnosis Inpatient"
		or exists "Asthma Diagnosis Inpatient"
		or exists "Diagnosis of Allergy to Beta Blocker Therapy Inpatient"
		or exists "Diagnosis of Intolerance to Beta Blocker Therapy Inpatient"
		or exists "Bradycardia Diagnosis Inpatient"
		or exists "Allergy or Intolerance to Beta Blocker Therapy Ingredient Inpatient"
		or "Atrioventricular Block without Cardiac Pacer Inpatient"

define "Numerator 2":
	exists "Beta Blocker Therapy for LVSD at Inpatient Discharge"
		or exists "Currently Taking Beta Blocker Therapy for LVSD Inpatient"

define "Consecutive Heart Rates Less than 50 Inpatient":
	from
		["Physical Exam, Performed": "Heart Rate"] HeartRate,
		"Heart Failure Inpatient Encounter with Moderate or Severe LVSD" ModerateOrSevereLVSDHFInpatientEncounter
		let PriorHeartRate: Last(["Physical Exam, Performed": "Heart Rate"] MostRecentPriorHeartRate
				where MostRecentPriorHeartRate.relevantPeriod during ModerateOrSevereLVSDHFInpatientEncounter.relevantPeriod
					and MostRecentPriorHeartRate.relevantPeriod starts before start of HeartRate.relevantPeriod
				sort by start of relevantPeriod
		)
		where HeartRate.relevantPeriod during ModerateOrSevereLVSDHFInpatientEncounter.relevantPeriod
			and HeartRate.result < 50 '{Beats}/min'
			and PriorHeartRate.result < 50 '{Beats}/min'

define "Consecutive Heart Rates Less than 50 Outpatient":
	from
		["Physical Exam, Performed": "Heart Rate"] HeartRate,
		"Heart Failure Outpatient Encounter with Moderate or Severe LVSD" ModerateOrSevereLVSDHFOutpatientEncounter
		let PriorHeartRate: Last(["Physical Exam, Performed": "Heart Rate"] MostRecentPriorHeartRate
				where MostRecentPriorHeartRate.relevantPeriod during ModerateOrSevereLVSDHFOutpatientEncounter.relevantPeriod
					and MostRecentPriorHeartRate.relevantPeriod starts before start of HeartRate.relevantPeriod
				sort by start of relevantPeriod
		)
		where HeartRate.relevantPeriod during ModerateOrSevereLVSDHFOutpatientEncounter.relevantPeriod
			and HeartRate.result < 50 '{Beats}/min'
			and PriorHeartRate.result < 50 '{Beats}/min'

define "Medical Patient or System Reason for No Beta Blocker for LVSD at Inpatient Discharge":
	["Medication, Not Discharged": "Beta Blocker Therapy for LVSD"] NoBetaBlockerAtDischarge
		with "Heart Failure Inpatient Encounter with Moderate or Severe LVSD" ModerateOrSevereLVSDHFInpatientEncounter
			such that NoBetaBlockerAtDischarge.authorDatetime during ModerateOrSevereLVSDHFInpatientEncounter.relevantPeriod
		where ( NoBetaBlockerAtDischarge.negationRationale in "Medical Reason"
				or NoBetaBlockerAtDischarge.negationRationale in "Patient Reason"
				or NoBetaBlockerAtDischarge.negationRationale in "System Reason"
		)

define "Medical Patient or System Reason for Not Ordering Beta Blocker for LVSD Outpatient":
	["Medication, Not Ordered": "Beta Blocker Therapy for LVSD"] NoBetaBlockerOrdered
		with "Heart Failure Outpatient Encounter with Moderate or Severe LVSD" ModerateOrSevereLVSDHFOutpatientEncounter
			such that NoBetaBlockerOrdered.authorDatetime during ModerateOrSevereLVSDHFOutpatientEncounter.relevantPeriod
		where ( NoBetaBlockerOrdered.negationRationale in "Medical Reason"
				or NoBetaBlockerOrdered.negationRationale in "Patient Reason"
				or NoBetaBlockerOrdered.negationRationale in "System Reason"
		)

define "Initial Population 1":
	exists ( ["Patient Characteristic Birthdate": "Birth date"] BirthDate
			where Global."CalendarAgeInYearsAt"(BirthDate.birthDatetime, start of "Measurement Period")>= 18
	)
		and Count("Qualifying Encounter")>= 2
		and exists "Heart Failure Outpatient Encounter"

define "Initial Population 2":
	exists ( ["Patient Characteristic Birthdate": "Birth date"] BirthDate
			where Global."CalendarAgeInYearsAt"(BirthDate.birthDatetime, start of "Measurement Period")>= 18
	)
		and exists "Heart Failure Inpatient Encounter"