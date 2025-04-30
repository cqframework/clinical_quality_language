library CqlEquivalentInOptionTest

// The result of this expression should be "true" when the CQL engine
// is using "equivalent" semantics for the "in" operator, and false when using "equality" semantics.
// There's an engine option to control this behavior.
define "QuantityListIncludes": {
    ToQuantity('1 \'m\'') } includes ToQuantity('1.1 \'m\'')