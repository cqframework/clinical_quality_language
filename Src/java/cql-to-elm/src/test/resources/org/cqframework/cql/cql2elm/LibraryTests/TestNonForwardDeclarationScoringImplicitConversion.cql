library TestNonForwardDeclarationScoringImplicitConversion

define function calledFunc(value System.Decimal): null
define function calledFunc(value System.Quantity): null

define function toString():
   calledFunc(1)
