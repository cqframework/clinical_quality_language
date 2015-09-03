// @Test: Interval
define Open: Interval(DateTime(2012, 1, 1), DateTime(2013, 1, 1))
define LeftOpen: Interval(DateTime(2012, 1, 1), DateTime(2013, 1, 1)]
define RightOpen: Interval[DateTime(2012, 1, 1), DateTime(2013, 1, 1))
define Closed: Interval[DateTime(2012, 1, 1), DateTime(2013, 1, 1)]

// @Test: Equal
define EqualClosed: Interval[1, 5] = Interval[1, 5]
define EqualOpen: Interval(1, 5) = Interval(1, 5)
define EqualOpenClosed: Interval(1, 5) = Interval[2, 4]
define UnequalClosed: Interval[1, 5] = Interval[2, 4]
define UnequalOpen: Interval(1, 5) = Interval(2, 4)
define UnequalClosedOpen: Interval[1, 5] = Interval(2, 4)
define EqualDates: Interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2013, 1, 1, 0, 0, 0, 0)) = Interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2013, 1, 1, 0, 0, 0, 0))
define EqualDatesOpenClosed: Interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2013, 1, 1, 0, 0, 0, 0)) = Interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2012, 12, 31, 23, 59, 59, 999)]
define SameDays: Interval[DateTime(2012, 1, 1), DateTime(2013, 1, 1)) = Interval[DateTime(2012, 1, 1), DateTime(2013, 1, 1))
define DifferentDays: Interval[DateTime(2012, 1, 1), DateTime(2013, 1, 1)) = Interval[DateTime(2012, 1, 1), DateTime(2012, 7, 1))

// @Test: NotEqual
define EqualClosed: Interval[1, 5] <> Interval[1, 5]
define EqualOpen: Interval(1, 5) <> Interval(1, 5)
define EqualOpenClosed: Interval(1, 5) <> Interval[2, 4]
define UnequalClosed: Interval[1, 5] <> Interval[2, 4]
define UnequalOpen: Interval(1, 5) <> Interval(2, 4)
define UnequalClosedOpen: Interval[1, 5] <> Interval(2, 4)
define EqualDates: Interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2013, 1, 1, 0, 0, 0, 0)) <> Interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2013, 1, 1, 0, 0, 0, 0))
define EqualDatesOpenClosed: Interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2013, 1, 1, 0, 0, 0, 0)) <> Interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2012, 12, 31, 23, 59, 59, 999)]
define SameDays: Interval[DateTime(2012, 1, 1), DateTime(2013, 1, 1)) <> Interval[DateTime(2012, 1, 1), DateTime(2013, 1, 1))
define DifferentDays: Interval[DateTime(2012, 1, 1), DateTime(2013, 1, 1)) <> Interval[DateTime(2012, 1, 1), DateTime(2012, 7, 1))

// @Test: Contains
define ContainsInt: Interval[1, 5] contains 3
define NotContainsInt: Interval(1, 5] contains 1
define ContainsReal: Interval[1.234, 3.456] contains 2.345
define NotContainsReal: Interval[1.234, 3.456] contains 4.567
define DateIvl: Interval[DateTime(2012, 3, 1, 0, 0, 0, 0), DateTime(2012, 9, 1, 0, 0, 0, 0))
define ContainsDate: DateIvl contains DateTime(2012, 6, 1, 0, 0, 0, 0)
define NotContainsDate: DateIvl contains DateTime(2012, 9, 1, 0, 0, 0, 0)
define ContainsImpreciseDate: DateIvl contains DateTime(2012, 4)
define NotContainsImpreciseDate: DateIvl contains DateTime(2012, 9)
define MayContainImpreciseDate: DateIvl contains DateTime(2012)
define ImpDateIvl: Interval[DateTime(2012, 3), DateTime(2012, 8)]
define ImpreciseContainsDate: ImpDateIvl contains DateTime(2012, 6, 1, 0, 0, 0, 0)
define ImpreciseNotContainsDate: ImpDateIvl contains DateTime(2012, 9, 1, 0, 0, 0, 0)
define ImpreciseMayContainDate: ImpDateIvl contains DateTime(2012, 3, 15, 0, 0, 0, 0)
define NegInfBegContainsInt: Interval[null, 5] contains -7
define NegInfBegNotContainsInt: Interval[null, 5] contains 7
define UnknownBegContainsInt: Interval(null, 5] contains 5
define UnknownBegMayContainInt: Interval(null, 5] contains -7
define UnknownBegNotContainsInt: Interval(null, 5] contains 7
define PosInfEndContainsInt: Interval[0, null] contains 123456789
define PosInfEndNotContainsInt: Interval[0, null] contains -1
define UnknownEndContainsInt: Interval[0, null) contains 0
define UnknownEndMayContainInt: Interval[0, null) contains 123456789
define UnknownEndNotContainsInt: Interval[0, null) contains -1
define NegInfBegContainsDate: Interval[null, DateTime(2013, 1, 1, 0, 0, 0, 0)) contains DateTime(1900, 1, 1, 0, 0, 0, 0)
define NegInfBegNotContainsDate: Interval[null, DateTime(2013, 1, 1, 0, 0, 0, 0)) contains DateTime(2013, 1, 2, 0, 0, 0, 0)
define UnknownBegContainsDate: Interval(null, DateTime(2013, 1, 1, 0, 0, 0, 0)) contains DateTime(2012, 12, 31, 23, 59, 59, 999)
define UnknownBegMayContainDate: Interval(null, DateTime(2013, 1, 1, 0, 0, 0, 0)) contains DateTime(1900, 1, 1, 0, 0, 0, 0)
define UnknownBegNotContainsDate: Interval(null, DateTime(2013, 1, 1, 0, 0, 0, 0)) contains DateTime(2013, 1, 2, 0, 0, 0, 0)
define PosInfEndContainsDate: Interval[DateTime(2013, 1, 1, 0, 0, 0, 0), null] contains DateTime(2014, 1, 1, 0, 0, 0, 0)
define PosInfEndNotContainsDate: Interval[DateTime(2013, 1, 1, 0, 0, 0, 0), null] contains DateTime(2012, 1, 1, 0, 0, 0, 0)
define UnknownEndContainsDate: Interval[DateTime(2013, 1, 1, 0, 0, 0, 0), null) contains DateTime(2013, 1, 1, 0, 0, 0, 0)
define UnknownEndMayContainDate: Interval[DateTime(2013, 1, 1, 0, 0, 0, 0), null) contains DateTime(2014, 1, 1, 0, 0, 0, 0)
define UnknownEndNotContainsDate: Interval[DateTime(2013, 1, 1, 0, 0, 0, 0), null) contains DateTime(2012, 1, 1, 0, 0, 0, 0)

// @Test: In
define ContainsInt: 3 in Interval[1, 5]
define NotContainsInt: 1 in Interval(1, 5]
define ContainsReal: 2.345 in Interval[1.234, 3.456]
define NotContainsReal: 4.567 in Interval[1.234, 3.456]
define DateIvl: Interval[DateTime(2012, 3, 1, 0, 0, 0, 0), DateTime(2012, 9, 1, 0, 0, 0, 0))
define ContainsDate: DateTime(2012, 6, 1, 0, 0, 0, 0) in DateIvl
define NotContainsDate: DateTime(2012, 9, 1, 0, 0, 0, 0) in DateIvl
define ContainsImpreciseDate: DateTime(2012, 4) in DateIvl
define NotContainsImpreciseDate: DateTime(2012, 9) in DateIvl
define MayContainImpreciseDate: DateTime(2012) in DateIvl
define ImpDateIvl: Interval[DateTime(2012, 3), DateTime(2012, 8)]
define ImpreciseContainsDate: DateTime(2012, 6, 1, 0, 0, 0, 0) in ImpDateIvl
define ImpreciseNotContainsDate: DateTime(2012, 9, 1, 0, 0, 0, 0) in ImpDateIvl
define ImpreciseMayContainDate: DateTime(2012, 3, 15, 0, 0, 0, 0) in ImpDateIvl
define NegInfBegContainsInt: -7 in Interval[null, 5]
define NegInfBegNotContainsInt: 7 in Interval[null, 5]
define UnknownBegContainsInt: 5 in Interval(null, 5]
define UnknownBegMayContainInt: -7 in Interval(null, 5]
define UnknownBegNotContainsInt: 7 in Interval(null, 5]
define PosInfEndContainsInt: 123456789 in Interval[0, null]
define PosInfEndNotContainsInt: -1 in Interval[0, null]
define UnknownEndContainsInt: 0 in Interval[0, null)
define UnknownEndMayContainInt: 123456789 in Interval[0, null)
define UnknownEndNotContainsInt: -1 in Interval[0, null)
define NegInfBegContainsDate: DateTime(1900, 1, 1, 0, 0, 0, 0) in Interval[null, DateTime(2013, 1, 1, 0, 0, 0, 0))
define NegInfBegNotContainsDate: DateTime(2013, 1, 2, 0, 0, 0, 0) in Interval[null, DateTime(2013, 1, 1, 0, 0, 0, 0))
define UnknownBegContainsDate: DateTime(2012, 12, 31, 23, 59, 59, 999) in Interval(null, DateTime(2013, 1, 1, 0, 0, 0, 0))
define UnknownBegMayContainDate: DateTime(1900, 1, 1, 0, 0, 0, 0) in Interval(null, DateTime(2013, 1, 1, 0, 0, 0, 0))
define UnknownBegNotContainsDate: DateTime(2013, 1, 2, 0, 0, 0, 0) in Interval(null, DateTime(2013, 1, 1, 0, 0, 0, 0))
define PosInfEndContainsDate:  DateTime(2014, 1, 1, 0, 0, 0, 0) in Interval[DateTime(2013, 1, 1, 0, 0, 0, 0), null]
define PosInfEndNotContainsDate: DateTime(2012, 1, 1, 0, 0, 0, 0) in Interval[DateTime(2013, 1, 1, 0, 0, 0, 0), null]
define UnknownEndContainsDate: DateTime(2013, 1, 1, 0, 0, 0, 0) in Interval[DateTime(2013, 1, 1, 0, 0, 0, 0), null)
define UnknownEndMayContainDate: DateTime(2014, 1, 1, 0, 0, 0, 0) in Interval[DateTime(2013, 1, 1, 0, 0, 0, 0), null)
define UnknownEndNotContainsDate: DateTime(2012, 1, 1, 0, 0, 0, 0) in Interval[DateTime(2013, 1, 1, 0, 0, 0, 0), null)

