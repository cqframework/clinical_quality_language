library EvaluatedResourcesTest version '1.0'

using FHIR version '4.0.1'

context Patient

define "Mother": singleton from ([RelatedPerson: "Mother Relationship"])

define "Estimated Due Date":
  Last(
    ["Mother" -> "Observation": "Estimated Due Date Exam"] Exam
      sort by effective
  )

define "Gestational Age in Days at Birth":
  (280 - (duration in days between "Estimated Due Date" and "Birth Date")) div 7