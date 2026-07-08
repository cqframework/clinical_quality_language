library TestCase2b

using FHIR version '4.0.1'

include FHIRHelpers version '4.0.1'

parameter "Measurement Period" Interval<DateTime> default Interval[@2019-01-01, @2020-01-01)

context Patient

/*
2g - Equal to a compile-time literal function
DataRequirement
type: Condition
dateFilter: { path: onset, value: Today() }
*/

define DateTimeEqualToFunction:
  [Condition] C
    where C.onset as dateTime = Today()
