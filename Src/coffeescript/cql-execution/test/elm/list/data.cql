// @Test: List
define Three = 1 + 2
define IntList = { 9, 7, 8 }
define StringList = { 'a', 'bee', 'see' }
define MixedList = { 1, 'two', Three }
define EmptyList = {}

  // @Test: Exists
define EmptyList = exists ({})
define FullList = exists ({ 1, 2, 3 })

// @Test: Union
define OneToTen = {1, 2, 3, 4, 5} union {6, 7, 8, 9, 10}
define OneToFiveOverlapped = {1, 2, 3, 4} union {3, 4, 5}
define Disjoint = {1, 2} union {4, 5}
define NestedToFifteen = {1, 2, 3} union {4, 5, 6} union {7 ,8 , 9} union {10, 11, 12} union {13, 14, 15}

// @Test: Intersect
define NoIntersection = {1, 2, 3} intersect {4, 5, 6}
define IntersectOnFive = {4, 5, 6} intersect {1, 3, 5, 7}
define IntersectOnEvens = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10} intersect {0, 2, 4, 6, 8, 10, 12}
define IntersectOnAll = {1, 2, 3, 4, 5} intersect {5, 4, 3, 2, 1}
define NestedIntersects = {1, 2, 3, 4, 5} intersect {2, 3, 4, 5, 6} intersect {3, 4, 5, 6, 7} intersect {4, 5, 6, 7, 8}

// @Test: InList
define IsIn = 4 in { 3, 4, 5 }
define IsNotIn = 4 in { 3, 5, 6 }

// @Test: Expand
define ListOfLists = expand { {1, 2, 3}, {4, 5, 6}, {7, 8, 9}, {9, 8, 7, 6, 5}, {4}, {3, 2, 1} }
define ListOfInts = expand { 1, 2, 3, 4, 5, 6, 7, 8, 9 }
define MixedList = expand { 1, 2, 3, {4, 5, 6}, 7, 8, 9 }
define NullValue = expand null

// @Test: Distinct
define LotsOfDups = distinct {1, 2, 2, 3, 3, 3, 4, 4, 4, 4, 5, 5, 5, 5, 5, 4, 3, 2, 1}
define NoDups = distinct {2, 4, 6, 8, 10}

// @Test: First
define Numbers = First({1, 2, 3, 4})
define Letters = First({'a', 'b', 'c'})
define Lists = First({{'a','b','c'},{1,2,3}})
define Tuples = First({ tuple{a: 1, b: 2, c: 3}, tuple{x: 24, y: 25, z: 26} })
define Unordered = First({3, 1, 4, 2})
define Empty = First({})
define NullValue = First(null)

// @Test: Last
define Numbers = Last({1, 2, 3, 4})
define Letters = Last({'a', 'b', 'c'})
define Lists = Last({{'a','b','c'},{1,2,3}})
define Tuples = Last({ tuple{a: 1, b: 2, c: 3}, tuple{x: 24, y: 25, z: 26} })
define Unordered = Last({3, 1, 4, 2})
define Empty = Last({})
define NullValue = Last(null)
