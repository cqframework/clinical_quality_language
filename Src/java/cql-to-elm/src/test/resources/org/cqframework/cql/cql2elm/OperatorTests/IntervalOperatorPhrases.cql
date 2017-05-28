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
// ('starts' | 'ends' | 'occurs')? quantityOffset? temporalRelationship dateTimePrecisionSpecifier? ('start' | 'end')?
define IntervalBefore: A before B
define IntervalBeforeWithPrecision: A before day of B
define IntervalBeforeWithOffset: A 3 days before B
define IntervalBeforeWithOffsetOrLess: A 3 days or less before B
define IntervalBeforeWithOffsetOrMore: A 3 days or more before B
define IntervalBeforeWithLessThanOffset: A less than 3 days before B
define IntervalBeforeWithMoreThanOffset: A more than 3 days before B
define IntervalBeforeWithOffsetWithPrecision: A 3 days before day of B
define IntervalBeforeWithOffsetOrLessWithPrecision: A 3 days or less before day of B
define IntervalBeforeWithOffsetOrMoreWithPrecision: A 3 days or more before day of B
define IntervalBeforeWithLessThanOffsetWithPrecision: A less than 3 days before day of B
define IntervalBeforeWithMoreThanOffsetWithPrecision: A more than 3 days before day of B

define IntervalOnOrBefore: A on or before B
define IntervalOnOrBeforeWithPrecision: A on or before day of B
define IntervalOnOrBeforeWithOffset: A 3 days on or before B
define IntervalOnOrBeforeWithOffsetOrLess: A 3 days or less on or before B
define IntervalOnOrBeforeWithOffsetOrMore: A 3 days or more on or before B
define IntervalOnOrBeforeWithLessThanOffset: A less than 3 days on or before B
define IntervalOnOrBeforeWithMoreThanOffset: A more than 3 days on or before B
define IntervalOnOrBeforeWithOffsetWithPrecision: A 3 days on or before day of B
define IntervalOnOrBeforeWithOffsetOrLessWithPrecision: A 3 days or less on or before day of B
define IntervalOnOrBeforeWithOffsetOrMoreWithPrecision: A 3 days or more on or before day of B
define IntervalOnOrBeforeWithLessThanOffsetWithPrecision: A less than 3 days on or before day of B
define IntervalOnOrBeforeWithMoreThanOffsetWithPrecision: A more than 3 days on or before day of B

define IntervalBeforeOrOn: A before or on B
define IntervalBeforeOrOnWithPrecision: A before or on day of B
define IntervalBeforeOrOnWithOffset: A 3 days before or on B
define IntervalBeforeOrOnWithOffsetOrLess: A 3 days or less before or on B
define IntervalBeforeOrOnWithOffsetOrMore: A 3 days or more before or on B
define IntervalBeforeOrOnWithLessThanOffset: A less than 3 days before or on B
define IntervalBeforeOrOnWithMoreThanOffset: A more than 3 days before or on B
define IntervalBeforeOrOnWithOffsetWithPrecision: A 3 days before or on day of B
define IntervalBeforeOrOnWithOffsetOrLessWithPrecision: A 3 days or less before or on day of B
define IntervalBeforeOrOnWithOffsetOrMoreWithPrecision: A 3 days or more before or on day of B
define IntervalBeforeOrOnWithLessThanOffsetWithPrecision: A less than 3 days before or on day of B
define IntervalBeforeOrOnWithMoreThanOffsetWithPrecision: A more than 3 days before or on day of B

define IntervalBeforeUsingUCUM: A 3 'd' before B

define IntervalBeforeStart: A before start B
define IntervalBeforeStartWithPrecision: A before day of start B
define IntervalBeforeStartWithOffset: A 3 days before start B
define IntervalBeforeStartWithOffsetOrLess: A 3 days or less before start B
define IntervalBeforeStartWithOffsetOrMore: A 3 days or more before start B
define IntervalBeforeStartWithLessThanOffset: A less than 3 days before start B
define IntervalBeforeStartWithMoreThanOffset: A more than 3 days before start B
define IntervalBeforeStartWithOffsetWithPrecision: A 3 days before day of start B
define IntervalBeforeStartWithOffsetOrLessWithPrecision: A 3 days or less before day of start B
define IntervalBeforeStartWithOffsetOrMoreWithPrecision: A 3 days or more before day of start B
define IntervalBeforeStartWithLessThanOffsetWithPrecision: A less than 3 days before day of start B
define IntervalBeforeStartWithMoreThanOffsetWithPrecision: A more than 3 days before day of start B

define IntervalOnOrBeforeStart: A on or before start B
define IntervalOnOrBeforeStartWithPrecision: A on or before day of start B
define IntervalOnOrBeforeStartWithOffset: A 3 days on or before start B
define IntervalOnOrBeforeStartWithOffsetOrLess: A 3 days or less on or before start B
define IntervalOnOrBeforeStartWithOffsetOrMore: A 3 days or more on or before start B
define IntervalOnOrBeforeStartWithLessThanOffset: A less than 3 days on or before start B
define IntervalOnOrBeforeStartWithMoreThanOffset: A more than 3 days on or before start B
define IntervalOnOrBeforeStartWithOffsetWithPrecision: A 3 days on or before day of start B
define IntervalOnOrBeforeStartWithOffsetOrLessWithPrecision: A 3 days or less on or before day of start B
define IntervalOnOrBeforeStartWithOffsetOrMoreWithPrecision: A 3 days or more on or before day of start B
define IntervalOnOrBeforeStartWithLessThanOffsetWithPrecision: A less than 3 days on or before day of start B
define IntervalOnOrBeforeStartWithMoreThanOffsetWithPrecision: A more than 3 days on or before day of start B

define IntervalBeforeOrOnStart: A before or on start B
define IntervalBeforeOrOnStartWithPrecision: A before or on day of start B
define IntervalBeforeOrOnStartWithOffset: A 3 days before or on start B
define IntervalBeforeOrOnStartWithOffsetOrLess: A 3 days or less before or on start B
define IntervalBeforeOrOnStartWithOffsetOrMore: A 3 days or more before or on start B
define IntervalBeforeOrOnStartWithLessThanOffset: A less than 3 days before or on start B
define IntervalBeforeOrOnStartWithMoreThanOffset: A more than 3 days before or on start B
define IntervalBeforeOrOnStartWithOffsetWithPrecision: A 3 days before or on day of start B
define IntervalBeforeOrOnStartWithOffsetOrLessWithPrecision: A 3 days or less before or on day of start B
define IntervalBeforeOrOnStartWithOffsetOrMoreWithPrecision: A 3 days or more before or on day of start B
define IntervalBeforeOrOnStartWithLessThanOffsetWithPrecision: A less than 3 days before or on day of start B
define IntervalBeforeOrOnStartWithMoreThanOffsetWithPrecision: A more than 3 days before or on day of start B

define IntervalBeforeEnd: A before end B
define IntervalBeforeEndWithPrecision: A before day of end B
define IntervalBeforeEndWithOffset: A 3 days before end B
define IntervalBeforeEndWithOffsetOrLess: A 3 days or less before end B
define IntervalBeforeEndWithOffsetOrMore: A 3 days or more before end B
define IntervalBeforeEndWithLessThanOffset: A less than 3 days before end B
define IntervalBeforeEndWithMoreThanOffset: A more than 3 days before end B
define IntervalBeforeEndWithOffsetWithPrecision: A 3 days before day of end B
define IntervalBeforeEndWithOffsetOrLessWithPrecision: A 3 days or less before day of end B
define IntervalBeforeEndWithOffsetOrMoreWithPrecision: A 3 days or more before day of end B
define IntervalBeforeEndWithLessThanOffsetWithPrecision: A less than 3 days before day of end B
define IntervalBeforeEndWithMoreThanOffsetWithPrecision: A more than 3 days before day of end B

define IntervalOnOrBeforeEnd: A on or before end B
define IntervalOnOrBeforeEndWithPrecision: A on or before day of end B
define IntervalOnOrBeforeEndWithOffset: A 3 days on or before end B
define IntervalOnOrBeforeEndWithOffsetOrLess: A 3 days or less on or before end B
define IntervalOnOrBeforeEndWithOffsetOrMore: A 3 days or more on or before end B
define IntervalOnOrBeforeEndWithLessThanOffset: A less than 3 days on or before end B
define IntervalOnOrBeforeEndWithMoreThanOffset: A more than 3 days on or before end B
define IntervalOnOrBeforeEndWithOffsetWithPrecision: A 3 days on or before day of end B
define IntervalOnOrBeforeEndWithOffsetOrLessWithPrecision: A 3 days or less on or before day of end B
define IntervalOnOrBeforeEndWithOffsetOrMoreWithPrecision: A 3 days or more on or before day of end B
define IntervalOnOrBeforeEndWithLessThanOffsetWithPrecision: A less than 3 days on or before day of end B
define IntervalOnOrBeforeEndWithMoreThanOffsetWithPrecision: A more than 3 days on or before day of end B

define IntervalBeforeOrOnEnd: A before or on end B
define IntervalBeforeOrOnEndWithPrecision: A before or on day of end B
define IntervalBeforeOrOnEndWithOffset: A 3 days before or on end B
define IntervalBeforeOrOnEndWithOffsetOrLess: A 3 days or less before or on end B
define IntervalBeforeOrOnEndWithOffsetOrMore: A 3 days or more before or on end B
define IntervalBeforeOrOnEndWithLessThanOffset: A less than 3 days before or on end B
define IntervalBeforeOrOnEndWithMoreThanOffset: A more than 3 days before or on end B
define IntervalBeforeOrOnEndWithOffsetWithPrecision: A 3 days before or on day of end B
define IntervalBeforeOrOnEndWithOffsetOrLessWithPrecision: A 3 days or less before or on day of end B
define IntervalBeforeOrOnEndWithOffsetOrMoreWithPrecision: A 3 days or more before or on day of end B
define IntervalBeforeOrOnEndWithLessThanOffsetWithPrecision: A less than 3 days before or on day of end B
define IntervalBeforeOrOnEndWithMoreThanOffsetWithPrecision: A more than 3 days before or on day of end B

define PointBefore: PointA before PointB
define PointBeforeWithPrecision: PointA before day of PointB
define PointBeforeWithOffset: PointA 3 days before PointB
define PointBeforeWithOffsetOrLess: PointA 3 days or less before PointB
define PointBeforeWithOffsetOrMore: PointA 3 days or more before PointB
define PointBeforeWithLessThanOffset: PointA less than 3 days before PointB
define PointBeforeWithMoreThanOffset: PointA more than 3 days before PointB
define PointBeforeWithOffsetWithPrecision: PointA 3 days before day of PointB
define PointBeforeWithOffsetOrLessWithPrecision: PointA 3 days or less before day of PointB
define PointBeforeWithOffsetOrMoreWithPrecision: PointA 3 days or more before day of PointB
define PointBeforeWithLessThanOffsetWithPrecision: PointA less than 3 days before day of PointB
define PointBeforeWithMoreThanOffsetWithPrecision: PointA more than 3 days before day of PointB

