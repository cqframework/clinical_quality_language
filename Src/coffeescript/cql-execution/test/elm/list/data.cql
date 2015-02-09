// @Test: List
define Three = 1 + 2
define IntList = { 9, 7, 8 }
define StringList = { 'a', 'bee', 'see' }
define MixedList = { 1, 'two', Three }
define EmptyList = {}

// @Test: Exists
define EmptyList = exists ({})
define FullList = exists ({ 1, 2, 3 })

// @Test: Equal
define EqualIntList = {1, 2, 3} = {1, 2, 3}
define UnequalIntList = {1, 2, 3} = {1, 2}
define ReverseIntList = {1, 2, 3} = {3, 2, 1}
define EqualStringList = {'hello', 'world'} = {'hello', 'world'}
define UnequalStringList = {'hello', 'world'} = {'foo', 'bar'}
define EqualTupleList = { tuple{a: 1, b: tuple{c: 1}}, tuple{x: 'y', z: 2} } = { tuple{a: 1, b: tuple{c: 1}}, tuple{x: 'y', z: 2} }
define UnequalTupleList = { tuple{a: 1, b: tuple{c: 1}}, tuple{x: 'y', z: 2} } = { tuple{a: 1, b: tuple{c: -1}}, tuple{x: 'y', z: 2} }

// @Test: NotEqual
define EqualIntList = {1, 2, 3} <> {1, 2, 3}
define UnequalIntList = {1, 2, 3} <> {1, 2}
define ReverseIntList = {1, 2, 3} <> {3, 2, 1}
define EqualStringList = {'hello', 'world'} <> {'hello', 'world'}
define UnequalStringList = {'hello', 'world'} <> {'foo', 'bar'}
define EqualTupleList = { tuple{a: 1, b: tuple{c: 1}}, tuple{x: 'y', z: 2} } <> { tuple{a: 1, b: tuple{c: 1}}, tuple{x: 'y', z: 2} }
define UnequalTupleList = { tuple{a: 1, b: tuple{c: 1}}, tuple{x: 'y', z: 2} } <> { tuple{a: 1, b: tuple{c: -1}}, tuple{x: 'y', z: 2} }

// @Test: Union
define OneToTen = {1, 2, 3, 4, 5} union {6, 7, 8, 9, 10}
define OneToFiveOverlapped = {1, 2, 3, 4} union {3, 4, 5}
define Disjoint = {1, 2} union {4, 5}
define NestedToFifteen = {1, 2, 3} union {4, 5, 6} union {7 ,8 , 9} union {10, 11, 12} union {13, 14, 15}
define NullUnion = null union {1, 2, 3}
define UnionNull = {1, 2, 3} union null

// @Test: Except
define ExceptThreeFour = {1, 2, 3, 4, 5} except {3, 4}
define ThreeFourExcept = {3, 4} except {1, 2, 3, 4, 5}
define ExceptFiveThree = {1, 2, 3, 4, 5} except {5, 3}
define ExceptNoOp = {1, 2, 3, 4, 5} except {6, 7, 8, 9, 10}
define ExceptEverything = {1, 2, 3, 4, 5} except {1, 2, 3, 4, 5}
define SomethingExceptNothing = {1, 2, 3, 4, 5} except {}
define NothingExceptSomething = {} except {1, 2, 3, 4, 5}
define ExceptTuples = {tuple{a: 1}, tuple{b: 2}, tuple{c: 3}} except {tuple{b: 2}}
define ExceptNull = {1, 2, 3, 4, 5} except null
define NullExcept = null except {1, 2, 3, 4, 5}

// @Test: Intersect
define NoIntersection = {1, 2, 3} intersect {4, 5, 6}
define IntersectOnFive = {4, 5, 6} intersect {1, 3, 5, 7}
define IntersectOnEvens = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10} intersect {0, 2, 4, 6, 8, 10, 12}
define IntersectOnAll = {1, 2, 3, 4, 5} intersect {5, 4, 3, 2, 1}
define NestedIntersects = {1, 2, 3, 4, 5} intersect {2, 3, 4, 5, 6} intersect {3, 4, 5, 6, 7} intersect {4, 5, 6, 7, 8}
define IntersectTuples = {tuple{a:1, b:'d'}, tuple{a:1, b:'c'}, tuple{a:2, b:'c'}} intersect {tuple{a:2, b:'d'}, tuple{a:1, b:'c'}, tuple{a:2, b:'c'}, tuple{a:5, b:'e'}}
define NullIntersect = null intersect {1, 2, 3}
define IntersectNull = {1, 2, 3} intersect null

// @Test: IndexOf
define IndexOfSecond = IndexOf({'a', 'b', 'c', 'd'}, 'b')
define IndexOfThirdTuple = IndexOf({tuple{a: 1}, tuple{b: 2}, tuple{c: 3}}, tuple{c: 3})
define MultipleMatches = IndexOf({'a', 'b', 'c', 'd', 'd', 'e', 'd'}, 'd')
define ItemNotFound = IndexOf({'a', 'b', 'c'}, 'd')
define NullList = IndexOf(null, 'a')
define NullItem = IndexOf({'a', 'b', 'c'}, null)

// @Test: Indexer
define SecondItem = {'a', 'b', 'c', 'd'}[2]
define ZeroIndex = {'a', 'b', 'c', 'd'}[0]
define OutOfBounds = {'a', 'b', 'c', 'd'}[100]
define NullList = null[1]
define NullIndexer = {'a', 'b', 'c', 'd'}[null]

