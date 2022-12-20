library FunctionalStatusAssessmentforTotalHipReplacement version '8.1.000'

using QDM version '5.4'

include MATGlobalCommonFunctions version '4.0.000' called Global
include Hospice version '2.0.000' called Hospice

codesystem "LOINC": 'urn:oid:2.16.840.1.113883.6.1'
codesystem "CPT": 'urn:oid:2.16.840.1.113883.6.12'
codesystem "SNOMEDCT": 'urn:oid:2.16.840.1.113883.6.96'

valueset "Ethnicity": 'urn:oid:2.16.840.1.114222.4.11.837'
valueset "Fracture - Lower Body": 'urn:oid:2.16.840.1.113883.3.464.1003.113.12.1037'
valueset "Hip Dysfunction and Osteoarthritis Outcome Score for Joint Replacement [HOOSJR]": 'urn:oid:2.16.840.1.113883.3.464.1003.118.12.1210'
valueset "Office Visit": 'urn:oid:2.16.840.1.113883.3.464.1003.101.12.1001'
valueset "ONC Administrative Sex": 'urn:oid:2.16.840.1.113762.1.4.1'
valueset "Outpatient Consultation": 'urn:oid:2.16.840.1.113883.3.464.1003.101.12.1008'
valueset "Payer": 'urn:oid:2.16.840.1.114222.4.11.3591'
valueset "Primary THA Procedure": 'urn:oid:2.16.840.1.113883.3.464.1003.198.12.1006'
valueset "Race": 'urn:oid:2.16.840.1.114222.4.11.836'

code "Activities of daily living score [HOOS]": '72095-3' from "LOINC" display 'Activities of daily living score [HOOS]'
code "Birth date": '21112-8' from "LOINC" display 'Birth date'
code "Pain score [HOOS]": '72097-9' from "LOINC" display 'Pain score [HOOS]'
code "Postoperative follow-up visit, normally included in the surgical package, to indicate that an evaluation and management service was performed during a postoperative period for a reason(s) related to the original procedure": '99024' from "CPT" display 'Postoperative follow-up visit, normally included in the surgical package, to indicate that an evaluation and management service was performed during a postoperative period for a reason(s) related to the original procedure'
code "PROMIS-10 Global Mental Health (GMH) score T-score": '71969-0' from "LOINC" display 'PROMIS-10 Global Mental Health (GMH) score T-score'
code "PROMIS-10 Global Physical Health (GPH) score T-score": '71971-6' from "LOINC" display 'PROMIS-10 Global Physical Health (GPH) score T-score'
code "Quality of life score [HOOS]": '72093-8' from "LOINC" display 'Quality of life score [HOOS]'
code "Severe dementia (disorder)": '428351000124105' from "SNOMEDCT" display 'Severe dementia (disorder)'
code "Sport-recreation score [HOOS]": '72094-6' from "LOINC" display 'Sport-recreation score [HOOS]'
code "Symptoms score [HOOS]": '72096-1' from "LOINC" display 'Symptoms score [HOOS]'
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

define "Denominator":
	"Initial Population"

define "Dementia Diagnosis":
	["Diagnosis": "Severe dementia (disorder)"] Dementia
		where Dementia.prevalencePeriod overlaps "Measurement Period"

define "Total Hip Arthroplasty with 2 or More Lower Body Fractures":
	"Total Hip Arthroplasty Procedure" THAProcedure
		where Count(["Diagnosis": "Fracture - Lower Body"] LowerBodyFracture
				where LowerBodyFracture.prevalencePeriod starts less than 1 day on or before THAProcedure.relevantPeriod
		)>= 2

define "Denominator Exclusions":
	Hospice."Has Hospice"
		or exists ( "Total Hip Arthroplasty with 2 or More Lower Body Fractures" )
		or exists ( "Dementia Diagnosis" )

