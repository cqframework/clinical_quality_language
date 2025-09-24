library Log_BaseTen

include TranslationTestsCommon version '1.0' called Common

// TODO: possible issue in translator causing Log_BaseTen = 4 to fail (Integer not being implicitly promoted to Decimal)
define Log_BaseTen: Log(10000,10)

define test_Log_BaseTen: Common.TestMessage(Log_BaseTen = 4.0, 'Log_BaseTen', Common.toString(4.0), Common.toString(Log_BaseTen))