// @Test: Interval
define Open = interval(DateTime(2012, 1, 1), DateTime(2013, 1, 1))
define LeftOpen = interval(DateTime(2012, 1, 1), DateTime(2013, 1, 1)]
define RightOpen = interval[DateTime(2012, 1, 1), DateTime(2013, 1, 1))
define Closed = interval[DateTime(2012, 1, 1), DateTime(2013, 1, 1)]

// @Test: Equal
define EqualClosed = interval[1, 5] = interval[1, 5]
define EqualOpen = interval(1, 5) = interval(1, 5)
define EqualOpenClosed = interval(1, 5) = interval[2, 4]
define UnequalClosed = interval[1, 5] = interval[2, 4]
define UnequalOpen = interval(1, 5) = interval(2, 4)
define UnequalClosedOpen = interval[1, 5] = interval(2, 4)
define EqualDates = interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2013, 1, 1, 0, 0, 0, 0)) = interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2013, 1, 1, 0, 0, 0, 0))
define EqualDatesOpenClosed = interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2013, 1, 1, 0, 0, 0, 0)) = interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2012, 12, 31, 23, 59, 59, 999)]
define SameDays = interval[DateTime(2012, 1, 1), DateTime(2013, 1, 1)) = interval[DateTime(2012, 1, 1), DateTime(2013, 1, 1))
define DifferentDays = interval[DateTime(2012, 1, 1), DateTime(2013, 1, 1)) = interval[DateTime(2012, 1, 1), DateTime(2012, 7, 1))

// @Test: NotEqual
define EqualClosed = interval[1, 5] <> interval[1, 5]
define EqualOpen = interval(1, 5) <> interval(1, 5)
define EqualOpenClosed = interval(1, 5) <> interval[2, 4]
define UnequalClosed = interval[1, 5] <> interval[2, 4]
define UnequalOpen = interval(1, 5) <> interval(2, 4)
define UnequalClosedOpen = interval[1, 5] <> interval(2, 4)
define EqualDates = interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2013, 1, 1, 0, 0, 0, 0)) <> interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2013, 1, 1, 0, 0, 0, 0))
define EqualDatesOpenClosed = interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2013, 1, 1, 0, 0, 0, 0)) <> interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2012, 12, 31, 23, 59, 59, 999)]
define SameDays = interval[DateTime(2012, 1, 1), DateTime(2013, 1, 1)) <> interval[DateTime(2012, 1, 1), DateTime(2013, 1, 1))
define DifferentDays = interval[DateTime(2012, 1, 1), DateTime(2013, 1, 1)) <> interval[DateTime(2012, 1, 1), DateTime(2012, 7, 1))

// @Test: Contains
define ContainsInt = interval[1, 5] contains 3
define NotContainsInt = interval(1, 5] contains 1
define ContainsReal = interval[1.234, 3.456] contains 2.345
define NotContainsReal = interval[1.234, 3.456] contains 4.567
define DateIvl = interval[DateTime(2012, 3, 1, 0, 0, 0, 0), DateTime(2012, 9, 1, 0, 0, 0, 0))
define ContainsDate = DateIvl contains DateTime(2012, 6, 1, 0, 0, 0, 0)
define NotContainsDate = DateIvl contains DateTime(2012, 9, 1, 0, 0, 0, 0)
define ContainsImpreciseDate = DateIvl contains DateTime(2012, 4)
define NotContainsImpreciseDate = DateIvl contains DateTime(2012, 9)
define MayContainImpreciseDate = DateIvl contains DateTime(2012)
define ImpDateIvl = interval[DateTime(2012, 3), DateTime(2012, 8)]
define ImpreciseContainsDate = ImpDateIvl contains DateTime(2012, 6, 1, 0, 0, 0, 0)
define ImpreciseNotContainsDate = ImpDateIvl contains DateTime(2012, 9, 1, 0, 0, 0, 0)
define ImpreciseMayContainDate = ImpDateIvl contains DateTime(2012, 3, 15, 0, 0, 0, 0)
define NegInfBegContainsInt = interval[null, 5] contains -7
define NegInfBegNotContainsInt = interval[null, 5] contains 7
define UnknownBegContainsInt = interval(null, 5] contains 5
define UnknownBegMayContainInt = interval(null, 5] contains -7
define UnknownBegNotContainsInt = interval(null, 5] contains 7
define PosInfEndContainsInt = interval[0, null] contains 123456789
define PosInfEndNotContainsInt = interval[0, null] contains -1
define UnknownEndContainsInt = interval[0, null) contains 0
define UnknownEndMayContainInt = interval[0, null) contains 123456789
define UnknownEndNotContainsInt = interval[0, null) contains -1
define NegInfBegContainsDate = interval[null, DateTime(2013, 1, 1, 0, 0, 0, 0)) contains DateTime(1900, 1, 1, 0, 0, 0, 0)
define NegInfBegNotContainsDate = interval[null, DateTime(2013, 1, 1, 0, 0, 0, 0)) contains DateTime(2013, 1, 2, 0, 0, 0, 0)
define UnknownBegContainsDate = interval(null, DateTime(2013, 1, 1, 0, 0, 0, 0)) contains DateTime(2012, 12, 31, 23, 59, 59, 999)
define UnknownBegMayContainDate = interval(null, DateTime(2013, 1, 1, 0, 0, 0, 0)) contains DateTime(1900, 1, 1, 0, 0, 0, 0)
define UnknownBegNotContainsDate = interval(null, DateTime(2013, 1, 1, 0, 0, 0, 0)) contains DateTime(2013, 1, 2, 0, 0, 0, 0)
define PosInfEndContainsDate = interval[DateTime(2013, 1, 1, 0, 0, 0, 0), null] contains DateTime(2014, 1, 1, 0, 0, 0, 0)
define PosInfEndNotContainsDate = interval[DateTime(2013, 1, 1, 0, 0, 0, 0), null] contains DateTime(2012, 1, 1, 0, 0, 0, 0)
define UnknownEndContainsDate = interval[DateTime(2013, 1, 1, 0, 0, 0, 0), null) contains DateTime(2013, 1, 1, 0, 0, 0, 0)
define UnknownEndMayContainDate = interval[DateTime(2013, 1, 1, 0, 0, 0, 0), null) contains DateTime(2014, 1, 1, 0, 0, 0, 0)
define UnknownEndNotContainsDate = interval[DateTime(2013, 1, 1, 0, 0, 0, 0), null) contains DateTime(2012, 1, 1, 0, 0, 0, 0)

// @Test: In
define ContainsInt = 3 in interval[1, 5]
define NotContainsInt = 1 in interval(1, 5]
define ContainsReal = 2.345 in interval[1.234, 3.456]
define NotContainsReal = 4.567 in interval[1.234, 3.456]
define DateIvl = interval[DateTime(2012, 3, 1, 0, 0, 0, 0), DateTime(2012, 9, 1, 0, 0, 0, 0))
define ContainsDate = DateTime(2012, 6, 1, 0, 0, 0, 0) in DateIvl
define NotContainsDate = DateTime(2012, 9, 1, 0, 0, 0, 0) in DateIvl
define ContainsImpreciseDate = DateTime(2012, 4) in DateIvl
define NotContainsImpreciseDate = DateTime(2012, 9) in DateIvl
define MayContainImpreciseDate = DateTime(2012) in DateIvl
define ImpDateIvl = interval[DateTime(2012, 3), DateTime(2012, 8)]
define ImpreciseContainsDate = DateTime(2012, 6, 1, 0, 0, 0, 0) in ImpDateIvl
define ImpreciseNotContainsDate = DateTime(2012, 9, 1, 0, 0, 0, 0) in ImpDateIvl
define ImpreciseMayContainDate = DateTime(2012, 3, 15, 0, 0, 0, 0) in ImpDateIvl
define NegInfBegContainsInt = -7 in interval[null, 5]
define NegInfBegNotContainsInt = 7 in interval[null, 5]
define UnknownBegContainsInt = 5 in interval(null, 5]
define UnknownBegMayContainInt = -7 in interval(null, 5]
define UnknownBegNotContainsInt = 7 in interval(null, 5]
define PosInfEndContainsInt = 123456789 in interval[0, null]
define PosInfEndNotContainsInt = -1 in interval[0, null]
define UnknownEndContainsInt = 0 in interval[0, null)
define UnknownEndMayContainInt = 123456789 in interval[0, null)
define UnknownEndNotContainsInt = -1 in interval[0, null)
define NegInfBegContainsDate = DateTime(1900, 1, 1, 0, 0, 0, 0) in interval[null, DateTime(2013, 1, 1, 0, 0, 0, 0))
define NegInfBegNotContainsDate = DateTime(2013, 1, 2, 0, 0, 0, 0) in interval[null, DateTime(2013, 1, 1, 0, 0, 0, 0))
define UnknownBegContainsDate = DateTime(2012, 12, 31, 23, 59, 59, 999) in interval(null, DateTime(2013, 1, 1, 0, 0, 0, 0))
define UnknownBegMayContainDate = DateTime(1900, 1, 1, 0, 0, 0, 0) in interval(null, DateTime(2013, 1, 1, 0, 0, 0, 0))
define UnknownBegNotContainsDate = DateTime(2013, 1, 2, 0, 0, 0, 0) in interval(null, DateTime(2013, 1, 1, 0, 0, 0, 0))
define PosInfEndContainsDate =  DateTime(2014, 1, 1, 0, 0, 0, 0) in interval[DateTime(2013, 1, 1, 0, 0, 0, 0), null]
define PosInfEndNotContainsDate = DateTime(2012, 1, 1, 0, 0, 0, 0) in interval[DateTime(2013, 1, 1, 0, 0, 0, 0), null]
define UnknownEndContainsDate = DateTime(2013, 1, 1, 0, 0, 0, 0) in interval[DateTime(2013, 1, 1, 0, 0, 0, 0), null)
define UnknownEndMayContainDate = DateTime(2014, 1, 1, 0, 0, 0, 0) in interval[DateTime(2013, 1, 1, 0, 0, 0, 0), null)
define UnknownEndNotContainsDate = DateTime(2012, 1, 1, 0, 0, 0, 0) in interval[DateTime(2013, 1, 1, 0, 0, 0, 0), null)

