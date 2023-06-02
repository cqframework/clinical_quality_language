library FunctionalStatusAssessmentforTotalHipReplacement version '9.2.000'

using QDM version '5.5'

include MATGlobalCommonFunctions version '5.0.000' called Global
include Hospice version '2.2.000' called Hospice

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

define "Has Dementia Diagnosis":
  exists ( ["Diagnosis": "Severe dementia (disorder)"] Dementia
      where Dementia.prevalencePeriod overlaps "Measurement Period"
  )

define "Denominator Exclusions":
  Hospice."Has Hospice"
    or "Has Total Hip Arthroplasty with 2 or More Lower Body Fractures"
    or "Has Dementia Diagnosis"

define "Has Qualifying Encounter":
  exists ( ( ["Encounter, Performed": "Outpatient Consultation"]
      union ["Encounter, Performed": "Office Visit"]
      union ["Encounter, Performed": "Postoperative follow-up visit, normally included in the surgical package, to indicate that an evaluation and management service was performed during a postoperative period for a reason(s) related to the original procedure"] ) ValidEncounters
      where ValidEncounters.relevantPeriod during "Measurement Period"
  )

define "Initial Population":
  "Has Qualifying Encounter"
    and exists ( "Total Hip Arthroplasty Procedure" )
    and exists ( ["Patient Characteristic Birthdate": "Birth date"] BirthDate
        where Global."CalendarAgeInYearsAt" ( BirthDate.birthDatetime, start of "Measurement Period" ) >= 19
    )

define "Time HOOS Total Assessment Completed":
  from
    ["Assessment, Performed": "Quality of life score [HOOS]"] HOOSLifeQuality,
    ["Assessment, Performed": "Sport-recreation score [HOOS]"] HOOSSport,
    ["Assessment, Performed": "Activities of daily living score [HOOS]"] HOOSActivityScore,
    ["Assessment, Performed": "Symptoms score [HOOS]"] HOOSSymptoms,
    ["Assessment, Performed": "Pain score [HOOS]"] HOOSPain
    where HOOSLifeQuality.relevantDatetime same day as HOOSSport.relevantDatetime
      and HOOSSport.result is not null
      and HOOSLifeQuality.relevantDatetime same day as HOOSActivityScore.relevantDatetime
      and HOOSActivityScore.result is not null
      and HOOSLifeQuality.relevantDatetime same day as HOOSSymptoms.relevantDatetime
      and HOOSSymptoms.result is not null
      and HOOSLifeQuality.relevantDatetime same day as HOOSPain.relevantDatetime
      and HOOSPain.result is not null
      and HOOSLifeQuality.result is not null
    return Max({ HOOSLifeQuality.relevantDatetime, HOOSSport.relevantDatetime, HOOSActivityScore.relevantDatetime, HOOSSymptoms.relevantDatetime, HOOSPain.relevantDatetime })

define "HOOSJr Total Assessment Completed":
  ( ["Assessment, Performed": "Hip Dysfunction and Osteoarthritis Outcome Score for Joint Replacement [HOOSJR]"] HOOSJr
      where HOOSJr.result is not null
  )

define "Time VR12 Oblique Total Assessment Completed":
  from
    ["Assessment, Performed": "VR-12 Mental component summary (MCS) score - oblique method T-score"] VR12MentalAssessment,
    ["Assessment, Performed": "VR-12 Physical component summary (PCS) score - oblique method T-score"] VR12PhysicalAssessment
    where VR12MentalAssessment.relevantDatetime same day as VR12PhysicalAssessment.relevantDatetime
      and VR12MentalAssessment.result is not null
      and VR12PhysicalAssessment.result is not null
    return Max({ VR12MentalAssessment.relevantDatetime, VR12PhysicalAssessment.relevantDatetime })

define "Time VR12 Orthogonal Total Assessment Completed":
  from
    ["Assessment, Performed": "VR-12 Mental component summary (MCS) score - orthogonal method T-score"] VR12MentalOrthogonal,
    ["Assessment, Performed": "VR-12 Physical component summary (PCS) score - orthogonal method T-score"] VR12PhysicalOrthogonal
    where VR12MentalOrthogonal.relevantDatetime same day as VR12PhysicalOrthogonal.relevantDatetime
      and VR12MentalOrthogonal.result is not null
      and VR12PhysicalOrthogonal.result is not null
    return Max({ VR12MentalOrthogonal.relevantDatetime, VR12PhysicalOrthogonal.relevantDatetime })

