library TestAmbiguousFailOnAmbiguousFunctionResolutionWithoutTypeInformation

define function TestAny(a Integer):
  a

define function TestAny(b String):
  b

define TestAnyFunctionWithInteger:
  TestAny(1)

define TestAnyFunctionWithString:
  TestAny('joe')
