library FunctionalStatusAssessmentforTotalKneeReplacement version '8.3.000'

using QDM version '5.4'

include MATGlobalCommonFunctions version '4.0.000' called Global
include Hospice version '2.0.000' called Hospice

codesystem "LOINC": 'urn:oid:2.16.840.1.113883.6.1'
codesystem "CPT": 'urn:oid:2.16.840.1.113883.6.12'
codesystem "SNOMEDCT": 'urn:oid:2.16.840.1.113883.6.96'

valueset "Ethnicity": 'urn:oid:2.16.840.1.114222.4.11.837'
valueset "Fracture - Lower Body": 'urn:oid:2.16.840.1.113883.3.464.1003.113.12.1037'
valueset "Knee Injury and Osteoarthritis Outcome Score for Joint Replacement [KOOSJR]": 'urn:oid:2.16.840.1.113883.3.464.1003.118.12.1218'
valueset "Office Visit": 'urn:oid:2.16.840.1.113883.3.464.1003.101.12.1001'
valueset "ONC Administrative Sex": 'urn:oid:2.16.840.1.113762.1.4.1'
valueset "Outpatient Consultation": 'urn:oid:2.16.840.1.113883.3.464.1003.101.12.1008'
valueset "Payer": 'urn:oid:2.16.840.1.114222.4.11.3591'
valueset "Primary TKA Procedure": 'urn:oid:2.16.840.1.113883.3.464.1003.198.12.1007'
valueset "Race": 'urn:oid:2.16.840.1.114222.4.11.836'

code "Activities of daily living score [KOOS]": '72100-1' from "LOINC" display 'Activities of daily living score [KOOS]'
code "Birth date": '21112-8' from "LOINC" display 'Birth date'
code "Pain score [KOOS]": '72102-7' from "LOINC" display 'Pain score [KOOS]'
code "Postoperative follow-up visit, normally included in the surgical package, to indicate that an evaluation and management service was performed during a postoperative period for a reason(s) related to the original procedure": '99024' from "CPT" display 'Postoperative follow-up visit, normally included in the surgical package, to indicate that an evaluation and management service was performed during a postoperative period for a reason(s) related to the original procedure'
code "PROMIS-10 Global Mental Health (GMH) score T-score": '71969-0' from "LOINC" display 'PROMIS-10 Global Mental Health (GMH) score T-score'
code "PROMIS-10 Global Physical Health (GPH) score T-score": '71971-6' from "LOINC" display 'PROMIS-10 Global Physical Health (GPH) score T-score'
code "Quality of life score [KOOS]": '72098-7' from "LOINC" display 'Quality of life score [KOOS]'
code "Severe dementia (disorder)": '428351000124105' from "SNOMEDCT" display 'Severe dementia (disorder)'
code "Sport-recreation score [KOOS]": '72099-5' from "LOINC" display 'Sport-recreation score [KOOS]'
code "Symptoms score [KOOS]": '72101-9' from "LOINC" display 'Symptoms score [KOOS]'
code "VR-12 Mental component summary (MCS) score - oblique method T-score": '72026-8' from "LOINC" display 'VR-12 Mental component summary (MCS) score - oblique method T-score'
code "VR-12 Mental component summary (MCS) score - orthogonal method T-score": '72028-4' from "LOINC" display 'VR-12 Mental component summary (MCS) score - orthogonal method T-score'
code "VR-12 Physical component summary (PCS) score - oblique method T-score": '72025-0' from "LOINC" display 'VR-12 Physical component summary (PCS) score - oblique method T-score'
code "VR-12 Physical component summary (PCS) score - orthogonal method T-score": '72027-6' from "LOINC" display 'VR-12 Physical component summary (PCS) score - orthogonal method T-score'

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

define "Total Knee Arthroplasty Procedure":
	["Procedure, Performed": "Primary TKA Procedure"] TKAProcedure
		where TKAProcedure.relevantPeriod starts 12 months or less before start of "Measurement Period"

define "Dementia Diagnosis":
	["Diagnosis": "Severe dementia (disorder)"] Dementia
		where Dementia.prevalencePeriod overlaps "Measurement Period"

