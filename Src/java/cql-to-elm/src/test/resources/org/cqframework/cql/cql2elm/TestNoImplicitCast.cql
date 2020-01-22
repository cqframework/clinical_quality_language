library TestNoImplicitCast

using FHIR version '4.0.0'

include FHIRHelpers version '4.0.0' called FHIRHelpers

valueset "Mammography": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113883.3.464.1003.108.12.1018'

context Patient

define function "ChoiceFunction"(choice Choice<FHIR.dateTime, FHIR.Period, FHIR.Timing, FHIR.instant, FHIR.string, FHIR.Age, FHIR.Range>):
  choice

define "TooManyCasts":
  [DiagnosticReport: "Mammography"] Mammogram
  return "ChoiceFunction"(Mammogram.effective)