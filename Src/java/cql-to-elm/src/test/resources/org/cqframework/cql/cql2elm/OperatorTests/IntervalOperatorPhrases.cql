define A: Interval[DateTime(2014, 1, 1, 0, 0, 0, 0), DateTime(2015, 1, 1, 0, 0, 0, 0))
define B: Interval[DateTime(2014, 6, 1, 0, 0, 0, 0), DateTime(2014, 6, 2, 0, 0, 0, 0))
define PointA: DateTime(2014, 1, 1, 0, 0, 0, 0)
define PointB: DateTime(2014, 6, 1, 0, 0, 0, 0)

// Concurrent interval operator phrase
// ('starts' | 'ends' | 'occurs')? 'same' dateTimePrecision? (relativeQualifier | 'as') ('start' | 'end')?
define IntervalSameAs: A same as B
define IntervalSameOrAfter: A same or after B
define IntervalSameOrBefore: A same or before B
define PointSameAs: PointA same as PointB
define PointSameOrAfter: PointA same or after PointB
define PointSameOrBefore: PointA same or before PointB
define IntervalSameAsWithPrecision: A same day as B
define IntervalSameOrAfterWithPrecision: A same day or after B
define IntervalSameOrBeforeWithPrecision: A same day or before B
define PointSameAsWithPrecision: PointA same day as PointB
define PointSameOrAfterWithPrecision: PointA same day or after PointB
define PointSameOrBeforeWithPrecision: PointA same day or before PointB
define IntervalStartsSameAsStart: A starts same as start B
define IntervalStartsSameOrAfterStart: A starts same or after start B
define IntervalStartsSameOrBeforeStart: A starts same or before start B
define IntervalStartsSameAsStartWithPrecision: A starts same day as start B
define IntervalStartsSameOrAfterStartWithPrecision: A starts same day or after start B
define IntervalStartsSameOrBeforeStartWithPrecision: A starts same day or before start B
define IntervalStartsSameAsEnd: A starts same as end B
define IntervalStartsSameOrAfterEnd: A starts same or after end B
define IntervalStartsSameOrBeforeEnd: A starts same or before end B
define IntervalStartsSameAsEndWithPrecision: A starts same day as end B
define IntervalStartsSameOrAfterEndWithPrecision: A starts same day or after end B
define IntervalStartsSameOrBeforeEndWithPrecision: A starts same day or before end B
define IntervalStartsSameAsPoint: A starts same as PointB
define IntervalStartsSameOrAfterPoint: A starts same or after PointB
define IntervalStartsSameOrBeforePoint: A starts same or before PointB
define IntervalStartsSameAsPointWithPrecision: A starts same day as PointB
define IntervalStartsSameOrAfterPointWithPrecision: A starts same day or after PointB
define IntervalStartsSameOrBeforePointWithPrecision: A starts same day or before PointB

define IntervalEndsSameAsStart: A ends same as start B
define IntervalEndsSameOrAfterStart: A ends same or after start B
define IntervalEndsSameOrBeforeStart: A ends same or before start B
define IntervalEndsSameAsStartWithPrecision: A ends same day as start B
define IntervalEndsSameOrAfterStartWithPrecision: A ends same day or after start B
define IntervalEndsSameOrBeforeStartWithPrecision: A ends same day or before start B
define IntervalEndsSameAsEnd: A ends same as end B
define IntervalEndsSameOrAfterEnd: A ends same or after end B
define IntervalEndsSameOrBeforeEnd: A ends same or before end B
define IntervalEndsSameAsEndWithPrecision: A ends same day as end B
define IntervalEndsSameOrAfterEndWithPrecision: A ends same day or after end B
define IntervalEndsSameOrBeforeEndWithPrecision: A ends same day or before end B
define IntervalEndsSameAsPoint: A ends same as PointB
define IntervalEndsSameOrAfterPoint: A ends same or after PointB
define IntervalEndsSameOrBeforePoint: A ends same or before PointB
define IntervalEndsSameAsPointWithPrecision: A ends same day as PointB
define IntervalEndsSameOrAfterPointWithPrecision: A ends same day or after PointB
define IntervalEndsSameOrBeforePointWithPrecision: A ends same day or before PointB

