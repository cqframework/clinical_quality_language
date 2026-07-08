library CqlErrorTestSuite version '2'

/*
    NOTES:
    This test suite is meant to serve as a comprehensive, portable test suite for Any implementation of a CQL execution engine.

    There are 3 components of this suite (of which this file is one):
      1. Optional Internal Type/value tests
      2. Valid operator tests
      3. Invalid operator tests (All these tests result in a runtime error) (this file)

    This suite is an extension of the Bonnie tests, which can be found here:
       https://github.com/cqframework/cql-execution/tree/master/test/elm
*/

/*
*************************
  Start Aggregate Tests
*************************
*/

// TODO - implement for Quantity
// define Min_q_throw1: Min({1 'ml',2 'm',3 'ml',4 'ml',5 'ml',0 'ml'})
// define Min_q_throw2: Min({1 ,2 'ml',3 'ml',4 'ml',5 'ml',0 'ml'})
// TODO - implement for Quantity
// define Max_q_throw1: Max({10 'ml',1 'm',2 'ml',3 'ml',4 'ml',5 'ml'})
// define Max_q_throw2: Max({10 ,1 'ml',2 'ml',3 'ml',4 'ml',5 'ml'})
// TODO - implement for Quantity
// define Avg_q_throw1: Avg({1 'ml',0.002 'm',0.03 'dl',4 'ml',5 'ml'})
// TODO - implement for Quantity
// define Median_q_throw1: Median({5 'ml',0.001 'l',0.22 'dl',3 'm',4 'h',6 'ml'})
// define Median_q_throw2: Median({5 ,1 ,2 ,3 ,4 'ml',6 'ml'})
// TODO - implement for Quantity
// define Variance_q_throw1: Variance({1.0 'm',2.0 'l',3.0 'h',4.0 'ml',5.0 'ml'})
// define Variance_q_throw2: Variance({1.0 ,2.0 ,3.0 ,4.0 'ml',5.0 'ml'})
// TODO - implement for Quantity
// define PopulationVariance_q_throw1: PopulationVariance({1.0 'm',2.0 'l',3.0 'h',4.0 'ml',5.0 'ml'})
// define PopulationVariance_q_throw2: PopulationVariance({1.0 ,2.0 ,3.0 ,4.0 'ml',5.0 'ml'})
// TODO - implement for Quantity
// define Std_sq_throw1: StdDev({1 'ml',2 'ml',3 'ml',4 'ml',5 'm'})
// define Std_q_throw2: StdDev({1 ,2 ,3 ,4 'ml',5 })
// TODO - implement for Quantity
// define PopulationStd_q_throw1: PopulationStdDev({1 'ml',2 'ml',3 'ml',4 'ml',5 'm'})
// define PopulationStd_q_throw2: PopulationStdDev({1 ,2 ,3 ,4 'ml',5 })

/*
***********************
  End Aggregate Tests
***********************
*/

/*
**************************
  Start Arithmetic Tests
**************************
*/

define MinWrongType: minimum Ratio
define MaxWrongType: maximum Ratio
define Successor_max_date: successor of DateTime(9999,12,31,23,59,59,999)
define Successor_ofr: successor of 2147483647
define Predecessor_min_date: predecessor of DateTime(0001,01,01,0,0,0,0)
define Predecessor_ufr: predecessor of -2147483648

/*
************************
  End Arithmetic Tests
************************
*/

/*
**************************
  Start Comparison Tests
**************************
*/

// TODO - implement for Quantity
// define Equal_AGtB_Quantity_incompatible: 5 'Cel' = 4 'm'
// define Equal_AEqB_Quantity_incompatible: 5 'Cel' = 5 'm'
// define Equal_ALtB_Quantity_incompatible: 5 'Cel' = 40 'm'
// TODO - implement for Quantity
// define NotEqual_AGtB_Quantity_incompatible: 5 'Cel' != 4 'm'
// define NotEqual_AEqB_Quantity_incompatible: 5 'Cel' != 5 'm'
// define NotEqual_ALtB_Quantity_incompatible: 5 'Cel' != 40 'm'
// TODO - these result in translation errors
// define Equivalent_TupleAndList: {a: '1'} ~ {'1'}
// define Equivalent_ListAndTuple: {'1'} ~ {a: '1'}
// TODO - implement for Quantity
// define Less_AGtB_Quantity_incompatible: 5 'Cel' < 4 'm'
// define Less_AEqB_Quantity_incompatible: 5 'Cel' < 5 'm'
// define Less_ALtB_Quantity_incompatible: 5 'Cel' < 40 'm'
// TODO - implement for Quantity
// define AGtB_Quantity_incompatible: 5 'Cel' <= 4 'm'
// define AEqB_Quantity_incompatible: 5 'Cel' <= 5 'm'
// define ALtB_Quantity_incompatible: 5 'Cel' <= 40 'm'
// TODO - implement for Quantity
// define Greater_AGtB_Quantity_incompatible: 5 'Cel' > 4 'm'
// define Greater_AEqB_Quantity_incompatible: 5 'Cel' > 5 'm'
// define Greater_ALtB_Quantity_incompatible: 5 'Cel' > 40 'm'
// TODO - implement for Quantity
// define AGtB_Quantity_incompatible: 5 'Cel' >= 4 'm'
// define AEqB_Quantity_incompatible: 5 'Cel' >= 5 'm'
// define ALtB_Quantity_incompatible: 5 'Cel' >= 40 'm'
// define DivideUcum_incompatible: (100 '[nmi_i]' / 2 'h') > 49 'mg/[lb_av]'

/*
************************
  End Comparison Tests
************************
*/

/*
**************************
  Start Conversion Tests
**************************
*/

/*
************************
  End Conversion Tests
************************
*/

