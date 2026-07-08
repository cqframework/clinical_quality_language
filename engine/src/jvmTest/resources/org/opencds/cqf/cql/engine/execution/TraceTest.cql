library TraceTest

define function func1(a Integer): a + 1

define expr1: 2

define function func2(b Integer): func1(b + 2) + func1(b + 3) + expr1

define expr2: func2(5)