define PointOccursSameAsStart: PointA occurs same as start B
define PointOccursSameOrAfterStart: PointA occurs same or after start B
define PointOccursSameOrBeforeStart: PointA occurs same or before start B
define PointOccursSameAsStartWithPrecision: PointA occurs same day as start B
define PointOccursSameOrAfterStartWithPrecision: PointA occurs same day or after start B
define PointOccursSameOrBeforeStartWithPrecision: PointA occurs same day or before start B
define PointOccursSameAsEnd: PointA occurs same as end B
define PointOccursSameOrAfterEnd: PointA occurs same or after end B
define PointOccursSameOrBeforeEnd: PointA occurs same or before end B
define PointOccursSameAsEndWithPrecision: PointA occurs same day as end B
define PointOccursSameOrAfterEndWithPrecision: PointA occurs same day or after end B
define PointOccursSameOrBeforeEndWithPrecision: PointA occurs same day or before end B
define PointOccursSameAsPoint: PointA occurs same as PointB
define PointOccursSameOrAfterPoint: PointA occurs same or after PointB
define PointOccursSameOrBeforePoint: PointA occurs same or before PointB
define PointOccursSameAsPointWithPrecision: PointA occurs same day as PointB
define PointOccursSameOrAfterPointWithPrecision: PointA occurs same day or after PointB
define PointOccursSameOrBeforePointWithPrecision: PointA occurs same day or before PointB

// Includes interval operator phrases
// 'properly'? 'includes' dateTimePrecisionSpecifier? ('start' | 'end')?
define IntervalIncludes: A includes B
define IntervalProperlyIncludes: A properly includes B
define IntervalIncludesWithPrecision: A includes day of B
define IntervalProperlyIncludesWithPrecision: A properly includes day of B
define IntervalIncludesPoint: A includes PointB
define IntervalProperlyIncludesPoint: A properly includes PointB
define IntervalIncludesPointWithPrecision: A properly includes day of PointB
define IntervalProperlyIncludesPointWithPrecision: A properly includes day of PointB
define IntervalIncludesStart: A includes start B
define IntervalProperlyIncludesStart: A properly includes start B
define IntervalIncludesStartWithPrecision: A includes day of start B
define IntervalProperlyIncludesStartWithPrecision: A properly includes day of start B
define IntervalIncludesEnd: A includes end B
define IntervalProperlyIncludesEnd: A properly includes end B
define IntervalIncludesEndWithPrecision: A includes day of end B
define IntervalProperlyIncludesEndWithPrecision: A properly includes day of end B

// IncludedIn interval operator phrase
// ('starts' | 'ends' | 'occurs')? 'properly'? ('during' | 'included in') dateTimePrecisionSpecifier?
define IntervalIncludedIn: A included in B
define IntervalProperlyIncludedIn: A properly included in B
define IntervalIncludedInWithPrecision: A included in day of B
define IntervalProperlyIncludedInWithPrecision: A properly included in day of B
define PointIncludedIn: PointA included in B
define PointProperlyIncludedIn: PointA properly included in B
define PointIncludedInWithPrecision: PointA included in day of B
define PointProperlyIncludedInWithPrecision: PointA properly included in day of B
define PointOccursIncludedIn: PointA occurs included in B
define PointOccursProperlyIncludedIn: PointA occurs properly included in B
define PointOccursIncludedInWithPrecision: PointA occurs included in day of B
define PointOccursProperlyIncludedInWithPrecision: PointA occurs properly included in day of B
define IntervalStartsIncludedIn: A starts included in B
define IntervalStartsProperlyIncludedIn: A starts properly included in B
define IntervalStartsIncludedInWithPrecision: A starts included in day of B
define IntervalStartsProperlyIncludedInWithPrecision: A starts properly included in day of B
define IntervalEndsIncludedIn: A ends included in B
define IntervalEndsProperlyIncludedIn: A ends properly included in B
define IntervalEndsIncludedInWithPrecision: A ends included in day of B
define IntervalEndsProperlyIncludedInWithPrecision: A ends properly included in day of B