// @Test: Includes
define IncludesIntIvl = interval[1, 5] includes interval[1, 4]
define NotIncludesIntIvl = interval(1, 5] includes interval[1, 4]
define IncludesRealIvl = interval[1.234, 3.456] includes interval[2.34, 2.56]
define NotIncludesRealIvl = interval[1.234, 3.456] includes interval[1.23, 2.56]
define DateIvl = interval[DateTime(2012, 3, 1, 0, 0, 0, 0), DateTime(2012, 9, 1, 0, 0, 0, 0))
define IncludesDateIvl = DateIvl includes interval[DateTime(2012, 4, 1, 0, 0, 0, 0), DateTime(2012, 6, 1, 0, 0, 0, 0))
define NotIncludesDateIvl = DateIvl includes interval[DateTime(2012, 6, 1, 0, 0, 0, 0), DateTime(2012, 9, 1, 0, 0, 0, 0)]
define IncludesImpreciseDateIvl = DateIvl includes interval[DateTime(2012, 4), DateTime(2012, 6)]
define NotIncludesImpreciseDateIvl = DateIvl includes interval[DateTime(2012, 4), DateTime(2012, 9)]
define MayIncludeImpreciseDateIvl = DateIvl includes interval[DateTime(2012), DateTime(2012)]
define ImpDateIvl = interval[DateTime(2012, 3), DateTime(2012, 8)]
define ImpreciseIncludesDateIvl = ImpDateIvl includes interval[DateTime(2012, 4, 1, 0, 0, 0, 0), DateTime(2012, 6, 1, 0, 0, 0, 0))
define ImpreciseNotIncludesDateIvl = ImpDateIvl includes interval[DateTime(2012, 2, 1, 0, 0, 0, 0), DateTime(2012, 6, 1, 0, 0, 0, 0))
define ImpreciseMayIncludeDateIvl = ImpDateIvl includes interval[DateTime(2012, 3, 15, 0, 0, 0, 0), DateTime(2012, 6, 1, 0, 0, 0, 0))
define NegInfBegIncludesIntIvl = interval[null, 5] includes interval[-7, 2]
define NegInfBegNotIncludesIntIvl = interval[null, 5] includes interval[4, 7]
define UnknownBegIncludesIntIvl = interval(null, 5] includes interval[5, 5]
define UnknownBegMayIncludeIntIvl = interval(null, 5] includes interval[-7, 2]
define UnknownBegNotIncludesIntIvl = interval(null, 5] includes interval[4, 7]
define PosInfEndIncludesIntIvl = interval[0, null] includes interval[1234, 5678]
define PosInfEndNotIncludesIntIvl = interval[0, null] includes interval[-1234, 5678]
define UnknownEndIncludesIntIvl = interval[0, null) includes interval[0, 0]
define UnknownEndMayIncludeIntIvl = interval[0, null) includes interval[1234, 5678]
define UnknownEndNotIncludesIntIvl = interval[0, null) includes interval[-1234, 5678]
define NegInfBegIncludesDateIvl = interval[null, DateTime(2013, 1, 1, 0, 0, 0, 0)) includes interval[DateTime(1900, 1, 1, 0, 0, 0, 0), DateTime(2000, 1, 1, 0, 0, 0, 0)]
define NegInfBegNotIncludesDateIvl = interval[null, DateTime(2013, 1, 1, 0, 0, 0, 0)) includes interval[DateTime(1900, 1, 1, 0, 0, 0, 0), DateTime(2013, 1, 1, 0, 0, 0, 0)]
define UnknownBegIncludesDateIvl = interval(null, DateTime(2013, 1, 1, 0, 0, 0, 0)) includes interval[DateTime(2012, 12, 31, 23, 59, 59, 999), DateTime(2012, 12, 31, 23, 59, 59, 999)]
define UnknownBegMayIncludeDateIvl = interval(null, DateTime(2013, 1, 1, 0, 0, 0, 0)) includes interval[DateTime(1900, 1, 1, 0, 0, 0, 0), DateTime(2000, 1, 1, 0, 0, 0, 0)]
define UnknownBegNotIncludesDateIvl = interval(null, DateTime(2013, 1, 1, 0, 0, 0, 0)) includes interval[DateTime(1900, 1, 1, 0, 0, 0, 0), DateTime(2013, 1, 1, 0, 0, 0, 0)]
define PosInfEndIncludesDateIvl = interval[DateTime(2013, 1, 1, 0, 0, 0, 0), null] includes interval[DateTime(2014, 1, 1, 0, 0, 0, 0), DateTime(2015, 1, 1, 0, 0, 0, 0)]
define PosInfEndNotIncludesDateIvl = interval[DateTime(2013, 1, 1, 0, 0, 0, 0), null] includes interval[DateTime(2000, 1, 1, 0, 0, 0, 0), DateTime(2014, 1, 1, 0, 0, 0, 0)]
define UnknownEndIncludesDateIvl = interval[DateTime(2013, 1, 1, 0, 0, 0, 0), null) includes interval[DateTime(2013, 1, 1, 0, 0, 0, 0), DateTime(2013, 1, 1, 0, 0, 0, 0)]
define UnknownEndMayIncludeDateIvl = interval[DateTime(2013, 1, 1, 0, 0, 0, 0), null) includes interval[DateTime(2014, 1, 1, 0, 0, 0, 0), DateTime(2015, 1, 1, 0, 0, 0, 0)]
define UnknownEndNotIncludesDateIvl = interval[DateTime(2013, 1, 1, 0, 0, 0, 0), null) includes interval[DateTime(2000, 1, 1, 0, 0, 0, 0), DateTime(2014, 1, 1, 0, 0, 0, 0)]

// @Test: ProperlyIncludes
define ProperlyIncludesIntIvl = interval[1, 5] properly includes interval[1, 4]
define NotProperlyIncludesIntIvl = interval[1, 5] properly includes interval[1, 5]
define ProperlyIncludesRealIvl = interval[1.234, 3.456] properly includes interval[2.34, 2.56]
define NotProperlyIncludesRealIvl = interval[1.234, 3.456] properly includes interval[1.234, 3.456]
define DateIvl = interval[DateTime(2012, 3, 1, 0, 0, 0, 0), DateTime(2012, 9, 1, 0, 0, 0, 0))
define ProperlyIncludesDateIvl = DateIvl properly includes interval[DateTime(2012, 4, 1, 0, 0, 0, 0), DateTime(2012, 6, 1, 0, 0, 0, 0))
define NotProperlyIncludesDateIvl = DateIvl properly includes interval[DateTime(2012, 3, 1, 0, 0, 0, 0), DateTime(2012, 9, 1, 0, 0, 0, 0))
define PosInfEndProperlyIncludesIntIvl = interval[0, null] properly includes interval[1234, 5678]
define PosInfEndNotProperlyIncludesIntIvl = interval[0, null] properly includes interval[0, null]
define UnknownEndMayProperlyIncludeIntIvl = interval[0, null) properly includes interval[0, 0]

