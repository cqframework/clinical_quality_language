define PointValue: @2018-01-01T00:00:00.0Z
define IntervalValue: Interval[@2018-01-01T00:00:00.0Z, @2018-01-02T00:00:00.0Z)

define PointBeforeInterval: PointValue before IntervalValue
define PointOnOrBeforeInterval: PointValue on or before IntervalValue
define PointSameOrBeforeInterval: PointValue same or before IntervalValue
define IntervalBeforePoint: IntervalValue before PointValue
define IntervalOnOrBeforePoint: IntervalValue on or before PointValue
define IntervalSameOrBeforePoint: IntervalValue same or before PointValue
define PointAfterInterval: PointValue after IntervalValue
define PointOnOrAfterInterval: PointValue on or after IntervalValue
define PointSameOrAfterInterval: PointValue same or after IntervalValue
define IntervalAfterPoint: IntervalValue after PointValue
define IntervalOnOrAfterPoint: IntervalValue on or after PointValue
define IntervalSameOrAfterPoint: IntervalValue same or after PointValue

// includes/included in|during (but resolves to contains/in)

define PointInInterval: PointValue in IntervalValue
//define PointProperlyInInterval: PointValue properly in IntervalValue // TODO: CQL grammar does not support this, doesn't _really_ need to since you can use the during/includes phrasing, but would be nice
define PointDuringInterval: PointValue during IntervalValue
define PointProperlyDuringInterval: PointValue properly during IntervalValue
define PointIncludedInInterval: PointValue included in IntervalValue
define PointProperlyIncludedInInterval: PointValue properly included in IntervalValue
define IntervalContainsPoint: IntervalValue contains PointValue
//define IntervalProperlyContainsPoint: IntervalValue properly contains PointValue // TODO: CQL grammar does not support this
define IntervalIncludesPoint: IntervalValue includes PointValue
define IntervalProperlyIncludesPoint: IntervalValue properly includes PointValue

