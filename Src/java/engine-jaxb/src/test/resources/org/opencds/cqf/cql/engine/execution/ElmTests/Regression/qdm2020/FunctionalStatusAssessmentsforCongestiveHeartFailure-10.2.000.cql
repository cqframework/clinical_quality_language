library FunctionalStatusAssessmentsforCongestiveHeartFailure version '10.2.000'

using QDM version '5.5'

include MATGlobalCommonFunctions version '5.0.000' called Global
include Hospice version '2.2.000' called Hospice

codesystem "LOINC": 'urn:oid:2.16.840.1.113883.6.1'
codesystem "SNOMEDCT": 'urn:oid:2.16.840.1.113883.6.96'

valueset "Ethnicity": 'urn:oid:2.16.840.1.114222.4.11.837'
valueset "Heart Failure": 'urn:oid:2.16.840.1.113883.3.526.3.376'
valueset "Office Visit": 'urn:oid:2.16.840.1.113883.3.464.1003.101.12.1001'
valueset "ONC Administrative Sex": 'urn:oid:2.16.840.1.113762.1.4.1'
valueset "Payer": 'urn:oid:2.16.840.1.114222.4.11.3591'
valueset "Race": 'urn:oid:2.16.840.1.114222.4.11.836'

code "Birth date": '21112-8' from "LOINC" display 'Birth date'
code "Emotional score [MLHFQ]": '85609-6' from "LOINC" display 'Emotional score [MLHFQ]'
code "Kansas City Cardiomyopathy Questionnaire - 12 item [KCCQ-12]": '86923-0' from "LOINC" display 'Kansas City Cardiomyopathy Questionnaire - 12 item [KCCQ-12]'
code "Overall summary score [KCCQ-12]": '86924-8' from "LOINC" display 'Overall summary score [KCCQ-12]'
code "Overall summary score [KCCQ]": '71940-1' from "LOINC" display 'Overall summary score [KCCQ]'
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
code "Severe dementia (disorder)": '428351000124105' from "SNOMEDCT" display 'Severe dementia (disorder)'
code "Social limitation score [KCCQ]": '72196-9' from "LOINC" display 'Social limitation score [KCCQ]'
code "Symptom stability score [KCCQ]": '72194-4' from "LOINC" display 'Symptom stability score [KCCQ]'
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

define "Denominator":
  "Initial Population"

define "Denominator Exclusions":
  Hospice."Has Hospice"
    or exists ( ["Diagnosis": "Severe dementia (disorder)"] Dementia
        where Dementia.prevalencePeriod overlaps "Measurement Period"
    )

define "Follow Up Encounter":
  "Qualifying Encounters" OfficeVisit
    with "Initial Encounter" InitialVisit
      such that OfficeVisit.relevantPeriod starts 30 days or more after day of 
      end of InitialVisit.relevantPeriod
        and OfficeVisit.relevantPeriod starts 180 days or less after day of 
        end of InitialVisit.relevantPeriod

define "Has Encounter with Initial and Follow Up KCCQ Domain Score Assessments":
  ( "Initial Encounter" Encounter
      with ( "Time KCCQ Domain Assessment Completed" InitialKCCQAssessmentTime
        with "Time KCCQ Domain Assessment Completed" FollowupKCCQAssessmentTime
          such that FollowupKCCQAssessmentTime in Interval[date from InitialKCCQAssessmentTime + 30 days, date from InitialKCCQAssessmentTime + 180 days]) KCCQAssessments
        such that KCCQAssessments 14 days or less on or before 
        end of Encounter.relevantPeriod
  ) is not null

define "Has Encounter with Initial and Follow Up KCCQ Total Score Assessments":
  ( "Initial Encounter" Encounter
      with ( "KCCQ Total Assessment Completed" InitialKCCQTotalScore
        with "KCCQ Total Assessment Completed" FollowupKCCQTotalScore
          such that FollowupKCCQTotalScore.relevantDatetime in Interval[date from InitialKCCQTotalScore.relevantDatetime + 30 days, date from InitialKCCQTotalScore.relevantDatetime + 180 days]) KCCQTotalScoreAssessments
        such that KCCQTotalScoreAssessments.relevantDatetime 14 days or less on or before 
        end of Encounter.relevantPeriod
  ) is not null

define "Has Encounter with Initial and Follow Up KCCQ12 Assessments":
  ( "Initial Encounter" Encounter
      with ( "Time KCCQ12 Total Assessment Completed" InitialKCCQ12Time
        with "Time KCCQ12 Total Assessment Completed" FollowupKCCQ12Time
          such that FollowupKCCQ12Time in Interval[date from InitialKCCQ12Time + 30 days, date from InitialKCCQ12Time + 180 days]) KCCQ12Assessments
        such that KCCQ12Assessments 14 days or less on or before 
        end of Encounter.relevantPeriod
  ) is not null

