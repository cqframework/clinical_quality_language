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

// @Test: Start
define Foo = start of interval[DateTime(2012, 1, 1), DateTime(2013, 1, 1)]
