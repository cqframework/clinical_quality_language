library FunctionOverloadTest version '1.0.000'

define function TestAny(a Integer):
  a

define function TestAny(b String):
  b

define function TestAny(c Decimal):
  c

define function TestAny():
  'any'

define function TestAny(a Integer, b Integer):
  a + b


define TestAnyFunctionWithInteger:
  TestAny(1)

define TestAnyFunctionWithString:
  TestAny('joe')

define TestAnyFunctionWithDecimal:
  TestAny(12.3)

define TestAnyFunctionWithNoArgs:
  TestAny()

  define TestAnyFunctionWith2Args:
  TestAny(1, 2)