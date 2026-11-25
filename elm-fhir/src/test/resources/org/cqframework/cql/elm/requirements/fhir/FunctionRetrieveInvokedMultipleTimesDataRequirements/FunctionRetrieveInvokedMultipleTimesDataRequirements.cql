library FunctionRetrieveInvokedMultipleTimesDataRequirements version '1.0.0'

using FHIR version '4.0.1'

include FHIRHelpers version '4.0.1' called FHIRHelpers

// A same-library function whose body contains a retrieve. The requirements processor inlines the
// body at each call site to trace filters, so invoking the function from multiple call sites would
// report the body's [Encounter] requirement once per call site. Deduplication must collapse those
// structurally-identical requirements so the retrieve is emitted exactly once.
define function "AllEncounters"(tag Integer):
  [Encounter]

define "First":
  "AllEncounters"(1)

define "Second":
  "AllEncounters"(2)

define "Third":
  "AllEncounters"(3)
