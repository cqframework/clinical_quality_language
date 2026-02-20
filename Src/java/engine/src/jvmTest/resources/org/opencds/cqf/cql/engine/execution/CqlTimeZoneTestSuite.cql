library CqlTimeZoneTestSuite version '2'

define function TestMessage(condition Boolean, testName String, expected String, result String):
  if condition
    then testName + ' TEST PASSED'
  else Message('TEST SUITE FAILED', true, 'Test ' + testName + ' failed', 'Warning', 'Expected ' + expected + ' Found ' + result)

define function toString(value Boolean):
  if value is null
    then 'null'
  else ToString(value)

define function toString(value Decimal):
  if value is null
    then 'null'
  else ToString(value)

define function toString(value Integer):
  if value is null
    then 'null'
  else ToString(value)

define function toString(value DateTime):
  if value is null
    then 'null'
  else ToString(value)

define function toString(value Date):
  if value is null
    then 'null'
  else ToString(value)

define function toString(value Time):
  if value is null
    then 'null'
  else ToString(value)

define function toString(value Quantity):
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

define function toString(value Concept):
  if value is null
    then 'null'
  else 'Concept { ' + toString(value.codes) + ' display: ' + value.display + ' }'

define function toString(value Tuple{a Integer}):
  if value is null
    then 'null'
  else 'Tuple{ a: ' + ToString(value.a) + ' }'

define function toString(value Tuple{hello String}):
  if value is null
    then 'null'
  else 'Tuple{ hello: ' + value.hello + ' }'

define function toString(value Tuple{a Integer, b String}):
  if value is null
    then 'null'
  else 'Tuple{ a: ' + ToString(value.a) + ', b: ' + value.b + ' }'

define function toString(value Tuple{a Integer, b Integer, c Integer}):
  if value is null
    then 'null'
  else 'Tuple{ a: ' + ToString(value.a) + ', b: ' + ToString(value.b) + ', c: ' + ToString(value.c) + ' }'

define function toString(value List<Integer>):
  if value is null
    then 'null'
  else Combine((value V return toString(V)), ', ')

define function toString(value Interval<Integer>):
  if value is null
    then 'null'
  else '[' + toString(start of value) + ', ' + toString(end of value) + ']'

define function toString(value Interval<Decimal>):
  if value is null
    then 'null'
  else '[' + toString(start of value) + ', ' + toString(end of value) + ']'

define function toString(value Interval<Quantity>):
  if value is null
    then 'null'
  else '[' + toString(start of value) + ', ' + toString(end of value) + ']'

define function toString(value Interval<DateTime>):
  if value is null
    then 'null'
  else '[' + toString(start of value) + ', ' + toString(end of value) + ']'

define function toString(value Interval<Date>):
  if value is null
    then 'null'
  else '[' + toString(start of value) + ', ' + toString(end of value) + ']'

define function toString(value Interval<Time>):
  if value is null
    then 'null'
  else '[' + toString(start of value) + ', ' + toString(end of value) + ']'

define function toString(value List<Interval<Integer>>):
  if value is null
    then 'null'
  else Combine((value V return toString(V)), ', ')

define function toString(value List<Interval<Decimal>>):
  if value is null
    then 'null'
  else Combine((value V return toString(V)), ', ')

define function toString(value List<Interval<Quantity>>):
  if value is null
    then 'null'
  else Combine((value V return toString(V)), ', ')

define function toString(value List<Interval<DateTime>>):
  if value is null
    then 'null'
  else Combine((value V return toString(V)), ', ')

define function toString(value List<Interval<Date>>):
  if value is null
    then 'null'
  else Combine((value V return toString(V)), ', ')

define function toString(value List<Interval<Time>>):
  if value is null
    then 'null'
  else Combine((value V return toString(V)), ', ')

define function toString(value List<Tuple{a Integer}>):
  if value is null
    then 'null'
  else Combine((value V return toString(V)), ', ')

define function toString(value List<Tuple{hello String}>):
  if value is null
    then 'null'
  else Combine((value V return toString(V)), ', ')

define function toString(value List<Tuple{a Integer, b String}>):
  if value is null
    then 'null'
  else Combine((value V return toString(V)), ', ')

define function toString(value List<Tuple{a Integer, b Integer, c Integer}>):
  if value is null
    then 'null'
  else Combine((value V return toString(V)), ', ')

define After_SameHour: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) after hour of DateTime(2000, 3, 15, 13, 14, 47, 500, +1.0)
define SameAs_SameHour: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) same hour as DateTime(2000, 3, 15, 13, 14, 47, 500, +1.0)
define SameOrAfter_HourBefore: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) same hour or after DateTime(2000, 3, 15, 14, 14, 47, 500, +1.0)
define SameOrBefore_SameHour: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) same hour or before DateTime(2000, 3, 15, 13, 14, 47, 500, +1.0)

define test_After_SameHour: TestMessage(not After_SameHour, 'After_SameHour', toString(false), toString(After_SameHour))
define test_SameAs_SameHour: TestMessage(SameAs_SameHour, 'SameAs_SameHour', toString(true), toString(SameAs_SameHour))
define test_SameOrAfter_HourBefore: TestMessage(not SameOrAfter_HourBefore, 'SameOrAfter_HourBefore', toString(false), toString(SameOrAfter_HourBefore))
define test_SameOrBefore_SameHour: TestMessage(SameOrBefore_SameHour, 'SameOrBefore_SameHour', toString(true), toString(SameOrBefore_SameHour))

