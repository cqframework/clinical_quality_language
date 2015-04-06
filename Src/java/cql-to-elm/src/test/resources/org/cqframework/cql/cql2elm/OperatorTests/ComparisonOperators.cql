define IntegerBetweenExpression: 1 between 4 and 5
define RealBetweenExpression: 1.0 between 4.0 and 5.0
define DateTimeBetweenExpression: DateTime(2014, 1, 1) between DateTime(2014, 1, 1) and DateTime(2014, 12, 31)
define TimeBetweenExpression: Time(10, 0, 0, 0) between Time(11, 0, 0, 0) and Time(12, 0, 0, 0)
define StringBetweenExpression: 'Beta' between 'Alpha' and 'Omega'
define QuantityBetweenExpression: 4'g/cm3' between 3'g/cm3' and 5'g/cm3'

define IntegerEqual: 1 = 1
define RealEqual: 1.0 = 1.0
define StringEqual: 'Alpha' = 'Omega'
define DateTimeEqual: DateTime(2014, 1, 1) = DateTime(2014, 1, 1)
define TimeEqual: Time(10, 0, 0) = Time(10, 0, 0)
define QuantityEqual: 5'mm[Hg]' = 5'mm[Hg]'
define IntervalEqual: Interval[1, 10] = Interval[1, 10]
define ListEqual: { 1, 2, 3 } = { 1, 2, 3 }
define TupleEqual: Tuple { Id : 1, Name : 'Joe' } = Tuple { Id : 1, Name : 'Joe' }

define IntegerLess: 4 < 5
define RealLess: 4.0 < 5.0
define StringLess: 'Alpha' < 'Omega'
define DateTimeLess: DateTime(2014, 1, 1) < DateTime(2015, 1, 1)
define TimeLess: Time(10, 0, 0) < Time(11, 0, 0)
define QuantityLess: 4'g/cm3' < 5'g/cm3'

define IntegerLessOrEqual: 4 <= 5
define RealLessOrEqual: 4.0 <= 5.0
define StringLessOrEqual: 'Alpha' <= 'Omega'
define DateTimeLessOrEqual: DateTime(2014, 1, 1) <= DateTime(2015, 1, 1)
define TimeLessOrEqual: Time(10, 0, 0) <= Time(11, 0, 0)
define QuantityLessOrEqual: 4'g/cm3' <= 5'g/cm3'

define IntegerGreater: 4 > 5
define RealGreater: 4.0 > 5.0
define StringGreater: 'Alpha' > 'Omega'
define DateTimeGreater: DateTime(2014, 1, 1) > DateTime(2015, 1, 1)
define TimeGreater: Time(11, 0, 0) > Time(10, 0, 0)
define QuantityGreater: 4'g/cm3' > 5'g/cm3'

define IntegerGreaterOrEqual: 4 >= 5
define RealGreaterOrEqual: 4.0 >= 5.0
define StringGreaterOrEqual: 'Alpha' >= 'Omega'
define DateTimeGreaterOrEqual: DateTime(2014, 1, 1) >= DateTime(2015, 1, 1)
define TimeGreaterOrEqual: Time(11, 0, 0) > Time(10, 0, 0)
define QuantityGreaterOrEqual: 4'g/cm3' >= 5'g/cm3'

define IntegerNotEqual: 1 <> 1
define RealNotEqual: 1.0 <> 1.0
define StringNotEqual: 'Alpha' <> 'Omega'
define DateTimeNotEqual: DateTime(2014, 1, 1) <> DateTime(2014, 1, 1)
define TimeNotEqual: Time(10, 0, 0) <> Time(11, 0, 0)
define QuantityNotEqual: 5'mm[Hg]' <> 5'mm[Hg]'
define IntervalNotEqual: Interval[1, 10] <> Interval[1, 10]
define ListNotEqual: { 1, 2, 3 } <> { 1, 2, 3 }
define TupleNotEqual: Tuple { Id: 1, Name: 'Joe' } <> Tuple { Id: 1, Name: 'Joe' }

define IntegerMatches: 1 matches 1
define RealMatches: 1.0 matches 1.0
define StringMatches: 'Foo' matches 'Foo'
define DateTimeMatches: DateTime(2015) matches DateTime(2015)
define TimeMatches: Time(10) matches Time(10)
define QuantityMatches: 5'mm[Hg]' matches 5'mm[Hg]'
define IntervalMatches: Interval[1, 10] matches Interval[1, 10]
define ListMatches: { 1, 2, 3 } matches { 1, 2, 3 }
define TupleMatches: Tuple { Id: 1, Name: 'Joe' } matches Tuple { Id: 1, Name: 'Joe' }

