library Arithmetic_Parenthetical

include TranslationTestsCommon version '1.0' called Common

// TODO: possible issue in translator causing Arithmetic_Parenthetical = -10 to fail (Integer not being implicitly promoted to Decimal)
define Arithmetic_Parenthetical: (1 + 5) * (10 - 15) / 3

define test_Arithmetic_Parenthetical: Common.TestMessage(Arithmetic_Parenthetical = -10.0, 'Arithmetic_Parenthetical', Common.toString(-10.0), Common.toString(Arithmetic_Parenthetical))
