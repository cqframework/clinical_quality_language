parameter AnyParameter Any

define AnyExpression: null

define AnyTest: AnyParameter = 1

define Add: Add(5, 5)

define test1: 2 in Interval[1, 5]
define test2: (List{2, 7, 9}) X where X in Interval[1, 5]
define test3: (List{2, 7, 9}) X where Interval[1, 5] contains X
define test4: 5 in 5 // Should translate with a list promotion by default

define NegInfBegContainsInt: -7 in Interval[null, 5]
define NegInfBegContainsDate: DateTime(1900, 1, 1, 0, 0, 0, 0) in Interval[null, DateTime(2013, 1, 1, 0, 0, 0, 0))

//define Recursive: Recursive

//define MutuallyRecursiveX: MutuallyRecursiveY
//define MutuallyRecursiveY: MutuallyRecursiveX

//define InvalidCall: Base.Base.Base.Call()

//define function Factorial(X Integer) { return if X = 1 then 1 else X * Factorial(X - 1) }