define "Total Hip Arthroplasty Procedure":
	( ["Procedure, Performed": "Primary THA Procedure"] THAProcedure
			where THAProcedure.relevantPeriod starts 12 months or less before start of "Measurement Period"
	)

define "HOOS Total Assessment Scores":
	["Assessment, Performed": "Quality of life score [HOOS]"] HOOSLifeQuality
		with ["Assessment, Performed": "Sport-recreation score [HOOS]"] HOOSSport
			such that HOOSLifeQuality.authorDatetime same day as HOOSSport.authorDatetime
				and HOOSSport.result is not null
		with ["Assessment, Performed": "Activities of daily living score [HOOS]"] HOOSActivityScore
			such that HOOSLifeQuality.authorDatetime same day as HOOSActivityScore.authorDatetime
				and HOOSActivityScore.result is not null
		with ["Assessment, Performed": "Symptoms score [HOOS]"] HOOSSymptoms
			such that HOOSLifeQuality.authorDatetime same day as HOOSSymptoms.authorDatetime
				and HOOSSymptoms.result is not null
		with ["Assessment, Performed": "Pain score [HOOS]"] HOOSPain
			such that HOOSLifeQuality.authorDatetime same day as HOOSPain.authorDatetime
				and HOOSPain.result is not null
		where HOOSLifeQuality.result is not null

define "HOOSJr Total Assessment Scores":
	( ["Assessment, Performed": "Hip Dysfunction and Osteoarthritis Outcome Score for Joint Replacement [HOOSJR]"] HOOSJr
			where HOOSJr.result is not null
	)

define "PROMIS10 Total Assessment Scores":
	["Assessment, Performed": "PROMIS-10 Global Mental Health (GMH) score T-score"] Promis10MentalScore
		with ["Assessment, Performed": "PROMIS-10 Global Physical Health (GPH) score T-score"] Promis10PhysicalScore
			such that Promis10MentalScore.authorDatetime same day as Promis10PhysicalScore.authorDatetime
				and Promis10PhysicalScore.result is not null
		where Promis10MentalScore.result is not null

define "Qualifying Encounter":
	( ["Encounter, Performed": "Outpatient Consultation"]
		union ["Encounter, Performed": "Office Visit"]
		union ["Encounter, Performed": "Postoperative follow-up visit, normally included in the surgical package, to indicate that an evaluation and management service was performed during a postoperative period for a reason(s) related to the original procedure"] ) ValidEncounters
		where ValidEncounters.relevantPeriod during "Measurement Period"

define "Assessed Initial and Followup HOOSJr with THA":
	"Total Hip Arthroplasty Procedure" TotalHip
		with "HOOSJr Total Assessment Scores" InitialHipAssessment
			such that TotalHip.relevantPeriod starts 90 days or less on or after day of InitialHipAssessment.authorDatetime
		with "HOOSJr Total Assessment Scores" FollowupHipAssessment
			such that FollowupHipAssessment.authorDatetime 270 days or more after day of 
			end of TotalHip.relevantPeriod
				and FollowupHipAssessment.authorDatetime 365 days or less after day of 
				end of TotalHip.relevantPeriod

define "Assessed Initial and Followup HOOS with THA":
	"Total Hip Arthroplasty Procedure" TotalHip
		with "HOOS Total Assessment Scores" InitialHipAssessmentHOOS
			such that TotalHip.relevantPeriod starts 90 days or less on or after day of InitialHipAssessmentHOOS.authorDatetime
		with "HOOS Total Assessment Scores" FollowupHipAssessmentHOOS
			such that FollowupHipAssessmentHOOS.authorDatetime 270 days or more after day of 
			end of TotalHip.relevantPeriod
				and FollowupHipAssessmentHOOS.authorDatetime 365 days or less after day of 
				end of TotalHip.relevantPeriod

