library FluentFunctionDataRequirements version '1.0.0'

using FHIR version '4.0.1'

include FHIRHelpers version '4.0.1' called FHIRHelpers

// Terminology filtering is expressed through a fluent helper that takes the value set
// as a PARAMETER (the HEDIS pattern). The requirements processor must trace the value set
// bound at the call site into the function body so it can attach a codeFilter to the
// [Observation] retrieve.
valueset "Test Observation Codes": 'http://example.org/fhir/ValueSet/test-observation-codes'

define fluent function "has code in"(observation Observation, codes System.ValueSet):
  observation.code in codes

define "Relevant Observations":
  [Observation] O
    where O."has code in"("Test Observation Codes")
