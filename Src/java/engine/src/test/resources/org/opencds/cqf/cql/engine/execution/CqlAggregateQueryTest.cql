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