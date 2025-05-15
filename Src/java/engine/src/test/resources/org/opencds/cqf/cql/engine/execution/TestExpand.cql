library TestExpand

// { Interval[@2018-01-01, @2018-01-01], Interval[@2018-01-02, @2018-01-02], Interval[@2018-01-03, @2018-01-03], Interval[@2018-01-04, @2018-01-04] }
define "ExpandPerDay":
  expand { Interval[@2018-01-01, @2018-01-04] } per day

//{ Interval[@2018-01-01, @2018-01-02], Interval[@2018-01-03, @2018-01-04] }
define "ExpandPer2Days":
 expand { Interval[@2018-01-01, @2018-01-04] } per 2 days

// { Interval[@T10:00, @T11:00), Interval[@T11:00, @T12:00) }
define "ExpandPerHour":
 expand { Interval[@T10:00, @T12:30] } per hour

 //  { Interval[10, 10], Interval[11, 11], Interval[12, 12] }
define "ExpandPer1":
 expand { Interval[10.0, 12.5] } per 1

// {}
define "ExpandPerMinute":
  expand { Interval[@T10, @T10] } per minute

// { Interval[10.0, 10.0], Interval[10.1, 10.1], Interval[10.2, 10.2], Interval[10.3, 10.3], Interval[10.4, 10.4], Interval[10.5, 10.5], Interval[10.6, 10.6], Interval[10.7, 10.7], Interval[10.8, 10.8], Interval[10.9, 10.9] }
define "ExpandPer0D1":
  expand { Interval[10, 10] } per 0.1

// { Interval[1, 1], Interval[2, 2], Interval[3, 3], Interval[4, 4], Interval[5, 5], Interval[6, 6], Interval[7, 7], Interval[8, 8], Interval[9, 9], Interval[10, 10] }
define "ExpandInterval":
   expand { Interval[1, 10] }

// { Interval[1, 2], Interval[3, 4], Interval[5, 6], Interval[7, 8], Interval[9, 10] }
define "ExpandIntervalPer2":
   expand { Interval[1, 10] } per 2

// {1, 2, 3, 4, 5}
define "Integers":
 expand Interval[1, 5]

 // {1, 2, 3, 4}
define "Integers Open":
 expand Interval[1, 5)

// {1, 3, 5}
define "Integers Per 2":
 expand Interval[1, 5] per 2

 // {1, 3}
define "Integers Per 2 Open":
 expand Interval[1, 5) per 2

// {1.0, 1.1, 1.2, 1.3, 1.4, 1.5 }
define "Decimal":
 expand Interval[1.0, 1.5]

// {1.0, 1.2, 1.4 }
define "Decimal Per 0_2":
 expand Interval[1.0, 1.4] per 0.2

// {1.0, 1.2 }
define "Decimal Per 0_2 Open":
 expand Interval[1.0, 1.4) per 0.2

// { @10, @11, @12 }
define "Time":
 expand Interval[@T10, @T12]

// { @10, @11 }
define "Time Open":
 expand Interval[@T10, @T12)

// 
define "Time Per Hour":
 expand Interval[@T10, @T11] per minute
   