define PointOnOrBefore: PointA on or before PointB
define PointOnOrBeforeWithPrecision: PointA on or before day of PointB
define PointOnOrBeforeWithOffset: PointA 3 days on or before PointB
define PointOnOrBeforeWithOffsetOrLess: PointA 3 days or less on or before PointB
define PointOnOrBeforeWithOffsetOrMore: PointA 3 days or more on or before PointB
define PointOnOrBeforeWithLessThanOffset: PointA less than 3 days on or before PointB
define PointOnOrBeforeWithMoreThanOffset: PointA more than 3 days on or before PointB
define PointOnOrBeforeWithOffsetWithPrecision: PointA 3 days on or before day of PointB
define PointOnOrBeforeWithOffsetOrLessWithPrecision: PointA 3 days or less on or before day of PointB
define PointOnOrBeforeWithOffsetOrMoreWithPrecision: PointA 3 days or more on or before day of PointB
define PointOnOrBeforeWithLessThanOffsetWithPrecision: PointA less than 3 days on or before day of PointB
define PointOnOrBeforeWithMoreThanOffsetWithPrecision: PointA more than 3 days on or before day of PointB

define PointBeforeOrOn: PointA before or on PointB
define PointBeforeOrOnWithPrecision: PointA before or on day of PointB
define PointBeforeOrOnWithOffset: PointA 3 days before or on PointB
define PointBeforeOrOnWithOffsetOrLess: PointA 3 days or less before or on PointB
define PointBeforeOrOnWithOffsetOrMore: PointA 3 days or more before or on PointB
define PointBeforeOrOnWithLessThanOffset: PointA less than 3 days before or on PointB
define PointBeforeOrOnWithMoreThanOffset: PointA more than 3 days before or on PointB
define PointBeforeOrOnWithOffsetWithPrecision: PointA 3 days before or on day of PointB
define PointBeforeOrOnWithOffsetOrLessWithPrecision: PointA 3 days or less before or on day of PointB
define PointBeforeOrOnWithOffsetOrMoreWithPrecision: PointA 3 days or more before or on day of PointB
define PointBeforeOrOnWithLessThanOffsetWithPrecision: PointA less than 3 days before or on day of PointB
define PointBeforeOrOnWithMoreThanOffsetWithPrecision: PointA more than 3 days before or on day of PointB

define PointBeforeInterval: PointA before B
define PointBeforeIntervalWithPrecision: PointA before day of B
define PointBeforeIntervalWithOffset: PointA 3 days before B
define PointBeforeIntervalWithOffsetOrLess: PointA 3 days or less before B
define PointBeforeIntervalWithOffsetOrMore: PointA 3 days or more before B
define PointBeforeIntervalWithLessThanOffset: PointA less than 3 days before B
define PointBeforeIntervalWithMoreThanOffset: PointA more than 3 days before B
define PointBeforeIntervalWithOffsetWithPrecision: PointA 3 days before day of B
define PointBeforeIntervalWithOffsetOrLessWithPrecision: PointA 3 days or less before day of B
define PointBeforeIntervalWithOffsetOrMoreWithPrecision: PointA 3 days or more before day of B
define PointBeforeIntervalWithLessThanOffsetWithPrecision: PointA less than 3 days before day of B
define PointBeforeIntervalWithMoreThanOffsetWithPrecision: PointA more than 3 days before day of B

define PointOnOrBeforeInterval: PointA on or before B
define PointOnOrBeforeIntervalWithPrecision: PointA on or before day of B
define PointOnOrBeforeIntervalWithOffset: PointA 3 days on or before B
define PointOnOrBeforeIntervalWithOffsetOrLess: PointA 3 days or less on or before B
define PointOnOrBeforeIntervalWithOffsetOrMore: PointA 3 days or more on or before B
define PointOnOrBeforeIntervalWithLessThanOffset: PointA less than 3 days on or before B
define PointOnOrBeforeIntervalWithMoreThanOffset: PointA more than 3 days on or before B
define PointOnOrBeforeIntervalWithOffsetWithPrecision: PointA 3 days on or before day of B
define PointOnOrBeforeIntervalWithOffsetOrLessWithPrecision: PointA 3 days or less on or before day of B
define PointOnOrBeforeIntervalWithOffsetOrMoreWithPrecision: PointA 3 days or more on or before day of B
define PointOnOrBeforeIntervalWithLessThanOffsetWithPrecision: PointA less than 3 days on or before day of B
define PointOnOrBeforeIntervalWithMoreThanOffsetWithPrecision: PointA more than 3 days on or before day of B

define PointBeforeOrOnInterval: PointA before or on B
define PointBeforeOrOnIntervalWithPrecision: PointA before or on day of B
define PointBeforeOrOnIntervalWithOffset: PointA 3 days before or on B
define PointBeforeOrOnIntervalWithOffsetOrLess: PointA 3 days or less before or on B
define PointBeforeOrOnIntervalWithOffsetOrMore: PointA 3 days or more before or on B
define PointBeforeOrOnIntervalWithLessThanOffset: PointA less than 3 days before or on B
define PointBeforeOrOnIntervalWithMoreThanOffset: PointA more than 3 days before or on B
define PointBeforeOrOnIntervalWithOffsetWithPrecision: PointA 3 days before or on day of B
define PointBeforeOrOnIntervalWithOffsetOrLessWithPrecision: PointA 3 days or less before or on day of B
define PointBeforeOrOnIntervalWithOffsetOrMoreWithPrecision: PointA 3 days or more before or on day of B
define PointBeforeOrOnIntervalWithLessThanOffsetWithPrecision: PointA less than 3 days before or on day of B
define PointBeforeOrOnIntervalWithMoreThanOffsetWithPrecision: PointA more than 3 days before or on day of B

define PointBeforeStartInterval: PointA before start B
define PointBeforeStartIntervalWithPrecision: PointA before day of start B
define PointBeforeStartIntervalWithOffset: PointA 3 days before start B
define PointBeforeStartIntervalWithOffsetOrLess: PointA 3 days or less before start B
define PointBeforeStartIntervalWithOffsetOrMore: PointA 3 days or more before start B
define PointBeforeStartIntervalWithLessThanOffset: PointA less than 3 days before start B
define PointBeforeStartIntervalWithMoreThanOffset: PointA more than 3 days before start B
define PointBeforeStartIntervalWithOffsetWithPrecision: PointA 3 days before day of start B
define PointBeforeStartIntervalWithOffsetOrLessWithPrecision: PointA 3 days or less before day of start B
define PointBeforeStartIntervalWithOffsetOrMoreWithPrecision: PointA 3 days or more before day of start B
define PointBeforeStartIntervalWithLessThanOffsetWithPrecision: PointA less than 3 days before day of start B
define PointBeforeStartIntervalWithMoreThanOffsetWithPrecision: PointA more than 3 days before day of start B

define PointOnOrBeforeStartInterval: PointA on or before start B
define PointOnOrBeforeStartIntervalWithPrecision: PointA on or before day of start B
define PointOnOrBeforeStartIntervalWithOffset: PointA 3 days on or before start B
define PointOnOrBeforeStartIntervalWithOffsetOrLess: PointA 3 days or less on or before start B
define PointOnOrBeforeStartIntervalWithOffsetOrMore: PointA 3 days or more on or before start B
define PointOnOrBeforeStartIntervalWithLessThanOffset: PointA less than 3 days on or before start B
define PointOnOrBeforeStartIntervalWithMoreThanOffset: PointA more than 3 days on or before start B
define PointOnOrBeforeStartIntervalWithOffsetWithPrecision: PointA 3 days on or before day of start B
define PointOnOrBeforeStartIntervalWithOffsetOrLessWithPrecision: PointA 3 days or less on or before day of start B
define PointOnOrBeforeStartIntervalWithOffsetOrMoreWithPrecision: PointA 3 days or more on or before day of start B
define PointOnOrBeforeStartIntervalWithLessThanOffsetWithPrecision: PointA less than 3 days on or before day of start B
define PointOnOrBeforeStartIntervalWithMoreThanOffsetWithPrecision: PointA more than 3 days on or before day of start B

define PointBeforeOrOnStartInterval: PointA before or on start B
define PointBeforeOrOnStartIntervalWithPrecision: PointA before or on day of start B
define PointBeforeOrOnStartIntervalWithOffset: PointA 3 days before or on start B
define PointBeforeOrOnStartIntervalWithOffsetOrLess: PointA 3 days or less before or on start B
define PointBeforeOrOnStartIntervalWithOffsetOrMore: PointA 3 days or more before or on start B
define PointBeforeOrOnStartIntervalWithLessThanOffset: PointA less than 3 days before or on start B
define PointBeforeOrOnStartIntervalWithMoreThanOffset: PointA more than 3 days before or on start B
define PointBeforeOrOnStartIntervalWithOffsetWithPrecision: PointA 3 days before or on day of start B
define PointBeforeOrOnStartIntervalWithOffsetOrLessWithPrecision: PointA 3 days or less before or on day of start B
define PointBeforeOrOnStartIntervalWithOffsetOrMoreWithPrecision: PointA 3 days or more before or on day of start B
define PointBeforeOrOnStartIntervalWithLessThanOffsetWithPrecision: PointA less than 3 days before or on day of start B
define PointBeforeOrOnStartIntervalWithMoreThanOffsetWithPrecision: PointA more than 3 days before or on day of start B

define PointBeforeEndInterval: PointA before end B
define PointBeforeEndIntervalWithPrecision: PointA before day of end B
define PointBeforeEndIntervalWithOffset: PointA 3 days before end B
define PointBeforeEndIntervalWithOffsetOrLess: PointA 3 days or less before end B
define PointBeforeEndIntervalWithOffsetOrMore: PointA 3 days or more before end B
define PointBeforeEndIntervalWithLessThanOffset: PointA less than 3 days before end B
define PointBeforeEndIntervalWithMoreThanOffset: PointA more than 3 days before end B
define PointBeforeEndIntervalWithOffsetWithPrecision: PointA 3 days before day of end B
define PointBeforeEndIntervalWithOffsetOrLessWithPrecision: PointA 3 days or less before day of end B
define PointBeforeEndIntervalWithOffsetOrMoreWithPrecision: PointA 3 days or more before day of end B
define PointBeforeEndIntervalWithLessThanOffsetWithPrecision: PointA less than 3 days before day of end B
define PointBeforeEndIntervalWithMoreThanOffsetWithPrecision: PointA more than 3 days before day of end B

