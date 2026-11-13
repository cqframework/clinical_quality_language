library MixedFilteredUnfilteredDataRequirements version '1.0.0'

using FHIR version '4.0.1'

include FHIRHelpers version '4.0.1' called FHIRHelpers

// Characterization fixture for the "union poisoning" soundness rule for scan filters:
// the same resource type (Observation) is retrieved three ways in one library —
//   * filtered against value set A (traceable codeFilter)
//   * filtered against value set B (a DIFFERENT traceable codeFilter)
//   * completely unfiltered (a bare retrieve)
// A sound acquisition manifest must surface the unfiltered use so that, unioned at the
// scan, Observation is fetched broadly (the unfiltered use dominates). This fixture lets
// us characterize exactly what collapse() emits for that situation.
valueset "Observation Codes A": 'http://example.org/fhir/ValueSet/observation-codes-a'
valueset "Observation Codes B": 'http://example.org/fhir/ValueSet/observation-codes-b'

define "Filtered By A":
  [Observation] O
    where O.code in "Observation Codes A"

define "Filtered By B":
  [Observation] O
    where O.code in "Observation Codes B"

define "All Observations Unfiltered":
  [Observation]