// @Test: IncludedIn
define IncludesIntIvl = interval[1, 4] included in interval[1, 5]
define NotIncludesIntIvl = interval[1, 4] included in interval(1, 5]
define IncludesRealIvl = interval[2.34, 2.56] included in interval[1.234, 3.456]
define NotIncludesRealIvl = interval[1.23, 2.56] included in interval[1.234, 3.456]
define DateIvl = interval[DateTime(2012, 3, 1, 0, 0, 0, 0), DateTime(2012, 9, 1, 0, 0, 0, 0))
define IncludesDateIvl = interval[DateTime(2012, 4, 1, 0, 0, 0, 0), DateTime(2012, 6, 1, 0, 0, 0, 0)) included in DateIvl
define NotIncludesDateIvl = interval[DateTime(2012, 6, 1, 0, 0, 0, 0), DateTime(2012, 9, 1, 0, 0, 0, 0)] included in DateIvl
define IncludesImpreciseDateIvl = interval[DateTime(2012, 4), DateTime(2012, 6)] included in DateIvl
define NotIncludesImpreciseDateIvl = interval[DateTime(2012, 4), DateTime(2012, 9)] included in DateIvl
define MayIncludeImpreciseDateIvl = interval[DateTime(2012), DateTime(2012)] included in DateIvl
define ImpDateIvl = interval[DateTime(2012, 3), DateTime(2012, 8)]
define ImpreciseIncludesDateIvl = interval[DateTime(2012, 4, 1, 0, 0, 0, 0), DateTime(2012, 6, 1, 0, 0, 0, 0)) included in ImpDateIvl
define ImpreciseNotIncludesDateIvl = interval[DateTime(2012, 2, 1, 0, 0, 0, 0), DateTime(2012, 6, 1, 0, 0, 0, 0)) included in ImpDateIvl
define ImpreciseMayIncludeDateIvl = interval[DateTime(2012, 3, 15, 0, 0, 0, 0), DateTime(2012, 6, 1, 0, 0, 0, 0)) included in ImpDateIvl
define NegInfBegIncludedInIntIvl = interval[null, 5] included in interval[null, 100]
define NegInfBegNotIncludedInIntIvl = interval[null, 5] included in interval[-100, 100]
define UnknownBegIncludedInIntIvl = interval(null, 5] included in interval[null, 100]
define UnknownBegMayBeIncludedInIntIvl = interval(null, 5] included in interval[-100, 100]
define UnknownBegNotIncludedInIntIvl = interval(null, 5] included in interval[-100, 0]
define PosInfEndIncludedInIntIvl = interval[0, null] included in interval[-100, null]
define PosInfEndNotIncludedInIntIvl = interval[0, null] included in interval[-100, 100]
define UnknownEndIncludedInIntIvl = interval[0, null) included in interval[-100, null]
define UnknownEndMayBeIncludedInIntIvl = interval[0, null) included in interval[-100, 100]
define UnknownEndNotIncludedInIntIvl = interval[0, null) included in interval[100, 200]
define NegInfBegIncludedInDateIvl = interval[null, DateTime(2013, 1, 1, 0, 0, 0, 0)) included in interval[null, DateTime(2020, 1, 1, 0, 0, 0, 0)]
define NegInfBegNotIncludedInDateIvl = interval[null, DateTime(2013, 1, 1, 0, 0, 0, 0)) included in interval[DateTime(2000, 1, 1, 0, 0, 0, 0), DateTime(2020, 1, 1, 0, 0, 0, 0)]
define UnknownBegIncludedInDateIvl = interval(null, DateTime(2013, 1, 1, 0, 0, 0, 0)) included in interval[null, DateTime(2020, 1, 1, 0, 0, 0, 0)]
define UnknownBegMayBeIncludedInDateIvl = interval(null, DateTime(2013, 1, 1, 0, 0, 0, 0)) included in interval[DateTime(2000, 1, 1, 0, 0, 0, 0), DateTime(2020, 1, 1, 0, 0, 0, 0)]
define UnknownBegNotIncludedInDateIvl = interval(null, DateTime(2013, 1, 1, 0, 0, 0, 0)) included in interval[DateTime(1900, 1, 1, 0, 0, 0, 0), DateTime(2000, 1, 1, 0, 0, 0, 0)]
define PosInfEndIncludedInDateIvl = interval[DateTime(2013, 1, 1, 0, 0, 0, 0), null] included in interval[DateTime(2000, 1, 1, 0, 0, 0, 0), null]
define PosInfEndNotIncludedInDateIvl = interval[DateTime(2013, 1, 1, 0, 0, 0, 0), null] included in interval[DateTime(2000, 1, 1, 0, 0, 0, 0), DateTime(2020, 1, 1, 0, 0, 0, 0)]
define UnknownEndIncludedInDateIvl = interval[DateTime(2013, 1, 1, 0, 0, 0, 0), null) included in interval[DateTime(2000, 1, 1, 0, 0, 0, 0), null]
define UnknownEndMayBeIncludedInDateIvl = interval[DateTime(2013, 1, 1, 0, 0, 0, 0), null) included in interval[DateTime(2000, 1, 1, 0, 0, 0, 0), DateTime(2020, 1, 1, 0, 0, 0, 0)]
define UnknownEndNotIncludedInDateIvl = interval[DateTime(2013, 1, 1, 0, 0, 0, 0), null) included in interval[DateTime(2020, 1, 1, 0, 0, 0, 0), DateTime(2040, 1, 1, 0, 0, 0, 0)]

// @Test: ProperlyIncludedIn
define ProperlyIncludesIntIvl = interval[1, 4] properly included in interval[1, 5]
define NotProperlyIncludesIntIvl = interval[1, 5] properly included in interval[1, 5]
define ProperlyIncludesRealIvl = interval[2.34, 2.56] properly included in interval[1.234, 3.456]
define NotProperlyIncludesRealIvl = interval[1.23, 2.56] properly included in interval[1.23, 2.56]
define DateIvl = interval[DateTime(2012, 3, 1, 0, 0, 0, 0), DateTime(2012, 9, 1, 0, 0, 0, 0))
define ProperlyIncludesDateIvl = interval[DateTime(2012, 4, 1, 0, 0, 0, 0), DateTime(2012, 6, 1, 0, 0, 0, 0)) properly included in DateIvl
define NotProperlyIncludesDateIvl = interval[DateTime(2012, 3, 1, 0, 0, 0, 0), DateTime(2012, 9, 1, 0, 0, 0, 0)) properly included in DateIvl
define PosInfEndProperlyIncludedInDateIvl = interval[DateTime(2013, 1, 1, 0, 0, 0, 0), null] properly included in interval[DateTime(2000, 1, 1, 0, 0, 0, 0), null]
define PosInfEndNotProperlyIncludedInDateIvl = interval[DateTime(2013, 1, 1, 0, 0, 0, 0), null] properly included in interval[DateTime(2000, 1, 1, 0, 0, 0, 0), DateTime(2020, 1, 1, 0, 0, 0, 0)]
define UnknownEndMayBeProperlyIncludedInDateIvl = interval[DateTime(2013, 1, 1, 0, 0, 0, 0), null) properly included in interval[DateTime(2000, 1, 1, 0, 0, 0, 0), DateTime(2020, 1, 1, 0, 0, 0, 0)]

// @Test: After
define AfterIntIvl = interval[5, 10] after interval[2, 4]
define NotAfterIntIvl = interval[5, 10] after interval[2, 5]
define AfterRealIvl = interval[1.234, 2.345] after interval[0.0, 1.23]
define NotAfterRealIvl = interval[1.234, 2.345] after interval[0.0, 1.234]
define DateIvl = interval[DateTime(2012, 3, 1, 0, 0, 0, 0), DateTime(2012, 9, 1, 0, 0, 0, 0))
define AfterDateIvl = DateIvl after interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2012, 3, 1, 0, 0, 0, 0))
define NotAfterDateIvl = DateIvl after interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2012, 3, 1, 0, 0, 0, 0)]
define AfterImpreciseDateIvl = DateIvl after interval[DateTime(2012, 1), DateTime(2012, 2)]
define NotAfterImpreciseDateIvl = DateIvl after interval[DateTime(2012, 1), DateTime(2012, 3)]
define MayBeAfterImpreciseDateIvl = DateIvl after interval[DateTime(2012), DateTime(2012)]
define ImpDateIvl = interval[DateTime(2012, 3), DateTime(2012, 8)]
define ImpreciseAfterDateIvl = ImpDateIvl after interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2012, 3, 1, 0, 0, 0, 0))
define ImpreciseNotAfterDateIvl = ImpDateIvl after interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2012, 4, 1, 0, 0, 0, 0))
define ImpreciseMayBeAfterDateIvl = ImpDateIvl after interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2012, 3, 15, 0, 0, 0, 0))
define NegInfBegNotAfterIntIvl = interval[null, 100] after interval[-100, 0]
define UnknownBegMayBeAfterIntIvl = interval(null, 100] after interval[-100, 0]
define UnknownBegNotAfterIntIvl = interval(null, 5] after interval[0, 100]
define PosInfEndAfterIntIvl = interval[0, null] after interval[-100, -20]
define PosInfEndNotAfterIntIvl = interval[0, null] after interval[-100, 0]
define UnknownEndAfterIntIvl = interval[0, null) after interval[-100, -20]
define UnknownEndNotAfterIntIvl = interval[0, null) after interval[-100, 0]
define NegInfBegNotAfterDateIvl = interval[null, DateTime(2013, 1, 1, 0, 0, 0, 0)) after interval[DateTime(2000, 1, 1, 0, 0, 0, 0), DateTime(2010, 1, 1, 0, 0, 0, 0)]
define UnknownBegMayBeAfterDateIvl = interval(null, DateTime(2013, 1, 1, 0, 0, 0, 0)) after interval[DateTime(2000, 1, 1, 0, 0, 0, 0), DateTime(2010, 1, 1, 0, 0, 0, 0)]
define UnknownBegNotAfterDateIvl = interval(null, DateTime(2013, 1, 1, 0, 0, 0, 0)) after interval[DateTime(2000, 1, 1, 0, 0, 0, 0), DateTime(2020, 1, 1, 0, 0, 0, 0)]
define PosInfEndAfterDateIvl = interval[DateTime(2013, 1, 1, 0, 0, 0, 0), null] after interval[DateTime(2000, 1, 1, 0, 0, 0, 0), DateTime(2010, 1, 1, 0, 0, 0, 0)]
define PosInfEndNotAfterDateIvl = interval[DateTime(2013, 1, 1, 0, 0, 0, 0), null] after interval[DateTime(2000, 1, 1, 0, 0, 0, 0), DateTime(2020, 1, 1, 0, 0, 0, 0)]
define UnknownEndAfterDateIvl = interval[DateTime(2013, 1, 1, 0, 0, 0, 0), null) after interval[DateTime(2000, 1, 1, 0, 0, 0, 0), DateTime(2010, 1, 1, 0, 0, 0, 0)]
define UnknownEndNotAfterDateIvl = interval[DateTime(2013, 1, 1, 0, 0, 0, 0), null) after interval[DateTime(2020, 1, 1, 0, 0, 0, 0), DateTime(2040, 1, 1, 0, 0, 0, 0)]