// @Test: Includes
define IncludesIntIvl: Interval[1, 5] includes Interval[1, 4]
define NotIncludesIntIvl: Interval(1, 5] includes Interval[1, 4]
define IncludesRealIvl: Interval[1.234, 3.456] includes Interval[2.34, 2.56]
define NotIncludesRealIvl: Interval[1.234, 3.456] includes Interval[1.23, 2.56]
define DateIvl: Interval[DateTime(2012, 3, 1, 0, 0, 0, 0), DateTime(2012, 9, 1, 0, 0, 0, 0))
define IncludesDateIvl: DateIvl includes Interval[DateTime(2012, 4, 1, 0, 0, 0, 0), DateTime(2012, 6, 1, 0, 0, 0, 0))
define NotIncludesDateIvl: DateIvl includes Interval[DateTime(2012, 6, 1, 0, 0, 0, 0), DateTime(2012, 9, 1, 0, 0, 0, 0)]
define IncludesImpreciseDateIvl: DateIvl includes Interval[DateTime(2012, 4), DateTime(2012, 6)]
define NotIncludesImpreciseDateIvl: DateIvl includes Interval[DateTime(2012, 4), DateTime(2012, 9)]
define MayIncludeImpreciseDateIvl: DateIvl includes Interval[DateTime(2012), DateTime(2012)]
define ImpDateIvl: Interval[DateTime(2012, 3), DateTime(2012, 8)]
define ImpreciseIncludesDateIvl: ImpDateIvl includes Interval[DateTime(2012, 4, 1, 0, 0, 0, 0), DateTime(2012, 6, 1, 0, 0, 0, 0))
define ImpreciseNotIncludesDateIvl: ImpDateIvl includes Interval[DateTime(2012, 2, 1, 0, 0, 0, 0), DateTime(2012, 6, 1, 0, 0, 0, 0))
define ImpreciseMayIncludeDateIvl: ImpDateIvl includes Interval[DateTime(2012, 3, 15, 0, 0, 0, 0), DateTime(2012, 6, 1, 0, 0, 0, 0))
define NegInfBegIncludesIntIvl: Interval[null, 5] includes Interval[-7, 2]
define NegInfBegNotIncludesIntIvl: Interval[null, 5] includes Interval[4, 7]
define UnknownBegIncludesIntIvl: Interval(null, 5] includes Interval[5, 5]
define UnknownBegMayIncludeIntIvl: Interval(null, 5] includes Interval[-7, 2]
define UnknownBegNotIncludesIntIvl: Interval(null, 5] includes Interval[4, 7]
define PosInfEndIncludesIntIvl: Interval[0, null] includes Interval[1234, 5678]
define PosInfEndNotIncludesIntIvl: Interval[0, null] includes Interval[-1234, 5678]
define UnknownEndIncludesIntIvl: Interval[0, null) includes Interval[0, 0]
define UnknownEndMayIncludeIntIvl: Interval[0, null) includes Interval[1234, 5678]
define UnknownEndNotIncludesIntIvl: Interval[0, null) includes Interval[-1234, 5678]
define NegInfBegIncludesDateIvl: Interval[null, DateTime(2013, 1, 1, 0, 0, 0, 0)) includes Interval[DateTime(1900, 1, 1, 0, 0, 0, 0), DateTime(2000, 1, 1, 0, 0, 0, 0)]
define NegInfBegNotIncludesDateIvl: Interval[null, DateTime(2013, 1, 1, 0, 0, 0, 0)) includes Interval[DateTime(1900, 1, 1, 0, 0, 0, 0), DateTime(2013, 1, 1, 0, 0, 0, 0)]
define UnknownBegIncludesDateIvl: Interval(null, DateTime(2013, 1, 1, 0, 0, 0, 0)) includes Interval[DateTime(2012, 12, 31, 23, 59, 59, 999), DateTime(2012, 12, 31, 23, 59, 59, 999)]
define UnknownBegMayIncludeDateIvl: Interval(null, DateTime(2013, 1, 1, 0, 0, 0, 0)) includes Interval[DateTime(1900, 1, 1, 0, 0, 0, 0), DateTime(2000, 1, 1, 0, 0, 0, 0)]
define UnknownBegNotIncludesDateIvl: Interval(null, DateTime(2013, 1, 1, 0, 0, 0, 0)) includes Interval[DateTime(1900, 1, 1, 0, 0, 0, 0), DateTime(2013, 1, 1, 0, 0, 0, 0)]
define PosInfEndIncludesDateIvl: Interval[DateTime(2013, 1, 1, 0, 0, 0, 0), null] includes Interval[DateTime(2014, 1, 1, 0, 0, 0, 0), DateTime(2015, 1, 1, 0, 0, 0, 0)]
define PosInfEndNotIncludesDateIvl: Interval[DateTime(2013, 1, 1, 0, 0, 0, 0), null] includes Interval[DateTime(2000, 1, 1, 0, 0, 0, 0), DateTime(2014, 1, 1, 0, 0, 0, 0)]
define UnknownEndIncludesDateIvl: Interval[DateTime(2013, 1, 1, 0, 0, 0, 0), null) includes Interval[DateTime(2013, 1, 1, 0, 0, 0, 0), DateTime(2013, 1, 1, 0, 0, 0, 0)]
define UnknownEndMayIncludeDateIvl: Interval[DateTime(2013, 1, 1, 0, 0, 0, 0), null) includes Interval[DateTime(2014, 1, 1, 0, 0, 0, 0), DateTime(2015, 1, 1, 0, 0, 0, 0)]
define UnknownEndNotIncludesDateIvl: Interval[DateTime(2013, 1, 1, 0, 0, 0, 0), null) includes Interval[DateTime(2000, 1, 1, 0, 0, 0, 0), DateTime(2014, 1, 1, 0, 0, 0, 0)]

// @Test: ProperlyIncludes
define ProperlyIncludesIntIvl: Interval[1, 5] properly includes Interval[2, 4]
define ProperlyIncludesIntBeginsIvl: Interval[1, 5] properly includes Interval[1, 4]
define ProperlyIncludesIntEndsIvl: Interval[1, 5] properly includes Interval[2, 5]
define NotProperlyIncludesIntIvl: Interval[1, 5] properly includes Interval[1, 5]
define ProperlyIncludesRealIvl: Interval[1.234, 3.456] properly includes Interval[2.34, 2.56]
define NotProperlyIncludesRealIvl: Interval[1.234, 3.456] properly includes Interval[1.234, 3.456]
define DateIvl: Interval[DateTime(2012, 3, 1, 0, 0, 0, 0), DateTime(2012, 9, 1, 0, 0, 0, 0))
define ProperlyIncludesDateIvl: DateIvl properly includes Interval[DateTime(2012, 4, 1, 0, 0, 0, 0), DateTime(2012, 6, 1, 0, 0, 0, 0))
define NotProperlyIncludesDateIvl: DateIvl properly includes Interval[DateTime(2012, 3, 1, 0, 0, 0, 0), DateTime(2012, 9, 1, 0, 0, 0, 0))
define PosInfEndProperlyIncludesIntIvl: Interval[0, null] properly includes Interval[1234, 5678]
define PosInfEndNotProperlyIncludesIntIvl: Interval[0, null] properly includes Interval[0, null]
define UnknownEndMayProperlyIncludeIntIvl: Interval[0, null) properly includes Interval[0, 0]

