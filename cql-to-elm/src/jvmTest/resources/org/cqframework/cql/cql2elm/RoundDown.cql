library RoundDown

include TranslationTestsCommon version '1.0' called Common

// TODO: possible issue in translator causing RoundDown = 4 to fail (Integer not being implicitly promoted to Decimal)
define RoundDown: Round(4.49)

define test_RoundDown: Common.TestMessage(RoundDown = 4.0, 'RoundDown', Common.toString(4.0), Common.toString(RoundDown))