// @Test: Before
define BeforeIntIvl = interval[2, 4] before interval[5, 10]
define NotBeforeIntIvl = interval[2, 5] before interval[5, 10]
define BeforeRealIvl = interval[0.0, 1.23] before interval[1.234, 2.345]
define NotBeforeRealIvl = interval[1.234, 2.345] before interval[0.0, 1.234]
define DateIvl = interval[DateTime(2012, 3, 1, 0, 0, 0, 0), DateTime(2012, 9, 1, 0, 0, 0, 0))
define BeforeDateIvl = interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2012, 3, 1, 0, 0, 0, 0)) before DateIvl
define NotBeforeDateIvl = interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2012, 3, 1, 0, 0, 0, 0)] before DateIvl
define BeforeImpreciseDateIvl = DateIvl before interval[DateTime(2012, 9), DateTime(2012, 12)]
define NotBeforeImpreciseDateIvl = DateIvl before interval[DateTime(2012, 8), DateTime(2012, 12)]
define MayBeBeforeImpreciseDateIvl = DateIvl before interval[DateTime(2012), DateTime(2012)]
define ImpDateIvl = interval[DateTime(2012, 3), DateTime(2012, 8)]
define ImpreciseBeforeDateIvl = ImpDateIvl before interval[DateTime(2012, 9, 1, 0, 0, 0, 0), DateTime(2012, 12, 1, 0, 0, 0, 0))
define ImpreciseNotBeforeDateIvl = ImpDateIvl before interval[DateTime(2012, 8, 1, 0, 0, 0, 0), DateTime(2012, 12, 1, 0, 0, 0, 0))
define ImpreciseMayBeBeforeDateIvl = ImpDateIvl before interval[DateTime(2012, 8, 15, 0, 0, 0, 0), DateTime(2012, 12, 15, 0, 0, 0, 0))
define NegInfBegBeforeIntIvl = interval[null, 100] before interval[101, 200]
define NegInfBegNotBeforeIntIvl = interval[null, 100] before interval[100, 200]
define UnknownBegBeforeIntIvl = interval(null, 100] before interval[101, 200]
define UnknownBegNotBeforeIntIvl = interval(null, 5] before interval[0, 100]
define PosInfEndNotBeforeIntIvl = interval[0, null] before interval[1000, 2000]
define UnknownEndMayBeBeforeIntIvl = interval[0, null) before interval[1000, 2000]
define UnknownEndNotBeforeIntIvl = interval[0, null) before interval[-100, 0]
define NegInfBegBeforeDateIvl = interval[null, DateTime(2013, 1, 1, 0, 0, 0, 0)) before interval[DateTime(2020, 1, 1, 0, 0, 0, 0), DateTime(2030, 1, 1, 0, 0, 0, 0)]
define NegInfBegNotBeforeDateIvl = interval[null, DateTime(2013, 1, 1, 0, 0, 0, 0)) before interval[DateTime(2000, 1, 1, 0, 0, 0, 0), DateTime(2010, 1, 1, 0, 0, 0, 0)]
define UnknownBegBeforeDateIvl = interval(null, DateTime(2013, 1, 1, 0, 0, 0, 0)) before interval[DateTime(2020, 1, 1, 0, 0, 0, 0), DateTime(2030, 1, 1, 0, 0, 0, 0)]
define UnknownBegNotBeforeDateIvl = interval(null, DateTime(2013, 1, 1, 0, 0, 0, 0)) before interval[DateTime(2000, 1, 1, 0, 0, 0, 0), DateTime(2020, 1, 1, 0, 0, 0, 0)]
define PosInfEndNotBeforeDateIvl = interval[DateTime(2013, 1, 1, 0, 0, 0, 0), null] before interval[DateTime(2000, 1, 1, 0, 0, 0, 0), DateTime(2020, 1, 1, 0, 0, 0, 0)]
define UnknownEndMayBeBeforeDateIvl = interval[DateTime(2013, 1, 1, 0, 0, 0, 0), null) before interval[DateTime(2020, 1, 1, 0, 0, 0, 0), DateTime(2040, 1, 1, 0, 0, 0, 0)]
define UnknownEndNotBeforeDateIvl = interval[DateTime(2013, 1, 1, 0, 0, 0, 0), null) before interval[DateTime(2000, 1, 1, 0, 0, 0, 0), DateTime(2013, 1, 1, 0, 0, 0, 0)]