// @Test: IncludedIn
define IncludesIntIvl: Interval[1, 4] included in Interval[1, 5]
define NotIncludesIntIvl: Interval[1, 4] included in Interval(1, 5]
define IncludesRealIvl: Interval[2.34, 2.56] included in Interval[1.234, 3.456]
define NotIncludesRealIvl: Interval[1.23, 2.56] included in Interval[1.234, 3.456]
define DateIvl: Interval[DateTime(2012, 3, 1, 0, 0, 0, 0), DateTime(2012, 9, 1, 0, 0, 0, 0))
define IncludesDateIvl: Interval[DateTime(2012, 4, 1, 0, 0, 0, 0), DateTime(2012, 6, 1, 0, 0, 0, 0)) included in DateIvl
define NotIncludesDateIvl: Interval[DateTime(2012, 6, 1, 0, 0, 0, 0), DateTime(2012, 9, 1, 0, 0, 0, 0)] included in DateIvl
define IncludesImpreciseDateIvl: Interval[DateTime(2012, 4), DateTime(2012, 6)] included in DateIvl
define NotIncludesImpreciseDateIvl: Interval[DateTime(2012, 4), DateTime(2012, 9)] included in DateIvl
define MayIncludeImpreciseDateIvl: Interval[DateTime(2012), DateTime(2012)] included in DateIvl
define ImpDateIvl: Interval[DateTime(2012, 3), DateTime(2012, 8)]
define ImpreciseIncludesDateIvl: Interval[DateTime(2012, 4, 1, 0, 0, 0, 0), DateTime(2012, 6, 1, 0, 0, 0, 0)) included in ImpDateIvl
define ImpreciseNotIncludesDateIvl: Interval[DateTime(2012, 2, 1, 0, 0, 0, 0), DateTime(2012, 6, 1, 0, 0, 0, 0)) included in ImpDateIvl
define ImpreciseMayIncludeDateIvl: Interval[DateTime(2012, 3, 15, 0, 0, 0, 0), DateTime(2012, 6, 1, 0, 0, 0, 0)) included in ImpDateIvl
define NegInfBegIncludedInIntIvl: Interval[null, 5] included in Interval[null, 100]
define NegInfBegNotIncludedInIntIvl: Interval[null, 5] included in Interval[-100, 100]
define UnknownBegIncludedInIntIvl: Interval(null, 5] included in Interval[null, 100]
define UnknownBegMayBeIncludedInIntIvl: Interval(null, 5] included in Interval[-100, 100]
define UnknownBegNotIncludedInIntIvl: Interval(null, 5] included in Interval[-100, 0]
define PosInfEndIncludedInIntIvl: Interval[0, null] included in Interval[-100, null]
define PosInfEndNotIncludedInIntIvl: Interval[0, null] included in Interval[-100, 100]
define UnknownEndIncludedInIntIvl: Interval[0, null) included in Interval[-100, null]
define UnknownEndMayBeIncludedInIntIvl: Interval[0, null) included in Interval[-100, 100]
define UnknownEndNotIncludedInIntIvl: Interval[0, null) included in Interval[100, 200]
define NegInfBegIncludedInDateIvl: Interval[null, DateTime(2013, 1, 1, 0, 0, 0, 0)) included in Interval[null, DateTime(2020, 1, 1, 0, 0, 0, 0)]
define NegInfBegNotIncludedInDateIvl: Interval[null, DateTime(2013, 1, 1, 0, 0, 0, 0)) included in Interval[DateTime(2000, 1, 1, 0, 0, 0, 0), DateTime(2020, 1, 1, 0, 0, 0, 0)]
define UnknownBegIncludedInDateIvl: Interval(null, DateTime(2013, 1, 1, 0, 0, 0, 0)) included in Interval[null, DateTime(2020, 1, 1, 0, 0, 0, 0)]
define UnknownBegMayBeIncludedInDateIvl: Interval(null, DateTime(2013, 1, 1, 0, 0, 0, 0)) included in Interval[DateTime(2000, 1, 1, 0, 0, 0, 0), DateTime(2020, 1, 1, 0, 0, 0, 0)]
define UnknownBegNotIncludedInDateIvl: Interval(null, DateTime(2013, 1, 1, 0, 0, 0, 0)) included in Interval[DateTime(1900, 1, 1, 0, 0, 0, 0), DateTime(2000, 1, 1, 0, 0, 0, 0)]
define PosInfEndIncludedInDateIvl: Interval[DateTime(2013, 1, 1, 0, 0, 0, 0), null] included in Interval[DateTime(2000, 1, 1, 0, 0, 0, 0), null]
define PosInfEndNotIncludedInDateIvl: Interval[DateTime(2013, 1, 1, 0, 0, 0, 0), null] included in Interval[DateTime(2000, 1, 1, 0, 0, 0, 0), DateTime(2020, 1, 1, 0, 0, 0, 0)]
define UnknownEndIncludedInDateIvl: Interval[DateTime(2013, 1, 1, 0, 0, 0, 0), null) included in Interval[DateTime(2000, 1, 1, 0, 0, 0, 0), null]
define UnknownEndMayBeIncludedInDateIvl: Interval[DateTime(2013, 1, 1, 0, 0, 0, 0), null) included in Interval[DateTime(2000, 1, 1, 0, 0, 0, 0), DateTime(2020, 1, 1, 0, 0, 0, 0)]
define UnknownEndNotIncludedInDateIvl: Interval[DateTime(2013, 1, 1, 0, 0, 0, 0), null) included in Interval[DateTime(2020, 1, 1, 0, 0, 0, 0), DateTime(2040, 1, 1, 0, 0, 0, 0)]

// @Test: ProperlyIncludedIn
define ProperlyIncludesIntIvl: Interval[2, 4] properly included in Interval[1, 5]
define ProperlyIncludesIntBeginsIvl: Interval[1, 4] properly included in Interval[1, 5]
define ProperlyIncludesIntEndsIvl: Interval[2, 5] properly included in Interval[1, 5]
define NotProperlyIncludesIntIvl: Interval[1, 5] properly included in Interval[1, 5]
define ProperlyIncludesRealIvl: Interval[2.34, 2.56] properly included in Interval[1.234, 3.456]
define NotProperlyIncludesRealIvl: Interval[1.23, 2.56] properly included in Interval[1.23, 2.56]
define DateIvl: Interval[DateTime(2012, 3, 1, 0, 0, 0, 0), DateTime(2012, 9, 1, 0, 0, 0, 0))
define ProperlyIncludesDateIvl: Interval[DateTime(2012, 4, 1, 0, 0, 0, 0), DateTime(2012, 6, 1, 0, 0, 0, 0)) properly included in DateIvl
define NotProperlyIncludesDateIvl: Interval[DateTime(2012, 3, 1, 0, 0, 0, 0), DateTime(2012, 9, 1, 0, 0, 0, 0)) properly included in DateIvl
define PosInfEndProperlyIncludedInDateIvl: Interval[DateTime(2013, 1, 1, 0, 0, 0, 0), null] properly included in Interval[DateTime(2000, 1, 1, 0, 0, 0, 0), null]
define PosInfEndNotProperlyIncludedInDateIvl: Interval[DateTime(2013, 1, 1, 0, 0, 0, 0), null] properly included in Interval[DateTime(2000, 1, 1, 0, 0, 0, 0), DateTime(2020, 1, 1, 0, 0, 0, 0)]
define UnknownEndMayBeProperlyIncludedInDateIvl: Interval[DateTime(2013, 1, 1, 0, 0, 0, 0), null) properly included in Interval[DateTime(2000, 1, 1, 0, 0, 0, 0), DateTime(2020, 1, 1, 0, 0, 0, 0)]

