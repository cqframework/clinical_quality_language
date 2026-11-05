define A: B
define B: '123'

// TODO: Support forward function definitions...
define MySum: MyAddingFunction(1, 2)
define function MyAddingFunction(a Integer, b Integer):
  a + b

