library FunctionUnreferencedOperandDataRequirements version '1.0.0'

using FHIR version '4.0.1'

include FHIRHelpers version '4.0.1' called FHIRHelpers

valueset "Active Conditions": 'http://example.org/fhir/ValueSet/active-conditions'

// An inlinable (same-library) function that IGNORES its second operand. The requirements
// processor inlines the body for filter tracing, but the body references only "keep" -- never
// "ignored". The argument bound to "ignored" carries a [Condition] retrieve that is still
// evaluated at runtime, so its data requirement must NOT be dropped by inlining.
define function "firstOf"(keep Integer, ignored List<Condition>):
  keep

define "Result":
  "firstOf"(5, [Condition] c where c.code in "Active Conditions")
