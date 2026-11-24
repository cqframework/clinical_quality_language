library CrossLibraryNestedMembership version '1.0.0'

using FHIR version '4.0.1'

include FHIRHelpers version '4.0.1' called FHIRHelpers
include EncounterTypeHelpers version '1.0.0' called Encounters

// End-to-end shape: a value set declared in the caller is forwarded through a delegating helper in
// one library to a terminal nested-exists membership in another. A complete trace would surface a
// codeFilter on the Encounter retrieve keyed to this value set.
valueset "Qualifying Encounter Types": 'http://example.org/fhir/ValueSet/qualifying-encounter-types'

define "Qualifying Encounters":
  [Encounter] enc
    where enc."typedAs"("Qualifying Encounter Types")
