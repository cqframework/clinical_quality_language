library CqlTestSuite version '2'

define function TestMessage(condition Boolean, testName String, expected String, result String):
  if condition
    then testName + ' TEST PASSED'
  else Message('TEST SUITE FAILED', true, 'Test ' + testName + ' failed', 'Warning', 'Expected ' + expected + ' Found ' + result)

define function toString(value Integer):
  if value is null
    then 'null'
  else ToString(value)

define Union_OneToTen: {1, 2, 3, 4, 5} union {6, 7, 8, 9, 10}
define Union_OneToFiveOverlapped: {1, 2, 3, 4} union {3, 4, 5}
define Union_OneToFiveOverlappedWithNulls: {1, null, 2, 3, 4} union {3, 4, 5, null}
define Union_Disjoint: {1, 2} union {4, 5}
define Union_NestedToFifteen: {1, 2, 3} union {4, 5, 6} union {7 ,8 , 9} union {10, 11, 12} union {13, 14, 15}
define Union_NullUnion: null union {1, 2, 3}
define Union_UnionNull: {1, 2, 3} union null

// TODO: This fails due to list-distinguised overloads
// define test_Union_OneToTen: TestMessage(Union_OneToTen = {1,2,3,4,5,6,7,8,9,10}, 'Union_OneToTen', toString({1,2,3,4,5,6,7,8,9,10}), toString(Union_OneToTen))
// define test_Union_OneToFiveOverlapped: TestMessage(Union_OneToFiveOverlapped = {1,2,3,4,5}, 'Union_OneToFiveOverlapped', toString({1,2,3,4,5}), toString(Union_OneToFiveOverlapped))
// define test_Union_OneToFiveOverlappedWithNulls: TestMessage(Union_OneToFiveOverlappedWithNulls ~ {1,null,2,3,4,5}, 'Union_OneToFiveOverlappedWithNulls', toString({1,null,2,3,4,5}), toString(Union_OneToFiveOverlappedWithNulls))
// define test_Union_Disjoint: TestMessage(Union_Disjoint = {1,2,4,5}, 'Union_Disjoint', toString({1,2,4,5}), toString(Union_Disjoint))
define test_Union_NestedToFifteen: TestMessage(Union_NestedToFifteen = {1,2,3,4,5,6,7,8,9,10,11,12,13,14,15}, 'Union_NestedToFifteen', toString({1,2,3,4,5,6,7,8,9,10,11,12,13,14,15}), toString(Union_NestedToFifteen))
define test_Union_NullUnion: TestMessage(Union_NullUnion = {1,2,3}, 'Union_NullUnion', toString({1,2,3}), toString(Union_NullUnion))
define test_Union_UnionNull: TestMessage(Union_UnionNull = {1,2,3}, 'Union_UnionNull', toString({1,2,3}), toString(Union_UnionNull))

