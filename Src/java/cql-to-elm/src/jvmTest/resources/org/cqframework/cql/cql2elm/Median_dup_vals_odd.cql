library Median_dup_vals_odd

include TranslationTestsCommon version '1.0' called Common

define Median_dup_vals_odd:  Median({3,1,2,2,2,3,4,5,6})

// TODO: possible issue in translator causing Median_dup_vals_odd = 3 to fail (Integer not being implicitly promoted to Decimal)
define test_Median_dup_vals_odd: Common.TestMessage(Median_dup_vals_odd = 3.0, 'Median_dup_vals_odd', Common.toString(3), Common.toString(Median_dup_vals_odd))
