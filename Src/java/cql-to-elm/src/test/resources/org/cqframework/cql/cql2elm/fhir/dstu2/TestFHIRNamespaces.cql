library TestFHIRNamespaces

using FHIR version '1.0.2'

include FHIRHelpers version '1.0.2'

context Patient

define Procedures: [Procedure]