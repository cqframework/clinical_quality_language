library FunctionalStatusAssessmentsforCongestiveHeartFailure version '9.1.000'

using QDM version '5.4'

include MATGlobalCommonFunctions version '4.0.000' called Global
include Hospice version '2.0.000' called Hospice

codesystem "LOINC": 'urn:oid:2.16.840.1.113883.6.1'

valueset "Ethnicity": 'urn:oid:2.16.840.1.114222.4.11.837'
valueset "Heart Failure": 'urn:oid:2.16.840.1.113883.3.526.3.376'
valueset "Office Visit": 'urn:oid:2.16.840.1.113883.3.464.1003.101.12.1001'
valueset "ONC Administrative Sex": 'urn:oid:2.16.840.1.113762.1.4.1'
valueset "Payer": 'urn:oid:2.16.840.1.114222.4.11.3591'
valueset "Race": 'urn:oid:2.16.840.1.114222.4.11.836'
valueset "Severe Dementia": 'urn:oid:2.16.840.1.113883.3.526.3.1025'

code "Birth date": '21112-8' from "LOINC" display 'Birth date'
code "Clinical summary score [KCCQ]": '72188-6' from "LOINC" display 'Clinical summary score [KCCQ]'
code "Emotional score [MLHFQ]": '85609-6' from "LOINC" display 'Emotional score [MLHFQ]'
code "Kansas City Cardiomyopathy Questionnaire - 12 item [KCCQ-12]": '86923-0' from "LOINC" display 'Kansas City Cardiomyopathy Questionnaire - 12 item [KCCQ-12]'
code "Overall summary score [KCCQ-12]": '86924-8' from "LOINC" display 'Overall summary score [KCCQ-12]'
code "Physical limitation score [KCCQ]": '72195-1' from "LOINC" display 'Physical limitation score [KCCQ]'
code "Physical score [MLHFQ]": '85618-7' from "LOINC" display 'Physical score [MLHFQ]'
code "PROMIS-10 Global Mental Health (GMH) score T-score": '71969-0' from "LOINC" display 'PROMIS-10 Global Mental Health (GMH) score T-score'
code "PROMIS-10 Global Physical Health (GPH) score T-score": '71971-6' from "LOINC" display 'PROMIS-10 Global Physical Health (GPH) score T-score'
code "PROMIS-29 Anxiety score T-score": '71967-4' from "LOINC" display 'PROMIS-29 Anxiety score T-score'
code "PROMIS-29 Depression score T-score": '71965-8' from "LOINC" display 'PROMIS-29 Depression score T-score'
code "PROMIS-29 Fatigue score T-score": '71963-3' from "LOINC" display 'PROMIS-29 Fatigue score T-score'
code "PROMIS-29 Pain interference score T-score": '71961-7' from "LOINC" display 'PROMIS-29 Pain interference score T-score'
code "PROMIS-29 Physical function score T-score": '71959-1' from "LOINC" display 'PROMIS-29 Physical function score T-score'
code "PROMIS-29 Satisfaction with participation in social roles score T-score": '71957-5' from "LOINC" display 'PROMIS-29 Satisfaction with participation in social roles score T-score'
code "PROMIS-29 Sleep disturbance score T-score": '71955-9' from "LOINC" display 'PROMIS-29 Sleep disturbance score T-score'
code "Quality of life score [KCCQ]": '72189-4' from "LOINC" display 'Quality of life score [KCCQ]'
code "Self-efficacy score [KCCQ]": '72190-2' from "LOINC" display 'Self-efficacy score [KCCQ]'
code "Social limitation score [KCCQ]": '72196-9' from "LOINC" display 'Social limitation score [KCCQ]'
code "Total symptom score [KCCQ]": '72191-0' from "LOINC" display 'Total symptom score [KCCQ]'
code "VR-12 Mental component summary (MCS) score - oblique method T-score": '72026-8' from "LOINC" display 'VR-12 Mental component summary (MCS) score - oblique method T-score'
code "VR-12 Mental component summary (MCS) score - orthogonal method T-score": '72028-4' from "LOINC" display 'VR-12 Mental component summary (MCS) score - orthogonal method T-score'
code "VR-12 Physical component summary (PCS) score - oblique method T-score": '72025-0' from "LOINC" display 'VR-12 Physical component summary (PCS) score - oblique method T-score'
code "VR-12 Physical component summary (PCS) score - orthogonal method T-score": '72027-6' from "LOINC" display 'VR-12 Physical component summary (PCS) score - orthogonal method T-score'
code "VR-36 Mental component summary (MCS) score - oblique method T-score": '71990-6' from "LOINC" display 'VR-36 Mental component summary (MCS) score - oblique method T-score'
code "VR-36 Mental component summary (MCS) score - orthogonal method T-score": '72008-6' from "LOINC" display 'VR-36 Mental component summary (MCS) score - orthogonal method T-score'
code "VR-36 Physical component summary (PCS) score - oblique method T-score": '71989-8' from "LOINC" display 'VR-36 Physical component summary (PCS) score - oblique method T-score'
code "VR-36 Physical component summary (PCS) score - orthogonal method T-score": '72007-8' from "LOINC" display 'VR-36 Physical component summary (PCS) score - orthogonal method T-score'

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

