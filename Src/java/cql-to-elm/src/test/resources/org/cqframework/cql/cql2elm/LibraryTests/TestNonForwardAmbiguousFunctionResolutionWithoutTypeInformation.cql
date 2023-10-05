library TestAmbiguousFailOnAmbiguousFunctionResolutionWithoutTypeInformation

define TestAnyFunctionWithInteger:
  TestAny(1)

define TestAnyFunctionWithString:
  TestAny('joe')

define function TestAny(a Integer):
  a

define function TestAny(b String):
  b