define "Has Total Hip Arthroplasty with 2 or More Lower Body Fractures":
  exists ( "Total Hip Arthroplasty Procedure" THAProcedure
      where Count(["Diagnosis": "Fracture - Lower Body"] LowerBodyFracture
          where LowerBodyFracture.prevalencePeriod starts less than 1 day on or before start of THAProcedure.relevantPeriod
      )>= 2
  )

define "Has THA with Initial and Follow Up HOOS Assessments":
  exists ( "Total Hip Arthroplasty Procedure" TotalHip
      with "Time HOOS Total Assessment Completed" InitialHipAssessmentHOOS
        such that TotalHip.relevantPeriod starts 90 days or less on or after InitialHipAssessmentHOOS
      with "Time HOOS Total Assessment Completed" FollowupHipAssessmentHOOS
        such that FollowupHipAssessmentHOOS in Interval[
        end of TotalHip.relevantPeriod + 270 days, 
        end of TotalHip.relevantPeriod + 365 days]
  )

define "Has THA with Initial and Follow Up HOOSJr Assessments":
  exists ( "Total Hip Arthroplasty Procedure" TotalHip
      with "HOOSJr Total Assessment Completed" InitialHipAssessment
        such that TotalHip.relevantPeriod starts 90 days or less on or after InitialHipAssessment.relevantDatetime
      with "HOOSJr Total Assessment Completed" FollowupHipAssessment
        such that FollowupHipAssessment.relevantDatetime in Interval[
        end of TotalHip.relevantPeriod + 270 days, 
        end of TotalHip.relevantPeriod + 365 days]
  )

define "Has THA with Initial and Follow Up PROMIS10 Assessments":
  exists ( "Total Hip Arthroplasty Procedure" TotalHip
      with "Time PROMIS10 Total Assessment Completed" InitialHipAssessmentPROMIS10
        such that TotalHip.relevantPeriod starts 90 days or less on or after InitialHipAssessmentPROMIS10
      with "Time PROMIS10 Total Assessment Completed" FollowupHipAssessmentPROMIS10
        such that FollowupHipAssessmentPROMIS10 in Interval[
        end of TotalHip.relevantPeriod + 270 days, 
        end of TotalHip.relevantPeriod + 365 days]
  )

define "Has THA with Initial and Follow Up VR12 Oblique Assessments":
  exists ( "Total Hip Arthroplasty Procedure" TotalHip
      with "Time VR12 Oblique Total Assessment Completed" InitialHipAssessmentOblique
        such that TotalHip.relevantPeriod starts 90 days or less on or after InitialHipAssessmentOblique
      with "Time VR12 Oblique Total Assessment Completed" FollowupHipAssessmentOblique
        such that FollowupHipAssessmentOblique in Interval[
        end of TotalHip.relevantPeriod + 270 days, 
        end of TotalHip.relevantPeriod + 365 days]
  )

define "Has THA with Initial and Follow Up VR12 Orthogonal Assessments":
  exists ( "Total Hip Arthroplasty Procedure" TotalHip
      with "Time VR12 Orthogonal Total Assessment Completed" InitialHipAssessmentOrthogonal
        such that TotalHip.relevantPeriod starts 90 days or less on or after InitialHipAssessmentOrthogonal
      with "Time VR12 Orthogonal Total Assessment Completed" FollowupHipAssessmentOrthogonal
        such that FollowupHipAssessmentOrthogonal in Interval[
        end of TotalHip.relevantPeriod + 270 days, 
        end of TotalHip.relevantPeriod + 365 days]
  )

define "Numerator":
  ( "Has THA with Initial and Follow Up HOOS Assessments" )
    or ( "Has THA with Initial and Follow Up HOOSJr Assessments" )
    or ( "Has THA with Initial and Follow Up PROMIS10 Assessments" )
    or ( "Has THA with Initial and Follow Up VR12 Oblique Assessments" )
    or ( "Has THA with Initial and Follow Up VR12 Orthogonal Assessments" )

define "Time PROMIS10 Total Assessment Completed":
  from
    ["Assessment, Performed": "PROMIS-10 Global Mental Health (GMH) score T-score"] Promis10MentalScore,
    ["Assessment, Performed": "PROMIS-10 Global Physical Health (GPH) score T-score"] Promis10PhysicalScore
    where Promis10MentalScore.relevantDatetime same day as Promis10PhysicalScore.relevantDatetime
      and Promis10PhysicalScore.result is not null
      and Promis10MentalScore.result is not null
    return Max({ Promis10MentalScore.relevantDatetime, Promis10PhysicalScore.relevantDatetime })

define "Total Hip Arthroplasty Procedure":
  ["Procedure, Performed": "Primary THA Procedure"] THAProcedure
    where THAProcedure.relevantPeriod starts 12 months or less on or before start of "Measurement Period"