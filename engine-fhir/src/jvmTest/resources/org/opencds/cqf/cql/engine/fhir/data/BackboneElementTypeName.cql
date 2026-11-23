library BackboneElementTypeName

using FHIR version '4.0.1'

include FHIRHelpers version '4.0.1'

context Patient

define "Contact":
  First(Patient.contact)

define "Contact Is Patient.Contact":
  First(Patient.contact) is FHIR.Patient.Contact

define "Contact Is BackboneElement":
  First(Patient.contact) is FHIR.BackboneElement

define "Contacts As Patient.Contact":
  Patient.contact C return C as FHIR.Patient.Contact

define "Reference Range":
  First(First([Observation]).referenceRange)

define "Reference Range Is Observation.ReferenceRange":
  First(First([Observation]).referenceRange) is FHIR.Observation.ReferenceRange

// Claim and ExplanationOfBenefit both have an ItemComponent backbone class in HAPI
define "Claim Item Is Claim.Item":
  First(First([Claim]).item) is FHIR.Claim.Item

define "Claim Item Is ExplanationOfBenefit.Item":
  First(First([Claim]).item) is FHIR.ExplanationOfBenefit.Item

define "EOB Item Is ExplanationOfBenefit.Item":
  First(First([ExplanationOfBenefit]).item) is FHIR.ExplanationOfBenefit.Item

define "EOB Item Is Claim.Item":
  First(First([ExplanationOfBenefit]).item) is FHIR.Claim.Item
