library CqlFunctionTests version '1'

// Github issue #76
define function f1(arg String): arg
define FunctionTestStringArg: f1('hello')
define FunctionTestNullStringArg: f1(null as String)

define function f2(arg String, anotherArg Integer):
  arg + ToString(anotherArg)
define FunctionTestMultipleArgs: f2('hell', 0)
define FunctionTestNullMultipleArgs: f2('hell', null)

define function f2(arg String, anotherArg Integer, yetAnotherArg Decimal):
  arg + ToString(anotherArg) + ToString(yetAnotherArg)
define FunctionTestOverload: f2('hell', 0, 0.000)
define FunctionTestNullOverload: f2('hell', 0, null)

// Github issue #74
define function f3(x Tuple { y Integer }): x.y
define FunctionTestTupleArg: f3(Tuple { y: 3 })
define FunctionTestNullTupleArg: f3(Tuple { y: null as Integer })

define function f3(x Quantity): x.unit
define FunctionTestQuantityArg: f3(12'cm')
define FunctionTestNullQuantityArg: f3(null as Quantity)