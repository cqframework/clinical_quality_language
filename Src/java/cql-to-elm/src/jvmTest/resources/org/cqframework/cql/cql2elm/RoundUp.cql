library RoundUp

include TranslationTestsCommon version '1.0' called Common

define RoundUp: Round(4.56)

// TODO: possible issue in translator causing RoundUp = 5 to fail (Integer not being implicitly promoted to Decimal)
define test_RoundUp: Common.TestMessage(RoundUp = 5.0, 'RoundUp', Common.toString(5.0), Common.toString(RoundUp))