// @Test: Meets
define MeetsAfterIntIvl = interval[11, 15] meets interval[5, 10]
define MeetsBeforeIntIvl = interval[1, 4] meets interval[5, 10]
define NotMeetsIntIvl = interval[1, 2] meets interval[5, 10]
define MeetsAfterRealIvl = interval[1.50000001, 2.5] meets interval[0.5, 1.5]
define MeetsBeforeRealIvl = interval[0.5, 1.5] meets interval[1.50000001, 2.5]
define NotMeetsRealIvl = interval[0.0, 1.0] meets interval[1.1, 2.0]
define DateIvl = interval[DateTime(2012, 3, 1, 0, 0, 0, 0), DateTime(2012, 9, 1, 0, 0, 0, 0))
define MeetsAfterDateIvl = interval[DateTime(2012, 9, 1, 0, 0, 0, 0), DateTime(2012, 12, 1, 0, 0, 0, 0)) meets DateIvl
define MeetsBeforeDateIvl = interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2012, 3, 1, 0, 0, 0, 0)) meets DateIvl
define NotMeetsDateIvl = interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2012, 2, 29, 0, 0, 0, 0)) meets DateIvl
define MayMeetAfterImpreciseDateIvl = DateIvl meets interval[DateTime(2012, 1), DateTime(2012, 2)]
define MayMeetBeforeImpreciseDateIvl = DateIvl meets interval[DateTime(2012, 9), DateTime(2012, 12)]
define NotMeetsImpreciseDateIvl = DateIvl meets interval[DateTime(2012, 1), DateTime(2012, 12)]
define ImpDateIvl = interval[DateTime(2012, 3), DateTime(2012, 8)]
define ImpreciseMayMeetAfterDateIvl = ImpDateIvl meets interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2012, 2, 29, 23, 59, 59, 999)]
define ImpreciseMayMeetBeforeDateIvl = ImpDateIvl meets interval[DateTime(2012, 9, 1, 0, 0, 0, 0), DateTime(2012, 12, 31, 23, 59, 59, 999)]
define ImpreciseNotMeetsDateIvl = ImpDateIvl meets interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2012, 12, 31, 23, 59, 59, 999))
define NegInfBegMeetsBeforeIntIvl = interval[null, 100] meets interval[101, 200]
define NegInfBegNotMeetsIntIvl = interval[null, 100] meets interval[100, 200]
define IntIvlNotMeetsNegInfBeg = interval[100, 200] meets interval[null, 400]
define UnknownBegMeetsBeforeIntIvl = interval(null, 100] meets interval[101, 200]
define UnknownBegMayMeetAfterIntIvl = interval(null, 100] meets interval[0, 50]
define UnknownBegNotMeetsIntIvl = interval(null, 5] meets interval[0, 100]
define IntIvlMayMeetBeforeUnknownBeg = interval[0, 100] meets interval(null, 400)
define PosInfEndMeetsAfterIntIvl = interval[100, null] meets interval[0, 99]
define PosInfEndNotMeetsIntIvl = interval[0, null] meets interval[1000, 2000]
define IntIvlNotMeetsPosInfEnd = interval[100, 200] meets interval[0, null]
define UnknownEndMeetsAfterIntIvl = interval[100, null) meets interval[0, 99]
define UnknownEndMayMeetBeforeIntIvl = interval[0, null) meets interval[50, 100]
define UnknownEndNotMeetsIntIvl = interval[0, null) meets interval[0, 100]
define IntIvlMayMeetAfterUnknownEnd = interval[0, 100] meets interval[-100, null)
define NegInfBegMeetsBeforeDateIvl = interval[null, DateTime(2013, 1, 1, 0, 0, 0, 0)) meets interval[DateTime(2013, 1, 1, 0, 0, 0, 0), DateTime(2030, 1, 1, 0, 0, 0, 0)]
define NegInfBegNotMeetsDateIvl = interval[null, DateTime(2013, 1, 1, 0, 0, 0, 0)) meets interval[DateTime(2000, 1, 1, 0, 0, 0, 0), DateTime(2010, 1, 1, 0, 0, 0, 0)]
define DateIvlNotMeetsNegInfBeg = interval[DateTime(2000, 1, 1, 0, 0, 0, 0), DateTime(2010, 1, 1, 0, 0, 0, 0)] meets interval[null, DateTime(2013, 1, 1, 0, 0, 0, 0))
define UnknownBegMeetsBeforeDateIvl = interval(null, DateTime(2013, 1, 1, 0, 0, 0, 0)) meets interval[DateTime(2013, 1, 1, 0, 0, 0, 0), DateTime(2030, 1, 1, 0, 0, 0, 0)]
define UnknownBegMayMeetAfterDateIvl = interval(null, DateTime(2013, 1, 1, 0, 0, 0, 0)) meets interval[DateTime(2000, 1, 1, 0, 0, 0, 0), DateTime(2010, 1, 1, 0, 0, 0, 0)]
define UnknownBegNotMeetsDateIvl = interval(null, DateTime(2013, 1, 1, 0, 0, 0, 0)) meets interval[DateTime(2000, 1, 1, 0, 0, 0, 0), DateTime(2020, 1, 1, 0, 0, 0, 0)]
define DateIvlMayMeetBeforeUnknownBeg = interval[DateTime(2000, 1, 1, 0, 0, 0, 0), DateTime(2010, 1, 1, 0, 0, 0, 0)] meets interval(null, DateTime(2013, 1, 1, 0, 0, 0, 0))
define PosInfEndMeetsAfterDateIvl = interval[DateTime(2013, 1, 1, 0, 0, 0, 0), null] meets interval[DateTime(2000, 1, 1, 0, 0, 0, 0), DateTime(2013, 1, 1, 0, 0, 0, 0))
define PosInfEndNotMeetsDateIvl = interval[DateTime(2013, 1, 1, 0, 0, 0, 0), null] meets interval[DateTime(2000, 1, 1, 0, 0, 0, 0), DateTime(2020, 1, 1, 0, 0, 0, 0)]
define DateIvlNotMeetsPosInfEnd = interval[DateTime(2000, 1, 1, 0, 0, 0, 0), DateTime(2020, 1, 1, 0, 0, 0, 0)] meets interval[DateTime(2013, 1, 1, 0, 0, 0, 0), null]
define UnknownEndMeetsAfterDateIvl = interval[DateTime(2013, 1, 1, 0, 0, 0, 0), null) meets interval[DateTime(2010, 1, 1, 0, 0, 0, 0), DateTime(2013, 1, 1, 0, 0, 0, 0))
define UnknownEndMayMeetBeforeDateIvl = interval[DateTime(2013, 1, 1, 0, 0, 0, 0), null) meets interval[DateTime(2020, 1, 1, 0, 0, 0, 0), DateTime(2040, 1, 1, 0, 0, 0, 0)]
define UnknownEndNotMeetsDateIvl = interval[DateTime(2013, 1, 1, 0, 0, 0, 0), null) meets interval[DateTime(2000, 1, 1, 0, 0, 0, 0), DateTime(2013, 1, 1, 0, 0, 0, 0)]
define DateIvlMayMeetAfterUnknownEnd = interval[DateTime(2020, 1, 1, 0, 0, 0, 0), DateTime(2040, 1, 1, 0, 0, 0, 0)] meets interval[DateTime(2013, 1, 1, 0, 0, 0, 0), null)

// @Test: MeetsAfter
define MeetsAfterIntIvl = interval[11, 15] meets after interval[5, 10]
define MeetsBeforeIntIvl = interval[1, 4] meets after interval[5, 10]
define NotMeetsIntIvl = interval[1, 2] meets after interval[5, 10]
define MeetsAfterRealIvl = interval[1.50000001, 2.5] meets after interval[0.5, 1.5]
define MeetsBeforeRealIvl = interval[0.5, 1.5] meets after interval[1.50000001, 2.5]
define NotMeetsRealIvl = interval[0.0, 1.0] meets after interval[1.1, 2.0]
define DateIvl = interval[DateTime(2012, 3, 1, 0, 0, 0, 0), DateTime(2012, 9, 1, 0, 0, 0, 0))
define MeetsAfterDateIvl = interval[DateTime(2012, 9, 1, 0, 0, 0, 0), DateTime(2012, 12, 1, 0, 0, 0, 0)) meets after DateIvl
define MeetsBeforeDateIvl = interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2012, 3, 1, 0, 0, 0, 0)) meets after DateIvl
define NotMeetsDateIvl = interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2012, 2, 29, 0, 0, 0, 0)) meets after DateIvl
define MayMeetAfterImpreciseDateIvl = DateIvl meets after interval[DateTime(2012, 1), DateTime(2012, 2)]
define MayMeetBeforeImpreciseDateIvl = DateIvl meets after interval[DateTime(2012, 9), DateTime(2012, 12)]
define NotMeetsImpreciseDateIvl = DateIvl meets after interval[DateTime(2012, 1), DateTime(2012, 12)]
define ImpDateIvl = interval[DateTime(2012, 3), DateTime(2012, 8)]
define ImpreciseMayMeetAfterDateIvl = ImpDateIvl meets after interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2012, 2, 29, 23, 59, 59, 999)]
define ImpreciseMayMeetBeforeDateIvl = ImpDateIvl meets after interval[DateTime(2012, 9, 1, 0, 0, 0, 0), DateTime(2012, 12, 31, 23, 59, 59, 999)]
define ImpreciseNotMeetsDateIvl = ImpDateIvl meets after interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2012, 12, 31, 23, 59, 59, 999))
define NegInfBegMeetsBeforeIntIvl = interval[null, 100] meets after interval[101, 200]
define NegInfBegNotMeetsIntIvl = interval[null, 100] meets after interval[100, 200]
define IntIvlNotMeetsNegInfBeg = interval[100, 200] meets after interval[null, 400]
define UnknownBegMeetsBeforeIntIvl = interval(null, 100] meets after interval[101, 200]
define UnknownBegMayMeetAfterIntIvl = interval(null, 100] meets after interval[0, 50]
define UnknownBegNotMeetsIntIvl = interval(null, 5] meets after interval[0, 100]
define IntIvlMayMeetBeforeUnknownBeg = interval[0, 100] meets after interval(null, 400)
define PosInfEndMeetsAfterIntIvl = interval[100, null] meets after interval[0, 99]
define PosInfEndNotMeetsIntIvl = interval[0, null] meets after interval[1000, 2000]
define IntIvlNotMeetsPosInfEnd = interval[100, 200] meets after interval[0, null]
define UnknownEndMeetsAfterIntIvl = interval[100, null) meets after interval[0, 99]
define UnknownEndMayMeetBeforeIntIvl = interval[0, null) meets after interval[50, 100]
define UnknownEndNotMeetsIntIvl = interval[0, null) meets after interval[0, 100]
define IntIvlMayMeetAfterUnknownEnd = interval[0, 100] meets after interval[-100, null)
define NegInfBegMeetsBeforeDateIvl = interval[null, DateTime(2013, 1, 1, 0, 0, 0, 0)) meets after interval[DateTime(2013, 1, 1, 0, 0, 0, 0), DateTime(2030, 1, 1, 0, 0, 0, 0)]
define NegInfBegNotMeetsDateIvl = interval[null, DateTime(2013, 1, 1, 0, 0, 0, 0)) meets after interval[DateTime(2000, 1, 1, 0, 0, 0, 0), DateTime(2010, 1, 1, 0, 0, 0, 0)]
define DateIvlNotMeetsNegInfBeg = interval[DateTime(2000, 1, 1, 0, 0, 0, 0), DateTime(2010, 1, 1, 0, 0, 0, 0)] meets after interval[null, DateTime(2013, 1, 1, 0, 0, 0, 0))
define UnknownBegMeetsBeforeDateIvl = interval(null, DateTime(2013, 1, 1, 0, 0, 0, 0)) meets after interval[DateTime(2013, 1, 1, 0, 0, 0, 0), DateTime(2030, 1, 1, 0, 0, 0, 0)]
define UnknownBegMayMeetAfterDateIvl = interval(null, DateTime(2013, 1, 1, 0, 0, 0, 0)) meets after interval[DateTime(2000, 1, 1, 0, 0, 0, 0), DateTime(2010, 1, 1, 0, 0, 0, 0)]
define UnknownBegNotMeetsDateIvl = interval(null, DateTime(2013, 1, 1, 0, 0, 0, 0)) meets after interval[DateTime(2000, 1, 1, 0, 0, 0, 0), DateTime(2020, 1, 1, 0, 0, 0, 0)]
define DateIvlMayMeetBeforeUnknownBeg = interval[DateTime(2000, 1, 1, 0, 0, 0, 0), DateTime(2010, 1, 1, 0, 0, 0, 0)] meets after interval(null, DateTime(2013, 1, 1, 0, 0, 0, 0))
define PosInfEndMeetsAfterDateIvl = interval[DateTime(2013, 1, 1, 0, 0, 0, 0), null] meets after interval[DateTime(2000, 1, 1, 0, 0, 0, 0), DateTime(2013, 1, 1, 0, 0, 0, 0))
define PosInfEndNotMeetsDateIvl = interval[DateTime(2013, 1, 1, 0, 0, 0, 0), null] meets after interval[DateTime(2000, 1, 1, 0, 0, 0, 0), DateTime(2020, 1, 1, 0, 0, 0, 0)]
define DateIvlNotMeetsPosInfEnd = interval[DateTime(2000, 1, 1, 0, 0, 0, 0), DateTime(2020, 1, 1, 0, 0, 0, 0)] meets after interval[DateTime(2013, 1, 1, 0, 0, 0, 0), null]
define UnknownEndMeetsAfterDateIvl = interval[DateTime(2013, 1, 1, 0, 0, 0, 0), null) meets after interval[DateTime(2010, 1, 1, 0, 0, 0, 0), DateTime(2013, 1, 1, 0, 0, 0, 0))
define UnknownEndMayMeetBeforeDateIvl = interval[DateTime(2013, 1, 1, 0, 0, 0, 0), null) meets after interval[DateTime(2020, 1, 1, 0, 0, 0, 0), DateTime(2040, 1, 1, 0, 0, 0, 0)]
define UnknownEndNotMeetsDateIvl = interval[DateTime(2013, 1, 1, 0, 0, 0, 0), null) meets after interval[DateTime(2000, 1, 1, 0, 0, 0, 0), DateTime(2013, 1, 1, 0, 0, 0, 0)]
define DateIvlMayMeetAfterUnknownEnd = interval[DateTime(2020, 1, 1, 0, 0, 0, 0), DateTime(2040, 1, 1, 0, 0, 0, 0)] meets after interval[DateTime(2013, 1, 1, 0, 0, 0, 0), null)

