define Add: Add(5, 5)

define test1 : 2 in interval[1, 5]
define test2 : (list{2, 7, 9}) X where X in interval[1, 5]
define test3 : (list{2, 7, 9}) X where interval[1, 5] contains X

define NegInfBegContainsInt : -7 in interval[null, 5]
define NegInfBegContainsDate : DateTime(1900, 1, 1, 0, 0, 0, 0) in interval[null, DateTime(2013, 1, 1, 0, 0, 0, 0))

//define Recursive: Recursive

//define MutuallyRecursiveX: MutuallyRecursiveY
//define MutuallyRecursiveY: MutuallyRecursiveX

//define InvalidCall: Base.Base.Base.Call()

//define function Factorial(X Integer) { return if X = 1 then 1 else X * Factorial(X - 1) }