// @Test: After
define AfterIntIvl: Interval[5, 10] after Interval[2, 4]
define NotAfterIntIvl: Interval[5, 10] after Interval[2, 5]
define AfterRealIvl: Interval[1.234, 2.345] after Interval[0.0, 1.23]
define NotAfterRealIvl: Interval[1.234, 2.345] after Interval[0.0, 1.234]
define DateIvl: Interval[DateTime(2012, 3, 1, 0, 0, 0, 0), DateTime(2012, 9, 1, 0, 0, 0, 0))
define AfterDateIvl: DateIvl after Interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2012, 3, 1, 0, 0, 0, 0))
define NotAfterDateIvl: DateIvl after Interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2012, 3, 1, 0, 0, 0, 0)]
define AfterImpreciseDateIvl: DateIvl after Interval[DateTime(2012, 1), DateTime(2012, 2)]
define NotAfterImpreciseDateIvl: DateIvl after Interval[DateTime(2012, 1), DateTime(2012, 3)]
define MayBeAfterImpreciseDateIvl: DateIvl after Interval[DateTime(2012), DateTime(2012)]
define ImpDateIvl: Interval[DateTime(2012, 3), DateTime(2012, 8)]
define ImpreciseAfterDateIvl: ImpDateIvl after Interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2012, 3, 1, 0, 0, 0, 0))
define ImpreciseNotAfterDateIvl: ImpDateIvl after Interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2012, 4, 1, 0, 0, 0, 0))
define ImpreciseMayBeAfterDateIvl: ImpDateIvl after Interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2012, 3, 15, 0, 0, 0, 0))
define NegInfBegNotAfterIntIvl: Interval[null, 100] after Interval[-100, 0]
define UnknownBegMayBeAfterIntIvl: Interval(null, 100] after Interval[-100, 0]
define UnknownBegNotAfterIntIvl: Interval(null, 5] after Interval[0, 100]
define PosInfEndAfterIntIvl: Interval[0, null] after Interval[-100, -20]
define PosInfEndNotAfterIntIvl: Interval[0, null] after Interval[-100, 0]
define UnknownEndAfterIntIvl: Interval[0, null) after Interval[-100, -20]
define UnknownEndNotAfterIntIvl: Interval[0, null) after Interval[-100, 0]
define NegInfBegNotAfterDateIvl: Interval[null, DateTime(2013, 1, 1, 0, 0, 0, 0)) after Interval[DateTime(2000, 1, 1, 0, 0, 0, 0), DateTime(2010, 1, 1, 0, 0, 0, 0)]
define UnknownBegMayBeAfterDateIvl: Interval(null, DateTime(2013, 1, 1, 0, 0, 0, 0)) after Interval[DateTime(2000, 1, 1, 0, 0, 0, 0), DateTime(2010, 1, 1, 0, 0, 0, 0)]
define UnknownBegNotAfterDateIvl: Interval(null, DateTime(2013, 1, 1, 0, 0, 0, 0)) after Interval[DateTime(2000, 1, 1, 0, 0, 0, 0), DateTime(2020, 1, 1, 0, 0, 0, 0)]
define PosInfEndAfterDateIvl: Interval[DateTime(2013, 1, 1, 0, 0, 0, 0), null] after Interval[DateTime(2000, 1, 1, 0, 0, 0, 0), DateTime(2010, 1, 1, 0, 0, 0, 0)]
define PosInfEndNotAfterDateIvl: Interval[DateTime(2013, 1, 1, 0, 0, 0, 0), null] after Interval[DateTime(2000, 1, 1, 0, 0, 0, 0), DateTime(2020, 1, 1, 0, 0, 0, 0)]
define UnknownEndAfterDateIvl: Interval[DateTime(2013, 1, 1, 0, 0, 0, 0), null) after Interval[DateTime(2000, 1, 1, 0, 0, 0, 0), DateTime(2010, 1, 1, 0, 0, 0, 0)]
define UnknownEndNotAfterDateIvl: Interval[DateTime(2013, 1, 1, 0, 0, 0, 0), null) after Interval[DateTime(2020, 1, 1, 0, 0, 0, 0), DateTime(2040, 1, 1, 0, 0, 0, 0)]

// @Test: Before
define BeforeIntIvl: Interval[2, 4] before Interval[5, 10]
define NotBeforeIntIvl: Interval[2, 5] before Interval[5, 10]
define BeforeRealIvl: Interval[0.0, 1.23] before Interval[1.234, 2.345]
define NotBeforeRealIvl: Interval[1.234, 2.345] before Interval[0.0, 1.234]
define DateIvl: Interval[DateTime(2012, 3, 1, 0, 0, 0, 0), DateTime(2012, 9, 1, 0, 0, 0, 0))
define BeforeDateIvl: Interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2012, 3, 1, 0, 0, 0, 0)) before DateIvl
define NotBeforeDateIvl: Interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2012, 3, 1, 0, 0, 0, 0)] before DateIvl
define BeforeImpreciseDateIvl: DateIvl before Interval[DateTime(2012, 9), DateTime(2012, 12)]
define NotBeforeImpreciseDateIvl: DateIvl before Interval[DateTime(2012, 8), DateTime(2012, 12)]
define MayBeBeforeImpreciseDateIvl: DateIvl before Interval[DateTime(2012), DateTime(2012)]
define ImpDateIvl: Interval[DateTime(2012, 3), DateTime(2012, 8)]
define ImpreciseBeforeDateIvl: ImpDateIvl before Interval[DateTime(2012, 9, 1, 0, 0, 0, 0), DateTime(2012, 12, 1, 0, 0, 0, 0))
define ImpreciseNotBeforeDateIvl: ImpDateIvl before Interval[DateTime(2012, 8, 1, 0, 0, 0, 0), DateTime(2012, 12, 1, 0, 0, 0, 0))
define ImpreciseMayBeBeforeDateIvl: ImpDateIvl before Interval[DateTime(2012, 8, 15, 0, 0, 0, 0), DateTime(2012, 12, 15, 0, 0, 0, 0))
define NegInfBegBeforeIntIvl: Interval[null, 100] before Interval[101, 200]
define NegInfBegNotBeforeIntIvl: Interval[null, 100] before Interval[100, 200]
define UnknownBegBeforeIntIvl: Interval(null, 100] before Interval[101, 200]
define UnknownBegNotBeforeIntIvl: Interval(null, 5] before Interval[0, 100]
define PosInfEndNotBeforeIntIvl: Interval[0, null] before Interval[1000, 2000]
define UnknownEndMayBeBeforeIntIvl: Interval[0, null) before Interval[1000, 2000]
define UnknownEndNotBeforeIntIvl: Interval[0, null) before Interval[-100, 0]
define NegInfBegBeforeDateIvl: Interval[null, DateTime(2013, 1, 1, 0, 0, 0, 0)) before Interval[DateTime(2020, 1, 1, 0, 0, 0, 0), DateTime(2030, 1, 1, 0, 0, 0, 0)]
define NegInfBegNotBeforeDateIvl: Interval[null, DateTime(2013, 1, 1, 0, 0, 0, 0)) before Interval[DateTime(2000, 1, 1, 0, 0, 0, 0), DateTime(2010, 1, 1, 0, 0, 0, 0)]
define UnknownBegBeforeDateIvl: Interval(null, DateTime(2013, 1, 1, 0, 0, 0, 0)) before Interval[DateTime(2020, 1, 1, 0, 0, 0, 0), DateTime(2030, 1, 1, 0, 0, 0, 0)]
define UnknownBegNotBeforeDateIvl: Interval(null, DateTime(2013, 1, 1, 0, 0, 0, 0)) before Interval[DateTime(2000, 1, 1, 0, 0, 0, 0), DateTime(2020, 1, 1, 0, 0, 0, 0)]
define PosInfEndNotBeforeDateIvl: Interval[DateTime(2013, 1, 1, 0, 0, 0, 0), null] before Interval[DateTime(2000, 1, 1, 0, 0, 0, 0), DateTime(2020, 1, 1, 0, 0, 0, 0)]
define UnknownEndMayBeBeforeDateIvl: Interval[DateTime(2013, 1, 1, 0, 0, 0, 0), null) before Interval[DateTime(2020, 1, 1, 0, 0, 0, 0), DateTime(2040, 1, 1, 0, 0, 0, 0)]
define UnknownEndNotBeforeDateIvl: Interval[DateTime(2013, 1, 1, 0, 0, 0, 0), null) before Interval[DateTime(2000, 1, 1, 0, 0, 0, 0), DateTime(2013, 1, 1, 0, 0, 0, 0)]