define PointOnOrBeforeEndInterval: PointA on or before end B
define PointOnOrBeforeEndIntervalWithPrecision: PointA on or before day of end B
define PointOnOrBeforeEndIntervalWithOffset: PointA 3 days on or before end B
define PointOnOrBeforeEndIntervalWithOffsetOrLess: PointA 3 days or less on or before end B
define PointOnOrBeforeEndIntervalWithOffsetOrMore: PointA 3 days or more on or before end B
define PointOnOrBeforeEndIntervalWithLessThanOffset: PointA less than 3 days on or before end B
define PointOnOrBeforeEndIntervalWithMoreThanOffset: PointA more than 3 days on or before end B
define PointOnOrBeforeEndIntervalWithOffsetWithPrecision: PointA 3 days on or before day of end B
define PointOnOrBeforeEndIntervalWithOffsetOrLessWithPrecision: PointA 3 days or less on or before day of end B
define PointOnOrBeforeEndIntervalWithOffsetOrMoreWithPrecision: PointA 3 days or more on or before day of end B
define PointOnOrBeforeEndIntervalWithLessThanOffsetWithPrecision: PointA less than 3 days on or before day of end B
define PointOnOrBeforeEndIntervalWithMoreThanOffsetWithPrecision: PointA more than 3 days on or before day of end B

define PointBeforeOrOnEndInterval: PointA before or on end B
define PointBeforeOrOnEndIntervalWithPrecision: PointA before or on day of end B
define PointBeforeOrOnEndIntervalWithOffset: PointA 3 days before or on end B
define PointBeforeOrOnEndIntervalWithOffsetOrLess: PointA 3 days or less before or on end B
define PointBeforeOrOnEndIntervalWithOffsetOrMore: PointA 3 days or more before or on end B
define PointBeforeOrOnEndIntervalWithLessThanOffset: PointA less than 3 days before or on end B
define PointBeforeOrOnEndIntervalWithMoreThanOffset: PointA more than 3 days before or on end B
define PointBeforeOrOnEndIntervalWithOffsetWithPrecision: PointA 3 days before or on day of end B
define PointBeforeOrOnEndIntervalWithOffsetOrLessWithPrecision: PointA 3 days or less before or on day of end B
define PointBeforeOrOnEndIntervalWithOffsetOrMoreWithPrecision: PointA 3 days or more before or on day of end B
define PointBeforeOrOnEndIntervalWithLessThanOffsetWithPrecision: PointA less than 3 days before or on day of end B
define PointBeforeOrOnEndIntervalWithMoreThanOffsetWithPrecision: PointA more than 3 days before or on day of end B

define PointOccursBefore: PointA occurs before PointB
define PointOccursBeforeWithPrecision: PointA occurs before day of PointB
define PointOccursBeforeWithOffset: PointA occurs 3 days before PointB
define PointOccursBeforeWithOffsetOrLess: PointA occurs 3 days or less before PointB
define PointOccursBeforeWithOffsetOrMore: PointA occurs 3 days or more before PointB
define PointOccursBeforeWithLessThanOffset: PointA occurs less than 3 days before PointB
define PointOccursBeforeWithMoreThanOffset: PointA occurs more than 3 days before PointB
define PointOccursBeforeWithOffsetWithPrecision: PointA occurs 3 days before day of PointB
define PointOccursBeforeWithOffsetOrLessWithPrecision: PointA occurs 3 days or less before day of PointB
define PointOccursBeforeWithOffsetOrMoreWithPrecision: PointA occurs 3 days or more before day of PointB
define PointOccursBeforeWithLessThanOffsetWithPrecision: PointA occurs less than 3 days before day of PointB
define PointOccursBeforeWithMoreThanOffsetWithPrecision: PointA occurs more than 3 days before day of PointB

define PointOccursOnOrBefore: PointA occurs on or before PointB
define PointOccursOnOrBeforeWithPrecision: PointA occurs on or before day of PointB
define PointOccursOnOrBeforeWithOffset: PointA occurs 3 days on or before PointB
define PointOccursOnOrBeforeWithOffsetOrLess: PointA occurs 3 days or less on or before PointB
define PointOccursOnOrBeforeWithOffsetOrMore: PointA occurs 3 days or more on or before PointB
define PointOccursOnOrBeforeWithLessThanOffset: PointA occurs less than 3 days on or before PointB
define PointOccursOnOrBeforeWithMoreThanOffset: PointA occurs more than 3 days on or before PointB
define PointOccursOnOrBeforeWithOffsetWithPrecision: PointA occurs 3 days on or before day of PointB
define PointOccursOnOrBeforeWithOffsetOrLessWithPrecision: PointA occurs 3 days or less on or before day of PointB
define PointOccursOnOrBeforeWithOffsetOrMoreWithPrecision: PointA occurs 3 days or more on or before day of PointB
define PointOccursOnOrBeforeWithLessThanOffsetWithPrecision: PointA occurs less than 3 days on or before day of PointB
define PointOccursOnOrBeforeWithMoreThanOffsetWithPrecision: PointA occurs more than 3 days on or before day of PointB

define PointOccursBeforeOrOn: PointA occurs before or on PointB
define PointOccursBeforeOrOnWithPrecision: PointA occurs before or on day of PointB
define PointOccursBeforeOrOnWithOffset: PointA occurs 3 days before or on PointB
define PointOccursBeforeOrOnWithOffsetOrLess: PointA occurs 3 days or less before or on PointB
define PointOccursBeforeOrOnWithOffsetOrMore: PointA occurs 3 days or more before or on PointB
define PointOccursBeforeOrOnWithLessThanOffset: PointA occurs less than 3 days before or on PointB
define PointOccursBeforeOrOnWithMoreThanOffset: PointA occurs more than 3 days before or on PointB
define PointOccursBeforeOrOnWithOffsetWithPrecision: PointA occurs 3 days before or on day of PointB
define PointOccursBeforeOrOnWithOffsetOrLessWithPrecision: PointA occurs 3 days or less before or on day of PointB
define PointOccursBeforeOrOnWithOffsetOrMoreWithPrecision: PointA occurs 3 days or more before or on day of PointB
define PointOccursBeforeOrOnWithLessThanOffsetWithPrecision: PointA occurs less than 3 days before or on day of PointB
define PointOccursBeforeOrOnWithMoreThanOffsetWithPrecision: PointA occurs more than 3 days before or on day of PointB

define PointOccursBeforeInterval: PointA occurs before B
define PointOccursBeforeIntervalWithPrecision: PointA occurs before day of B
define PointOccursBeforeIntervalWithOffset: PointA occurs 3 days before B
define PointOccursBeforeIntervalWithOffsetOrLess: PointA occurs 3 days or less before B
define PointOccursBeforeIntervalWithOffsetOrMore: PointA occurs 3 days or more before B
define PointOccursBeforeIntervalWithLessThanOffset: PointA occurs less than 3 days before B
define PointOccursBeforeIntervalWithMoreThanOffset: PointA occurs more than 3 days before B
define PointOccursBeforeIntervalWithOffsetWithPrecision: PointA occurs 3 days before day of B
define PointOccursBeforeIntervalWithOffsetOrLessWithPrecision: PointA occurs 3 days or less before day of B
define PointOccursBeforeIntervalWithOffsetOrMoreWithPrecision: PointA occurs 3 days or more before day of B
define PointOccursBeforeIntervalWithLessThanOffsetWithPrecision: PointA occurs less than 3 days before day of B
define PointOccursBeforeIntervalWithMoreThanOffsetWithPrecision: PointA occurs more than 3 days before day of B

define PointOccursOnOrBeforeInterval: PointA occurs on or before B
define PointOccursOnOrBeforeIntervalWithPrecision: PointA occurs on or before day of B
define PointOccursOnOrBeforeIntervalWithOffset: PointA occurs 3 days on or before B
define PointOccursOnOrBeforeIntervalWithOffsetOrLess: PointA occurs 3 days or less on or before B
define PointOccursOnOrBeforeIntervalWithOffsetOrMore: PointA occurs 3 days or more on or before B
define PointOccursOnOrBeforeIntervalWithLessThanOffset: PointA occurs less than 3 days on or before B
define PointOccursOnOrBeforeIntervalWithMoreThanOffset: PointA occurs more than 3 days on or before B
define PointOccursOnOrBeforeIntervalWithOffsetWithPrecision: PointA occurs 3 days on or before day of B
define PointOccursOnOrBeforeIntervalWithOffsetOrLessWithPrecision: PointA occurs 3 days or less on or before day of B
define PointOccursOnOrBeforeIntervalWithOffsetOrMoreWithPrecision: PointA occurs 3 days or more on or before day of B
define PointOccursOnOrBeforeIntervalWithLessThanOffsetWithPrecision: PointA occurs less than 3 days on or before day of B
define PointOccursOnOrBeforeIntervalWithMoreThanOffsetWithPrecision: PointA occurs more than 3 days on or before day of B

define PointOccursBeforeOrOnInterval: PointA occurs before or on B
define PointOccursBeforeOrOnIntervalWithPrecision: PointA occurs before or on day of B
define PointOccursBeforeOrOnIntervalWithOffset: PointA occurs 3 days before or on B
define PointOccursBeforeOrOnIntervalWithOffsetOrLess: PointA occurs 3 days or less before or on B
define PointOccursBeforeOrOnIntervalWithOffsetOrMore: PointA occurs 3 days or more before or on B
define PointOccursBeforeOrOnIntervalWithLessThanOffset: PointA occurs less than 3 days before or on B
define PointOccursBeforeOrOnIntervalWithMoreThanOffset: PointA occurs more than 3 days before or on B
define PointOccursBeforeOrOnIntervalWithOffsetWithPrecision: PointA occurs 3 days before or on day of B
define PointOccursBeforeOrOnIntervalWithOffsetOrLessWithPrecision: PointA occurs 3 days or less before or on day of B
define PointOccursBeforeOrOnIntervalWithOffsetOrMoreWithPrecision: PointA occurs 3 days or more before or on day of B
define PointOccursBeforeOrOnIntervalWithLessThanOffsetWithPrecision: PointA occurs less than 3 days before or on day of B
define PointOccursBeforeOrOnIntervalWithMoreThanOffsetWithPrecision: PointA occurs more than 3 days before or on day of B

define PointOccursBeforeStartInterval: PointA occurs before start B
define PointOccursBeforeStartIntervalWithPrecision: PointA occurs before day of start B
define PointOccursBeforeStartIntervalWithOffset: PointA occurs 3 days before start B
define PointOccursBeforeStartIntervalWithOffsetOrLess: PointA occurs 3 days or less before start B
define PointOccursBeforeStartIntervalWithOffsetOrMore: PointA occurs 3 days or more before start B
define PointOccursBeforeStartIntervalWithLessThanOffset: PointA occurs less than 3 days before start B
define PointOccursBeforeStartIntervalWithMoreThanOffset: PointA occurs more than 3 days before start B
define PointOccursBeforeStartIntervalWithOffsetWithPrecision: PointA occurs 3 days before day of start B
define PointOccursBeforeStartIntervalWithOffsetOrLessWithPrecision: PointA occurs 3 days or less before day of start B
define PointOccursBeforeStartIntervalWithOffsetOrMoreWithPrecision: PointA occurs 3 days or more before day of start B
define PointOccursBeforeStartIntervalWithLessThanOffsetWithPrecision: PointA occurs less than 3 days before day of start B
define PointOccursBeforeStartIntervalWithMoreThanOffsetWithPrecision: PointA occurs more than 3 days before day of start B

