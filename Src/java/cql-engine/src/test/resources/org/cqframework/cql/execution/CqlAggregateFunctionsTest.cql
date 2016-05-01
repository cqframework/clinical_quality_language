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

//Count

//Max

//Median

//Min

//Mode

//PopulationStdDev

//PopulationVariance

//StdDev

//Sum

//Variance