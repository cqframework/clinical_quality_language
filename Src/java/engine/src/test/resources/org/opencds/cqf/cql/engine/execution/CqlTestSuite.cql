library CqlTestSuite version '2'

codesystem "SNOMED-CT": 'http://snomed.info/sct'

/*
    NOTES:
    This test suite is meant to serve as a comprehensive, portable test suite for Any implementation of a CQL execution engine.

    There are 3 components of this suite (of which this file is one):
      1. Optional Internal Type/value tests
      2. Valid operator tests (this file)
      3. Invalid operator tests (All these tests result in a runtime error)

    This suite is an extension of the Bonnie tests, which can be found here:
       https://github.com/cqframework/cql-execution/tree/master/test/elm
*/

define function TestMessage(condition Boolean, testName String, expected String, result String):
  if condition
    then testName + ' TEST PASSED'
  else Message('TEST SUITE FAILED', true, 'Test ' + testName + ' failed', 'Warning', 'Expected ' + expected + ' Found ' + result)

define function toString(value Boolean):
  if value is null
    then 'null'
  else ToString(value)

define function toString(value Decimal):
  if value is null
    then 'null'
  else ToString(value)

define function toString(value Integer):
  if value is null
    then 'null'
  else ToString(value)

define function toString(value DateTime):
  if value is null
    then 'null'
  else ToString(value)

define function toString(value Date):
  if value is null
    then 'null'
  else ToString(value)

define function toString(value Time):
  if value is null
    then 'null'
  else ToString(value)

define function toString(value Quantity):
  if value is null
    then 'null'
  else ToString(value)

define function toString(value Code):
  if value is null
    then 'null'
  else 'Code { code: ' + value.code + ' system: ' + value.system + ' version: ' + value.version + ' display: ' + value.display + ' }'

define function toString(value List<Code>):
  if value is null
    then 'null'
  else Combine((value V return toString(V)), ', ')

define function toString(value Concept):
  if value is null
    then 'null'
  else 'Concept { ' + toString(value.codes) + ' display: ' + value.display + ' }'

define function toString(value Tuple{a Integer}):
  if value is null
    then 'null'
  else 'Tuple{ a: ' + ToString(value.a) + ' }'

define function toString(value Tuple{hello String}):
  if value is null
    then 'null'
  else 'Tuple{ hello: ' + value.hello + ' }'

define function toString(value Tuple{a Integer, b String}):
  if value is null
    then 'null'
  else 'Tuple{ a: ' + ToString(value.a) + ', b: ' + value.b + ' }'

define function toString(value Tuple{a Integer, b Integer, c Integer}):
  if value is null
    then 'null'
  else 'Tuple{ a: ' + ToString(value.a) + ', b: ' + ToString(value.b) + ', c: ' + ToString(value.c) + ' }'

define function toString(value List<Integer>):
  if value is null
    then 'null'
  else Combine((value V return toString(V)), ', ')

define function toString(value Interval<Integer>):
  if value is null
    then 'null'
  else '[' + toString(start of value) + ', ' + toString(end of value) + ']'

define function toString(value Interval<Decimal>):
  if value is null
    then 'null'
  else '[' + toString(start of value) + ', ' + toString(end of value) + ']'

define function toString(value Interval<Quantity>):
  if value is null
    then 'null'
  else '[' + toString(start of value) + ', ' + toString(end of value) + ']'

define function toString(value Interval<DateTime>):
  if value is null
    then 'null'
  else '[' + toString(start of value) + ', ' + toString(end of value) + ']'

define function toString(value Interval<Date>):
  if value is null
    then 'null'
  else '[' + toString(start of value) + ', ' + toString(end of value) + ']'

define function toString(value Interval<Time>):
  if value is null
    then 'null'
  else '[' + toString(start of value) + ', ' + toString(end of value) + ']'

define function toString(value List<Interval<Integer>>):
  if value is null
    then 'null'
  else Combine((value V return toString(V)), ', ')

define function toString(value List<Interval<Decimal>>):
  if value is null
    then 'null'
  else Combine((value V return toString(V)), ', ')

define function toString(value List<Interval<Quantity>>):
  if value is null
    then 'null'
  else Combine((value V return toString(V)), ', ')

define function toString(value List<Interval<DateTime>>):
  if value is null
    then 'null'
  else Combine((value V return toString(V)), ', ')

define function toString(value List<Interval<Date>>):
  if value is null
    then 'null'
  else Combine((value V return toString(V)), ', ')

define function toString(value List<Interval<Time>>):
  if value is null
    then 'null'
  else Combine((value V return toString(V)), ', ')

define function toString(value List<Tuple{a Integer}>):
  if value is null
    then 'null'
  else Combine((value V return toString(V)), ', ')

define function toString(value List<Tuple{hello String}>):
  if value is null
    then 'null'
  else Combine((value V return toString(V)), ', ')

define function toString(value List<Tuple{a Integer, b String}>):
  if value is null
    then 'null'
  else Combine((value V return toString(V)), ', ')

define function toString(value List<Tuple{a Integer, b Integer, c Integer}>):
  if value is null
    then 'null'
  else Combine((value V return toString(V)), ', ')

/*
*************************
  Start Aggregate Tests
*************************
*/

// Count
define Count_not_null: Count({1,2,3,4,5})
define Count_has_null: Count({1,null,null,null,2})
define Count_empty: Count({})

define test_Count_not_null: TestMessage(Count_not_null = 5, 'Count_not_null', toString(5), toString(Count_not_null))
define test_Count_has_null: TestMessage(Count_has_null = 2, 'Count_has_null', toString(2), toString(Count_not_null))
define test_Count_empty: TestMessage(Count_empty = 0, 'Count_empty', toString(0), toString(Count_empty))

// Sum
define Sum_not_null: Sum({1,2,3,4,5})
define Sum_has_null: Sum({1,null,null,null,2})
define Sum_not_null_q: Sum({1 'ml',2 'ml',3 'ml',4 'ml',5 'ml'})
define Sum_has_null_q: Sum({1 'ml',null,null,null,2 'ml'})
define Sum_unmatched_units_q: Sum({1 'ml',2 'L',3 'ml',4 'ml',5 'ml',0 'ml'})
define Sum_empty: Sum(List<Integer>{})
define Sum_q_diff_units: Sum({1 'ml',0.002 'l',0.03 'dl',4 'ml',0.005 'l'})

define test_Sum_not_null: TestMessage(Sum_not_null = 15, 'Sum_not_null', toString(15), toString(Sum_not_null))
define test_Sum_has_null: TestMessage(Sum_has_null = 3, 'Sum_has_null', toString(3), toString(Sum_has_null))
define test_Sum_not_null_q: TestMessage(Sum_not_null_q = 15'ml', 'Sum_not_null_q', toString(15'ml'), toString(Sum_not_null_q))
define test_Sum_has_null_q: TestMessage(Sum_has_null_q = 3'ml', 'Sum_has_null_q', toString(3'ml'), toString(Sum_has_null_q))
// TODO - this behavior has yet to be implemented for Quantity
// define test_Sum_unmatched_units_q: TestMessage(Sum_unmatched_units_q is null, 'Sum_unmatched_units_q', 'null', toString(Sum_unmatched_units_q))
define test_Sum_empty: TestMessage(Sum_empty is null, 'Sum_empty', 'null', toString(Sum_empty))
// TODO - this behavior has yet to be implemented for Quantity
// define test_Sum_q_diff_units: TestMessage(Sum_q_diff_units = 15'ml', 'Sum_q_diff_units', toString(15'ml'), toString(Sum_q_diff_units))

// Min
define Min_not_null: Min({1,2,3,4,5,0})
define Min_has_null: Min({1,null,-1,null,2})
define Min_empty: Min(List<Integer>{})
define Min_not_null_q: Min({1 'ml',2 'ml',3 'ml',4 'ml',5 'ml',0 'ml'})
define Min_has_null_q: Min({1 'ml',null,-1 'ml',null,2 'ml'})
define Min_q_diff_units: Min({1 'ml',2 'dl',3 'l',4 'l',5 'l',0 'ml'})

define test_Min_not_null: TestMessage(Min_not_null = 0, 'Min_not_null', toString(0), toString(Min_not_null))
define test_Min_has_null: TestMessage(Min_has_null = -1, 'Min_has_null', toString(-1), toString(Min_has_null))
define test_Min_empty: TestMessage(Min_empty is null, 'Min_empty', 'null', toString(Min_empty))
define test_Min_not_null_q: TestMessage(Min_not_null_q = 0 'ml', 'Min_not_null_q', toString(0 'ml'), toString(Min_not_null_q))
define test_Min_has_null_q: TestMessage(Min_has_null_q = -1 'ml', 'Min_has_null_q', toString(-1 'ml'), toString(Min_has_null_q))
// TODO - this behavior has yet to be implemented for Quantity - this test will succeed by accident
// define test_Min_q_diff_units: TestMessage(Min_q_diff_units = 0 'ml', 'Min_q_diff_units', toString(0 'ml'), toString(Min_q_diff_units))

// Max
define Max_not_null: Max({10,1,2,3,4,5})
define Max_has_null: Max({1,null,null,2})
define Max_empty: Max(List<Integer>{})
define Max_not_null_q: Max({10 'ml',1 'ml',2 'ml',3 'ml',4 'ml',5 'ml'})
define Max_has_null_q: Max({1 'ml',null,null,2 'ml'})
define Max_q_diff_units: Max({10 'ml',1 'ml',2 'ml',3 'ml',4 'ml',5 'l'})

define test_Max_not_null: TestMessage(Max_not_null = 10, 'Max_not_null', toString(10), toString(Max_not_null))
define test_Max_has_null: TestMessage(Max_has_null = 2, 'Max_has_null', toString(2), toString(Max_has_null))
define test_Max_empty: TestMessage(Max_empty is null, 'Max_empty', 'null', toString(Max_empty))
define test_Max_not_null_q: TestMessage(Max_not_null_q = 10 'ml', 'Max_not_null_q', toString(10 'ml'), toString(Max_not_null_q))
define test_Max_has_null_q: TestMessage(Max_has_null_q = 2 'ml', 'Max_has_null_q', toString(2 'ml'), toString(Max_has_null_q))
// TODO - this behavior has yet to be implemented for Quantity
// define test_Max_q_diff_units: TestMessage(Max_q_diff_units = 5000 'ml', 'Max_q_diff_units', toString(5000 'ml'), toString(Max_q_diff_units))

// Avg
define Avg_not_null: Avg({1,2,3,4,5})
define Avg_has_null: Avg({1,null,null,2})
define Avg_empty: Avg(List<Integer>{})
define Avg_not_null_q: Avg({1 'ml',2 'ml',3 'ml',4 'ml',5 'ml'})
define Avg_has_null_q: Avg({1 'ml',null,null,2 'ml'})
define Avg_q_diff_units: Avg({1 'ml',0.002 'l',0.03 'dl',4 'ml',5 'ml'})

define test_Avg_not_null: TestMessage(Avg_not_null = 3.0, 'Avg_not_null', toString(3.0), toString(Avg_not_null))
define test_Avg_has_null: TestMessage(Avg_has_null = 1.5, 'Avg_has_null', toString(1.5), toString(Avg_has_null))
define test_Avg_empty: TestMessage(Avg_empty is null, 'Avg_empty', 'null', toString(Avg_empty))
define test_Avg_not_null_q: TestMessage(Avg_not_null_q = 3.0 'ml', 'Avg_not_null_q', toString(3.0 'ml'), toString(Avg_not_null_q))
define test_Avg_has_null_q: TestMessage(Avg_has_null_q = 1.5 'ml', 'Avg_has_null_q', toString(1.5 'ml'), toString(Avg_has_null_q))
// TODO - this behavior has yet to be implemented for Quantity
// define test_Avg_q_diff_units: TestMessage(Avg_q_diff_units = 3.0 'ml', 'Avg_q_diff_units', toString(3.0 'ml'), toString(Avg_q_diff_units))

// Median
define Median_odd: Median({5,1,2,3,4})
define Median_even: Median({5,1,2,3,4,6})
define Median_odd_q: Median({5 'ml',1 'ml',2 'ml',3 'ml',4 'ml'})
define Median_even_q: Median({5 'ml',1 'ml',2 'ml',3 'ml',4 'ml',6 'ml'})
define Median_empty: Median(List<Integer>{})
define Median_has_null: Median({1,null,null,2})
define Median_dup_vals_even: Median({3,1,2,2,2,3,4,5})
define Median_dup_vals_odd:  Median({3,1,2,2,2,3,4,5,6})
define Median_has_null_q: Median({1 'ml',null,null,2 'ml'})
define Median_dup_vals_even_q: Median({3 'ml',1 'ml',2 'ml',2 'ml',2 'ml',3 'ml',4 'ml',5 'ml'})
define Median_dup_vals_odd_q:  Median({3 'ml',1 'ml',2 'ml',2 'ml',2 'ml',3 'ml',4 'ml',5 'ml',6 'ml'})
define Median_q_diff_units: Median({5 'ml',0.001 'l',0.02 'dl',3 'ml',4 'ml',6 'ml'})

// TODO: possible issue in translator causing Median_odd = 3 to fail (Integer not being implicitly promoted to Decimal)
define test_Median_odd: TestMessage(Median_odd = 3.0, 'Median_odd', toString(3), toString(Median_odd))
define test_Median_even: TestMessage(Median_even = 3.5, 'Median_even', toString(3.5), toString(Median_even))
define test_Median_odd_q: TestMessage(Median_odd_q = 3 'ml', 'Median_odd_q', toString(3 'ml'), toString(Median_odd_q))
define test_Median_even_q: TestMessage(Median_even_q = 3.5 'ml', 'Median_even_q', toString(3.5 'ml'), toString(Median_even_q))
define test_Median_empty: TestMessage(Median_empty is null, 'Median_empty', 'null', toString(Median_empty))
define test_Median_has_null: TestMessage(Median_has_null = 1.5, 'Median_has_null', toString(1.5), toString(Median_has_null))
define test_Median_dup_vals_even: TestMessage(Median_dup_vals_even = 2.5, 'Median_dup_vals_even', toString(2.5), toString(Median_dup_vals_even))
// TODO: possible issue in translator causing Median_dup_vals_odd = 3 to fail (Integer not being implicitly promoted to Decimal)
define test_Median_dup_vals_odd: TestMessage(Median_dup_vals_odd = 3.0, 'Median_dup_vals_odd', toString(3), toString(Median_dup_vals_odd))
define test_Median_has_null_q: TestMessage(Median_has_null_q = 1.5 'ml', 'Median_has_null_q', toString(1.5 'ml'), toString(Median_has_null_q))
define test_Median_dup_vals_even_q: TestMessage(Median_dup_vals_even_q = 2.5 'ml', 'Median_dup_vals_even_q', toString(2.5 'ml'), toString(Median_dup_vals_even_q))
define test_Median_dup_vals_odd_q: TestMessage(Median_dup_vals_odd_q = 3 'ml', 'Median_dup_vals_odd_q', toString(3 'ml'), toString(Median_dup_vals_odd_q))
// TODO - this behavior has yet to be implemented for Quantity
// define test_Median_q_diff_units: TestMessage(Median_q_diff_units = 3.5 'ml', 'Median_q_diff_units', toString(3.5 'ml'), toString(Median_q_diff_units))

// Mode
define Mode_not_null: Mode({1,2,2,2,3,4,5})
define Mode_has_null: Mode({1,null,null,2,2})
define Mode_empty: Mode(List<Integer>{})
define Mode_bi_modal: Mode({1,2,2,2,3,3,3,4,5})

define test_Mode_not_null: TestMessage(Mode_not_null = 2, 'Mode_not_null', toString(2), toString(Mode_not_null))
define test_Mode_has_null: TestMessage(Mode_has_null = 2, 'Mode_has_null', toString(2), toString(Mode_has_null))
define test_Mode_empty: TestMessage(Mode_empty is null, 'Mode_empty', 'null', toString(Mode_empty))
// TODO - CQL doesn't support multimodal operations
// define test_Mode_bi_modal: TestMessage(Mode_bi_modal = { 2, 3 }, 'Mode_bi_modal', '{ 2, 3 }', toString(Mode_bi_modal))

// Variance
define Variance_v: Variance({1,2,3,4,5})
define Variance_v_q: Variance({1 'ml',2 'ml',3 'ml',4 'ml',5 'ml'})
define Variance_q_diff_units: Variance({1.0 'ml',0.002 'l',0.003 'l',0.04 'dl',5.0 'ml'})

define test_Variance_v: TestMessage(Variance_v = 2.5, 'Variance_v', toString(2.5), toString(Variance_v))
define test_Variance_v_q: TestMessage(Variance_v_q = 2.5 'ml', 'Variance_v_q', toString(2.5 'ml'), toString(Variance_v_q))
// TODO - this behavior has yet to be implemented for Quantity
// define test_Variance_q_diff_units: TestMessage(Variance_q_diff_units = 2.5 'ml', 'Variance_q_diff_units', toString(2.5 'ml'), toString(Variance_q_diff_units))

// PopulationVariance
define PopulationVariance_v: PopulationVariance({1.0,2.0,3.0,4.0,5.0})
define PopulationVariance_v_q: PopulationVariance({1.0 'ml',2.0 'ml',3.0 'ml',4.0 'ml',5.0 'ml'})
define PopulationVariance_q_diff_units: PopulationVariance({1.0 'ml',0.002 'l',0.003 'l',0.04 'dl',5.0 'ml'})

define test_PopulationVariance_v: TestMessage(PopulationVariance_v = 2.0, 'PopulationVariance_v', toString(2.0), toString(PopulationVariance_v))
define test_PopulationVariance_v_q: TestMessage(PopulationVariance_v_q = 2.0 'ml', 'PopulationVariance_v_q', toString(2.0 'ml'), toString(PopulationVariance_v_q))
// TODO - this behavior has yet to be implemented for Quantity
// define test_PopulationVariance_q_diff_units: TestMessage(PopulationVariance_q_diff_units = 2.0 'ml', 'PopulationVariance_q_diff_units', toString(2.0 'ml'), toString(PopulationVariance_q_diff_units))

// StdDev
define Std: StdDev({1,2,3,4,5})
define Std_q: StdDev({1 'ml',2 'ml',3 'ml',4 'ml',5 'ml'})
define Std_q_diff_units: StdDev({1 'ml', 0.002 'l',3 'ml',4 'ml', 0.05 'dl'})

define test_Std: TestMessage(Std = 1.58113883, 'Std', toString(1.58113883), toString(Std))
define test_Std_q: TestMessage(Std_q = 1.58113883 'ml', 'Std_q', toString(1.58113883 'ml'), toString(Std_q))
// TODO - this behavior has yet to be implemented for Quantity
// define test_Std_q_diff_units: TestMessage(Std_q_diff_units = 1.58113883 'ml', 'Std_q_diff_units', toString(1.58113883 'ml'), toString(Std_q_diff_units))

// PopulationStdDev
define PopulationStd: PopulationStdDev({1,2,3,4,5})
define PopulationStd_q: PopulationStdDev({1 'ml',2 'ml',3 'ml',4 'ml',5 'ml'})
define PopulationStd_q_diff_units: PopulationStdDev({1 'ml', 0.002 'l',3 'ml',4 'ml', 0.05 'dl'})

define test_PopulationStd: TestMessage(PopulationStd = 1.41421356, 'PopulationStd', toString(1.41421356), toString(PopulationStd))
define test_PopulationStd_q: TestMessage(PopulationStd_q = 1.41421356 'ml', 'PopulationStd_q', toString(1.41421356 'ml'), toString(PopulationStd_q))
// TODO - this behavior has yet to be implemented for Quantity
// define test_PopulationStd_q_diff_units: TestMessage(PopulationStd_q_diff_units = 1.41421356 'ml', 'PopulationStd_q_diff_units', toString(1.41421356 'ml'), toString(PopulationStd_q_diff_units))

// Product
define Product_Decimal: Product({1.0, 2.0, 3.0, 4.0})
define Product_Integer: Product({5, 4, 5})
define Product_Zero: Product({0, 5, 10})
define Product_Quantity: Product({1.0 'g', 2.0 'g', 3.0 'g', 4.0 'g'})
define Product_Quantity_Zero: Product({1.0 'g', 2.0 'g', 0 'g'})
define Product_Null: Product({5, 4, null})
define Product_Nulls: Product({null as Integer, null, null})
define Product_Null_List: Product(null as List<Decimal>)
define Product_Null_Quantity: Product({null as Quantity, null as Quantity, null as Quantity})

define test_Product_Decimal: TestMessage(Product_Decimal = 24.0, 'Product_Decimal', toString(24.0), toString(Product_Decimal))
define test_Product_Integer: TestMessage(Product_Integer = 100, 'Product_Integer', toString(100), toString(Product_Integer))
define test_Product_Zero: TestMessage(Product_Zero = 0, 'Product_Zero', toString(0), toString(Product_Zero))
define test_Product_Quantity: TestMessage(Product_Quantity = 24.0'g', 'Product_Quantity', toString(24.0'g'), toString(Product_Quantity))
define test_Product_Quantity_Zero: TestMessage(Product_Quantity_Zero = 0'g', 'Product_Quantity_Zero', toString(0'g'), toString(Product_Quantity_Zero))
define test_Product_Null: TestMessage(Product_Null is null, 'Product_Null', 'null', toString(Product_Null))
define test_Product_Nulls: TestMessage(Product_Nulls is null, 'Product_Nulls', 'null', toString(Product_Nulls))
define test_Product_Null_List: TestMessage(Product_Null_List is null, 'Product_Null_List', 'null', toString(Product_Null_List))
define test_Product_Null_Quantity: TestMessage(Product_Null_Quantity is null, 'Product_Null_Quantity', 'null', toString(Product_Null_Quantity))

// GeometricMean
define GeometricMean_Decimal: GeometricMean({2.0, 8.0, null})
define GeometricMean_Zero: GeometricMean({2.0, 8.0, 0})
define GeometricMean_Null: GeometricMean({1, 2, null})
define GeometricMean_Nulls: GeometricMean({null, null, null})
define GeometricMean_Null_List: GeometricMean(null as List<Decimal>)

define test_GeometricMean_Decimal: TestMessage(GeometricMean_Decimal = 4.0, 'GeometricMean_Decimal', toString(4.0), toString(GeometricMean_Decimal))
// TODO: possible issue in translator causing GeometricMean_Zero = 0 to fail (Integer not being implicitly promoted to Decimal)
define test_GeometricMean_Zero: TestMessage(GeometricMean_Zero = 0.0, 'GeometricMean_Zero', toString(0.0), toString(GeometricMean_Zero))
define test_GeometricMean_Null: TestMessage(GeometricMean_Null = 1.41421356, 'GeometricMean_Null', toString(1.41421356), toString(GeometricMean_Null))
define test_GeometricMean_Nulls: TestMessage(GeometricMean_Nulls is null, 'GeometricMean_Nulls', 'null', toString(GeometricMean_Nulls))
define test_GeometricMean_Null_List: TestMessage(GeometricMean_Null_List is null, 'GeometricMean_Null_List', 'null', toString(GeometricMean_Null_List))

// AllTrue
define All_True: AllTrue({true,true,true,true})
define All_True_wn: AllTrue({true,true,null,null,true,true})
define All_True_f: AllTrue({true,true,true,false})
define All_True_fwn: AllTrue({true,true,null,null,true,false})

define test_All_True: TestMessage(All_True, 'All_True', toString(true), toString(All_True))
define test_All_True_wn: TestMessage(All_True_wn, 'All_True_wn', toString(true), toString(All_True_wn))
define test_All_True_f: TestMessage(not All_True_f, 'All_True_f', toString(false), toString(All_True_f))
define test_All_True_fwn: TestMessage(not All_True_fwn, 'All_True_fwn', toString(false), toString(All_True_fwn))

// AnyTrue
define Any_True: AnyTrue({true,false,false,true})
define Any_True_wn: AnyTrue({true,false,null,null,false,true})
define Any_True_f: AnyTrue({false,false,false,false})
define Any_True_fwn: AnyTrue({false,false,null,null,false,false})

define test_Any_True: TestMessage(Any_True, 'Any_True', toString(true), toString(Any_True))
define test_Any_True_wn: TestMessage(Any_True_wn, 'Any_True_wn', toString(true), toString(Any_True_wn))
define test_Any_True_f: TestMessage(not Any_True_f, 'Any_True_f', toString(false), toString(Any_True_f))
define test_Any_True_fwn: TestMessage(not Any_True_fwn, 'Any_True_fwn', toString(false), toString(Any_True_fwn))

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

define Ten: 10
define Eleven: 11

// Add
define OnePlusTwo: 1 + 2
define AddMultiple: 1 + 2 + 3 + 4 + 5 + 6 + 7 + 8 + 9 + 10
define AddVariables: Ten + Eleven

define test_OnePlusTwo: TestMessage(OnePlusTwo = 3, 'OnePlusTwo', toString(3), toString(OnePlusTwo))
define test_AddMultiple: TestMessage(AddMultiple = 55, 'AddMultiple', toString(55), toString(AddMultiple))
define test_AddVariables: TestMessage(AddVariables = 21, 'AddVariables', toString(21), toString(AddVariables))

// Subtract
define FiveMinusTwo: 5 - 2
define SubtractMultiple: 100 - 50 - 25 - 10
define SubtractVariables: Eleven - Ten

define test_FiveMinusTwo: TestMessage(FiveMinusTwo = 3, 'FiveMinusTwo', toString(3), toString(FiveMinusTwo))
define test_SubtractMultiple: TestMessage(SubtractMultiple = 15, 'SubtractMultiple', toString(15), toString(SubtractMultiple))
define test_SubtractVariables: TestMessage(SubtractVariables = 1, 'SubtractVariables', toString(1), toString(SubtractVariables))

// Multiply
define FiveTimesTwo: 5 * 2
define MultiplyMultiple: 1 * 2 * 3 * 4 * 5
define MultiplyVariables: Eleven * Ten

define test_FiveTimesTwo: TestMessage(FiveTimesTwo = 10, 'FiveTimesTwo', toString(10), toString(FiveTimesTwo))
define test_MultiplyMultiple: TestMessage(MultiplyMultiple = 120, 'MultiplyMultiple', toString(120), toString(MultiplyMultiple))
define test_MultiplyVariables: TestMessage(MultiplyVariables = 110, 'MultiplyVariables', toString(110), toString(MultiplyVariables))

// Divide
define Hundred: 100
define Four: 4
define TenDividedByTwo: 10 / 2
define TenDividedByFour: 10 / 4
define DivideMultiple: 1000 / 4 / 10 / 5
define DivideVariables: Hundred / Four

// TODO: possible issue in translator causing TenDividedByTwo = 5 to fail (Integer not being implicitly promoted to Decimal)
define test_TenDividedByTwo: TestMessage(TenDividedByTwo = 5.0, 'TenDividedByTwo', toString(5.0), toString(TenDividedByTwo))
define test_TenDividedByFour: TestMessage(TenDividedByFour = 2.5, 'TenDividedByFour', toString(2.5), toString(TenDividedByFour))
// TODO: possible issue in translator causing DivideMultiple = 5 to fail (Integer not being implicitly promoted to Decimal)
define test_DivideMultiple: TestMessage(DivideMultiple = 5.0, 'DivideMultiple', toString(5.0), toString(DivideMultiple))
// TODO: possible issue in translator causing DivideVariables = 25 to fail (Integer not being implicitly promoted to Decimal)
define test_DivideVariables: TestMessage(DivideVariables = 25.0, 'DivideVariables', toString(25.0), toString(DivideVariables))

// Negate
define NegativeOne: -1

define test_NegativeOne: TestMessage(NegativeOne = -1, 'NegativeOne', toString(-1), toString(NegativeOne))

// MathPrecedence
define Arithmetic_Mixed: 1 + 5 * 10 - 15 / 3
define Arithmetic_Parenthetical: (1 + 5) * (10 - 15) / 3

// TODO: possible issue in translator causing Arithmetic_Mixed = 46 to fail (Integer not being implicitly promoted to Decimal)
define test_Arithmetic_Mixed: TestMessage(Arithmetic_Mixed = 46.0, 'Arithmetic_Mixed', toString(46.0), toString(Arithmetic_Mixed))
// TODO: possible issue in translator causing Arithmetic_Parenthetical = -10 to fail (Integer not being implicitly promoted to Decimal)
define test_Arithmetic_Parenthetical: TestMessage(Arithmetic_Parenthetical = -10.0, 'Arithmetic_Parenthetical', toString(-10.0), toString(Arithmetic_Parenthetical))

// Power
define Pow: 3 ^ 4

define test_Pow: TestMessage(Pow = 81, 'Pow', toString(81), toString(Pow))

// MinValue
define MinInteger: minimum Integer
define MinDecimal: minimum Decimal
define MinDate: minimum Date
define MinDateTime: minimum  DateTime
define MinTime: minimum Time

define test_MinInteger: TestMessage(MinInteger = -2147483648, 'MinInteger', toString(-2147483648), toString(MinInteger))
define test_MinDecimal: TestMessage(MinDecimal = -99999999999999999999.99999999, 'MinDecimal', toString(-99999999999999999999.99999999), toString(MinDecimal))
define test_MinDate: TestMessage(MinDate = @0001-01-01, 'MinDate', toString(@0001-01-01), toString(MinDate))
define test_MinDateTime: TestMessage(MinDateTime = @0001-01-01T00:00:00.000Z, 'MinDateTime', toString(@0001-01-01T00:00:00.000Z), toString(MinDateTime))
define test_MinTime: TestMessage(MinTime = @T00:00:00.000, 'MinTime', toString(@T00:00:00.000), toString(MinTime))

// MaxValue
define MaxInteger: maximum Integer
define MaxDecimal: maximum Decimal
define MaxDate: maximum Date
define MaxDateTime: maximum DateTime
define MaxTime: maximum Time

define test_MaxInteger: TestMessage(MaxInteger = 2147483647, 'MaxInteger', toString(2147483647), toString(MaxInteger))
define test_MaxDecimal: TestMessage(MaxDecimal = 99999999999999999999.99999999, 'MaxDecimal', toString(99999999999999999999.99999999), toString(MaxDecimal))
define test_MaxDate: TestMessage(MaxDate = @9999-12-31, 'MaxDate', toString(@9999-12-31), toString(MaxDate))
define test_MaxDateTime: TestMessage(MaxDateTime = @9999-12-31T23:59:59.999Z, 'MaxDateTime', toString(@9999-12-31T23:59:59.999Z), toString(MaxDateTime))
define test_MaxTime: TestMessage(MaxTime = @T23:59:59.999, 'MaxTime', toString(@T23:59:59.999), toString(MaxTime))

// TruncatedDivide
define TruncDivide: 10 div 3
define TruncDivideEven: 9 div 3

define test_TruncDivide: TestMessage(TruncDivide = 3, 'TruncDivide', toString(3), toString(TruncDivide))
define test_TruncDivideEven: TestMessage(TruncDivideEven = 3, 'TruncDivideEven', toString(3), toString(TruncDivideEven))

// Modulo
define Mod: 3 mod 2

define test_Mod: TestMessage(Mod = 1, 'Mod', toString(1), toString(Mod))

// Ceiling
define Ceil: Ceiling(10.1)
define CeilEven: Ceiling(10)

define test_Ceil: TestMessage(Ceil = 11, 'Ceil', toString(11), toString(Ceil))
define test_CeilEven: TestMessage(CeilEven = 10, 'CeilEven', toString(10), toString(CeilEven))

// Floor
define FloorTen: Floor(10.1)
define FloorEven: Floor(10)

define test_FloorTen: TestMessage(FloorTen = 10, 'FloorTen', toString(10), toString(FloorTen))
define test_FloorEven: TestMessage(FloorEven = 10, 'FloorEven', toString(10), toString(FloorEven))

// Truncate
define Trunc: Truncate(10.1)
define TruncEven: Truncate(10)

define test_Trunc: TestMessage(Trunc = 10, 'Trunc', toString(10), toString(Trunc))
define test_TruncEven: TestMessage(TruncEven = 10, 'TruncEven', toString(10), toString(TruncEven))

// Abs
define AbsPos: Abs(10)
define AbsNeg: Abs(-10)
define AbsZero: Abs(0)

define test_AbsPos: TestMessage(AbsPos = 10, 'AbsPos', toString(10), toString(AbsPos))
define test_AbsNeg: TestMessage(AbsNeg = 10, 'AbsNeg', toString(10), toString(AbsNeg))
define test_AbsZero: TestMessage(AbsZero = 0, 'AbsZero', toString(0), toString(AbsZero))

// Round
define RoundUp: Round(4.56)
define RoundUp_percent: Round(4.56,1)
define RoundDown: Round(4.49)
define RoundDown_percent: Round(4.43,1)

// TODO: possible issue in translator causing RoundUp = 5 to fail (Integer not being implicitly promoted to Decimal)
define test_RoundUp: TestMessage(RoundUp = 5.0, 'RoundUp', toString(5.0), toString(RoundUp))
define test_RoundUp_percent: TestMessage(RoundUp_percent = 4.6, 'RoundUp_percent', toString(4.6), toString(RoundUp_percent))
// TODO: possible issue in translator causing RoundDown = 4 to fail (Integer not being implicitly promoted to Decimal)
define test_RoundDown: TestMessage(RoundDown = 4.0, 'RoundDown', toString(4.0), toString(RoundDown))
define test_RoundDown_percent: TestMessage(RoundDown_percent = 4.4, 'RoundDown_percent', toString(4.4), toString(RoundDown_percent))

// Ln
define Ln_Four: Ln(4)

define test_Ln_Four: TestMessage(Ln_Four = 1.38629436, 'Ln_Four', toString(1.38629436), toString(Ln_Four))

// Log
define Log_BaseTen: Log(10000,10)

// TODO: possible issue in translator causing Log_BaseTen = 4 to fail (Integer not being implicitly promoted to Decimal)
define test_Log_BaseTen: TestMessage(Log_BaseTen = 4.0, 'Log_BaseTen', toString(4.0), toString(Log_BaseTen))

// Successor
define Successor_Is: successor of 2
define Successor_Rs: successor of 2.2
define Successor_y_date: successor of DateTime(2015)
define Successor_ym_date: successor of DateTime(2015,01)
define Successor_ymd_date: successor of DateTime(2015,01,01)
define Successor_ymdh_date: successor of DateTime(2015,01,01,0)
define Successor_ymdhm_date: successor of DateTime(2015,01,01,0,0)
define Successor_ymdhms_date: successor of DateTime(2015,01,01,0,0,0)
define Successor_ymdhmsm_date: successor of  DateTime(2015,01,01,0,0,0,0)

define test_Successor_Is: TestMessage(Successor_Is = 3, 'Successor_Is', toString(3), toString(Successor_Is))
define test_Successor_Rs: TestMessage(Successor_Rs = 2.20000001, 'Successor_Rs', toString(2.20000001), toString(Successor_Rs))
define test_Successor_y_date: TestMessage(Successor_y_date = DateTime(2016), 'Successor_y_date', toString(DateTime(2016)), toString(Successor_y_date))
define test_Successor_ym_date: TestMessage(Successor_ym_date = DateTime(2015, 2), 'Successor_ym_date', toString(DateTime(2015, 2)), toString(Successor_ym_date))
define test_Successor_ymd_date: TestMessage(Successor_ymd_date = DateTime(2015, 1, 2), 'Successor_ymd_date', toString(DateTime(2015, 1, 2)), toString(Successor_ymd_date))
define test_Successor_ymdh_date: TestMessage(Successor_ymdh_date = DateTime(2015, 1, 1, 1), 'Successor_ymdh_date', toString(DateTime(2015, 1, 1, 1)), toString(Successor_ymdh_date))
define test_Successor_ymdhm_date: TestMessage(Successor_ymdhm_date = DateTime(2015, 1, 1, 0, 1), 'Successor_ymdhm_date', toString(DateTime(2015, 1, 1, 0, 1)), toString(Successor_ymdhm_date))
define test_Successor_ymdhms_date: TestMessage(Successor_ymdhms_date = DateTime(2015, 1, 1, 0, 0, 1), 'Successor_ymdhms_date', toString(DateTime(2015, 1, 1, 0, 0, 1)), toString(Successor_ymdhms_date))
define test_Successor_ymdhmsm_date: TestMessage(Successor_ymdhmsm_date = DateTime(2015, 1, 1, 0, 0, 0, 1), 'Successor_ymdhmsm_date', toString(DateTime(2015, 1, 1, 0, 0, 0, 1)), toString(Successor_ymdhmsm_date))

// Predecessor
define Predecessor_Is: predecessor of 2
define Predecessor_Rs: predecessor of 2.2
define Predecessor_y_date: predecessor of DateTime(2015)
define Predecessor_ym_date: predecessor of DateTime(2015,01)
define Predecessor_ymd_date: predecessor of DateTime(2015,01,01)
define Predecessor_ymdh_date: predecessor of DateTime(2015,01,01,0)
define Predecessor_ymdhm_date: predecessor of DateTime(2015,01,01,0,0)
define Predecessor_ymdhms_date: predecessor of DateTime(2015,01,01,0,0,0)
define Predecessor_ymdhmsm_date: predecessor of DateTime(2015,01,01,0,0,0,0)

define test_Predecessor_Is: TestMessage(Predecessor_Is = 1, 'Predecessor_Is', toString(1), toString(Predecessor_Is))
define test_Predecessor_Rs: TestMessage(Predecessor_Rs = 2.19999999, 'Predecessor_Rs', toString(2.19999999), toString(Predecessor_Rs))
define test_Predecessor_y_date: TestMessage(Predecessor_y_date = DateTime(2014), 'Predecessor_y_date', toString(DateTime(2014)), toString(Predecessor_y_date))
define test_Predecessor_ym_date: TestMessage(Predecessor_ym_date = DateTime(2014, 12), 'Predecessor_ym_date', toString(DateTime(2014, 12)), toString(Predecessor_ym_date))
define test_Predecessor_ymd_date: TestMessage(Predecessor_ymd_date = DateTime(2014, 12, 31), 'Predecessor_ymd_date', toString(DateTime(2014, 12, 31)), toString(Predecessor_ymd_date))
define test_Predecessor_ymdh_date: TestMessage(Predecessor_ymdh_date = DateTime(2014, 12, 31, 23), 'Predecessor_ymdh_date', toString(DateTime(2014, 12, 31, 23)), toString(Predecessor_ymdh_date))
define test_Predecessor_ymdhm_date: TestMessage(Predecessor_ymdhm_date = DateTime(2014, 12, 31, 23, 59), 'Predecessor_ymdhm_date', toString(DateTime(2014, 12, 31, 23, 59)), toString(Predecessor_ymdhm_date))
define test_Predecessor_ymdhms_date: TestMessage(Predecessor_ymdhms_date = DateTime(2014, 12, 31, 23, 59, 59), 'Predecessor_ymdhms_date', toString(DateTime(2014, 12, 31, 23, 59, 59)), toString(Predecessor_ymdhms_date))
define test_Predecessor_ymdhmsm_date: TestMessage(Predecessor_ymdhmsm_date = DateTime(2014, 12, 31, 23, 59, 59, 999), 'Predecessor_ymdhmsm_date', toString(DateTime(2014, 12, 31, 23, 59, 59, 999)), toString(Predecessor_ymdhmsm_date))

// Quantity
define Quantity_days_10: 10 days
define Quantity_QL10Days: Quantity{value: 10, unit: 'days'}
define Quantity_QL10Min : Quantity{value: 10, unit: 'min' }
define Quantity_Jan1_2000: DateTime(2000, 1, 1)
define Quantity_add_q_q : Quantity_days_10 + Quantity_QL10Days
define Quantity_add_d_q : Quantity_Jan1_2000 + Quantity_days_10
define Quantity_sub_q_q : Quantity_days_10 - Quantity_QL10Days
define Quantity_sub_d_q : Quantity_Jan1_2000 - Quantity_days_10
// TODO - implement for Quantity
// define Quantity_add_q_q_diff : Quantity_QL10Days + Quantity_QL10Min
// define Quantity_sub_q_q_diff : Quantity_QL10Days - Quantity_QL10Min
// define Quantity_div_q_q : Quantity_days_10 / QL10Days
// define Quantity_mul_q_q : 2 'm' * 10 'm'
// define Quantity_mul_q_q_diff : 2 'm' * 10 '/d'
// define Quantity_MultiplyUcum: (5 'm' * 25 'km') = 125000 'm2'
// define Quantity_DivideUcum: (20 'm2' / 5 'm') = 4 'm'
// define Quantity_AddUcum: (5 'm' + 5 'km') = 5005 'm'
// define Quantity_SubtractUcum: (25 'km' - 5 'm') = 24995 'm'
define Quantity_div_q_d : Quantity_days_10 / 2
define Quantity_mul_q_d : Quantity_days_10 * 2
define Quantity_mul_d_q : 2 * Quantity_QL10Days
define Quantity_neg : - Quantity_days_10
define Quantity_abs : Abs(Quantity_neg)

define test_Quantity_add_q_q: TestMessage(Quantity_add_q_q = 20 days, 'Quantity_add_q_q', toString(20 days), toString(Quantity_add_q_q))
define test_Quantity_add_d_q: TestMessage(Quantity_add_d_q = DateTime(2000, 1, 11), 'Quantity_add_d_q', toString(DateTime(2000, 1, 11)), toString(Quantity_add_d_q))
define test_Quantity_sub_q_q: TestMessage(Quantity_sub_q_q = 0 days, 'Quantity_sub_q_q', toString(0 days), toString(Quantity_sub_q_q))
define test_Quantity_sub_d_q: TestMessage(Quantity_sub_d_q = DateTime(1999, 12, 22), 'Quantity_sub_d_q', toString(DateTime(1999, 12, 22)), toString(Quantity_sub_d_q))
define test_Quantity_div_q_d: TestMessage(Quantity_div_q_d = 5 days, 'Quantity_div_q_d', toString(5 days), toString(Quantity_div_q_d))
define test_Quantity_mul_q_d: TestMessage(Quantity_mul_q_d = 20 days, 'Quantity_mul_q_d', toString(20 days), toString(Quantity_mul_q_d))
define test_Quantity_mul_d_q: TestMessage(Quantity_mul_d_q = 20 days, 'Quantity_mul_d_q', toString(20 days), toString(Quantity_mul_d_q))
define test_Quantity_neg: TestMessage(Quantity_neg = -10 days, 'Quantity_neg', toString(-10 days), toString(Quantity_neg))
define test_Quantity_abs: TestMessage(Quantity_abs = 10 days, 'Quantity_abs', toString(10 days), toString(Quantity_abs))


/*
************************
  End Arithmetic Tests
************************
*/


/*
************************
  Start Clinical Tests
************************
*/

// TODO

// Equal
define CodeWithoutDisplayAndVersion1: Code '66071002' from "SNOMED-CT"
define CodeWithoutDisplayAndVersion2: Code '66071003' from "SNOMED-CT"
define CodeWithoutVersion1: Code '66071002' from "SNOMED-CT" display 'Type B viral hepatitis'
define ConceptWithoutDisplayAndVersion1: Concept { codes: { CodeWithoutDisplayAndVersion1, CodeWithoutDisplayAndVersion2 } }
define ConceptWithoutDisplayAndVersion2: Concept { codes: { CodeWithoutDisplayAndVersion1 } }
define ConceptWithoutDisplay1: Concept { codes: { CodeWithoutVersion1 } }
define ConceptWithoutVersion1: Concept { codes: { CodeWithoutVersion1 }, display: 'Test Display' }

define CodeWithoutDisplayAndVersionEqual: CodeWithoutDisplayAndVersion1 = CodeWithoutDisplayAndVersion1
define CodeWithoutDisplayAndVersionNotEqual: CodeWithoutDisplayAndVersion1 = CodeWithoutDisplayAndVersion2
define CodeWithoutDisplayAndVersionNull: CodeWithoutDisplayAndVersion1 = CodeWithoutVersion1
define ConceptWithoutDisplayAndVersionEqual: ConceptWithoutDisplayAndVersion1 = ConceptWithoutDisplayAndVersion1
define ConceptWithoutDisplayAndVersionNotEqual: ConceptWithoutDisplayAndVersion1 = ConceptWithoutDisplayAndVersion2
define ConceptWithoutDisplayAndVersionNull: ConceptWithoutDisplay1 = ConceptWithoutVersion1

define test_CodeWithoutDisplayAndVersionEqual: TestMessage(CodeWithoutDisplayAndVersionEqual, 'CodeWithoutDisplayAndVersionEqual', toString(true), toString(CodeWithoutDisplayAndVersionEqual))
define test_CodeWithoutDisplayAndVersionNotEqual: TestMessage(not CodeWithoutDisplayAndVersionNotEqual, 'CodeWithoutDisplayAndVersionNotEqual', toString(false), toString(CodeWithoutDisplayAndVersionNotEqual))
define test_CodeWithoutDisplayAndVersionNull: TestMessage(CodeWithoutDisplayAndVersionNull is null, 'CodeWithoutDisplayAndVersionNull', 'null', toString(CodeWithoutDisplayAndVersionNull))
define test_ConceptWithoutDisplayAndVersionEqual: TestMessage(ConceptWithoutDisplayAndVersionEqual, 'ConceptWithoutDisplayAndVersionEqual', toString(true), toString(ConceptWithoutDisplayAndVersionEqual))
define test_ConceptWithoutDisplayAndVersionNotEqual: TestMessage(not ConceptWithoutDisplayAndVersionNotEqual, 'ConceptWithoutDisplayAndVersionNotEqual', toString(false), toString(ConceptWithoutDisplayAndVersionNotEqual))
define test_ConceptWithoutDisplayAndVersionNull: TestMessage(ConceptWithoutDisplayAndVersionNull is null, 'ConceptWithoutDisplayAndVersionNull', 'null', toString(ConceptWithoutDisplayAndVersionNull))

/*
**********************
  End Clinical Tests
**********************
*/


/*
**************************
  Start Comparison Tests
**************************
*/

// Equal
define Equal_AGtB_Int: 5 = 4
define Equal_AEqB_Int: 5 = 5
define Equal_ALtB_Int: 5 = 6
define Equal_EqTuples: Tuple{a: 1, b: Tuple{c: 1}} = Tuple{a: 1, b: Tuple{c: 1}}
define Equal_UneqTuples: Tuple{a: 1, b: Tuple{c: 1}} = Tuple{a: 1, b: Tuple{c: -1}}
//define Equal_TupleDifferentKeys: Tuple{ Foo: null } = Tuple{ Bar: null }
define Equal_EqTuplesWithNullFields: Tuple{a: 'Hello', b: null} = Tuple{a: 'Hello', b: null}
define Equal_UneqTuplesWithNullFields: Tuple{a: 'Hello', b: null} = Tuple{a: 'Goodbye', b: null}
define Equal_UncertTuplesWithNullFieldOnOne: Tuple{a: 'Hello', b: null} = Tuple{a: 'Hello', b: 'null'}
//define Equal_UncertTuplesWithDiffNullFields: Tuple{a: 'Hello', b: null} = Tuple{a: 'Hello', c: null}
define Equal_EqDateTimes: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) = DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0)
define Equal_UneqDateTimes: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) = DateTime(2000, 3, 15, 13, 30, 25, 201, +1.0)
define Equal_EqDateTimesTZ: DateTime(2000, 3, 15, 23, 30, 25, 200, +1.0) = DateTime(2000, 3, 16, 2, 30, 25, 200, +4.0)
define Equal_UneqDateTimesTZ: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) = DateTime(2000, 3, 15, 13, 30, 25, 200, +2.0)
define Equal_EqDateTimesOnlyDate: DateTime(2000, 3, 15) = DateTime(2000, 3, 15)
define Equal_UneqDateTimesOnlyDate: DateTime(2000, 3, 14) = DateTime(2000, 3, 15)
define Equal_PossiblyEqDateTimesOnlyDateOnOne: DateTime(2000, 3, 13) = DateTime(2000, 3, 13, 13, 43, 32)
define Equal_UneqDateTimesOnlyDateOnOne: DateTime(2000, 4, 13, 12, 43, 32) = DateTime(2000, 3, 13)
define Equal_PossiblyEqualDateTimes: DateTime(2000, 3, 15) = DateTime(2000)
define Equal_ImpossiblyEqualDateTimes: DateTime(2000, 3, 15) = DateTime(2000, 4)
define Equal_DateAndDateTimeEqual: Date(2000, 3, 13) = DateTime(2000, 3, 13)
define Equal_DateAndDateTimeNotEqual: Date(2000, 3, 13) = DateTime(2000, 3, 12)
define Equal_DateAndDateTimeUncertainEqual: Date(2000, 3, 13) = DateTime(2000, 3, 13, 2, 4, 23)
define Equal_DateTimeAndDateEqual: DateTime(2000, 3, 13) = Date(2000, 3, 13)
define Equal_DateTimeAndDateNotEqual: DateTime(2000, 3, 12) = Date(2000, 3, 13)
define Equal_DateTimeAndDateUncertainEqual: DateTime(2000, 3, 13, 2, 4, 23) = Date(2000, 3, 13)
define Equal_AGtB_Quantity: 5 'm' = 4 'm'
define Equal_AEqB_Quantity: 5 'm' = 5 'm'
define Equal_ALtB_Quantity: 5 'm' = 6 'm'
// TODO - implement for Quantity
// define Equal_AGtB_Quantity_diff: 5 'm' = 5 'cm'
// define Equal_AEqB_Quantity_diff: 5 'm' = 500 'cm'
// define Equal_ALtB_Quantity_diff: 5 'm' = 5 'km'

define test_Equal_AGtB_Int: TestMessage(not Equal_AGtB_Int, 'Equal_AGtB_Int', toString(false), toString(Equal_AGtB_Int))
define test_Equal_AEqB_Int: TestMessage(Equal_AEqB_Int, 'Equal_AEqB_Int', toString(true), toString(Equal_AEqB_Int))
define test_Equal_ALtB_Int: TestMessage(not Equal_ALtB_Int, 'Equal_ALtB_Int', toString(false), toString(Equal_ALtB_Int))
define test_Equal_EqTuples: TestMessage(Equal_EqTuples, 'Equal_EqTuples', toString(true), toString(Equal_EqTuples))
define test_Equal_UneqTuples: TestMessage(not Equal_UneqTuples, 'Equal_UneqTuples', toString(false), toString(Equal_UneqTuples))
//define test_Equal_TupleDifferentKeys: TestMessage(not Equal_TupleDifferentKeys, 'Equal_TupleDifferentKeys', toString(false), toString(Equal_TupleDifferentKeys))
define test_Equal_EqTuplesWithNullFields: TestMessage(Equal_EqTuplesWithNullFields, 'Equal_EqTuplesWithNullFields', toString(true), toString(Equal_EqTuplesWithNullFields))
define test_Equal_UneqTuplesWithNullFields: TestMessage(not Equal_UneqTuplesWithNullFields, 'Equal_UneqTuplesWithNullFields', toString(false), toString(Equal_UneqTuplesWithNullFields))
define test_Equal_UncertTuplesWithNullFieldOnOne: TestMessage(Equal_UncertTuplesWithNullFieldOnOne is null, 'Equal_UncertTuplesWithNullFieldOnOne', 'null', toString(Equal_UncertTuplesWithNullFieldOnOne))
//define test_Equal_UncertTuplesWithDiffNullFields: TestMessage(not Equal_UncertTuplesWithDiffNullFields, 'Equal_UncertTuplesWithDiffNullFields', toString(false), toString(Equal_UncertTuplesWithDiffNullFields))
define test_Equal_EqDateTimes: TestMessage(Equal_EqDateTimes, 'Equal_EqDateTimes', toString(true), toString(Equal_EqDateTimes))
define test_Equal_UneqDateTimes: TestMessage(not Equal_UneqDateTimes, 'Equal_UneqDateTimes', toString(false), toString(Equal_UneqDateTimes))
define test_Equal_EqDateTimesTZ: TestMessage(Equal_EqDateTimesTZ, 'Equal_EqDateTimesTZ', toString(true), toString(Equal_EqDateTimesTZ))
define test_Equal_UneqDateTimesTZ: TestMessage(not Equal_UneqDateTimesTZ, 'Equal_UneqDateTimesTZ', toString(false), toString(Equal_UneqDateTimesTZ))
define test_Equal_EqDateTimesOnlyDate: TestMessage(Equal_EqDateTimesOnlyDate, 'Equal_EqDateTimesOnlyDate', toString(true), toString(Equal_EqDateTimesOnlyDate))
define test_Equal_UneqDateTimesOnlyDate: TestMessage(not Equal_UneqDateTimesOnlyDate, 'Equal_UneqDateTimesOnlyDate', toString(false), toString(Equal_UneqDateTimesOnlyDate))
define test_Equal_PossiblyEqDateTimesOnlyDateOnOne: TestMessage(Equal_PossiblyEqDateTimesOnlyDateOnOne is null, 'Equal_PossiblyEqDateTimesOnlyDateOnOne', 'null', toString(Equal_PossiblyEqDateTimesOnlyDateOnOne))
define test_Equal_UneqDateTimesOnlyDateOnOne: TestMessage(not Equal_UneqDateTimesOnlyDateOnOne, 'Equal_UneqDateTimesOnlyDateOnOne', toString(false), toString(Equal_UneqDateTimesOnlyDateOnOne))
define test_Equal_PossiblyEqualDateTimes: TestMessage(Equal_PossiblyEqualDateTimes is null, 'Equal_PossiblyEqualDateTimes', 'null', toString(Equal_PossiblyEqualDateTimes))
define test_Equal_ImpossiblyEqualDateTimes: TestMessage(not Equal_ImpossiblyEqualDateTimes, 'Equal_ImpossiblyEqualDateTimes', toString(false), toString(Equal_ImpossiblyEqualDateTimes))
define test_Equal_DateAndDateTimeEqual: TestMessage(Equal_DateAndDateTimeEqual is null, 'Equal_DateAndDateTimeEqual', 'null', toString(Equal_DateAndDateTimeEqual))
define test_Equal_DateAndDateTimeNotEqual: TestMessage(not Equal_DateAndDateTimeNotEqual, 'Equal_DateAndDateTimeNotEqual', toString(false), toString(Equal_DateAndDateTimeNotEqual))
define test_Equal_DateAndDateTimeUncertainEqual: TestMessage(not Equal_DateAndDateTimeUncertainEqual, 'Equal_DateAndDateTimeUncertainEqual', toString(false), toString(Equal_DateAndDateTimeUncertainEqual))
define test_Equal_DateTimeAndDateEqual: TestMessage(Equal_DateTimeAndDateEqual is null, 'Equal_DateTimeAndDateEqual', 'null', toString(Equal_DateTimeAndDateEqual))
define test_Equal_DateTimeAndDateNotEqual: TestMessage(not Equal_DateTimeAndDateNotEqual, 'Equal_DateTimeAndDateNotEqual', toString(false), toString(Equal_DateTimeAndDateNotEqual))
define test_Equal_DateTimeAndDateUncertainEqual: TestMessage(not Equal_DateTimeAndDateUncertainEqual, 'Equal_DateTimeAndDateUncertainEqual', toString(false), toString(Equal_DateTimeAndDateUncertainEqual))
define test_Equal_AGtB_Quantity: TestMessage(not Equal_AGtB_Quantity, 'Equal_AGtB_Quantity', toString(false), toString(Equal_AGtB_Quantity))
define test_Equal_AEqB_Quantity: TestMessage(Equal_AEqB_Quantity, 'Equal_AEqB_Quantity', toString(true), toString(Equal_AEqB_Quantity))
define test_Equal_ALtB_Quantity: TestMessage(not Equal_ALtB_Quantity, 'Equal_ALtB_Quantity', toString(false), toString(Equal_ALtB_Quantity))

// NotEqual
define NotEqual_AGtB_Int: 5 != 4
define NotEqual_AEqB_Int: 5 != 5
define NotEqual_ALtB_Int: 5 != 6
define NotEqual_EqTuples: Tuple{a: 1, b: Tuple{c: 1}} != Tuple{a: 1, b: Tuple{c: 1}}
define NotEqual_UneqTuples: Tuple{a: 1, b: Tuple{c: 1}} != Tuple{a: 1, b: Tuple{c: -1}}
// TODO: fix once issue is resolved in translator
//define NotEqual_TupleDifferentKeys: Tuple{ Foo: null } != Tuple{ Bar: null }
define NotEqual_EqTuplesWithNullFields: Tuple{a: 'Hello', b: null} != Tuple{a: 'Hello', b: null}
define NotEqual_UneqTuplesWithNullFields: Tuple{a: 'Hello', b: null} != Tuple{a: 'Goodbye', b: null}
define NotEqual_UncertTuplesWithNullFieldOnOne: Tuple{a: 'Hello', b: null} != Tuple{a: 'Hello', b: 'null'}
// TODO: fix once issue is resolved in translator
//define NotEqual_UncertTuplesWithDiffNullFields: Tuple{a: 'Hello', b: null} != Tuple{a: 'Hello', c: null}
define NotEqual_EqDateTimes: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) != DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0)
define NotEqual_UneqDateTimes: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) != DateTime(2000, 3, 15, 13, 30, 25, 201, +1.0)
define NotEqual_EqDateTimesTZ: DateTime(2000, 3, 15, 23, 30, 25, 200, +1.0) != DateTime(2000, 3, 16, 2, 30, 25, 200, +4.0)
define NotEqual_UneqDateTimesTZ: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) != DateTime(2000, 3, 15, 13, 30, 25, 200, +2.0)
define NotEqual_EqDateTimesOnlyDate: DateTime(2000, 3, 15) != DateTime(2000, 3, 15)
define NotEqual_UneqDateTimesOnlyDate: DateTime(2000, 3, 14) != DateTime(2000, 3, 15)
define NotEqual_PossiblyEqDateTimesOnlyDateOnOne: DateTime(2000, 3, 13) != DateTime(2000, 3, 13, 13, 43, 32)
define NotEqual_UneqDateTimesOnlyDateOnOne: DateTime(2000, 4, 13, 12, 43, 32) != DateTime(2000, 3, 13)
define NotEqual_PossiblyEqualDateTimes: DateTime(2000, 3, 15) != DateTime(2000)
define NotEqual_ImpossiblyEqualDateTimes: DateTime(2000, 3, 15) != DateTime(2000, 4)
define NotEqual_DateAndDateTimeEqual: Date(2000, 3, 13) != DateTime(2000, 3, 13)
define NotEqual_DateAndDateTimeNotEqual: Date(2000, 3, 13) != DateTime(2000, 3, 12)
define NotEqual_DateAndDateTimeUncertainEqual: Date(2000, 3, 13) != DateTime(2000, 3, 13, 2, 4, 23)
define NotEqual_DateTimeAndDateEqual: DateTime(2000, 3, 13) != Date(2000, 3, 13)
define NotEqual_DateTimeAndDateNotEqual: DateTime(2000, 3, 12) != Date(2000, 3, 13)
define NotEqual_DateTimeAndDateUncertainEqual: DateTime(2000, 3, 13, 2, 4, 23) != Date(2000, 3, 13)
define NotEqual_AGtB_Quantity: 5 'm' != 4 'm'
define NotEqual_AEqB_Quantity: 5 'm' != 5 'm'
define NotEqual_ALtB_Quantity: 5 'm' != 6 'm'
// TODO - implement for Quantity
define NotEqual_AGtB_Quantity_diff: 5 'm' != 5 'cm'
define NotEqual_AEqB_Quantity_diff: 5 'm' != 500 'cm'
define NotEqual_ALtB_Quantity_diff: 5 'm' != 5 'km'

define test_NotEqual_AGtB_Int: TestMessage(NotEqual_AGtB_Int, 'NotEqual_AGtB_Int', toString(true), toString(NotEqual_AGtB_Int))
define test_NotEqual_AEqB_Int: TestMessage(not NotEqual_AEqB_Int, 'NotEqual_AEqB_Int', toString(false), toString(NotEqual_AEqB_Int))
define test_NotEqual_ALtB_Int: TestMessage(NotEqual_ALtB_Int, 'NotEqual_ALtB_Int', toString(true), toString(NotEqual_ALtB_Int))
define test_NotEqual_EqTuples: TestMessage(not NotEqual_EqTuples, 'NotEqual_EqTuples', toString(false), toString(NotEqual_EqTuples))
define test_NotEqual_UneqTuples: TestMessage(NotEqual_UneqTuples, 'NotEqual_UneqTuples', toString(true), toString(NotEqual_UneqTuples))
//define test_NotEqual_TupleDifferentKeys: TestMessage(NotEqual_TupleDifferentKeys, 'NotEqual_TupleDifferentKeys', toString(true), toString(NotEqual_TupleDifferentKeys))
define test_NotEqual_EqTuplesWithNullFields: TestMessage(not NotEqual_EqTuplesWithNullFields, 'NotEqual_EqTuplesWithNullFields', toString(false), toString(NotEqual_EqTuplesWithNullFields))
define test_NotEqual_UneqTuplesWithNullFields: TestMessage(NotEqual_UneqTuplesWithNullFields, 'NotEqual_UneqTuplesWithNullFields', toString(true), toString(NotEqual_UneqTuplesWithNullFields))
define test_NotEqual_UncertTuplesWithNullFieldOnOne: TestMessage(NotEqual_UncertTuplesWithNullFieldOnOne is null, 'NotEqual_UncertTuplesWithNullFieldOnOne', 'null', toString(NotEqual_UncertTuplesWithNullFieldOnOne))
//define test_NotEqual_UncertTuplesWithDiffNullFields: TestMessage(NotEqual_UncertTuplesWithDiffNullFields, 'NotEqual_UncertTuplesWithDiffNullFields', toString(true), toString(NotEqual_UncertTuplesWithDiffNullFields))
define test_NotEqual_EqDateTimes: TestMessage(not NotEqual_EqDateTimes, 'NotEqual_EqDateTimes', toString(false), toString(NotEqual_EqDateTimes))
define test_NotEqual_UneqDateTimes: TestMessage(NotEqual_UneqDateTimes, 'NotEqual_UneqDateTimes', toString(true), toString(NotEqual_UneqDateTimes))
define test_NotEqual_EqDateTimesTZ: TestMessage(not NotEqual_EqDateTimesTZ, 'NotEqual_EqDateTimesTZ', toString(false), toString(NotEqual_EqDateTimesTZ))
define test_NotEqual_UneqDateTimesTZ: TestMessage(NotEqual_UneqDateTimesTZ, 'NotEqual_UneqDateTimesTZ', toString(true), toString(NotEqual_UneqDateTimesTZ))
define test_NotEqual_EqDateTimesOnlyDate: TestMessage(not NotEqual_EqDateTimesOnlyDate, 'NotEqual_EqDateTimesOnlyDate', toString(true), toString(NotEqual_EqDateTimesOnlyDate))
define test_NotEqual_UneqDateTimesOnlyDate: TestMessage(NotEqual_UneqDateTimesOnlyDate, 'NotEqual_UneqDateTimesOnlyDate', toString(false), toString(NotEqual_UneqDateTimesOnlyDate))
define test_NotEqual_PossiblyEqDateTimesOnlyDateOnOne: TestMessage(NotEqual_PossiblyEqDateTimesOnlyDateOnOne is null, 'NotEqual_PossiblyEqDateTimesOnlyDateOnOne', 'null', toString(NotEqual_PossiblyEqDateTimesOnlyDateOnOne))
define test_NotEqual_UneqDateTimesOnlyDateOnOne: TestMessage(NotEqual_UneqDateTimesOnlyDateOnOne, 'NotEqual_UneqDateTimesOnlyDateOnOne', toString(false), toString(NotEqual_UneqDateTimesOnlyDateOnOne))
define test_NotEqual_PossiblyEqualDateTimes: TestMessage(NotEqual_PossiblyEqualDateTimes is null, 'NotEqual_PossiblyEqualDateTimes', 'null', toString(NotEqual_PossiblyEqualDateTimes))
define test_NotEqual_ImpossiblyEqualDateTimes: TestMessage(NotEqual_ImpossiblyEqualDateTimes, 'NotEqual_ImpossiblyEqualDateTimes', toString(true), toString(NotEqual_ImpossiblyEqualDateTimes))
define test_NotEqual_DateAndDateTimeEqual: TestMessage(NotEqual_DateAndDateTimeEqual is null, 'NotEqual_DateAndDateTimeEqual', 'null', toString(NotEqual_DateAndDateTimeEqual))
define test_NotEqual_DateAndDateTimeNotEqual: TestMessage(NotEqual_DateAndDateTimeNotEqual, 'NotEqual_DateAndDateTimeNotEqual', toString(true), toString(NotEqual_DateAndDateTimeNotEqual))
define test_NotEqual_DateAndDateTimeUncertainEqual: TestMessage(NotEqual_DateAndDateTimeUncertainEqual, 'NotEqual_DateAndDateTimeUncertainEqual', toString(true), toString(NotEqual_DateAndDateTimeUncertainEqual))
define test_NotEqual_DateTimeAndDateEqual: TestMessage(NotEqual_DateTimeAndDateEqual is null, 'NotEqual_DateTimeAndDateEqual', 'null', toString(NotEqual_DateTimeAndDateEqual))
define test_NotEqual_DateTimeAndDateNotEqual: TestMessage(NotEqual_DateTimeAndDateNotEqual, 'NotEqual_DateTimeAndDateNotEqual', toString(true), toString(NotEqual_DateTimeAndDateNotEqual))
define test_NotEqual_DateTimeAndDateUncertainEqual: TestMessage(NotEqual_DateTimeAndDateUncertainEqual, 'NotEqual_DateTimeAndDateUncertainEqual', toString(true), toString(NotEqual_DateTimeAndDateUncertainEqual))
define test_NotEqual_AGtB_Quantity: TestMessage(NotEqual_AGtB_Quantity, 'NotEqual_AGtB_Quantity', toString(true), toString(NotEqual_AGtB_Quantity))
define test_NotEqual_AEqB_Quantity: TestMessage(not NotEqual_AEqB_Quantity, 'NotEqual_AEqB_Quantity', toString(false), toString(NotEqual_AEqB_Quantity))
define test_NotEqual_ALtB_Quantity: TestMessage(NotEqual_ALtB_Quantity, 'NotEqual_ALtB_Quantity', toString(true), toString(NotEqual_ALtB_Quantity))

// Equivalent
define Equivalent_ANull_BDefined: null ~ 4
define Equivalent_ADefined_BNull: 5 ~ null
define Equivalent_ANull_BNull: null as Integer ~ null
define Equivalent_ADefined_BDefined: 3 ~ 3
define Equivalent_CaseInsensitiveStrings: 'FOO' ~ 'foo'

define test_Equivalent_ANull_BDefined: TestMessage(not Equivalent_ANull_BDefined, 'Equivalent_ANull_BDefined', toString(false), toString(Equivalent_ANull_BDefined))
define test_Equivalent_ADefined_BNull: TestMessage(not Equivalent_ADefined_BNull, 'Equivalent_ADefined_BNull', toString(false), toString(Equivalent_ADefined_BNull))
define test_Equivalent_ANull_BNull: TestMessage(Equivalent_ANull_BNull, 'Equivalent_ANull_BNull', toString(true), toString(Equivalent_ANull_BNull))
define test_Equivalent_ADefined_BDefined: TestMessage(Equivalent_ADefined_BDefined, 'Equivalent_ADefined_BDefined', toString(true), toString(Equivalent_ADefined_BDefined))
define test_Equivalent_CaseInsensitiveStrings: TestMessage(Equivalent_CaseInsensitiveStrings, 'Equivalent_CaseInsensitiveStrings', toString(true), toString(Equivalent_CaseInsensitiveStrings))

// Tuple Equivalence
define Equivalent_EmptyTuples: { : } ~ { : }
define Equivalent_SameTuples: Tuple{a: 'a', b: 'b'} ~ Tuple{a: 'a', b: 'b'}
define Equivalent_SameTuplesNull: Tuple{a: null} ~ Tuple{a : null}
define Equivalent_DifferentTuples: Tuple{a: 'a', b: 'b'} ~ Tuple{a: 'x', b: 'b'}
define Equivalent_SameNestedTuples: Tuple{a: 'a', b: Tuple{c: 'c'}} ~ Tuple{a: 'a', b: Tuple{c: 'c'}}
define Equivalent_SameNestedTuplesNull: Tuple{a: 'a', b: Tuple{c: null}} ~ Tuple{a: 'a', b: Tuple{c: null}}
define Equivalent_TupleAndNullList: {a: '1'} ~ {null}
define Equivalent_NullListAndTuple: {null} ~ {a: '1'}

define test_Equivalent_EmptyTuples: TestMessage(Equivalent_EmptyTuples, 'Equivalent_EmptyTuples', toString(true), toString(Equivalent_EmptyTuples))
define test_Equivalent_SameTuples: TestMessage(Equivalent_SameTuples, 'Equivalent_SameTuples', toString(true), toString(Equivalent_SameTuples))
define test_Equivalent_SameTuplesNull: TestMessage(Equivalent_SameTuplesNull, 'Equivalent_SameTuplesNull', toString(true), toString(Equivalent_SameTuplesNull))
define test_Equivalent_DifferentTuples: TestMessage(not Equivalent_DifferentTuples, 'Equivalent_DifferentTuples', toString(false), toString(Equivalent_DifferentTuples))
define test_Equivalent_SameNestedTuples: TestMessage(Equivalent_SameNestedTuples, 'Equivalent_SameNestedTuples', toString(true), toString(Equivalent_SameNestedTuples))
define test_Equivalent_SameNestedTuplesNull: TestMessage(Equivalent_SameNestedTuplesNull, 'Equivalent_SameNestedTuplesNull', toString(true), toString(Equivalent_SameNestedTuplesNull))
define test_Equivalent_TupleAndNullList: TestMessage(not Equivalent_TupleAndNullList, 'Equivalent_TupleAndNullList', toString(false), toString(Equivalent_TupleAndNullList))
define test_Equivalent_NullListAndTuple: TestMessage(not Equivalent_NullListAndTuple, 'Equivalent_NullListAndTuple', toString(false), toString(Equivalent_NullListAndTuple))

// List Equivalence
define Equivalent_EmptyLists: { } ~ { }
define Equivalent_DifferentTypesLists: {'1', '2', '3'} ~ {1, 2, 3}
define Equivalent_DifferentLengthLists: {'a'} ~ {'a', 'a'}
define Equivalent_DifferentOrderLists: {'a', 'b'} ~ {'b', 'a'}
define Equivalent_SameLists: {'a', 'b', 'c'} ~ {'a', 'b', 'c'}
define Equivalent_SameListsNull: {null, null, null} ~ {null, null, null}
define Equivalent_SameNestedLists: {{'a','d'}, {'b', 'c'}} ~ {{'a','d'}, {'b', 'c'}}
define Equivalent_SameNestedListsNull: {null, {null, null}} ~ {null, {null, null}}

define test_Equivalent_EmptyLists: TestMessage(Equivalent_EmptyLists, 'Equivalent_EmptyLists', toString(true), toString(Equivalent_EmptyLists))
define test_Equivalent_DifferentTypesLists: TestMessage(not Equivalent_DifferentTypesLists, 'Equivalent_DifferentTypesLists', toString(false), toString(Equivalent_DifferentTypesLists))
define test_Equivalent_DifferentLengthLists: TestMessage(not Equivalent_DifferentLengthLists, 'Equivalent_DifferentLengthLists', toString(false), toString(Equivalent_DifferentLengthLists))
define test_Equivalent_DifferentOrderLists: TestMessage(not Equivalent_DifferentOrderLists, 'Equivalent_DifferentOrderLists', toString(false), toString(Equivalent_DifferentOrderLists))
define test_Equivalent_SameLists: TestMessage(Equivalent_SameLists, 'Equivalent_SameLists', toString(true), toString(Equivalent_SameLists))
define test_Equivalent_SameListsNull: TestMessage(Equivalent_SameListsNull, 'Equivalent_SameListsNull', toString(true), toString(Equivalent_SameListsNull))
define test_Equivalent_SameNestedLists: TestMessage(Equivalent_SameNestedLists, 'Equivalent_SameNestedLists', toString(true), toString(Equivalent_SameNestedLists))
define test_Equivalent_SameNestedListsNull: TestMessage(Equivalent_SameNestedListsNull, 'Equivalent_SameNestedListsNull', toString(true), toString(Equivalent_SameNestedListsNull))

// Interval Equivalence
define Equivalent_EmptyInterval: Interval(null, null) ~ Interval(null, null)
define Equivalent_IntervalDifferentPointTypes:  Interval[1, 5] ~ Interval['1', '5']
define Equivalent_IntervalDifferentStarts: Interval[1,3] ~ Interval[2,3]
define Equivalent_IntervalDifferentEndings: Interval[1,3] ~ Interval[1,4]
define Equivalent_SameIntervals: Interval[1,3] ~ Interval[1,3]

define test_Equivalent_EmptyInterval: TestMessage(Equivalent_EmptyInterval, 'Equivalent_EmptyInterval', toString(true), toString(Equivalent_EmptyInterval))
define test_Equivalent_IntervalDifferentPointTypes: TestMessage(not Equivalent_IntervalDifferentPointTypes, 'Equivalent_IntervalDifferentPointTypes', toString(false), toString(Equivalent_IntervalDifferentPointTypes))
define test_Equivalent_IntervalDifferentStarts: TestMessage(not Equivalent_IntervalDifferentStarts, 'Equivalent_IntervalDifferentStarts', toString(false), toString(Equivalent_IntervalDifferentStarts))
define test_Equivalent_IntervalDifferentEndings: TestMessage(not Equivalent_IntervalDifferentEndings, 'Equivalent_IntervalDifferentEndings', toString(false), toString(Equivalent_IntervalDifferentEndings))
define test_Equivalent_SameIntervals: TestMessage(Equivalent_SameIntervals, 'Equivalent_SameIntervals', toString(true), toString(Equivalent_SameIntervals))

// Less
define Less_AGtB_Int: 5 < 4
define Less_AEqB_Int: 5 < 5
define Less_ALtB_Int: 5 < 6
define Less_AGtB_Quantity: 5 'm' < 4 'm'
define Less_AEqB_Quantity: 5 'm' < 5 'm'
define Less_ALtB_Quantity: 5 'm' < 6 'm'
// TODO - implement for Quantity
// define Less_AGtB_Quantity_diff: 5 'm' < 5 'cm'
// define Less_AEqB_Quantity_diff: 5 'm' < 500 'cm'
// define Less_ALtB_Quantity_diff: 5 'm' < 5 'km'

define test_Less_AGtB_Int: TestMessage(not Less_AGtB_Int, 'Less_AGtB_Int', toString(false), toString(Less_AGtB_Int))
define test_Less_AEqB_Int: TestMessage(not Less_AEqB_Int, 'Less_AEqB_Int', toString(false), toString(Less_AEqB_Int))
define test_Less_ALtB_Int: TestMessage(Less_ALtB_Int, 'Less_ALtB_Int', toString(true), toString(Less_ALtB_Int))
define test_Less_AGtB_Quantity: TestMessage(not Less_AGtB_Quantity, 'Less_AGtB_Quantity', toString(false), toString(Less_AGtB_Quantity))
define test_Less_AEqB_Quantity: TestMessage(not Less_AEqB_Quantity, 'Less_AEqB_Quantity', toString(false), toString(Less_AEqB_Quantity))
define test_Less_ALtB_Quantity: TestMessage(Less_ALtB_Quantity, 'Less_ALtB_Quantity', toString(true), toString(Less_ALtB_Quantity))

// LessOrEqual
define LessOrEqual_AGtB_Int: 5 <= 4
define LessOrEqual_AEqB_Int: 5 <= 5
define LessOrEqual_ALtB_Int: 5 <= 6
define LessOrEqual_AGtB_Quantity: 5 'm' <= 4 'm'
define LessOrEqual_AEqB_Quantity: 5 'm' <= 5 'm'
define LessOrEqual_ALtB_Quantity: 5 'm' <= 6 'm'
// TODO - implement for Quantity
// define AGtB_Quantity_diff: 5 'm' <= 4 'm'
// define AEqB_Quantity_diff: 5 'm' <= 500 'cm'
// define ALtB_Quantity_diff: 5 'm' <= 5 'km'

define test_LessOrEqual_AGtB_Int: TestMessage(not LessOrEqual_AGtB_Int, 'LessOrEqual_AGtB_Int', toString(false), toString(LessOrEqual_AGtB_Int))
define test_LessOrEqual_AEqB_Int: TestMessage(LessOrEqual_AEqB_Int, 'LessOrEqual_AEqB_Int', toString(true), toString(LessOrEqual_AEqB_Int))
define test_LessOrEqual_ALtB_Int: TestMessage(LessOrEqual_ALtB_Int, 'LessOrEqual_ALtB_Int', toString(true), toString(LessOrEqual_ALtB_Int))
define test_LessOrEqual_AGtB_Quantity: TestMessage(not LessOrEqual_AGtB_Quantity, 'LessOrEqual_AGtB_Quantity', toString(false), toString(LessOrEqual_AGtB_Quantity))
define test_LessOrEqual_AEqB_Quantity: TestMessage(LessOrEqual_AEqB_Quantity, 'LessOrEqual_AEqB_Quantity', toString(true), toString(LessOrEqual_AEqB_Quantity))
define test_LessOrEqual_ALtB_Quantity: TestMessage(LessOrEqual_ALtB_Quantity, 'LessOrEqual_ALtB_Quantity', toString(true), toString(LessOrEqual_ALtB_Quantity))

// Greater
define Greater_AGtB_Int: 5 > 4
define Greater_AEqB_Int: 5 > 5
define Greater_ALtB_Int: 5 > 6
define Greater_AGtB_Quantity: 5 'm' > 4 'm'
define Greater_AEqB_Quantity: 5 'm' > 5 'm'
define Greater_ALtB_Quantity: 5 'm' > 6 'm'
// TODO - implement for Quantity
// define Greater_AGtB_Quantity_diff: 5 'm' > 5 'cm'
// define Greater_AEqB_Quantity_diff: 5 'm' > 500 'cm'
// define Greater_ALtB_Quantity_diff: 5 'm' > 5 'km'
// TODO - implement and add to error tests
// define Greater_AGtB_Quantity_incompatible: 5 'Cel' > 4 'm'
// define Greater_AEqB_Quantity_incompatible: 5 'Cel' > 5 'm'
// define Greater_ALtB_Quantity_incompatible: 5 'Cel' > 40 'm'

define test_Greater_AGtB_Int: TestMessage(Greater_AGtB_Int, 'Greater_AGtB_Int', toString(true), toString(Greater_AGtB_Int))
define test_Greater_AEqB_Int: TestMessage(not Greater_AEqB_Int, 'Greater_AEqB_Int', toString(false), toString(Greater_AEqB_Int))
define test_Greater_ALtB_Int: TestMessage(not Greater_ALtB_Int, 'Greater_ALtB_Int', toString(false), toString(Greater_ALtB_Int))
define test_Greater_AGtB_Quantity: TestMessage(Greater_AGtB_Quantity, 'Greater_AGtB_Quantity', toString(true), toString(Greater_AGtB_Quantity))
define test_Greater_AEqB_Quantity: TestMessage(not Greater_AEqB_Quantity, 'Greater_AEqB_Quantity', toString(false), toString(Greater_AEqB_Quantity))
define test_Greater_ALtB_Quantity: TestMessage(not Greater_ALtB_Quantity, 'Greater_ALtB_Quantity', toString(false), toString(Greater_ALtB_Quantity))

// GreaterOrEqual
define GreaterOrEqual_AGtB_Int: 5 >= 4
define GreaterOrEqual_AEqB_Int: 5 >= 5
define GreaterOrEqual_ALtB_Int: 5 >= 6
define GreaterOrEqual_AGtB_Quantity: 5 'm' >= 4 'm'
define GreaterOrEqual_AEqB_Quantity: 5 'm' >= 5 'm'
define GreaterOrEqual_ALtB_Quantity: 5 'm' >= 6 'm'
// TODO - implement for Quantity
// define AGtB_Quantity_diff: 5 'm' >= 5 'cm'
// define AEqB_Quantity_diff: 5 'm' >= 500 'cm'
// define ALtB_Quantity_diff: 5 'm' >= 5 'km'
// define DivideUcum: (100 'mg' / 2 '[lb_av]') > 49 'mg/[lb_av]'

define test_GreaterOrEqual_AGtB_Int: TestMessage(GreaterOrEqual_AGtB_Int, 'GreaterOrEqual_AGtB_Int', toString(true), toString(GreaterOrEqual_AGtB_Int))
define test_GreaterOrEqual_AEqB_Int: TestMessage(GreaterOrEqual_AEqB_Int, 'GreaterOrEqual_AEqB_Int', toString(true), toString(GreaterOrEqual_AEqB_Int))
define test_GreaterOrEqual_ALtB_Int: TestMessage(not GreaterOrEqual_ALtB_Int, 'GreaterOrEqual_ALtB_Int', toString(false), toString(GreaterOrEqual_ALtB_Int))
define test_GreaterOrEqual_AGtB_Quantity: TestMessage(GreaterOrEqual_AGtB_Quantity, 'GreaterOrEqual_AGtB_Quantity', toString(true), toString(GreaterOrEqual_AGtB_Quantity))
define test_GreaterOrEqual_AEqB_Quantity: TestMessage(GreaterOrEqual_AEqB_Quantity, 'GreaterOrEqual_AEqB_Quantity', toString(true), toString(GreaterOrEqual_AEqB_Quantity))
define test_GreaterOrEqual_ALtB_Quantity: TestMessage(not GreaterOrEqual_ALtB_Quantity, 'GreaterOrEqual_ALtB_Quantity', toString(false), toString(GreaterOrEqual_ALtB_Quantity))


/*
************************
  End Comparison Tests
************************
*/

/*
***************************
  Start Conditional Tests
***************************
*/

// If
define function IfThenElse(var Boolean): if var then 'true return' else 'false return'

define test_IfThenElse_true: TestMessage(IfThenElse(true) = 'true return', 'IfThenElse_true', 'true return', IfThenElse(true))
define test_IfThenElse_false: TestMessage(IfThenElse(false) = 'false return', 'IfThenElse_false', 'false return', IfThenElse(false))

// Case

define function Case_selected(var Integer):
  case var
   when 1 then 'one'
   when 2 then 'two'
   else
    '?'
  end

define test_Case_selected_1: TestMessage(Case_selected(1) = 'one', 'Case_selected_1', 'one', Case_selected(1))
define test_Case_selected_2: TestMessage(Case_selected(2) = 'two', 'Case_selected_2', 'two', Case_selected(2))
define test_Case_selected_3: TestMessage(Case_selected(3) = '?',   'Case_selected_3', '?',   Case_selected(3))

define function Case_standard(X Integer, Y Integer):
  case
    when X > Y then 'X > Y'
    when X < Y then 'X < Y'
    else 'X == Y'
  end

define test_Case_Standard_1_2: TestMessage(Case_standard(1, 2) = 'X < Y', 'Case_Standard_1_2', 'X < Y', Case_standard(1, 2))
define test_Case_Standard_2_1: TestMessage(Case_standard(2, 1) = 'X > Y', 'Case_Standard_2_1', 'X > Y', Case_standard(2, 1))
define test_Case_Standard_1_1: TestMessage(Case_standard(1, 1) = 'X == Y', 'Case_Standard_1_1', 'X == Y', Case_standard(1, 1))

/*
*************************
  End Conditional Tests
*************************
*/

/*
**************************
  Start Conversion Tests
**************************
*/

// FromString
define FromString_stringStr: convert 'str' to String
define FromString_stringNull: convert null to String
define FromString_boolTrue: convert 'true' to Boolean
define FromString_boolFalse: convert 'false' to Boolean
define FromString_decimalValid: convert '10.2' to Decimal
define FromString_decimalInvalid: convert 'abc' to Decimal
define FromString_integerValid: convert '10' to Integer
define FromString_integerInvalid: convert 'abc' to Integer
define FromString_integerDropDecimal: convert '10.2' to Integer
define FromString_quantityStr: convert '10 \'A\'' to Quantity
define FromString_posQuantityStr: convert '+10 \'A\'' to Quantity
define FromString_negQuantityStr: convert '-10 \'A\'' to Quantity
define FromString_quantityStrDecimal: convert '10.0 \'mA\'' to Quantity
define FromString_dateTimeStr: convert '2015-01-02' to DateTime
define FromString_dateStr: convert '2015-01-02' to Date
define FromString_NullConvert: convert 'foo' to DateTime
define FromString_ZDateTime: convert '2014-01-01T14:30:00.0Z' to DateTime // January 1st, 2014, 2:30PM UTC
define FromString_TimezoneDateTime: convert '2014-01-01T14:30:00.0-07:00' to DateTime // January 1st, 2014, 2:30PM Mountain Standard (GMT-7:00)
define FromString_timeStr: convert 'T12:30:00.000' to Time

define test_FromString_stringStr: TestMessage(FromString_stringStr = 'str', 'FromString_stringStr', 'str', FromString_stringStr)
define test_FromString_stringNull: TestMessage(FromString_stringNull is null, 'FromString_stringNull', 'null', FromString_stringNull)
define test_FromString_boolTrue: TestMessage(FromString_boolTrue, 'FromString_boolTrue', toString(true), toString(FromString_boolTrue))
define test_FromString_boolFalse: TestMessage(not FromString_boolFalse, 'FromString_boolFalse', toString(false), toString(FromString_boolFalse))
define test_FromString_decimalValid: TestMessage(FromString_decimalValid = 10.2, 'FromString_decimalValid', toString(10.2), toString(FromString_decimalValid))
define test_FromString_decimalInvalid: TestMessage(FromString_decimalInvalid is null, 'FromString_decimalInvalid', 'null', toString(FromString_decimalInvalid))
define test_FromString_integerValid: TestMessage(FromString_integerValid = 10, 'FromString_integerValid', toString(10), toString(FromString_integerValid))
define test_FromString_integerInvalid: TestMessage(FromString_integerInvalid is null, 'FromString_integerInvalid', 'null', toString(FromString_integerInvalid))
define test_FromString_integerDropDecimal: TestMessage(FromString_integerDropDecimal = 10, 'FromString_integerDropDecimal', toString(10), toString(FromString_integerDropDecimal))
define test_FromString_quantityStr: TestMessage(FromString_quantityStr = 10 'A', 'FromString_quantityStr', toString(10 'A'), toString(FromString_quantityStr))
define test_FromString_posQuantityStr: TestMessage(FromString_posQuantityStr = 10 'A', 'FromString_posQuantityStr', toString(10 'A'), toString(FromString_posQuantityStr))
define test_FromString_negQuantityStr: TestMessage(FromString_negQuantityStr = -10 'A', 'FromString_negQuantityStr', toString(-10 'A'), toString(FromString_negQuantityStr))
define test_FromString_quantityStrDecimal: TestMessage(FromString_quantityStrDecimal = 10.0 'mA', 'FromString_quantityStrDecimal', toString(10.0 'mA'), toString(FromString_quantityStrDecimal))
define test_FromString_dateTimeStr: TestMessage(FromString_dateTimeStr = DateTime(2015, 1, 2), 'FromString_dateTimeStr', toString(DateTime(2015, 1, 2)), toString(FromString_dateTimeStr))
define test_FromString_dateStr: TestMessage(FromString_dateStr = Date(2015, 1, 2), 'FromString_dateStr', toString(Date(2015, 1, 2)), toString(FromString_dateStr))
define test_FromString_NullConvert: TestMessage(FromString_NullConvert is null, 'FromString_NullConvert', 'null', toString(FromString_NullConvert))
define test_FromString_ZDateTime: TestMessage(FromString_ZDateTime = DateTime(2014, 1, 1, 14, 30, 0, 0, 0), 'FromString_ZDateTime', toString(DateTime(2014, 1, 1, 14, 30, 0, 0, 0)), toString(FromString_ZDateTime))
define test_FromString_TimezoneDateTime: TestMessage(FromString_TimezoneDateTime = DateTime(2014, 1, 1, 14, 30, 0, 0, -7), 'FromString_TimezoneDateTime', toString(DateTime(2014, 1, 1, 14, 30, 0, 0, -7)), toString(FromString_TimezoneDateTime))
define test_FromString_timeStr: TestMessage(FromString_timeStr = @T12:30:00.000, 'FromString_timeStr', toString(@T12:30:00.000), toString(FromString_timeStr))

// FromInteger
define FromInteger_string10: convert 10 to String
define FromInteger_decimal10: convert 10 to Decimal
define FromInteger_intNull: convert null to Decimal
define FromInteger_intInt: convert 10 to Integer

define test_FromInteger_string10: TestMessage(FromInteger_string10 = '10', 'FromInteger_string10', '10', FromInteger_string10)
define test_FromInteger_decimal10: TestMessage(FromInteger_decimal10 = 10.0, 'FromInteger_decimal10', toString(10.0), toString(FromInteger_decimal10))
define test_FromInteger_intNull: TestMessage(FromInteger_intNull is null, 'FromInteger_intNull', 'null', toString(FromInteger_intNull))
define test_FromInteger_intInt: TestMessage(FromInteger_intInt = 10, 'FromInteger_intInt', toString(10), toString(FromInteger_intInt))

// FromQuantity
define FromQuantity_quantityStr: convert 10 'A' to String
define FromQuantity_negQuantityStr: convert -10 'A' to String
define FromQuantity_posQuantityStr: convert +10 'A' to String
define FromQuantity_quantityQuantity: convert 10 'A' to Quantity

define test_FromQuantity_quantityStr: TestMessage(FromQuantity_quantityStr = '10 \'A\'', 'FromQuantity_quantityStr', '10 \'A\'', FromQuantity_quantityStr)
define test_FromQuantity_negQuantityStr: TestMessage(FromQuantity_negQuantityStr = '-10 \'A\'', 'FromQuantity_negQuantityStr', '-10 \'A\'', FromQuantity_negQuantityStr)
define test_FromQuantity_posQuantityStr: TestMessage(FromQuantity_posQuantityStr = '10 \'A\'', 'FromQuantity_posQuantityStr', '10 \'A\'', FromQuantity_posQuantityStr)
define test_FromQuantity_quantityQuantity: TestMessage(FromQuantity_quantityQuantity = 10 'A', 'FromQuantity_quantityQuantity', toString(10 'A'), toString(FromQuantity_quantityQuantity))

// FromBoolean
define FromBoolean_booleanTrueStr: convert true to String
define FromBoolean_booleanFalseStr: convert false to String
define FromBoolean_booleanTrueBool: convert true to Boolean
define FromBoolean_booleanFalseBool: convert false to Boolean

define test_FromBoolean_booleanTrueStr: TestMessage(FromBoolean_booleanTrueStr = 'true', 'FromBoolean_booleanTrueStr', 'true', FromBoolean_booleanTrueStr)
define test_FromBoolean_booleanFalseStr: TestMessage(FromBoolean_booleanFalseStr = 'false', 'FromBoolean_booleanFalseStr', 'false', FromBoolean_booleanFalseStr)
define test_FromBoolean_booleanTrueBool: TestMessage(FromBoolean_booleanTrueBool, 'FromBoolean_booleanTrueBool', toString(true), toString(FromBoolean_booleanTrueBool))
define test_FromBoolean_booleanFalseBool: TestMessage(not FromBoolean_booleanFalseBool, 'FromBoolean_booleanFalseBool', toString(false), toString(FromBoolean_booleanFalseBool))

// FromDateTime
define FromDateTime_dateTimeToStr: convert @2015-01-02T12:01:02.321-06:00 to String
define FromDateTime_dateTimeToDate: convert @2015-01-02T12:01:02.321-06:00 to Date
define FromDateTime_dateTimeToDateTime: convert @2015-01-02T12:01:02.321-06:00 to DateTime

define test_FromDateTime_dateTimeToStr: TestMessage(FromDateTime_dateTimeToStr = '2015-01-02T12:01:02.321-06:00', 'FromDateTime_dateTimeToStr', '2015-01-02T12:01:02.321', FromDateTime_dateTimeToStr)
define test_FromDateTime_dateTimeToDate: TestMessage(FromDateTime_dateTimeToDate = Date(2015, 1, 2), 'FromDateTime_dateTimeToDate', toString(Date(2015, 1, 2)), toString(FromDateTime_dateTimeToDate))
define test_FromDateTime_dateTimeToDateTime: TestMessage(FromDateTime_dateTimeToDateTime = @2015-01-02T12:01:02.321-06:00, 'FromDateTime_dateTimeToDateTime', toString(@2015-01-02T12:01:02.321-06:00), toString(FromDateTime_dateTimeToDateTime))

// FromDate
define FromDate_dateYMDToDateTime: convert @2015-01-01 to DateTime
define FromDate_dateYMToDateTime: convert @2015-01 to DateTime
define FromDate_dateYToDateTime: convert @2015 to DateTime
define FromDate_dateToDate: convert @2015-01-01 to Date
define FromDate_dateToStr: convert @2015-01-01 to String

define test_FromDate_dateYMDToDateTime: TestMessage(FromDate_dateYMDToDateTime ~ DateTime(2015, 1, 1, 0, 0, 0, 0), 'FromDate_dateYMDToDateTime', toString(DateTime(2015, 1, 1, 0, 0, 0, 0)), toString(FromDate_dateYMDToDateTime))
define test_FromDate_dateYMToDateTime: TestMessage(FromDate_dateYMToDateTime ~ DateTime(2015, 1, 1, 0, 0, 0, 0), 'FromDate_dateYMToDateTime', toString(DateTime(2015, 1, 1, 0, 0, 0, 0)), toString(FromDate_dateYMToDateTime))
define test_FromDate_dateYToDateTime: TestMessage(FromDate_dateYToDateTime ~ DateTime(2015, 1, 1, 0, 0, 0, 0), 'FromDate_dateYToDateTime', toString(DateTime(2015, 1, 1, 0, 0, 0, 0)), toString(FromDate_dateYToDateTime))
define test_FromDate_dateToDate: TestMessage(FromDate_dateToDate ~ Date(2015, 1, 1), 'FromDate_dateToDate', toString(Date(2015, 1, 1)), toString(FromDate_dateToDate))
define test_FromDate_dateToStr: TestMessage(FromDate_dateToStr = '2015-01-01', 'FromDate_dateToStr', '2015-01-01', FromDate_dateToStr)

// FromTime
define FromTime_timeStr: convert @T11:57 to String
define FromTime_timeTime: convert @T11:57 to Time

define test_FromTime_timeStr: TestMessage(FromTime_timeStr = '11:57', 'FromTime_timeStr', '11:57', FromTime_timeStr)
define test_FromTime_timeTime: TestMessage(FromTime_timeTime = @T11:57, 'FromTime_timeTime', toString(@T11:57), toString(FromTime_timeTime))

// FromCode
define hepB_Code: Code '66071002' from "SNOMED-CT" display 'Type B viral hepatitis'
define hepB_Concept: Concept { codes: { hepB_Code } }
define FromCode_codeConcept: convert hepB_Code to Concept
define FromCode_codeCode: convert hepB_Code to Code

define test_FromCode_codeCode: TestMessage(FromCode_codeCode ~ hepB_Code, 'FromCode_codeCode', toString(hepB_Code), toString(FromCode_codeCode))
define test_FromCode_codeConcept: TestMessage(FromCode_codeConcept ~ hepB_Concept, 'FromCode_codeConcept', toString(hepB_Concept), toString(FromCode_codeConcept))

// ToDecimal
define ToDecimal_NoSign: ToDecimal('0.0')
define ToDecimal_PositiveSign: ToDecimal('+1.1')
define ToDecimal_NegativeSign: ToDecimal('-1.1')
define ToDecimal_NullDecimal: ToDecimal((null as String))
define ToDecimal_TooLargeDec: ToDecimal('444444444444444444444444444444')
define ToDecimal_TooSmallDec: ToDecimal('-444444444444444444444444444444')
// TODO: Bonnie has ToDecimal('.444444444'), but the format is invalid
define ToDecimal_TooPrecise: ToDecimal('0.444444444')
define ToDecimal_WrongFormat: ToDecimal('+.1')

define test_ToDecimal_NoSign: TestMessage(ToDecimal_NoSign = 0.0, 'ToDecimal_NoSign', toString(0.0), toString(ToDecimal_NoSign))
define test_ToDecimal_PositiveSign: TestMessage(ToDecimal_PositiveSign = 1.1, 'ToDecimal_PositiveSign', toString(1.1), toString(ToDecimal_PositiveSign))
define test_ToDecimal_NegativeSign: TestMessage(ToDecimal_NegativeSign = -1.1, 'ToDecimal_NegativeSign', toString(-1.1), toString(ToDecimal_NegativeSign))
define test_ToDecimal_NullDecimal: TestMessage(ToDecimal_NullDecimal is null, 'ToDecimal_NullDecimal', 'null', toString(ToDecimal_NullDecimal))
define test_ToDecimal_TooLargeDec: TestMessage(ToDecimal_TooLargeDec is null, 'ToDecimal_TooLargeDec', 'null', toString(ToDecimal_TooLargeDec))
define test_ToDecimal_TooSmallDec: TestMessage(ToDecimal_TooSmallDec is null, 'ToDecimal_TooSmallDec', 'null', toString(ToDecimal_TooSmallDec))
define test_ToDecimal_TooPrecise: TestMessage(ToDecimal_TooPrecise = 0.44444444, 'ToDecimal_TooPrecise', toString(0.44444444), toString(ToDecimal_TooPrecise))
define test_ToDecimal_WrongFormat: TestMessage(ToDecimal_WrongFormat is null, 'ToDecimal_WrongFormat', 'null', toString(ToDecimal_WrongFormat))

// ToInteger
define ToInteger_NoSign: ToInteger('12345')
define ToInteger_PositiveSign: ToInteger('+12345')
define ToInteger_NegativeSign: ToInteger('-12345')
define ToInteger_TooLargeInt: ToInteger('2147483648')
define ToInteger_TooSmallInt: ToInteger('-2147483649')

define test_ToInteger_NoSign: TestMessage(ToInteger_NoSign = 12345, 'ToInteger_NoSign', toString(12345), toString(ToInteger_NoSign))
define test_ToInteger_PositiveSign: TestMessage(ToInteger_PositiveSign = 12345, 'ToInteger_PositiveSign', toString(12345), toString(ToInteger_PositiveSign))
define test_ToInteger_NegativeSign: TestMessage(ToInteger_NegativeSign = -12345, 'ToInteger_NegativeSign', toString(-12345), toString(ToInteger_NegativeSign))
define test_ToInteger_TooLargeInt: TestMessage(ToInteger_TooLargeInt is null, 'ToInteger_TooLargeInt', 'null', toString(ToInteger_TooLargeInt))
define test_ToInteger_TooSmallInt: TestMessage(ToInteger_TooSmallInt is null, 'ToInteger_TooSmallInt', 'null', toString(ToInteger_TooSmallInt))

// ToQuantity
define ToQuantity_NullArg: ToQuantity((null as String))
define ToQuantity_WrongFormatQuantity: ToQuantity('abc')
define ToQuantity_TooLargeQuantity: ToQuantity('444444444444444444444444444444 \'\'')
define ToQuantity_TooSmallQuantity: ToQuantity('-444444444444444444444444444444 \'\'')

define test_ToQuantity_NullArg: TestMessage(ToQuantity_NullArg is null, 'ToQuantity_NullArg', 'null', toString(ToQuantity_NullArg))
define test_ToQuantity_WrongFormatQuantity: TestMessage(ToQuantity_WrongFormatQuantity is null, 'ToQuantity_WrongFormatQuantity', 'null', toString(ToQuantity_WrongFormatQuantity))
define test_ToQuantity_TooLargeQuantity: TestMessage(ToQuantity_TooLargeQuantity is null, 'ToQuantity_TooLargeQuantity', 'null', toString(ToQuantity_TooLargeQuantity))
define test_ToQuantity_TooSmallQuantity: TestMessage(ToQuantity_TooSmallQuantity is null, 'ToQuantity_TooSmallQuantity', 'null', toString(ToQuantity_TooSmallQuantity))

// ToTime
define ToTime_NullArgTime: ToTime((null as String))
define ToTime_TimeH: ToTime('T02')
define ToTime_TimeHM: ToTime('T02:04')
define ToTime_TimeHMS: ToTime('T02:04:59')
define ToTime_TimeHMSMs: ToTime('T02:04:59.123')
define ToTime_IncorrectFormatTime: ToTime('10:00PM')
define ToTime_InvalidTime: ToTime('25:99.000+00.00')
define ToTime_HourTooHigh: ToTime('T24')
define ToTime_MinuteTooHigh: ToTime('T23:60')
define ToTime_SecondTooHigh: ToTime('T23:59:60')

define test_ToTime_NullArgTime: TestMessage(ToTime_NullArgTime is null, 'ToTime_NullArgTime', 'null', toString(ToTime_NullArgTime))
define test_ToTime_TimeH: TestMessage(ToTime_TimeH = @T02, 'ToTime_TimeH', toString(@T02), toString(ToTime_TimeH))
define test_ToTime_TimeHM: TestMessage(ToTime_TimeHM = @T02:04, 'ToTime_TimeHM', toString(@T02:04), toString(ToTime_TimeHM))
define test_ToTime_TimeHMS: TestMessage(ToTime_TimeHMS = @T02:04:59, 'ToTime_TimeHMS', toString(@T02:04:59), toString(ToTime_TimeHMS))
define test_ToTime_TimeHMSMs: TestMessage(ToTime_TimeHMSMs = @T02:04:59.123, 'ToTime_TimeHMSMs', toString(@T02:04:59.123), toString(ToTime_TimeHMSMs))
define test_ToTime_IncorrectFormatTime: TestMessage(ToTime_IncorrectFormatTime is null, 'ToTime_IncorrectFormatTime', 'null', toString(ToTime_IncorrectFormatTime))
define test_ToTime_InvalidTime: TestMessage(ToTime_InvalidTime is null, 'ToTime_InvalidTime', 'null', toString(ToTime_InvalidTime))
define test_ToTime_HourTooHigh: TestMessage(ToTime_HourTooHigh is null, 'ToTime_HourTooHigh', 'null', toString(ToTime_HourTooHigh))
define test_ToTime_MinuteTooHigh: TestMessage(ToTime_MinuteTooHigh is null, 'ToTime_MinuteTooHigh', 'null', toString(ToTime_MinuteTooHigh))
define test_ToTime_SecondTooHigh: TestMessage(ToTime_SecondTooHigh is null, 'ToTime_SecondTooHigh', 'null', toString(ToTime_SecondTooHigh))

// ToBoolean
define ToBoolean_UpperCaseTrue: ToBoolean('TRUE')
define ToBoolean_UpperCaseFalse: ToBoolean('FALSE')
define ToBoolean_LowerCaseTrue: ToBoolean('true')
define ToBoolean_LowerCaseFalse: ToBoolean('false')
define ToBoolean_UpperCaseT: ToBoolean('T')
define ToBoolean_UpperCaseF: ToBoolean('F')
define ToBoolean_LowerCaseT: ToBoolean('t')
define ToBoolean_LowerCaseF: ToBoolean('f')
define ToBoolean_IsTrue: ToBoolean('y')
define ToBoolean_IsFalse: ToBoolean('0')
define ToBoolean_IsNull: ToBoolean('falsetto')

define test_ToBoolean_UpperCaseTrue: TestMessage(ToBoolean_UpperCaseTrue, 'ToBoolean_UpperCaseTrue', toString(true), toString(ToBoolean_UpperCaseTrue))
define test_ToBoolean_UpperCaseFalse: TestMessage(not ToBoolean_UpperCaseFalse, 'ToBoolean_UpperCaseFalse', toString(false), toString(ToBoolean_UpperCaseFalse))
define test_ToBoolean_LowerCaseTrue: TestMessage(ToBoolean_LowerCaseTrue, 'ToBoolean_LowerCaseTrue', toString(true), toString(ToBoolean_LowerCaseTrue))
define test_ToBoolean_LowerCaseFalse: TestMessage(not ToBoolean_LowerCaseFalse, 'ToBoolean_LowerCaseFalse', toString(false), toString(ToBoolean_LowerCaseFalse))
define test_ToBoolean_UpperCaseT: TestMessage(ToBoolean_UpperCaseT, 'ToBoolean_UpperCaseT', toString(true), toString(ToBoolean_UpperCaseT))
define test_ToBoolean_UpperCaseF: TestMessage(not ToBoolean_UpperCaseF, 'ToBoolean_UpperCaseF', toString(false), toString(ToBoolean_UpperCaseF))
define test_ToBoolean_LowerCaseT: TestMessage(ToBoolean_LowerCaseT, 'ToBoolean_LowerCaseT', toString(true), toString(ToBoolean_LowerCaseT))
define test_ToBoolean_LowerCaseF: TestMessage(not ToBoolean_LowerCaseF, 'ToBoolean_LowerCaseF', toString(false), toString(ToBoolean_LowerCaseF))
define test_ToBoolean_IsTrue: TestMessage(ToBoolean_IsTrue, 'ToBoolean_IsTrue', toString(true), toString(ToBoolean_IsTrue))
define test_ToBoolean_IsFalse: TestMessage(not ToBoolean_IsFalse, 'ToBoolean_IsFalse', toString(false), toString(ToBoolean_IsFalse))
define test_ToBoolean_IsNull: TestMessage(ToBoolean_IsNull is null, 'ToBoolean_IsNull', 'null', toString(ToBoolean_IsNull))

// ToConcept
define ToConcept_IsValid: ToConcept(Code { system: 'http://loinc.org', code: '8480-6' }) // Concept { codes: { Code { system: 'http://loinc.org', code: '8480-6' } } }
define ToConcept_IsNull: ToConcept(null as Code)

define test_ToConcept_IsValid: TestMessage(ToConcept_IsValid ~ Concept { codes: { Code { system: 'http://loinc.org', code: '8480-6' } } }, 'ToConcept_IsValid', toString(Concept { codes: { Code { system: 'http://loinc.org', code: '8480-6' } } }), toString(ToConcept_IsValid))
define test_ToConcept_IsNull: TestMessage(ToConcept_IsNull is null, 'ToConcept_IsNull', 'null', toString(ToConcept_IsNull))

/*
************************
  End Conversion Tests
************************
*/

/*
************************
  Start Date Tests
************************
*/

// Globals
define IdesOfMarch_Date: Date(2000, 3, 15)
define ImpreciseIdesOfMarch_Date: Date(2000, 3)
define NewYear2013_Date: Date(2013, 1, 1)
define NewYear2014_Date: Date(2014, 1, 1)
define January2014_Date: Date(2014, 1)
define February2014_Date: Date(2014, 2)
define DecTen2013_Date: Date(2013, 12, 10)
define JanOne2015_Date: Date(2015, 1, 1)
define January2015_Date: Date(2015, 1)
define June15th2013_Date: Date(2013, 6, 15)

// DateComponentFrom
define DateComponentFrom_Year: year from IdesOfMarch_Date
define DateComponentFrom_Month: month from IdesOfMarch_Date
define DateComponentFrom_Day: day from IdesOfMarch
define DateComponentFrom_ImpreciseComponentTuple:
    Tuple {
        Year: year from ImpreciseIdesOfMarch_Date,
        Month: month from ImpreciseIdesOfMarch_Date,
        Day: day from ImpreciseIdesOfMarch_Date
    }
define DateComponentFrom_ImpreciseComponentTuple_Day: DateComponentFrom_ImpreciseComponentTuple.Day
define DateComponentFrom_NullDate: year from (null as Date)

define test_DateComponentFrom_Year: TestMessage(DateComponentFrom_Year = 2000, 'DateComponentFrom_Year', toString(2000), toString(DateComponentFrom_Year))
define test_DateComponentFrom_Month: TestMessage(DateComponentFrom_Month = 3, 'DateComponentFrom_Month', toString(3), toString(DateComponentFrom_Month))
define test_DateComponentFrom_Day: TestMessage(DateComponentFrom_Day = 15, 'DateComponentFrom_Day', toString(15), toString(DateComponentFrom_Day))
define test_DateComponentFrom_ImpreciseComponentTuple_Day: TestMessage(DateComponentFrom_ImpreciseComponentTuple_Day is null, 'DateComponentFrom_ImpreciseComponentTuple_Day', 'null', toString(DateComponentFrom_ImpreciseComponentTuple_Day))

// SameAs
define SameAs_Date_SameYear: Date(2000, 3, 15) same year as Date(2000, 11, 23)
define SameAs_Date_NotSameYear: Date(2000, 3, 15) same year as Date(2001, 11, 23)
define SameAs_Date_SameMonth: Date(2000, 3, 15) same month as Date(2000, 3, 23)
define SameAs_Date_NotSameMonth: Date(2000, 3, 15) same month as Date(2000, 4, 23)
define SameAs_Date_SameMonthWrongYear: Date(2000, 3, 15) same month as Date(2001, 3, 23)
define SameAs_Date_SameDay: Date(2000, 3, 15) same day as Date(2000, 3, 15)
define SameAs_Date_NotSameDay: Date(2000, 3, 15) same day as Date(2000, 3, 23)
define SameAs_Date_SameDayWrongMonth: Date(2000, 3, 15) same day as Date(2000, 4, 15)
define SameAs_Date_Same: Date(2000, 3, 15) same as Date(2000, 3, 15)
define SameAs_Date_NotSame: Date(2000, 3, 16) same as Date(2000, 3, 15)
define SameAs_Date_NullLeft: null same as Date(2000, 3, 15)
define SameAs_Date_NullRight: Date(2000, 3, 15) same as (null as Date)
define SameAs_Date_NullBoth: (null as Date) same as null

define test_SameAs_Date_SameYear: TestMessage(SameAs_Date_SameYear, 'SameAs_Date_SameYear', toString(true), toString(SameAs_Date_SameYear))
define test_SameAs_Date_NotSameYear: TestMessage(not SameAs_Date_NotSameYear, 'SameAs_Date_NotSameYear', toString(false), toString(SameAs_Date_NotSameYear))
define test_SameAs_Date_SameMonth: TestMessage(SameAs_Date_SameMonth, 'SameAs_Date_SameMonth', toString(true), toString(SameAs_Date_SameMonth))
define test_SameAs_Date_NotSameMonth: TestMessage(not SameAs_Date_NotSameMonth, 'SameAs_Date_NotSameMonth', toString(false), toString(SameAs_Date_NotSameMonth))
define test_SameAs_Date_SameMonthWrongYear: TestMessage(not SameAs_Date_SameMonthWrongYear, 'SameAs_Date_SameMonthWrongYear', toString(false), toString(SameAs_Date_SameMonthWrongYear))
define test_SameAs_Date_SameDay: TestMessage(SameAs_Date_SameDay, 'SameAs_Date_SameDay', toString(true), toString(SameAs_Date_SameDay))
define test_SameAs_Date_NotSameDay: TestMessage(not SameAs_Date_NotSameDay, 'SameAs_Date_NotSameDay', toString(false), toString(SameAs_Date_NotSameDay))
define test_SameAs_Date_SameDayWrongMonth: TestMessage(not SameAs_Date_SameDayWrongMonth, 'SameAs_Date_SameDayWrongMonth', toString(false), toString(SameAs_Date_SameDayWrongMonth))
define test_SameAs_Date_Same: TestMessage(SameAs_Date_Same, 'SameAs_Date_Same', toString(true), toString(SameAs_Date_Same))
define test_SameAs_Date_NotSame: TestMessage(not SameAs_Date_NotSame, 'SameAs_Date_NotSame', toString(false), toString(SameAs_Date_NotSame))
define test_SameAs_Date_NullLeft: TestMessage(SameAs_Date_NullLeft is null, 'SameAs_Date_NullLeft', 'null', toString(SameAs_Date_NullLeft))
define test_SameAs_Date_NullRight: TestMessage(SameAs_Date_NullRight is null, 'SameAs_Date_NullRight', 'null', toString(SameAs_Date_NullRight))
define test_SameAs_Date_NullBoth: TestMessage(SameAs_Date_NullBoth is null, 'SameAs_Date_NullBoth', 'null', toString(SameAs_Date_NullBoth))

// SameOrAfter
define SameOrAfter_Date_SameYear: Date(2000, 3, 15) same year or after Date(2000, 11, 23)
define SameOrAfter_Date_YearAfter: Date(2000, 3, 15) same year or after Date(1999, 11, 23)
define SameOrAfter_Date_YearBefore: Date(2000, 3, 15) same year or after Date(2001, 11, 23)
define SameOrAfter_Date_SameMonth: Date(2000, 3, 15) same month or after Date(2000, 3, 23)
define SameOrAfter_Date_MonthAfter: Date(2000, 3, 15) same month or after Date(2000, 2, 23)
define SameOrAfter_Date_MonthBefore: Date(2000, 3, 15) same month or after Date(2000, 4, 23)
define SameOrAfter_Date_SameDay: Date(2000, 3, 15) same day or after Date(2000, 3, 15)
define SameOrAfter_Date_DayAfter: Date(2000, 3, 15) same day or after Date(2000, 3, 14)
define SameOrAfter_Date_DayBefore: Date(2000, 3, 15) same day or after Date(2000, 3, 16)
define SameOrAfter_Date_Same: Date(2000, 3, 15) same or after Date(2000, 3, 15)
define SameOrAfter_Date_After: Date(2000, 3, 15) same or after Date(2000, 3, 15)
define SameOrAfter_Date_Before: Date(2000, 3, 14) same or after Date(2000, 3, 15)
define SameOrAfter_Date_SameDayMonthBefore: Date(2000, 3, 15) same day or after Date(2000, 4, 15)
define SameOrAfter_Date_DayAfterMonthBefore: Date(2000, 3, 15) same day or after Date(2000, 4, 14)
define SameOrAfter_Date_DayBeforeMonthAfter: Date(2000, 3, 15) same day or after Date(2000, 2, 16)
define SameOrAfter_Date_ImpreciseDay: Date(2000, 3, 15) same day or after Date(2000, 3)
define SameOrAfter_Date_ImpreciseDayMonthAfter: Date(2000, 3, 15) same day or after Date(2000, 2)
define SameOrAfter_Date_ImpreciseDayMonthBefore: Date(2000, 3, 15) same day or after Date(2000, 4)
define SameOrAfter_Date_NullLeft: null same or after Date(2000, 3, 15)
define SameOrAfter_Date_NullRight: Date(2000, 3, 15) same or after null
define SameOrAfter_Date_NullBoth: (null as Date) same or after null

define test_SameOrAfter_Date_SameYear: TestMessage(SameOrAfter_Date_SameYear, 'SameOrAfter_Date_SameYear', toString(true), toString(SameOrAfter_Date_SameYear))
define test_SameOrAfter_Date_YearAfter: TestMessage(SameOrAfter_Date_YearAfter, 'SameOrAfter_Date_YearAfter', toString(true), toString(SameOrAfter_Date_YearAfter))
define test_SameOrAfter_Date_YearBefore: TestMessage(not SameOrAfter_Date_YearBefore, 'SameOrAfter_Date_YearBefore', toString(false), toString(SameOrAfter_Date_YearBefore))
define test_SameOrAfter_Date_SameMonth: TestMessage(SameOrAfter_Date_SameMonth, 'SameOrAfter_Date_SameMonth', toString(true), toString(SameOrAfter_Date_SameMonth))
define test_SameOrAfter_Date_MonthAfter: TestMessage(SameOrAfter_Date_MonthAfter, 'SameOrAfter_Date_MonthAfter', toString(true), toString(SameOrAfter_Date_MonthAfter))
define test_SameOrAfter_Date_MonthBefore: TestMessage(not SameOrAfter_Date_MonthBefore, 'SameOrAfter_Date_MonthBefore', toString(false), toString(SameOrAfter_Date_MonthBefore))
define test_SameOrAfter_Date_SameDay: TestMessage(SameOrAfter_Date_SameDay, 'SameOrAfter_Date_SameDay', toString(true), toString(SameOrAfter_Date_SameDay))
define test_SameOrAfter_Date_DayAfter: TestMessage(SameOrAfter_Date_DayAfter, 'SameOrAfter_Date_DayAfter', toString(true), toString(SameOrAfter_Date_DayAfter))
define test_SameOrAfter_Date_DayBefore: TestMessage(not SameOrAfter_Date_DayBefore, 'SameOrAfter_Date_DayBefore', toString(false), toString(SameOrAfter_Date_DayBefore))
define test_SameOrAfter_Date_Same: TestMessage(SameOrAfter_Date_Same, 'SameOrAfter_Date_Same', toString(true), toString(SameOrAfter_Date_Same))
define test_SameOrAfter_Date_After: TestMessage(SameOrAfter_Date_After, 'SameOrAfter_Date_After', toString(true), toString(SameOrAfter_Date_After))
define test_SameOrAfter_Date_Before: TestMessage(not SameOrAfter_Date_Before, 'SameOrAfter_Date_Before', toString(false), toString(SameOrAfter_Date_Before))
define test_SameOrAfter_Date_SameDayMonthBefore: TestMessage(not SameOrAfter_Date_SameDayMonthBefore, 'SameOrAfter_Date_SameDayMonthBefore', toString(false), toString(SameOrAfter_Date_SameDayMonthBefore))
define test_SameOrAfter_Date_DayAfterMonthBefore: TestMessage(not SameOrAfter_Date_DayAfterMonthBefore, 'SameOrAfter_Date_DayAfterMonthBefore', toString(false), toString(SameOrAfter_Date_DayAfterMonthBefore))
define test_SameOrAfter_Date_DayBeforeMonthAfter: TestMessage(SameOrAfter_Date_DayBeforeMonthAfter, 'SameOrAfter_Date_DayBeforeMonthAfter', toString(true), toString(SameOrAfter_Date_DayBeforeMonthAfter))
define test_SameOrAfter_Date_ImpreciseDay: TestMessage(SameOrAfter_Date_ImpreciseDay is null, 'SameOrAfter_Date_ImpreciseDay', 'null', toString(SameOrAfter_Date_ImpreciseDay))
define test_SameOrAfter_Date_ImpreciseDayMonthAfter: TestMessage(SameOrAfter_Date_ImpreciseDayMonthAfter, 'SameOrAfter_Date_ImpreciseDayMonthAfter', toString(true), toString(SameOrAfter_Date_ImpreciseDayMonthAfter))
define test_SameOrAfter_Date_ImpreciseDayMonthBefore: TestMessage(not SameOrAfter_Date_ImpreciseDayMonthBefore, 'SameOrAfter_Date_ImpreciseDayMonthBefore', toString(false), toString(SameOrAfter_Date_ImpreciseDayMonthBefore))
define test_SameOrAfter_Date_NullLeft: TestMessage(SameOrAfter_Date_NullLeft is null, 'SameOrAfter_Date_NullLeft', 'null', toString(SameOrAfter_Date_NullLeft))
define test_SameOrAfter_Date_NullRight: TestMessage(SameOrAfter_Date_NullRight is null, 'SameOrAfter_Date_NullRight', 'null', toString(SameOrAfter_Date_NullRight))
define test_SameOrAfter_Date_NullBoth: TestMessage(SameOrAfter_Date_NullBoth is null, 'SameOrAfter_Date_NullBoth', 'null', toString(SameOrAfter_Date_NullBoth))

// SameOrBefore
define SameOrBefore_Date_SameYear: Date(2000, 3, 15) same year or before Date(2000, 11, 23)
define SameOrBefore_Date_YearAfter: Date(2000, 3, 15) same year or before Date(1999, 11, 23)
define SameOrBefore_Date_YearBefore: Date(2000, 3, 15) same year or before Date(2001, 11, 23)
define SameOrBefore_Date_SameMonth: Date(2000, 3, 15) same month or before Date(2000, 3, 23)
define SameOrBefore_Date_MonthAfter: Date(2000, 3, 15) same month or before Date(2000, 2, 23)
define SameOrBefore_Date_MonthBefore: Date(2000, 3, 15) same month or before Date(2000, 4, 23)
define SameOrBefore_Date_SameDay: Date(2000, 3, 15) same day or before Date(2000, 3, 15)
define SameOrBefore_Date_DayAfter: Date(2000, 3, 15) same day or before Date(2000, 3, 14)
define SameOrBefore_Date_DayBefore: Date(2000, 3, 15) same day or before Date(2000, 3, 16)
define SameOrBefore_Date_Same: Date(2000, 3, 15) same or before Date(2000, 3, 15)
define SameOrBefore_Date_After: Date(2000, 3, 16) same or before Date(2000, 3, 15)
define SameOrBefore_Date_Before: Date(2000, 3, 15) same or before Date(2000, 3, 15)
define SameOrBefore_Date_SameDayMonthBefore: Date(2000, 3, 15) same day or before Date(2000, 4, 15)
define SameOrBefore_Date_DayAfterMonthBefore: Date(2000, 3, 15) same day or before Date(2000, 4, 14)
define SameOrBefore_Date_DayBeforeMonthAfter: Date(2000, 3, 15) same day or before Date(2000, 2, 16)
define SameOrBefore_Date_ImpreciseDay: Date(2000, 3, 15) same day or before Date(2000, 3)
define SameOrBefore_Date_ImpreciseDayMonthAfter: Date(2000, 3, 15) same day or before Date(2000, 2)
define SameOrBefore_Date_ImpreciseDayMonthBefore: Date(2000, 3, 15) same day or before Date(2000, 4)
define SameOrBefore_Date_NullLeft: null same or before Date(2000, 3, 15)
define SameOrBefore_Date_NullRight: Date(2000, 3, 15) same or before null
define SameOrBefore_Date_NullBoth: (null as Date) same or before null

define test_SameOrBefore_Date_SameYear: TestMessage(SameOrBefore_Date_SameYear, 'SameOrBefore_Date_SameYear', toString(true), toString(SameOrBefore_Date_SameYear))
define test_SameOrBefore_Date_YearAfter: TestMessage(not SameOrBefore_Date_YearAfter, 'SameOrBefore_Date_YearAfter', toString(false), toString(SameOrBefore_Date_YearAfter))
define test_SameOrBefore_Date_YearBefore: TestMessage(SameOrBefore_Date_YearBefore, 'SameOrBefore_Date_YearBefore', toString(true), toString(SameOrBefore_Date_YearBefore))
define test_SameOrBefore_Date_SameMonth: TestMessage(SameOrBefore_Date_SameMonth, 'SameOrBefore_Date_SameMonth', toString(true), toString(SameOrBefore_Date_SameMonth))
define test_SameOrBefore_Date_MonthAfter: TestMessage(not SameOrBefore_Date_MonthAfter, 'SameOrBefore_Date_MonthAfter', toString(false), toString(SameOrBefore_Date_MonthAfter))
define test_SameOrBefore_Date_MonthBefore: TestMessage(SameOrBefore_Date_MonthBefore, 'SameOrBefore_Date_MonthBefore', toString(true), toString(SameOrBefore_Date_MonthBefore))
define test_SameOrBefore_Date_SameDay: TestMessage(SameOrBefore_Date_SameDay, 'SameOrBefore_Date_SameDay', toString(true), toString(SameOrBefore_Date_SameDay))
define test_SameOrBefore_Date_DayAfter: TestMessage(not SameOrBefore_Date_DayAfter, 'SameOrBefore_Date_DayAfter', toString(false), toString(SameOrBefore_Date_DayAfter))
define test_SameOrBefore_Date_DayBefore: TestMessage(SameOrBefore_Date_DayBefore, 'SameOrBefore_Date_DayBefore', toString(true), toString(SameOrBefore_Date_DayBefore))
define test_SameOrBefore_Date_Same: TestMessage(SameOrBefore_Date_Same, 'SameOrBefore_Date_Same', toString(true), toString(SameOrBefore_Date_Same))
define test_SameOrBefore_Date_After: TestMessage(not SameOrBefore_Date_After, 'SameOrBefore_Date_After', toString(false), toString(SameOrBefore_Date_After))
define test_SameOrBefore_Date_Before: TestMessage(SameOrBefore_Date_Before, 'SameOrBefore_Date_Before', toString(true), toString(SameOrBefore_Date_Before))
define test_SameOrBefore_Date_SameDayMonthBefore: TestMessage(SameOrBefore_Date_SameDayMonthBefore, 'SameOrBefore_Date_SameDayMonthBefore', toString(true), toString(SameOrBefore_Date_SameDayMonthBefore))
define test_SameOrBefore_Date_DayAfterMonthBefore: TestMessage(SameOrBefore_Date_DayAfterMonthBefore, 'SameOrBefore_Date_DayAfterMonthBefore', toString(true), toString(SameOrBefore_Date_DayAfterMonthBefore))
define test_SameOrBefore_Date_DayBeforeMonthAfter: TestMessage(not SameOrBefore_Date_DayBeforeMonthAfter, 'SameOrBefore_Date_DayBeforeMonthAfter', toString(false), toString(SameOrBefore_Date_DayBeforeMonthAfter))
define test_SameOrBefore_Date_ImpreciseDay: TestMessage(SameOrBefore_Date_ImpreciseDay is null, 'SameOrBefore_Date_ImpreciseDay', 'null', toString(SameOrBefore_Date_ImpreciseDay))
define test_SameOrBefore_Date_ImpreciseDayMonthAfter: TestMessage(not SameOrBefore_Date_ImpreciseDayMonthAfter, 'SameOrBefore_Date_ImpreciseDayMonthAfter', toString(false), toString(SameOrBefore_Date_ImpreciseDayMonthAfter))
define test_SameOrBefore_Date_ImpreciseDayMonthBefore: TestMessage(SameOrBefore_Date_ImpreciseDayMonthBefore, 'SameOrBefore_Date_ImpreciseDayMonthBefore', toString(true), toString(SameOrBefore_Date_ImpreciseDayMonthBefore))
define test_SameOrBefore_Date_NullLeft: TestMessage(SameOrBefore_Date_NullLeft is null, 'SameOrBefore_Date_NullLeft', 'null', toString(SameOrBefore_Date_NullLeft))
define test_SameOrBefore_Date_NullRight: TestMessage(SameOrBefore_Date_NullRight is null, 'SameOrBefore_Date_NullRight', 'null', toString(SameOrBefore_Date_NullRight))
define test_SameOrBefore_Date_NullBoth: TestMessage(SameOrBefore_Date_NullBoth is null, 'SameOrBefore_Date_NullBoth', 'null', toString(SameOrBefore_Date_NullBoth))

// After
define After_Date_SameYear: Date(2000, 12, 15) after year of Date(2000, 11, 23)
define After_Date_YearAfter: Date(2000, 12, 15) after year of Date(1999, 11, 23)
define After_Date_YearBefore: Date(2000, 12, 15) after year of Date(2001, 11, 23)
define After_Date_SameMonth: Date(2000, 3, 25) after month of Date(2000, 3, 23)
define After_Date_MonthAfter: Date(2000, 3, 25) after month of Date(2000, 2, 23)
define After_Date_MonthBefore: Date(2000, 3, 25) after month of Date(2000, 4, 23)
define After_Date_SameDay: Date(2000, 3, 15) after day of Date(2000, 3, 15)
define After_Date_DayAfter: Date(2000, 3, 15) after day of Date(2000, 3, 14)
define After_Date_DayBefore: Date(2000, 3, 15) after day of Date(2000, 3, 16)
define After_Date_Same: Date(2000, 3, 15) after Date(2000, 3, 15)
define After_Date_After: Date(2000, 3, 16) after Date(2000, 3, 15)
define After_Date_Before: Date(2000, 3, 15) after Date(2000, 3, 15)
define After_Date_ImpreciseDay: Date(2000, 3, 15) after day of Date(2000, 3)
define After_Date_ImpreciseDayMonthAfter: Date(2000, 3, 15) after day of Date(2000, 2)
define After_Date_ImpreciseDayMonthBefore: Date(2000, 3, 15) after day of Date(2000, 4)
define After_Date_NullLeft: null after Date(2000, 3, 15)
define After_Date_NullRight: Date(2000, 3, 15) after null
define After_Date_NullBoth: (null as Date) after null

define test_After_Date_SameYear: TestMessage(not After_Date_SameYear, 'After_Date_SameYear', toString(false), toString(After_Date_SameYear))
define test_After_Date_YearAfter: TestMessage(After_Date_YearAfter, 'After_Date_YearAfter', toString(true), toString(After_Date_YearAfter))
define test_After_Date_YearBefore: TestMessage(not After_Date_YearBefore, 'After_Date_YearBefore', toString(false), toString(After_Date_YearBefore))
define test_After_Date_SameMonth: TestMessage(not After_Date_SameMonth, 'After_Date_SameMonth', toString(false), toString(After_Date_SameMonth))
define test_After_Date_MonthAfter: TestMessage(After_Date_MonthAfter, 'After_Date_MonthAfter', toString(true), toString(After_Date_MonthAfter))
define test_After_Date_MonthBefore: TestMessage(not After_Date_MonthBefore, 'After_Date_MonthBefore', toString(false), toString(After_Date_MonthBefore))
define test_After_Date_SameDay: TestMessage(not After_Date_SameDay, 'After_Date_SameDay', toString(false), toString(After_Date_SameDay))
define test_After_Date_DayAfter: TestMessage(After_Date_DayAfter, 'After_Date_DayAfter', toString(true), toString(After_Date_DayAfter))
define test_After_Date_DayBefore: TestMessage(not After_Date_DayBefore, 'After_Date_DayBefore', toString(false), toString(After_Date_DayBefore))
define test_After_Date_Same: TestMessage(not After_Date_Same, 'After_Date_Same', toString(false), toString(After_Date_Same))
define test_After_Date_After: TestMessage(After_Date_After, 'After_Date_After', toString(true), toString(After_Date_After))
define test_After_Date_Before: TestMessage(not After_Date_Before, 'After_Date_Before', toString(false), toString(After_Date_Before))
define test_After_Date_ImpreciseDay: TestMessage(After_Date_ImpreciseDay is null, 'After_Date_ImpreciseDay', 'null', toString(After_Date_ImpreciseDay))
define test_After_Date_ImpreciseDayMonthAfter: TestMessage(After_Date_ImpreciseDayMonthAfter, 'After_Date_ImpreciseDayMonthAfter', toString(true), toString(After_Date_ImpreciseDayMonthAfter))
define test_After_Date_ImpreciseDayMonthBefore: TestMessage(not After_Date_ImpreciseDayMonthBefore, 'After_Date_ImpreciseDayMonthBefore', toString(false), toString(After_Date_ImpreciseDayMonthBefore))
define test_After_Date_NullLeft: TestMessage(After_Date_NullLeft is null, 'After_Date_NullLeft', 'null', toString(After_Date_NullLeft))
define test_After_Date_NullRight: TestMessage(After_Date_NullRight is null, 'After_Date_NullRight', 'null', toString(After_Date_NullRight))
define test_After_Date_NullBoth: TestMessage(After_Date_NullBoth is null, 'After_Date_NullBoth', 'null', toString(After_Date_NullBoth))

// Before
define Before_Date_SameYear: Date(2000, 10, 15) before year of Date(2000, 11, 23)
define Before_Date_YearAfter: Date(2000, 10, 15) before year of Date(1999, 11, 23)
define Before_Date_YearBefore: Date(2000, 10, 15) before year of Date(2001, 11, 23)
define Before_Date_SameMonth: Date(2000, 3, 22) before month of Date(2000, 3, 23)
define Before_Date_MonthAfter: Date(2000, 3, 22) before month of Date(2000, 2, 23)
define Before_Date_MonthBefore: Date(2000, 3, 22) before month of Date(2000, 4, 23)
define Before_Date_SameDay: Date(2000, 3, 15) before day of Date(2000, 3, 15)
define Before_Date_DayAfter: Date(2000, 3, 15) before day of Date(2000, 3, 14)
define Before_Date_DayBefore: Date(2000, 3, 15) before day of Date(2000, 3, 16)
define Before_Date_Same: Date(2000, 3, 15) before Date(2000, 3, 15)
define Before_Date_After: Date(2000, 3, 15) before Date(2000, 3, 15)
define Before_Date_Before: Date(2000, 3, 14) before Date(2000, 3, 15)
define Before_Date_ImpreciseDay: Date(2000, 3, 15) before day of Date(2000, 3)
define Before_Date_ImpreciseDayMonthAfter: Date(2000, 3, 15) before day of Date(2000, 2)
define Before_Date_ImpreciseDayMonthBefore: Date(2000, 3, 15) before day of Date(2000, 4)
define Before_Date_NullLeft: null before Date(2000, 3, 15)
define Before_Date_NullRight: Date(2000, 3, 15) before null
define Before_Date_NullBoth: (null as Date) before null

define test_Before_Date_SameYear: TestMessage(not Before_Date_SameYear, 'Before_Date_SameYear', toString(false), toString(Before_Date_SameYear))
define test_Before_Date_YearAfter: TestMessage(not Before_Date_YearAfter, 'Before_Date_YearAfter', toString(false), toString(Before_Date_YearAfter))
define test_Before_Date_YearBefore: TestMessage(Before_Date_YearBefore, 'Before_Date_YearBefore', toString(true), toString(Before_Date_YearBefore))
define test_Before_Date_SameMonth: TestMessage(not Before_Date_SameMonth, 'Before_Date_SameMonth', toString(false), toString(Before_Date_SameMonth))
define test_Before_Date_MonthAfter: TestMessage(not Before_Date_MonthAfter, 'Before_Date_MonthAfter', toString(false), toString(Before_Date_MonthAfter))
define test_Before_Date_MonthBefore: TestMessage(Before_Date_MonthBefore, 'Before_Date_MonthBefore', toString(true), toString(Before_Date_MonthBefore))
define test_Before_Date_SameDay: TestMessage(not Before_Date_SameDay, 'Before_Date_SameDay', toString(false), toString(Before_Date_SameDay))
define test_Before_Date_DayAfter: TestMessage(not Before_Date_DayAfter, 'Before_Date_DayAfter', toString(false), toString(Before_Date_DayAfter))
define test_Before_Date_DayBefore: TestMessage(Before_Date_DayBefore, 'Before_Date_DayBefore', toString(true), toString(Before_Date_DayBefore))
define test_Before_Date_Same: TestMessage(not Before_Date_Same, 'Before_Date_Same', toString(false), toString(Before_Date_Same))
define test_Before_Date_After: TestMessage(not Before_Date_After, 'Before_Date_After', toString(false), toString(Before_Date_After))
define test_Before_Date_Before: TestMessage(Before_Date_Before, 'Before_Date_Before', toString(true), toString(Before_Date_Before))
define test_Before_Date_ImpreciseDay: TestMessage(Before_Date_ImpreciseDay is null, 'Before_Date_ImpreciseDay', 'null', toString(Before_Date_ImpreciseDay))
define test_Before_Date_ImpreciseDayMonthAfter: TestMessage(not Before_Date_ImpreciseDayMonthAfter, 'Before_Date_ImpreciseDayMonthAfter', toString(false), toString(Before_Date_ImpreciseDayMonthAfter))
define test_Before_Date_ImpreciseDayMonthBefore: TestMessage(Before_Date_ImpreciseDayMonthBefore, 'Before_Date_ImpreciseDayMonthBefore', toString(true), toString(Before_Date_ImpreciseDayMonthBefore))
define test_Before_Date_NullLeft: TestMessage(Before_Date_NullLeft is null, 'Before_Date_NullLeft', 'null', toString(Before_Date_NullLeft))
define test_Before_Date_NullRight: TestMessage(Before_Date_NullRight is null, 'Before_Date_NullRight', 'null', toString(Before_Date_NullRight))
define test_Before_Date_NullBoth: TestMessage(Before_Date_NullBoth is null, 'Before_Date_NullBoth', 'null', toString(Before_Date_NullBoth))

// DifferenceBetween
define DifferenceBetween_Date_YearsBetween: difference in years between NewYear2013_Date and NewYear2014_Date
define DifferenceBetween_Date_MonthsBetween: difference in months between NewYear2013_Date and NewYear2014_Date
define DifferenceBetween_Date_WeeksBetween: difference in weeks between NewYear2013_Date and NewYear2014_Date
define DifferenceBetween_Date_DaysBetween: difference in days between NewYear2013_Date and NewYear2014_Date
define DifferenceBetween_Date_YearsBetweenUncertainty: difference in years between NewYear2014_Date and January2014_Date
define DifferenceBetween_Date_MonthsBetweenUncertainty: difference in months between NewYear2014_Date and January2014_Date
define DifferenceBetween_Date_WeeksBetweenUncertainty: difference in weeks between NewYear2014_Date and January2014_Date
define DifferenceBetween_Date_DaysBetweenUncertainty: difference in days between NewYear2014_Date and January2014_Date

define test_DifferenceBetween_Date_YearsBetween: TestMessage(DifferenceBetween_Date_YearsBetween = 1, 'DifferenceBetween_Date_YearsBetween', toString(1), toString(DifferenceBetween_Date_YearsBetween))
define test_DifferenceBetween_Date_MonthsBetween: TestMessage(DifferenceBetween_Date_MonthsBetween = 12, 'DifferenceBetween_Date_MonthsBetween', toString(12), toString(DifferenceBetween_Date_MonthsBetween))
define test_DifferenceBetween_Date_WeeksBetween: TestMessage(DifferenceBetween_Date_WeeksBetween = 52, 'DifferenceBetween_Date_WeeksBetween', toString(52), toString(DifferenceBetween_Date_WeeksBetween))
define test_DifferenceBetween_Date_DaysBetween: TestMessage(DifferenceBetween_Date_DaysBetween = 365, 'DifferenceBetween_Date_DaysBetween', toString(365), toString(DifferenceBetween_Date_DaysBetween))
define test_DifferenceBetween_Date_YearsBetweenUncertainty: TestMessage(DifferenceBetween_Date_YearsBetweenUncertainty = 0, 'DifferenceBetween_Date_YearsBetweenUncertainty', toString(0), toString(DifferenceBetween_Date_YearsBetweenUncertainty))
define test_DifferenceBetween_Date_MonthsBetweenUncertainty: TestMessage(DifferenceBetween_Date_MonthsBetweenUncertainty = 0, 'DifferenceBetween_Date_MonthsBetweenUncertainty', toString(0), toString(DifferenceBetween_Date_MonthsBetweenUncertainty))
define test_DifferenceBetween_Date_WeeksBetweenUncertainty: TestMessage(DifferenceBetween_Date_WeeksBetweenUncertainty > -1 and (DifferenceBetween_Date_WeeksBetweenUncertainty > 0) is null and DifferenceBetween_Date_WeeksBetweenUncertainty < 5 and (DifferenceBetween_Date_WeeksBetweenUncertainty < 4) is null, 'DifferenceBetween_Date_WeeksBetweenUncertainty', toString(true), toString(DifferenceBetween_Date_WeeksBetweenUncertainty > -1 and (DifferenceBetween_Date_WeeksBetweenUncertainty > 0) is null and DifferenceBetween_Date_WeeksBetweenUncertainty < 5 and (DifferenceBetween_Date_WeeksBetweenUncertainty < 4) is null))
define test_DifferenceBetween_Date_DaysBetweenUncertainty: TestMessage(DifferenceBetween_Date_DaysBetweenUncertainty > -1 and (DifferenceBetween_Date_DaysBetweenUncertainty > 0) is null and DifferenceBetween_Date_DaysBetweenUncertainty < 31 and (DifferenceBetween_Date_DaysBetweenUncertainty < 30) is null, 'DifferenceBetween_Date_DaysBetweenUncertainty', toString(true), toString(DifferenceBetween_Date_DaysBetweenUncertainty > -1 and (DifferenceBetween_Date_DaysBetweenUncertainty > 0) is null and DifferenceBetween_Date_DaysBetweenUncertainty < 31 and (DifferenceBetween_Date_DaysBetweenUncertainty < 30) is null))

// DifferenceBetween Comparisons
define DifferenceBetween_Date_GreaterThan25DaysAfter: difference in days between NewYear2014_Date and February2014_Date > 25
define DifferenceBetween_Date_GreaterThan40DaysAfter: difference in days between NewYear2014_Date and February2014_Date > 40
define DifferenceBetween_Date_GreaterThan80DaysAfter: difference in days between NewYear2014_Date and February2014_Date > 80
define DifferenceBetween_Date_GreaterOrEqualTo25DaysAfter: difference in days between NewYear2014_Date and February2014_Date >= 25
define DifferenceBetween_Date_GreaterOrEqualTo40DaysAfter: difference in days between NewYear2014_Date and February2014_Date >= 40
define DifferenceBetween_Date_GreaterOrEqualTo80DaysAfter: difference in days between NewYear2014_Date and February2014_Date >= 80
define DifferenceBetween_Date_EqualTo25DaysAfter: difference in days between NewYear2014_Date and February2014_Date = 25
define DifferenceBetween_Date_EqualTo40DaysAfter: difference in days between NewYear2014_Date and February2014_Date = 40
define DifferenceBetween_Date_EqualTo80DaysAfter: difference in days between NewYear2014_Date and February2014_Date = 80
define DifferenceBetween_Date_LessOrEqualTo25DaysAfter: difference in days between NewYear2014_Date and February2014_Date <= 25
define DifferenceBetween_Date_LessOrEqualTo40DaysAfter: difference in days between NewYear2014_Date and February2014_Date <= 40
define DifferenceBetween_Date_LessOrEqualTo80DaysAfter: difference in days between NewYear2014_Date and February2014_Date <= 80
define DifferenceBetween_Date_LessThan25DaysAfter: difference in days between NewYear2014_Date and February2014_Date < 25
define DifferenceBetween_Date_LessThan40DaysAfter: difference in days between NewYear2014_Date and February2014_Date < 40
define DifferenceBetween_Date_LessThan80DaysAfter: difference in days between NewYear2014_Date and February2014_Date < 80
define DifferenceBetween_Date_TwentyFiveDaysLessThanDaysBetween: 25 < difference in days between NewYear2014_Date and February2014_Date
define DifferenceBetween_Date_FortyDaysEqualToDaysBetween: 40 = difference in days between NewYear2014_Date and February2014_Date
define DifferenceBetween_Date_TwentyFiveDaysGreaterThanDaysBetween: 25 > difference in days between NewYear2014_Date and February2014_Date

define test_DifferenceBetween_Date_GreaterThan25DaysAfter: TestMessage(DifferenceBetween_Date_GreaterThan25DaysAfter, 'DifferenceBetween_Date_GreaterThan25DaysAfter', toString(true), toString(DifferenceBetween_Date_GreaterThan25DaysAfter))
define test_DifferenceBetween_Date_GreaterThan40DaysAfter: TestMessage(DifferenceBetween_Date_GreaterThan40DaysAfter is null, 'DifferenceBetween_Date_GreaterThan40DaysAfter', 'null', toString(DifferenceBetween_Date_GreaterThan40DaysAfter))
define test_DifferenceBetween_Date_GreaterThan80DaysAfter: TestMessage(not DifferenceBetween_Date_GreaterThan80DaysAfter, 'DifferenceBetween_Date_GreaterThan80DaysAfter', toString(false), toString(DifferenceBetween_Date_GreaterThan80DaysAfter))
define test_DifferenceBetween_Date_GreaterOrEqualTo25DaysAfter: TestMessage(DifferenceBetween_Date_GreaterOrEqualTo25DaysAfter, 'DifferenceBetween_Date_GreaterOrEqualTo25DaysAfter', toString(true), toString(DifferenceBetween_Date_GreaterOrEqualTo25DaysAfter))
define test_DifferenceBetween_Date_GreaterOrEqualTo40DaysAfter: TestMessage(DifferenceBetween_Date_GreaterOrEqualTo40DaysAfter is null, 'DifferenceBetween_Date_GreaterOrEqualTo40DaysAfter', 'null', toString(DifferenceBetween_Date_GreaterOrEqualTo40DaysAfter))
define test_DifferenceBetween_Date_GreaterOrEqualTo80DaysAfter: TestMessage(not DifferenceBetween_Date_GreaterOrEqualTo80DaysAfter, 'DifferenceBetween_Date_GreaterOrEqualTo80DaysAfter', toString(false), toString(DifferenceBetween_Date_GreaterOrEqualTo80DaysAfter))
define test_DifferenceBetween_Date_EqualTo25DaysAfter: TestMessage(not DifferenceBetween_Date_EqualTo25DaysAfter, 'DifferenceBetween_Date_EqualTo25DaysAfter', toString(false), toString(DifferenceBetween_Date_EqualTo25DaysAfter))
define test_DifferenceBetween_Date_EqualTo40DaysAfter: TestMessage(DifferenceBetween_Date_EqualTo40DaysAfter is null, 'DifferenceBetween_Date_EqualTo40DaysAfter', 'null', toString(DifferenceBetween_Date_EqualTo40DaysAfter))
define test_DifferenceBetween_Date_EqualTo80DaysAfter: TestMessage(not DifferenceBetween_Date_EqualTo80DaysAfter, 'DifferenceBetween_Date_EqualTo80DaysAfter', toString(false), toString(DifferenceBetween_Date_EqualTo80DaysAfter))
define test_DifferenceBetween_Date_LessOrEqualTo25DaysAfter: TestMessage(not DifferenceBetween_Date_LessOrEqualTo25DaysAfter, 'DifferenceBetween_Date_LessOrEqualTo25DaysAfter', toString(false), toString(DifferenceBetween_Date_LessOrEqualTo25DaysAfter))
define test_DifferenceBetween_Date_LessOrEqualTo40DaysAfter: TestMessage(DifferenceBetween_Date_LessOrEqualTo40DaysAfter is null, 'DifferenceBetween_Date_LessOrEqualTo40DaysAfter', 'null', toString(DifferenceBetween_Date_LessOrEqualTo40DaysAfter))
define test_DifferenceBetween_Date_LessOrEqualTo80DaysAfter: TestMessage(DifferenceBetween_Date_LessOrEqualTo80DaysAfter, 'DifferenceBetween_Date_LessOrEqualTo80DaysAfter', toString(true), toString(DifferenceBetween_Date_LessOrEqualTo80DaysAfter))
define test_DifferenceBetween_Date_LessThan25DaysAfter: TestMessage(not DifferenceBetween_Date_LessThan25DaysAfter, 'DifferenceBetween_Date_LessThan25DaysAfter', toString(false), toString(DifferenceBetween_Date_LessThan25DaysAfter))
define test_DifferenceBetween_Date_LessThan40DaysAfter: TestMessage(DifferenceBetween_Date_LessThan40DaysAfter is null, 'DifferenceBetween_Date_LessThan40DaysAfter', 'null', toString(DifferenceBetween_Date_LessThan40DaysAfter))
define test_DifferenceBetween_Date_LessThan80DaysAfter: TestMessage(DifferenceBetween_Date_LessThan80DaysAfter, 'DifferenceBetween_Date_LessThan80DaysAfter', toString(true), toString(DifferenceBetween_Date_LessThan80DaysAfter))
define test_DifferenceBetween_Date_TwentyFiveDaysLessThanDaysBetween: TestMessage(DifferenceBetween_Date_TwentyFiveDaysLessThanDaysBetween, 'DifferenceBetween_Date_TwentyFiveDaysLessThanDaysBetween', toString(true), toString(DifferenceBetween_Date_TwentyFiveDaysLessThanDaysBetween))
define test_DifferenceBetween_Date_FortyDaysEqualToDaysBetween: TestMessage(DifferenceBetween_Date_FortyDaysEqualToDaysBetween is null, 'DifferenceBetween_Date_FortyDaysEqualToDaysBetween', 'null', toString(DifferenceBetween_Date_FortyDaysEqualToDaysBetween))
define test_DifferenceBetween_Date_TwentyFiveDaysGreaterThanDaysBetween: TestMessage(not DifferenceBetween_Date_TwentyFiveDaysGreaterThanDaysBetween, 'DifferenceBetween_Date_TwentyFiveDaysGreaterThanDaysBetween', toString(false), toString(DifferenceBetween_Date_TwentyFiveDaysGreaterThanDaysBetween))

// DurationBetween
define DurationBetween_Date_YearsBetween: years between DecTen2013_Date and JanOne2015_Date
define DurationBetween_Date_MonthsBetween: months between DecTen2013_Date and JanOne2015_Date
define DurationBetween_Date_WeeksBetween: weeks between DecTen2013_Date and JanOne2015_Date
define DurationBetween_Date_DaysBetween: days between DecTen2013_Date and JanOne2015_Date
define DurationBetween_Date_YearsBetweenUncertainty: years between JanOne2015_Date and January2015_Date
define DurationBetween_Date_MonthsBetweenUncertainty: months between JanOne2015_Date and January2015_Date
define DurationBetween_Date_WeeksBetweenUncertainty: weeks between JanOne2015_Date and January2015_Date
define DurationBetween_Date_DaysBetweenUncertainty: days between JanOne2015_Date and January2015_Date

define test_DurationBetween_Date_YearsBetween: TestMessage(DurationBetween_Date_YearsBetween = 1, 'DurationBetween_Date_YearsBetween', toString(1), toString(DurationBetween_Date_YearsBetween))
define test_DurationBetween_Date_MonthsBetween: TestMessage(DurationBetween_Date_MonthsBetween = 12, 'DurationBetween_Date_MonthsBetween', toString(12), toString(DurationBetween_Date_MonthsBetween))
define test_DurationBetween_Date_WeeksBetween: TestMessage(DurationBetween_Date_WeeksBetween = 55, 'DurationBetween_Date_WeeksBetween', toString(55), toString(DurationBetween_Date_WeeksBetween))
define test_DurationBetween_Date_DaysBetween: TestMessage(DurationBetween_Date_DaysBetween = 387, 'DurationBetween_Date_DaysBetween', toString(387), toString(DurationBetween_Date_DaysBetween))
define test_DurationBetween_Date_YearsBetweenUncertainty: TestMessage(DurationBetween_Date_YearsBetweenUncertainty = 0, 'DurationBetween_Date_YearsBetweenUncertainty', toString(0), toString(DurationBetween_Date_YearsBetweenUncertainty))
define test_DurationBetween_Date_MonthsBetweenUncertainty: TestMessage(DurationBetween_Date_MonthsBetweenUncertainty = 0, 'DurationBetween_Date_MonthsBetweenUncertainty', toString(0), toString(DurationBetween_Date_MonthsBetweenUncertainty))
define test_DurationBetween_Date_WeeksBetweenUncertainty: TestMessage(DurationBetween_Date_WeeksBetweenUncertainty > -1 and (DurationBetween_Date_WeeksBetweenUncertainty > 0) is null and DurationBetween_Date_WeeksBetweenUncertainty < 5 and (DurationBetween_Date_WeeksBetweenUncertainty < 4) is null, 'DurationBetween_Date_WeeksBetweenUncertainty', toString(true), toString(DurationBetween_Date_WeeksBetweenUncertainty > -1 and (DurationBetween_Date_WeeksBetweenUncertainty > 0) is null and DurationBetween_Date_WeeksBetweenUncertainty < 5 and (DurationBetween_Date_WeeksBetweenUncertainty < 4) is null))
define test_DurationBetween_Date_DaysBetweenUncertainty: TestMessage(DurationBetween_Date_DaysBetweenUncertainty > -1 and (DurationBetween_Date_DaysBetweenUncertainty > 0) is null and DurationBetween_Date_DaysBetweenUncertainty < 31 and (DurationBetween_Date_DaysBetweenUncertainty < 30) is null, 'DurationBetween_Date_DaysBetweenUncertainty', toString(true), toString(DurationBetween_Date_DaysBetweenUncertainty > -1 and (DurationBetween_Date_DaysBetweenUncertainty > 0) is null and DurationBetween_Date_DaysBetweenUncertainty < 31 and (DurationBetween_Date_DaysBetweenUncertainty < 30) is null))

// DateMath
define DateMath_Date_PlusThreeYears: June15th2013_Date + 3 years
define DateMath_Date_MinusThreeYears: June15th2013_Date - 3 years
define DateMath_Date_PlusEightMonths: June15th2013_Date + 8 months
define DateMath_Date_MinusEightMonths: June15th2013_Date - 8 months
define DateMath_Date_PlusThreeWeeks: June15th2013_Date + 3 weeks
define DateMath_Date_MinusThreeWeeks: June15th2013_Date - 3 weeks
define DateMath_Date_PlusTwentyDays: June15th2013_Date + 20 days
define DateMath_Date_MinusTwentyDays: June15th2013_Date - 20 days

define test_DateMath_Date_PlusThreeYears: TestMessage(DateMath_Date_PlusThreeYears = Date(2016, 6, 15), 'DateMath_Date_PlusThreeYears', toString(Date(2016, 6, 15)), toString(DateMath_Date_PlusThreeYears))
define test_DateMath_Date_MinusThreeYears: TestMessage(DateMath_Date_MinusThreeYears = Date(2010, 6, 15), 'DateMath_Date_MinusThreeYears', toString(Date(2010, 6, 15)), toString(DateMath_Date_MinusThreeYears))
define test_DateMath_Date_PlusEightMonths: TestMessage(DateMath_Date_PlusEightMonths = Date(2014, 2, 15), 'DateMath_Date_PlusEightMonths', toString(Date(2014, 2, 15)), toString(DateMath_Date_PlusEightMonths))
define test_DateMath_Date_MinusEightMonths: TestMessage(DateMath_Date_MinusEightMonths = Date(2012, 10, 15), 'DateMath_Date_MinusEightMonths', toString(Date(2012, 10, 15)), toString(DateMath_Date_MinusEightMonths))
define test_DateMath_Date_PlusThreeWeeks: TestMessage(DateMath_Date_PlusThreeWeeks = Date(2013, 7, 6), 'DateMath_Date_PlusThreeWeeks', toString(Date(2013, 7, 6)), toString(DateMath_Date_PlusThreeWeeks))
define test_DateMath_Date_MinusThreeWeeks: TestMessage(DateMath_Date_MinusThreeWeeks = Date(2013, 5, 25), 'DateMath_Date_MinusThreeWeeks', toString(Date(2013, 5, 25)), toString(DateMath_Date_MinusThreeWeeks))
define test_DateMath_Date_PlusTwentyDays: TestMessage(DateMath_Date_PlusTwentyDays = Date(2013, 7, 5), 'DateMath_Date_PlusTwentyDays', toString(Date(2013, 7, 5)), toString(DateMath_Date_PlusTwentyDays))
define test_DateMath_Date_MinusTwentyDays: TestMessage(DateMath_Date_MinusTwentyDays = Date(2013, 5, 26), 'DateMath_Date_MinusTwentyDays', toString(Date(2013, 5, 26)), toString(DateMath_Date_MinusTwentyDays))

/*
************************
  End Date Tests
************************
*/

/*
************************
  Start DateTime Tests
************************
*/

// Globals
define IdesOfMarch: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0)
define ImpreciseIdesOfMarch: DateTime(2000, 3, 15)
define NewYear2013: DateTime(2013, 1, 1, 0, 0, 0, 0)
define NewYear2014: DateTime(2014, 1, 1, 0, 0, 0, 0)
define January2014: DateTime(2014, 1)
define February2014: DateTime(2014, 2)
define DecTen2013: DateTime(2013, 12, 10, 12, 30, 30, 500)
define JanOne2015: DateTime(2015, 1, 1, 0, 0, 0, 0)
define January2015: DateTime(2015, 1)
define June15th2013: DateTime(2013, 6, 15, 0, 0, 0, 0)

// Today
define Today_Var: Today()

// NOTE: this test uses the evaluation date/time specified in the Context
define test_Today_Var: TestMessage(Today_Var = @2018-01-01, 'Today_Var', toString(@2018-01-01), toString(Today_Var))

// Now
define Now_Var: Now()

// NOTE: this test uses the evaluation date/time specified in the Context
define test_Now_Var: TestMessage(Now_Var = @2018-01-01T07:00:00.000, 'Now_Var', toString(@2018-01-01T07:00:00.000), toString(Now_Var))

// TimeOfDay
define TimeOfDay_Var: TimeOfDay()

// NOTE: this test uses the evaluation date/time specified in the Context
define test_TimeOfDay_Var: TestMessage(TimeOfDay_Var = @T07:00:00.000, 'TimeOfDay_Var', toString(@T07:00:00.000), toString(TimeOfDay_Var))

// DateTimeComponentFrom
define DTCFrom_Year: year from IdesOfMarch
define DTCFrom_Month: month from IdesOfMarch
define DTCFrom_Day: day from IdesOfMarch
define DTCFrom_Hour: hour from IdesOfMarch
define DTCFrom_Minute: minute from IdesOfMarch
define DTCFrom_Second: second from IdesOfMarch
define DTCFrom_Millisecond: millisecond from IdesOfMarch
define ImpreciseComponentTuple: Tuple {
  Year: year from ImpreciseIdesOfMarch,
  Month: month from ImpreciseIdesOfMarch,
  Day: day from ImpreciseIdesOfMarch,
  Hour: hour from ImpreciseIdesOfMarch,
  Minute: minute from ImpreciseIdesOfMarch,
  Second: second from ImpreciseIdesOfMarch,
  Millisecond: millisecond from ImpreciseIdesOfMarch
}
define DTCFrom_ImpreciseComponentTuple_Hour: ImpreciseComponentTuple.Hour
define DTCFrom_ImpreciseComponentTuple_Minute: ImpreciseComponentTuple.Minute
define DTCFrom_ImpreciseComponentTuple_Second: ImpreciseComponentTuple.Second
define DTCFrom_ImpreciseComponentTuple_Millisecond: ImpreciseComponentTuple.Millisecond
define DTCFrom_NullDate: year from (null as DateTime)

define test_DTCFrom_Year: TestMessage(DTCFrom_Year = 2000, 'DTCFrom_Year', toString(2000), toString(DTCFrom_Year))
define test_DTCFrom_Month: TestMessage(DTCFrom_Month = 3, 'DTCFrom_Month', toString(3), toString(DTCFrom_Month))
define test_DTCFrom_Day: TestMessage(DTCFrom_Day = 15, 'DTCFrom_Day', toString(15), toString(DTCFrom_Day))
define test_DTCFrom_Hour: TestMessage(DTCFrom_Hour = 13, 'DTCFrom_Hour', toString(13), toString(DTCFrom_Hour))
define test_DTCFrom_Minute: TestMessage(DTCFrom_Minute = 30, 'DTCFrom_Minute', toString(30), toString(DTCFrom_Minute))
define test_DTCFrom_Second: TestMessage(DTCFrom_Second = 25, 'DTCFrom_Second', toString(25), toString(DTCFrom_Second))
define test_DTCFrom_Millisecond: TestMessage(DTCFrom_Millisecond = 200, 'DTCFrom_Millisecond', toString(200), toString(DTCFrom_Millisecond))
define test_DTCFrom_ImpreciseComponentTuple_Hour: TestMessage(DTCFrom_ImpreciseComponentTuple_Hour is null, 'DTCFrom_ImpreciseComponentTuple_Hour', 'null', toString(DTCFrom_ImpreciseComponentTuple_Hour))
define test_DTCFrom_ImpreciseComponentTuple_Minute: TestMessage(DTCFrom_ImpreciseComponentTuple_Minute is null, 'DTCFrom_ImpreciseComponentTuple_Minute', 'null', toString(DTCFrom_ImpreciseComponentTuple_Minute))
define test_DTCFrom_ImpreciseComponentTuple_Second: TestMessage(DTCFrom_ImpreciseComponentTuple_Second is null, 'DTCFrom_ImpreciseComponentTuple_Second', 'null', toString(DTCFrom_ImpreciseComponentTuple_Second))
define test_DTCFrom_ImpreciseComponentTuple_Millisecond: TestMessage(DTCFrom_ImpreciseComponentTuple_Millisecond is null, 'DTCFrom_ImpreciseComponentTuple_Millisecond', 'null', toString(DTCFrom_ImpreciseComponentTuple_Millisecond))
define test_DTCFrom_NullDate: TestMessage(DTCFrom_NullDate is null, 'DTCFrom_NullDate', 'null', toString(DTCFrom_NullDate))

// DateFrom
define DateFrom_Date: date from IdesOfMarch
define DateFrom_ImpreciseDate: date from DateTime(2000)
define DateFrom_NullDate: date from (null as DateTime)

define test_DateFrom_Date: TestMessage(DateFrom_Date same day as Date(2000, 3, 15), 'DateFrom_Date', toString(Date(2000, 3, 15)), toString(DateFrom_Date))
define test_DateFrom_ImpreciseDate: TestMessage(DateFrom_ImpreciseDate = @2000, 'DateFrom_ImpreciseDate', toString(@2000), toString(DateFrom_ImpreciseDate))
define test_DateFrom_NullDate: TestMessage(DateFrom_NullDate is null, 'DateFrom_NullDate', 'null', toString(DateFrom_NullDate))

// TimeFrom
define TimeFrom_Time: time from IdesOfMarch
define TimeFrom_NoTime: time from DateTime(2000, 3, 15)
define TimeFrom_NullDate: time from null

define test_TimeFrom_Time: TestMessage(TimeFrom_Time = Time(13, 30, 25, 200), 'TimeFrom_Time', toString(Time(13, 30, 25, 200)), toString(TimeFrom_Time))
define test_TimeFrom_NoTime: TestMessage(TimeFrom_NoTime is null, 'TimeFrom_NoTime', 'null', toString(TimeFrom_NoTime))
define test_TimeFrom_NullDate: TestMessage(TimeFrom_NullDate is null, 'TimeFrom_NullDate', 'null', toString(TimeFrom_NullDate))

// TimezoneFrom
define TimezoneFrom_CentralEuropean: timezoneoffset from IdesOfMarch
define TimezoneFrom_EasternStandard: timezoneoffset from DateTime(2000, 3, 15, 13, 30, 25, 200, -5.0)
define TimezoneFrom_NullDate: timezoneoffset from (null as DateTime)

define test_TimezoneFrom_CentralEuropean: TestMessage(TimezoneFrom_CentralEuropean = 1.0, 'TimezoneFrom_CentralEuropean', toString(1.0), toString(TimezoneFrom_CentralEuropean))
define test_TimezoneFrom_EasternStandard: TestMessage(TimezoneFrom_EasternStandard = -5.0, 'TimezoneFrom_EasternStandard', toString(-5.0), toString(TimezoneFrom_EasternStandard))
define test_TimezoneFrom_NullDate: TestMessage(TimezoneFrom_NullDate is null, 'TimezoneFrom_NullDate', 'null', toString(TimezoneFrom_NullDate))

// SameAs
define SameAs_SameYear: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) same year as DateTime(2000, 11, 23, 8, 14, 47, 500, +1.0)
define SameAs_NotSameYear: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) same year as DateTime(2001, 11, 23, 8, 14, 47, 500, +1.0)
define SameAs_SameMonth: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) same month as DateTime(2000, 3, 23, 8, 14, 47, 500, +1.0)
define SameAs_NotSameMonth: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) same month as DateTime(2000, 4, 23, 8, 14, 47, 500, +1.0)
define SameAs_SameMonthWrongYear: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) same month as DateTime(2001, 3, 23, 8, 14, 47, 500, +1.0)
define SameAs_SameDay: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) same day as DateTime(2000, 3, 15, 8, 14, 47, 500, +1.0)
define SameAs_NotSameDay: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) same day as DateTime(2000, 3, 23, 8, 14, 47, 500, +1.0)
define SameAs_SameDayWrongMonth: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) same day as DateTime(2000, 4, 15, 8, 14, 47, 500, +1.0)
define SameAs_SameHour: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) same hour as DateTime(2000, 3, 15, 13, 14, 47, 500, +1.0)
define SameAs_NotSameHour: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) same hour as DateTime(2000, 3, 15, 8, 14, 47, 500, +1.0)
define SameAs_SameHourWrongDay: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) same hour as DateTime(2000, 3, 16, 13, 14, 47, 500, +1.0)
define SameAs_SameMinute: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) same minute as DateTime(2000, 3, 15, 13, 30, 47, 500, +1.0)
define SameAs_NotSameMinute: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) same minute as DateTime(2000, 3, 15, 13, 14, 47, 500, +1.0)
define SameAs_SameMinuteWrongHour: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) same minute as DateTime(2000, 3, 15, 14, 30, 47, 500, +1.0)
define SameAs_SameSecond: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) same second as DateTime(2000, 3, 15, 13, 30, 25, 500, +1.0)
define SameAs_NotSameSecond: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) same second as DateTime(2000, 3, 15, 13, 30, 47, 500, +1.0)
define SameAs_SameSecondWrongMinute: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) same second as DateTime(2000, 3, 15, 13, 31, 25, 500, +1.0)
define SameAs_SameMillisecond: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) same millisecond as DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0)
define SameAs_NotSameMillisecond: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) same millisecond as DateTime(2000, 3, 15, 13, 30, 25, 500, +1.0)
define SameAs_SameMillisecondWrongSecond: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) same millisecond as DateTime(2000, 3, 15, 13, 30, 26, 200, +1.0)
define SameAs_Same: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) same as DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0)
define SameAs_NotSame: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) same as DateTime(2000, 3, 15, 13, 30, 25, 500, +1.0)
define SameAs_SameNormalized: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) same as DateTime(2000, 3, 15, 7, 30, 25, 200, -5.0)
define SameAs_SameHourWrongTimezone: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) same hour as DateTime(2000, 3, 15, 13, 30, 25, 200, -5.0)
define SameAs_ImpreciseHour: DateTime(2000, 3, 15, 13, 30, 25, 200) same hour as DateTime(2000, 3, 15)
define SameAs_ImpreciseHourWrongDay: DateTime(2000, 3, 15, 13, 30, 25, 200) same hour as DateTime(2000, 3, 16)
define SameAs_NullLeft: null same as DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0)
define SameAs_NullRight: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) same as (null as DateTime)
define SameAs_NullBoth: (null as DateTime) same as null

define test_SameAs_SameYear: TestMessage(SameAs_SameYear, 'SameAs_SameYear', toString(true), toString(SameAs_SameYear))
define test_SameAs_NotSameYear: TestMessage(not SameAs_NotSameYear, 'SameAs_NotSameYear', toString(false), toString(SameAs_NotSameYear))
define test_SameAs_SameMonth: TestMessage(SameAs_SameMonth, 'SameAs_SameMonth', toString(true), toString(SameAs_SameMonth))
define test_SameAs_NotSameMonth: TestMessage(not SameAs_NotSameMonth, 'SameAs_NotSameMonth', toString(false), toString(SameAs_NotSameMonth))
define test_SameAs_SameMonthWrongYear: TestMessage(not SameAs_SameMonthWrongYear, 'SameAs_SameMonthWrongYear', toString(false), toString(SameAs_SameMonthWrongYear))
define test_SameAs_SameDay: TestMessage(SameAs_SameDay, 'SameAs_SameDay', toString(true), toString(SameAs_SameDay))
define test_SameAs_NotSameDay: TestMessage(not SameAs_NotSameDay, 'SameAs_NotSameDay', toString(false), toString(SameAs_NotSameDay))
define test_SameAs_SameDayWrongMonth: TestMessage(not SameAs_SameDayWrongMonth, 'SameAs_SameDayWrongMonth', toString(false), toString(SameAs_SameDayWrongMonth))
define test_SameAs_SameHour: TestMessage(SameAs_SameHour, 'SameAs_SameHour', toString(true), toString(SameAs_SameHour))
define test_SameAs_NotSameHour: TestMessage(not SameAs_NotSameHour, 'SameAs_NotSameHour', toString(false), toString(SameAs_NotSameHour))
define test_SameAs_SameHourWrongDay: TestMessage(not SameAs_SameHourWrongDay, 'SameAs_SameHourWrongDay', toString(false), toString(SameAs_SameHourWrongDay))
define test_SameAs_SameMinute: TestMessage(SameAs_SameMinute, 'SameAs_SameMinute', toString(true), toString(SameAs_SameMinute))
define test_SameAs_NotSameMinute: TestMessage(not SameAs_NotSameMinute, 'SameAs_NotSameMinute', toString(false), toString(SameAs_NotSameMinute))
define test_SameAs_SameMinuteWrongHour: TestMessage(not SameAs_SameMinuteWrongHour, 'SameAs_SameMinuteWrongHour', toString(false), toString(SameAs_SameMinuteWrongHour))
define test_SameAs_SameSecond: TestMessage(SameAs_SameSecond, 'SameAs_SameSecond', toString(true), toString(SameAs_SameSecond))
define test_SameAs_NotSameSecond: TestMessage(not SameAs_NotSameSecond, 'SameAs_NotSameSecond', toString(false), toString(SameAs_NotSameSecond))
define test_SameAs_SameSecondWrongMinute: TestMessage(not SameAs_SameSecondWrongMinute, 'SameAs_SameSecondWrongMinute', toString(false), toString(SameAs_SameSecondWrongMinute))
define test_SameAs_SameMillisecond: TestMessage(SameAs_SameMillisecond, 'SameAs_SameMillisecond', toString(true), toString(SameAs_SameMillisecond))
define test_SameAs_NotSameMillisecond: TestMessage(not SameAs_NotSameMillisecond, 'SameAs_NotSameMillisecond', toString(false), toString(SameAs_NotSameMillisecond))
define test_SameAs_SameMillisecondWrongSecond: TestMessage(not SameAs_SameMillisecondWrongSecond, 'SameAs_SameMillisecondWrongSecond', toString(false), toString(SameAs_SameMillisecondWrongSecond))
define test_SameAs_Same: TestMessage(SameAs_Same, 'SameAs_Same', toString(true), toString(SameAs_Same))
define test_SameAs_NotSame: TestMessage(not SameAs_NotSame, 'SameAs_NotSame', toString(false), toString(SameAs_NotSame))
define test_SameAs_SameNormalized: TestMessage(SameAs_SameNormalized, 'SameAs_SameNormalized', toString(true), toString(SameAs_SameNormalized))
define test_SameAs_SameHourWrongTimezone: TestMessage(not SameAs_SameHourWrongTimezone, 'SameAs_SameHourWrongTimezone', toString(false), toString(SameAs_SameHourWrongTimezone))
define test_SameAs_ImpreciseHour: TestMessage(SameAs_ImpreciseHour is null, 'SameAs_ImpreciseHour', 'null', toString(SameAs_ImpreciseHour))
define test_SameAs_ImpreciseHourWrongDay: TestMessage(not SameAs_ImpreciseHourWrongDay, 'SameAs_ImpreciseHourWrongDay', toString(false), toString(SameAs_ImpreciseHourWrongDay))
define test_SameAs_NullLeft: TestMessage(SameAs_NullLeft is null, 'SameAs_NullLeft', 'null', toString(SameAs_NullLeft))
define test_SameAs_NullRight: TestMessage(SameAs_NullRight is null, 'SameAs_NullRight', 'null', toString(SameAs_NullRight))
define test_SameAs_NullBoth: TestMessage(SameAs_NullBoth is null, 'SameAs_NullBoth', 'null', toString(SameAs_NullBoth))

// SameOrAfter
define SameOrAfter_SameYear: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) same year or after DateTime(2000, 11, 23, 8, 14, 47, 500, +1.0)
define SameOrAfter_YearAfter: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) same year or after DateTime(1999, 11, 23, 8, 14, 47, 500, +1.0)
define SameOrAfter_YearBefore: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) same year or after DateTime(2001, 11, 23, 8, 14, 47, 500, +1.0)
define SameOrAfter_SameMonth: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) same month or after DateTime(2000, 3, 23, 8, 14, 47, 500, +1.0)
define SameOrAfter_MonthAfter: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) same month or after DateTime(2000, 2, 23, 8, 14, 47, 500, +1.0)
define SameOrAfter_MonthBefore: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) same month or after DateTime(2000, 4, 23, 8, 14, 47, 500, +1.0)
define SameOrAfter_SameDay: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) same day or after DateTime(2000, 3, 15, 8, 14, 47, 500, +1.0)
define SameOrAfter_DayAfter: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) same day or after DateTime(2000, 3, 14, 8, 14, 47, 500, +1.0)
define SameOrAfter_DayBefore: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) same day or after DateTime(2000, 3, 16, 8, 14, 47, 500, +1.0)
define SameOrAfter_SameHour: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) same hour or after DateTime(2000, 3, 15, 13, 14, 47, 500, +1.0)
define SameOrAfter_HourAfter: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) same hour or after DateTime(2000, 3, 15, 12, 14, 47, 500, +1.0)
define SameOrAfter_HourBefore: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) same hour or after DateTime(2000, 3, 15, 14, 14, 47, 500, +1.0)
define SameOrAfter_SameMinute: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) same minute or after DateTime(2000, 3, 15, 13, 30, 47, 500, +1.0)
define SameOrAfter_MinuteAfter: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) same minute or after DateTime(2000, 3, 15, 13, 29, 47, 500, +1.0)
define SameOrAfter_MinuteBefore: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) same minute or after DateTime(2000, 3, 15, 13, 31, 47, 500, +1.0)
define SameOrAfter_SameSecond: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) same second or after DateTime(2000, 3, 15, 13, 30, 25, 500, +1.0)
define SameOrAfter_SecondAfter: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) same second or after DateTime(2000, 3, 15, 13, 30, 24, 500, +1.0)
define SameOrAfter_SecondBefore: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) same second or after DateTime(2000, 3, 15, 13, 30, 26, 500, +1.0)
define SameOrAfter_SameMillisecond: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) same millisecond or after DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0)
define SameOrAfter_MillisecondAfter: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) same millisecond or after DateTime(2000, 3, 15, 13, 30, 25, 199, +1.0)
define SameOrAfter_MillisecondBefore: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) same millisecond or after DateTime(2000, 3, 15, 13, 30, 25, 201, +1.0)
define SameOrAfter_Same: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) same or after DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0)
define SameOrAfter_After: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) same or after DateTime(2000, 3, 15, 13, 30, 25, 199, +1.0)
define SameOrAfter_Before: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) same or after DateTime(2000, 3, 15, 13, 30, 25, 201, +1.0)
define SameOrAfter_SameDayMonthBefore: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) same day or after DateTime(2000, 4, 15, 8, 14, 47, 500, +1.0)
define SameOrAfter_DayAfterMonthBefore: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) same day or after DateTime(2000, 4, 14, 8, 14, 47, 500, +1.0)
define SameOrAfter_DayBeforeMonthAfter: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) same day or after DateTime(2000, 2, 16, 8, 14, 47, 500, +1.0)
define SameOrAfter_ImpreciseDay: DateTime(2000, 3, 15, 13, 30, 25, 200) same day or after DateTime(2000, 3)
define SameOrAfter_ImpreciseDayMonthAfter: DateTime(2000, 3, 15, 13, 30, 25, 200) same day or after DateTime(2000, 2)
define SameOrAfter_ImpreciseDayMonthBefore: DateTime(2000, 3, 15, 13, 30, 25, 200) same day or after DateTime(2000, 4)
define SameOrAfter_SameHourNormalizeZones: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) same hour or after DateTime(2000, 3, 15, 7, 30, 25, 200, -5.0)
define SameOrAfter_HourAfterNormalizeZones: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) same hour or after DateTime(2000, 3, 15, 6, 30, 25, 200, -5.0)
define SameOrAfter_HourBeforeNormalizeZones: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) same hour or after DateTime(2000, 3, 15, 8, 30, 25, 200, -5.0)
define SameOrAfter_NullLeft: null same or after DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0)
define SameOrAfter_NullRight: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) same or after null
define SameOrAfter_NullBoth: (null as DateTime) same or after null
// on or after:
define OnOrAfter_SameOOA: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) on or after DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0)
define OnOrAfter_AfterOOA: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) on or after DateTime(2000, 3, 15, 13, 30, 25, 199, +1.0)
define OnOrAfter_BeforeOOA: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) on or after DateTime(2000, 3, 15, 13, 30, 25, 201, +1.0)
define OnOrAfter_NullLeftOOA: null on or after DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0)
define OnOrAfter_NullRightOOA: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) on or after null
define OnOrAfter_NullBothOOA: (null as DateTime) on or after null
// after or on:
define AfterOrOn_SameAOO: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) after or on DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0)
define AfterOrOn_AfterAOO: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) after or on DateTime(2000, 3, 15, 13, 30, 25, 199, +1.0)
define AfterOrOn_BeforeAOO: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) after or on DateTime(2000, 3, 15, 13, 30, 25, 201, +1.0)
define AfterOrOn_NullLeftAOO: null after or on DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0)
define AfterOrOn_NullRightAOO: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) after or on null
define AfterOrOn_NullBothAOO: (null as DateTime) after or on null

define test_SameOrAfter_SameYear: TestMessage(SameOrAfter_SameYear, 'SameOrAfter_SameYear', toString(true), toString(SameOrAfter_SameYear))
define test_SameOrAfter_YearAfter: TestMessage(SameOrAfter_YearAfter, 'SameOrAfter_YearAfter', toString(true), toString(SameOrAfter_YearAfter))
define test_SameOrAfter_YearBefore: TestMessage(not SameOrAfter_YearBefore, 'SameOrAfter_YearBefore', toString(false), toString(SameOrAfter_YearBefore))
define test_SameOrAfter_SameMonth: TestMessage(SameOrAfter_SameMonth, 'SameOrAfter_SameMonth', toString(true), toString(SameOrAfter_SameMonth))
define test_SameOrAfter_MonthAfter: TestMessage(SameOrAfter_MonthAfter, 'SameOrAfter_MonthAfter', toString(true), toString(SameOrAfter_MonthAfter))
define test_SameOrAfter_MonthBefore: TestMessage(not SameOrAfter_MonthBefore, 'SameOrAfter_MonthBefore', toString(false), toString(SameOrAfter_MonthBefore))
define test_SameOrAfter_SameDay: TestMessage(SameOrAfter_SameDay, 'SameOrAfter_SameDay', toString(true), toString(SameOrAfter_SameDay))
define test_SameOrAfter_DayAfter: TestMessage(SameOrAfter_DayAfter, 'SameOrAfter_DayAfter', toString(true), toString(SameOrAfter_DayAfter))
define test_SameOrAfter_DayBefore: TestMessage(not SameOrAfter_DayBefore, 'SameOrAfter_DayBefore', toString(false), toString(SameOrAfter_DayBefore))
define test_SameOrAfter_SameHour: TestMessage(SameOrAfter_SameHour, 'SameOrAfter_SameHour', toString(true), toString(SameOrAfter_SameHour))
define test_SameOrAfter_HourAfter: TestMessage(SameOrAfter_HourAfter, 'SameOrAfter_HourAfter', toString(true), toString(SameOrAfter_HourAfter))
define test_SameOrAfter_HourBefore: TestMessage(not SameOrAfter_HourBefore, 'SameOrAfter_HourBefore', toString(false), toString(SameOrAfter_HourBefore))
define test_SameOrAfter_SameMinute: TestMessage(SameOrAfter_SameMinute, 'SameOrAfter_SameMinute', toString(true), toString(SameOrAfter_SameMinute))
define test_SameOrAfter_MinuteAfter: TestMessage(SameOrAfter_MinuteAfter, 'SameOrAfter_MinuteAfter', toString(true), toString(SameOrAfter_MinuteAfter))
define test_SameOrAfter_MinuteBefore: TestMessage(not SameOrAfter_MinuteBefore, 'SameOrAfter_MinuteBefore', toString(false), toString(SameOrAfter_MinuteBefore))
define test_SameOrAfter_SameSecond: TestMessage(SameOrAfter_SameSecond, 'SameOrAfter_SameSecond', toString(true), toString(SameOrAfter_SameSecond))
define test_SameOrAfter_SecondAfter: TestMessage(SameOrAfter_SecondAfter, 'SameOrAfter_SecondAfter', toString(true), toString(SameOrAfter_SecondAfter))
define test_SameOrAfter_SecondBefore: TestMessage(not SameOrAfter_SecondBefore, 'SameOrAfter_SecondBefore', toString(false), toString(SameOrAfter_SecondBefore))
define test_SameOrAfter_SameMillisecond: TestMessage(SameOrAfter_SameMillisecond, 'SameOrAfter_SameMillisecond', toString(true), toString(SameOrAfter_SameMillisecond))
define test_SameOrAfter_MillisecondAfter: TestMessage(SameOrAfter_MillisecondAfter, 'SameOrAfter_MillisecondAfter', toString(true), toString(SameOrAfter_MillisecondAfter))
define test_SameOrAfter_MillisecondBefore: TestMessage(not SameOrAfter_MillisecondBefore, 'SameOrAfter_MillisecondBefore', toString(false), toString(SameOrAfter_MillisecondBefore))
define test_SameOrAfter_Same: TestMessage(SameOrAfter_Same, 'SameOrAfter_Same', toString(true), toString(SameOrAfter_Same))
define test_SameOrAfter_After: TestMessage(SameOrAfter_After, 'SameOrAfter_After', toString(true), toString(SameOrAfter_After))
define test_SameOrAfter_Before: TestMessage(not SameOrAfter_Before, 'SameOrAfter_Before', toString(false), toString(SameOrAfter_Before))
define test_SameOrAfter_SameDayMonthBefore: TestMessage(not SameOrAfter_SameDayMonthBefore, 'SameOrAfter_SameDayMonthBefore', toString(false), toString(SameOrAfter_SameDayMonthBefore))
define test_SameOrAfter_DayAfterMonthBefore: TestMessage(not SameOrAfter_DayAfterMonthBefore, 'SameOrAfter_DayAfterMonthBefore', toString(false), toString(SameOrAfter_DayAfterMonthBefore))
define test_SameOrAfter_DayBeforeMonthAfter: TestMessage(SameOrAfter_DayBeforeMonthAfter, 'SameOrAfter_DayBeforeMonthAfter', toString(true), toString(SameOrAfter_DayBeforeMonthAfter))
define test_SameOrAfter_ImpreciseDay: TestMessage(SameOrAfter_ImpreciseDay is null, 'SameOrAfter_ImpreciseDay', 'null', toString(SameOrAfter_ImpreciseDay))
define test_SameOrAfter_ImpreciseDayMonthAfter: TestMessage(SameOrAfter_ImpreciseDayMonthAfter, 'SameOrAfter_ImpreciseDayMonthAfter', toString(true), toString(SameOrAfter_ImpreciseDayMonthAfter))
define test_SameOrAfter_ImpreciseDayMonthBefore: TestMessage(not SameOrAfter_ImpreciseDayMonthBefore, 'SameOrAfter_ImpreciseDayMonthBefore', toString(false), toString(SameOrAfter_ImpreciseDayMonthBefore))
define test_SameOrAfter_SameHourNormalizeZones: TestMessage(SameOrAfter_SameHourNormalizeZones, 'SameOrAfter_SameHourNormalizeZones', toString(true), toString(SameOrAfter_SameHourNormalizeZones))
define test_SameOrAfter_HourAfterNormalizeZones: TestMessage(SameOrAfter_HourAfterNormalizeZones, 'SameOrAfter_HourAfterNormalizeZones', toString(true), toString(SameOrAfter_HourAfterNormalizeZones))
define test_SameOrAfter_HourBeforeNormalizeZones: TestMessage(not SameOrAfter_HourBeforeNormalizeZones, 'SameOrAfter_HourBeforeNormalizeZones', toString(false), toString(SameOrAfter_HourBeforeNormalizeZones))
define test_SameOrAfter_NullLeft: TestMessage(SameOrAfter_NullLeft is null, 'SameOrAfter_NullLeft', 'null', toString(SameOrAfter_NullLeft))
define test_SameOrAfter_NullRight: TestMessage(SameOrAfter_NullRight is null, 'SameOrAfter_NullRight', 'null', toString(SameOrAfter_NullRight))
define test_SameOrAfter_NullBoth: TestMessage(SameOrAfter_NullBoth is null, 'SameOrAfter_NullBoth', 'null', toString(SameOrAfter_NullBoth))
define test_OnOrAfter_SameOOA: TestMessage(OnOrAfter_SameOOA, 'OnOrAfter_SameOOA', toString(true), toString(OnOrAfter_SameOOA))
define test_OnOrAfter_AfterOOA: TestMessage(OnOrAfter_AfterOOA, 'OnOrAfter_AfterOOA', toString(true), toString(OnOrAfter_AfterOOA))
define test_OnOrAfter_BeforeOOA: TestMessage(not OnOrAfter_BeforeOOA, 'OnOrAfter_BeforeOOA', toString(false), toString(OnOrAfter_BeforeOOA))
define test_OnOrAfter_NullLeftOOA: TestMessage(OnOrAfter_NullLeftOOA is null, 'OnOrAfter_NullLeftOOA', 'null', toString(OnOrAfter_NullLeftOOA))
define test_OnOrAfter_NullRightOOA: TestMessage(OnOrAfter_NullRightOOA is null, 'OnOrAfter_NullRightOOA', 'null', toString(OnOrAfter_NullRightOOA))
define test_OnOrAfter_NullBothOOA: TestMessage(OnOrAfter_NullBothOOA is null, 'OnOrAfter_NullBothOOA', 'null', toString(OnOrAfter_NullBothOOA))
define test_AfterOrOn_SameAOO: TestMessage(AfterOrOn_SameAOO, 'AfterOrOn_SameAOO', toString(true), toString(AfterOrOn_SameAOO))
define test_AfterOrOn_AfterAOO: TestMessage(AfterOrOn_AfterAOO, 'AfterOrOn_AfterAOO', toString(true), toString(AfterOrOn_AfterAOO))
define test_AfterOrOn_BeforeAOO: TestMessage(not AfterOrOn_BeforeAOO, 'AfterOrOn_BeforeAOO', toString(false), toString(AfterOrOn_BeforeAOO))
define test_AfterOrOn_NullLeftAOO: TestMessage(AfterOrOn_NullLeftAOO is null, 'AfterOrOn_NullLeftAOO', 'null', toString(AfterOrOn_NullLeftAOO))
define test_AfterOrOn_NullRightAOO: TestMessage(AfterOrOn_NullRightAOO is null, 'AfterOrOn_NullRightAOO', 'null', toString(AfterOrOn_NullRightAOO))
define test_AfterOrOn_NullBothAOO: TestMessage(AfterOrOn_NullBothAOO is null, 'AfterOrOn_NullBothAOO', 'null', toString(AfterOrOn_NullBothAOO))

// SameOrBefore
define SameOrBefore_SameYear: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) same year or before DateTime(2000, 11, 23, 8, 14, 47, 500, +1.0)
define SameOrBefore_YearAfter: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) same year or before DateTime(1999, 11, 23, 8, 14, 47, 500, +1.0)
define SameOrBefore_YearBefore: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) same year or before DateTime(2001, 11, 23, 8, 14, 47, 500, +1.0)
define SameOrBefore_SameMonth: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) same month or before DateTime(2000, 3, 23, 8, 14, 47, 500, +1.0)
define SameOrBefore_MonthAfter: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) same month or before DateTime(2000, 2, 23, 8, 14, 47, 500, +1.0)
define SameOrBefore_MonthBefore: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) same month or before DateTime(2000, 4, 23, 8, 14, 47, 500, +1.0)
define SameOrBefore_SameDay: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) same day or before DateTime(2000, 3, 15, 8, 14, 47, 500, +1.0)
define SameOrBefore_DayAfter: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) same day or before DateTime(2000, 3, 14, 8, 14, 47, 500, +1.0)
define SameOrBefore_DayBefore: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) same day or before DateTime(2000, 3, 16, 8, 14, 47, 500, +1.0)
define SameOrBefore_SameHour: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) same hour or before DateTime(2000, 3, 15, 13, 14, 47, 500, +1.0)
define SameOrBefore_HourAfter: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) same hour or before DateTime(2000, 3, 15, 12, 14, 47, 500, +1.0)
define SameOrBefore_HourBefore: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) same hour or before DateTime(2000, 3, 15, 14, 14, 47, 500, +1.0)
define SameOrBefore_SameMinute: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) same minute or before DateTime(2000, 3, 15, 13, 30, 47, 500, +1.0)
define SameOrBefore_MinuteAfter: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) same minute or before DateTime(2000, 3, 15, 13, 29, 47, 500, +1.0)
define SameOrBefore_MinuteBefore: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) same minute or before DateTime(2000, 3, 15, 13, 31, 47, 500, +1.0)
define SameOrBefore_SameSecond: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) same second or before DateTime(2000, 3, 15, 13, 30, 25, 500, +1.0)
define SameOrBefore_SecondAfter: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) same second or before DateTime(2000, 3, 15, 13, 30, 24, 500, +1.0)
define SameOrBefore_SecondBefore: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) same second or before DateTime(2000, 3, 15, 13, 30, 26, 500, +1.0)
define SameOrBefore_SameMillisecond: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) same millisecond or before DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0)
define SameOrBefore_MillisecondAfter: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) same millisecond or before DateTime(2000, 3, 15, 13, 30, 25, 199, +1.0)
define SameOrBefore_MillisecondBefore: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) same millisecond or before DateTime(2000, 3, 15, 13, 30, 25, 201, +1.0)
define SameOrBefore_Same: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) same or before DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0)
define SameOrBefore_After: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) same or before DateTime(2000, 3, 15, 13, 30, 25, 199, +1.0)
define SameOrBefore_Before: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) same or before DateTime(2000, 3, 15, 13, 30, 25, 201, +1.0)
define SameOrBefore_SameDayMonthBefore: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) same day or before DateTime(2000, 4, 15, 8, 14, 47, 500, +1.0)
define SameOrBefore_DayAfterMonthBefore: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) same day or before DateTime(2000, 4, 14, 8, 14, 47, 500, +1.0)
define SameOrBefore_DayBeforeMonthAfter: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) same day or before DateTime(2000, 2, 16, 8, 14, 47, 500, +1.0)
define SameOrBefore_ImpreciseDay: DateTime(2000, 3, 15, 13, 30, 25, 200) same day or before DateTime(2000, 3)
define SameOrBefore_ImpreciseDayMonthAfter: DateTime(2000, 3, 15, 13, 30, 25, 200) same day or before DateTime(2000, 2)
define SameOrBefore_ImpreciseDayMonthBefore: DateTime(2000, 3, 15, 13, 30, 25, 200) same day or before DateTime(2000, 4)
define SameOrBefore_SameHourNormalizeZones: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) same hour or before DateTime(2000, 3, 15, 7, 30, 25, 200, -5.0)
define SameOrBefore_HourAfterNormalizeZones: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) same hour or before DateTime(2000, 3, 15, 6, 30, 25, 200, -5.0)
define SameOrBefore_HourBeforeNormalizeZones: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) same hour or before DateTime(2000, 3, 15, 8, 30, 25, 200, -5.0)
define SameOrBefore_NullLeft: null same or before DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0)
define SameOrBefore_NullRight: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) same or before null
define SameOrBefore_NullBoth: (null as DateTime) same or before null
// on or before
define OnOrBefore_SameOOB: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) on or before DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0)
define OnOrBefore_AfterOOB: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) on or before DateTime(2000, 3, 15, 13, 30, 25, 199, +1.0)
define OnOrBefore_BeforeOOB: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) on or before DateTime(2000, 3, 15, 13, 30, 25, 201, +1.0)
define OnOrBefore_NullLeftOOB: null on or before DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0)
define OnOrBefore_NullRightOOB: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) on or before null
define OnOrBefore_NullBothOOB: (null as DateTime) on or before null
// before or on
define BeforeOrOn_SameBOO: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) before or on DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0)
define BeforeOrOn_AfterBOO: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) before or on DateTime(2000, 3, 15, 13, 30, 25, 199, +1.0)
define BeforeOrOn_BeforeBOO: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) before or on DateTime(2000, 3, 15, 13, 30, 25, 201, +1.0)
define BeforeOrOn_NullLeftBOO: null before or on DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0)
define BeforeOrOn_NullRightBOO: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) before or on null
define BeforeOrOn_NullBothBOO: (null as DateTime) before or on null

define test_SameOrBefore_SameYear: TestMessage(SameOrBefore_SameYear, 'SameOrBefore_SameYear', toString(true), toString(SameOrBefore_SameYear))
define test_SameOrBefore_YearAfter: TestMessage(not SameOrBefore_YearAfter, 'SameOrBefore_YearAfter', toString(false), toString(SameOrBefore_YearAfter))
define test_SameOrBefore_YearBefore: TestMessage(SameOrBefore_YearBefore, 'SameOrBefore_YearBefore', toString(true), toString(SameOrBefore_YearBefore))
define test_SameOrBefore_SameMonth: TestMessage(SameOrBefore_SameMonth, 'SameOrBefore_SameMonth', toString(true), toString(SameOrBefore_SameMonth))
define test_SameOrBefore_MonthAfter: TestMessage(not SameOrBefore_MonthAfter, 'SameOrBefore_MonthAfter', toString(false), toString(SameOrBefore_MonthAfter))
define test_SameOrBefore_MonthBefore: TestMessage(SameOrBefore_MonthBefore, 'SameOrBefore_MonthBefore', toString(true), toString(SameOrBefore_MonthBefore))
define test_SameOrBefore_SameDay: TestMessage(SameOrBefore_SameDay, 'SameOrBefore_SameDay', toString(true), toString(SameOrBefore_SameDay))
define test_SameOrBefore_DayAfter: TestMessage(not SameOrBefore_DayAfter, 'SameOrBefore_DayAfter', toString(false), toString(SameOrBefore_DayAfter))
define test_SameOrBefore_DayBefore: TestMessage(SameOrBefore_DayBefore, 'SameOrBefore_DayBefore', toString(true), toString(SameOrBefore_DayBefore))
define test_SameOrBefore_SameHour: TestMessage(SameOrBefore_SameHour, 'SameOrBefore_SameHour', toString(true), toString(SameOrBefore_SameHour))
define test_SameOrBefore_HourAfter: TestMessage(not SameOrBefore_HourAfter, 'SameOrBefore_HourAfter', toString(false), toString(SameOrBefore_HourAfter))
define test_SameOrBefore_HourBefore: TestMessage(SameOrBefore_HourBefore, 'SameOrBefore_HourBefore', toString(true), toString(SameOrBefore_HourBefore))
define test_SameOrBefore_SameMinute: TestMessage(SameOrBefore_SameMinute, 'SameOrBefore_SameMinute', toString(true), toString(SameOrBefore_SameMinute))
define test_SameOrBefore_MinuteAfter: TestMessage(not SameOrBefore_MinuteAfter, 'SameOrBefore_MinuteAfter', toString(false), toString(SameOrBefore_MinuteAfter))
define test_SameOrBefore_MinuteBefore: TestMessage(SameOrBefore_MinuteBefore, 'SameOrBefore_MinuteBefore', toString(true), toString(SameOrBefore_MinuteBefore))
define test_SameOrBefore_SameSecond: TestMessage(SameOrBefore_SameSecond, 'SameOrBefore_SameSecond', toString(true), toString(SameOrBefore_SameSecond))
define test_SameOrBefore_SecondAfter: TestMessage(not SameOrBefore_SecondAfter, 'SameOrBefore_SecondAfter', toString(false), toString(SameOrBefore_SecondAfter))
define test_SameOrBefore_SecondBefore: TestMessage(SameOrBefore_SecondBefore, 'SameOrBefore_SecondBefore', toString(true), toString(SameOrBefore_SecondBefore))
define test_SameOrBefore_SameMillisecond: TestMessage(SameOrBefore_SameMillisecond, 'SameOrBefore_SameMillisecond', toString(true), toString(SameOrBefore_SameMillisecond))
define test_SameOrBefore_MillisecondAfter: TestMessage(not SameOrBefore_MillisecondAfter, 'SameOrBefore_MillisecondAfter', toString(false), toString(SameOrBefore_MillisecondAfter))
define test_SameOrBefore_MillisecondBefore: TestMessage(SameOrBefore_MillisecondBefore, 'SameOrBefore_MillisecondBefore', toString(true), toString(SameOrBefore_MillisecondBefore))
define test_SameOrBefore_Same: TestMessage(SameOrBefore_Same, 'SameOrBefore_Same', toString(true), toString(SameOrBefore_Same))
define test_SameOrBefore_After: TestMessage(not SameOrBefore_After, 'SameOrBefore_After', toString(false), toString(SameOrBefore_After))
define test_SameOrBefore_Before: TestMessage(SameOrBefore_Before, 'SameOrBefore_Before', toString(true), toString(SameOrBefore_Before))
define test_SameOrBefore_SameDayMonthBefore: TestMessage(SameOrBefore_SameDayMonthBefore, 'SameOrBefore_SameDayMonthBefore', toString(true), toString(SameOrBefore_SameDayMonthBefore))
define test_SameOrBefore_DayAfterMonthBefore: TestMessage(SameOrBefore_DayAfterMonthBefore, 'SameOrBefore_DayAfterMonthBefore', toString(true), toString(SameOrBefore_DayAfterMonthBefore))
define test_SameOrBefore_DayBeforeMonthAfter: TestMessage(not SameOrBefore_DayBeforeMonthAfter, 'SameOrBefore_DayBeforeMonthAfter', toString(false), toString(SameOrBefore_DayBeforeMonthAfter))
define test_SameOrBefore_ImpreciseDay: TestMessage(SameOrBefore_ImpreciseDay is null, 'SameOrBefore_ImpreciseDay', 'null', toString(SameOrBefore_ImpreciseDay))
define test_SameOrBefore_ImpreciseDayMonthAfter: TestMessage(not SameOrBefore_ImpreciseDayMonthAfter, 'SameOrBefore_ImpreciseDayMonthAfter', toString(false), toString(SameOrBefore_ImpreciseDayMonthAfter))
define test_SameOrBefore_ImpreciseDayMonthBefore: TestMessage(SameOrBefore_ImpreciseDayMonthBefore, 'SameOrBefore_ImpreciseDayMonthBefore', toString(true), toString(SameOrBefore_ImpreciseDayMonthBefore))
define test_SameOrBefore_SameHourNormalizeZones: TestMessage(SameOrBefore_SameHourNormalizeZones, 'SameOrBefore_SameHourNormalizeZones', toString(true), toString(SameOrBefore_SameHourNormalizeZones))
define test_SameOrBefore_HourAfterNormalizeZones: TestMessage(not SameOrBefore_HourAfterNormalizeZones, 'SameOrBefore_HourAfterNormalizeZones', toString(false), toString(SameOrBefore_HourAfterNormalizeZones))
define test_SameOrBefore_HourBeforeNormalizeZones: TestMessage(SameOrBefore_HourBeforeNormalizeZones, 'SameOrBefore_HourBeforeNormalizeZones', toString(true), toString(SameOrBefore_HourBeforeNormalizeZones))
define test_SameOrBefore_NullLeft: TestMessage(SameOrBefore_NullLeft is null, 'SameOrBefore_NullLeft', 'null', toString(SameOrBefore_NullLeft))
define test_SameOrBefore_NullRight: TestMessage(SameOrBefore_NullRight is null, 'SameOrBefore_NullRight', 'null', toString(SameOrBefore_NullRight))
define test_SameOrBefore_NullBoth: TestMessage(SameOrBefore_NullBoth is null, 'SameOrBefore_NullBoth', 'null', toString(SameOrBefore_NullBoth))
define test_OnOrBefore_SameOOB: TestMessage(OnOrBefore_SameOOB, 'OnOrBefore_SameOOB', toString(true), toString(OnOrBefore_SameOOB))
define test_OnOrBefore_AfterOOB: TestMessage(not OnOrBefore_AfterOOB, 'OnOrBefore_AfterOOB', toString(false), toString(OnOrBefore_AfterOOB))
define test_OnOrBefore_BeforeOOB: TestMessage(OnOrBefore_BeforeOOB, 'OnOrBefore_BeforeOOB', toString(true), toString(OnOrBefore_BeforeOOB))
define test_OnOrBefore_NullLeftOOB: TestMessage(OnOrBefore_NullLeftOOB is null, 'OnOrBefore_NullLeftOOB', 'null', toString(OnOrBefore_NullLeftOOB))
define test_OnOrBefore_NullRightOOB: TestMessage(OnOrBefore_NullRightOOB is null, 'OnOrBefore_NullRightOOB', 'null', toString(OnOrBefore_NullRightOOB))
define test_OnOrBefore_NullBothOOB: TestMessage(OnOrBefore_NullBothOOB is null, 'OnOrBefore_NullBothOOB', 'null', toString(OnOrBefore_NullBothOOB))
define test_BeforeOrOn_SameBOO: TestMessage(BeforeOrOn_SameBOO, 'BeforeOrOn_SameBOO', toString(true), toString(BeforeOrOn_SameBOO))
define test_BeforeOrOn_AfterBOO: TestMessage(not BeforeOrOn_AfterBOO, 'BeforeOrOn_AfterBOO', toString(false), toString(BeforeOrOn_AfterBOO))
define test_BeforeOrOn_BeforeBOO: TestMessage(BeforeOrOn_BeforeBOO, 'BeforeOrOn_BeforeBOO', toString(true), toString(BeforeOrOn_BeforeBOO))
define test_BeforeOrOn_NullLeftBOO: TestMessage(BeforeOrOn_NullLeftBOO is null, 'BeforeOrOn_NullLeftBOO', 'null', toString(BeforeOrOn_NullLeftBOO))
define test_BeforeOrOn_NullRightBOO: TestMessage(BeforeOrOn_NullRightBOO is null, 'BeforeOrOn_NullRightBOO', 'null', toString(BeforeOrOn_NullRightBOO))
define test_BeforeOrOn_NullBothBOO: TestMessage(BeforeOrOn_NullBothBOO is null, 'BeforeOrOn_NullBothBOO', 'null', toString(BeforeOrOn_NullBothBOO))

// After
define After_SameYear: DateTime(2000, 12, 15, 13, 30, 25, 200, +1.0) after year of DateTime(2000, 11, 23, 8, 14, 47, 500, +1.0)
define After_YearAfter: DateTime(2000, 12, 15, 13, 30, 25, 200, +1.0) after year of DateTime(1999, 11, 23, 8, 14, 47, 500, +1.0)
define After_YearBefore: DateTime(2000, 12, 15, 13, 30, 25, 200, +1.0) after year of DateTime(2001, 11, 23, 8, 14, 47, 500, +1.0)
define After_SameMonth: DateTime(2000, 3, 25, 13, 30, 25, 200, +1.0) after month of DateTime(2000, 3, 23, 8, 14, 47, 500, +1.0)
define After_MonthAfter: DateTime(2000, 3, 25, 13, 30, 25, 200, +1.0) after month of DateTime(2000, 2, 23, 8, 14, 47, 500, +1.0)
define After_MonthBefore: DateTime(2000, 3, 25, 13, 30, 25, 200, +1.0) after month of DateTime(2000, 4, 23, 8, 14, 47, 500, +1.0)
define After_SameDay: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) after day of DateTime(2000, 3, 15, 8, 14, 47, 500, +1.0)
define After_DayAfter: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) after day of DateTime(2000, 3, 14, 8, 14, 47, 500, +1.0)
define After_DayBefore: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) after day of DateTime(2000, 3, 16, 8, 14, 47, 500, +1.0)
define After_SameHour: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) after hour of DateTime(2000, 3, 15, 13, 14, 47, 500, +1.0)
define After_HourAfter: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) after hour of DateTime(2000, 3, 15, 12, 14, 47, 500, +1.0)
define After_HourBefore: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) after hour of DateTime(2000, 3, 15, 14, 14, 47, 500, +1.0)
define After_SameMinute: DateTime(2000, 3, 15, 13, 30, 55, 200, +1.0) after minute of DateTime(2000, 3, 15, 13, 30, 47, 500, +1.0)
define After_MinuteAfter: DateTime(2000, 3, 15, 13, 30, 55, 200, +1.0) after minute of DateTime(2000, 3, 15, 13, 29, 47, 500, +1.0)
define After_MinuteBefore: DateTime(2000, 3, 15, 13, 30, 55, 200, +1.0) after minute of DateTime(2000, 3, 15, 13, 31, 47, 500, +1.0)
define After_SameSecond: DateTime(2000, 3, 15, 13, 30, 25, 700, +1.0) after second of DateTime(2000, 3, 15, 13, 30, 25, 500, +1.0)
define After_SecondAfter: DateTime(2000, 3, 15, 13, 30, 25, 700, +1.0) after second of DateTime(2000, 3, 15, 13, 30, 24, 500, +1.0)
define After_SecondBefore: DateTime(2000, 3, 15, 13, 30, 25, 700, +1.0) after second of DateTime(2000, 3, 15, 13, 30, 26, 500, +1.0)
define After_SameMillisecond: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) after millisecond of DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0)
define After_MillisecondAfter: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) after millisecond of DateTime(2000, 3, 15, 13, 30, 25, 199, +1.0)
define After_MillisecondBefore: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) after millisecond of DateTime(2000, 3, 15, 13, 30, 25, 201, +1.0)
define After_Same: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) after DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0)
define After_After: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) after DateTime(2000, 3, 15, 13, 30, 25, 199, +1.0)
define After_Before: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) after DateTime(2000, 3, 15, 13, 30, 25, 201, +1.0)
define After_ImpreciseDay: DateTime(2000, 3, 15, 13, 30, 25, 200) after day of DateTime(2000, 3)
define After_ImpreciseDayMonthAfter: DateTime(2000, 3, 15, 13, 30, 25, 200) after day of DateTime(2000, 2)
define After_ImpreciseDayMonthBefore: DateTime(2000, 3, 15, 13, 30, 25, 200) after day of DateTime(2000, 4)
define After_SameHourNormalizeZones: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) after hour of DateTime(2000, 3, 15, 7, 30, 25, 200, -5.0)
define After_HourAfterNormalizeZones: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) after hour of DateTime(2000, 3, 15, 6, 30, 25, 200, -5.0)
define After_HourBeforeNormalizeZones: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) after hour of DateTime(2000, 3, 15, 8, 30, 25, 200, -5.0)
define After_NullLeft: null after DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0)
define After_NullRight: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) after null
define After_NullBoth: (null as DateTime) after null

define test_After_SameYear: TestMessage(not After_SameYear, 'After_SameYear', toString(false), toString(After_SameYear))
define test_After_YearAfter: TestMessage(After_YearAfter, 'After_YearAfter', toString(true), toString(After_YearAfter))
define test_After_YearBefore: TestMessage(not After_YearBefore, 'After_YearBefore', toString(false), toString(After_YearBefore))
define test_After_SameMonth: TestMessage(not After_SameMonth, 'After_SameMonth', toString(false), toString(After_SameMonth))
define test_After_MonthAfter: TestMessage(After_MonthAfter, 'After_MonthAfter', toString(true), toString(After_MonthAfter))
define test_After_MonthBefore: TestMessage(not After_MonthBefore, 'After_MonthBefore', toString(false), toString(After_MonthBefore))
define test_After_SameDay: TestMessage(not After_SameDay, 'After_SameDay', toString(false), toString(After_SameDay))
define test_After_DayAfter: TestMessage(After_DayAfter, 'After_DayAfter', toString(true), toString(After_DayAfter))
define test_After_DayBefore: TestMessage(not After_DayBefore, 'After_DayBefore', toString(false), toString(After_DayBefore))
define test_After_SameHour: TestMessage(not After_SameHour, 'After_SameHour', toString(false), toString(After_SameHour))
define test_After_HourAfter: TestMessage(After_HourAfter, 'After_HourAfter', toString(true), toString(After_HourAfter))
define test_After_HourBefore: TestMessage(not After_HourBefore, 'After_HourBefore', toString(false), toString(After_HourBefore))
define test_After_SameMinute: TestMessage(not After_SameMinute, 'After_SameMinute', toString(false), toString(After_SameMinute))
define test_After_MinuteAfter: TestMessage(After_MinuteAfter, 'After_MinuteAfter', toString(true), toString(After_MinuteAfter))
define test_After_MinuteBefore: TestMessage(not After_MinuteBefore, 'After_MinuteBefore', toString(false), toString(After_MinuteBefore))
define test_After_SameSecond: TestMessage(not After_SameSecond, 'After_SameSecond', toString(false), toString(After_SameSecond))
define test_After_SecondAfter: TestMessage(After_SecondAfter, 'After_SecondAfter', toString(true), toString(After_SecondAfter))
define test_After_SecondBefore: TestMessage(not After_SecondBefore, 'After_SecondBefore', toString(false), toString(After_SecondBefore))
define test_After_SameMillisecond: TestMessage(not After_SameMillisecond, 'After_SameMillisecond', toString(false), toString(After_SameMillisecond))
define test_After_MillisecondAfter: TestMessage(After_MillisecondAfter, 'After_MillisecondAfter', toString(true), toString(After_MillisecondAfter))
define test_After_MillisecondBefore: TestMessage(not After_MillisecondBefore, 'After_MillisecondBefore', toString(false), toString(After_MillisecondBefore))
define test_After_Same: TestMessage(not After_Same, 'After_Same', toString(false), toString(After_Same))
define test_After_After: TestMessage(After_After, 'After_After', toString(true), toString(After_After))
define test_After_Before: TestMessage(not After_Before, 'After_Before', toString(false), toString(After_Before))
define test_After_ImpreciseDay: TestMessage(After_ImpreciseDay is null, 'After_ImpreciseDay', 'null', toString(After_ImpreciseDay))
define test_After_ImpreciseDayMonthAfter: TestMessage(After_ImpreciseDayMonthAfter, 'After_ImpreciseDayMonthAfter', toString(true), toString(After_ImpreciseDayMonthAfter))
define test_After_ImpreciseDayMonthBefore: TestMessage(not After_ImpreciseDayMonthBefore, 'After_ImpreciseDayMonthBefore', toString(false), toString(After_ImpreciseDayMonthBefore))
define test_After_SameHourNormalizeZones: TestMessage(not After_SameHourNormalizeZones, 'After_SameHourNormalizeZones', toString(false), toString(After_SameHourNormalizeZones))
define test_After_HourAfterNormalizeZones: TestMessage(After_HourAfterNormalizeZones, 'After_HourAfterNormalizeZones', toString(true), toString(After_HourAfterNormalizeZones))
define test_After_HourBeforeNormalizeZones: TestMessage(not After_HourBeforeNormalizeZones, 'After_HourBeforeNormalizeZones', toString(false), toString(After_HourBeforeNormalizeZones))
define test_After_NullLeft: TestMessage(After_NullLeft is null, 'After_NullLeft', 'null', toString(After_NullLeft))
define test_After_NullRight: TestMessage(After_NullRight is null, 'After_NullRight', 'null', toString(After_NullRight))
define test_After_NullBoth: TestMessage(After_NullBoth is null, 'After_NullBoth', 'null', toString(After_NullBoth))

// Before
define Before_SameYear: DateTime(2000, 10, 15, 13, 30, 25, 200, +1.0) before year of DateTime(2000, 11, 23, 8, 14, 47, 500, +1.0)
define Before_YearAfter: DateTime(2000, 10, 15, 13, 30, 25, 200, +1.0) before year of DateTime(1999, 11, 23, 8, 14, 47, 500, +1.0)
define Before_YearBefore: DateTime(2000, 10, 15, 13, 30, 25, 200, +1.0) before year of DateTime(2001, 11, 23, 8, 14, 47, 500, +1.0)
define Before_SameMonth: DateTime(2000, 3, 22, 13, 30, 25, 200, +1.0) before month of DateTime(2000, 3, 23, 8, 14, 47, 500, +1.0)
define Before_MonthAfter: DateTime(2000, 3, 22, 13, 30, 25, 200, +1.0) before month of DateTime(2000, 2, 23, 8, 14, 47, 500, +1.0)
define Before_MonthBefore: DateTime(2000, 3, 22, 13, 30, 25, 200, +1.0) before month of DateTime(2000, 4, 23, 8, 14, 47, 500, +1.0)
// Removed offset because it was resulting in a test failure when shifting to system default
// define Before_SameDay: DateTime(2000, 3, 15, 6, 30, 25, 200, +1.0) before day of DateTime(2000, 3, 15, 8, 14, 47, 500, +1.0)
define Before_SameDay: DateTime(2000, 3, 15, 6, 30, 25, 200) before day of DateTime(2000, 3, 15, 8, 14, 47, 500)
define Before_DayAfter: DateTime(2000, 3, 15, 6, 30, 25, 200, +1.0) before day of DateTime(2000, 3, 14, 8, 14, 47, 500, +1.0)
define Before_DayBefore: DateTime(2000, 3, 15, 6, 30, 25, 200, +1.0) before day of DateTime(2000, 3, 16, 8, 14, 47, 500, +1.0)
define Before_SameHour: DateTime(2000, 3, 15, 13, 10, 25, 200, +1.0) before hour of DateTime(2000, 3, 15, 13, 14, 47, 500, +1.0)
define Before_HourAfter: DateTime(2000, 3, 15, 13, 10, 25, 200, +1.0) before hour of DateTime(2000, 3, 15, 12, 14, 47, 500, +1.0)
define Before_HourBefore: DateTime(2000, 3, 15, 13, 10, 25, 200, +1.0) before hour of DateTime(2000, 3, 15, 14, 14, 47, 500, +1.0)
define Before_SameMinute: DateTime(2000, 3, 15, 13, 30, 44, 200, +1.0) before minute of DateTime(2000, 3, 15, 13, 30, 47, 500, +1.0)
define Before_MinuteAfter: DateTime(2000, 3, 15, 13, 30, 44, 200, +1.0) before minute of DateTime(2000, 3, 15, 13, 29, 47, 500, +1.0)
define Before_MinuteBefore: DateTime(2000, 3, 15, 13, 30, 44, 200, +1.0) before minute of DateTime(2000, 3, 15, 13, 31, 47, 500, +1.0)
define Before_SameSecond: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) before second of DateTime(2000, 3, 15, 13, 30, 25, 500, +1.0)
define Before_SecondAfter: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) before second of DateTime(2000, 3, 15, 13, 30, 24, 500, +1.0)
define Before_SecondBefore: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) before second of DateTime(2000, 3, 15, 13, 30, 26, 500, +1.0)
define Before_SameMillisecond: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) before millisecond of DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0)
define Before_MillisecondAfter: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) before millisecond of DateTime(2000, 3, 15, 13, 30, 25, 199, +1.0)
define Before_MillisecondBefore: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) before millisecond of DateTime(2000, 3, 15, 13, 30, 25, 201, +1.0)
define Before_Same: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) before DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0)
define Before_After: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) before DateTime(2000, 3, 15, 13, 30, 25, 199, +1.0)
define Before_Before: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) before DateTime(2000, 3, 15, 13, 30, 25, 201, +1.0)
define Before_ImpreciseDay: DateTime(2000, 3, 15, 13, 30, 25, 200) before day of DateTime(2000, 3)
define Before_ImpreciseDayMonthAfter: DateTime(2000, 3, 15, 13, 30, 25, 200) before day of DateTime(2000, 2)
define Before_ImpreciseDayMonthBefore: DateTime(2000, 3, 15, 13, 30, 25, 200) before day of DateTime(2000, 4)
define Before_SameHourNormalizeZones: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) before hour of DateTime(2000, 3, 15, 7, 30, 25, 200, -5.0)
define Before_HourAfterNormalizeZones: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) before hour of DateTime(2000, 3, 15, 6, 30, 25, 200, -5.0)
define Before_HourBeforeNormalizeZones: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) before hour of DateTime(2000, 3, 15, 8, 30, 25, 200, -5.0)
define Before_NullLeft: null before DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0)
define Before_NullRight: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) before null
define Before_NullBoth: (null as DateTime) before null

define test_Before_SameYear: TestMessage(not Before_SameYear, 'Before_SameYear', toString(false), toString(Before_SameYear))
define test_Before_YearAfter: TestMessage(not Before_YearAfter, 'Before_YearAfter', toString(false), toString(Before_YearAfter))
define test_Before_YearBefore: TestMessage(Before_YearBefore, 'Before_YearBefore', toString(true), toString(Before_YearBefore))
define test_Before_SameMonth: TestMessage(not Before_SameMonth, 'Before_SameMonth', toString(false), toString(Before_SameMonth))
define test_Before_MonthAfter: TestMessage(not Before_MonthAfter, 'Before_MonthAfter', toString(false), toString(Before_MonthAfter))
define test_Before_MonthBefore: TestMessage(Before_MonthBefore, 'Before_MonthBefore', toString(true), toString(Before_MonthBefore))
define test_Before_SameDay: TestMessage(not Before_SameDay, 'Before_SameDay', toString(false), toString(Before_SameDay))
define test_Before_DayAfter: TestMessage(not Before_DayAfter, 'Before_DayAfter', toString(false), toString(Before_DayAfter))
define test_Before_DayBefore: TestMessage(Before_DayBefore, 'Before_DayBefore', toString(true), toString(Before_DayBefore))
define test_Before_SameHour: TestMessage(not Before_SameHour, 'Before_SameHour', toString(false), toString(Before_SameHour))
define test_Before_HourAfter: TestMessage(not Before_HourAfter, 'Before_HourAfter', toString(false), toString(Before_HourAfter))
define test_Before_HourBefore: TestMessage(Before_HourBefore, 'Before_HourBefore', toString(true), toString(Before_HourBefore))
define test_Before_SameMinute: TestMessage(not Before_SameMinute, 'Before_SameMinute', toString(false), toString(Before_SameMinute))
define test_Before_MinuteAfter: TestMessage(not Before_MinuteAfter, 'Before_MinuteAfter', toString(false), toString(Before_MinuteAfter))
define test_Before_MinuteBefore: TestMessage(Before_MinuteBefore, 'Before_MinuteBefore', toString(true), toString(Before_MinuteBefore))
define test_Before_SameSecond: TestMessage(not Before_SameSecond, 'Before_SameSecond', toString(false), toString(Before_SameSecond))
define test_Before_SecondAfter: TestMessage(not Before_SecondAfter, 'Before_SecondAfter', toString(false), toString(Before_SecondAfter))
define test_Before_SecondBefore: TestMessage(Before_SecondBefore, 'Before_SecondBefore', toString(true), toString(Before_SecondBefore))
define test_Before_SameMillisecond: TestMessage(not Before_SameMillisecond, 'Before_SameMillisecond', toString(false), toString(Before_SameMillisecond))
define test_Before_MillisecondAfter: TestMessage(not Before_MillisecondAfter, 'Before_MillisecondAfter', toString(false), toString(Before_MillisecondAfter))
define test_Before_MillisecondBefore: TestMessage(Before_MillisecondBefore, 'Before_MillisecondBefore', toString(true), toString(Before_MillisecondBefore))
define test_Before_Same: TestMessage(not Before_Same, 'Before_Same', toString(false), toString(Before_Same))
define test_Before_After: TestMessage(not Before_After, 'Before_After', toString(false), toString(Before_After))
define test_Before_Before: TestMessage(Before_Before, 'Before_Before', toString(true), toString(Before_Before))
define test_Before_ImpreciseDay: TestMessage(Before_ImpreciseDay is null, 'Before_ImpreciseDay', 'null', toString(Before_ImpreciseDay))
define test_Before_ImpreciseDayMonthAfter: TestMessage(not Before_ImpreciseDayMonthAfter, 'Before_ImpreciseDayMonthAfter', toString(false), toString(Before_ImpreciseDayMonthAfter))
define test_Before_ImpreciseDayMonthBefore: TestMessage(Before_ImpreciseDayMonthBefore, 'Before_ImpreciseDayMonthBefore', toString(true), toString(Before_ImpreciseDayMonthBefore))
define test_Before_SameHourNormalizeZones: TestMessage(not Before_SameHourNormalizeZones, 'Before_SameHourNormalizeZones', toString(false), toString(Before_SameHourNormalizeZones))
define test_Before_HourAfterNormalizeZones: TestMessage(not Before_HourAfterNormalizeZones, 'Before_HourAfterNormalizeZones', toString(false), toString(Before_HourAfterNormalizeZones))
define test_Before_HourBeforeNormalizeZones: TestMessage(Before_HourBeforeNormalizeZones, 'Before_HourBeforeNormalizeZones', toString(true), toString(Before_HourBeforeNormalizeZones))
define test_Before_NullLeft: TestMessage(Before_NullLeft is null, 'Before_NullLeft', 'null', toString(Before_NullLeft))
define test_Before_NullRight: TestMessage(Before_NullRight is null, 'Before_NullRight', 'null', toString(Before_NullRight))
define test_Before_NullBoth: TestMessage(Before_NullBoth is null, 'Before_NullBoth', 'null', toString(Before_NullBoth))

// DifferenceBetween
define DifferenceBetween_YearsBetween: difference in years between NewYear2013 and NewYear2014
define DifferenceBetween_MonthsBetween: difference in months between NewYear2013 and NewYear2014
define DifferenceBetween_WeeksBetween: difference in weeks between NewYear2013 and NewYear2014
define DifferenceBetween_DaysBetween: difference in days between NewYear2013 and NewYear2014
define DifferenceBetween_HoursBetween: difference in hours between NewYear2013 and NewYear2014
define DifferenceBetween_MinutesBetween: difference in minutes between NewYear2013 and NewYear2014
define DifferenceBetween_SecondsBetween: difference in seconds between NewYear2013 and NewYear2014
define DifferenceBetween_SecondsBetweenReversed: difference in seconds between NewYear2014 and NewYear2013
define DifferenceBetween_YearsBetweenUncertainty: difference in years between NewYear2014 and January2014
define DifferenceBetween_MonthsBetweenUncertainty: difference in months between NewYear2014 and January2014
define DifferenceBetween_WeeksBetweenUncertainty: difference in weeks between NewYear2014 and January2014
define DifferenceBetween_DaysBetweenUncertainty: difference in days between NewYear2014 and January2014
define DifferenceBetween_HoursBetweenUncertainty: difference in hours between NewYear2014 and January2014
define DifferenceBetween_MinutesBetweenUncertainty: difference in minutes between NewYear2014 and January2014
define DifferenceBetween_SecondsBetweenUncertainty: difference in seconds between NewYear2014 and January2014
define DifferenceBetween_SecondsBetweenReversedUncertainty: difference in seconds between January2014 and NewYear2014
define DifferenceBetween_HoursBetween1and3CrossingSpringDST: difference in hours between DateTime(2017, 3, 12, 1, 0, 0, 0, -5.0) and DateTime(2017, 3, 12, 3, 0, 0, 0, -4.0)
define DifferenceBetween_HoursBetween1and3CrossingFallDST: difference in hours between DateTime(2017, 11, 5, 1, 0, 0, 0, -4.0) and DateTime(2017, 11, 5, 3, 0, 0, 0, -5.0)

define test_DifferenceBetween_YearsBetween: TestMessage(DifferenceBetween_YearsBetween = 1, 'DifferenceBetween_YearsBetween', toString(1), toString(DifferenceBetween_YearsBetween))
define test_DifferenceBetween_MonthsBetween: TestMessage(DifferenceBetween_MonthsBetween = 12, 'DifferenceBetween_MonthsBetween', toString(12), toString(DifferenceBetween_MonthsBetween))
define test_DifferenceBetween_WeeksBetween: TestMessage(DifferenceBetween_WeeksBetween = 52, 'DifferenceBetween_WeeksBetween', toString(52), toString(DifferenceBetween_WeeksBetween))
define test_DifferenceBetween_DaysBetween: TestMessage(DifferenceBetween_DaysBetween = 365, 'DifferenceBetween_DaysBetween', toString(365), toString(DifferenceBetween_DaysBetween))
define test_DifferenceBetween_HoursBetween: TestMessage(DifferenceBetween_HoursBetween = 8760, 'DifferenceBetween_HoursBetween', toString(8760), toString(DifferenceBetween_HoursBetween))
define test_DifferenceBetween_MinutesBetween: TestMessage(DifferenceBetween_MinutesBetween = 525600, 'DifferenceBetween_MinutesBetween', toString(525600), toString(DifferenceBetween_MinutesBetween))
define test_DifferenceBetween_SecondsBetween: TestMessage(DifferenceBetween_SecondsBetween = 31536000, 'DifferenceBetween_SecondsBetween', toString(31536000), toString(DifferenceBetween_SecondsBetween))
define test_DifferenceBetween_SecondsBetweenReversed: TestMessage(DifferenceBetween_SecondsBetweenReversed = -31536000, 'DifferenceBetween_SecondsBetweenReversed', toString(-31536000), toString(DifferenceBetween_SecondsBetweenReversed))
define test_DifferenceBetween_YearsBetweenUncertainty: TestMessage(DifferenceBetween_YearsBetweenUncertainty = 0, 'DifferenceBetween_YearsBetweenUncertainty', toString(0), toString(DifferenceBetween_YearsBetweenUncertainty))
define test_DifferenceBetween_MonthsBetweenUncertainty: TestMessage(DifferenceBetween_MonthsBetweenUncertainty = 0, 'DifferenceBetween_MonthsBetweenUncertainty', toString(0), toString(DifferenceBetween_MonthsBetweenUncertainty))
define test_DifferenceBetween_WeeksBetweenUncertainty: TestMessage(DifferenceBetween_WeeksBetweenUncertainty > -1 and (DifferenceBetween_WeeksBetweenUncertainty > 0) is null and DifferenceBetween_WeeksBetweenUncertainty < 5 and (DifferenceBetween_WeeksBetweenUncertainty < 4) is null, 'DifferenceBetween_WeeksBetweenUncertainty', toString(true), toString(DifferenceBetween_WeeksBetweenUncertainty > -1 and (DifferenceBetween_WeeksBetweenUncertainty > 0) is null and DifferenceBetween_WeeksBetweenUncertainty < 5 and (DifferenceBetween_WeeksBetweenUncertainty < 4) is null))
define test_DifferenceBetween_DaysBetweenUncertainty: TestMessage(DifferenceBetween_DaysBetweenUncertainty > -1 and (DifferenceBetween_DaysBetweenUncertainty > 0) is null and DifferenceBetween_DaysBetweenUncertainty < 31 and (DifferenceBetween_DaysBetweenUncertainty < 30) is null, 'DifferenceBetween_DaysBetweenUncertainty', toString(true), toString(DifferenceBetween_DaysBetweenUncertainty > -1 and (DifferenceBetween_DaysBetweenUncertainty > 0) is null and DifferenceBetween_DaysBetweenUncertainty < 31 and (DifferenceBetween_DaysBetweenUncertainty < 30) is null))
define test_DifferenceBetween_HoursBetweenUncertainty: TestMessage(DifferenceBetween_HoursBetweenUncertainty > -1 and (DifferenceBetween_HoursBetweenUncertainty > 0) is null and DifferenceBetween_HoursBetweenUncertainty < 744 and (DifferenceBetween_HoursBetweenUncertainty < 743) is null, 'DifferenceBetween_HoursBetweenUncertainty', toString(true), toString(DifferenceBetween_HoursBetweenUncertainty > -1 and (DifferenceBetween_HoursBetweenUncertainty > 0) is null and DifferenceBetween_HoursBetweenUncertainty < 721 and (DifferenceBetween_HoursBetweenUncertainty < 720) is null))
define test_DifferenceBetween_MinutesBetweenUncertainty: TestMessage(DifferenceBetween_MinutesBetweenUncertainty > -1 and (DifferenceBetween_MinutesBetweenUncertainty > 0) is null and DifferenceBetween_MinutesBetweenUncertainty < 44640 and (DifferenceBetween_MinutesBetweenUncertainty < 44639) is null, 'DifferenceBetween_MinutesBetweenUncertainty', toString(true), toString(DifferenceBetween_MinutesBetweenUncertainty > -1 and (DifferenceBetween_MinutesBetweenUncertainty > 0) is null and DifferenceBetween_MinutesBetweenUncertainty < 43201 and (DifferenceBetween_MinutesBetweenUncertainty < 43200) is null))
define test_DifferenceBetween_SecondsBetweenUncertainty: TestMessage(DifferenceBetween_SecondsBetweenUncertainty > -1 and (DifferenceBetween_SecondsBetweenUncertainty > 0) is null and DifferenceBetween_SecondsBetweenUncertainty < 2678400 and (DifferenceBetween_SecondsBetweenUncertainty < 2678399) is null, 'DifferenceBetween_SecondsBetweenUncertainty', toString(true), toString(DifferenceBetween_SecondsBetweenUncertainty > -1 and (DifferenceBetween_SecondsBetweenUncertainty > 0) is null and DifferenceBetween_SecondsBetweenUncertainty < 2592001 and (DifferenceBetween_SecondsBetweenUncertainty < 2592000) is null))
define test_DifferenceBetween_SecondsBetweenReversedUncertainty: TestMessage(DifferenceBetween_SecondsBetweenReversedUncertainty > -2678400 and (DifferenceBetween_SecondsBetweenReversedUncertainty > -2678399) is null and DifferenceBetween_SecondsBetweenReversedUncertainty < 1 and (DifferenceBetween_SecondsBetweenReversedUncertainty < 0) is null, 'DifferenceBetween_SecondsBetweenReversedUncertainty', toString(true), toString(DifferenceBetween_SecondsBetweenReversedUncertainty > -2592001 and (DifferenceBetween_SecondsBetweenReversedUncertainty > -2592000) is null and DifferenceBetween_SecondsBetweenReversedUncertainty < 1 and (DifferenceBetween_SecondsBetweenReversedUncertainty < 0) is null))
define test_DifferenceBetween_HoursBetween1and3CrossingSpringDST: TestMessage(DifferenceBetween_HoursBetween1and3CrossingSpringDST = 1, 'DifferenceBetween_HoursBetween1and3CrossingSpringDST', toString(1), toString(DifferenceBetween_HoursBetween1and3CrossingSpringDST))
define test_DifferenceBetween_HoursBetween1and3CrossingFallDST: TestMessage(DifferenceBetween_HoursBetween1and3CrossingFallDST = 3, 'DifferenceBetween_HoursBetween1and3CrossingFallDST', toString(3), toString(DifferenceBetween_HoursBetween1and3CrossingFallDST))

// DifferenceBetween Comparisons
define DiffComp_GreaterThan25DaysAfter: difference in days between NewYear2014 and February2014 > 25
define DiffComp_GreaterThan40DaysAfter: difference in days between NewYear2014 and February2014 > 40
define DiffComp_GreaterThan80DaysAfter: difference in days between NewYear2014 and February2014 > 80
define DiffComp_GreaterOrEqualTo25DaysAfter: difference in days between NewYear2014 and February2014 >= 25
define DiffComp_GreaterOrEqualTo40DaysAfter: difference in days between NewYear2014 and February2014 >= 40
define DiffComp_GreaterOrEqualTo80DaysAfter: difference in days between NewYear2014 and February2014 >= 80
define DiffComp_EqualTo25DaysAfter: difference in days between NewYear2014 and February2014 = 25
define DiffComp_EqualTo40DaysAfter: difference in days between NewYear2014 and February2014 = 40
define DiffComp_EqualTo80DaysAfter: difference in days between NewYear2014 and February2014 = 80
define DiffComp_LessOrEqualTo25DaysAfter: difference in days between NewYear2014 and February2014 <= 25
define DiffComp_LessOrEqualTo40DaysAfter: difference in days between NewYear2014 and February2014 <= 40
define DiffComp_LessOrEqualTo80DaysAfter: difference in days between NewYear2014 and February2014 <= 80
define DiffComp_LessThan25DaysAfter: difference in days between NewYear2014 and February2014 < 25
define DiffComp_LessThan40DaysAfter: difference in days between NewYear2014 and February2014 < 40
define DiffComp_LessThan80DaysAfter: difference in days between NewYear2014 and February2014 < 80
define DiffComp_TwentyFiveDaysLessThanDaysBetween: 25 < difference in days between NewYear2014 and February2014
define DiffComp_FortyDaysEqualToDaysBetween: 40 = difference in days between NewYear2014 and February2014
define DiffComp_TwentyFiveDaysGreaterThanDaysBetween: 25 > difference in days between NewYear2014 and February2014
define DiffComp_BonnieTestCase: difference in months between DateTime(2012, 9, 13, 14, 50, 0, 0, -4.0) and DateTime(2012, 12, 31, 23, 59, 59, 999, -5.0) <= 2
define DiffComp_BonnieTestCaseZulu: difference in months between DateTime(2012, 9, 13, 14, 50, 0, 0, 0.0) and DateTime(2012, 12, 31, 23, 59, 59, 999, 0.0) <= 2

define test_DiffComp_GreaterThan25DaysAfter: TestMessage(DiffComp_GreaterThan25DaysAfter, 'DiffComp_GreaterThan25DaysAfter', toString(true), toString(DiffComp_GreaterThan25DaysAfter))
define test_DiffComp_GreaterThan40DaysAfter: TestMessage(DiffComp_GreaterThan40DaysAfter is null, 'DiffComp_GreaterThan40DaysAfter', 'null', toString(DiffComp_GreaterThan40DaysAfter))
define test_DiffComp_GreaterThan80DaysAfter: TestMessage(not DiffComp_GreaterThan80DaysAfter, 'DiffComp_GreaterThan80DaysAfter', toString(false), toString(DiffComp_GreaterThan80DaysAfter))
define test_DiffComp_GreaterOrEqualTo25DaysAfter: TestMessage(DiffComp_GreaterOrEqualTo25DaysAfter, 'DiffComp_GreaterOrEqualTo25DaysAfter', toString(true), toString(DiffComp_GreaterOrEqualTo25DaysAfter))
define test_DiffComp_GreaterOrEqualTo40DaysAfter: TestMessage(DiffComp_GreaterOrEqualTo40DaysAfter is null, 'DiffComp_GreaterOrEqualTo40DaysAfter', 'null', toString(DiffComp_GreaterOrEqualTo40DaysAfter))
define test_DiffComp_GreaterOrEqualTo80DaysAfter: TestMessage(not DiffComp_GreaterOrEqualTo80DaysAfter, 'DiffComp_GreaterOrEqualTo80DaysAfter', toString(false), toString(DiffComp_GreaterOrEqualTo80DaysAfter))
define test_DiffComp_EqualTo25DaysAfter: TestMessage(not DiffComp_EqualTo25DaysAfter, 'DiffComp_EqualTo25DaysAfter', toString(false), toString(DiffComp_EqualTo25DaysAfter))
define test_DiffComp_EqualTo40DaysAfter: TestMessage(DiffComp_EqualTo40DaysAfter is null, 'DiffComp_EqualTo40DaysAfter', 'null', toString(DiffComp_EqualTo40DaysAfter))
define test_DiffComp_EqualTo80DaysAfter: TestMessage(not DiffComp_EqualTo80DaysAfter, 'DiffComp_EqualTo80DaysAfter', toString(false), toString(DiffComp_EqualTo80DaysAfter))
define test_DiffComp_LessOrEqualTo25DaysAfter: TestMessage(not DiffComp_LessOrEqualTo25DaysAfter, 'DiffComp_LessOrEqualTo25DaysAfter', toString(false), toString(DiffComp_LessOrEqualTo25DaysAfter))
define test_DiffComp_LessOrEqualTo40DaysAfter: TestMessage(DiffComp_LessOrEqualTo40DaysAfter is null, 'DiffComp_LessOrEqualTo40DaysAfter', 'null', toString(DiffComp_LessOrEqualTo40DaysAfter))
define test_DiffComp_LessOrEqualTo80DaysAfter: TestMessage(DiffComp_LessOrEqualTo80DaysAfter, 'DiffComp_LessOrEqualTo80DaysAfter', toString(true), toString(DiffComp_LessOrEqualTo80DaysAfter))
define test_DiffComp_LessThan25DaysAfter: TestMessage(not DiffComp_LessThan25DaysAfter, 'DiffComp_LessThan25DaysAfter', toString(false), toString(DiffComp_LessThan25DaysAfter))
define test_DiffComp_LessThan40DaysAfter: TestMessage(DiffComp_LessThan40DaysAfter is null, 'DiffComp_LessThan40DaysAfter', 'null', toString(DiffComp_LessThan40DaysAfter))
define test_DiffComp_LessThan80DaysAfter: TestMessage(DiffComp_LessThan80DaysAfter, 'DiffComp_LessThan80DaysAfter', toString(true), toString(DiffComp_LessThan80DaysAfter))
define test_DiffComp_TwentyFiveDaysLessThanDaysBetween: TestMessage(DiffComp_TwentyFiveDaysLessThanDaysBetween, 'DiffComp_TwentyFiveDaysLessThanDaysBetween', toString(true), toString(DiffComp_TwentyFiveDaysLessThanDaysBetween))
define test_DiffComp_FortyDaysEqualToDaysBetween: TestMessage(DiffComp_FortyDaysEqualToDaysBetween is null, 'DiffComp_FortyDaysEqualToDaysBetween', 'null', toString(DiffComp_FortyDaysEqualToDaysBetween))
define test_DiffComp_TwentyFiveDaysGreaterThanDaysBetween: TestMessage(not DiffComp_TwentyFiveDaysGreaterThanDaysBetween, 'DiffComp_TwentyFiveDaysGreaterThanDaysBetween', toString(false), toString(DiffComp_TwentyFiveDaysGreaterThanDaysBetween))
define test_DiffComp_BonnieTestCase: TestMessage(not DiffComp_BonnieTestCase, 'DiffComp_BonnieTestCase', toString(false), toString(DiffComp_BonnieTestCase))
define test_DiffComp_BonnieTestCaseZulu: TestMessage(not DiffComp_BonnieTestCaseZulu, 'DiffComp_BonnieTestCaseZulu', toString(false), toString(DiffComp_BonnieTestCaseZulu))

// DurationBetween
define DurationBetween_YearsBetween: years between DecTen2013 and JanOne2015
define DurationBetween_MonthsBetween: months between DecTen2013 and JanOne2015
define DurationBetween_WeeksBetween: weeks between DecTen2013 and JanOne2015
define DurationBetween_DaysBetween: days between DecTen2013 and JanOne2015
define DurationBetween_HoursBetween: hours between DecTen2013 and JanOne2015
define DurationBetween_MinutesBetween: minutes between DecTen2013 and JanOne2015
define DurationBetween_SecondsBetween: seconds between DecTen2013 and JanOne2015
define DurationBetween_SecondsBetweenReversed: seconds between JanOne2015 and DecTen2013
// TODO - ms between tests
define DurationBetween_YearsBetweenUncertainty: years between JanOne2015 and January2015
define DurationBetween_MonthsBetweenUncertainty: months between JanOne2015 and January2015
define DurationBetween_WeeksBetweenUncertainty: weeks between JanOne2015 and January2015
define DurationBetween_DaysBetweenUncertainty: days between JanOne2015 and January2015
define DurationBetween_HoursBetweenUncertainty: hours between JanOne2015 and January2015
define DurationBetween_MinutesBetweenUncertainty: minutes between JanOne2015 and January2015
define DurationBetween_SecondsBetweenUncertainty: seconds between JanOne2015 and January2015
define DurationBetween_SecondsBetweenReversedUncertainty: seconds between January2015 and JanOne2015
define DurationBetween_HoursBetween1and3CrossingSpringDST: hours between DateTime(2017, 3, 12, 1, 0, 0, 0, -5.0) and DateTime(2017, 3, 12, 3, 0, 0, 0, -4.0)
define DurationBetween_HoursBetween1and3CrossingFallDST: hours between DateTime(2017, 11, 5, 1, 0, 0, 0, -4.0) and DateTime(2017, 11, 5, 3, 0, 0, 0, -5.0)

define test_DurationBetween_YearsBetween: TestMessage(DurationBetween_YearsBetween = 1, 'DurationBetween_YearsBetween', toString(1), toString(DurationBetween_YearsBetween))
define test_DurationBetween_MonthsBetween: TestMessage(DurationBetween_MonthsBetween = 12, 'DurationBetween_MonthsBetween', toString(12), toString(DurationBetween_MonthsBetween))
define test_DurationBetween_WeeksBetween: TestMessage(DurationBetween_WeeksBetween = 55, 'DurationBetween_WeeksBetween', toString(55), toString(DurationBetween_WeeksBetween))
define test_DurationBetween_DaysBetween: TestMessage(DurationBetween_DaysBetween = 386, 'DurationBetween_DaysBetween', toString(386), toString(DurationBetween_DaysBetween))
define test_DurationBetween_HoursBetween: TestMessage(DurationBetween_HoursBetween = 9275, 'DurationBetween_HoursBetween', toString(9275), toString(DurationBetween_HoursBetween))
define test_DurationBetween_MinutesBetween: TestMessage(DurationBetween_MinutesBetween = 556529, 'DurationBetween_MinutesBetween', toString(556529), toString(DurationBetween_MinutesBetween))
define test_DurationBetween_SecondsBetween: TestMessage(DurationBetween_SecondsBetween = 33391769, 'DurationBetween_SecondsBetween', toString(33391769), toString(DurationBetween_SecondsBetween))
define test_DurationBetween_SecondsBetweenReversed: TestMessage(DurationBetween_SecondsBetweenReversed = -33391769, 'DurationBetween_SecondsBetweenReversed', toString(-33391769), toString(DurationBetween_SecondsBetweenReversed))
// TODO - ms between tests
define test_DurationBetween_YearsBetweenUncertainty: TestMessage(DurationBetween_YearsBetweenUncertainty = 0, 'DurationBetween_YearsBetweenUncertainty', toString(0), toString(DurationBetween_YearsBetweenUncertainty))
define test_DurationBetween_MonthsBetweenUncertainty: TestMessage(DurationBetween_MonthsBetweenUncertainty = 0, 'DurationBetween_MonthsBetweenUncertainty', toString(0), toString(DurationBetween_MonthsBetweenUncertainty))
define test_DurationBetween_WeeksBetweenUncertainty: TestMessage(DurationBetween_WeeksBetweenUncertainty > -1 and (DurationBetween_WeeksBetweenUncertainty > 0) is null and DurationBetween_WeeksBetweenUncertainty < 5 and (DurationBetween_WeeksBetweenUncertainty < 4) is null, 'DurationBetween_WeeksBetweenUncertainty', toString(true), toString(DurationBetween_WeeksBetweenUncertainty > -1 and (DurationBetween_WeeksBetweenUncertainty > 0) is null and DurationBetween_WeeksBetweenUncertainty < 5 and (DurationBetween_WeeksBetweenUncertainty < 4) is null))
define test_DurationBetween_DaysBetweenUncertainty: TestMessage(DurationBetween_DaysBetweenUncertainty > -1 and (DurationBetween_DaysBetweenUncertainty > 0) is null and DurationBetween_DaysBetweenUncertainty < 31 and (DurationBetween_DaysBetweenUncertainty < 30) is null, 'DurationBetween_DaysBetweenUncertainty', toString(true), toString(DurationBetween_DaysBetweenUncertainty > -1 and (DurationBetween_DaysBetweenUncertainty > 0) is null and DurationBetween_DaysBetweenUncertainty < 31 and (DurationBetween_DaysBetweenUncertainty < 30) is null))
define test_DurationBetween_HoursBetweenUncertainty: TestMessage(DurationBetween_HoursBetweenUncertainty > -1 and (DurationBetween_HoursBetweenUncertainty > 0) is null and DurationBetween_HoursBetweenUncertainty < 744 and (DurationBetween_HoursBetweenUncertainty < 743) is null, 'DurationBetween_HoursBetweenUncertainty', toString(true), toString(DurationBetween_HoursBetweenUncertainty > -1 and (DurationBetween_HoursBetweenUncertainty > 0) is null and DurationBetween_HoursBetweenUncertainty < 721 and (DurationBetween_HoursBetweenUncertainty < 720) is null))
define test_DurationBetween_MinutesBetweenUncertainty: TestMessage(DurationBetween_MinutesBetweenUncertainty > -1 and (DurationBetween_MinutesBetweenUncertainty > 0) is null and DurationBetween_MinutesBetweenUncertainty < 44640 and (DurationBetween_MinutesBetweenUncertainty < 44639) is null, 'DurationBetween_MinutesBetweenUncertainty', toString(true), toString(DurationBetween_MinutesBetweenUncertainty > -1 and (DurationBetween_MinutesBetweenUncertainty > 0) is null and DurationBetween_MinutesBetweenUncertainty < 43201 and (DurationBetween_MinutesBetweenUncertainty < 43200) is null))
define test_DurationBetween_SecondsBetweenUncertainty: TestMessage(DurationBetween_SecondsBetweenUncertainty > -1 and (DurationBetween_SecondsBetweenUncertainty > 0) is null and DurationBetween_SecondsBetweenUncertainty < 2678400 and (DurationBetween_SecondsBetweenUncertainty < 2678399) is null, 'DurationBetween_SecondsBetweenUncertainty', toString(true), toString(DurationBetween_SecondsBetweenUncertainty > -1 and (DurationBetween_SecondsBetweenUncertainty > 0) is null and DurationBetween_SecondsBetweenUncertainty < 2592001 and (DurationBetween_SecondsBetweenUncertainty < 2592000) is null))
define test_DurationBetween_SecondsBetweenReversedUncertainty: TestMessage(DurationBetween_SecondsBetweenReversedUncertainty > -2678400 and (DurationBetween_SecondsBetweenReversedUncertainty > -2678399) is null and DurationBetween_SecondsBetweenReversedUncertainty < 1 and (DurationBetween_SecondsBetweenReversedUncertainty < 0) is null, 'DurationBetween_SecondsBetweenReversedUncertainty', toString(true), toString(DurationBetween_SecondsBetweenReversedUncertainty > -2592001 and (DurationBetween_SecondsBetweenReversedUncertainty > -2592000) is null and DurationBetween_SecondsBetweenReversedUncertainty < 1 and (DurationBetween_SecondsBetweenReversedUncertainty < 0) is null))
define test_DurationBetween_HoursBetween1and3CrossingSpringDST: TestMessage(DurationBetween_HoursBetween1and3CrossingSpringDST = 1, 'DurationBetween_HoursBetween1and3CrossingSpringDST', toString(1), toString(DurationBetween_HoursBetween1and3CrossingSpringDST))
define test_DurationBetween_HoursBetween1and3CrossingFallDST: TestMessage(DurationBetween_HoursBetween1and3CrossingFallDST = 3, 'DurationBetween_HoursBetween1and3CrossingFallDST', toString(3), toString(DurationBetween_HoursBetween1and3CrossingFallDST))

// DateMath
define DateMath_PlusThreeYears: June15th2013 + 3 years
define DateMath_MinusThreeYears: June15th2013 - 3 years
define DateMath_PlusEightMonths: June15th2013 + 8 months
define DateMath_MinusEightMonths: June15th2013 - 8 months
define DateMath_PlusThreeWeeks: June15th2013 + 3 weeks
define DateMath_MinusThreeWeeks: June15th2013 - 3 weeks
define DateMath_PlusTwentyDays: June15th2013 + 20 days
define DateMath_MinusTwentyDays: June15th2013 - 20 days
define DateMath_PlusThreeHours: June15th2013 + 3 hours
define DateMath_MinusThreeHours: June15th2013 - 3 hours
define DateMath_PlusThreeMinutes: June15th2013 + 3 minutes
define DateMath_MinusThreeMinutes: June15th2013 - 3 minutes
define DateMath_PlusThreeSeconds: June15th2013 + 3 seconds
define DateMath_MinusThreeSeconds: June15th2013 - 3 seconds
define DateMath_PlusThreeMilliseconds: June15th2013 + 3 milliseconds
define DateMath_MinusThreeMilliseconds: June15th2013 - 3 milliseconds

define test_DateMath_PlusThreeYears: TestMessage(DateMath_PlusThreeYears = DateTime(2016, 6, 15, 0, 0, 0, 0), 'DateMath_PlusThreeYears', toString(DateTime(2016, 6, 15, 0, 0, 0, 0)), toString(DateMath_PlusThreeYears))
define test_DateMath_MinusThreeYears: TestMessage(DateMath_MinusThreeYears = DateTime(2010, 6, 15, 0, 0, 0, 0), 'DateMath_MinusThreeYears', toString(DateTime(2010, 6, 15, 0, 0, 0, 0)), toString(DateMath_MinusThreeYears))
define test_DateMath_PlusEightMonths: TestMessage(DateMath_PlusEightMonths same day as DateTime(2014, 2, 15, 0, 0, 0, 0), 'DateMath_PlusEightMonths', toString(DateTime(2014, 2, 15, 0, 0, 0, 0)), toString(DateMath_PlusEightMonths))
define test_DateMath_MinusEightMonths: TestMessage(DateMath_MinusEightMonths same day as DateTime(2012, 10, 15, 0, 0, 0, 0), 'DateMath_MinusEightMonths', toString(DateTime(2012, 10, 15, 0, 0, 0, 0)), toString(DateMath_MinusEightMonths))
define test_DateMath_PlusThreeWeeks: TestMessage(DateMath_PlusThreeWeeks = DateTime(2013, 7, 6, 0, 0, 0, 0), 'DateMath_PlusThreeWeeks', toString(DateTime(2013, 7, 6, 0, 0, 0, 0)), toString(DateMath_PlusThreeWeeks))
define test_DateMath_MinusThreeWeeks: TestMessage(DateMath_MinusThreeWeeks = DateTime(2013, 5, 25, 0, 0, 0, 0), 'DateMath_MinusThreeWeeks', toString(DateTime(2013, 5, 25, 0, 0, 0, 0)), toString(DateMath_MinusThreeWeeks))
define test_DateMath_PlusTwentyDays: TestMessage(DateMath_PlusTwentyDays = DateTime(2013, 7, 5, 0, 0, 0, 0), 'DateMath_PlusTwentyDays', toString(DateTime(2013, 7, 5, 0, 0, 0, 0)), toString(DateMath_PlusTwentyDays))
define test_DateMath_MinusTwentyDays: TestMessage(DateMath_MinusTwentyDays = DateTime(2013, 5, 26, 0, 0, 0, 0), 'DateMath_MinusTwentyDays', toString(DateTime(2013, 5, 26, 0, 0, 0, 0)), toString(DateMath_MinusTwentyDays))
define test_DateMath_PlusThreeHours: TestMessage(DateMath_PlusThreeHours = DateTime(2013, 6, 15, 3, 0, 0, 0), 'DateMath_PlusThreeHours', toString(DateTime(2013, 6, 15, 3, 0, 0, 0)), toString(DateMath_PlusThreeHours))
define test_DateMath_MinusThreeHours: TestMessage(DateMath_MinusThreeHours = DateTime(2013, 6, 14, 21, 0, 0, 0), 'DateMath_MinusThreeHours', toString(DateTime(2013, 6, 14, 21, 0, 0, 0)), toString(DateMath_MinusThreeHours))
define test_DateMath_PlusThreeMinutes: TestMessage(DateMath_PlusThreeMinutes = DateTime(2013, 6, 15, 0, 3, 0, 0), 'DateMath_PlusThreeMinutes', toString(DateTime(2013, 6, 15, 0, 3, 0, 0)), toString(DateMath_PlusThreeMinutes))
define test_DateMath_MinusThreeMinutes: TestMessage(DateMath_MinusThreeMinutes = DateTime(2013, 6, 14, 23, 57, 0, 0), 'DateMath_MinusThreeMinutes', toString(DateTime(2013, 6, 14, 23, 57, 0, 0)), toString(DateMath_MinusThreeMinutes))
define test_DateMath_PlusThreeSeconds: TestMessage(DateMath_PlusThreeSeconds = DateTime(2013, 6, 15, 0, 0, 3, 0), 'DateMath_PlusThreeSeconds', toString(DateTime(2013, 6, 15, 0, 0, 3, 0)), toString(DateMath_PlusThreeSeconds))
define test_DateMath_MinusThreeSeconds: TestMessage(DateMath_MinusThreeSeconds = DateTime(2013, 6, 14, 23, 59, 57, 0), 'DateMath_MinusThreeSeconds', toString(DateTime(2013, 6, 14, 23, 59, 57, 0)), toString(DateMath_MinusThreeSeconds))
define test_DateMath_PlusThreeMilliseconds: TestMessage(DateMath_PlusThreeMilliseconds = DateTime(2013, 6, 15, 0, 0, 0, 3), 'DateMath_PlusThreeMilliseconds', toString(DateTime(2013, 6, 15, 0, 0, 0, 3)), toString(DateMath_PlusThreeMilliseconds))
define test_DateMath_MinusThreeMilliseconds: TestMessage(DateMath_MinusThreeMilliseconds = DateTime(2013, 6, 14, 23, 59, 59, 997), 'DateMath_MinusThreeMilliseconds', toString(DateTime(2013, 6, 14, 23, 59, 59, 997)), toString(DateMath_MinusThreeMilliseconds))

/*
**********************
  End DateTime Tests
**********************
*/

/*
************************
  Start Interval Tests
************************
*/

// Globals
define DateIvl: Interval[DateTime(2012, 3, 1, 0, 0, 0, 0), DateTime(2012, 9, 1, 0, 0, 0, 0))
define PrecisionDateIvl: Interval[DateTime(2012, 3, 2, 12, 34, 56, 789), DateTime(2012, 9, 2, 1, 23, 45, 678))
define ImpDateIvl: Interval[DateTime(2012, 3), DateTime(2012, 8)]

// Equal
define Equal_EqualClosed: Interval[1, 5] = Interval[1, 5]
define Equal_EqualOpen: Interval(1, 5) = Interval(1, 5)
define Equal_EqualOpenClosed: Interval(1, 5) = Interval[2, 4]
define Equal_UnequalClosed: Interval[1, 5] = Interval[2, 4]
define Equal_UnequalOpen: Interval(1, 5) = Interval(2, 4)
define Equal_UnequalClosedOpen: Interval[1, 5] = Interval(2, 4)
define Equal_EqualQuantityClosed: Interval[1 'mg', 5 'mg'] = Interval[1 'mg', 5 'mg']
define Equal_EqualQuantityOpen: Interval(1 'mg', 5 'mg') = Interval(1 'mg', 5 'mg')
define Equal_EqualQuantityOpenClosed: Interval(1 'mg', 5 'mg') = Interval[1.00000001 'mg', 4.99999999 'mg']
define Equal_UnequalQuantityClosed: Interval[1 'mg', 5 'mg'] = Interval[2 'mg', 4 'mg']
define Equal_UnequalQuantityOpen: Interval(1 'mg', 5 'mg') = Interval(2 'mg', 4 'mg')
define Equal_UnequalQuantityClosedOpen: Interval[1 'mg', 5 'mg'] = Interval(2 'mg', 4 'mg')
define Equal_EqualDates: Interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2013, 1, 1, 0, 0, 0, 0)) = Interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2013, 1, 1, 0, 0, 0, 0))
define Equal_EqualDatesOpenClosed: Interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2013, 1, 1, 0, 0, 0, 0)) = Interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2012, 12, 31, 23, 59, 59, 999)]
define Equal_SameDays: Interval[DateTime(2012, 1, 1), DateTime(2013, 1, 1)) = Interval[DateTime(2012, 1, 1), DateTime(2013, 1, 1))
define Equal_DifferentDays: Interval[DateTime(2012, 1, 1), DateTime(2013, 1, 1)) = Interval[DateTime(2012, 1, 1), DateTime(2012, 7, 1))
define Equal_DifferingPrecision: Interval[DateTime(2012, 1, 1, 12, 30, 21), DateTime(2013, 1, 1, 12, 30, 21)] = Interval[DateTime(2012, 1, 1, 12, 30), DateTime(2013, 1, 1, 12, 30)]

define test_Equal_EqualClosed: TestMessage(Equal_EqualClosed, 'Equal_EqualClosed', toString(true), toString(Equal_EqualClosed))
define test_Equal_EqualOpen: TestMessage(Equal_EqualOpen, 'Equal_EqualOpen', toString(true), toString(Equal_EqualOpen))
define test_Equal_EqualOpenClosed: TestMessage(Equal_EqualOpenClosed, 'Equal_EqualOpenClosed', toString(true), toString(Equal_EqualOpenClosed))
define test_Equal_UnequalClosed: TestMessage(not Equal_UnequalClosed, 'Equal_UnequalClosed', toString(false), toString(Equal_UnequalClosed))
define test_Equal_UnequalOpen: TestMessage(not Equal_UnequalOpen, 'Equal_UnequalOpen', toString(false), toString(Equal_UnequalOpen))
define test_Equal_UnequalClosedOpen: TestMessage(not Equal_UnequalClosedOpen, 'Equal_UnequalClosedOpen', toString(false), toString(Equal_UnequalClosedOpen))
define test_Equal_EqualQuantityClosed: TestMessage(Equal_EqualQuantityClosed, 'Equal_EqualQuantityClosed', toString(true), toString(Equal_EqualQuantityClosed))
define test_Equal_EqualQuantityOpen: TestMessage(Equal_EqualQuantityOpen, 'Equal_EqualQuantityOpen', toString(true), toString(Equal_EqualQuantityOpen))
define test_Equal_EqualQuantityOpenClosed: TestMessage(Equal_EqualQuantityOpenClosed, 'Equal_EqualQuantityOpenClosed', toString(true), toString(Equal_EqualQuantityOpenClosed))
define test_Equal_UnequalQuantityClosed: TestMessage(not Equal_UnequalQuantityClosed, 'Equal_UnequalQuantityClosed', toString(false), toString(Equal_UnequalQuantityClosed))
define test_Equal_UnequalQuantityOpen: TestMessage(not Equal_UnequalQuantityOpen, 'Equal_UnequalQuantityOpen', toString(false), toString(Equal_UnequalQuantityOpen))
define test_Equal_UnequalQuantityClosedOpen: TestMessage(not Equal_UnequalQuantityClosedOpen, 'Equal_UnequalQuantityClosedOpen', toString(false), toString(Equal_UnequalQuantityClosedOpen))
define test_Equal_EqualDates: TestMessage(Equal_EqualDates, 'Equal_EqualDates', toString(true), toString(Equal_EqualDates))
define test_Equal_EqualDatesOpenClosed: TestMessage(Equal_EqualDatesOpenClosed, 'Equal_EqualDatesOpenClosed', toString(true), toString(Equal_EqualDatesOpenClosed))
define test_Equal_SameDays: TestMessage(Equal_SameDays, 'Equal_SameDays', toString(true), toString(Equal_SameDays))
define test_Equal_DifferentDays: TestMessage(not Equal_DifferentDays, 'Equal_DifferentDays', toString(false), toString(Equal_DifferentDays))
define test_Equal_DifferingPrecision: TestMessage(Equal_DifferingPrecision is null, 'Equal_DifferingPrecision', 'null', toString(Equal_DifferingPrecision))

// NotEqual
define NotEqual_EqualClosed: Interval[1, 5] != Interval[1, 5]
define NotEqual_EqualOpen: Interval(1, 5) != Interval(1, 5)
define NotEqual_EqualOpenClosed: Interval(1, 5) != Interval[2, 4]
define NotEqual_UnequalClosed: Interval[1, 5] != Interval[2, 4]
define NotEqual_UnequalOpen: Interval(1, 5) != Interval(2, 4)
define NotEqual_UnequalClosedOpen: Interval[1, 5] != Interval(2, 4)
define NotEqual_EqualQuantityClosed: Interval[1 'mg', 5 'mg'] != Interval[1 'mg', 5 'mg']
define NotEqual_EqualQuantityOpen: Interval(1 'mg', 5 'mg') != Interval(1 'mg', 5 'mg')
define NotEqual_EqualQuantityOpenClosed: Interval(1 'mg', 5 'mg') != Interval[1.00000001 'mg', 4.99999999 'mg']
define NotEqual_UnequalQuantityClosed: Interval[1 'mg', 5 'mg'] != Interval[2 'mg', 4 'mg']
define NotEqual_UnequalQuantityOpen: Interval(1 'mg', 5 'mg') != Interval(2 'mg', 4 'mg')
define NotEqual_UnequalQuantityClosedOpen: Interval[1 'mg', 5 'mg'] != Interval(2 'mg', 4 'mg')
define NotEqual_EqualDates: Interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2013, 1, 1, 0, 0, 0, 0)) != Interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2013, 1, 1, 0, 0, 0, 0))
define NotEqual_EqualDatesOpenClosed: Interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2013, 1, 1, 0, 0, 0, 0)) != Interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2012, 12, 31, 23, 59, 59, 999)]
define NotEqual_SameDays: Interval[DateTime(2012, 1, 1), DateTime(2013, 1, 1)) != Interval[DateTime(2012, 1, 1), DateTime(2013, 1, 1))
define NotEqual_DifferentDays: Interval[DateTime(2012, 1, 1), DateTime(2013, 1, 1)) != Interval[DateTime(2012, 1, 1), DateTime(2012, 7, 1))
define NotEqual_DifferingPrecision: Interval[DateTime(2012, 1, 1, 12, 30, 21), DateTime(2013, 1, 1, 12, 30, 21)] != Interval[DateTime(2012, 1, 1, 12, 30), DateTime(2013, 1, 1, 12, 30)]

define test_NotEqual_EqualClosed: TestMessage(not NotEqual_EqualClosed, 'NotEqual_EqualClosed', toString(false), toString(NotEqual_EqualClosed))
define test_NotEqual_EqualOpen: TestMessage(not NotEqual_EqualOpen, 'NotEqual_EqualOpen', toString(false), toString(NotEqual_EqualOpen))
define test_NotEqual_EqualOpenClosed: TestMessage(not NotEqual_EqualOpenClosed, 'NotEqual_EqualOpenClosed', toString(false), toString(NotEqual_EqualOpenClosed))
define test_NotEqual_UnequalClosed: TestMessage(NotEqual_UnequalClosed, 'NotEqual_UnequalClosed', toString(true), toString(NotEqual_UnequalClosed))
define test_NotEqual_UnequalOpen: TestMessage(NotEqual_UnequalOpen, 'NotEqual_UnequalOpen', toString(true), toString(NotEqual_UnequalOpen))
define test_NotEqual_UnequalClosedOpen: TestMessage(NotEqual_UnequalClosedOpen, 'NotEqual_UnequalClosedOpen', toString(true), toString(NotEqual_UnequalClosedOpen))
define test_NotEqual_EqualQuantityClosed: TestMessage(not NotEqual_EqualQuantityClosed, 'NotEqual_EqualQuantityClosed', toString(false), toString(NotEqual_EqualQuantityClosed))
define test_NotEqual_EqualQuantityOpen: TestMessage(not NotEqual_EqualQuantityOpen, 'NotEqual_EqualQuantityOpen', toString(false), toString(NotEqual_EqualQuantityOpen))
define test_NotEqual_EqualQuantityOpenClosed: TestMessage(not NotEqual_EqualQuantityOpenClosed, 'NotEqual_EqualQuantityOpenClosed', toString(false), toString(NotEqual_EqualQuantityOpenClosed))
define test_NotEqual_UnequalQuantityClosed: TestMessage(NotEqual_UnequalQuantityClosed, 'NotEqual_UnequalQuantityClosed', toString(true), toString(NotEqual_UnequalQuantityClosed))
define test_NotEqual_UnequalQuantityOpen: TestMessage(NotEqual_UnequalQuantityOpen, 'NotEqual_UnequalQuantityOpen', toString(true), toString(NotEqual_UnequalQuantityOpen))
define test_NotEqual_UnequalQuantityClosedOpen: TestMessage(NotEqual_UnequalQuantityClosedOpen, 'NotEqual_UnequalQuantityClosedOpen', toString(true), toString(NotEqual_UnequalQuantityClosedOpen))
define test_NotEqual_EqualDates: TestMessage(not NotEqual_EqualDates, 'NotEqual_EqualDates', toString(false), toString(NotEqual_EqualDates))
define test_NotEqual_EqualDatesOpenClosed: TestMessage(not NotEqual_EqualDatesOpenClosed, 'NotEqual_EqualDatesOpenClosed', toString(false), toString(NotEqual_EqualDatesOpenClosed))
define test_NotEqual_SameDays: TestMessage(not NotEqual_SameDays, 'NotEqual_SameDays', toString(false), toString(NotEqual_SameDays))
define test_NotEqual_DifferentDays: TestMessage(NotEqual_DifferentDays, 'NotEqual_DifferentDays', toString(true), toString(NotEqual_DifferentDays))
define test_NotEqual_DifferingPrecision: TestMessage(NotEqual_DifferingPrecision is null, 'NotEqual_DifferingPrecision', 'null', toString(NotEqual_DifferingPrecision))

// Contains
define Contains_ContainsInt: Interval[1, 5] contains 3
define Contains_NotContainsInt: Interval(1, 5] contains 1
define Contains_ContainsReal: Interval[1.234, 3.456] contains 2.345
define Contains_NotContainsReal: Interval[1.234, 3.456] contains 4.567
define Contains_ContainsQuantity: Interval[1 'mg', 5 'mg'] contains 3 'mg'
define Contains_ContainsQuantityInclusiveEdge: Interval[1 'mg', 5 'mg'] contains 5 'mg'
define Contains_NotContainsQuantity: Interval[2 'mg', 5 'mg'] contains 1 'mg'
define Contains_NotContainsQuantityExclusiveEdge: Interval(1 'mg', 5 'mg'] contains 1 'mg'
define Contains_ContainsDate: DateIvl contains DateTime(2012, 6, 1, 0, 0, 0, 0)
define Contains_NotContainsDate: DateIvl contains DateTime(2012, 9, 1, 0, 0, 0, 0)
define Contains_ContainsImpreciseDate: DateIvl contains DateTime(2012, 4)
define Contains_NotContainsImpreciseDate: DateIvl contains DateTime(2012, 9)
define Contains_MayContainImpreciseDate: DateIvl contains DateTime(2012)
define Contains_ContainsDayOfDateLowEdge: PrecisionDateIvl contains day of DateTime(2012, 3, 2, 0, 0, 0, 0)
define Contains_ContainsDayOfDateHighEdge: PrecisionDateIvl contains day of DateTime(2012, 9, 2, 23, 59, 59, 999)
define Contains_NotContainsDayOfDateLowEdge: PrecisionDateIvl contains day of DateTime(2012, 3, 1, 23, 59, 59, 999)
define Contains_NotContainsDayOfDateHighEdge: PrecisionDateIvl contains day of DateTime(2012, 9, 3, 0, 0, 0, 0)
define Contains_ContainsDayOfDateImpreciseLowEdge: PrecisionDateIvl contains day of DateTime(2012, 3, 2)
define Contains_ContainsDayOfDateImpreciseHighEdge: PrecisionDateIvl contains day of DateTime(2012, 9, 2)
define Contains_ContainsDayOfDateVeryImpreciseMiddle: PrecisionDateIvl contains day of DateTime(2012, 6)
define Contains_NotContainsDayOfDateVeryImpreciseLow: PrecisionDateIvl contains day of DateTime(2012, 2)
define Contains_NotContainsDayOfDateVeryImpreciseHigh: PrecisionDateIvl contains day of DateTime(2012, 10)
define Contains_MayContainDayOfDateVeryImpreciseLow: PrecisionDateIvl contains day of DateTime(2012, 3)
define Contains_MayContainDayOfDateVeryImpreciseHigh: PrecisionDateIvl contains day of DateTime(2012, 9)
define Contains_MayContainDayOfDateVeryImpreciseSurrounding: PrecisionDateIvl contains day of DateTime(2012)
define Contains_ImpreciseContainsDate: ImpDateIvl contains DateTime(2012, 6, 1, 0, 0, 0, 0)
define Contains_ImpreciseNotContainsDate: ImpDateIvl contains DateTime(2012, 9, 1, 0, 0, 0, 0)
define Contains_ImpreciseMayContainDate: ImpDateIvl contains DateTime(2012, 3, 15, 0, 0, 0, 0)
define Contains_NegInfBegContainsInt: Interval[null, 5] contains -7
define Contains_NegInfBegNotContainsInt: Interval[null, 5] contains 7
define Contains_UnknownBegContainsInt: Interval(null, 5] contains 5
define Contains_UnknownBegMayContainInt: Interval(null, 5] contains -7
define Contains_UnknownBegNotContainsInt: Interval(null, 5] contains 7
define Contains_PosInfEndContainsInt: Interval[0, null] contains 123456789
define Contains_PosInfEndNotContainsInt: Interval[0, null] contains -1
define Contains_UnknownEndContainsInt: Interval[0, null) contains 0
define Contains_UnknownEndMayContainInt: Interval[0, null) contains 123456789
define Contains_UnknownEndNotContainsInt: Interval[0, null) contains -1
define Contains_NegInfBegContainsDate: Interval[null, DateTime(2013, 1, 1, 0, 0, 0, 0)) contains DateTime(1, 1, 1, 0, 0, 0, 0)
define Contains_NegInfBegNotContainsDate: Interval[null, DateTime(2013, 1, 1, 0, 0, 0, 0)) contains DateTime(2013, 1, 2, 0, 0, 0, 0)
define Contains_UnknownBegContainsDate: Interval(null, DateTime(2013, 1, 1, 0, 0, 0, 0)) contains DateTime(2012, 12, 31, 23, 59, 59, 999)
define Contains_UnknownBegMayContainDate: Interval(null, DateTime(2013, 1, 1, 0, 0, 0, 0)) contains DateTime(1, 1, 1, 0, 0, 0, 0)
define Contains_UnknownBegNotContainsDate: Interval(null, DateTime(2013, 1, 1, 0, 0, 0, 0)) contains DateTime(2013, 1, 2, 0, 0, 0, 0)
define Contains_PosInfEndContainsDate: Interval[DateTime(2013, 1, 1, 0, 0, 0, 0), null] contains DateTime(2014, 1, 1, 0, 0, 0, 0)
define Contains_PosInfEndNotContainsDate: Interval[DateTime(2013, 1, 1, 0, 0, 0, 0), null] contains DateTime(2012, 1, 1, 0, 0, 0, 0)
define Contains_UnknownEndContainsDate: Interval[DateTime(2013, 1, 1, 0, 0, 0, 0), null) contains DateTime(2013, 1, 1, 0, 0, 0, 0)
define Contains_UnknownEndMayContainDate: Interval[DateTime(2013, 1, 1, 0, 0, 0, 0), null) contains DateTime(2014, 1, 1, 0, 0, 0, 0)
define Contains_UnknownEndNotContainsDate: Interval[DateTime(2013, 1, 1, 0, 0, 0, 0), null) contains DateTime(2012, 1, 1, 0, 0, 0, 0)

define test_Contains_ContainsInt: TestMessage(Contains_ContainsInt, 'Contains_ContainsInt', toString(true), toString(Contains_ContainsInt))
define test_Contains_NotContainsInt: TestMessage(not Contains_NotContainsInt, 'Contains_NotContainsInt', toString(false), toString(Contains_NotContainsInt))
define test_Contains_ContainsReal: TestMessage(Contains_ContainsReal, 'Contains_ContainsReal', toString(true), toString(Contains_ContainsReal))
define test_Contains_NotContainsReal: TestMessage(not Contains_NotContainsReal, 'Contains_NotContainsReal', toString(false), toString(Contains_NotContainsReal))
define test_Contains_ContainsQuantity: TestMessage(Contains_ContainsQuantity, 'Contains_ContainsQuantity', toString(true), toString(Contains_ContainsQuantity))
define test_Contains_ContainsQuantityInclusiveEdge: TestMessage(Contains_ContainsQuantityInclusiveEdge, 'Contains_ContainsQuantityInclusiveEdge', toString(true), toString(Contains_ContainsQuantityInclusiveEdge))
define test_Contains_NotContainsQuantityExclusiveEdge: TestMessage(not Contains_NotContainsQuantityExclusiveEdge, 'Contains_NotContainsQuantityExclusiveEdge', toString(false), toString(Contains_NotContainsQuantityExclusiveEdge))
define test_Contains_ContainsDate: TestMessage(Contains_ContainsDate, 'Contains_ContainsDate', toString(true), toString(Contains_ContainsDate))
define test_Contains_NotContainsDate: TestMessage(not Contains_NotContainsDate, 'Contains_NotContainsDate', toString(false), toString(Contains_NotContainsDate))
define test_Contains_ContainsImpreciseDate: TestMessage(Contains_ContainsImpreciseDate, 'Contains_ContainsImpreciseDate', toString(true), toString(Contains_ContainsImpreciseDate))
define test_Contains_NotContainsImpreciseDate: TestMessage(not Contains_NotContainsImpreciseDate, 'Contains_NotContainsImpreciseDate', toString(false), toString(Contains_NotContainsImpreciseDate))
define test_Contains_MayContainImpreciseDate: TestMessage(Contains_MayContainImpreciseDate is null, 'Contains_MayContainImpreciseDate', 'null', toString(Contains_MayContainImpreciseDate))
define test_Contains_ContainsDayOfDateLowEdge: TestMessage(Contains_ContainsDayOfDateLowEdge, 'Contains_ContainsDayOfDateLowEdge', toString(true), toString(Contains_ContainsDayOfDateLowEdge))
define test_Contains_ContainsDayOfDateHighEdge: TestMessage(Contains_ContainsDayOfDateHighEdge, 'Contains_ContainsDayOfDateHighEdge', toString(true), toString(Contains_ContainsDayOfDateHighEdge))
define test_Contains_NotContainsDayOfDateLowEdge: TestMessage(not Contains_NotContainsDayOfDateLowEdge, 'Contains_NotContainsDayOfDateLowEdge', toString(false), toString(Contains_NotContainsDayOfDateLowEdge))
define test_Contains_NotContainsDayOfDateHighEdge: TestMessage(not Contains_NotContainsDayOfDateHighEdge, 'Contains_NotContainsDayOfDateHighEdge', toString(false), toString(Contains_NotContainsDayOfDateHighEdge))
define test_Contains_ContainsDayOfDateImpreciseLowEdge: TestMessage(Contains_ContainsDayOfDateImpreciseLowEdge, 'Contains_ContainsDayOfDateImpreciseLowEdge', toString(true), toString(Contains_ContainsDayOfDateImpreciseLowEdge))
define test_Contains_ContainsDayOfDateImpreciseHighEdge: TestMessage(Contains_ContainsDayOfDateImpreciseHighEdge, 'Contains_ContainsDayOfDateImpreciseHighEdge', toString(true), toString(Contains_ContainsDayOfDateImpreciseHighEdge))
define test_Contains_ContainsDayOfDateVeryImpreciseMiddle: TestMessage(Contains_ContainsDayOfDateVeryImpreciseMiddle, 'Contains_ContainsDayOfDateVeryImpreciseMiddle', toString(true), toString(Contains_ContainsDayOfDateVeryImpreciseMiddle))
define test_Contains_NotContainsDayOfDateVeryImpreciseLow: TestMessage(not Contains_NotContainsDayOfDateVeryImpreciseLow, 'Contains_NotContainsDayOfDateVeryImpreciseLow', toString(false), toString(Contains_NotContainsDayOfDateVeryImpreciseLow))
define test_Contains_NotContainsDayOfDateVeryImpreciseHigh: TestMessage(not Contains_NotContainsDayOfDateVeryImpreciseHigh, 'Contains_NotContainsDayOfDateVeryImpreciseHigh', toString(false), toString(Contains_NotContainsDayOfDateVeryImpreciseHigh))
define test_Contains_MayContainDayOfDateVeryImpreciseLow: TestMessage(Contains_MayContainDayOfDateVeryImpreciseLow is null, 'Contains_MayContainDayOfDateVeryImpreciseLow', 'null', toString(Contains_MayContainDayOfDateVeryImpreciseLow))
define test_Contains_MayContainDayOfDateVeryImpreciseHigh: TestMessage(Contains_MayContainDayOfDateVeryImpreciseHigh is null, 'Contains_MayContainDayOfDateVeryImpreciseHigh', 'null', toString(Contains_MayContainDayOfDateVeryImpreciseHigh))
define test_Contains_MayContainDayOfDateVeryImpreciseSurrounding: TestMessage(Contains_MayContainDayOfDateVeryImpreciseSurrounding is null, 'Contains_MayContainDayOfDateVeryImpreciseSurrounding', 'null', toString(Contains_MayContainDayOfDateVeryImpreciseSurrounding))
define test_Contains_ImpreciseContainsDate: TestMessage(Contains_ImpreciseContainsDate, 'Contains_ImpreciseContainsDate', toString(true), toString(Contains_ImpreciseContainsDate))
define test_Contains_ImpreciseNotContainsDate: TestMessage(not Contains_ImpreciseNotContainsDate, 'Contains_ImpreciseNotContainsDate', toString(false), toString(Contains_ImpreciseNotContainsDate))
define test_Contains_ImpreciseMayContainDate: TestMessage(Contains_ImpreciseMayContainDate is null, 'Contains_ImpreciseMayContainDate', 'null', toString(Contains_ImpreciseMayContainDate))
define test_Contains_NegInfBegContainsInt: TestMessage(Contains_NegInfBegContainsInt, 'Contains_NegInfBegContainsInt', toString(true), toString(Contains_NegInfBegContainsInt))
define test_Contains_NegInfBegNotContainsInt: TestMessage(not Contains_NegInfBegNotContainsInt, 'Contains_NegInfBegNotContainsInt', toString(false), toString(Contains_NegInfBegNotContainsInt))
define test_Contains_UnknownBegContainsDate: TestMessage(Contains_UnknownBegContainsDate, 'Contains_UnknownBegContainsDate', toString(true), toString(Contains_UnknownBegContainsDate))
define test_Contains_UnknownBegMayContainDate: TestMessage(Contains_UnknownBegMayContainDate is null, 'Contains_UnknownBegMayContainDate', 'null', toString(Contains_UnknownBegMayContainDate))
define test_Contains_UnknownBegNotContainsDate: TestMessage(not Contains_UnknownBegNotContainsDate, 'Contains_UnknownBegNotContainsDate', toString(false), toString(Contains_UnknownBegNotContainsDate))
define test_Contains_PosInfEndContainsDate: TestMessage(Contains_PosInfEndContainsDate, 'Contains_PosInfEndContainsDate', toString(true), toString(Contains_PosInfEndContainsDate))
define test_Contains_PosInfEndNotContainsDate: TestMessage(not Contains_PosInfEndNotContainsDate, 'Contains_PosInfEndNotContainsDate', toString(false), toString(Contains_PosInfEndNotContainsDate))
define test_Contains_UnknownEndContainsDate: TestMessage(Contains_UnknownEndContainsDate, 'Contains_UnknownEndContainsDate', toString(true), toString(Contains_UnknownEndContainsDate))
define test_Contains_UnknownEndMayContainDate: TestMessage(Contains_UnknownEndMayContainDate is null, 'Contains_UnknownEndMayContainDate', 'null', toString(Contains_UnknownEndMayContainDate))
define test_Contains_UnknownEndNotContainsDate: TestMessage(not Contains_UnknownEndNotContainsDate, 'Contains_UnknownEndNotContainsDate', toString(false), toString(Contains_UnknownEndNotContainsDate))

// In
define In_ContainsInt: 3 in Interval[1, 5]
define In_NotContainsInt: 1 in Interval(1, 5]
define In_ContainsReal: 2.345 in Interval[1.234, 3.456]
define In_NotContainsReal: 4.567 in Interval[1.234, 3.456]
define In_ContainsQuantity: 5 'mg' in Interval[4 'mg', 6 'mg']
define In_ContainsQuantityInclusiveEdge: 6 'mg' in Interval[4 'mg', 6 'mg']
define In_NotContainsQuantity: 3 'mg' in Interval[4 'mg', 6 'mg']
define In_NotContainsQuantityExclusiveEdge: 6 'mg' in Interval[4 'mg', 6 'mg')
define In_ContainsDate: DateTime(2012, 6, 1, 0, 0, 0, 0) in DateIvl
define In_NotContainsDate: DateTime(2012, 9, 1, 0, 0, 0, 0) in DateIvl
define In_ContainsImpreciseDate: DateTime(2012, 4) in DateIvl
define In_NotContainsImpreciseDate: DateTime(2012, 9) in DateIvl
define In_MayContainImpreciseDate: DateTime(2012) in DateIvl
define In_ContainsDayOfDateLowEdge: DateTime(2012, 3, 2, 0, 0, 0, 0) in day of PrecisionDateIvl
define In_ContainsDayOfDateHighEdge: DateTime(2012, 9, 2, 23, 59, 59, 999) in day of PrecisionDateIvl
define In_NotContainsDayOfDateLowEdge: DateTime(2012, 3, 1, 23, 59, 59, 999) in day of PrecisionDateIvl
define In_NotContainsDayOfDateHighEdge: DateTime(2012, 9, 3, 0, 0, 0, 0) in day of PrecisionDateIvl
define In_ContainsDayOfDateImpreciseLowEdge: DateTime(2012, 3, 2) in day of PrecisionDateIvl
define In_ContainsDayOfDateImpreciseHighEdge: DateTime(2012, 9, 2) in day of PrecisionDateIvl
define In_ContainsDayOfDateVeryImpreciseMiddle: DateTime(2012, 6) in day of PrecisionDateIvl
define In_NotContainsDayOfDateVeryImpreciseLow: DateTime(2012, 2) in day of PrecisionDateIvl
define In_NotContainsDayOfDateVeryImpreciseHigh: DateTime(2012, 10) in day of PrecisionDateIvl
define In_MayContainDayOfDateVeryImpreciseLow: DateTime(2012, 3) in day of PrecisionDateIvl
define In_MayContainDayOfDateVeryImpreciseHigh: DateTime(2012, 9) in day of PrecisionDateIvl
define In_MayContainDayOfDateVeryImpreciseSurrounding: DateTime(2012) in day of PrecisionDateIvl
define In_ImpreciseContainsDate: DateTime(2012, 6, 1, 0, 0, 0, 0) in ImpDateIvl
define In_ImpreciseNotContainsDate: DateTime(2012, 9, 1, 0, 0, 0, 0) in ImpDateIvl
define In_ImpreciseMayContainDate: DateTime(2012, 3, 15, 0, 0, 0, 0) in ImpDateIvl
define In_NegInfBegContainsInt: -7 in Interval[null, 5]
define In_NegInfBegNotContainsInt: 7 in Interval[null, 5]
define In_UnknownBegContainsInt: 5 in Interval(null, 5]
define In_UnknownBegMayContainInt: -7 in Interval(null, 5]
define In_UnknownBegNotContainsInt: 7 in Interval(null, 5]
define In_PosInfEndContainsInt: 123456789 in Interval[0, null]
define In_PosInfEndNotContainsInt: -1 in Interval[0, null]
define In_UnknownEndContainsInt: 0 in Interval[0, null)
define In_UnknownEndMayContainInt: 123456789 in Interval[0, null)
define In_UnknownEndNotContainsInt: -1 in Interval[0, null)
define In_NegInfBegContainsDate: DateTime(1, 1, 1, 0, 0, 0, 0) in Interval[null, DateTime(2013, 1, 1, 0, 0, 0, 0))
define In_NegInfBegNotContainsDate: DateTime(2013, 1, 2, 0, 0, 0, 0) in Interval[null, DateTime(2013, 1, 1, 0, 0, 0, 0))
define In_UnknownBegContainsDate: DateTime(2012, 12, 31, 23, 59, 59, 999) in Interval(null, DateTime(2013, 1, 1, 0, 0, 0, 0))
define In_UnknownBegMayContainDate: DateTime(1, 1, 1, 0, 0, 0, 0) in Interval(null, DateTime(2013, 1, 1, 0, 0, 0, 0))
define In_UnknownBegNotContainsDate: DateTime(2013, 1, 2, 0, 0, 0, 0) in Interval(null, DateTime(2013, 1, 1, 0, 0, 0, 0))
define In_PosInfEndContainsDate:  DateTime(2014, 1, 1, 0, 0, 0, 0) in Interval[DateTime(2013, 1, 1, 0, 0, 0, 0), null]
define In_PosInfEndNotContainsDate: DateTime(2012, 1, 1, 0, 0, 0, 0) in Interval[DateTime(2013, 1, 1, 0, 0, 0, 0), null]
define In_UnknownEndContainsDate: DateTime(2013, 1, 1, 0, 0, 0, 0) in Interval[DateTime(2013, 1, 1, 0, 0, 0, 0), null)
define In_UnknownEndMayContainDate: DateTime(2014, 1, 1, 0, 0, 0, 0) in Interval[DateTime(2013, 1, 1, 0, 0, 0, 0), null)
define In_UnknownEndNotContainsDate: DateTime(2012, 1, 1, 0, 0, 0, 0) in Interval[DateTime(2013, 1, 1, 0, 0, 0, 0), null)

define test_In_ContainsInt: TestMessage(In_ContainsInt, 'In_ContainsInt', toString(true), toString(In_ContainsInt))
define test_In_NotContainsInt: TestMessage(not In_NotContainsInt, 'In_NotContainsInt', toString(false), toString(In_NotContainsInt))
define test_In_ContainsReal: TestMessage(In_ContainsReal, 'In_ContainsReal', toString(true), toString(In_ContainsReal))
define test_In_NotContainsReal: TestMessage(not In_NotContainsReal, 'In_NotContainsReal', toString(false), toString(In_NotContainsReal))
define test_In_ContainsQuantity: TestMessage(In_ContainsQuantity, 'In_ContainsQuantity', toString(true), toString(In_ContainsQuantity))
define test_In_ContainsQuantityInclusiveEdge: TestMessage(In_ContainsQuantityInclusiveEdge, 'In_ContainsQuantityInclusiveEdge', toString(true), toString(In_ContainsQuantityInclusiveEdge))
define test_In_NotContainsQuantityExclusiveEdge: TestMessage(not In_NotContainsQuantityExclusiveEdge, 'In_NotContainsQuantityExclusiveEdge', toString(false), toString(In_NotContainsQuantityExclusiveEdge))
define test_In_ContainsDate: TestMessage(In_ContainsDate, 'In_ContainsDate', toString(true), toString(In_ContainsDate))
define test_In_NotContainsDate: TestMessage(not In_NotContainsDate, 'In_NotContainsDate', toString(false), toString(In_NotContainsDate))
define test_In_ContainsImpreciseDate: TestMessage(In_ContainsImpreciseDate, 'In_ContainsImpreciseDate', toString(true), toString(In_ContainsImpreciseDate))
define test_In_NotContainsImpreciseDate: TestMessage(not In_NotContainsImpreciseDate, 'In_NotContainsImpreciseDate', toString(false), toString(In_NotContainsImpreciseDate))
define test_In_MayContainImpreciseDate: TestMessage(In_MayContainImpreciseDate is null, 'In_MayContainImpreciseDate', 'null', toString(In_MayContainImpreciseDate))
define test_In_ContainsDayOfDateLowEdge: TestMessage(In_ContainsDayOfDateLowEdge, 'In_ContainsDayOfDateLowEdge', toString(true), toString(In_ContainsDayOfDateLowEdge))
define test_In_ContainsDayOfDateHighEdge: TestMessage(In_ContainsDayOfDateHighEdge, 'In_ContainsDayOfDateHighEdge', toString(true), toString(In_ContainsDayOfDateHighEdge))
define test_In_NotContainsDayOfDateLowEdge: TestMessage(not In_NotContainsDayOfDateLowEdge, 'In_NotContainsDayOfDateLowEdge', toString(false), toString(In_NotContainsDayOfDateLowEdge))
define test_In_NotContainsDayOfDateHighEdge: TestMessage(not In_NotContainsDayOfDateHighEdge, 'In_NotContainsDayOfDateHighEdge', toString(false), toString(In_NotContainsDayOfDateHighEdge))
define test_In_ContainsDayOfDateImpreciseLowEdge: TestMessage(In_ContainsDayOfDateImpreciseLowEdge, 'In_ContainsDayOfDateImpreciseLowEdge', toString(true), toString(In_ContainsDayOfDateImpreciseLowEdge))
define test_In_ContainsDayOfDateImpreciseHighEdge: TestMessage(In_ContainsDayOfDateImpreciseHighEdge, 'In_ContainsDayOfDateImpreciseHighEdge', toString(true), toString(In_ContainsDayOfDateImpreciseHighEdge))
define test_In_ContainsDayOfDateVeryImpreciseMiddle: TestMessage(In_ContainsDayOfDateVeryImpreciseMiddle, 'In_ContainsDayOfDateVeryImpreciseMiddle', toString(true), toString(In_ContainsDayOfDateVeryImpreciseMiddle))
define test_In_NotContainsDayOfDateVeryImpreciseLow: TestMessage(not In_NotContainsDayOfDateVeryImpreciseLow, 'In_NotContainsDayOfDateVeryImpreciseLow', toString(false), toString(In_NotContainsDayOfDateVeryImpreciseLow))
define test_In_NotContainsDayOfDateVeryImpreciseHigh: TestMessage(not In_NotContainsDayOfDateVeryImpreciseHigh, 'In_NotContainsDayOfDateVeryImpreciseHigh', toString(false), toString(In_NotContainsDayOfDateVeryImpreciseHigh))
define test_In_MayContainDayOfDateVeryImpreciseLow: TestMessage(In_MayContainDayOfDateVeryImpreciseLow is null, 'In_MayContainDayOfDateVeryImpreciseLow', 'null', toString(In_MayContainDayOfDateVeryImpreciseLow))
define test_In_MayContainDayOfDateVeryImpreciseHigh: TestMessage(In_MayContainDayOfDateVeryImpreciseHigh is null, 'In_MayContainDayOfDateVeryImpreciseHigh', 'null', toString(In_MayContainDayOfDateVeryImpreciseHigh))
define test_In_MayContainDayOfDateVeryImpreciseSurrounding: TestMessage(In_MayContainDayOfDateVeryImpreciseSurrounding is null, 'In_MayContainDayOfDateVeryImpreciseSurrounding', 'null', toString(In_MayContainDayOfDateVeryImpreciseSurrounding))
define test_In_ImpreciseContainsDate: TestMessage(In_ImpreciseContainsDate, 'In_ImpreciseContainsDate', toString(true), toString(In_ImpreciseContainsDate))
define test_In_ImpreciseNotContainsDate: TestMessage(not In_ImpreciseNotContainsDate, 'In_ImpreciseNotContainsDate', toString(false), toString(In_ImpreciseNotContainsDate))
define test_In_ImpreciseMayContainDate: TestMessage(In_ImpreciseMayContainDate is null, 'In_ImpreciseMayContainDate', 'null', toString(In_ImpreciseMayContainDate))
define test_In_NegInfBegContainsInt: TestMessage(In_NegInfBegContainsInt, 'In_NegInfBegContainsInt', toString(true), toString(In_NegInfBegContainsInt))
define test_In_NegInfBegNotContainsInt: TestMessage(not In_NegInfBegNotContainsInt, 'In_NegInfBegNotContainsInt', toString(false), toString(In_NegInfBegNotContainsInt))
define test_In_UnknownBegContainsDate: TestMessage(In_UnknownBegContainsDate, 'In_UnknownBegContainsDate', toString(true), toString(In_UnknownBegContainsDate))
define test_In_UnknownBegMayContainDate: TestMessage(In_UnknownBegMayContainDate is null, 'In_UnknownBegMayContainDate', 'null', toString(In_UnknownBegMayContainDate))
define test_In_UnknownBegNotContainsDate: TestMessage(not In_UnknownBegNotContainsDate, 'In_UnknownBegNotContainsDate', toString(false), toString(In_UnknownBegNotContainsDate))
define test_In_PosInfEndContainsDate: TestMessage(In_PosInfEndContainsDate, 'In_PosInfEndContainsDate', toString(true), toString(In_PosInfEndContainsDate))
define test_In_PosInfEndNotContainsDate: TestMessage(not In_PosInfEndNotContainsDate, 'In_PosInfEndNotContainsDate', toString(false), toString(In_PosInfEndNotContainsDate))
define test_In_UnknownEndContainsDate: TestMessage(In_UnknownEndContainsDate, 'In_UnknownEndContainsDate', toString(true), toString(In_UnknownEndContainsDate))
define test_In_UnknownEndMayContainDate: TestMessage(In_UnknownEndMayContainDate is null, 'In_UnknownEndMayContainDate', 'null', toString(In_UnknownEndMayContainDate))
define test_In_UnknownEndNotContainsDate: TestMessage(not In_UnknownEndNotContainsDate, 'In_UnknownEndNotContainsDate', toString(false), toString(In_UnknownEndNotContainsDate))

// Includes
define Includes_IncludesIntIvl: Interval[1, 5] includes Interval[1, 4]
define Includes_NotIncludesIntIvl: Interval(1, 5] includes Interval[1, 4]
define Includes_IncludesRealIvl: Interval[1.234, 3.456] includes Interval[2.34, 2.56]
define Includes_NotIncludesRealIvl: Interval[1.234, 3.456] includes Interval[1.23, 2.56]
define Includes_IncludesDateIvl: DateIvl includes Interval[DateTime(2012, 4, 1, 0, 0, 0, 0), DateTime(2012, 6, 1, 0, 0, 0, 0))
define Includes_NotIncludesDateIvl: DateIvl includes Interval[DateTime(2012, 6, 1, 0, 0, 0, 0), DateTime(2012, 9, 1, 0, 0, 0, 0)]
define Includes_IncludesImpreciseDateIvl: DateIvl includes Interval[DateTime(2012, 4), DateTime(2012, 6)]
define Includes_NotIncludesImpreciseDateIvl: DateIvl includes Interval[DateTime(2012, 4), DateTime(2012, 9)]
define Includes_MayIncludeImpreciseDateIvl: DateIvl includes Interval[DateTime(2012), DateTime(2012)]
define Includes_IncludesDayOfIvlLowEdge: PrecisionDateIvl includes day of Interval[DateTime(2012, 3, 2, 0, 0, 0, 0), DateTime(2012, 6, 1, 0, 0, 0, 0))
define Includes_IncludesDayOfIvlHighEdge: PrecisionDateIvl includes day of Interval[DateTime(2012, 6, 1, 0, 0, 0, 0), DateTime(2012, 9, 2, 23, 59, 59, 999))
define Includes_NotIncludesDayOfIvlLowEdge: PrecisionDateIvl includes day of Interval[DateTime(2012, 3, 1, 0, 0, 0, 0), DateTime(2012, 6, 1, 0, 0, 0, 0)]
define Includes_NotIncludesDayOfIvlHighEdge: PrecisionDateIvl includes day of Interval[DateTime(2012, 6, 1, 0, 0, 0, 0), DateTime(2012, 9, 3, 0, 0, 0, 0)]
define Includes_IncludesDayOfIvlImpreciseLowEdge: PrecisionDateIvl includes day of Interval[DateTime(2012, 3, 2), DateTime(2012, 6, 1)]
define Includes_IncludesDayOfIvlImpreciseHighEdge: PrecisionDateIvl includes day of Interval[DateTime(2012, 6, 1), DateTime(2012, 9, 2)]
define Includes_IncludesDayOfIvlVeryImpreciseMiddle: PrecisionDateIvl includes day of Interval[DateTime(2012, 5), DateTime(2012, 6)]
define Includes_NotIncludesDayOfIvlVeryImpreciseLow: PrecisionDateIvl includes day of Interval[DateTime(2012, 2), DateTime(2012, 6)]
define Includes_NotIncludesDayOfIvlVeryImpreciseHigh: PrecisionDateIvl includes day of Interval[DateTime(2012, 6), DateTime(2012, 10)]
define Includes_MayIncludeDayOfIvlVeryImpreciseLow: PrecisionDateIvl includes day of Interval[DateTime(2012, 3), DateTime(2012, 6)]
define Includes_MayIncludeDayOfIvlVeryImpreciseHigh: PrecisionDateIvl includes day of Interval[DateTime(2012, 6), DateTime(2012, 9)]
define Includes_MayIncludeDayOfIvlVeryImpreciseSurrounding: PrecisionDateIvl includes day of Interval[DateTime(2012), DateTime(2012)]
define Includes_ImpreciseIncludesDateIvl: ImpDateIvl includes Interval[DateTime(2012, 4, 1, 0, 0, 0, 0), DateTime(2012, 6, 1, 0, 0, 0, 0))
define Includes_ImpreciseIncludesDate: ImpDateIvl includes DateTime(2012, 4, 1, 0, 0, 0, 0)
define Includes_ImpreciseDoesntIncludeDate: ImpDateIvl includes DateTime(2016, 4, 1, 0, 0, 0, 0)
define Includes_IntervalIncludesQuantity: Interval[4 'mg', 6 'mg'] includes 5 'mg'
define Includes_IntervalDoesntIncludeQuantity: Interval[4 'mg', 6 'mg'] includes 50 'mg'
define Includes_ImpreciseNotIncludesDateIvl: ImpDateIvl includes Interval[DateTime(2012, 2, 1, 0, 0, 0, 0), DateTime(2012, 6, 1, 0, 0, 0, 0))
define Includes_ImpreciseMayIncludeDateIvl: ImpDateIvl includes Interval[DateTime(2012, 3, 15, 0, 0, 0, 0), DateTime(2012, 6, 1, 0, 0, 0, 0))
define Includes_NegInfBegIncludesIntIvl: Interval[null, 5] includes Interval[-7, 2]
define Includes_NegInfBegNotIncludesIntIvl: Interval[null, 5] includes Interval[4, 7]
define Includes_UnknownBegIncludesIntIvl: Interval(null, 5] includes Interval[5, 5]
define Includes_UnknownBegMayIncludeIntIvl: Interval(null, 5] includes Interval[-7, 2]
define Includes_UnknownBegNotIncludesIntIvl: Interval(null, 5] includes Interval[4, 7]
define Includes_PosInfEndIncludesIntIvl: Interval[0, null] includes Interval[1234, 5678]
define Includes_PosInfEndNotIncludesIntIvl: Interval[0, null] includes Interval[-1234, 5678]
define Includes_UnknownEndIncludesIntIvl: Interval[0, null) includes Interval[0, 0]
define Includes_UnknownEndMayIncludeIntIvl: Interval[0, null) includes Interval[1234, 5678]
define Includes_UnknownEndNotIncludesIntIvl: Interval[0, null) includes Interval[-1234, 5678]
define Includes_NegInfBegIncludesDateIvl: Interval[null, DateTime(2013, 1, 1, 0, 0, 0, 0)) includes Interval[DateTime(1, 1, 1, 0, 0, 0, 0, 0), DateTime(2000, 1, 1, 0, 0, 0, 0)]
define Includes_NegInfBegNotIncludesDateIvl: Interval[null, DateTime(2013, 1, 1, 0, 0, 0, 0)) includes Interval[DateTime(1, 1, 1, 0, 0, 0, 0, 0), DateTime(2013, 1, 1, 0, 0, 0, 0)]
define Includes_UnknownBegIncludesDateIvl: Interval(null, DateTime(2013, 1, 1, 0, 0, 0, 0)) includes Interval[DateTime(2012, 12, 31, 23, 59, 59, 999), DateTime(2012, 12, 31, 23, 59, 59, 999)]
define Includes_UnknownBegMayIncludeDateIvl: Interval(null, DateTime(2013, 1, 1, 0, 0, 0, 0)) includes Interval[DateTime(1, 1, 1, 0, 0, 0, 0), DateTime(2000, 1, 1, 0, 0, 0, 0)]
define Includes_UnknownBegNotIncludesDateIvl: Interval(null, DateTime(2013, 1, 1, 0, 0, 0, 0)) includes Interval[DateTime(1, 1, 1, 0, 0, 0, 0), DateTime(2013, 1, 1, 0, 0, 0, 0)]
define Includes_PosInfEndIncludesDateIvl: Interval[DateTime(2013, 1, 1, 0, 0, 0, 0), null] includes Interval[DateTime(2014, 1, 1, 0, 0, 0, 0), DateTime(2015, 1, 1, 0, 0, 0, 0)]
define Includes_PosInfEndNotIncludesDateIvl: Interval[DateTime(2013, 1, 1, 0, 0, 0, 0), null] includes Interval[DateTime(2000, 1, 1, 0, 0, 0, 0), DateTime(2014, 1, 1, 0, 0, 0, 0)]
define Includes_UnknownEndIncludesDateIvl: Interval[DateTime(2013, 1, 1, 0, 0, 0, 0), null) includes Interval[DateTime(2013, 1, 1, 0, 0, 0, 0), DateTime(2013, 1, 1, 0, 0, 0, 0)]
define Includes_UnknownEndMayIncludeDateIvl: Interval[DateTime(2013, 1, 1, 0, 0, 0, 0), null) includes Interval[DateTime(2014, 1, 1, 0, 0, 0, 0), DateTime(2015, 1, 1, 0, 0, 0, 0)]
define Includes_UnknownEndNotIncludesDateIvl: Interval[DateTime(2013, 1, 1, 0, 0, 0, 0), null) includes Interval[DateTime(2000, 1, 1, 0, 0, 0, 0), DateTime(2014, 1, 1, 0, 0, 0, 0)]

define test_Includes_IncludesIntIvl: TestMessage(Includes_IncludesIntIvl, 'Includes_IncludesIntIvl', toString(true), toString(Includes_IncludesIntIvl))
define test_Includes_NotIncludesIntIvl: TestMessage(not Includes_NotIncludesIntIvl, 'Includes_NotIncludesIntIvl', toString(false), toString(Includes_NotIncludesIntIvl))
define test_Includes_IncludesRealIvl: TestMessage(Includes_IncludesRealIvl, 'Includes_IncludesRealIvl', toString(true), toString(Includes_IncludesRealIvl))
define test_Includes_NotIncludesRealIvl: TestMessage(not Includes_NotIncludesRealIvl, 'Includes_NotIncludesRealIvl', toString(false), toString(Includes_NotIncludesRealIvl))
define test_Includes_IncludesDateIvl: TestMessage(Includes_IncludesDateIvl, 'Includes_IncludesDateIvl', toString(true), toString(Includes_IncludesDateIvl))
define test_Includes_NotIncludesDateIvl: TestMessage(not Includes_NotIncludesDateIvl, 'Includes_NotIncludesDateIvl', toString(false), toString(Includes_NotIncludesDateIvl))
define test_Includes_IncludesImpreciseDateIvl: TestMessage(Includes_IncludesImpreciseDateIvl, 'Includes_IncludesImpreciseDateIvl', toString(true), toString(Includes_IncludesImpreciseDateIvl))
define test_Includes_NotIncludesImpreciseDateIvl: TestMessage(not Includes_NotIncludesImpreciseDateIvl, 'Includes_NotIncludesImpreciseDateIvl', toString(false), toString(Includes_NotIncludesImpreciseDateIvl))
define test_Includes_MayIncludeImpreciseDateIvl: TestMessage(Includes_MayIncludeImpreciseDateIvl is null, 'Includes_MayIncludeImpreciseDateIvl', 'null', toString(Includes_MayIncludeImpreciseDateIvl))
define test_Includes_IncludesDayOfIvlLowEdge: TestMessage(Includes_IncludesDayOfIvlLowEdge, 'Includes_IncludesDayOfIvlLowEdge', toString(true), toString(Includes_IncludesDayOfIvlLowEdge))
define test_Includes_IncludesDayOfIvlHighEdge: TestMessage(Includes_IncludesDayOfIvlHighEdge, 'Includes_IncludesDayOfIvlHighEdge', toString(true), toString(Includes_IncludesDayOfIvlHighEdge))
define test_Includes_NotIncludesDayOfIvlLowEdge: TestMessage(not Includes_NotIncludesDayOfIvlLowEdge, 'Includes_NotIncludesDayOfIvlLowEdge', toString(false), toString(Includes_NotIncludesDayOfIvlLowEdge))
define test_Includes_NotIncludesDayOfIvlHighEdge: TestMessage(not Includes_NotIncludesDayOfIvlHighEdge, 'Includes_NotIncludesDayOfIvlHighEdge', toString(false), toString(Includes_NotIncludesDayOfIvlHighEdge))
define test_Includes_IncludesDayOfIvlImpreciseLowEdge: TestMessage(Includes_IncludesDayOfIvlImpreciseLowEdge, 'Includes_IncludesDayOfIvlImpreciseLowEdge', toString(true), toString(Includes_IncludesDayOfIvlImpreciseLowEdge))
define test_Includes_IncludesDayOfIvlImpreciseHighEdge: TestMessage(Includes_IncludesDayOfIvlImpreciseHighEdge, 'Includes_IncludesDayOfIvlImpreciseHighEdge', toString(true), toString(Includes_IncludesDayOfIvlImpreciseHighEdge))
define test_Includes_IncludesDayOfIvlVeryImpreciseMiddle: TestMessage(Includes_IncludesDayOfIvlVeryImpreciseMiddle, 'Includes_IncludesDayOfIvlVeryImpreciseMiddle', toString(true), toString(Includes_IncludesDayOfIvlVeryImpreciseMiddle))
define test_Includes_NotIncludesDayOfIvlVeryImpreciseLow: TestMessage(not Includes_NotIncludesDayOfIvlVeryImpreciseLow, 'Includes_NotIncludesDayOfIvlVeryImpreciseLow', toString(false), toString(Includes_NotIncludesDayOfIvlVeryImpreciseLow))
define test_Includes_NotIncludesDayOfIvlVeryImpreciseHigh: TestMessage(not Includes_NotIncludesDayOfIvlVeryImpreciseHigh, 'Includes_NotIncludesDayOfIvlVeryImpreciseHigh', toString(false), toString(Includes_NotIncludesDayOfIvlVeryImpreciseHigh))
define test_Includes_MayIncludeDayOfIvlVeryImpreciseLow: TestMessage(Includes_MayIncludeDayOfIvlVeryImpreciseLow is null, 'Includes_MayIncludeDayOfIvlVeryImpreciseLow', 'null', toString(Includes_MayIncludeDayOfIvlVeryImpreciseLow))
define test_Includes_MayIncludeDayOfIvlVeryImpreciseHigh: TestMessage(Includes_MayIncludeDayOfIvlVeryImpreciseHigh is null, 'Includes_MayIncludeDayOfIvlVeryImpreciseHigh', 'null', toString(Includes_MayIncludeDayOfIvlVeryImpreciseHigh))
define test_Includes_MayIncludeDayOfIvlVeryImpreciseSurrounding: TestMessage(Includes_MayIncludeDayOfIvlVeryImpreciseSurrounding is null, 'Includes_MayIncludeDayOfIvlVeryImpreciseSurrounding', 'null', toString(Includes_MayIncludeDayOfIvlVeryImpreciseSurrounding))
define test_Includes_ImpreciseIncludesDateIvl: TestMessage(Includes_ImpreciseIncludesDateIvl, 'Includes_ImpreciseIncludesDateIvl', toString(true), toString(Includes_ImpreciseIncludesDateIvl))
define test_Includes_ImpreciseIncludesDate: TestMessage(Includes_ImpreciseIncludesDate, 'Includes_ImpreciseIncludesDate', toString(true), toString(Includes_ImpreciseIncludesDate))
define test_Includes_ImpreciseDoesntIncludeDate: TestMessage(not Includes_ImpreciseDoesntIncludeDate, 'Includes_ImpreciseDoesntIncludeDate', toString(false), toString(Includes_ImpreciseDoesntIncludeDate))
define test_Includes_IntervalIncludesQuantity: TestMessage(Includes_IntervalIncludesQuantity, 'Includes_IntervalIncludesQuantity', toString(true), toString(Includes_IntervalIncludesQuantity))
define test_Includes_IntervalDoesntIncludeQuantity: TestMessage(not Includes_IntervalDoesntIncludeQuantity, 'Includes_IntervalDoesntIncludeQuantity', toString(false), toString(Includes_IntervalDoesntIncludeQuantity))
define test_Includes_ImpreciseNotIncludesDateIvl: TestMessage(not Includes_ImpreciseNotIncludesDateIvl, 'Includes_ImpreciseNotIncludesDateIvl', toString(false), toString(Includes_ImpreciseNotIncludesDateIvl))
define test_Includes_ImpreciseMayIncludeDateIvl: TestMessage(Includes_ImpreciseMayIncludeDateIvl is null, 'Includes_ImpreciseMayIncludeDateIvl', 'null', toString(Includes_ImpreciseMayIncludeDateIvl))
define test_Includes_NegInfBegIncludesIntIvl: TestMessage(Includes_NegInfBegIncludesIntIvl, 'Includes_NegInfBegIncludesIntIvl', toString(true), toString(Includes_NegInfBegIncludesIntIvl))
define test_Includes_NegInfBegNotIncludesIntIvl: TestMessage(not Includes_NegInfBegNotIncludesIntIvl, 'Includes_NegInfBegNotIncludesIntIvl', toString(false), toString(Includes_NegInfBegNotIncludesIntIvl))
define test_Includes_UnknownBegIncludesIntIvl: TestMessage(Includes_UnknownBegIncludesIntIvl, 'Includes_UnknownBegIncludesIntIvl', toString(true), toString(Includes_UnknownBegIncludesIntIvl))
define test_Includes_UnknownBegMayIncludeIntIvl: TestMessage(Includes_UnknownBegMayIncludeIntIvl is null, 'Includes_UnknownBegMayIncludeIntIvl', 'null', toString(Includes_UnknownBegMayIncludeIntIvl))
define test_Includes_UnknownBegNotIncludesIntIvl: TestMessage(not Includes_UnknownBegNotIncludesIntIvl, 'Includes_UnknownBegNotIncludesIntIvl', toString(false), toString(Includes_UnknownBegNotIncludesIntIvl))
define test_Includes_PosInfEndIncludesIntIvl: TestMessage(Includes_PosInfEndIncludesIntIvl, 'Includes_PosInfEndIncludesIntIvl', toString(true), toString(Includes_PosInfEndIncludesIntIvl))
define test_Includes_PosInfEndNotIncludesIntIvl: TestMessage(not Includes_PosInfEndNotIncludesIntIvl, 'Includes_PosInfEndNotIncludesIntIvl', toString(false), toString(Includes_PosInfEndNotIncludesIntIvl))
define test_Includes_UnknownEndIncludesIntIvl: TestMessage(Includes_UnknownEndIncludesIntIvl, 'Includes_UnknownEndIncludesIntIvl', toString(true), toString(Includes_UnknownEndIncludesIntIvl))
define test_Includes_UnknownEndMayIncludeIntIvl: TestMessage(Includes_UnknownEndMayIncludeIntIvl is null, 'Includes_UnknownEndMayIncludeIntIvl', 'null', toString(Includes_UnknownEndMayIncludeIntIvl))
define test_Includes_UnknownEndNotIncludesIntIvl: TestMessage(not Includes_UnknownEndNotIncludesIntIvl, 'Includes_UnknownEndNotIncludesIntIvl', toString(false), toString(Includes_UnknownEndNotIncludesIntIvl))
define test_Includes_NegInfBegIncludesDateIvl: TestMessage(Includes_NegInfBegIncludesDateIvl, 'Includes_NegInfBegIncludesDateIvl', toString(true), toString(Includes_NegInfBegIncludesDateIvl))
define test_Includes_NegInfBegNotIncludesDateIvl: TestMessage(not Includes_NegInfBegNotIncludesDateIvl, 'Includes_NegInfBegNotIncludesDateIvl', toString(false), toString(Includes_NegInfBegNotIncludesDateIvl))
define test_Includes_UnknownBegIncludesDateIvl: TestMessage(Includes_UnknownBegIncludesDateIvl, 'Includes_UnknownBegIncludesDateIvl', toString(true), toString(Includes_UnknownBegIncludesDateIvl))
define test_Includes_UnknownBegMayIncludeDateIvl: TestMessage(Includes_UnknownBegMayIncludeDateIvl is null, 'Includes_UnknownBegMayIncludeDateIvl', 'null', toString(Includes_UnknownBegMayIncludeDateIvl))
define test_Includes_UnknownBegNotIncludesDateIvl: TestMessage(not Includes_UnknownBegNotIncludesDateIvl, 'Includes_UnknownBegNotIncludesDateIvl', toString(false), toString(Includes_UnknownBegNotIncludesDateIvl))
define test_Includes_PosInfEndIncludesDateIvl: TestMessage(Includes_PosInfEndIncludesDateIvl, 'Includes_PosInfEndIncludesDateIvl', toString(true), toString(Includes_PosInfEndIncludesDateIvl))
define test_Includes_PosInfEndNotIncludesDateIvl: TestMessage(not Includes_PosInfEndNotIncludesDateIvl, 'Includes_PosInfEndNotIncludesDateIvl', toString(false), toString(Includes_PosInfEndNotIncludesDateIvl))
define test_Includes_UnknownEndIncludesDateIvl: TestMessage(Includes_UnknownEndIncludesDateIvl, 'Includes_UnknownEndIncludesDateIvl', toString(true), toString(Includes_UnknownEndIncludesDateIvl))
define test_Includes_UnknownEndMayIncludeDateIvl: TestMessage(Includes_UnknownEndMayIncludeDateIvl is null, 'Includes_UnknownEndMayIncludeDateIvl', 'null', toString(Includes_UnknownEndMayIncludeDateIvl))
define test_Includes_UnknownEndNotIncludesDateIvl: TestMessage(not Includes_UnknownEndNotIncludesDateIvl, 'Includes_UnknownEndNotIncludesDateIvl', toString(false), toString(Includes_UnknownEndNotIncludesDateIvl))

// ProperlyIncludes
define ProperlyIncludes_ProperlyIncludesIntIvl: Interval[1, 5] properly includes Interval[2, 4]
define ProperlyIncludes_ProperlyIncludesIntBeginsIvl: Interval[1, 5] properly includes Interval[1, 4]
define ProperlyIncludes_ProperlyIncludesIntEndsIvl: Interval[1, 5] properly includes Interval[2, 5]
define ProperlyIncludes_NotProperlyIncludesIntIvl: Interval[1, 5] properly includes Interval[1, 5]
define ProperlyIncludes_ProperlyIncludesRealIvl: Interval[1.234, 3.456] properly includes Interval[2.34, 2.56]
define ProperlyIncludes_NotProperlyIncludesRealIvl: Interval[1.234, 3.456] properly includes Interval[1.234, 3.456]
define ProperlyIncludes_ProperlyIncludesDateIvl: DateIvl properly includes Interval[DateTime(2012, 4, 1, 0, 0, 0, 0), DateTime(2012, 6, 1, 0, 0, 0, 0))
define ProperlyIncludes_NotProperlyIncludesDateIvl: DateIvl properly includes Interval[DateTime(2012, 3, 1, 0, 0, 0, 0), DateTime(2012, 9, 1, 0, 0, 0, 0))
define ProperlyIncludes_PosInfEndProperlyIncludesIntIvl: Interval[0, null] properly includes Interval[1234, 5678]
define ProperlyIncludes_PosInfEndNotProperlyIncludesIntIvl: Interval[0, null] properly includes Interval[0, null]
define ProperlyIncludes_UnknownEndMayProperlyIncludeIntIvl: Interval[0, null) properly includes Interval[0, 0]
define ProperlyIncludes_ProperlyIncludesDayOfIvlLowEdge: PrecisionDateIvl properly includes day of Interval[DateTime(2012, 3, 2, 0, 0, 0, 0), DateTime(2012, 6, 1, 0, 0, 0, 0))
define ProperlyIncludes_ProperlyIncludesDayOfIvlHighEdge: PrecisionDateIvl properly includes day of Interval[DateTime(2012, 6, 1, 0, 0, 0, 0), DateTime(2012, 9, 2, 23, 59, 59, 999))
define ProperlyIncludes_ProperlyIncludesDayOfIvlNearEdges: PrecisionDateIvl properly includes day of Interval[DateTime(2012, 3, 3, 0, 0, 0, 0), DateTime(2012, 9, 1, 23, 59, 59, 999))
define ProperlyIncludes_NotProperlyIncludesDayOfIvlSameEdges: PrecisionDateIvl properly includes day of Interval[DateTime(2012, 3, 2, 0, 0, 0, 0), DateTime(2012, 9, 2, 0, 0, 0, 0)]
define ProperlyIncludes_MayProperlyIncludeDayOfIvlVeryImpreciseLow: PrecisionDateIvl properly includes day of Interval[DateTime(2012, 3), DateTime(2012, 6)]
define ProperlyIncludes_MayProperlyIncludeDayOfIvlVeryImpreciseHigh: PrecisionDateIvl properly includes day of Interval[DateTime(2012, 6), DateTime(2012, 9)]
define ProperlyIncludes_MayProperlyIncludeDayOfIvlVeryImpreciseLowAndHigh: PrecisionDateIvl properly includes day of Interval[DateTime(2012, 3), DateTime(2012, 9)]
define ProperlyIncludes_MayProperlyIncludeDayOfIvlVeryImpreciseSurrounding: PrecisionDateIvl properly includes day of Interval[DateTime(2012), DateTime(2012)]

define test_ProperlyIncludes_ProperlyIncludesIntIvl: TestMessage(ProperlyIncludes_ProperlyIncludesIntIvl, 'ProperlyIncludes_ProperlyIncludesIntIvl', toString(true), toString(ProperlyIncludes_ProperlyIncludesIntIvl))
define test_ProperlyIncludes_ProperlyIncludesIntBeginsIvl: TestMessage(ProperlyIncludes_ProperlyIncludesIntBeginsIvl, 'ProperlyIncludes_ProperlyIncludesIntBeginsIvl', toString(true), toString(ProperlyIncludes_ProperlyIncludesIntBeginsIvl))
define test_ProperlyIncludes_ProperlyIncludesIntEndsIvl: TestMessage(ProperlyIncludes_ProperlyIncludesIntEndsIvl, 'ProperlyIncludes_ProperlyIncludesIntEndsIvl', toString(true), toString(ProperlyIncludes_ProperlyIncludesIntEndsIvl))
define test_ProperlyIncludes_NotProperlyIncludesIntIvl: TestMessage(not ProperlyIncludes_NotProperlyIncludesIntIvl, 'ProperlyIncludes_NotProperlyIncludesIntIvl', toString(false), toString(ProperlyIncludes_NotProperlyIncludesIntIvl))
define test_ProperlyIncludes_ProperlyIncludesRealIvl: TestMessage(ProperlyIncludes_ProperlyIncludesRealIvl, 'ProperlyIncludes_ProperlyIncludesRealIvl', toString(true), toString(ProperlyIncludes_ProperlyIncludesRealIvl))
define test_ProperlyIncludes_NotProperlyIncludesRealIvl: TestMessage(not ProperlyIncludes_NotProperlyIncludesRealIvl, 'ProperlyIncludes_NotProperlyIncludesRealIvl', toString(false), toString(ProperlyIncludes_NotProperlyIncludesRealIvl))
define test_ProperlyIncludes_ProperlyIncludesDateIvl: TestMessage(ProperlyIncludes_ProperlyIncludesDateIvl, 'ProperlyIncludes_ProperlyIncludesDateIvl', toString(true), toString(ProperlyIncludes_ProperlyIncludesDateIvl))
define test_ProperlyIncludes_NotProperlyIncludesDateIvl: TestMessage(not ProperlyIncludes_NotProperlyIncludesDateIvl, 'ProperlyIncludes_NotProperlyIncludesDateIvl', toString(false), toString(ProperlyIncludes_NotProperlyIncludesDateIvl))
define test_ProperlyIncludes_PosInfEndProperlyIncludesIntIvl: TestMessage(ProperlyIncludes_PosInfEndProperlyIncludesIntIvl, 'ProperlyIncludes_PosInfEndProperlyIncludesIntIvl', toString(true), toString(ProperlyIncludes_PosInfEndProperlyIncludesIntIvl))
define test_ProperlyIncludes_PosInfEndNotProperlyIncludesIntIvl: TestMessage(not ProperlyIncludes_PosInfEndNotProperlyIncludesIntIvl, 'ProperlyIncludes_PosInfEndNotProperlyIncludesIntIvl', toString(false), toString(ProperlyIncludes_PosInfEndNotProperlyIncludesIntIvl))
define test_ProperlyIncludes_UnknownEndMayProperlyIncludeIntIvl: TestMessage(ProperlyIncludes_UnknownEndMayProperlyIncludeIntIvl is null, 'ProperlyIncludes_UnknownEndMayProperlyIncludeIntIvl', 'null', toString(ProperlyIncludes_UnknownEndMayProperlyIncludeIntIvl))
define test_ProperlyIncludes_ProperlyIncludesDayOfIvlLowEdge: TestMessage(ProperlyIncludes_ProperlyIncludesDayOfIvlLowEdge, 'ProperlyIncludes_ProperlyIncludesDayOfIvlLowEdge', toString(true), toString(ProperlyIncludes_ProperlyIncludesDayOfIvlLowEdge))
define test_ProperlyIncludes_ProperlyIncludesDayOfIvlHighEdge: TestMessage(ProperlyIncludes_ProperlyIncludesDayOfIvlHighEdge, 'ProperlyIncludes_ProperlyIncludesDayOfIvlHighEdge', toString(true), toString(ProperlyIncludes_ProperlyIncludesDayOfIvlHighEdge))
define test_ProperlyIncludes_ProperlyIncludesDayOfIvlNearEdges: TestMessage(ProperlyIncludes_ProperlyIncludesDayOfIvlNearEdges, 'ProperlyIncludes_ProperlyIncludesDayOfIvlNearEdges', toString(true), toString(ProperlyIncludes_ProperlyIncludesDayOfIvlNearEdges))
define test_ProperlyIncludes_NotProperlyIncludesDayOfIvlSameEdges: TestMessage(not ProperlyIncludes_NotProperlyIncludesDayOfIvlSameEdges, 'ProperlyIncludes_NotProperlyIncludesDayOfIvlSameEdges', toString(false), toString(ProperlyIncludes_NotProperlyIncludesDayOfIvlSameEdges))
define test_ProperlyIncludes_MayProperlyIncludeDayOfIvlVeryImpreciseLow: TestMessage(ProperlyIncludes_MayProperlyIncludeDayOfIvlVeryImpreciseLow is null, 'ProperlyIncludes_MayProperlyIncludeDayOfIvlVeryImpreciseLow', 'null', toString(ProperlyIncludes_MayProperlyIncludeDayOfIvlVeryImpreciseLow))
define test_ProperlyIncludes_MayProperlyIncludeDayOfIvlVeryImpreciseHigh: TestMessage(ProperlyIncludes_MayProperlyIncludeDayOfIvlVeryImpreciseHigh is null, 'ProperlyIncludes_MayProperlyIncludeDayOfIvlVeryImpreciseHigh', 'null', toString(ProperlyIncludes_MayProperlyIncludeDayOfIvlVeryImpreciseHigh))
define test_ProperlyIncludes_MayProperlyIncludeDayOfIvlVeryImpreciseLowAndHigh: TestMessage(ProperlyIncludes_MayProperlyIncludeDayOfIvlVeryImpreciseLowAndHigh is null, 'ProperlyIncludes_MayProperlyIncludeDayOfIvlVeryImpreciseLowAndHigh', 'null', toString(ProperlyIncludes_MayProperlyIncludeDayOfIvlVeryImpreciseLowAndHigh))
define test_ProperlyIncludes_MayProperlyIncludeDayOfIvlVeryImpreciseSurrounding: TestMessage(ProperlyIncludes_MayProperlyIncludeDayOfIvlVeryImpreciseSurrounding is null, 'ProperlyIncludes_MayProperlyIncludeDayOfIvlVeryImpreciseSurrounding', 'null', toString(ProperlyIncludes_MayProperlyIncludeDayOfIvlVeryImpreciseSurrounding))

// IncludedIn
define IncludedIn_IncludesIntIvl: Interval[1, 4] included in Interval[1, 5]
define IncludedIn_NotIncludesIntIvl: Interval[1, 4] included in Interval(1, 5]
define IncludedIn_IncludesRealIvl: Interval[2.34, 2.56] included in Interval[1.234, 3.456]
define IncludedIn_NotIncludesRealIvl: Interval[1.23, 2.56] included in Interval[1.234, 3.456]
define IncludedIn_IncludesDateIvl: Interval[DateTime(2012, 4, 1, 0, 0, 0, 0), DateTime(2012, 6, 1, 0, 0, 0, 0)) included in DateIvl
define IncludedIn_NotIncludesDateIvl: Interval[DateTime(2012, 6, 1, 0, 0, 0, 0), DateTime(2012, 9, 1, 0, 0, 0, 0)] included in DateIvl
define IncludedIn_IncludesImpreciseDateIvl: Interval[DateTime(2012, 4), DateTime(2012, 6)] included in DateIvl
define IncludedIn_NotIncludesImpreciseDateIvl: Interval[DateTime(2012, 4), DateTime(2012, 9)] included in DateIvl
define IncludedIn_MayIncludeImpreciseDateIvl: Interval[DateTime(2012), DateTime(2012)] included in DateIvl
define IncludedIn_IncludesDayOfIvlLowEdge: Interval[DateTime(2012, 3, 2, 0, 0, 0, 0), DateTime(2012, 6, 1, 0, 0, 0, 0)) included in day of PrecisionDateIvl
define IncludedIn_IncludesDayInInterval: DateTime(2012, 3, 3) included in day of PrecisionDateIvl
define IncludedIn_DoesNotIncludeDayInInterval: DateTime(2014, 3, 3) included in day of PrecisionDateIvl
define IncludedIn_QuantityIncludedInterval: 5 'mg' included in Interval[4 'mg', 6 'mg']
define IncludedIn_QuantityNotIncludedInterval: 50 'mg' included in Interval[4 'mg', 6 'mg']
define IncludedIn_IncludesDayOfIvlHighEdge: Interval[DateTime(2012, 6, 1, 0, 0, 0, 0), DateTime(2012, 9, 2, 23, 59, 59, 999)) included in day of PrecisionDateIvl
define IncludedIn_NotIncludesDayOfIvlLowEdge: Interval[DateTime(2012, 3, 1, 0, 0, 0, 0), DateTime(2012, 6, 1, 0, 0, 0, 0)] included in day of PrecisionDateIvl
define IncludedIn_NotIncludesDayOfIvlHighEdge: Interval[DateTime(2012, 6, 1, 0, 0, 0, 0), DateTime(2012, 9, 3, 0, 0, 0, 0)] included in day of PrecisionDateIvl
define IncludedIn_IncludesDayOfIvlImpreciseLowEdge: Interval[DateTime(2012, 3, 2), DateTime(2012, 6, 1)] included in day of PrecisionDateIvl
define IncludedIn_IncludesDayOfIvlImpreciseHighEdge: Interval[DateTime(2012, 6, 1), DateTime(2012, 9, 2)] included in day of PrecisionDateIvl
define IncludedIn_IncludesDayOfIvlVeryImpreciseMiddle: Interval[DateTime(2012, 5), DateTime(2012, 6)] included in day of PrecisionDateIvl
define IncludedIn_NotIncludesDayOfIvlVeryImpreciseLow: Interval[DateTime(2012, 2), DateTime(2012, 6)] included in day of PrecisionDateIvl
define IncludedIn_NotIncludesDayOfIvlVeryImpreciseHigh: Interval[DateTime(2012, 6), DateTime(2012, 10)] included in day of PrecisionDateIvl
define IncludedIn_MayIncludeDayOfIvlVeryImpreciseLow: Interval[DateTime(2012, 3), DateTime(2012, 6)] included in day of PrecisionDateIvl
define IncludedIn_MayIncludeDayOfIvlVeryImpreciseHigh: Interval[DateTime(2012, 6), DateTime(2012, 9)] included in day of PrecisionDateIvl
define IncludedIn_MayIncludeDayOfIvlVeryImpreciseSurrounding: Interval[DateTime(2012), DateTime(2012)] included in day of PrecisionDateIvl
define IncludedIn_ImpreciseIncludesDateIvl: Interval[DateTime(2012, 4, 1, 0, 0, 0, 0), DateTime(2012, 6, 1, 0, 0, 0, 0)) included in ImpDateIvl
define IncludedIn_ImpreciseNotIncludesDateIvl: Interval[DateTime(2012, 2, 1, 0, 0, 0, 0), DateTime(2012, 6, 1, 0, 0, 0, 0)) included in ImpDateIvl
define IncludedIn_ImpreciseMayIncludeDateIvl: Interval[DateTime(2012, 3, 15, 0, 0, 0, 0), DateTime(2012, 6, 1, 0, 0, 0, 0)) included in ImpDateIvl
define IncludedIn_NegInfBegIncludedInIntIvl: Interval[null, 5] included in Interval[null, 100]
define IncludedIn_NegInfBegNotIncludedInIntIvl: Interval[null, 5] included in Interval[-100, 100]
// TODO - this test should return null, but Bonnie expects true
// define IncludedIn_UnknownBegIncludedInIntIvl: Interval(null, 5] included in Interval[null, 100]
define IncludedIn_UnknownBegMayBeIncludedInIntIvl: Interval(null, 5] included in Interval[-100, 100]
define IncludedIn_UnknownBegNotIncludedInIntIvl: Interval(null, 5] included in Interval[-100, 0]
define IncludedIn_PosInfEndIncludedInIntIvl: Interval[0, null] included in Interval[-100, null]
define IncludedIn_PosInfEndNotIncludedInIntIvl: Interval[0, null] included in Interval[-100, 100]
// TODO - this test should return null, but Bonnie expects true
// define IncludedIn_UnknownEndIncludedInIntIvl: Interval[0, null) included in Interval[-100, null]
define IncludedIn_UnknownEndMayBeIncludedInIntIvl: Interval[0, null) included in Interval[-100, 100]
define IncludedIn_UnknownEndNotIncludedInIntIvl: Interval[0, null) included in Interval[100, 200]
define IncludedIn_NegInfBegIncludedInDateIvl: Interval[null, DateTime(2013, 1, 1, 0, 0, 0, 0)) included in Interval[null, DateTime(2020, 1, 1, 0, 0, 0, 0)]
define IncludedIn_NegInfBegNotIncludedInDateIvl: Interval[null, DateTime(2013, 1, 1, 0, 0, 0, 0)) included in Interval[DateTime(2000, 1, 1, 0, 0, 0, 0), DateTime(2020, 1, 1, 0, 0, 0, 0)]
// TODO - this test should return null, but Bonnie expects true
// define IncludedIn_UnknownBegIncludedInDateIvl: Interval(null, DateTime(2013, 1, 1, 0, 0, 0, 0)) included in Interval[null, DateTime(2020, 1, 1, 0, 0, 0, 0)]
define IncludedIn_UnknownBegMayBeIncludedInDateIvl: Interval(null, DateTime(2013, 1, 1, 0, 0, 0, 0)) included in Interval[DateTime(2000, 1, 1, 0, 0, 0, 0), DateTime(2020, 1, 1, 0, 0, 0, 0)]
define IncludedIn_UnknownBegNotIncludedInDateIvl: Interval(null, DateTime(2013, 1, 1, 0, 0, 0, 0)) included in Interval[DateTime(1, 1, 1, 0, 0, 0, 0), DateTime(2000, 1, 1, 0, 0, 0, 0)]
define IncludedIn_PosInfEndIncludedInDateIvl: Interval[DateTime(2013, 1, 1, 0, 0, 0, 0), null] included in Interval[DateTime(2000, 1, 1, 0, 0, 0, 0), null]
define IncludedIn_PosInfEndNotIncludedInDateIvl: Interval[DateTime(2013, 1, 1, 0, 0, 0, 0), null] included in Interval[DateTime(2000, 1, 1, 0, 0, 0, 0), DateTime(2020, 1, 1, 0, 0, 0, 0)]
// TODO - this test should return null, but Bonnie expects true
// define IncludedIn_UnknownEndIncludedInDateIvl: Interval[DateTime(2013, 1, 1, 0, 0, 0, 0), null) included in Interval[DateTime(2000, 1, 1, 0, 0, 0, 0), null]
define IncludedIn_UnknownEndMayBeIncludedInDateIvl: Interval[DateTime(2013, 1, 1, 0, 0, 0, 0), null) included in Interval[DateTime(2000, 1, 1, 0, 0, 0, 0), DateTime(2020, 1, 1, 0, 0, 0, 0)]
define IncludedIn_UnknownEndNotIncludedInDateIvl: Interval[DateTime(2013, 1, 1, 0, 0, 0, 0), null) included in Interval[DateTime(2020, 1, 1, 0, 0, 0, 0), DateTime(2040, 1, 1, 0, 0, 0, 0)]

define test_IncludedIn_IncludesIntIvl: TestMessage(IncludedIn_IncludesIntIvl, 'IncludedIn_IncludesIntIvl', toString(true), toString(IncludedIn_IncludesIntIvl))
define test_IncludedIn_NotIncludesIntIvl: TestMessage(not IncludedIn_NotIncludesIntIvl, 'IncludedIn_NotIncludesIntIvl', toString(false), toString(IncludedIn_NotIncludesIntIvl))
define test_IncludedIn_IncludesRealIvl: TestMessage(IncludedIn_IncludesRealIvl, 'IncludedIn_IncludesRealIvl', toString(true), toString(IncludedIn_IncludesRealIvl))
define test_IncludedIn_NotIncludesRealIvl: TestMessage(not IncludedIn_NotIncludesRealIvl, 'IncludedIn_NotIncludesRealIvl', toString(false), toString(IncludedIn_NotIncludesRealIvl))
define test_IncludedIn_IncludesDateIvl: TestMessage(IncludedIn_IncludesDateIvl, 'IncludedIn_IncludesDateIvl', toString(true), toString(IncludedIn_IncludesDateIvl))
define test_IncludedIn_NotIncludesDateIvl: TestMessage(not IncludedIn_NotIncludesDateIvl, 'IncludedIn_NotIncludesDateIvl', toString(false), toString(IncludedIn_NotIncludesDateIvl))
define test_IncludedIn_IncludesImpreciseDateIvl: TestMessage(IncludedIn_IncludesImpreciseDateIvl, 'IncludedIn_IncludesImpreciseDateIvl', toString(true), toString(IncludedIn_IncludesImpreciseDateIvl))
define test_IncludedIn_NotIncludesImpreciseDateIvl: TestMessage(not IncludedIn_NotIncludesImpreciseDateIvl, 'IncludedIn_NotIncludesImpreciseDateIvl', toString(false), toString(IncludedIn_NotIncludesImpreciseDateIvl))
define test_IncludedIn_MayIncludeImpreciseDateIvl: TestMessage(IncludedIn_MayIncludeImpreciseDateIvl is null, 'IncludedIn_MayIncludeImpreciseDateIvl', 'null', toString(IncludedIn_MayIncludeImpreciseDateIvl))
define test_IncludedIn_IncludesDayOfIvlLowEdge: TestMessage(IncludedIn_IncludesDayOfIvlLowEdge, 'IncludedIn_IncludesDayOfIvlLowEdge', toString(true), toString(IncludedIn_IncludesDayOfIvlLowEdge))
define test_IncludedIn_IncludesDayInInterval: TestMessage(IncludedIn_IncludesDayInInterval, 'IncludedIn_IncludesDayInInterval', toString(true), toString(IncludedIn_IncludesDayInInterval))
define test_IncludedIn_DoesNotIncludeDayInInterval: TestMessage(not IncludedIn_DoesNotIncludeDayInInterval, 'IncludedIn_DoesNotIncludeDayInInterval', toString(false), toString(IncludedIn_DoesNotIncludeDayInInterval))
define test_IncludedIn_QuantityIncludedInterval: TestMessage(IncludedIn_QuantityIncludedInterval, 'IncludedIn_QuantityIncludedInterval', toString(true), toString(IncludedIn_QuantityIncludedInterval))
define test_IncludedIn_QuantityNotIncludedInterval: TestMessage(not IncludedIn_QuantityNotIncludedInterval, 'IncludedIn_QuantityNotIncludedInterval', toString(false), toString(IncludedIn_QuantityNotIncludedInterval))
define test_IncludedIn_IncludesDayOfIvlHighEdge: TestMessage(IncludedIn_IncludesDayOfIvlHighEdge, 'IncludedIn_IncludesDayOfIvlHighEdge', toString(true), toString(IncludedIn_IncludesDayOfIvlHighEdge))
define test_IncludedIn_NotIncludesDayOfIvlLowEdge: TestMessage(not IncludedIn_NotIncludesDayOfIvlLowEdge, 'IncludedIn_NotIncludesDayOfIvlLowEdge', toString(false), toString(IncludedIn_NotIncludesDayOfIvlLowEdge))
define test_IncludedIn_NotIncludesDayOfIvlHighEdge: TestMessage(not IncludedIn_NotIncludesDayOfIvlHighEdge, 'IncludedIn_NotIncludesDayOfIvlHighEdge', toString(false), toString(IncludedIn_NotIncludesDayOfIvlHighEdge))
define test_IncludedIn_IncludesDayOfIvlImpreciseLowEdge: TestMessage(IncludedIn_IncludesDayOfIvlImpreciseLowEdge, 'IncludedIn_IncludesDayOfIvlImpreciseLowEdge', toString(true), toString(IncludedIn_IncludesDayOfIvlImpreciseLowEdge))
define test_IncludedIn_IncludesDayOfIvlImpreciseHighEdge: TestMessage(IncludedIn_IncludesDayOfIvlImpreciseHighEdge, 'IncludedIn_IncludesDayOfIvlImpreciseHighEdge', toString(true), toString(IncludedIn_IncludesDayOfIvlImpreciseHighEdge))
define test_IncludedIn_IncludesDayOfIvlVeryImpreciseMiddle: TestMessage(IncludedIn_IncludesDayOfIvlVeryImpreciseMiddle, 'IncludedIn_IncludesDayOfIvlVeryImpreciseMiddle', toString(true), toString(IncludedIn_IncludesDayOfIvlVeryImpreciseMiddle))
define test_IncludedIn_NotIncludesDayOfIvlVeryImpreciseLow: TestMessage(not IncludedIn_NotIncludesDayOfIvlVeryImpreciseLow, 'IncludedIn_NotIncludesDayOfIvlVeryImpreciseLow', toString(false), toString(IncludedIn_NotIncludesDayOfIvlVeryImpreciseLow))
define test_IncludedIn_NotIncludesDayOfIvlVeryImpreciseHigh: TestMessage(not IncludedIn_NotIncludesDayOfIvlVeryImpreciseHigh, 'IncludedIn_NotIncludesDayOfIvlVeryImpreciseHigh', toString(false), toString(IncludedIn_NotIncludesDayOfIvlVeryImpreciseHigh))
define test_IncludedIn_MayIncludeDayOfIvlVeryImpreciseLow: TestMessage(IncludedIn_MayIncludeDayOfIvlVeryImpreciseLow is null, 'IncludedIn_MayIncludeDayOfIvlVeryImpreciseLow', 'null', toString(IncludedIn_MayIncludeDayOfIvlVeryImpreciseLow))
define test_IncludedIn_MayIncludeDayOfIvlVeryImpreciseHigh: TestMessage(IncludedIn_MayIncludeDayOfIvlVeryImpreciseHigh is null, 'IncludedIn_MayIncludeDayOfIvlVeryImpreciseHigh', 'null', toString(IncludedIn_MayIncludeDayOfIvlVeryImpreciseHigh))
define test_IncludedIn_MayIncludeDayOfIvlVeryImpreciseSurrounding: TestMessage(IncludedIn_MayIncludeDayOfIvlVeryImpreciseSurrounding is null, 'IncludedIn_MayIncludeDayOfIvlVeryImpreciseSurrounding', 'null', toString(IncludedIn_MayIncludeDayOfIvlVeryImpreciseSurrounding))
define test_IncludedIn_ImpreciseIncludesDateIvl: TestMessage(IncludedIn_ImpreciseIncludesDateIvl, 'IncludedIn_ImpreciseIncludesDateIvl', toString(true), toString(IncludedIn_ImpreciseIncludesDateIvl))
define test_IncludedIn_ImpreciseNotIncludesDateIvl: TestMessage(not IncludedIn_ImpreciseNotIncludesDateIvl, 'IncludedIn_ImpreciseNotIncludesDateIvl', toString(false), toString(IncludedIn_ImpreciseNotIncludesDateIvl))
define test_IncludedIn_ImpreciseMayIncludeDateIvl: TestMessage(IncludedIn_ImpreciseMayIncludeDateIvl is null, 'IncludedIn_ImpreciseMayIncludeDateIvl', 'null', toString(IncludedIn_ImpreciseMayIncludeDateIvl))
define test_IncludedIn_NegInfBegIncludedInIntIvl: TestMessage(IncludedIn_NegInfBegIncludedInIntIvl, 'IncludedIn_NegInfBegIncludedInIntIvl', toString(true), toString(IncludedIn_NegInfBegIncludedInIntIvl))
define test_IncludedIn_NegInfBegNotIncludedInIntIvl: TestMessage(not IncludedIn_NegInfBegNotIncludedInIntIvl, 'IncludedIn_NegInfBegNotIncludedInIntIvl', toString(false), toString(IncludedIn_NegInfBegNotIncludedInIntIvl))
// define test_IncludedIn_UnknownBegIncludedInIntIvl: TestMessage(IncludedIn_UnknownBegIncludedInIntIvl, 'IncludedIn_UnknownBegIncludedInIntIvl', toString(true), toString(IncludedIn_UnknownBegIncludedInIntIvl))
define test_IncludedIn_UnknownBegMayBeIncludedInIntIvl: TestMessage(IncludedIn_UnknownBegMayBeIncludedInIntIvl is null, 'IncludedIn_UnknownBegMayBeIncludedInIntIvl', 'null', toString(IncludedIn_UnknownBegMayBeIncludedInIntIvl))
define test_IncludedIn_UnknownBegNotIncludedInIntIvl: TestMessage(not IncludedIn_UnknownBegNotIncludedInIntIvl, 'IncludedIn_UnknownBegNotIncludedInIntIvl', toString(false), toString(IncludedIn_UnknownBegNotIncludedInIntIvl))
define test_IncludedIn_PosInfEndIncludedInIntIvl: TestMessage(IncludedIn_PosInfEndIncludedInIntIvl, 'IncludedIn_PosInfEndIncludedInIntIvl', toString(true), toString(IncludedIn_PosInfEndIncludedInIntIvl))
define test_IncludedIn_PosInfEndNotIncludedInIntIvl: TestMessage(not IncludedIn_PosInfEndNotIncludedInIntIvl, 'IncludedIn_PosInfEndNotIncludedInIntIvl', toString(false), toString(IncludedIn_PosInfEndNotIncludedInIntIvl))
// define test_IncludedIn_UnknownEndIncludedInIntIvl: TestMessage(IncludedIn_UnknownEndIncludedInIntIvl, 'IncludedIn_UnknownEndIncludedInIntIvl', toString(true), toString(IncludedIn_UnknownEndIncludedInIntIvl))
define test_IncludedIn_UnknownEndMayBeIncludedInIntIvl: TestMessage(IncludedIn_UnknownEndMayBeIncludedInIntIvl is null, 'IncludedIn_UnknownEndMayBeIncludedInIntIvl', 'null', toString(IncludedIn_UnknownEndMayBeIncludedInIntIvl))
define test_IncludedIn_UnknownEndNotIncludedInIntIvl: TestMessage(not IncludedIn_UnknownEndNotIncludedInIntIvl, 'IncludedIn_UnknownEndNotIncludedInIntIvl', toString(false), toString(IncludedIn_UnknownEndNotIncludedInIntIvl))
define test_IncludedIn_NegInfBegIncludedInDateIvl: TestMessage(IncludedIn_NegInfBegIncludedInDateIvl, 'IncludedIn_NegInfBegIncludedInDateIvl', toString(true), toString(IncludedIn_NegInfBegIncludedInDateIvl))
define test_IncludedIn_NegInfBegNotIncludedInDateIvl: TestMessage(not IncludedIn_NegInfBegNotIncludedInDateIvl, 'IncludedIn_NegInfBegNotIncludedInDateIvl', toString(false), toString(IncludedIn_NegInfBegNotIncludedInDateIvl))
// define test_IncludedIn_UnknownBegIncludedInDateIvl: TestMessage(IncludedIn_UnknownBegIncludedInDateIvl, 'IncludedIn_UnknownBegIncludedInDateIvl', toString(true), toString(IncludedIn_UnknownBegIncludedInDateIvl))
define test_IncludedIn_UnknownBegMayBeIncludedInDateIvl: TestMessage(IncludedIn_UnknownBegMayBeIncludedInDateIvl is null, 'IncludedIn_UnknownBegMayBeIncludedInDateIvl', 'null', toString(IncludedIn_UnknownBegMayBeIncludedInDateIvl))
define test_IncludedIn_UnknownBegNotIncludedInDateIvl: TestMessage(not IncludedIn_UnknownBegNotIncludedInDateIvl, 'IncludedIn_UnknownBegNotIncludedInDateIvl', toString(false), toString(IncludedIn_UnknownBegNotIncludedInDateIvl))
define test_IncludedIn_PosInfEndIncludedInDateIvl: TestMessage(IncludedIn_PosInfEndIncludedInDateIvl, 'IncludedIn_PosInfEndIncludedInDateIvl', toString(true), toString(IncludedIn_PosInfEndIncludedInDateIvl))
define test_IncludedIn_PosInfEndNotIncludedInDateIvl: TestMessage(not IncludedIn_PosInfEndNotIncludedInDateIvl, 'IncludedIn_PosInfEndNotIncludedInDateIvl', toString(false), toString(IncludedIn_PosInfEndNotIncludedInDateIvl))
// define test_IncludedIn_UnknownEndIncludedInDateIvl: TestMessage(IncludedIn_UnknownEndIncludedInDateIvl, 'IncludedIn_UnknownEndIncludedInDateIvl', toString(true), toString(IncludedIn_UnknownEndIncludedInDateIvl))
define test_IncludedIn_UnknownEndMayBeIncludedInDateIvl: TestMessage(IncludedIn_UnknownEndMayBeIncludedInDateIvl is null, 'IncludedIn_UnknownEndMayBeIncludedInDateIvl', 'null', toString(IncludedIn_UnknownEndMayBeIncludedInDateIvl))
define test_IncludedIn_UnknownEndNotIncludedInDateIvl: TestMessage(not IncludedIn_UnknownEndNotIncludedInDateIvl, 'IncludedIn_UnknownEndNotIncludedInDateIvl', toString(false), toString(IncludedIn_UnknownEndNotIncludedInDateIvl))

// ProperlyIncludedIn
define ProperlyIncludedIn_ProperlyIncludesIntIvl: Interval[2, 4] properly included in Interval[1, 5]
define ProperlyIncludedIn_ProperlyIncludesIntBeginsIvl: Interval[1, 4] properly included in Interval[1, 5]
define ProperlyIncludedIn_ProperlyIncludesIntEndsIvl: Interval[2, 5] properly included in Interval[1, 5]
define ProperlyIncludedIn_NotProperlyIncludesIntIvl: Interval[1, 5] properly included in Interval[1, 5]
define ProperlyIncludedIn_ProperlyIncludesRealIvl: Interval[2.34, 2.56] properly included in Interval[1.234, 3.456]
define ProperlyIncludedIn_NotProperlyIncludesRealIvl: Interval[1.23, 2.56] properly included in Interval[1.23, 2.56]
define ProperlyIncludedIn_ProperlyIncludesDateIvl: Interval[DateTime(2012, 4, 1, 0, 0, 0, 0), DateTime(2012, 6, 1, 0, 0, 0, 0)) properly included in DateIvl
define ProperlyIncludedIn_NotProperlyIncludesDateIvl: Interval[DateTime(2012, 3, 1, 0, 0, 0, 0), DateTime(2012, 9, 1, 0, 0, 0, 0)) properly included in DateIvl
define ProperlyIncludedIn_PosInfEndProperlyIncludedInDateIvl: Interval[DateTime(2013, 1, 1, 0, 0, 0, 0), null] properly included in Interval[DateTime(2000, 1, 1, 0, 0, 0, 0), null]
define ProperlyIncludedIn_PosInfEndNotProperlyIncludedInDateIvl: Interval[DateTime(2013, 1, 1, 0, 0, 0, 0), null] properly included in Interval[DateTime(2000, 1, 1, 0, 0, 0, 0), DateTime(2020, 1, 1, 0, 0, 0, 0)]
define ProperlyIncludedIn_UnknownEndMayBeProperlyIncludedInDateIvl: Interval[DateTime(2013, 1, 1, 0, 0, 0, 0), null) properly included in Interval[DateTime(2000, 1, 1, 0, 0, 0, 0), DateTime(2020, 1, 1, 0, 0, 0, 0)]
define ProperlyIncludedIn_ProperlyIncludesDayOfIvlLowEdge: Interval[DateTime(2012, 3, 2, 0, 0, 0, 0), DateTime(2012, 6, 1, 0, 0, 0, 0)) properly included in day of PrecisionDateIvl
define ProperlyIncludedIn_ProperlyIncludesDayOfIvlHighEdge: Interval[DateTime(2012, 6, 1, 0, 0, 0, 0), DateTime(2012, 9, 2, 23, 59, 59, 999)) properly included in day of PrecisionDateIvl
define ProperlyIncludedIn_ProperlyIncludesDayOfIvlNearEdges: Interval[DateTime(2012, 3, 3, 0, 0, 0, 0), DateTime(2012, 9, 1, 23, 59, 59, 999)) properly included in day of PrecisionDateIvl
define ProperlyIncludedIn_NotProperlyIncludesDayOfIvlSameEdges: Interval[DateTime(2012, 3, 2, 0, 0, 0, 0), DateTime(2012, 9, 2, 0, 0, 0, 0)] properly included in day of PrecisionDateIvl
define ProperlyIncludedIn_MayProperlyIncludeDayOfIvlVeryImpreciseLow: Interval[DateTime(2012, 3), DateTime(2012, 6)] properly included in day of PrecisionDateIvl
define ProperlyIncludedIn_MayProperlyIncludeDayOfIvlVeryImpreciseHigh: Interval[DateTime(2012, 6), DateTime(2012, 9)] properly included in day of PrecisionDateIvl
define ProperlyIncludedIn_MayProperlyIncludeDayOfIvlVeryImpreciseLowAndHigh: Interval[DateTime(2012, 3), DateTime(2012, 9)] properly included in day of PrecisionDateIvl
define ProperlyIncludedIn_MayProperlyIncludeDayOfIvlVeryImpreciseSurrounding: Interval[DateTime(2012), DateTime(2012)] properly included in day of PrecisionDateIvl

define test_ProperlyIncludedIn_ProperlyIncludesIntIvl: TestMessage(ProperlyIncludedIn_ProperlyIncludesIntIvl, 'ProperlyIncludedIn_ProperlyIncludesIntIvl', toString(true), toString(ProperlyIncludedIn_ProperlyIncludesIntIvl))
define test_ProperlyIncludedIn_ProperlyIncludesIntBeginsIvl: TestMessage(ProperlyIncludedIn_ProperlyIncludesIntBeginsIvl, 'ProperlyIncludedIn_ProperlyIncludesIntBeginsIvl', toString(true), toString(ProperlyIncludedIn_ProperlyIncludesIntBeginsIvl))
define test_ProperlyIncludedIn_ProperlyIncludesIntEndsIvl: TestMessage(ProperlyIncludedIn_ProperlyIncludesIntEndsIvl, 'ProperlyIncludedIn_ProperlyIncludesIntEndsIvl', toString(true), toString(ProperlyIncludedIn_ProperlyIncludesIntEndsIvl))
define test_ProperlyIncludedIn_NotProperlyIncludesIntIvl: TestMessage(not ProperlyIncludedIn_NotProperlyIncludesIntIvl, 'ProperlyIncludedIn_NotProperlyIncludesIntIvl', toString(false), toString(ProperlyIncludedIn_NotProperlyIncludesIntIvl))
define test_ProperlyIncludedIn_ProperlyIncludesRealIvl: TestMessage(ProperlyIncludedIn_ProperlyIncludesRealIvl, 'ProperlyIncludedIn_ProperlyIncludesRealIvl', toString(true), toString(ProperlyIncludedIn_ProperlyIncludesRealIvl))
define test_ProperlyIncludedIn_NotProperlyIncludesRealIvl: TestMessage(not ProperlyIncludedIn_NotProperlyIncludesRealIvl, 'ProperlyIncludedIn_NotProperlyIncludesRealIvl', toString(false), toString(ProperlyIncludedIn_NotProperlyIncludesRealIvl))
define test_ProperlyIncludedIn_ProperlyIncludesDateIvl: TestMessage(ProperlyIncludedIn_ProperlyIncludesDateIvl, 'ProperlyIncludedIn_ProperlyIncludesDateIvl', toString(true), toString(ProperlyIncludedIn_ProperlyIncludesDateIvl))
define test_ProperlyIncludedIn_NotProperlyIncludesDateIvl: TestMessage(not ProperlyIncludedIn_NotProperlyIncludesDateIvl, 'ProperlyIncludedIn_NotProperlyIncludesDateIvl', toString(false), toString(ProperlyIncludedIn_NotProperlyIncludesDateIvl))
define test_ProperlyIncludedIn_PosInfEndProperlyIncludedInDateIvl: TestMessage(ProperlyIncludedIn_PosInfEndProperlyIncludedInDateIvl, 'ProperlyIncludedIn_PosInfEndProperlyIncludedInDateIvl', toString(true), toString(ProperlyIncludedIn_PosInfEndProperlyIncludedInDateIvl))
define test_ProperlyIncludedIn_PosInfEndNotProperlyIncludedInDateIvl: TestMessage(not ProperlyIncludedIn_PosInfEndNotProperlyIncludedInDateIvl, 'ProperlyIncludedIn_PosInfEndNotProperlyIncludedInDateIvl', toString(false), toString(ProperlyIncludedIn_PosInfEndNotProperlyIncludedInDateIvl))
define test_ProperlyIncludedIn_UnknownEndMayBeProperlyIncludedInDateIvl: TestMessage(ProperlyIncludedIn_UnknownEndMayBeProperlyIncludedInDateIvl is null, 'ProperlyIncludedIn_UnknownEndMayBeProperlyIncludedInDateIvl', 'null', toString(ProperlyIncludedIn_UnknownEndMayBeProperlyIncludedInDateIvl))
define test_ProperlyIncludedIn_ProperlyIncludesDayOfIvlLowEdge: TestMessage(ProperlyIncludedIn_ProperlyIncludesDayOfIvlLowEdge, 'ProperlyIncludedIn_ProperlyIncludesDayOfIvlLowEdge', toString(true), toString(ProperlyIncludedIn_ProperlyIncludesDayOfIvlLowEdge))
define test_ProperlyIncludedIn_ProperlyIncludesDayOfIvlHighEdge: TestMessage(ProperlyIncludedIn_ProperlyIncludesDayOfIvlHighEdge, 'ProperlyIncludedIn_ProperlyIncludesDayOfIvlHighEdge', toString(true), toString(ProperlyIncludedIn_ProperlyIncludesDayOfIvlHighEdge))
define test_ProperlyIncludedIn_ProperlyIncludesDayOfIvlNearEdges: TestMessage(ProperlyIncludedIn_ProperlyIncludesDayOfIvlNearEdges, 'ProperlyIncludedIn_ProperlyIncludesDayOfIvlNearEdges', toString(true), toString(ProperlyIncludedIn_ProperlyIncludesDayOfIvlNearEdges))
define test_ProperlyIncludedIn_NotProperlyIncludesDayOfIvlSameEdges: TestMessage(not ProperlyIncludedIn_NotProperlyIncludesDayOfIvlSameEdges, 'ProperlyIncludedIn_NotProperlyIncludesDayOfIvlSameEdges', toString(false), toString(ProperlyIncludedIn_NotProperlyIncludesDayOfIvlSameEdges))
define test_ProperlyIncludedIn_MayProperlyIncludeDayOfIvlVeryImpreciseLow: TestMessage(ProperlyIncludedIn_MayProperlyIncludeDayOfIvlVeryImpreciseLow is null, 'ProperlyIncludedIn_MayProperlyIncludeDayOfIvlVeryImpreciseLow', 'null', toString(ProperlyIncludedIn_MayProperlyIncludeDayOfIvlVeryImpreciseLow))
define test_ProperlyIncludedIn_MayProperlyIncludeDayOfIvlVeryImpreciseHigh: TestMessage(ProperlyIncludedIn_MayProperlyIncludeDayOfIvlVeryImpreciseHigh is null, 'ProperlyIncludedIn_MayProperlyIncludeDayOfIvlVeryImpreciseHigh', 'null', toString(ProperlyIncludedIn_MayProperlyIncludeDayOfIvlVeryImpreciseHigh))
define test_ProperlyIncludedIn_MayProperlyIncludeDayOfIvlVeryImpreciseLowAndHigh: TestMessage(ProperlyIncludedIn_MayProperlyIncludeDayOfIvlVeryImpreciseLowAndHigh is null, 'ProperlyIncludedIn_MayProperlyIncludeDayOfIvlVeryImpreciseLowAndHigh', 'null', toString(ProperlyIncludedIn_MayProperlyIncludeDayOfIvlVeryImpreciseLowAndHigh))
define test_ProperlyIncludedIn_MayProperlyIncludeDayOfIvlVeryImpreciseSurrounding: TestMessage(ProperlyIncludedIn_MayProperlyIncludeDayOfIvlVeryImpreciseSurrounding is null, 'ProperlyIncludedIn_MayProperlyIncludeDayOfIvlVeryImpreciseSurrounding', 'null', toString(ProperlyIncludedIn_MayProperlyIncludeDayOfIvlVeryImpreciseSurrounding))

// After
define After_AfterIntIvl: Interval[5, 10] after Interval[2, 4]
define After_NotAfterIntIvl: Interval[5, 10] after Interval[2, 5]
define After_AfterRealIvl: Interval[1.234, 2.345] after Interval[0.0, 1.23]
define After_NotAfterRealIvl: Interval[1.234, 2.345] after Interval[0.0, 1.234]
define After_AfterDateIvl: DateIvl after Interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2012, 3, 1, 0, 0, 0, 0))
define After_NotAfterDateIvl: DateIvl after Interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2012, 3, 1, 0, 0, 0, 0)]
define After_AfterImpreciseDateIvl: DateIvl after Interval[DateTime(2012, 1), DateTime(2012, 2)]
// TODO - this test should return null, but Bonnie expects false
// define After_NotAfterImpreciseDateIvl: DateIvl after Interval[DateTime(2012, 1), DateTime(2012, 3)]
define After_MayBeAfterImpreciseDateIvl: DateIvl after Interval[DateTime(2012), DateTime(2012)]
define After_AfterDayOfIvl: PrecisionDateIvl after day of Interval[DateTime(2012, 1, 2, 0, 0, 0, 0), DateTime(2012, 3, 1, 0, 0, 0, 0)]
define After_BeforeDayOfIvl: PrecisionDateIvl after day of Interval[DateTime(2012, 9, 3, 0, 0, 0, 0), DateTime(2012, 12, 1, 0, 0, 0, 0)]
define After_StartsSameDayOfIvlEnd: PrecisionDateIvl after day of Interval[DateTime(2012, 9, 2, 23, 59, 59, 999), DateTime(2012, 10, 2, 0, 0, 0, 0)]
define After_EndsSameDayOfIvlStart: PrecisionDateIvl after day of Interval[DateTime(2012, 1, 2, 0, 0, 0, 0), DateTime(2012, 3, 2, 0, 0, 0, 0)]
define After_MayBeAfterDayOfImpreciseIvl: PrecisionDateIvl after day of Interval[DateTime(2012, 1), DateTime(2012, 3)]
define After_MayBeBeforeDayOfImpreciseIvl: PrecisionDateIvl after day of Interval[DateTime(2012, 9), DateTime(2012, 12)]
define After_ImpreciseAfterDateIvl: ImpDateIvl after Interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2012, 3, 1, 0, 0, 0, 0))
// TODO - this test should return null, but Bonnie expects false
// define After_ImpreciseNotAfterDateIvl: ImpDateIvl after Interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2012, 4, 1, 0, 0, 0, 0))
define After_ImpreciseMayBeAfterDateIvl: ImpDateIvl after Interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2012, 3, 15, 0, 0, 0, 0))
define After_NegInfBegNotAfterIntIvl: Interval[null, 100] after Interval[-100, 0]
define After_UnknownBegMayBeAfterIntIvl: Interval(null, 100] after Interval[-100, 0]
// TODO - this test should return null, but Bonnie expects false
// define After_UnknownBegNotAfterIntIvl: Interval(null, 5] after Interval[0, 100]
define After_PosInfEndAfterIntIvl: Interval[0, null] after Interval[-100, -20]
define After_PosInfEndNotAfterIntIvl: Interval[0, null] after Interval[-100, 0]
define After_UnknownEndAfterIntIvl: Interval[0, null) after Interval[-100, -20]
define After_UnknownEndNotAfterIntIvl: Interval[0, null) after Interval[-100, 0]
define After_NegInfBegNotAfterDateIvl: Interval[null, DateTime(2013, 1, 1, 0, 0, 0, 0)) after Interval[DateTime(2000, 1, 1, 0, 0, 0, 0), DateTime(2010, 1, 1, 0, 0, 0, 0)]
define After_UnknownBegMayBeAfterDateIvl: Interval(null, DateTime(2013, 1, 1, 0, 0, 0, 0)) after Interval[DateTime(2000, 1, 1, 0, 0, 0, 0), DateTime(2010, 1, 1, 0, 0, 0, 0)]
// TODO - this test should return null, but Bonnie expects false
// define After_UnknownBegNotAfterDateIvl: Interval(null, DateTime(2013, 1, 1, 0, 0, 0, 0)) after Interval[DateTime(2000, 1, 1, 0, 0, 0, 0), DateTime(2020, 1, 1, 0, 0, 0, 0)]
define After_PosInfEndAfterDateIvl: Interval[DateTime(2013, 1, 1, 0, 0, 0, 0), null] after Interval[DateTime(2000, 1, 1, 0, 0, 0, 0), DateTime(2010, 1, 1, 0, 0, 0, 0)]
define After_PosInfEndNotAfterDateIvl: Interval[DateTime(2013, 1, 1, 0, 0, 0, 0), null] after Interval[DateTime(2000, 1, 1, 0, 0, 0, 0), DateTime(2020, 1, 1, 0, 0, 0, 0)]
define After_UnknownEndAfterDateIvl: Interval[DateTime(2013, 1, 1, 0, 0, 0, 0), null) after Interval[DateTime(2000, 1, 1, 0, 0, 0, 0), DateTime(2010, 1, 1, 0, 0, 0, 0)]
define After_UnknownEndNotAfterDateIvl: Interval[DateTime(2013, 1, 1, 0, 0, 0, 0), null) after Interval[DateTime(2020, 1, 1, 0, 0, 0, 0), DateTime(2040, 1, 1, 0, 0, 0, 0)]

define test_After_AfterIntIvl: TestMessage(After_AfterIntIvl, 'After_AfterIntIvl', toString(true), toString(After_AfterIntIvl))
define test_After_NotAfterIntIvl: TestMessage(not After_NotAfterIntIvl, 'After_NotAfterIntIvl', toString(false), toString(After_NotAfterIntIvl))
define test_After_AfterRealIvl: TestMessage(After_AfterRealIvl, 'After_AfterRealIvl', toString(true), toString(After_AfterRealIvl))
define test_After_NotAfterRealIvl: TestMessage(not After_NotAfterRealIvl, 'After_NotAfterRealIvl', toString(false), toString(After_NotAfterRealIvl))
define test_After_AfterDateIvl: TestMessage(After_AfterDateIvl, 'After_AfterDateIvl', toString(true), toString(After_AfterDateIvl))
define test_After_NotAfterDateIvl: TestMessage(not After_NotAfterDateIvl, 'After_NotAfterDateIvl', toString(false), toString(After_NotAfterDateIvl))
define test_After_AfterImpreciseDateIvl: TestMessage(After_AfterImpreciseDateIvl, 'After_AfterImpreciseDateIvl', toString(true), toString(After_AfterImpreciseDateIvl))
// define test_After_NotAfterImpreciseDateIvl: TestMessage(not After_NotAfterImpreciseDateIvl, 'After_NotAfterImpreciseDateIvl', toString(false), toString(After_NotAfterImpreciseDateIvl))
define test_After_MayBeAfterImpreciseDateIvl: TestMessage(After_MayBeAfterImpreciseDateIvl is null, 'After_MayBeAfterImpreciseDateIvl', 'null', toString(After_MayBeAfterImpreciseDateIvl))
define test_After_AfterDayOfIvl: TestMessage(After_AfterDayOfIvl, 'After_AfterDayOfIvl', toString(true), toString(After_AfterDayOfIvl))
define test_After_BeforeDayOfIvl: TestMessage(not After_BeforeDayOfIvl, 'After_BeforeDayOfIvl', toString(false), toString(After_BeforeDayOfIvl))
define test_After_StartsSameDayOfIvlEnd: TestMessage(not After_StartsSameDayOfIvlEnd, 'After_StartsSameDayOfIvlEnd', toString(false), toString(After_StartsSameDayOfIvlEnd))
define test_After_EndsSameDayOfIvlStart: TestMessage(not After_EndsSameDayOfIvlStart, 'After_EndsSameDayOfIvlStart', toString(false), toString(After_EndsSameDayOfIvlStart))
define test_After_MayBeAfterDayOfImpreciseIvl: TestMessage(After_MayBeAfterDayOfImpreciseIvl is null, 'After_MayBeAfterDayOfImpreciseIvl', 'null', toString(After_MayBeAfterDayOfImpreciseIvl))
define test_After_MayBeBeforeDayOfImpreciseIvl: TestMessage(not After_MayBeBeforeDayOfImpreciseIvl, 'After_MayBeBeforeDayOfImpreciseIvl', toString(false), toString(After_MayBeBeforeDayOfImpreciseIvl))
define test_After_ImpreciseAfterDateIvl: TestMessage(After_ImpreciseAfterDateIvl, 'After_ImpreciseAfterDateIvl', toString(true), toString(After_ImpreciseAfterDateIvl))
// define test_After_ImpreciseNotAfterDateIvl: TestMessage(not After_ImpreciseNotAfterDateIvl, 'After_ImpreciseNotAfterDateIvl', toString(false), toString(After_ImpreciseNotAfterDateIvl))
define test_After_ImpreciseMayBeAfterDateIvl: TestMessage(After_ImpreciseMayBeAfterDateIvl is null, 'After_ImpreciseMayBeAfterDateIvl', 'null', toString(After_ImpreciseMayBeAfterDateIvl))
define test_After_NegInfBegNotAfterIntIvl: TestMessage(not After_NegInfBegNotAfterIntIvl, 'After_NegInfBegNotAfterIntIvl', toString(false), toString(After_NegInfBegNotAfterIntIvl))
define test_After_UnknownBegMayBeAfterIntIvl: TestMessage(After_UnknownBegMayBeAfterIntIvl is null, 'After_UnknownBegMayBeAfterIntIvl', 'null', toString(After_UnknownBegMayBeAfterIntIvl))
// define test_After_UnknownBegNotAfterIntIvl: TestMessage(not After_UnknownBegNotAfterIntIvl, 'After_UnknownBegNotAfterIntIvl', toString(false), toString(After_UnknownBegNotAfterIntIvl))
define test_After_PosInfEndAfterIntIvl: TestMessage(After_PosInfEndAfterIntIvl, 'After_PosInfEndAfterIntIvl', toString(true), toString(After_PosInfEndAfterIntIvl))
define test_After_PosInfEndNotAfterIntIvl: TestMessage(not After_PosInfEndNotAfterIntIvl, 'After_PosInfEndNotAfterIntIvl', toString(false), toString(After_PosInfEndNotAfterIntIvl))
define test_After_UnknownEndAfterIntIvl: TestMessage(After_UnknownEndAfterIntIvl, 'After_UnknownEndAfterIntIvl', toString(true), toString(After_UnknownEndAfterIntIvl))
define test_After_UnknownEndNotAfterIntIvl: TestMessage(not After_UnknownEndNotAfterIntIvl, 'After_UnknownEndNotAfterIntIvl', toString(false), toString(After_UnknownEndNotAfterIntIvl))
define test_After_NegInfBegNotAfterDateIvl: TestMessage(not After_NegInfBegNotAfterDateIvl, 'After_NegInfBegNotAfterDateIvl', toString(false), toString(After_NegInfBegNotAfterDateIvl))
define test_After_UnknownBegMayBeAfterDateIvl: TestMessage(After_UnknownBegMayBeAfterDateIvl is null, 'After_UnknownBegMayBeAfterDateIvl', 'null', toString(After_UnknownBegMayBeAfterDateIvl))
// define test_After_UnknownBegNotAfterDateIvl: TestMessage(not After_UnknownBegNotAfterDateIvl, 'After_UnknownBegNotAfterDateIvl', toString(false), toString(After_UnknownBegNotAfterDateIvl))
define test_After_PosInfEndAfterDateIvl: TestMessage(After_PosInfEndAfterDateIvl, 'After_PosInfEndAfterDateIvl', toString(true), toString(After_PosInfEndAfterDateIvl))
define test_After_PosInfEndNotAfterDateIvl: TestMessage(not After_PosInfEndNotAfterDateIvl, 'After_PosInfEndNotAfterDateIvl', toString(false), toString(After_PosInfEndNotAfterDateIvl))
define test_After_UnknownEndAfterDateIvl: TestMessage(After_UnknownEndAfterDateIvl, 'After_UnknownEndAfterDateIvl', toString(true), toString(After_UnknownEndAfterDateIvl))
define test_After_UnknownEndNotAfterDateIvl: TestMessage(not After_UnknownEndNotAfterDateIvl, 'After_UnknownEndNotAfterDateIvl', toString(false), toString(After_UnknownEndNotAfterDateIvl))

// Before
define Before_BeforeIntIvl: Interval[2, 4] before Interval[5, 10]
define Before_NotBeforeIntIvl: Interval[2, 5] before Interval[5, 10]
define Before_BeforeRealIvl: Interval[0.0, 1.23] before Interval[1.234, 2.345]
define Before_NotBeforeRealIvl: Interval[1.234, 2.345] before Interval[0.0, 1.234]
define Before_BeforeDateIvl: Interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2012, 3, 1, 0, 0, 0, 0)) before DateIvl
define Before_NotBeforeDateIvl: Interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2012, 3, 1, 0, 0, 0, 0)] before DateIvl
define Before_BeforeImpreciseDateIvl: DateIvl before Interval[DateTime(2012, 9), DateTime(2012, 12)]
// TODO - this test should return null, but Bonnie expects false
// define Before_NotBeforeImpreciseDateIvl: DateIvl before Interval[DateTime(2012, 8), DateTime(2012, 12)]
define Before_MayBeBeforeImpreciseDateIvl: DateIvl before Interval[DateTime(2012), DateTime(2012)]
define Before_AfterDayOfIvl: PrecisionDateIvl before day of Interval[DateTime(2012, 1, 2, 0, 0, 0, 0), DateTime(2012, 3, 1, 0, 0, 0, 0)]
define Before_BeforeDayOfIvl: PrecisionDateIvl before day of Interval[DateTime(2012, 9, 3, 0, 0, 0, 0), DateTime(2012, 12, 1, 0, 0, 0, 0)]
define Before_StartsSameDayOfIvlEnd: PrecisionDateIvl before day of Interval[DateTime(2012, 9, 2, 23, 59, 59, 999), DateTime(2012, 10, 2, 0, 0, 0, 0)]
define Before_EndsSameDayOfIvlStart: PrecisionDateIvl before day of Interval[DateTime(2012, 1, 2, 0, 0, 0, 0), DateTime(2012, 3, 2, 0, 0, 0, 0)]
define Before_MayBeAfterDayOfImpreciseIvl: PrecisionDateIvl before day of Interval[DateTime(2012, 1), DateTime(2012, 3)]
define Before_MayBeBeforeDayOfImpreciseIvl: PrecisionDateIvl before day of Interval[DateTime(2012, 9), DateTime(2012, 12)]
define Before_ImpreciseBeforeDateIvl: ImpDateIvl before Interval[DateTime(2012, 9, 1, 0, 0, 0, 0), DateTime(2012, 12, 1, 0, 0, 0, 0))
// TODO - this test should return null, but Bonnie expects false
// define Before_ImpreciseNotBeforeDateIvl: ImpDateIvl before Interval[DateTime(2012, 8, 1, 0, 0, 0, 0), DateTime(2012, 12, 1, 0, 0, 0, 0))
define Before_ImpreciseMayBeBeforeDateIvl: ImpDateIvl before Interval[DateTime(2012, 8, 15, 0, 0, 0, 0), DateTime(2012, 12, 15, 0, 0, 0, 0))
define Before_NegInfBegBeforeIntIvl: Interval[null, 100] before Interval[101, 200]
define Before_NegInfBegNotBeforeIntIvl: Interval[null, 100] before Interval[100, 200]
define Before_UnknownBegBeforeIntIvl: Interval(null, 100] before Interval[101, 200]
define Before_UnknownBegNotBeforeIntIvl: Interval(null, 5] before Interval[0, 100]
define Before_PosInfEndNotBeforeIntIvl: Interval[0, null] before Interval[1000, 2000]
define Before_UnknownEndMayBeBeforeIntIvl: Interval[0, null) before Interval[1000, 2000]
// TODO - this test should return null, but Bonnie expects false
// define Before_UnknownEndNotBeforeIntIvl: Interval[0, null) before Interval[-100, 0]
define Before_NegInfBegBeforeDateIvl: Interval[null, DateTime(2013, 1, 1, 0, 0, 0, 0)) before Interval[DateTime(2020, 1, 1, 0, 0, 0, 0), DateTime(2030, 1, 1, 0, 0, 0, 0)]
define Before_NegInfBegNotBeforeDateIvl: Interval[null, DateTime(2013, 1, 1, 0, 0, 0, 0)) before Interval[DateTime(2000, 1, 1, 0, 0, 0, 0), DateTime(2010, 1, 1, 0, 0, 0, 0)]
define Before_UnknownBegBeforeDateIvl: Interval(null, DateTime(2013, 1, 1, 0, 0, 0, 0)) before Interval[DateTime(2020, 1, 1, 0, 0, 0, 0), DateTime(2030, 1, 1, 0, 0, 0, 0)]
define Before_UnknownBegNotBeforeDateIvl: Interval(null, DateTime(2013, 1, 1, 0, 0, 0, 0)) before Interval[DateTime(2000, 1, 1, 0, 0, 0, 0), DateTime(2020, 1, 1, 0, 0, 0, 0)]
define Before_PosInfEndNotBeforeDateIvl: Interval[DateTime(2013, 1, 1, 0, 0, 0, 0), null] before Interval[DateTime(2000, 1, 1, 0, 0, 0, 0), DateTime(2020, 1, 1, 0, 0, 0, 0)]
define Before_UnknownEndMayBeBeforeDateIvl: Interval[DateTime(2013, 1, 1, 0, 0, 0, 0), null) before Interval[DateTime(2020, 1, 1, 0, 0, 0, 0), DateTime(2040, 1, 1, 0, 0, 0, 0)]
// TODO - this test should return null, but Bonnie expects false
// define Before_UnknownEndNotBeforeDateIvl: Interval[DateTime(2013, 1, 1, 0, 0, 0, 0), null) before Interval[DateTime(2000, 1, 1, 0, 0, 0, 0), DateTime(2013, 1, 1, 0, 0, 0, 0)]

define test_Before_BeforeIntIvl: TestMessage(Before_BeforeIntIvl, 'Before_BeforeIntIvl', toString(true), toString(Before_BeforeIntIvl))
define test_Before_NotBeforeIntIvl: TestMessage(not Before_NotBeforeIntIvl, 'Before_NotBeforeIntIvl', toString(false), toString(Before_NotBeforeIntIvl))
define test_Before_BeforeRealIvl: TestMessage(Before_BeforeRealIvl, 'Before_BeforeRealIvl', toString(true), toString(Before_BeforeRealIvl))
define test_Before_NotBeforeRealIvl: TestMessage(not Before_NotBeforeRealIvl, 'Before_NotBeforeRealIvl', toString(false), toString(Before_NotBeforeRealIvl))
define test_Before_BeforeDateIvl: TestMessage(Before_BeforeDateIvl, 'Before_BeforeDateIvl', toString(true), toString(Before_BeforeDateIvl))
define test_Before_NotBeforeDateIvl: TestMessage(not Before_NotBeforeDateIvl, 'Before_NotBeforeDateIvl', toString(false), toString(Before_NotBeforeDateIvl))
define test_Before_BeforeImpreciseDateIvl: TestMessage(Before_BeforeImpreciseDateIvl, 'Before_BeforeImpreciseDateIvl', toString(true), toString(Before_BeforeImpreciseDateIvl))
// define test_Before_NotBeforeImpreciseDateIvl: TestMessage(not Before_NotBeforeImpreciseDateIvl, 'Before_NotBeforeImpreciseDateIvl', toString(false), toString(Before_NotBeforeImpreciseDateIvl))
define test_Before_MayBeBeforeImpreciseDateIvl: TestMessage(Before_MayBeBeforeImpreciseDateIvl is null, 'Before_MayBeBeforeImpreciseDateIvl', 'null', toString(Before_MayBeBeforeImpreciseDateIvl))
define test_Before_AfterDayOfIvl: TestMessage(not Before_AfterDayOfIvl, 'Before_AfterDayOfIvl', toString(false), toString(Before_AfterDayOfIvl))
define test_Before_BeforeDayOfIvl: TestMessage(Before_BeforeDayOfIvl, 'Before_BeforeDayOfIvl', toString(true), toString(Before_BeforeDayOfIvl))
define test_Before_StartsSameDayOfIvlEnd: TestMessage(not Before_StartsSameDayOfIvlEnd, 'Before_StartsSameDayOfIvlEnd', toString(false), toString(Before_StartsSameDayOfIvlEnd))
define test_Before_EndsSameDayOfIvlStart: TestMessage(not Before_EndsSameDayOfIvlStart, 'Before_EndsSameDayOfIvlStart', toString(false), toString(Before_EndsSameDayOfIvlStart))
define test_Before_MayBeAfterDayOfImpreciseIvl: TestMessage(not Before_MayBeAfterDayOfImpreciseIvl, 'Before_MayBeAfterDayOfImpreciseIvl', toString(false), toString(Before_MayBeAfterDayOfImpreciseIvl))
define test_Before_MayBeBeforeDayOfImpreciseIvl: TestMessage(Before_MayBeBeforeDayOfImpreciseIvl is null, 'Before_MayBeBeforeDayOfImpreciseIvl', 'null', toString(Before_MayBeBeforeDayOfImpreciseIvl))
define test_Before_ImpreciseBeforeDateIvl: TestMessage(Before_ImpreciseBeforeDateIvl, 'Before_ImpreciseBeforeDateIvl', toString(true), toString(Before_ImpreciseBeforeDateIvl))
// define test_Before_ImpreciseNotBeforeDateIvl: TestMessage(not Before_ImpreciseNotBeforeDateIvl, 'Before_ImpreciseNotBeforeDateIvl', toString(false), toString(Before_ImpreciseNotBeforeDateIvl))
define test_Before_ImpreciseMayBeBeforeDateIvl: TestMessage(Before_ImpreciseMayBeBeforeDateIvl is null, 'Before_ImpreciseMayBeBeforeDateIvl', 'null', toString(Before_ImpreciseMayBeBeforeDateIvl))
define test_Before_NegInfBegNotBeforeIntIvl: TestMessage(not Before_NegInfBegNotBeforeIntIvl, 'Before_NegInfBegNotBeforeIntIvl', toString(false), toString(Before_NegInfBegNotBeforeIntIvl))
define test_Before_UnknownBegBeforeIntIvl: TestMessage(Before_UnknownBegBeforeIntIvl, 'Before_UnknownBegBeforeIntIvl', toString(true), toString(Before_UnknownBegBeforeIntIvl))
define test_Before_UnknownBegNotBeforeIntIvl: TestMessage(not Before_UnknownBegNotBeforeIntIvl, 'Before_UnknownBegNotBeforeIntIvl', toString(false), toString(Before_UnknownBegNotBeforeIntIvl))
define test_Before_PosInfEndNotBeforeIntIvl: TestMessage(not Before_PosInfEndNotBeforeIntIvl, 'Before_PosInfEndNotBeforeIntIvl', toString(false), toString(Before_PosInfEndNotBeforeIntIvl))
define test_Before_UnknownEndMayBeBeforeIntIvl: TestMessage(Before_UnknownEndMayBeBeforeIntIvl is null, 'Before_UnknownEndMayBeBeforeIntIvl', 'null', toString(Before_UnknownEndMayBeBeforeIntIvl))
// define test_Before_UnknownEndNotBeforeIntIvl: TestMessage(not Before_UnknownEndNotBeforeIntIvl, 'Before_UnknownEndNotBeforeIntIvl', toString(false), toString(Before_UnknownEndNotBeforeIntIvl))
define test_Before_NegInfBegNotBeforeDateIvl: TestMessage(not Before_NegInfBegNotBeforeDateIvl, 'Before_NegInfBegNotBeforeDateIvl', toString(false), toString(Before_NegInfBegNotBeforeDateIvl))
define test_Before_UnknownBegBeforeDateIvl: TestMessage(Before_UnknownBegBeforeDateIvl, 'Before_UnknownBegBeforeDateIvl', toString(true), toString(Before_UnknownBegBeforeDateIvl))
define test_Before_UnknownBegNotBeforeDateIvl: TestMessage(not Before_UnknownBegNotBeforeDateIvl, 'Before_UnknownBegNotBeforeDateIvl', toString(false), toString(Before_UnknownBegNotBeforeDateIvl))
define test_Before_PosInfEndNotBeforeDateIvl: TestMessage(not Before_PosInfEndNotBeforeDateIvl, 'Before_PosInfEndNotBeforeDateIvl', toString(false), toString(Before_PosInfEndNotBeforeDateIvl))
define test_Before_UnknownEndMayBeBeforeDateIvl: TestMessage(Before_UnknownEndMayBeBeforeDateIvl is null, 'Before_UnknownEndMayBeBeforeDateIvl', 'null', toString(Before_UnknownEndMayBeBeforeDateIvl))
// define test_Before_UnknownEndNotBeforeDateIvl: TestMessage(not Before_UnknownEndNotBeforeDateIvl, 'Before_UnknownEndNotBeforeDateIvl', toString(false), toString(Before_UnknownEndNotBeforeDateIvl))

// Meets
define Meets_MeetsAfterIntIvl: Interval[11, 15] meets Interval[5, 10]
define Meets_MeetsBeforeIntIvl: Interval[1, 4] meets Interval[5, 10]
define Meets_NotMeetsIntIvl: Interval[1, 2] meets Interval[5, 10]
define Meets_MeetsAfterRealIvl: Interval[1.50000001, 2.5] meets Interval[0.5, 1.5]
define Meets_MeetsBeforeRealIvl: Interval[0.5, 1.5] meets Interval[1.50000001, 2.5]
define Meets_NotMeetsRealIvl: Interval[0.0, 1.0] meets Interval[1.1, 2.0]
define Meets_MeetsAfterDateIvl: Interval[DateTime(2012, 9, 1, 0, 0, 0, 0), DateTime(2012, 12, 1, 0, 0, 0, 0)) meets DateIvl
define Meets_MeetsBeforeDateIvl: Interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2012, 3, 1, 0, 0, 0, 0)) meets DateIvl
define Meets_NotMeetsDateIvl: Interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2012, 2, 29, 0, 0, 0, 0)) meets DateIvl
define Meets_MayMeetAfterImpreciseDateIvl: DateIvl meets Interval[DateTime(2012, 1), DateTime(2012, 2)]
define Meets_MayMeetBeforeImpreciseDateIvl: DateIvl meets Interval[DateTime(2012, 9), DateTime(2012, 12)]
define Meets_NotMeetsImpreciseDateIvl: DateIvl meets Interval[DateTime(2012, 1), DateTime(2012, 12)]
define Meets_MeetsAfterDayOfIvl: PrecisionDateIvl meets day of Interval[DateTime(2012, 1, 2, 0, 0, 0, 0), DateTime(2012, 3, 1, 0, 0, 0, 0)]
define Meets_MeetsBeforeDayOfIvl: PrecisionDateIvl meets day of Interval[DateTime(2012, 9, 3, 0, 0, 0, 0), DateTime(2012, 12, 1, 0, 0, 0, 0)]
define Meets_NotMeetsDayOfIvl: PrecisionDateIvl meets day of Interval[DateTime(2012, 1, 2, 0, 0, 0, 0), DateTime(2012, 3, 2, 0, 0, 0, 0)]
define Meets_NotMeetsDayOfImpreciseIVL: PrecisionDateIvl meets day of Interval[DateTime(2012, 1), DateTime(2012, 2)]
define Meets_MayMeetAfterDayOfImpreciseIvl: PrecisionDateIvl meets day of Interval[DateTime(2012, 1), DateTime(2012, 3)]
define Meets_MayMeetBeforeDayOfImpreciseIvl: PrecisionDateIvl meets day of Interval[DateTime(2012, 9), DateTime(2012, 12)]
define Meets_ImpreciseMayMeetAfterDateIvl: ImpDateIvl meets Interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2012, 2, 29, 23, 59, 59, 999)]
define Meets_ImpreciseMayMeetBeforeDateIvl: ImpDateIvl meets Interval[DateTime(2012, 9, 1, 0, 0, 0, 0), DateTime(2012, 12, 31, 23, 59, 59, 999)]
define Meets_ImpreciseNotMeetsDateIvl: ImpDateIvl meets Interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2012, 12, 31, 23, 59, 59, 999))
define Meets_NegInfBegMeetsBeforeIntIvl: Interval[null, 100] meets Interval[101, 200]
define Meets_NegInfBegNotMeetsIntIvl: Interval[null, 100] meets Interval[100, 200]
define Meets_IntIvlNotMeetsNegInfBeg: Interval[100, 200] meets Interval[null, 400]
define Meets_UnknownBegMeetsBeforeIntIvl: Interval(null, 100] meets Interval[101, 200]
define Meets_UnknownBegMayMeetAfterIntIvl: Interval(null, 100] meets Interval[0, 50]
define Meets_UnknownBegNotMeetsIntIvl: Interval(null, 5] meets Interval[0, 100]
define Meets_IntIvlMayMeetBeforeUnknownBeg: Interval[0, 100] meets Interval(null, 400)
define Meets_PosInfEndMeetsAfterIntIvl: Interval[100, null] meets Interval[0, 99]
define Meets_PosInfEndNotMeetsIntIvl: Interval[0, null] meets Interval[1000, 2000]
define Meets_IntIvlNotMeetsPosInfEnd: Interval[100, 200] meets Interval[0, null]
define Meets_UnknownEndMeetsAfterIntIvl: Interval[100, null) meets Interval[0, 99]
define Meets_UnknownEndMayMeetBeforeIntIvl: Interval[0, null) meets Interval[50, 100]
define Meets_UnknownEndNotMeetsIntIvl: Interval[0, null) meets Interval[0, 100]
define Meets_IntIvlMayMeetAfterUnknownEnd: Interval[0, 100] meets Interval[-100, null)
define Meets_NegInfBegMeetsBeforeDateIvl: Interval[null, DateTime(2013, 1, 1, 0, 0, 0, 0)) meets Interval[DateTime(2013, 1, 1, 0, 0, 0, 0), DateTime(2030, 1, 1, 0, 0, 0, 0)]
define Meets_NegInfBegNotMeetsDateIvl: Interval[null, DateTime(2013, 1, 1, 0, 0, 0, 0)) meets Interval[DateTime(2000, 1, 1, 0, 0, 0, 0), DateTime(2010, 1, 1, 0, 0, 0, 0)]
define Meets_DateIvlNotMeetsNegInfBeg: Interval[DateTime(2000, 1, 1, 0, 0, 0, 0), DateTime(2010, 1, 1, 0, 0, 0, 0)] meets Interval[null, DateTime(2013, 1, 1, 0, 0, 0, 0))
define Meets_UnknownBegMeetsBeforeDateIvl: Interval(null, DateTime(2013, 1, 1, 0, 0, 0, 0)) meets Interval[DateTime(2013, 1, 1, 0, 0, 0, 0), DateTime(2030, 1, 1, 0, 0, 0, 0)]
define Meets_UnknownBegMayMeetAfterDateIvl: Interval(null, DateTime(2013, 1, 1, 0, 0, 0, 0)) meets Interval[DateTime(2000, 1, 1, 0, 0, 0, 0), DateTime(2010, 1, 1, 0, 0, 0, 0)]
define Meets_UnknownBegNotMeetsDateIvl: Interval(null, DateTime(2013, 1, 1, 0, 0, 0, 0)) meets Interval[DateTime(2000, 1, 1, 0, 0, 0, 0), DateTime(2020, 1, 1, 0, 0, 0, 0)]
define Meets_DateIvlMayMeetBeforeUnknownBeg: Interval[DateTime(2000, 1, 1, 0, 0, 0, 0), DateTime(2010, 1, 1, 0, 0, 0, 0)] meets Interval(null, DateTime(2013, 1, 1, 0, 0, 0, 0))
define Meets_PosInfEndMeetsAfterDateIvl: Interval[DateTime(2013, 1, 1, 0, 0, 0, 0), null] meets Interval[DateTime(2000, 1, 1, 0, 0, 0, 0), DateTime(2013, 1, 1, 0, 0, 0, 0))
define Meets_PosInfEndNotMeetsDateIvl: Interval[DateTime(2013, 1, 1, 0, 0, 0, 0), null] meets Interval[DateTime(2000, 1, 1, 0, 0, 0, 0), DateTime(2020, 1, 1, 0, 0, 0, 0)]
define Meets_DateIvlNotMeetsPosInfEnd: Interval[DateTime(2000, 1, 1, 0, 0, 0, 0), DateTime(2020, 1, 1, 0, 0, 0, 0)] meets Interval[DateTime(2013, 1, 1, 0, 0, 0, 0), null]
define Meets_UnknownEndMeetsAfterDateIvl: Interval[DateTime(2013, 1, 1, 0, 0, 0, 0), null) meets Interval[DateTime(2010, 1, 1, 0, 0, 0, 0), DateTime(2013, 1, 1, 0, 0, 0, 0))
define Meets_UnknownEndMayMeetBeforeDateIvl: Interval[DateTime(2013, 1, 1, 0, 0, 0, 0), null) meets Interval[DateTime(2020, 1, 1, 0, 0, 0, 0), DateTime(2040, 1, 1, 0, 0, 0, 0)]
define Meets_UnknownEndNotMeetsDateIvl: Interval[DateTime(2013, 1, 1, 0, 0, 0, 0), null) meets Interval[DateTime(2000, 1, 1, 0, 0, 0, 0), DateTime(2013, 1, 1, 0, 0, 0, 0)]
define Meets_DateIvlMayMeetAfterUnknownEnd: Interval[DateTime(2020, 1, 1, 0, 0, 0, 0), DateTime(2040, 1, 1, 0, 0, 0, 0)] meets Interval[DateTime(2013, 1, 1, 0, 0, 0, 0), null)

define test_Meets_MeetsAfterIntIvl: TestMessage(Meets_MeetsAfterIntIvl, 'Meets_MeetsAfterIntIvl', toString(true), toString(Meets_MeetsAfterIntIvl))
define test_Meets_MeetsBeforeIntIvl: TestMessage(Meets_MeetsBeforeIntIvl, 'Meets_MeetsBeforeIntIvl', toString(true), toString(Meets_MeetsBeforeIntIvl))
define test_Meets_NotMeetsIntIvl: TestMessage(not Meets_NotMeetsIntIvl, 'Meets_NotMeetsIntIvl', toString(false), toString(Meets_NotMeetsIntIvl))
define test_Meets_MeetsAfterRealIvl: TestMessage(Meets_MeetsAfterRealIvl, 'Meets_MeetsAfterRealIvl', toString(true), toString(Meets_MeetsAfterRealIvl))
define test_Meets_MeetsBeforeRealIvl: TestMessage(Meets_MeetsBeforeRealIvl, 'Meets_MeetsBeforeRealIvl', toString(true), toString(Meets_MeetsBeforeRealIvl))
define test_Meets_NotMeetsRealIvl: TestMessage(not Meets_NotMeetsRealIvl, 'Meets_NotMeetsRealIvl', toString(false), toString(Meets_NotMeetsRealIvl))
define test_Meets_MeetsAfterDateIvl: TestMessage(Meets_MeetsAfterDateIvl, 'Meets_MeetsAfterDateIvl', toString(true), toString(Meets_MeetsAfterDateIvl))
define test_Meets_MeetsBeforeDateIvl: TestMessage(Meets_MeetsBeforeDateIvl, 'Meets_MeetsBeforeDateIvl', toString(true), toString(Meets_MeetsBeforeDateIvl))
define test_Meets_NotMeetsDateIvl: TestMessage(not Meets_NotMeetsDateIvl, 'Meets_NotMeetsDateIvl', toString(false), toString(Meets_NotMeetsDateIvl))
define test_Meets_MayMeetAfterImpreciseDateIvl: TestMessage(Meets_MayMeetAfterImpreciseDateIvl is null, 'Meets_MayMeetAfterImpreciseDateIvl', 'null', toString(Meets_MayMeetAfterImpreciseDateIvl))
define test_Meets_MayMeetBeforeImpreciseDateIvl: TestMessage(Meets_MayMeetBeforeImpreciseDateIvl is null, 'Meets_MayMeetBeforeImpreciseDateIvl', 'null', toString(Meets_MayMeetBeforeImpreciseDateIvl))
define test_Meets_NotMeetsImpreciseDateIvl: TestMessage(not Meets_NotMeetsImpreciseDateIvl, 'Meets_NotMeetsImpreciseDateIvl', toString(false), toString(Meets_NotMeetsImpreciseDateIvl))
define test_Meets_MeetsAfterDayOfIvl: TestMessage(Meets_MeetsAfterDayOfIvl, 'Meets_MeetsAfterDayOfIvl', toString(true), toString(Meets_MeetsAfterDayOfIvl))
define test_Meets_MeetsBeforeDayOfIvl: TestMessage(Meets_MeetsBeforeDayOfIvl, 'Meets_MeetsBeforeDayOfIvl', toString(true), toString(Meets_MeetsBeforeDayOfIvl))
define test_Meets_NotMeetsDayOfIvl: TestMessage(not Meets_NotMeetsDayOfIvl, 'Meets_NotMeetsDayOfIvl', toString(false), toString(Meets_NotMeetsDayOfIvl))
define test_Meets_NotMeetsDayOfImpreciseIVL: TestMessage(not Meets_NotMeetsDayOfImpreciseIVL, 'Meets_NotMeetsDayOfImpreciseIVL', toString(false), toString(Meets_NotMeetsDayOfImpreciseIVL))
define test_Meets_MayMeetAfterDayOfImpreciseIvl: TestMessage(Meets_MayMeetAfterDayOfImpreciseIvl is null, 'Meets_MayMeetAfterDayOfImpreciseIvl', 'null', toString(Meets_MayMeetAfterDayOfImpreciseIvl))
define test_Meets_MayMeetBeforeDayOfImpreciseIvl: TestMessage(Meets_MayMeetBeforeDayOfImpreciseIvl is null, 'Meets_MayMeetBeforeDayOfImpreciseIvl', 'null', toString(Meets_MayMeetBeforeDayOfImpreciseIvl))
define test_Meets_ImpreciseMayMeetAfterDateIvl: TestMessage(Meets_ImpreciseMayMeetAfterDateIvl is null, 'Meets_ImpreciseMayMeetAfterDateIvl', 'null', toString(Meets_ImpreciseMayMeetAfterDateIvl))
define test_Meets_ImpreciseMayMeetBeforeDateIvl: TestMessage(Meets_ImpreciseMayMeetBeforeDateIvl is null, 'Meets_ImpreciseMayMeetBeforeDateIvl', 'null', toString(Meets_ImpreciseMayMeetBeforeDateIvl))
define test_Meets_ImpreciseNotMeetsDateIvl: TestMessage(not Meets_ImpreciseNotMeetsDateIvl, 'Meets_ImpreciseNotMeetsDateIvl', toString(false), toString(Meets_ImpreciseNotMeetsDateIvl))
define test_Meets_NegInfBegMeetsBeforeIntIvl: TestMessage(Meets_NegInfBegMeetsBeforeIntIvl, 'Meets_NegInfBegMeetsBeforeIntIvl', toString(true), toString(Meets_NegInfBegMeetsBeforeIntIvl))
define test_Meets_NegInfBegNotMeetsIntIvl: TestMessage(not Meets_NegInfBegNotMeetsIntIvl, 'Meets_NegInfBegNotMeetsIntIvl', toString(false), toString(Meets_NegInfBegNotMeetsIntIvl))
define test_Meets_IntIvlNotMeetsNegInfBeg: TestMessage(not Meets_IntIvlNotMeetsNegInfBeg, 'Meets_IntIvlNotMeetsNegInfBeg', toString(false), toString(Meets_IntIvlNotMeetsNegInfBeg))
define test_Meets_UnknownBegMeetsBeforeIntIvl: TestMessage(Meets_UnknownBegMeetsBeforeIntIvl, 'Meets_UnknownBegMeetsBeforeIntIvl', toString(true), toString(Meets_UnknownBegMeetsBeforeIntIvl))
define test_Meets_UnknownBegMayMeetAfterIntIvl: TestMessage(Meets_UnknownBegMayMeetAfterIntIvl is null, 'Meets_UnknownBegMayMeetAfterIntIvl', 'null', toString(Meets_UnknownBegMayMeetAfterIntIvl))
define test_Meets_UnknownBegNotMeetsIntIvl: TestMessage(not Meets_UnknownBegNotMeetsIntIvl, 'Meets_UnknownBegNotMeetsIntIvl', toString(false), toString(Meets_UnknownBegNotMeetsIntIvl))
define test_Meets_IntIvlMayMeetBeforeUnknownBeg: TestMessage(Meets_IntIvlMayMeetBeforeUnknownBeg is null, 'Meets_IntIvlMayMeetBeforeUnknownBeg', 'null', toString(Meets_IntIvlMayMeetBeforeUnknownBeg))
define test_Meets_PosInfEndMeetsAfterIntIvl: TestMessage(Meets_PosInfEndMeetsAfterIntIvl, 'Meets_PosInfEndMeetsAfterIntIvl', toString(true), toString(Meets_PosInfEndMeetsAfterIntIvl))
define test_Meets_PosInfEndNotMeetsIntIvl: TestMessage(not Meets_PosInfEndNotMeetsIntIvl, 'Meets_PosInfEndNotMeetsIntIvl', toString(false), toString(Meets_PosInfEndNotMeetsIntIvl))
define test_Meets_IntIvlNotMeetsPosInfEnd: TestMessage(not Meets_IntIvlNotMeetsPosInfEnd, 'Meets_IntIvlNotMeetsPosInfEnd', toString(false), toString(Meets_IntIvlNotMeetsPosInfEnd))
define test_Meets_UnknownEndMeetsAfterIntIvl: TestMessage(Meets_UnknownEndMeetsAfterIntIvl, 'Meets_UnknownEndMeetsAfterIntIvl', toString(true), toString(Meets_UnknownEndMeetsAfterIntIvl))
define test_Meets_UnknownEndMayMeetBeforeIntIvl: TestMessage(Meets_UnknownEndMayMeetBeforeIntIvl is null, 'Meets_UnknownEndMayMeetBeforeIntIvl', 'null', toString(Meets_UnknownEndMayMeetBeforeIntIvl))
define test_Meets_UnknownEndNotMeetsIntIvl: TestMessage(not Meets_UnknownEndNotMeetsIntIvl, 'Meets_UnknownEndNotMeetsIntIvl', toString(false), toString(Meets_UnknownEndNotMeetsIntIvl))
define test_Meets_IntIvlMayMeetAfterUnknownEnd: TestMessage(Meets_IntIvlMayMeetAfterUnknownEnd is null, 'Meets_IntIvlMayMeetAfterUnknownEnd', 'null', toString(Meets_IntIvlMayMeetAfterUnknownEnd))
define test_Meets_NegInfBegMeetsBeforeDateIvl: TestMessage(Meets_NegInfBegMeetsBeforeDateIvl, 'Meets_NegInfBegMeetsBeforeDateIvl', toString(true), toString(Meets_NegInfBegMeetsBeforeDateIvl))
define test_Meets_NegInfBegNotMeetsDateIvl: TestMessage(not Meets_NegInfBegNotMeetsDateIvl, 'Meets_NegInfBegNotMeetsDateIvl', toString(false), toString(Meets_NegInfBegNotMeetsDateIvl))
define test_Meets_DateIvlNotMeetsNegInfBeg: TestMessage(not Meets_DateIvlNotMeetsNegInfBeg, 'Meets_DateIvlNotMeetsNegInfBeg', toString(false), toString(Meets_DateIvlNotMeetsNegInfBeg))
define test_Meets_UnknownBegMeetsBeforeDateIvl: TestMessage(Meets_UnknownBegMeetsBeforeDateIvl, 'Meets_UnknownBegMeetsBeforeDateIvl', toString(true), toString(Meets_UnknownBegMeetsBeforeDateIvl))
define test_Meets_UnknownBegMayMeetAfterDateIvl: TestMessage(Meets_UnknownBegMayMeetAfterDateIvl is null, 'Meets_UnknownBegMayMeetAfterDateIvl', 'null', toString(Meets_UnknownBegMayMeetAfterDateIvl))
define test_Meets_UnknownBegNotMeetsDateIvl: TestMessage(not Meets_UnknownBegNotMeetsDateIvl, 'Meets_UnknownBegNotMeetsDateIvl', toString(false), toString(Meets_UnknownBegNotMeetsDateIvl))
define test_Meets_DateIvlMayMeetBeforeUnknownBeg: TestMessage(Meets_DateIvlMayMeetBeforeUnknownBeg is null, 'Meets_DateIvlMayMeetBeforeUnknownBeg', 'null', toString(Meets_DateIvlMayMeetBeforeUnknownBeg))
define test_Meets_PosInfEndMeetsAfterDateIvl: TestMessage(Meets_PosInfEndMeetsAfterDateIvl, 'Meets_PosInfEndMeetsAfterDateIvl', toString(true), toString(Meets_PosInfEndMeetsAfterDateIvl))
define test_Meets_PosInfEndNotMeetsDateIvl: TestMessage(not Meets_PosInfEndNotMeetsDateIvl, 'Meets_PosInfEndNotMeetsDateIvl', toString(false), toString(Meets_PosInfEndNotMeetsDateIvl))
define test_Meets_DateIvlNotMeetsPosInfEnd: TestMessage(not Meets_DateIvlNotMeetsPosInfEnd, 'Meets_DateIvlNotMeetsPosInfEnd', toString(false), toString(Meets_DateIvlNotMeetsPosInfEnd))
define test_Meets_UnknownEndMeetsAfterDateIvl: TestMessage(Meets_UnknownEndMeetsAfterDateIvl, 'Meets_UnknownEndMeetsAfterDateIvl', toString(true), toString(Meets_UnknownEndMeetsAfterDateIvl))
define test_Meets_UnknownEndMayMeetBeforeDateIvl: TestMessage(Meets_UnknownEndMayMeetBeforeDateIvl is null, 'Meets_UnknownEndMayMeetBeforeDateIvl', 'null', toString(Meets_UnknownEndMayMeetBeforeDateIvl))
define test_Meets_UnknownEndNotMeetsDateIvl: TestMessage(not Meets_UnknownEndNotMeetsDateIvl, 'Meets_UnknownEndNotMeetsDateIvl', toString(false), toString(Meets_UnknownEndNotMeetsDateIvl))
define test_Meets_DateIvlMayMeetAfterUnknownEnd: TestMessage(Meets_DateIvlMayMeetAfterUnknownEnd is null, 'Meets_DateIvlMayMeetAfterUnknownEnd', 'null', toString(Meets_DateIvlMayMeetAfterUnknownEnd))

// MeetsAfter
define MeetsAfter_MeetsAfterIntIvl: Interval[11, 15] meets after Interval[5, 10]
define MeetsAfter_MeetsBeforeIntIvl: Interval[1, 4] meets after Interval[5, 10]
define MeetsAfter_NotMeetsIntIvl: Interval[1, 2] meets after Interval[5, 10]
define MeetsAfter_MeetsAfterRealIvl: Interval[1.50000001, 2.5] meets after Interval[0.5, 1.5]
define MeetsAfter_MeetsBeforeRealIvl: Interval[0.5, 1.5] meets after Interval[1.50000001, 2.5]
define MeetsAfter_NotMeetsRealIvl: Interval[0.0, 1.0] meets after Interval[1.1, 2.0]
define MeetsAfter_MeetsAfterDateIvl: Interval[DateTime(2012, 9, 1, 0, 0, 0, 0), DateTime(2012, 12, 1, 0, 0, 0, 0)) meets after DateIvl
define MeetsAfter_MeetsBeforeDateIvl: Interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2012, 3, 1, 0, 0, 0, 0)) meets after DateIvl
define MeetsAfter_NotMeetsDateIvl: Interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2012, 2, 29, 0, 0, 0, 0)) meets after DateIvl
define MeetsAfter_MayMeetAfterImpreciseDateIvl: DateIvl meets after Interval[DateTime(2012, 1), DateTime(2012, 2)]
define MeetsAfter_MayMeetBeforeImpreciseDateIvl: DateIvl meets after Interval[DateTime(2012, 9), DateTime(2012, 12)]
define MeetsAfter_NotMeetsImpreciseDateIvl: DateIvl meets after Interval[DateTime(2012, 1), DateTime(2012, 12)]
define MeetsAfter_MeetsAfterDayOfIvl: PrecisionDateIvl meets after day of Interval[DateTime(2012, 1, 2, 0, 0, 0, 0), DateTime(2012, 3, 1, 0, 0, 0, 0)]
define MeetsAfter_MeetsBeforeDayOfIvl: PrecisionDateIvl meets after day of Interval[DateTime(2012, 9, 3, 0, 0, 0, 0), DateTime(2012, 12, 1, 0, 0, 0, 0)]
define MeetsAfter_NotMeetsDayOfIvl: PrecisionDateIvl meets after day of Interval[DateTime(2012, 1, 2, 0, 0, 0, 0), DateTime(2012, 3, 2, 0, 0, 0, 0)]
define MeetsAfter_NotMeetsDayOfImpreciseIVL: PrecisionDateIvl meets after day of Interval[DateTime(2012, 1), DateTime(2012, 2)]
define MeetsAfter_MayMeetAfterDayOfImpreciseIvl: PrecisionDateIvl meets after day of Interval[DateTime(2012, 1), DateTime(2012, 3)]
define MeetsAfter_MayMeetBeforeDayOfImpreciseIvl: PrecisionDateIvl meets after day of Interval[DateTime(2012, 9), DateTime(2012, 12)]
define MeetsAfter_ImpreciseMayMeetAfterDateIvl: ImpDateIvl meets after Interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2012, 2, 29, 23, 59, 59, 999)]
define MeetsAfter_ImpreciseMayMeetBeforeDateIvl: ImpDateIvl meets after Interval[DateTime(2012, 9, 1, 0, 0, 0, 0), DateTime(2012, 12, 31, 23, 59, 59, 999)]
define MeetsAfter_ImpreciseNotMeetsDateIvl: ImpDateIvl meets after Interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2012, 12, 31, 23, 59, 59, 999))
define MeetsAfter_NegInfBegMeetsBeforeIntIvl: Interval[null, 100] meets after Interval[101, 200]
define MeetsAfter_NegInfBegNotMeetsIntIvl: Interval[null, 100] meets after Interval[100, 200]
define MeetsAfter_IntIvlNotMeetsNegInfBeg: Interval[100, 200] meets after Interval[null, 400]
define MeetsAfter_UnknownBegMeetsBeforeIntIvl: Interval(null, 100] meets after Interval[101, 200]
define MeetsAfter_UnknownBegMayMeetAfterIntIvl: Interval(null, 100] meets after Interval[0, 50]
define MeetsAfter_UnknownBegNotMeetsIntIvl: Interval(null, 5] meets after Interval[0, 100]
define MeetsAfter_IntIvlMayMeetBeforeUnknownBeg: Interval[0, 100] meets after Interval(null, 400)
define MeetsAfter_PosInfEndMeetsAfterIntIvl: Interval[100, null] meets after Interval[0, 99]
define MeetsAfter_PosInfEndNotMeetsIntIvl: Interval[0, null] meets after Interval[1000, 2000]
define MeetsAfter_IntIvlNotMeetsPosInfEnd: Interval[100, 200] meets after Interval[0, null]
define MeetsAfter_UnknownEndMeetsAfterIntIvl: Interval[100, null) meets after Interval[0, 99]
define MeetsAfter_UnknownEndMayMeetBeforeIntIvl: Interval[0, null) meets after Interval[50, 100]
define MeetsAfter_UnknownEndNotMeetsIntIvl: Interval[0, null) meets after Interval[0, 100]
define MeetsAfter_IntIvlMayMeetAfterUnknownEnd: Interval[0, 100] meets after Interval[-100, null)
define MeetsAfter_NegInfBegMeetsBeforeDateIvl: Interval[null, DateTime(2013, 1, 1, 0, 0, 0, 0)) meets after Interval[DateTime(2013, 1, 1, 0, 0, 0, 0), DateTime(2030, 1, 1, 0, 0, 0, 0)]
define MeetsAfter_NegInfBegNotMeetsDateIvl: Interval[null, DateTime(2013, 1, 1, 0, 0, 0, 0)) meets after Interval[DateTime(2000, 1, 1, 0, 0, 0, 0), DateTime(2010, 1, 1, 0, 0, 0, 0)]
define MeetsAfter_DateIvlNotMeetsNegInfBeg: Interval[DateTime(2000, 1, 1, 0, 0, 0, 0), DateTime(2010, 1, 1, 0, 0, 0, 0)] meets after Interval[null, DateTime(2013, 1, 1, 0, 0, 0, 0))
define MeetsAfter_UnknownBegMeetsBeforeDateIvl: Interval(null, DateTime(2013, 1, 1, 0, 0, 0, 0)) meets after Interval[DateTime(2013, 1, 1, 0, 0, 0, 0), DateTime(2030, 1, 1, 0, 0, 0, 0)]
define MeetsAfter_UnknownBegMayMeetAfterDateIvl: Interval(null, DateTime(2013, 1, 1, 0, 0, 0, 0)) meets after Interval[DateTime(2000, 1, 1, 0, 0, 0, 0), DateTime(2010, 1, 1, 0, 0, 0, 0)]
define MeetsAfter_UnknownBegNotMeetsDateIvl: Interval(null, DateTime(2013, 1, 1, 0, 0, 0, 0)) meets after Interval[DateTime(2000, 1, 1, 0, 0, 0, 0), DateTime(2020, 1, 1, 0, 0, 0, 0)]
define MeetsAfter_DateIvlMayMeetBeforeUnknownBeg: Interval[DateTime(2000, 1, 1, 0, 0, 0, 0), DateTime(2010, 1, 1, 0, 0, 0, 0)] meets after Interval(null, DateTime(2013, 1, 1, 0, 0, 0, 0))
define MeetsAfter_PosInfEndMeetsAfterDateIvl: Interval[DateTime(2013, 1, 1, 0, 0, 0, 0), null] meets after Interval[DateTime(2000, 1, 1, 0, 0, 0, 0), DateTime(2013, 1, 1, 0, 0, 0, 0))
define MeetsAfter_PosInfEndNotMeetsDateIvl: Interval[DateTime(2013, 1, 1, 0, 0, 0, 0), null] meets after Interval[DateTime(2000, 1, 1, 0, 0, 0, 0), DateTime(2020, 1, 1, 0, 0, 0, 0)]
define MeetsAfter_DateIvlNotMeetsPosInfEnd: Interval[DateTime(2000, 1, 1, 0, 0, 0, 0), DateTime(2020, 1, 1, 0, 0, 0, 0)] meets after Interval[DateTime(2013, 1, 1, 0, 0, 0, 0), null]
define MeetsAfter_UnknownEndMeetsAfterDateIvl: Interval[DateTime(2013, 1, 1, 0, 0, 0, 0), null) meets after Interval[DateTime(2010, 1, 1, 0, 0, 0, 0), DateTime(2013, 1, 1, 0, 0, 0, 0))
define MeetsAfter_UnknownEndMayMeetBeforeDateIvl: Interval[DateTime(2013, 1, 1, 0, 0, 0, 0), null) meets after Interval[DateTime(2020, 1, 1, 0, 0, 0, 0), DateTime(2040, 1, 1, 0, 0, 0, 0)]
define MeetsAfter_UnknownEndNotMeetsDateIvl: Interval[DateTime(2013, 1, 1, 0, 0, 0, 0), null) meets after Interval[DateTime(2000, 1, 1, 0, 0, 0, 0), DateTime(2013, 1, 1, 0, 0, 0, 0)]
define MeetsAfter_DateIvlMayMeetAfterUnknownEnd: Interval[DateTime(2020, 1, 1, 0, 0, 0, 0), DateTime(2040, 1, 1, 0, 0, 0, 0)] meets after Interval[DateTime(2013, 1, 1, 0, 0, 0, 0), null)

define test_MeetsAfter_MeetsAfterIntIvl: TestMessage(MeetsAfter_MeetsAfterIntIvl, 'MeetsAfter_MeetsAfterIntIvl', toString(true), toString(MeetsAfter_MeetsAfterIntIvl))
define test_MeetsAfter_MeetsBeforeIntIvl: TestMessage(not MeetsAfter_MeetsBeforeIntIvl, 'MeetsAfter_MeetsBeforeIntIvl', toString(false), toString(MeetsAfter_MeetsBeforeIntIvl))
define test_MeetsAfter_NotMeetsIntIvl: TestMessage(not MeetsAfter_NotMeetsIntIvl, 'MeetsAfter_NotMeetsIntIvl', toString(false), toString(MeetsAfter_NotMeetsIntIvl))
define test_MeetsAfter_MeetsAfterRealIvl: TestMessage(MeetsAfter_MeetsAfterRealIvl, 'MeetsAfter_MeetsAfterRealIvl', toString(true), toString(MeetsAfter_MeetsAfterRealIvl))
define test_MeetsAfter_MeetsBeforeRealIvl: TestMessage(not MeetsAfter_MeetsBeforeRealIvl, 'MeetsAfter_MeetsBeforeRealIvl', toString(false), toString(MeetsAfter_MeetsBeforeRealIvl))
define test_MeetsAfter_NotMeetsRealIvl: TestMessage(not MeetsAfter_NotMeetsRealIvl, 'MeetsAfter_NotMeetsRealIvl', toString(false), toString(MeetsAfter_NotMeetsRealIvl))
define test_MeetsAfter_MeetsAfterDateIvl: TestMessage(MeetsAfter_MeetsAfterDateIvl, 'MeetsAfter_MeetsAfterDateIvl', toString(true), toString(MeetsAfter_MeetsAfterDateIvl))
define test_MeetsAfter_MeetsBeforeDateIvl: TestMessage(not MeetsAfter_MeetsBeforeDateIvl, 'MeetsAfter_MeetsBeforeDateIvl', toString(false), toString(MeetsAfter_MeetsBeforeDateIvl))
define test_MeetsAfter_NotMeetsDateIvl: TestMessage(not MeetsAfter_NotMeetsDateIvl, 'MeetsAfter_NotMeetsDateIvl', toString(false), toString(MeetsAfter_NotMeetsDateIvl))
define test_MeetsAfter_MayMeetAfterImpreciseDateIvl: TestMessage(MeetsAfter_MayMeetAfterImpreciseDateIvl is null, 'MeetsAfter_MayMeetAfterImpreciseDateIvl', 'null', toString(MeetsAfter_MayMeetAfterImpreciseDateIvl))
define test_MeetsAfter_MayMeetBeforeImpreciseDateIvl: TestMessage(not MeetsAfter_MayMeetBeforeImpreciseDateIvl, 'MeetsAfter_MayMeetBeforeImpreciseDateIvl', toString(false), toString(MeetsAfter_MayMeetBeforeImpreciseDateIvl))
define test_MeetsAfter_NotMeetsImpreciseDateIvl: TestMessage(not MeetsAfter_NotMeetsImpreciseDateIvl, 'MeetsAfter_NotMeetsImpreciseDateIvl', toString(false), toString(MeetsAfter_NotMeetsImpreciseDateIvl))
define test_MeetsAfter_MeetsAfterDayOfIvl: TestMessage(MeetsAfter_MeetsAfterDayOfIvl, 'MeetsAfter_MeetsAfterDayOfIvl', toString(true), toString(MeetsAfter_MeetsAfterDayOfIvl))
define test_MeetsAfter_MeetsBeforeDayOfIvl: TestMessage(not MeetsAfter_MeetsBeforeDayOfIvl, 'MeetsAfter_MeetsBeforeDayOfIvl', toString(false), toString(MeetsAfter_MeetsBeforeDayOfIvl))
define test_MeetsAfter_NotMeetsDayOfIvl: TestMessage(not MeetsAfter_NotMeetsDayOfIvl, 'MeetsAfter_NotMeetsDayOfIvl', toString(false), toString(MeetsAfter_NotMeetsDayOfIvl))
define test_MeetsAfter_NotMeetsDayOfImpreciseIVL: TestMessage(not MeetsAfter_NotMeetsDayOfImpreciseIVL, 'MeetsAfter_NotMeetsDayOfImpreciseIVL', toString(false), toString(MeetsAfter_NotMeetsDayOfImpreciseIVL))
define test_MeetsAfter_MayMeetAfterDayOfImpreciseIvl: TestMessage(MeetsAfter_MayMeetAfterDayOfImpreciseIvl is null, 'MeetsAfter_MayMeetAfterDayOfImpreciseIvl', 'null', toString(MeetsAfter_MayMeetAfterDayOfImpreciseIvl))
define test_MeetsAfter_MayMeetBeforeDayOfImpreciseIvl: TestMessage(not MeetsAfter_MayMeetBeforeDayOfImpreciseIvl, 'MeetsAfter_MayMeetBeforeDayOfImpreciseIvl', toString(false), toString(MeetsAfter_MayMeetBeforeDayOfImpreciseIvl))
define test_MeetsAfter_ImpreciseMayMeetAfterDateIvl: TestMessage(MeetsAfter_ImpreciseMayMeetAfterDateIvl is null, 'MeetsAfter_ImpreciseMayMeetAfterDateIvl', 'null', toString(MeetsAfter_ImpreciseMayMeetAfterDateIvl))
define test_MeetsAfter_ImpreciseMayMeetBeforeDateIvl: TestMessage(not MeetsAfter_ImpreciseMayMeetBeforeDateIvl, 'MeetsAfter_ImpreciseMayMeetBeforeDateIvl', toString(false), toString(MeetsAfter_ImpreciseMayMeetBeforeDateIvl))
define test_MeetsAfter_ImpreciseNotMeetsDateIvl: TestMessage(not MeetsAfter_ImpreciseNotMeetsDateIvl, 'MeetsAfter_ImpreciseNotMeetsDateIvl', toString(false), toString(MeetsAfter_ImpreciseNotMeetsDateIvl))
define test_MeetsAfter_NegInfBegMeetsBeforeIntIvl: TestMessage(not MeetsAfter_NegInfBegMeetsBeforeIntIvl, 'MeetsAfter_NegInfBegMeetsBeforeIntIvl', toString(false), toString(MeetsAfter_NegInfBegMeetsBeforeIntIvl))
define test_MeetsAfter_NegInfBegNotMeetsIntIvl: TestMessage(not MeetsAfter_NegInfBegNotMeetsIntIvl, 'MeetsAfter_NegInfBegNotMeetsIntIvl', toString(false), toString(MeetsAfter_NegInfBegNotMeetsIntIvl))
define test_MeetsAfter_IntIvlNotMeetsNegInfBeg: TestMessage(not MeetsAfter_IntIvlNotMeetsNegInfBeg, 'MeetsAfter_IntIvlNotMeetsNegInfBeg', toString(false), toString(MeetsAfter_IntIvlNotMeetsNegInfBeg))
define test_MeetsAfter_UnknownBegMeetsBeforeIntIvl: TestMessage(not MeetsAfter_UnknownBegMeetsBeforeIntIvl, 'MeetsAfter_UnknownBegMeetsBeforeIntIvl', toString(false), toString(MeetsAfter_UnknownBegMeetsBeforeIntIvl))
define test_MeetsAfter_UnknownBegMayMeetAfterIntIvl: TestMessage(MeetsAfter_UnknownBegMayMeetAfterIntIvl is null, 'MeetsAfter_UnknownBegMayMeetAfterIntIvl', 'null', toString(MeetsAfter_UnknownBegMayMeetAfterIntIvl))
define test_MeetsAfter_UnknownBegNotMeetsIntIvl: TestMessage(not MeetsAfter_UnknownBegNotMeetsIntIvl, 'MeetsAfter_UnknownBegNotMeetsIntIvl', toString(false), toString(MeetsAfter_UnknownBegNotMeetsIntIvl))
define test_MeetsAfter_IntIvlMayMeetBeforeUnknownBeg: TestMessage(not MeetsAfter_IntIvlMayMeetBeforeUnknownBeg, 'MeetsAfter_IntIvlMayMeetBeforeUnknownBeg', toString(false), toString(MeetsAfter_IntIvlMayMeetBeforeUnknownBeg))
define test_MeetsAfter_PosInfEndMeetsAfterIntIvl: TestMessage(MeetsAfter_PosInfEndMeetsAfterIntIvl, 'MeetsAfter_PosInfEndMeetsAfterIntIvl', toString(true), toString(MeetsAfter_PosInfEndMeetsAfterIntIvl))
define test_MeetsAfter_PosInfEndNotMeetsIntIvl: TestMessage(not MeetsAfter_PosInfEndNotMeetsIntIvl, 'MeetsAfter_PosInfEndNotMeetsIntIvl', toString(false), toString(MeetsAfter_PosInfEndNotMeetsIntIvl))
define test_MeetsAfter_IntIvlNotMeetsPosInfEnd: TestMessage(not MeetsAfter_IntIvlNotMeetsPosInfEnd, 'MeetsAfter_IntIvlNotMeetsPosInfEnd', toString(false), toString(MeetsAfter_IntIvlNotMeetsPosInfEnd))
define test_MeetsAfter_UnknownEndMeetsAfterIntIvl: TestMessage(MeetsAfter_UnknownEndMeetsAfterIntIvl, 'MeetsAfter_UnknownEndMeetsAfterIntIvl', toString(true), toString(MeetsAfter_UnknownEndMeetsAfterIntIvl))
define test_MeetsAfter_UnknownEndMayMeetBeforeIntIvl: TestMessage(not MeetsAfter_UnknownEndMayMeetBeforeIntIvl, 'MeetsAfter_UnknownEndMayMeetBeforeIntIvl', toString(false), toString(MeetsAfter_UnknownEndMayMeetBeforeIntIvl))
define test_MeetsAfter_UnknownEndNotMeetsIntIvl: TestMessage(not MeetsAfter_UnknownEndNotMeetsIntIvl, 'MeetsAfter_UnknownEndNotMeetsIntIvl', toString(false), toString(MeetsAfter_UnknownEndNotMeetsIntIvl))
define test_MeetsAfter_IntIvlMayMeetAfterUnknownEnd: TestMessage(MeetsAfter_IntIvlMayMeetAfterUnknownEnd is null, 'MeetsAfter_IntIvlMayMeetAfterUnknownEnd', 'null', toString(MeetsAfter_IntIvlMayMeetAfterUnknownEnd))
define test_MeetsAfter_NegInfBegMeetsBeforeDateIvl: TestMessage(not MeetsAfter_NegInfBegMeetsBeforeDateIvl, 'MeetsAfter_NegInfBegMeetsBeforeDateIvl', toString(false), toString(MeetsAfter_NegInfBegMeetsBeforeDateIvl))
define test_MeetsAfter_NegInfBegNotMeetsDateIvl: TestMessage(not MeetsAfter_NegInfBegNotMeetsDateIvl, 'MeetsAfter_NegInfBegNotMeetsDateIvl', toString(false), toString(MeetsAfter_NegInfBegNotMeetsDateIvl))
define test_MeetsAfter_DateIvlNotMeetsNegInfBeg: TestMessage(not MeetsAfter_DateIvlNotMeetsNegInfBeg, 'MeetsAfter_DateIvlNotMeetsNegInfBeg', toString(false), toString(MeetsAfter_DateIvlNotMeetsNegInfBeg))
define test_MeetsAfter_UnknownBegMeetsBeforeDateIvl: TestMessage(not MeetsAfter_UnknownBegMeetsBeforeDateIvl, 'MeetsAfter_UnknownBegMeetsBeforeDateIvl', toString(false), toString(MeetsAfter_UnknownBegMeetsBeforeDateIvl))
define test_MeetsAfter_UnknownBegMayMeetAfterDateIvl: TestMessage(MeetsAfter_UnknownBegMayMeetAfterDateIvl is null, 'MeetsAfter_UnknownBegMayMeetAfterDateIvl', 'null', toString(MeetsAfter_UnknownBegMayMeetAfterDateIvl))
define test_MeetsAfter_UnknownBegNotMeetsDateIvl: TestMessage(not MeetsAfter_UnknownBegNotMeetsDateIvl, 'MeetsAfter_UnknownBegNotMeetsDateIvl', toString(false), toString(MeetsAfter_UnknownBegNotMeetsDateIvl))
define test_MeetsAfter_DateIvlMayMeetBeforeUnknownBeg: TestMessage(not MeetsAfter_DateIvlMayMeetBeforeUnknownBeg, 'MeetsAfter_DateIvlMayMeetBeforeUnknownBeg', toString(false), toString(MeetsAfter_DateIvlMayMeetBeforeUnknownBeg))
define test_MeetsAfter_PosInfEndMeetsAfterDateIvl: TestMessage(MeetsAfter_PosInfEndMeetsAfterDateIvl, 'MeetsAfter_PosInfEndMeetsAfterDateIvl', toString(true), toString(MeetsAfter_PosInfEndMeetsAfterDateIvl))
define test_MeetsAfter_PosInfEndNotMeetsDateIvl: TestMessage(not MeetsAfter_PosInfEndNotMeetsDateIvl, 'MeetsAfter_PosInfEndNotMeetsDateIvl', toString(false), toString(MeetsAfter_PosInfEndNotMeetsDateIvl))
define test_MeetsAfter_DateIvlNotMeetsPosInfEnd: TestMessage(not MeetsAfter_DateIvlNotMeetsPosInfEnd, 'MeetsAfter_DateIvlNotMeetsPosInfEnd', toString(false), toString(MeetsAfter_DateIvlNotMeetsPosInfEnd))
define test_MeetsAfter_UnknownEndMeetsAfterDateIvl: TestMessage(MeetsAfter_UnknownEndMeetsAfterDateIvl, 'MeetsAfter_UnknownEndMeetsAfterDateIvl', toString(true), toString(MeetsAfter_UnknownEndMeetsAfterDateIvl))
define test_MeetsAfter_UnknownEndMayMeetBeforeDateIvl: TestMessage(not MeetsAfter_UnknownEndMayMeetBeforeDateIvl, 'MeetsAfter_UnknownEndMayMeetBeforeDateIvl', toString(false), toString(MeetsAfter_UnknownEndMayMeetBeforeDateIvl))
define test_MeetsAfter_UnknownEndNotMeetsDateIvl: TestMessage(not MeetsAfter_UnknownEndNotMeetsDateIvl, 'MeetsAfter_UnknownEndNotMeetsDateIvl', toString(false), toString(MeetsAfter_UnknownEndNotMeetsDateIvl))
define test_MeetsAfter_DateIvlMayMeetAfterUnknownEnd: TestMessage(MeetsAfter_DateIvlMayMeetAfterUnknownEnd is null, 'MeetsAfter_DateIvlMayMeetAfterUnknownEnd', 'null', toString(MeetsAfter_DateIvlMayMeetAfterUnknownEnd))

// MeetsBefore
define MeetsBefore_MeetsAfterIntIvl: Interval[11, 15] meets before Interval[5, 10]
define MeetsBefore_MeetsBeforeIntIvl: Interval[1, 4] meets before Interval[5, 10]
define MeetsBefore_NotMeetsIntIvl: Interval[1, 2] meets before Interval[5, 10]
define MeetsBefore_MeetsAfterRealIvl: Interval[1.50000001, 2.5] meets before Interval[0.5, 1.5]
define MeetsBefore_MeetsBeforeRealIvl: Interval[0.5, 1.5] meets before Interval[1.50000001, 2.5]
define MeetsBefore_NotMeetsRealIvl: Interval[0.0, 1.0] meets before Interval[1.1, 2.0]
define MeetsBefore_MeetsAfterDateIvl: Interval[DateTime(2012, 9, 1, 0, 0, 0, 0), DateTime(2012, 12, 1, 0, 0, 0, 0)) meets before DateIvl
define MeetsBefore_MeetsBeforeDateIvl: Interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2012, 3, 1, 0, 0, 0, 0)) meets before DateIvl
define MeetsBefore_NotMeetsDateIvl: Interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2012, 2, 29, 0, 0, 0, 0)) meets before DateIvl
define MeetsBefore_MayMeetAfterImpreciseDateIvl: DateIvl meets before Interval[DateTime(2012, 1), DateTime(2012, 2)]
define MeetsBefore_MayMeetBeforeImpreciseDateIvl: DateIvl meets before Interval[DateTime(2012, 9), DateTime(2012, 12)]
define MeetsBefore_NotMeetsImpreciseDateIvl: DateIvl meets before Interval[DateTime(2012, 1), DateTime(2012, 12)]
define MeetsBefore_MeetsAfterDayOfIvl: PrecisionDateIvl meets before day of Interval[DateTime(2012, 1, 2, 0, 0, 0, 0), DateTime(2012, 3, 1, 0, 0, 0, 0)]
define MeetsBefore_MeetsBeforeDayOfIvl: PrecisionDateIvl meets before day of Interval[DateTime(2012, 9, 3, 0, 0, 0, 0), DateTime(2012, 12, 1, 0, 0, 0, 0)]
define MeetsBefore_NotMeetsDayOfIvl: PrecisionDateIvl meets before day of Interval[DateTime(2012, 1, 2, 0, 0, 0, 0), DateTime(2012, 3, 2, 0, 0, 0, 0)]
define MeetsBefore_NotMeetsDayOfImpreciseIVL: PrecisionDateIvl meets before day of Interval[DateTime(2012, 1), DateTime(2012, 2)]
define MeetsBefore_MayMeetAfterDayOfImpreciseIvl: PrecisionDateIvl meets before day of Interval[DateTime(2012, 1), DateTime(2012, 3)]
define MeetsBefore_MayMeetBeforeDayOfImpreciseIvl: PrecisionDateIvl meets before day of Interval[DateTime(2012, 9), DateTime(2012, 12)]
define MeetsBefore_ImpreciseMayMeetAfterDateIvl: ImpDateIvl meets before Interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2012, 2, 29, 23, 59, 59, 999)]
define MeetsBefore_ImpreciseMayMeetBeforeDateIvl: ImpDateIvl meets before Interval[DateTime(2012, 9, 1, 0, 0, 0, 0), DateTime(2012, 12, 31, 23, 59, 59, 999)]
define MeetsBefore_ImpreciseNotMeetsDateIvl: ImpDateIvl meets before Interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2012, 12, 31, 23, 59, 59, 999))
define MeetsBefore_NegInfBegMeetsBeforeIntIvl: Interval[null, 100] meets before Interval[101, 200]
define MeetsBefore_NegInfBegNotMeetsIntIvl: Interval[null, 100] meets before Interval[100, 200]
define MeetsBefore_IntIvlNotMeetsNegInfBeg: Interval[100, 200] meets before Interval[null, 400]
define MeetsBefore_UnknownBegMeetsBeforeIntIvl: Interval(null, 100] meets before Interval[101, 200]
define MeetsBefore_UnknownBegMayMeetAfterIntIvl: Interval(null, 100] meets before Interval[0, 50]
define MeetsBefore_UnknownBegNotMeetsIntIvl: Interval(null, 5] meets before Interval[0, 100]
define MeetsBefore_IntIvlMayMeetBeforeUnknownBeg: Interval[0, 100] meets before Interval(null, 400)
define MeetsBefore_PosInfEndMeetsAfterIntIvl: Interval[100, null] meets before Interval[0, 99]
define MeetsBefore_PosInfEndNotMeetsIntIvl: Interval[0, null] meets before Interval[1000, 2000]
define MeetsBefore_IntIvlNotMeetsPosInfEnd: Interval[100, 200] meets before Interval[0, null]
define MeetsBefore_UnknownEndMeetsAfterIntIvl: Interval[100, null) meets before Interval[0, 99]
define MeetsBefore_UnknownEndMayMeetBeforeIntIvl: Interval[0, null) meets before Interval[50, 100]
define MeetsBefore_UnknownEndNotMeetsIntIvl: Interval[0, null) meets before Interval[0, 100]
define MeetsBefore_IntIvlMayMeetAfterUnknownEnd: Interval[0, 100] meets before Interval[-100, null)
define MeetsBefore_NegInfBegMeetsBeforeDateIvl: Interval[null, DateTime(2013, 1, 1, 0, 0, 0, 0)) meets before Interval[DateTime(2013, 1, 1, 0, 0, 0, 0), DateTime(2030, 1, 1, 0, 0, 0, 0)]
define MeetsBefore_NegInfBegNotMeetsDateIvl: Interval[null, DateTime(2013, 1, 1, 0, 0, 0, 0)) meets before Interval[DateTime(2000, 1, 1, 0, 0, 0, 0), DateTime(2010, 1, 1, 0, 0, 0, 0)]
define MeetsBefore_DateIvlNotMeetsNegInfBeg: Interval[DateTime(2000, 1, 1, 0, 0, 0, 0), DateTime(2010, 1, 1, 0, 0, 0, 0)] meets before Interval[null, DateTime(2013, 1, 1, 0, 0, 0, 0))
define MeetsBefore_UnknownBegMeetsBeforeDateIvl: Interval(null, DateTime(2013, 1, 1, 0, 0, 0, 0)) meets before Interval[DateTime(2013, 1, 1, 0, 0, 0, 0), DateTime(2030, 1, 1, 0, 0, 0, 0)]
define MeetsBefore_UnknownBegMayMeetAfterDateIvl: Interval(null, DateTime(2013, 1, 1, 0, 0, 0, 0)) meets before Interval[DateTime(2000, 1, 1, 0, 0, 0, 0), DateTime(2010, 1, 1, 0, 0, 0, 0)]
define MeetsBefore_UnknownBegNotMeetsDateIvl: Interval(null, DateTime(2013, 1, 1, 0, 0, 0, 0)) meets before Interval[DateTime(2000, 1, 1, 0, 0, 0, 0), DateTime(2020, 1, 1, 0, 0, 0, 0)]
define MeetsBefore_DateIvlMayMeetBeforeUnknownBeg: Interval[DateTime(2000, 1, 1, 0, 0, 0, 0), DateTime(2010, 1, 1, 0, 0, 0, 0)] meets before Interval(null, DateTime(2013, 1, 1, 0, 0, 0, 0))
define MeetsBefore_PosInfEndMeetsAfterDateIvl: Interval[DateTime(2013, 1, 1, 0, 0, 0, 0), null] meets before Interval[DateTime(2000, 1, 1, 0, 0, 0, 0), DateTime(2013, 1, 1, 0, 0, 0, 0))
define MeetsBefore_PosInfEndNotMeetsDateIvl: Interval[DateTime(2013, 1, 1, 0, 0, 0, 0), null] meets before Interval[DateTime(2000, 1, 1, 0, 0, 0, 0), DateTime(2020, 1, 1, 0, 0, 0, 0)]
define MeetsBefore_DateIvlNotMeetsPosInfEnd: Interval[DateTime(2000, 1, 1, 0, 0, 0, 0), DateTime(2020, 1, 1, 0, 0, 0, 0)] meets before Interval[DateTime(2013, 1, 1, 0, 0, 0, 0), null]
define MeetsBefore_UnknownEndMeetsAfterDateIvl: Interval[DateTime(2013, 1, 1, 0, 0, 0, 0), null) meets before Interval[DateTime(2010, 1, 1, 0, 0, 0, 0), DateTime(2013, 1, 1, 0, 0, 0, 0))
define MeetsBefore_UnknownEndMayMeetBeforeDateIvl: Interval[DateTime(2013, 1, 1, 0, 0, 0, 0), null) meets before Interval[DateTime(2020, 1, 1, 0, 0, 0, 0), DateTime(2040, 1, 1, 0, 0, 0, 0)]
define MeetsBefore_UnknownEndNotMeetsDateIvl: Interval[DateTime(2013, 1, 1, 0, 0, 0, 0), null) meets before Interval[DateTime(2000, 1, 1, 0, 0, 0, 0), DateTime(2013, 1, 1, 0, 0, 0, 0)]
define MeetsBefore_DateIvlMayMeetAfterUnknownEnd: Interval[DateTime(2020, 1, 1, 0, 0, 0, 0), DateTime(2040, 1, 1, 0, 0, 0, 0)] meets before Interval[DateTime(2013, 1, 1, 0, 0, 0, 0), null)

define test_MeetsBefore_MeetsAfterIntIvl: TestMessage(not MeetsBefore_MeetsAfterIntIvl, 'MeetsBefore_MeetsAfterIntIvl', toString(false), toString(MeetsBefore_MeetsAfterIntIvl))
define test_MeetsBefore_MeetsBeforeIntIvl: TestMessage(MeetsBefore_MeetsBeforeIntIvl, 'MeetsBefore_MeetsBeforeIntIvl', toString(true), toString(MeetsBefore_MeetsBeforeIntIvl))
define test_MeetsBefore_NotMeetsIntIvl: TestMessage(not MeetsBefore_NotMeetsIntIvl, 'MeetsBefore_NotMeetsIntIvl', toString(false), toString(MeetsBefore_NotMeetsIntIvl))
define test_MeetsBefore_MeetsAfterRealIvl: TestMessage(not MeetsBefore_MeetsAfterRealIvl, 'MeetsBefore_MeetsAfterRealIvl', toString(false), toString(MeetsBefore_MeetsAfterRealIvl))
define test_MeetsBefore_MeetsBeforeRealIvl: TestMessage(MeetsBefore_MeetsBeforeRealIvl, 'MeetsBefore_MeetsBeforeRealIvl', toString(true), toString(MeetsBefore_MeetsBeforeRealIvl))
define test_MeetsBefore_NotMeetsRealIvl: TestMessage(not MeetsBefore_NotMeetsRealIvl, 'MeetsBefore_NotMeetsRealIvl', toString(false), toString(MeetsBefore_NotMeetsRealIvl))
define test_MeetsBefore_MeetsAfterDateIvl: TestMessage(not MeetsBefore_MeetsAfterDateIvl, 'MeetsBefore_MeetsAfterDateIvl', toString(false), toString(MeetsBefore_MeetsAfterDateIvl))
define test_MeetsBefore_MeetsBeforeDateIvl: TestMessage(MeetsBefore_MeetsBeforeDateIvl, 'MeetsBefore_MeetsBeforeDateIvl', toString(true), toString(MeetsBefore_MeetsBeforeDateIvl))
define test_MeetsBefore_NotMeetsDateIvl: TestMessage(not MeetsBefore_NotMeetsDateIvl, 'MeetsBefore_NotMeetsDateIvl', toString(false), toString(MeetsBefore_NotMeetsDateIvl))
define test_MeetsBefore_MayMeetAfterImpreciseDateIvl: TestMessage(not MeetsBefore_MayMeetAfterImpreciseDateIvl, 'MeetsBefore_MayMeetAfterImpreciseDateIvl', toString(false), toString(MeetsBefore_MayMeetAfterImpreciseDateIvl))
define test_MeetsBefore_MayMeetBeforeImpreciseDateIvl: TestMessage(MeetsBefore_MayMeetBeforeImpreciseDateIvl is null, 'MeetsBefore_MayMeetBeforeImpreciseDateIvl', 'null', toString(MeetsBefore_MayMeetBeforeImpreciseDateIvl))
define test_MeetsBefore_NotMeetsImpreciseDateIvl: TestMessage(not MeetsBefore_NotMeetsImpreciseDateIvl, 'MeetsBefore_NotMeetsImpreciseDateIvl', toString(false), toString(MeetsBefore_NotMeetsImpreciseDateIvl))
define test_MeetsBefore_MeetsAfterDayOfIvl: TestMessage(not MeetsBefore_MeetsAfterDayOfIvl, 'MeetsBefore_MeetsAfterDayOfIvl', toString(false), toString(MeetsBefore_MeetsAfterDayOfIvl))
define test_MeetsBefore_MeetsBeforeDayOfIvl: TestMessage(MeetsBefore_MeetsBeforeDayOfIvl, 'MeetsBefore_MeetsBeforeDayOfIvl', toString(true), toString(MeetsBefore_MeetsBeforeDayOfIvl))
define test_MeetsBefore_NotMeetsDayOfIvl: TestMessage(not MeetsBefore_NotMeetsDayOfIvl, 'MeetsBefore_NotMeetsDayOfIvl', toString(false), toString(MeetsBefore_NotMeetsDayOfIvl))
define test_MeetsBefore_NotMeetsDayOfImpreciseIVL: TestMessage(not MeetsBefore_NotMeetsDayOfImpreciseIVL, 'MeetsBefore_NotMeetsDayOfImpreciseIVL', toString(false), toString(MeetsBefore_NotMeetsDayOfImpreciseIVL))
define test_MeetsBefore_MayMeetAfterDayOfImpreciseIvl: TestMessage(not MeetsBefore_MayMeetAfterDayOfImpreciseIvl, 'MeetsBefore_MayMeetAfterDayOfImpreciseIvl', toString(false), toString(MeetsBefore_MayMeetAfterDayOfImpreciseIvl))
define test_MeetsBefore_MayMeetBeforeDayOfImpreciseIvl: TestMessage(MeetsBefore_MayMeetBeforeDayOfImpreciseIvl is null, 'MeetsBefore_MayMeetBeforeDayOfImpreciseIvl', 'null', toString(MeetsBefore_MayMeetBeforeDayOfImpreciseIvl))
define test_MeetsBefore_ImpreciseMayMeetAfterDateIvl: TestMessage(not MeetsBefore_ImpreciseMayMeetAfterDateIvl, 'MeetsBefore_ImpreciseMayMeetAfterDateIvl', toString(false), toString(MeetsBefore_ImpreciseMayMeetAfterDateIvl))
define test_MeetsBefore_ImpreciseMayMeetBeforeDateIvl: TestMessage(MeetsBefore_ImpreciseMayMeetBeforeDateIvl is null, 'MeetsBefore_ImpreciseMayMeetBeforeDateIvl', 'null', toString(MeetsBefore_ImpreciseMayMeetBeforeDateIvl))
define test_MeetsBefore_ImpreciseNotMeetsDateIvl: TestMessage(not MeetsBefore_ImpreciseNotMeetsDateIvl, 'MeetsBefore_ImpreciseNotMeetsDateIvl', toString(false), toString(MeetsBefore_ImpreciseNotMeetsDateIvl))
define test_MeetsBefore_NegInfBegMeetsBeforeIntIvl: TestMessage(MeetsBefore_NegInfBegMeetsBeforeIntIvl, 'MeetsBefore_NegInfBegMeetsBeforeIntIvl', toString(true), toString(MeetsBefore_NegInfBegMeetsBeforeIntIvl))
define test_MeetsBefore_NegInfBegNotMeetsIntIvl: TestMessage(not MeetsBefore_NegInfBegNotMeetsIntIvl, 'MeetsBefore_NegInfBegNotMeetsIntIvl', toString(false), toString(MeetsBefore_NegInfBegNotMeetsIntIvl))
define test_MeetsBefore_IntIvlNotMeetsNegInfBeg: TestMessage(not MeetsBefore_IntIvlNotMeetsNegInfBeg, 'MeetsBefore_IntIvlNotMeetsNegInfBeg', toString(false), toString(MeetsBefore_IntIvlNotMeetsNegInfBeg))
define test_MeetsBefore_UnknownBegMeetsBeforeIntIvl: TestMessage(MeetsBefore_UnknownBegMeetsBeforeIntIvl, 'MeetsBefore_UnknownBegMeetsBeforeIntIvl', toString(true), toString(MeetsBefore_UnknownBegMeetsBeforeIntIvl))
define test_MeetsBefore_UnknownBegMayMeetAfterIntIvl: TestMessage(not MeetsBefore_UnknownBegMayMeetAfterIntIvl, 'MeetsBefore_UnknownBegMayMeetAfterIntIvl', toString(false), toString(MeetsBefore_UnknownBegMayMeetAfterIntIvl))
define test_MeetsBefore_UnknownBegNotMeetsIntIvl: TestMessage(not MeetsBefore_UnknownBegNotMeetsIntIvl, 'MeetsBefore_UnknownBegNotMeetsIntIvl', toString(false), toString(MeetsBefore_UnknownBegNotMeetsIntIvl))
define test_MeetsBefore_IntIvlMayMeetBeforeUnknownBeg: TestMessage(MeetsBefore_IntIvlMayMeetBeforeUnknownBeg is null, 'MeetsBefore_IntIvlMayMeetBeforeUnknownBeg', 'null', toString(MeetsBefore_IntIvlMayMeetBeforeUnknownBeg))
define test_MeetsBefore_PosInfEndMeetsAfterIntIvl: TestMessage(not MeetsBefore_PosInfEndMeetsAfterIntIvl, 'MeetsBefore_PosInfEndMeetsAfterIntIvl', toString(false), toString(MeetsBefore_PosInfEndMeetsAfterIntIvl))
define test_MeetsBefore_PosInfEndNotMeetsIntIvl: TestMessage(not MeetsBefore_PosInfEndNotMeetsIntIvl, 'MeetsBefore_PosInfEndNotMeetsIntIvl', toString(false), toString(MeetsBefore_PosInfEndNotMeetsIntIvl))
define test_MeetsBefore_IntIvlNotMeetsPosInfEnd: TestMessage(not MeetsBefore_IntIvlNotMeetsPosInfEnd, 'MeetsBefore_IntIvlNotMeetsPosInfEnd', toString(false), toString(MeetsBefore_IntIvlNotMeetsPosInfEnd))
define test_MeetsBefore_UnknownEndMeetsAfterIntIvl: TestMessage(not MeetsBefore_UnknownEndMeetsAfterIntIvl, 'MeetsBefore_UnknownEndMeetsAfterIntIvl', toString(false), toString(MeetsBefore_UnknownEndMeetsAfterIntIvl))
define test_MeetsBefore_UnknownEndMayMeetBeforeIntIvl: TestMessage(MeetsBefore_UnknownEndMayMeetBeforeIntIvl is null, 'MeetsBefore_UnknownEndMayMeetBeforeIntIvl', 'null', toString(MeetsBefore_UnknownEndMayMeetBeforeIntIvl))
define test_MeetsBefore_UnknownEndNotMeetsIntIvl: TestMessage(not MeetsBefore_UnknownEndNotMeetsIntIvl, 'MeetsBefore_UnknownEndNotMeetsIntIvl', toString(false), toString(MeetsBefore_UnknownEndNotMeetsIntIvl))
define test_MeetsBefore_IntIvlMayMeetAfterUnknownEnd: TestMessage(not MeetsBefore_IntIvlMayMeetAfterUnknownEnd, 'MeetsBefore_IntIvlMayMeetAfterUnknownEnd', toString(false), toString(MeetsBefore_IntIvlMayMeetAfterUnknownEnd))
define test_MeetsBefore_NegInfBegMeetsBeforeDateIvl: TestMessage(MeetsBefore_NegInfBegMeetsBeforeDateIvl, 'MeetsBefore_NegInfBegMeetsBeforeDateIvl', toString(true), toString(MeetsBefore_NegInfBegMeetsBeforeDateIvl))
define test_MeetsBefore_NegInfBegNotMeetsDateIvl: TestMessage(not MeetsBefore_NegInfBegNotMeetsDateIvl, 'MeetsBefore_NegInfBegNotMeetsDateIvl', toString(false), toString(MeetsBefore_NegInfBegNotMeetsDateIvl))
define test_MeetsBefore_DateIvlNotMeetsNegInfBeg: TestMessage(not MeetsBefore_DateIvlNotMeetsNegInfBeg, 'MeetsBefore_DateIvlNotMeetsNegInfBeg', toString(false), toString(MeetsBefore_DateIvlNotMeetsNegInfBeg))
define test_MeetsBefore_UnknownBegMeetsBeforeDateIvl: TestMessage(MeetsBefore_UnknownBegMeetsBeforeDateIvl, 'MeetsBefore_UnknownBegMeetsBeforeDateIvl', toString(true), toString(MeetsBefore_UnknownBegMeetsBeforeDateIvl))
define test_MeetsBefore_UnknownBegMayMeetAfterDateIvl: TestMessage(not MeetsBefore_UnknownBegMayMeetAfterDateIvl, 'MeetsBefore_UnknownBegMayMeetAfterDateIvl', toString(false), toString(MeetsBefore_UnknownBegMayMeetAfterDateIvl))
define test_MeetsBefore_UnknownBegNotMeetsDateIvl: TestMessage(not MeetsBefore_UnknownBegNotMeetsDateIvl, 'MeetsBefore_UnknownBegNotMeetsDateIvl', toString(false), toString(MeetsBefore_UnknownBegNotMeetsDateIvl))
define test_MeetsBefore_DateIvlMayMeetBeforeUnknownBeg: TestMessage(MeetsBefore_DateIvlMayMeetBeforeUnknownBeg is null, 'MeetsBefore_DateIvlMayMeetBeforeUnknownBeg', 'null', toString(MeetsBefore_DateIvlMayMeetBeforeUnknownBeg))
define test_MeetsBefore_PosInfEndMeetsAfterDateIvl: TestMessage(not MeetsBefore_PosInfEndMeetsAfterDateIvl, 'MeetsBefore_PosInfEndMeetsAfterDateIvl', toString(false), toString(MeetsBefore_PosInfEndMeetsAfterDateIvl))
define test_MeetsBefore_PosInfEndNotMeetsDateIvl: TestMessage(not MeetsBefore_PosInfEndNotMeetsDateIvl, 'MeetsBefore_PosInfEndNotMeetsDateIvl', toString(false), toString(MeetsBefore_PosInfEndNotMeetsDateIvl))
define test_MeetsBefore_DateIvlNotMeetsPosInfEnd: TestMessage(not MeetsBefore_DateIvlNotMeetsPosInfEnd, 'MeetsBefore_DateIvlNotMeetsPosInfEnd', toString(false), toString(MeetsBefore_DateIvlNotMeetsPosInfEnd))
define test_MeetsBefore_UnknownEndMeetsAfterDateIvl: TestMessage(not MeetsBefore_UnknownEndMeetsAfterDateIvl, 'MeetsBefore_UnknownEndMeetsAfterDateIvl', toString(false), toString(MeetsBefore_UnknownEndMeetsAfterDateIvl))
define test_MeetsBefore_UnknownEndMayMeetBeforeDateIvl: TestMessage(MeetsBefore_UnknownEndMayMeetBeforeDateIvl is null, 'MeetsBefore_UnknownEndMayMeetBeforeDateIvl', 'null', toString(MeetsBefore_UnknownEndMayMeetBeforeDateIvl))
define test_MeetsBefore_UnknownEndNotMeetsDateIvl: TestMessage(not MeetsBefore_UnknownEndNotMeetsDateIvl, 'MeetsBefore_UnknownEndNotMeetsDateIvl', toString(false), toString(MeetsBefore_UnknownEndNotMeetsDateIvl))
define test_MeetsBefore_DateIvlMayMeetAfterUnknownEnd: TestMessage(not MeetsBefore_DateIvlMayMeetAfterUnknownEnd, 'MeetsBefore_DateIvlMayMeetAfterUnknownEnd', toString(false), toString(MeetsBefore_DateIvlMayMeetAfterUnknownEnd))

// Overlaps
define Overlaps_OverlapsBeforeIntIvl: Interval[1, 5] overlaps Interval[2, 7]
define Overlaps_OverlapsAfterIntIvl: Interval[3, 8] overlaps Interval[1, 6]
define Overlaps_OverlapsBoundaryIntIvl: Interval[1, 5] overlaps Interval[5, 10]
define Overlaps_NoOverlapsIntIvl: Interval[1,5) overlaps Interval[5, 10]
define Overlaps_OverlapsBeforeRealIvl: Interval[1.234, 1.567] overlaps Interval[1.345, 1.678]
define Overlaps_OverlapsAfterRealIvl: Interval[1.345, 1.678] overlaps Interval[1.234, 1.567]
define Overlaps_OverlapsBoundaryRealIvl: Interval[1.0, 1.234] overlaps Interval[1.234, 2.0]
define Overlaps_NoOverlapsRealIvl: Interval[1.0, 1.23456789) overlaps Interval[1.23456789, 2.0]

define test_Overlaps_OverlapsBeforeIntIvl: TestMessage(Overlaps_OverlapsBeforeIntIvl, 'Overlaps_OverlapsBeforeIntIvl', toString(true), toString(Overlaps_OverlapsBeforeIntIvl))
define test_Overlaps_OverlapsAfterIntIvl: TestMessage(Overlaps_OverlapsAfterIntIvl, 'Overlaps_OverlapsAfterIntIvl', toString(true), toString(Overlaps_OverlapsAfterIntIvl))
define test_Overlaps_OverlapsBoundaryIntIvl: TestMessage(Overlaps_OverlapsBoundaryIntIvl, 'Overlaps_OverlapsBoundaryIntIvl', toString(true), toString(Overlaps_OverlapsBoundaryIntIvl))
define test_Overlaps_NoOverlapsIntIvl: TestMessage(not Overlaps_NoOverlapsIntIvl, 'Overlaps_NoOverlapsIntIvl', toString(false), toString(Overlaps_NoOverlapsIntIvl))
define test_Overlaps_OverlapsBeforeRealIvl: TestMessage(Overlaps_OverlapsBeforeRealIvl, 'Overlaps_OverlapsBeforeRealIvl', toString(true), toString(Overlaps_OverlapsBeforeRealIvl))
define test_Overlaps_OverlapsAfterRealIvl: TestMessage(Overlaps_OverlapsAfterRealIvl, 'Overlaps_OverlapsAfterRealIvl', toString(true), toString(Overlaps_OverlapsAfterRealIvl))
define test_Overlaps_OverlapsBoundaryRealIvl: TestMessage(Overlaps_OverlapsBoundaryRealIvl, 'Overlaps_OverlapsBoundaryRealIvl', toString(true), toString(Overlaps_OverlapsBoundaryRealIvl))
define test_Overlaps_NoOverlapsRealIvl: TestMessage(not Overlaps_NoOverlapsRealIvl, 'Overlaps_NoOverlapsRealIvl', toString(false), toString(Overlaps_NoOverlapsRealIvl))

// OverlapsDateTime
define ivlA: Interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2012, 6, 1, 0, 0, 0, 0))
define ivlB: Interval[DateTime(2012, 3, 1, 0, 0, 0, 0), DateTime(2012, 9, 1, 0, 0, 0, 0))
define ivlC: Interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2013, 1, 1, 0, 0, 0, 0))
define ivlD: Interval[DateTime(2013, 1, 1, 0, 0, 0, 0), DateTime(2014, 1, 1, 0, 0, 0, 0))
define ivlE: Interval[DateTime(2013), DateTime(2015)]
define ivlF: Interval[DateTime(2014), DateTime(2016)]
define ivlG: Interval[DateTime(2016), DateTime(2017)]
define ivlH: Interval[DateTime(2012, 2, 3), DateTime(2013, 3)]

define OverlapsDT_OverlapsBefore: ivlA overlaps ivlB
define OverlapsDT_OverlapsAfter: ivlB overlaps ivlA
define OverlapsDT_OverlapsContained: ivlB overlaps ivlC
define OverlapsDT_OverlapsContains: ivlC overlaps ivlB
// TODO - this test should return null, but Bonnie expects true
// define OverlapsDT_ImpreciseOverlap: ivlD overlaps ivlH
define OverlapsDT_NoOverlap: ivlC overlaps ivlD
define OverlapsDT_NoImpreciseOverlap: ivlE overlaps ivlG
define OverlapsDT_UnknownOverlap: ivlE overlaps ivlH
define OverlapsDT_MatchingPrecisionOverlap: ivlF overlaps ivlG
// NOTE: There appears to be a bug in cql-to-elm that translates these 'overlaps' to 'OverlapsAfter'!
define OverlapsDT_OverlapsBeforeDayOfIvlEdge: PrecisionDateIvl overlaps day of Interval[DateTime(2012, 9, 2, 23, 59, 59, 999), DateTime(2012, 10, 1, 0, 0, 0, 0)]
define OverlapsDT_OverlapsAfterDayOfIvlEdge: PrecisionDateIvl overlaps day of Interval[DateTime(2012, 1, 2, 0, 0, 0, 0), DateTime(2012, 3, 2, 0, 0, 0, 0)]
define OverlapsDT_OverlapsContainsDayOfIvl: PrecisionDateIvl overlaps day of Interval[DateTime(2012, 5, 1, 0, 0, 0, 0), DateTime(2012, 6, 1, 0, 0, 0, 0)]
define OverlapsDT_OverlapsContainedByDayOfIvl: PrecisionDateIvl overlaps day of Interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2012, 12, 1, 0, 0, 0, 0)]
define OverlapsDT_NotOverlapsDayOfIvl: PrecisionDateIvl overlaps day of Interval[DateTime(2012, 1, 2, 0, 0, 0, 0), DateTime(2012, 3, 1, 0, 0, 0, 0)]
define OverlapsDT_OverlapsAfterDayOfImpreciseInterval: PrecisionDateIvl overlaps day of Interval[DateTime(2012, 1), DateTime(2012, 4)]
define OverlapsDT_MayOverlapBeforeDayOfImpreciseIvl: PrecisionDateIvl overlaps day of Interval[DateTime(2012, 9), DateTime(2012, 10)]
define OverlapsDT_MayOverlapAfterDayOfImpreciseIvl: PrecisionDateIvl overlaps day of Interval[DateTime(2012, 1), DateTime(2012, 3)]

define test_OverlapsDT_OverlapsBefore: TestMessage(OverlapsDT_OverlapsBefore, 'OverlapsDT_OverlapsBefore', toString(true), toString(OverlapsDT_OverlapsBefore))
define test_OverlapsDT_OverlapsAfter: TestMessage(OverlapsDT_OverlapsAfter, 'OverlapsDT_OverlapsAfter', toString(true), toString(OverlapsDT_OverlapsAfter))
define test_OverlapsDT_OverlapsContained: TestMessage(OverlapsDT_OverlapsContained, 'OverlapsDT_OverlapsContained', toString(true), toString(OverlapsDT_OverlapsContained))
define test_OverlapsDT_OverlapsContains: TestMessage(OverlapsDT_OverlapsContains, 'OverlapsDT_OverlapsContains', toString(true), toString(OverlapsDT_OverlapsContains))
// define test_OverlapsDT_ImpreciseOverlap: TestMessage(OverlapsDT_ImpreciseOverlap, 'OverlapsDT_ImpreciseOverlap', toString(true), toString(OverlapsDT_ImpreciseOverlap))
define test_OverlapsDT_NoOverlap: TestMessage(not OverlapsDT_NoOverlap, 'OverlapsDT_NoOverlap', toString(false), toString(OverlapsDT_NoOverlap))
define test_OverlapsDT_NoImpreciseOverlap: TestMessage(not OverlapsDT_NoImpreciseOverlap, 'OverlapsDT_NoImpreciseOverlap', toString(false), toString(OverlapsDT_OverlapsBefore))
define test_OverlapsDT_UnknownOverlap: TestMessage(OverlapsDT_UnknownOverlap is null, 'OverlapsDT_UnknownOverlap', 'null', toString(OverlapsDT_UnknownOverlap))
define test_OverlapsDT_MatchingPrecisionOverlap: TestMessage(OverlapsDT_MatchingPrecisionOverlap, 'OverlapsDT_MatchingPrecisionOverlap', toString(true), toString(OverlapsDT_MatchingPrecisionOverlap))
define test_OverlapsDT_OverlapsBeforeDayOfIvlEdge: TestMessage(OverlapsDT_OverlapsBeforeDayOfIvlEdge, 'OverlapsDT_OverlapsBeforeDayOfIvlEdge', toString(true), toString(OverlapsDT_OverlapsBeforeDayOfIvlEdge))
define test_OverlapsDT_OverlapsAfterDayOfIvlEdge: TestMessage(OverlapsDT_OverlapsAfterDayOfIvlEdge, 'OverlapsDT_OverlapsAfterDayOfIvlEdge', toString(true), toString(OverlapsDT_OverlapsAfterDayOfIvlEdge))
define test_OverlapsDT_OverlapsContainsDayOfIvl: TestMessage(OverlapsDT_OverlapsContainsDayOfIvl, 'OverlapsDT_OverlapsContainsDayOfIvl', toString(true), toString(OverlapsDT_OverlapsContainsDayOfIvl))
define test_OverlapsDT_OverlapsContainedByDayOfIvl: TestMessage(OverlapsDT_OverlapsContainedByDayOfIvl, 'OverlapsDT_OverlapsContainedByDayOfIvl', toString(true), toString(OverlapsDT_OverlapsContainedByDayOfIvl))
define test_OverlapsDT_NotOverlapsDayOfIvl: TestMessage(not OverlapsDT_NotOverlapsDayOfIvl, 'OverlapsDT_NotOverlapsDayOfIvl', toString(false), toString(OverlapsDT_NotOverlapsDayOfIvl))
define test_OverlapsDT_OverlapsAfterDayOfImpreciseInterval: TestMessage(OverlapsDT_OverlapsAfterDayOfImpreciseInterval, 'OverlapsDT_OverlapsAfterDayOfImpreciseInterval', toString(true), toString(OverlapsDT_OverlapsAfterDayOfImpreciseInterval))
define test_OverlapsDT_MayOverlapBeforeDayOfImpreciseIvl: TestMessage(OverlapsDT_MayOverlapBeforeDayOfImpreciseIvl is null, 'OverlapsDT_MayOverlapBeforeDayOfImpreciseIvl', 'null', toString(OverlapsDT_MayOverlapBeforeDayOfImpreciseIvl))
define test_OverlapsDT_MayOverlapAfterDayOfImpreciseIvl: TestMessage(OverlapsDT_MayOverlapAfterDayOfImpreciseIvl is null, 'OverlapsDT_MayOverlapAfterDayOfImpreciseIvl', 'null', toString(OverlapsDT_MayOverlapAfterDayOfImpreciseIvl))

// OverlapsAfter
define OverlapsAfter_OverlapsBeforeIntIvl: Interval[1, 5] overlaps after Interval[2, 7]
define OverlapsAfter_OverlapsAfterIntIvl: Interval[3, 8] overlaps after Interval[1, 6]
define OverlapsAfter_OverlapsBoundaryIntIvl: Interval[5, 10] overlaps after Interval[1, 5]
define OverlapsAfter_NoOverlapsIntIvl: Interval[1,5) overlaps after Interval[5, 10]
define OverlapsAfter_OverlapsBeforeRealIvl: Interval[1.234, 1.567] overlaps after Interval[1.345, 1.678]
define OverlapsAfter_OverlapsAfterRealIvl: Interval[1.345, 1.678] overlaps after Interval[1.234, 1.567]
define OverlapsAfter_OverlapsBoundaryRealIvl: Interval[1.234, 2.0] overlaps after Interval[1.0, 1.234]
define OverlapsAfter_NoOverlapsRealIvl: Interval[1.0, 1.23456789) overlaps after Interval[1.23456789, 2.0]

define test_OverlapsAfter_OverlapsBeforeIntIvl: TestMessage(not OverlapsAfter_OverlapsBeforeIntIvl, 'OverlapsAfter_OverlapsBeforeIntIvl', toString(false), toString(OverlapsAfter_OverlapsBeforeIntIvl))
define test_OverlapsAfter_OverlapsAfterIntIvl: TestMessage(OverlapsAfter_OverlapsAfterIntIvl, 'OverlapsAfter_OverlapsAfterIntIvl', toString(true), toString(OverlapsAfter_OverlapsAfterIntIvl))
define test_OverlapsAfter_OverlapsBoundaryIntIvl: TestMessage(OverlapsAfter_OverlapsBoundaryIntIvl, 'OverlapsAfter_OverlapsBoundaryIntIvl', toString(true), toString(OverlapsAfter_OverlapsBoundaryIntIvl))
define test_OverlapsAfter_NoOverlapsIntIvl: TestMessage(not OverlapsAfter_NoOverlapsIntIvl, 'OverlapsAfter_NoOverlapsIntIvl', toString(false), toString(OverlapsAfter_NoOverlapsIntIvl))
define test_OverlapsAfter_OverlapsBeforeRealIvl: TestMessage(not OverlapsAfter_OverlapsBeforeRealIvl, 'OverlapsAfter_OverlapsBeforeRealIvl', toString(false), toString(OverlapsAfter_OverlapsBeforeRealIvl))
define test_OverlapsAfter_OverlapsAfterRealIvl: TestMessage(OverlapsAfter_OverlapsAfterRealIvl, 'OverlapsAfter_OverlapsAfterRealIvl', toString(true), toString(OverlapsAfter_OverlapsAfterRealIvl))
define test_OverlapsAfter_OverlapsBoundaryRealIvl: TestMessage(OverlapsAfter_OverlapsBoundaryRealIvl, 'OverlapsAfter_OverlapsBoundaryRealIvl', toString(true), toString(OverlapsAfter_OverlapsBoundaryRealIvl))
define test_OverlapsAfter_NoOverlapsRealIvl: TestMessage(not OverlapsAfter_NoOverlapsRealIvl, 'OverlapsAfter_NoOverlapsRealIvl', toString(false), toString(OverlapsAfter_NoOverlapsRealIvl))

// OverlapsAfterDateTime
define ivlH_OA: Interval[DateTime(2013, 1), DateTime(2015, 1)]
define OverlapsAfterDT_OverlapsBefore: ivlA overlaps after ivlB
define OverlapsAfterDT_OverlapsAfter: ivlB overlaps after ivlA
define OverlapsAfterDT_OverlapsContained: ivlB overlaps after ivlC
define OverlapsAfterDT_OverlapsContains: ivlC overlaps after ivlB
define OverlapsAfterDT_ImpreciseOverlapBefore: ivlE overlaps after ivlF
define OverlapsAfterDT_ImpreciseOverlapAfter: ivlF overlaps after ivlE
define OverlapsAfterDT_NoOverlap: ivlC overlaps after ivlD
define OverlapsAfterDT_NoImpreciseOverlap: ivlE overlaps after ivlG
define OverlapsAfterDT_MatchingPrecisionOverlap: ivlG overlaps after ivlF
define OverlapsAfterDT_UnknownOverlap: ivlH_OA overlaps after ivlE
define OverlapsAfterDT_PrecisionDateIvl: Interval[DateTime(2012, 3, 2, 12, 34, 56, 789), DateTime(2012, 9, 2, 1, 23, 45, 678))
define OverlapsAfterDT_OverlapsBeforeDayOfIvlEdge: PrecisionDateIvl overlaps after day of Interval[DateTime(2012, 9, 2, 23, 59, 59, 999), DateTime(2012, 10, 1, 0, 0, 0, 0)]
define OverlapsAfterDT_OverlapsAfterDayOfIvlEdge: PrecisionDateIvl overlaps after day of Interval[DateTime(2012, 1, 2, 0, 0, 0, 0), DateTime(2012, 3, 2, 0, 0, 0, 0)]
define OverlapsAfterDT_OverlapsContainsDayOfIvl: PrecisionDateIvl overlaps after day of Interval[DateTime(2012, 5, 1, 0, 0, 0, 0), DateTime(2012, 6, 1, 0, 0, 0, 0)]
define OverlapsAfterDT_OverlapsContainedByDayOfIvl: PrecisionDateIvl overlaps after day of Interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2012, 12, 1, 0, 0, 0, 0)]
define OverlapsAfterDT_NotOverlapsDayOfIvl: PrecisionDateIvl overlaps after day of Interval[DateTime(2012, 1, 2, 0, 0, 0, 0), DateTime(2012, 3, 1, 0, 0, 0, 0)]
define OverlapsAfterDT_OverlapsAfterDayOfImpreciseInterval: PrecisionDateIvl overlaps after day of Interval[DateTime(2012, 1), DateTime(2012, 4)]
define OverlapsAfterDT_MayOverlapBeforeDayOfImpreciseIvl: PrecisionDateIvl overlaps after day of Interval[DateTime(2012, 9), DateTime(2012, 10)]
define OverlapsAfterDT_MayOverlapAfterDayOfImpreciseIvl: PrecisionDateIvl overlaps after day of Interval[DateTime(2012, 1), DateTime(2012, 3)]

define test_OverlapsAfterDT_OverlapsBefore: TestMessage(not OverlapsAfterDT_OverlapsBefore, 'OverlapsAfterDT_OverlapsBefore', toString(false), toString(OverlapsAfterDT_OverlapsBefore))
define test_OverlapsAfterDT_OverlapsAfter: TestMessage(OverlapsAfterDT_OverlapsAfter, 'OverlapsAfterDT_OverlapsAfter', toString(true), toString(OverlapsAfterDT_OverlapsAfter))
define test_OverlapsAfterDT_OverlapsContained: TestMessage(not OverlapsAfterDT_OverlapsContained, 'OverlapsAfterDT_OverlapsContained', toString(false), toString(OverlapsAfterDT_OverlapsContained))
define test_OverlapsAfterDT_OverlapsContains: TestMessage(OverlapsAfterDT_OverlapsContains, 'OverlapsAfterDT_OverlapsContains', toString(true), toString(OverlapsAfterDT_OverlapsContains))
define test_OverlapsAfterDT_ImpreciseOverlapBefore: TestMessage(not OverlapsAfterDT_ImpreciseOverlapBefore, 'OverlapsAfterDT_ImpreciseOverlapBefore', toString(false), toString(OverlapsAfterDT_ImpreciseOverlapBefore))
define test_OverlapsAfterDT_ImpreciseOverlapAfter: TestMessage(OverlapsAfterDT_ImpreciseOverlapAfter, 'OverlapsAfterDT_ImpreciseOverlapAfter', toString(true), toString(OverlapsAfterDT_ImpreciseOverlapAfter))
define test_OverlapsAfterDT_NoOverlap: TestMessage(not OverlapsAfterDT_NoOverlap, 'OverlapsAfterDT_NoOverlap', toString(false), toString(OverlapsAfterDT_NoOverlap))
define test_OverlapsAfterDT_NoImpreciseOverlap: TestMessage(not OverlapsAfterDT_NoImpreciseOverlap, 'OverlapsAfterDT_NoImpreciseOverlap', toString(false), toString(OverlapsAfterDT_NoImpreciseOverlap))
define test_OverlapsAfterDT_MatchingPrecisionOverlap: TestMessage(OverlapsAfterDT_MatchingPrecisionOverlap, 'OverlapsAfterDT_MatchingPrecisionOverlap', toString(true), toString(OverlapsAfterDT_MatchingPrecisionOverlap))
define test_OverlapsAfterDT_UnknownOverlap: TestMessage(OverlapsAfterDT_UnknownOverlap is null, 'OverlapsAfterDT_UnknownOverlap', 'null', toString(OverlapsAfterDT_UnknownOverlap))
define test_OverlapsAfterDT_OverlapsBeforeDayOfIvlEdge: TestMessage(not OverlapsAfterDT_OverlapsBeforeDayOfIvlEdge, 'OverlapsAfterDT_OverlapsBeforeDayOfIvlEdge', toString(false), toString(OverlapsAfterDT_OverlapsBeforeDayOfIvlEdge))
define test_OverlapsAfterDT_OverlapsAfterDayOfIvlEdge: TestMessage(OverlapsAfterDT_OverlapsAfterDayOfIvlEdge, 'OverlapsAfterDT_OverlapsAfterDayOfIvlEdge', toString(true), toString(OverlapsAfterDT_OverlapsAfterDayOfIvlEdge))
define test_OverlapsAfterDT_OverlapsContainsDayOfIvl: TestMessage(OverlapsAfterDT_OverlapsContainsDayOfIvl, 'OverlapsAfterDT_OverlapsContainsDayOfIvl', toString(true), toString(OverlapsAfterDT_OverlapsContainsDayOfIvl))
define test_OverlapsAfterDT_OverlapsContainedByDayOfIvl: TestMessage(not OverlapsAfterDT_OverlapsContainedByDayOfIvl, 'OverlapsAfterDT_OverlapsContainedByDayOfIvl', toString(false), toString(OverlapsAfterDT_OverlapsContainedByDayOfIvl))
define test_OverlapsAfterDT_NotOverlapsDayOfIvl: TestMessage(not OverlapsAfterDT_NotOverlapsDayOfIvl, 'OverlapsAfterDT_NotOverlapsDayOfIvl', toString(false), toString(OverlapsAfterDT_NotOverlapsDayOfIvl))
define test_OverlapsAfterDT_OverlapsAfterDayOfImpreciseInterval: TestMessage(OverlapsAfterDT_OverlapsAfterDayOfImpreciseInterval, 'OverlapsAfterDT_OverlapsAfterDayOfImpreciseInterval', toString(true), toString(OverlapsAfterDT_OverlapsAfterDayOfImpreciseInterval))
define test_OverlapsAfterDT_MayOverlapBeforeDayOfImpreciseIvl: TestMessage(not OverlapsAfterDT_MayOverlapBeforeDayOfImpreciseIvl, 'OverlapsAfterDT_MayOverlapBeforeDayOfImpreciseIvl', toString(false), toString(OverlapsAfterDT_MayOverlapBeforeDayOfImpreciseIvl))
define test_OverlapsAfterDT_MayOverlapAfterDayOfImpreciseIvl: TestMessage(OverlapsAfterDT_MayOverlapAfterDayOfImpreciseIvl is null, 'OverlapsAfterDT_MayOverlapAfterDayOfImpreciseIvl', 'null', toString(OverlapsAfterDT_MayOverlapAfterDayOfImpreciseIvl))

// OverlapsBefore
define OverlapsBefore_OverlapsBeforeIntIvl: Interval[1, 5] overlaps before Interval[2, 7]
define OverlapsBefore_OverlapsAfterIntIvl: Interval[3, 8] overlaps before Interval[1, 6]
define OverlapsBefore_OverlapsBoundaryIntIvl: Interval[1, 5] overlaps before Interval[5, 10]
define OverlapsBefore_NoOverlapsIntIvl: Interval[1,5) overlaps before Interval[5, 10]
define OverlapsBefore_OverlapsBeforeRealIvl: Interval[1.234, 1.567] overlaps before Interval[1.345, 1.678]
define OverlapsBefore_OverlapsAfterRealIvl: Interval[1.345, 1.678] overlaps before Interval[1.234, 1.567]
define OverlapsBefore_OverlapsBoundaryRealIvl: Interval[1.0, 1.234] overlaps before Interval[1.234, 2.0]
define OverlapsBefore_NoOverlapsRealIvl: Interval[1.0, 1.23456789) overlaps before Interval[1.23456789, 2.0]

define test_OverlapsBefore_OverlapsBeforeIntIvl: TestMessage(OverlapsBefore_OverlapsBeforeIntIvl, 'OverlapsBefore_OverlapsBeforeIntIvl', toString(true), toString(OverlapsBefore_OverlapsBeforeIntIvl))
define test_OverlapsBefore_OverlapsAfterIntIvl: TestMessage(not OverlapsBefore_OverlapsAfterIntIvl, 'OverlapsBefore_OverlapsAfterIntIvl', toString(false), toString(OverlapsBefore_OverlapsAfterIntIvl))
define test_OverlapsBefore_OverlapsBoundaryIntIvl: TestMessage(OverlapsBefore_OverlapsBoundaryIntIvl, 'OverlapsBefore_OverlapsBoundaryIntIvl', toString(true), toString(OverlapsBefore_OverlapsBoundaryIntIvl))
define test_OverlapsBefore_NoOverlapsIntIvl: TestMessage(not OverlapsBefore_NoOverlapsIntIvl, 'OverlapsBefore_NoOverlapsIntIvl', toString(false), toString(OverlapsBefore_NoOverlapsIntIvl))
define test_OverlapsBefore_OverlapsBeforeRealIvl: TestMessage(OverlapsBefore_OverlapsBeforeRealIvl, 'OverlapsBefore_OverlapsBeforeRealIvl', toString(true), toString(OverlapsBefore_OverlapsBeforeRealIvl))
define test_OverlapsBefore_OverlapsAfterRealIvl: TestMessage(not OverlapsBefore_OverlapsAfterRealIvl, 'OverlapsBefore_OverlapsAfterRealIvl', toString(false), toString(OverlapsBefore_OverlapsAfterRealIvl))
define test_OverlapsBefore_OverlapsBoundaryRealIvl: TestMessage(OverlapsBefore_OverlapsBoundaryRealIvl, 'OverlapsBefore_OverlapsBoundaryRealIvl', toString(true), toString(OverlapsBefore_OverlapsBoundaryRealIvl))
define test_OverlapsBefore_NoOverlapsRealIvl: TestMessage(not OverlapsBefore_NoOverlapsRealIvl, 'OverlapsBefore_NoOverlapsRealIvl', toString(false), toString(OverlapsBefore_NoOverlapsRealIvl))

// OverlapsBeforeDateTime
define ivlH_OB: Interval[DateTime(2011, 3), DateTime(2013, 3)]
define OverlapsBeforeDT_OverlapsBefore: ivlA overlaps before ivlB
define OverlapsBeforeDT_OverlapsAfter: ivlB overlaps before ivlA
define OverlapsBeforeDT_OverlapsContained: ivlB overlaps before ivlC
define OverlapsBeforeDT_OverlapsContains: ivlC overlaps before ivlB
define OverlapsBeforeDT_ImpreciseOverlapBefore: ivlE overlaps before ivlF
define OverlapsBeforeDT_ImpreciseOverlapAfter: ivlF overlaps before ivlE
define OverlapsBeforeDT_NoOverlap: ivlC overlaps before ivlD
define OverlapsBeforeDT_NoImpreciseOverlap: ivlE overlaps before ivlG
define OverlapsBeforeDT_MatchingPrecisionOverlap: ivlF overlaps before ivlG
define OverlapsBeforeDT_UnknownOverlap: ivlH_OB overlaps before ivlE
define OverlapsBeforeDT_OverlapsBeforeDayOfIvlEdge: PrecisionDateIvl overlaps before day of Interval[DateTime(2012, 9, 2, 23, 59, 59, 999), DateTime(2012, 10, 1, 0, 0, 0, 0)]
define OverlapsBeforeDT_OverlapsAfterDayOfIvlEdge: PrecisionDateIvl overlaps before day of Interval[DateTime(2012, 1, 2, 0, 0, 0, 0), DateTime(2012, 3, 2, 0, 0, 0, 0)]
define OverlapsBeforeDT_OverlapsContainsDayOfIvl: PrecisionDateIvl overlaps before day of Interval[DateTime(2012, 5, 1, 0, 0, 0, 0), DateTime(2012, 6, 1, 0, 0, 0, 0)]
define OverlapsBeforeDT_OverlapsContainedByDayOfIvl: PrecisionDateIvl overlaps before day of Interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2012, 12, 1, 0, 0, 0, 0)]
define OverlapsBeforeDT_NotOverlapsDayOfIvl: PrecisionDateIvl overlaps before day of Interval[DateTime(2012, 1, 2, 0, 0, 0, 0), DateTime(2012, 3, 1, 0, 0, 0, 0)]
define OverlapsBeforeDT_OverlapsAfterDayOfImpreciseInterval: PrecisionDateIvl overlaps before day of Interval[DateTime(2012, 1), DateTime(2012, 4)]
define OverlapsBeforeDT_MayOverlapBeforeDayOfImpreciseIvl: PrecisionDateIvl overlaps before day of Interval[DateTime(2012, 9), DateTime(2012, 10)]
define OverlapsBeforeDT_MayOverlapAfterDayOfImpreciseIvl: PrecisionDateIvl overlaps before day of Interval[DateTime(2012, 1), DateTime(2012, 3)]

define test_OverlapsBeforeDT_OverlapsBefore: TestMessage(OverlapsBeforeDT_OverlapsBefore, 'OverlapsBeforeDT_OverlapsBefore', toString(true), toString(OverlapsBeforeDT_OverlapsBefore))
define test_OverlapsBeforeDT_OverlapsAfter: TestMessage(not OverlapsBeforeDT_OverlapsAfter, 'OverlapsBeforeDT_OverlapsAfter', toString(false), toString(OverlapsBeforeDT_OverlapsAfter))
define test_OverlapsBeforeDT_OverlapsContained: TestMessage(not OverlapsBeforeDT_OverlapsContained, 'OverlapsBeforeDT_OverlapsContained', toString(false), toString(OverlapsBeforeDT_OverlapsContained))
define test_OverlapsBeforeDT_OverlapsContains: TestMessage(OverlapsBeforeDT_OverlapsContains, 'OverlapsBeforeDT_OverlapsContains', toString(true), toString(OverlapsBeforeDT_OverlapsContains))
define test_OverlapsBeforeDT_ImpreciseOverlapAfter: TestMessage(not OverlapsBeforeDT_ImpreciseOverlapAfter, 'OverlapsBeforeDT_ImpreciseOverlapAfter', toString(false), toString(OverlapsBeforeDT_ImpreciseOverlapAfter))
define test_OverlapsBeforeDT_ImpreciseOverlapBefore: TestMessage(OverlapsBeforeDT_ImpreciseOverlapBefore, 'OverlapsBeforeDT_ImpreciseOverlapBefore', toString(true), toString(OverlapsBeforeDT_ImpreciseOverlapBefore))
define test_OverlapsBeforeDT_NoOverlap: TestMessage(not OverlapsBeforeDT_NoOverlap, 'OverlapsBeforeDT_NoOverlap', toString(false), toString(OverlapsBeforeDT_NoOverlap))
define test_OverlapsBeforeDT_NoImpreciseOverlap: TestMessage(not OverlapsBeforeDT_NoImpreciseOverlap, 'OverlapsBeforeDT_NoImpreciseOverlap', toString(false), toString(OverlapsBeforeDT_NoImpreciseOverlap))
define test_OverlapsBeforeDT_MatchingPrecisionOverlap: TestMessage(OverlapsBeforeDT_MatchingPrecisionOverlap, 'OverlapsBeforeDT_MatchingPrecisionOverlap', toString(true), toString(OverlapsBeforeDT_MatchingPrecisionOverlap))
define test_OverlapsBeforeDT_UnknownOverlap: TestMessage(OverlapsBeforeDT_UnknownOverlap is null, 'OverlapsBeforeDT_UnknownOverlap', 'null', toString(OverlapsBeforeDT_UnknownOverlap))
define test_OverlapsBeforeDT_OverlapsBeforeDayOfIvlEdge: TestMessage(OverlapsBeforeDT_OverlapsBeforeDayOfIvlEdge, 'OverlapsBeforeDT_OverlapsBeforeDayOfIvlEdge', toString(true), toString(OverlapsBeforeDT_OverlapsBeforeDayOfIvlEdge))
define test_OverlapsBeforeDT_OverlapsAfterDayOfIvlEdge: TestMessage(not OverlapsBeforeDT_OverlapsAfterDayOfIvlEdge, 'OverlapsBeforeDT_OverlapsAfterDayOfIvlEdge', toString(false), toString(OverlapsBeforeDT_OverlapsAfterDayOfIvlEdge))
define test_OverlapsBeforeDT_OverlapsContainsDayOfIvl: TestMessage(OverlapsBeforeDT_OverlapsContainsDayOfIvl, 'OverlapsBeforeDT_OverlapsContainsDayOfIvl', toString(true), toString(OverlapsBeforeDT_OverlapsContainsDayOfIvl))
define test_OverlapsBeforeDT_OverlapsContainedByDayOfIvl: TestMessage(not OverlapsBeforeDT_OverlapsContainedByDayOfIvl, 'OverlapsBeforeDT_OverlapsContainedByDayOfIvl', toString(false), toString(OverlapsBeforeDT_OverlapsContainedByDayOfIvl))
define test_OverlapsBeforeDT_NotOverlapsDayOfIvl: TestMessage(not OverlapsBeforeDT_NotOverlapsDayOfIvl, 'OverlapsBeforeDT_NotOverlapsDayOfIvl', toString(false), toString(OverlapsBeforeDT_NotOverlapsDayOfIvl))
define test_OverlapsBeforeDT_OverlapsAfterDayOfImpreciseInterval: TestMessage(not OverlapsBeforeDT_OverlapsAfterDayOfImpreciseInterval, 'OverlapsBeforeDT_OverlapsAfterDayOfImpreciseInterval', toString(false), toString(OverlapsBeforeDT_OverlapsAfterDayOfImpreciseInterval))
define test_OverlapsBeforeDT_MayOverlapBeforeDayOfImpreciseIvl: TestMessage(OverlapsBeforeDT_MayOverlapBeforeDayOfImpreciseIvl is null, 'OverlapsBeforeDT_MayOverlapBeforeDayOfImpreciseIvl', 'null', toString(OverlapsBeforeDT_MayOverlapBeforeDayOfImpreciseIvl))
define test_OverlapsBeforeDT_MayOverlapAfterDayOfImpreciseIvl: TestMessage(not OverlapsBeforeDT_MayOverlapAfterDayOfImpreciseIvl, 'OverlapsBeforeDT_MayOverlapAfterDayOfImpreciseIvl', toString(false), toString(OverlapsBeforeDT_MayOverlapAfterDayOfImpreciseIvl))

// Width
define Width_IntWidth: width of Interval[-2, 5]
define Width_IntOpenWidth: width of Interval(-2, 5)
define Width_IntWidthThreeToMax: width of Interval[3, null]
define Width_IntWidthMinToThree: width of Interval[null, 3]
define Width_IntWidthThreeToUnknown: width of Interval[3, null)
define Width_IntWidthUnknownToThree: width of Interval(null, 3]
define Width_RealWidth: width of Interval[1.23, 4.56]
define Width_RealOpenWidth: width of Interval(1.23, 4.56)

define test_Width_IntWidth: TestMessage(Width_IntWidth = 7, 'Width_IntWidth', toString(7), toString(Width_IntWidth))
define test_Width_IntOpenWidth: TestMessage(Width_IntOpenWidth = 5, 'Width_IntOpenWidth', toString(5), toString(Width_IntOpenWidth))
define test_Width_IntWidthThreeToMax: TestMessage(Width_IntWidthThreeToMax = 2147483644, 'Width_IntWidthThreeToMax', toString(2147483644), toString(Width_IntWidthThreeToMax))
// TODO - result of test results in value larger than maximum allowed Integer value
// define test_Width_IntWidthMinToThree: TestMessage(Width_IntWidthMinToThree = 2147483651, 'Width_IntWidthMinToThree', toString(2147483651), toString(Width_IntWidthMinToThree))
define test_Width_IntWidthThreeToUnknown: TestMessage(Width_IntWidthThreeToUnknown is null, 'Width_IntWidthThreeToUnknown', 'null', toString(Width_IntWidthThreeToUnknown))
define test_Width_IntWidthUnknownToThree: TestMessage(Width_IntWidthUnknownToThree is null, 'Width_IntWidthUnknownToThree', 'null', toString(Width_IntWidthUnknownToThree))
define test_Width_RealWidth: TestMessage(Width_RealWidth = 3.33, 'Width_RealWidth', toString(3.33), toString(Width_RealWidth))
define test_Width_RealOpenWidth: TestMessage(Width_RealOpenWidth = 3.32999998, 'Width_RealOpenWidth', toString(3.32999998), toString(Width_RealOpenWidth))

// Start
define Start_ClosedNotNull: start of Interval[DateTime(2012, 1, 1), DateTime(2013, 1, 1)]
define Start_ClosedNullDateTime: start of Interval[null, DateTime(2013, 1, 1)]
define Start_ClosedNullInteger: start of Interval[null, 3]
define Start_ClosedNullDecimal: start of Interval[null, 5.1]
define Start_NullInterval: start of (null as Interval<DateTime>)
define Start_OpenNotNull: start of Interval(DateTime(2012, 1, 1), DateTime(2013, 1, 1))
define Start_OpenNull: start of Interval(null, DateTime(2013, 1, 1))

define test_Start_ClosedNotNull: TestMessage(Start_ClosedNotNull = DateTime(2012, 1, 1), 'Start_ClosedNotNull', toString(DateTime(2012, 1, 1)), toString(Start_ClosedNotNull))
define test_Start_ClosedNullDateTime: TestMessage(Start_ClosedNullDateTime = minimum DateTime, 'Start_ClosedNullDateTime', toString(minimum DateTime), toString(Start_ClosedNullDateTime))
define test_Start_ClosedNullInteger: TestMessage(Start_ClosedNullInteger = minimum Integer, 'Start_ClosedNullInteger', toString(minimum Integer), toString(Start_ClosedNullInteger))
define test_Start_ClosedNullDecimal: TestMessage(Start_ClosedNullDecimal = minimum Decimal, 'Start_ClosedNullDecimal', toString(minimum Decimal), toString(Start_ClosedNullDecimal))
define test_Start_NullInterval: TestMessage(Start_NullInterval is null, 'Start_NullInterval', 'null', toString(Start_NullInterval))
define test_Start_OpenNotNull: TestMessage(Start_OpenNotNull = successor of DateTime(2012, 1, 1), 'Start_OpenNotNull', toString(successor of DateTime(2012, 1, 1)), toString(Start_OpenNotNull))
define test_Start_OpenNull: TestMessage(Start_OpenNull is null, 'Start_OpenNull', 'null', toString(Start_OpenNull))

// End
define End_ClosedNotNull: end of Interval[DateTime(2012, 1, 1), DateTime(2013, 1, 1)]
define End_ClosedNullDateTime: end of Interval[DateTime(2013, 1, 1), null]
define End_ClosedNullInteger: end of Interval[5, null]
define End_ClosedNullDecimal: end of Interval[7.3, null]
define End_NullInterval: end of (null as Interval<DateTime>)
define End_OpenNotNull: end of Interval(DateTime(2012, 1, 1), DateTime(2013, 1, 1))
define End_OpenNull: end of Interval(DateTime(2013, 1, 1), null)

define test_End_ClosedNotNull: TestMessage(End_ClosedNotNull = DateTime(2013, 1, 1), 'End_ClosedNotNull', toString(DateTime(2013, 1, 1)), toString(End_ClosedNotNull))
define test_End_ClosedNullDateTime: TestMessage(End_ClosedNullDateTime = maximum DateTime, 'End_ClosedNullDateTime', toString(maximum DateTime), toString(End_ClosedNullDateTime))
define test_End_ClosedNullInteger: TestMessage(End_ClosedNullInteger = maximum Integer, 'End_ClosedNullInteger', toString(maximum Integer), toString(End_ClosedNullInteger))
define test_End_ClosedNullDecimal: TestMessage(End_ClosedNullDecimal = maximum Decimal, 'End_ClosedNullDecimal', toString(maximum Decimal), toString(End_ClosedNullDecimal))
define test_End_NullInterval: TestMessage(End_NullInterval is null, 'End_NullInterval', 'null', toString(End_NullInterval))
define test_End_OpenNotNull: TestMessage(End_OpenNotNull = predecessor of DateTime(2013, 1, 1), 'End_OpenNotNull', toString(predecessor of DateTime(2013, 1, 1)), toString(End_OpenNotNull))
define test_End_OpenNull: TestMessage(End_OpenNull is null, 'End_OpenNull', 'null', toString(End_OpenNull))

// Starts
define Starts_TestStartsNull: Interval[null, null] starts Interval[1, 10]
define Starts_IntegerIntervalStartsTrue: Interval[4,10] starts Interval[4, 15]
define Starts_IntegerIntervalStartsFalse: Interval[1, 10] starts Interval[4, 10]
define Starts_IntegerIntervalStartEndsFalse: Interval[4, 10] starts Interval[4, 9]
define Starts_DecimalIntervalStartsTrue: Interval[4.0, 10.0] starts Interval[4.0, 15.0]
define Starts_DecimalIntervalStartsFalse: Interval[1.0, 10.0] starts Interval[4.0, 10.0]
define Starts_DecimalIntervalStartsEndsFalse: Interval[4.0, 10.0] starts Interval[4.0, 9.0]
define Starts_QuantityIntervalStartsTrue: Interval[5.0 'g', 10.0 'g'] starts Interval[5.0 'g', 15.0 'g']
define Starts_QuantityIntervalStartsFalse: Interval[1.0 'g', 10.0 'g'] starts Interval[5.0 'g', 10.0 'g']
define Starts_QuantityIntervalStartsEndsFalse: Interval[5.0 'g', 10.0 'g'] starts Interval[5.0 'g', 9.9 'g']
define Starts_DateTimeIntervalStartsTrue: Interval[DateTime(2012, 1, 5, 0, 0, 0, 0), DateTime(2012, 1, 25, 0, 0, 0, 0)] starts Interval[DateTime(2012, 1, 5, 0, 0, 0, 0), DateTime(2012, 1, 27, 0, 0, 0, 0)]
define Starts_DateTimeIntervalStartsFalse: Interval[DateTime(2012, 1, 5), DateTime(2012, 1, 25)] starts day of Interval[DateTime(2012, 1, 6), DateTime(2012, 1, 27)]
define Starts_DateTimeIntervalStartsDayOfTrue: Interval[DateTime(2012, 1, 5), DateTime(2012, 1, 25)] starts day of Interval[DateTime(2012, 1, 5), DateTime(2012, 1, 27)]
define Starts_DateTimeIntervalStartsEndsFalse: Interval[DateTime(2012, 1, 5), DateTime(2012, 1, 25)] starts day of Interval[DateTime(2012, 1, 5), DateTime(2012, 1, 24)]

define test_Starts_TestStartsNull: TestMessage(Starts_TestStartsNull is null, 'Starts_TestStartsNull', 'null', toString(Starts_TestStartsNull))
define test_Starts_IntegerIntervalStartsTrue: TestMessage(Starts_IntegerIntervalStartsTrue, 'Starts_IntegerIntervalStartsTrue', toString(true), toString(Starts_IntegerIntervalStartsTrue))
define test_Starts_IntegerIntervalStartsFalse: TestMessage(not Starts_IntegerIntervalStartsFalse, 'Starts_IntegerIntervalStartsFalse', toString(false), toString(Starts_IntegerIntervalStartsFalse))
define test_Starts_IntegerIntervalStartEndsFalse: TestMessage(not Starts_IntegerIntervalStartEndsFalse, 'Starts_IntegerIntervalStartEndsFalse', toString(false), toString(Starts_IntegerIntervalStartEndsFalse))
define test_Starts_DecimalIntervalStartsTrue: TestMessage(Starts_DecimalIntervalStartsTrue, 'Starts_DecimalIntervalStartsTrue', toString(true), toString(Starts_DecimalIntervalStartsTrue))
define test_Starts_DecimalIntervalStartsFalse: TestMessage(not Starts_DecimalIntervalStartsFalse, 'Starts_DecimalIntervalStartsFalse', toString(false), toString(Starts_DecimalIntervalStartsFalse))
define test_Starts_DecimalIntervalStartsEndsFalse: TestMessage(not Starts_DecimalIntervalStartsEndsFalse, 'Starts_DecimalIntervalStartsEndsFalse', toString(false), toString(Starts_DecimalIntervalStartsEndsFalse))
define test_Starts_QuantityIntervalStartsTrue: TestMessage(Starts_QuantityIntervalStartsTrue, 'Starts_QuantityIntervalStartsTrue', toString(true), toString(Starts_QuantityIntervalStartsTrue))
define test_Starts_QuantityIntervalStartsFalse: TestMessage(not Starts_QuantityIntervalStartsFalse, 'Starts_QuantityIntervalStartsFalse', toString(false), toString(Starts_QuantityIntervalStartsFalse))
define test_Starts_QuantityIntervalStartsEndsFalse: TestMessage(not Starts_QuantityIntervalStartsEndsFalse, 'Starts_QuantityIntervalStartsEndsFalse', toString(false), toString(Starts_QuantityIntervalStartsEndsFalse))
define test_Starts_DateTimeIntervalStartsTrue: TestMessage(Starts_DateTimeIntervalStartsTrue, 'Starts_DateTimeIntervalStartsTrue', toString(true), toString(Starts_DateTimeIntervalStartsTrue))
define test_Starts_DateTimeIntervalStartsFalse: TestMessage(not Starts_DateTimeIntervalStartsFalse, 'Starts_DateTimeIntervalStartsFalse', toString(false), toString(Starts_DateTimeIntervalStartsFalse))
define test_Starts_DateTimeIntervalStartsDayOfTrue: TestMessage(Starts_DateTimeIntervalStartsDayOfTrue, 'Starts_DateTimeIntervalStartsDayOfTrue', toString(true), toString(Starts_DateTimeIntervalStartsDayOfTrue))
define test_Starts_DateTimeIntervalStartsEndsFalse: TestMessage(not Starts_DateTimeIntervalStartsEndsFalse, 'Starts_DateTimeIntervalStartsEndsFalse', toString(false), toString(Starts_DateTimeIntervalStartsEndsFalse))

// Ends
define Ends_TestEndsNull: Interval[1, 10] ends Interval[null, null]
define Ends_IntegerIntervalEndsTrue: Interval[4,10] ends Interval[1,10]
define Ends_IntegerIntervalEndsFalse: Interval[4, 9] ends Interval[1,10]
define Ends_IntegerIntervalEndsStartsFalse: Interval[0, 10] ends Interval[1,10]
define Ends_DecimalIntervalEndsTrue: Interval[4.0,10.0] ends Interval[1.0,10.0]
define Ends_DecimalIntervalEndsFalse: Interval[11.0, 20.0] ends Interval[1.0,10.0]
define Ends_DecimalIntervalEndsStartsFalse: Interval[0.0, 10.0] ends Interval[1.0, 10.0]
define Ends_QuantityIntervalEndsTrue: Interval[5.0 'g', 10.0 'g'] ends Interval[1.0 'g', 10.0 'g']
define Ends_QuantityIntervalEndsFalse: Interval[5.0 'g', 10.0 'g'] ends Interval[1.0 'g', 10.5 'g']
define Ends_QuantityIntervalEndsStartsFalse: Interval[4.0 'g', 10.0 'g'] ends Interval[5.0 'g', 10.0 'g']
define Ends_DateTimeIntervalEndsTrue: Interval[DateTime(2012, 1, 5, 0, 0, 0, 0), DateTime(2012, 1, 15, 0, 0, 0, 0)] ends Interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2012, 1, 15, 0, 0, 0, 0)]
define Ends_DateTimeIntervalEndsFalse: Interval[DateTime(2012, 1, 5), DateTime(2012, 1, 15)] ends day of Interval[DateTime(2012, 1, 1), DateTime(2012, 1, 16)]
define Ends_DateTimeIntervalEndsDayOfTrue: Interval[DateTime(2012, 1, 5), DateTime(2012, 1, 15)] ends day of Interval[DateTime(2012, 1, 1), DateTime(2012, 1, 15)]
define Ends_DateTimeIntervalEndsStartsFalse: Interval[DateTime(2012, 1, 5), DateTime(2012, 2, 16)] ends day of Interval[DateTime(2012, 2, 1), DateTime(2012, 2, 16)]

define test_Ends_TestEndsNull: TestMessage(Ends_TestEndsNull is null, 'Ends_TestEndsNull', 'null', toString(Ends_TestEndsNull))
define test_Ends_IntegerIntervalEndsTrue: TestMessage(Ends_IntegerIntervalEndsTrue, 'Ends_IntegerIntervalEndsTrue', toString(true), toString(Ends_IntegerIntervalEndsTrue))
define test_Ends_IntegerIntervalEndsFalse: TestMessage(not Ends_IntegerIntervalEndsFalse, 'Ends_IntegerIntervalEndsFalse', toString(false), toString(Ends_IntegerIntervalEndsFalse))
define test_Ends_IntegerIntervalEndsStartsFalse: TestMessage(not Ends_IntegerIntervalEndsStartsFalse, 'Ends_IntegerIntervalEndsStartsFalse', toString(false), toString(Ends_IntegerIntervalEndsStartsFalse))
define test_Ends_DecimalIntervalEndsTrue: TestMessage(Ends_DecimalIntervalEndsTrue, 'Ends_DecimalIntervalEndsTrue', toString(true), toString(Ends_DecimalIntervalEndsTrue))
define test_Ends_DecimalIntervalEndsFalse: TestMessage(not Ends_DecimalIntervalEndsFalse, 'Ends_DecimalIntervalEndsFalse', toString(false), toString(Ends_DecimalIntervalEndsFalse))
define test_Ends_DecimalIntervalEndsStartsFalse: TestMessage(not Ends_DecimalIntervalEndsStartsFalse, 'Ends_DecimalIntervalEndsStartsFalse', toString(false), toString(Ends_DecimalIntervalEndsStartsFalse))
define test_Ends_QuantityIntervalEndsTrue: TestMessage(Ends_QuantityIntervalEndsTrue, 'Ends_QuantityIntervalEndsTrue', toString(true), toString(Ends_QuantityIntervalEndsTrue))
define test_Ends_QuantityIntervalEndsFalse: TestMessage(not Ends_QuantityIntervalEndsFalse, 'Ends_QuantityIntervalEndsFalse', toString(false), toString(Ends_QuantityIntervalEndsFalse))
define test_Ends_QuantityIntervalEndsStartsFalse: TestMessage(not Ends_QuantityIntervalEndsStartsFalse, 'Ends_QuantityIntervalEndsStartsFalse', toString(false), toString(Ends_QuantityIntervalEndsStartsFalse))
define test_Ends_DateTimeIntervalEndsTrue: TestMessage(Ends_DateTimeIntervalEndsTrue, 'Ends_DateTimeIntervalEndsTrue', toString(true), toString(Ends_DateTimeIntervalEndsTrue))
define test_Ends_DateTimeIntervalEndsFalse: TestMessage(not Ends_DateTimeIntervalEndsFalse, 'Ends_DateTimeIntervalEndsFalse', toString(false), toString(Ends_DateTimeIntervalEndsFalse))
define test_Ends_DateTimeIntervalEndsDayOfTrue: TestMessage(Ends_DateTimeIntervalEndsDayOfTrue, 'Ends_DateTimeIntervalEndsDayOfTrue', toString(true), toString(Ends_DateTimeIntervalEndsDayOfTrue))
define test_Ends_DateTimeIntervalEndsStartsFalse: TestMessage(not Ends_DateTimeIntervalEndsStartsFalse, 'Ends_DateTimeIntervalEndsStartsFalse', toString(false), toString(Ends_DateTimeIntervalEndsStartsFalse))

// IntegerIntervalUnion
define IntFullInterval: Interval[0,10]
define IntIvlUnion_IntClosedUnionClosed: Interval[0,5] union Interval[5,10]
define IntIvlUnion_IntClosedUnionOpen: Interval[0,5] union Interval[5,10)
define IntIvlUnion_IntOpenUnionOpen: Interval(0,5] union Interval[5,10)
define IntIvlUnion_IntOpenUnionClosed: Interval(0,5] union Interval[5,10]
define IntIvlUnion_IntSameAsUnion: Interval[0,10] union Interval[0,10]
define IntIvlUnion_IntBeforeUnion: Interval[0,4] union Interval[6,10]
define IntIvlUnion_IntMeetsUnion: Interval[0,5] union Interval[5,10]
define IntIvlUnion_IntOverlapsUnion: Interval[0,7] union Interval[3,10]
define IntIvlUnion_IntBeginsUnion: Interval[0,5] union Interval[0,10]
define IntIvlUnion_IntDuringUnion: Interval[3,5] union Interval[0,10]
define IntIvlUnion_IntEndsUnion: Interval[5,10] union Interval[0,10]

define test_IntIvlUnion_IntClosedUnionClosed: TestMessage(IntIvlUnion_IntClosedUnionClosed = IntFullInterval, 'IntIvlUnion_IntClosedUnionClosed', toString(IntFullInterval), toString(IntIvlUnion_IntClosedUnionClosed))
define test_IntIvlUnion_IntClosedUnionOpen: TestMessage(IntIvlUnion_IntClosedUnionOpen = Interval[0, 9], 'IntIvlUnion_IntClosedUnionOpen', toString(Interval[0, 9]), toString(IntIvlUnion_IntClosedUnionOpen))
define test_IntIvlUnion_IntOpenUnionOpen: TestMessage(IntIvlUnion_IntOpenUnionOpen = Interval[1, 9], 'IntIvlUnion_IntOpenUnionOpen', toString(Interval[1, 9]), toString(IntIvlUnion_IntOpenUnionOpen))
define test_IntIvlUnion_IntOpenUnionClosed: TestMessage(IntIvlUnion_IntOpenUnionClosed = Interval[1, 10], 'IntIvlUnion_IntOpenUnionClosed', toString(Interval[1, 10]), toString(IntIvlUnion_IntOpenUnionClosed))
define test_IntIvlUnion_IntSameAsUnion: TestMessage(IntIvlUnion_IntSameAsUnion = IntFullInterval, 'IntIvlUnion_IntSameAsUnion', toString(IntFullInterval), toString(IntIvlUnion_IntSameAsUnion))
define test_IntIvlUnion_IntBeforeUnion: TestMessage(IntIvlUnion_IntBeforeUnion is null, 'IntIvlUnion_IntBeforeUnion', 'null', toString(IntIvlUnion_IntBeforeUnion))
define test_IntIvlUnion_IntMeetsUnion: TestMessage(IntIvlUnion_IntMeetsUnion = IntFullInterval, 'IntIvlUnion_IntMeetsUnion', toString(IntFullInterval), toString(IntIvlUnion_IntMeetsUnion))
define test_IntIvlUnion_IntOverlapsUnion: TestMessage(IntIvlUnion_IntOverlapsUnion = IntFullInterval, 'IntIvlUnion_IntOverlapsUnion', toString(IntFullInterval), toString(IntIvlUnion_IntOverlapsUnion))
define test_IntIvlUnion_IntBeginsUnion: TestMessage(IntIvlUnion_IntBeginsUnion = IntFullInterval, 'IntIvlUnion_IntBeginsUnion', toString(IntFullInterval), toString(IntIvlUnion_IntBeginsUnion))
define test_IntIvlUnion_IntDuringUnion: TestMessage(IntIvlUnion_IntDuringUnion = IntFullInterval, 'IntIvlUnion_IntDuringUnion', toString(IntFullInterval), toString(IntIvlUnion_IntDuringUnion))
define test_IntIvlUnion_IntEndsUnion: TestMessage(IntIvlUnion_IntEndsUnion = IntFullInterval, 'IntIvlUnion_IntEndsUnion', toString(IntFullInterval), toString(IntIvlUnion_IntEndsUnion))

// DateTimeIntervalUnion
define DateTimeFullInterval: Interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2013, 1, 1, 0, 0, 0, 0)]
define DtIvlUnion_DateTimeClosedUnionClosed: Interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2012, 6, 1, 0, 0, 0, 0)] union Interval[DateTime(2012, 6, 1, 0, 0, 0, 0), DateTime(2013, 1, 1, 0, 0, 0, 0)]
define DtIvlUnion_DateTimeClosedUnionOpen: Interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2012, 6, 1, 0, 0, 0, 0)] union Interval[DateTime(2012, 6, 1, 0, 0, 0, 0), DateTime(2013, 1, 1, 0, 0, 0, 0))
define DtIvlUnion_DateTimeOpenUnionOpen: Interval(DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2012, 6, 1, 0, 0, 0, 0)] union Interval[DateTime(2012, 6, 1, 0, 0, 0, 0), DateTime(2013, 1, 1, 0, 0, 0, 0))
define DtIvlUnion_DateTimeOpenUnionClosed: Interval(DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2012, 6, 1, 0, 0, 0, 0)] union Interval[DateTime(2012, 6, 1, 0, 0, 0, 0), DateTime(2013, 1, 1, 0, 0, 0, 0)]
define DtIvlUnion_DateTimeSameAsUnion: Interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2013, 1, 1, 0, 0, 0, 0)] union Interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2013, 1, 1, 0, 0, 0, 0)]
define DtIvlUnion_DateTimeBeforeUnion: Interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2012, 4, 1, 0, 0, 0, 0)] union Interval[DateTime(2012, 7, 1, 0, 0, 0, 0), DateTime(2013, 1, 1, 0, 0, 0, 0)]
define DtIvlUnion_DateTimeMeetsUnion: Interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2012, 6, 1, 0, 0, 0, 0)] union Interval[DateTime(2012, 6, 1, 0, 0, 0, 0), DateTime(2013, 1, 1, 0, 0, 0, 0)]
define DtIvlUnion_DateTimeOverlapsUnion: Interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2012, 7, 1, 0, 0, 0, 0)] union Interval[DateTime(2012, 3, 1, 0, 0, 0, 0), DateTime(2013, 1, 1, 0, 0, 0, 0)]
define DtIvlUnion_DateTimeBeginsUnion: Interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2012, 6, 1, 0, 0, 0, 0)] union Interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2013, 1, 1, 0, 0, 0, 0)]
define DtIvlUnion_DateTimeDuringUnion: Interval[DateTime(2012, 3, 1, 0, 0, 0, 0), DateTime(2012, 5, 1, 0, 0, 0, 0)] union Interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2013, 1, 1, 0, 0, 0, 0)]
define DtIvlUnion_DateTimeEndsUnion: Interval[DateTime(2012, 6, 1, 0, 0, 0, 0), DateTime(2013, 1, 1, 0, 0, 0, 0)] union Interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2013, 1, 1, 0, 0, 0, 0)]

define test_DtIvlUnion_DateTimeClosedUnionClosed: TestMessage(DtIvlUnion_DateTimeClosedUnionClosed = DateTimeFullInterval, 'DtIvlUnion_DateTimeClosedUnionClosed', toString(DateTimeFullInterval), toString(DtIvlUnion_DateTimeClosedUnionClosed))
define test_DtIvlUnion_DateTimeClosedUnionOpen: TestMessage(DtIvlUnion_DateTimeClosedUnionOpen = Interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2012, 12, 31, 23, 59, 59, 999)], 'DtIvlUnion_DateTimeClosedUnionOpen', toString(Interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2012, 12, 31, 23, 59, 59, 999)]), toString(DtIvlUnion_DateTimeClosedUnionOpen))
define test_DtIvlUnion_DateTimeOpenUnionOpen: TestMessage(DtIvlUnion_DateTimeOpenUnionOpen = Interval[DateTime(2012, 1, 1, 0, 0, 0, 1), DateTime(2012, 12, 31, 23, 59, 59, 999)], 'DtIvlUnion_DateTimeOpenUnionOpen', toString(Interval[DateTime(2012, 1, 1, 0, 0, 0, 1), DateTime(2012, 12, 31, 23, 59, 59, 999)]), toString(DtIvlUnion_DateTimeOpenUnionOpen))
define test_DtIvlUnion_DateTimeOpenUnionClosed: TestMessage(DtIvlUnion_DateTimeOpenUnionClosed = Interval[DateTime(2012, 1, 1, 0, 0, 0, 1), DateTime(2013, 1, 1, 0, 0, 0, 0)], 'DtIvlUnion_DateTimeOpenUnionClosed', toString(Interval[DateTime(2012, 1, 1, 0, 0, 0, 1), DateTime(2013, 1, 1, 0, 0, 0, 0)]), toString(DtIvlUnion_DateTimeOpenUnionClosed))
define test_DtIvlUnion_DateTimeSameAsUnion: TestMessage(DtIvlUnion_DateTimeSameAsUnion = DateTimeFullInterval, 'DtIvlUnion_DateTimeSameAsUnion', toString(DateTimeFullInterval), toString(DtIvlUnion_DateTimeSameAsUnion))
define test_DtIvlUnion_DateTimeBeforeUnion: TestMessage(DtIvlUnion_DateTimeBeforeUnion is null, 'DtIvlUnion_DateTimeBeforeUnion', 'null', toString(DtIvlUnion_DateTimeBeforeUnion))
define test_DtIvlUnion_DateTimeMeetsUnion: TestMessage(DtIvlUnion_DateTimeMeetsUnion = DateTimeFullInterval, 'DtIvlUnion_DateTimeMeetsUnion', toString(DateTimeFullInterval), toString(DtIvlUnion_DateTimeMeetsUnion))
define test_DtIvlUnion_DateTimeOverlapsUnion: TestMessage(DtIvlUnion_DateTimeOverlapsUnion = DateTimeFullInterval, 'DtIvlUnion_DateTimeOverlapsUnion', toString(DateTimeFullInterval), toString(DtIvlUnion_DateTimeOverlapsUnion))
define test_DtIvlUnion_DateTimeBeginsUnion: TestMessage(DtIvlUnion_DateTimeBeginsUnion = DateTimeFullInterval, 'DtIvlUnion_DateTimeBeginsUnion', toString(DateTimeFullInterval), toString(DtIvlUnion_DateTimeBeginsUnion))
define test_DtIvlUnion_DateTimeDuringUnion: TestMessage(DtIvlUnion_DateTimeDuringUnion = DateTimeFullInterval, 'DtIvlUnion_DateTimeDuringUnion', toString(DateTimeFullInterval), toString(DtIvlUnion_DateTimeDuringUnion))
define test_DtIvlUnion_DateTimeEndsUnion: TestMessage(DtIvlUnion_DateTimeEndsUnion = DateTimeFullInterval, 'DtIvlUnion_DateTimeEndsUnion', toString(DateTimeFullInterval), toString(DtIvlUnion_DateTimeEndsUnion))

// IntegerIntervalExcept
define IntHalfInterval: Interval[0,5)
define IntIvlExcept_IntSameAsExcept: Interval[0,10] except Interval[0,10]
define IntIvlExcept_IntBeforeExcept: Interval[0,4] except Interval[6,10]
define IntIvlExcept_IntMeetsExcept: Interval[0,5] except Interval[5,10]
define IntIvlExcept_IntOverlapsExcept: Interval[0,7] except Interval[5,10]
define IntIvlExcept_IntBeginsExcept: Interval[0,5] except Interval[0,10]
define IntIvlExcept_IntDuringExcept: Interval[3,5] except Interval[0,10]
define IntIvlExcept_IntEndsExcept: Interval[5,10] except Interval[0,10]

define test_IntIvlExcept_IntSameAsExcept: TestMessage(IntIvlExcept_IntSameAsExcept is null, 'IntIvlExcept_IntSameAsExcept', 'null', toString(IntIvlExcept_IntSameAsExcept))
define test_IntIvlExcept_IntBeforeExcept: TestMessage(IntIvlExcept_IntBeforeExcept = Interval[0, 4], 'IntIvlExcept_IntBeforeExcept', toString(Interval[0, 4]), toString(IntIvlExcept_IntBeforeExcept))
define test_IntIvlExcept_IntMeetsExcept: TestMessage(IntIvlExcept_IntMeetsExcept = IntHalfInterval, 'IntIvlExcept_IntMeetsExcept', toString(IntHalfInterval), toString(IntIvlExcept_IntMeetsExcept))
define test_IntIvlExcept_IntOverlapsExcept: TestMessage(IntIvlExcept_IntOverlapsExcept = IntHalfInterval, 'IntIvlExcept_IntOverlapsExcept', toString(IntHalfInterval), toString(IntIvlExcept_IntOverlapsExcept))
define test_IntIvlExcept_IntBeginsExcept: TestMessage(IntIvlExcept_IntBeginsExcept is null, 'IntIvlExcept_IntBeginsExcept', 'null', toString(IntIvlExcept_IntBeginsExcept))
define test_IntIvlExcept_IntDuringExcept: TestMessage(IntIvlExcept_IntDuringExcept is null, 'IntIvlExcept_IntDuringExcept', 'null', toString(IntIvlExcept_IntDuringExcept))
define test_IntIvlExcept_IntEndsExcept: TestMessage(IntIvlExcept_IntEndsExcept is null, 'IntIvlExcept_IntEndsExcept', 'null', toString(IntIvlExcept_IntEndsExcept))

// DateTimeIntervalExcept
define DateTimeHalfInterval: Interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2012, 6, 1, 0, 0, 0, 0))
define DtIvlExcept_DateTimeSameAsExcept: Interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2013, 1, 1, 0, 0, 0, 0)] except Interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2013, 1, 1, 0, 0, 0, 0)]
define DtIvlExcept_DateTimeBeforeExcept: Interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2012, 4, 1, 0, 0, 0, 0)] except Interval[DateTime(2012, 7, 1, 0, 0, 0, 0), DateTime(2013, 1, 1, 0, 0, 0, 0)]
define DtIvlExcept_DateTimeMeetsExcept: Interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2012, 6, 1, 0, 0, 0, 0)] except Interval[DateTime(2012, 6, 1, 0, 0, 0, 0), DateTime(2013, 1, 1, 0, 0, 0, 0)]
define DtIvlExcept_DateTimeOverlapsExcept: Interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2012, 7, 1, 0, 0, 0, 0)] except Interval[DateTime(2012, 6, 1, 0, 0, 0, 0), DateTime(2013, 1, 1, 0, 0, 0, 0)]
define DtIvlExcept_DateTimeBeginsExcept: Interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2012, 6, 1, 0, 0, 0, 0)] except Interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2013, 1, 1, 0, 0, 0, 0)]
define DtIvlExcept_DateTimeDuringExcept: Interval[DateTime(2012, 3, 1, 0, 0, 0, 0), DateTime(2012, 5, 1, 0, 0, 0, 0)] except Interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2013, 1, 1, 0, 0, 0, 0)]
define DtIvlExcept_DateTimeEndsExcept: Interval[DateTime(2012, 6, 1, 0, 0, 0, 0), DateTime(2013, 1, 1, 0, 0, 0, 0)] except Interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2013, 1, 1, 0, 0, 0, 0)]

define test_DtIvlExcept_DateTimeSameAsExcept: TestMessage(DtIvlExcept_DateTimeSameAsExcept is null, 'DtIvlExcept_DateTimeSameAsExcept', 'null', toString(DtIvlExcept_DateTimeSameAsExcept))
define test_DtIvlExcept_DateTimeBeforeExcept: TestMessage(DtIvlExcept_DateTimeBeforeExcept = Interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2012, 4, 1, 0, 0, 0, 0)], 'DtIvlExcept_DateTimeBeforeExcept', toString(Interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2012, 4, 1, 0, 0, 0, 0)]), toString(DtIvlExcept_DateTimeBeforeExcept))
define test_DtIvlExcept_DateTimeMeetsExcept: TestMessage(DtIvlExcept_DateTimeMeetsExcept = DateTimeHalfInterval, 'DtIvlExcept_DateTimeMeetsExcept', toString(DateTimeHalfInterval), toString(DtIvlExcept_DateTimeMeetsExcept))
define test_DtIvlExcept_DateTimeOverlapsExcept: TestMessage(DtIvlExcept_DateTimeOverlapsExcept = DateTimeHalfInterval, 'DtIvlExcept_DateTimeOverlapsExcept', toString(DateTimeHalfInterval), toString(DtIvlExcept_DateTimeOverlapsExcept))
define test_DtIvlExcept_DateTimeBeginsExcept: TestMessage(DtIvlExcept_DateTimeBeginsExcept is null, 'DtIvlExcept_DateTimeBeginsExcept', 'null', toString(DtIvlExcept_DateTimeBeginsExcept))
define test_DtIvlExcept_DateTimeDuringExcept: TestMessage(DtIvlExcept_DateTimeDuringExcept is null, 'DtIvlExcept_DateTimeDuringExcept', 'null', toString(DtIvlExcept_DateTimeDuringExcept))
define test_DtIvlExcept_DateTimeEndsExcept: TestMessage(DtIvlExcept_DateTimeEndsExcept is null, 'DtIvlExcept_DateTimeEndsExcept', 'null', toString(DtIvlExcept_DateTimeEndsExcept))

// IntegerIntervalIntersect
define IntMeetsInterval: Interval[5,5]
define IntOverlapsInterval: Interval[5,7]
define IntBeginsInterval: Interval[0,5]
define IntDuringInterval: Interval[3,5]
define IntEndsInterval: Interval[5,10]
define IntIvlIntersect_IntSameAsIntersect: Interval[0,10] intersect Interval[0,10]
define IntIvlIntersect_IntBeforeIntersect: Interval[0,4] intersect Interval[6,10]
define IntIvlIntersect_IntMeetsIntersect: Interval[0,5] intersect Interval[5,10]
define IntIvlIntersect_IntOverlapsIntersect: Interval[0,7] intersect Interval[5,10]
define IntIvlIntersect_IntBeginsIntersect: Interval[0,5] intersect Interval[0,10]
define IntIvlIntersect_IntDuringIntersect: Interval[3,5] intersect Interval[0,10]
define IntIvlIntersect_IntEndsIntersect: Interval[5,10] intersect Interval[0,10]

define test_IntIvlIntersect_IntSameAsIntersect: TestMessage(IntIvlIntersect_IntSameAsIntersect = IntFullInterval, 'IntIvlIntersect_IntSameAsIntersect', toString(IntFullInterval), toString(IntIvlIntersect_IntSameAsIntersect))
define test_IntIvlIntersect_IntBeforeIntersect: TestMessage(IntIvlIntersect_IntBeforeIntersect is null, 'IntIvlIntersect_IntBeforeIntersect', 'null', toString(IntIvlIntersect_IntBeforeIntersect))
define test_IntIvlIntersect_IntMeetsIntersect: TestMessage(IntIvlIntersect_IntMeetsIntersect = IntMeetsInterval, 'IntIvlIntersect_IntMeetsIntersect', toString(IntMeetsInterval), toString(IntIvlIntersect_IntMeetsIntersect))
define test_IntIvlIntersect_IntOverlapsIntersect: TestMessage(IntIvlIntersect_IntOverlapsIntersect = IntOverlapsInterval, 'IntIvlIntersect_IntOverlapsIntersect', toString(IntOverlapsInterval), toString(IntIvlIntersect_IntOverlapsIntersect))
define test_IntIvlIntersect_IntBeginsIntersect: TestMessage(IntIvlIntersect_IntBeginsIntersect = IntBeginsInterval, 'IntIvlIntersect_IntBeginsIntersect', toString(IntBeginsInterval), toString(IntIvlIntersect_IntBeginsIntersect))
define test_IntIvlIntersect_IntDuringIntersect: TestMessage(IntIvlIntersect_IntDuringIntersect = IntDuringInterval, 'IntIvlIntersect_IntDuringIntersect', toString(IntDuringInterval), toString(IntIvlIntersect_IntDuringIntersect))
define test_IntIvlIntersect_IntEndsIntersect: TestMessage(IntIvlIntersect_IntEndsIntersect = IntEndsInterval, 'IntIvlIntersect_IntEndsIntersect', toString(IntEndsInterval), toString(IntIvlIntersect_IntEndsIntersect))

// DateTimeIntervalIntersect
define DateTimeMeetsInterval: Interval[DateTime(2012, 6, 1, 0, 0, 0, 0), DateTime(2012, 6, 1, 0, 0, 0, 0)]
define DateTimeOverlapsInterval: Interval[DateTime(2012, 6, 1, 0, 0, 0, 0), DateTime(2012, 7, 1, 0, 0, 0, 0)]
define DateTimeBeginsInterval: Interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2012, 6, 1, 0, 0, 0, 0)]
define DateTimeDuringInterval: Interval[DateTime(2012, 3, 1, 0, 0, 0, 0), DateTime(2012, 5, 1, 0, 0, 0, 0)]
define DateTimeEndsInterval: Interval[DateTime(2012, 6, 1, 0, 0, 0, 0), DateTime(2013, 1, 1, 0, 0, 0, 0)]
define DateTimeDifferentPrecisionInterval: Interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2012, 12, 31, 23, 59, 59, 999)]
define DtIvlIntersect_DateTimeSameAsIntersect: Interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2013, 1, 1, 0, 0, 0, 0)] intersect Interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2013, 1, 1, 0, 0, 0, 0)]
define DtIvlIntersect_DateTimeBeforeIntersect: Interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2012, 4, 1, 0, 0, 0, 0)] intersect Interval[DateTime(2012, 7, 1, 0, 0, 0, 0), DateTime(2013, 1, 1, 0, 0, 0, 0)]
define DtIvlIntersect_DateTimeMeetsIntersect: Interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2012, 6, 1, 0, 0, 0, 0)] intersect Interval[DateTime(2012, 6, 1, 0, 0, 0, 0), DateTime(2013, 1, 1, 0, 0, 0, 0)]
define DtIvlIntersect_DateTimeOverlapsIntersect: Interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2012, 7, 1, 0, 0, 0, 0)] intersect Interval[DateTime(2012, 6, 1, 0, 0, 0, 0), DateTime(2013, 1, 1, 0, 0, 0, 0)]
define DtIvlIntersect_DateTimeBeginsIntersect: Interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2012, 6, 1, 0, 0, 0, 0)] intersect Interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2013, 1, 1, 0, 0, 0, 0)]
define DtIvlIntersect_DateTimeDuringIntersect: Interval[DateTime(2012, 3, 1, 0, 0, 0, 0), DateTime(2012, 5, 1, 0, 0, 0, 0)] intersect Interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2013, 1, 1, 0, 0, 0, 0)]
define DtIvlIntersect_DateTimeEndsIntersect: Interval[DateTime(2012, 6, 1, 0, 0, 0, 0), DateTime(2013, 1, 1, 0, 0, 0, 0)] intersect Interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2013, 1, 1, 0, 0, 0, 0)]
define DtIvlIntersect_DateTimeDifferentPrecisionIntersect: Interval[DateTime(2012, 1, 1), DateTime(2012, 12, 31)] intersect Interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2012, 12, 31, 23, 59, 59, 999)]

define test_DtIvlIntersect_DateTimeSameAsIntersect: TestMessage(DtIvlIntersect_DateTimeSameAsIntersect = DateTimeFullInterval, 'DtIvlIntersect_DateTimeSameAsIntersect', toString(DateTimeFullInterval), toString(DtIvlIntersect_DateTimeSameAsIntersect))
define test_DtIvlIntersect_DateTimeBeforeIntersect: TestMessage(DtIvlIntersect_DateTimeBeforeIntersect is null, 'DtIvlIntersect_DateTimeBeforeIntersect', 'null', toString(DtIvlIntersect_DateTimeBeforeIntersect))
define test_DtIvlIntersect_DateTimeMeetsIntersect: TestMessage(DtIvlIntersect_DateTimeMeetsIntersect = DateTimeMeetsInterval, 'DtIvlIntersect_DateTimeMeetsIntersect', toString(DateTimeMeetsInterval), toString(DtIvlIntersect_DateTimeMeetsIntersect))
define test_DtIvlIntersect_DateTimeOverlapsIntersect: TestMessage(DtIvlIntersect_DateTimeOverlapsIntersect = DateTimeOverlapsInterval, 'DtIvlIntersect_DateTimeOverlapsIntersect', toString(DateTimeOverlapsInterval), toString(DtIvlIntersect_DateTimeOverlapsIntersect))
define test_DtIvlIntersect_DateTimeBeginsIntersect: TestMessage(DtIvlIntersect_DateTimeBeginsIntersect = DateTimeBeginsInterval, 'DtIvlIntersect_DateTimeBeginsIntersect', toString(DateTimeBeginsInterval), toString(DtIvlIntersect_DateTimeBeginsIntersect))
define test_DtIvlIntersect_DateTimeDuringIntersect: TestMessage(DtIvlIntersect_DateTimeDuringIntersect = DateTimeDuringInterval, 'DtIvlIntersect_DateTimeDuringIntersect', toString(DateTimeDuringInterval), toString(DtIvlIntersect_DateTimeDuringIntersect))
define test_DtIvlIntersect_DateTimeEndsIntersect: TestMessage(DtIvlIntersect_DateTimeEndsIntersect = DateTimeEndsInterval, 'DtIvlIntersect_DateTimeEndsIntersect', toString(DateTimeEndsInterval), toString(DtIvlIntersect_DateTimeEndsIntersect))
define test_DtIvlIntersect_DateTimeDifferentPrecisionIntersect: TestMessage(DtIvlIntersect_DateTimeDifferentPrecisionIntersect = DateTimeDifferentPrecisionInterval, 'DtIvlIntersect_DateTimeDifferentPrecisionIntersect', toString(DateTimeEndsInterval), toString(DtIvlIntersect_DateTimeDifferentPrecisionIntersect))

// IntegerIntervalCollapse
define IntEmptyIntervalList: List<Interval<Integer>>{}
define Int1_6Interval: Interval[1, 6]
define Int1_10Interval: Interval[1, 10]
define Int5_10Interval: Interval[5, 10]
define Int5_12Interval: Interval[5, 12]
define Int10_15Interval: Interval[10, 15]
define Int15_20Interval: Interval[15, 20]
define Int1_12Interval: Interval[1, 12]
define Int1_15Interval: Interval[1, 15]
define Int1_10IntervalList: { Int1_10Interval }
define Int1_12IntervalList: { Int1_12Interval }
define Int1_15IntervalList: { Int1_15Interval }
define IntTwoItemDisjointList: { Int1_10Interval, Int15_20Interval }
define IntIvlCollapse_IntCollapseEmpty: collapse IntEmptyIntervalList
define IntIvlCollapse_IntCollapseSingleInterval: collapse Int1_10IntervalList
define IntIvlCollapse_IntCollapseDisjoint: collapse IntTwoItemDisjointList
define IntIvlCollapse_IntCollapseDisjointReversed: collapse { Int15_20Interval, Int1_10Interval }
define IntIvlCollapse_IntCollapseAdjacent: collapse { Int1_10Interval, Int10_15Interval }
define IntIvlCollapse_IntCollapseOverlap: collapse { Int1_10Interval, Int5_12Interval }
define IntIvlCollapse_IntCollapseOverlapContained: collapse { Int1_15Interval, Int5_12Interval }
define IntIvlCollapse_IntCollapseOverlapContainedEdge: collapse { Int1_10Interval, Int5_10Interval }
define IntIvlCollapse_IntCollapseOverlapContainedEdge2: collapse { Int1_15Interval, Int1_10Interval }
define IntIvlCollapse_IntCollapseOverlapMultipleCombine: collapse { Int1_6Interval, Int5_12Interval, Int10_15Interval }

define test_IntIvlCollapse_IntCollapseEmpty: TestMessage(not exists IntIvlCollapse_IntCollapseEmpty, 'IntIvlCollapse_IntCollapseEmpty', toString(IntEmptyIntervalList), toString(IntIvlCollapse_IntCollapseEmpty))
define test_IntIvlCollapse_IntCollapseSingleInterval: TestMessage(IntIvlCollapse_IntCollapseSingleInterval = Int1_10IntervalList, 'IntIvlCollapse_IntCollapseSingleInterval', toString(Int1_10IntervalList), toString(IntIvlCollapse_IntCollapseSingleInterval))
define test_IntIvlCollapse_IntCollapseDisjoint: TestMessage(IntIvlCollapse_IntCollapseDisjoint = IntTwoItemDisjointList, 'IntIvlCollapse_IntCollapseDisjoint', toString(IntTwoItemDisjointList), toString(IntIvlCollapse_IntCollapseDisjoint))
define test_IntIvlCollapse_IntCollapseDisjointReversed: TestMessage(IntIvlCollapse_IntCollapseDisjointReversed = IntTwoItemDisjointList, 'IntIvlCollapse_IntCollapseDisjointReversed', toString(IntTwoItemDisjointList), toString(IntIvlCollapse_IntCollapseDisjointReversed))
define test_IntIvlCollapse_IntCollapseAdjacent: TestMessage(IntIvlCollapse_IntCollapseAdjacent = Int1_15IntervalList, 'IntIvlCollapse_IntCollapseAdjacent', toString(Int1_15IntervalList), toString(IntIvlCollapse_IntCollapseAdjacent))
define test_IntIvlCollapse_IntCollapseOverlap: TestMessage(IntIvlCollapse_IntCollapseOverlap = Int1_12IntervalList, 'IntIvlCollapse_IntCollapseOverlap', toString(Int1_12IntervalList), toString(IntIvlCollapse_IntCollapseOverlap))
define test_IntIvlCollapse_IntCollapseOverlapContained: TestMessage(IntIvlCollapse_IntCollapseOverlapContained = Int1_15IntervalList, 'IntIvlCollapse_IntCollapseOverlapContained', toString(Int1_15IntervalList), toString(IntIvlCollapse_IntCollapseOverlapContained))
define test_IntIvlCollapse_IntCollapseOverlapContainedEdge: TestMessage(IntIvlCollapse_IntCollapseOverlapContainedEdge = Int1_10IntervalList, 'IntIvlCollapse_IntCollapseOverlapContainedEdge', toString(Int1_10IntervalList), toString(IntIvlCollapse_IntCollapseOverlapContainedEdge))
define test_IntIvlCollapse_IntCollapseOverlapContainedEdge2: TestMessage(IntIvlCollapse_IntCollapseOverlapContainedEdge2 = Int1_15IntervalList, 'IntIvlCollapse_IntCollapseOverlapContainedEdge2', toString(Int1_15IntervalList), toString(IntIvlCollapse_IntCollapseOverlapContainedEdge2))
define test_IntIvlCollapse_IntCollapseOverlapMultipleCombine: TestMessage(IntIvlCollapse_IntCollapseOverlapMultipleCombine = Int1_15IntervalList, 'IntIvlCollapse_IntCollapseOverlapMultipleCombine', toString(Int1_15IntervalList), toString(IntIvlCollapse_IntCollapseOverlapMultipleCombine))

// DateTimeIntervalCollapse
define DateTimeEmptyIntervalList: List<Interval<DateTime>>{}
define DateTime1_6Interval: Interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2012, 1, 6, 0, 0, 0, 0)]
define DateTime1_10Interval: Interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2012, 1, 10, 0, 0, 0, 0)]
define DateTime5_10Interval: Interval[DateTime(2012, 1, 5, 0, 0, 0, 0), DateTime(2012, 1, 10, 0, 0, 0, 0)]
define DateTime5_12Interval: Interval[DateTime(2012, 1, 5, 0, 0, 0, 0), DateTime(2012, 1, 12, 0, 0, 0, 0)]
define DateTime10_15Interval: Interval[DateTime(2012, 1, 10, 0, 0, 0, 0), DateTime(2012, 1, 15, 0, 0, 0, 0)]
define DateTime15_20Interval: Interval[DateTime(2012, 1, 15, 0, 0, 0, 0), DateTime(2012, 1, 20, 0, 0, 0, 0)]
define DateTime1_12Interval: Interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2012, 1, 12, 0, 0, 0, 0)]
define DateTime1_15Interval: Interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2012, 1, 15, 0, 0, 0, 0)]
define DateTime1_3ImpreciseInterval: Interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2012, 1, 3)]
define DateTime1_10IntervalList: { DateTime1_10Interval }
define DateTime1_12IntervalList: { DateTime1_12Interval }
define DateTime1_15IntervalList: { DateTime1_15Interval }
define DateTimeTwoItemDisjointList: { DateTime1_10Interval, DateTime15_20Interval }
define DtIvlCollapse_DateTimeCollapseEmpty: collapse DateTimeEmptyIntervalList
define DtIvlCollapse_DateTimeCollapseSingleInterval: collapse DateTime1_10IntervalList
define DtIvlCollapse_DateTimeCollapseDisjoint: collapse DateTimeTwoItemDisjointList
define DtIvlCollapse_DateTimeCollapseDisjointReversed: collapse { DateTime15_20Interval, DateTime1_10Interval }
define DtIvlCollapse_DateTimeCollapseAdjacent: collapse { DateTime1_10Interval, DateTime10_15Interval }
define DtIvlCollapse_DateTimeCollapseOverlap: collapse { DateTime1_10Interval, DateTime5_12Interval }
define DtIvlCollapse_DateTimeCollapseOverlapContained: collapse { DateTime1_15Interval, DateTime5_12Interval }
define DtIvlCollapse_DateTimeCollapseOverlapContainedEdge: collapse { DateTime1_10Interval, DateTime5_10Interval }
define DtIvlCollapse_DateTimeCollapseOverlapContainedEdge2: collapse { DateTime1_15Interval, DateTime1_10Interval }
define DtIvlCollapse_DateTimeCollapseOverlapMultipleCombine: collapse { DateTime1_6Interval, DateTime5_12Interval, DateTime10_15Interval }
define DtIvlCollapse_DateTimeCollapseImpreciseBoundary: collapse { DateTime1_3ImpreciseInterval, DateTime1_10Interval }

define test_DtIvlCollapse_DateTimeCollapseEmpty: TestMessage(not exists DtIvlCollapse_DateTimeCollapseEmpty, 'DtIvlCollapse_DateTimeCollapseEmpty', toString(DateTimeEmptyIntervalList), toString(DtIvlCollapse_DateTimeCollapseEmpty))
define test_DtIvlCollapse_DateTimeCollapseSingleInterval: TestMessage(DtIvlCollapse_DateTimeCollapseSingleInterval = DateTime1_10IntervalList, 'DtIvlCollapse_DateTimeCollapseSingleInterval', toString(DateTime1_10IntervalList), toString(DtIvlCollapse_DateTimeCollapseSingleInterval))
define test_DtIvlCollapse_DateTimeCollapseDisjoint: TestMessage(DtIvlCollapse_DateTimeCollapseDisjoint = DateTimeTwoItemDisjointList, 'DtIvlCollapse_DateTimeCollapseDisjoint', toString(DateTimeTwoItemDisjointList), toString(DtIvlCollapse_DateTimeCollapseDisjoint))
define test_DtIvlCollapse_DateTimeCollapseDisjointReversed: TestMessage(DtIvlCollapse_DateTimeCollapseDisjointReversed = DateTimeTwoItemDisjointList, 'DtIvlCollapse_DateTimeCollapseDisjointReversed', toString(DateTimeTwoItemDisjointList), toString(DtIvlCollapse_DateTimeCollapseDisjointReversed))
define test_DtIvlCollapse_DateTimeCollapseAdjacent: TestMessage(DtIvlCollapse_DateTimeCollapseAdjacent = DateTime1_15IntervalList, 'DtIvlCollapse_DateTimeCollapseAdjacent', toString(DateTime1_15IntervalList), toString(DtIvlCollapse_DateTimeCollapseAdjacent))
define test_DtIvlCollapse_DateTimeCollapseOverlap: TestMessage(DtIvlCollapse_DateTimeCollapseOverlap = DateTime1_12IntervalList, 'DtIvlCollapse_DateTimeCollapseOverlap', toString(DateTime1_12IntervalList), toString(DtIvlCollapse_DateTimeCollapseOverlap))
define test_DtIvlCollapse_DateTimeCollapseOverlapContained: TestMessage(DtIvlCollapse_DateTimeCollapseOverlapContained = DateTime1_15IntervalList, 'DtIvlCollapse_DateTimeCollapseOverlapContained', toString(DateTime1_15IntervalList), toString(DtIvlCollapse_DateTimeCollapseOverlapContained))
define test_DtIvlCollapse_DateTimeCollapseOverlapContainedEdge: TestMessage(DtIvlCollapse_DateTimeCollapseOverlapContainedEdge = DateTime1_10IntervalList, 'DtIvlCollapse_DateTimeCollapseOverlapContainedEdge', toString(DateTime1_10IntervalList), toString(DtIvlCollapse_DateTimeCollapseOverlapContainedEdge))
define test_DtIvlCollapse_DateTimeCollapseOverlapContainedEdge2: TestMessage(DtIvlCollapse_DateTimeCollapseOverlapContainedEdge2 = DateTime1_15IntervalList, 'DtIvlCollapse_DateTimeCollapseOverlapContainedEdge2', toString(DateTime1_15IntervalList), toString(DtIvlCollapse_DateTimeCollapseOverlapContainedEdge2))
define test_DtIvlCollapse_DateTimeCollapseOverlapMultipleCombine: TestMessage(DtIvlCollapse_DateTimeCollapseOverlapMultipleCombine = DateTime1_15IntervalList, 'DtIvlCollapse_DateTimeCollapseOverlapMultipleCombine', toString(DateTime1_15IntervalList), toString(DtIvlCollapse_DateTimeCollapseOverlapMultipleCombine))
define test_DtIvlCollapse_DateTimeCollapseImpreciseBoundary: TestMessage(DtIvlCollapse_DateTimeCollapseImpreciseBoundary = DateTime1_10IntervalList, 'DtIvlCollapse_DateTimeCollapseImpreciseBoundary', toString(DateTime1_10IntervalList), toString(DtIvlCollapse_DateTimeCollapseImpreciseBoundary))

// Numeric Intervals
define IntIntervalList: { Interval[1, 4], Interval[4, 8], Interval[7, 9] }
define IntIntervalSeparatedList: { Interval[3, 5], Interval[8, 10] }
define ExpectedIntervalList: { Interval[3,10] }
define NumIvlCollapse_IntCollapsePerUnit1: collapse IntIntervalList per ToQuantity(1)
define NumIvlCollapse_IntCollapseNoPer: collapse IntIntervalList
define NumIvlCollapse_IntCollapseSeparatedListPer3: collapse IntIntervalSeparatedList per 3

define test_NumIvlCollapse_IntCollapsePerUnit1: TestMessage(NumIvlCollapse_IntCollapsePerUnit1 = { Interval[1, 9] }, 'NumIvlCollapse_IntCollapsePerUnit1', toString({ Interval[1, 9] }), toString(NumIvlCollapse_IntCollapsePerUnit1))
define test_NumIvlCollapse_IntCollapseNoPer: TestMessage(NumIvlCollapse_IntCollapseNoPer = { Interval[1, 9] }, 'NumIvlCollapse_IntCollapseNoPer', toString({ Interval[1, 9] }), toString(NumIvlCollapse_IntCollapseNoPer))
define test_NumIvlCollapse_IntCollapseSeparatedListPer3: TestMessage(NumIvlCollapse_IntCollapseSeparatedListPer3 = { Interval[3,10] }, 'NumIvlCollapse_IntCollapseSeparatedListPer3', toString({ Interval[3,10] }), toString(NumIvlCollapse_IntCollapseSeparatedListPer3))

// DateTime Intervals
define DateTime3_15Interval: Interval[DateTime(2012, 1, 3, 0, 0, 0, 0), DateTime(2012, 1, 15, 0, 0, 0, 0)]
define DtIvlCollapse_DateTimeCollapseNoPer: collapse { DateTime1_12Interval, DateTime3_15Interval}
define DtIvlCollapse_DateTimeCollapsePerMs: collapse { DateTime1_12Interval, DateTime3_15Interval} per millisecond
define DtIvlCollapse_DateTimeCollapsePerDay: collapse { DateTime1_12Interval, DateTime3_15Interval} per day
// NOTE: the following tests were added (Bonnie is missing datetime collapse with tests like NumIvlCollapse_IntCollapseSeparatedListPer3)
define DtIvlCollapse_DateTimeCollapseSeparatedPerDay: collapse { DateTime1_6Interval, DateTime10_15Interval} per 4 days
define DtIvlCollapse_DateTimeCollapseSeparatedPerHour: collapse { DateTime1_6Interval, DateTime10_15Interval} per 96 hours

define test_DtIvlCollapse_DateTimeCollapseNoPer: TestMessage(DtIvlCollapse_DateTimeCollapseNoPer = DateTime1_15IntervalList, 'DtIvlCollapse_DateTimeCollapseNoPer', toString(DateTime1_15IntervalList), toString(DtIvlCollapse_DateTimeCollapseNoPer))
define test_DtIvlCollapse_DateTimeCollapsePerMs: TestMessage(DtIvlCollapse_DateTimeCollapsePerMs = DateTime1_15IntervalList, 'DtIvlCollapse_DateTimeCollapsePerMs', toString(DateTime1_15IntervalList), toString(DtIvlCollapse_DateTimeCollapsePerMs))
define test_DtIvlCollapse_DateTimeCollapsePerDay: TestMessage(DtIvlCollapse_DateTimeCollapsePerDay = DateTime1_15IntervalList, 'DtIvlCollapse_DateTimeCollapsePerDay', toString(DateTime1_15IntervalList), toString(DtIvlCollapse_DateTimeCollapsePerDay))
define test_DtIvlCollapse_DateTimeCollapseSeparatedPerDay: TestMessage(DtIvlCollapse_DateTimeCollapseSeparatedPerDay = DateTime1_15IntervalList, 'DtIvlCollapse_DateTimeCollapseSeparatedPerDay', toString(DateTime1_15IntervalList), toString(DtIvlCollapse_DateTimeCollapseSeparatedPerDay))
define test_DtIvlCollapse_DateTimeCollapseSeparatedPerHour: TestMessage(DtIvlCollapse_DateTimeCollapseSeparatedPerHour = DateTime1_15IntervalList, 'DtIvlCollapse_DateTimeCollapseSeparatedPerHour', toString(DateTime1_15IntervalList), toString(DtIvlCollapse_DateTimeCollapseSeparatedPerHour))

// Quantity Intervals with default units
define QuantityIntervalList: { Interval[ToQuantity(1), ToQuantity(4)], Interval[ToQuantity(4), ToQuantity(8)] }
define ExpectedQuantityList: { Interval[ToQuantity(1), ToQuantity(8)] }
define QuantitySeparatedBy3: { Interval[ToQuantity(3), ToQuantity(5)], Interval[ToQuantity(8), ToQuantity(10)]}
define ExpectedSeparatedQuantity: { Interval[ToQuantity(3), ToQuantity(10)] }
define QtyIvlCollapse_QuantityIntervalCollapseNoPer: collapse QuantityIntervalList
define QtyIvlCollapse_QuantityIntervalCollapsePerUnit1: collapse QuantityIntervalList per ToQuantity(1)
define QtyIvlCollapse_CollapseSeparatedQuantity: collapse QuantitySeparatedBy3
define QtyIvlCollapse_CollapseSeparatedQuantityPer3: collapse QuantitySeparatedBy3 per 3

define test_QtyIvlCollapse_QuantityIntervalCollapseNoPer: TestMessage(QtyIvlCollapse_QuantityIntervalCollapseNoPer = ExpectedQuantityList, 'QtyIvlCollapse_QuantityIntervalCollapseNoPer', toString(ExpectedQuantityList), toString(QtyIvlCollapse_QuantityIntervalCollapseNoPer))
define test_QtyIvlCollapse_QuantityIntervalCollapsePerUnit1: TestMessage(QtyIvlCollapse_QuantityIntervalCollapsePerUnit1 = ExpectedQuantityList, 'QtyIvlCollapse_QuantityIntervalCollapsePerUnit1', toString(ExpectedQuantityList), toString(QtyIvlCollapse_QuantityIntervalCollapsePerUnit1))
define test_QtyIvlCollapse_CollapseSeparatedQuantity: TestMessage(QtyIvlCollapse_CollapseSeparatedQuantity = QuantitySeparatedBy3, 'QtyIvlCollapse_CollapseSeparatedQuantity', toString(QuantitySeparatedBy3), toString(QtyIvlCollapse_CollapseSeparatedQuantity))
define test_QtyIvlCollapse_CollapseSeparatedQuantityPer3: TestMessage(QtyIvlCollapse_CollapseSeparatedQuantityPer3 = ExpectedSeparatedQuantity, 'QtyIvlCollapse_CollapseSeparatedQuantityPer3', toString(ExpectedSeparatedQuantity), toString(QtyIvlCollapse_CollapseSeparatedQuantityPer3))

// Quantity Intervals with units TODO: when UCUM service is plugged in
define QuantityMeterIntervalList: { Interval[ToQuantity('1 \'m\''), ToQuantity('1.995 \'m\'')], Interval[ToQuantity('2 \'m\''), ToQuantity('3 \'m\'')] }
define QtyIvlCollapse_CollapseDisjointQuantityUnits: collapse QuantityMeterIntervalList
define QtyIvlCollapse_ExpectedQuantityUnitsCollapse: { Interval[ToQuantity('1 \'m\''), ToQuantity('3 \'m\'')] }
define QtyIvlCollapse_CollapseQuantityUnitsWithinPer: collapse QuantityMeterIntervalList per ToQuantity('1 \'cm\'')
define QtyIvlCollapse_CollapseQuantityUnitsNotWithinPer: collapse QuantityMeterIntervalList per ToQuantity('1 \'mm\'')

// Null test cases
define NullLowIntervalList: { Interval[null, 4], Interval[3, 5] }
define ExpectedNullLowIntervalCollapse: { Interval[null, 5] }
define NullHighIntervalList: { Interval[1, 4], Interval[3, null] }
define ExpectedNullHighIntervalCollapse: { Interval[1, null] }
define ExpectedResultWithNull: { Interval[1,3] }
define ExpectedResultNullPer: { Interval[1,7] }
define DateTime5_NullInterval: Interval[DateTime(2012, 1, 5, 0, 0, 0, 0), null]
define DateTimeNull_5Interval: Interval[null, DateTime(2012, 1, 5, 0, 0, 0, 0)]
define DateTime9_10Interval: Interval[DateTime(2012, 1, 9, 0, 0, 0, 0), DateTime(2012, 1, 10, 0, 0, 0, 0)]
define DateTime1_2Interval: Interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2012, 1, 2, 0, 0, 0, 0)]
define DateTimeNullStartCollapseExpected: { Interval[null, DateTime(2012, 1, 10, 0, 0, 0, 0)] }
define DateTimeNullEndCollapseNoOverlapExpected: { DateTime1_2Interval, DateTime5_NullInterval }
define DateTimeNullStartCollapseNoOverlapExpected: { DateTimeNull_5Interval, DateTime9_10Interval }
define DateTimeNullEndCollapseExpected: { Interval[DateTime(2012, 1, 1, 0, 0, 0, 0), null] }
// TODO: should be { Interval[(null as DateTime), (null as DateTime)] } but that currently evaluates to null in the engine
define DateTimeNullStartEndCollapseExpected: { Interval[null, DateTime(9999, 12, 31, 23, 59, 59, 999, 0.0)] }
define QuantityMeterNullLowIntervalList: { Interval[null, ToQuantity('1.995 \'m\'')], Interval[ToQuantity('2 \'m\''), ToQuantity('3 \'m\'')] }
define CollapseQuantityNullLowUnitsWithinPerExpected : { Interval[null, ToQuantity('3 \'m\'')] }
define QuantityMeterNullHighIntervalList: { Interval[ToQuantity('1 \'m\''),ToQuantity('1.995 \'m\'')], Interval[ToQuantity('2 \'m\''), null] }
define CollapseQuantityNullHighUnitsWithinPerExpected : { Interval[ToQuantity('1 \'m\''), null] }
define QuantityIntervalListWithNulls: { Interval[ToQuantity(4), ToQuantity(8)], Interval[null, ToQuantity(2)], Interval[ToQuantity(1), ToQuantity(4)], Interval[ToQuantity(7), null] }
// TODO: should be { Interval[(null as Quantity), (null as Quantity)] } but that currently evaluates to null in the engine
define CollapseQuantityIntervalListWithNullsExpected: { Interval[null, 99999999999999999999.99999999 '1'] }
define QuantityIntervalListWithNullLowNoOverlap: { Interval[ToQuantity(4), ToQuantity(8)], Interval[null, ToQuantity(2)] }
define CollapseQuantityIntervalListWithNullLowNoOverlapExpected: { Interval[null, ToQuantity(2)], Interval[ToQuantity(4), ToQuantity(8)]}
define QuantityIntervalListWithNullHighNoOverlap: { Interval[ToQuantity(4), null], Interval[ToQuantity(1), ToQuantity(2)] }
define CollapseQuantityIntervalListWithNullHighNoOverlapExpected: { Interval[ToQuantity(1), ToQuantity(2)], Interval[ToQuantity(4), null]}
// define NullIvlCollapse_NullCollapse: collapse null // Translation Error
define NullIvlCollapse_CollapseNullLowIntervalList: collapse NullLowIntervalList
define NullIvlCollapse_CollapseNullHighIntervalList: collapse NullHighIntervalList
define NullIvlCollapse_NullInCollapse: collapse { Interval[1,3], null }
define NullIvlCollapse_DateTimeNullStartCollapse: collapse { DateTime1_10Interval, DateTimeNull_5Interval }
define NullIvlCollapse_DateTimeNullEndCollapse: collapse { DateTime1_10Interval, DateTime5_NullInterval }
define NullIvlCollapse_NullPerCollapse: collapse { Interval[1,4], Interval[4,7] } per null
define NullIvlCollapse_DateTimeNullEndCollapseNoOverlap: collapse { DateTime1_2Interval, DateTime5_NullInterval }
define NullIvlCollapse_DateTimeNullStartCollapseNoOverlap: collapse { DateTime9_10Interval, DateTimeNull_5Interval }
define NullIvlCollapse_DateTimeNullStartEndCollapse: collapse { DateTimeNull_5Interval, DateTime1_10Interval, DateTime5_NullInterval }
define NullIvlCollapse_CollapseQuantityNullLowUnitsWithinPer: collapse QuantityMeterNullLowIntervalList per ToQuantity('1 \'m\'')
define NullIvlCollapse_CollapseQuantityNullHighUnitsWithinPer: collapse QuantityMeterNullHighIntervalList per ToQuantity('1 \'m\'')
define NullIvlCollapse_CollapseQuantityIntervalListWithNulls: collapse QuantityIntervalListWithNulls
define NullIvlCollapse_CollapseQuantityIntervalListWithNullLowNoOverlap: collapse QuantityIntervalListWithNullLowNoOverlap
define NullIvlCollapse_CollapseQuantityIntervalListWithNullHighNoOverlap: collapse QuantityIntervalListWithNullHighNoOverlap

define test_NullIvlCollapse_CollapseNullLowIntervalList: TestMessage(NullIvlCollapse_CollapseNullLowIntervalList = ExpectedNullLowIntervalCollapse, 'NullIvlCollapse_CollapseNullLowIntervalList', toString(ExpectedNullLowIntervalCollapse), toString(NullIvlCollapse_CollapseNullLowIntervalList))
define test_NullIvlCollapse_CollapseNullHighIntervalList: TestMessage(NullIvlCollapse_CollapseNullHighIntervalList = ExpectedNullHighIntervalCollapse, 'NullIvlCollapse_CollapseNullHighIntervalList', toString(ExpectedNullHighIntervalCollapse), toString(NullIvlCollapse_CollapseNullHighIntervalList))
define test_NullIvlCollapse_NullInCollapse: TestMessage(NullIvlCollapse_NullInCollapse = ExpectedResultWithNull, 'NullIvlCollapse_NullInCollapse', toString(ExpectedResultWithNull), toString(NullIvlCollapse_NullInCollapse))
define test_NullIvlCollapse_DateTimeNullStartCollapse: TestMessage(NullIvlCollapse_DateTimeNullStartCollapse = DateTimeNullStartCollapseExpected, 'NullIvlCollapse_DateTimeNullStartCollapse', toString(DateTimeNullStartCollapseExpected), toString(NullIvlCollapse_DateTimeNullStartCollapse))
define test_NullIvlCollapse_DateTimeNullEndCollapse: TestMessage(NullIvlCollapse_DateTimeNullEndCollapse = DateTimeNullEndCollapseExpected, 'NullIvlCollapse_DateTimeNullEndCollapse', toString(DateTimeNullEndCollapseExpected), toString(NullIvlCollapse_DateTimeNullEndCollapse))
define test_NullIvlCollapse_NullPerCollapse: TestMessage(NullIvlCollapse_NullPerCollapse = ExpectedResultNullPer, 'NullIvlCollapse_NullPerCollapse', toString(ExpectedResultNullPer), toString(NullIvlCollapse_NullPerCollapse))
define test_NullIvlCollapse_DateTimeNullEndCollapseNoOverlap: TestMessage(NullIvlCollapse_DateTimeNullEndCollapseNoOverlap = DateTimeNullEndCollapseNoOverlapExpected, 'NullIvlCollapse_DateTimeNullEndCollapseNoOverlap', toString(DateTimeNullEndCollapseNoOverlapExpected), toString(NullIvlCollapse_DateTimeNullEndCollapseNoOverlap))
define test_NullIvlCollapse_DateTimeNullStartCollapseNoOverlap: TestMessage(NullIvlCollapse_DateTimeNullStartCollapseNoOverlap = DateTimeNullStartCollapseNoOverlapExpected, 'NullIvlCollapse_DateTimeNullStartCollapseNoOverlap', toString(DateTimeNullStartCollapseNoOverlapExpected), toString(NullIvlCollapse_DateTimeNullStartCollapseNoOverlap))
define test_NullIvlCollapse_DateTimeNullStartEndCollapse: TestMessage(NullIvlCollapse_DateTimeNullStartEndCollapse = DateTimeNullStartEndCollapseExpected, 'NullIvlCollapse_DateTimeNullStartEndCollapse', toString(DateTimeNullStartEndCollapseExpected), toString(NullIvlCollapse_DateTimeNullStartEndCollapse))
//TODO: Fix non Datetime intervals with null
//define test_NullIvlCollapse_CollapseQuantityNullLowUnitsWithinPer: TestMessage(NullIvlCollapse_CollapseQuantityNullLowUnitsWithinPer = CollapseQuantityNullLowUnitsWithinPerExpected, 'NullIvlCollapse_CollapseQuantityNullLowUnitsWithinPer', toString(CollapseQuantityNullLowUnitsWithinPerExpected), toString(NullIvlCollapse_CollapseQuantityNullLowUnitsWithinPer))
//define test_NullIvlCollapse_CollapseQuantityNullHighUnitsWithinPer: TestMessage(NullIvlCollapse_CollapseQuantityNullHighUnitsWithinPer = CollapseQuantityNullHighUnitsWithinPerExpected, 'NullIvlCollapse_CollapseQuantityNullHighUnitsWithinPer', toString(CollapseQuantityNullHighUnitsWithinPerExpected), toString(NullIvlCollapse_CollapseQuantityNullHighUnitsWithinPer))
define test_NullIvlCollapse_CollapseQuantityIntervalListWithNulls: TestMessage(NullIvlCollapse_CollapseQuantityIntervalListWithNulls = CollapseQuantityIntervalListWithNullsExpected, 'NullIvlCollapse_CollapseQuantityIntervalListWithNulls', toString(CollapseQuantityIntervalListWithNullsExpected), toString(NullIvlCollapse_CollapseQuantityIntervalListWithNulls))
define test_NullIvlCollapse_CollapseQuantityIntervalListWithNullLowNoOverlap: TestMessage(NullIvlCollapse_CollapseQuantityIntervalListWithNullLowNoOverlap = CollapseQuantityIntervalListWithNullLowNoOverlapExpected, 'NullIvlCollapse_CollapseQuantityIntervalListWithNullLowNoOverlap', toString(CollapseQuantityIntervalListWithNullLowNoOverlapExpected), toString(NullIvlCollapse_CollapseQuantityIntervalListWithNullLowNoOverlap))
define test_NullIvlCollapse_CollapseQuantityIntervalListWithNullHighNoOverlap: TestMessage(NullIvlCollapse_CollapseQuantityIntervalListWithNullHighNoOverlap = CollapseQuantityIntervalListWithNullHighNoOverlapExpected, 'NullIvlCollapse_CollapseQuantityIntervalListWithNullHighNoOverlap', toString(CollapseQuantityIntervalListWithNullHighNoOverlapExpected), toString(NullIvlCollapse_CollapseQuantityIntervalListWithNullHighNoOverlap))

// DateIntervalExpand
define ClosedSinglePerDayExpected: { Interval[@2018-01-01, @2018-01-01], Interval[@2018-01-02, @2018-01-02], Interval[@2018-01-03, @2018-01-03] }
define ClosedSinglePerWeekExpected: { Interval[@2018-01-01, @2018-01-07], Interval[@2018-01-08, @2018-01-14], Interval[@2018-01-15, @2018-01-21] }
define ClosedSinglePerMonthExpected: { Interval[@2018-01-01, @2018-01-31], Interval[@2018-02-01, @2018-02-28], Interval[@2018-03-01, @2018-03-31] }
define ClosedSinglePerYearExpected: { Interval[@2016-01-01, @2016-12-31], Interval[@2017-01-01, @2017-12-31], Interval[@2018-01-01, @2018-12-31] }
define DateIvlExpand_ClosedSinglePerDay: expand { Interval[@2018-01-01, @2018-01-03] } per day
define DateIvlExpand_ClosedSinglePerWeek: expand { Interval[@2018-01-01, @2018-01-21] } per week
define DateIvlExpand_ClosedSinglePerMonth: expand { Interval[@2018-01-01, @2018-03-31] } per month
define DateIvlExpand_ClosedSinglePerMonthTrunc: expand { Interval[@2018-01-01, @2018-04-29] } per month
define DateIvlExpand_ClosedSinglePerYear: expand { Interval[@2016-01-01, @2018-12-31] } per year
define DateIvlExpand_ClosedSinglePerYearTrunc: expand { Interval[@2016-01-01, @2019-12-30] } per year

define test_DateIvlExpand_ClosedSinglePerDay: TestMessage(DateIvlExpand_ClosedSinglePerDay = ClosedSinglePerDayExpected, 'DateIvlExpand_ClosedSinglePerDay', toString(ClosedSinglePerDayExpected), toString(DateIvlExpand_ClosedSinglePerDay))
define test_DateIvlExpand_ClosedSinglePerWeek: TestMessage(DateIvlExpand_ClosedSinglePerWeek = ClosedSinglePerWeekExpected, 'DateIvlExpand_ClosedSinglePerWeek', toString(ClosedSinglePerWeekExpected), toString(DateIvlExpand_ClosedSinglePerWeek))
define test_DateIvlExpand_ClosedSinglePerMonth: TestMessage(DateIvlExpand_ClosedSinglePerMonth = ClosedSinglePerMonthExpected, 'DateIvlExpand_ClosedSinglePerMonth', toString(ClosedSinglePerMonthExpected), toString(DateIvlExpand_ClosedSinglePerMonth))
define test_DateIvlExpand_ClosedSinglePerMonthTrunc: TestMessage(DateIvlExpand_ClosedSinglePerMonthTrunc = ClosedSinglePerMonthExpected, 'DateIvlExpand_ClosedSinglePerMonthTrunc', toString(ClosedSinglePerMonthExpected), toString(DateIvlExpand_ClosedSinglePerMonthTrunc))
define test_DateIvlExpand_ClosedSinglePerYear: TestMessage(DateIvlExpand_ClosedSinglePerYear = ClosedSinglePerYearExpected, 'DateIvlExpand_ClosedSinglePerYear', toString(ClosedSinglePerYearExpected), toString(DateIvlExpand_ClosedSinglePerYear))
define test_DateIvlExpand_ClosedSinglePerYearTrunc: TestMessage(DateIvlExpand_ClosedSinglePerYearTrunc = ClosedSinglePerYearExpected, 'DateIvlExpand_ClosedSinglePerYearTrunc', toString(ClosedSinglePerYearExpected), toString(DateIvlExpand_ClosedSinglePerYearTrunc))

define MidBoundariesPerDayExpected: { Interval[@2017-12-30, @2017-12-30], Interval[@2017-12-31, @2017-12-31], Interval[@2018-01-01, @2018-01-01] }
define MidBoundariesPerMonthExpected: { Interval[@2017-11-14, @2017-12-13], Interval[@2017-12-14, @2018-01-13] }
define MidBoundariesPerYearExpected: { Interval[@2016-04-06, @2017-04-05], Interval[@2017-04-06, @2018-04-05] }
define DateIvlExpand_MidBoundariesPerDay: expand { Interval[@2017-12-30, @2018-01-01] } per day
define DateIvlExpand_MidBoundariesPerMonth: expand { Interval[@2017-11-14, @2018-01-18] } per month
define DateIvlExpand_MidBoundariesPerYear: expand { Interval[@2016-04-06, @2018-04-06] } per year

define test_DateIvlExpand_MidBoundariesPerDay: TestMessage(DateIvlExpand_MidBoundariesPerDay = MidBoundariesPerDayExpected, 'DateIvlExpand_MidBoundariesPerDay', toString(MidBoundariesPerDayExpected), toString(DateIvlExpand_MidBoundariesPerDay))
define test_DateIvlExpand_MidBoundariesPerMonth: TestMessage(DateIvlExpand_MidBoundariesPerMonth = MidBoundariesPerMonthExpected, 'DateIvlExpand_MidBoundariesPerMonth', toString(MidBoundariesPerMonthExpected), toString(DateIvlExpand_MidBoundariesPerMonth))
define test_DateIvlExpand_MidBoundariesPerYear: TestMessage(DateIvlExpand_MidBoundariesPerYear = MidBoundariesPerYearExpected, 'DateIvlExpand_MidBoundariesPerYear', toString(MidBoundariesPerYearExpected), toString(DateIvlExpand_MidBoundariesPerYear))

define OverlappingExpected: { Interval[@2018-01-01, @2018-01-01], Interval[@2018-01-02, @2018-01-02], Interval[@2018-01-03, @2018-01-03], Interval[@2018-01-04, @2018-01-04] }
define NonOverlappingExpected: { Interval[@2018-01-01, @2018-01-01], Interval[@2018-01-02, @2018-01-02], Interval[@2018-01-03, @2018-01-03], Interval[@2018-01-08, @2018-01-08] }
define DateIvlExpand_NullInList: expand { Interval[@2018-01-01, @2018-01-03], null } per day
define DateIvlExpand_Overlapping: expand { Interval[@2018-01-01, @2018-01-03], Interval[@2018-01-02, @2018-01-04] } per day
define DateIvlExpand_NonOverlapping: expand { Interval[@2018-01-01, @2018-01-03], Interval[@2018-01-08, @2018-01-08] } per day

define test_DateIvlExpand_NullInList: TestMessage(DateIvlExpand_NullInList = ClosedSinglePerDayExpected, 'DateIvlExpand_NullInList', toString(ClosedSinglePerDayExpected), toString(DateIvlExpand_NullInList))
define test_DateIvlExpand_Overlapping: TestMessage(DateIvlExpand_Overlapping = OverlappingExpected, 'DateIvlExpand_Overlapping', toString(OverlappingExpected), toString(DateIvlExpand_Overlapping))
define test_DateIvlExpand_NonOverlapping: TestMessage(DateIvlExpand_NonOverlapping = NonOverlappingExpected, 'DateIvlExpand_NonOverlapping', toString(NonOverlappingExpected), toString(DateIvlExpand_NonOverlapping))

define NoPerDefaultMonthExpected: { Interval[@2018-01, @2018-01], Interval[@2018-02, @2018-02], Interval[@2018-03, @2018-03] }
define NoPerDefaultYearExpected: { Interval[@2016, @2016], Interval[@2017, @2017], Interval[@2018, @2018] }
define DateIvlExpand_NoPerDefaultDay: expand { Interval[@2018-01-01, @2018-01-03] }
define DateIvlExpand_NoPerDefaultMonth: expand { Interval[@2018-01, @2018-03] }
define DateIvlExpand_NoPerDefaultYear: expand { Interval[@2016, @2018] }
define DateIvlExpand_NoPerDefaultMonthWithMismatch: expand { Interval[@2016, @2018-03] }

define test_DateIvlExpand_NoPerDefaultDay: TestMessage(DateIvlExpand_NoPerDefaultDay = ClosedSinglePerDayExpected, 'DateIvlExpand_NoPerDefaultDay', toString(ClosedSinglePerDayExpected), toString(DateIvlExpand_NoPerDefaultDay))
define test_DateIvlExpand_NoPerDefaultMonth: TestMessage(DateIvlExpand_NoPerDefaultMonth = NoPerDefaultMonthExpected, 'DateIvlExpand_NoPerDefaultMonth', toString(NoPerDefaultMonthExpected), toString(DateIvlExpand_NoPerDefaultMonth))
define test_DateIvlExpand_NoPerDefaultYear: TestMessage(DateIvlExpand_NoPerDefaultYear = NoPerDefaultYearExpected, 'DateIvlExpand_NoPerDefaultYear', toString(NoPerDefaultYearExpected), toString(DateIvlExpand_NoPerDefaultYear))
define test_DateIvlExpand_NoPerDefaultMonthWithMismatch: TestMessage(DateIvlExpand_NoPerDefaultMonthWithMismatch = NoPerDefaultYearExpected, 'DateIvlExpand_NoPerDefaultMonthWithMismatch', toString(NoPerDefaultYearExpected), toString(DateIvlExpand_NoPerDefaultMonthWithMismatch))

define OpenStartExpected: { Interval[@2018-01-02, @2018-01-02], Interval[@2018-01-03, @2018-01-03] }
define OpenEndExpected: { Interval[@2018-01-01, @2018-01-01], Interval[@2018-01-02, @2018-01-02] }
define OpenBothExpected: { Interval[@2018-01-02, @2018-01-02] }
define DateIvlExpand_OpenStart: expand { Interval(@2018-01-01, @2018-01-03] } per day
define DateIvlExpand_OpenEnd: expand { Interval[@2018-01-01, @2018-01-03) } per day
define DateIvlExpand_OpenBoth: expand { Interval(@2018-01-01, @2018-01-03) } per day

define test_DateIvlExpand_OpenStart: TestMessage(DateIvlExpand_OpenStart = OpenStartExpected, 'DateIvlExpand_OpenStart', toString(OpenStartExpected), toString(DateIvlExpand_OpenStart))
define test_DateIvlExpand_OpenEnd: TestMessage(DateIvlExpand_OpenEnd = OpenEndExpected, 'DateIvlExpand_OpenEnd', toString(OpenEndExpected), toString(DateIvlExpand_OpenEnd))
define test_DateIvlExpand_OpenBoth: TestMessage(DateIvlExpand_OpenBoth = OpenBothExpected, 'DateIvlExpand_OpenBoth', toString(OpenBothExpected), toString(DateIvlExpand_OpenBoth))

define MismatchPrecisionExpected: { Interval[@2018-01-01, @2018-01-31], Interval[@2018-02-01, @2018-02-28] }
define MismatchPrecisionEmptyExpected: { Interval[@2018-01, @2018-01], Interval[@2018-02, @2018-02] }
define DateIvlExpand_MismatchPrecision: expand { Interval[@2018-01-01, @2018-03] } per month
define DateIvlExpand_MismatchPrecisionEmpty: expand { Interval[@2018-01, @2018-02-28] } per month

define test_DateIvlExpand_MismatchPrecision: TestMessage(DateIvlExpand_MismatchPrecision = MismatchPrecisionExpected, 'DateIvlExpand_MismatchPrecision', toString(MismatchPrecisionExpected), toString(DateIvlExpand_MismatchPrecision))
define test_DateIvlExpand_MismatchPrecisionEmpty: TestMessage(DateIvlExpand_MismatchPrecisionEmpty = MismatchPrecisionEmptyExpected, 'DateIvlExpand_MismatchPrecisionEmpty', toString(MismatchPrecisionEmptyExpected), toString(DateIvlExpand_MismatchPrecisionEmpty))

define DateIvlExpand_NullOpen: expand { Interval[null, @2018-01-03] } per day
define DateIvlExpand_NullClose: expand { Interval[@2018-01-01, null] } per day
// TODO: should be { Interval[null as Date, null] } but that currently evaluates to null in the engine, uncomment once fixed
//define DateIvlExpand_NullBoth: expand { Interval[null, null] } per day
define DateIvlExpand_MonthDayPer: expand { Interval[@2018-01, @2018-03] } per day
define DateIvlExpand_YearMonthPer: expand { Interval[@2016, @2018] } per month
define DateIvlExpand_YearDayPer: expand { Interval[@2016, @2018] } per day
define DateIvlExpand_BadPerMinute: expand { Interval[@2018-01-01, @2018-01-04] } per minute
define DateIvlExpand_BadPerGram: expand { Interval[@2018-01-01, @2018-01-04] } per 1 'g'

define test_DateIvlExpand_NullOpen: TestMessage(DateIvlExpand_NullOpen is null, 'DateIvlExpand_NullOpen', 'null', toString(DateIvlExpand_NullOpen))
define test_DateIvlExpand_NullClose: TestMessage(DateIvlExpand_NullClose is null, 'DateIvlExpand_NullClose', 'null', toString(DateIvlExpand_NullClose))
//define test_DateIvlExpand_NullBoth: TestMessage(DateIvlExpand_NullBoth is null, 'DateIvlExpand_NullBoth', 'null', toString(DateIvlExpand_NullBoth))
define test_DateIvlExpand_MonthDayPer: TestMessage(DateIvlExpand_MonthDayPer is null, 'DateIvlExpand_MonthDayPer', 'null', toString(DateIvlExpand_MonthDayPer))
define test_DateIvlExpand_YearMonthPer: TestMessage(DateIvlExpand_YearMonthPer is null, 'DateIvlExpand_YearMonthPer', 'null', toString(DateIvlExpand_YearMonthPer))
define test_DateIvlExpand_YearDayPer: TestMessage(DateIvlExpand_YearDayPer is null, 'DateIvlExpand_YearDayPer', 'null', toString(DateIvlExpand_YearDayPer))
define test_DateIvlExpand_BadPerMinute: TestMessage(DateIvlExpand_BadPerMinute is null, 'DateIvlExpand_BadPerMinute', 'null', toString(DateIvlExpand_BadPerMinute))
define test_DateIvlExpand_BadPerGram: TestMessage(DateIvlExpand_BadPerGram is null, 'DateIvlExpand_BadPerGram', 'null', toString(DateIvlExpand_BadPerGram))

// DateTimeIntervalExpand
define MsPrecPerYearExpected: { Interval[@2016-01-01T00:00:00.000+00:00, @2016-12-31T23:59:59.999+00:00], Interval[@2017-01-01T00:00:00.000+00:00, @2017-12-31T23:59:59.999+00:00] }
define MsPrecPerMonthExpected: { Interval[@2018-01-01T00:00:00.000+00:00, @2018-01-31T23:59:59.999+00:00], Interval[@2018-02-01T00:00:00.000+00:00, @2018-02-28T23:59:59.999+00:00] }
define MsPrecPerWeekExpected: { Interval[@2018-01-01T00:00:00.000+00:00, @2018-01-07T23:59:59.999+00:00], Interval[@2018-01-08T00:00:00.000+00:00, @2018-01-14T23:59:59.999+00:00] }
define MsPrecPerDayExpected: { Interval[@2018-01-01T00:00:00.000+00:00, @2018-01-01T23:59:59.999+00:00], Interval[@2018-01-02T00:00:00.000+00:00, @2018-01-02T23:59:59.999+00:00] }
define MsPrecPerHourExpected: { Interval[@2018-01-01T01:00:00.000+00:00, @2018-01-01T01:59:59.999+00:00], Interval[@2018-01-01T02:00:00.000+00:00, @2018-01-01T02:59:59.999+00:00] }
define MsPrecPerMinuteExpected: { Interval[@2018-01-01T01:00:00.000+00:00, @2018-01-01T01:00:59.999+00:00], Interval[@2018-01-01T01:01:00.000+00:00, @2018-01-01T01:01:59.999+00:00] }
define MsPrecPerSecondExpected: { Interval[@2018-01-01T01:00:00.000+00:00, @2018-01-01T01:00:00.999+00:00], Interval[@2018-01-01T01:00:01.000+00:00, @2018-01-01T01:00:01.999+00:00] }
define MsPrecPerMillisecondExpected: { Interval[@2018-01-01T01:00:00.000+00:00, @2018-01-01T01:00:00.000+00:00], Interval[@2018-01-01T01:00:00.001+00:00, @2018-01-01T01:00:00.001+00:00] }
define DateTimeIvlExpand_MsPrecPerYear: expand { Interval[@2016-01-01T00:00:00.000+00:00, @2018-01-01T00:00:00.000+00:00] } per year
define DateTimeIvlExpand_MsPrecPerMonth: expand { Interval[@2018-01-01T00:00:00.000+00:00, @2018-03-01T00:00:00.000+00:00] } per month
define DateTimeIvlExpand_MsPrecPerWeek: expand { Interval[@2018-01-01T00:00:00.000+00:00, @2018-01-21T00:00:00.000+00:00] } per week
define DateTimeIvlExpand_MsPrecPerDay: expand { Interval[@2018-01-01T00:00:00.000+00:00, @2018-01-03T00:00:00.000+00:00] } per day
define DateTimeIvlExpand_MsPrecPerHour: expand { Interval[@2018-01-01T01:00:00.000+00:00, @2018-01-01T03:00:00.000+00:00] } per hour
define DateTimeIvlExpand_MsPrecPerMinute: expand { Interval[@2018-01-01T01:00:00.000+00:00, @2018-01-01T01:02:00.000+00:00] } per minute
define DateTimeIvlExpand_MsPrecPerSecond: expand { Interval[@2018-01-01T01:00:00.000+00:00, @2018-01-01T01:00:02.000+00:00] } per second
define DateTimeIvlExpand_MsPrecPerMillisecond: expand { Interval[@2018-01-01T01:00:00.000+00:00, @2018-01-01T01:00:00.001+00:00] } per millisecond

define test_DateTimeIvlExpand_MsPrecPerYear: TestMessage(DateTimeIvlExpand_MsPrecPerYear = MsPrecPerYearExpected, 'DateTimeIvlExpand_MsPrecPerYear', toString(MsPrecPerYearExpected), toString(DateTimeIvlExpand_MsPrecPerYear))
define test_DateTimeIvlExpand_MsPrecPerMonth: TestMessage(DateTimeIvlExpand_MsPrecPerMonth = MsPrecPerMonthExpected, 'DateTimeIvlExpand_MsPrecPerMonth', toString(MsPrecPerMonthExpected), toString(DateTimeIvlExpand_MsPrecPerMonth))
define test_DateTimeIvlExpand_MsPrecPerWeek: TestMessage(DateTimeIvlExpand_MsPrecPerWeek = MsPrecPerWeekExpected, 'DateTimeIvlExpand_MsPrecPerWeek', toString(MsPrecPerWeekExpected), toString(DateTimeIvlExpand_MsPrecPerWeek))
define test_DateTimeIvlExpand_MsPrecPerDay: TestMessage(DateTimeIvlExpand_MsPrecPerDay = MsPrecPerDayExpected, 'DateTimeIvlExpand_MsPrecPerDay', toString(MsPrecPerDayExpected), toString(DateTimeIvlExpand_MsPrecPerDay))
define test_DateTimeIvlExpand_MsPrecPerHour: TestMessage(DateTimeIvlExpand_MsPrecPerHour = MsPrecPerHourExpected, 'DateTimeIvlExpand_MsPrecPerHour', toString(MsPrecPerHourExpected), toString(DateTimeIvlExpand_MsPrecPerHour))
define test_DateTimeIvlExpand_MsPrecPerMinute: TestMessage(DateTimeIvlExpand_MsPrecPerMinute = MsPrecPerMinuteExpected, 'DateTimeIvlExpand_MsPrecPerMinute', toString(MsPrecPerMinuteExpected), toString(DateTimeIvlExpand_MsPrecPerMinute))
define test_DateTimeIvlExpand_MsPrecPerSecond: TestMessage(DateTimeIvlExpand_MsPrecPerSecond = MsPrecPerSecondExpected, 'DateTimeIvlExpand_MsPrecPerSecond', toString(MsPrecPerSecondExpected), toString(DateTimeIvlExpand_MsPrecPerSecond))
define test_DateTimeIvlExpand_MsPrecPerMillisecond: TestMessage(DateTimeIvlExpand_MsPrecPerMillisecond = MsPrecPerMillisecondExpected, 'DateTimeIvlExpand_MsPrecPerMillisecond', toString(MsPrecPerMillisecondExpected), toString(DateTimeIvlExpand_MsPrecPerMillisecond))

define SecPrecPerYearExpected: { Interval[@2016-01-01T00:00:00+00:00, @2016-12-31T23:59:59+00:00], Interval[@2017-01-01T00:00:00+00:00, @2017-12-31T23:59:59+00:00] }
define SecPrecPerMonthExpected: { Interval[@2018-01-01T00:00:00+00:00, @2018-01-31T23:59:59+00:00], Interval[@2018-02-01T00:00:00+00:00, @2018-02-28T23:59:59+00:00] }
define SecPrecPerWeekExpected: { Interval[@2018-01-01T00:00:00+00:00, @2018-01-07T23:59:59+00:00], Interval[@2018-01-08T00:00:00+00:00, @2018-01-14T23:59:59+00:00] }
define SecPrecPerDayExpected: { Interval[@2018-01-01T00:00:00+00:00, @2018-01-01T23:59:59+00:00], Interval[@2018-01-02T00:00:00+00:00, @2018-01-02T23:59:59+00:00] }
define SecPrecPerHourExpected: { Interval[@2018-01-01T01:00:00+00:00, @2018-01-01T01:59:59+00:00], Interval[@2018-01-01T02:00:00+00:00, @2018-01-01T02:59:59+00:00] }
define SecPrecPerMinuteExpected: { Interval[@2018-01-01T01:00:00+00:00, @2018-01-01T01:00:59+00:00], Interval[@2018-01-01T01:01:00+00:00, @2018-01-01T01:01:59+00:00] }
define SecPrecPerSecondExpected: { Interval[@2018-01-01T01:00:00+00:00, @2018-01-01T01:00:00+00:00], Interval[@2018-01-01T01:00:01+00:00, @2018-01-01T01:00:01+00:00] }
define DateTimeIvlExpand_SecPrecPerYear: expand { Interval[@2016-01-01T00:00:00+00:00, @2018-01-01T00:00:00+00:00] } per year
define DateTimeIvlExpand_SecPrecPerMonth: expand { Interval[@2018-01-01T00:00:00+00:00, @2018-03-01T00:00:00+00:00] } per month
define DateTimeIvlExpand_SecPrecPerWeek: expand { Interval[@2018-01-01T00:00:00+00:00, @2018-01-21T00:00:00+00:00] } per week
define DateTimeIvlExpand_SecPrecPerDay: expand { Interval[@2018-01-01T00:00:00+00:00, @2018-01-03T00:00:00+00:00] } per day
define DateTimeIvlExpand_SecPrecPerHour: expand { Interval[@2018-01-01T01:00:00+00:00, @2018-01-01T03:00:00+00:00] } per hour
define DateTimeIvlExpand_SecPrecPerMinute: expand { Interval[@2018-01-01T01:00:00+00:00, @2018-01-01T01:02:00+00:00] } per minute
define DateTimeIvlExpand_SecPrecPerSecond: expand { Interval[@2018-01-01T01:00:00+00:00, @2018-01-01T01:00:01+00:00] } per second
define DateTimeIvlExpand_SecPrecPerMillisecond: expand { Interval[@2018-01-01T01:00:00+00:00, @2018-01-01T01:00:00+00:00] } per millisecond

define test_DateTimeIvlExpand_SecPrecPerYear: TestMessage(DateTimeIvlExpand_SecPrecPerYear = SecPrecPerYearExpected, 'DateTimeIvlExpand_SecPrecPerYear', toString(SecPrecPerYearExpected), toString(DateTimeIvlExpand_SecPrecPerYear))
define test_DateTimeIvlExpand_SecPrecPerMonth: TestMessage(DateTimeIvlExpand_SecPrecPerMonth = SecPrecPerMonthExpected, 'DateTimeIvlExpand_SecPrecPerMonth', toString(SecPrecPerMonthExpected), toString(DateTimeIvlExpand_SecPrecPerMonth))
define test_DateTimeIvlExpand_SecPrecPerWeek: TestMessage(DateTimeIvlExpand_SecPrecPerWeek = SecPrecPerWeekExpected, 'DateTimeIvlExpand_SecPrecPerWeek', toString(SecPrecPerWeekExpected), toString(DateTimeIvlExpand_SecPrecPerWeek))
define test_DateTimeIvlExpand_SecPrecPerDay: TestMessage(DateTimeIvlExpand_SecPrecPerDay = SecPrecPerDayExpected, 'DateTimeIvlExpand_SecPrecPerDay', toString(SecPrecPerDayExpected), toString(DateTimeIvlExpand_SecPrecPerDay))
define test_DateTimeIvlExpand_SecPrecPerHour: TestMessage(DateTimeIvlExpand_SecPrecPerHour = SecPrecPerHourExpected, 'DateTimeIvlExpand_SecPrecPerHour', toString(SecPrecPerHourExpected), toString(DateTimeIvlExpand_SecPrecPerHour))
define test_DateTimeIvlExpand_SecPrecPerMinute: TestMessage(DateTimeIvlExpand_SecPrecPerMinute = SecPrecPerMinuteExpected, 'DateTimeIvlExpand_SecPrecPerMinute', toString(SecPrecPerMinuteExpected), toString(DateTimeIvlExpand_SecPrecPerMinute))
define test_DateTimeIvlExpand_SecPrecPerSecond: TestMessage(DateTimeIvlExpand_SecPrecPerSecond = SecPrecPerSecondExpected, 'DateTimeIvlExpand_SecPrecPerSecond', toString(SecPrecPerSecondExpected), toString(DateTimeIvlExpand_SecPrecPerSecond))
define test_DateTimeIvlExpand_SecPrecPerMillisecond: TestMessage(DateTimeIvlExpand_SecPrecPerMillisecond is null, 'DateTimeIvlExpand_SecPrecPerMillisecond', 'null', toString(DateTimeIvlExpand_SecPrecPerMillisecond))

define MinPrecPerYearExpected: { Interval[@2016-01-01T00:00+00:00, @2016-12-31T23:59+00:00], Interval[@2017-01-01T00:00+00:00, @2017-12-31T23:59+00:00] }
define MinPrecPerMonthExpected: { Interval[@2018-01-01T00:00+00:00, @2018-01-31T23:59+00:00], Interval[@2018-02-01T00:00+00:00, @2018-02-28T23:59+00:00] }
define MinPrecPerWeekExpected: { Interval[@2018-01-01T00:00+00:00, @2018-01-07T23:59+00:00], Interval[@2018-01-08T00:00+00:00, @2018-01-14T23:59+00:00] }
define MinPrecPerDayExpected: { Interval[@2018-01-01T00:00+00:00, @2018-01-01T23:59+00:00], Interval[@2018-01-02T00:00+00:00, @2018-01-02T23:59+00:00] }
define MinPrecPerHourExpected: { Interval[@2018-01-01T01:00+00:00, @2018-01-01T01:59+00:00], Interval[@2018-01-01T02:00+00:00, @2018-01-01T02:59+00:00] }
define MinPrecPerMinuteExpected: { Interval[@2018-01-01T01:00+00:00, @2018-01-01T01:00+00:00], Interval[@2018-01-01T01:01+00:00, @2018-01-01T01:01+00:00] }
define DateTimeIvlExpand_MinPrecPerYear: expand { Interval[@2016-01-01T00:00+00:00, @2018-01-01T00:00+00:00] } per year
define DateTimeIvlExpand_MinPrecPerMonth: expand { Interval[@2018-01-01T00:00+00:00, @2018-03-01T00:00+00:00] } per month
define DateTimeIvlExpand_MinPrecPerWeek: expand { Interval[@2018-01-01T00:00+00:00, @2018-01-21T00:00+00:00] } per week
define DateTimeIvlExpand_MinPrecPerDay: expand { Interval[@2018-01-01T00:00+00:00, @2018-01-03T00:00+00:00] } per day
define DateTimeIvlExpand_MinPrecPerHour: expand { Interval[@2018-01-01T01:00+00:00, @2018-01-01T03:00+00:00] } per hour
define DateTimeIvlExpand_MinPrecPerMinute: expand { Interval[@2018-01-01T01:00+00:00, @2018-01-01T01:01+00:00] } per minute
define DateTimeIvlExpand_MinPrecPerSecond: expand { Interval[@2018-01-01T01:00+00:00, @2018-01-01T01:00+00:00] } per second
define DateTimeIvlExpand_MinPrecPerMillisecond: expand { Interval[@2018-01-01T01:00+00:00, @2018-01-01T01:00+00:00] } per millisecond

define test_DateTimeIvlExpand_MinPrecPerYear: TestMessage(DateTimeIvlExpand_MinPrecPerYear = MinPrecPerYearExpected, 'DateTimeIvlExpand_MinPrecPerYear', toString(MinPrecPerYearExpected), toString(DateTimeIvlExpand_MinPrecPerYear))
define test_DateTimeIvlExpand_MinPrecPerMonth: TestMessage(DateTimeIvlExpand_MinPrecPerMonth = MinPrecPerMonthExpected, 'DateTimeIvlExpand_MinPrecPerMonth', toString(MinPrecPerMonthExpected), toString(DateTimeIvlExpand_MinPrecPerMonth))
define test_DateTimeIvlExpand_MinPrecPerWeek: TestMessage(DateTimeIvlExpand_MinPrecPerWeek = MinPrecPerWeekExpected, 'DateTimeIvlExpand_MinPrecPerWeek', toString(MinPrecPerWeekExpected), toString(DateTimeIvlExpand_MinPrecPerWeek))
define test_DateTimeIvlExpand_MinPrecPerDay: TestMessage(DateTimeIvlExpand_MinPrecPerDay = MinPrecPerDayExpected, 'DateTimeIvlExpand_MinPrecPerDay', toString(MinPrecPerDayExpected), toString(DateTimeIvlExpand_MinPrecPerDay))
define test_DateTimeIvlExpand_MinPrecPerHour: TestMessage(DateTimeIvlExpand_MinPrecPerHour = MinPrecPerHourExpected, 'DateTimeIvlExpand_MinPrecPerHour', toString(MinPrecPerHourExpected), toString(DateTimeIvlExpand_MinPrecPerHour))
define test_DateTimeIvlExpand_MinPrecPerMinute: TestMessage(DateTimeIvlExpand_MinPrecPerMinute = MinPrecPerMinuteExpected, 'DateTimeIvlExpand_MinPrecPerMinute', toString(MinPrecPerMinuteExpected), toString(DateTimeIvlExpand_MinPrecPerMinute))
define test_DateTimeIvlExpand_MinPrecPerSecond: TestMessage(DateTimeIvlExpand_MinPrecPerSecond is null, 'DateTimeIvlExpand_MinPrecPerSecond', 'null', toString(DateTimeIvlExpand_MinPrecPerSecond))
define test_DateTimeIvlExpand_MinPrecPerMillisecond: TestMessage(DateTimeIvlExpand_MinPrecPerMillisecond is null, 'DateTimeIvlExpand_MinPrecPerMillisecond', 'null', toString(DateTimeIvlExpand_MinPrecPerMillisecond))

define HourPrecPerYearExpected: { Interval[@2016-01-01T00+00:00, @2016-12-31T23+00:00], Interval[@2017-01-01T00+00:00, @2017-12-31T23+00:00] }
define HourPrecPerMonthExpected: { Interval[@2018-01-01T00+00:00, @2018-01-31T23+00:00], Interval[@2018-02-01T00+00:00, @2018-02-28T23+00:00] }
define HourPrecPerWeekExpected: { Interval[@2018-01-01T00+00:00, @2018-01-07T23+00:00], Interval[@2018-01-08T00+00:00, @2018-01-14T23+00:00] }
define HourPrecPerDayExpected: { Interval[@2018-01-01T00+00:00, @2018-01-01T23+00:00], Interval[@2018-01-02T00+00:00, @2018-01-02T23+00:00] }
define HourPrecPerHourExpected: { Interval[@2018-01-01T01+00:00, @2018-01-01T01+00:00], Interval[@2018-01-01T02+00:00, @2018-01-01T02+00:00] }
define DateTimeIvlExpand_HourPrecPerYear: expand { Interval[@2016-01-01T00+00:00, @2018-01-01T00+00:00] } per year
define DateTimeIvlExpand_HourPrecPerMonth: expand { Interval[@2018-01-01T00+00:00, @2018-03-01T00+00:00] } per month
define DateTimeIvlExpand_HourPrecPerWeek: expand { Interval[@2018-01-01T00+00:00, @2018-01-21T00+00:00] } per week
define DateTimeIvlExpand_HourPrecPerDay: expand { Interval[@2018-01-01T00+00:00, @2018-01-03T00+00:00] } per day
define DateTimeIvlExpand_HourPrecPerHour: expand { Interval[@2018-01-01T01+00:00, @2018-01-01T02+00:00] } per hour
define DateTimeIvlExpand_HourPrecPerMinute: expand { Interval[@2018-01-01T01+00:00, @2018-01-01T01+00:00] } per minute
define DateTimeIvlExpand_HourPrecPerSecond: expand { Interval[@2018-01-01T01+00:00, @2018-01-01T01+00:00] } per second
define DateTimeIvlExpand_HourPrecPerMillisecond: expand { Interval[@2018-01-01T01+00:00, @2018-01-01T01+00:00] } per millisecond

define test_DateTimeIvlExpand_HourPrecPerYear: TestMessage(DateTimeIvlExpand_HourPrecPerYear = HourPrecPerYearExpected, 'DateTimeIvlExpand_HourPrecPerYear', toString(HourPrecPerYearExpected), toString(DateTimeIvlExpand_HourPrecPerYear))
define test_DateTimeIvlExpand_HourPrecPerMonth: TestMessage(DateTimeIvlExpand_HourPrecPerMonth = HourPrecPerMonthExpected, 'DateTimeIvlExpand_HourPrecPerMonth', toString(HourPrecPerMonthExpected), toString(DateTimeIvlExpand_HourPrecPerMonth))
define test_DateTimeIvlExpand_HourPrecPerWeek: TestMessage(DateTimeIvlExpand_HourPrecPerWeek = HourPrecPerWeekExpected, 'DateTimeIvlExpand_HourPrecPerWeek', toString(HourPrecPerWeekExpected), toString(DateTimeIvlExpand_HourPrecPerWeek))
define test_DateTimeIvlExpand_HourPrecPerDay: TestMessage(DateTimeIvlExpand_HourPrecPerDay = HourPrecPerDayExpected, 'DateTimeIvlExpand_HourPrecPerDay', toString(HourPrecPerDayExpected), toString(DateTimeIvlExpand_HourPrecPerDay))
define test_DateTimeIvlExpand_HourPrecPerHour: TestMessage(DateTimeIvlExpand_HourPrecPerHour = HourPrecPerHourExpected, 'DateTimeIvlExpand_HourPrecPerHour', toString(HourPrecPerHourExpected), toString(DateTimeIvlExpand_HourPrecPerHour))
define test_DateTimeIvlExpand_HourPrecPerMinute: TestMessage(DateTimeIvlExpand_HourPrecPerMinute is null, 'DateTimeIvlExpand_HourPrecPerMinute', 'null', toString(DateTimeIvlExpand_HourPrecPerMinute))
define test_DateTimeIvlExpand_HourPrecPerSecond: TestMessage(DateTimeIvlExpand_HourPrecPerSecond is null, 'DateTimeIvlExpand_HourPrecPerSecond', 'null', toString(DateTimeIvlExpand_HourPrecPerSecond))
define test_DateTimeIvlExpand_HourPrecPerMillisecond: TestMessage(DateTimeIvlExpand_HourPrecPerMillisecond is null, 'DateTimeIvlExpand_HourPrecPerMillisecond', 'null', toString(DateTimeIvlExpand_HourPrecPerMillisecond))

define DayPrecPerYearExpected: { Interval[DateTime(2016,01,01), DateTime(2016,12,31)], Interval[DateTime(2017,01,01), DateTime(2017,12,31)] }
define DayPrecPerMonthExpected: { Interval[DateTime(2018,01,01), DateTime(2018,01,31)], Interval[DateTime(2018,02,01), DateTime(2018,02,28)] }
define DayPrecPerWeekExpected: { Interval[DateTime(2018,01,01), DateTime(2018,01,07)], Interval[DateTime(2018,01,08), DateTime(2018,01,14)] }
define DayPrecPerDayExpected: { Interval[DateTime(2018,01,01), DateTime(2018,01,01)], Interval[DateTime(2018,01,02), DateTime(2018,01,02)] }
define DateTimeIvlExpand_DayPrecPerYear: expand { Interval[DateTime(2016,01,01), DateTime(2018,01,01)] } per year
define DateTimeIvlExpand_DayPrecPerMonth: expand { Interval[DateTime(2018,01,01), DateTime(2018,03,01)] } per month
define DateTimeIvlExpand_DayPrecPerWeek: expand { Interval[DateTime(2018,01,01), DateTime(2018,01,14)] } per week
define DateTimeIvlExpand_DayPrecPerDay: expand { Interval[DateTime(2018,01,01), DateTime(2018,01,02)] } per day
define DateTimeIvlExpand_DayPrecPerHour: expand { Interval[DateTime(2018,01,01), DateTime(2018,01,01)] } per hour
define DateTimeIvlExpand_DayPrecPerMinute: expand { Interval[DateTime(2018,01,01), DateTime(2018,01,01)] } per minute
define DateTimeIvlExpand_DayPrecPerSecond: expand { Interval[DateTime(2018,01,01), DateTime(2018,01,01)] } per second
define DateTimeIvlExpand_DayPrecPerMillisecond: expand { Interval[DateTime(2018,01,01), DateTime(2018,01,01)] } per millisecond

define test_DateTimeIvlExpand_DayPrecPerYear: TestMessage(DateTimeIvlExpand_DayPrecPerYear = DayPrecPerYearExpected, 'DateTimeIvlExpand_DayPrecPerYear', toString(DayPrecPerYearExpected), toString(DateTimeIvlExpand_DayPrecPerYear))
define test_DateTimeIvlExpand_DayPrecPerMonth: TestMessage(DateTimeIvlExpand_DayPrecPerMonth = DayPrecPerMonthExpected, 'DateTimeIvlExpand_DayPrecPerMonth', toString(DayPrecPerMonthExpected), toString(DateTimeIvlExpand_DayPrecPerMonth))
define test_DateTimeIvlExpand_DayPrecPerWeek: TestMessage(DateTimeIvlExpand_DayPrecPerWeek = DayPrecPerWeekExpected, 'DateTimeIvlExpand_DayPrecPerWeek', toString(DayPrecPerWeekExpected), toString(DateTimeIvlExpand_DayPrecPerWeek))
define test_DateTimeIvlExpand_DayPrecPerDay: TestMessage(DateTimeIvlExpand_DayPrecPerDay = DayPrecPerDayExpected, 'DateTimeIvlExpand_DayPrecPerDay', toString(DayPrecPerDayExpected), toString(DateTimeIvlExpand_DayPrecPerDay))
define test_DateTimeIvlExpand_DayPrecPerHour: TestMessage(DateTimeIvlExpand_DayPrecPerHour is null, 'DateTimeIvlExpand_DayPrecPerHour', 'null', toString(DateTimeIvlExpand_DayPrecPerHour))
define test_DateTimeIvlExpand_DayPrecPerMinute: TestMessage(DateTimeIvlExpand_DayPrecPerMinute is null, 'DateTimeIvlExpand_DayPrecPerMinute', 'null', toString(DateTimeIvlExpand_DayPrecPerMinute))
define test_DateTimeIvlExpand_DayPrecPerSecond: TestMessage(DateTimeIvlExpand_DayPrecPerSecond is null, 'DateTimeIvlExpand_DayPrecPerSecond', 'null', toString(DateTimeIvlExpand_DayPrecPerSecond))
define test_DateTimeIvlExpand_DayPrecPerMillisecond: TestMessage(DateTimeIvlExpand_DayPrecPerMillisecond is null, 'DateTimeIvlExpand_DayPrecPerMillisecond', 'null', toString(DateTimeIvlExpand_DayPrecPerMillisecond))

define MonthPrecPerYearExpected: { Interval[DateTime(2016,01), DateTime(2016,12)], Interval[DateTime(2017,01), DateTime(2017,12)] }
define MonthPrecPerMonthExpected: { Interval[DateTime(2018,01), DateTime(2018,01)], Interval[DateTime(2018,02), DateTime(2018,02)] }
define DateTimeIvlExpand_MonthPrecPerYear: expand { Interval[DateTime(2016,01), DateTime(2018,01)] } per year
define DateTimeIvlExpand_MonthPrecPerMonth: expand { Interval[DateTime(2018,01), DateTime(2018,02)] } per month
define DateTimeIvlExpand_MonthPrecPerWeek: expand { Interval[DateTime(2018,01), DateTime(2018,01)] } per week
define DateTimeIvlExpand_MonthPrecPerDay: expand { Interval[DateTime(2018,01), DateTime(2018,01)] } per day
define DateTimeIvlExpand_MonthPrecPerHour: expand { Interval[DateTime(2018,01), DateTime(2018,01)] } per hour
define DateTimeIvlExpand_MonthPrecPerMinute: expand { Interval[DateTime(2018,01), DateTime(2018,01)] } per minute
define DateTimeIvlExpand_MonthPrecPerSecond: expand { Interval[DateTime(2018,01), DateTime(2018,01)] } per second
define DateTimeIvlExpand_MonthPrecPerMillisecond: expand { Interval[DateTime(2018,01), DateTime(2018,01)] } per millisecond

define test_DateTimeIvlExpand_MonthPrecPerYear: TestMessage(DateTimeIvlExpand_MonthPrecPerYear = MonthPrecPerYearExpected, 'DateTimeIvlExpand_MonthPrecPerYear', toString(MonthPrecPerYearExpected), toString(DateTimeIvlExpand_MonthPrecPerYear))
define test_DateTimeIvlExpand_MonthPrecPerMonth: TestMessage(DateTimeIvlExpand_MonthPrecPerMonth = MonthPrecPerMonthExpected, 'DateTimeIvlExpand_MonthPrecPerMonth', toString(MonthPrecPerMonthExpected), toString(DateTimeIvlExpand_MonthPrecPerMonth))
define test_DateTimeIvlExpand_MonthPrecPerWeek: TestMessage(DateTimeIvlExpand_MonthPrecPerWeek is null, 'DateTimeIvlExpand_MonthPrecPerWeek', 'null', toString(DateTimeIvlExpand_MonthPrecPerWeek))
define test_DateTimeIvlExpand_MonthPrecPerDay: TestMessage(DateTimeIvlExpand_MonthPrecPerDay is null, 'DateTimeIvlExpand_MonthPrecPerDay', 'null', toString(DateTimeIvlExpand_MonthPrecPerDay))
define test_DateTimeIvlExpand_MonthPrecPerHour: TestMessage(DateTimeIvlExpand_MonthPrecPerHour is null, 'DateTimeIvlExpand_MonthPrecPerHour', 'null', toString(DateTimeIvlExpand_MonthPrecPerHour))
define test_DateTimeIvlExpand_MonthPrecPerMinute: TestMessage(DateTimeIvlExpand_MonthPrecPerMinute is null, 'DateTimeIvlExpand_MonthPrecPerMinute', 'null', toString(DateTimeIvlExpand_MonthPrecPerMinute))
define test_DateTimeIvlExpand_MonthPrecPerSecond: TestMessage(DateTimeIvlExpand_MonthPrecPerSecond is null, 'DateTimeIvlExpand_MonthPrecPerSecond', 'null', toString(DateTimeIvlExpand_MonthPrecPerSecond))
define test_DateTimeIvlExpand_MonthPrecPerMillisecond: TestMessage(DateTimeIvlExpand_MonthPrecPerMillisecond is null, 'DateTimeIvlExpand_MonthPrecPerMillisecond', 'null', toString(DateTimeIvlExpand_MonthPrecPerMillisecond))

define YearPrecPerYearExpected: { Interval[DateTime(2016), DateTime(2016)], Interval[DateTime(2017), DateTime(2017)], Interval[DateTime(2018), DateTime(2018)] }
define DateTimeIvlExpand_YearPrecPerYear: expand { Interval[DateTime(2016), DateTime(2018)] } per year
define DateTimeIvlExpand_YearPrecPerMonth: expand { Interval[DateTime(2018), DateTime(2018)] } per month
define DateTimeIvlExpand_YearPrecPerWeek: expand { Interval[DateTime(2018), DateTime(2018)] } per week
define DateTimeIvlExpand_YearPrecPerDay: expand { Interval[DateTime(2018), DateTime(2018)] } per day
define DateTimeIvlExpand_YearPrecPerHour: expand { Interval[DateTime(2018), DateTime(2018)] } per hour
define DateTimeIvlExpand_YearPrecPerMinute: expand { Interval[DateTime(2018), DateTime(2018)] } per minute
define DateTimeIvlExpand_YearPrecPerSecond: expand { Interval[DateTime(2018), DateTime(2018)] } per second
define DateTimeIvlExpand_YearPrecPerMillisecond: expand { Interval[DateTime(2018), DateTime(2018)] } per millisecond

define test_DateTimeIvlExpand_YearPrecPerYear: TestMessage(DateTimeIvlExpand_YearPrecPerYear = YearPrecPerYearExpected, 'DateTimeIvlExpand_YearPrecPerYear', toString(YearPrecPerYearExpected), toString(DateTimeIvlExpand_YearPrecPerYear))
define test_DateTimeIvlExpand_YearPrecPerMonth: TestMessage(DateTimeIvlExpand_YearPrecPerMonth is null, 'DateTimeIvlExpand_YearPrecPerMonth', 'null', toString(DateTimeIvlExpand_YearPrecPerMonth))
define test_DateTimeIvlExpand_YearPrecPerWeek: TestMessage(DateTimeIvlExpand_YearPrecPerWeek is null, 'DateTimeIvlExpand_YearPrecPerWeek', 'null', toString(DateTimeIvlExpand_YearPrecPerWeek))
define test_DateTimeIvlExpand_YearPrecPerDay: TestMessage(DateTimeIvlExpand_YearPrecPerDay is null, 'DateTimeIvlExpand_YearPrecPerDay', 'null', toString(DateTimeIvlExpand_YearPrecPerDay))
define test_DateTimeIvlExpand_YearPrecPerHour: TestMessage(DateTimeIvlExpand_YearPrecPerHour is null, 'DateTimeIvlExpand_YearPrecPerHour', 'null', toString(DateTimeIvlExpand_YearPrecPerHour))
define test_DateTimeIvlExpand_YearPrecPerMinute: TestMessage(DateTimeIvlExpand_YearPrecPerMinute is null, 'DateTimeIvlExpand_YearPrecPerMinute', 'null', toString(DateTimeIvlExpand_YearPrecPerMinute))
define test_DateTimeIvlExpand_YearPrecPerSecond: TestMessage(DateTimeIvlExpand_YearPrecPerSecond is null, 'DateTimeIvlExpand_YearPrecPerSecond', 'null', toString(DateTimeIvlExpand_YearPrecPerSecond))
define test_DateTimeIvlExpand_YearPrecPerMillisecond: TestMessage(DateTimeIvlExpand_YearPrecPerMillisecond is null, 'DateTimeIvlExpand_YearPrecPerMillisecond', 'null', toString(DateTimeIvlExpand_YearPrecPerMillisecond))

define NullInListExpected: { Interval[@2018-01-01T01+00:00, @2018-01-01T01+00:00] }
define DateTimeOverlappingExpected: { Interval[@2018-01-01T01+00:00, @2018-01-01T01+00:00], Interval[@2018-01-01T02+00:00, @2018-01-01T02+00:00], Interval[@2018-01-01T03+00:00, @2018-01-01T03+00:00], Interval[@2018-01-01T04+00:00, @2018-01-01T04+00:00] }
define DateTimeNonOverlappingExpected: { Interval[@2018-01-01T01+00:00, @2018-01-01T01+00:00], Interval[@2018-01-01T02+00:00, @2018-01-01T02+00:00], Interval[@2018-01-01T05+00:00, @2018-01-01T05+00:00] }
define DateTimeIvlExpand_NullInList: expand { Interval[@2018-01-01T01+00:00, @2018-01-01T01+00:00], null } per hour
define DateTimeIvlExpand_Overlapping: expand { Interval[@2018-01-01T01+00:00, @2018-01-01T03+00:00], Interval[@2018-01-01T02+00:00, @2018-01-01T04+00:00] } per hour
define DateTimeIvlExpand_NonOverlapping: expand { Interval[@2018-01-01T01+00:00, @2018-01-01T02+00:00], Interval[@2018-01-01T05+00:00, @2018-01-01T05+00:00] } per hour

define test_DateTimeIvlExpand_NullInList: TestMessage(DateTimeIvlExpand_NullInList = NullInListExpected, 'DateTimeIvlExpand_NullInList', toString(NullInListExpected), toString(DateTimeIvlExpand_NullInList))
define test_DateTimeIvlExpand_Overlapping: TestMessage(DateTimeIvlExpand_Overlapping = DateTimeOverlappingExpected, 'DateTimeIvlExpand_Overlapping', toString(DateTimeOverlappingExpected), toString(DateTimeIvlExpand_Overlapping))
define test_DateTimeIvlExpand_NonOverlapping: TestMessage(DateTimeIvlExpand_NonOverlapping = DateTimeNonOverlappingExpected, 'DateTimeIvlExpand_NonOverlapping', toString(DateTimeNonOverlappingExpected), toString(DateTimeIvlExpand_NonOverlapping))

define NoPerDefaultHourExpected: { Interval[@2018-01-01T01+00:00, @2018-01-01T01+00:00] }
define NoPerDefaultDayExpected: { Interval[DateTime(2018,01,01), DateTime(2018,01,01)] }
define DateTimeNoPerDefaultMonthExpected: { Interval[DateTime(2018,01), DateTime(2018,01)] }
define DateTimeNoPerDefaultYearExpected: { Interval[DateTime(2018), DateTime(2018)] }
define DateTimeIvlExpand_NoPerDefaultMS: expand { Interval[@2018-01-01T01:00:00.000+00:00, @2018-01-01T01:00:00.001+00:00] }
define DateTimeIvlExpand_NoPerDefaultSec: expand { Interval[@2018-01-01T01:00:00+00:00, @2018-01-01T01:00:01+00:00] }
define DateTimeIvlExpand_NoPerDefaultMin: expand { Interval[@2018-01-01T01:00+00:00, @2018-01-01T01:01+00:00] }
define DateTimeIvlExpand_NoPerDefaultHour: expand { Interval[@2018-01-01T01+00:00, @2018-01-01T01+00:00] }
define DateTimeIvlExpand_NoPerDefaultDay: expand { Interval[DateTime(2018,01,01), DateTime(2018,01,01)] }
define DateTimeIvlExpand_NoPerDefaultMonth: expand { Interval[DateTime(2018,01), DateTime(2018,01)] }
define DateTimeIvlExpand_NoPerDefaultYear: expand { Interval[DateTime(2018), DateTime(2018)] }

define test_DateTimeIvlExpand_NoPerDefaultMS: TestMessage(DateTimeIvlExpand_NoPerDefaultMS = MsPrecPerMillisecondExpected, 'DateTimeIvlExpand_NoPerDefaultMS', toString(MsPrecPerMillisecondExpected), toString(DateTimeIvlExpand_NoPerDefaultMS))
define test_DateTimeIvlExpand_NoPerDefaultSec: TestMessage(DateTimeIvlExpand_NoPerDefaultSec = SecPrecPerSecondExpected, 'DateTimeIvlExpand_NoPerDefaultSec', toString(SecPrecPerSecondExpected), toString(DateTimeIvlExpand_NoPerDefaultSec))
define test_DateTimeIvlExpand_NoPerDefaultMin: TestMessage(DateTimeIvlExpand_NoPerDefaultMin = MinPrecPerMinuteExpected, 'DateTimeIvlExpand_NoPerDefaultMin', toString(MinPrecPerMinuteExpected), toString(DateTimeIvlExpand_NoPerDefaultMin))
define test_DateTimeIvlExpand_NoPerDefaultHour: TestMessage(DateTimeIvlExpand_NoPerDefaultHour = NoPerDefaultHourExpected, 'DateTimeIvlExpand_NoPerDefaultHour', toString(NoPerDefaultHourExpected), toString(DateTimeIvlExpand_NoPerDefaultHour))
define test_DateTimeIvlExpand_NoPerDefaultDay: TestMessage(DateTimeIvlExpand_NoPerDefaultDay = NoPerDefaultDayExpected, 'DateTimeIvlExpand_NoPerDefaultDay', toString(NoPerDefaultDayExpected), toString(DateTimeIvlExpand_NoPerDefaultDay))
define test_DateTimeIvlExpand_NoPerDefaultMonth: TestMessage(DateTimeIvlExpand_NoPerDefaultMonth = DateTimeNoPerDefaultMonthExpected, 'DateTimeIvlExpand_NoPerDefaultMonth', toString(DateTimeNoPerDefaultMonthExpected), toString(DateTimeIvlExpand_NoPerDefaultMonth))
define test_DateTimeIvlExpand_NoPerDefaultYear: TestMessage(DateTimeIvlExpand_NoPerDefaultYear = DateTimeNoPerDefaultYearExpected, 'DateTimeIvlExpand_NoPerDefaultYear', toString(DateTimeNoPerDefaultYearExpected), toString(DateTimeIvlExpand_NoPerDefaultYear))

define DateTimeOpenStartExpected: { Interval[@2018-01-01T02+00:00, @2018-01-02T01+00:00], Interval[@2018-01-02T02+00:00, @2018-01-03T01+00:00] }
define DateTimeOpenEndExpected: { Interval[@2018-01-01T01+00:00, @2018-01-02T00+00:00], Interval[@2018-01-02T01+00:00, @2018-01-03T00+00:00] }
define DateTimeOpenBothExpected: { Interval[@2018-01-01T02+00:00, @2018-01-02T01+00:00] }
define DateTimeIvlExpand_OpenStart: expand { Interval(@2018-01-01T01+00:00, @2018-01-03T01+00:00] } per day
define DateTimeIvlExpand_OpenEnd: expand { Interval[@2018-01-01T01+00:00, @2018-01-03T01+00:00) } per day
define DateTimeIvlExpand_OpenBoth: expand { Interval(@2018-01-01T01+00:00, @2018-01-03T01+00:00) } per day

define test_DateTimeIvlExpand_OpenStart: TestMessage(DateTimeIvlExpand_OpenStart = DateTimeOpenStartExpected, 'DateTimeIvlExpand_OpenStart', toString(DateTimeOpenStartExpected), toString(DateTimeIvlExpand_OpenStart))
define test_DateTimeIvlExpand_OpenEnd: TestMessage(DateTimeIvlExpand_OpenEnd = DateTimeOpenEndExpected, 'DateTimeIvlExpand_OpenEnd', toString(DateTimeOpenEndExpected), toString(DateTimeIvlExpand_OpenEnd))
define test_DateTimeIvlExpand_OpenBoth: TestMessage(DateTimeIvlExpand_OpenBoth = DateTimeOpenBothExpected, 'DateTimeIvlExpand_OpenBoth', toString(DateTimeOpenBothExpected), toString(DateTimeIvlExpand_OpenBoth))

define DateTimeMismatchPrecisionExpected: { Interval[@2012-01-01T12:00+00:00, @2012-01-02T11:59+00:00] }
define DateTimeIvlExpand_MismatchPrecision: expand { Interval[@2012-01-01T12:00+00:00, @2012-01-02T12:00:00+00:00] } per day
define DateTimeIvlExpand_MismatchPrecisionEmpty: expand { Interval[@2012-01-01T13:00:00+00:00, @2012-01-02T12:59+00:00] } per day

define test_DateTimeIvlExpand_MismatchPrecision: TestMessage(DateTimeIvlExpand_MismatchPrecision = DateTimeMismatchPrecisionExpected, 'DateTimeIvlExpand_MismatchPrecision', toString(DateTimeMismatchPrecisionExpected), toString(DateTimeIvlExpand_MismatchPrecision))
define test_DateTimeIvlExpand_MismatchPrecisionEmpty: TestMessage(not exists DateTimeIvlExpand_MismatchPrecisionEmpty, 'DateTimeIvlExpand_MismatchPrecisionEmpty', toString(List<Interval<Date>>{}), toString(DateTimeIvlExpand_MismatchPrecisionEmpty))

define DateTimeIvlExpand_NullOpen: expand { Interval[null, @2018-01-03T01+00:00] } per day
define DateTimeIvlExpand_NullClose: expand { Interval[@2018-01-01T01+00:00, null] } per day
// TODO: should be { Interval[null as DateTime, null] } but that currently evaluates to null in the engine, uncomment once fixed
//define DateTimeIvlExpand_NullBoth: expand { Interval[null, null] } per day
define DateTimeIvlExpand_BadPerGram: expand { Interval[@2018-01-01T01+00:00, @2018-01-04T01+00:00] } per 1 'g'

define test_DateTimeIvlExpand_NullOpen: TestMessage(DateTimeIvlExpand_NullOpen is null, 'DateTimeIvlExpand_NullOpen', 'null', toString(DateTimeIvlExpand_NullOpen))
define test_DateTimeIvlExpand_NullClose: TestMessage(DateTimeIvlExpand_NullClose is null, 'DateTimeIvlExpand_NullClose', 'null', toString(DateTimeIvlExpand_NullClose))
//define test_DateTimeIvlExpand_NullBoth: TestMessage(DateTimeIvlExpand_NullBoth is null, 'DateTimeIvlExpand_NullBoth', 'null', toString(DateTimeIvlExpand_NullBoth))
define test_DateTimeIvlExpand_BadPerGram: TestMessage(DateTimeIvlExpand_BadPerGram is null, 'DateTimeIvlExpand_BadPerGram', 'null', toString(DateTimeIvlExpand_BadPerGram))

// TimeIntervalExpand
define TimeMsPrecPerHourExpected: { Interval[@T01:00:00.000, @T01:59:59.999], Interval[@T02:00:00.000, @T02:59:59.999] }
define TimeMsPrecPerMinuteExpected: { Interval[@T01:00:00.000, @T01:00:59.999], Interval[@T01:01:00.000, @T01:01:59.999] }
define TimeMsPrecPerSecondExpected: { Interval[@T01:00:00.000, @T01:00:00.999], Interval[@T01:00:01.000, @T01:00:01.999] }
define TimeMsPrecPerMillisecondExpected: { Interval[@T01:00:00.000, @T01:00:00.000], Interval[@T01:00:00.001, @T01:00:00.001] }
define TimeIvlExpand_MsPrecPerHour: expand { Interval[@T01:00:00.000, @T03:00:00.000] } per hour
define TimeIvlExpand_MsPrecPerMinute: expand { Interval[@T01:00:00.000, @T01:02:00.000] } per minute
define TimeIvlExpand_MsPrecPerSecond: expand { Interval[@T01:00:00.000, @T01:00:02.000] } per second
define TimeIvlExpand_MsPrecPerMillisecond: expand { Interval[@T01:00:00.000, @T01:00:00.001] } per millisecond

define test_TimeIvlExpand_MsPrecPerHour: TestMessage(TimeIvlExpand_MsPrecPerHour = TimeMsPrecPerHourExpected, 'TimeIvlExpand_MsPrecPerHour', toString(TimeMsPrecPerHourExpected), toString(TimeIvlExpand_MsPrecPerHour))
define test_TimeIvlExpand_MsPrecPerMinute: TestMessage(TimeIvlExpand_MsPrecPerMinute = TimeMsPrecPerMinuteExpected, 'TimeIvlExpand_MsPrecPerMinute', toString(TimeMsPrecPerMinuteExpected), toString(TimeIvlExpand_MsPrecPerMinute))
define test_TimeIvlExpand_MsPrecPerSecond: TestMessage(TimeIvlExpand_MsPrecPerSecond = TimeMsPrecPerSecondExpected, 'TimeIvlExpand_MsPrecPerSecond', toString(TimeMsPrecPerSecondExpected), toString(TimeIvlExpand_MsPrecPerSecond))
define test_TimeIvlExpand_MsPrecPerMillisecond: TestMessage(TimeIvlExpand_MsPrecPerMillisecond = TimeMsPrecPerMillisecondExpected, 'TimeIvlExpand_MsPrecPerMillisecond', toString(TimeMsPrecPerMillisecondExpected), toString(TimeIvlExpand_MsPrecPerMillisecond))

define TimeSecPrecPerHourExpected: { Interval[@T01:00:00, @T01:59:59], Interval[@T02:00:00, @T02:59:59] }
define TimeSecPrecPerMinuteExpected: { Interval[@T01:00:00, @T01:00:59], Interval[@T01:01:00, @T01:01:59] }
define TimeSecPrecPerSecondExpected: { Interval[@T01:00:00, @T01:00:00], Interval[@T01:00:01, @T01:00:01] }
define TimeIvlExpand_SecPrecPerHour: expand { Interval[@T01:00:00, @T03:00:00] } per hour
define TimeIvlExpand_SecPrecPerMinute: expand { Interval[@T01:00:00, @T01:02:00] } per minute
define TimeIvlExpand_SecPrecPerSecond: expand { Interval[@T01:00:00, @T01:00:01] } per second
define TimeIvlExpand_SecPrecPerMillisecond: expand { Interval[@T01:00:00, @T01:00:00] } per millisecond

define test_TimeIvlExpand_SecPrecPerHour: TestMessage(TimeIvlExpand_SecPrecPerHour = TimeSecPrecPerHourExpected, 'TimeIvlExpand_SecPrecPerHour', toString(TimeSecPrecPerHourExpected), toString(TimeIvlExpand_SecPrecPerHour))
define test_TimeIvlExpand_SecPrecPerMinute: TestMessage(TimeIvlExpand_SecPrecPerMinute = TimeSecPrecPerMinuteExpected, 'TimeIvlExpand_SecPrecPerMinute', toString(TimeSecPrecPerMinuteExpected), toString(TimeIvlExpand_SecPrecPerMinute))
define test_TimeIvlExpand_SecPrecPerSecond: TestMessage(TimeIvlExpand_SecPrecPerSecond = TimeSecPrecPerSecondExpected, 'TimeIvlExpand_SecPrecPerSecond', toString(TimeSecPrecPerSecondExpected), toString(TimeIvlExpand_SecPrecPerSecond))
define test_TimeIvlExpand_SecPrecPerMillisecond: TestMessage(TimeIvlExpand_SecPrecPerMillisecond is null, 'TimeIvlExpand_SecPrecPerMillisecond', 'null', toString(TimeIvlExpand_SecPrecPerMillisecond))

define TimeMinPrecPerHourExpected: { Interval[@T01:00, @T01:59], Interval[@T02:00, @T02:59] }
define TimeMinPrecPerMinuteExpected: { Interval[@T01:00, @T01:00], Interval[@T01:01, @T01:01] }
define TimeIvlExpand_MinPrecPerHour: expand { Interval[@T01:00, @T03:00] } per hour
define TimeIvlExpand_MinPrecPerMinute: expand { Interval[@T01:00, @T01:01] } per minute
define TimeIvlExpand_MinPrecPerSecond: expand { Interval[@T01:00, @T01:00] } per second
define TimeIvlExpand_MinPrecPerMillisecond: expand { Interval[@T01:00, @T01:00] } per millisecond

define test_TimeIvlExpand_MinPrecPerHour: TestMessage(TimeIvlExpand_MinPrecPerHour = TimeMinPrecPerHourExpected, 'TimeIvlExpand_MinPrecPerHour', toString(TimeMinPrecPerHourExpected), toString(TimeIvlExpand_MinPrecPerHour))
define test_TimeIvlExpand_MinPrecPerMinute: TestMessage(TimeIvlExpand_MinPrecPerMinute = TimeMinPrecPerMinuteExpected, 'TimeIvlExpand_MinPrecPerMinute', toString(TimeMinPrecPerMinuteExpected), toString(TimeIvlExpand_MinPrecPerMinute))
define test_TimeIvlExpand_MinPrecPerSecond: TestMessage(TimeIvlExpand_MinPrecPerSecond is null, 'TimeIvlExpand_MinPrecPerSecond', 'null', toString(TimeIvlExpand_MinPrecPerSecond))
define test_TimeIvlExpand_MinPrecPerMillisecond: TestMessage(TimeIvlExpand_MinPrecPerMillisecond is null, 'TimeIvlExpand_MinPrecPerMillisecond', 'null', toString(TimeIvlExpand_MinPrecPerMillisecond))

define TimeHourPrecPerHourExpected: { Interval[@T01, @T01], Interval[@T02, @T02] }
define TimeIvlExpand_HourPrecPerHour: expand { Interval[@T01, @T02] } per hour
define TimeIvlExpand_HourPrecPerMinute: expand { Interval[@T01, @T01] } per minute
define TimeIvlExpand_HourPrecPerSecond: expand { Interval[@T01, @T01] } per second
define TimeIvlExpand_HourPrecPerMillisecond: expand { Interval[@T01, @T01] } per millisecond

define test_TimeIvlExpand_HourPrecPerHour: TestMessage(TimeIvlExpand_HourPrecPerHour = TimeHourPrecPerHourExpected, 'TimeIvlExpand_HourPrecPerHour', toString(TimeHourPrecPerHourExpected), toString(TimeIvlExpand_HourPrecPerHour))
define test_TimeIvlExpand_HourPrecPerMinute: TestMessage(TimeIvlExpand_HourPrecPerMinute is null, 'TimeIvlExpand_HourPrecPerMinute', 'null', toString(TimeIvlExpand_HourPrecPerMinute))
define test_TimeIvlExpand_HourPrecPerSecond: TestMessage(TimeIvlExpand_HourPrecPerSecond is null, 'TimeIvlExpand_HourPrecPerSecond', 'null', toString(TimeIvlExpand_HourPrecPerSecond))
define test_TimeIvlExpand_HourPrecPerMillisecond: TestMessage(TimeIvlExpand_HourPrecPerMillisecond is null, 'TimeIvlExpand_HourPrecPerMillisecond', 'null', toString(TimeIvlExpand_HourPrecPerMillisecond))

// QuantityIntervalExpand
// TODO: Bonnie expects this:
//define ClosedSingleGPerGExpected: { Interval[2 'g', 2 'g'], Interval[3 'g', 3 'g'], Interval[4 'g', 4 'g'] }
// This seems like the expected behavior and is more in line with how the date expand works
define ClosedSingleGPerGExpected: { Interval[2 'g', 2.99999999 'g'], Interval[3 'g', 3.99999999 'g'] }
define ClosedSingleGPerGDecimalExpected: { Interval[2.1 'g', 3.09999999 'g'], Interval[3.1 'g', 4.09999999 'g'] }
define ClosedSingleMGPerMGTruncExpected: { Interval[2000 'mg', 2799.99999999 'mg'], Interval[2800 'mg', 3599.99999999 'mg'], Interval[3600 'mg', 4399.99999999 'mg'] }
define ClosedSingleMGPerMGDecimalExpected: { Interval[2000.01 'mg', 2800.00999999 'mg'], Interval[2800.01 'mg', 3600.00999999 'mg'], Interval[3600.01 'mg', 4400.00999999 'mg'] }
define QtyIvlExpand_ClosedSingleGPerG: expand { Interval[2 'g', 4 'g'] } per 1 'g'
define QtyIvlExpand_ClosedSingleGPerGDecimal: expand { Interval[2.1 'g', 4.1 'g'] } per 1 'g'
// TODO: uncomment once UCUM service is implemented
//define QtyIvlExpand_ClosedSingleGPerMG: expand { Interval[2 'g', 2.003 'g'] } per 1 'mg'
//define QtyIvlExpand_ClosedSingleMGPerGTrunc: expand { Interval[2999 'mg', 4200 'mg'] } per 1 'g'
define QtyIvlExpand_ClosedSingleMGPerMGTrunc: expand { Interval[2000 'mg', 4500 'mg'] } per 800 'mg'
define QtyIvlExpand_ClosedSingleMGPerMGDecimal: expand { Interval[2000.01 'mg', 4500 'mg'] } per 800 'mg'

define test_QtyIvlExpand_ClosedSingleGPerG: TestMessage(QtyIvlExpand_ClosedSingleGPerG = ClosedSingleGPerGExpected, 'QtyIvlExpand_ClosedSingleGPerG', toString(ClosedSingleGPerGExpected), toString(QtyIvlExpand_ClosedSingleGPerG))
define test_QtyIvlExpand_ClosedSingleGPerGDecimal: TestMessage(QtyIvlExpand_ClosedSingleGPerGDecimal = ClosedSingleGPerGDecimalExpected, 'QtyIvlExpand_ClosedSingleGPerGDecimal', toString(ClosedSingleGPerGDecimalExpected), toString(QtyIvlExpand_ClosedSingleGPerGDecimal))
define test_QtyIvlExpand_ClosedSingleMGPerMGTrunc: TestMessage(QtyIvlExpand_ClosedSingleMGPerMGTrunc = ClosedSingleMGPerMGTruncExpected, 'QtyIvlExpand_ClosedSingleMGPerMGTrunc', toString(ClosedSingleMGPerMGTruncExpected), toString(QtyIvlExpand_ClosedSingleMGPerMGTrunc))
define test_QtyIvlExpand_ClosedSingleMGPerMGDecimal: TestMessage(QtyIvlExpand_ClosedSingleMGPerMGDecimal = ClosedSingleMGPerMGDecimalExpected, 'QtyIvlExpand_ClosedSingleMGPerMGDecimal', toString(ClosedSingleMGPerMGDecimalExpected), toString(QtyIvlExpand_ClosedSingleMGPerMGDecimal))

define QtyIvlOverlappingExpected: { Interval[2 'g', 2.99999999 'g'], Interval[3 'g', 3.99999999 'g'], Interval[4 'g', 4.99999999 'g'] }
define QtyIvlNonOverlappingExpected: { Interval[2 'g', 2.99999999 'g'], Interval[3 'g', 3.99999999 'g'], Interval[6 'g', 6 'g'] }
define QtyIvlExpand_NullInList: expand { Interval[2 'g', 4 'g'], null } per 1 'g'
define QtyIvlExpand_Overlapping: expand { Interval[2 'g', 4 'g'], Interval[3 'g', 5 'g'] } per 1 'g'
define QtyIvlExpand_NonOverlapping: expand { Interval[2 'g', 4 'g'], Interval[6 'g', 6 'g'] } per 1 'g'

define test_QtyIvlExpand_NullInList: TestMessage(QtyIvlExpand_NullInList = ClosedSingleGPerGExpected, 'QtyIvlExpand_NullInList', toString(ClosedSingleGPerGExpected), toString(QtyIvlExpand_NullInList))
define test_QtyIvlExpand_Overlapping: TestMessage(QtyIvlExpand_Overlapping = QtyIvlOverlappingExpected, 'QtyIvlExpand_Overlapping', toString(QtyIvlOverlappingExpected), toString(QtyIvlExpand_Overlapping))
define test_QtyIvlExpand_NonOverlapping: TestMessage(QtyIvlExpand_NonOverlapping = QtyIvlNonOverlappingExpected, 'QtyIvlExpand_NonOverlapping', toString(QtyIvlNonOverlappingExpected), toString(QtyIvlExpand_NonOverlapping))

// TODO: uncomment once UCUM service is implemented
//define QtyIvlExpand_NoPerDefaultM: expand { Interval[2 'm', 400 'cm'] }
define QtyIvlExpand_NoPerDefaultG: expand { Interval[2 'g', 4 'g'] }

define test_QtyIvlExpand_NoPerDefaultG: TestMessage(QtyIvlExpand_NoPerDefaultG = ClosedSingleGPerGExpected, 'QtyIvlExpand_NoPerDefaultG', toString(ClosedSingleGPerGExpected), toString(QtyIvlExpand_NoPerDefaultG))

// NOTE: Bonnie expects { Interval[3 'g', 3 'g'], Interval[4 'g', 4 'g'] }
define QtyIvlOpenStartExpected: { Interval[2.00000001 'g', 3 'g'], Interval[3.00000001 'g', 4 'g'] }
define QtyIvlOpenEndExpected: { Interval[2 'g', 2.99999999 'g'], Interval[3 'g', 3.99999999 'g'] }
define QtyIvlOpenBothExpected: { Interval[2.00000001 'g', 3 'g'] }
define QtyIvlOpenBothDecimalExpected: { Interval[2.10000001 'g', 3.1 'g'] }
define QtyIvlOpenBothDecimalTruncExpected: { Interval[2.10000001 'g', 3.1 'g'], Interval[3.10000001 'g', 4.1 'g'] }
define QtyIvlExpand_OpenStart: expand { Interval(2 'g', 4 'g'] } per 1 'g'
define QtyIvlExpand_OpenEnd: expand { Interval[2 'g', 4 'g') } per 1 'g'
define QtyIvlExpand_OpenBoth: expand { Interval(2 'g', 4 'g') } per 1 'g'
define QtyIvlExpand_OpenBothDecimal: expand { Interval(2.1 'g', 4.1 'g') } per 1 'g'
define QtyIvlExpand_OpenBothDecimalTrunc: expand { Interval(2.1 'g', 4.101 'g') } per 1 'g'

define test_QtyIvlExpand_OpenStart: TestMessage(QtyIvlExpand_OpenStart = QtyIvlOpenStartExpected, 'QtyIvlExpand_OpenStart', toString(QtyIvlOpenStartExpected), toString(QtyIvlExpand_OpenStart))
define test_QtyIvlExpand_OpenEnd: TestMessage(QtyIvlExpand_OpenEnd = QtyIvlOpenEndExpected, 'QtyIvlExpand_OpenEnd', toString(QtyIvlOpenEndExpected), toString(QtyIvlExpand_OpenEnd))
define test_QtyIvlExpand_OpenBoth: TestMessage(QtyIvlExpand_OpenBoth = QtyIvlOpenBothExpected, 'QtyIvlExpand_OpenBoth', toString(QtyIvlOpenBothExpected), toString(QtyIvlExpand_OpenBoth))
define test_QtyIvlExpand_OpenBothDecimal: TestMessage(QtyIvlExpand_OpenBothDecimal = QtyIvlOpenBothDecimalExpected, 'QtyIvlExpand_OpenBothDecimal', toString(QtyIvlOpenBothDecimalExpected), toString(QtyIvlExpand_OpenBothDecimal))
define test_QtyIvlExpand_OpenBothDecimalTrunc: TestMessage(QtyIvlExpand_OpenBothDecimalTrunc = QtyIvlOpenBothDecimalTruncExpected, 'QtyIvlExpand_OpenBothDecimalTrunc', toString(QtyIvlOpenBothDecimalTruncExpected), toString(QtyIvlExpand_OpenBothDecimalTrunc))

define QtyIvlExpand_PerTooBig: expand { Interval[2 'g', 4 'g'], null } per 5 'g'
define QtyIvlExpand_NullOpen: expand { Interval[null, 4 'g'] } per 1 'g'
define QtyIvlExpand_NullClose: expand { Interval[2 'g', null] } per 1 'g'
// TODO: should be { Interval[null as Quantity, null] } but that currently evaluates to null in the engine, uncomment once fixed
//define QtyIvlExpand_NullBoth: expand { Interval[null, null] } per 1 'g'
// TODO: uncomment once UCUM service is implemented
//define QtyIvlExpand_BadPerMinute: expand { Interval(2 'g', 4 'g'] } per 1 minute
//define QtyIvlExpand_BadPerGram: expand { Interval(2 'km', 4 'km'] }  per 1 'g'

define test_QtyIvlExpand_PerTooBig: TestMessage(not exists QtyIvlExpand_PerTooBig, 'QtyIvlExpand_PerTooBig', toString(List<Interval<Quantity>>{}), toString(QtyIvlExpand_PerTooBig))
define test_QtyIvlExpand_NullOpen: TestMessage(QtyIvlExpand_NullOpen is null, 'QtyIvlExpand_NullOpen', 'null', toString(QtyIvlExpand_NullOpen))
define test_QtyIvlExpand_NullClose: TestMessage(QtyIvlExpand_NullClose is null, 'QtyIvlExpand_NullClose', 'null', toString(QtyIvlExpand_NullClose))
//define test_QtyIvlExpand_NullBoth: TestMessage(QtyIvlExpand_NullBoth is null, 'QtyIvlExpand_NullBoth', 'null', toString(QtyIvlExpand_NullBoth))

// IntegerIntervalExpand
define IntIvlClosedSinglePer1Expected: { Interval[2, 2], Interval[3, 3], Interval[4, 4] }
define IntIvlClosedSinglePer3Expected: { Interval[2, 4], Interval[5, 7], Interval[8, 10] }
define IntIvlClosedSinglePer3NoteTheWidthExpected: { Interval[2, 4] }
define IntIvlExpand_ClosedSinglePer1: expand { Interval[2, 4] } per 1 '1'
define IntIvlExpand_ClosedSinglePer3: expand { Interval[2, 10] } per 3 '1'
define IntIvlExpand_ClosedSinglePer3NoteTheWidth: expand { Interval[2, 4] } per 3 '1'

define test_IntIvlExpand_ClosedSinglePer1: TestMessage(IntIvlExpand_ClosedSinglePer1 = IntIvlClosedSinglePer1Expected, 'IntIvlExpand_ClosedSinglePer1', toString(IntIvlClosedSinglePer1Expected), toString(IntIvlExpand_ClosedSinglePer1))
define test_IntIvlExpand_ClosedSinglePer3: TestMessage(IntIvlExpand_ClosedSinglePer3 = IntIvlClosedSinglePer3Expected, 'IntIvlExpand_ClosedSinglePer3', toString(IntIvlClosedSinglePer3Expected), toString(IntIvlExpand_ClosedSinglePer3))
define test_IntIvlExpand_ClosedSinglePer3NoteTheWidth: TestMessage(IntIvlExpand_ClosedSinglePer3NoteTheWidth = IntIvlClosedSinglePer3NoteTheWidthExpected, 'IntIvlExpand_ClosedSinglePer3NoteTheWidth', toString(IntIvlClosedSinglePer3NoteTheWidthExpected), toString(IntIvlExpand_ClosedSinglePer3NoteTheWidth))

define IntIvlOverlappingExpected: { Interval[2, 2], Interval[3, 3], Interval[4, 4], Interval[5, 5] }
define IntIvlNonOverlappingExpected: { Interval[2, 2], Interval[3, 3], Interval[4, 4], Interval[6, 6] }
define IntIvlExpand_NullInList: expand { Interval[2, 4], null } per 1 '1'
define IntIvlExpand_Overlapping: expand { Interval[2, 4], Interval[3, 5] } per 1 '1'
define IntIvlExpand_NonOverlapping: expand { Interval[2, 4], Interval[6, 6] } per 1 '1'

define test_IntIvlExpand_NullInList: TestMessage(IntIvlExpand_NullInList = IntIvlClosedSinglePer1Expected, 'IntIvlExpand_NullInList', toString(IntIvlClosedSinglePer1Expected), toString(IntIvlExpand_NullInList))
define test_IntIvlExpand_Overlapping: TestMessage(IntIvlExpand_Overlapping = IntIvlOverlappingExpected, 'IntIvlExpand_Overlapping', toString(IntIvlOverlappingExpected), toString(IntIvlExpand_Overlapping))
define test_IntIvlExpand_NonOverlapping: TestMessage(IntIvlExpand_NonOverlapping = IntIvlNonOverlappingExpected, 'IntIvlExpand_NonOverlapping', toString(IntIvlNonOverlappingExpected), toString(IntIvlExpand_NonOverlapping))

define IntIvlExpand_NoPer: expand { Interval[2, 4] }

define test_IntIvlExpand_NoPer: TestMessage(IntIvlExpand_NoPer = IntIvlClosedSinglePer1Expected, 'IntIvlExpand_NoPer', toString(IntIvlClosedSinglePer1Expected), toString(IntIvlExpand_NoPer))

define IntIvlOpenStartExpected: { Interval[3, 3], Interval[4, 4] }
define IntIvlOpenEndExpected: { Interval[2, 2], Interval[3, 3] }
define IntIvlOpenBothExpected: { Interval[3, 3] }
define IntIvlExpand_OpenStart: expand { Interval(2, 4] } per 1 '1'
define IntIvlExpand_OpenEnd: expand { Interval[2, 4) } per 1 '1'
define IntIvlExpand_OpenBoth: expand { Interval(2, 4) } per 1 '1'

define test_IntIvlExpand_OpenStart: TestMessage(IntIvlExpand_OpenStart = IntIvlOpenStartExpected, 'IntIvlExpand_OpenStart', toString(IntIvlOpenStartExpected), toString(IntIvlExpand_OpenStart))
define test_IntIvlExpand_OpenEnd: TestMessage(IntIvlExpand_OpenEnd = IntIvlOpenEndExpected, 'IntIvlExpand_OpenEnd', toString(IntIvlOpenEndExpected), toString(IntIvlExpand_OpenEnd))
define test_IntIvlExpand_OpenBoth: TestMessage(IntIvlExpand_OpenBoth = IntIvlOpenBothExpected, 'IntIvlExpand_OpenBoth', toString(IntIvlOpenBothExpected), toString(IntIvlExpand_OpenBoth))

define IntIvlExpand_PerTooBig: expand { Interval[2, 4], null } per 5 '1'
define IntIvlExpand_NullOpen: expand { Interval[null, 4] } per 1 '1'
define IntIvlExpand_NullClose: expand { Interval[2, null] } per 1 '1'
// TODO: should be { Interval[null as Integer, null] } but that currently evaluates to null in the engine, uncomment once fixed
//define IntIvlExpand_NullBoth: expand { Interval[null, null] } per 1 '1'
define IntIvlExpand_BadPerMinute: expand { Interval(2, 4] } per 1 minute

define test_IntIvlExpand_PerTooBig: TestMessage(not exists IntIvlExpand_PerTooBig, 'IntIvlExpand_PerTooBig', toString(List<Interval<Integer>>{}), toString(IntIvlExpand_PerTooBig))
define test_IntIvlExpand_NullOpen: TestMessage(IntIvlExpand_NullOpen is null, 'IntIvlExpand_NullOpen', 'null', toString(IntIvlExpand_NullOpen))
define test_IntIvlExpand_NullClose: TestMessage(IntIvlExpand_NullClose is null, 'IntIvlExpand_NullClose', 'null', toString(IntIvlExpand_NullClose))
define test_IntIvlExpand_BadPerMinute: TestMessage(IntIvlExpand_BadPerMinute is null, 'IntIvlExpand_BadPerMinute', 'null', toString(IntIvlExpand_BadPerMinute))

// DecimalIntervalExpand
define DecIvlClosedSingleExpected: { Interval[2, 3.49999999], Interval[3.5, 4.99999999] }
define DecIvlClosedSingle1Expected: { Interval[2.5, 4.49999999], Interval[4.5, 6.49999999], Interval[6.5, 8.49999999] }
define DecIvlClosedSingle2Expected: { Interval[2, 2.49999999], Interval[2.5, 2.99999999], Interval[3, 3.49999999], Interval[3.5, 3.99999999], Interval[4, 4.49999999] }
// NOTE: Bonnie has Interval[2, 5], but that results in an integer interval with a 1 '1' per
define DecIvlExpand_ClosedSingle: expand { Interval[2.0, 5] } per 1.5 '1'
define DecIvlExpand_ClosedSingle1: expand { Interval[2.5, 10] } per 2 '1'
define DecIvlExpand_ClosedSingle2: expand { Interval[2, 4.5] } per 0.5 '1'

define test_DecIvlExpand_ClosedSingle: TestMessage(DecIvlExpand_ClosedSingle = DecIvlClosedSingleExpected, 'DecIvlExpand_ClosedSingle', toString(DecIvlClosedSingleExpected), toString(DecIvlExpand_ClosedSingle))
define test_DecIvlExpand_ClosedSingle1: TestMessage(DecIvlExpand_ClosedSingle1 = DecIvlClosedSingle1Expected, 'DecIvlExpand_ClosedSingle1', toString(DecIvlClosedSingle1Expected), toString(DecIvlExpand_ClosedSingle1))
define test_DecIvlExpand_ClosedSingle2: TestMessage(DecIvlExpand_ClosedSingle2 = DecIvlClosedSingle2Expected, 'DecIvlExpand_ClosedSingle2', toString(DecIvlClosedSingle2Expected), toString(DecIvlExpand_ClosedSingle2))

// NOTE: Bonnie has Interval[2, 5], but that results in an integer interval with a 1 '1' per
define DecIvlOverlappingExpected: { Interval[2, 3.49999999], Interval[3.5, 4.99999999], Interval[5, 6.49999999] }
define DecIvlNonOverlappingExpected: { Interval[2, 3.49999999], Interval[6, 7.49999999] }
define DecIvlExpand_NullInList: expand { Interval[2.0, 5], null } per 1.5 '1'
define DecIvlExpand_Overlapping: expand { Interval[2.0, 5], Interval[4.0, 7] } per 1.5 '1'
define DecIvlExpand_NonOverlapping: expand { Interval[2.0, 4], Interval[6.0, 8] } per 1.5 '1'

define test_DecIvlExpand_NullInList: TestMessage(DecIvlExpand_NullInList = DecIvlClosedSingleExpected, 'DecIvlExpand_NullInList', toString(DecIvlClosedSingleExpected), toString(DecIvlExpand_NullInList))
define test_DecIvlExpand_Overlapping: TestMessage(DecIvlExpand_Overlapping = DecIvlOverlappingExpected, 'DecIvlExpand_Overlapping', toString(DecIvlOverlappingExpected), toString(DecIvlExpand_Overlapping))
define test_DecIvlExpand_NonOverlapping: TestMessage(DecIvlExpand_NonOverlapping = DecIvlNonOverlappingExpected, 'DecIvlExpand_NonOverlapping', toString(DecIvlNonOverlappingExpected), toString(DecIvlExpand_NonOverlapping))

define DecIvlNoPerExpected: { Interval[2.5, 3.49999999], Interval[3.5, 4.49999999] }
define DecIvlExpand_NoPer: expand { Interval[2.5, 4.5] }

define test_DecIvlExpand_NoPer: TestMessage(DecIvlExpand_NoPer = DecIvlNoPerExpected, 'DecIvlExpand_NoPer', toString(DecIvlNoPerExpected), toString(DecIvlExpand_NoPer))

define DecIvlOpenStartExpected: { Interval[2.00000001, 3.5], Interval[3.50000001, 5] }
define DecIvlOpenEndExpected: { Interval[2, 3.49999999], Interval[3.5, 4.99999999] }
define DecIvlOpenBothExpected: { Interval[2.00000001, 3.5] }
define DecIvlExpand_OpenStart: expand { Interval(2.0, 5] } per 1.5 '1'
define DecIvlExpand_OpenEnd: expand { Interval[2.0, 5) } per 1.5 '1'
define DecIvlExpand_OpenBoth: expand { Interval(2.0, 5) } per 1.5 '1'

define test_DecIvlExpand_OpenStart: TestMessage(DecIvlExpand_OpenStart = DecIvlOpenStartExpected, 'DecIvlExpand_OpenStart', toString(DecIvlOpenStartExpected), toString(DecIvlExpand_OpenStart))
define test_DecIvlExpand_OpenEnd: TestMessage(DecIvlExpand_OpenEnd = DecIvlOpenEndExpected, 'DecIvlExpand_OpenEnd', toString(DecIvlOpenEndExpected), toString(DecIvlExpand_OpenEnd))
define test_DecIvlExpand_OpenBoth: TestMessage(DecIvlExpand_OpenBoth = DecIvlOpenBothExpected, 'DecIvlExpand_OpenBoth', toString(DecIvlOpenBothExpected), toString(DecIvlExpand_OpenBoth))

define DecIvlExpand_PerTooBig: expand { Interval[2, 4], null } per 5.5 '1'
define DecIvlExpand_NullOpen: expand { Interval[null, 4] } per 1.5 '1'
define DecIvlExpand_NullClose: expand { Interval[2, null] } per 1.5 '1'
// TODO: should be { Interval[null as Decimal, null] } but that currently evaluates to null in the engine, uncomment once fixed
//define DecIvlExpand_NullBoth: expand { Interval[null, null] } per 1.5 '1'
define DecIvlExpand_BadPerMinute: expand { Interval(2.1, 4.1] } per 0.5 minute

define test_DecIvlExpand_PerTooBig: TestMessage(not exists DecIvlExpand_PerTooBig, 'DecIvlExpand_PerTooBig', toString(List<Interval<Decimal>>{}), toString(DecIvlExpand_PerTooBig))
define test_DecIvlExpand_NullOpen: TestMessage(DecIvlExpand_NullOpen is null, 'DecIvlExpand_NullOpen', 'null', toString(DecIvlExpand_NullOpen))
define test_DecIvlExpand_NullClose: TestMessage(DecIvlExpand_NullClose is null, 'DecIvlExpand_NullClose', 'null', toString(DecIvlExpand_NullClose))
define test_DecIvlExpand_BadPerMinute: TestMessage(DecIvlExpand_BadPerMinute is null, 'DecIvlExpand_BadPerMinute', 'null', toString(DecIvlExpand_BadPerMinute))

// Interval SameAs
// TODO: should be Interval[null as DateTime, null] but that currently evaluates to null (i.e. null as Interval<DateTime>) in the engine, uncomment once fixed
//define SameAsIvl_NullBoth: Interval[null,null] same as Interval[null,null]
//define SameAsIvl_NullOne: Interval[DateTime(2018,01,01), DateTime(2018,02,02)] same as Interval[null,null]
define SameAsIvl_Equal: Interval[DateTime(2018,01,01), DateTime(2018,01,01)] same as Interval[DateTime(2018,01,01), DateTime(2018,01,01)]
define SameAsIvl_NotEqual: Interval[DateTime(2018,01,01), DateTime(2018,01,01)] same as Interval[DateTime(2018,02,01), DateTime(2018,05,01)]
define SameAsIvl_DateTimeAndDateComparisonEqual: Interval[DateTime(2018,01,01), DateTime(2018,01,01)] same as Interval[Date(2018,01,01), Date(2018,01,01)]
define SameAsIvl_NullIntervals: (null as Interval<DateTime>) same as (null as Interval<DateTime>)
define SameAsIvl_OpenAndClosed: Interval[DateTime(2018,01,01,00,00,00,0), DateTime(2019,01,01,00,00,00,0)) same as Interval[DateTime(2018,01,01,00,00,00,0), DateTime(2018,12,31,23,59,59,999)]
define SameAsIvl_OpenEnded: Interval[DateTime(2018,01,01), null] same day as Interval[DateTime(2018,01,01), null]
define SameAsIvl_OpenEndedNotSame: Interval[DateTime(2018,01,01), null] same day as Interval[DateTime(2018,01,01), DateTime(2019,01,01)]
define SameAsIvl_OpenEndedNotSame2: Interval[DateTime(2018,01,01), DateTime(2019,01,01)] same day as Interval[DateTime(2018,01,01), null]
define SameAsIvl_OpenBeginningSame: Interval[null,DateTime(2018,01,01)] same as Interval[null,DateTime(2018,01,01)]
define SameAsIvl_OpenBeginningNotSame: Interval[DateTime(2017,01,01),DateTime(2018,01,01)] same as Interval[null,DateTime(2018,01,01)]
define SameAsIvl_DateOpenAndClosed: Interval[Date(2018,01,01), Date(2018,02,02)] same as Interval[Date(2018,01,01), Date(2018,02,03))
define SameAsIvl_DateOpenEnded: Interval[Date(2018,01,01), null] same as Interval[Date(2018,01,01), null]
define SameAsIvl_TimeOpenAndClosed: Interval[Time(01,01), Time(02,02)] same as Interval[Time(01,01), Time(02,03))
define SameAsIvl_TimeOpenEnded: Interval[Time(01,01), null] same as Interval[Time(01,01), null]
define SameAsIvl_DateIntervalComparisonSame: Interval[Date(2018,01,01), Date(2018,02,02)] same as Interval[Date(2018,01,01), Date(2018,02,02)]
define SameAsIvl_DateIntervalComparisonNotSame: Interval[Date(2018,01,01), Date(2018,02,02)] same as Interval[Date(2018,01,01), Date(2018,02,01)]
define SameAsIvl_TimeIntervalComparisonSame: Interval[Time(01,01), Time(02,02)] same as Interval[Time(01,01), Time(02,02)]
define SameAsIvl_TimeIntervalComparisonNotSame: Interval[Time(01,01), Time(02,02)] same as Interval[Time(01,01), Time(08,01)]
define SameAsIvl_DateTimeYearPrecisionSame: Interval[DateTime(2018,01,01), DateTime(2019,01,01)] same year as Interval[DateTime(2018,02,01), DateTime(2019,05,01)]
define SameAsIvl_DateTimeYearPrecisionNotSame: Interval[DateTime(2018,01,01), DateTime(2019,01,01)] same year as Interval[DateTime(2018,02,01), DateTime(2020,05,01)]
define SameAsIvl_DateYearPrecisionSame: Interval[Date(2018,01,01), Date(2019,01,01)] same year as Interval[Date(2018,02,01), Date(2019,05,01)]
define SameAsIvl_DateYearPrecisionNotSame: Interval[Date(2018,01,01), Date(2019,01,01)] same year as Interval[Date(2018,02,01), Date(2020,05,01)]
define SameAsIvl_DateTimeMonthPrecisionSame: Interval[DateTime(2018,01,01), DateTime(2019,01,01)] same month as Interval[DateTime(2018,01,01), DateTime(2019,01,03)]
define SameAsIvl_DateTimeMonthPrecisionNotSame: Interval[DateTime(2018,01,01), DateTime(2019,01,01)] same month as Interval[DateTime(2018,02,01), DateTime(2019,01,01)]
define SameAsIvl_DateTimeDayPrecisionSame: Interval[DateTime(2018,01,01), DateTime(2019,01,01)] same day as Interval[DateTime(2018,01,01,05), DateTime(2019,01,01,09)]
define SameAsIvl_DateTimeDayPrecisionNotSame: Interval[DateTime(2018,01,01), DateTime(2019,01,01)] same day as Interval[DateTime(2018,01,01), DateTime(2019,01,02,06)]
define SameAsIvl_DateTimeHourPrecisionSame: Interval[DateTime(2018,01,01,01), DateTime(2019,01,01,01)] same hour as Interval[DateTime(2018,01,01,01), DateTime(2019,01,01,01,05)]
define SameAsIvl_DateTimeHourPrecisionNotSame: Interval[DateTime(2018,01,01,01,01), DateTime(2019,01,01,01)] same hour as Interval[DateTime(2018,01,01,06), DateTime(2019,01,01,01)]
define SameAsIvl_DateTimeMinutePrecisionSame: Interval[DateTime(2018,01,01,01,01), DateTime(2019,01,01,01,01)] same minute as Interval[DateTime(2018,01,01,01,01,09), DateTime(2019,01,01,01,01,06)]
define SameAsIvl_DateTimeMinutePrecisionNotSame: Interval[DateTime(2018,01,01,01,01), DateTime(2019,01,01,01,01)] same minute as Interval[DateTime(2018,01,01,06,03), DateTime(2019,01,01,01,06)]
define SameAsIvl_DateTimeSecondPrecisionSame: Interval[DateTime(2018,01,01,01,01,01), DateTime(2019,01,01,01,01,01)] same second as Interval[DateTime(2018,01,01,01,01,01), DateTime(2019,01,01,01,01,01,07)]
define SameAsIvl_DateTimeSecondPrecisionNotSame: Interval[DateTime(2018,01,01,01,01,01), DateTime(2019,01,01,01,01,01)] same second as Interval[DateTime(2018,01,01,01,01,01), DateTime(2019,01,01,01,07,55)]
define SameAsIvl_DateTimeMillisecondPrecisionSame: Interval[DateTime(2018,01,01,01,01,01,01), DateTime(2019,01,01,01,01,01,01)] same millisecond as Interval[DateTime(2018,01,01,01,01,01,01), DateTime(2019,01,01,01,01,01,01)]
define SameAsIvl_DateTimeMillisecondPrecisionNotSame: Interval[DateTime(2018,01,01,01,01,01,01), DateTime(2019,01,01,01,01,01,01)] same millisecond as Interval[DateTime(2018,01,01,01,01,01,01), DateTime(2019,01,01,01,01,01,09)]
define SameAsIvl_IntegerIntervalSame: Interval[2,5] same as Interval[2,5]
define SameAsIvl_IntegerIntervalNotSame: Interval[2,5] same as Interval[2,4]
define SameAsIvl_IntegerIntervalSameOpen: Interval[2,5] same as Interval[2,6)
define SameAsIvl_OpenNullHighLowDifferent: Interval(3,null) same as Interval(2,4)
define SameAsIvl_OpenNullLowHighDifferent: Interval(1,5) same as Interval(null,4)
define SameAsIvl_OpenNullHighLowSame: Interval(2,null) same as Interval(2,4)
define SameAsIvl_OpenNullLowHighSame: Interval(1,4) same as Interval(null,4)
define SameAsIvl_OpenNullLowOpenNullHigh: Interval(1,null) same as Interval(null,4)
define SameAsIvl_OpenNullHighsLowsDifferent: Interval(1,null) same as Interval(2,null)
define SameAsIvl_OpenNullHighsLowsSame: Interval(1,null) same as Interval(1,null)
define SameAsIvl_OpenNullLowsHighsSame: Interval(null,3) same as Interval(null,3)

define test_SameAsIvl_Equal: TestMessage(SameAsIvl_Equal, 'SameAsIvl_Equal', toString(true), toString(SameAsIvl_Equal))
define test_SameAsIvl_NotEqual: TestMessage(not SameAsIvl_NotEqual, 'SameAsIvl_NotEqual', toString(false), toString(SameAsIvl_NotEqual))
define test_SameAsIvl_DateTimeAndDateComparisonEqual: TestMessage(SameAsIvl_DateTimeAndDateComparisonEqual is null, 'SameAsIvl_DateTimeAndDateComparisonEqual', 'null', toString(SameAsIvl_DateTimeAndDateComparisonEqual))
define test_SameAsIvl_NullIntervals: TestMessage(SameAsIvl_NullIntervals is null, 'SameAsIvl_NullIntervals', 'null', toString(SameAsIvl_NullIntervals))
define test_SameAsIvl_OpenAndClosed: TestMessage(SameAsIvl_OpenAndClosed, 'SameAsIvl_OpenAndClosed', toString(true), toString(SameAsIvl_OpenAndClosed))
define test_SameAsIvl_OpenEnded: TestMessage(SameAsIvl_OpenEnded, 'SameAsIvl_OpenEnded', toString(true), toString(SameAsIvl_OpenEnded))
define test_SameAsIvl_OpenEndedNotSame: TestMessage(not SameAsIvl_OpenEndedNotSame, 'SameAsIvl_OpenEndedNotSame', toString(false), toString(SameAsIvl_OpenEndedNotSame))
define test_SameAsIvl_OpenEndedNotSame2: TestMessage(not SameAsIvl_OpenEndedNotSame2, 'SameAsIvl_OpenEndedNotSame2', toString(false), toString(SameAsIvl_OpenEndedNotSame2))
define test_SameAsIvl_OpenBeginningSame: TestMessage(SameAsIvl_OpenBeginningSame, 'SameAsIvl_OpenBeginningSame', toString(true), toString(SameAsIvl_OpenBeginningSame))
define test_SameAsIvl_OpenBeginningNotSame: TestMessage(not SameAsIvl_OpenBeginningNotSame, 'SameAsIvl_OpenBeginningNotSame', toString(false), toString(SameAsIvl_OpenBeginningNotSame))
define test_SameAsIvl_DateOpenAndClosed: TestMessage(SameAsIvl_DateOpenAndClosed, 'SameAsIvl_DateOpenAndClosed', toString(true), toString(SameAsIvl_DateOpenAndClosed))
define test_SameAsIvl_DateOpenEnded: TestMessage(SameAsIvl_DateOpenEnded, 'SameAsIvl_DateOpenEnded', toString(true), toString(SameAsIvl_DateOpenEnded))
define test_SameAsIvl_TimeOpenAndClosed: TestMessage(SameAsIvl_TimeOpenAndClosed, 'SameAsIvl_TimeOpenAndClosed', toString(true), toString(SameAsIvl_TimeOpenAndClosed))
define test_SameAsIvl_TimeOpenEnded: TestMessage(SameAsIvl_TimeOpenEnded, 'SameAsIvl_TimeOpenEnded', toString(true), toString(SameAsIvl_TimeOpenEnded))
define test_SameAsIvl_DateIntervalComparisonSame: TestMessage(SameAsIvl_DateIntervalComparisonSame, 'SameAsIvl_DateIntervalComparisonSame', toString(true), toString(SameAsIvl_DateIntervalComparisonSame))
define test_SameAsIvl_DateIntervalComparisonNotSame: TestMessage(not SameAsIvl_DateIntervalComparisonNotSame, 'SameAsIvl_DateIntervalComparisonNotSame', toString(false), toString(SameAsIvl_DateIntervalComparisonNotSame))
define test_SameAsIvl_TimeIntervalComparisonSame: TestMessage(SameAsIvl_TimeIntervalComparisonSame, 'SameAsIvl_TimeIntervalComparisonSame', toString(true), toString(SameAsIvl_TimeIntervalComparisonSame))
define test_SameAsIvl_TimeIntervalComparisonNotSame: TestMessage(not SameAsIvl_TimeIntervalComparisonNotSame, 'SameAsIvl_TimeIntervalComparisonNotSame', toString(false), toString(SameAsIvl_TimeIntervalComparisonNotSame))
define test_SameAsIvl_DateTimeYearPrecisionSame: TestMessage(SameAsIvl_DateTimeYearPrecisionSame, 'SameAsIvl_DateTimeYearPrecisionSame', toString(true), toString(SameAsIvl_DateTimeYearPrecisionSame))
define test_SameAsIvl_DateTimeYearPrecisionNotSame: TestMessage(not SameAsIvl_DateTimeYearPrecisionNotSame, 'SameAsIvl_DateTimeYearPrecisionNotSame', toString(false), toString(SameAsIvl_DateTimeYearPrecisionNotSame))
define test_SameAsIvl_DateYearPrecisionSame: TestMessage(SameAsIvl_DateYearPrecisionSame, 'SameAsIvl_DateYearPrecisionSame', toString(true), toString(SameAsIvl_DateYearPrecisionSame))
define test_SameAsIvl_DateYearPrecisionNotSame: TestMessage(not SameAsIvl_DateYearPrecisionNotSame, 'SameAsIvl_DateYearPrecisionNotSame', toString(false), toString(SameAsIvl_DateYearPrecisionNotSame))
define test_SameAsIvl_DateTimeMonthPrecisionSame: TestMessage(SameAsIvl_DateTimeMonthPrecisionSame, 'SameAsIvl_DateTimeMonthPrecisionSame', toString(true), toString(SameAsIvl_DateTimeMonthPrecisionSame))
define test_SameAsIvl_DateTimeMonthPrecisionNotSame: TestMessage(not SameAsIvl_DateTimeMonthPrecisionNotSame, 'SameAsIvl_DateTimeMonthPrecisionNotSame', toString(false), toString(SameAsIvl_DateTimeMonthPrecisionNotSame))
define test_SameAsIvl_DateTimeDayPrecisionSame: TestMessage(SameAsIvl_DateTimeDayPrecisionSame, 'SameAsIvl_DateTimeDayPrecisionSame', toString(true), toString(SameAsIvl_DateTimeDayPrecisionSame))
define test_SameAsIvl_DateTimeDayPrecisionNotSame: TestMessage(not SameAsIvl_DateTimeDayPrecisionNotSame, 'SameAsIvl_DateTimeDayPrecisionNotSame', toString(false), toString(SameAsIvl_DateTimeDayPrecisionNotSame))
define test_SameAsIvl_DateTimeHourPrecisionSame: TestMessage(SameAsIvl_DateTimeHourPrecisionSame, 'SameAsIvl_DateTimeHourPrecisionSame', toString(true), toString(SameAsIvl_DateTimeHourPrecisionSame))
define test_SameAsIvl_DateTimeHourPrecisionNotSame: TestMessage(not SameAsIvl_DateTimeHourPrecisionNotSame, 'SameAsIvl_DateTimeHourPrecisionNotSame', toString(false), toString(SameAsIvl_DateTimeHourPrecisionNotSame))
define test_SameAsIvl_DateTimeMinutePrecisionSame: TestMessage(SameAsIvl_DateTimeMinutePrecisionSame, 'SameAsIvl_DateTimeMinutePrecisionSame', toString(true), toString(SameAsIvl_DateTimeMinutePrecisionSame))
define test_SameAsIvl_DateTimeMinutePrecisionNotSame: TestMessage(not SameAsIvl_DateTimeMinutePrecisionNotSame, 'SameAsIvl_DateTimeMinutePrecisionNotSame', toString(false), toString(SameAsIvl_DateTimeMinutePrecisionNotSame))
define test_SameAsIvl_DateTimeSecondPrecisionSame: TestMessage(SameAsIvl_DateTimeSecondPrecisionSame, 'SameAsIvl_DateTimeSecondPrecisionSame', toString(true), toString(SameAsIvl_DateTimeSecondPrecisionSame))
define test_SameAsIvl_DateTimeSecondPrecisionNotSame: TestMessage(not SameAsIvl_DateTimeSecondPrecisionNotSame, 'SameAsIvl_DateTimeSecondPrecisionNotSame', toString(false), toString(SameAsIvl_DateTimeSecondPrecisionNotSame))
define test_SameAsIvl_DateTimeMillisecondPrecisionSame: TestMessage(SameAsIvl_DateTimeMillisecondPrecisionSame, 'SameAsIvl_DateTimeMillisecondPrecisionSame', toString(true), toString(SameAsIvl_DateTimeMillisecondPrecisionSame))
define test_SameAsIvl_DateTimeMillisecondPrecisionNotSame: TestMessage(not SameAsIvl_DateTimeMillisecondPrecisionNotSame, 'SameAsIvl_DateTimeMillisecondPrecisionNotSame', toString(false), toString(SameAsIvl_DateTimeMillisecondPrecisionNotSame))
define test_SameAsIvl_IntegerIntervalSame: TestMessage(SameAsIvl_IntegerIntervalSame, 'SameAsIvl_IntegerIntervalSame', toString(true), toString(SameAsIvl_IntegerIntervalSame))
define test_SameAsIvl_IntegerIntervalNotSame: TestMessage(not SameAsIvl_IntegerIntervalNotSame, 'SameAsIvl_IntegerIntervalNotSame', toString(false), toString(SameAsIvl_IntegerIntervalNotSame))
define test_SameAsIvl_IntegerIntervalSameOpen: TestMessage(SameAsIvl_IntegerIntervalSameOpen, 'SameAsIvl_IntegerIntervalSameOpen', toString(true), toString(SameAsIvl_IntegerIntervalSameOpen))
define test_SameAsIvl_OpenNullHighLowDifferent: TestMessage(not SameAsIvl_OpenNullHighLowDifferent, 'SameAsIvl_OpenNullHighLowDifferent', toString(false), toString(SameAsIvl_OpenNullHighLowDifferent))
define test_SameAsIvl_OpenNullLowHighDifferent: TestMessage(not SameAsIvl_OpenNullLowHighDifferent, 'SameAsIvl_OpenNullLowHighDifferent', toString(false), toString(SameAsIvl_OpenNullLowHighDifferent))
define test_SameAsIvl_OpenNullHighLowSame: TestMessage(SameAsIvl_OpenNullHighLowSame is null, 'SameAsIvl_OpenNullHighLowSame', 'null', toString(SameAsIvl_OpenNullHighLowSame))
define test_SameAsIvl_OpenNullLowHighSame: TestMessage(SameAsIvl_OpenNullLowHighSame is null, 'SameAsIvl_OpenNullLowHighSame', 'null', toString(SameAsIvl_OpenNullLowHighSame))
define test_SameAsIvl_OpenNullLowOpenNullHigh: TestMessage(SameAsIvl_OpenNullLowOpenNullHigh is null, 'SameAsIvl_OpenNullLowOpenNullHigh', 'null', toString(SameAsIvl_OpenNullLowOpenNullHigh))
define test_SameAsIvl_OpenNullHighsLowsDifferent: TestMessage(not SameAsIvl_OpenNullHighsLowsDifferent, 'SameAsIvl_OpenNullHighsLowsDifferent', toString(false), toString(SameAsIvl_OpenNullHighsLowsDifferent))
define test_SameAsIvl_OpenNullHighsLowsSame: TestMessage(SameAsIvl_OpenNullHighsLowsSame is null, 'SameAsIvl_OpenNullHighsLowsSame', 'null', toString(SameAsIvl_OpenNullHighsLowsSame))
define test_SameAsIvl_OpenNullLowsHighsSame: TestMessage(SameAsIvl_OpenNullLowsHighsSame is null, 'SameAsIvl_OpenNullLowsHighsSame', 'null', toString(SameAsIvl_OpenNullLowsHighsSame))

/*
**********************
  End Interval Tests
**********************
*/

/*
************************
  Start Library Tests
************************
*/

// TODO

/*
**********************
  End Library Tests
**********************
*/

/*
*********************
  Start List Tests
*********************
*/

// Exists
define Exists_EmptyList: exists (List<Integer>{})
define Exists_FullList: exists ({ 1, 2, 3 })
define Exists_ListWithOneNull: exists ({ null })
define Exists_ListWithTwoNulls: exists ({ null, null })
define Exists_ListStartingWithNull: exists ({ null, 3, 4 })
define Exists_ListWithNull: exists ({ 3, null, 5 })
define Exists_NullExists: exists (null)

define test_Exists_EmptyList: TestMessage(Exists_EmptyList = false, 'Exists_EmptyList', toString(false), toString(Exists_EmptyList))
define test_Exists_FullList: TestMessage(Exists_FullList = true, 'Exists_FullList', toString(true), toString(Exists_FullList))
define test_Exists_ListWithOneNull: TestMessage(Exists_ListWithOneNull = false, 'Exists_ListWithOneNull', toString(false), toString(Exists_ListWithOneNull))
define test_Exists_ListWithTwoNulls: TestMessage(Exists_ListWithTwoNulls = false, 'Exists_ListWithTwoNulls', toString(false), toString(Exists_ListWithTwoNulls))
define test_Exists_ListStartingWithNull: TestMessage(Exists_ListStartingWithNull = true, 'Exists_ListStartingWithNull', toString(true), toString(Exists_ListStartingWithNull))
define test_Exists_ListWithNull: TestMessage(Exists_ListWithNull = true, 'Exists_ListWithNull', toString(true), toString(Exists_ListWithNull))
define test_Exists_NullExists: TestMessage(Exists_NullExists = false, 'Exists_NullExists', toString(false), toString(Exists_NullExists))

// Equal
define Equal_EqualIntList: {1, 2, 3} = {1, 2, 3}
define Equal_UnequalIntList: {1, 2, 3} = {1, 2}
define Equal_ReverseIntList: {1, 2, 3} = {3, 2, 1}
define Equal_EqualStringList: {'hello', 'world'} = {'hello', 'world'}
define Equal_UnequalStringList: {'hello', 'world'} = {'foo', 'bar'}
define Equal_EqualTupleList: List<Any>{ Tuple{a: 1, b: Tuple{c: 1}}, Tuple{x: 'y', z: 2} } = List<Any>{ Tuple{a: 1, b: Tuple{c: 1}}, Tuple{x: 'y', z: 2} }
define Equal_UnequalTupleList: List<Any>{ Tuple{a: 1, b: Tuple{c: 1}}, Tuple{x: 'y', z: 2} } = List<Any>{ Tuple{a: 1, b: Tuple{c: -1}}, Tuple{x: 'y', z: 2} }
define Equal_FirstListHasNull: {'hello', 'world', (null as String)} = {'hello', 'world'}
define Equal_SecondListHasNull: {'hello', 'world'} = {'hello', 'world', (null as String)}
define Equal_BothListsHaveNull: List<Integer>{(null as Integer)} = List<Integer>{(null as Integer)}

define test_Equal_EqualIntList: TestMessage(Equal_EqualIntList, 'Equal_EqualIntList', toString(true), toString(Equal_EqualIntList))
define test_Equal_UnequalIntList: TestMessage(not Equal_UnequalIntList, 'Equal_UnequalIntList', toString(false), toString(Equal_UnequalIntList))
define test_Equal_ReverseIntList: TestMessage(not Equal_ReverseIntList, 'Equal_ReverseIntList', toString(false), toString(Equal_ReverseIntList))
define test_Equal_EqualStringList: TestMessage(Equal_EqualStringList, 'Equal_EqualStringList', toString(true), toString(Equal_EqualStringList))
define test_Equal_UnequalStringList: TestMessage(not Equal_UnequalStringList, 'Equal_UnequalStringList', toString(false), toString(Equal_UnequalStringList))
define test_Equal_EqualTupleList: TestMessage(Equal_EqualTupleList, 'Equal_EqualTupleList', toString(true), toString(Equal_EqualTupleList))
define test_Equal_UnequalTupleList: TestMessage(not Equal_UnequalTupleList, 'Equal_UnequalTupleList', toString(false), toString(Equal_UnequalTupleList))
define test_Equal_FirstListHasNull: TestMessage(Equal_FirstListHasNull is null, 'Equal_FirstListHasNull', 'null', toString(Equal_FirstListHasNull))
define test_Equal_SecondListHasNull: TestMessage(Equal_SecondListHasNull is null, 'Equal_SecondListHasNull', 'null', toString(Equal_SecondListHasNull))
define test_Equal_BothListsHaveNull: TestMessage(Equal_BothListsHaveNull, 'Equal_BothListsHaveNull', toString(true), toString(Equal_BothListsHaveNull))

// NotEqual
define NotEqual_EqualIntList: {1, 2, 3} != {1, 2, 3}
define NotEqual_UnequalIntList: {1, 2, 3} != {1, 2}
define NotEqual_ReverseIntList: {1, 2, 3} != {3, 2, 1}
define NotEqual_EqualStringList: {'hello', 'world'} != {'hello', 'world'}
define NotEqual_UnequalStringList: {'hello', 'world'} != {'foo', 'bar'}
define NotEqual_EqualTupleList: List<Any>{ Tuple{a: 1, b: Tuple{c: 1}}, Tuple{x: 'y', z: 2} } != List<Any>{ Tuple{a: 1, b: Tuple{c: 1}}, Tuple{x: 'y', z: 2} }
define NotEqual_UnequalTupleList: List<Any>{ Tuple{a: 1, b: Tuple{c: 1}}, Tuple{x: 'y', z: 2} } != List<Any>{ Tuple{a: 1, b: Tuple{c: -1}}, Tuple{x: 'y', z: 2} }

define test_NotEqual_EqualIntList: TestMessage(not NotEqual_EqualIntList, 'NotEqual_EqualIntList', toString(false), toString(NotEqual_EqualIntList))
define test_NotEqual_UnequalIntList: TestMessage(NotEqual_UnequalIntList, 'NotEqual_UnequalIntList', toString(true), toString(NotEqual_UnequalIntList))
define test_NotEqual_ReverseIntList: TestMessage(NotEqual_ReverseIntList, 'NotEqual_ReverseIntList', toString(true), toString(NotEqual_ReverseIntList))
define test_NotEqual_EqualStringList: TestMessage(not NotEqual_EqualStringList, 'NotEqual_EqualStringList', toString(false), toString(NotEqual_EqualStringList))
define test_NotEqual_UnequalStringList: TestMessage(NotEqual_UnequalStringList, 'NotEqual_UnequalStringList', toString(true), toString(NotEqual_UnequalStringList))
define test_NotEqual_EqualTupleList: TestMessage(not NotEqual_EqualTupleList, 'NotEqual_EqualTupleList', toString(false), toString(NotEqual_EqualTupleList))
define test_NotEqual_UnequalTupleList: TestMessage(NotEqual_UnequalTupleList, 'NotEqual_UnequalTupleList', toString(true), toString(NotEqual_UnequalTupleList))

// Union
define Union_OneToTen: {1, 2, 3, 4, 5} union {6, 7, 8, 9, 10}
define Union_OneToFiveOverlapped: {1, 2, 3, 4} union {3, 4, 5}
define Union_OneToFiveOverlappedWithNulls: {1, null, 2, 3, 4} union {3, 4, 5, null}
define Union_Disjoint: {1, 2} union {4, 5}
define Union_NestedToFifteen: {1, 2, 3} union {4, 5, 6} union {7 ,8 , 9} union {10, 11, 12} union {13, 14, 15}
define Union_NullUnion: null union {1, 2, 3}
define Union_UnionNull: {1, 2, 3} union null

// TODO: This fails due to list-distinguised overloads
// define test_Union_OneToTen: TestMessage(Union_OneToTen = {1,2,3,4,5,6,7,8,9,10}, 'Union_OneToTen', toString({1,2,3,4,5,6,7,8,9,10}), toString(Union_OneToTen))
// define test_Union_OneToFiveOverlapped: TestMessage(Union_OneToFiveOverlapped = {1,2,3,4,5}, 'Union_OneToFiveOverlapped', toString({1,2,3,4,5}), toString(Union_OneToFiveOverlapped))
// define test_Union_OneToFiveOverlappedWithNulls: TestMessage(Union_OneToFiveOverlappedWithNulls ~ {1,null,2,3,4,5}, 'Union_OneToFiveOverlappedWithNulls', toString({1,null,2,3,4,5}), toString(Union_OneToFiveOverlappedWithNulls))
// define test_Union_Disjoint: TestMessage(Union_Disjoint = {1,2,4,5}, 'Union_Disjoint', toString({1,2,4,5}), toString(Union_Disjoint))
// define test_Union_NestedToFifteen: TestMessage(Union_NestedToFifteen = {1,2,3,4,5,6,7,8,9,10,11,12,13,14,15}, 'Union_NestedToFifteen', toString({1,2,3,4,5,6,7,8,9,10,11,12,13,14,15}), toString(Union_NestedToFifteen))
// define test_Union_NullUnion: TestMessage(Union_NullUnion = {1,2,3}, 'Union_NullUnion', toString({1,2,3}), toString(Union_NullUnion))
// define test_Union_UnionNull: TestMessage(Union_UnionNull = {1,2,3}, 'Union_UnionNull', toString({1,2,3}), toString(Union_UnionNull))

// Except
define Except_ExceptThreeFour: {1, 2, 3, 4, 5} except {3, 4}
define Except_ThreeFourExcept: {3, 4} except {1, 2, 3, 4, 5}
define Except_ExceptFiveThree: {1, 2, 3, 4, 5} except {5, 3}
define Except_ExceptNoOp: {1, 2, 3, 3, 4, 5} except {6, 7, 8, 9, 10}
define Except_ExceptEverything: {1, 2, 3, 3, 4, 5} except {1, 2, 3, 4, 5}
define Except_MultipleNullExcept: {1, 3, 5, 7, null, null} except {null, 3}
define Except_SomethingExceptNothing: {1, 2, 3, 4, 5} except List<Integer>{}
define Except_NothingExceptSomething: List<Integer>{} except {1, 2, 3, 4, 5}
define Except_ExceptTuples: {Tuple{a: 1}, Tuple{a: 2}, Tuple{a: 3}} except {Tuple{a: 2}}
define Except_ExceptNull: {1, 2, 3, 4, 5} except null
define Except_NullExcept: null except {1, 2, 3, 4, 5}

// TODO - This fails due to list-distinguised overloads
// define test_Except_ExceptThreeFour: TestMessage(Except_ExceptThreeFour = {1,2,5}, 'Except_ExceptThreeFour', toString({1,2,5}), toString(Except_ExceptThreeFour))
define test_Except_ThreeFourExcept: TestMessage(not exists Except_ThreeFourExcept, 'Except_ThreeFourExcept', toString(List<Integer>{}), toString(Except_ThreeFourExcept))

// TODO - This fails due to list-distinguised overloads
// define test_Except_ExceptFiveThree: TestMessage(Except_ExceptFiveThree = {1,2,4}, 'Except_ExceptFiveThree', toString({1,2,4}), toString(Except_ExceptFiveThree))
// define test_Except_ExceptNoOp: TestMessage(Except_ExceptNoOp = {1,2,3,4,5}, 'Except_ExceptNoOp', toString({1,2,3,4,5}), toString(Except_ExceptNoOp))
define test_Except_ExceptEverything: TestMessage(not exists Except_ExceptEverything, 'Except_ExceptEverything', toString(List<Integer>{}), toString(Except_ExceptEverything))

// TODO - This fails due to list-distinguised overloads
// define test_Except_MultipleNullExcept: TestMessage(Except_MultipleNullExcept = {1,5,7}, 'Except_MultipleNullExcept', toString({1,5,7}), toString(Except_MultipleNullExcept))
// define test_Except_SomethingExceptNothing: TestMessage(Except_SomethingExceptNothing = {1,2,3,4,5}, 'Except_SomethingExceptNothing', toString({1,2,3,4,5}), toString(Except_SomethingExceptNothing))
define test_Except_NothingExceptSomething: TestMessage(not exists Except_NothingExceptSomething, 'Except_NothingExceptSomething', toString(List<Integer>{}), toString(Except_NothingExceptSomething))
define test_Except_ExceptTuples: TestMessage(Except_ExceptTuples = {Tuple{a: 1}, Tuple{a: 3}}, 'Except_ExceptTuples', toString({Tuple{a: 1}, Tuple{a: 3}}), toString(Except_ExceptTuples))
// TODO - This fails due to list-distinguised overloads
//define test_Except_ExceptNull: TestMessage(Except_ExceptNull = {1, 2, 3, 4, 5}, 'Except_ExceptNull', toString({1, 2, 3, 4, 5}), toString(Except_ExceptNull))
define test_Except_NullExcept: TestMessage(Except_NullExcept is null, 'Except_NullExcept', 'null', toString(Except_NullExcept))

// Intersect
define Intersect_NoIntersection: {1, 2, 2, 3} intersect {4, 5, 6}
define Intersect_IntersectOnFive: {4, 5, 5, 6} intersect {1, 3, 5, 7}
define Intersect_IntersectionOnFourDuplicates: {4, 4, 1, 1, 2} intersect {4, 4, 3, 9}
define Intersect_IntersectOnEvens: {1, 2, 3, 4, 5, 6, 7, 8, 9, 10} intersect {0, 2, 4, 6, 8, 10, 12}
define Intersect_IntersectOnAll: {1, 2, 3, 4, 5} intersect {5, 4, 3, 2, 1}
define Intersect_NestedIntersects: {1, 2, 3, 4, 5} intersect {2, 3, 4, 5, 6} intersect {3, 4, 5, 6, 7} intersect {4, 5, 6, 7, 8}
define Intersect_IntersectTuples: {Tuple{a:1, b:'d'}, Tuple{a:1, b:'c'}, Tuple{a:2, b:'c'}} intersect {Tuple{a:2, b:'d'}, Tuple{a:1, b:'c'}, Tuple{a:2, b:'c'}, Tuple{a:5, b:'e'}}
define Intersect_NullIntersect: null intersect {1, 2, 3}
define Intersect_IntersectNull: {1, 2, 3} intersect null
define Intersect_MultipleNullInListIntersect: {1, 2, 3, null} intersect {null, 3}

define test_Intersect_NoIntersection: TestMessage(not exists Intersect_NoIntersection, 'Intersect_NoIntersection', toString(List<Integer>{}), toString(Intersect_NoIntersection))

// TODO: This fails due to list-distinguised overloads
//define test_Intersect_IntersectOnFive: TestMessage(Intersect_IntersectOnFive = {5}, 'Intersect_IntersectOnFive', toString({5}), toString(Intersect_IntersectOnFive))
// define test_Intersect_IntersectionOnFourDuplicates: TestMessage(Intersect_IntersectionOnFourDuplicates = {4}, 'Intersect_IntersectionOnFourDuplicates', toString({4}), toString(Intersect_IntersectionOnFourDuplicates))
// define test_Intersect_IntersectOnEvens: TestMessage(Intersect_IntersectOnEvens = {2,4,6,8,10}, 'Intersect_IntersectOnEvens', toString({2,4,6,8,10}), toString(Intersect_IntersectOnEvens))
// define test_Intersect_IntersectOnAll: TestMessage(Intersect_IntersectOnAll = {1,2,3,4,5}, 'Intersect_IntersectOnAll', toString({1,2,3,4,5}), toString(Intersect_IntersectOnAll))
// define test_Intersect_NestedIntersects: TestMessage(Intersect_NestedIntersects = {4,5}, 'Intersect_NestedIntersects', toString({4,5}), toString(Intersect_NestedIntersects))
define test_Intersect_IntersectTuples: TestMessage(Intersect_IntersectTuples = {Tuple{a: 1, b:'c'}, Tuple{a: 2, b:'c'}}, 'Intersect_IntersectTuples', toString({Tuple{a: 1, b:'c'}, Tuple{a: 2, b: 'c'}}), toString(Intersect_IntersectTuples))
define test_Intersect_NullIntersect: TestMessage(Intersect_NullIntersect is null, 'Intersect_NullIntersect', 'null', toString(Intersect_NullIntersect))
define test_Intersect_IntersectNull: TestMessage(Intersect_IntersectNull is null, 'Intersect_IntersectNull', 'null', toString(Intersect_IntersectNull))

// TODO: This fails due to list-distinguised overloads
// define test_Intersect_MultipleNullInListIntersect: TestMessage(Intersect_MultipleNullInListIntersect = {3}, 'Intersect_MultipleNullInListIntersect', toString({3}), toString(Intersect_MultipleNullInListIntersect))

// IndexOf
define IndexOf_IndexOfSecond: IndexOf({'a', 'b', 'c', 'd'}, 'b')
define IndexOf_IndexOfThirdTuple: IndexOf({Tuple{a: 1}, Tuple{a: 2}, Tuple{a: 3}}, Tuple{a: 3})
define IndexOf_MultipleMatches: IndexOf({'a', 'b', 'c', 'd', 'd', 'e', 'd'}, 'd')
define IndexOf_ItemNotFound: IndexOf({'a', 'b', 'c'}, 'd')
define IndexOf_NullList: IndexOf(null, 'a')
define IndexOf_NullItem: IndexOf({'a', 'b', 'c'}, null)
define IndexOf_ListCode: IndexOf({Code{code: 'F', system: '2.16.840.1.113883.18.2', version: 'HL7V2.5', display: 'foo'}}, Code{code: 'F', system: '2.16.840.1.113883.18.2', version: 'HL7V2.5', display: 'foo'})
define IndexOf_ListCodeUndefined: IndexOf({Code{code: 'F', system: '2.16.840.1.113883.18.2', version: 'HL7V2.5'}}, Code{code: 'F', system: '2.16.840.1.113883.18.2', version: 'HL7V2.5'})
define IndexOf_ListWrongCode: IndexOf({Code{code: 'M', system: '2.16.840.1.113883.18.2', version: 'HL7V2.5'}}, Code{code: 'F', system: '2.16.840.1.113883.18.2', version: 'foo'})
define IndexOf_ListWrongCodeSystem: IndexOf({Code{code: 'F', system: '1.16.840.1.113883.18.2', version: 'HL7V2.5'}}, Code{code: 'F', system: '2.16.840.1.113883.18.2', version: 'foo'})

define test_IndexOf_IndexOfSecond: TestMessage(IndexOf_IndexOfSecond = 1, 'IndexOf_IndexOfSecond', toString(1), toString(IndexOf_IndexOfSecond))
define test_IndexOf_IndexOfThirdTuple: TestMessage(IndexOf_IndexOfThirdTuple = 2, 'IndexOf_IndexOfThirdTuple', toString(2), toString(IndexOf_IndexOfThirdTuple))
define test_IndexOf_MultipleMatches: TestMessage(IndexOf_MultipleMatches = 3, 'IndexOf_MultipleMatches', toString(3), toString(IndexOf_MultipleMatches))
define test_IndexOf_ItemNotFound: TestMessage(IndexOf_ItemNotFound = -1, 'IndexOf_ItemNotFound', toString(-1), toString(IndexOf_ItemNotFound))
define test_IndexOf_NullList: TestMessage(IndexOf_NullList is null, 'IndexOf_NullList', 'null', toString(IndexOf_NullList))
define test_IndexOf_NullItem: TestMessage(IndexOf_NullItem is null, 'IndexOf_NullItem', 'null', toString(IndexOf_NullItem))
define test_IndexOf_ListCode: TestMessage(IndexOf_ListCode = 0, 'IndexOf_ListCode', toString(0), toString(IndexOf_ListCode))
define test_IndexOf_ListCodeUndefined: TestMessage(IndexOf_ListCodeUndefined = 0, 'IndexOf_ListCodeUndefined', toString(0), toString(IndexOf_ListCodeUndefined))
define test_IndexOf_ListWrongCode: TestMessage(IndexOf_ListWrongCode = -1, 'IndexOf_ListWrongCode', toString(-1), toString(IndexOf_ListWrongCode))
define test_IndexOf_ListWrongCodeSystem: TestMessage(IndexOf_ListWrongCodeSystem = -1, 'IndexOf_ListWrongCodeSystem', toString(-1), toString(IndexOf_ListWrongCodeSystem))

// Indexer
define Indexer_SecondItem: {'a', 'b', 'c', 'd'}[1]
define Indexer_ZeroIndex: {'a', 'b', 'c', 'd'}[0]
define Indexer_OutOfBounds: {'a', 'b', 'c', 'd'}[100]
define Indexer_NullList: (null as List<String>)[1]
define Indexer_NullIndexer: {'a', 'b', 'c', 'd'}[null]

define test_Indexer_SecondItem: TestMessage(Indexer_SecondItem = 'b', 'Indexer_SecondItem', 'b', Indexer_SecondItem)
define test_Indexer_ZeroIndex: TestMessage(Indexer_ZeroIndex = 'a', 'Indexer_ZeroIndex', 'a', Indexer_ZeroIndex)
define test_Indexer_OutOfBounds: TestMessage(Indexer_OutOfBounds is null, 'Indexer_OutOfBounds', 'null', Indexer_OutOfBounds)
define test_Indexer_NullList: TestMessage(Indexer_NullList is null, 'Indexer_NullList', 'null', Indexer_NullList)
define test_Indexer_NullIndexer: TestMessage(Indexer_NullIndexer is null, 'Indexer_NullIndexer', 'null', Indexer_NullIndexer)

// In
define In_IsIn: 4 in { 3, 4, 5 }
define In_IsNotIn: 4 in { 3, 5, 6 }
define In_TupleIsIn: Tuple{a: 1, b: 'c'} in {Tuple{a:1, b:'d'}, Tuple{a:1, b:'c'}, Tuple{a:2, b:'c'}}
define In_TupleIsNotIn: Tuple{a: 1, b: 'c'} in {Tuple{a:1, b:'d'}, Tuple{a:2, b:'d'}, Tuple{a:2, b:'c'}}
define In_NullIn: null in {1, 2, null, 3}
define In_InNull: 1 in null as List<Integer>
define In_NullNotIn: null in {1, 2, 3}

define test_In_IsIn: TestMessage(In_IsIn, 'In_IsIn', toString(true), toString(In_IsIn))
define test_In_IsNotIn: TestMessage(not In_IsNotIn, 'In_IsNotIn', toString(false), toString(In_IsNotIn))
define test_In_TupleIsIn: TestMessage(In_TupleIsIn, 'In_TupleIsIn', toString(true), toString(In_TupleIsIn))
define test_In_TupleIsNotIn: TestMessage(not In_TupleIsNotIn, 'In_TupleIsNotIn', toString(false), toString(In_TupleIsNotIn))
define test_In_NullIn: TestMessage(In_NullIn, 'In_NullIn', toString(true), toString(In_NullIn))
define test_In_InNull: TestMessage(not In_InNull, 'In_InNull', toString(false), toString(In_InNull))
define test_In_NullNotIn: TestMessage(not In_NullNotIn, 'In_NullNotIn', toString(false), toString(In_NullNotIn))

// Contains
define Contains_IsIn: { 3, 4, 5 } contains 4
define Contains_IsNotIn: { 3, 5, 6 } contains 4
define Contains_TupleIsIn: {Tuple{a:1, b:'d'}, Tuple{a:1, b:'c'}, Tuple{a:2, b:'c'}} contains Tuple{a: 1, b: 'c'}
define Contains_TupleIsNotIn: {Tuple{a:1, b:'d'}, Tuple{a:2, b:'d'}, Tuple{a:2, b:'c'}} contains Tuple{a: 1, b: 'c'}
define Contains_InNull: (null as List<Integer>) contains 1
define Contains_NullIn: {1, 2, null, 3} contains null
define Contains_NullNotIn: {1, 2, 3} contains null

define test_Contains_IsIn: TestMessage(Contains_IsIn, 'Contains_IsIn', toString(true), toString(Contains_IsIn))
define test_Contains_IsNotIn: TestMessage(not Contains_IsNotIn, 'Contains_IsNotIn', toString(false), toString(Contains_IsNotIn))
define test_Contains_TupleIsIn: TestMessage(Contains_TupleIsIn, 'Contains_TupleIsIn', toString(true), toString(Contains_TupleIsIn))
define test_Contains_TupleIsNotIn: TestMessage(not Contains_TupleIsNotIn, 'Contains_TupleIsNotIn', toString(false), toString(Contains_TupleIsNotIn))
define test_Contains_InNull: TestMessage(not Contains_InNull, 'Contains_InNull', toString(false), toString(Contains_InNull))
define test_Contains_NullIn: TestMessage(Contains_NullIn, 'Contains_NullIn', toString(true), toString(Contains_NullIn))
define test_Contains_NullNotIn: TestMessage(not Contains_NullNotIn, 'Contains_NullNotIn', toString(false), toString(Contains_NullNotIn))

// Includes
define Includes_IsIncluded: {1, 2, 3, 4, 5} includes {2, 3, 4}
define Includes_IsIncludedReversed: {1, 2, 3, 4, 5} includes {4, 3, 2}
define Includes_IsSame: {1, 2, 3, 4, 5} includes {1, 2, 3, 4, 5}
define Includes_IsNotIncluded: {1, 2, 3, 4, 5} includes {4, 5, 6}
define Includes_TuplesIncluded: {Tuple{a:1, b:'d'}, Tuple{a:2, b:'d'}, Tuple{a:2, b:'c'}} includes {Tuple{a:2, b:'d'}, Tuple{a:2, b:'c'}}
define Includes_TuplesNotIncluded: {Tuple{a:1, b:'d'}, Tuple{a:2, b:'d'}, Tuple{a:2, b:'c'}} includes {Tuple{a:2, b:'d'}, Tuple{a:3, b:'c'}}
define Includes_NullIncluded: {1, 2, 3, 4, 5} includes null as List<Integer>
define Includes_NullIncludes: null includes {1, 2, 3, 4, 5}
define Includes_DayIncluded: {DateTime(2012, 3, 2), DateTime(2012, 3, 3), DateTime(2012, 3, 4)} includes DateTime(2012, 3, 3)
define Includes_DayNotIncluded: {DateTime(2012, 3, 2), DateTime(2012, 3, 3), DateTime(2012, 3, 4)} includes DateTime(2014, 3, 3)
define Includes_IntegerIncluded: {1, 2, 3, 4, 5} includes 3
define Includes_IntegerNotIncluded: {1, 2, 3, 4, 5} includes 33
define Includes_QuantityInList: { ToQuantity('1 \'m\''), ToQuantity('1.995 \'m\''), ToQuantity('2 \'m\''), ToQuantity('3 \'m\'')} includes ToQuantity('1 \'m\'')
define Includes_QuantityNotInList: { ToQuantity('100 \'m\''), ToQuantity('1.995 \'m\''), ToQuantity('2 \'m\''), ToQuantity('3 \'m\'')} includes ToQuantity('1 \'m\'')

define test_Includes_IsIncluded: TestMessage(Includes_IsIncluded, 'Includes_IsIncluded', toString(true), toString(Includes_IsIncluded))
define test_Includes_IsIncludedReversed: TestMessage(Includes_IsIncludedReversed, 'Includes_IsIncludedReversed', toString(true), toString(Includes_IsIncludedReversed))
define test_Includes_IsSame: TestMessage(Includes_IsSame, 'Includes_IsSame', toString(true), toString(Includes_IsSame))
define test_Includes_IsNotIncluded: TestMessage(not Includes_IsNotIncluded, 'Includes_IsNotIncluded', toString(false), toString(Includes_IsNotIncluded))
define test_Includes_TuplesIncluded: TestMessage(Includes_TuplesIncluded, 'Includes_TuplesIncluded', toString(true), toString(Includes_TuplesIncluded))
define test_Includes_TuplesNotIncluded: TestMessage(not Includes_TuplesNotIncluded, 'Includes_TuplesNotIncluded', toString(false), toString(Includes_TuplesNotIncluded))
define test_Includes_NullIncluded: TestMessage(not Includes_NullIncluded, 'Includes_NullIncluded', toString(false), toString(Includes_NullIncluded))
define test_Includes_NullIncludes: TestMessage(not Includes_NullIncludes, 'Includes_NullIncludes', toString(false), toString(Includes_NullIncludes))
define test_Includes_DayIncluded: TestMessage(Includes_DayIncluded, 'Includes_DayIncluded', toString(true), toString(Includes_DayIncluded))
define test_Includes_DayNotIncluded: TestMessage(not Includes_DayNotIncluded, 'Includes_DayNotIncluded', toString(false), toString(Includes_DayNotIncluded))
define test_Includes_IntegerIncluded: TestMessage(Includes_IntegerIncluded, 'Includes_IntegerIncluded', toString(true), toString(Includes_IntegerIncluded))
define test_Includes_IntegerNotIncluded: TestMessage(not Includes_IntegerNotIncluded, 'Includes_IntegerNotIncluded', toString(false), toString(Includes_IntegerNotIncluded))
define test_Includes_QuantityInList: TestMessage(Includes_QuantityInList, 'Includes_QuantityInList', toString(true), toString(Includes_QuantityInList))
define test_Includes_QuantityNotInList: TestMessage(not Includes_QuantityNotInList, 'Includes_QuantityNotInList', toString(false), toString(Includes_QuantityNotInList))

// IncludedIn
define IncludedIn_IsIncluded: {2, 3, 4} included in {1, 2, 3, 4, 5}
define IncludedIn_IsIncludedReversed: {4, 3, 2} included in {1, 2, 3, 4, 5}
define IncludedIn_IsSame: {1, 2, 3, 4, 5} included in {1, 2, 3, 4, 5}
define IncludedIn_IsNotIncluded: {4, 5, 6} included in {1, 2, 3, 4, 5}
define IncludedIn_TuplesIncluded: {Tuple{a:2, b:'d'}, Tuple{a:2, b:'c'}} included in {Tuple{a:1, b:'d'}, Tuple{a:2, b:'d'}, Tuple{a:2, b:'c'}}
define IncludedIn_TuplesNotIncluded: {Tuple{a:2, b:'d'}, Tuple{a:3, b:'c'}} included in {Tuple{a:1, b:'d'}, Tuple{a:2, b:'d'}, Tuple{a:2, b:'c'}}
define IncludedIn_NullIncludes: {1, 2, 3, 4, 5} included in null
define IncludedIn_NullIncluded: null as List<Integer> included in {1, 2, 3, 4, 5}
define IncludedIn_DayIncluded: DateTime(2012, 3, 3) included in {DateTime(2012, 3, 2), DateTime(2012, 3, 3), DateTime(2012, 3, 4)}
define IncludedIn_DayNotIncluded: DateTime(2014, 3, 3) included in {DateTime(2012, 3, 2), DateTime(2012, 3, 3), DateTime(2012, 3, 4)}
define IncludedIn_IntegerIncluded: 3 included in {1, 2, 3, 4, 5}
define IncludedIn_IntegerNotIncluded: 33 included in {1, 2, 3, 4, 5}
define IncludedIn_QuantityInList: ToQuantity('1 \'m\'') included in { ToQuantity('1 \'m\''), ToQuantity('1.995 \'m\''), ToQuantity('2 \'m\''), ToQuantity('3 \'m\'')}
define IncludedIn_QuantityNotInList: ToQuantity('100 \'m\'') included in { ToQuantity('1 \'m\''), ToQuantity('1.995 \'m\''), ToQuantity('2 \'m\''), ToQuantity('3 \'m\'')}

define test_IncludedIn_IsIncluded: TestMessage(IncludedIn_IsIncluded, 'IncludedIn_IsIncluded', toString(true), toString(IncludedIn_IsIncluded))
define test_IncludedIn_IsIncludedReversed: TestMessage(IncludedIn_IsIncludedReversed, 'IncludedIn_IsIncludedReversed', toString(true), toString(IncludedIn_IsIncludedReversed))
define test_IncludedIn_IsSame: TestMessage(IncludedIn_IsSame, 'IncludedIn_IsSame', toString(true), toString(IncludedIn_IsSame))
define test_IncludedIn_IsNotIncluded: TestMessage(not IncludedIn_IsNotIncluded, 'IncludedIn_IsNotIncluded', toString(false), toString(IncludedIn_IsNotIncluded))
define test_IncludedIn_TuplesIncluded: TestMessage(IncludedIn_TuplesIncluded, 'IncludedIn_TuplesIncluded', toString(true), toString(IncludedIn_TuplesIncluded))
define test_IncludedIn_TuplesNotIncluded: TestMessage(not IncludedIn_TuplesNotIncluded, 'IncludedIn_TuplesNotIncluded', toString(false), toString(IncludedIn_TuplesNotIncluded))
define test_IncludedIn_NullIncludes: TestMessage(not IncludedIn_NullIncludes, 'IncludedIn_NullIncludes', toString(false), toString(IncludedIn_NullIncludes))
define test_IncludedIn_NullIncluded: TestMessage(not IncludedIn_NullIncluded, 'IncludedIn_NullIncluded', toString(false), toString(IncludedIn_NullIncluded))
define test_IncludedIn_DayIncluded: TestMessage(IncludedIn_DayIncluded, 'IncludedIn_DayIncluded', toString(true), toString(IncludedIn_DayIncluded))
define test_IncludedIn_DayNotIncluded: TestMessage(not IncludedIn_DayNotIncluded, 'IncludedIn_DayNotIncluded', toString(false), toString(IncludedIn_DayNotIncluded))
define test_IncludedIn_IntegerIncluded: TestMessage(IncludedIn_IntegerIncluded, 'IncludedIn_IntegerIncluded', toString(true), toString(IncludedIn_IntegerIncluded))
define test_IncludedIn_IntegerNotIncluded: TestMessage(not IncludedIn_IntegerNotIncluded, 'IncludedIn_IntegerNotIncluded', toString(false), toString(IncludedIn_IntegerNotIncluded))
define test_IncludedIn_QuantityInList: TestMessage(IncludedIn_QuantityInList, 'IncludedIn_QuantityInList', toString(true), toString(IncludedIn_QuantityInList))
define test_IncludedIn_QuantityNotInList: TestMessage(not IncludedIn_QuantityNotInList, 'IncludedIn_QuantityNotInList', toString(false), toString(IncludedIn_QuantityNotInList))

// ProperIncludes
define ProperIncludes_IsIncluded: {1, 2, 3, 4, 5} properly includes {2, 3, 4, 5}
define ProperIncludes_IsIncludedReversed: {1, 2, 3, 4, 5} properly includes {5, 4, 3, 2}
define ProperIncludes_IsSame: {1, 2, 3, 4, 5} properly includes {1, 2, 3, 4, 5}
define ProperIncludes_IsNotIncluded: {1, 2, 3, 4, 5} properly includes {3, 4, 5, 6}
define ProperIncludes_TuplesIncluded: {Tuple{a:1, b:'d'}, Tuple{a:2, b:'d'}, Tuple{a:2, b:'c'}} properly includes {Tuple{a:2, b:'d'}, Tuple{a:2, b:'c'}}
define ProperIncludes_TuplesNotIncluded: {Tuple{a:1, b:'d'}, Tuple{a:2, b:'d'}, Tuple{a:2, b:'c'}} properly includes {Tuple{a:2, b:'d'}, Tuple{a:3, b:'c'}}
define ProperIncludes_NullIncluded: {1, 2, 3, 4, 5} properly includes null as List<Integer>
define ProperIncludes_NullIncludes: null properly includes {1, 2, 3, 4, 5}

define test_ProperIncludes_IsIncluded: TestMessage(ProperIncludes_IsIncluded, 'ProperIncludes_IsIncluded', toString(true), toString(ProperIncludes_IsIncluded))
define test_ProperIncludes_IsIncludedReversed: TestMessage(ProperIncludes_IsIncludedReversed, 'ProperIncludes_IsIncludedReversed', toString(true), toString(ProperIncludes_IsIncludedReversed))
define test_ProperIncludes_IsSame: TestMessage(not ProperIncludes_IsSame, 'ProperIncludes_IsSame', toString(false), toString(ProperIncludes_IsSame))
define test_ProperIncludes_IsNotIncluded: TestMessage(not ProperIncludes_IsNotIncluded, 'ProperIncludes_IsNotIncluded', toString(false), toString(ProperIncludes_IsNotIncluded))
define test_ProperIncludes_TuplesIncluded: TestMessage(ProperIncludes_TuplesIncluded, 'ProperIncludes_TuplesIncluded', toString(true), toString(ProperIncludes_TuplesIncluded))
define test_ProperIncludes_TuplesNotIncluded: TestMessage(not ProperIncludes_TuplesNotIncluded, 'ProperIncludes_TuplesNotIncluded', toString(false), toString(ProperIncludes_TuplesNotIncluded))
define test_ProperIncludes_NullIncludes: TestMessage(not ProperIncludes_NullIncludes, 'ProperIncludes_NullIncludes', toString(false), toString(ProperIncludes_NullIncludes))
define test_ProperIncludes_NullIncluded: TestMessage(ProperIncludes_NullIncluded, 'ProperIncludes_NullIncluded', toString(true), toString(ProperIncludes_NullIncluded))

// ProperIncludedIn
define ProperIncludedIn_IsIncluded: {2, 3, 4} properly included in {1, 2, 3, 4, 5}
define ProperIncludedIn_IsIncludedReversed: {4, 3, 2} properly included in {1, 2, 3, 4, 5}
define ProperIncludedIn_IsSame: {1, 2, 3, 4, 5} properly included in {1, 2, 3, 4, 5}
define ProperIncludedIn_IsNotIncluded: {4, 5, 6} properly included in {1, 2, 3, 4, 5}
define ProperIncludedIn_TuplesIncluded: {Tuple{a:2, b:'d'}, Tuple{a:2, b:'c'}} properly included in {Tuple{a:1, b:'d'}, Tuple{a:2, b:'d'}, Tuple{a:2, b:'c'}}
define ProperIncludedIn_TuplesNotIncluded: {Tuple{a:2, b:'d'}, Tuple{a:3, b:'c'}} properly included in {Tuple{a:1, b:'d'}, Tuple{a:2, b:'d'}, Tuple{a:2, b:'c'}}
define ProperIncludedIn_NullIncludes: {1, 2, 3, 4, 5} properly included in null as List<Integer>
define ProperIncludedIn_NullIncluded: (null as List<Integer>) properly included in {1, 2, 3, 4, 5}

define test_ProperIncludedIn_IsIncluded: TestMessage(ProperIncludedIn_IsIncluded, 'ProperIncludedIn_IsIncluded', toString(true), toString(ProperIncludedIn_IsIncluded))
define test_ProperIncludedIn_IsIncludedReversed: TestMessage(ProperIncludedIn_IsIncludedReversed, 'ProperIncludedIn_IsIncludedReversed', toString(true), toString(ProperIncludedIn_IsIncludedReversed))
define test_ProperIncludedIn_IsSame: TestMessage(not ProperIncludedIn_IsSame, 'ProperIncludedIn_IsSame', toString(false), toString(ProperIncludedIn_IsSame))
define test_ProperIncludedIn_IsNotIncluded: TestMessage(not ProperIncludedIn_IsNotIncluded, 'ProperIncludedIn_IsNotIncluded', toString(false), toString(ProperIncludedIn_IsNotIncluded))
define test_ProperIncludedIn_TuplesIncluded: TestMessage(ProperIncludedIn_TuplesIncluded, 'ProperIncludedIn_TuplesIncluded', toString(true), toString(ProperIncludedIn_TuplesIncluded))
define test_ProperIncludedIn_TuplesNotIncluded: TestMessage(not ProperIncludedIn_TuplesNotIncluded, 'ProperIncludedIn_TuplesNotIncluded', toString(false), toString(ProperIncludedIn_TuplesNotIncluded))
define test_ProperIncludedIn_NullIncludes: TestMessage(not ProperIncludedIn_NullIncludes, 'ProperIncludedIn_NullIncludes', toString(false), toString(ProperIncludedIn_NullIncludes))
define test_ProperIncludedIn_NullIncluded: TestMessage(ProperIncludedIn_NullIncluded, 'ProperIncludedIn_NullIncluded', toString(true), toString(ProperIncludedIn_NullIncluded))

// Flatten
define Flatten_ListOfLists: flatten { {1, 2, 3}, {4, 5, 6}, {7, 8, 9}, {9, 8, 7, 6, 5}, {4}, {3, 2, 1} }
define Flatten_NullValue: flatten null

// TODO: This fails due to list-distinguised overloads
// define test_Flatten_ListOfLists: TestMessage(Flatten_ListOfLists = {1,2,3,4,5,6,7,8,9,9,8,7,6,5,4,3,2,1}, 'Flatten_ListOfLists', toString({1,2,3,4,5,6,7,8,9,9,8,7,6,5,4,3,2,1}), toString(Flatten_ListOfLists))
define test_Flatten_NullValue: TestMessage(Flatten_NullValue is null, 'Flatten_NullValue', 'null', toString(Flatten_NullValue))

// Distinct
define Distinct_LotsOfDups: distinct {1, 2, 2, 3, 3, 3, 4, 4, 4, 4, 5, 5, 5, 5, 5, 4, 3, 2, 1}
define Distinct_NoDups: distinct {2, 4, 6, 8, 10}
define Distinct_DupsTuples: distinct { Tuple{ hello: 'world' }, Tuple{ hello: 'cleveland' }, Tuple{ hello: 'world' }, Tuple{ hello: 'dolly' } }
define Distinct_NoDupsTuples: distinct { Tuple{ hello: 'world' }, Tuple{ hello: 'cleveland' } }
define Distinct_DuplicateNulls: distinct {null, 1, 2, null, 3, 4, 5, null}

// TODO: This fails due to list-distinguised overloads
// define test_Distinct_LotsOfDups: TestMessage(Distinct_LotsOfDups = {1,2,3,4,5}, 'Distinct_LotsOfDups', toString({1,2,3,4,5}), toString(Distinct_LotsOfDups))
// define test_Distinct_NoDups: TestMessage(Distinct_NoDups = {2,4,6,8,10}, 'Distinct_NoDups', toString({2,4,6,8,10}), toString(Distinct_NoDups))
define test_Distinct_DupsTuples: TestMessage(Distinct_DupsTuples = { Tuple{ hello: 'world' }, Tuple{ hello: 'cleveland' }, Tuple{ hello: 'dolly' } }, 'Distinct_DupsTuples', toString({ Tuple{ hello: 'world' }, Tuple{ hello: 'cleveland' }, Tuple{ hello: 'dolly' } }), toString(Distinct_DupsTuples))
define test_Distinct_NoDupsTuples: TestMessage(Distinct_NoDupsTuples = { Tuple{ hello: 'world' }, Tuple{ hello: 'cleveland' } }, 'Distinct_NoDupsTuples', toString({ Tuple{ hello: 'world' }, Tuple{ hello: 'cleveland' } }), toString(Distinct_NoDupsTuples))

// TODO: This fails due to list-distinguised overloads
//define test_Distinct_DuplicateNulls: TestMessage(Distinct_DuplicateNulls ~ {null,1,2,3,4,5}, 'Distinct_DuplicateNulls', toString({null,1,2,3,4,5}), toString(Distinct_DuplicateNulls))

// First
define First_Numbers: First({1, 2, 3, 4})
define First_Letters: First({'a', 'b', 'c'})
define First_Lists: First({{'a','b','c'},{'d','e','f'}})
define First_Tuples: First({ Tuple{a: 1, b: 2, c: 3}, Tuple{a: 24, b: 25, c: 26} })
define First_Unordered: First({3, 1, 4, 2})
define First_Empty: First(List<Integer>{})
define First_NullValue: First(null as List<Integer>)

define test_First_Numbers: TestMessage(First_Numbers = 1, 'First_Numbers', toString(1), toString(First_Numbers))
define test_First_Letters: TestMessage(First_Letters = 'a', 'First_Letters', 'a', First_Letters)
define test_First_Lists: TestMessage(First_Lists = {'a','b','c'}, 'First_Lists', Combine({'a','b','c'}, ', '), Combine(First_Lists, ', '))
define test_First_Tuples: TestMessage(First_Tuples = Tuple{a: 1, b: 2, c: 3}, 'First_Tuples', toString(Tuple{a: 1, b: 2, c: 3}), toString(First_Tuples))
define test_First_Unordered: TestMessage(First_Unordered = 3, 'First_Unordered', toString(3), toString(First_Unordered))
define test_First_Empty: TestMessage(First_Empty is null, 'First_Empty', 'null', toString(First_Empty))
define test_First_NullValue: TestMessage(First_NullValue is null, 'First_NullValue', 'null', toString(First_NullValue))

// Last
define Last_Numbers: Last({1, 2, 3, 4})
define Last_Letters: Last({'a', 'b', 'c'})
define Last_Lists: Last({{'a','b','c'},{'d','e','f'}})
define Last_Tuples: Last({ Tuple{a: 1, b: 2, c: 3}, Tuple{a: 24, b: 25, c: 26} })
define Last_Unordered: Last({3, 1, 4, 2})
define Last_Empty: Last(List<Integer>{})
define Last_NullValue: Last(null as List<Integer>)

define test_Last_Numbers: TestMessage(Last_Numbers = 4, 'Last_Numbers', toString(4), toString(Last_Numbers))
define test_Last_Letters: TestMessage(Last_Letters = 'c', 'Last_Letters', 'c', Last_Letters)
define test_Last_Lists: TestMessage(Last_Lists = {'d','e','f'}, 'Last_Lists', Combine({'d','e','f'}, ', '), Combine(Last_Lists, ', '))
define test_Last_Tuples: TestMessage(Last_Tuples = Tuple{a: 24, b: 25, c: 26}, 'Last_Tuples', toString(Tuple{a: 24, b: 25, c: 26}), toString(Last_Tuples))
define test_Last_Unordered: TestMessage(Last_Unordered = 2, 'Last_Unordered', toString(2), toString(Last_Unordered))
define test_Last_Empty: TestMessage(Last_Empty is null, 'Last_Empty', 'null', toString(Last_Empty))
define test_Last_NullValue: TestMessage(Last_NullValue is null, 'Last_NullValue', 'null', toString(Last_NullValue))

// Length
define Length_Numbers: Length({2, 4, 6, 8, 10})
define Length_Lists: Length({ {1, 2, 3}, {4, 5, 6}, {7, 8, 9}, {10, 11, 12}})
define Length_Tuples: Length({ Tuple{a: 1, b: 2, c: 3}, Tuple{a: 24, b: 25, c: 26} })
define Length_Empty: Length(List<Integer>{})
define Length_NullValue: Length(null as List<Integer>)

define test_Length_Numbers: TestMessage(Length_Numbers = 5, 'Length_Numbers', toString(5), toString(Length_Numbers))
define test_Length_Lists: TestMessage(Length_Lists = 4, 'Length_Lists', toString(4), toString(Length_Lists))
define test_Length_Tuples: TestMessage(Length_Tuples = 2, 'Length_Tuples', toString(2), toString(Length_Tuples))
define test_Length_Empty: TestMessage(Length_Empty = 0, 'Length_Empty', toString(0), toString(Length_Empty))
define test_Length_NullValue: TestMessage(Length_NullValue = 0, 'Length_NullValue', toString(0), toString(Length_NullValue))

// ToList
define ToList_FiveInFive: 5 in 5 // CQL-to-ELM will promote the second 5 to a list via ToList
define ToList_FourInFive: 4 in 5 // CQL-to-ELM will promote the 5 to a list via ToList
define ToList_LengthOfNull: Length(null as Integer) // CQL-to-ELM will promote the null to a list via ToList

define test_ToList_FiveInFive: TestMessage(ToList_FiveInFive, 'ToList_FiveInFive', toString(true), toString(ToList_FiveInFive))
define test_ToList_FourInFive: TestMessage(not ToList_FourInFive, 'ToList_FourInFive', toString(false), toString(ToList_FourInFive))
define test_ToList_LengthOfNull: TestMessage(ToList_LengthOfNull = 0, 'ToList_LengthOfNull', toString(0), toString(ToList_LengthOfNull))

/*
*******************
  End List Tests
*******************
*/

/*
************************
  Start Logical Tests
************************
*/

/*

// And
define And_TT: true and true
define And_TF: true and false
define And_TN: true and null
define And_FF: false and false
define And_FT: false and true
define And_FN: false and null
define And_NN: null and null
define And_NT: null and true
define And_NF: null and false

define test_And_TT: TestMessage(And_TT, 'And_TT', toString(true), toString(And_TT))
define test_And_TF: TestMessage(not And_TF, 'And_TF', toString(false), toString(And_TF))
define test_And_TN: TestMessage(And_TN is null, 'And_TN', 'null', toString(And_TN))
define test_And_FF: TestMessage(not And_FF, 'And_FF', toString(false), toString(And_FF))
define test_And_FT: TestMessage(not And_FT, 'And_FT', toString(false), toString(And_FT))
define test_And_FN: TestMessage(not And_FN, 'And_FN', toString(false), toString(And_FN))
define test_And_NN: TestMessage(And_NN is null, 'And_NN', 'null', toString(And_NN))
define test_And_NT: TestMessage(And_NT is null, 'And_NT', 'null', toString(And_NT))
define test_And_NF: TestMessage(not And_NF, 'And_NF', toString(false), toString(And_NF))

// Or
define Or_TT: true or true
define Or_TF: true or false
define Or_TN: true or null
define Or_FF: false or false
define Or_FT: false or true
define Or_FN: false or null
define Or_NN: null or null
define Or_NT: null or true
define Or_NF: null or false

define test_Or_TT: TestMessage(Or_TT, 'Or_TT', toString(true), toString(Or_TT))
define test_Or_TF: TestMessage(Or_TF, 'Or_TF', toString(true), toString(Or_TF))
define test_Or_TN: TestMessage(Or_TN, 'Or_TN', toString(true), toString(Or_TN))
define test_Or_FF: TestMessage(not Or_FF, 'Or_FF', toString(false), toString(Or_FF))
define test_Or_FT: TestMessage(Or_FT, 'Or_FT', toString(true), toString(Or_FT))
define test_Or_FN: TestMessage(Or_FN is null, 'Or_FN', 'null', toString(Or_FN))
define test_Or_NN: TestMessage(Or_NN is null, 'Or_NN', 'null', toString(Or_NN))
define test_Or_NT: TestMessage(Or_NT, 'Or_NT', toString(true), toString(Or_NT))
define test_Or_NF: TestMessage(Or_NF is null, 'Or_NF', 'null', toString(Or_NF))

// XOr
define XOr_TT: true xor true
define XOr_TF: true xor false
define XOr_TN: true xor null
define XOr_FF: false xor false
define XOr_FT: false xor true
define XOr_FN: false xor null
define XOr_NN: null xor null
define XOr_NT: null xor true
define XOr_NF: null xor false

define test_XOr_TT: TestMessage(not XOr_TT, 'XOr_TT', toString(false), toString(XOr_TT))
define test_XOr_TF: TestMessage(XOr_TF, 'XOr_TF', toString(true), toString(XOr_TF))
define test_XOr_TN: TestMessage(XOr_TN is null, 'XOr_TN', 'null', toString(XOr_TN))
define test_XOr_FF: TestMessage(not XOr_FF, 'XOr_FF', toString(false), toString(XOr_FF))
define test_XOr_FT: TestMessage(XOr_FT, 'XOr_FT', toString(true), toString(XOr_FT))
define test_XOr_FN: TestMessage(XOr_FN is null, 'XOr_FN', 'null', toString(XOr_FN))
define test_XOr_NN: TestMessage(XOr_NN is null, 'XOr_NN', 'null', toString(XOr_NN))
define test_XOr_NT: TestMessage(XOr_NT is null, 'XOr_NT', 'null', toString(XOr_NT))
define test_XOr_NF: TestMessage(XOr_NF is null, 'XOr_NF', 'null', toString(XOr_NF))

// Not
define Not_NotTrue: not true
define Not_NotFalse: not false
define Not_NotNull: not null

define test_Not_NotTrue: TestMessage(not Not_NotTrue, 'Not_NotTrue', toString(false), toString(Not_NotTrue))
define test_Not_NotFalse: TestMessage(Not_NotFalse, 'Not_NotFalse', toString(true), toString(Not_NotFalse))
define test_Not_NotNull: TestMessage(Not_NotNull is null, 'Not_NotNull', 'null', toString(Not_NotNull))

*/

/*
**********************
  End Logical Tests
**********************
*/

/*
**************************
  Start Nullogical Tests
**************************
*/

/*

// IsNull
define Nil: null
define One: 1
define IsNull_NullIsNull: null is null
define IsNull_NullVarIsNull: Nil is null
define IsNull_StringIsNull: '' is null
define IsNull_NonNullVarIsNull: One is null

define test_IsNull_NullIsNull: TestMessage(IsNull_NullIsNull, 'IsNull_NullIsNull', toString(true), toString(IsNull_NullIsNull))
define test_IsNull_NullVarIsNull: TestMessage(IsNull_NullVarIsNull, 'IsNull_NullVarIsNull', toString(true), toString(IsNull_NullVarIsNull))
define test_IsNull_StringIsNull: TestMessage(not IsNull_StringIsNull, 'IsNull_StringIsNull', toString(false), toString(IsNull_StringIsNull))
define test_IsNull_NonNullVarIsNull: TestMessage(not IsNull_NonNullVarIsNull, 'IsNull_NonNullVarIsNull', toString(false), toString(IsNull_NonNullVarIsNull))

// Coalesce
define ListWithNull: List{null, null, 'One', null, 'Two'}
define ListA: List{null, null, 3}
define Coalesce_NullNullHelloNullWorld: Coalesce(null, null, 'Hello', null, 'World')
define Coalesce_FooNullNullBar: Coalesce('Foo', null, null, 'Bar')
define Coalesce_AllNull: Coalesce(null, null, null)
define Coalesce_ListArgStartsWithNull: Coalesce(List{null, null, 'One', null, 'Two'})
define Coalesce_ListArgAllNull: Coalesce(List{null, null, null, null})
define Coalesce_ListExpressionRef: Coalesce(ListWithNull)
// define Coalesce_RetrieveAsList: Coalesce([Encounter])
define Coalesce_UnionAsList: Coalesce(ListA union ListWithNull)

define test_Coalesce_NullNullHelloNullWorld: TestMessage(Coalesce_NullNullHelloNullWorld = 'Hello', 'Coalesce_NullNullHelloNullWorld', 'Hello', Coalesce_NullNullHelloNullWorld)
define test_Coalesce_FooNullNullBar: TestMessage(Coalesce_FooNullNullBar = 'Foo', 'Coalesce_FooNullNullBar', 'Foo', Coalesce_FooNullNullBar)
define test_Coalesce_AllNull: TestMessage(Coalesce_AllNull is null, 'Coalesce_AllNull', 'null', Coalesce_AllNull)
define test_Coalesce_ListArgStartsWithNull: TestMessage(Coalesce_ListArgStartsWithNull = 'One', 'Coalesce_ListArgStartsWithNull', 'One', Coalesce_ListArgStartsWithNull)
define test_Coalesce_ListArgAllNull: TestMessage(Coalesce_ListArgAllNull is null, 'Coalesce_ListArgAllNull', 'null', Coalesce_ListArgAllNull)
define test_Coalesce_ListExpressionRef: TestMessage(Coalesce_ListExpressionRef = 'One', 'Coalesce_ListExpressionRef', 'One', Coalesce_ListExpressionRef)
define test_Coalesce_UnionAsList: TestMessage(Coalesce_UnionAsList = 3, 'Coalesce_UnionAsList', toString(3), toString(Coalesce_UnionAsList))

// IsTrue
define IsTrue_TrueIsTrue: true is true
define IsTrue_FalseIsTrue: false is true
define IsTrue_NullIsTrue: null is true

define test_IsTrue_TrueIsTrue: TestMessage(IsTrue_TrueIsTrue, 'IsTrue_TrueIsTrue', toString(true), toString(IsTrue_TrueIsTrue))
define test_IsTrue_FalseIsTrue: TestMessage(not IsTrue_FalseIsTrue, 'IsTrue_FalseIsTrue', toString(false), toString(IsTrue_FalseIsTrue))
define test_IsTrue_NullIsTrue: TestMessage(not IsTrue_NullIsTrue, 'IsTrue_NullIsTrue', toString(false), toString(IsTrue_NullIsTrue))

// IsFalse
define IsFalse_TrueIsFalse: true is false
define IsFalse_FalseIsFalse: false is false
define IsFalse_NullIsFalse: null is false

define test_IsFalse_TrueIsFalse: TestMessage(not IsFalse_TrueIsFalse, 'IsFalse_TrueIsFalse', toString(false), toString(IsFalse_TrueIsFalse))
define test_IsFalse_FalseIsFalse: TestMessage(IsFalse_FalseIsFalse, 'IsFalse_FalseIsFalse', toString(true), toString(IsFalse_FalseIsFalse))
define test_IsFalse_NullIsFalse: TestMessage(not IsFalse_NullIsFalse, 'IsFalse_NullIsFalse', toString(false), toString(IsFalse_NullIsFalse))

*/

/*
************************
  End Nullogical Tests
************************
*/

/*
*************************
  Start Parameter Tests
*************************
*/

// TODO

/*
***********************
  End Parameter Tests
***********************
*/

/*
************************
  Start Quantity Tests
************************
*/

// TODO

/*
**********************
  End Quantity Tests
**********************
*/

/*
*********************
  Start Query Tests
*********************
*/

// TODO

/*
*******************
  End Query Tests
*******************
*/

/*
**********************
  Start String Tests
**********************
*/

/*

// Concat
define Hello: 'Hello'
define World: 'World'
define Concat_HelloWorld: 'Hello' + 'World'
define Concat_Sentence: 'The' + ' ' + 'quick' + ' ' + 'brown' + ' ' + 'fox' + ' ' + 'jumps' + ' '
                  + 'over' + ' ' + 'the' + ' ' + 'lazy' + ' ' + 'dog.'
define Concat_ConcatNull: 'Hello' + null
define Concat_HelloWorldVariables: Hello + World

define test_Concat_HelloWorld: TestMessage(Concat_HelloWorld = 'HelloWorld', 'Concat_HelloWorld', 'HelloWorld', Concat_HelloWorld)
define test_Concat_Sentence: TestMessage(Concat_Sentence = 'The quick brown fox jumps over the lazy dog.', 'Concat_Sentence', 'The quick brown fox jumps over the lazy dog.', Concat_Sentence)
define test_Concat_ConcatNull: TestMessage(Concat_ConcatNull is null, 'Concat_ConcatNull', 'null', Concat_ConcatNull)
define test_Concat_HelloWorldVariables: TestMessage(Concat_HelloWorldVariables = 'HelloWorld', 'Concat_HelloWorldVariables', 'HelloWorld', Concat_HelloWorldVariables)

// Combine
define Combine_NoSeparator: Combine({ 'abc', 'def', 'ghi', 'jkl' })
define Combine_Separator: Combine({ 'abc', 'def', 'ghi', 'jkl' }, ';')
define Combine_CombineNull: Combine(null, ';')
define Combine_CombineNullItem: Combine({ 'abc', 'def', null, 'jkl' }, ';')

define test_Combine_NoSeparator: TestMessage(Combine_NoSeparator = 'abcdefghijkl', 'Combine_NoSeparator', 'abcdefghijkl', Combine_NoSeparator)
define test_Combine_Separator: TestMessage(Combine_Separator = 'abc;def;ghi;jkl', 'Combine_Separator', 'abc;def;ghi;jkl', Combine_Separator)
define test_Combine_CombineNull: TestMessage(Combine_CombineNull is null, 'Combine_CombineNull', 'null', Combine_CombineNull)
define test_Combine_CombineNullItem: TestMessage(Combine_CombineNullItem is null, 'Combine_CombineNullItem', 'null', Combine_CombineNullItem)

// Split
define Split_CommaSeparated: Split('a,b,c,,1,2,3', ',')
define Split_SeparatorNotUsed: Split('a,b,c,,1,2,3', ';')
define Split_SeparateNull: Split(null, ',')
define Split_SeparateUsingNull: Split('a,b,c', null)

define test_Split_CommaSeparated: TestMessage(Split_CommaSeparated = { 'a','b','c','','1','2','3' }, 'Split_CommaSeparated', Combine({ 'a','b','c','','1','2','3' }, ','), Combine(Split_CommaSeparated, ','))
define test_Split_SeparatorNotUsed: TestMessage(Split_SeparatorNotUsed = { 'a,b,c,,1,2,3' }, 'Split_SeparatorNotUsed', Combine({ 'a,b,c,,1,2,3' }, ','), Combine(Split_SeparatorNotUsed, ','))
define test_Split_SeparateNull: TestMessage(Split_SeparateNull is null, 'Split_SeparateNull', 'null', Split_SeparateNull)
define test_Split_SeparateUsingNull: TestMessage(Split_SeparateUsingNull = 'a,b,c', 'Split_SeparateUsingNull', 'a,b,c', Split_SeparateUsingNull)

// Length
define Length_ElevenLetters: Length('Hello World')
define Length_NullString: Length(null as String)

define test_Length_ElevenLetters: TestMessage(Length_ElevenLetters = 11, 'Length_ElevenLetters', toString(11), toString(Length_ElevenLetters))
define test_Length_NullString: TestMessage(Length_NullString is null, 'Length_NullString', 'null', toString(Length_NullString))

// Upper
define Upper_LowerC: Upper('abcdefg123')
define Upper_UpperC: Upper('ABCDEFG123')
define Upper_CamelC: Upper('aBcDeFg123')
define Upper_NullString: Upper(null)

define test_Upper_LowerC: TestMessage(Upper_LowerC = 'ABCDEFG123', 'Upper_LowerC', 'ABCDEFG123', Upper_LowerC)
define test_Upper_UpperC: TestMessage(Upper_UpperC = 'ABCDEFG123', 'Upper_UpperC', 'ABCDEFG123', Upper_UpperC)
define test_Upper_CamelC: TestMessage(Upper_CamelC = 'ABCDEFG123', 'Upper_CamelC', 'ABCDEFG123', Upper_CamelC)
define test_Upper_NullString: TestMessage(Upper_NullString is null, 'Upper_NullString', 'null', Upper_NullString)

// Lower
define Lower_LowerC: Lower('abcdefg123')
define Lower_UpperC: Lower('ABCDEFG123')
define Lower_CamelC: Lower('aBcDeFg123')
define Lower_NullString: Lower(null)

define test_Lower_LowerC: TestMessage(Lower_LowerC = 'abcdefg123', 'Lower_LowerC', 'abcdefg123', Lower_LowerC)
define test_Lower_UpperC: TestMessage(Lower_UpperC = 'abcdefg123', 'Lower_UpperC', 'abcdefg123', Lower_UpperC)
define test_Lower_CamelC: TestMessage(Lower_CamelC = 'abcdefg123', 'Lower_CamelC', 'abcdefg123', Lower_CamelC)
define test_Lower_NullString: TestMessage(Lower_NullString is null, 'Lower_NullString', 'null', Lower_NullString)

// Indexer
define Indexer_HelloWorldSix: 'HelloWorld'[6]
define Indexer_HelloWorldZero: 'HelloWorld'[0]
define Indexer_HelloWorldTwenty: 'HelloWorld'[20]
define Indexer_NullString: (null as String)[4]
define Indexer_NullIndex: 'HelloWorld'[null]

define test_Indexer_HelloWorldSix: TestMessage(Indexer_HelloWorldSix = 'o', 'Indexer_HelloWorldSix', 'o', Indexer_HelloWorldSix)
define test_Indexer_HelloWorldZero: TestMessage(Indexer_HelloWorldZero = 'H', 'Indexer_HelloWorldZero', 'H', Indexer_HelloWorldZero)
define test_Indexer_HelloWorldTwenty: TestMessage(Indexer_HelloWorldTwenty is null, 'Indexer_HelloWorldTwenty', 'null', Indexer_HelloWorldTwenty)
define test_Indexer_NullString: TestMessage(Indexer_NullString is null, 'Indexer_NullString', 'null', Indexer_NullString)
define test_Indexer_NullIndex: TestMessage(Indexer_NullIndex is null, 'Indexer_NullIndex', 'null', Indexer_NullIndex)

// PositionOf
define PositionOf_found: PositionOf('cde', 'abcdefg')
define PositionOf_notFound: PositionOf('fgh', 'abcdefg')
define PositionOf_nullPattern: PositionOf(null, 'abcdefg')
define PositionOf_nullString: PositionOf('cde', null)

define test_PositionOf_found: TestMessage(PositionOf_found = 2, 'PositionOf_found', toString(2), toString(PositionOf_found))
define test_PositionOf_notFound: TestMessage(PositionOf_notFound = -1, 'PositionOf_notFound', toString(-1), toString(PositionOf_notFound))
define test_PositionOf_nullPattern: TestMessage(PositionOf_nullPattern is null, 'PositionOf_nullPattern', 'null', toString(PositionOf_nullPattern))
define test_PositionOf_nullString: TestMessage(PositionOf_nullString is null, 'PositionOf_nullString', 'null', toString(PositionOf_nullString))

// Substring
define Substring_World: Substring('HelloWorld', 5)
define Substring_Or: Substring('HelloWorld', 7, 2)
define Substring_ZeroLength: Substring('HelloWorld', 7, 0)
define Substring_StartTooLow: Substring('HelloWorld', -1)
define Substring_StartZero: Substring('HelloWorld', 0)
define Substring_TooMuchLength: Substring('HelloWorld', 7, 25)
define Substring_NegativeLength: Substring('HelloWorld', 7, -1)
define Substring_NullString: Substring(null, 5)
define Substring_NullStart: Substring('HelloWorld', null)

define test_Substring_World: TestMessage(Substring_World = 'World', 'Substring_World', 'World', Substring_World)
define test_Substring_Or: TestMessage(Substring_Or = 'rl', 'Substring_Or', 'rl', Substring_Or)
define test_Substring_ZeroLength: TestMessage(Substring_ZeroLength = '', 'Substring_ZeroLength', '', Substring_ZeroLength)
define test_Substring_StartTooLow: TestMessage(Substring_StartTooLow is null, 'Substring_StartTooLow', 'null', Substring_StartTooLow)
define test_Substring_StartZero: TestMessage(Substring_StartZero = 'HelloWorld', 'Substring_StartZero', 'HelloWorld', Substring_StartZero)
define test_Substring_TooMuchLength: TestMessage(Substring_TooMuchLength = 'rld', 'Substring_TooMuchLength', 'rld', Substring_TooMuchLength)
define test_Substring_NegativeLength: TestMessage(Substring_NegativeLength = '', 'Substring_NegativeLength', '', Substring_NegativeLength)
define test_Substring_NullString: TestMessage(Substring_NullString is null, 'Substring_NullString', 'null', Substring_NullString)
define test_Substring_NullStart: TestMessage(Substring_NullStart is null, 'Substring_NullStart', 'null', Substring_NullStart)

// StartsWith
define StartsWith_FooBarStartsWithFoo: StartsWith('FooBar', 'Foo')
define StartsWith_FooBarStartsWithBar: StartsWith('FooBar', 'Bar')
define StartsWith_FooBarStartsWithBlank: StartsWith('FooBar', '')
define StartsWith_BlankStartsWithFoo: StartsWith('', 'Foo')
define StartsWith_StartsWithNull: StartsWith('FooBar', null)
define StartsWith_StartsWithNullAsString: StartsWith('FooBar', null as String)
define StartsWith_NullStartsWith: StartsWith(null, 'Foo')
define StartsWith_NullAsStringStartsWith: StartsWith(null as String, 'Foo')

define test_StartsWith_FooBarStartsWithFoo: TestMessage(StartsWith_FooBarStartsWithFoo, 'StartsWith_FooBarStartsWithFoo', toString(true), toString(StartsWith_FooBarStartsWithFoo))
define test_StartsWith_FooBarStartsWithBar: TestMessage(not StartsWith_FooBarStartsWithBar, 'StartsWith_FooBarStartsWithBar', toString(false), toString(StartsWith_FooBarStartsWithBar))
define test_StartsWith_FooBarStartsWithBlank: TestMessage(StartsWith_FooBarStartsWithBlank, 'StartsWith_FooBarStartsWithBlank', toString(true), toString(StartsWith_FooBarStartsWithBlank))
define test_StartsWith_StartsWithNull: TestMessage(StartsWith_StartsWithNull is null, 'StartsWith_StartsWithNull', 'null', toString(StartsWith_StartsWithNull))
define test_StartsWith_StartsWithNullAsString: TestMessage(StartsWith_StartsWithNullAsString is null, 'StartsWith_StartsWithNullAsString', 'null', toString(StartsWith_StartsWithNullAsString))
define test_StartsWith_NullStartsWith: TestMessage(StartsWith_NullStartsWith is null, 'StartsWith_NullStartsWith', 'null', toString(StartsWith_NullStartsWith))
define test_StartsWith_NullAsStringStartsWith: TestMessage(StartsWith_NullAsStringStartsWith is null, 'StartsWith_NullAsStringStartsWith', 'null', toString(StartsWith_NullAsStringStartsWith))

// EndsWith
define EndsWith_FooBarEndsWithBar: EndsWith('FooBar', 'Bar')
define EndsWith_FooBarEndsWithFoo: EndsWith('FooBar', 'Foo')
define EndsWith_FooBarEndsWithBlank: EndsWith('FooBar', '')
define EndsWith_BlankEndsWithFoo: EndsWith('', 'Foo')
define EndsWith_EndsWithNull: EndsWith('FooBar', null)
define EndsWith_EndsWithNullAsString: EndsWith('FooBar', null as String)
define EndsWith_NullEndsWith: EndsWith(null as String, 'Foo')
define EndsWith_NullAsStringEndsWith: EndsWith(null as String, 'Foo')

define test_EndsWith_FooBarEndsWithBar: TestMessage(EndsWith_FooBarEndsWithBar, 'EndsWith_FooBarEndsWithBar', toString(true), toString(EndsWith_FooBarEndsWithBar))
define test_EndsWith_FooBarEndsWithFoo: TestMessage(not EndsWith_FooBarEndsWithFoo, 'EndsWith_FooBarEndsWithFoo', toString(false), toString(EndsWith_FooBarEndsWithFoo))
define test_EndsWith_BlankEndsWithFoo: TestMessage(not EndsWith_BlankEndsWithFoo, 'EndsWith_BlankEndsWithFoo', toString(false), toString(EndsWith_BlankEndsWithFoo))
define test_EndsWith_EndsWithNull: TestMessage(EndsWith_EndsWithNull is null, 'EndsWith_EndsWithNull', 'null', toString(EndsWith_EndsWithNull))
define test_EndsWith_EndsWithNullAsString: TestMessage(EndsWith_EndsWithNullAsString is null, 'EndsWith_EndsWithNullAsString', 'null', toString(EndsWith_EndsWithNullAsString))
define test_EndsWith_NullEndsWith: TestMessage(EndsWith_NullEndsWith is null, 'EndsWith_NullEndsWith', 'null', toString(EndsWith_NullEndsWith))
define test_EndsWith_NullAsStringEndsWith: TestMessage(EndsWith_NullAsStringEndsWith is null, 'EndsWith_NullAsStringEndsWith', 'null', toString(EndsWith_NullAsStringEndsWith))

*/

/*
********************
  End String Tests
********************
*/
