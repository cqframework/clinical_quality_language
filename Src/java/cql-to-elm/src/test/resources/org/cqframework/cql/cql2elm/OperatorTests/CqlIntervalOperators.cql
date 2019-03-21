library CqlIntervalOperatorsTest version '1'

context Unspecified

//After
//define TestAfterNull: null after IntegerIntervalTest // Could use the upcast, but do we really need to support this construct?
define IntegerIntervalAfterTrue: IntegerIntervalTest2 after IntegerIntervalTest
define IntegerIntervalAfterFalse: IntegerIntervalTest after IntegerIntervalTest2
define IntegerIntervalPointAfterTrue: 12 after IntegerIntervalTest
define IntegerIntervalPointAfterFalse: 9 after IntegerIntervalTest
define IntegerIntervalAfterPointTrue: IntegerIntervalTest2 after 5
define IntegerIntervalAfterPointFalse: IntegerIntervalTest2 after 12
define DecimalIntervalAfterTrue: DecimalIntervalTest2 after DecimalIntervalTest
define DecimalIntervalAfterFalse: DecimalIntervalTest after DecimalIntervalTest2
define DecimalIntervalPointAfterTrue: 12.0 after DecimalIntervalTest
define DecimalIntervalPointAfterFalse: 9.0 after DecimalIntervalTest
define DecimalIntervalAfterPointTrue: DecimalIntervalTest2 after 5.0
define DecimalIntervalAfterPointFalse: DecimalIntervalTest2 after 12.0
define QuantityIntervalAfterTrue: QuantityIntervalTest2 after QuantityIntervalTest
define QuantityIntervalAfterFalse: QuantityIntervalTest after QuantityIntervalTest2
define QuantityIntervalPointAfterTrue: 12.0'g' after QuantityIntervalTest
define QuantityIntervalPointAfterFalse: 9.0'g' after QuantityIntervalTest
define QuantityIntervalAfterPointTrue: QuantityIntervalTest2 after 5.0'g'
define QuantityIntervalAfterPointFalse: QuantityIntervalTest2 after 12.0'g'
define DateTimeAfterTrue: Interval[DateTime(2012, 1, 1), DateTime(2012, 1, 15)] after DateTime(2011, 12, 31)
define DateTimeAfterFalse: Interval[DateTime(2012, 1, 1), DateTime(2012, 1, 15)] after DateTime(2012, 12, 31)
define TimeAfterTrue: Interval[@T15:59:59.999, @T20:59:59.999] after @T12:59:59.999
define TimeAfterFalse: Interval[@T15:59:59.999, @T20:59:59.999] after @T17:59:59.999

//Before
//define TestBeforeNull : null before IntegerIntervalTest // Could use the upcast, but do we really need to support this construct?
define IntegerIntervalBeforeFalse: IntegerIntervalTest2 before IntegerIntervalTest
define IntegerIntervalBeforeTrue: IntegerIntervalTest before IntegerIntervalTest2
define IntegerIntervalPointBeforeTrue: 9 before IntegerIntervalTest2
define IntegerIntervalPointBeforeFalse: 9 before IntegerIntervalTest
define IntegerIntervalBeforePointTrue: IntegerIntervalTest before 11
define IntegerIntervalBeforePointFalse: IntegerIntervalTest before 8
define DecimalIntervalBeforeFalse: DecimalIntervalTest2 before DecimalIntervalTest
define DecimalIntervalBeforeTrue: DecimalIntervalTest before DecimalIntervalTest2
define DecimalIntervalPointBeforeTrue: 9.0 before DecimalIntervalTest2
define DecimalIntervalPointBeforeFalse: 9.0 before DecimalIntervalTest
define DecimalIntervalBeforePointTrue: DecimalIntervalTest before 11.0
define DecimalIntervalBeforePointFalse: DecimalIntervalTest before 8.0
define QuantityIntervalBeforeTrue: QuantityIntervalTest before QuantityIntervalTest2
define QuantityIntervalBeforeFalse: QuantityIntervalTest2 before QuantityIntervalTest
define QuantityIntervalPointBeforeTrue: QuantityIntervalTest before 12.0'g'
define QuantityIntervalPointBeforeFalse: QuantityIntervalTest before 9.0'g'
define QuantityIntervalBeforePointTrue:  5.0'g' before QuantityIntervalTest2
define QuantityIntervalBeforePointFalse: 12.0'g' before QuantityIntervalTest2
define DateTimeBeforeTrue: Interval[DateTime(2012, 1, 1), DateTime(2012, 1, 15)] before DateTime(2012, 2, 27)
define DateTimeBeforeFalse: Interval[DateTime(2012, 1, 1), DateTime(2012, 1, 15)] before DateTime(2011, 12, 31)
define TimeBeforeTrue: Interval[@T15:59:59.999, @T20:59:59.999] before @T22:59:59.999
define TimeBeforeFalse: Interval[@T15:59:59.999, @T20:59:59.999] before @T10:59:59.999

