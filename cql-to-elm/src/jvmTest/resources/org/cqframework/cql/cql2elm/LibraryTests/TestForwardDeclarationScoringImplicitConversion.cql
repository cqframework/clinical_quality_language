library TestForwardDeclarationScoringImplicitConversion

define function toString():
   calledFunc(1)

define function calledFunc(value System.Decimal): null
define function calledFunc(value System.Quantity): null