define "Has Encounter with Initial and Follow Up MLHFQ Assessments":
  ( "Initial Encounter" Encounter
      with ( "Time MLHFQ Total Assessment Completed" InitialMLHFQTime
        with "Time MLHFQ Total Assessment Completed" FollowupMLHFQTime
          such that FollowupMLHFQTime in Interval[date from InitialMLHFQTime + 30 days, date from InitialMLHFQTime + 180 days]) MLHFQAssessments
        such that MLHFQAssessments 14 days or less on or before 
        end of Encounter.relevantPeriod
  ) is not null

define "Has Encounter with Initial and Follow Up PROMIS10 Assessments":
  ( "Initial Encounter" Encounter
      with ( "Time PROMIS10 Total Assessment Completed" InitialPROMIS10Time
        with "Time PROMIS10 Total Assessment Completed" FollowupPROMIS10Time
          such that FollowupPROMIS10Time in Interval[date from InitialPROMIS10Time + 30 days, date from InitialPROMIS10Time + 180 days]) PROMIS10Assessments
        such that PROMIS10Assessments 14 days or less on or before 
        end of Encounter.relevantPeriod
  ) is not null

define "Has Encounter with Initial and Follow Up PROMIS29 Assessments":
  ( "Initial Encounter" Encounter
      with ( "Time PROMIS29 Total Assessment Completed" InitialPROMIS29Time
        with "Time PROMIS29 Total Assessment Completed" FollowupPROMIS29Time
          such that FollowupPROMIS29Time in Interval[date from InitialPROMIS29Time + 30 days, date from InitialPROMIS29Time + 180 days]) PROMIS29Assessments
        such that PROMIS29Assessments 14 days or less on or before 
        end of Encounter.relevantPeriod
  ) is not null

define "Has Encounter with Initial and Follow Up VR12 Oblique Assessments":
  ( "Initial Encounter" Encounter
      with ( "Time VR12 Oblique Total Assessment Completed" InitialVR12ObliqueTime
        with "Time VR12 Oblique Total Assessment Completed" FollowupVR12ObliqueTime
          such that FollowupVR12ObliqueTime in Interval[date from InitialVR12ObliqueTime + 30 days, date from InitialVR12ObliqueTime + 180 days]) VR12ObliqueAssessments
        such that VR12ObliqueAssessments 14 days or less on or before 
        end of Encounter.relevantPeriod
  ) is not null

define "Has Encounter with Initial and Follow Up VR12 Orthogonal Assessments":
  ( "Initial Encounter" Encounter
      with ( "Time VR12 Orthogonal Total Assessment Completed" InitialVR12OrthogonalTime
        with "Time VR12 Orthogonal Total Assessment Completed" FollowupVR12OrthogonalTime
          such that FollowupVR12OrthogonalTime in Interval[date from InitialVR12OrthogonalTime + 30 days, date from InitialVR12OrthogonalTime + 180 days]) VR12OrthogonalAssessments
        such that VR12OrthogonalAssessments 14 days or less on or before 
        end of Encounter.relevantPeriod
  ) is not null

define "Has Encounter with Initial and Follow Up VR36 Oblique Assessments":
  ( "Initial Encounter" Encounter
      with ( "Time VR36 Oblique Total Assessment Completed" InitialVR36ObliqueTime
        with "Time VR36 Oblique Total Assessment Completed" FollowupVR36ObliqueTime
          such that FollowupVR36ObliqueTime in Interval[date from InitialVR36ObliqueTime + 30 days, date from InitialVR36ObliqueTime + 180 days]) VR36ObliqueAssessments
        such that VR36ObliqueAssessments 14 days or less on or before 
        end of Encounter.relevantPeriod
  ) is not null

define "Has Encounter with Initial and Follow Up VR36 Orthogonal Assessments":
  ( "Initial Encounter" Encounter
      with ( "Time VR36 Orthogonal Total Assessment Completed" InitialVR36OrthogonalTime
        with "Time VR36 Orthogonal Total Assessment Completed" FollowupVR36OrthogonalTime
          such that FollowupVR36OrthogonalTime in Interval[date from InitialVR36OrthogonalTime + 30 days, date from InitialVR36OrthogonalTime + 180 days]) VR36OrthogonalAssessments
        such that VR36OrthogonalAssessments 14 days or less on or before 
        end of Encounter.relevantPeriod
  ) is not null

define "Initial Encounter":
  First("Qualifying Encounters" QualifyingEncounter
      where QualifyingEncounter.relevantPeriod ends 180 days or more before 
      end of "Measurement Period"
      sort by start of relevantPeriod
  )

