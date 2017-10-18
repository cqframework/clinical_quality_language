// @Test: ExpressionDef
context Patient
define Foo: 'Bar'

// @Test: ExpressionRef
define Life: 42
define Foo: Life

// @Test: FunctionDefinitions
define function "foo bar"(a Integer, b Integer) :
  a + b

define testValue: "foo bar" (1,2)

// @Test: FunctionOverloads
define function "foo bar" (a System.Integer) :
  a + 1

define function "foo bar" (a System.String) :
  'Hello ' + a

define testValue1: "foo bar"(1)
define testValue2: "foo bar"('World')