//Collapse
define TestCollapseNull: collapse {Interval(null, null)}
define IntegerIntervalCollapse: collapse { Interval[1,5], Interval[3,7], Interval[12,19], Interval[7,10] }
define IntegerIntervalCollapse2: collapse { Interval[1,2], Interval[3,7], Interval[10,19], Interval[7,10] }
define DecimalIntervalCollapse: collapse { Interval[1.0,5.0], Interval[3.0,7.0], Interval[12.0,19.0], Interval[7.0,10.0] }
define QuantityIntervalCollapse: collapse { Interval[1.0 'g',5.0 'g'], Interval[3.0 'g',7.0 'g'], Interval[12.0 'g',19.0 'g'], Interval[7.0 'g',10.0 'g'] }
define DateTimeCollapse: collapse { Interval[DateTime(2012, 1, 1), DateTime(2012, 1, 15)], Interval[DateTime(2012, 1, 10), DateTime(2012, 1, 25)], Interval[DateTime(2012, 5, 10), DateTime(2012, 5, 25)], Interval[DateTime(2012, 5, 20), DateTime(2012, 5, 30)] }
define TimeCollapse: collapse { Interval[@T01:59:59.999, @T10:59:59.999], Interval[@T08:59:59.999, @T15:59:59.999], Interval[@T17:59:59.999, @T20:59:59.999], Interval[@T18:59:59.999, @T22:59:59.999] }

//Contains
define TestContainsNull: IntegerIntervalTest contains null
define IntegerIntervalContainsTrue: IntegerIntervalTest contains 5
define IntegerIntervalContainsFalse: IntegerIntervalTest contains 25
define DecimalIntervalContainsTrue: DecimalIntervalTest contains 8.0
define DecimalIntervalContainsFalse: DecimalIntervalTest contains 255.0
define QuantityIntervalContainsTrue: QuantityIntervalTest contains 2.0 'g'
define QuantityIntervalContainsFalse: QuantityIntervalTest contains 100.0 'g'
define DateTimeContainsTrue: Interval[DateTime(2012, 1, 1), DateTime(2012, 1, 15)] contains DateTime(2012, 1, 10)
define DateTimeContainsFalse: Interval[DateTime(2012, 1, 1), DateTime(2012, 1, 15)] contains DateTime(2012, 1, 16)
define TimeContainsTrue: Interval[@T01:59:59.999, @T10:59:59.999] contains @T05:59:59.999
define TimeContainsFalse: Interval[@T01:59:59.999, @T10:59:59.999] contains @T15:59:59.999

//End
define IntegerIntervalEnd: end of IntegerIntervalTest
define DecimalIntervalEnd: end of DecimalIntervalTest
define QuantityIntervalEnd: end of QuantityIntervalTest
define DateTimeIntervalEnd: end of DateTimeIntervalTest
define TimeIntervalEnd: end of TimeIntervalTest

//Ends
define TestEndsNull: IntegerIntervalTest ends Interval(null, null)
define IntegerIntervalEndsTrue: IntegerIntervalTest4 ends IntegerIntervalTest
define IntegerIntervalEndsFalse: IntegerIntervalTest3 ends IntegerIntervalTest
define DecimalIntervalEndsTrue: DecimalIntervalTest3 ends DecimalIntervalTest
define DecimalIntervalEndsFalse: DecimalIntervalTest2 ends DecimalIntervalTest
define QuantityIntervalEndsTrue: QuantityIntervalTest3 ends QuantityIntervalTest
define QuantityIntervalEndsFalse: QuantityIntervalTest2 ends QuantityIntervalTest
define DateTimeEndsTrue: Interval[DateTime(2012, 1, 5), DateTime(2012, 1, 15)] ends Interval[DateTime(2012, 1, 1), DateTime(2012, 1, 15)]
define DateTimeEndsFalse: Interval[DateTime(2012, 1, 5), DateTime(2012, 1, 15)] ends Interval[DateTime(2012, 1, 1), DateTime(2012, 1, 16)]
define TimeEndsTrue: Interval[@T05:59:59.999, @T10:59:59.999] ends Interval[@T01:59:59.999, @T10:59:59.999]
define TimeEndsFalse: Interval[@T05:59:59.999, @T10:59:59.999] ends Interval[@T01:59:59.999, @T11:59:59.999]

