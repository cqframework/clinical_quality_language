library TestHidingIncludeDefinition

using FHIR version '4.0.1'
include FHIRHelpers version '4.0.1'

//// Should result in a "Hiding operand identifier"
define function HidingIncludeHiding(value Integer):
    ({1, 2, 3}) "FHIRHelpers"
        return "FHIRHelpers"