define IntervalDuring: A during B
define IntervalProperlyDuring: A properly during B
define IntervalDuringWithPrecision: A during day of B
define IntervalProperlyDuringWithPrecision: A properly during day of B
define PointDuring: PointA during B
define PointProperlyDuring: PointA properly during B
define PointDuringWithPrecision: PointA during day of B
define PointProperlyDuringWithPrecision: PointA properly during day of B
define PointOccursDuring: PointA occurs during B
define PointOccursProperlyDuring: PointA occurs properly during B
define PointOccursDuringWithPrecision: PointA occurs during day of B
define PointOccursProperlyDuringWithPrecision: PointA occurs properly during day of B
define IntervalStartsDuring: A starts during B
define IntervalStartsProperlyDuring: A starts properly during B
define IntervalStartsDuringWithPrecision: A starts during day of B
define IntervalStartsProperlyDuringWithPrecision: A starts properly during day of B
define IntervalEndsDuring: A ends during B
define IntervalEndsProperlyDuring: A ends properly during B
define IntervalEndsDuringWithPrecision: A ends during day of B
define IntervalEndsProperlyDuringWithPrecision: A ends properly during day of B

// Before or After interval operator phrase
// ('starts' | 'ends' | 'occurs')? quantityOffset? ('before' | 'after') dateTimePrecisionSpecifier? ('start' | 'end')?
define IntervalBefore: A before B
define IntervalBeforeWithPrecision: A before day of B
define IntervalBeforeWithOffset: A 3 days before B
define IntervalBeforeWithOffsetOrLess: A 3 days or less before B
define IntervalBeforeWithOffsetOrMore: A 3 days or more before B
define IntervalBeforeWithOffsetWithPrecision: A 3 days before day of B
define IntervalBeforeWithOffsetOrLessWithPrecision: A 3 days or less before day of B
define IntervalBeforeWithOffsetOrMoreWithPrecision: A 3 days or more before day of B

define IntervalBeforeUsingUCUM: A 3 'd' before B

define IntervalBeforeStart: A before start B
define IntervalBeforeStartWithPrecision: A before day of start B
define IntervalBeforeStartWithOffset: A 3 days before start B
define IntervalBeforeStartWithOffsetOrLess: A 3 days or less before start B
define IntervalBeforeStartWithOffsetOrMore: A 3 days or more before start B
define IntervalBeforeStartWithOffsetWithPrecision: A 3 days before day of start B
define IntervalBeforeStartWithOffsetOrLessWithPrecision: A 3 days or less before day of start B
define IntervalBeforeStartWithOffsetOrMoreWithPrecision: A 3 days or more before day of start B

define IntervalBeforeEnd: A before end B
define IntervalBeforeEndWithPrecision: A before day of end B
define IntervalBeforeEndWithOffset: A 3 days before end B
define IntervalBeforeEndWithOffsetOrLess: A 3 days or less before end B
define IntervalBeforeEndWithOffsetOrMore: A 3 days or more before end B
define IntervalBeforeEndWithOffsetWithPrecision: A 3 days before day of end B
define IntervalBeforeEndWithOffsetOrLessWithPrecision: A 3 days or less before day of end B
define IntervalBeforeEndWithOffsetOrMoreWithPrecision: A 3 days or more before day of end B

define PointBefore: PointA before PointB
define PointBeforeWithPrecision: PointA before day of PointB
define PointBeforeWithOffset: PointA 3 days before PointB
define PointBeforeWithOffsetOrLess: PointA 3 days or less before PointB
define PointBeforeWithOffsetOrMore: PointA 3 days or more before PointB
define PointBeforeWithOffsetWithPrecision: PointA 3 days before day of PointB
define PointBeforeWithOffsetOrLessWithPrecision: PointA 3 days or less before day of PointB
define PointBeforeWithOffsetOrMoreWithPrecision: PointA 3 days or more before day of PointB

define PointBeforeInterval: PointA before B
define PointBeforeIntervalWithPrecision: PointA before day of B
define PointBeforeIntervalWithOffset: PointA 3 days before B
define PointBeforeIntervalWithOffsetOrLess: PointA 3 days or less before B
define PointBeforeIntervalWithOffsetOrMore: PointA 3 days or more before B
define PointBeforeIntervalWithOffsetWithPrecision: PointA 3 days before day of B
define PointBeforeIntervalWithOffsetOrLessWithPrecision: PointA 3 days or less before day of B
define PointBeforeIntervalWithOffsetOrMoreWithPrecision: PointA 3 days or more before day of B