//Equal
define TestEqualNull: IntegerIntervalTest = Interval(null, null)
define IntegerIntervalEqualTrue: IntegerIntervalTest = IntegerIntervalTest
define IntegerIntervalEqualFalse: IntegerIntervalTest = IntegerIntervalTest2
define DecimalIntervalEqualTrue: DecimalIntervalTest = DecimalIntervalTest
define DecimalIntervalEqualFalse: DecimalIntervalTest = DecimalIntervalTest2
define QuantityIntervalEqualTrue: QuantityIntervalTest = QuantityIntervalTest
define QuantityIntervalEqualFalse: QuantityIntervalTest = QuantityIntervalTest2
define DateTimeEqualTrue: Interval[DateTime(2012, 1, 5, 0, 0, 0, 0), DateTime(2012, 1, 15, 0, 0, 0, 0)] = Interval[DateTime(2012, 1, 5, 0, 0, 0, 0), DateTime(2012, 1, 15, 0, 0, 0, 0)]
define DateTimeEqualFalse: Interval[DateTime(2012, 1, 5, 0, 0, 0, 0), DateTime(2012, 1, 15, 0, 0, 0, 0)] = Interval[DateTime(2012, 1, 5, 0, 0, 0, 0), DateTime(2012, 1, 16, 0, 0, 0, 0)]
define TimeEqualTrue: Interval[@T05:59:59.999, @T10:59:59.999] = Interval[@T05:59:59.999, @T10:59:59.999]
define TimeEqualFalse: Interval[@T05:59:59.999, @T10:59:59.999] = Interval[@T05:59:59.999, @T10:58:59.999]

//Except
define NullInterval: Interval[null, null]
define TestExceptNull: NullInterval except NullInterval
define IntegerIntervalExcept1to3: IntegerIntervalTest except IntegerIntervalTest4
define IntegerIntervalExceptNull: Interval[1, 10] except Interval[3, 7]
define DecimalIntervalExcept1to3: DecimalIntervalTest except DecimalIntervalTest3
define DecimalIntervalExceptNull: Interval[1.0, 10.0] except Interval[3.0, 7.0]
define QuantityIntervalExcept1to4: QuantityIntervalTest except QuantityIntervalTest3
define Except12: Interval[1, 4] except Interval[3, 6]
define ExceptDateTime: Interval[DateTime(2012, 1, 5), DateTime(2012, 1, 15)] except Interval[DateTime(2012, 1, 7), DateTime(2012, 1, 15)]
define ExceptDateTime2: Interval[DateTime(2012, 1, 7), DateTime(2012, 1, 16)] except Interval[DateTime(2012, 1, 5), DateTime(2012, 1, 12)]
define ExceptTime: Interval[@T05:59:59.999, @T10:59:59.999] except Interval[@T08:59:59.999, @T10:59:59.999]
define ExceptTime2: Interval[@T08:59:59.999, @T11:59:59.999] except Interval[@T05:59:59.999, @T10:59:59.999]

//In
define TestInNull: 5 in NullInterval
define IntegerIntervalInTrue: 5 in IntegerIntervalTest
define IntegerIntervalInFalse: 500 in IntegerIntervalTest
define DecimalIntervalInTrue: 9.0 in DecimalIntervalTest
define DecimalIntervalInFalse: -2.0 in DecimalIntervalTest
define QuantityIntervalInTrue: 1.0 'g' in QuantityIntervalTest
define QuantityIntervalInFalse: 55.0 'g' in QuantityIntervalTest
define DateTimeInTrue: DateTime(2012, 1, 7) in Interval[DateTime(2012, 1, 5), DateTime(2012, 1, 15)]
define DateTimeInFalse: DateTime(2012, 1, 17) in Interval[DateTime(2012, 1, 5), DateTime(2012, 1, 15)]
define DateTimeInNullTrue: DateTime(2012, 1, 7) in Interval[DateTime(2012, 1, 5), null]
define TimeInTrue: @T07:59:59.999 in Interval[@T05:59:59.999, @T10:59:59.999]
define TimeInFalse: @T17:59:59.999 in Interval[@T05:59:59.999, @T10:59:59.999]
define TimeInNull: null in Interval[@T05:59:59.999, @T10:59:59.999]

//Includes
define TestIncludesNull: IntegerIntervalTest includes null
define IntegerIntervalIncludesTrue: IntegerIntervalTest includes IntegerIntervalTest4
define IntegerIntervalIncludesFalse: IntegerIntervalTest includes IntegerIntervalTest3
define DecimalIntervalIncludesTrue: DecimalIntervalTest includes DecimalIntervalTest3
define DecimalIntervalIncludesFalse: DecimalIntervalTest includes DecimalIntervalTest2
define QuantityIntervalIncludesTrue: QuantityIntervalTest includes QuantityIntervalTest3
define QuantityIntervalIncludesFalse: QuantityIntervalTest includes QuantityIntervalTest2
define DateTimeIncludesTrue: Interval[DateTime(2012, 1, 5), DateTime(2012, 1, 15)] includes Interval[DateTime(2012, 1, 7), DateTime(2012, 1, 14)]
define DateTimeIncludesFalse: Interval[DateTime(2012, 1, 5), DateTime(2012, 1, 15)] includes Interval[DateTime(2012, 1, 4), DateTime(2012, 1, 14)]
define TimeIncludesTrue: Interval[@T05:59:59.999, @T10:59:59.999] includes Interval[@T06:59:59.999, @T09:59:59.999]
define TimeIncludesFalse: Interval[@T05:59:59.999, @T10:59:59.999] includes Interval[@T04:59:59.999, @T09:59:59.999]

