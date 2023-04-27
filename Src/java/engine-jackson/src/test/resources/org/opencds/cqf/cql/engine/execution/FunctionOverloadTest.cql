library FunctionOverload version '1.0.000'

define function TestAny(a Integer):
  a

define function TestAny(b String):
  b

define function TestAny(c Decimal):
  c

define function TestAny():
  'any'

define TestAnyFunctionWithInteger:
  TestAny(1)

define TestAnyFunctionWithString:
  TestAny('joe')

define TestAnyFunctionWithDecimal:
  TestAny(12.3)

define TestAnyFunctionWithNoArgs:
  TestAny()