define "Qualifying Encounters":
	["Encounter, Performed": "Office Visit"] ValidEncounter
		where ValidEncounter.relevantPeriod during "Measurement Period"

define "Denominator Exclusions":
	Hospice."Has Hospice"
		or exists ( ["Diagnosis": "Severe Dementia"] Dementia
				where Dementia.prevalencePeriod overlaps "Measurement Period"
		)

define "Congestive Heart Failure Followup Encounter":
	"Qualifying Encounters" OfficeVisit
		with "Initial Encounter" CHFInitial
			such that OfficeVisit.relevantPeriod starts 30 days or more after day of 
			end of CHFInitial.relevantPeriod
				and OfficeVisit.relevantPeriod starts 180 days or less after day of 
				end of CHFInitial.relevantPeriod

define "KCCQ Total Assessment Scores":
	["Assessment, Performed": "Quality of life score [KCCQ]"] KCCQLifeQuality
		with ["Assessment, Performed": "Clinical summary score [KCCQ]"] KCCQClinical
			such that KCCQLifeQuality.authorDatetime same day as KCCQClinical.authorDatetime
				and KCCQClinical.result is not null
		with ["Assessment, Performed": "Self-efficacy score [KCCQ]"] KCCQSelfEfficacy
			such that KCCQLifeQuality.authorDatetime same day as KCCQSelfEfficacy.authorDatetime
				and KCCQSelfEfficacy.result is not null
		with ["Assessment, Performed": "Total symptom score [KCCQ]"] KCCQSymptoms
			such that KCCQLifeQuality.authorDatetime same day as KCCQSymptoms.authorDatetime
				and KCCQSymptoms.result is not null
		with ["Assessment, Performed": "Physical limitation score [KCCQ]"] KCCQPhysicalLimits
			such that KCCQLifeQuality.authorDatetime same day as KCCQPhysicalLimits.authorDatetime
				and KCCQPhysicalLimits.result is not null
		with ["Assessment, Performed": "Social limitation score [KCCQ]"] KCCQSocialLimits
			such that KCCQLifeQuality.authorDatetime same day as KCCQSocialLimits.authorDatetime
				and KCCQSocialLimits.result is not null
		where KCCQLifeQuality.result is not null

define "KCCQ12 Total Assessment Scores":
	["Assessment, Performed": "Kansas City Cardiomyopathy Questionnaire - 12 item [KCCQ-12]"] KCCQ
		with ["Assessment, Performed": "Overall summary score [KCCQ-12]"] KCCQSum
			such that KCCQ.authorDatetime same day as KCCQSum.authorDatetime
				and KCCQSum.result is not null
				and KCCQ.result is not null

