library ListOperator version '1'

context Population

//Contains
define ListContains: { "a", "b", "c" } contains "a"

//Distinct
define ListDistinct: distinct { 1, 1, 2, 2, 3, 3, 4, 4, 5, 5 }

//Equal
define ListEqual: { 1, 2, 3 } = { 1, 2, 3 }

//Except
define ListExcept: { 1, 2, 3, 4, 5 } except { 2, 3, 4 }

//Exists
define ListExists: exists { 1, 2, 3, 4, 5 }

//Flatten
define ListFlatten: flatten { { 1, 2, 3 }, { 4, 5, 6 } }

//First
define ListFirst: First({ 1, 2, 3, 4, 5 })

//In
define ListIn: 2 in { 1, 2, 3 }

//Includes
define ListIncludes: { 1, 2, 3, 4, 5 } includes { 2, 3, 4 }

//IncludedIn
define ListIncludedIn: { 2, 3, 4 } included in { 1, 2, 3, 4, 5 }

//Indexer
define ListIndexer: { 1, 2, 3 }[1]

//IndexerOf
define ListIndexOf: IndexOf({ 1, 2, 3 }, 2)

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



