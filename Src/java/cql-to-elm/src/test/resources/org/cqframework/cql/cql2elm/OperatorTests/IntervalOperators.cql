define IntervalAfter: Interval[10, 15] after Interval [1, 5]
define PointIntervalAfter: 10 after Interval [1, 5]
define IntervalPointAfter: Interval[10, 15] after 5
define IntervalBefore: Interval[1, 5] before Interval[10, 15]
define PointIntervalBefore: 5 before Interval[10, 15]
define IntervalPointBefore: Interval[1, 5] before 10
define IntervalCollapse: collapse { Interval[1, 5], Interval[3, 7], Interval[5, 10], Interval[50, 100] }
define IntervalCollapseWithPer: collapse { Interval[1, 5], Interval[3, 7], Interval[5, 10], Interval[50, 100] } per 1 '1'
define IntervalContains: Interval[1, 5] contains 3
define IntervalEnd: end of Interval[1, 5]
define IntervalEnds: Interval[3, 5] ends Interval[1, 5]
define IntervalEqual: Interval[1, 1] = Interval[1, 1]
define IntervalExcept: Interval[1, 10] except Interval[5, 10]
define IntervalExpand: expand { Interval[1, 5] } per 1 '1' // TODO: Should be able to take advantage of implicit conversion of integer to quantity here
define DecimalIntervalExpand: expand { Interval[1.0, 2.0] } per 0.1 '1' // TODO: Should be able to take advantage of implicit conversion of decimal to quantity here
define QuantityIntervalExpand: expand { Interval[1 'm', 2 'm'] } per 0.1 'm'
define DateTimeIntervalExpand: expand { Interval[@2012-01-01T10:00:00, @2012-01-01T11:00:00] } per minute
define DateIntervalExpand: expand { Interval[@2012-01-01, @2012-01-31] } per day
define TimeIntervalExpand: expand { Interval[@T10:00:00, @T11:00:00] } per minute
define IntervalIn: 5 in Interval[1, 10]
define IntervalIncludes: Interval[1, 10] includes Interval[3, 7]
define IntervalIncludedIn: Interval[3, 7] included in Interval[1, 10]
define IntervalIntersect: Interval[1, 10] intersect Interval[3, 7]
define IntervalMeets: Interval[1, 5] meets Interval[6, 10]
define IntervalMeetsBefore: Interval[1, 5] meets before Interval[6, 10]
define IntervalMeetsAfter: Interval[6, 10] meets after Interval[1, 5]
define IntervalOverlaps: Interval[1, 7] overlaps Interval[3, 10]
define IntervalOverlapsBefore: Interval[1, 7] overlaps before Interval[3, 10]
define IntervalOverlapsAfter: Interval[3, 10] overlaps after Interval[1, 7]
define IntervalProperIncludes: Interval[1, 10] properly includes Interval[3, 7]
define IntervalProperIncludedIn: Interval[3, 7] properly included in Interval[1, 10]
define IntervalStart: start of Interval[1, 10]
define IntervalStarts: Interval[1, 5] starts Interval[1, 10]
define IntervalUnion: Interval[1, 7] union Interval [3, 10]
define IntervalWidth: width of Interval[1, 10]
define IntervalPointFrom: point from Interval[1, 1]
define IntervalSize: Size(Interval[1, 1])
