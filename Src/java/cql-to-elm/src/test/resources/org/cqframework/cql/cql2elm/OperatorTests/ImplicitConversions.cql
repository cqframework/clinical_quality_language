// Integer to Decimal
define ThisShouldWork = 5 = 5.5
define IntegerDecimalMultiplication = 5 * 5.0
define DecimalIntegerMultiplication = 5.0 * 5

// null to any type
define IntegerAnyMultiplication = 5 * null

// Within IfNull
define NullIfNull = IfNull(null, 1)
define IntegerIfNull = IfNull(1, 5.0)
define DecimalIfNull = IfNull(1.0, 5)

// Within Coalesce
define NullCoalesce = coalesce ( null, 1, 2, 3 )
define IntegerCoalesce = coalesce ( 1, 2.0, 3.0, null )
define DecimalCoalesce = coalesce ( 1.0, 2, 3, null )

// Within Interval selector
define IntegerIntervalSelector = interval[5, null]
define DecimalIntervalSelector = interval[5, 5.0]

// Within List selector
define EmptyList = { }
define IntegerList = { null, 1 }
define DecimalList = { 1, 1.5 }
define DecimalIntegerList = { 1.5, 1 }
define ExplicitIntegerList = list<Integer> { null, 1, 2, 3 }
define ExplicitDecimalList = list<Decimal> { 1.0, null, 3 }

// Within If
define NullIf = if true then null else 1
define IntegerIf = if true then 1 else null
define DecimalIf = if true then 1 else 1.0

// Within Case
define NullCase = case when true then null else 1 end
define IntegerCase = case when true then 1 else null end
define DecimalCase = case when true then 1 else 1.0 end
define DecimalIntegerCase = case when true then 1.0 else 1 end
define NullComparandCase = case 1 when null then 0 else 1 end
define IntegerComparandCase = case 1.0 when 1 then 1.0 else 1 end