//Included In
//define TestIncludedInNull: null included in IntegerIntervalTest
define IntegerIntervalIncludedInTrue: IntegerIntervalTest4 included in IntegerIntervalTest
define IntegerIntervalIncludedInFalse: IntegerIntervalTest3 included in IntegerIntervalTest
define DecimalIntervalIncludedInTrue: DecimalIntervalTest3 included in DecimalIntervalTest
define DecimalIntervalIncludedInFalse: DecimalIntervalTest2 included in DecimalIntervalTest
define QuantityIntervalIncludedInTrue: QuantityIntervalTest3 included in QuantityIntervalTest
define QuantityIntervalIncludedInFalse: QuantityIntervalTest2 included in QuantityIntervalTest
define DateTimeIncludedInTrue: Interval[DateTime(2012, 1, 7), DateTime(2012, 1, 14)] included in Interval[DateTime(2012, 1, 5), DateTime(2012, 1, 15)]
define DateTimeIncludedInFalse: Interval[DateTime(2012, 1, 4), DateTime(2012, 1, 14)] included in Interval[DateTime(2012, 1, 5), DateTime(2012, 1, 15)]
define TimeIncludedInTrue: Interval[@T06:59:59.999, @T09:59:59.999] included in Interval[@T05:59:59.999, @T10:59:59.999]
define TimeIncludedInFalse: Interval[@T04:59:59.999, @T09:59:59.999] included in Interval[@T05:59:59.999, @T10:59:59.999]

//Intersect
//define TestIntersectNull: IntegerIntervalTest intersect Interval[5, null)
define IntegerIntervalIntersectTest4to10: IntegerIntervalTest intersect IntegerIntervalTest4
define IntegerIntervalIntersectTestNull: IntegerIntervalTest intersect IntegerIntervalTest2
define DecimalIntervalIntersectTest4to10: DecimalIntervalTest intersect DecimalIntervalTest3
define DecimalIntervalIntersectTestNull: DecimalIntervalTest intersect DecimalIntervalTest2
define QuantityIntervalIntersectTest5to10: QuantityIntervalTest intersect QuantityIntervalTest3
define QuantityIntervalIntersectTestNull: QuantityIntervalTest intersect QuantityIntervalTest2
define DateTimeIntersect: Interval[DateTime(2012, 1, 7), DateTime(2012, 1, 14)] intersect Interval[DateTime(2012, 1, 7), DateTime(2012, 1, 10)]
define TimeIntersect: Interval[@T04:59:59.999, @T09:59:59.999] intersect Interval[@T04:59:59.999, @T06:59:59.999]

//Equivalent
define IntegerIntervalEquivalentTrue: IntegerIntervalTest ~ IntegerIntervalTest
define IntegerIntervalEquivalentFalse: IntegerIntervalTest3 ~ IntegerIntervalTest
define DecimalIntervalEquivalentTrue: DecimalIntervalTest ~ DecimalIntervalTest
define DecimalIntervalEquivalentFalse: DecimalIntervalTest2 ~ DecimalIntervalTest
define QuantityIntervalEquivalentTrue: QuantityIntervalTest ~ QuantityIntervalTest
define QuantityIntervalEquivalentFalse: QuantityIntervalTest2 ~ QuantityIntervalTest
define DateTimeEquivalentTrue: Interval[DateTime(2012, 1, 7), DateTime(2012, 1, 14)] ~ Interval[DateTime(2012, 1, 7), DateTime(2012, 1, 14)]
define DateTimeEquivalentFalse: Interval[DateTime(2012, 1, 7), DateTime(2012, 1, 14)] ~ Interval[DateTime(2012, 1, 7), DateTime(2012, 1, 15)]
define TimeEquivalentTrue: Interval[@T04:59:59.999, @T09:59:59.999] ~ Interval[@T04:59:59.999, @T09:59:59.999]
define TimeEquivalentFalse: Interval[@T04:59:59.999, @T09:59:59.999] ~ Interval[@T04:58:59.999, @T09:59:59.999]

