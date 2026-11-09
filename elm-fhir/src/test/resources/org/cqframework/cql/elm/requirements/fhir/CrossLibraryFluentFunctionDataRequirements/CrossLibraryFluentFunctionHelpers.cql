library CrossLibraryFluentFunctionHelpers version '1.0.0'

using FHIR version '4.0.1'

include FHIRHelpers version '4.0.1' called FHIRHelpers

// The value-set-parameter fluent helper lives in a SEPARATE (shared) library, mirroring the
// HEDIS pattern where helpers like Encounters."Has any code" are defined away from the caller.
// The body reads observation.code and tests membership in the value set bound at the call site.
define fluent function "has code in"(observation Observation, codes System.ValueSet):
  observation.code in codes