define PointBeforeStartInterval: PointA before start B
define PointBeforeStartIntervalWithPrecision: PointA before day of start B
define PointBeforeStartIntervalWithOffset: PointA 3 days before start B
define PointBeforeStartIntervalWithOffsetOrLess: PointA 3 days or less before start B
define PointBeforeStartIntervalWithOffsetOrMore: PointA 3 days or more before start B
define PointBeforeStartIntervalWithOffsetWithPrecision: PointA 3 days before day of start B
define PointBeforeStartIntervalWithOffsetOrLessWithPrecision: PointA 3 days or less before day of start B
define PointBeforeStartIntervalWithOffsetOrMoreWithPrecision: PointA 3 days or more before day of start B

define PointBeforeEndInterval: PointA before end B
define PointBeforeEndIntervalWithPrecision: PointA before day of end B
define PointBeforeEndIntervalWithOffset: PointA 3 days before end B
define PointBeforeEndIntervalWithOffsetOrLess: PointA 3 days or less before end B
define PointBeforeEndIntervalWithOffsetOrMore: PointA 3 days or more before end B
define PointBeforeEndIntervalWithOffsetWithPrecision: PointA 3 days before day of end B
define PointBeforeEndIntervalWithOffsetOrLessWithPrecision: PointA 3 days or less before day of end B
define PointBeforeEndIntervalWithOffsetOrMoreWithPrecision: PointA 3 days or more before day of end B

define PointOccursBefore: PointA occurs before PointB
define PointOccursBeforeWithPrecision: PointA occurs before day of PointB
define PointOccursBeforeWithOffset: PointA occurs 3 days before PointB
define PointOccursBeforeWithOffsetOrLess: PointA occurs 3 days or less before PointB
define PointOccursBeforeWithOffsetOrMore: PointA occurs 3 days or more before PointB
define PointOccursBeforeWithOffsetWithPrecision: PointA occurs 3 days before day of PointB
define PointOccursBeforeWithOffsetOrLessWithPrecision: PointA occurs 3 days or less before day of PointB
define PointOccursBeforeWithOffsetOrMoreWithPrecision: PointA occurs 3 days or more before day of PointB

define PointOccursBeforeInterval: PointA occurs before B
define PointOccursBeforeIntervalWithPrecision: PointA occurs before day of B
define PointOccursBeforeIntervalWithOffset: PointA occurs 3 days before B
define PointOccursBeforeIntervalWithOffsetOrLess: PointA occurs 3 days or less before B
define PointOccursBeforeIntervalWithOffsetOrMore: PointA occurs 3 days or more before B
define PointOccursBeforeIntervalWithOffsetWithPrecision: PointA occurs 3 days before day of B
define PointOccursBeforeIntervalWithOffsetOrLessWithPrecision: PointA occurs 3 days or less before day of B
define PointOccursBeforeIntervalWithOffsetOrMoreWithPrecision: PointA occurs 3 days or more before day of B

define PointOccursBeforeStartInterval: PointA occurs before start B
define PointOccursBeforeStartIntervalWithPrecision: PointA occurs before day of start B
define PointOccursBeforeStartIntervalWithOffset: PointA occurs 3 days before start B
define PointOccursBeforeStartIntervalWithOffsetOrLess: PointA occurs 3 days or less before start B
define PointOccursBeforeStartIntervalWithOffsetOrMore: PointA occurs 3 days or more before start B
define PointOccursBeforeStartIntervalWithOffsetWithPrecision: PointA occurs 3 days before day of start B
define PointOccursBeforeStartIntervalWithOffsetOrLessWithPrecision: PointA occurs 3 days or less before day of start B
define PointOccursBeforeStartIntervalWithOffsetOrMoreWithPrecision: PointA occurs 3 days or more before day of start B

