library TranslationTestsCommon version '1.0'

define function TestMessage(condition Boolean, testName String, expected String, result String):
  if condition
    then testName + ' TEST PASSED'
  else Message('TEST SUITE FAILED', true, 'Test ' + testName + ' failed', 'Warning', 'Expected ' + expected + ' Found ' + result)

define function toString(value Decimal):
  if value is null
    then 'null'
  else ToString(value)

define function toString(value Quantity):
  if value is null
    then 'null'
  else ToString(value)
