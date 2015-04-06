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
