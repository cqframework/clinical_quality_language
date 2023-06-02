library CoronaryArteryDiseaseCADBetaBlockerTherapyPriorMyocardialInfarctionMIorLeftVentricularSystolicDysfunctionLVEF40 version '8.2.000'

using QDM version '5.4'

include MATGlobalCommonFunctions version '4.0.000' called Global

codesystem "LOINC": 'urn:oid:2.16.840.1.113883.6.1'

valueset "Allergy to Beta Blocker Therapy": 'urn:oid:2.16.840.1.113883.3.526.3.1177'
valueset "Arrhythmia": 'urn:oid:2.16.840.1.113883.3.526.3.366'
valueset "Asthma": 'urn:oid:2.16.840.1.113883.3.526.3.362'
valueset "Atrioventricular Block": 'urn:oid:2.16.840.1.113883.3.526.3.367'
valueset "Beta Blocker Therapy": 'urn:oid:2.16.840.1.113883.3.526.3.1174'
valueset "Beta Blocker Therapy for LVSD": 'urn:oid:2.16.840.1.113883.3.526.3.1184'
valueset "Beta Blocker Therapy Ingredient": 'urn:oid:2.16.840.1.113883.3.526.3.1493'
valueset "Bradycardia": 'urn:oid:2.16.840.1.113883.3.526.3.412'
valueset "Cardiac Pacer": 'urn:oid:2.16.840.1.113883.3.526.3.1193'
valueset "Cardiac Pacer in Situ": 'urn:oid:2.16.840.1.113883.3.526.3.368'
valueset "Cardiac Surgery": 'urn:oid:2.16.840.1.113883.3.526.3.371'
valueset "Care Services in Long-Term Residential Facility": 'urn:oid:2.16.840.1.113883.3.464.1003.101.12.1014'
valueset "Coronary Artery Disease No MI": 'urn:oid:2.16.840.1.113883.3.526.3.369'
valueset "Ejection Fraction": 'urn:oid:2.16.840.1.113883.3.526.3.1134'
valueset "Ethnicity": 'urn:oid:2.16.840.1.114222.4.11.837'
valueset "Heart Rate": 'urn:oid:2.16.840.1.113883.3.526.3.1176'
valueset "Home Healthcare Services": 'urn:oid:2.16.840.1.113883.3.464.1003.101.12.1016'
valueset "Hypotension": 'urn:oid:2.16.840.1.113883.3.526.3.370'
valueset "Intolerance to Beta Blocker Therapy": 'urn:oid:2.16.840.1.113883.3.526.3.1178'
valueset "Left Ventricular Systolic Dysfunction": 'urn:oid:2.16.840.1.113883.3.526.3.1091'
valueset "Medical Reason": 'urn:oid:2.16.840.1.113883.3.526.3.1007'
valueset "Moderate or Severe": 'urn:oid:2.16.840.1.113883.3.526.3.1092'
valueset "Moderate or Severe LVSD": 'urn:oid:2.16.840.1.113883.3.526.3.1090'
valueset "Myocardial Infarction": 'urn:oid:2.16.840.1.113883.3.526.3.403'
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

define "Face to Face Encounter During Measurement Period":
	( ["Encounter, Performed": "Office Visit"]
		union ["Encounter, Performed": "Outpatient Consultation"]
		union ["Encounter, Performed": "Care Services in Long-Term Residential Facility"]
		union ["Encounter, Performed": "Home Healthcare Services"]
		union ["Encounter, Performed": "Nursing Facility Visit"] ) FaceToFaceEncounter
		where FaceToFaceEncounter.relevantPeriod during "Measurement Period"

define "Qualifying CAD Encounter":
	"Coronary Artery Disease Encounter"
		union "History of Cardiac Surgery"

define "Qualifying CAD Encounter and Moderate or Severe LVSD":
	"Qualifying CAD Encounter" EncounterWithCADProxy
		with ( ( ["Diagnostic Study, Performed": "Ejection Fraction"] EjectionFraction
				where EjectionFraction.result < 40 '%'
		)
			union ["Diagnosis": "Moderate or Severe LVSD"]
			union ( ["Diagnosis": "Left Ventricular Systolic Dysfunction"] LVSD
					where LVSD.severity in "Moderate or Severe"
			) ) LVSDFindings
			such that LVSDFindings.prevalencePeriod starts before 
			end of EncounterWithCADProxy.relevantPeriod
				or LVSDFindings.relevantPeriod starts before 
				end of EncounterWithCADProxy.relevantPeriod

