// @Test: ExpressionDef
context Patient
define Foo: 'Bar'

// @Test: ExpressionRef
define Life: 42
define Foo: Life

// @Test: FunctionDefinitions
define function foo (a Integer, b Integer) :
  a + b

define testValue: foo(1,2)

// @Test: FunctionOverloads
define function foo (a System.Integer) :
  a + 1

define function foo (a System.String) :
  'Hello ' + a

define testValue1: foo(1)
define testValue2: foo('World')
