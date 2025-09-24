library TestNonForwardDeclarationScoringImplicitConversionMultipleParams

define function callee(value System.Decimal, value2 System.Decimal): null
define function callee(value System.Quantity, value2 System.Quantity): null

define function caller():
   callee(1, 2)