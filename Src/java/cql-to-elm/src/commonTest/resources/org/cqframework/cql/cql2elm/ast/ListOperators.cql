library ListOperators version '1.0.0'
using System
context Patient

define IntList: { 1, 2, 3, 4, 5 }
define IntList2: { 3, 4, 5, 6, 7 }

// Set operators
define UnionTest: IntList union IntList2
define IntersectTest: IntList intersect IntList2
define ExceptTest: IntList except IntList2

// Aggregate functions
define FirstTest: First(IntList)
define LastTest: Last(IntList)
define CountTest: Count(IntList)
define SumTest: Sum(IntList)
define MinTest: Min(IntList)
define MaxTest: Max(IntList)
define ModeTest: Mode(IntList)

// List functions
define DistinctTest: distinct IntList
define FlattenTest: Flatten({ IntList, IntList2 })
define IndexOfTest: IndexOf(IntList, 3)
