library TestFHIRNamespaces

using FHIR version '3.0.1'

include FHIRHelpers version '3.0.1'

context Patient

define Procedures: [Procedure]