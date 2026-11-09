library NestedExistsMembershipProbe version '1.0.0'

using FHIR version '4.0.1'

include FHIRHelpers version '4.0.1' called FHIRHelpers

valueset "Direct VS": 'http://example.org/fhir/ValueSet/direct-vs'
valueset "Nested VS": 'http://example.org/fhir/ValueSet/nested-vs'

// Membership buried in a nested exists over a property path -- the shape a list-valued coded filter
// takes once a helper that does it is inlined. The core inference does not currently lift this into
// a codeFilter.
define "Nested Exists Membership":
  [Encounter] enc
    where exists(
      (enc.type) cc
        with (cc.coding) coding
          such that coding in "Nested VS"
    )

// Direct membership on a scalar coded property: the inference DOES lift this into a codeFilter.
define "Direct Membership":
  [Encounter] enc
    where enc.class in "Direct VS"
