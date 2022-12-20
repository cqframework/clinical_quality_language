library UrinarySymptomScoreChange6to12MonthsAfterDiagnosisofBenignProstaticHyperplasia version '2.2.000'

using QDM version '5.5'

codesystem "LOINC": 'urn:oid:2.16.840.1.113883.6.1'
codesystem "ICD10CM": 'urn:oid:2.16.840.1.113883.6.90'
codesystem "AdministrativeGender": 'urn:oid:2.16.840.1.113883.5.1'

valueset "Ethnicity": 'urn:oid:2.16.840.1.114222.4.11.837'
valueset "Hospital Services for urology care": 'urn:oid:2.16.840.1.113762.1.4.1164.64'
valueset "Morbid Obesity": 'urn:oid:2.16.840.1.113762.1.4.1164.67'
valueset "Office Visit": 'urn:oid:2.16.840.1.113883.3.464.1003.101.12.1001'
valueset "ONC Administrative Sex": 'urn:oid:2.16.840.1.113762.1.4.1'
valueset "Payer": 'urn:oid:2.16.840.1.114222.4.11.3591'
valueset "Race": 'urn:oid:2.16.840.1.114222.4.11.836'
valueset "Urinary retention": 'urn:oid:2.16.840.1.113762.1.4.1164.52'

code "American Urological Association Symptom Index [AUASI]": '80883-2' from "LOINC" display 'American Urological Association Symptom Index [AUASI]'
code "Benign prostatic hyperplasia with lower urinary tract symptoms": 'N40.1' from "ICD10CM" display 'Benign prostatic hyperplasia with lower urinary tract symptoms'
code "Estimated BMI greater than 40": '88087-2' from "LOINC" display 'Estimated BMI greater than 40'
code "If you were to spend the rest of your life with your urinary condition just the way it is now, how would you feel about that [IPSS]": '81090-3' from "LOINC" display 'If you were to spend the rest of your life with your urinary condition just the way it is now, how would you feel about that [IPSS]'
code "International Prostate Symptom Score [IPSS]": '80976-4' from "LOINC" display 'International Prostate Symptom Score [IPSS]'
code "Male": 'M' from "AdministrativeGender" display 'Male'

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

define "Numerator":
  "Urinary Symptom Score Improvement Greater Than or Equal To 3"

define "Urinary Symptom Score Improvement":
  from
    "Urinary Symptom Score Within 1 Month after Initial BPH Diagnosis" FirstUSSAssessment,
    "Urinary Symptom Score 6 to 12 Months After Initial BPH Diagnosis" FollowUpUSSAssessment
    let USSChange: FirstUSSAssessment.result - FollowUpUSSAssessment.result
    return USSChange

define "Urinary Symptom Score Improvement Greater Than or Equal To 3":
  ( "Urinary Symptom Score Improvement" USSImprovement
      where USSImprovement >= 3
  ) is not null

define "Has Qualifying Encounter":
  exists ["Encounter, Performed": "Office Visit"] ValidEncounter
    where ValidEncounter.relevantPeriod during "Measurement Period"

define "Urinary Symptom Score Tuple Return":
  "Documented IPSS Assessment Result"
    union "AUA Symptom Index and Quality of Life Assessment Result"

define "Has Initial BPH Diagnosis Starts During or Within 30 Days After End of Hospitalization":
  "Initial BPH Diagnosis Within 6 Months Before or After Start of Measurement Period Prevalence Period" InitialBPHDiagnosis
    with ["Encounter, Performed": "Hospital Services for urology care"] InHospitalServices
      such that InitialBPHDiagnosis.prevalencePeriod starts during Interval[start of InHospitalServices.relevantPeriod, 
      end of InHospitalServices.relevantPeriod + 31 days )

define "Urinary Retention Diagnosis Starts Within 1 Year After Initial BPH Diagnosis":
  ["Diagnosis": "Urinary retention"] UrinaryRetention
    with "Initial BPH Diagnosis Within 6 Months Before or After Start of Measurement Period Prevalence Period" InitialBPHDiagnosis
      such that UrinaryRetention.prevalencePeriod starts 1 year or less on or after day of start of InitialBPHDiagnosis.prevalencePeriod

define "Initial Population":
  ( "Patient is Male" )
    and "Has Qualifying Encounter"
    and "Has Qualifying BPH Diagnosis"
    and "Urinary Symptom Score Within 1 Month after Initial BPH Diagnosis" is not null
    and "Urinary Symptom Score 6 to 12 Months After Initial BPH Diagnosis" is not null

