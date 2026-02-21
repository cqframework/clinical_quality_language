library BacktraceTest

define function F(X Integer):
  (1) _ // Hack to allow let
    let A: 7
      return if X = 3 then
               Message(X, true, 'X', 'Error', 'X')
             else
               X + 1

define function G(Y Integer, Z String):
  { F(Y), F(Y + 1) }

define E1:
  G(1, 'hi')

define E2:
  G(2, 'hi')