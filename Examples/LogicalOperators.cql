library LogicalOperator version '1'

context Population

//AND
define AndTrue       : true and true
define AndFalse      : true and false
define AndAlsoFalse  : false and null
define AndNull       :  true and null
define AndAlsoNull   :  null and null

//NOT
define NotTrue  : not true
define NotFalse : not false
define NotNull  : not null

//OR
define OrTrue       : true or false
define OrAlsoTrue   : true or null
define OrFalse      : false or false
define OrNull       :  false or null
define OrAlsoNull   :  null or null

//XOR
define XOrTrue      : true xor false
define XOrAlsoTrue  : false xor true
define XOrFalse     : false xor false
define XOrAlsoFalse :  true xor true
define XOrNull      :  null xor null
define XOrAlsoNull1 :  true xor null
define XOrAlsoNull2 :  false xor null