define PointOccursOnOrBeforeStartInterval: PointA occurs on or before start B
define PointOccursOnOrBeforeStartIntervalWithPrecision: PointA occurs on or before day of start B
define PointOccursOnOrBeforeStartIntervalWithOffset: PointA occurs 3 days on or before start B
define PointOccursOnOrBeforeStartIntervalWithOffsetOrLess: PointA occurs 3 days or less on or before start B
define PointOccursOnOrBeforeStartIntervalWithOffsetOrMore: PointA occurs 3 days or more on or before start B
define PointOccursOnOrBeforeStartIntervalWithLessThanOffset: PointA occurs less than 3 days on or before start B
define PointOccursOnOrBeforeStartIntervalWithMoreThanOffset: PointA occurs more than 3 days on or before start B
define PointOccursOnOrBeforeStartIntervalWithOffsetWithPrecision: PointA occurs 3 days on or before day of start B
define PointOccursOnOrBeforeStartIntervalWithOffsetOrLessWithPrecision: PointA occurs 3 days or less on or before day of start B
define PointOccursOnOrBeforeStartIntervalWithOffsetOrMoreWithPrecision: PointA occurs 3 days or more on or before day of start B
define PointOccursOnOrBeforeStartIntervalWithLessThanOffsetWithPrecision: PointA occurs less than 3 days on or before day of start B
define PointOccursOnOrBeforeStartIntervalWithMoreThanOffsetWithPrecision: PointA occurs more than 3 days on or before day of start B

define PointOccursBeforeOrOnStartInterval: PointA occurs before or on start B
define PointOccursBeforeOrOnStartIntervalWithPrecision: PointA occurs before or on day of start B
define PointOccursBeforeOrOnStartIntervalWithOffset: PointA occurs 3 days before or on start B
define PointOccursBeforeOrOnStartIntervalWithOffsetOrLess: PointA occurs 3 days or less before or on start B
define PointOccursBeforeOrOnStartIntervalWithOffsetOrMore: PointA occurs 3 days or more before or on start B
define PointOccursBeforeOrOnStartIntervalWithLessThanOffset: PointA occurs less than 3 days before or on start B
define PointOccursBeforeOrOnStartIntervalWithMoreThanOffset: PointA occurs more than 3 days before or on start B
define PointOccursBeforeOrOnStartIntervalWithOffsetWithPrecision: PointA occurs 3 days before or on day of start B
define PointOccursBeforeOrOnStartIntervalWithOffsetOrLessWithPrecision: PointA occurs 3 days or less before or on day of start B
define PointOccursBeforeOrOnStartIntervalWithOffsetOrMoreWithPrecision: PointA occurs 3 days or more before or on day of start B
define PointOccursBeforeOrOnStartIntervalWithLessThanOffsetWithPrecision: PointA occurs less than 3 days before or on day of start B
define PointOccursBeforeOrOnStartIntervalWithMoreThanOffsetWithPrecision: PointA occurs more than 3 days before or on day of start B

define PointOccursBeforeEndInterval: PointA occurs before end B
define PointOccursBeforeEndIntervalWithPrecision: PointA occurs before day of end B
define PointOccursBeforeEndIntervalWithOffset: PointA occurs 3 days before end B
define PointOccursBeforeEndIntervalWithOffsetOrLess: PointA occurs 3 days or less before end B
define PointOccursBeforeEndIntervalWithOffsetOrMore: PointA occurs 3 days or more before end B
define PointOccursBeforeEndIntervalWithLessThanOffset: PointA occurs less than 3 days before end B
define PointOccursBeforeEndIntervalWithMoreThanOffset: PointA occurs more than 3 days before end B
define PointOccursBeforeEndIntervalWithOffsetWithPrecision: PointA occurs 3 days before day of end B
define PointOccursBeforeEndIntervalWithOffsetOrLessWithPrecision: PointA occurs 3 days or less before day of end B
define PointOccursBeforeEndIntervalWithOffsetOrMoreWithPrecision: PointA occurs 3 days or more before day of end B
define PointOccursBeforeEndIntervalWithLessThanOffsetWithPrecision: PointA occurs less than 3 days before day of end B
define PointOccursBeforeEndIntervalWithMoreThanOffsetWithPrecision: PointA occurs more than 3 days before day of end B

define PointOccursOnOrBeforeEndInterval: PointA occurs on or before end B
define PointOccursOnOrBeforeEndIntervalWithPrecision: PointA occurs on or before day of end B
define PointOccursOnOrBeforeEndIntervalWithOffset: PointA occurs 3 days on or before end B
define PointOccursOnOrBeforeEndIntervalWithOffsetOrLess: PointA occurs 3 days or less on or before end B
define PointOccursOnOrBeforeEndIntervalWithOffsetOrMore: PointA occurs 3 days or more on or before end B
define PointOccursOnOrBeforeEndIntervalWithLessThanOffset: PointA occurs less than 3 days on or before end B
define PointOccursOnOrBeforeEndIntervalWithMoreThanOffset: PointA occurs more than 3 days on or before end B
define PointOccursOnOrBeforeEndIntervalWithOffsetWithPrecision: PointA occurs 3 days on or before day of end B
define PointOccursOnOrBeforeEndIntervalWithOffsetOrLessWithPrecision: PointA occurs 3 days or less on or before day of end B
define PointOccursOnOrBeforeEndIntervalWithOffsetOrMoreWithPrecision: PointA occurs 3 days or more on or before day of end B
define PointOccursOnOrBeforeEndIntervalWithLessThanOffsetWithPrecision: PointA occurs less than 3 days on or before day of end B
define PointOccursOnOrBeforeEndIntervalWithMoreThanOffsetWithPrecision: PointA occurs more than 3 days on or before day of end B

define PointOccursBeforeOrOnEndInterval: PointA occurs before or on end B
define PointOccursBeforeOrOnEndIntervalWithPrecision: PointA occurs before or on day of end B
define PointOccursBeforeOrOnEndIntervalWithOffset: PointA occurs 3 days before or on end B
define PointOccursBeforeOrOnEndIntervalWithOffsetOrLess: PointA occurs 3 days or less before or on end B
define PointOccursBeforeOrOnEndIntervalWithOffsetOrMore: PointA occurs 3 days or more before or on end B
define PointOccursBeforeOrOnEndIntervalWithLessThanOffset: PointA occurs less than 3 days before or on end B
define PointOccursBeforeOrOnEndIntervalWithMoreThanOffset: PointA occurs more than 3 days before or on end B
define PointOccursBeforeOrOnEndIntervalWithOffsetWithPrecision: PointA occurs 3 days before or on day of end B
define PointOccursBeforeOrOnEndIntervalWithOffsetOrLessWithPrecision: PointA occurs 3 days or less before or on day of end B
define PointOccursBeforeOrOnEndIntervalWithOffsetOrMoreWithPrecision: PointA occurs 3 days or more before or on day of end B
define PointOccursBeforeOrOnEndIntervalWithLessThanOffsetWithPrecision: PointA occurs less than 3 days before or on day of end B
define PointOccursBeforeOrOnEndIntervalWithMoreThanOffsetWithPrecision: PointA occurs more than 3 days before or on day of end B

define IntervalBeforePoint: A before PointB
define IntervalBeforePointWithPrecision: A before day of PointB
define IntervalBeforePointWithOffset: A 3 days before PointB
define IntervalBeforePointWithOffsetOrLess: A 3 days or less before PointB
define IntervalBeforePointWithOffsetOrMore: A 3 days or more before PointB
define IntervalBeforePointWithLessThanOffset: A less than 3 days before PointB
define IntervalBeforePointWithMoreThanOffset: A more than 3 days before PointB
define IntervalBeforePointWithOffsetWithPrecision: A 3 days before day of PointB
define IntervalBeforePointWithOffsetOrLessWithPrecision: A 3 days or less before day of PointB
define IntervalBeforePointWithOffsetOrMoreWithPrecision: A 3 days or more before day of PointB
define IntervalBeforePointWithLessThanOffsetWithPrecision: A less than 3 days before day of PointB
define IntervalBeforePointWithMoreThanOffsetWithPrecision: A more than 3 days before day of PointB

define IntervalOnOrBeforePoint: A on or before PointB
define IntervalOnOrBeforePointWithPrecision: A on or before day of PointB
define IntervalOnOrBeforePointWithOffset: A 3 days on or before PointB
define IntervalOnOrBeforePointWithOffsetOrLess: A 3 days or less on or before PointB
define IntervalOnOrBeforePointWithOffsetOrMore: A 3 days or more on or before PointB
define IntervalOnOrBeforePointWithLessThanOffset: A less than 3 days on or before PointB
define IntervalOnOrBeforePointWithMoreThanOffset: A more than 3 days on or before PointB
define IntervalOnOrBeforePointWithOffsetWithPrecision: A 3 days on or before day of PointB
define IntervalOnOrBeforePointWithOffsetOrLessWithPrecision: A 3 days or less on or before day of PointB
define IntervalOnOrBeforePointWithOffsetOrMoreWithPrecision: A 3 days or more on or before day of PointB
define IntervalOnOrBeforePointWithLessThanOffsetWithPrecision: A less than 3 days on or before day of PointB
define IntervalOnOrBeforePointWithMoreThanOffsetWithPrecision: A more than 3 days on or before day of PointB

define IntervalBeforeOrOnPoint: A before or on PointB
define IntervalBeforeOrOnPointWithPrecision: A before or on day of PointB
define IntervalBeforeOrOnPointWithOffset: A 3 days before or on PointB
define IntervalBeforeOrOnPointWithOffsetOrLess: A 3 days or less before or on PointB
define IntervalBeforeOrOnPointWithOffsetOrMore: A 3 days or more before or on PointB
define IntervalBeforeOrOnPointWithLessThanOffset: A less than 3 days before or on PointB
define IntervalBeforeOrOnPointWithMoreThanOffset: A more than 3 days before or on PointB
define IntervalBeforeOrOnPointWithOffsetWithPrecision: A 3 days before or on day of PointB
define IntervalBeforeOrOnPointWithOffsetOrLessWithPrecision: A 3 days or less before or on day of PointB
define IntervalBeforeOrOnPointWithOffsetOrMoreWithPrecision: A 3 days or more before or on day of PointB
define IntervalBeforeOrOnPointWithLessThanOffsetWithPrecision: A less than 3 days before or on day of PointB
define IntervalBeforeOrOnPointWithMoreThanOffsetWithPrecision: A more than 3 days before or on day of PointB

define IntervalStartsBeforePoint: A starts before PointB
define IntervalStartsBeforePointWithPrecision: A starts before day of PointB
define IntervalStartsBeforePointWithOffset: A starts 3 days before PointB
define IntervalStartsBeforePointWithOffsetOrLess: A starts 3 days or less before PointB
define IntervalStartsBeforePointWithOffsetOrMore: A starts 3 days or more before PointB
define IntervalStartsBeforePointWithLessThanOffset: A starts less than 3 days before PointB
define IntervalStartsBeforePointWithMoreThanOffset: A starts more than 3 days before PointB
define IntervalStartsBeforePointWithOffsetWithPrecision: A starts 3 days before day of PointB
define IntervalStartsBeforePointWithOffsetOrLessWithPrecision: A starts 3 days or less before day of PointB
define IntervalStartsBeforePointWithOffsetOrMoreWithPrecision: A starts 3 days or more before day of PointB
define IntervalStartsBeforePointWithLessThanOffsetWithPrecision: A starts less than 3 days before day of PointB
define IntervalStartsBeforePointWithMoreThanOffsetWithPrecision: A starts more than 3 days before day of PointB

