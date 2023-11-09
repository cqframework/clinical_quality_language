library CqlAggregateQueryTest

//Aggregate clause
define AggregateSumWithStart:
  ({ 1, 2, 3, 4, 5 }) Num
    aggregate Result starting 1: Result + Num  // 15 + 1 (the initial value)

define AggregateSumWithNull:
  ({ 1, 2, 3, 4, 5 }) Num
    aggregate Result: Coalesce(Result, 0) + Num // 15 + 0 (the initial value from null)

define AggregateSumAll:
  ({ 1, 1, 2, 2, 2, 3, 4, 4, 5 }) Num
    aggregate all Result: Coalesce(Result, 0) + Num // 24 + 0

define AggregateSumDistinct:
  ({ 1, 1, 2, 2, 2, 3, 4, 4, 5 }) Num
    aggregate distinct Result: Coalesce(Result, 0) + Num // 15 + 0 (the initial value)


define First: {1}
define Second: {2}
define Third: {3}

define Multi:
   from First X, Second Y, Third Z
    aggregate Agg: Coalesce(Agg, 0) + X + Y + Z // 6

define "A": {1, 2}
define "B": {1, 2}
define "C": {1, 2}

define MegaMulti:
   from "A" X, "B" Y, "C" Z
    aggregate Agg starting 0: Agg + X + Y + Z // 36 -- (1+1+1)+(1+1+2)+(1+2+1)+(1+2+2)+(2+1+1)+(2+1+2)+(2+2+1)+(2+2+2)


define "1": {1, 2, 2, 1}
define "2": {1, 2, 1, 2}
define "3": {2, 1, 2, 1}

define MegaMultiDistinct:
   from "1" X, "2" Y, "3" Z
    aggregate distinct Agg starting 1: Agg + X + Y + Z // 37 -- 1 + (1+1+1)+(1+1+2)+(1+2+1)+(1+2+2)+(2+1+1)+(2+1+2)+(2+2+1)+(2+2+2)

