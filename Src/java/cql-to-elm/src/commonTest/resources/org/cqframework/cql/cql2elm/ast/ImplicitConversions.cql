library ImplicitConversions version '1.0.0'
using System
context Patient

// Integer to Long
define IntegerToLongEqual: 5 = 5L
define IntegerLongMultiplication: 5 * 5L
define LongIntegerMultiplication: 5L * 5

// Long to Decimal
define LongToDecimalEqual: 5L = 5.0
define LongToDecimalMultiplication: 5L * 5.0
define DecimalToLongMultiplication: 5.0 * 5L

// Integer to Decimal
define ThisShouldWork: 5 = 5.5
define IntegerDecimalMultiplication: 5 * 5.0
define DecimalIntegerMultiplication: 5.0 * 5

// null to any type
define IntegerAnyMultiplication: 5 * null

// Within Coalesce
define NullCoalesce: Coalesce(null, 1, 2, 3)
define IntegerCoalesce: Coalesce(1, 2.0, 3.0, null)
define DecimalCoalesce: Coalesce(1.0, 2, 3, null)

// Within Interval selector
define IntegerIntervalSelector: Interval[5, null]
define DecimalIntervalSelector: Interval[5, 5.0]

// Within List selector
define EmptyList: { }
define IntegerList: { null, 1 }
define DecimalList: { 1, 1.5 }
define DecimalIntegerList: { 1.5, 1 }

// Within If
define NullIf: if true then null else 1
define IntegerIf: if true then 1 else null
define DecimalIf: if true then 1 else 1.0

// Within Case
define NullCase: case when true then null else 1 end
define IntegerCase: case when true then 1 else null end
define DecimalCase: case when true then 1 else 1.0 end
define DecimalIntegerCase: case when true then 1.0 else 1 end
define NullComparandCase: case 1 when null then 0 else 1 end
define IntegerComparandCase: case 1.0 when 1 then 1.0 else 1 end

// Implicit Interval Conversions
define ImplicitIntervalConversion: Interval[3.0, 5.0] = Interval[3, 5]
