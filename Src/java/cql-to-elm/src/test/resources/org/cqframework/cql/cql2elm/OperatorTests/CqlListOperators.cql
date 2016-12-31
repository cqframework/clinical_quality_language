library ListOperator version '1'

context Population

//Contains
define ContainsABNullHasNull: { 'a', 'b', null } contains null
define ContainsABCHasA: { 'a', 'b', 'c' } contains 'a'
define ContainsJan2012True: { DateTime(2012, 10, 5), DateTime(2012, 9, 5), DateTime(2012, 1, 1) } contains DateTime(2012, 1, 1)
define ContainsJan2012False: { DateTime(2012, 10, 5), DateTime(2012, 9, 5), DateTime(2012, 10, 1) } contains DateTime(2012, 1, 1)
define ContainsTimeTrue: { @T15:59:59.999, @T05:59:59.999, @T20:59:59.999 } contains @T05:59:59.999
define ContainsTimeFalse: { @T15:59:59.999, @T05:59:59.999, @T20:59:59.999 } contains @T08:59:59.999

//Distinct
define DistinctEmptyList: distinct {}
define DistinctNullNullNull: distinct { null, null, null}
define DistinctANullANull: distinct { 'a', null, 'a', null}
define Distinct112233: distinct { 1, 1, 2, 2, 3, 3}
define Distinct123123: distinct { 1, 1, 2, 2, 3, 3}
define DistinctAABBCC: distinct { 'a', 'a', 'b', 'b', 'c', 'c'}
define DistinctABCABC: distinct { 'a', 'b', 'c', 'a', 'b', 'c'}
define DistinctDateTime: distinct { DateTime(2012, 10, 5), DateTime(2012, 1, 1), DateTime(2012, 1, 1)}
define DistinctTime: distinct { @T15:59:59.999, @T20:59:59.999, @T20:59:59.999 }

//Equal
define EqualNullNull: {null} = {null}
define EqualEmptyListNull: {} = null
define EqualNullEmptyList: null = {}
define EqualEmptyListAndEmptyList: {} = {}
define Equal12And123: { 1, 2 } = { 1, 2, 3 }
define Equal123And12: { 1, 2, 3 } = { 1, 2 }
define Equal123And123: { 1, 2, 3 } = { 1, 2, 3 }
define EqualDateTimeTrue: {DateTime(2012, 5, 10, 0, 0, 0, 0), DateTime(2014, 12, 10, 0, 0, 0, 0)} = {DateTime(2012, 5, 10, 0, 0, 0, 0), DateTime(2014, 12, 10, 0, 0, 0, 0)}
define EqualDateTimeFalse: {DateTime(2012, 5, 10, 0, 0, 0, 0), DateTime(2014, 12, 10, 0, 0, 0, 0)} = {DateTime(2012, 1, 10, 0, 0, 0, 0), DateTime(2014, 12, 10, 0, 0, 0, 0)}
define EqualDateTimeNull: {DateTime(2012, 5, 10), DateTime(2014, 12, 10)} = {DateTime(2012, 1, 10), DateTime(2014, 12, 10)}
define EqualTimeTrue: { @T15:59:59.999, @T20:59:59.999, @T20:59:59.999 } = { @T15:59:59.999, @T20:59:59.999, @T20:59:59.999 }
define EqualTimeFalse: { @T15:59:59.999, @T20:59:59.999, @T20:59:59.999 } = { @T10:59:59.999, @T20:59:59.999, @T20:59:59.999 }

//Except
define ExceptEmptyListAndEmptyList: Except({}, {})
define Except1234And23: { 1, 2, 3, 4 } except { 2, 3 }
define Except23And1234: { 2, 3 } except { 1, 2, 3, 4 }
define ExceptDateTime: { DateTime(2012, 5, 10), DateTime(2014, 12, 10), DateTime(2010, 1, 1)} except {DateTime(2014, 12, 10), DateTime(2010, 1, 1) }
define ExceptTime: { @T15:59:59.999, @T20:59:59.999, @T12:59:59.999 } except { @T20:59:59.999, @T12:59:59.999 }

//Exists
define ExistsEmpty: Exists({})
define ExistsListNull: Exists({ null })
define Exists1: Exists({ 1 })
define Exists12: Exists({ 1, 2 })
define ExistsDateTime: Exists({ DateTime(2012, 5, 10), DateTime(2014, 12, 10) })
define ExistsTime: Exists({ @T15:59:59.999, @T20:59:59.999 })

