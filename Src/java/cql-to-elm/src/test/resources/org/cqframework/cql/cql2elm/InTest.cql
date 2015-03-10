define test1 = 2 in interval[1, 5]
define test2 = (list{2, 7, 9}) X where X in interval[1, 5]
define test3 = (list{2, 7, 9}) X where interval[1, 5] contains X

//define v = Variance({1,2,3,4,5})
// Could not resolve call to operator Variance with signature (list<System.Integer>).
// This won't work because we don't support implicit conversion for lists...

//define std = StdDev({1,2,3,4,5})
// Could not resolve call to operator StdDev with signature (list<System.Integer>).

//define dev = PopulationStdDev({1,2,3,4,5})
// Could not resolve call to operator PopulationStdDev with signature (list<System.Integer>).

define NegInfBegContainsInt = -7 in interval[null, 5]
// Operator In already has a registration for signature: (System.Integer,interval<System.Integer>).

define NegInfBegContainsDate = DateTime(1900, 1, 1, 0, 0, 0, 0) in interval[null, DateTime(2013, 1, 1, 0, 0, 0, 0))
// Operator In already has a registration for signature: (System.DateTime,interval<System.DateTime>).

define NoSeparator = Combine({ 'abc', 'def', 'ghi', 'jkl' })
// Could not resolve call to operator Combine with signature (list<System.String>).
