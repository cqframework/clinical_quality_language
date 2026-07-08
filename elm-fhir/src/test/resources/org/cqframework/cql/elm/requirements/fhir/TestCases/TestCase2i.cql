library TestCase2i

using FHIR version '4.0.1'

include FHIRHelpers version '4.0.1'

context Patient

/*
2i - In a compile-time literal interval
DataRequirement
type: Condition
dateFilter: { path: onset, value: Interval[@2022-12-31 - 90 days, @2022-12-31] }
*/

define "Date Filter Expression":
  [Condition] C
    where C.onset as dateTime in Interval[@2022-12-31T - 90 days, @2022-12-31T]
