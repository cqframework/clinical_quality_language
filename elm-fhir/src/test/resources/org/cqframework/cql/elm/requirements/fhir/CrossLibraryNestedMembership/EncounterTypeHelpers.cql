library EncounterTypeHelpers version '1.0.0'

using FHIR version '4.0.1'

include FHIRHelpers version '4.0.1' called FHIRHelpers
include CodingMembershipHelpers version '1.0.0' called Codings

// Delegating helper: forwards the value set to the terminal membership helper in another library,
// applied to the encounter's (list-valued) type element. Reproduces the delegation-across-libraries
// shape without copying any third-party content.
define fluent function "typedAs"(encounter FHIR.Encounter, codes System.ValueSet):
  encounter.type."anyConceptIn"(codes)