define PointOccursBeforeEndInterval: PointA occurs before end B
define PointOccursBeforeEndIntervalWithPrecision: PointA occurs before day of end B
define PointOccursBeforeEndIntervalWithOffset: PointA occurs 3 days before end B
define PointOccursBeforeEndIntervalWithOffsetOrLess: PointA occurs 3 days or less before end B
define PointOccursBeforeEndIntervalWithOffsetOrMore: PointA occurs 3 days or more before end B
define PointOccursBeforeEndIntervalWithOffsetWithPrecision: PointA occurs 3 days before day of end B
define PointOccursBeforeEndIntervalWithOffsetOrLessWithPrecision: PointA occurs 3 days or less before day of end B
define PointOccursBeforeEndIntervalWithOffsetOrMoreWithPrecision: PointA occurs 3 days or more before day of end B

define IntervalBeforePoint: A before PointB
define IntervalBeforePointWithPrecision: A before day of PointB
define IntervalBeforePointWithOffset: A 3 days before PointB
define IntervalBeforePointWithOffsetOrLess: A 3 days or less before PointB
define IntervalBeforePointWithOffsetOrMore: A 3 days or more before PointB
define IntervalBeforePointWithOffsetWithPrecision: A 3 days before day of PointB
define IntervalBeforePointWithOffsetOrLessWithPrecision: A 3 days or less before day of PointB
define IntervalBeforePointWithOffsetOrMoreWithPrecision: A 3 days or more before day of PointB

define IntervalStartsBeforePoint: A starts before PointB
define IntervalStartsBeforePointWithPrecision: A starts before day of PointB
define IntervalStartsBeforePointWithOffset: A starts 3 days before PointB
define IntervalStartsBeforePointWithOffsetOrLess: A starts 3 days or less before PointB
define IntervalStartsBeforePointWithOffsetOrMore: A starts 3 days or more before PointB
define IntervalStartsBeforePointWithOffsetWithPrecision: A starts 3 days before day of PointB
define IntervalStartsBeforePointWithOffsetOrLessWithPrecision: A starts 3 days or less before day of PointB
define IntervalStartsBeforePointWithOffsetOrMoreWithPrecision: A starts 3 days or more before day of PointB

define IntervalEndsBeforePoint: A ends before PointB
define IntervalEndsBeforePointWithPrecision: A ends before day of PointB
define IntervalEndsBeforePointWithOffset: A ends 3 days before PointB
define IntervalEndsBeforePointWithOffsetOrLess: A ends 3 days or less before PointB
define IntervalEndsBeforePointWithOffsetOrMore: A ends 3 days or more before PointB
define IntervalEndsBeforePointWithOffsetWithPrecision: A ends 3 days before day of PointB
define IntervalEndsBeforePointWithOffsetOrLessWithPrecision: A ends 3 days or less before day of PointB
define IntervalEndsBeforePointWithOffsetOrMoreWithPrecision: A ends 3 days or more before day of PointB

define IntervalOccursBeforePoint: A occurs before PointB
define IntervalOccursBeforePointWithPrecision: A occurs before day of PointB
define IntervalOccursBeforePointWithOffset: A occurs 3 days before PointB
define IntervalOccursBeforePointWithOffsetOrLess: A occurs 3 days or less before PointB
define IntervalOccursBeforePointWithOffsetOrMore: A occurs 3 days or more before PointB
define IntervalOccursBeforePointWithOffsetWithPrecision: A occurs 3 days before day of PointB
define IntervalOccursBeforePointWithOffsetOrLessWithPrecision: A occurs 3 days or less before day of PointB
define IntervalOccursBeforePointWithOffsetOrMoreWithPrecision: A occurs 3 days or more before day of PointB

define IntervalStartsBefore: A starts before B
define IntervalStartsBeforeWithPrecision: A starts before day of B
define IntervalStartsBeforeWithOffset: A starts 3 days before B
define IntervalStartsBeforeWithOffsetOrLess: A starts 3 days or less before B
define IntervalStartsBeforeWithOffsetOrMore: A starts 3 days or more before B
define IntervalStartsBeforeWithOffsetWithPrecision: A starts 3 days before day of B
define IntervalStartsBeforeWithOffsetOrLessWithPrecision: A starts 3 days or less before day of B
define IntervalStartsBeforeWithOffsetOrMoreWithPrecision: A starts 3 days or more before day of B

