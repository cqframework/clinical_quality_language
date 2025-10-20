library Tests

include Library1

define function assertEquals(expected Integer, actual Integer):
  Message('Assertion failed', not (expected = actual), 'Undefined', 'Error',  'Expected ' + ToString(expected) + ' Found ' + ToString(actual))

define test1: assertEquals(Library1.a, 3)
define test2: assertEquals(Library1.b, 4)
define test3: assertEquals(Library1.c, 3)
