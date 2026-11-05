library TestCase2j

using FHIR version '4.0.1'

include FHIRHelpers version '4.0.1'

context Patient

/*
2j - Before and after
DataRequirement
type: Condition
dateFilter: { path: onset, value: Interval[@2022-12-31T - 90 days, @2022-12-31T] }
*/

define "Date Filter Expression":
  [Condition] C
    where C.onset as dateTime >= @2022-12-31T - 90 days
      and C.onset as dateTime <= @2022-12-31T