define "MLHFQ Total Assessment Scores":
	["Assessment, Performed": "Physical score [MLHFQ]"] MLHFQPhysical
		with ["Assessment, Performed": "Emotional score [MLHFQ]"] MLHFQEmotional
			such that MLHFQPhysical.authorDatetime same day as MLHFQEmotional.authorDatetime
				and MLHFQEmotional.result is not null
				and MLHFQPhysical.result is not null

define "PROMIS10 Total Assessment Scores":
	["Assessment, Performed": "PROMIS-10 Global Mental Health (GMH) score T-score"] PROMIS10MentalScore
		with ["Assessment, Performed": "PROMIS-10 Global Physical Health (GPH) score T-score"] PROMIS10PhysicalScore
			such that PROMIS10MentalScore.authorDatetime same day as PROMIS10PhysicalScore.authorDatetime
				and PROMIS10PhysicalScore.result is not null
		where PROMIS10MentalScore.result is not null

define "PROMIS29 Total Assessment Scores":
	["Assessment, Performed": "PROMIS-29 Sleep disturbance score T-score"] Promis29Sleep
		with ["Assessment, Performed": "PROMIS-29 Satisfaction with participation in social roles score T-score"] Promis29SocialRoles
			such that Promis29Sleep.authorDatetime same day as Promis29SocialRoles.authorDatetime
				and Promis29SocialRoles.result is not null
		with ["Assessment, Performed": "PROMIS-29 Physical function score T-score"] Promis29Physical
			such that Promis29Sleep.authorDatetime same day as Promis29Physical.authorDatetime
				and Promis29Physical.result is not null
		with ["Assessment, Performed": "PROMIS-29 Pain interference score T-score"] Promis29Pain
			such that Promis29Sleep.authorDatetime same day as Promis29Pain.authorDatetime
				and Promis29Pain.result is not null
		with ["Assessment, Performed": "PROMIS-29 Fatigue score T-score"] Promis29Fatigue
			such that Promis29Sleep.authorDatetime same day as Promis29Fatigue.authorDatetime
				and Promis29Fatigue.result is not null
		with ["Assessment, Performed": "PROMIS-29 Depression score T-score"] Promis29Depression
			such that Promis29Sleep.authorDatetime same day as Promis29Depression.authorDatetime
				and Promis29Depression.result is not null
		with ["Assessment, Performed": "PROMIS-29 Anxiety score T-score"] Promis29Anxiety
			such that Promis29Sleep.authorDatetime same day as Promis29Anxiety.authorDatetime
				and Promis29Anxiety.result is not null
		where Promis29Sleep.result is not null

define "VR12 Oblique Total Assessment Scores":
	( ["Assessment, Performed": "VR-12 Mental component summary (MCS) score - oblique method T-score"] VR12MentalAssessment
		where VR12MentalAssessment.authorDatetime during "Measurement Period"
			and VR12MentalAssessment.result is not null ) VR12MentalScore
		with ( ["Assessment, Performed": "VR-12 Physical component summary (PCS) score - oblique method T-score"] VR12PhysicalAssessment
			where VR12PhysicalAssessment.authorDatetime during "Measurement Period"
				and VR12PhysicalAssessment.result is not null ) VR12PhysicalScore
			such that VR12MentalScore.authorDatetime same day as VR12PhysicalScore.authorDatetime

define "VR12 Orthogonal Total Assessment Scores":
	( ["Assessment, Performed": "VR-12 Mental component summary (MCS) score - orthogonal method T-score"] VR12MentalAssessment
		where VR12MentalAssessment.authorDatetime during "Measurement Period"
			and VR12MentalAssessment.result is not null ) VR12MentalScore
		with ( ["Assessment, Performed": "VR-12 Physical component summary (PCS) score - orthogonal method T-score"] VR12PhysicalAssessment
			where VR12PhysicalAssessment.authorDatetime during "Measurement Period"
				and VR12PhysicalAssessment.result is not null ) VR12PhysicalScore
			such that VR12MentalScore.authorDatetime same day as VR12PhysicalScore.authorDatetime

