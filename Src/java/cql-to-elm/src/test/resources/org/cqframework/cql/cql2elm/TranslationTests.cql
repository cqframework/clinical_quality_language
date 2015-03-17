library PropertyTest

using QUICK

valueset "Female Administrative Sex" : '2.16.840.1.113883.3.560.100.2'

context Patient

define InDemographic :
    AgeInYears() >= 18
        and Patient.gender in "Female Administrative Sex"

define MedianAny : Median({ null, null, null })
define MedianInt : Median({ 1, 2, 3 })

define ListsOfLists : list<list<Any>>{{'a','b','c'},{1,2,3}}
define LotsOfDups : distinct {1, 2, 2, 3, 3, 3, 4, 4, 4, 4, 5, 5, 5, 5, 5, 4, 3, 2, 1}
define IsIncluded : {2, 3, 4} included in {1, 2, 3, 4, 5}
define IsProperlyIncludes : {1, 2, 3, 4, 5} properly includes {2, 3, 4, 5}
define IsProperlyIncluded : {2, 3, 4} properly included in {1, 2, 3, 4, 5}
define EqInteger : 4 = 4
define EqIntervals : interval[1, 3] = interval[1, 3]
define EqTuples : tuple{a: 1, b: tuple{c: 1}} = tuple{a: 1, b: tuple{c: 1}}
define EqLists : { 1, 2, 3 } = { 1, 2, 3 }

define IsNull : 5 is null
define StringConcat : 'a' + 'b'
define CoalesceExpression : Coalesce(1, 2, 3, 4, 5)
define CoalesceWithNulls : Coalesce(1, null, null, 4, 5)

define HasNull : Count({ 1, null, null, null, 2 })
define Empty : Count({ })

define TypedHasNull : Count(list<Integer> { 1, null, null, null, 2 })
define TypedEmpty : Count(list<Integer> { })