//Meets
define TestMeetsNull: Interval(null, 5] meets Interval(null, 15)
define IntegerIntervalMeetsTrue: IntegerIntervalTest meets IntegerIntervalTest2
define IntegerIntervalMeetsFalse: IntegerIntervalTest meets IntegerIntervalTest3
define DecimalIntervalMeetsTrue: Interval[3.01, 5.00000001] meets Interval[5.00000002, 8.50]
define DecimalIntervalMeetsFalse: Interval[3.01, 5.00000001] meets Interval[5.5, 8.50]
define QuantityIntervalMeetsTrue: Interval[3.01 'g', 5.00000001 'g'] meets Interval[5.00000002 'g', 8.50 'g']
define QuantityIntervalMeetsFalse: Interval[3.01 'g', 5.00000001 'g'] meets Interval[5.5 'g', 8.50 'g']
define DateTimeMeetsTrue: Interval[DateTime(2012, 1, 7), DateTime(2012, 1, 14)] meets Interval[DateTime(2012, 1, 15), DateTime(2012, 1, 25)]
define DateTimeMeetsFalse: Interval[DateTime(2012, 1, 7), DateTime(2012, 1, 14)] meets Interval[DateTime(2012, 1, 20), DateTime(2012, 1, 25)]
define TimeMeetsTrue: Interval[@T04:59:59.999, @T09:59:59.999] meets Interval[@T10:00:00.000, @T19:59:59.999]
define TimeMeetsFalse: Interval[@T04:59:59.999, @T09:59:59.999] meets Interval[@T10:12:00.000, @T19:59:59.999]

//MeetsBefore
define TestMeetsBeforeNull: Interval(null, 5] meets before Interval(null, 25]
define IntegerIntervalMeetsBeforeTrue: IntegerIntervalTest meets before IntegerIntervalTest2
define IntegerIntervalMeetsBeforeFalse: IntegerIntervalTest meets before IntegerIntervalTest3
define DecimalIntervalMeetsBeforeTrue: Interval[3.50000001, 5.00000011] meets before Interval[5.00000012, 8.50]
define DecimalIntervalMeetsBeforeFalse: Interval[8.01, 15.00000001] meets before Interval[15.00000000, 18.50]
define QuantityIntervalMeetsBeforeTrue: Interval[3.50000001 'g', 5.00000011 'g'] meets before Interval[5.00000012 'g', 8.50 'g']
define QuantityIntervalMeetsBeforeFalse: Interval[8.01 'g', 15.00000001 'g'] meets before Interval[15.00000000 'g', 18.50 'g']
define DateTimeMeetsBeforeTrue: Interval[DateTime(2012, 1, 7), DateTime(2012, 1, 14)] meets Interval[DateTime(2012, 1, 15), DateTime(2012, 1, 25)]
define DateTimeMeetsBeforeFalse: Interval[DateTime(2012, 1, 7), DateTime(2012, 1, 14)] meets Interval[DateTime(2012, 1, 20), DateTime(2012, 1, 25)]
define TimeMeetsBeforeTrue: Interval[@T04:59:59.999, @T09:59:59.999] meets Interval[@T10:00:00.000, @T19:59:59.999]
define TimeMeetsBeforeFalse: Interval[@T04:59:59.999, @T09:59:59.999] meets Interval[@T10:12:00.000, @T19:59:59.999]

//MeetsAfter
define TestMeetsAfterNull: Interval(null, 5] meets after Interval[11, null)
define IntegerIntervalMeetsAfterTrue: IntegerIntervalTest2 meets after IntegerIntervalTest
define IntegerIntervalMeetsAfterFalse: IntegerIntervalTest3 meets after IntegerIntervalTest
define DecimalIntervalMeetsAfterTrue: Interval[55.00000123, 128.032156] meets after Interval[12.00258, 55.00000122]
define DecimalIntervalMeetsAfterFalse: Interval[55.00000124, 150.222222] meets after Interval[12.00258, 55.00000122]
define QuantityIntervalMeetsAfterTrue: Interval[55.00000123 'g', 128.032156 'g'] meets after Interval[12.00258 'g', 55.00000122 'g']
define QuantityIntervalMeetsAfterFalse: Interval[55.00000124 'g', 150.222222 'g'] meets after Interval[12.00258 'g', 55.00000122 'g']
define DateTimeMeetsAfterTrue: Interval[DateTime(2012, 1, 15), DateTime(2012, 1, 25)] meets Interval[DateTime(2012, 1, 7), DateTime(2012, 1, 14)]
define DateTimeMeetsAfterFalse: Interval[DateTime(2012, 1, 20), DateTime(2012, 1, 25)] meets Interval[DateTime(2012, 1, 7), DateTime(2012, 1, 14)]
define TimeMeetsAfterTrue: Interval[@T10:00:00.000, @T19:59:59.999] meets Interval[@T04:59:59.999, @T09:59:59.999]
define TimeMeetsAfterFalse: Interval[@T10:12:00.000, @T19:59:59.999] meets Interval[@T04:59:59.999, @T09:59:59.999]

