library CqlTestSuite version '2'

define function something(text String): null

define function toString(value Integer):
  if value is null
    then 'null'
  else ToString(value)

define function toString(value Code):
  if value is null
    then 'null'
  else 'Code { code: ' + value.code + ' system: ' + value.system + ' version: ' + value.version + ' display: ' + value.display + ' }'

define function toString(value List<Code>):
  if value is null
    then 'null'
  else Combine((value V return toString(V)), ', ')

define function toString(value List<Integer>):
  if value is null
    then 'null'
  else Combine((value V return toString(V)), ', ')

define innocuous_test_1: something('myString')

define failed_test_1: something(toString({1,2,3,4,5,6,7,8,9,10}))
