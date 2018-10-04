codesystem "FAKECS": '1.2.3.4.5.6.7.8.9' version '1'

define AsExpression: null as Boolean
define CastExpression: cast null as Boolean
define IsExpression: null is Boolean

// ToString(Boolean) : String
define BooleanToString: convert false to String
define BooleanToStringFun: ToString(false)
define BooleanConvertsToString: ConvertsToString(false)
// ToString(Integer) : String
define IntegerToString: convert 3 to String
define IntegerToStringFun: ToString(3)
define IntegerConvertsToString: ConvertsToString(3)
// ToString(Decimal) : String
define DecimalToString: convert 3.0 to String
define DecimalToStringFun: ToString(3.0)
define DecimalConvertsToString: ConvertsToString(3.0)
// ToString(Quantity) : String
define QuantityToString: convert 3.0'm' to String
define QuantityToStringFun: ToString(3.0'm')
define QuantityConvertsToString: ConvertsToString(3.0'm')
// ToString(DateTime) : String
define DateTimeToString: convert @2014-01-01T00:00:00.0 to String
define DateTimeToStringFun: ToString(@2014-01-01T00:00:00.0)
define DateTimeConvertsToString: ConvertsToString(@2014-01-01T00:00:00.0)
// ToString(Time) : String
define TimeToString: convert @T00:00:00.0 to String
define TimeToStringFun: ToString(@T00:00:00.0)
define TimeConvertsToString: ConvertsToString(@T00:00:00.0)
// ToBoolean(String) : Boolean
define StringToBoolean: convert 'false' to Boolean
define StringToBooleanFun: ToBoolean('false')
define StringConvertsToBoolean: ConvertsToBoolean('false')
// ToInteger(String) : Integer
define StringToInteger: convert '1' to Integer
define StringToIntegerFun: ToInteger('1')
define StringConvertsToInteger: ConvertsToInteger('1')
// ToDecimal(String) : Decimal
define StringToDecimal: convert '3.0' to Decimal
define StringToDecimalFun: ToDecimal('3.0')
// ToDecimal(Integer) : Decimal
define IntegerToDecimal: convert 1 to Decimal
define IntegerToDecimalFun: ToDecimal(1)
define IntegerConvertsToDecimal: ConvertsToDecimal(1)
// ToDateTime(String) : DateTime
define StringToDateTime: convert '2014-01-01T00:00:00.0000+0700' to DateTime
define StringToDateTimeFun: ToDateTime('2014-01-01T00:00:00.0000+0700')
define StringConvertsToDateTime: ConvertsToDateTime('2014-01-01T00:00:00.00.0000+0700')
// ToTime(String) : Time
define StringToTime: convert 'T00:00:00.0000+0700' to Time
define StringToTimeFun: ToTime('T00:00:00.0000+0700')
define StringConvertsToTime: ConvertsToTime('T00:00:00.0000+0700')
// ToConcept(Code) : Concept
define MyCode: Code 'ABC' from "FAKECS" display 'ABC'
define CodeToConcept: convert MyCode to Concept
define CodeToConceptFun: ToConcept(MyCode)
define MyCodes: {
  Code 'ABC' from "FAKECS" display 'ABC',
  Code 'DEF' from "FAKECS" display 'DEF',
  Code 'GHI' from "FAKECS" display 'GHI'
}
define CodesToConcept: convert MyCodes to Concept
define CodesToConceptFun: ToConcept(MyCodes)

define MinimumInteger: minimum Integer
define MinimumDecimal: minimum Decimal
define MinimumDateTime: minimum DateTime
define MinimumTime: minimum Time

define MaximumInteger: maximum Integer
define MaximumDecimal: maximum Decimal
define MaximumDateTime: maximum DateTime
define MaximumTime: maximum Time

define CastChoice: ({ 'A', 'B', 'C' } union { 1, 2, 3 }) X where (X as String) = 'A'

define "ConvertsToResultTest1": true and ConvertsToInteger('1')
define "ConvertsToResultTest2": true and ConvertsToString('1')
define "ConvertsToResultTest3": true and ConvertsToDecimal('1')