define "VR36 Oblique Total Assessment Scores":
	( ["Assessment, Performed": "VR-36 Mental component summary (MCS) score - oblique method T-score"] VR12MentalAssessment
		where VR12MentalAssessment.authorDatetime during "Measurement Period"
			and VR12MentalAssessment.result is not null ) VR12MentalScore
		with ( ["Assessment, Performed": "VR-36 Physical component summary (PCS) score - oblique method T-score"] VR12PhysicalAssessment
			where VR12PhysicalAssessment.authorDatetime during "Measurement Period"
				and VR12PhysicalAssessment.result is not null ) VR12PhysicalScore
			such that VR12MentalScore.authorDatetime same day as VR12PhysicalScore.authorDatetime

define "VR36 Orthogonal Total Assessment Scores":
	( ["Assessment, Performed": "VR-36 Mental component summary (MCS) score - orthogonal method T-score"] VR12MentalAssessment
		where VR12MentalAssessment.authorDatetime during "Measurement Period"
			and VR12MentalAssessment.result is not null ) VR12MentalScore
		with ( ["Assessment, Performed": "VR-36 Physical component summary (PCS) score - orthogonal method T-score"] VR12PhysicalAssessment
			where VR12PhysicalAssessment.authorDatetime during "Measurement Period"
				and VR12PhysicalAssessment.result is not null ) VR12PhysicalScore
			such that VR12MentalScore.authorDatetime same day as VR12PhysicalScore.authorDatetime

define "Initial Encounter":
	First("Qualifying Encounters" QualifyingEncounter
			where QualifyingEncounter.relevantPeriod ends 185 days or less after start of "Measurement Period"
			sort by start of relevantPeriod
	)

define "Assessed Initial and Followup VR36 Orthogonal":
	"Initial Encounter" Encounter
		with "VR36 Orthogonal Total Assessment Scores" InitialVR36Orthogonal
			such that InitialVR36Orthogonal.authorDatetime 14 days or less on or before Encounter.relevantPeriod
		with "VR36 Orthogonal Total Assessment Scores" FollowupVR36Orthogonal
			such that FollowupVR36Orthogonal.authorDatetime 30 days or more after day of 
			end of Encounter.relevantPeriod
				and FollowupVR36Orthogonal.authorDatetime 180 days or less after day of 
				end of Encounter.relevantPeriod

define "Assessed Initial and Followup MLHFQ":
	"Initial Encounter" Encounter
		with "MLHFQ Total Assessment Scores" InitialMLHFQ
			such that InitialMLHFQ.authorDatetime 14 days or less on or before Encounter.relevantPeriod
		with "MLHFQ Total Assessment Scores" FollowupMLHFQ
			such that FollowupMLHFQ.authorDatetime 30 days or more after day of 
			end of Encounter.relevantPeriod
				and FollowupMLHFQ.authorDatetime 180 days or less after day of 
				end of Encounter.relevantPeriod

define "Assessed Initial and Followup KCCQ":
	"Initial Encounter" Encounter
		with "KCCQ Total Assessment Scores" InitialKCCQ
			such that InitialKCCQ.authorDatetime 14 days or less on or before Encounter.relevantPeriod
		with "KCCQ Total Assessment Scores" FollowupKCCQ
			such that FollowupKCCQ.authorDatetime 30 days or more after day of 
			end of Encounter.relevantPeriod
				and FollowupKCCQ.authorDatetime 180 days or less after day of 
				end of Encounter.relevantPeriod

define "Assessed Initial and Followup KCCQ12":
	"Initial Encounter" Encounter
		with "KCCQ12 Total Assessment Scores" InitialKCCQ12
			such that InitialKCCQ12.authorDatetime 14 days or less on or before Encounter.relevantPeriod
		with "KCCQ12 Total Assessment Scores" FollowupKCCQ12
			such that FollowupKCCQ12.authorDatetime 30 days or more after day of 
			end of Encounter.relevantPeriod
				and FollowupKCCQ12.authorDatetime 180 days or less after day of 
				end of Encounter.relevantPeriod

