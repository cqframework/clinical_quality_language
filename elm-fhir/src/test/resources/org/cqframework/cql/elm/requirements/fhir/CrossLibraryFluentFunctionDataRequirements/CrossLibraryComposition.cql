library CrossLibraryComposition version '1.0.0'

using FHIR version '4.0.1'

include FHIRHelpers version '4.0.1' called FHIRHelpers
include CrossLibraryFluentFunctionHelpers version '1.0.0' called Helpers

// The call routes through a DELEGATING cross-library helper ("obs code in") that does no
// membership itself but forwards to the terminal membership helper ("code in"). This is the real
// HEDIS shape (Has any code -> Has). Tracing the value set into a codeFilter requires following
// the delegation chain, not just the directly-invoked helper's body.
valueset "Test Observation Codes": 'http://example.org/fhir/ValueSet/test-observation-codes'

define "Composed Filtered Observations":
  [Observation] O
    where O."obs code in"("Test Observation Codes")
