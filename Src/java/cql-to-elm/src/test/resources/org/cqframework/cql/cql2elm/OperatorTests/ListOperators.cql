define ListContains: { 1, 2, 3 } contains 2
define ListDistinct: distinct { 1, 1, 2, 2, 3, 3, 4, 4, 5, 5 }
define ListEqual: { 1, 2, 3 } = { 1, 2, 3 }
define ListExcept: { 1, 2, 3, 4, 5 } except { 2, 3, 4 }
define ListExists: exists { 1, 2, 3, 4, 5 }
define ListFlatten: flatten { { 1, 2, 3 }, { 4, 5, 6 } }
define ListFirst: First({ 1, 2, 3, 4, 5 })
define ListIn: 2 in { 1, 2, 3 }
define ListIncludes: { 1, 2, 3, 4, 5 } includes { 2, 3, 4 }
define ListIncludedIn: { 2, 3, 4 } included in { 1, 2, 3, 4, 5 }
define ListIndexer: { 1, 2, 3 }[1]
define ListIndexOf: IndexOf({ 1, 2, 3 }, 2)
define ListIntersect: { 1, 2, 3, 4, 5 } intersect { 3, 4, 5, 6, 7 }
define ListLast: Last({ 1, 2, 3 })
define ListLength: Length( { 1, 2, 3, 4, 5 })
define ListProperIncludes: { 1, 2, 3, 4, 5 } properly includes { 2, 3, 4 }
define ListProperIncludedIn: { 2, 3, 4 } properly included in { 1, 2, 3, 4, 5 }
define ListSingletonFrom: singleton from { 1 }
define ListTail: Tail({ 1, 2, 3 })
define ListTake: Take({ 1, 2, 3 }, 1)
define ListUnion: { 1, 2, 3 } union { 4, 5, 6 }
define ListUnionWithChoice: { 1, 2, 3 } union { 'a', 'b', 'c' }

define StringListEqual: { 'x', 'y', 'z' } = { 'x', 'y', 'z' }
define IntegerListEqual: { 1, 2, 3, 4, 5 } = { 1, 2, 3, 4, 5 }
define EqualTupleList: List<Any>{ Tuple{a: 1, b: Tuple{c: 1}}, Tuple{x: 'y', z: 2} } = List<Any>{ Tuple{a: 1, b: Tuple{c: 1}}, Tuple{x: 'y', z: 2} }
define UnequalTupleList: List<Any>{ Tuple{a: 1, b: Tuple{c: 1}}, Tuple{x: 'y', z: 2} } = List<Any>{ Tuple{a: 1, b: Tuple{c: -1}}, Tuple{x: 'y', z: 2} }
