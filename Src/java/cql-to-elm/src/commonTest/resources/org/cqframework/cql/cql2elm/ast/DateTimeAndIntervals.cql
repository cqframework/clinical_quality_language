library DateTimeAndIntervals version '1.0.0'
using System
context Patient

// Component extraction
define YearFrom: year from @2014-01-01T00:00:00
define MonthFrom: month from @2014-01-01T00:00:00
define DayFrom: day from @2014-01-01T00:00:00
define HourFrom: hour from @2014-01-01T00:00:00
define MinuteFrom: minute from @2014-01-01T00:00:00
define SecondFrom: second from @2014-01-01T00:00:00
define MillisecondFrom: millisecond from @2014-01-01T00:00:00.001
define DateFrom: date from @2014-01-01T00:00:00
define TimeFrom: time from @2014-01-01T00:00:00
define TimezoneOffsetFrom: timezoneoffset from @2014-01-01T00:00:00

// Duration/Difference between
define DaysBetween: days between @2014-01-01 and @2014-01-31
define MonthsBetween: months between @2014-01-01 and @2014-12-31
define DifferenceInDays: difference in days between @2014-01-01 and @2014-01-31

// Time boundaries
define StartOfInterval: start of Interval[1, 10]
define EndOfInterval: end of Interval[1, 10]

// Width
define WidthOfInterval: width of Interval[1, 10]

// Point from / singleton from
define PointFromInterval: point from Interval[1, 1]
define SingletonFromList: singleton from { 1 }

// Type extents
define MinimumInteger: minimum Integer
define MaximumInteger: maximum Integer
define MinimumDecimal: minimum Decimal
define MaximumDecimal: maximum Decimal
define MinimumDateTime: minimum DateTime
define MaximumDateTime: maximum DateTime
define MinimumTime: minimum Time
define MaximumTime: maximum Time

// Exists
define ExistsList: exists { 1, 2, 3 }

// Between
define BetweenExpression: 5 between 1 and 10
define ProperlyBetweenExpression: 5 properly between 1 and 10

// Interval operators
define ContainsExpression: Interval[1, 10] contains 5
define InExpression: 5 in Interval[1, 10]
define IncludesExpression: Interval[1, 10] includes Interval[2, 5]
define IncludedInExpression: Interval[2, 5] included in Interval[1, 10]
define ProperlyIncludesExpression: Interval[1, 10] properly includes Interval[2, 5]
define ProperlyIncludedInExpression: Interval[2, 5] properly included in Interval[1, 10]

define BeforeExpression: Interval[1, 5] before Interval[6, 10]
define AfterExpression: Interval[6, 10] after Interval[1, 5]
define MeetsExpression: Interval[1, 5] meets Interval[6, 10]
define MeetsBeforeExpression: Interval[1, 5] meets before Interval[6, 10]
define MeetsAfterExpression: Interval[6, 10] meets after Interval[1, 5]
define OverlapsExpression: Interval[1, 7] overlaps Interval[5, 10]
define OverlapsBeforeExpression: Interval[1, 7] overlaps before Interval[5, 10]
define OverlapsAfterExpression: Interval[5, 10] overlaps after Interval[1, 7]
define StartsExpression: Interval[1, 5] starts Interval[1, 10]
define EndsExpression: Interval[5, 10] ends Interval[1, 10]