define "BMI Exam Result Greater Than 40 During Measurement Period and On or Before Follow Up USS Assessment":
  ["Physical Exam, Performed": "Estimated BMI greater than 40"] BMIExam
    with "Urinary Symptom Score 6 to 12 Months After Initial BPH Diagnosis" FollowUpUSSAssessment
      such that BMIExam.relevantDatetime during "Measurement Period"
        and BMIExam.relevantDatetime on or before FollowUpUSSAssessment.relevantDatetime
        and BMIExam.result > 40 'kg/m2'

define "Patient is Male":
  exists ["Patient Characteristic Sex": "Male"]

define "Has Qualifying BPH Diagnosis":
  "Initial BPH Diagnosis Within 6 Months Before or After Start of Measurement Period Prevalence Period" is not null

define "Urinary Symptom Score 6 to 12 Months After Initial BPH Diagnosis":
  Last("Urinary Symptom Score Tuple Return" LastUSSAssessment
      with "Initial BPH Diagnosis Within 6 Months Before or After Start of Measurement Period Prevalence Period" InitialBPHDiagnosis
        such that months between start of InitialBPHDiagnosis.prevalencePeriod and LastUSSAssessment.relevantDatetime in Interval[6, 12]
      sort by relevantDatetime
  )

define "Urinary Symptom Score Within 1 Month after Initial BPH Diagnosis":
  First("Urinary Symptom Score Tuple Return" InitialUSSAssessment
      with "Initial BPH Diagnosis Within 6 Months Before or After Start of Measurement Period Prevalence Period" InitialBPHDiagnosis
        such that InitialUSSAssessment.relevantDatetime 1 month or less on or after day of start of InitialBPHDiagnosis.prevalencePeriod
      sort by relevantDatetime
  )

define "Denominator Exclusions":
  exists ( "Urinary Retention Diagnosis Starts Within 1 Year After Initial BPH Diagnosis" )
    or ( "Has Initial BPH Diagnosis Starts During or Within 30 Days After End of Hospitalization" is not null )
    or ( "Morbid Obesity Diagnosis or BMI Exam Result Greater Than 40 Starts On or Before Follow Up USS Assessment" )

define "Morbid Obesity Diagnosis or BMI Exam Result Greater Than 40 Starts On or Before Follow Up USS Assessment":
  exists "Morbid Obesity Diagnosis On or Before Follow Up USS Assessment"
    or exists "BMI Exam Result Greater Than 40 During Measurement Period and On or Before Follow Up USS Assessment"

define "Morbid Obesity Diagnosis On or Before Follow Up USS Assessment":
  ["Diagnosis": "Morbid Obesity"] MorbidObesity
    with "Urinary Symptom Score 6 to 12 Months After Initial BPH Diagnosis" FollowUpUSSAssessment
      such that MorbidObesity.prevalencePeriod overlaps "Measurement Period"
        and MorbidObesity.prevalencePeriod starts on or before FollowUpUSSAssessment.relevantDatetime

define "AUA Symptom Index and Quality of Life Assessment Result":
  ["Assessment, Performed": "American Urological Association Symptom Index [AUASI]"] AUASIAssessment
    let LastAUASIOnDate: Last(["Assessment, Performed": "American Urological Association Symptom Index [AUASI]"] AUASI
        where AUASI.relevantDatetime same day as AUASIAssessment.relevantDatetime
          and AUASI.result is not null
        sort by relevantDatetime
    ),
    LastQOLOnDate: Last(["Assessment, Performed": "If you were to spend the rest of your life with your urinary condition just the way it is now, how would you feel about that [IPSS]"] QOLAssessment
        where QOLAssessment.relevantDatetime same day as AUASIAssessment.relevantDatetime
          and QOLAssessment.result is not null
        sort by relevantDatetime
    )
    return {
      relevantDatetime: LastAUASIOnDate.relevantDatetime,
      result: ( LastAUASIOnDate.result as Integer ) + ( LastQOLOnDate.result as Integer )
    }

define "Initial BPH Diagnosis Within 6 Months Before or After Start of Measurement Period Prevalence Period":
  First(["Diagnosis": "Benign prostatic hyperplasia with lower urinary tract symptoms"] NewBPHDiagnosis
      where NewBPHDiagnosis.prevalencePeriod starts during Interval[start of "Measurement Period" - 6 months, 
      end of "Measurement Period"]
  )

define "Documented IPSS Assessment Result":
  ["Assessment, Performed": "International Prostate Symptom Score [IPSS]"] IPSSAssessment
    where IPSSAssessment.result is not null
    return {
      relevantDatetime: ( IPSSAssessment.relevantDatetime ),
      result: IPSSAssessment.result as Integer
    }