library ProperlyIncludesTests

// These should all resolve to ProperContains(Interval<Integer>, Integer)
// ProperContains(Interval<Integer>, Integer)
define TestA: Interval[1, null] properly includes null
define TestB: Interval[1, null) properly includes null
define TestC: Interval[null, null] properly includes null
define TestD: Interval(null, null) properly includes null
define TestE: Interval[1, null] properly includes null as Choice<Integer, Interval<Integer>>
