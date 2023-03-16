library MethodOverload version '1.0.000'

using FHIR version '4.0.1'

define function TestAny(a Integer):
  a

define function TestAny(b String):
  b

define TestAnyFunctionWithInteger:
  TestAny(1)

define TestAnyFunctionWithString:
  TestAny('joe')