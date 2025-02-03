library DivideMultiple

include TranslationTestsCommon version '1.0' called Common

// TODO: possible issue in translator causing DivideMultiple = 5 to fail (Integer not being implicitly promoted to Decimal)
define DivideMultiple: 1000 / 4 / 10 / 5

define test_DivideMultiple: Common.TestMessage(DivideMultiple = 5.0, 'DivideMultiple', Common.toString(5.0), Common.toString(DivideMultiple))