define IntervalStartsOnOrBeforePoint: A starts on or before PointB
define IntervalStartsOnOrBeforePointWithPrecision: A starts on or before day of PointB
define IntervalStartsOnOrBeforePointWithOffset: A starts 3 days on or before PointB
define IntervalStartsOnOrBeforePointWithOffsetOrLess: A starts 3 days or less on or before PointB
define IntervalStartsOnOrBeforePointWithOffsetOrMore: A starts 3 days or more on or before PointB
define IntervalStartsOnOrBeforePointWithLessThanOffset: A starts less than 3 days on or before PointB
define IntervalStartsOnOrBeforePointWithMoreThanOffset: A starts more than 3 days on or before PointB
define IntervalStartsOnOrBeforePointWithOffsetWithPrecision: A starts 3 days on or before day of PointB
define IntervalStartsOnOrBeforePointWithOffsetOrLessWithPrecision: A starts 3 days or less on or before day of PointB
define IntervalStartsOnOrBeforePointWithOffsetOrMoreWithPrecision: A starts 3 days or more on or before day of PointB
define IntervalStartsOnOrBeforePointWithLessThanOffsetWithPrecision: A starts less than 3 days on or before day of PointB
define IntervalStartsOnOrBeforePointWithMoreThanOffsetWithPrecision: A starts more than 3 days on or before day of PointB

define IntervalStartsBeforeOrOnPoint: A starts before or on PointB
define IntervalStartsBeforeOrOnPointWithPrecision: A starts before or on day of PointB
define IntervalStartsBeforeOrOnPointWithOffset: A starts 3 days before or on PointB
define IntervalStartsBeforeOrOnPointWithOffsetOrLess: A starts 3 days or less before or on PointB
define IntervalStartsBeforeOrOnPointWithOffsetOrMore: A starts 3 days or more before or on PointB
define IntervalStartsBeforeOrOnPointWithLessThanOffset: A starts less than 3 days before or on PointB
define IntervalStartsBeforeOrOnPointWithMoreThanOffset: A starts more than 3 days before or on PointB
define IntervalStartsBeforeOrOnPointWithOffsetWithPrecision: A starts 3 days before or on day of PointB
define IntervalStartsBeforeOrOnPointWithOffsetOrLessWithPrecision: A starts 3 days or less before or on day of PointB
define IntervalStartsBeforeOrOnPointWithOffsetOrMoreWithPrecision: A starts 3 days or more before or on day of PointB
define IntervalStartsBeforeOrOnPointWithLessThanOffsetWithPrecision: A starts less than 3 days before or on day of PointB
define IntervalStartsBeforeOrOnPointWithMoreThanOffsetWithPrecision: A starts more than 3 days before or on day of PointB

define IntervalEndsBeforePoint: A ends before PointB
define IntervalEndsBeforePointWithPrecision: A ends before day of PointB
define IntervalEndsBeforePointWithOffset: A ends 3 days before PointB
define IntervalEndsBeforePointWithOffsetOrLess: A ends 3 days or less before PointB
define IntervalEndsBeforePointWithOffsetOrMore: A ends 3 days or more before PointB
define IntervalEndsBeforePointWithLessThanOffset: A ends less than 3 days before PointB
define IntervalEndsBeforePointWithMoreThanOffset: A ends more than 3 days before PointB
define IntervalEndsBeforePointWithOffsetWithPrecision: A ends 3 days before day of PointB
define IntervalEndsBeforePointWithOffsetOrLessWithPrecision: A ends 3 days or less before day of PointB
define IntervalEndsBeforePointWithOffsetOrMoreWithPrecision: A ends 3 days or more before day of PointB
define IntervalEndsBeforePointWithLessThanOffsetWithPrecision: A ends less than 3 days before day of PointB
define IntervalEndsBeforePointWithMoreThanOffsetWithPrecision: A ends more than 3 days before day of PointB

define IntervalEndsOnOrBeforePoint: A ends on or before PointB
define IntervalEndsOnOrBeforePointWithPrecision: A ends on or before day of PointB
define IntervalEndsOnOrBeforePointWithOffset: A ends 3 days on or before PointB
define IntervalEndsOnOrBeforePointWithOffsetOrLess: A ends 3 days or less on or before PointB
define IntervalEndsOnOrBeforePointWithOffsetOrMore: A ends 3 days or more on or before PointB
define IntervalEndsOnOrBeforePointWithLessThanOffset: A ends less than 3 days on or before PointB
define IntervalEndsOnOrBeforePointWithMoreThanOffset: A ends more than 3 days on or before PointB
define IntervalEndsOnOrBeforePointWithOffsetWithPrecision: A ends 3 days on or before day of PointB
define IntervalEndsOnOrBeforePointWithOffsetOrLessWithPrecision: A ends 3 days or less on or before day of PointB
define IntervalEndsOnOrBeforePointWithOffsetOrMoreWithPrecision: A ends 3 days or more on or before day of PointB
define IntervalEndsOnOrBeforePointWithLessThanOffsetWithPrecision: A ends less than 3 days on or before day of PointB
define IntervalEndsOnOrBeforePointWithMoreThanOffsetWithPrecision: A ends more than 3 days on or before day of PointB

define IntervalEndsBeforeOrOnPoint: A ends before or on PointB
define IntervalEndsBeforeOrOnPointWithPrecision: A ends before or on day of PointB
define IntervalEndsBeforeOrOnPointWithOffset: A ends 3 days before or on PointB
define IntervalEndsBeforeOrOnPointWithOffsetOrLess: A ends 3 days or less before or on PointB
define IntervalEndsBeforeOrOnPointWithOffsetOrMore: A ends 3 days or more before or on PointB
define IntervalEndsBeforeOrOnPointWithLessThanOffset: A ends less than 3 days before or on PointB
define IntervalEndsBeforeOrOnPointWithMoreThanOffset: A ends more than 3 days before or on PointB
define IntervalEndsBeforeOrOnPointWithOffsetWithPrecision: A ends 3 days before or on day of PointB
define IntervalEndsBeforeOrOnPointWithOffsetOrLessWithPrecision: A ends 3 days or less before or on day of PointB
define IntervalEndsBeforeOrOnPointWithOffsetOrMoreWithPrecision: A ends 3 days or more before or on day of PointB
define IntervalEndsBeforeOrOnPointWithLessThanOffsetWithPrecision: A ends less than 3 days before or on day of PointB
define IntervalEndsBeforeOrOnPointWithMoreThanOffsetWithPrecision: A ends more than 3 days before or on day of PointB

define IntervalOccursBeforePoint: A occurs before PointB
define IntervalOccursBeforePointWithPrecision: A occurs before day of PointB
define IntervalOccursBeforePointWithOffset: A occurs 3 days before PointB
define IntervalOccursBeforePointWithOffsetOrLess: A occurs 3 days or less before PointB
define IntervalOccursBeforePointWithOffsetOrMore: A occurs 3 days or more before PointB
define IntervalOccursBeforePointWithLessThanOffset: A occurs less than 3 days before PointB
define IntervalOccursBeforePointWithMoreThanOffset: A occurs more than 3 days before PointB
define IntervalOccursBeforePointWithOffsetWithPrecision: A occurs 3 days before day of PointB
define IntervalOccursBeforePointWithOffsetOrLessWithPrecision: A occurs 3 days or less before day of PointB
define IntervalOccursBeforePointWithOffsetOrMoreWithPrecision: A occurs 3 days or more before day of PointB
define IntervalOccursBeforePointWithLessThanOffsetWithPrecision: A occurs less than 3 days before day of PointB
define IntervalOccursBeforePointWithMoreThanOffsetWithPrecision: A occurs more than 3 days before day of PointB

define IntervalOccursOnOrBeforePoint: A occurs on or before PointB
define IntervalOccursOnOrBeforePointWithPrecision: A occurs on or before day of PointB
define IntervalOccursOnOrBeforePointWithOffset: A occurs 3 days on or before PointB
define IntervalOccursOnOrBeforePointWithOffsetOrLess: A occurs 3 days or less on or before PointB
define IntervalOccursOnOrBeforePointWithOffsetOrMore: A occurs 3 days or more on or before PointB
define IntervalOccursOnOrBeforePointWithLessThanOffset: A occurs less than 3 days on or before PointB
define IntervalOccursOnOrBeforePointWithMoreThanOffset: A occurs more than 3 days on or before PointB
define IntervalOccursOnOrBeforePointWithOffsetWithPrecision: A occurs 3 days on or before day of PointB
define IntervalOccursOnOrBeforePointWithOffsetOrLessWithPrecision: A occurs 3 days or less on or before day of PointB
define IntervalOccursOnOrBeforePointWithOffsetOrMoreWithPrecision: A occurs 3 days or more on or before day of PointB
define IntervalOccursOnOrBeforePointWithLessThanOffsetWithPrecision: A occurs less than 3 days on or before day of PointB
define IntervalOccursOnOrBeforePointWithMoreThanOffsetWithPrecision: A occurs more than 3 days on or before day of PointB

define IntervalOccursBeforeOrOnPoint: A occurs before or on PointB
define IntervalOccursBeforeOrOnPointWithPrecision: A occurs before or on day of PointB
define IntervalOccursBeforeOrOnPointWithOffset: A occurs 3 days before or on PointB
define IntervalOccursBeforeOrOnPointWithOffsetOrLess: A occurs 3 days or less before or on PointB
define IntervalOccursBeforeOrOnPointWithOffsetOrMore: A occurs 3 days or more before or on PointB
define IntervalOccursBeforeOrOnPointWithLessThanOffset: A occurs less than 3 days before or on PointB
define IntervalOccursBeforeOrOnPointWithMoreThanOffset: A occurs more than 3 days before or on PointB
define IntervalOccursBeforeOrOnPointWithOffsetWithPrecision: A occurs 3 days before or on day of PointB
define IntervalOccursBeforeOrOnPointWithOffsetOrLessWithPrecision: A occurs 3 days or less before or on day of PointB
define IntervalOccursBeforeOrOnPointWithOffsetOrMoreWithPrecision: A occurs 3 days or more before or on day of PointB
define IntervalOccursBeforeOrOnPointWithLessThanOffsetWithPrecision: A occurs less than 3 days before or on day of PointB
define IntervalOccursBeforeOrOnPointWithMoreThanOffsetWithPrecision: A occurs more than 3 days before or on day of PointB

