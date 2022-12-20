library FunctionalStatusAssessmentforTotalKneeReplacement version '9.3.000'

using QDM version '5.5'

include MATGlobalCommonFunctions version '5.0.000' called Global
include Hospice version '2.2.000' called Hospice

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

define "Has Dementia Diagnosis":
  exists ( ["Diagnosis": "Severe dementia (disorder)"] Dementia
      where Dementia.prevalencePeriod overlaps "Measurement Period"
  )

define "Denominator":
  "Initial Population"

define "Denominator Exclusions":
  Hospice."Has Hospice"
    or "Has Total Knee Arthroplasty with 2 or More Lower Body Fractures"
    or "Has Dementia Diagnosis"

define "Has Qualifying Encounters":
  exists ( ( ["Encounter, Performed": "Outpatient Consultation"]
      union ["Encounter, Performed": "Office Visit"]
      union ["Encounter, Performed": "Postoperative follow-up visit, normally included in the surgical package, to indicate that an evaluation and management service was performed during a postoperative period for a reason(s) related to the original procedure"] ) ValidEncounters
      where ValidEncounters.relevantPeriod during "Measurement Period"
  )

define "Initial Population":
  "Has Qualifying Encounters"
    and exists ( "Total Knee Arthroplasty Procedure" )
    and exists ( ["Patient Characteristic Birthdate": "Birth date"] BirthDate
        where Global."CalendarAgeInYearsAt" ( BirthDate.birthDatetime, start of "Measurement Period" ) >= 19
    )

define "Total Knee Arthroplasty Procedure":
  ["Procedure, Performed": "Primary TKA Procedure"] TKAProcedure
    where TKAProcedure.relevantPeriod starts 12 months or less before start of "Measurement Period"

define "Has Total Knee Arthroplasty with 2 or More Lower Body Fractures":
  exists ( "Total Knee Arthroplasty Procedure" TKAProcedure
      where Count(["Diagnosis": "Fracture - Lower Body"] LowerBodyFracture
          where LowerBodyFracture.prevalencePeriod starts less than 1 day on or before TKAProcedure.relevantPeriod
      )>= 2
  )

define "Time KOOS Total Assessment Completed":
  from
    ["Assessment, Performed": "Quality of life score [KOOS]"] KOOSLifeQuality,
    ["Assessment, Performed": "Sport-recreation score [KOOS]"] KOOSSport,
    ["Assessment, Performed": "Activities of daily living score [KOOS]"] KOOSActivityScore,
    ["Assessment, Performed": "Symptoms score [KOOS]"] KOOSSymptoms,
    ["Assessment, Performed": "Pain score [KOOS]"] KOOSPain
    where KOOSLifeQuality.relevantDatetime same day as KOOSSport.relevantDatetime
      and KOOSSport.result is not null
      and KOOSLifeQuality.relevantDatetime same day as KOOSActivityScore.relevantDatetime
      and KOOSActivityScore.result is not null
      and KOOSLifeQuality.relevantDatetime same day as KOOSSymptoms.relevantDatetime
      and KOOSSymptoms.result is not null
      and KOOSLifeQuality.relevantDatetime same day as KOOSPain.relevantDatetime
      and KOOSPain.result is not null
      and KOOSLifeQuality.result is not null
    return Max({ KOOSLifeQuality.relevantDatetime, KOOSSport.relevantDatetime, KOOSActivityScore.relevantDatetime, KOOSSymptoms.relevantDatetime, KOOSPain.relevantDatetime })

define "Time VR12 Orthogonal Total Assessment Completed":
  from
    ["Assessment, Performed": "VR-12 Mental component summary (MCS) score - orthogonal method T-score"] VR12MentalAssessment,
    ["Assessment, Performed": "VR-12 Physical component summary (PCS) score - orthogonal method T-score"] VR12PhysicalAssessment
    where VR12MentalAssessment.relevantDatetime same day as VR12PhysicalAssessment.relevantDatetime
      and VR12MentalAssessment.result is not null
      and VR12PhysicalAssessment.result is not null
    return Max({ VR12MentalAssessment.relevantDatetime, VR12PhysicalAssessment.relevantDatetime })

define "Time VR12 Oblique Total Assessment Completed":
  from
    ["Assessment, Performed": "VR-12 Mental component summary (MCS) score - oblique method T-score"] VR12MentalAssessment,
    ["Assessment, Performed": "VR-12 Physical component summary (PCS) score - oblique method T-score"] VR12PhysicalAssessment
    where VR12MentalAssessment.relevantDatetime same day as VR12PhysicalAssessment.relevantDatetime
      and VR12MentalAssessment.result is not null
      and VR12PhysicalAssessment.result is not null
    return Max({ VR12MentalAssessment.relevantDatetime, VR12PhysicalAssessment.relevantDatetime })

