library CrossLibraryGateNegative version '1.0.0'

using FHIR version '4.0.1'

include FHIRHelpers version '4.0.1' called FHIRHelpers
include CrossLibraryFluentFunctionHelpers version '1.0.0' called Helpers

// Routes an Observation retrieve through a cross-library helper that does NO terminology
// membership. The eligibility gate must decline to inline it, and the analysis must fall back to
// the safe behavior: the [Observation] retrieve is still emitted (unfiltered), never dropped.
define "Observations With Effective":
  [Observation] O
    where O."has effective"()
