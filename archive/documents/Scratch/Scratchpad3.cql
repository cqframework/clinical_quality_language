//* CQL Updates - Post Publication
// Choice type
// Introduce a Choice type, derived from Any, that specifies a set of possible types:
parameter ChoiceParameter Choice<String, Integer>

// Support implicit casting to any of the possible types through casting:
define StringChoiceReference: ChoiceParameter = '123' // valid, ChoiceParameter as String = '123'
define IntegerChoiceReference: ChoiceParameter = 123 // valid, ChoiceParameter as Integer = 123
define DecimalChoiceReference: ChoiceParameter = 123.0 // invalid, syntax error

// Allows natural use of elements like Observation.value, which are defined as union types in the model
// Should we support as a first-class type in CQL, or just an aspect of the model info and translator reference implementation?
// Decision: Because the solution doesn't provide much beyond what is already provided by the Any-valued elements, we are holding off on this.

// Prohibited attributes
// Define a prohibited attribute on instance type elements.
// Allow descendant classes to redeclare attributes as prohibited
// Attempting to use a prohibited element results in a syntax error.
// The tuple type for a class excludes prohibited elements
// Can a descendant reintroduce a prohibited element? Yes, because it's possible to do this in XSD, CDA, and FHIR

// Allows for natural use of profiles that have removed elements of a base type.

// Redeclaring the type of an element
// Allow descendant classes to redeclare elements, changing the element type in one of the following ways:
// 1) Redefining a Choice (must be a restriction of the specified types in the parent choice)
// 2) Redeclaring the type to be a subtype of the parent type
// 3) Redeclaring the type to be a Singleton if the parent type is a list
// Any other redeclaration would be invalid and result in an error when loading the model info.

// Allows for natural use of profiles that restrict types based on the parent type.

// These are all backwards compatible changes that would only impact the CQL-to-ELM translator,
// and potentially the data access layer generation tooling.

// If we make the Choice a first-class type category in CQL, then we would need to update CQL spec and ELM to reflect.
// Still a backwards compatible change though.

// Decision: We will implement these changes to the model info and reference implementation, but will not introduce them
// to the specification until a later point.