define "Arrhythmia with Qualifying CAD Encounter and Moderate or Severe LVSD":
	["Diagnosis": "Arrhythmia"] Arrhythmia
		with "Qualifying CAD Encounter and Moderate or Severe LVSD" CADEncounterModerateOrSevereLVSD
			such that Arrhythmia.prevalencePeriod overlaps CADEncounterModerateOrSevereLVSD.relevantPeriod

define "Arrhythmia with Qualifying CAD Encounter and Prior MI":
	["Diagnosis": "Arrhythmia"] Arrhythmia
		with "Qualifying CAD Encounter and Prior MI" CADEncounterMI
			such that Arrhythmia.prevalencePeriod overlaps CADEncounterMI.relevantPeriod

define "Asthma with Qualifying CAD Encounter and Moderate or Severe LVSD":
	["Diagnosis": "Asthma"] Asthma
		with "Qualifying CAD Encounter and Moderate or Severe LVSD" CADEncounterModerateOrSevereLVSD
			such that Asthma.prevalencePeriod overlaps CADEncounterModerateOrSevereLVSD.relevantPeriod

define "Asthma with Qualifying CAD Encounter and Prior MI":
	["Diagnosis": "Asthma"] Asthma
		with "Qualifying CAD Encounter and Prior MI" CADEncounterMI
			such that Asthma.prevalencePeriod overlaps CADEncounterMI.relevantPeriod

define "Atrioventricular Block with Qualifying CAD Encounter and Prior MI":
	["Diagnosis": "Atrioventricular Block"] AtrioventricularBlock
		with "Qualifying CAD Encounter and Prior MI" CADEncounterMI
			such that AtrioventricularBlock.prevalencePeriod overlaps CADEncounterMI.relevantPeriod

define "Beta Blocker Therapy for LVSD Ordered":
	["Medication, Order": "Beta Blocker Therapy for LVSD"] BetaBlockerForLVSDOrdered
		with "Qualifying CAD Encounter and Moderate or Severe LVSD" CADEncounterModerateOrSevereLVSD
			such that BetaBlockerForLVSDOrdered.authorDatetime during CADEncounterModerateOrSevereLVSD.relevantPeriod

define "Beta Blocker Therapy Ordered":
	["Medication, Order": "Beta Blocker Therapy"] BetaBlockerOrdered
		with "Qualifying CAD Encounter and Prior MI" CADEncounterMI
			such that BetaBlockerOrdered.authorDatetime during CADEncounterMI.relevantPeriod

define "Bradycardia with Qualifying CAD Encounter and Moderate or Severe LVSD":
	["Diagnosis": "Bradycardia"] Bradycardia
		with "Qualifying CAD Encounter and Moderate or Severe LVSD" CADEncounterModerateOrSevereLVSD
			such that Bradycardia.prevalencePeriod overlaps CADEncounterModerateOrSevereLVSD.relevantPeriod

define "Bradycardia with Qualifying CAD Encounter and Prior MI":
	["Diagnosis": "Bradycardia"] Bradycardia
		with "Qualifying CAD Encounter and Prior MI" CADEncounterMI
			such that Bradycardia.prevalencePeriod overlaps CADEncounterMI.relevantPeriod

define "Hypotension with Qualifying CAD Encounter and Moderate or Severe LVSD":
	["Diagnosis": "Hypotension"] Hypotension
		with "Qualifying CAD Encounter and Moderate or Severe LVSD" CADEncounterModerateOrSevereLVSD
			such that Hypotension.prevalencePeriod overlaps CADEncounterModerateOrSevereLVSD.relevantPeriod

define "Hypotension with Qualifying CAD Encounter and Prior MI":
	["Diagnosis": "Hypotension"] Hypotension
		with "Qualifying CAD Encounter and Prior MI" CADEncounterMI
			such that Hypotension.prevalencePeriod overlaps CADEncounterMI.relevantPeriod

define "Numerator 1":
	exists "Beta Blocker Therapy for LVSD Ordered"
		or exists "Currently Taking Beta Blocker Therapy for LVSD"

define "Numerator 2":
	exists "Beta Blocker Therapy Ordered"
		or exists "Currently Taking Beta Blocker Therapy"

define "Allergy or Intolerance to Beta Blocker Therapy Ingredient with Qualifying CAD Encounter and Moderate or Severe LVSD":
	["Allergy/Intolerance": "Beta Blocker Therapy Ingredient"] BetaBlockerAllergyIntolerance
		with "Qualifying CAD Encounter and Moderate or Severe LVSD" CADEncounterModerateOrSevereLVSD
			such that BetaBlockerAllergyIntolerance.prevalencePeriod overlaps after CADEncounterModerateOrSevereLVSD.relevantPeriod