// @Test: MeetsBefore
define MeetsAfterIntIvl = interval[11, 15] meets before interval[5, 10]
define MeetsBeforeIntIvl = interval[1, 4] meets before interval[5, 10]
define NotMeetsIntIvl = interval[1, 2] meets before interval[5, 10]
define MeetsAfterRealIvl = interval[1.50000001, 2.5] meets before interval[0.5, 1.5]
define MeetsBeforeRealIvl = interval[0.5, 1.5] meets before interval[1.50000001, 2.5]
define NotMeetsRealIvl = interval[0.0, 1.0] meets before interval[1.1, 2.0]
define DateIvl = interval[DateTime(2012, 3, 1, 0, 0, 0, 0), DateTime(2012, 9, 1, 0, 0, 0, 0))
define MeetsAfterDateIvl = interval[DateTime(2012, 9, 1, 0, 0, 0, 0), DateTime(2012, 12, 1, 0, 0, 0, 0)) meets before DateIvl
define MeetsBeforeDateIvl = interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2012, 3, 1, 0, 0, 0, 0)) meets before DateIvl
define NotMeetsDateIvl = interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2012, 2, 29, 0, 0, 0, 0)) meets before DateIvl
define MayMeetAfterImpreciseDateIvl = DateIvl meets before interval[DateTime(2012, 1), DateTime(2012, 2)]
define MayMeetBeforeImpreciseDateIvl = DateIvl meets before interval[DateTime(2012, 9), DateTime(2012, 12)]
define NotMeetsImpreciseDateIvl = DateIvl meets before interval[DateTime(2012, 1), DateTime(2012, 12)]
define ImpDateIvl = interval[DateTime(2012, 3), DateTime(2012, 8)]
define ImpreciseMayMeetAfterDateIvl = ImpDateIvl meets before interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2012, 2, 29, 23, 59, 59, 999)]
define ImpreciseMayMeetBeforeDateIvl = ImpDateIvl meets before interval[DateTime(2012, 9, 1, 0, 0, 0, 0), DateTime(2012, 12, 31, 23, 59, 59, 999)]
define ImpreciseNotMeetsDateIvl = ImpDateIvl meets before interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2012, 12, 31, 23, 59, 59, 999))
define NegInfBegMeetsBeforeIntIvl = interval[null, 100] meets before interval[101, 200]
define NegInfBegNotMeetsIntIvl = interval[null, 100] meets before interval[100, 200]
define IntIvlNotMeetsNegInfBeg = interval[100, 200] meets before interval[null, 400]
define UnknownBegMeetsBeforeIntIvl = interval(null, 100] meets before interval[101, 200]
define UnknownBegMayMeetAfterIntIvl = interval(null, 100] meets before interval[0, 50]
define UnknownBegNotMeetsIntIvl = interval(null, 5] meets before interval[0, 100]
define IntIvlMayMeetBeforeUnknownBeg = interval[0, 100] meets before interval(null, 400)
define PosInfEndMeetsAfterIntIvl = interval[100, null] meets before interval[0, 99]
define PosInfEndNotMeetsIntIvl = interval[0, null] meets before interval[1000, 2000]
define IntIvlNotMeetsPosInfEnd = interval[100, 200] meets before interval[0, null]
define UnknownEndMeetsAfterIntIvl = interval[100, null) meets before interval[0, 99]
define UnknownEndMayMeetBeforeIntIvl = interval[0, null) meets before interval[50, 100]
define UnknownEndNotMeetsIntIvl = interval[0, null) meets before interval[0, 100]
define IntIvlMayMeetAfterUnknownEnd = interval[0, 100] meets before interval[-100, null)
define NegInfBegMeetsBeforeDateIvl = interval[null, DateTime(2013, 1, 1, 0, 0, 0, 0)) meets before interval[DateTime(2013, 1, 1, 0, 0, 0, 0), DateTime(2030, 1, 1, 0, 0, 0, 0)]
define NegInfBegNotMeetsDateIvl = interval[null, DateTime(2013, 1, 1, 0, 0, 0, 0)) meets before interval[DateTime(2000, 1, 1, 0, 0, 0, 0), DateTime(2010, 1, 1, 0, 0, 0, 0)]
define DateIvlNotMeetsNegInfBeg = interval[DateTime(2000, 1, 1, 0, 0, 0, 0), DateTime(2010, 1, 1, 0, 0, 0, 0)] meets before interval[null, DateTime(2013, 1, 1, 0, 0, 0, 0))
define UnknownBegMeetsBeforeDateIvl = interval(null, DateTime(2013, 1, 1, 0, 0, 0, 0)) meets before interval[DateTime(2013, 1, 1, 0, 0, 0, 0), DateTime(2030, 1, 1, 0, 0, 0, 0)]
define UnknownBegMayMeetAfterDateIvl = interval(null, DateTime(2013, 1, 1, 0, 0, 0, 0)) meets before interval[DateTime(2000, 1, 1, 0, 0, 0, 0), DateTime(2010, 1, 1, 0, 0, 0, 0)]
define UnknownBegNotMeetsDateIvl = interval(null, DateTime(2013, 1, 1, 0, 0, 0, 0)) meets before interval[DateTime(2000, 1, 1, 0, 0, 0, 0), DateTime(2020, 1, 1, 0, 0, 0, 0)]
define DateIvlMayMeetBeforeUnknownBeg = interval[DateTime(2000, 1, 1, 0, 0, 0, 0), DateTime(2010, 1, 1, 0, 0, 0, 0)] meets before interval(null, DateTime(2013, 1, 1, 0, 0, 0, 0))
define PosInfEndMeetsAfterDateIvl = interval[DateTime(2013, 1, 1, 0, 0, 0, 0), null] meets before interval[DateTime(2000, 1, 1, 0, 0, 0, 0), DateTime(2013, 1, 1, 0, 0, 0, 0))
define PosInfEndNotMeetsDateIvl = interval[DateTime(2013, 1, 1, 0, 0, 0, 0), null] meets before interval[DateTime(2000, 1, 1, 0, 0, 0, 0), DateTime(2020, 1, 1, 0, 0, 0, 0)]
define DateIvlNotMeetsPosInfEnd = interval[DateTime(2000, 1, 1, 0, 0, 0, 0), DateTime(2020, 1, 1, 0, 0, 0, 0)] meets before interval[DateTime(2013, 1, 1, 0, 0, 0, 0), null]
define UnknownEndMeetsAfterDateIvl = interval[DateTime(2013, 1, 1, 0, 0, 0, 0), null) meets before interval[DateTime(2010, 1, 1, 0, 0, 0, 0), DateTime(2013, 1, 1, 0, 0, 0, 0))
define UnknownEndMayMeetBeforeDateIvl = interval[DateTime(2013, 1, 1, 0, 0, 0, 0), null) meets before interval[DateTime(2020, 1, 1, 0, 0, 0, 0), DateTime(2040, 1, 1, 0, 0, 0, 0)]
define UnknownEndNotMeetsDateIvl = interval[DateTime(2013, 1, 1, 0, 0, 0, 0), null) meets before interval[DateTime(2000, 1, 1, 0, 0, 0, 0), DateTime(2013, 1, 1, 0, 0, 0, 0)]
define DateIvlMayMeetAfterUnknownEnd = interval[DateTime(2020, 1, 1, 0, 0, 0, 0), DateTime(2040, 1, 1, 0, 0, 0, 0)] meets before interval[DateTime(2013, 1, 1, 0, 0, 0, 0), null)