// @Test: In
define IsIn = 4 in { 3, 4, 5 }
define IsNotIn = 4 in { 3, 5, 6 }
define TupleIsIn = tuple{a: 1, b: 'c'} in {tuple{a:1, b:'d'}, tuple{a:1, b:'c'}, tuple{a:2, b:'c'}}
define TupleIsNotIn = tuple{a: 1, b: 'c'} in {tuple{a:1, b:'d'}, tuple{a:2, b:'d'}, tuple{a:2, b:'c'}}
define NullIn = null in {1, 2, 3}
define InNull = 1 in null

// @Test: Contains
define IsIn = { 3, 4, 5 } contains 4
define IsNotIn = { 3, 5, 6 } contains 4
define TupleIsIn = {tuple{a:1, b:'d'}, tuple{a:1, b:'c'}, tuple{a:2, b:'c'}} contains tuple{a: 1, b: 'c'}
define TupleIsNotIn = {tuple{a:1, b:'d'}, tuple{a:2, b:'d'}, tuple{a:2, b:'c'}} contains tuple{a: 1, b: 'c'}
define InNull = null contains {1, 2, 3}
define NullIn = 1 contains null

// @Test: Includes
define IsIncluded = {1, 2, 3, 4, 5} includes {2, 3, 4}
define IsIncludedReversed = {1, 2, 3, 4, 5} includes {4, 3, 2}
define IsSame = {1, 2, 3, 4, 5} includes {1, 2, 3, 4, 5}
define IsNotIncluded = {1, 2, 3, 4, 5} includes {4, 5, 6}
define TuplesIncluded = {tuple{a:1, b:'d'}, tuple{a:2, b:'d'}, tuple{a:2, b:'c'}} includes {tuple{a:2, b:'d'}, tuple{a:2, b:'c'}}
define TuplesNotIncluded = {tuple{a:1, b:'d'}, tuple{a:2, b:'d'}, tuple{a:2, b:'c'}} includes {tuple{a:2, b:'d'}, tuple{a:3, b:'c'}}
define NullIncluded = {1, 2, 3, 4, 5} includes null
define NullIncludes = null includes {1, 2, 3, 4, 5}

// @Test: IncludedIn
define IsIncluded = {2, 3, 4} included in {1, 2, 3, 4, 5}
define IsIncludedReversed = {4, 3, 2} included in {1, 2, 3, 4, 5}
define IsSame = {1, 2, 3, 4, 5} included in {1, 2, 3, 4, 5}
define IsNotIncluded = {4, 5, 6} included in {1, 2, 3, 4, 5}
define TuplesIncluded = {tuple{a:2, b:'d'}, tuple{a:2, b:'c'}} included in {tuple{a:1, b:'d'}, tuple{a:2, b:'d'}, tuple{a:2, b:'c'}}
define TuplesNotIncluded = {tuple{a:2, b:'d'}, tuple{a:3, b:'c'}} included in {tuple{a:1, b:'d'}, tuple{a:2, b:'d'}, tuple{a:2, b:'c'}}
define NullIncludes = {1, 2, 3, 4, 5} included in null
define NullIncluded = null included in {1, 2, 3, 4, 5}

// @Test: ProperIncludes
define IsIncluded = {1, 2, 3, 4, 5} properly includes {2, 3, 4, 5}
define IsIncludedReversed = {1, 2, 3, 4, 5} properly includes {5, 4, 3, 2}
define IsSame = {1, 2, 3, 4, 5} properly includes {1, 2, 3, 4, 5}
define IsNotIncluded = {1, 2, 3, 4, 5} properly includes {3, 4, 5, 6}
define TuplesIncluded = {tuple{a:1, b:'d'}, tuple{a:2, b:'d'}, tuple{a:2, b:'c'}} properly includes {tuple{a:2, b:'d'}, tuple{a:2, b:'c'}}
define TuplesNotIncluded = {tuple{a:1, b:'d'}, tuple{a:2, b:'d'}, tuple{a:2, b:'c'}} properly includes {tuple{a:2, b:'d'}, tuple{a:3, b:'c'}}
define NullIncluded = {1, 2, 3, 4, 5} properly includes null
define NullIncludes = null properly includes {1, 2, 3, 4, 5}

// @Test: ProperIncludedIn
define IsIncluded = {2, 3, 4} properly included in {1, 2, 3, 4, 5}
define IsIncludedReversed = {4, 3, 2} properly included in {1, 2, 3, 4, 5}
define IsSame = {1, 2, 3, 4, 5} properly included in {1, 2, 3, 4, 5}
define IsNotIncluded = {4, 5, 6} properly included in {1, 2, 3, 4, 5}
define TuplesIncluded = {tuple{a:2, b:'d'}, tuple{a:2, b:'c'}} properly included in {tuple{a:1, b:'d'}, tuple{a:2, b:'d'}, tuple{a:2, b:'c'}}
define TuplesNotIncluded = {tuple{a:2, b:'d'}, tuple{a:3, b:'c'}} properly included in {tuple{a:1, b:'d'}, tuple{a:2, b:'d'}, tuple{a:2, b:'c'}}
define NullIncludes = {1, 2, 3, 4, 5} properly included in null
define NullIncluded = null properly included in {1, 2, 3, 4, 5}

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

// @Test: Length
define Numbers = Length({2, 4, 6, 8, 10})
define Lists = Length({ {1, 2, 3}, {4, 5, 6}, {7, 8, 9}, {10, 11, 12}})
define Tuples = Length({ tuple{a: 1, b: 2, c: 3}, tuple{x: 24, y: 25, z: 26} })
define Empty = Length({})
define NullValue = Length(null)