define IntervalStartsBeforeStart: A starts before start B
define IntervalStartsBeforeStartWithPrecision: A starts before day of start B
define IntervalStartsBeforeStartWithOffset: A starts 3 days before start B
define IntervalStartsBeforeStartWithOffsetOrLess: A starts 3 days or less before start B
define IntervalStartsBeforeStartWithOffsetOrMore: A starts 3 days or more before start B
define IntervalStartsBeforeStartWithOffsetWithPrecision: A starts 3 days before day of start B
define IntervalStartsBeforeStartWithOffsetOrLessWithPrecision: A starts 3 days or less before day of start B
define IntervalStartsBeforeStartWithOffsetOrMoreWithPrecision: A starts 3 days or more before day of start B

define IntervalStartsBeforeEnd: A starts before end B
define IntervalStartsBeforeEndWithPrecision: A starts before day of end B
define IntervalStartsBeforeEndWithOffset: A starts 3 days before end B
define IntervalStartsBeforeEndWithOffsetOrLess: A starts 3 days or less before end B
define IntervalStartsBeforeEndWithOffsetOrMore: A starts 3 days or more before end B
define IntervalStartsBeforeEndWithOffsetWithPrecision: A starts 3 days before day of end B
define IntervalStartsBeforeEndWithOffsetOrLessWithPrecision: A starts 3 days or less before day of end B
define IntervalStartsBeforeEndWithOffsetOrMoreWithPrecision: A starts 3 days or more before day of end B

define IntervalEndsBeforeWithPrecision: A ends before day of B
define IntervalEndsBeforeWithOffset: A ends 3 days before B
define IntervalEndsBeforeWithOffsetOrLess: A ends 3 days or less before B
define IntervalEndsBeforeWithOffsetOrMore: A ends 3 days or more before B
define IntervalEndsBeforeWithOffsetWithPrecision: A ends 3 days before day of B
define IntervalEndsBeforeWithOffsetOrLessWithPrecision: A ends 3 days or less before day of B
define IntervalEndsBeforeWithOffsetOrMoreWithPrecision: A ends 3 days or more before day of B

define IntervalEndsBefore: A ends before B
define IntervalEndsBeforeStart: A ends before start B
define IntervalEndsBeforeStartWithPrecision: A ends before day of start B
define IntervalEndsBeforeStartWithOffset: A ends 3 days before start B
define IntervalEndsBeforeStartWithOffsetOrLess: A ends 3 days or less before start B
define IntervalEndsBeforeStartWithOffsetOrMore: A ends 3 days or more before start B
define IntervalEndsBeforeStartWithOffsetWithPrecision: A ends 3 days before day of start B
define IntervalEndsBeforeStartWithOffsetOrLessWithPrecision: A ends 3 days or less before day of start B
define IntervalEndsBeforeStartWithOffsetOrMoreWithPrecision: A ends 3 days or more before day of start B

define IntervalEndsBeforeEnd: A ends before end B
define IntervalEndsBeforeEndWithPrecision: A ends before day of end B
define IntervalEndsBeforeEndWithOffset: A ends 3 days before end B
define IntervalEndsBeforeEndWithOffsetOrLess: A ends 3 days or less before end B
define IntervalEndsBeforeEndWithOffsetOrMore: A ends 3 days or more before end B
define IntervalEndsBeforeEndWithOffsetWithPrecision: A ends 3 days before day of end B
define IntervalEndsBeforeEndWithOffsetOrLessWithPrecision: A ends 3 days or less before day of end B
define IntervalEndsBeforeEndWithOffsetOrMoreWithPrecision: A ends 3 days or more before day of end B

define IntervalOccursBefore: A occurs before B
define IntervalOccursBeforeWithPrecision: A occurs before day of B
define IntervalOccursBeforeWithOffset: A occurs 3 days before B
define IntervalOccursBeforeWithOffsetOrLess: A occurs 3 days or less before B
define IntervalOccursBeforeWithOffsetOrMore: A occurs 3 days or more before B
define IntervalOccursBeforeWithOffsetWithPrecision: A occurs 3 days before day of B
define IntervalOccursBeforeWithOffsetOrLessWithPrecision: A occurs 3 days or less before day of B
define IntervalOccursBeforeWithOffsetOrMoreWithPrecision: A occurs 3 days or more before day of B

