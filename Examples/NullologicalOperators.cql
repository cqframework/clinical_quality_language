library NullologicalOperator version '1'

context Population

//IsNUll
define IsNullTrue                   : IsNull(null)
define IsNullFalseEmptyString       : IsNull('')
define IsNullAlsoFalseAbcString     : IsNull('abc')
define IsNullAlsoFalseNumber1       : IsNull(1)
define IsNullAlsoFalseNumberZero    : IsNull(0)