library TestCase10a

using FHIR version '4.0.1'

include FHIRHelpers version '4.0.1'

valueset "ESRD Diagnosis": 'http://fakeurl.com/ersd-diagnosis'

context Patient

/*
10a. Date Filter
Stated DataRequirement: ERSD Observations
type: Observation
codeFilter: { path:  code, valueSet:  'http://fakeurl.com/ersd-diagnosis' }
dateFilter: { path: issued, value: Interval[@2022-02-15, null] }
*/

define "ESRD Observations":
  [Observation: "ESRD Diagnosis"] O
    where O.issued same day or after @2022-02-15