define IntervalOccursBeforeStart: A occurs before start B
define IntervalOccursBeforeStartWithPrecision: A occurs before day of start B
define IntervalOccursBeforeStartWithOffset: A occurs 3 days before start B
define IntervalOccursBeforeStartWithOffsetOrLess: A occurs 3 days or less before start B
define IntervalOccursBeforeStartWithOffsetOrMore: A occurs 3 days or more before start B
define IntervalOccursBeforeStartWithOffsetWithPrecision: A occurs 3 days before day of start B
define IntervalOccursBeforeStartWithOffsetOrLessWithPrecision: A occurs 3 days or less before day of start B
define IntervalOccursBeforeStartWithOffsetOrMoreWithPrecision: A occurs 3 days or more before day of start B

define IntervalOccursBeforeEnd: A occurs before end B
define IntervalOccursBeforeEndWithPrecision: A occurs before day of end B
define IntervalOccursBeforeEndWithOffset: A occurs 3 days before end B
define IntervalOccursBeforeEndWithOffsetOrLess: A occurs 3 days or less before end B
define IntervalOccursBeforeEndWithOffsetOrMore: A occurs 3 days or more before end B
define IntervalOccursBeforeEndWithOffsetWithPrecision: A occurs 3 days before day of end B
define IntervalOccursBeforeEndWithOffsetOrLessWithPrecision: A occurs 3 days or less before day of end B
define IntervalOccursBeforeEndWithOffsetOrMoreWithPrecision: A occurs 3 days or more before day of end B

define IntervalOccursUsingUCUM: A occurs 3 'd' before B

// Within interval operator phrase
// ('starts' | 'ends' | 'occurs')? 'properly'? 'within' quantityLiteral 'of' ('start' | 'end')?
//define IntervalWithin: A within 3 days of B // This should not be supported, it's too ambiguous
//define IntervalProperlyWithin: A properly within 3 days of B
define PointWithin: PointA within 3 days of PointB
define PointProperlyWithin: PointA properly within 3 days of PointB
define PointOccursWithin: PointA occurs within 3 days of PointB
define PointOccursProperlyWithin: PointA occurs properly within 3 days of PointB
define PointWithinInterval: PointA within 3 days of B
define PointProperlyWithinInterval: PointA properly within 3 days of B
define PointOccursWithinInterval: PointA occurs within 3 days of B
define PointOccursProperlyWithinInterval: PointA occurs properly within 3 days of B

define IntervalStartsWithin: A starts within 3 days of B
define IntervalStartsProperlyWithin: A starts properly within 3 days of B
define IntervalStartsWithinPoint: A starts within 3 days of PointB
define IntervalStartsProperlyWithinPoint: A starts properly within 3 days of PointB
define IntervalStartsWithinStart: A starts within 3 days of start B
define IntervalStartsProperlyWithinStart: A starts properly within 3 days of start B
define IntervalStartsWithinEnd: A starts within 3 days of end B
define IntervalStartsProperlyWithinEnd: A starts properly within 3 days of end B

define IntervalEndsWithin: A ends within 3 days of B
define IntervalEndsProperlyWithin: A ends properly within 3 days of B
define IntervalEndsWithinPoint: A ends within 3 days of PointB
define IntervalEndsProperlyWithinPoint: A ends properly within 3 days of PointB
define IntervalEndsWithinStart: A ends within 3 days of start B
define IntervalEndsProperlyWithinStart: A ends properly within 3 days of start B
define IntervalEndsWithinEnd: A ends within 3 days of end B
define IntervalEndsProperlyWithinEnd: A ends properly within 3 days of end B

define IntervalWithinUsingUCUM: A starts within 3 'd' of B

//define IntervalWithin: IntervalA occurs within 3 days of IntervalB
//define IntervalExpression: IntervalA starts same day or after start of IntervalB - 3 days and IntervalA ends same day or before end of IntervalB + 3 days

