// NOTE: This library must be DUPLICATED in the data.cql file as well!
library COM2
using QUICK
parameter SomeNumber default 17

context Patient

define TheParameter:
  SomeNumber

define function addToParameter(a Integer):
  SomeNumber + a

define function multiply(a Integer, b Integer) :
  a * b

define function square(a Integer):
  multiply(a, a)

define TwoTimesThree:
  multiply(2, 3)

define Two:
  2

define function addTwo(a Integer):
  a + Two

define TwoPlusOne:
  Two + 1

define SortUsingFunction:
  ({1, 3, 2, 5, 4}) N return Tuple{N: N} sort by square(N)
