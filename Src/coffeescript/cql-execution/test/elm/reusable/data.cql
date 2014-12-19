// @Test: ExpressionDef
context Patient
define Foo = 'Bar'

// @Test: ExpressionRef
define Life = 42
define Foo = Life

// @Test: FunctionDefinitions
define function foo (a:integer ,b:integer)
{ return a + b }
define testValue = foo(1,2)