//Flatten
define FlattenEmpty: Flatten({{},{}})
define FlattenListNullAndNull: Flatten({{null}, {null}})
define FlattenList12And34: Flatten({{1,2}, {3,4}})
define FlattenDateTime: Flatten({ {DateTime(2012, 5, 10)}, {DateTime(2014, 12, 10)} })
define FlattenTime: Flatten({ {@T15:59:59.999}, {@T20:59:59.999} })

//First
define FirstEmpty: First({})
define FirstNull1: First({ null, 1 })
define First1Null: First({ 1, null })
define First12: First({ 1, 2 })
define FirstDateTime: First({ DateTime(2012, 5, 10), DateTime(2014, 12, 10) })
define FirstTime: First({ @T15:59:59.999, @T20:59:59.999 })

//In
define InNullEmpty: null in {}
define InNullAnd1Null: null in { 1, null }
define In1Null: 1 in null
define In1And12: 1 in { 1, 2 }
define In3And12: 3 in { 1, 2 }
define InDateTimeTrue: DateTime(2012, 5, 10) in { DateTime(2001, 9, 11), DateTime(2012, 5, 10), DateTime(2014, 12, 10) }
define InDateTimeFalse: DateTime(2012, 6, 10) in { DateTime(2001, 9, 11), DateTime(2012, 5, 10), DateTime(2014, 12, 10) }
define InTimeTrue: @T15:59:59.999 in { @T02:29:15.156, @T15:59:59.999, @T20:59:59.999 }
define InTimeFalse: @T16:59:59.999 in { @T02:29:15.156, @T15:59:59.999, @T20:59:59.999 }

//Includes
define IncludesEmptyAndEmpty: {} includes {}
define IncludesListNullAndListNull: {null} includes {null}
define Includes123AndEmpty: {1, 2, 3} includes {}
define Includes123And2: {1, 2, 3} includes {2}
define Includes123And4: {1, 2, 3} includes {4}
define IncludesDateTimeTrue: {DateTime(2001, 9, 11), DateTime(2012, 5, 10), DateTime(2014, 12, 10)} includes {DateTime(2012, 5, 10)}
define IncludesDateTimeFalse: {DateTime(2001, 9, 11), DateTime(2012, 5, 10), DateTime(2014, 12, 10)} includes {DateTime(2012, 5, 11)}
define IncludesTimeTrue: { @T02:29:15.156, @T15:59:59.999, @T20:59:59.999 } includes @T15:59:59.999
define IncludesTimeFalse: { @T02:29:15.156, @T15:59:59.999, @T20:59:59.999 } includes @T16:59:59.999

//IncludedIn
define IncludedInEmptyAndEmpty: {} included in {}
define IncludedInListNullAndListNull: { null } included in { null }
define IncludedInEmptyAnd123: {} included in { 1, 2, 3 }
define IncludedIn2And123: { 2 } included in { 1, 2, 3 }
define IncludedIn4And123: { 4 } included in { 1, 2, 3 }
define IncludedInDateTimeTrue: { DateTime(2012, 5, 10)} included in {DateTime(2001, 9, 11), DateTime(2012, 5, 10), DateTime(2014, 12, 10)}
define IncludedInDateTimeFalse: {DateTime(2012, 5, 11)} included in {DateTime(2001, 9, 11), DateTime(2012, 5, 10), DateTime(2014, 12, 10)}
define IncludedInTimeTrue: @T15:59:59.999 included in { @T02:29:15.156, @T15:59:59.999, @T20:59:59.999 }
define IncludedInTimeFalse: @T16:59:59.999 included in { @T02:29:15.156, @T15:59:59.999, @T20:59:59.999 }

//Indexer
define IndexerNull1 :    (null as List<System.Any>)[1]
define Indexer0Of12 :    { 1, 2 }[0]
define Indexer1Of12 :    { 1, 2 }[1]
define Indexer2Of12 :    { 1, 2 }[2]
define IndexerNeg1Of12 : { 1, 2 }[-1]
define IndexerDateTime: { DateTime(2001, 9, 11), DateTime(2012, 5, 10), DateTime(2014, 12, 10) }[1]
define IndexerTime: { @T02:29:15.156, @T15:59:59.999, @T20:59:59.999 }[1]

//IndexOf
define IndexOfEmptyNull : IndexOf({}, null)
define IndexOfNullEmpty : IndexOf(null, {})
define IndexOfNullIn1Null : IndexOf({ 1, null }, null)
define IndexOf1In12 : IndexOf({ 1, 2 }, 1)
define IndexOf2In12 : IndexOf({ 1, 2 }, 2)
define IndexOf3In12 : IndexOf({ 1, 2 }, 3)
define IndexOfDateTime: IndexOf({ DateTime(2001, 9, 11), DateTime(2012, 5, 10), DateTime(2014, 12, 10) }, DateTime(2014, 12, 10))
define IndexOfTime: IndexOf({ @T02:29:15.156, @T15:59:59.999, @T20:59:59.999 }, @T15:59:59.999)

