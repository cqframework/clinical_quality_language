library Tests

include Library1

define function assertEquals(expected Integer, actual Integer):
  Message('Assertion failed', not (expected = actual), 'Undefined', 'Error',  'Expected ' + ToString(expected) + ' Found ' + ToString(actual))
define function assertEquals(expected Boolean, actual Boolean):
  Message('Assertion failed', not (expected = actual), 'Undefined', 'Error',  'Expected ' + ToString(expected) + ' Found ' + ToString(actual))

define test1: assertEquals(Library1.expr1, 3)
define test2: assertEquals(Library1.expr2, 4)
define test3: assertEquals(Library1.expr3, 3)
define test4: assertEquals(Library1.expr4, 1)
define test5: assertEquals(Library1.expr5, 1)
define test6: assertEquals(Library1.expr6, 1)
define test7: assertEquals(Library1.expr7, 0)
define test8: assertEquals(Library1.expr8, 0)
define test9: assertEquals(Library1.expr9, true)
