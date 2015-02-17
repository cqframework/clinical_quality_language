// @Test: Count
define not_null= Count({1,2,3,4,5})
define has_null = Count({1,null,null,null,2})
define empty = Count({})

// @Test: Sum
define not_null= Sum({1,2,3,4,5})
define has_null = Sum({1,null,null,null,2})
define empty = Sum({})

// @Test: Min
define not_null= Min({1,2,3,4,5,0})
define has_null = Min({1,null,-1,null,2})
define empty = Min({})

// @Test: Max
define not_null= Max({10,1,2,3,4,5})
define has_null = Max({1,null,null,2})
define empty = Max({})

// @Test: Avg
define not_null= Avg({1,2,3,4,5})
define has_null = Avg({1,null,null,2})
define empty = Avg({})

// @Test: Median
define odd= Median({5,1,2,3,4})
define even= Median({5,1,2,3,4,6})

define empty = Median({})
define has_null = Median({1,null,null,2})
define dup_vals_even = Median({3,1,2,2,2,3,4,5})
define dup_vals_odd =  Median({3,1,2,2,2,3,4,5,6})

// @Test: Mode
define not_null= Mode({1,2,2,2,3,4,5})
define has_null = Mode({1,null,null,2,2})
define empty = Mode({})

define bi_modal= Mode({1,2,2,2,3,3,3,4,5})

// @Test: Variance
define v = Variance({1,2,3,4,5})

// @Test: PopulationVariance
define v = PopulationVariance({1.0,2.0,3.0,4.0,5.0})

// @Test: StdDev
define std = StdDev({1,2,3,4,5})

// @Test: PopulationStdDev
define dev = PopulationStdDev({1,2,3,4,5})

// @Test: AllTrue
define at = AllTrue({true,true,true,true})
define atwn = AllTrue({true,true,null,null,true,true})

define atf = AllTrue({true,true,true,false})
define atfwn = AllTrue({true,true,null,null,true,false})

// @Test: AnyTrue
define at = AnyTrue({true,false,false,true})
define atwn = AnyTrue({true,false,null,null,false,true})

define atf = AnyTrue({false,false,false,false})
define atfwn = AnyTrue({false,false,null,null,false,false})