define "Denominator":
	"Initial Population"

define "Total Knee Arthroplasty with 2 or More Lower Body Fractures":
	"Total Knee Arthroplasty Procedure" TKAProcedure
		where Count(["Diagnosis": "Fracture - Lower Body"] LowerBodyFracture
				where LowerBodyFracture.prevalencePeriod starts less than 1 day on or before TKAProcedure.relevantPeriod
		)>= 2

define "Denominator Exclusions":
	Hospice."Has Hospice"
		or exists "Total Knee Arthroplasty with 2 or More Lower Body Fractures"
		or exists "Dementia Diagnosis"

define "KOOS Total Assessment Scores":
	["Assessment, Performed": "Quality of life score [KOOS]"] KOOSLifeQuality
		with ["Assessment, Performed": "Sport-recreation score [KOOS]"] KOOSSport
			such that KOOSLifeQuality.authorDatetime same day as KOOSSport.authorDatetime
				and KOOSSport.result is not null
		with ["Assessment, Performed": "Activities of daily living score [KOOS]"] KOOSActivityScore
			such that KOOSLifeQuality.authorDatetime same day as KOOSActivityScore.authorDatetime
				and KOOSActivityScore.result is not null
		with ["Assessment, Performed": "Symptoms score [KOOS]"] KOOSSymptoms
			such that KOOSLifeQuality.authorDatetime same day as KOOSSymptoms.authorDatetime
				and KOOSSymptoms.result is not null
		with ["Assessment, Performed": "Pain score [KOOS]"] KOOSPain
			such that KOOSLifeQuality.authorDatetime same day as KOOSPain.authorDatetime
				and KOOSPain.result is not null
		where KOOSLifeQuality.result is not null

define "PROMIS10 Total Assessment Scores":
	["Assessment, Performed": "PROMIS-10 Global Mental Health (GMH) score T-score"] Promis10MentalScore
		with ["Assessment, Performed": "PROMIS-10 Global Physical Health (GPH) score T-score"] Promis10PhysicalScore
			such that Promis10MentalScore.authorDatetime same day as Promis10PhysicalScore.authorDatetime
				and Promis10PhysicalScore.result is not null
		where Promis10MentalScore.result is not null

define "KOOSJr Total Assessment Scores":
	( ["Assessment, Performed": "Knee Injury and Osteoarthritis Outcome Score for Joint Replacement [KOOSJR]"] KOOSJr
			where KOOSJr.result is not null
	)

define "Qualifying Encounters":
	( ["Encounter, Performed": "Outpatient Consultation"]
		union ["Encounter, Performed": "Office Visit"]
		union ["Encounter, Performed": "Postoperative follow-up visit, normally included in the surgical package, to indicate that an evaluation and management service was performed during a postoperative period for a reason(s) related to the original procedure"] ) ValidEncounters
		where ValidEncounters.relevantPeriod during "Measurement Period"

define "Assessed Initial and Follow up KOOS with TKA":
	"Total Knee Arthroplasty Procedure" TotalKnee
		with "KOOS Total Assessment Scores" InitialKneeAssessmentKOOS
			such that InitialKneeAssessmentKOOS.authorDatetime 90 days or less on or before start of TotalKnee.relevantPeriod
		with "KOOS Total Assessment Scores" FollowupKneeAssessmentKOOS
			such that FollowupKneeAssessmentKOOS.authorDatetime between 
			end of TotalKnee.relevantPeriod + 270 days and 
			end of TotalKnee.relevantPeriod + 365 days

define "Assessed Initial and Follow up KOOSJr with TKA":
	"Total Knee Arthroplasty Procedure" TotalKnee
		with "KOOSJr Total Assessment Scores" InitialKneeAssessmentKOOSJr
			such that InitialKneeAssessmentKOOSJr.authorDatetime 90 days or less on or before start of TotalKnee.relevantPeriod
		with "KOOSJr Total Assessment Scores" FollowupKneeAssessmentKOOSJr
			such that FollowupKneeAssessmentKOOSJr.authorDatetime between 
			end of TotalKnee.relevantPeriod + 270 days and 
			end of TotalKnee.relevantPeriod + 365 days

