library TestTrace

using FHIR version '4.0.1'

include FHIRHelpers version '4.0.1'

parameter Patient Patient

context Patient

define Test1: name.given.trace('test').count() = 5

// TODO: Not supported, the second argument here is a selector: https://hl7.org/fhir/fhirpath.html#functions
// define Test2: name.trace('test', given).count() = 3