// @Test: Meets
define MeetsAfterIntIvl: Interval[11, 15] meets Interval[5, 10]
define MeetsBeforeIntIvl: Interval[1, 4] meets Interval[5, 10]
define NotMeetsIntIvl: Interval[1, 2] meets Interval[5, 10]
define MeetsAfterRealIvl: Interval[1.50000001, 2.5] meets Interval[0.5, 1.5]
define MeetsBeforeRealIvl: Interval[0.5, 1.5] meets Interval[1.50000001, 2.5]
define NotMeetsRealIvl: Interval[0.0, 1.0] meets Interval[1.1, 2.0]
define DateIvl: Interval[DateTime(2012, 3, 1, 0, 0, 0, 0), DateTime(2012, 9, 1, 0, 0, 0, 0))
define MeetsAfterDateIvl: Interval[DateTime(2012, 9, 1, 0, 0, 0, 0), DateTime(2012, 12, 1, 0, 0, 0, 0)) meets DateIvl
define MeetsBeforeDateIvl: Interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2012, 3, 1, 0, 0, 0, 0)) meets DateIvl
define NotMeetsDateIvl: Interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2012, 2, 29, 0, 0, 0, 0)) meets DateIvl
define MayMeetAfterImpreciseDateIvl: DateIvl meets Interval[DateTime(2012, 1), DateTime(2012, 2)]
define MayMeetBeforeImpreciseDateIvl: DateIvl meets Interval[DateTime(2012, 9), DateTime(2012, 12)]
define NotMeetsImpreciseDateIvl: DateIvl meets Interval[DateTime(2012, 1), DateTime(2012, 12)]
define ImpDateIvl: Interval[DateTime(2012, 3), DateTime(2012, 8)]
define ImpreciseMayMeetAfterDateIvl: ImpDateIvl meets Interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2012, 2, 29, 23, 59, 59, 999)]
define ImpreciseMayMeetBeforeDateIvl: ImpDateIvl meets Interval[DateTime(2012, 9, 1, 0, 0, 0, 0), DateTime(2012, 12, 31, 23, 59, 59, 999)]
define ImpreciseNotMeetsDateIvl: ImpDateIvl meets Interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2012, 12, 31, 23, 59, 59, 999))
define NegInfBegMeetsBeforeIntIvl: Interval[null, 100] meets Interval[101, 200]
define NegInfBegNotMeetsIntIvl: Interval[null, 100] meets Interval[100, 200]
define IntIvlNotMeetsNegInfBeg: Interval[100, 200] meets Interval[null, 400]
define UnknownBegMeetsBeforeIntIvl: Interval(null, 100] meets Interval[101, 200]
define UnknownBegMayMeetAfterIntIvl: Interval(null, 100] meets Interval[0, 50]
define UnknownBegNotMeetsIntIvl: Interval(null, 5] meets Interval[0, 100]
define IntIvlMayMeetBeforeUnknownBeg: Interval[0, 100] meets Interval(null, 400)
define PosInfEndMeetsAfterIntIvl: Interval[100, null] meets Interval[0, 99]
define PosInfEndNotMeetsIntIvl: Interval[0, null] meets Interval[1000, 2000]
define IntIvlNotMeetsPosInfEnd: Interval[100, 200] meets Interval[0, null]
define UnknownEndMeetsAfterIntIvl: Interval[100, null) meets Interval[0, 99]
define UnknownEndMayMeetBeforeIntIvl: Interval[0, null) meets Interval[50, 100]
define UnknownEndNotMeetsIntIvl: Interval[0, null) meets Interval[0, 100]
define IntIvlMayMeetAfterUnknownEnd: Interval[0, 100] meets Interval[-100, null)
define NegInfBegMeetsBeforeDateIvl: Interval[null, DateTime(2013, 1, 1, 0, 0, 0, 0)) meets Interval[DateTime(2013, 1, 1, 0, 0, 0, 0), DateTime(2030, 1, 1, 0, 0, 0, 0)]
define NegInfBegNotMeetsDateIvl: Interval[null, DateTime(2013, 1, 1, 0, 0, 0, 0)) meets Interval[DateTime(2000, 1, 1, 0, 0, 0, 0), DateTime(2010, 1, 1, 0, 0, 0, 0)]
define DateIvlNotMeetsNegInfBeg: Interval[DateTime(2000, 1, 1, 0, 0, 0, 0), DateTime(2010, 1, 1, 0, 0, 0, 0)] meets Interval[null, DateTime(2013, 1, 1, 0, 0, 0, 0))
define UnknownBegMeetsBeforeDateIvl: Interval(null, DateTime(2013, 1, 1, 0, 0, 0, 0)) meets Interval[DateTime(2013, 1, 1, 0, 0, 0, 0), DateTime(2030, 1, 1, 0, 0, 0, 0)]
define UnknownBegMayMeetAfterDateIvl: Interval(null, DateTime(2013, 1, 1, 0, 0, 0, 0)) meets Interval[DateTime(2000, 1, 1, 0, 0, 0, 0), DateTime(2010, 1, 1, 0, 0, 0, 0)]
define UnknownBegNotMeetsDateIvl: Interval(null, DateTime(2013, 1, 1, 0, 0, 0, 0)) meets Interval[DateTime(2000, 1, 1, 0, 0, 0, 0), DateTime(2020, 1, 1, 0, 0, 0, 0)]
define DateIvlMayMeetBeforeUnknownBeg: Interval[DateTime(2000, 1, 1, 0, 0, 0, 0), DateTime(2010, 1, 1, 0, 0, 0, 0)] meets Interval(null, DateTime(2013, 1, 1, 0, 0, 0, 0))
define PosInfEndMeetsAfterDateIvl: Interval[DateTime(2013, 1, 1, 0, 0, 0, 0), null] meets Interval[DateTime(2000, 1, 1, 0, 0, 0, 0), DateTime(2013, 1, 1, 0, 0, 0, 0))
define PosInfEndNotMeetsDateIvl: Interval[DateTime(2013, 1, 1, 0, 0, 0, 0), null] meets Interval[DateTime(2000, 1, 1, 0, 0, 0, 0), DateTime(2020, 1, 1, 0, 0, 0, 0)]
define DateIvlNotMeetsPosInfEnd: Interval[DateTime(2000, 1, 1, 0, 0, 0, 0), DateTime(2020, 1, 1, 0, 0, 0, 0)] meets Interval[DateTime(2013, 1, 1, 0, 0, 0, 0), null]
define UnknownEndMeetsAfterDateIvl: Interval[DateTime(2013, 1, 1, 0, 0, 0, 0), null) meets Interval[DateTime(2010, 1, 1, 0, 0, 0, 0), DateTime(2013, 1, 1, 0, 0, 0, 0))
define UnknownEndMayMeetBeforeDateIvl: Interval[DateTime(2013, 1, 1, 0, 0, 0, 0), null) meets Interval[DateTime(2020, 1, 1, 0, 0, 0, 0), DateTime(2040, 1, 1, 0, 0, 0, 0)]
define UnknownEndNotMeetsDateIvl: Interval[DateTime(2013, 1, 1, 0, 0, 0, 0), null) meets Interval[DateTime(2000, 1, 1, 0, 0, 0, 0), DateTime(2013, 1, 1, 0, 0, 0, 0)]
define DateIvlMayMeetAfterUnknownEnd: Interval[DateTime(2020, 1, 1, 0, 0, 0, 0), DateTime(2040, 1, 1, 0, 0, 0, 0)] meets Interval[DateTime(2013, 1, 1, 0, 0, 0, 0), null)

// @Test: MeetsAfter
define MeetsAfterIntIvl: Interval[11, 15] meets after Interval[5, 10]
define MeetsBeforeIntIvl: Interval[1, 4] meets after Interval[5, 10]
define NotMeetsIntIvl: Interval[1, 2] meets after Interval[5, 10]
define MeetsAfterRealIvl: Interval[1.50000001, 2.5] meets after Interval[0.5, 1.5]
define MeetsBeforeRealIvl: Interval[0.5, 1.5] meets after Interval[1.50000001, 2.5]
define NotMeetsRealIvl: Interval[0.0, 1.0] meets after Interval[1.1, 2.0]
define DateIvl: Interval[DateTime(2012, 3, 1, 0, 0, 0, 0), DateTime(2012, 9, 1, 0, 0, 0, 0))
define MeetsAfterDateIvl: Interval[DateTime(2012, 9, 1, 0, 0, 0, 0), DateTime(2012, 12, 1, 0, 0, 0, 0)) meets after DateIvl
define MeetsBeforeDateIvl: Interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2012, 3, 1, 0, 0, 0, 0)) meets after DateIvl
define NotMeetsDateIvl: Interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2012, 2, 29, 0, 0, 0, 0)) meets after DateIvl
define MayMeetAfterImpreciseDateIvl: DateIvl meets after Interval[DateTime(2012, 1), DateTime(2012, 2)]
define MayMeetBeforeImpreciseDateIvl: DateIvl meets after Interval[DateTime(2012, 9), DateTime(2012, 12)]
define NotMeetsImpreciseDateIvl: DateIvl meets after Interval[DateTime(2012, 1), DateTime(2012, 12)]
define ImpDateIvl: Interval[DateTime(2012, 3), DateTime(2012, 8)]
define ImpreciseMayMeetAfterDateIvl: ImpDateIvl meets after Interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2012, 2, 29, 23, 59, 59, 999)]
define ImpreciseMayMeetBeforeDateIvl: ImpDateIvl meets after Interval[DateTime(2012, 9, 1, 0, 0, 0, 0), DateTime(2012, 12, 31, 23, 59, 59, 999)]
define ImpreciseNotMeetsDateIvl: ImpDateIvl meets after Interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2012, 12, 31, 23, 59, 59, 999))
define NegInfBegMeetsBeforeIntIvl: Interval[null, 100] meets after Interval[101, 200]
define NegInfBegNotMeetsIntIvl: Interval[null, 100] meets after Interval[100, 200]
define IntIvlNotMeetsNegInfBeg: Interval[100, 200] meets after Interval[null, 400]
define UnknownBegMeetsBeforeIntIvl: Interval(null, 100] meets after Interval[101, 200]
define UnknownBegMayMeetAfterIntIvl: Interval(null, 100] meets after Interval[0, 50]
define UnknownBegNotMeetsIntIvl: Interval(null, 5] meets after Interval[0, 100]
define IntIvlMayMeetBeforeUnknownBeg: Interval[0, 100] meets after Interval(null, 400)
define PosInfEndMeetsAfterIntIvl: Interval[100, null] meets after Interval[0, 99]
define PosInfEndNotMeetsIntIvl: Interval[0, null] meets after Interval[1000, 2000]
define IntIvlNotMeetsPosInfEnd: Interval[100, 200] meets after Interval[0, null]
define UnknownEndMeetsAfterIntIvl: Interval[100, null) meets after Interval[0, 99]
define UnknownEndMayMeetBeforeIntIvl: Interval[0, null) meets after Interval[50, 100]
define UnknownEndNotMeetsIntIvl: Interval[0, null) meets after Interval[0, 100]
define IntIvlMayMeetAfterUnknownEnd: Interval[0, 100] meets after Interval[-100, null)
define NegInfBegMeetsBeforeDateIvl: Interval[null, DateTime(2013, 1, 1, 0, 0, 0, 0)) meets after Interval[DateTime(2013, 1, 1, 0, 0, 0, 0), DateTime(2030, 1, 1, 0, 0, 0, 0)]
define NegInfBegNotMeetsDateIvl: Interval[null, DateTime(2013, 1, 1, 0, 0, 0, 0)) meets after Interval[DateTime(2000, 1, 1, 0, 0, 0, 0), DateTime(2010, 1, 1, 0, 0, 0, 0)]
define DateIvlNotMeetsNegInfBeg: Interval[DateTime(2000, 1, 1, 0, 0, 0, 0), DateTime(2010, 1, 1, 0, 0, 0, 0)] meets after Interval[null, DateTime(2013, 1, 1, 0, 0, 0, 0))
define UnknownBegMeetsBeforeDateIvl: Interval(null, DateTime(2013, 1, 1, 0, 0, 0, 0)) meets after Interval[DateTime(2013, 1, 1, 0, 0, 0, 0), DateTime(2030, 1, 1, 0, 0, 0, 0)]
define UnknownBegMayMeetAfterDateIvl: Interval(null, DateTime(2013, 1, 1, 0, 0, 0, 0)) meets after Interval[DateTime(2000, 1, 1, 0, 0, 0, 0), DateTime(2010, 1, 1, 0, 0, 0, 0)]
define UnknownBegNotMeetsDateIvl: Interval(null, DateTime(2013, 1, 1, 0, 0, 0, 0)) meets after Interval[DateTime(2000, 1, 1, 0, 0, 0, 0), DateTime(2020, 1, 1, 0, 0, 0, 0)]
define DateIvlMayMeetBeforeUnknownBeg: Interval[DateTime(2000, 1, 1, 0, 0, 0, 0), DateTime(2010, 1, 1, 0, 0, 0, 0)] meets after Interval(null, DateTime(2013, 1, 1, 0, 0, 0, 0))
define PosInfEndMeetsAfterDateIvl: Interval[DateTime(2013, 1, 1, 0, 0, 0, 0), null] meets after Interval[DateTime(2000, 1, 1, 0, 0, 0, 0), DateTime(2013, 1, 1, 0, 0, 0, 0))
define PosInfEndNotMeetsDateIvl: Interval[DateTime(2013, 1, 1, 0, 0, 0, 0), null] meets after Interval[DateTime(2000, 1, 1, 0, 0, 0, 0), DateTime(2020, 1, 1, 0, 0, 0, 0)]
define DateIvlNotMeetsPosInfEnd: Interval[DateTime(2000, 1, 1, 0, 0, 0, 0), DateTime(2020, 1, 1, 0, 0, 0, 0)] meets after Interval[DateTime(2013, 1, 1, 0, 0, 0, 0), null]
define UnknownEndMeetsAfterDateIvl: Interval[DateTime(2013, 1, 1, 0, 0, 0, 0), null) meets after Interval[DateTime(2010, 1, 1, 0, 0, 0, 0), DateTime(2013, 1, 1, 0, 0, 0, 0))
define UnknownEndMayMeetBeforeDateIvl: Interval[DateTime(2013, 1, 1, 0, 0, 0, 0), null) meets after Interval[DateTime(2020, 1, 1, 0, 0, 0, 0), DateTime(2040, 1, 1, 0, 0, 0, 0)]
define UnknownEndNotMeetsDateIvl: Interval[DateTime(2013, 1, 1, 0, 0, 0, 0), null) meets after Interval[DateTime(2000, 1, 1, 0, 0, 0, 0), DateTime(2013, 1, 1, 0, 0, 0, 0)]
define DateIvlMayMeetAfterUnknownEnd: Interval[DateTime(2020, 1, 1, 0, 0, 0, 0), DateTime(2040, 1, 1, 0, 0, 0, 0)] meets after Interval[DateTime(2013, 1, 1, 0, 0, 0, 0), null)

