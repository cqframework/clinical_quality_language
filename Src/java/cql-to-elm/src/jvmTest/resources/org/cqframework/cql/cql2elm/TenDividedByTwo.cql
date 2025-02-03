library TenDividedByTwo

include TranslationTestsCommon version '1.0' called Common

define TenDividedByTwo: 10 / 2

// TODO: possible issue in translator causing TenDividedByTwo = 5 to fail (Integer not being implicitly promoted to Decimal)
define test_TenDividedByTwo: Common.TestMessage(TenDividedByTwo = 5.0, 'TenDividedByTwo', Common.toString(5.0), Common.toString(TenDividedByTwo))