//NotEqual
define IntegerIntervalNotEqualTrue: IntegerIntervalTest != IntegerIntervalTest2
define IntegerIntervalNotEqualFalse: IntegerIntervalTest != IntegerIntervalTest
define DecimalIntervalNotEqualTrue: DecimalIntervalTest != DecimalIntervalTest2
define DecimalIntervalNotEqualFalse: DecimalIntervalTest != DecimalIntervalTest
define QuantityIntervalNotEqualTrue: QuantityIntervalTest != QuantityIntervalTest2
define QuantityIntervalNotEqualFalse: QuantityIntervalTest != QuantityIntervalTest
define DateTimeNotEqualTrue: Interval[DateTime(2012, 1, 15, 0, 0, 0, 0), DateTime(2012, 1, 25, 0, 0, 0, 0)] != Interval[DateTime(2012, 1, 15, 0, 0, 0, 0), DateTime(2012, 1, 25, 0, 0, 0, 22)]
define DateTimeNotEqualFalse: Interval[DateTime(2012, 1, 15, 0, 0, 0, 0), DateTime(2012, 1, 25, 0, 0, 0, 0)] != Interval[DateTime(2012, 1, 15, 0, 0, 0, 0), DateTime(2012, 1, 25, 0, 0, 0, 0)]
define TimeNotEqualTrue: Interval[@T10:00:00.000, @T19:59:59.999] != Interval[@T10:10:00.000, @T19:59:59.999]
define TimeNotEqualFalse: Interval[@T10:00:00.000, @T19:59:59.999] != Interval[@T10:00:00.000, @T19:59:59.999]

//Overlaps
define TestOverlapsNull: NullInterval overlaps IntegerIntervalTest
define IntegerIntervalOverlapsTrue: IntegerIntervalTest overlaps IntegerIntervalTest4
define IntegerIntervalOverlapsFalse: IntegerIntervalTest overlaps IntegerIntervalTest2
define DecimalIntervalOverlapsTrue: DecimalIntervalTest overlaps DecimalIntervalTest3
define DecimalIntervalOverlapsFalse: DecimalIntervalTest overlaps DecimalIntervalTest2
define QuantityIntervalOverlapsTrue: QuantityIntervalTest overlaps QuantityIntervalTest3
define QuantityIntervalOverlapsFalse: QuantityIntervalTest overlaps QuantityIntervalTest2
define DateTimeOverlapsTrue: Interval[DateTime(2012, 1, 5), DateTime(2012, 1, 25)] overlaps Interval[DateTime(2012, 1, 15), DateTime(2012, 1, 28)]
define DateTimeOverlapsFalse: Interval[DateTime(2012, 1, 5), DateTime(2012, 1, 25)] overlaps Interval[DateTime(2012, 1, 26), DateTime(2012, 1, 28)]
define TimeOverlapsTrue: Interval[@T10:00:00.000, @T19:59:59.999] overlaps Interval[@T12:00:00.000, @T21:59:59.999]
define TimeOverlapsFalse: Interval[@T10:00:00.000, @T19:59:59.999] overlaps Interval[@T20:00:00.000, @T21:59:59.999]
define TestOverlapsDay: Interval[@2012-01-05, @2012-01-25] overlaps day of Interval[@2012-01-15, @2012-01-28]

//OverlapsBefore
define TestOverlapsBeforeNull: NullInterval overlaps before IntegerIntervalTest
define IntegerIntervalOverlapsBeforeTrue: IntegerIntervalTest overlaps before IntegerIntervalTest4
define IntegerIntervalOverlapsBeforeFalse: IntegerIntervalTest4 overlaps before IntegerIntervalTest
define DecimalIntervalOverlapsBeforeTrue: DecimalIntervalTest overlaps before DecimalIntervalTest3
define DecimalIntervalOverlapsBeforeFalse: DecimalIntervalTest3 overlaps before DecimalIntervalTest
define QuantityIntervalOverlapsBeforeTrue: QuantityIntervalTest overlaps before QuantityIntervalTest3
define QuantityIntervalOverlapsBeforeFalse: QuantityIntervalTest3 overlaps before QuantityIntervalTest
define DateTimeOverlapsBeforeTrue: Interval[DateTime(2012, 1, 5), DateTime(2012, 1, 25)] overlaps Interval[DateTime(2012, 1, 15), DateTime(2012, 1, 28)]
define DateTimeOverlapsBeforeFalse: Interval[DateTime(2012, 1, 5), DateTime(2012, 1, 25)] overlaps Interval[DateTime(2012, 1, 26), DateTime(2012, 1, 28)]
define TimeOverlapsBeforeTrue: Interval[@T10:00:00.000, @T19:59:59.999] overlaps Interval[@T12:00:00.000, @T21:59:59.999]
define TimeOverlapsBeforeFalse: Interval[@T10:00:00.000, @T19:59:59.999] overlaps Interval[@T20:00:00.000, @T21:59:59.999]
define TestOverlapsDayBefore: Interval[@2012-01-05, @2012-01-25] overlaps before day of Interval[@2012-01-15, @2012-01-28]