// @Test: Overlaps
define OverlapsBeforeIntIvl = interval[1, 5] overlaps interval[2, 7]
define OverlapsAfterIntIvl = interval[3, 8] overlaps interval[1, 6]
define OverlapsBoundaryIntIvl = interval[1, 5] overlaps interval[5, 10]
define NoOverlapsIntIvl = interval[1,5) overlaps interval[5, 10]
define StartOverlapsInt = interval[1, 5] overlaps 1
define EndOverlapsInt = interval[1, 5] overlaps 5
define NoOverlapsInt = interval[1, 5) overlaps 5
define OverlapsBeforeRealIvl = interval[1.234, 1.567] overlaps interval[1.345, 1.678]
define OverlapsAfterRealIvl = interval[1.345, 1.678] overlaps interval[1.234, 1.567]
define OverlapsBoundaryRealIvl = interval[1.0, 1.234] overlaps interval[1.234, 2.0]
define NoOverlapsRealIvl = interval[1.0, 1.23456789) overlaps interval[1.23456789, 2.0]
define StartOverlapsReal = interval[1.234, 5.678] overlaps 1.234
define EndOverlapsReal = interval[1.234, 5.678] overlaps 5.678
define NoOverlapsReal = interval[1.234, 5.678) overlaps 5.678

// @Test: OverlapsDateTime
define ivlA = interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2012, 6, 1, 0, 0, 0, 0))
define ivlB = interval[DateTime(2012, 3, 1, 0, 0, 0, 0), DateTime(2012, 9, 1, 0, 0, 0, 0))
define ivlC = interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2013, 1, 1, 0, 0, 0, 0))
define ivlD = interval[DateTime(2013, 1, 1, 0, 0, 0, 0), DateTime(2014, 1, 1, 0, 0, 0, 0))
define ivlE = interval[DateTime(2013), DateTime(2015)]
define ivlF = interval[DateTime(2014), DateTime(2016)]
define ivlG = interval[DateTime(2016), DateTime(2017)]
define OverlapsBefore = ivlA overlaps ivlB
define OverlapsAfter = ivlB overlaps ivlA
define OverlapsContained = ivlB overlaps ivlC
define OverlapsContains = ivlC overlaps ivlB
define ImpreciseOverlap = ivlD overlaps ivlE
define NoOverlap = ivlC overlaps ivlD
define NoImpreciseOverlap = ivlE overlaps ivlG
define UnknownOverlap = ivlF overlaps ivlG
define OverlapsDate = ivlC overlaps DateTime(2012, 4, 1, 0, 0, 0, 0)
define StartOverlapsDate = ivlC overlaps DateTime(2012, 1, 1, 0, 0, 0, 0)
define EndOverlapsDate = ivlC overlaps DateTime(2012, 12, 31, 23, 59, 59, 999)
define NoOverlapsDate = ivlC overlaps DateTime(2013, 4, 1, 0, 0, 0, 0)
define UnknownOverlapsDate = ivlE overlaps DateTime(2013, 4, 1, 0, 0, 0, 0)
define OverlapsUnknownDate = ivlB overlaps DateTime(2012)

// @Test: OverlapsAfter
define OverlapsBeforeIntIvl = interval[1, 5] overlaps after interval[2, 7]
define OverlapsAfterIntIvl = interval[3, 8] overlaps after interval[1, 6]
define OverlapsBoundaryIntIvl = interval[5, 10] overlaps after interval[1, 5]
define NoOverlapsIntIvl = interval[1,5) overlaps after interval[5, 10]
define StartOverlapsInt = interval[1, 5] overlaps after 1
define EndOverlapsInt = interval[1, 5] overlaps after 5
define NoOverlapsInt = interval[1, 5) overlaps after 5
define OverlapsBeforeRealIvl = interval[1.234, 1.567] overlaps after interval[1.345, 1.678]
define OverlapsAfterRealIvl = interval[1.345, 1.678] overlaps after interval[1.234, 1.567]
define OverlapsBoundaryRealIvl = interval[1.234, 2.0] overlaps after interval[1.0, 1.234]
define NoOverlapsRealIvl = interval[1.0, 1.23456789) overlaps after interval[1.23456789, 2.0]
define StartOverlapsReal = interval[1.234, 5.678] overlaps after 1.234
define EndOverlapsReal = interval[1.234, 5.678] overlaps after 5.678
define NoOverlapsReal = interval[1.234, 5.678) overlaps after 5.678

// @Test: OverlapsAfterDateTime
define ivlA = interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2012, 6, 1, 0, 0, 0, 0))
define ivlB = interval[DateTime(2012, 3, 1, 0, 0, 0, 0), DateTime(2012, 9, 1, 0, 0, 0, 0))
define ivlC = interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2013, 1, 1, 0, 0, 0, 0))
define ivlD = interval[DateTime(2013, 1, 1, 0, 0, 0, 0), DateTime(2014, 1, 1, 0, 0, 0, 0))
define ivlE = interval[DateTime(2013), DateTime(2015)]
define ivlF = interval[DateTime(2014), DateTime(2016)]
define ivlG = interval[DateTime(2016), DateTime(2017)]
define OverlapsBefore = ivlA overlaps after ivlB
define OverlapsAfter = ivlB overlaps after ivlA
define OverlapsContained = ivlB overlaps after ivlC
define OverlapsContains = ivlC overlaps after ivlB
define ImpreciseOverlapBefore = ivlE overlaps after ivlF
define ImpreciseOverlapAfter = ivlF overlaps after ivlE
define NoOverlap = ivlC overlaps after ivlD
define NoImpreciseOverlap = ivlE overlaps after ivlG
define UnknownOverlap = ivlG overlaps after ivlF
define OverlapsDate = ivlC overlaps after DateTime(2012, 4, 1, 0, 0, 0, 0)
define StartOverlapsDate = ivlC overlaps after DateTime(2012, 1, 1, 0, 0, 0, 0)
define EndOverlapsDate = ivlC overlaps after DateTime(2012, 12, 31, 23, 59, 59, 999)
define NoOverlapsDate = ivlC overlaps after DateTime(2013, 4, 1, 0, 0, 0, 0)
define UnknownOverlapsDate = ivlE overlaps after DateTime(2013, 4, 1, 0, 0, 0, 0)
define OverlapsUnknownDate = ivlB overlaps after DateTime(2012)

// @Test: OverlapsBefore
define OverlapsBeforeIntIvl = interval[1, 5] overlaps before interval[2, 7]
define OverlapsAfterIntIvl = interval[3, 8] overlaps before interval[1, 6]
define OverlapsBoundaryIntIvl = interval[1, 5] overlaps before interval[5, 10]
define NoOverlapsIntIvl = interval[1,5) overlaps before interval[5, 10]
define StartOverlapsInt = interval[1, 5] overlaps before 1
define EndOverlapsInt = interval[1, 5] overlaps before 5
define NoOverlapsInt = interval[1, 5) overlaps before 5
define OverlapsBeforeRealIvl = interval[1.234, 1.567] overlaps before interval[1.345, 1.678]
define OverlapsAfterRealIvl = interval[1.345, 1.678] overlaps before interval[1.234, 1.567]
define OverlapsBoundaryRealIvl = interval[1.0, 1.234] overlaps before interval[1.234, 2.0]
define NoOverlapsRealIvl = interval[1.0, 1.23456789) overlaps before interval[1.23456789, 2.0]
define StartOverlapsReal = interval[1.234, 5.678] overlaps before 1.234
define EndOverlapsReal = interval[1.234, 5.678] overlaps before 5.678
define NoOverlapsReal = interval[1.234, 5.678) overlaps before 5.678

