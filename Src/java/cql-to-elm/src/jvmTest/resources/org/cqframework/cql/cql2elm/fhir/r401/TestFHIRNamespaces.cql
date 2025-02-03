library TestFHIRNamespaces

using FHIR version '4.0.1'

include FHIRHelpers version '4.0.1'

context Patient

define Procedures: [Procedure]