define "Allergy or Intolerance to Beta Blocker Therapy Ingredient with Qualifying CAD Encounter and Prior MI":
	["Allergy/Intolerance": "Beta Blocker Therapy Ingredient"] BetaBlockerAllergyIntolerance
		with "Qualifying CAD Encounter and Prior MI" CADEncounterMI
			such that BetaBlockerAllergyIntolerance.prevalencePeriod overlaps after CADEncounterMI.relevantPeriod

define "Cardiac Pacer Device Implanted with Qualifying CAD Encounter and Moderate or Severe LVSD":
	["Device, Applied": "Cardiac Pacer"] ImplantedCardiacPacer
		with "Qualifying CAD Encounter and Moderate or Severe LVSD" CADEncounterModerateOrSevereLVSD
			such that ImplantedCardiacPacer.relevantPeriod overlaps after CADEncounterModerateOrSevereLVSD.relevantPeriod

define "Cardiac Pacer Device Implanted with Qualifying CAD Encounter and Prior MI":
	["Device, Applied": "Cardiac Pacer"] ImplantedCardiacPacer
		with "Qualifying CAD Encounter and Prior MI" CADEncounterMI
			such that ImplantedCardiacPacer.relevantPeriod overlaps after CADEncounterMI.relevantPeriod

define "Currently Taking Beta Blocker Therapy":
	["Medication, Active": "Beta Blocker Therapy"] ActiveBetaBlocker
		with "Qualifying CAD Encounter and Prior MI" CADEncounterMI
			such that ActiveBetaBlocker.relevantPeriod overlaps after CADEncounterMI.relevantPeriod

define "Currently Taking Beta Blocker Therapy for LVSD":
	["Medication, Active": "Beta Blocker Therapy for LVSD"] ActiveBetaBlockerForLVSD
		with "Qualifying CAD Encounter and Moderate or Severe LVSD" CADEncounterModerateOrSevereLVSD
			such that ActiveBetaBlockerForLVSD.relevantPeriod overlaps after CADEncounterModerateOrSevereLVSD.relevantPeriod

define "Diagnosis of Allergy to Beta Blocker Therapy with Qualifying CAD Encounter and Moderate or Severe LVSD":
	["Diagnosis": "Allergy to Beta Blocker Therapy"] BetaBlockerAllergyDiagnosis
		with "Qualifying CAD Encounter and Moderate or Severe LVSD" CADEncounterModerateOrSevereLVSD
			such that BetaBlockerAllergyDiagnosis.prevalencePeriod overlaps after CADEncounterModerateOrSevereLVSD.relevantPeriod

define "Diagnosis of Allergy to Beta Blocker Therapy with Qualifying CAD Encounter and Prior MI":
	["Diagnosis": "Allergy to Beta Blocker Therapy"] BetaBlockerAllergyDiagnosis
		with "Qualifying CAD Encounter and Prior MI" CADEncounterMI
			such that BetaBlockerAllergyDiagnosis.prevalencePeriod overlaps after CADEncounterMI.relevantPeriod

define "Diagnosis of Cardiac Pacer in Situ with Qualifying CAD Encounter and Moderate or Severe LVSD":
	["Diagnosis": "Cardiac Pacer in Situ"] CardiacPacerDiagnosis
		with "Qualifying CAD Encounter and Moderate or Severe LVSD" CADEncounterModerateOrSevereLVSD
			such that CardiacPacerDiagnosis.prevalencePeriod overlaps after CADEncounterModerateOrSevereLVSD.relevantPeriod

define "Diagnosis of Cardiac Pacer in Situ with Qualifying CAD Encounter and Prior MI":
	["Diagnosis": "Cardiac Pacer in Situ"] CardiacPacerDiagnosis
		with "Qualifying CAD Encounter and Prior MI" CADEncounterMI
			such that CardiacPacerDiagnosis.prevalencePeriod overlaps after CADEncounterMI.relevantPeriod

define "Diagnosis of Intolerance to Beta Blocker Therapy with Qualifying CAD Encounter and Moderate or Severe LVSD":
	["Diagnosis": "Intolerance to Beta Blocker Therapy"] BetaBlockerIntoleranceDiagnosis
		with "Qualifying CAD Encounter and Moderate or Severe LVSD" CADEncounterModerateOrSevereLVSD
			such that BetaBlockerIntoleranceDiagnosis.prevalencePeriod overlaps after CADEncounterModerateOrSevereLVSD.relevantPeriod

