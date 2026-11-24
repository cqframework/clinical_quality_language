library CrossLibraryMixedFilteredUnfiltered version '1.0.0'

using FHIR version '4.0.1'

include FHIRHelpers version '4.0.1' called FHIRHelpers
include CrossLibraryFluentFunctionHelpers version '1.0.0' called Helpers

// Union-poisoning guard at CROSS-LIBRARY scope. Observation is retrieved two ways:
//   * filtered through the cross-library fluent helper (a traced codeFilter), and
//   * completely unfiltered (a bare retrieve).
// Cross-library tracing must ADD the filtered requirement without dropping the unfiltered one;
// if the unfiltered use were lost, a scan that applied the traced codeFilter would silently
// drop the data the bare [Observation] use needs.
valueset "Test Observation Codes": 'http://example.org/fhir/ValueSet/test-observation-codes'

define "Filtered Observations":
  [Observation] O
    where O."has code in"("Test Observation Codes")

define "All Observations Unfiltered":
  [Observation]
