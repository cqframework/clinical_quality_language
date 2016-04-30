// @Test: FromString
define stringStr: convert 'str' to String
define stringNull: convert null to String
define boolTrue: convert 'true' to Boolean
define boolFalse: convert 'false' to Boolean
define decimalValid: convert '10.2' to Decimal
define decimalInvalid: convert 'abc' to Decimal
define integerValid: convert '10' to Integer
define integerDropDecimal: convert '10.2' to Integer
define integerInvalid: convert 'abc' to Integer
define quantityStr: convert '10 ''A''' to Quantity
define posQuantityStr: convert '+10 ''A''' to Quantity
define negQuantityStr: convert '-10 ''A''' to Quantity
define quantityStrDecimal: convert '10.0''mA''' to Quantity
define dateStr: convert '2015-01-02' to DateTime

// @Test: FromInteger
define string10: convert 10 to String
define decimal10: convert 10 to Decimal
define intNull: convert null to Decimal
define intInt: convert 10 to Integer

// @Test: FromQuantity
define quantityStr: convert 10 'A' to String
define negQuantityStr: convert -10 'A' to String
define posQuantityStr: convert +10 'A' to String
define quantityQuantity: convert 10 'A' to Quantity

// @Test: FromBoolean
define booleanTrueStr: convert true to String
define booleanFalseStr: convert false to String
define booleanTrueBool: convert true to Boolean
define booleanFalseBool: convert false to Boolean

// @Test: FromDateTime
define dateStr: convert @2015-01-02 to String
define dateDate: convert @2015-01-02 to DateTime

// @Test: FromTime
define timeStr: convert @T11:57 to String
define timeTime: convert @T11:57 to Time

// @Test: FromCode
// define hepB: Code '66071002' from "SNOMED-CT" display 'Type B viral hepatitis'
// define codeConcept: convert hepB to Concept
// define codeCode: convert hepB to Code
define foo: 'bar'