//Intersect
define IntersectEmptyListAndEmptyList: {} intersect {}
define Intersect1234And23: { 1, 2, 3, 4 } intersect { 2, 3 }
define Intersect23And1234: {2, 3} intersect { 1, 2, 3, 4 }
define IntersectDateTime: { DateTime(2001, 9, 11), DateTime(2012, 5, 10), DateTime(2014, 12, 10) } intersect { DateTime(2012, 5, 10), DateTime(2014, 12, 10), DateTime(2000, 5, 5) }
define IntersectTime: { @T02:29:15.156, @T15:59:59.999, @T20:59:59.999 } intersect { @T01:29:15.156, @T15:59:59.999, @T20:59:59.999 }

//Last
define LastEmpty: Last({})
define LastNull1: Last({null, 1})
define Last1Null: Last({1, null})
define Last12: Last({1, 2})
define LastDateTime: Last({DateTime(2012, 5, 10), DateTime(2014, 12, 10)})
define LastTime: Last({ @T15:59:59.999, @T20:59:59.999 })

//Length
define LengthEmpty: Length({})
define LengthNull1: Length({null, 1})
define Length1Null: Length({1, null})
define Length12: Length({1, 2})
define LengthDateTime: Length({DateTime(2001, 9, 11), DateTime(2012, 5, 10), DateTime(2014, 12, 10)})
define LengthTime: Length({ @T15:59:59.999, @T20:59:59.999, @T15:59:59.999, @T20:59:59.999, @T15:59:59.999, @T20:59:59.999 })

//Equivalent
define EquivalentEmptyAndEmpty          : {} ~ {}
define EquivalentABCAndABC              : { 'a', 'b', 'c' } ~ { 'a', 'b', 'c' }
define EquivalentABCAndAB               : { 'a', 'b', 'c' } ~ { 'a', 'b' }
define EquivalentABCAnd123              : { 'a', 'b', 'c' } ~ { 1, 2, 3 }
define Equivalent123AndABC              : { 1, 2, 3 } ~ { 'a', 'b', 'c' }
define Equivalent123AndString123        : { 1, 2, 3 } ~ { '1', '2', '3' }
define EquivalentDateTimeTrue           : {DateTime(2001, 9, 11), DateTime(2012, 5, 10), DateTime(2014, 12, 10), null} ~ {DateTime(2001, 9, 11), DateTime(2012, 5, 10), DateTime(2014, 12, 10), null}
define EquivalentDateTimeNull           : {DateTime(2001, 9, 11), DateTime(2012, 5, 10), DateTime(2014, 12, 10)} ~ {DateTime(2001, 9, 11), DateTime(2012, 5, 10), DateTime(2014, 12, 10), null}
define EquivalentDateTimeFalse          : {DateTime(2001, 9, 11), DateTime(2012, 5, 10), DateTime(2014, 12, 10)} ~ {DateTime(2001, 9, 11), DateTime(2012, 5, 10), DateTime(2014, 12, 1)}
define EquivalentTimeTrue               : { @T15:59:59.999, @T20:59:59.999 } ~ { @T15:59:59.999, @T20:59:59.999 }
define EquivalentTimeNull               : { @T15:59:59.999, @T20:59:59.999 } ~ { @T15:59:59.999, @T20:59:59.999, null }
define EquivalentTimeFalse               : { @T15:59:59.999, @T20:59:59.999 } ~ { @T15:59:59.999, @T20:59:59.995 }

//NotEqual
define NotEqualEmptyAndEmpty : {} != {}
define NotEqualABCAndABC : { 'a', 'b', 'c' } != { 'a', 'b', 'c' }
define NotEqualABCAndAB : { 'a', 'b', 'c' } != { 'a', 'b' }
define NotEqualABCAnd123: { 'a', 'b', 'c' } != { 1, 2, 3 }
define NotEqual123AndABC: { 1, 2, 3 } != { 'a', 'b', 'c' }
define NotEqual123AndString123: { 1, 2, 3 } != { '1', '2', '3' }
define NotEqualDateTimeTrue: {DateTime(2001, 9, 11, 0, 0, 0, 0), DateTime(2012, 5, 10, 0, 0, 0, 0), DateTime(2014, 12, 10, 0, 0, 0, 0)} != {DateTime(2001, 9, 11, 0, 0, 0, 0), DateTime(2012, 5, 10, 0, 0, 0, 0), DateTime(2014, 12, 1, 0, 0, 0, 0)}
define NotEqualDateTimeFalse: {DateTime(2001, 9, 11, 0, 0, 0, 0), DateTime(2012, 5, 10, 0, 0, 0, 0), DateTime(2014, 12, 10, 0, 0, 0, 0)} != {DateTime(2001, 9, 11, 0, 0, 0, 0), DateTime(2012, 5, 10, 0, 0, 0, 0), DateTime(2014, 12, 10, 0, 0, 0, 0)}
define NotEqualTimeTrue: { @T15:59:59.999, @T20:59:59.999 } = { @T15:59:59.999, @T20:59:59.999 }
define NotEqualTimeFalse: { @T15:59:59.999, @T20:59:59.999 } = { @T15:59:59.999, @T20:59:49.999 }