define "Diagnosis of Intolerance to Beta Blocker Therapy with Qualifying CAD Encounter and Prior MI":
	["Diagnosis": "Intolerance to Beta Blocker Therapy"] BetaBlockerIntoleranceDiagnosis
		with "Qualifying CAD Encounter and Prior MI" CADEncounterMI
			such that BetaBlockerIntoleranceDiagnosis.prevalencePeriod overlaps after CADEncounterMI.relevantPeriod

define "Qualifying Encounter":
	( ["Encounter, Performed": "Office Visit"]
		union ["Encounter, Performed": "Outpatient Consultation"]
		union ["Encounter, Performed": "Nursing Facility Visit"]
		union ["Encounter, Performed": "Care Services in Long-Term Residential Facility"]
		union ["Encounter, Performed": "Home Healthcare Services"]
		union ["Encounter, Performed": "Patient Provider Interaction"] ) ValidEncounter
		where ValidEncounter.relevantPeriod during "Measurement Period"

define "Qualifying CAD Encounter and Prior MI":
	"Qualifying CAD Encounter" EncounterWithCADProxy
		with ["Diagnosis": "Myocardial Infarction"] MyocardialInfarction
			such that MyocardialInfarction.prevalencePeriod ends 3 years or less before day of start of EncounterWithCADProxy.relevantPeriod

define "Denominator 1":
	"Initial Population"
		and exists "Qualifying CAD Encounter and Moderate or Severe LVSD"

define "Denominator 2":
	"Initial Population"
		and exists "Qualifying CAD Encounter and Prior MI"

define "Atrioventricular Block with Qualifying CAD Encounter and Prior MI without Cardiac Pacer":
	exists "Atrioventricular Block with Qualifying CAD Encounter and Prior MI"
		and not exists "Diagnosis of Cardiac Pacer in Situ with Qualifying CAD Encounter and Prior MI"
		and not exists "Cardiac Pacer Device Implanted with Qualifying CAD Encounter and Prior MI"

define "Atrioventricular Block with Qualifying CAD Encounter and Moderate or Severe LVSD":
	["Diagnosis": "Atrioventricular Block"] AtrioventricularBlock
		with "Qualifying CAD Encounter and Moderate or Severe LVSD" CADEncounterModerateOrSevereLVSD
			such that AtrioventricularBlock.prevalencePeriod overlaps CADEncounterModerateOrSevereLVSD.relevantPeriod

define "Atrioventricular Block with Qualifying CAD Encounter and Moderate or Severe LVSD without Cardiac Pacer":
	exists "Atrioventricular Block with Qualifying CAD Encounter and Moderate or Severe LVSD"
		and not exists "Diagnosis of Cardiac Pacer in Situ with Qualifying CAD Encounter and Moderate or Severe LVSD"
		and not exists "Cardiac Pacer Device Implanted with Qualifying CAD Encounter and Moderate or Severe LVSD"

define "History of Cardiac Surgery":
	"Face to Face Encounter During Measurement Period" ValidFaceToFaceEncounter
		with ["Procedure, Performed": "Cardiac Surgery"] CardiacSurgery
			such that CardiacSurgery.relevantPeriod starts before 
			end of ValidFaceToFaceEncounter.relevantPeriod

define "Coronary Artery Disease Encounter":
	"Face to Face Encounter During Measurement Period" ValidFaceToFaceEncounter
		with ["Diagnosis": "Coronary Artery Disease No MI"] CoronaryArteryDisease
			such that CoronaryArteryDisease.prevalencePeriod overlaps ValidFaceToFaceEncounter.relevantPeriod

define "Denominator Exceptions 1":
	exists "Arrhythmia with Qualifying CAD Encounter and Moderate or Severe LVSD"
		or exists "Asthma with Qualifying CAD Encounter and Moderate or Severe LVSD"
		or exists "Bradycardia with Qualifying CAD Encounter and Moderate or Severe LVSD"
		or exists "Hypotension with Qualifying CAD Encounter and Moderate or Severe LVSD"
		or exists "Consecutive Heart Rates Less than 50 with Qualifying CAD Encounter and Moderate or Severe LVSD"
		or exists "Allergy or Intolerance to Beta Blocker Therapy Ingredient with Qualifying CAD Encounter and Moderate or Severe LVSD"
		or exists "Diagnosis of Allergy to Beta Blocker Therapy with Qualifying CAD Encounter and Moderate or Severe LVSD"
		or exists "Diagnosis of Intolerance to Beta Blocker Therapy with Qualifying CAD Encounter and Moderate or Severe LVSD"
		or exists "Medical Patient or System Reason for Not Ordering Beta Blocker for LVSD"
		or "Atrioventricular Block with Qualifying CAD Encounter and Moderate or Severe LVSD without Cardiac Pacer"