define "VR12 Oblique Total Assessment Scores":
	( ["Assessment, Performed": "VR-12 Mental component summary (MCS) score - oblique method T-score"] VR12Mental
		where VR12Mental.result is not null ) VR12MentalScore
		with ( ["Assessment, Performed": "VR-12 Physical component summary (PCS) score - oblique method T-score"] VR12PhysicalAssessment
			where VR12PhysicalAssessment.result is not null ) VR12PhysicalScore
			such that VR12MentalScore.authorDatetime same day as VR12PhysicalScore.authorDatetime

define "VR12 Orthogonal Total Assessment Scores":
	( ["Assessment, Performed": "VR-12 Mental component summary (MCS) score - orthogonal method T-score"] VR12MentalAssessment
		where VR12MentalAssessment.result is not null ) VR12MentalScore
		with ( ["Assessment, Performed": "VR-12 Physical component summary (PCS) score - orthogonal method T-score"] VR12PhysicalAssessment
			where VR12PhysicalAssessment.result is not null ) VR12PhysicalScore
			such that VR12MentalScore.authorDatetime same day as VR12PhysicalScore.authorDatetime

define "Initial Population":
	exists ( "Total Knee Arthroplasty Procedure" )
		and exists ( "Qualifying Encounters" )
		and exists ( ["Patient Characteristic Birthdate": "Birth date"] BirthDate
				where Global."CalendarAgeInYearsAt"(BirthDate.birthDatetime, start of "Measurement Period")>= 19
		)

define "Assessed Initial and Follow up VR12 Orthogonal with TKA":
	"Total Knee Arthroplasty Procedure" TotalKnee
		with "VR12 Orthogonal Total Assessment Scores" InitialKneeAssessmentOrthogonal
			such that InitialKneeAssessmentOrthogonal.authorDatetime 90 days or less on or before start of TotalKnee.relevantPeriod
		with "VR12 Orthogonal Total Assessment Scores" FollowupKneeAssessmentOrthogonal
			such that FollowupKneeAssessmentOrthogonal.authorDatetime between 
			end of TotalKnee.relevantPeriod + 270 days and 
			end of TotalKnee.relevantPeriod + 365 days

define "Assessed Initial and Follow up PROMIS10 with TKA":
	"Total Knee Arthroplasty Procedure" TotalKnee
		with "PROMIS10 Total Assessment Scores" InitialKneeAssessmentPROMIS10
			such that InitialKneeAssessmentPROMIS10.authorDatetime 90 days or less on or before start of TotalKnee.relevantPeriod
		with "PROMIS10 Total Assessment Scores" FollowupKneeAssessmentPROMIS10
			such that FollowupKneeAssessmentPROMIS10.authorDatetime between 
			end of TotalKnee.relevantPeriod + 270 days and 
			end of TotalKnee.relevantPeriod + 365 days

define "Assessed Initial and Follow up VR12 Oblique with TKA":
	"Total Knee Arthroplasty Procedure" TotalKnee
		with "VR12 Oblique Total Assessment Scores" InitialKneeAssessmentOblique
			such that InitialKneeAssessmentOblique.authorDatetime 90 days or less on or before start of TotalKnee.relevantPeriod
		with "VR12 Oblique Total Assessment Scores" FollowupKneeAssessmentOblique
			such that FollowupKneeAssessmentOblique.authorDatetime between 
			end of TotalKnee.relevantPeriod + 270 days and 
			end of TotalKnee.relevantPeriod + 365 days

define "Numerator":
	exists ( "Assessed Initial and Follow up KOOS with TKA" )
		or exists ( "Assessed Initial and Follow up KOOSJr with TKA" )
		or exists ( "Assessed Initial and Follow up PROMIS10 with TKA" )
		or exists ( "Assessed Initial and Follow up VR12 Oblique with TKA" )
		or exists ( "Assessed Initial and Follow up VR12 Orthogonal with TKA" )