//ProperlyInclues
define ProperIncludesEmptyAndEmpty:       {} properly includes {}
define ProperIncludesListNullAndListNull: {null} properly includes {null}
define ProperIncludes123AndEmpty:         {1, 2, 3} properly includes {}
define ProperIncludes123And2:             {1, 2, 3} properly includes {2}
define ProperIncludes123And4:             {1, 2, 3} properly includes {4}
define ProperIncludesDateTimeTrue:        {DateTime(2001, 9, 11), DateTime(2012, 5, 10), DateTime(2014, 12, 10)} properly includes {DateTime(2012, 5, 10), DateTime(2014, 12, 10)}
define ProperIncludesDateTimeFalse:       {DateTime(2001, 9, 11), DateTime(2012, 5, 10), DateTime(2014, 12, 10)} properly includes {DateTime(2001, 9, 11), DateTime(2012, 5, 10), DateTime(2014, 12, 10)}
define ProperIncludesTimeTrue:            { @T15:59:59.999, @T20:59:59.999, @T20:59:49.999 } properly includes { @T15:59:59.999, @T20:59:59.999 }
define ProperIncludesTimeFalse:            { @T15:59:59.999, @T20:59:59.999, @T20:59:49.999 } properly includes { @T15:59:59.999, @T20:59:59.999, @T14:59:22.999 }

//ProperlyIncludedIn
define ProperIncludedInEmptyAndEmpty: {} properly included in {}
define ProperIncludedInListNullAndListNull: {null} properly included in {null}
define ProperIncludedInEmptyAnd123: {} properly included in {1, 2, 3}
define ProperIncludedIn2And123: {2} properly included in {1, 2, 3}
define ProperIncludedIn4And123: {4} properly included in {1, 2, 3}
define ProperIncludedInDateTimeTrue: {DateTime(2012, 5, 10), DateTime(2014, 12, 10)} properly included in {DateTime(2001, 9, 11), DateTime(2012, 5, 10), DateTime(2014, 12, 10)}
define ProperIncludedInDateTimeFalse: {DateTime(2001, 9, 11), DateTime(2012, 5, 10), DateTime(2014, 12, 10)} properly included in {DateTime(2001, 9, 11), DateTime(2012, 5, 10), DateTime(2014, 12, 10)}
define ProperIncludedInTimeTrue: { @T15:59:59.999, @T20:59:59.999 } properly included in { @T15:59:59.999, @T20:59:59.999, @T20:59:49.999 }
define ProperIncludedInTimeFalse: { @T15:59:59.999, @T20:59:59.999, @T14:59:22.999 } properly included in { @T15:59:59.999, @T20:59:59.999, @T20:59:49.999 }

//SingletonFrom
define SingletonFromEmpty: singleton from {}
define SingletonFromListNull: singleton from {null}
define SingletonFrom1: singleton from { 1 }
define SingletonFrom12: singleton from { 1, 2 }
define SingletonFromDateTime: singleton from { DateTime(2012, 5, 10) }
define SingletonFromTime: singleton from { @T15:59:59.999 }

//Union
define UnionEmptyAndEmpty: {} union {}
define UnionListNullAndListNull: { null } union { null }
define Union123AndEmpty: { 1, 2, 3 } union {}
define Union123And2: { 1, 2, 3 } union { 2 }
define Union123And4: { 1, 2, 3 } union { 4 }
define UnionDateTime: { DateTime(2001, 9, 11)} union {DateTime(2012, 5, 10), DateTime(2014, 12, 10) }
define UnionTime: { @T15:59:59.999, @T20:59:59.999, @T12:59:59.999 } union { @T10:59:59.999 }

// Sort
/*define intList: { 3, 2, 1 }
define decimalList: { 3.8, 2.4, 1.9 }
define quantityList: { 19.99 'lbs', 17.33 'lbs', 10.66 'lbs' }
define dateTimeList: { DateTime(2016), DateTime(2015), DateTime(2010) }
define timeList: { @T15:59:59.999, @T15:12:59.999, @T15:12:13.999 }*/
