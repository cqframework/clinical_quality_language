library DivideVariables

include TranslationTestsCommon version '1.0' called Common

define Hundred: 100
define Four: 4

// TODO: possible issue in translator causing DivideMultiple = 5 to fail (Integer not being implicitly promoted to Decimal)
define DivideVariables: Hundred / Four

define test_DivideVariables: Common.TestMessage(DivideVariables = 25.0, 'DivideVariables', Common.toString(25.0), Common.toString(DivideVariables))