// @Test: MeetsBefore
define MeetsAfterIntIvl: Interval[11, 15] meets before Interval[5, 10]
define MeetsBeforeIntIvl: Interval[1, 4] meets before Interval[5, 10]
define NotMeetsIntIvl: Interval[1, 2] meets before Interval[5, 10]
define MeetsAfterRealIvl: Interval[1.50000001, 2.5] meets before Interval[0.5, 1.5]
define MeetsBeforeRealIvl: Interval[0.5, 1.5] meets before Interval[1.50000001, 2.5]
define NotMeetsRealIvl: Interval[0.0, 1.0] meets before Interval[1.1, 2.0]
define DateIvl: Interval[DateTime(2012, 3, 1, 0, 0, 0, 0), DateTime(2012, 9, 1, 0, 0, 0, 0))
define MeetsAfterDateIvl: Interval[DateTime(2012, 9, 1, 0, 0, 0, 0), DateTime(2012, 12, 1, 0, 0, 0, 0)) meets before DateIvl
define MeetsBeforeDateIvl: Interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2012, 3, 1, 0, 0, 0, 0)) meets before DateIvl
define NotMeetsDateIvl: Interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2012, 2, 29, 0, 0, 0, 0)) meets before DateIvl
define MayMeetAfterImpreciseDateIvl: DateIvl meets before Interval[DateTime(2012, 1), DateTime(2012, 2)]
define MayMeetBeforeImpreciseDateIvl: DateIvl meets before Interval[DateTime(2012, 9), DateTime(2012, 12)]
define NotMeetsImpreciseDateIvl: DateIvl meets before Interval[DateTime(2012, 1), DateTime(2012, 12)]
define ImpDateIvl: Interval[DateTime(2012, 3), DateTime(2012, 8)]
define ImpreciseMayMeetAfterDateIvl: ImpDateIvl meets before Interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2012, 2, 29, 23, 59, 59, 999)]
define ImpreciseMayMeetBeforeDateIvl: ImpDateIvl meets before Interval[DateTime(2012, 9, 1, 0, 0, 0, 0), DateTime(2012, 12, 31, 23, 59, 59, 999)]
define ImpreciseNotMeetsDateIvl: ImpDateIvl meets before Interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2012, 12, 31, 23, 59, 59, 999))
define NegInfBegMeetsBeforeIntIvl: Interval[null, 100] meets before Interval[101, 200]
define NegInfBegNotMeetsIntIvl: Interval[null, 100] meets before Interval[100, 200]
define IntIvlNotMeetsNegInfBeg: Interval[100, 200] meets before Interval[null, 400]
define UnknownBegMeetsBeforeIntIvl: Interval(null, 100] meets before Interval[101, 200]
define UnknownBegMayMeetAfterIntIvl: Interval(null, 100] meets before Interval[0, 50]
define UnknownBegNotMeetsIntIvl: Interval(null, 5] meets before Interval[0, 100]
define IntIvlMayMeetBeforeUnknownBeg: Interval[0, 100] meets before Interval(null, 400)
define PosInfEndMeetsAfterIntIvl: Interval[100, null] meets before Interval[0, 99]
define PosInfEndNotMeetsIntIvl: Interval[0, null] meets before Interval[1000, 2000]
define IntIvlNotMeetsPosInfEnd: Interval[100, 200] meets before Interval[0, null]
define UnknownEndMeetsAfterIntIvl: Interval[100, null) meets before Interval[0, 99]
define UnknownEndMayMeetBeforeIntIvl: Interval[0, null) meets before Interval[50, 100]
define UnknownEndNotMeetsIntIvl: Interval[0, null) meets before Interval[0, 100]
define IntIvlMayMeetAfterUnknownEnd: Interval[0, 100] meets before Interval[-100, null)
define NegInfBegMeetsBeforeDateIvl: Interval[null, DateTime(2013, 1, 1, 0, 0, 0, 0)) meets before Interval[DateTime(2013, 1, 1, 0, 0, 0, 0), DateTime(2030, 1, 1, 0, 0, 0, 0)]
define NegInfBegNotMeetsDateIvl: Interval[null, DateTime(2013, 1, 1, 0, 0, 0, 0)) meets before Interval[DateTime(2000, 1, 1, 0, 0, 0, 0), DateTime(2010, 1, 1, 0, 0, 0, 0)]
define DateIvlNotMeetsNegInfBeg: Interval[DateTime(2000, 1, 1, 0, 0, 0, 0), DateTime(2010, 1, 1, 0, 0, 0, 0)] meets before Interval[null, DateTime(2013, 1, 1, 0, 0, 0, 0))
define UnknownBegMeetsBeforeDateIvl: Interval(null, DateTime(2013, 1, 1, 0, 0, 0, 0)) meets before Interval[DateTime(2013, 1, 1, 0, 0, 0, 0), DateTime(2030, 1, 1, 0, 0, 0, 0)]
define UnknownBegMayMeetAfterDateIvl: Interval(null, DateTime(2013, 1, 1, 0, 0, 0, 0)) meets before Interval[DateTime(2000, 1, 1, 0, 0, 0, 0), DateTime(2010, 1, 1, 0, 0, 0, 0)]
define UnknownBegNotMeetsDateIvl: Interval(null, DateTime(2013, 1, 1, 0, 0, 0, 0)) meets before Interval[DateTime(2000, 1, 1, 0, 0, 0, 0), DateTime(2020, 1, 1, 0, 0, 0, 0)]
define DateIvlMayMeetBeforeUnknownBeg: Interval[DateTime(2000, 1, 1, 0, 0, 0, 0), DateTime(2010, 1, 1, 0, 0, 0, 0)] meets before Interval(null, DateTime(2013, 1, 1, 0, 0, 0, 0))
define PosInfEndMeetsAfterDateIvl: Interval[DateTime(2013, 1, 1, 0, 0, 0, 0), null] meets before Interval[DateTime(2000, 1, 1, 0, 0, 0, 0), DateTime(2013, 1, 1, 0, 0, 0, 0))
define PosInfEndNotMeetsDateIvl: Interval[DateTime(2013, 1, 1, 0, 0, 0, 0), null] meets before Interval[DateTime(2000, 1, 1, 0, 0, 0, 0), DateTime(2020, 1, 1, 0, 0, 0, 0)]
define DateIvlNotMeetsPosInfEnd: Interval[DateTime(2000, 1, 1, 0, 0, 0, 0), DateTime(2020, 1, 1, 0, 0, 0, 0)] meets before Interval[DateTime(2013, 1, 1, 0, 0, 0, 0), null]
define UnknownEndMeetsAfterDateIvl: Interval[DateTime(2013, 1, 1, 0, 0, 0, 0), null) meets before Interval[DateTime(2010, 1, 1, 0, 0, 0, 0), DateTime(2013, 1, 1, 0, 0, 0, 0))
define UnknownEndMayMeetBeforeDateIvl: Interval[DateTime(2013, 1, 1, 0, 0, 0, 0), null) meets before Interval[DateTime(2020, 1, 1, 0, 0, 0, 0), DateTime(2040, 1, 1, 0, 0, 0, 0)]
define UnknownEndNotMeetsDateIvl: Interval[DateTime(2013, 1, 1, 0, 0, 0, 0), null) meets before Interval[DateTime(2000, 1, 1, 0, 0, 0, 0), DateTime(2013, 1, 1, 0, 0, 0, 0)]
define DateIvlMayMeetAfterUnknownEnd: Interval[DateTime(2020, 1, 1, 0, 0, 0, 0), DateTime(2040, 1, 1, 0, 0, 0, 0)] meets before Interval[DateTime(2013, 1, 1, 0, 0, 0, 0), null)

