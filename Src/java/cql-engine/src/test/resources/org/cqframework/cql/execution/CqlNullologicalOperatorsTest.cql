library NullologicalOperator version '1'

context Population

//Coalesce
define CoalesceEmpty : Coalesce()
define CoalesceNull : Coalesce(null)
define CoalesceANull : Coalesce('a', null)
define CoalesceA : Coalesce('a')
define CoalesceNullA : Coalesce(null, 'a')
define CoalesceEmptyList : Coalesce({})
define CoalesceListFirstA : Coalesce({'a', null, null})
define CoalesceListLastA : Coalesce({null, null, 'a'})
define CoalesceFirstList : Coalesce({'a'},null, null)
define CoalesceLastList : Coalesce(null, null, {'a'})

//IsNUll
define IsNullTrue                   : IsNull(null)
define IsNullFalseEmptyString       : IsNull('')
define IsNullAlsoFalseAbcString     : IsNull('abc')
define IsNullAlsoFalseNumber1       : IsNull(1)
define IsNullAlsoFalseNumberZero    : IsNull(0)

//IsFalse
define IsFalseFalse : IsFalse(false)
define IsFalseTrue  : IsFalse(true)
define IsFalseNull  : IsFalse(null)

//IsTrue
define IsTrueTrue   : IsTrue(true)
define IsTrueFalse  : IsTrue(false)
define IsTrueNull   : IsTrue(null)
