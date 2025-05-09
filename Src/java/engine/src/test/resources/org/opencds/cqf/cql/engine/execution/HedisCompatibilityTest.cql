library HedisCompatibilityTest version '1.0.0'

// The result of this expression should be "true" when the CQL engine
// is using "equivalent" semantics for the "in" operator, and false when using "equality" semantics.
// There's an engine option to control this behavior.
define "QuantityListIncludes": {
    ToQuantity('1 \'m\'') } includes ToQuantity('1.1 \'m\'')


// default behavior is "distinct", "all" for HEDIS
define "ReturnUnspecified":
   ({1, 1, 1, 2, 2 }) X return X

// both standard and HEDIS behavior is "all"
define "ReturnAll":
   ({1, 1, 1, 2, 2 }) X return all X

// standard behavior is "distinct", HEDIS behavior is "all", even if "distinct" is specified
define "ReturnDistinct":
    ({1, 1, 1, 2, 2 }) X return distinct X


define "Null Tuple":
   if false then Tuple { x : 5 }
   else null

define "Test Null Tuple":
   ("Null Tuple") T where T.x > 4