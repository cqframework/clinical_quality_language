library TestCase1

using FHIR version '4.0.1'

include FHIRHelpers version '4.0.1'

valueset "ESRD Diagnosis": 'http://fakeurl.com/ersd-diagnosis'

context Patient

/*
1.
Stated DataRequirement: ERSD Observations
type: Observation
codeFilter: { path:  code, valueSet:  'http://fakeurl.com/ersd-diagnosis' }
*/

define "ESRD Observations":
  [Observation: "ESRD Diagnosis"]

