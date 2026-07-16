library AggregateCoalesceTypeBug

define Sequence: { 1, 2, 3 }

// R starts as null (type Any). Coalesce(R, 1) should have result type Integer
// (common type of concrete args), not Any.
define Factorial:
  Sequence X
    aggregate R: Coalesce(R, 1) * X