// @Test: Overlaps
define OverlapsBeforeIntIvl: Interval[1, 5] overlaps Interval[2, 7]
define OverlapsAfterIntIvl: Interval[3, 8] overlaps Interval[1, 6]
define OverlapsBoundaryIntIvl: Interval[1, 5] overlaps Interval[5, 10]
define NoOverlapsIntIvl: Interval[1,5) overlaps Interval[5, 10]
define OverlapsBeforeRealIvl: Interval[1.234, 1.567] overlaps Interval[1.345, 1.678]
define OverlapsAfterRealIvl: Interval[1.345, 1.678] overlaps Interval[1.234, 1.567]
define OverlapsBoundaryRealIvl: Interval[1.0, 1.234] overlaps Interval[1.234, 2.0]
define NoOverlapsRealIvl: Interval[1.0, 1.23456789) overlaps Interval[1.23456789, 2.0]

// @Test: OverlapsDateTime
define ivlA: Interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2012, 6, 1, 0, 0, 0, 0))
define ivlB: Interval[DateTime(2012, 3, 1, 0, 0, 0, 0), DateTime(2012, 9, 1, 0, 0, 0, 0))
define ivlC: Interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2013, 1, 1, 0, 0, 0, 0))
define ivlD: Interval[DateTime(2013, 1, 1, 0, 0, 0, 0), DateTime(2014, 1, 1, 0, 0, 0, 0))
define ivlE: Interval[DateTime(2013), DateTime(2015)]
define ivlF: Interval[DateTime(2014), DateTime(2016)]
define ivlG: Interval[DateTime(2016), DateTime(2017)]
define OverlapsBefore: ivlA overlaps ivlB
define OverlapsAfter: ivlB overlaps ivlA
define OverlapsContained: ivlB overlaps ivlC
define OverlapsContains: ivlC overlaps ivlB
define ImpreciseOverlap: ivlD overlaps ivlE
define NoOverlap: ivlC overlaps ivlD
define NoImpreciseOverlap: ivlE overlaps ivlG
define UnknownOverlap: ivlF overlaps ivlG

// @Test: OverlapsAfter
define OverlapsBeforeIntIvl: Interval[1, 5] overlaps after Interval[2, 7]
define OverlapsAfterIntIvl: Interval[3, 8] overlaps after Interval[1, 6]
define OverlapsBoundaryIntIvl: Interval[5, 10] overlaps after Interval[1, 5]
define NoOverlapsIntIvl: Interval[1,5) overlaps after Interval[5, 10]
define OverlapsBeforeRealIvl: Interval[1.234, 1.567] overlaps after Interval[1.345, 1.678]
define OverlapsAfterRealIvl: Interval[1.345, 1.678] overlaps after Interval[1.234, 1.567]
define OverlapsBoundaryRealIvl: Interval[1.234, 2.0] overlaps after Interval[1.0, 1.234]
define NoOverlapsRealIvl: Interval[1.0, 1.23456789) overlaps after Interval[1.23456789, 2.0]

// @Test: OverlapsAfterDateTime
define ivlA: Interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2012, 6, 1, 0, 0, 0, 0))
define ivlB: Interval[DateTime(2012, 3, 1, 0, 0, 0, 0), DateTime(2012, 9, 1, 0, 0, 0, 0))
define ivlC: Interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2013, 1, 1, 0, 0, 0, 0))
define ivlD: Interval[DateTime(2013, 1, 1, 0, 0, 0, 0), DateTime(2014, 1, 1, 0, 0, 0, 0))
define ivlE: Interval[DateTime(2013), DateTime(2015)]
define ivlF: Interval[DateTime(2014), DateTime(2016)]
define ivlG: Interval[DateTime(2016), DateTime(2017)]
define OverlapsBefore: ivlA overlaps after ivlB
define OverlapsAfter: ivlB overlaps after ivlA
define OverlapsContained: ivlB overlaps after ivlC
define OverlapsContains: ivlC overlaps after ivlB
define ImpreciseOverlapBefore: ivlE overlaps after ivlF
define ImpreciseOverlapAfter: ivlF overlaps after ivlE
define NoOverlap: ivlC overlaps after ivlD
define NoImpreciseOverlap: ivlE overlaps after ivlG
define UnknownOverlap: ivlG overlaps after ivlF

// @Test: OverlapsBefore
define OverlapsBeforeIntIvl: Interval[1, 5] overlaps before Interval[2, 7]
define OverlapsAfterIntIvl: Interval[3, 8] overlaps before Interval[1, 6]
define OverlapsBoundaryIntIvl: Interval[1, 5] overlaps before Interval[5, 10]
define NoOverlapsIntIvl: Interval[1,5) overlaps before Interval[5, 10]
define OverlapsBeforeRealIvl: Interval[1.234, 1.567] overlaps before Interval[1.345, 1.678]
define OverlapsAfterRealIvl: Interval[1.345, 1.678] overlaps before Interval[1.234, 1.567]
define OverlapsBoundaryRealIvl: Interval[1.0, 1.234] overlaps before Interval[1.234, 2.0]
define NoOverlapsRealIvl: Interval[1.0, 1.23456789) overlaps before Interval[1.23456789, 2.0]

// @Test: OverlapsBeforeDateTime
define ivlA: Interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2012, 6, 1, 0, 0, 0, 0))
define ivlB: Interval[DateTime(2012, 3, 1, 0, 0, 0, 0), DateTime(2012, 9, 1, 0, 0, 0, 0))
define ivlC: Interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2013, 1, 1, 0, 0, 0, 0))
define ivlD: Interval[DateTime(2013, 1, 1, 0, 0, 0, 0), DateTime(2014, 1, 1, 0, 0, 0, 0))
define ivlE: Interval[DateTime(2013), DateTime(2015)]
define ivlF: Interval[DateTime(2014), DateTime(2016)]
define ivlG: Interval[DateTime(2016), DateTime(2017)]
define OverlapsBefore: ivlA overlaps before ivlB
define OverlapsAfter: ivlB overlaps before ivlA
define OverlapsContained: ivlB overlaps before ivlC
define OverlapsContains: ivlC overlaps before ivlB
define ImpreciseOverlapBefore: ivlE overlaps before ivlF
define ImpreciseOverlapAfter: ivlF overlaps before ivlE
define NoOverlap: ivlC overlaps before ivlD
define NoImpreciseOverlap: ivlE overlaps before ivlG
define UnknownOverlap: ivlF overlaps before ivlG

// @Test: Width
define IntWidth: width of Interval[-2, 5]
define IntOpenWidth: width of Interval(-2, 5)
define IntWidthThreeToMax: width of Interval[3, null]
define IntWidthMinToThree: width of Interval[null, 3]
define IntWidthThreeToUnknown: width of Interval[3, null)
define IntWidthUnknownToThree: width of Interval(null, 3]
define RealWidth: width of Interval[1.23, 4.56]
define RealOpenWidth: width of Interval(1.23, 4.56)

// @Test: Start
define Foo: start of Interval[DateTime(2012, 1, 1), DateTime(2013, 1, 1)]

// @Test: End
define Foo: end of Interval[DateTime(2012, 1, 1), DateTime(2013, 1, 1)]

