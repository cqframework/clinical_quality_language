library ProfilingTest

define function F(X Integer): X + 1

define function G(Count Integer):
  ((expand {Interval[0, Count]}) i return all start of i)
    x return all { F(x), F(x + 1) }

define E1:
  G(5)

define E2:
  G(10)

define E3: E2

define E4: E2