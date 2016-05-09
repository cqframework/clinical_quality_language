library ListOperator version '1'

context Population

//Contains
define ContainsEmptyListHasNull: Contains({}, null)
define ContainsABNullHasNull: { 'a', 'b', null } contains null
define ContainsABCHasA: { 'a', 'b', 'c' } contains 'a'

//Distinct
define DistinctEmptyList: Distinct({})
define DistinctNullNullNull: distinct { null, null, null}
define DistinctANullANull: distinct { 'a', null, 'a', null}
define Distinct112233: distinct { 1, 1, 2, 2, 3, 3}
define Distinct123123: distinct { 1, 1, 2, 2, 3, 3}
define DistinctAABBCC: distinct { 'a', 'a', 'b', 'b', 'c', 'c'}
define DistinctABCABC: distinct { 'a', 'b', 'c', 'a', 'b', 'c'}

//Equal
define EqualNullNull: Equal(null, null)
define EqualEmptyListNull: Equal({}, null)
define EqualNullEmptyList: Equal(null, {})
define EqualEmptyListAndEmptyList: Equal({}, {})
define Equal12And123: { 1, 2 } = { 1, 2, 3 }
define Equal123And12: { 1, 2, 3 } = { 1, 2 }
define Equal123And123: { 1, 2, 3 } = { 1, 2, 3 }

//Except
define ExceptEmptyListAndEmptyList: Except({}, {})
define Except1234And23: { 1, 2, 3, 4 } except { 2, 3 }
define Except23And1234: { 2, 3} except { 1, 2, 3, 4 }

//Exists
define ExistsEmpty: Exists({})
define ExistsListNull: Exists({null})
define Exists1: Exists({1})
define Exists12: Exists({1, 2})

//Flatten
define FlattenEmpty: Flatten({{},{}})
define FlattenListNullAndNull: Flatten({{null}, {null}})
define FlattenList12And34: Flatten({{1,2}, {3,4}})

//First
define FirstEmpty: First({})
define FirstNull1: First({null, 1})
define First1Null: First({1, null})
define First12: First({1, 2})

//In
define InNullEmpty: In(null, {})
define InNullAnd1Null: In(null, {1, null})
define In1Null: In(1, null)
define In1And12: In(1, {1, 2})
define In3And12: In(3, {1, 2})

//Includes
define IncludesEmptyAndEmpty: Includes({}, {})
define IncludesListNullAndListNull: Includes({null}, {null})
define Includes123And2: Includes({1, 2, 3}, {2})
define Includes123And4: Includes({1, 2, 3}, {4})

//IncludedIn
define IncludedInEmptyAndEmpty: IncludedIn({}, {})
define IncludedInListNullAndListNull: IncludedIn({null}, {null})
define IncludedIn2And123: IncludedIn({2}, {1, 2, 3})
define IncludedIn4And123: IncludedIn({4}, {1, 2, 3})

//Indexer
define IndexerNull1 : Indexer(null, 1)
define Indexer0Of12 :    Indexer({1, 2}, 0)
define Indexer1Of12 :    Indexer({1, 2}, 1)
define Indexer2Of12 :    Indexer({1, 2}, 2)
define IndexerNeg1Of12 : Indexer({1, 2}, -1)

//IndexOf
define IndexOfEmptyNull : IndexOf({}, null)
define IndexOfNullEmpty : IndexOf(null, {})
define IndexOfNullIn1Null : IndexOf({1, null}, null)
define IndexOf1In12 : IndexOf({1, 2}, 1)
define IndexOf2In12 : IndexOf({1, 2}, 2)
define IndexOf3In12 : IndexOf({1, 2}, 3)

//Intersect
define ListIntersect: { 1, 2, 3, 4, 5 } intersect { 3, 4, 5, 6, 7 }

//Last
define ListLast: Last({ 1, 2, 3 })

//Length
define ListLength: Length( { 1, 2, 3, 4, 5 })

//ProperlyInclues
define ListProperIncludes: { 1, 2, 3, 4, 5 } properly includes { 2, 3, 4 }

//ProperlyIncludedIn
define ListProperIncludedIn: { 2, 3, 4 } properly included in { 1, 2, 3, 4, 5 }

//SingletonFrom
define ListSingletonFrom: singleton from { 1 }

//Union
define ListUnion: { 1, 2, 3 } union { 4, 5, 6 }

//Equivalent
define StringListEqual: { 'x', 'y', 'z' } = { 'x', 'y', 'z' }
define IntegerListEqual: { 1, 2, 3, 4, 5 } = { 1, 2, 3, 4, 5 }
define EqualTupleList: List<Any>{ Tuple{a: 1, b: Tuple{c: 1}}, Tuple{x: 'y', z: 2} } = List<Any>{ Tuple{a: 1, b: Tuple{c: 1}}, Tuple{x: 'y', z: 2} }

//NotEqual
define UnequalTupleList: List<Any>{ Tuple{a: 1, b: Tuple{c: 1}}, Tuple{x: 'y', z: 2} } = List<Any>{ Tuple{a: 1, b: Tuple{c: -1}}, Tuple{x: 'y', z: 2} }