define IntervalStartsBefore: A starts before B
define IntervalStartsBeforeWithPrecision: A starts before day of B
define IntervalStartsBeforeWithOffset: A starts 3 days before B
define IntervalStartsBeforeWithOffsetOrLess: A starts 3 days or less before B
define IntervalStartsBeforeWithOffsetOrMore: A starts 3 days or more before B
define IntervalStartsBeforeWithLessThanOffset: A starts less than 3 days before B
define IntervalStartsBeforeWithMoreThanOffset: A starts more than 3 days before B
define IntervalStartsBeforeWithOffsetWithPrecision: A starts 3 days before day of B
define IntervalStartsBeforeWithOffsetOrLessWithPrecision: A starts 3 days or less before day of B
define IntervalStartsBeforeWithOffsetOrMoreWithPrecision: A starts 3 days or more before day of B
define IntervalStartsBeforeWithLessThanOffsetWithPrecision: A starts less than 3 days before day of B
define IntervalStartsBeforeWithMoreThanOffsetWithPrecision: A starts more than 3 days before day of B

define IntervalStartsOnOrBefore: A starts on or before B
define IntervalStartsOnOrBeforeWithPrecision: A starts on or before day of B
define IntervalStartsOnOrBeforeWithOffset: A starts 3 days on or before B
define IntervalStartsOnOrBeforeWithOffsetOrLess: A starts 3 days or less on or before B
define IntervalStartsOnOrBeforeWithOffsetOrMore: A starts 3 days or more on or before B
define IntervalStartsOnOrBeforeWithLessThanOffset: A starts less than 3 days on or before B
define IntervalStartsOnOrBeforeWithMoreThanOffset: A starts more than 3 days on or before B
define IntervalStartsOnOrBeforeWithOffsetWithPrecision: A starts 3 days on or before day of B
define IntervalStartsOnOrBeforeWithOffsetOrLessWithPrecision: A starts 3 days or less on or before day of B
define IntervalStartsOnOrBeforeWithOffsetOrMoreWithPrecision: A starts 3 days or more on or before day of B
define IntervalStartsOnOrBeforeWithLessThanOffsetWithPrecision: A starts less than 3 days on or before day of B
define IntervalStartsOnOrBeforeWithMoreThanOffsetWithPrecision: A starts more than 3 days on or before day of B

define IntervalStartsBeforeOrOn: A starts before or on B
define IntervalStartsBeforeOrOnWithPrecision: A starts before or on day of B
define IntervalStartsBeforeOrOnWithOffset: A starts 3 days before or on B
define IntervalStartsBeforeOrOnWithOffsetOrLess: A starts 3 days or less before or on B
define IntervalStartsBeforeOrOnWithOffsetOrMore: A starts 3 days or more before or on B
define IntervalStartsBeforeOrOnWithLessThanOffset: A starts less than 3 days before or on B
define IntervalStartsBeforeOrOnWithMoreThanOffset: A starts more than 3 days before or on B
define IntervalStartsBeforeOrOnWithOffsetWithPrecision: A starts 3 days before or on day of B
define IntervalStartsBeforeOrOnWithOffsetOrLessWithPrecision: A starts 3 days or less before or on day of B
define IntervalStartsBeforeOrOnWithOffsetOrMoreWithPrecision: A starts 3 days or more before or on day of B
define IntervalStartsBeforeOrOnWithLessThanOffsetWithPrecision: A starts less than 3 days before or on day of B
define IntervalStartsBeforeOrOnWithMoreThanOffsetWithPrecision: A starts more than 3 days before or on day of B

define IntervalStartsBeforeStart: A starts before start B
define IntervalStartsBeforeStartWithPrecision: A starts before day of start B
define IntervalStartsBeforeStartWithOffset: A starts 3 days before start B
define IntervalStartsBeforeStartWithOffsetOrLess: A starts 3 days or less before start B
define IntervalStartsBeforeStartWithOffsetOrMore: A starts 3 days or more before start B
define IntervalStartsBeforeStartWithLessThanOffset: A starts less than 3 days before start B
define IntervalStartsBeforeStartWithMoreThanOffset: A starts more than 3 days before start B
define IntervalStartsBeforeStartWithOffsetWithPrecision: A starts 3 days before day of start B
define IntervalStartsBeforeStartWithOffsetOrLessWithPrecision: A starts 3 days or less before day of start B
define IntervalStartsBeforeStartWithOffsetOrMoreWithPrecision: A starts 3 days or more before day of start B
define IntervalStartsBeforeStartWithLessThanOffsetWithPrecision: A starts less than 3 days before day of start B
define IntervalStartsBeforeStartWithMoreThanOffsetWithPrecision: A starts more than 3 days before day of start B

define IntervalStartsOnOrBeforeStart: A starts on or before start B
define IntervalStartsOnOrBeforeStartWithPrecision: A starts on or before day of start B
define IntervalStartsOnOrBeforeStartWithOffset: A starts 3 days on or before start B
define IntervalStartsOnOrBeforeStartWithOffsetOrLess: A starts 3 days or less on or before start B
define IntervalStartsOnOrBeforeStartWithOffsetOrMore: A starts 3 days or more on or before start B
define IntervalStartsOnOrBeforeStartWithLessThanOffset: A starts less than 3 days on or before start B
define IntervalStartsOnOrBeforeStartWithMoreThanOffset: A starts more than 3 days on or before start B
define IntervalStartsOnOrBeforeStartWithOffsetWithPrecision: A starts 3 days on or before day of start B
define IntervalStartsOnOrBeforeStartWithOffsetOrLessWithPrecision: A starts 3 days or less on or before day of start B
define IntervalStartsOnOrBeforeStartWithOffsetOrMoreWithPrecision: A starts 3 days or more on or before day of start B
define IntervalStartsOnOrBeforeStartWithLessThanOffsetWithPrecision: A starts less than 3 days on or before day of start B
define IntervalStartsOnOrBeforeStartWithMoreThanOffsetWithPrecision: A starts more than 3 days on or before day of start B

define IntervalStartsBeforeOrOnStart: A starts before or on start B
define IntervalStartsBeforeOrOnStartWithPrecision: A starts before or on day of start B
define IntervalStartsBeforeOrOnStartWithOffset: A starts 3 days before or on start B
define IntervalStartsBeforeOrOnStartWithOffsetOrLess: A starts 3 days or less before or on start B
define IntervalStartsBeforeOrOnStartWithOffsetOrMore: A starts 3 days or more before or on start B
define IntervalStartsBeforeOrOnStartWithLessThanOffset: A starts less than 3 days before or on start B
define IntervalStartsBeforeOrOnStartWithMoreThanOffset: A starts more than 3 days before or on start B
define IntervalStartsBeforeOrOnStartWithOffsetWithPrecision: A starts 3 days before or on day of start B
define IntervalStartsBeforeOrOnStartWithOffsetOrLessWithPrecision: A starts 3 days or less before or on day of start B
define IntervalStartsBeforeOrOnStartWithOffsetOrMoreWithPrecision: A starts 3 days or more before or on day of start B
define IntervalStartsBeforeOrOnStartWithLessThanOffsetWithPrecision: A starts less than 3 days before or on day of start B
define IntervalStartsBeforeOrOnStartWithMoreThanOffsetWithPrecision: A starts more than 3 days before or on day of start B

define IntervalStartsBeforeEnd: A starts before end B
define IntervalStartsBeforeEndWithPrecision: A starts before day of end B
define IntervalStartsBeforeEndWithOffset: A starts 3 days before end B
define IntervalStartsBeforeEndWithOffsetOrLess: A starts 3 days or less before end B
define IntervalStartsBeforeEndWithOffsetOrMore: A starts 3 days or more before end B
define IntervalStartsBeforeEndWithLessThanOffset: A starts less than 3 days before end B
define IntervalStartsBeforeEndWithMoreThanOffset: A starts more than 3 days before end B
define IntervalStartsBeforeEndWithOffsetWithPrecision: A starts 3 days before day of end B
define IntervalStartsBeforeEndWithOffsetOrLessWithPrecision: A starts 3 days or less before day of end B
define IntervalStartsBeforeEndWithOffsetOrMoreWithPrecision: A starts 3 days or more before day of end B
define IntervalStartsBeforeEndWithLessThanOffsetWithPrecision: A starts less than 3 days before day of end B
define IntervalStartsBeforeEndWithMoreThanOffsetWithPrecision: A starts more than 3 days before day of end B

define IntervalStartsOnOrBeforeEnd: A starts on or before end B
define IntervalStartsOnOrBeforeEndWithPrecision: A starts on or before day of end B
define IntervalStartsOnOrBeforeEndWithOffset: A starts 3 days on or before end B
define IntervalStartsOnOrBeforeEndWithOffsetOrLess: A starts 3 days or less on or before end B
define IntervalStartsOnOrBeforeEndWithOffsetOrMore: A starts 3 days or more on or before end B
define IntervalStartsOnOrBeforeEndWithLessThanOffset: A starts less than 3 days on or before end B
define IntervalStartsOnOrBeforeEndWithMoreThanOffset: A starts more than 3 days on or before end B
define IntervalStartsOnOrBeforeEndWithOffsetWithPrecision: A starts 3 days on or before day of end B
define IntervalStartsOnOrBeforeEndWithOffsetOrLessWithPrecision: A starts 3 days or less on or before day of end B
define IntervalStartsOnOrBeforeEndWithOffsetOrMoreWithPrecision: A starts 3 days or more on or before day of end B
define IntervalStartsOnOrBeforeEndWithLessThanOffsetWithPrecision: A starts less than 3 days on or before day of end B
define IntervalStartsOnOrBeforeEndWithMoreThanOffsetWithPrecision: A starts more than 3 days on or before day of end B

define IntervalStartsBeforeOrOnEnd: A starts before or on end B
define IntervalStartsBeforeOrOnEndWithPrecision: A starts before or on day of end B
define IntervalStartsBeforeOrOnEndWithOffset: A starts 3 days before or on end B
define IntervalStartsBeforeOrOnEndWithOffsetOrLess: A starts 3 days or less before or on end B
define IntervalStartsBeforeOrOnEndWithOffsetOrMore: A starts 3 days or more before or on end B
define IntervalStartsBeforeOrOnEndWithLessThanOffset: A starts less than 3 days before or on end B
define IntervalStartsBeforeOrOnEndWithMoreThanOffset: A starts more than 3 days before or on end B
define IntervalStartsBeforeOrOnEndWithOffsetWithPrecision: A starts 3 days before or on day of end B
define IntervalStartsBeforeOrOnEndWithOffsetOrLessWithPrecision: A starts 3 days or less before or on day of end B
define IntervalStartsBeforeOrOnEndWithOffsetOrMoreWithPrecision: A starts 3 days or more before or on day of end B
define IntervalStartsBeforeOrOnEndWithLessThanOffsetWithPrecision: A starts less than 3 days before or on day of end B
define IntervalStartsBeforeOrOnEndWithMoreThanOffsetWithPrecision: A starts more than 3 days before or on day of end B

