library TestForwardDeclarationScoringImplicitConversionMultipleParams

define function caller():
   callee(1, 2, '')

define function callee(value1 System.Quantity, value2 System.Quantity, value3 System.String): null
define function callee(value1 System.Decimal, value2 System.Decimal): null
define function callee(value1 System.Decimal, value2 System.Decimal, value3 System.String): null