define "Assessed Initial and Followup PROMIS10":
	"Initial Encounter" Encounter
		with "PROMIS10 Total Assessment Scores" InitialPROMIS10
			such that InitialPROMIS10.authorDatetime 14 days or less on or before Encounter.relevantPeriod
		with "PROMIS10 Total Assessment Scores" FollowupPROMIS10
			such that FollowupPROMIS10.authorDatetime 30 days or more after day of 
			end of Encounter.relevantPeriod
				and FollowupPROMIS10.authorDatetime 180 days or less after day of 
				end of Encounter.relevantPeriod

define "Assessed Initial and Followup PROMIS29":
	"Initial Encounter" Encounter
		with "PROMIS29 Total Assessment Scores" InitialPROMIS29
			such that InitialPROMIS29.authorDatetime 14 days or less on or before Encounter.relevantPeriod
		with "PROMIS29 Total Assessment Scores" FollowupPROMIS29
			such that FollowupPROMIS29.authorDatetime 30 days or more after day of 
			end of Encounter.relevantPeriod
				and FollowupPROMIS29.authorDatetime 180 days or less after day of 
				end of Encounter.relevantPeriod

define "Assessed Initial and Followup VR12 Oblique":
	"Initial Encounter" Encounter
		with "VR12 Oblique Total Assessment Scores" InitialVR12Oblique
			such that InitialVR12Oblique.authorDatetime 14 days or less on or before Encounter.relevantPeriod
		with "VR12 Oblique Total Assessment Scores" FollowupVR12Oblique
			such that FollowupVR12Oblique.authorDatetime 30 days or more after day of 
			end of Encounter.relevantPeriod
				and FollowupVR12Oblique.authorDatetime 180 days or less after day of 
				end of Encounter.relevantPeriod

define "Assessed Initial and Followup VR12 Orthogonal":
	"Initial Encounter" Encounter
		with "VR12 Orthogonal Total Assessment Scores" InitialVR12Orthogonal
			such that InitialVR12Orthogonal.authorDatetime 14 days or less on or before Encounter.relevantPeriod
		with "VR12 Orthogonal Total Assessment Scores" FollowupVR12Orthogonal
			such that FollowupVR12Orthogonal.authorDatetime 30 days or more after day of 
			end of Encounter.relevantPeriod
				and FollowupVR12Orthogonal.authorDatetime 180 days or less after day of 
				end of Encounter.relevantPeriod

define "Assessed Initial and Followup VR36 Oblique":
	"Initial Encounter" Encounter
		with "VR36 Oblique Total Assessment Scores" InitialVR36Oblique
			such that InitialVR36Oblique.authorDatetime 14 days or less on or before Encounter.relevantPeriod
		with "VR36 Oblique Total Assessment Scores" FollowupVR36Oblique
			such that FollowupVR36Oblique.authorDatetime 30 days or more after day of 
			end of Encounter.relevantPeriod
				and FollowupVR36Oblique.authorDatetime 180 days or less after day of 
				end of Encounter.relevantPeriod

define "Numerator":
	( "Assessed Initial and Followup PROMIS10" ) is not null
		or ( "Assessed Initial and Followup PROMIS29" ) is not null
		or ( "Assessed Initial and Followup VR12 Oblique" ) is not null
		or ( "Assessed Initial and Followup VR12 Orthogonal" ) is not null
		or ( "Assessed Initial and Followup VR36 Oblique" ) is not null
		or ( "Assessed Initial and Followup VR36 Orthogonal" ) is not null
		or ( "Assessed Initial and Followup MLHFQ" ) is not null
		or ( "Assessed Initial and Followup KCCQ12" ) is not null
		or ( "Assessed Initial and Followup KCCQ" ) is not null

define "Initial Population":
	exists ( ["Patient Characteristic Birthdate": "Birth date"] BirthDate
			where Global."CalendarAgeInYearsAt"(BirthDate.birthDatetime, start of "Measurement Period")>= 18
	)
		and exists ( ["Diagnosis": "Heart Failure"] HeartFailure
				where HeartFailure.prevalencePeriod overlaps "Measurement Period"
		)
		and exists ( "Congestive Heart Failure Followup Encounter" )