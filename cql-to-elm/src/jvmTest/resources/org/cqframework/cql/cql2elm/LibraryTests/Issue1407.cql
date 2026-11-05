library Issue1407

using FHIR version '4.0.1'

include FHIRHelpers version '4.0.1' called FHIRHelpers

include Issue1407ValueSets

context Patient

define TestStatement: Issue1407ValueSets.TestValueSet