define "Denominator Exceptions 2":
	exists "Arrhythmia with Qualifying CAD Encounter and Prior MI"
		or exists "Asthma with Qualifying CAD Encounter and Prior MI"
		or exists "Bradycardia with Qualifying CAD Encounter and Prior MI"
		or exists "Hypotension with Qualifying CAD Encounter and Prior MI"
		or exists "Consecutive Heart Rates Less than 50 with Qualifying CAD Encounter and Prior MI"
		or exists "Allergy or Intolerance to Beta Blocker Therapy Ingredient with Qualifying CAD Encounter and Prior MI"
		or exists "Diagnosis of Allergy to Beta Blocker Therapy with Qualifying CAD Encounter and Prior MI"
		or exists "Diagnosis of Intolerance to Beta Blocker Therapy with Qualifying CAD Encounter and Prior MI"
		or exists "Medical Patient or System Reason for Not Ordering Beta Blocker Therapy"
		or "Atrioventricular Block with Qualifying CAD Encounter and Prior MI without Cardiac Pacer"

define "Consecutive Heart Rates Less than 50 with Qualifying CAD Encounter and Moderate or Severe LVSD":
	from
		["Physical Exam, Performed": "Heart Rate"] HeartRate,
		"Qualifying CAD Encounter and Moderate or Severe LVSD" CADEncounterModerateOrSevereLVSD
		let PriorHeartRate: Last(["Physical Exam, Performed": "Heart Rate"] MostRecentPriorHeartRate
				where MostRecentPriorHeartRate.relevantPeriod during CADEncounterModerateOrSevereLVSD.relevantPeriod
					and MostRecentPriorHeartRate.relevantPeriod starts before start of HeartRate.relevantPeriod
				sort by start of relevantPeriod
		)
		where HeartRate.relevantPeriod during CADEncounterModerateOrSevereLVSD.relevantPeriod
			and HeartRate.result < 50 '{Beats}/min'
			and PriorHeartRate.result < 50 '{Beats}/min'

define "Consecutive Heart Rates Less than 50 with Qualifying CAD Encounter and Prior MI":
	from
		["Physical Exam, Performed": "Heart Rate"] HeartRate,
		"Qualifying CAD Encounter and Prior MI" CADEncounterMI
		let PriorHeartRate: Last(["Physical Exam, Performed": "Heart Rate"] MostRecentPriorHeartRate
				where MostRecentPriorHeartRate.relevantPeriod during CADEncounterMI.relevantPeriod
					and MostRecentPriorHeartRate.relevantPeriod starts before start of HeartRate.relevantPeriod
				sort by start of relevantPeriod
		)
		where HeartRate.relevantPeriod during CADEncounterMI.relevantPeriod
			and HeartRate.result < 50 '{Beats}/min'
			and PriorHeartRate.result < 50 '{Beats}/min'

define "Medical Patient or System Reason for Not Ordering Beta Blocker for LVSD":
	["Medication, Not Ordered": "Beta Blocker Therapy for LVSD"] NoBetaBlockerForLVSDOrdered
		with "Qualifying CAD Encounter and Moderate or Severe LVSD" CADEncounterModerateOrSevereLVSD
			such that NoBetaBlockerForLVSDOrdered.authorDatetime during CADEncounterModerateOrSevereLVSD.relevantPeriod
		where ( NoBetaBlockerForLVSDOrdered.negationRationale in "Medical Reason"
				or NoBetaBlockerForLVSDOrdered.negationRationale in "Patient Reason"
				or NoBetaBlockerForLVSDOrdered.negationRationale in "System Reason"
		)

define "Medical Patient or System Reason for Not Ordering Beta Blocker Therapy":
	["Medication, Not Ordered": "Beta Blocker Therapy"] NoBetaBlockerOrdered
		with "Qualifying CAD Encounter and Prior MI" CADEncounterMI
			such that NoBetaBlockerOrdered.authorDatetime during CADEncounterMI.relevantPeriod
		where ( NoBetaBlockerOrdered.negationRationale in "Medical Reason"
				or NoBetaBlockerOrdered.negationRationale in "Patient Reason"
				or NoBetaBlockerOrdered.negationRationale in "System Reason"
		)

define "Initial Population":
	exists ( ["Patient Characteristic Birthdate": "Birth date"] BirthDate
			where Global."CalendarAgeInYearsAt"(BirthDate.birthDatetime, start of "Measurement Period")>= 18
	)
		and Count("Qualifying Encounter")>= 2
		and ( exists "Qualifying CAD Encounter" )