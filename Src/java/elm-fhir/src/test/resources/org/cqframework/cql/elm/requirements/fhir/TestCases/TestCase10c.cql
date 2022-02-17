library TestCase10a

using FHIR version '4.0.1'

include FHIRHelpers version '4.0.1'

valueset "ESRD Diagnosis": 'http://fakeurl.com/ersd-diagnosis'

context Patient

/*
10c. Date Filter on choice
Stated DataRequirement: ERSD Observations
type: Observation
codeFilter: { path:  code, valueSet:  'http://fakeurl.com/ersd-diagnosis' }
dateFilter: { path: effective, value: Interval[@2022-02-15, null] }

NOTE: property reference is a case...
*/

define "ESRD Observations":
  [Observation: "ESRD Diagnosis"] O
    where O.effective same day or after @2022-02-15

/*
define "ESRD Observations":
  [Observation: "ESRD Diagnosis"] O
    where O.effective as dateTime same day or after @2022-02-15
  union
  [Observation: "ESRD Diagnosis"] O
    where O.effective as instant same day or after @2022-02-15
 */