//OverlapsAfter
define TestOverlapsAfterNull: NullInterval overlaps after IntegerIntervalTest
define IntegerIntervalOverlapsAfterTrue: IntegerIntervalTest5 overlaps after IntegerIntervalTest
define IntegerIntervalOverlapsAfterFalse: IntegerIntervalTest4 overlaps after IntegerIntervalTest
define DecimalIntervalOverlapsAfterTrue: DecimalIntervalTest4 overlaps after DecimalIntervalTest
define DecimalIntervalOverlapsAfterFalse: DecimalIntervalTest3 overlaps after DecimalIntervalTest
define QuantityIntervalOverlapsAfterTrue: QuantityIntervalTest4 overlaps after QuantityIntervalTest
define QuantityIntervalOverlapsAfterFalse: QuantityIntervalTest3 overlaps after QuantityIntervalTest
define DateTimeOverlapsAfterTrue: Interval[DateTime(2012, 1, 15), DateTime(2012, 1, 28)] overlaps Interval[DateTime(2012, 1, 5), DateTime(2012, 1, 25)]
define DateTimeOverlapsAfterFalse: Interval[DateTime(2012, 1, 26), DateTime(2012, 1, 28)] overlaps Interval[DateTime(2012, 1, 5), DateTime(2012, 1, 25)]
define TimeOverlapsAfterTrue: Interval[@T12:00:00.000, @T21:59:59.999] overlaps Interval[@T10:00:00.000, @T19:59:59.999]
define TimeOverlapsAfterFalse: Interval[@T20:00:00.000, @T21:59:59.999] overlaps Interval[@T10:00:00.000, @T19:59:59.999]
define TestOverlapsDayAfter: Interval[@2012-01-15, @2012-01-28] overlaps after day of Interval[@2012-01-05, @2012-01-25]

//ProperlyIncludes
define TestProperlyIncludesNull: NullInterval properly includes IntegerIntervalTest
define IntegerIntervalProperlyIncludesTrue: IntegerIntervalTest properly includes IntegerIntervalTest4
define IntegerIntervalProperlyIncludesFalse: IntegerIntervalTest properly includes IntegerIntervalTest5
define DecimalIntervalProperlyIncludesTrue: DecimalIntervalTest properly includes DecimalIntervalTest3
define DecimalIntervalProperlyIncludesFalse: DecimalIntervalTest properly includes DecimalIntervalTest4
define QuantityIntervalProperlyIncludesTrue: QuantityIntervalTest properly includes QuantityIntervalTest3
define QuantityIntervalProperlyIncludesFalse: QuantityIntervalTest properly includes QuantityIntervalTest4
define DateTimeProperlyIncludesTrue: Interval[DateTime(2012, 1, 15), DateTime(2012, 1, 28)] properly includes Interval[DateTime(2012, 1, 16), DateTime(2012, 1, 27)]
define DateTimeProperlyIncludesFalse: Interval[DateTime(2012, 1, 15), DateTime(2012, 1, 28)] properly includes Interval[DateTime(2012, 1, 16), DateTime(2012, 1, 29)]
define TimeProperlyIncludesTrue: Interval[@T12:00:00.000, @T21:59:59.999] properly includes Interval[@T12:01:01.000, @T21:59:59.998]
define TimeProperlyIncludesFalse: Interval[@T12:00:00.000, @T21:59:59.999] properly includes Interval[@T12:01:01.000, @T22:00:00.000]

//ProperlyIncludedIn
define TestProperlyIncludedInNull: IntegerIntervalTest properly included in NullInterval
define IntegerIntervalProperlyIncludedInTrue: IntegerIntervalTest4 properly included in IntegerIntervalTest
define IntegerIntervalProperlyIncludedInFalse: IntegerIntervalTest5 properly included in IntegerIntervalTest
define DecimalIntervalProperlyIncludedInTrue: DecimalIntervalTest3 properly included in DecimalIntervalTest
define DecimalIntervalProperlyIncludedInFalse: DecimalIntervalTest4 properly included in DecimalIntervalTest
define QuantityIntervalProperlyIncludedInTrue: QuantityIntervalTest3 properly included in QuantityIntervalTest
define QuantityIntervalProperlyIncludedInFalse: QuantityIntervalTest properly included in QuantityIntervalTest4
define DateTimeProperlyIncludedInTrue: Interval[DateTime(2012, 1, 16), DateTime(2012, 1, 27)] properly included in Interval[DateTime(2012, 1, 15), DateTime(2012, 1, 28)]
define DateTimeProperlyIncludedInFalse: Interval[DateTime(2012, 1, 16), DateTime(2012, 1, 29)] properly included in Interval[DateTime(2012, 1, 15), DateTime(2012, 1, 28)]
define TimeProperlyIncludedInTrue: Interval[@T12:01:01.000, @T21:59:59.998] properly included in Interval[@T12:00:00.000, @T21:59:59.999]
define TimeProperlyIncludedInFalse: Interval[@T12:01:01.000, @T22:00:00.000] properly included in Interval[@T12:00:00.000, @T21:59:59.999]