define "KOOSJr Total Assessment Completed":
  ( ["Assessment, Performed": "Knee Injury and Osteoarthritis Outcome Score for Joint Replacement [KOOSJR]"] KOOSJr
      where KOOSJr.result is not null
  )

define "Time PROMIS10 Total Assessment Completed":
  from
    ["Assessment, Performed": "PROMIS-10 Global Mental Health (GMH) score T-score"] Promis10MentalScore,
    ["Assessment, Performed": "PROMIS-10 Global Physical Health (GPH) score T-score"] Promis10PhysicalScore
    where Promis10MentalScore.relevantDatetime same day as Promis10PhysicalScore.relevantDatetime
      and Promis10PhysicalScore.result is not null
      and Promis10MentalScore.result is not null
    return Max({ Promis10MentalScore.relevantDatetime, Promis10PhysicalScore.relevantDatetime })

define "Has TKA with Initial and Follow Up PROMIS10 Assessments":
  exists ( "Total Knee Arthroplasty Procedure" TotalKnee
      with "Time PROMIS10 Total Assessment Completed" InitialPROMIS10AssessmentTime
        such that TotalKnee.relevantPeriod starts 90 days or less on or after InitialPROMIS10AssessmentTime
      with "Time PROMIS10 Total Assessment Completed" FollowupPROMIS10AssessmentTime
        such that FollowupPROMIS10AssessmentTime in Interval[
        end of TotalKnee.relevantPeriod + 270 days, 
        end of TotalKnee.relevantPeriod + 365 days]
  )

define "Has TKA with Initial and Follow Up VR12 Oblique Assessments":
  exists ( "Total Knee Arthroplasty Procedure" TotalKnee
      with "Time VR12 Oblique Total Assessment Completed" InitialKneeAssessmentObliqueTime
        such that TotalKnee.relevantPeriod starts 90 days or less on or after InitialKneeAssessmentObliqueTime
      with "Time VR12 Oblique Total Assessment Completed" FollowupObliqueAssessmentTime
        such that FollowupObliqueAssessmentTime in Interval[
        end of TotalKnee.relevantPeriod + 270 days, 
        end of TotalKnee.relevantPeriod + 365 days]
  )

define "Numerator":
  "Has TKA with Initial and Follow Up KOOS Assessments"
    or "Has TKA with Initial and Follow Up KOOSJr Assessments"
    or "Has TKA with Initial and Follow Up PROMIS10 Assessments"
    or "Has TKA with Initial and Follow Up VR12 Oblique Assessments"
    or "Has TKA with Initial and Follow Up VR12 Orthogonal Assessments"

define "Has TKA with Initial and Follow Up KOOS Assessments":
  exists ( "Total Knee Arthroplasty Procedure" TotalKnee
      with "Time KOOS Total Assessment Completed" InitialKOOSAssessmentTime
        such that TotalKnee.relevantPeriod starts 90 days or less on or after InitialKOOSAssessmentTime
      with "Time KOOS Total Assessment Completed" FollowupKOOSAssessmentTime
        such that FollowupKOOSAssessmentTime in Interval[
        end of TotalKnee.relevantPeriod + 270 days, 
        end of TotalKnee.relevantPeriod + 365 days]
  )

define "Has TKA with Initial and Follow Up KOOSJr Assessments":
  exists ( "Total Knee Arthroplasty Procedure" TotalKnee
      with "KOOSJr Total Assessment Completed" InitialKOOSJrAssessment
        such that TotalKnee.relevantPeriod starts 90 days or less on or after InitialKOOSJrAssessment.relevantDatetime
      with "KOOSJr Total Assessment Completed" FollowupKOOSJrAssessment
        such that FollowupKOOSJrAssessment.relevantDatetime in Interval[
        end of TotalKnee.relevantPeriod + 270 days, 
        end of TotalKnee.relevantPeriod + 365 days]
  )

define "Has TKA with Initial and Follow Up VR12 Orthogonal Assessments":
  exists ( "Total Knee Arthroplasty Procedure" TotalKnee
      with "Time VR12 Orthogonal Total Assessment Completed" InitialOrthogonalAssessmentTime
        such that TotalKnee.relevantPeriod starts 90 days or less on or after InitialOrthogonalAssessmentTime
      with "Time VR12 Orthogonal Total Assessment Completed" FollowupOrthogonalAssessmentTime
        such that FollowupOrthogonalAssessmentTime in Interval[
        end of TotalKnee.relevantPeriod + 270 days, 
        end of TotalKnee.relevantPeriod + 365 days]
  )