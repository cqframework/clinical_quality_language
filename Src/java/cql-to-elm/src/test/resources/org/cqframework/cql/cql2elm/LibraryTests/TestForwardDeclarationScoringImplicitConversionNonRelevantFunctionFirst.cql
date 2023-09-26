library TestForwardDeclaration

define function toString():
   calledFunc(1)

define function calledFunc(value System.Quantity): null
define function calledFunc(value System.Decimal): null

// LUKETODO:  come up with better example
