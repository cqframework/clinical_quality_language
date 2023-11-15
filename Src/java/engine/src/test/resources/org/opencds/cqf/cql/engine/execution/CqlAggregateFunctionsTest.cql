library CqlAggregateFunctionsTest

//AllTrue
define AllTrueAllTrue               : AllTrue({true,true})
define AllTrueTrueFirst             : AllTrue({true,false})
define AllTrueFalseFirst            : AllTrue({false,true})
define AllTrueAllTrueFalseTrue      : AllTrue({true,false,true})
define AllTrueAllFalseTrueFalse     : AllTrue({false,true,false})
define AllTrueNullFirst             : AllTrue({null,true,true})
define AllTrueEmptyList             : AllTrue({})

//AnyTrue
define AnyTrueAllTrue               : AnyTrue({true,true})
define AnyTrueAllFalse              : AnyTrue({false,false})
define AnyTrueAllTrueFalseTrue      : AnyTrue({true,false,true})
define AnyTrueAllFalseTrueFalse     : AnyTrue({false,true,false})
define AnyTrueTrueFirst             : AnyTrue({true,false})
define AnyTrueFalseFirst            : AnyTrue({false,true})
define AnyTrueNullFirstThenTrue     : AnyTrue({null,true})
define AnyTrueNullFirstThenFalse    : AnyTrue({null,false})
define AnyTrueEmptyList             : AnyTrue({})

//Avg
define AvgTest1: Avg({ 1.0, 2.0, 3.0, 6.0 })

//Product
define Product_Long: Product({5L, 4L, 5L})

//Count
define CountTest1: Count({ 15, 5, 99, null, 1 })
define CountTestDateTime: Count({ DateTime(2014), DateTime(2001), DateTime(2010) })
define CountTestTime: Count({ @T15:59:59.999, @T05:59:59.999, @T20:59:59.999 })
define CountTestNull: Count({})

//Max
define MaxTestInteger: Max({ 5, 12, 1, 15, 0, 4, 90, 44 })
define MaxTestLong: Max({ 5L, 12L, 1L, 15L, 0L, 4L, 90L, 44L })
define MaxTestString: Max({ 'hi', 'bye', 'zebra' })
define MaxTestDateTime: Max({ DateTime(2012, 10, 5), DateTime(2012, 9, 5), DateTime(2012, 10, 6) })
define MaxTestTime: Max({ @T15:59:59.999, @T05:59:59.999, @T20:59:59.999 })

//Median
define MedianTestDecimal: Median({6.0, 5.0, 4.0, 3.0, 2.0, 1.0})

//Min
define MinTestInteger: Min({5, 12, 1, 15, 0, 4, 90, 44})
define MinTestLong: Min({5L, 12L, 1L, 15L, 0L, 4L, 90L, 44L})
define MinTestString: Min({'hi', 'bye', 'zebra'})
define MinTestDateTime: Min({ DateTime(2012, 10, 5), DateTime(2012, 9, 5), DateTime(2012, 10, 6) })
define MinTestTime: Min({ @T15:59:59.999, @T05:59:59.999, @T20:59:59.999 })

//Mode
define ModeTestInteger: Mode({ 2, 1, 8, 2, 9, 1, 9, 9 })
define ModeTestDateTime: Mode({ DateTime(2012, 10, 5), DateTime(2012, 9, 5), DateTime(2012, 10, 6), DateTime(2012, 9, 5) })
define ModeTestDateTime2: Mode({ DateTime(2012, 10, 5), DateTime(2012, 10, 5), DateTime(2012, 10, 6), DateTime(2012, 9, 5) })
define ModeTestTime: Mode({ @T15:59:59.999, @T05:59:59.999, @T20:59:59.999, @T05:59:59.999 })

//PopulationStdDev
define PopStdDevTest1: PopulationStdDev({ 1.0, 2.0, 3.0, 4.0, 5.0 })

//PopulationVariance
define PopVarianceTest1: PopulationVariance({ 1.0, 2.0, 3.0, 4.0, 5.0 })

//StdDev
define StdDevTest1: StdDev({ 1.0, 2.0, 3.0, 4.0, 5.0 })

//Sum
define SumTest1: Sum({ 6.0, 2.0, 3.0, 4.0, 5.0 })
define SumTestLong: Sum({ 6L, 2L, 3L, 4L, 5L })
define SumTestQuantity: Sum({1 'ml',2 'ml',3 'ml',4 'ml',5 'ml'})
define SumTestNull: Sum({ null, 1, null })

//Variance
define VarianceTest1: Variance({ 1.0, 2.0, 3.0, 4.0, 5.0 })