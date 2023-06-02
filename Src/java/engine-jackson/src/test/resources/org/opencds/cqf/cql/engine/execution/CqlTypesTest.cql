library Types version '1'

codesystem "LOINC": 'http://loinc.org'

//Any
define AnyInteger: 5
define AnyLong: 12L
define AnyDecimal: 5.0
define AnyQuantity: 5.0 'g'
define AnyDateTime: DateTime(2012, 4, 4)
define AnyTime: @T09:00:00.000
define AnyInterval: Interval[2, 7]
define AnyList: {1, 2, 3}
define AnyTuple: Tuple { id: 5, name: 'Chris'}
define AnyString: AnyTuple.name

//Boolean
define BooleanTestTrue: true
define BooleanTestFalse: false

//Code
define CodeLiteral: Code { code: '8480-6', system: 'http://loinc.org', version: '1.0', display: 'Systolic blood pressure' }
define CodeLiteral2: Code { code: '1234-5', system: 'http://example.org', version: '1.05', display: 'Test Code' }

//Concept
define ConceptTest: Concept { codes: { CodeLiteral, CodeLiteral2 }, display: 'Type B viral hepatitis' }

//DateTime
define DateTimeNull: DateTime(null)
define DateTimeUpperBoundExcept: DateTime(10000, 12, 31, 23, 59, 59, 999)
define DateTimeLowerBoundExcept: DateTime(0000, 1, 1, 0, 0, 0, 0)
define DateTimeProper: DateTime(2016, 7, 7, 6, 25, 33, 910)
define DateTimeIncomplete: DateTime(2015, 2, 10)
define DateTimeUncertain: days between DateTime(2015, 2, 10) and DateTime(2015, 3)
define DateTimeMin: DateTime(0001, 1, 1, 0, 0, 0, 0)
define DateTimeMax: DateTime(9999, 12, 31, 23, 59, 59, 999)

//Decimal
// problem with size of decimal and size of fractional part
define DecimalUpperBoundExcept: 10000000000000000000000000000000000.00000000
define DecimalLowerBoundExcept: -10000000000000000000000000000000000.00000000
define DecimalFractionalTooBig: 5.999999999
define DecimalPi: 3.14159265

//Integer
// good
/*define IntegerUpperBoundExcept: 2147483649
define IntegerLowerBoundExcept: -2147483649*/
define IntegerProper: 5000

//Quantity
define QuantityTest: 150.2 '[lb_av]'
define QuantityTest2: 2.5589 '{eskimo kisses}'
define QuantityFractionalTooBig: 5.999999999 'g'

//Ratio
define RatioTest: 150.2'[lb_av]':2.5589'{eskimo kisses}'

//String
// Problem here -- escape chars included in result string
define StringTestEscapeQuotes: '\'I start with a single quote and end with a double quote\"'
define StringUnicodeTest: '\u0048\u0069'

//Time
// these all correctly result in translation errors
/*define TimeUpperBoundHours: @T24:59:59.999
define TimeUpperBoundMinutes: @T23:60:59.999
define TimeUpperBoundSeconds: @T23:59:60.999
define TimeUpperBoundMillis: @T23:59:59.10000*/
define TimeProper: @T10:25:12.863
define TimeAllMax: @T23:59:59.999
define TimeAllMin: @T00:00:00.000
