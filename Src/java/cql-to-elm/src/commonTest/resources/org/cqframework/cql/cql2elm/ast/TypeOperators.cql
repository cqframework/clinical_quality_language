library TypeOperators version '1.0.0'
using System
context Patient

define AsExpression: null as Boolean
define CastExpression: cast null as Boolean
define IsExpression: null is Boolean

define IntegerToString: convert 1 to String
define IntegerToStringFun: ToString(1)
define IntegerToDecimal: convert 1 to Decimal
define IntegerToDecimalFun: ToDecimal(1)
define IntegerToLong: convert 1 to Long
define IntegerToLongFun: ToLong(1)

define StringToBoolean: convert 'true' to Boolean
define StringToBooleanFun: ToBoolean('true')
define StringToInteger: convert '1' to Integer
define StringToIntegerFun: ToInteger('1')
define StringToDecimal: convert '3.0' to Decimal
define StringToDecimalFun: ToDecimal('3.0')
define StringToLong: convert '1' to Long
define StringToLongFun: ToLong('1')

define BooleanToString: convert false to String
define BooleanToStringFun: ToString(false)

define DecimalToString: convert 3.0 to String
define DecimalToStringFun: ToString(3.0)
