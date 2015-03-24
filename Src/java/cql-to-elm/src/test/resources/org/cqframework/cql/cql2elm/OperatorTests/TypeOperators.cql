define AsExpression: null as Boolean
define CastExpression: cast null as Boolean
define IsExpression: null is Boolean

// ToString(Boolean) : String
define BooleanToString: convert false to String
// ToString(Integer) : String
define IntegerToString: convert 3 to String
// ToString(Decimal) : String
define DecimalToString: convert 3.0 to String
// ToString(DateTime) : String
define DateTimeToString: convert DateTime(2014, 1, 1, 0, 0, 0, 0) to String
// ToBoolean(String) : Boolean
define StringToBoolean: convert 'false' to Boolean
// ToInteger(String) : Integer
define StringToInteger: convert '1' to Integer
// ToDecimal(String) : Decimal
define StringToDecimal: convert '3.0' to Decimal
// ToDecimal(Integer) : Decimal
define IntegerToDecimal: convert 1 to Decimal
// ToDateTime(String) : DateTime
define StringToDateTime: convert '2014-01-01T00:00:00:00.0000+0700' to DateTime
