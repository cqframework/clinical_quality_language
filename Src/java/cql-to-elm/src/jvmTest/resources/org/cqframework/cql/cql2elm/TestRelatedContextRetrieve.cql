using QDM version '5.5'

valueset "Mother Relationship": 'TBD'
valueset "Estimated Due Date Exam Codes": 'TBD'

context Patient

define "Mother": singleton from (["Related Person": "Mother Relationship"])

define "Estimated Due Date":
  Last(
    ["Mother" -> "Physical Exam, Performed": "Estimated Due Date Exam Codes"] Exam
      sort by start of relevantPeriod
  )

define "Gestational Age in Days at Birth":
  (280 - (duration in days between start of "Estimated Due Date".relevantPeriod and Patient.birthDatetime)) div 7