library abstractClassNotRetrievable

using FHIR version '4.0.1'

include FHIRHelpers version '4.0.1'

context Patient

define "Bar":
  [DomainResource] // should be marked non-retrievable, should give error/warning in translator
