library TestCase10b

using FHIR version '4.0.1'

include FHIRHelpers version '4.0.1'

valueset "ESRD Diagnosis": 'http://fakeurl.com/ersd-diagnosis'

context Patient

/*
10b. Date filter with coalesce
type: Observation
codeFilter: { path: code, valueSet: 'http://fakeurl.com/ersd-diagnosis' }
dateFilter: { path: effective, value: Interval[@2022-02-15, null] }

type: Observation
codeFilter: { path: code, valueSet: 'http://fakeurl.com/ersd-diagnosis' }
dateFilter: { path: effective, value: null } // Not sure we can say this, or if it's even useful to do so...
dateFilter: { path: issued, value: Interval[@2022-02-15, null] }
 */

define "ESRD Observations":
  [Observation: "ESRD Diagnosis"] O
    where Coalesce(O.effective, O.issued) same day or after @2022-02-15

/*
TODO: Handle coalesce as a short-hand for disjunction:
// This is short-hand for:
define "ESRD Observations":
  [Observation: "ESRD Diagnosis"] O
    where O.effective same day or after @2022-02-15
      or (O.effective is null and O.issued same day or after @2022-02-15)

*/