define IntervalEndsBeforeWithPrecision: A ends before day of B
define IntervalEndsBeforeWithOffset: A ends 3 days before B
define IntervalEndsBeforeWithOffsetOrLess: A ends 3 days or less before B
define IntervalEndsBeforeWithOffsetOrMore: A ends 3 days or more before B
define IntervalEndsBeforeWithLessThanOffset: A ends less than 3 days before B
define IntervalEndsBeforeWithMoreThanOffset: A ends more than 3 days before B
define IntervalEndsBeforeWithOffsetWithPrecision: A ends 3 days before day of B
define IntervalEndsBeforeWithOffsetOrLessWithPrecision: A ends 3 days or less before day of B
define IntervalEndsBeforeWithOffsetOrMoreWithPrecision: A ends 3 days or more before day of B
define IntervalEndsBeforeWithLessThanOffsetWithPrecision: A ends less than 3 days before day of B
define IntervalEndsBeforeWithMoreThanOffsetWithPrecision: A ends more than 3 days before day of B

define IntervalEndsOnOrBeforeWithPrecision: A ends on or before day of B
define IntervalEndsOnOrBeforeWithOffset: A ends 3 days on or before B
define IntervalEndsOnOrBeforeWithOffsetOrLess: A ends 3 days or less on or before B
define IntervalEndsOnOrBeforeWithOffsetOrMore: A ends 3 days or more on or before B
define IntervalEndsOnOrBeforeWithLessThanOffset: A ends less than 3 days on or before B
define IntervalEndsOnOrBeforeWithMoreThanOffset: A ends more than 3 days on or before B
define IntervalEndsOnOrBeforeWithOffsetWithPrecision: A ends 3 days on or before day of B
define IntervalEndsOnOrBeforeWithOffsetOrLessWithPrecision: A ends 3 days or less on or before day of B
define IntervalEndsOnOrBeforeWithOffsetOrMoreWithPrecision: A ends 3 days or more on or before day of B
define IntervalEndsOnOrBeforeWithLessThanOffsetWithPrecision: A ends less than 3 days on or before day of B
define IntervalEndsOnOrBeforeWithMoreThanOffsetWithPrecision: A ends more than 3 days on or before day of B

define IntervalEndsBeforeOrOnWithPrecision: A ends before or on day of B
define IntervalEndsBeforeOrOnWithOffset: A ends 3 days before or on B
define IntervalEndsBeforeOrOnWithOffsetOrLess: A ends 3 days or less before or on B
define IntervalEndsBeforeOrOnWithOffsetOrMore: A ends 3 days or more before or on B
define IntervalEndsBeforeOrOnWithLessThanOffset: A ends less than 3 days before or on B
define IntervalEndsBeforeOrOnWithMoreThanOffset: A ends more than 3 days before or on B
define IntervalEndsBeforeOrOnWithOffsetWithPrecision: A ends 3 days before or on day of B
define IntervalEndsBeforeOrOnWithOffsetOrLessWithPrecision: A ends 3 days or less before or on day of B
define IntervalEndsBeforeOrOnWithOffsetOrMoreWithPrecision: A ends 3 days or more before or on day of B
define IntervalEndsBeforeOrOnWithLessThanOffsetWithPrecision: A ends less than 3 days before or on day of B
define IntervalEndsBeforeOrOnWithMoreThanOffsetWithPrecision: A ends more than 3 days before or on day of B

define IntervalEndsBefore: A ends before B
define IntervalEndsBeforeStart: A ends before start B
define IntervalEndsBeforeStartWithPrecision: A ends before day of start B
define IntervalEndsBeforeStartWithOffset: A ends 3 days before start B
define IntervalEndsBeforeStartWithOffsetOrLess: A ends 3 days or less before start B
define IntervalEndsBeforeStartWithOffsetOrMore: A ends 3 days or more before start B
define IntervalEndsBeforeStartWithLessThanOffset: A ends less than 3 days before start B
define IntervalEndsBeforeStartWithMoreThanOffset: A ends more than 3 days before start B
define IntervalEndsBeforeStartWithOffsetWithPrecision: A ends 3 days before day of start B
define IntervalEndsBeforeStartWithOffsetOrLessWithPrecision: A ends 3 days or less before day of start B
define IntervalEndsBeforeStartWithOffsetOrMoreWithPrecision: A ends 3 days or more before day of start B
define IntervalEndsBeforeStartWithLessThanOffsetWithPrecision: A ends less than 3 days before day of start B
define IntervalEndsBeforeStartWithMoreThanOffsetWithPrecision: A ends more than 3 days before day of start B

define IntervalEndsOnOrBefore: A ends on or before B
define IntervalEndsOnOrBeforeStart: A ends on or before start B
define IntervalEndsOnOrBeforeStartWithPrecision: A ends on or before day of start B
define IntervalEndsOnOrBeforeStartWithOffset: A ends 3 days on or before start B
define IntervalEndsOnOrBeforeStartWithOffsetOrLess: A ends 3 days or less on or before start B
define IntervalEndsOnOrBeforeStartWithOffsetOrMore: A ends 3 days or more on or before start B
define IntervalEndsOnOrBeforeStartWithLessThanOffset: A ends less than 3 days on or before start B
define IntervalEndsOnOrBeforeStartWithMoreThanOffset: A ends more than 3 days on or before start B
define IntervalEndsOnOrBeforeStartWithOffsetWithPrecision: A ends 3 days on or before day of start B
define IntervalEndsOnOrBeforeStartWithOffsetOrLessWithPrecision: A ends 3 days or less on or before day of start B
define IntervalEndsOnOrBeforeStartWithOffsetOrMoreWithPrecision: A ends 3 days or more on or before day of start B
define IntervalEndsOnOrBeforeStartWithLessThanOffsetWithPrecision: A ends less than 3 days on or before day of start B
define IntervalEndsOnOrBeforeStartWithMoreThanOffsetWithPrecision: A ends more than 3 days on or before day of start B

define IntervalEndsBeforeOrOn: A ends before or on B
define IntervalEndsBeforeOrOnStart: A ends before or on start B
define IntervalEndsBeforeOrOnStartWithPrecision: A ends before or on day of start B
define IntervalEndsBeforeOrOnStartWithOffset: A ends 3 days before or on start B
define IntervalEndsBeforeOrOnStartWithOffsetOrLess: A ends 3 days or less before or on start B
define IntervalEndsBeforeOrOnStartWithOffsetOrMore: A ends 3 days or more before or on start B
define IntervalEndsBeforeOrOnStartWithLessThanOffset: A ends less than 3 days before or on start B
define IntervalEndsBeforeOrOnStartWithMoreThanOffset: A ends more than 3 days before or on start B
define IntervalEndsBeforeOrOnStartWithOffsetWithPrecision: A ends 3 days before or on day of start B
define IntervalEndsBeforeOrOnStartWithOffsetOrLessWithPrecision: A ends 3 days or less before or on day of start B
define IntervalEndsBeforeOrOnStartWithOffsetOrMoreWithPrecision: A ends 3 days or more before or on day of start B
define IntervalEndsBeforeOrOnStartWithLessThanOffsetWithPrecision: A ends less than 3 days before or on day of start B
define IntervalEndsBeforeOrOnStartWithMoreThanOffsetWithPrecision: A ends more than 3 days before or on day of start B

define IntervalEndsBeforeEnd: A ends before end B
define IntervalEndsBeforeEndWithPrecision: A ends before day of end B
define IntervalEndsBeforeEndWithOffset: A ends 3 days before end B
define IntervalEndsBeforeEndWithOffsetOrLess: A ends 3 days or less before end B
define IntervalEndsBeforeEndWithOffsetOrMore: A ends 3 days or more before end B
define IntervalEndsBeforeEndWithLessThanOffset: A ends less than 3 days before end B
define IntervalEndsBeforeEndWithMoreThanOffset: A ends more than 3 days before end B
define IntervalEndsBeforeEndWithOffsetWithPrecision: A ends 3 days before day of end B
define IntervalEndsBeforeEndWithOffsetOrLessWithPrecision: A ends 3 days or less before day of end B
define IntervalEndsBeforeEndWithOffsetOrMoreWithPrecision: A ends 3 days or more before day of end B
define IntervalEndsBeforeEndWithLessThanOffsetWithPrecision: A ends less than 3 days before day of end B
define IntervalEndsBeforeEndWithMoreThanOffsetWithPrecision: A ends more than 3 days before day of end B

define IntervalEndsOnOrBeforeEnd: A ends on or before end B
define IntervalEndsOnOrBeforeEndWithPrecision: A ends on or before day of end B
define IntervalEndsOnOrBeforeEndWithOffset: A ends 3 days on or before end B
define IntervalEndsOnOrBeforeEndWithOffsetOrLess: A ends 3 days or less on or before end B
define IntervalEndsOnOrBeforeEndWithOffsetOrMore: A ends 3 days or more on or before end B
define IntervalEndsOnOrBeforeEndWithLessThanOffset: A ends less than 3 days on or before end B
define IntervalEndsOnOrBeforeEndWithMoreThanOffset: A ends more than 3 days on or before end B
define IntervalEndsOnOrBeforeEndWithOffsetWithPrecision: A ends 3 days on or before day of end B
define IntervalEndsOnOrBeforeEndWithOffsetOrLessWithPrecision: A ends 3 days or less on or before day of end B
define IntervalEndsOnOrBeforeEndWithOffsetOrMoreWithPrecision: A ends 3 days or more on or before day of end B
define IntervalEndsOnOrBeforeEndWithLessThanOffsetWithPrecision: A ends less than 3 days on or before day of end B
define IntervalEndsOnOrBeforeEndWithMoreThanOffsetWithPrecision: A ends more than 3 days on or before day of end B

define IntervalEndsBeforeOrOnEnd: A ends before or on end B
define IntervalEndsBeforeOrOnEndWithPrecision: A ends before or on day of end B
define IntervalEndsBeforeOrOnEndWithOffset: A ends 3 days before or on end B
define IntervalEndsBeforeOrOnEndWithOffsetOrLess: A ends 3 days or less before or on end B
define IntervalEndsBeforeOrOnEndWithOffsetOrMore: A ends 3 days or more before or on end B
define IntervalEndsBeforeOrOnEndWithLessThanOffset: A ends less than 3 days before or on end B
define IntervalEndsBeforeOrOnEndWithMoreThanOffset: A ends more than 3 days before or on end B
define IntervalEndsBeforeOrOnEndWithOffsetWithPrecision: A ends 3 days before or on day of end B
define IntervalEndsBeforeOrOnEndWithOffsetOrLessWithPrecision: A ends 3 days or less before or on day of end B
define IntervalEndsBeforeOrOnEndWithOffsetOrMoreWithPrecision: A ends 3 days or more before or on day of end B
define IntervalEndsBeforeOrOnEndWithLessThanOffsetWithPrecision: A ends less than 3 days before or on day of end B
define IntervalEndsBeforeOrOnEndWithMoreThanOffsetWithPrecision: A ends more than 3 days before or on day of end B

