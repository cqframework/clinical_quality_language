define test1 : 2 in interval[1, 5]
define test2 : (list{2, 7, 9}) X where X in interval[1, 5]
define test3 : (list{2, 7, 9}) X where interval[1, 5] contains X

define NegInfBegContainsInt : -7 in interval[null, 5]
define NegInfBegContainsDate : DateTime(1900, 1, 1, 0, 0, 0, 0) in interval[null, DateTime(2013, 1, 1, 0, 0, 0, 0))

