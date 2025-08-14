library Median_q_diff_units

include TranslationTestsCommon version '1.0' called Common

define Median_q_diff_units: Median({5 'ml',0.001 'l',0.02 'dl',3 'ml',4 'ml',6 'ml'})
// TODO - this behavior has yet to be implemented for Quantity
define test_Median_q_diff_units: Common.TestMessage(Median_q_diff_units = 3.5 'ml', 'Median_q_diff_units', Common.toString(3.5 'ml'), Common.toString(Median_q_diff_units))