define IntervalOccursBefore: A occurs before B
define IntervalOccursBeforeWithPrecision: A occurs before day of B
define IntervalOccursBeforeWithOffset: A occurs 3 days before B
define IntervalOccursBeforeWithOffsetOrLess: A occurs 3 days or less before B
define IntervalOccursBeforeWithOffsetOrMore: A occurs 3 days or more before B
define IntervalOccursBeforeWithLessThanOffset: A occurs less than 3 days before B
define IntervalOccursBeforeWithMoreThanOffset: A occurs more than 3 days before B
define IntervalOccursBeforeWithOffsetWithPrecision: A occurs 3 days before day of B
define IntervalOccursBeforeWithOffsetOrLessWithPrecision: A occurs 3 days or less before day of B
define IntervalOccursBeforeWithOffsetOrMoreWithPrecision: A occurs 3 days or more before day of B
define IntervalOccursBeforeWithLessThanOffsetWithPrecision: A occurs less than 3 days before day of B
define IntervalOccursBeforeWithMoreThanOffsetWithPrecision: A occurs more than 3 days before day of B

define IntervalOccursOnOrBefore: A occurs on or before B
define IntervalOccursOnOrBeforeWithPrecision: A occurs on or before day of B
define IntervalOccursOnOrBeforeWithOffset: A occurs 3 days on or before B
define IntervalOccursOnOrBeforeWithOffsetOrLess: A occurs 3 days or less on or before B
define IntervalOccursOnOrBeforeWithOffsetOrMore: A occurs 3 days or more on or before B
define IntervalOccursOnOrBeforeWithLessThanOffset: A occurs less than 3 days on or before B
define IntervalOccursOnOrBeforeWithMoreThanOffset: A occurs more than 3 days on or before B
define IntervalOccursOnOrBeforeWithOffsetWithPrecision: A occurs 3 days on or before day of B
define IntervalOccursOnOrBeforeWithOffsetOrLessWithPrecision: A occurs 3 days or less on or before day of B
define IntervalOccursOnOrBeforeWithOffsetOrMoreWithPrecision: A occurs 3 days or more on or before day of B
define IntervalOccursOnOrBeforeWithLessThanOffsetWithPrecision: A occurs less than 3 days on or before day of B
define IntervalOccursOnOrBeforeWithMoreThanOffsetWithPrecision: A occurs more than 3 days on or before day of B

define IntervalOccursBeforeOrOn: A occurs before or on B
define IntervalOccursBeforeOrOnWithPrecision: A occurs before or on day of B
define IntervalOccursBeforeOrOnWithOffset: A occurs 3 days before or on B
define IntervalOccursBeforeOrOnWithOffsetOrLess: A occurs 3 days or less before or on B
define IntervalOccursBeforeOrOnWithOffsetOrMore: A occurs 3 days or more before or on B
define IntervalOccursBeforeOrOnWithLessThanOffset: A occurs less than 3 days before or on B
define IntervalOccursBeforeOrOnWithMoreThanOffset: A occurs more than 3 days before or on B
define IntervalOccursBeforeOrOnWithOffsetWithPrecision: A occurs 3 days before or on day of B
define IntervalOccursBeforeOrOnWithOffsetOrLessWithPrecision: A occurs 3 days or less before or on day of B
define IntervalOccursBeforeOrOnWithOffsetOrMoreWithPrecision: A occurs 3 days or more before or on day of B
define IntervalOccursBeforeOrOnWithLessThanOffsetWithPrecision: A occurs less than 3 days before or on day of B
define IntervalOccursBeforeOrOnWithMoreThanOffsetWithPrecision: A occurs more than 3 days before or on day of B

define IntervalOccursBeforeStart: A occurs before start B
define IntervalOccursBeforeStartWithPrecision: A occurs before day of start B
define IntervalOccursBeforeStartWithOffset: A occurs 3 days before start B
define IntervalOccursBeforeStartWithOffsetOrLess: A occurs 3 days or less before start B
define IntervalOccursBeforeStartWithOffsetOrMore: A occurs 3 days or more before start B
define IntervalOccursBeforeStartWithLessThanOffset: A occurs less than 3 days before start B
define IntervalOccursBeforeStartWithMoreThanOffset: A occurs more than 3 days before start B
define IntervalOccursBeforeStartWithOffsetWithPrecision: A occurs 3 days before day of start B
define IntervalOccursBeforeStartWithOffsetOrLessWithPrecision: A occurs 3 days or less before day of start B
define IntervalOccursBeforeStartWithOffsetOrMoreWithPrecision: A occurs 3 days or more before day of start B
define IntervalOccursBeforeStartWithLessThanOffsetWithPrecision: A occurs less than 3 days before day of start B
define IntervalOccursBeforeStartWithMoreThanOffsetWithPrecision: A occurs more than 3 days before day of start B

define IntervalOccursOnOrBeforeStart: A occurs on or before start B
define IntervalOccursOnOrBeforeStartWithPrecision: A occurs on or before day of start B
define IntervalOccursOnOrBeforeStartWithOffset: A occurs 3 days on or before start B
define IntervalOccursOnOrBeforeStartWithOffsetOrLess: A occurs 3 days or less on or before start B
define IntervalOccursOnOrBeforeStartWithOffsetOrMore: A occurs 3 days or more on or before start B
define IntervalOccursOnOrBeforeStartWithLessThanOffset: A occurs less than 3 days on or before start B
define IntervalOccursOnOrBeforeStartWithMoreThanOffset: A occurs more than 3 days on or before start B
define IntervalOccursOnOrBeforeStartWithOffsetWithPrecision: A occurs 3 days on or before day of start B
define IntervalOccursOnOrBeforeStartWithOffsetOrLessWithPrecision: A occurs 3 days or less on or before day of start B
define IntervalOccursOnOrBeforeStartWithOffsetOrMoreWithPrecision: A occurs 3 days or more on or before day of start B
define IntervalOccursOnOrBeforeStartWithLessThanOffsetWithPrecision: A occurs less than 3 days on or before day of start B
define IntervalOccursOnOrBeforeStartWithMoreThanOffsetWithPrecision: A occurs more than 3 days on or before day of start B

define IntervalOccursBeforeOrOnStart: A occurs before or on start B
define IntervalOccursBeforeOrOnStartWithPrecision: A occurs before or on day of start B
define IntervalOccursBeforeOrOnStartWithOffset: A occurs 3 days before or on start B
define IntervalOccursBeforeOrOnStartWithOffsetOrLess: A occurs 3 days or less before or on start B
define IntervalOccursBeforeOrOnStartWithOffsetOrMore: A occurs 3 days or more before or on start B
define IntervalOccursBeforeOrOnStartWithLessThanOffset: A occurs less than 3 days before or on start B
define IntervalOccursBeforeOrOnStartWithMoreThanOffset: A occurs more than 3 days before or on start B
define IntervalOccursBeforeOrOnStartWithOffsetWithPrecision: A occurs 3 days before or on day of start B
define IntervalOccursBeforeOrOnStartWithOffsetOrLessWithPrecision: A occurs 3 days or less before or on day of start B
define IntervalOccursBeforeOrOnStartWithOffsetOrMoreWithPrecision: A occurs 3 days or more before or on day of start B
define IntervalOccursBeforeOrOnStartWithLessThanOffsetWithPrecision: A occurs less than 3 days before or on day of start B
define IntervalOccursBeforeOrOnStartWithMoreThanOffsetWithPrecision: A occurs more than 3 days before or on day of start B

define IntervalOccursBeforeEnd: A occurs before end B
define IntervalOccursBeforeEndWithPrecision: A occurs before day of end B
define IntervalOccursBeforeEndWithOffset: A occurs 3 days before end B
define IntervalOccursBeforeEndWithOffsetOrLess: A occurs 3 days or less before end B
define IntervalOccursBeforeEndWithOffsetOrMore: A occurs 3 days or more before end B
define IntervalOccursBeforeEndWithLessThanOffset: A occurs less than 3 days before end B
define IntervalOccursBeforeEndWithMoreThanOffset: A occurs more than 3 days before end B
define IntervalOccursBeforeEndWithOffsetWithPrecision: A occurs 3 days before day of end B
define IntervalOccursBeforeEndWithOffsetOrLessWithPrecision: A occurs 3 days or less before day of end B
define IntervalOccursBeforeEndWithOffsetOrMoreWithPrecision: A occurs 3 days or more before day of end B
define IntervalOccursBeforeEndWithLessThanOffsetWithPrecision: A occurs less than 3 days before day of end B
define IntervalOccursBeforeEndWithMoreThanOffsetWithPrecision: A occurs more than 3 days before day of end B

define IntervalOccursOnOrBeforeEnd: A occurs on or before end B
define IntervalOccursOnOrBeforeEndWithPrecision: A occurs on or before day of end B
define IntervalOccursOnOrBeforeEndWithOffset: A occurs 3 days on or before end B
define IntervalOccursOnOrBeforeEndWithOffsetOrLess: A occurs 3 days or less on or before end B
define IntervalOccursOnOrBeforeEndWithOffsetOrMore: A occurs 3 days or more on or before end B
define IntervalOccursOnOrBeforeEndWithLessThanOffset: A occurs less than 3 days on or before end B
define IntervalOccursOnOrBeforeEndWithMoreThanOffset: A occurs more than 3 days on or before end B
define IntervalOccursOnOrBeforeEndWithOffsetWithPrecision: A occurs 3 days on or before day of end B
define IntervalOccursOnOrBeforeEndWithOffsetOrLessWithPrecision: A occurs 3 days or less on or before day of end B
define IntervalOccursOnOrBeforeEndWithOffsetOrMoreWithPrecision: A occurs 3 days or more on or before day of end B
define IntervalOccursOnOrBeforeEndWithLessThanOffsetWithPrecision: A occurs less than 3 days on or before day of end B
define IntervalOccursOnOrBeforeEndWithMoreThanOffsetWithPrecision: A occurs more than 3 days on or before day of end B

define IntervalOccursBeforeOrOnEnd: A occurs before or on end B
define IntervalOccursBeforeOrOnEndWithPrecision: A occurs before or on day of end B
define IntervalOccursBeforeOrOnEndWithOffset: A occurs 3 days before or on end B
define IntervalOccursBeforeOrOnEndWithOffsetOrLess: A occurs 3 days or less before or on end B
define IntervalOccursBeforeOrOnEndWithOffsetOrMore: A occurs 3 days or more before or on end B
define IntervalOccursBeforeOrOnEndWithLessThanOffset: A occurs less than 3 days before or on end B
define IntervalOccursBeforeOrOnEndWithMoreThanOffset: A occurs more than 3 days before or on end B
define IntervalOccursBeforeOrOnEndWithOffsetWithPrecision: A occurs 3 days before or on day of end B
define IntervalOccursBeforeOrOnEndWithOffsetOrLessWithPrecision: A occurs 3 days or less before or on day of end B
define IntervalOccursBeforeOrOnEndWithOffsetOrMoreWithPrecision: A occurs 3 days or more before or on day of end B
define IntervalOccursBeforeOrOnEndWithLessThanOffsetWithPrecision: A occurs less than 3 days before or on day of end B
define IntervalOccursBeforeOrOnEndWithMoreThanOffsetWithPrecision: A occurs more than 3 days before or on day of end B

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

