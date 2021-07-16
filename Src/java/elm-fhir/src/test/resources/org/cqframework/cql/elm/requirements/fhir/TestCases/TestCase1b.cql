library TestCase1b

using FHIR version '4.0.1'

include FHIRHelpers version '4.0.1'

context Patient

/*
1b. Similar to 1, but not on a primary code path and with a constant
DataRequirement
type: Observation
codeFilter: { path: status, code: Coding { code: 'final'}}
*/

define "Observations":
  [Observation] O
    where O.status = 'final'

