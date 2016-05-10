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
define Includes123AndEmpty: Includes({1, 2, 3}, {})
define Includes123And2: Includes({1, 2, 3}, {2})
define Includes123And4: Includes({1, 2, 3}, {4})

//IncludedIn
define IncludedInEmptyAndEmpty: IncludedIn({}, {})
define IncludedInListNullAndListNull: IncludedIn({null}, {null})
define IncludedInEmptyAnd123: IncludedIn({}, {1, 2, 3})
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
define IntersectEmptyListAndEmptyList: Intersect({}, {})
define Intersect1234And23: Intersect({ 1, 2, 3, 4 }, { 2, 3 })
define Intersect23And1234: Intersect({2, 3}, { 1, 2, 3, 4 })

//Last
define LastEmpty: Last({})
define LastNull1: Last({null, 1})
define Last1Null: Last({1, null})
define Last12: Last({1, 2})

//Length
define LengthEmpty: Length({})
define LengthNull1: Length({null, 1})
define Length1Null: Length({1, null})
define Length12: Length({1, 2})

//Equivalent
define EquivalentEmptyAndEmpty          : Equivalent({}, {})
define EquivalentABCAndABC              : Equivalent({ 'a', 'b', 'c' }, { 'a', 'b', 'c' })
define EquivalentABCAndAB               : Equivalent({ 'a', 'b', 'c' }, { 'a', 'b' })
define EquivalentABCAnd123              : Equivalent({ 'a', 'b', 'c' }, { 1, 2, 3 })
define Equivalent123AndABC              : Equivalent({ 1, 2, 3 }, { 'a', 'b', 'c' })
define Equivalent123AndString123        : Equivalent({ 1, 2, 3 },  { '1', '2', '3' })

//NotEqual
define NotEqualEmptyAndEmpty : NotEqual({}, {})
define NotEqualABCAndABC : NotEqual({ 'a', 'b', 'c' }, { 'a', 'b', 'c' })
define NotEqualABCAndAB : NotEqual({ 'a', 'b', 'c' }, { 'a', 'b' })
define NotEqualABCAnd123: NotEqual({ 'a', 'b', 'c' }, { 1, 2, 3 })
define NotEqual123AndABC: NotEqual({ 1, 2, 3 }, { 'a', 'b', 'c' })
define NotEqual123AndString123:  NotEqual({ 1, 2, 3 },  { '1', '2', '3' })

//ProperlyInclues
define ProperIncludesEmptyAndEmpty:       ProperIncludes({}, {})
define ProperIncludesListNullAndListNull: ProperIncludes({null}, {null})
define ProperIncludes123AndEmpty: ProperIncludes({1, 2, 3}, {})
define ProperIncludes123And2: ProperIncludes({1, 2, 3}, {2})
define ProperIncludes123And4: ProperIncludes({1, 2, 3}, {4})

//ProperlyIncludedIn
define ProperIncludedInEmptyAndEmpty: ProperIncludedIn({}, {})
define ProperIncludedInListNullAndListNull: ProperIncludedIn({null}, {null})
define ProperIncludedInEmptyAnd123: ProperIncludedIn({}, {1, 2, 3})
define ProperIncludedIn2And123: ProperIncludedIn({2}, {1, 2, 3})
define ProperIncludedIn4And123: ProperIncludedIn({4}, {1, 2, 3})

//SingletonFrom
define SingletonFromEmpty: SingletonFrom({})
define SingletonFromListNull: SingletonFrom({null})
define SingletonFrom1: SingletonFrom({1})
define SingletonFrom12: SingletonFrom({1, 2})

//Union
define UnionEmptyAndEmpty: Union({}, {})
define UnionListNullAndListNull: Union({null}, {null})
define Union123AndEmpty: Union({1, 2, 3}, {})
define Union123And2: Union({1, 2, 3}, {2})
define Union123And4: Union({1, 2, 3}, {4})
