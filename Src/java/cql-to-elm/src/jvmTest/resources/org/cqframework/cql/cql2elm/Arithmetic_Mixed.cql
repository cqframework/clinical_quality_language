library Arithmetic_Mixed

include TranslationTestsCommon version '1.0' called Common

// TODO: possible issue in translator causing Arithmetic_Mixed = 46 to fail (Integer not being implicitly promoted to Decimal)
define Arithmetic_Mixed: 1 + 5 * 10 - 15 / 3

define test_Arithmetic_Mixed: Common.TestMessage(Arithmetic_Mixed = 46.0, 'Arithmetic_Mixed', Common.toString(46.0), Common.toString(Arithmetic_Mixed))
