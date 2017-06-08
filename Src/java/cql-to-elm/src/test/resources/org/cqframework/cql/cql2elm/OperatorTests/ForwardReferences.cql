library ForwardReferences version '2'

define A: B
define B: '123'

define MySum: MyAddingFunction(1, 2)
define function MyAddingFunction(a Integer, b Integer):
  a + b