// @Test: IntegerIntervalUnion
define IntFullInterval: Interval[0,10]
define IntClosedUnionClosed: Interval[0,5] union Interval[5,10]
define IntClosedUnionOpen: Interval[0,5] union Interval[5,10)
define IntOpenUnionOpen: Interval(0,5] union Interval[5,10)
define IntOpenUnionClosed: Interval(0,5] union Interval[5,10]
define IntSameAsUnion: Interval[0,10] union Interval[0,10]
define IntBeforeUnion: Interval[0,4] union Interval[6,10]
define IntMeetsUnion: Interval[0,5] union Interval[5,10]
define IntOverlapsUnion: Interval[0,7] union Interval[3,10]
define IntBeginsUnion: Interval[0,5] union Interval[0,10]
define IntDuringUnion: Interval[3,5] union Interval[0,10]
define IntEndsUnion: Interval[5,10] union Interval[0,10]

// @Test: DateTimeIntervalUnion
define DateTimeFullInterval: Interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2013, 1, 1, 0, 0, 0, 0)]
define DateTimeClosedUnionClosed: Interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2012, 6, 1, 0, 0, 0, 0)] union Interval[DateTime(2012, 6, 1, 0, 0, 0, 0), DateTime(2013, 1, 1, 0, 0, 0, 0)]
define DateTimeClosedUnionOpen: Interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2012, 6, 1, 0, 0, 0, 0)] union Interval[DateTime(2012, 6, 1, 0, 0, 0, 0), DateTime(2013, 1, 1, 0, 0, 0, 0))
define DateTimeOpenUnionOpen: Interval(DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2012, 6, 1, 0, 0, 0, 0)] union Interval[DateTime(2012, 6, 1, 0, 0, 0, 0), DateTime(2013, 1, 1, 0, 0, 0, 0))
define DateTimeOpenUnionClosed: Interval(DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2012, 6, 1, 0, 0, 0, 0)] union Interval[DateTime(2012, 6, 1, 0, 0, 0, 0), DateTime(2013, 1, 1, 0, 0, 0, 0)]
define DateTimeSameAsUnion: Interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2013, 1, 1, 0, 0, 0, 0)] union Interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2013, 1, 1, 0, 0, 0, 0)]
define DateTimeBeforeUnion: Interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2012, 4, 1, 0, 0, 0, 0)] union Interval[DateTime(2012, 7, 1, 0, 0, 0, 0), DateTime(2013, 1, 1, 0, 0, 0, 0)]
define DateTimeMeetsUnion: Interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2012, 6, 1, 0, 0, 0, 0)] union Interval[DateTime(2012, 6, 1, 0, 0, 0, 0), DateTime(2013, 1, 1, 0, 0, 0, 0)]
define DateTimeOverlapsUnion: Interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2012, 7, 1, 0, 0, 0, 0)] union Interval[DateTime(2012, 3, 1, 0, 0, 0, 0), DateTime(2013, 1, 1, 0, 0, 0, 0)]
define DateTimeBeginsUnion: Interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2012, 6, 1, 0, 0, 0, 0)] union Interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2013, 1, 1, 0, 0, 0, 0)]
define DateTimeDuringUnion: Interval[DateTime(2012, 3, 1, 0, 0, 0, 0), DateTime(2012, 5, 1, 0, 0, 0, 0)] union Interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2013, 1, 1, 0, 0, 0, 0)]
define DateTimeEndsUnion: Interval[DateTime(2012, 6, 1, 0, 0, 0, 0), DateTime(2013, 1, 1, 0, 0, 0, 0)] union Interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2013, 1, 1, 0, 0, 0, 0)]

// @Test: IntegerIntervalExcept
define IntFullInterval: Interval[0,10]
define IntHalfInterval: Interval[0,5)
define IntSameAsExcept: Interval[0,10] except Interval[0,10]
define IntBeforeExcept: Interval[0,4] except Interval[6,10]
define IntMeetsExcept: Interval[0,5] except Interval[5,10]
define IntOverlapsExcept: Interval[0,7] except Interval[5,10]
define IntBeginsExcept: Interval[0,5] except Interval[0,10]
define IntDuringExcept: Interval[3,5] except Interval[0,10]
define IntEndsExcept: Interval[5,10] except Interval[0,10]

// @Test: DateTimeIntervalExcept
define DateTimeFullInterval: Interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2013, 1, 1, 0, 0, 0, 0)]
define DateTimeHalfInterval: Interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2012, 6, 1, 0, 0, 0, 0))
define DateTimeSameAsExcept: Interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2013, 1, 1, 0, 0, 0, 0)] except Interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2013, 1, 1, 0, 0, 0, 0)]
define DateTimeBeforeExcept: Interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2012, 4, 1, 0, 0, 0, 0)] except Interval[DateTime(2012, 7, 1, 0, 0, 0, 0), DateTime(2013, 1, 1, 0, 0, 0, 0)]
define DateTimeMeetsExcept: Interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2012, 6, 1, 0, 0, 0, 0)] except Interval[DateTime(2012, 6, 1, 0, 0, 0, 0), DateTime(2013, 1, 1, 0, 0, 0, 0)]
define DateTimeOverlapsExcept: Interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2012, 7, 1, 0, 0, 0, 0)] except Interval[DateTime(2012, 6, 1, 0, 0, 0, 0), DateTime(2013, 1, 1, 0, 0, 0, 0)]
define DateTimeBeginsExcept: Interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2012, 6, 1, 0, 0, 0, 0)] except Interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2013, 1, 1, 0, 0, 0, 0)]
define DateTimeDuringExcept: Interval[DateTime(2012, 3, 1, 0, 0, 0, 0), DateTime(2012, 5, 1, 0, 0, 0, 0)] except Interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2013, 1, 1, 0, 0, 0, 0)]
define DateTimeEndsExcept: Interval[DateTime(2012, 6, 1, 0, 0, 0, 0), DateTime(2013, 1, 1, 0, 0, 0, 0)] except Interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2013, 1, 1, 0, 0, 0, 0)]

// @Test: IntegerIntervalIntersect
define IntFullInterval: Interval[0,10]
define IntMeetsInterval: Interval[5,5]
define IntOverlapsInterval: Interval[5,7]
define IntBeginsInterval: Interval[0,5]
define IntDuringInterval: Interval[3,5]
define IntEndsInterval: Interval[5,10]
define IntSameAsIntersect: Interval[0,10] intersect Interval[0,10]
define IntBeforeIntersect: Interval[0,4] intersect Interval[6,10]
define IntMeetsIntersect: Interval[0,5] intersect Interval[5,10]
define IntOverlapsIntersect: Interval[0,7] intersect Interval[5,10]
define IntBeginsIntersect: Interval[0,5] intersect Interval[0,10]
define IntDuringIntersect: Interval[3,5] intersect Interval[0,10]
define IntEndsIntersect: Interval[5,10] intersect Interval[0,10]

// @Test: DateTimeIntervalIntersect
define DateTimeFullInterval: Interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2013, 1, 1, 0, 0, 0, 0)]
define DateTimeMeetsInterval: Interval[DateTime(2012, 6, 1, 0, 0, 0, 0), DateTime(2012, 6, 1, 0, 0, 0, 0)]
define DateTimeOverlapsInterval: Interval[DateTime(2012, 6, 1, 0, 0, 0, 0), DateTime(2012, 7, 1, 0, 0, 0, 0)]
define DateTimeBeginsInterval: Interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2012, 6, 1, 0, 0, 0, 0)]
define DateTimeDuringInterval: Interval[DateTime(2012, 3, 1, 0, 0, 0, 0), DateTime(2012, 5, 1, 0, 0, 0, 0)]
define DateTimeEndsInterval: Interval[DateTime(2012, 6, 1, 0, 0, 0, 0), DateTime(2013, 1, 1, 0, 0, 0, 0)]
define DateTimeSameAsIntersect: Interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2013, 1, 1, 0, 0, 0, 0)] intersect Interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2013, 1, 1, 0, 0, 0, 0)]
define DateTimeBeforeIntersect: Interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2012, 4, 1, 0, 0, 0, 0)] intersect Interval[DateTime(2012, 7, 1, 0, 0, 0, 0), DateTime(2013, 1, 1, 0, 0, 0, 0)]
define DateTimeMeetsIntersect: Interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2012, 6, 1, 0, 0, 0, 0)] intersect Interval[DateTime(2012, 6, 1, 0, 0, 0, 0), DateTime(2013, 1, 1, 0, 0, 0, 0)]
define DateTimeOverlapsIntersect: Interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2012, 7, 1, 0, 0, 0, 0)] intersect Interval[DateTime(2012, 6, 1, 0, 0, 0, 0), DateTime(2013, 1, 1, 0, 0, 0, 0)]
define DateTimeBeginsIntersect: Interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2012, 6, 1, 0, 0, 0, 0)] intersect Interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2013, 1, 1, 0, 0, 0, 0)]
define DateTimeDuringIntersect: Interval[DateTime(2012, 3, 1, 0, 0, 0, 0), DateTime(2012, 5, 1, 0, 0, 0, 0)] intersect Interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2013, 1, 1, 0, 0, 0, 0)]
define DateTimeEndsIntersect: Interval[DateTime(2012, 6, 1, 0, 0, 0, 0), DateTime(2013, 1, 1, 0, 0, 0, 0)] intersect Interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2013, 1, 1, 0, 0, 0, 0)]
