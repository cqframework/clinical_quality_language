context Population

//AND
define AndTrue       : true and true

define AndFalse      : true and false

define AndAlsoFalse  : false and null

define AndNull       :  true and null

define AndAlsoNull   :  null and null

//Not
define NotTrue  : not true

define NotFalse : not false

define NotNull  : not null

//Or
define OrTrue       : true or false

define OrAlsoTrue   : true or null

define OrFalse      : false or false

define OrNull       :  false or null

define OrAlsoNull   :  null or null