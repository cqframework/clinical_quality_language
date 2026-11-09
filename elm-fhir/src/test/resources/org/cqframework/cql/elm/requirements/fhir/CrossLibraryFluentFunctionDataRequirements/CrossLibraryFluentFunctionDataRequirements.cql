library CrossLibraryFluentFunctionDataRequirements version '1.0.0'

using FHIR version '4.0.1'

include FHIRHelpers version '4.0.1' called FHIRHelpers
include CrossLibraryFluentFunctionHelpers version '1.0.0' called Helpers

// This is the cross-library analogue of FluentFunctionDataRequirements: the value set is
// declared and bound HERE (the caller), while the fluent helper that consumes it is defined
// in the included Helpers library. A sound requirements analysis must trace the value set
// bound at THIS call site into the helper body (resolved in the Helpers library) and surface
// it as a codeFilter on the [Observation] retrieve. This is the exact caller/callee context
// split flagged in the branch's inlining comment.
valueset "Test Observation Codes": 'http://example.org/fhir/ValueSet/test-observation-codes'

define "Relevant Observations":
  [Observation] O
    where O."has code in"("Test Observation Codes")
