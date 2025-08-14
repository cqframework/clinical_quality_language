library Median_odd

include TranslationTestsCommon version '1.0' called Common

// TODO: possible issue in translator causing Median_odd = 3 to fail (Integer not being implicitly promoted to Decimal)
define Median_odd: Median({5,1,2,3,4})

define test_Median_odd: Common.TestMessage(Median_odd = 3.0, 'Median_odd', Common.toString(3), Common.toString(Median_odd))
