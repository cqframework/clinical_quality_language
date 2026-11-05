library TestCase2e

using FHIR version '4.0.1'

include FHIRHelpers version '4.0.1'

context Patient

/*
2e - Timing phrase 90 days or less before
DataRequirement
type: Condition
dateFilter: { path: onset, value: Interval[Today() - 90 days, Today()] }
*/

define "Date Filter Expression":
  [Condition] C
    where C.onset as Period starts 90 days or less before Today()

/*
Emits as:
define "Date Filter Expression":
  [Condition] C
    where start of (onset as Period) in Interval[Today() - 90 days, Today()]
*/
