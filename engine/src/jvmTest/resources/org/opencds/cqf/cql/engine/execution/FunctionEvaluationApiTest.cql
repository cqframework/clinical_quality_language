library FunctionEvaluationApiTest

define function func(a Integer, b Integer): a + b

define function funcWithOverloads(a Integer, b Integer): a + b
define function funcWithOverloads(a Decimal, b Decimal): a + b

define fluent function fluentFunc(a Integer): a + 1

define function externalFunc() returns Integer : external

define expr: 5