define "Initial Population":
  exists ( ["Patient Characteristic Birthdate": "Birth date"] BirthDate
      where Global."CalendarAgeInYearsAt" ( BirthDate.birthDatetime, start of "Measurement Period" ) >= 18
  )
    and exists ( ["Diagnosis": "Heart Failure"] HeartFailure
        where HeartFailure.prevalencePeriod overlaps before "Measurement Period"
    )
    and exists ( "Follow Up Encounter" )

define "KCCQ Total Assessment Completed":
  ["Assessment, Performed": "Overall summary score [KCCQ]"] KCCQSummaryScore
    where KCCQSummaryScore.result is not null

define "Numerator":
  ( "Has Encounter with Initial and Follow Up PROMIS10 Assessments" )
    or ( "Has Encounter with Initial and Follow Up PROMIS29 Assessments" )
    or ( "Has Encounter with Initial and Follow Up VR12 Oblique Assessments" )
    or ( "Has Encounter with Initial and Follow Up VR12 Orthogonal Assessments" )
    or ( "Has Encounter with Initial and Follow Up VR36 Oblique Assessments" )
    or ( "Has Encounter with Initial and Follow Up VR36 Orthogonal Assessments" )
    or ( "Has Encounter with Initial and Follow Up MLHFQ Assessments" )
    or ( "Has Encounter with Initial and Follow Up KCCQ12 Assessments" )
    or ( "Has Encounter with Initial and Follow Up KCCQ Domain Score Assessments" )
    or ( "Has Encounter with Initial and Follow Up KCCQ Total Score Assessments" )

define "Qualifying Encounters":
  ["Encounter, Performed": "Office Visit"] ValidEncounter
    where ValidEncounter.relevantPeriod during "Measurement Period"

define "SDE Ethnicity":
  ["Patient Characteristic Ethnicity": "Ethnicity"]

define "SDE Payer":
  ["Patient Characteristic Payer": "Payer"]

define "SDE Race":
  ["Patient Characteristic Race": "Race"]

define "SDE Sex":
  ["Patient Characteristic Sex": "ONC Administrative Sex"]

define "Time KCCQ Domain Assessment Completed":
  from
    ["Assessment, Performed": "Quality of life score [KCCQ]"] KCCQLifeQuality,
    ["Assessment, Performed": "Symptom stability score [KCCQ]"] KCCQSymptomStability,
    ["Assessment, Performed": "Self-efficacy score [KCCQ]"] KCCQSelfEfficacy,
    ["Assessment, Performed": "Total symptom score [KCCQ]"] KCCQSymptoms,
    ["Assessment, Performed": "Physical limitation score [KCCQ]"] KCCQPhysicalLimits,
    ["Assessment, Performed": "Social limitation score [KCCQ]"] KCCQSocialLimits
    where KCCQLifeQuality.relevantDatetime same day as KCCQSymptomStability.relevantDatetime
      and KCCQSymptomStability.result is not null
      and KCCQLifeQuality.relevantDatetime same day as KCCQSelfEfficacy.relevantDatetime
      and KCCQSelfEfficacy.result is not null
      and KCCQLifeQuality.relevantDatetime same day as KCCQSymptoms.relevantDatetime
      and KCCQSymptoms.result is not null
      and KCCQLifeQuality.relevantDatetime same day as KCCQPhysicalLimits.relevantDatetime
      and KCCQPhysicalLimits.result is not null
      and KCCQLifeQuality.relevantDatetime same day as KCCQSocialLimits.relevantDatetime
      and KCCQSocialLimits.result is not null
      and KCCQLifeQuality.result is not null
    return Max({ KCCQLifeQuality.relevantDatetime, KCCQSymptomStability.relevantDatetime, KCCQSelfEfficacy.relevantDatetime, KCCQSymptoms.relevantDatetime, KCCQPhysicalLimits.relevantDatetime, KCCQSocialLimits.relevantDatetime })

define "Time KCCQ12 Total Assessment Completed":
  from
    ["Assessment, Performed": "Kansas City Cardiomyopathy Questionnaire - 12 item [KCCQ-12]"] KCCQ12Item,
    ["Assessment, Performed": "Overall summary score [KCCQ-12]"] KCCQ12Summary
    where KCCQ12Item.relevantDatetime same day as KCCQ12Summary.relevantDatetime
      and KCCQ12Summary.result is not null
      and KCCQ12Item.result is not null
    return Max({ KCCQ12Item.relevantDatetime, KCCQ12Summary.relevantDatetime })

define "Time MLHFQ Total Assessment Completed":
  from
    ["Assessment, Performed": "Physical score [MLHFQ]"] MLHFQPhysical,
    ["Assessment, Performed": "Emotional score [MLHFQ]"] MLHFQEmotional
    where MLHFQPhysical.relevantDatetime same day as MLHFQEmotional.relevantDatetime
      and MLHFQEmotional.result is not null
      and MLHFQPhysical.result is not null
    return Max({ MLHFQPhysical.relevantDatetime, MLHFQEmotional.relevantDatetime })

