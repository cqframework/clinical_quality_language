library SignatureLevelFallback version '0.0.1'

define function TestAny(a Integer):
  a

define function TestAny(b String):
  b

define TestAnyFunctionWithInteger:
  TestAny(1)

define TestAnyFunctionWithString:
  TestAny('joe')