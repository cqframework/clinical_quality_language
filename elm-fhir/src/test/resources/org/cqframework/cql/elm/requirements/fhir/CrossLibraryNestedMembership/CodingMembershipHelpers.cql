library CodingMembershipHelpers version '1.0.0'

using FHIR version '4.0.1'

include FHIRHelpers version '4.0.1' called FHIRHelpers

// Terminal value-set membership over a list of codeable concepts, written as a nested exists over
// the codings -- the natural CQL shape for filtering a list-valued coded element. Authored here to
// exercise the "membership buried in a nested exists over a property path" structure.
define fluent function "anyConceptIn"(concepts List<FHIR.CodeableConcept>, codes System.ValueSet):
  exists(
    (concepts) cc
      with (cc.coding) coding
        such that coding in codes
  )