// @Test: OverlapsBeforeDateTime
define ivlA = interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2012, 6, 1, 0, 0, 0, 0))
define ivlB = interval[DateTime(2012, 3, 1, 0, 0, 0, 0), DateTime(2012, 9, 1, 0, 0, 0, 0))
define ivlC = interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2013, 1, 1, 0, 0, 0, 0))
define ivlD = interval[DateTime(2013, 1, 1, 0, 0, 0, 0), DateTime(2014, 1, 1, 0, 0, 0, 0))
define ivlE = interval[DateTime(2013), DateTime(2015)]
define ivlF = interval[DateTime(2014), DateTime(2016)]
define ivlG = interval[DateTime(2016), DateTime(2017)]
define OverlapsBefore = ivlA overlaps before ivlB
define OverlapsAfter = ivlB overlaps before ivlA
define OverlapsContained = ivlB overlaps before ivlC
define OverlapsContains = ivlC overlaps before ivlB
define ImpreciseOverlapBefore = ivlE overlaps before ivlF
define ImpreciseOverlapAfter = ivlF overlaps before ivlE
define NoOverlap = ivlC overlaps before ivlD
define NoImpreciseOverlap = ivlE overlaps before ivlG
define UnknownOverlap = ivlF overlaps before ivlG
define OverlapsDate = ivlC overlaps before DateTime(2012, 4, 1, 0, 0, 0, 0)
define StartOverlapsDate = ivlC overlaps before DateTime(2012, 1, 1, 0, 0, 0, 0)
define EndOverlapsDate = ivlC overlaps before DateTime(2012, 12, 31, 23, 59, 59, 999)
define NoOverlapsDate = ivlC overlaps before DateTime(2013, 4, 1, 0, 0, 0, 0)
define UnknownOverlapsDate = ivlE overlaps before DateTime(2013, 4, 1, 0, 0, 0, 0)
define OverlapsUnknownDate = ivlB overlaps before DateTime(2012)

// @Test: Width
define IntWidth = width of interval[-2, 5]
define IntOpenWidth = width of interval(-2, 5)
define IntWidthThreeToMax = width of interval[3, null]
define IntWidthMinToThree = width of interval[null, 3]
define IntWidthThreeToUnknown = width of interval[3, null)
define IntWidthUnknownToThree = width of interval(null, 3]
define RealWidth = width of interval[1.23, 4.56]
define RealOpenWidth = width of interval(1.23, 4.56)
define DateTimeWidth = width of interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2012, 1, 9, 0, 0, 0, 0)]
define DateTimeOpenWidth = width of interval(DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2012, 1, 9, 0, 0, 0, 0))

// @Test: Start
define Foo = start of interval[DateTime(2012, 1, 1), DateTime(2013, 1, 1)]

// @Test: IntegerIntervalUnion
define IntFullInterval = interval[0,10]
define IntClosedUnionClosed = interval[0,5] union interval[5,10]
define IntClosedUnionOpen = interval[0,5] union interval[5,10)
define IntOpenUnionOpen = interval(0,5] union interval[5,10)
define IntOpenUnionClosed = interval(0,5] union interval[5,10]
define IntSameAsUnion = interval[0,10] union interval[0,10]
define IntBeforeUnion = interval[0,4] union interval[6,10]
define IntMeetsUnion = interval[0,5] union interval[5,10]
define IntOverlapsUnion = interval[0,7] union interval[3,10]
define IntBeginsUnion = interval[0,5] union interval[0,10]
define IntDuringUnion = interval[3,5] union interval[0,10]
define IntEndsUnion = interval[5,10] union interval[0,10]

// @Test: DateTimeIntervalUnion
define DateTimeFullInterval = interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2013, 1, 1, 0, 0, 0, 0)]
define DateTimeClosedUnionClosed = interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2012, 6, 1, 0, 0, 0, 0)] union interval[DateTime(2012, 6, 1, 0, 0, 0, 0), DateTime(2013, 1, 1, 0, 0, 0, 0)]
define DateTimeClosedUnionOpen = interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2012, 6, 1, 0, 0, 0, 0)] union interval[DateTime(2012, 6, 1, 0, 0, 0, 0), DateTime(2013, 1, 1, 0, 0, 0, 0))
define DateTimeOpenUnionOpen = interval(DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2012, 6, 1, 0, 0, 0, 0)] union interval[DateTime(2012, 6, 1, 0, 0, 0, 0), DateTime(2013, 1, 1, 0, 0, 0, 0))
define DateTimeOpenUnionClosed = interval(DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2012, 6, 1, 0, 0, 0, 0)] union interval[DateTime(2012, 6, 1, 0, 0, 0, 0), DateTime(2013, 1, 1, 0, 0, 0, 0)]
define DateTimeSameAsUnion = interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2013, 1, 1, 0, 0, 0, 0)] union interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2013, 1, 1, 0, 0, 0, 0)]
define DateTimeBeforeUnion = interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2012, 4, 1, 0, 0, 0, 0)] union interval[DateTime(2012, 7, 1, 0, 0, 0, 0), DateTime(2013, 1, 1, 0, 0, 0, 0)]
define DateTimeMeetsUnion = interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2012, 6, 1, 0, 0, 0, 0)] union interval[DateTime(2012, 6, 1, 0, 0, 0, 0), DateTime(2013, 1, 1, 0, 0, 0, 0)]
define DateTimeOverlapsUnion = interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2012, 7, 1, 0, 0, 0, 0)] union interval[DateTime(2012, 3, 1, 0, 0, 0, 0), DateTime(2013, 1, 1, 0, 0, 0, 0)]
define DateTimeBeginsUnion = interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2012, 6, 1, 0, 0, 0, 0)] union interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2013, 1, 1, 0, 0, 0, 0)]
define DateTimeDuringUnion = interval[DateTime(2012, 3, 1, 0, 0, 0, 0), DateTime(2012, 5, 1, 0, 0, 0, 0)] union interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2013, 1, 1, 0, 0, 0, 0)]
define DateTimeEndsUnion = interval[DateTime(2012, 6, 1, 0, 0, 0, 0), DateTime(2013, 1, 1, 0, 0, 0, 0)] union interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2013, 1, 1, 0, 0, 0, 0)]

// @Test: IntegerIntervalExcept
define IntFullInterval = interval[0,10]
define IntHalfInterval = interval[0,5)
define IntSameAsExcept = interval[0,10] except interval[0,10]
define IntBeforeExcept = interval[0,4] except interval[6,10]
define IntMeetsExcept = interval[0,5] except interval[5,10]
define IntOverlapsExcept = interval[0,7] except interval[5,10]
define IntBeginsExcept = interval[0,5] except interval[0,10]
define IntDuringExcept = interval[3,5] except interval[0,10]
define IntEndsExcept = interval[5,10] except interval[0,10]

// @Test: DateTimeIntervalExcept
define DateTimeFullInterval = interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2013, 1, 1, 0, 0, 0, 0)]
define DateTimeHalfInterval = interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2012, 6, 1, 0, 0, 0, 0))
define DateTimeSameAsExcept = interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2013, 1, 1, 0, 0, 0, 0)] except interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2013, 1, 1, 0, 0, 0, 0)]
define DateTimeBeforeExcept = interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2012, 4, 1, 0, 0, 0, 0)] except interval[DateTime(2012, 7, 1, 0, 0, 0, 0), DateTime(2013, 1, 1, 0, 0, 0, 0)]
define DateTimeMeetsExcept = interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2012, 6, 1, 0, 0, 0, 0)] except interval[DateTime(2012, 6, 1, 0, 0, 0, 0), DateTime(2013, 1, 1, 0, 0, 0, 0)]
define DateTimeOverlapsExcept = interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2012, 7, 1, 0, 0, 0, 0)] except interval[DateTime(2012, 6, 1, 0, 0, 0, 0), DateTime(2013, 1, 1, 0, 0, 0, 0)]
define DateTimeBeginsExcept = interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2012, 6, 1, 0, 0, 0, 0)] except interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2013, 1, 1, 0, 0, 0, 0)]
define DateTimeDuringExcept = interval[DateTime(2012, 3, 1, 0, 0, 0, 0), DateTime(2012, 5, 1, 0, 0, 0, 0)] except interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2013, 1, 1, 0, 0, 0, 0)]
define DateTimeEndsExcept = interval[DateTime(2012, 6, 1, 0, 0, 0, 0), DateTime(2013, 1, 1, 0, 0, 0, 0)] except interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2013, 1, 1, 0, 0, 0, 0)]
