library Library1

include Library2

define expr1: 1 + 2

define expr2: Library2.expr1 + 3

define expr3: Library2.func1(1, 2, true)

define expr4: if true then 1 else
  0

define expr5: if true then 1
  else 0

define expr6: if
  true then 1 else 0

define expr7: if false then 1 else
  0

define expr8: if false then 1
  else 0

define expr9: AnyTrue({
    false,
    true,
    false
 })
