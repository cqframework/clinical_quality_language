library CqlQueryTests version '1'

// #204: Unexpected results
// list [1:1] [A, B, C, D]
// RightShift [4:1] [null, A, B, C]
// LeftShift [9:1] [null, A, B, C]
// LeftShift2 [14:1] [B, C, D, null]
define list:
{ 'A', 'B', 'C', 'D' }

define RightShift:
  list L1
    let L2: list[IndexOf(list, L1) - 1]
  return L2

define LeftShift:
  list L1
    let L2: list[IndexOf(list, L1) + 1]
  return L2

define LeftShift2:
  list L1
    let L3: list[IndexOf(list, L1) + 1]
  return L3

// #152: Without not working with precision between
define dummyA:
  {
    billablePeriod: {
      "start": DateTime(2012,1,1,12,0,0,0),
      "end": DateTime(2012,1,31,23,59,59,999)
    }
  }

define dummyB:
  {
    period: {
      "start": DateTime(2012,1,1,13,0,0,0),
      "end": DateTime(2012,1,1,14,0,0,0)
    }
  }

define ClaimWithQualifiyingPOSWithoutEncounter:
  dummyA C
    without dummyB E
      such that hours between C.billablePeriod."start"
        and E.period."start" <= 24

// #196: Unexpected results
// Multisource [10:1] Index: 0, Size: 0
define "a":
  {
    { code: 1, periods: {Interval[1, 2], Interval[3, 4]} }
  }
define "b":
  {
    { code: 1, periods: {Interval[1, 2], Interval[3, 4]} }
  }

define "Multisource":
from "a" A, "b" B

// More complex scenario
define "aa":
  {
    { code: 1, periods: {Interval[1, 2], Interval[3, 4]} },
    { code: 2, periods: {Interval[1, 2], Interval[3, 4]} }
  }
define "bb":
  {
    { code: 1, periods: {Interval[1, 2], Interval[3, 4]} },
    { code: 11, periods: {Interval[1, 2], Interval[3, 4]} }
  }

define "Complex Multisource":
from "aa" A, "bb" B

define "List 1":
  ({1, 2, 3}) i
    return i

define "List 2":
  ({4, 5, 6}) i
    return i

define "List of Lists":
  { "List 1", "List 2" }

// Issue #208: Could not resolve let expression reference 'renamedIntegerList' in library 'repro_let_tuple'.
define "Let Test Fails":
  "List of Lists" IntegerList
    let renamedIntegerList: (IntegerList i where 1 = 1)
    //let renamedIntegerList: IntegerList
  return renamedIntegerList

define "List 3":
  { 7, 8, 9 }

define "Triple Source Query":
  from "List 1" A, "List 2" B, "List 3" C

define "A":
{ 1 }

define "B":
{ 2 }

define "C":
{ 3 } 

// Issue #218 - Let expression in multi-source query
define "Let Expression in Multi Source Query":
    from "A" A, "B" B
    let sum: A + B
    return sum
 
// Issue #219 - Index out of range on multi-source query
define "Accessing Third Element of Triple Source Query":
    from "A" A, "B" B, "C" C
    return C

