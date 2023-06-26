library NullologicalOperator version '1'

//Coalesce
define CoalesceANull : Coalesce('a', null)
define CoalesceNullA : Coalesce(null, 'a')
define CoalesceEmptyList : Coalesce({})
define CoalesceListFirstA : Coalesce({'a', null, null})
define CoalesceListLastA : Coalesce({null, null, 'a'})
define CoalesceFirstList : Coalesce({'a'},null, null)
define CoalesceLastList : Coalesce(null, null, {'a'})
define DateTimeCoalesce: Coalesce(null, null, DateTime(2012, 5, 18))
define DateTimeListCoalesce: Coalesce({ null, null, DateTime(2012, 5, 18) })
define TimeCoalesce: Coalesce(null, null, @T05:15:33.556)
define TimeListCoalesce: Coalesce({ null, null, @T05:15:33.556 })

//IsNull
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
