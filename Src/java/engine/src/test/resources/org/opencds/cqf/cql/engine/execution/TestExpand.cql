library TestExpand

// { Interval[@2018-01-01, @2018-01-02], Interval[@2018-01-03, @2018-01-04] }
define "ExpandPer2Days":
  expand { Interval[@2018-01-01, @2018-01-04] } per 2 days

// { @2018-01-01, @2018-01-03 }
define "ExpandPer2DaysIntervalOverload":
  expand Interval[@2018-01-01, @2018-01-04] per 2 days

// { Interval[10, 10], Interval[11, 11], Interval[12, 12] }
define "ExpandPer1":
  expand { Interval[10.0, 12.5] } per 1

// { 10, 11, 12 }
define "ExpandPer1IntervalOverload":
  expand Interval[10.0, 12.5] per 1

// { Interval[10, 10], Interval[11, 11], Interval[12, 12] }
define "ExpandPer1Open":
  expand { Interval[10.0, 12.5) } per 1

// { 10, 11, 12 }
define "ExpandPer1OpenIntervalOverload":
  expand Interval[10.0, 12.5) per 1

// { }
define "ExpandPerMinute":
  expand { Interval[@T10, @T10] } per minute

// { }
define "ExpandPerMinuteIntervalOverload":
  expand Interval[@T10, @T10] per minute

// { Interval[10.0, 10.0], Interval[10.1, 10.1], Interval[10.2, 10.2], Interval[10.3, 10.3], Interval[10.4, 10.4], Interval[10.5, 10.5], Interval[10.6, 10.6], Interval[10.7, 10.7], Interval[10.8, 10.8], Interval[10.9, 10.9] }
define "ExpandPer0D1":
  expand { Interval[10, 10] } per 0.1

// { 10.0, 10.1, 10.2, 10.3, 10.4, 10.5, 10.6, 10.7, 10.8, 10.9 }
define "ExpandPer0D1IntervalOverload":
  expand Interval[10, 10] per 0.1
