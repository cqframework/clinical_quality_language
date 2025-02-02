library TestImplicitContext

using FHIR version '4.0.1'

context Observation

define DiagnosticReports: [DiagnosticReport]

context Questionnaire

define QuestionnaireResponses: [QuestionnaireResponse]