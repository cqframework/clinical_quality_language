library TestForwardDeclaration

define ToString(value System.Integer): null

define ToString(value System.Decimal): null

// LUKETODO:  come up with better example
define ToString(value System.Quantity):
   ToString(value.value)
