library Aggregate

define Sequence:
  { 1, 2, 3, 4, 5, 6, 7 }

define Factorial:
  Sequence X
    aggregate R: Coalesce(R, 1) * X

define ProductWithStart:
  Sequence X
    aggregate R starting 1: R * X

define Intervals:
  {
    Interval[@2012-01-01, @2012-02-28],
    Interval[@2012-02-01, @2012-03-31],
    Interval[@2012-03-01, @2012-04-30]
  }

define RolledOutIntervals:
  Intervals M
    aggregate R starting (null as List<Interval<Date>>): R union ({
        M X
          let S: Max({ end of Last(R) + 1 day, start of X }),
            E: S + duration in days of X
          return Interval[S, E]
    })
