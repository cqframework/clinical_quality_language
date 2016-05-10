library AggregateFunctions version '1'

context Population

//AllTrue
define AllTrueAllTrue               : AllTrue({true,true})
define AllTrueTrueFirst             : AllTrue({true,false})
define AllTrueFalseFirst            : AllTrue({false,true})
define AllTrueAllTrueFalseTrue      : AllTrue({true,false,true})
define AllTrueAllFalseTrueFalse     : AllTrue({false,true,false})
define AllTrueNullFirst             : AllTrue({null,true,true})
define AllTrueEmptyList             : AllTrue({})

//AnyTrue
define AnyTrueNull                  : AnyTrue(null)
define AnyTrueEmptyList             : AnyTrue({})
define AnyTrueNullList              : AnyTrue({null, null})
define AnyTrueAllTrue               : AnyTrue({true,true})
define AnyTrueAllFalse              : AnyTrue({false,false})
define AnyTrueAllTrueFalseTrue      : AnyTrue({true,false,true})
define AnyTrueAllFalseTrueFalse     : AnyTrue({false,true,false})
define AnyTrueTrueFirst             : AnyTrue({true,false})
define AnyTrueFalseFirst            : AnyTrue({false,true})
define AnyTrueNullFirstThenTrue     : AnyTrue({null,true})
define AnyTrueNullFirstThenFalse    : AnyTrue({null,false})

//Avg
//define AvgEmptyList             : Avg({})
//define AvgNullNull              : Avg({null, null})
define Avg12Null3               : Avg({1.0, 2.0, null, 3.0})
define Avg22               : Avg({2.0, 2.0})
define Avg12               : Avg({1.0, 2.0})

//Count
define CountEmptyList: Count({})
define CountNullNull: Count({null, null})
define Count12Null3: Count({1.0, 2.0, null, 3.0})
define Count22: Count({2.0, 2.0})
define CountAB: Count({'a', 'b'})

//Max
define Max12Null3: Max({1.0, 2.0, null, 3.0})
define Max12: Max({1, 2})
define MaxAB: Max({'a', 'b'})
define Max1CM2CM: Max({1'cm', 2'cm'})
define Max1M2CM: Max({1'm', 2'cm'})

//Median

//Min

//Mode

//PopulationStdDev

//PopulationVariance

//StdDev

//Sum

//Variance