define "Time PROMIS10 Total Assessment Completed":
  from
    ["Assessment, Performed": "PROMIS-10 Global Mental Health (GMH) score T-score"] PROMIS10MentalScore,
    ["Assessment, Performed": "PROMIS-10 Global Physical Health (GPH) score T-score"] PROMIS10PhysicalScore
    where PROMIS10MentalScore.relevantDatetime same day as PROMIS10PhysicalScore.relevantDatetime
      and PROMIS10PhysicalScore.result is not null
      and PROMIS10MentalScore.result is not null
    return Max({ PROMIS10MentalScore.relevantDatetime, PROMIS10PhysicalScore.relevantDatetime })

define "Time PROMIS29 Total Assessment Completed":
  from
    ["Assessment, Performed": "PROMIS-29 Sleep disturbance score T-score"] Promis29Sleep,
    ["Assessment, Performed": "PROMIS-29 Satisfaction with participation in social roles score T-score"] Promis29SocialRoles,
    ["Assessment, Performed": "PROMIS-29 Physical function score T-score"] Promis29Physical,
    ["Assessment, Performed": "PROMIS-29 Pain interference score T-score"] Promis29Pain,
    ["Assessment, Performed": "PROMIS-29 Fatigue score T-score"] Promis29Fatigue,
    ["Assessment, Performed": "PROMIS-29 Depression score T-score"] Promis29Depression,
    ["Assessment, Performed": "PROMIS-29 Anxiety score T-score"] Promis29Anxiety
    where Promis29Sleep.relevantDatetime same day as Promis29SocialRoles.relevantDatetime
      and Promis29SocialRoles.result is not null
      and Promis29Sleep.relevantDatetime same day as Promis29Physical.relevantDatetime
      and Promis29Physical.result is not null
      and Promis29Sleep.relevantDatetime same day as Promis29Pain.relevantDatetime
      and Promis29Pain.result is not null
      and Promis29Sleep.relevantDatetime same day as Promis29Fatigue.relevantDatetime
      and Promis29Fatigue.result is not null
      and Promis29Sleep.relevantDatetime same day as Promis29Depression.relevantDatetime
      and Promis29Depression.result is not null
      and Promis29Sleep.relevantDatetime same day as Promis29Anxiety.relevantDatetime
      and Promis29Anxiety.result is not null
      and Promis29Sleep.result is not null
    return Max({ Promis29Sleep.relevantDatetime, Promis29SocialRoles.relevantDatetime, Promis29Physical.relevantDatetime, Promis29Pain.relevantDatetime, Promis29Fatigue.relevantDatetime, Promis29Depression.relevantDatetime, Promis29Anxiety.relevantDatetime })

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
    ["Assessment, Performed": "VR-12 Mental component summary (MCS) score - orthogonal method T-score"] VR12MentalAssessment,
    ["Assessment, Performed": "VR-12 Physical component summary (PCS) score - orthogonal method T-score"] VR12PhysicalAssessment
    where VR12MentalAssessment.relevantDatetime same day as VR12PhysicalAssessment.relevantDatetime
      and VR12MentalAssessment.result is not null
      and VR12PhysicalAssessment.result is not null
    return Max({ VR12MentalAssessment.relevantDatetime, VR12PhysicalAssessment.relevantDatetime })

define "Time VR36 Oblique Total Assessment Completed":
  from
    ["Assessment, Performed": "VR-36 Mental component summary (MCS) score - oblique method T-score"] VR36MentalAssessment,
    ["Assessment, Performed": "VR-36 Physical component summary (PCS) score - oblique method T-score"] VR36PhysicalAssessment
    where VR36MentalAssessment.relevantDatetime same day as VR36PhysicalAssessment.relevantDatetime
      and VR36MentalAssessment.result is not null
      and VR36PhysicalAssessment.result is not null
    return Max({ VR36MentalAssessment.relevantDatetime, VR36PhysicalAssessment.relevantDatetime })

define "Time VR36 Orthogonal Total Assessment Completed":
  from
    ["Assessment, Performed": "VR-36 Mental component summary (MCS) score - orthogonal method T-score"] VR36MentalAssessment,
    ["Assessment, Performed": "VR-36 Physical component summary (PCS) score - orthogonal method T-score"] VR36PhysicalAssessment
    where VR36MentalAssessment.relevantDatetime same day as VR36PhysicalAssessment.relevantDatetime
      and VR36MentalAssessment.result is not null
      and VR36PhysicalAssessment.result is not null
    return Max({ VR36MentalAssessment.relevantDatetime, VR36PhysicalAssessment.relevantDatetime })