//Start
define IntegerIntervalStart: start of IntegerIntervalTest
define DecimalIntervalStart: start of DecimalIntervalTest
define QuantityIntervalStart: start of QuantityIntervalTest
define DateTimeIntervalStart: start of DateTimeIntervalTest
define TimeIntervalStart: start of TimeIntervalTest

//Starts
define TestStartsNull: NullInterval starts IntegerIntervalTest
define IntegerIntervalStartsTrue: IntegerIntervalTest4 starts IntegerIntervalTest5
define IntegerIntervalStartsFalse: IntegerIntervalTest starts IntegerIntervalTest4
define DecimalIntervalStartsTrue: DecimalIntervalTest3 starts DecimalIntervalTest4
define DecimalIntervalStartsFalse: DecimalIntervalTest starts DecimalIntervalTest3
define QuantityIntervalStartsTrue: QuantityIntervalTest3 starts QuantityIntervalTest4
define QuantityIntervalStartsFalse: QuantityIntervalTest starts QuantityIntervalTest3
define DateTimeStartsTrue: Interval[DateTime(2012, 1, 5), DateTime(2012, 1, 25)] starts Interval[DateTime(2012, 1, 5), DateTime(2012, 1, 27)]
define DateTimeStartsFalse: Interval[DateTime(2012, 1, 5), DateTime(2012, 1, 25)] starts Interval[DateTime(2012, 1, 6), DateTime(2012, 1, 27)]
define TimeStartsTrue: Interval[@T05:59:59.999, @T15:59:59.999] starts Interval[@T05:59:59.999, @T17:59:59.999]
define TimeStartsFalse: Interval[@T05:59:59.999, @T15:59:59.999] starts Interval[@T04:59:59.999, @T17:59:59.999]

//Union
define TestUnionNull: NullInterval union IntegerIntervalTest
define IntegerIntervalUnion1To15: IntegerIntervalTest union IntegerIntervalTest5
define IntegerIntervalUnionNull: IntegerIntervalTest union IntegerIntervalTest3
define DecimalIntervalUnion1To15: DecimalIntervalTest union DecimalIntervalTest4
define DecimalIntervalUnionNull: DecimalIntervalTest union DecimalIntervalTest5
define QuantityIntervalUnion1To15: QuantityIntervalTest union QuantityIntervalTest4
define QuantityIntervalUnionNull: QuantityIntervalTest union QuantityIntervalTest5
define DateTimeUnion: Interval[DateTime(2012, 1, 5), DateTime(2012, 1, 25)] union Interval[DateTime(2012, 1, 25), DateTime(2012, 1, 28)]
define DateTimeUnionNull: Interval[DateTime(2012, 1, 5), DateTime(2012, 1, 25)] union Interval[DateTime(2012, 1, 27), DateTime(2012, 1, 28)]
define TimeUnion: Interval[@T05:59:59.999, @T15:59:59.999] union Interval[@T10:59:59.999, @T20:59:59.999]
define TimeUnionNull: Interval[@T05:59:59.999, @T15:59:59.999] union Interval[@T16:59:59.999, @T20:59:59.999]

//Width
define IntegerIntervalTestWidth9: width of IntegerIntervalTest
define IntervalTestWidthNull: width of null
define DecimalIntervalTestWidth11: width of DecimalIntervalTest4
define QuantityIntervalTestWidth5: width of QuantityIntervalTest3
define DateTimeWidth: width of Interval[DateTime(2012, 1, 5), DateTime(2012, 1, 25)]
define TimeWidth: width of Interval[@T05:59:59.999, @T15:59:59.999]

//Interval
define IntegerIntervalTest: Interval[1, 10]
define IntegerIntervalTest2: Interval[11, 20]
define IntegerIntervalTest3: Interval[44, 50]
define IntegerIntervalTest4: Interval[4, 10]
define IntegerIntervalTest5: Interval[4, 15]
define DecimalIntervalTest: Interval[1.0, 10.0]
define DecimalIntervalTest2: Interval[11.0, 20.0]
define DecimalIntervalTest3: Interval[4.0, 10.0]
define DecimalIntervalTest4: Interval[4.0, 15.0]
define DecimalIntervalTest5: Interval[14.0, 15.0]
define QuantityIntervalTest: Interval[1.0 'g', 10.0 'g']
define QuantityIntervalTest2: Interval[11.0 'g', 20.0 'g']
define QuantityIntervalTest3: Interval[5.0 'g', 10.0 'g']
define QuantityIntervalTest4: Interval[5.0 'g', 15.0 'g']
define QuantityIntervalTest5: Interval[14.0 'g', 15.0 'g']
define DateTimeIntervalTest: Interval[@2016-05-01T00:00:00.000, @2016-05-02T00:00:00.000]
define TimeIntervalTest: Interval[@T00:00:00.000, @T23:59:59.599]
define CollapseIntervalTestInteger: {IntegerIntervalTest, IntegerIntervalTest2, IntegerIntervalTest3}