define "Assessed Initial and Followup PROMIS10 with THA":
	"Total Hip Arthroplasty Procedure" TotalHip
		with "PROMIS10 Total Assessment Scores" InitialHipAssessmentPROMIS10
			such that TotalHip.relevantPeriod starts 90 days or less on or after day of InitialHipAssessmentPROMIS10.authorDatetime
		with "PROMIS10 Total Assessment Scores" FollowupHipAssessmentPROMIS10
			such that FollowupHipAssessmentPROMIS10.authorDatetime 270 days or more after day of 
			end of TotalHip.relevantPeriod
				and FollowupHipAssessmentPROMIS10.authorDatetime 365 days or less after day of 
				end of TotalHip.relevantPeriod

define "Assessed Initial and Followup VR12 Oblique with THA":
	"Total Hip Arthroplasty Procedure" TotalHip
		with "VR12 Oblique Total Assessment Scores" InitialHipAssessmentOblique
			such that TotalHip.relevantPeriod starts 90 days or less on or after day of InitialHipAssessmentOblique.authorDatetime
		with "VR12 Oblique Total Assessment Scores" FollowupHipAssessmentOblique
			such that FollowupHipAssessmentOblique.authorDatetime 270 days or more after day of 
			end of TotalHip.relevantPeriod
				and FollowupHipAssessmentOblique.authorDatetime 365 days or less after day of 
				end of TotalHip.relevantPeriod

define "Assessed Initial and Followup VR12 Orthogonal with THA":
	"Total Hip Arthroplasty Procedure" TotalHip
		with "VR12 Orthogonal Total Assessment Scores" InitialHipAssessmentOrthogonal
			such that TotalHip.relevantPeriod starts 90 days or less on or after day of InitialHipAssessmentOrthogonal.authorDatetime
		with "VR12 Orthogonal Total Assessment Scores" FollowupHipAssessmentOrthogonal
			such that FollowupHipAssessmentOrthogonal.authorDatetime 270 days or more after day of 
			end of TotalHip.relevantPeriod
				and FollowupHipAssessmentOrthogonal.authorDatetime 365 days or less after day of 
				end of TotalHip.relevantPeriod

define "Numerator":
	exists ( "Assessed Initial and Followup HOOS with THA" )
		or exists ( "Assessed Initial and Followup HOOSJr with THA" )
		or exists ( "Assessed Initial and Followup PROMIS10 with THA" )
		or exists ( "Assessed Initial and Followup VR12 Oblique with THA" )
		or exists ( "Assessed Initial and Followup VR12 Orthogonal with THA" )

define "VR12 Orthogonal Total Assessment Scores":
	( ["Assessment, Performed": "VR-12 Mental component summary (MCS) score - orthogonal method T-score"] VR12MentalOrthogonal
		where VR12MentalOrthogonal.result is not null ) VR12MentalScore
		with ( ["Assessment, Performed": "VR-12 Physical component summary (PCS) score - orthogonal method T-score"] VR12PhysicalOrthogonal
			where VR12PhysicalOrthogonal.result is not null ) VR12PhysicalScore
			such that VR12MentalScore.authorDatetime same day as VR12PhysicalScore.authorDatetime

define "VR12 Oblique Total Assessment Scores":
	( ["Assessment, Performed": "VR-12 Mental component summary (MCS) score - oblique method T-score"] VR12MentalAssessment
		where VR12MentalAssessment.result is not null ) VR12MentalScore
		with ( ["Assessment, Performed": "VR-12 Physical component summary (PCS) score - oblique method T-score"] VR12PhysicalAssessment
			where VR12PhysicalAssessment.result is not null ) VR12PhysicalScore
			such that VR12MentalScore.authorDatetime same day as VR12PhysicalScore.authorDatetime

define "Initial Population":
	exists ( "Total Hip Arthroplasty Procedure" )
		and exists ( "Qualifying Encounter" )
		and exists ["Patient Characteristic Birthdate": "Birth date"] BirthDate
			where Global."CalendarAgeInYearsAt"(BirthDate.birthDatetime, start of "Measurement Period")>= 19