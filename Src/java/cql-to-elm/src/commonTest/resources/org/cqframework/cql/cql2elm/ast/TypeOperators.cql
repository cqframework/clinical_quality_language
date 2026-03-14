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
define IntegerToQuantity: convert 1 to Quantity
define IntegerToQuantityFun: ToQuantity(1)

define StringToBoolean: convert 'true' to Boolean
define StringToBooleanFun: ToBoolean('true')
define StringToInteger: convert '1' to Integer
define StringToIntegerFun: ToInteger('1')
define StringToDecimal: convert '3.0' to Decimal
define StringToDecimalFun: ToDecimal('3.0')
define StringToLong: convert '1' to Long
define StringToLongFun: ToLong('1')
define StringToDate: convert '2014-01-01' to Date
define StringToDateFun: ToDate('2014-01-01')
define StringToDateTime: convert '2014-01-01T00:00:00.0' to DateTime
define StringToDateTimeFun: ToDateTime('2014-01-01T00:00:00.0')
define StringToTime: convert 'T00:00:00.0' to Time
define StringToTimeFun: ToTime('T00:00:00.0')

define BooleanToString: convert false to String
define BooleanToStringFun: ToString(false)

define DecimalToString: convert 3.0 to String
define DecimalToStringFun: ToString(3.0)
define DecimalToQuantity: convert 1.0 to Quantity
define DecimalToQuantityFun: ToQuantity(1.0)

define ConvertsToStringTest: ConvertsToString(1)
define ConvertsToIntegerTest: ConvertsToInteger('1')
define ConvertsToBooleanTest: ConvertsToBoolean('true')
define ConvertsToDecimalTest: ConvertsToDecimal('3.0')
define ConvertsToLongTest: ConvertsToLong('1')
define ConvertsToDateTest: ConvertsToDate('2014-01-01')
define ConvertsToDateTimeTest: ConvertsToDateTime('2014-01-01T00:00:00.0')
define ConvertsToTimeTest: ConvertsToTime('T00:00:00.0')
define ConvertsToQuantityTest: ConvertsToQuantity(1)

define LongToString: convert 3L to String
define LongToStringFun: ToString(3L)
define LongToDecimal: convert 1L to Decimal
define LongToDecimalFun: ToDecimal(1L)

define StringToQuantity: convert '3.0 \'m\'' to Quantity
define StringToQuantityFun: ToQuantity('3.0 \'m\'')
